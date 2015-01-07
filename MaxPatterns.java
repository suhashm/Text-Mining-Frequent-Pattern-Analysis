

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

public class MaxPatterns {

	Apriori fap = new Apriori();
	
	public void getMaxPatterns(ArrayList<Hashtable<ArrayList<Integer>, Integer>> freqPatterns, Hashtable<Integer, String> vocabHT, int fileNumber) throws FileNotFoundException, UnsupportedEncodingException{
		System.out.println("pattern size in maxpattern is "+freqPatterns.size());
		int count = 0;
		boolean shouldAdd = true;
		
		// Hashtable for final result of closed patterns
		Hashtable<ArrayList<Integer>, Integer> result = new Hashtable<>();
		
		// go till freqPatterns size-1 as last frequent pattern is always a Max pattern
		for(int i = 0; i < freqPatterns.size()-1; i++){
			Hashtable<ArrayList<Integer>, Integer> L1 = new Hashtable<>();
			Hashtable<ArrayList<Integer>, Integer> L2 = new Hashtable<>();
			L1 = freqPatterns.get(i);
			L2 = freqPatterns.get(i+1);
			for(ArrayList<Integer> b : L1.keySet()){
				shouldAdd = true;
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
								break;
							}
						}
					}
					// if the item from first frequent set is present in next frequent set, it is not a max pattern
					if(count == b.size()){
						shouldAdd = false;
						break;
					}else{
						shouldAdd = true;
					}
				}
				// Add the max pattern to result
				if(shouldAdd)
					result.put(b, L1.get(b));
			}
		}
		Hashtable<ArrayList<Integer>, Integer> interHT = new Hashtable<>();
		interHT = freqPatterns.get(freqPatterns.size()-1);
		result.putAll(interHT);
		System.out.println("Final Max Patterns size is "+result.size());
		
		// use the writeResultToFile method defined in Apriori.java to write the contents to the file
		fap.writeResultToFile(result, fileNumber, "max");
	}	
}
