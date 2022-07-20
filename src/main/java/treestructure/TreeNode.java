package treestructure;

import java.util.ArrayList;
import java.util.StringJoiner;

public class TreeNode {

	private TreeNode parent;
	private String content; // e.g. Tomcat.start
	private String componentTypeName; // e.g. Tomcat
	private String portTypeName; // e.g. start
	private boolean trigger; // trigger or sync
	private ArrayList<ArrayList<TreeNode>> export; // null means the node is a leaf, i.e. a port
	private ArrayList<TreeNode> children; // null means the node is a leaf, i.e. a port
	
	public TreeNode(String _content, boolean _trigger, TreeNode _parent) {
		content = removeBrackets(_content);
		setNameAndAction(_content);
		trigger = _trigger;
		parent = _parent;
		children = new ArrayList<TreeNode>();
		export = new ArrayList<ArrayList<TreeNode>>();
	}
	
	public TreeNode(String _content, boolean _trigger) {
		content = removeBrackets(_content);
		setNameAndAction(_content);
		trigger = _trigger;
		children = new ArrayList<TreeNode>();
	}
	
	/**
	 * Generating codes
	 * ---------------------------------------------------------------------
	 * */
	
	public void genDefineBIP(ArrayList<String> result1, String annoId) {
		for (TreeNode child : children) {
			child.genDefineBIP(result1, annoId);
		}
		
		if (children.size() > 0) {
			String result = "";
			result += "\tconnector type " + annoId + "_" + getComponentTypeName() + "_define(";
			StringJoiner joiner = new StringJoiner(", ");
			
			for (int i=0 ; i<children.size() ; i++) {
				joiner.add("Port p" + (i+1));
			}
			result += joiner.toString() + ")";
			
			if (!this.getContent().contains("root")) {
				result += "\n\t\texport port Port ep()";
			}
			result += "\n\t\tdefine ";
			for (int i=0 ; i<children.size() ; i++) {
				String temp = "p" + (i+1);
				if (children.get(i).isTrigger())
					temp += "\'";
				result += temp + " ";
			}
			result += "\n\tend\n";
			result1.add(result);
			
		}
	}
	
	public void genBIPConnector(ArrayList<String> result1, String annoId) {
		for (TreeNode child : children) {
			child.genBIPConnector(result1, annoId);
		}
		
		if (children.size() > 0) {
			String result = "";
			result += "\t\tconnector " +  annoId + "_" + getComponentTypeName() + "_define "
					+ annoId + "_" + getComponentTypeName() + "_CompInstanceId" + "(";
			StringJoiner joiner = new StringJoiner(", ");
			
			for (TreeNode child : children) {
				if (child.getChildren().size() > 0) {
					joiner.add(annoId + "_" + child.getComponentTypeName() + "_CompInstanceId" + ".ep");
				}else {
					joiner.add(child.getContent());
				}
			}
			
			result += joiner.toString() + ")\n";
			result1.add(result);
		}
	}
	/**
	 * Add information of export
	 * ------------------------------------------------------------------------------------
	 * */
	public void addExportedPort() {
				
		for (TreeNode child : children) {
			child.addExportedPort();
		}
		
		if(children.size() == 0) {
			ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
			temp.add(this);
			this.export.add(temp);
		}else {
			//case 1: Compound with no trigger children -> exportedPort = all synch children
			if (allChildrenAreSync()) {
				export = getChildren().get(0).getExport();
				if (getChildren().size() > 1) {
					for (int i=1 ; i<getChildren().size() ; i++) {
						export = merge2ArrayList(export, getChildren().get(i).getExport());
					}
				}
			}else {
				//case 2: Compound with some trigger children -> exportedPort = all trigs children
				for (TreeNode childTriggers : getListTriggers(getChildren())) {
					ArrayList<ArrayList<TreeNode>> childTriggerExport = childTriggers.getExport();
					for (ArrayList<TreeNode> listExportI : childTriggerExport) {
						this.export.add(listExportI);
					}
				}
			}
		}
	}
	
	public ArrayList<ArrayList<TreeNode>> merge2ArrayList(ArrayList<ArrayList<TreeNode>> first, ArrayList<ArrayList<TreeNode>> second) {
		ArrayList<ArrayList<TreeNode>> result = new ArrayList<ArrayList<TreeNode>>();
		for (ArrayList<TreeNode> f_elem : first) {
			for (ArrayList<TreeNode> s_elem : second) {
				ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
				temp.addAll(f_elem);
				temp.addAll(s_elem);
				result.add(temp);
			}
		}
		return result;
	}
	
	/**
	 * Supporting functions
	 * ---------------------------------------------------------------------
	 * */
	public String removeBrackets(String content) {
		return content.replaceAll("`|\\*|\\)|\\(", "");
	}
	
	public void setNameAndAction(String _content) {
		System.out.println("print content: " + _content);
		String[] sp = _content.replaceAll("`|\\*|\\)|\\(", "").split("\\.");
		componentTypeName = sp[0];
		portTypeName = sp[1];
	}
	
	public void traversal() {
		String rs = "";
		if (parent != null) {
			rs += content + "\t (trig? " + isTrigger() + ")\t parent:" + parent.getContent();
			rs += "\t export: " + this.getExport();
		}else {
			rs += content + "\t (trig? " + isTrigger();
			rs += ")\t export: " + this.getExport();// + "\t children: " + this.getChildren();
		}
		System.out.println(rs);
		for (TreeNode leaf : children) {
			leaf.traversal();
		}
	}
	
	public void print() {
		if (trigger) {
			System.out.println(componentTypeName + "_" + portTypeName + "\t trigger \tparent:" + parent);
		} else {
			System.out.println(componentTypeName + "_" + portTypeName + "\t synchron \tparent:" + parent);
		}
	}
	
	public boolean allChildrenAreSync() {
    	for (TreeNode child : children) {
    		if (child.isTrigger())
    			return false;
    	}
    	return true;
    }
	
	public ArrayList<TreeNode> getListSynchron(ArrayList<TreeNode> input){
    	ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
    	for (TreeNode child : input) {
    		if (!child.isTrigger()) {
    			rs.add(child);
    		}
    	}
    	return rs;
    }
	
	public ArrayList<TreeNode> getListTriggers(ArrayList<TreeNode> input){
    	ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
    	for (TreeNode child : input) {
    		if (child.isTrigger()) {
    			rs.add(child);
    		}
    	}
    	return rs;
    }
	
	public ArrayList<TreeNode> getSiblings(){
		ArrayList<TreeNode> rs = new ArrayList<TreeNode>();
		if (parent != null) {
			for (TreeNode sib : parent.getChildren()) {
				if (sib != this) {
					rs.add(sib);
				}
			}
		}
		return rs;
	}
	
//	public boolean hasTriggersInList(ArrayList<TreeNode> input) {
//		for (TreeNode t : )
//	}
	/**
	 * Getters and Setters
	 * ---------------------------------------------------------------------
	 * */
	public String toString() {
		return componentTypeName + "." + portTypeName;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getComponentTypeName() {
		return componentTypeName;
	}

	public void setComponentTypeName(String componentTypeName) {
		this.componentTypeName = componentTypeName;
	}

	public String getPortTypeName() {
		return portTypeName;
	}

	public void setPortTypeName(String portTypeName) {
		this.portTypeName = portTypeName;
	}

	public boolean isTrigger() {
		return trigger;
	}

	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}

	public ArrayList<ArrayList<TreeNode>> getExport() {
		return export;
	}

	public void setExport(ArrayList<ArrayList<TreeNode>> export) {
		this.export = export;
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}
}
