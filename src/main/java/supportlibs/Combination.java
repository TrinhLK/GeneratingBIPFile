package supportlibs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Combination {
    public static void main (String[] args) { 
//    	Combination testCombination = new Combination();
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        String test = "Person talked to Person";
        String test1 = "Person requires (Person, Person, Itemize, Itemize)";
        //Just to populate.
        ArrayList<String> input = new ArrayList<String>();
        ArrayList<String> input1 = new ArrayList<String>();
        input.add("p1");
        input.add("p2");
        input.add("p3");
        input1.add("it1");
        input1.add("it2");
        input1.add("it3");
        data.put("Person", input);
        data.put("Itemize", input1);
        generateAllPossibleInstances(test, data);
        generateAllPossibleInstances(test1, data);
        
    }
    
    /**
     * Gen instances from schema
     * */
    public static ArrayList<String> generateAllPossibleInstances(String connectorString,  HashMap<String, ArrayList<String>> data){
    	ArrayList<String> result = new ArrayList<String>();
		result.add(connectorString);
//		System.out.println("data set:" + data);
        for (String akey : data.keySet()) {
//        	System.out.println("considering: " + akey);
        	ArrayList<String> tempResult = new ArrayList<String>();
        	for (String anInput : result) {
//        		System.out.println("check: " + anInput + "\t" + countMatches(anInput, akey) + "\t" + data.get(akey).size());
//        		System.out.println("check1: " + data.get(akey));
        		if (countMatches(anInput, akey) > data.get(akey).size()) {
                	System.out.println("Error: the number of value {" + akey + "} is lesser than the number of key in the input");
                	return null;
            	}else {
            		
//            		System.out.println("test k,v: " + data.get(akey));
            		ArrayList<ArrayList<String>> listCombine = new ArrayList<ArrayList<String>>();
            		ArrayList<ArrayList<String>> listPermute = new ArrayList<ArrayList<String>>();
                	int k = countMatches(anInput, akey);
                	String[] tempRs = new String[k];
                	//Combination k of n
                	combinationsString(data.get(akey), k, 0, tempRs, listCombine);
                	//permuteString
                	for (ArrayList<String> elems : listCombine) {
                		permuteString(elems, 0, listPermute);
                	}
//                	System.out.println("----After permute");
//            		System.out.println(listPermute);
                	for (ArrayList<String> aPermute : listPermute) {
//                		System.out.println("Before replace: " + akey + "\t" + aPermute);
                		String akeyName = akey + ".";
                		ArrayList<String> aPermuteName = new ArrayList<String>();
                		for (String per : aPermute) {
                			aPermuteName.add(per + ".");
                		}
//                		String aPermuteName = aPermute + ".";
                		
                		tempResult.add(replaceAStringByHashMapValues(anInput, akeyName, aPermuteName));
                	}
                	System.out.println("size: " + tempResult.size() + "\n" + tempResult);
            	}
        		result = tempResult;
        	}
        }
//        System.out.println("size: " + result.size());
        for (int i=0 ; i<result.size() ; i++) {
        	String temp = result.get(i).replaceAll("CompInstanceId", i+"");
        	result.set(i, temp);
//        	System.out.println(result.get(i));
        }
        System.out.println(connectorString);
        System.out.println("-------------------------");
        int i=0;
        for (String s : result) {
        	i++;
        	System.out.println(i + ":\t" + s);
        }
        return result;
    }
    
    private static String replaceAStringByHashMapValues(String input, String replaceValue, ArrayList<String> data) {
    	for (String aValue : data) {
//    		System.out.println("CHECK REPLACING: " + input + "\t" + replaceValue + "\t" + data);
    		input = input.replace(replaceValue, aValue);
    	}
    
    	return input;
    }
    
    private static void combinationsString(ArrayList<String> input, int len, int startPosition, String[] result, ArrayList<ArrayList<String>> listCombine){
        if (len == 0){
        	listCombine.add(new ArrayList<String>(Arrays.asList(result)));
            return;
        }       
        for (int i = startPosition; i <= input.size()-len; i++){
            result[result.length - len] = input.get(i);
            combinationsString(input, len-1, i+1, result, listCombine);
        }
    }

    private static void permuteString(ArrayList<String> str, int index, ArrayList<ArrayList<String>> result) {
    	if(index >= str.size() - 1){//If we are at the last element - nothing left to permute
    		
    		ArrayList<String> arrayListClone =  (ArrayList<String>) str.clone();
    		result.add(arrayListClone);
    		return;
    	}
    	
    	for(int i = index; i < str.size(); i++){
            Collections.swap(str, index, i);	 //Swap the elements at indices index and i
            permuteString(str, index+1, result);	//Recurse on the sub array arr[index+1...end]
            Collections.swap(str, index, i);	//Swap the elements back
    	}
    }
    
    private static int countMatches(String original, String check) {
    	int count = 0, index = 0;
    	ArrayList<Integer> indices=new ArrayList<Integer>();
        while ((index = original.indexOf(check+".", index)) != -1 ){
            count++;
            indices.add(index);
            index++;
        }
        return count;
    }
}
/**
 * 
 * public static void myReplace(String input, String replaceValue, HashMap<String, ArrayList<String>> data, int indexOfValue) {

    	while (input.contains(replaceValue)) {
    		
    		if (indexOfValue >= data.get(replaceValue).size()-1) {
    			indexOfValue = indexOfValue % data.get(replaceValue).size();
    		}
    		input = input.replaceFirst(replaceValue, data.get(replaceValue).get(indexOfValue));
			indexOfValue++;
    	}
    	System.out.println(input);
    }
 * static void combinations2(String[] arr, int len, int startPosition, String[] result){
        if (len == 0){
            System.out.println(Arrays.toString(result));
            return;
        }       
        for (int i = startPosition; i <= arr.length-len; i++){
            result[result.length - len] = arr[i];
            combinations2(arr, len-1, i+1, result);
        }
    }
    public static void permute(int[] arr){
        permuteHelper(arr, 0);
    }
 private static void permuteHelper(int[] arr, int index){
        if(index >= arr.length - 1){ //If we are at the last element - nothing left to permute
            //System.out.println(Arrays.toString(arr));
            //Print the array
            System.out.print("[");
            for(int i = 0; i < arr.length - 1; i++){
                System.out.print(arr[i] + ", ");
            }
            if(arr.length > 0) 
                System.out.print(arr[arr.length - 1]);
            System.out.println("]");
            return;
        }

        for(int i = index; i < arr.length; i++){ //For each index in the sub array arr[index...end]

            //Swap the elements at indices index and i
            int t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;

            //Recurse on the sub array arr[index+1...end]
            permuteHelper(arr, index+1);

            //Swap the elements back
            t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;
        }
    }
    
     public static void combination(List<String> list, int k, String accumulated){

    	// 1. stop
    	if(list.size() < k)
    		return;
    	
    	// 2. add each element in e to accumulated
    	if(k == 1)
    		for(String s:list)
    			System.out.println(accumulated+s);
    	
    	// 3. add all elements in e to accumulated
    	else if(list.size() == k){
    		for(String s:list)
    			accumulated+=s;
    		System.out.println(accumulated);
    	}
    	
    	// 4. for each element, call combination
    	else if(list.size() > k)
    		for(int i = 0 ; i < list.size() ; i++)
    			combination(list.subList(i+1, list.size()), k-1, accumulated+list.get(i));
    	
    }
 * */
