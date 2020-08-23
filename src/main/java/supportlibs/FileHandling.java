package supportlibs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileHandling {

	public static void createFiles(String fileName, String content) {
		try (FileWriter writer = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(content);
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}
	
	public static ArrayList<String> splitBrackets(String input) {
		String result[] = input.split("[\\(||\\)]");
		ArrayList<String> rs = new ArrayList<String>();
		for (String s: result) {
			s = s.trim();
			if (s.length() > 0)
				rs.add(s);
		}
		
		return rs;
	}
	
	public static String createConcreteData(String inputStr, HashMap<String, ArrayList<String>> listComponent) {
    	HashMap<String, String> oldAndNew = new HashMap<String, String>();
    	
    	ArrayList<String> listElems = splitBrackets(inputStr);
    	
    	for (String elems : listElems) {
    		String tempSplit[] = elems.split(" ");
        	String input = "";
        	for (String s : tempSplit) {
        		if (s.contains(".")) {
        			input = s;
        		}
        	}
        	//find key of listComponent in input, if existed, replaced by list value, save them into oldAndNew
        	for (HashMap.Entry<String, ArrayList<String>> entry : listComponent.entrySet()) {
//    		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
    		    if (input.contains(entry.getKey())){
    		    	String tempRs = "";
    		    	if (entry.getValue().size() == 1) {
    		    		tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(entry.getValue().size()-1));
    		    	} else {
    		    		tempRs += "(+ ";
    		    		for (int i=0 ; i<entry.getValue().size() ; i++) {
        		    		tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(i)) + " ";
        		    	}
    		    		tempRs += ")";
    		    	}
//    		    	for (int i=0 ; i<entry.getValue().size()-1 ; i++) {
//    		    		tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(i)) + " + ";
//    		    	}
//    		    	tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(entry.getValue().size()-1));
//    		    	
//    		    	System.out.println(input + "\t->\t" + tempRs);
    		    	oldAndNew.put(input, tempRs);
    		    }
    		}
    	}
    	
    	for (HashMap.Entry<String, String> entry : oldAndNew.entrySet()) {
    		if (inputStr.contains(entry.getKey())) {
    			inputStr = inputStr.replaceAll(entry.getKey(), entry.getValue());
    		}
    	}
    	inputStr = inputStr.replaceAll("\\.", "_");
//    	System.out.println(inputStr);
    	return inputStr;
    }
	/**
	 * Get list annotations properties from the XML file
	 * */
	public static ArrayList<Annotation> getListPropertiesAnnotations(String fileName) {
		ArrayList<Annotation> listAnnotation = new ArrayList<Annotation>();
		
	    try {
	    	File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			NodeList nListAnnos = doc.getElementsByTagName("annotations");
			for (int iAnnos=0 ; iAnnos<nListAnnos.getLength() ; iAnnos++) {
				Node curAnnos = nListAnnos.item(iAnnos);
				if (curAnnos.getNodeType() == Node.ELEMENT_NODE) {
					if (curAnnos.hasAttributes()) {
						NamedNodeMap nodeMapAnnos = curAnnos.getAttributes();
						for (int jAnnos = 0; jAnnos < nodeMapAnnos.getLength(); jAnnos++) {
							
							Node nodeAnnos = nodeMapAnnos.item(jAnnos);
							if (nodeAnnos.getNodeName().equals("name")) {
								if (nodeAnnos.getNodeValue().equalsIgnoreCase("Specification")) {
									NodeList nList = curAnnos.getChildNodes();
									//System.out.println("Check properties: " + nList.getLength());

									for (int i=0 ; i<nList.getLength() ; i++) {
										Node curAnno = nList.item(i);
										//System.out.println(curAnno.getNodeName() + "\t" + curAnno.getNodeValue());
										String id, value;
										id = value = "";
										if (curAnno.getNodeType() == Node.ELEMENT_NODE) {
											if (curAnno.hasAttributes()) {
												NamedNodeMap nodeMap = curAnno.getAttributes();
												for (int j = 0; j < nodeMap.getLength(); j++) {
													
													Node node = nodeMap.item(j);
													if (node.getNodeName().equals("id")) {
														id = node.getNodeValue();
													}
												}
											}
										}
										value = curAnno.getTextContent();
										
										if (value.contains("prop:")) {
											listAnnotation.add(new Annotation(id, value));
											System.out.println(value);
										}
											
									}
								}
							}
						}
					}
				}
			}
	    }catch (Exception e) {
			// TODO: handle exception
		}
	    return listAnnotation;
	}
	
	/**
	 * Get BIP Components From the XML file
	 * */
	public static ArrayList<Component> getBIPComponentsFromXML(String fileName) {
		ArrayList<Component> listComponent = new ArrayList<Component>();
	    try {

			File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList nList = doc.getElementsByTagName("class");
			
			
			for (int i=0 ; i<nList.getLength() ; i++) {
				boolean shouldAdd = true;
				Component tempComponent = getComponentsInfor(nList.item(i).getChildNodes());
				Node curClass = nList.item(i);
				
				if (curClass.getNodeType() == Node.ELEMENT_NODE) {
					if (curClass.hasAttributes()) {
						NamedNodeMap nodeMap = curClass.getAttributes();
						for (int j = 0; j < nodeMap.getLength(); j++) {
							
							Node node = nodeMap.item(j);
							if (node.getNodeName().equals("parent") && node.getNodeValue().equalsIgnoreCase("link")) {
								shouldAdd = false;
								break;
							}
							
							if (node.getNodeName().equals("name")) {
//								System.out.println("class name: " + node.getNodeValue());
								tempComponent.setType(node.getNodeValue());
							}
						}
					}
				}
				
//				tempComponent.printComponent();
				if (shouldAdd) listComponent.add(tempComponent);
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	    
	    return listComponent;
	}
	/**
	 * Get list annotations from the XML file
	 * */
	public static ArrayList<Annotation> getListAnnotations(String fileName) {
		ArrayList<Annotation> listAnnotation = new ArrayList<Annotation>();
		
	    try {
	    	File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			NodeList nListAnnos = doc.getElementsByTagName("annotations");
			for (int iAnnos=0 ; iAnnos<nListAnnos.getLength() ; iAnnos++) {
				Node curAnnos = nListAnnos.item(iAnnos);
				if (curAnnos.getNodeType() == Node.ELEMENT_NODE) {
					if (curAnnos.hasAttributes()) {
						NamedNodeMap nodeMapAnnos = curAnnos.getAttributes();
						for (int jAnnos = 0; jAnnos < nodeMapAnnos.getLength(); jAnnos++) {
							
							Node nodeAnnos = nodeMapAnnos.item(jAnnos);
							if (nodeAnnos.getNodeName().equals("name")) {
								if (nodeAnnos.getNodeValue().equalsIgnoreCase("Specification")) {
									
									NodeList nList = curAnnos.getChildNodes();
									System.out.println("list Anno: " + nList.getLength());
									for (int i=0 ; i<nList.getLength() ; i++) {
										
										Node curAnno = nList.item(i);
										if (curAnno.getNodeType() == Node.ELEMENT_NODE) {
											if (curAnno.hasAttributes()) {
											
												NamedNodeMap nodeMap = curAnno.getAttributes();
												for (int j = 0; j < nodeMap.getLength(); j++) {	
													Node node = nodeMap.item(j);
													if (node.getNodeName().equals("id")) {
														System.out.println("Normal anno: " + node.getNodeValue().replaceAll("-", "_") + "\t" + curAnno.getTextContent());
														if (!curAnno.getTextContent().contains("prop: ") && !curAnno.getTextContent().contains("data: "))
															listAnnotation.add(new Annotation(node.getNodeValue().replaceAll("-", "_"), curAnno.getTextContent()));
													}
												}
											}
										}								
									}
								}
							}
						}
					}
				}
			}
			
	    }catch (Exception e) {
			// TODO: handle exception
		}
	    return listAnnotation;
	}
	
	/**
	 * Get all instances of each class from the configuration file
	 * @input: configuration file's name
	 * */
	public static HashMap<String, ArrayList<String>> getAllInstancesFromConfigurationFile(String fileName) {
		ComponentInstance ci = new ComponentInstance();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {

                if (line.contains("resource")) {
                	String rs = line.substring(line.lastIndexOf(":")+1).trim();
//                	System.out.println(rs);
                    String className = rs.substring(rs.indexOf(".")+1, rs.indexOf("title")).trim();
                    String objName = rs.substring(rs.indexOf("\"")+1, rs.lastIndexOf("\"")).trim();
                    ci.add(className, objName);
                }
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return ci.getMapInstance();
	}
	
	/**
	 * Get BIP Model name
	 * */
	public static String getBIPModelNameFromXML(String fileName) {
		String rs = "";
		
		try {

			File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList nList = doc.getElementsByTagName("extension");
			
			for (int i=0 ; i<nList.getLength() ; i++) {
				Node curExtension = nList.item(i);
				if (curExtension.getNodeType() == Node.ELEMENT_NODE) {
					if (curExtension.hasAttributes()) {
						NamedNodeMap nodeMap = curExtension.getAttributes();
						for (int j = 0; j < nodeMap.getLength(); j++) {
							Node node = nodeMap.item(j);
							if (node.getNodeName().equals("name")) {
								rs = node.getNodeValue();
							}
						}
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	
	
	public static Component getComponentsInfor(NodeList nodeList) {

//		Component component = new Component();
		ArrayList<Variable> variables = new ArrayList<Variable>();
		ArrayList<String> actions = new ArrayList<String>();
		ArrayList<States> states = new ArrayList<States>();
		ArrayList<Transition> listTrans = new ArrayList<Transition>();
		
		for (int count = 0; count < nodeList.getLength(); count++) {
		
			Node tempNode = nodeList.item(count);
			
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				
				if (tempNode.getNodeName().equals("variable")) {
					String type, name, value;
					type = name = value = "";
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("type"))
								type = node.getNodeValue().toLowerCase();
							if (node.getNodeName().equals("name"))
								name = node.getNodeValue().replaceAll("\\.", "_");
							if (node.getNodeName().equals("value"))
								value = node.getNodeValue();
						}
					}
					variables.add(new Variable(type, name, value));
				}
				
				if (tempNode.getNodeName().equals("action")) {
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("name"))
								actions.add(node.getNodeValue());
						}
					}
				}
				
				if (tempNode.getNodeName().equals("place")) {
					String name = "";
					boolean init = false, finalS = false;
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("name")) {
								name = node.getNodeValue();
							}
							
							if (node.getNodeName().equals("initial")) {
								init = Boolean.parseBoolean(node.getNodeValue());
							}
							
							if (node.getNodeName().equals("final")) {
								finalS = Boolean.parseBoolean(node.getNodeValue());
							}
							
						}
					}
					states.add(new States(name, init, finalS));
				}
				
				if (tempNode.getNodeName().equals("transition")) {
					String from, to, action;
					from = to = action = "";
					
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("from")) {
								from = node.getNodeValue();
							}
							
							if (node.getNodeName().equals("to")) {
								to = node.getNodeValue();
							}
							
							if (node.getNodeName().equals("action")) {
								action = node.getNodeValue();
							}
							
						}
					}
					listTrans.add(new Transition(from, to, action));
				}
				
//				System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
			}
		}
		return new Component("", "", variables, actions, states, listTrans);
	}
}
