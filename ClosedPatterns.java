

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;


public class ClosedPatterns {
	Apriori cap = new Apriori();
	
	// get closed patterns for list of frequent items for each file
	public void getClosedPatterns(ArrayList<Hashtable<ArrayList<Integer>, Integer>> freqPatterns, Hashtable<Integer, String> vocabHT, int fileNumber) throws FileNotFoundException, UnsupportedEncodingException{
	
		int count = 0;
		boolean shouldAdd = false;
		
		// Hashtable for final result of closed patterns
		Hashtable<ArrayList<Integer>, Integer> result = new Hashtable<>();
		
		// go till freqPatterns size-1 as last frequent pattern is always closed
		for(int i = 0; i < freqPatterns.size()-1; i++){
			Hashtable<ArrayList<Integer>, Integer> L1 = new Hashtable<>();
			Hashtable<ArrayList<Integer>, Integer> L2 = new Hashtable<>();
			L1 = freqPatterns.get(i);
			L2 = freqPatterns.get(i+1);
			for(ArrayList<Integer> b : L1.keySet()){
				shouldAdd = false;
				for(ArrayList<Integer> c : L2.keySet()){
					count = 0;
					
					// for each pattern, check if all the values are matching with next freq items
					for(int j = 0; j < b.size(); j++){
						for(int k = 0; k < c.size(); k++){
							int x = b.get(j);
							int y = c.get(k);
							if(x == y)
							{
								count++;
							}
						}
					}
					if(count == b.size()){
						// if all are matched and count of item in first frequent set less than that of second, it is not a closed pattern

						int k1 = L1.get(b);
						int k2 = L2.get(c);
						if( k1 == k2){
							shouldAdd = false;
							break;
						}
						else
							shouldAdd = true;
					}else{
						shouldAdd = true;
					}
				}
				// Add the closed patterns to final result
				if(shouldAdd)
					result.put(b, L1.get(b));
			}
		}
		
		ArrayList<ArrayList<Integer>> lastFreqPattern = new ArrayList<>();
		Hashtable<ArrayList<Integer>, Integer> interHT = new Hashtable<>();
		interHT = freqPatterns.get(freqPatterns.size()-1);
		
		// Last frequent item set is always closed
		result.putAll(interHT);
		System.out.println("Final Closed Patterns size "+result.size());
		
		// use the writeResultToFile method defined in Apriori.java to write the contents to the file
		cap.writeResultToFile(result, fileNumber, "closed");
		}

}
