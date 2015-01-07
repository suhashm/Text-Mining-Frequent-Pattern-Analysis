

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

public class Phraseness {
	
// Once phraseness is obtained, it is written to file under directory phraseness
public  void writePhraseCoverageToFile(Hashtable<?, Double>  finalResult, int fileNumber, Hashtable<Integer, String> vocabHT, String type) throws FileNotFoundException, UnsupportedEncodingException{
				
		ArrayList<Map.Entry<?, Double>> sortedList = new ArrayList(finalResult.entrySet());
			// Sort the list based on the Value of the Hashtable in Descending Order
	       Collections.sort(sortedList, new Comparator<Map.Entry<?, Double>>(){
	         public int compare(Map.Entry<?, Double> o1, Map.Entry<?, Double> o2) {
	        	 		 return o2.getValue().compareTo(o1.getValue());
	        }});
	       
	       StringBuilder sb = new StringBuilder();
	       ArrayList<Double> keyArray = new ArrayList<>();
	       PrintWriter writer;
	       File file1;
	       if(type.equals("phraseness")){
	    	    file1 = new File("phraseness");
	       }
	       else{
	    	    file1 = new File("coverage");
	    }
			if (!file1.exists()) {
				if (file1.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
			if(type.equals("phraseness"))
				writer = new PrintWriter(file1+"/phraseness-"+fileNumber+".txt", "UTF-8");
			else
				writer = new PrintWriter(file1+"/coverage-"+fileNumber+".txt", "UTF-8");
	     
	       for(int i = 0; i<sortedList.size(); i++){
	    	   Double val = sortedList.get(i).getValue();	    	   
	    	   keyArray = (ArrayList<Double>) sortedList.get(i).getKey();	    	   
	    	   sb.setLength(0);
	    	   for(int j =0 ;j<keyArray.size();j++){
	    		   sb.append(vocabHT.get(keyArray.get(j))).append(" ");
	    	   }
	    	   writer.println(val+" "+sb);
	       }
	       writer.close();
		
	}

	// calculate phraseness for each of the frequent item of files
	public void getPhraseness(Hashtable<ArrayList<Integer>, Integer> freqItems, int numOfLines, int fno, Hashtable<Integer, String> vocabHT) throws FileNotFoundException, UnsupportedEncodingException{
		int k = 0;
		Hashtable<ArrayList<Integer>, Double> phrasenessResult = new Hashtable<>();
		for(ArrayList<Integer> fItems : freqItems.keySet()){
			Double res = 0.0;
			if(fItems.size() == 1){
				int ftp = freqItems.get(fItems);
				res = Math.log(ftp/(numOfLines * 1.0));
				phrasenessResult.put(fItems, (double) Math.round(res * 10000) / 10000);
			}else{
				int ftp = freqItems.get(fItems);
				Double sum = 0.0;
				for(int i = 0; i < fItems.size(); i++){
					ArrayList<Integer> aba = new ArrayList<>();
					aba.add(fItems.get(i));
					int ftw = freqItems.get(aba);
					sum+=Math.log((ftw/(numOfLines * 1.0)));
				}
				res = Math.log(ftp/(numOfLines * 1.0)) - sum;
				phrasenessResult.put(fItems, (double) Math.round(res * 10000) / 10000);
			}
		}
		writePhraseCoverageToFile(phrasenessResult, fno, vocabHT, "phraseness");
	}
}
