package treestructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.stream.Collectors;

import supportlibs.*;



public class GenBIPModel {
	
	public void test() {
//		String connectorString = "[(p.1)-(p.2)]-(p.3)`-[(p.4)-(p.5)]";
//		String connectorString1 = "[(p.1)-(p.2)]-[(p.3)-(p.4)]";
//		String connectorString21 = "(p.1)-(p.2)-(p.3)-(p.4)";
//		String connectorString22 = "[(p.1)-(p.2)]-(p.3)-(p.4)";
//		String connectorString2 = "[(p.1)-(p.2)]`-(p.3)`-[(p.4)-(p.5)]";
//		String connectorString3 = "[(p.1)-(p.2)]`-(p.3)-(p.4)";
//		String connectorString4 = "(p.1)`-(p.2)-[(p.3)-(p.4)]";
//		String connectorString5 = "[(p.1)`-(p.2)]-(p.3)-[(p.4)`-[(p.5)`-(p.6)]]";
//		String connectorString6 = "[(p.1)`-(p.2)]-[(p.3)`-(p.4)`-(p.5)]-(p.6)";
//		String connectorString7 = "[(p.1)`-(p.2)]`-(p.3)-[(p.4)-(p.5)]";
//		String connectorString8 = "(p.1)`-(p.2)-[(p.3)`-(p.4)`]";
//		String connectorString9 = "(p.1)-[(p.2)`-(p.3)`]";
//		String connectorString10 = "(p.1)`-[[(p.2)`-(p.21)`-[(p.2a)-(p.2b]]-[(p.3)`-(p.31)`-(p.3a)]]";
//		String connectorString11 = "(p.1)`-(p.2)`-(p.3)";
//		String connectorString12 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)-(p.3c)]`-[(p.3d)-(p.3e)]]"
//				+ "-[(p.3a1)`-[(p.3b1)-(p.3c1)]`-[(p.3d1)-(p.3e1)]]";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
//		String connectorString13 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)`-[(p.3c1)-(p.3c2)]`-(p.3e)]`-(p.3d)]";//[1a'-1b'-1c]-2-[3a'-[3b-3c]'-3d]
//		String connectorString14 = "[(p.1a)`-[(p.1b)-(p.1c)]`]`-[(p.3a)`-[(p.3b)-(p.3c)]`-(p.3d)]";
//		String connectorString15 = "[(p.1a)`-(p.1b)`-(p.1c)]-(p.2)-[(p.3a)`-[(p.3b)-(p.3c)]`-(p.3d)]";
		
//		System.out.println("--- String 1: " + connectorString);
//		System.out.println(genBIPDefineCode(connectorString));
//		
//		System.out.println("--- String 2: " + connectorString1);
//		System.out.println(genBIPDefineCode(connectorString1));
//		
//		System.out.println("--- String 21: " + connectorString21);
//		System.out.println(genBIPDefineCode(connectorString21));
//		
//		System.out.println("--- String 22: " + connectorString22);
//		System.out.println(genBIPDefineCode(connectorString22));
//		
//		System.out.println("--- String 3: " + connectorString2);
//		System.out.println(genBIPDefineCode(connectorString2));
//		
//		System.out.println("--- String 4: " + connectorString3);
//		System.out.println(genBIPDefineCode(connectorString3));
////		
//		System.out.println("--- String 5: " + connectorString4);
//		System.out.println(genBIPDefineCode(connectorString4));
////		
//		System.out.println("--- String 6: " + connectorString5);
//		System.out.println(genBIPDefineCode(connectorString5));
////		
//		System.out.println("--- String 7: " + connectorString6);
//		System.out.println(genBIPDefineCode(connectorString6));
////		
//		System.out.println("--- String 8: " + connectorString7);
//		System.out.println(genBIPDefineCode(connectorString7));
////		
//		System.out.println("--- String 9: " + connectorString8);
//		System.out.println(genBIPDefineCode(connectorString8));
////		
//		System.out.println("--- String 10: " + connectorString9);
//		System.out.println(genBIPDefineCode(connectorString9));
////		
//		System.out.println("--- String 11: " + connectorString10);
//		System.out.println(genBIPDefineCode(connectorString10));
////		
//		System.out.println("--- String 12: " + connectorString11);
//		System.out.println(genBIPDefineCode(connectorString11));
////		
//		System.out.println("--- String 13: " + connectorString12);
//		System.out.println(genBIPDefineCode(connectorString12));
////		
//		System.out.println("--- String 14: " + connectorString13);
//		System.out.println(genBIPDefineCode(connectorString13));
////		
//		System.out.println("--- String 15: " + connectorString14);
//		System.out.println(genBIPDefineCode(connectorString14));
//		
//		System.out.println("--- String 16: " + connectorString15);
//		System.out.println(genBIPDefineCode(connectorString15, "str15"));
	}
	