/*
 * 
  arr[]  ---> Input Array 
    data[] ---> Temporary array to store current combination 
    start & end ---> Staring and Ending indexes in arr[] 
    index  ---> Current index in data[] 
    r ---> Size of a combination to be printed 
    static void combinationUtil(String arr[], String data[], int start, 
                                int end, int index, int r) 
    { 
        // Current combination is ready to be printed, print it 
        if (index == r) 
        { 
            for (int j=0; j<r; j++) 
                System.out.print(data[j]+" "); 
            System.out.println(""); 
            return; 
        } 
  
        // replace index with all possible elements. The condition 
        // "end-i+1 >= r-index" makes sure that including one element 
        // at index will make a combination with remaining elements 
        // at remaining positions 
        for (int i=start; i<=end && end-i+1 >= r-index; i++) 
        { 
            data[index] = arr[i]; 
            combinationUtil(arr, data, i+1, end, index+1, r); 
        } 
    } 
  
    // The main function that prints all combinations of size r 
    // in arr[] of size n. This function mainly uses combinationUtil() 
    static void printCombination(String arr[], int n, int r) 
    { 
        // A temporary array to store all combination one by one 
    	String data[] = new String[r]; 
  
        // Print all combination using temprary array 'data[]' 
        combinationUtil(arr, data, 0, n-1, 0, r); 
    } 
    /*Driver function to check for above function
 * */
