package treestructure;

import java.util.ArrayList;

public class BIPConnector {

	private String name;
	private ArrayList<TreeNode> children;
	private ArrayList<BIPConnector> childrenBIP;
	private BIPConnector parent;
	
	public BIPConnector(String _name, ArrayList<TreeNode> _children, ArrayList<BIPConnector> _childrenBIP,  BIPConnector _parent) {
		// TODO Auto-generated constructor stub
		name = _name;
		children = _children;
		childrenBIP = _childrenBIP;
		parent = _parent;
	}
	
	public BIPConnector(String _name, ArrayList<TreeNode> _children, BIPConnector _parent) {
		// TODO Auto-generated constructor stub
		name = _name;
		children = _children;
		childrenBIP = new ArrayList<BIPConnector>();
		parent = _parent;
	}
	

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BIPConnector getParent() {
		return parent;
	}

	public void setParent(BIPConnector parent) {
		this.parent = parent;
	}
	
	
}