	public static void main(String[] args) {
		GenBIPModel testMT = new GenBIPModel();
		testMT.test();
		testMT.generateBIPModel(args[0], args[1]);
	}
	
	public void generateBIPModel(String fileXML, String fileConfigure) {
		ArrayList<Annotation> listAnnotation = FileHandling.getListAnnotations(fileXML);
		HashMap<String, ArrayList<String>> dataOfInstances = FileHandling.getAllInstancesFromConfigurationFile(fileConfigure);
		String bipModelName = FileHandling.getBIPModelNameFromXML(fileXML);
		ArrayList<Component> listComponent = FileHandling.getBIPComponentsFromXML(fileXML);
		StringBuilder sb = new StringBuilder();
		
		/**
		 * BIP Model
		 * ----------------------------------------
		 * */
		//generate package infor
		sb.append("package " + bipModelName + "\n");
		sb.append("\n\tport type Port()\n\n");
		//Generate the components in detail
		for (Component c : listComponent) {
			sb.append(c.generateComponent() + "\n\n");
		}
				
		for (Annotation anno : listAnnotation) {
			TreeNode root = new TreeNode("root.null", false, null);
			createTree(root, anno.getValue(), 0);
			sb.append(genBIPDefineCode(root, anno.getValue(), anno.getId()));
		}
		
		//Create Compound
		sb.append("\n\tcompound type " + bipModelName + "Compound()\n");
		
		for (Annotation anno : listAnnotation) {
			TreeNode root = new TreeNode("root.null", false, null);
			createTree(root, anno.getValue(), 0);
			sb.append(genDetailBIPConnector(root, anno.getValue(), anno.getId(), dataOfInstances));
		}
		sb.append("\tend\nend");
		System.out.println(sb);
	}
	
	/**
	 * Generate Connector Definition
	 * */
	public String genBIPDefineCode(TreeNode root, String connectorString, String annoId) {

		ArrayList<String> bipDefine = new ArrayList<String>();
		String bipDefString = "";
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n\tport type Port()\n\n");
		
		//Connector Definition
		root.genDefineBIP(bipDefine, annoId);
		bipDefString = bipDefine.stream().collect(Collectors.joining(""));
		sb.append(bipDefString);

		return sb.toString();
	}
	
	/**
	 * Generate Connector Definition
	 * */
	public String genDetailBIPConnector(TreeNode root, String connectorString, String annoId, HashMap<String, ArrayList<String>> dataOfInstances) {
		
		ArrayList<String> bipConnector = new ArrayList<String>();
		String bipConnectorStr = "";
		
		//Generating a template
		root.genBIPConnector(bipConnector, annoId);
		bipConnectorStr = bipConnector.stream().collect(Collectors.joining(""));
		
		//Generating all possible connectors 
		bipConnector = Combination.generateAllPossibleInstances(bipConnectorStr, dataOfInstances);
		
		return bipConnector.stream().collect(Collectors.joining("\n"));
	}
	
	public void createTree(TreeNode root, String connectorString, int index){
		
		if (connectorString.length() == 0)
			return ;

		int pos = connectorString.indexOf('[');
		//if it's flat
		if (pos == -1) {
			String[] elems = connectorString.split("-");
			//for all elements
			for(String e : elems) {
				String content = e.trim();
				boolean isTrigger = false;
				
				if (content.contains("`")) {
					isTrigger = true;
				}
				
				TreeNode elem = new TreeNode(content, isTrigger, root);
				root.getChildren().add(elem);
			}
			return ;
		} else {
			Stack<Character> stack = new Stack<>();
			stack.push(connectorString.charAt(pos));
			int q = pos + 1;
			while (q < connectorString.length()) {
				if (connectorString.charAt(q) == ']') {
					if (!stack.empty())
						stack.pop();
				} else if (connectorString.charAt(q) == '[') {
					stack.push(connectorString.charAt(q));
				}
				q++;
				if (stack.empty())
					break;
			}
			
			//before compound
			String baseLevelConnector = connectorString.substring(0, pos);
			createTree(root, baseLevelConnector, index);
			
			//the compound
			String nextLevelConnector = connectorString.substring(pos + 1, q-1);
			
			boolean isTrigger = false;
			if (q + 1 <= connectorString.length()) {
				String temp = connectorString.substring(pos, q+1);
				if (temp.charAt(temp.length()-1) == '`')
					isTrigger = true;
			}
			
			TreeNode compound = new TreeNode("c" + pos + index + ".null", isTrigger, root);
			root.getChildren().add(compound);
			createTree(compound, nextLevelConnector, index+1);
			
			//after compound
			if (q + 1 < connectorString.length()) {
				index = index+10;
				String remainStr = connectorString.substring(q + 1, connectorString.length());
				if (remainStr.indexOf("-") == 0) {
					remainStr = remainStr.substring(1);
				}
				createTree(root, remainStr, index+1);
			}
		}
	}
}
