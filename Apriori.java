

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

public class Apriori {
	
	// relative minimum support is set for frequent item calculation
	private static final Double minSupport = 0.002;	
	public static int numberOfLines;
	public static Hashtable<Integer, String> vocabHT = new Hashtable<>();
	public static ArrayList<ArrayList<Integer>> fileInputs = new ArrayList<>();
	
	public static void computeVocabHT(File file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line=br.readLine())!=null){
			String[] vocabs = line.split("	");
			vocabHT.put(Integer.parseInt(vocabs[0]), vocabs[1]);
		}
	}
	
	// Generate Initial Candidate Set for each file
	public static Hashtable<ArrayList<Integer>, Integer> getInitialCandidateSet(File file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		Hashtable<ArrayList<Integer>, Integer> ht = new Hashtable<>();
		String line;
		numberOfLines = 0;
		fileInputs = new ArrayList<>();
		
		// for each line, split the string on space, get the count and add it to hash table
		while((line=br.readLine())!=null){
			String[] sp = line.split(" ");
			
			ArrayList<Integer> tmpWords = new ArrayList<Integer>();
			for(int i = 0; i< sp.length; i++){
				ArrayList<Integer> word = new ArrayList<Integer>();
				word.add(Integer.parseInt(sp[i]));
				tmpWords.add(Integer.parseInt(sp[i]));
				if(ht.get(word)!=null){
					int count = ht.get(word);
					ht.put(word, count+1);
				}else{
					ht.put(word, 1);
				}
			}
			fileInputs.add(tmpWords);
			numberOfLines++;
		}
		return ht;		
	}
	
	// Get first frequent items L1
	public static Hashtable<ArrayList<Integer>, Integer> getL1(Hashtable<ArrayList<Integer>, Integer> c){
		double mSupport = minSupport * numberOfLines;
		Hashtable<ArrayList<Integer>, Integer> lTable = new Hashtable<>();
		
		// Check to see if value is greater than minimum support, then only add it to L1 Hash table
		for(ArrayList<Integer> ai : c.keySet()){
			if(c.get(ai) > mSupport){
				lTable.put(ai, c.get(ai));
			}
		}
		System.out.println("number of items in L1 is "+ lTable.size());
		return lTable;
	}
	
	// Check if the keys are frequent in previous item sets
	public static boolean checkIsInfrequent(ArrayList<Integer> ar, Hashtable<ArrayList<Integer>, Integer> prevItemSet){
			for(int i=0; i < ar.size();i++){
				ArrayList<Integer> tmpAr = new ArrayList<>();
				// for each combination of tuples, check if the value is frequent
				for(int j=0; j < ar.size(); j++){
					if(i != j){
						tmpAr.add(ar.get(j));
					}
				}
				if(prevItemSet.get(tmpAr) == null){
					return true;
				}
			}		
		return false;
	}
	
	   //This function generate candidate k - item sets, from frequent (k-1) item sets
    public static Hashtable<ArrayList<Integer>,Integer> selfJoin(Hashtable<ArrayList<Integer>,Integer> prevFreqItems,File file) throws IOException{
        int l = prevFreqItems.keySet().size();
        Hashtable<ArrayList<Integer>,Integer> itemsTable = new Hashtable<ArrayList<Integer>, Integer>();
        for(int i = 0; i < l-1; i++){
            ArrayList<Integer> firstSet = (ArrayList<Integer>) prevFreqItems.keySet().toArray()[i];
            int size = firstSet.size();
            for(int j = i+1; j < l; j++){
                ArrayList<Integer> latestResult = new ArrayList<Integer>();
                ArrayList<Integer> secondSet = (ArrayList<Integer>) prevFreqItems.keySet().toArray()[j];
                int isSameVal = 0;

                for(int k = 0; k <= size-2; k++)
                {
                    int a1 = firstSet.get(k);
                    int a2 = secondSet.get(k);
                    if(a1 != a2){
                    	isSameVal = 1;
                        break;
                    }
                    else{
                    	latestResult.add(firstSet.get(k));
                    }
                }

                if(isSameVal == 0) {
                    int tval = secondSet.get(secondSet.size() - 1);
                    latestResult.add(firstSet.get(firstSet.size() - 1));
                    latestResult.add(tval);
                    Collections.sort(latestResult);
                    
                    if(!checkIsInfrequent(latestResult, prevFreqItems))
                    	itemsTable.put(latestResult, 0);
                }
            }
        }
        System.out.println();
        return getFinalFreqPattens(itemsTable, fileInputs);
    }


 // Return Frequent patterns for each candidate set
    public static Hashtable<ArrayList<Integer>,Integer> getFinalFreqPattens(Hashtable<ArrayList<Integer>,Integer> selfJoinedSet, ArrayList<ArrayList<Integer>> topicItems){
       	for(ArrayList<Integer> keys : selfJoinedSet.keySet()){
            ArrayList<Integer> finFreqItems = (ArrayList<Integer>) keys;
            for(int i = 0; i <  topicItems.size(); i++){
                ArrayList<Integer> tempList = topicItems.get(i);
                int count = 0;
                for(int j = 0; j < finFreqItems.size(); j++){
                    int fl1 = finFreqItems.get(j);
                    for(int k = 0; k < tempList.size(); k++){
                        int fl2 = tempList.get(k);
                        if(fl1 == fl2) {
                            count++;
                            break;
                        }
                    }
                }

                if(count == finFreqItems.size()) {
                    int vals = selfJoinedSet.get(finFreqItems);
                    selfJoinedSet.put(finFreqItems,++vals);
                }
            }
        }

       // return candidates;
        double mSupport = minSupport * numberOfLines;
    	Hashtable<ArrayList<Integer>, Integer> freqPattern = new Hashtable<>();
    	
    		// Add to frequentItem set only if min support condition is met
    		for(ArrayList<Integer> ai : selfJoinedSet.keySet()){
    			if(selfJoinedSet.get(ai) > mSupport){
    				freqPattern.put(ai, selfJoinedSet.get(ai));
    			}
    		}
    		return freqPattern;
    }
	
	// write the final output of pattern, closed and max to file
	public  void writeResultToFile(Hashtable<?, Integer>  finalResult, int fileNumber, String type) throws FileNotFoundException, UnsupportedEncodingException{
		
		ArrayList<Map.Entry<?, Integer>> sortedList = new ArrayList(finalResult.entrySet());
			// Sort the list based on the Value of the Hashtable in Descending Order
	       Collections.sort(sortedList, new Comparator<Map.Entry<?, Integer>>(){
	         public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
	            return o2.getValue().compareTo(o1.getValue());
	        }});
	       
	       StringBuilder sb = new StringBuilder();
	       ArrayList<Integer> keyArray = new ArrayList<>();
	       PrintWriter writer;
	       
	       File file1 = new File(type);
			if (!file1.exists()) {
				if (file1.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
			
	       if(type.equals("patterns")){	    	  
	       		writer = new PrintWriter(file1+"/pattern-"+fileNumber+".txt", "UTF-8");
	       }
	       else if(type.equals("closed"))
	    	   writer = new PrintWriter(file1+"/closed-"+fileNumber+".txt", "UTF-8");
	       else
	    	   writer = new PrintWriter(file1+"/max-"+fileNumber+".txt", "UTF-8");
	       for(int i = 0; i<sortedList.size(); i++){
	    	   int val = sortedList.get(i).getValue();	    	   
	    	   keyArray = (ArrayList<Integer>) sortedList.get(i).getKey();	    	   
	    	   sb.setLength(0);
	    	   for(int j =0 ;j<keyArray.size();j++){
	    		   //texts+=refHT.get(lo.get(j))+" ";
	    		   sb.append(vocabHT.get(keyArray.get(j))).append(" ");
	    	   }
	    	   writer.println(val+" "+sb);
	       }
	       writer.close();
		
	}
	
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		Apriori ap = new Apriori();
		// Compute Hashtable for Vocab File
		File vocabFile = new File("vocab.txt");
		computeVocabHT(vocabFile);
		
		// set of files on which frequent pattern/ Closed Pattern needs to be mined
		String[] files = {
			"topic-0.txt",
			"topic-1.txt",
			"topic-2.txt",
			"topic-3.txt",
			"topic-4.txt"
		};

		// List of all freq items i.e. l1, l2, l3 of all the topic files
		ArrayList<Hashtable<ArrayList<Integer>, Integer>> allFrequentItemsPerFile = new ArrayList<>();
		for(int fno = 0 ;fno < files.length; fno++){
			ArrayList<Hashtable<ArrayList<Integer>, Integer>> frequentItemTable = new ArrayList<>();
			System.out.println("=================For "+fno+" file=======================");
			File file = new File(files[fno]);
			
			// Get initial Candidate set for each file
			Hashtable<ArrayList<Integer>, Integer> c1 = new Hashtable<>();
			c1 = getInitialCandidateSet(file);
			
			// Get first frequent items L1
			Hashtable<ArrayList<Integer>, Integer> L1 = new Hashtable<>();
			L1 = getL1(c1);
			
			// for computing rest of frequent items
			Hashtable<ArrayList<Integer>, Integer> C2 = new Hashtable<>();
			Hashtable<ArrayList<Integer>, Integer> finalResult = new Hashtable<>();
			
			// First add L1 to finalResult
			finalResult.putAll(L1);
			
			// Add L1 to frequentItemTable
			frequentItemTable.add(L1);
			int numOfFreqPatterns = L1.size();
			int i = 1;
			
			// Proceed only if First Frequent item set, L1 size is greater than zero
			while(numOfFreqPatterns > 0){
				
				// Based on Apriori algorithm, do self join and compute next frequent set
				C2 = selfJoin(L1, file);
				
				if(C2.size() > 0){
					
					// Add each frequent item set,L i's to finalResult, so to print in descending order once its done
					finalResult.putAll(C2);
					
					//Add each frequentItem SET L to frequentItemTable
					frequentItemTable.add(C2);
					System.out.println("number of items in L"+(++i)+" is "+C2.size());
					numOfFreqPatterns = C2.size();
					L1 = C2;
				}else{
					numOfFreqPatterns = 0;
				}
			}
			System.out.println("Final Result size is "+finalResult.size());
			allFrequentItemsPerFile.add(finalResult);
			// write final result to appropriate file in the format : <Support> <Phrases>
			ap.writeResultToFile(finalResult, fno, "patterns");
			
			// Calculate Closed Patterns
			ClosedPatterns cp = new ClosedPatterns();
			cp.getClosedPatterns(frequentItemTable, vocabHT, fno);
			
			// Calculate Max Patterns
			MaxPatterns mp = new MaxPatterns();
			mp.getMaxPatterns(frequentItemTable, vocabHT, fno);
								
			//Calculate Phraseness for frequent patterns
			Phraseness phr = new Phraseness();
			phr.getPhraseness(finalResult, numberOfLines, fno, vocabHT);
			
			//Calculate Coverage for frequent patterns
			Coverage cvr = new Coverage();
			cvr.calculateCoverage(finalResult, numberOfLines, fno, vocabHT);
		}
		System.out.println("fileInputs size is "+fileInputs.size());
		long endTime = System.currentTimeMillis();
        
		// Calculate New Purity Value
		PurityValue pv = new PurityValue();
		pv.getPurityForFreqItems(allFrequentItemsPerFile, vocabHT);
		
		System.out.println("DONE in "+(endTime - startTime) + " milliseconds");
	}

}
