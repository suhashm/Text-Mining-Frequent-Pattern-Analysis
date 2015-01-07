

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

public class PurityValue {

	public static int dTableVal[][] = {    {10047,17326,17988,17999,17820} , 
		{17326,9674,17446,17902,17486},
		{17988,17446,9959,18077,17492},
		{17999,17902,18077,10161,17912},
		{17820,17486,17492,17912,9845}
};

// writing the purity result to file under folder purity folder
public  static void writePurityResultToFile(Hashtable<?, Double>  finalResult, Hashtable<ArrayList<Integer>, Double>  comparePurity, int fileNumber, Hashtable<Integer, String> vocabHT, Hashtable<ArrayList<Integer>, Integer> freqItems) throws FileNotFoundException, UnsupportedEncodingException{
		
	ArrayList<Map.Entry<?, Double>> sortedList = new ArrayList(finalResult.entrySet());
	
		// Sort the list based on the Value of the Hashtable in Descending Order
       Collections.sort(sortedList, new Comparator<Map.Entry<?, Double>>(){
         public int compare(Map.Entry<?, Double> o1, Map.Entry<?, Double> o2) {
        		 return o2.getValue().compareTo(o1.getValue());
        }});
       
       StringBuilder sb = new StringBuilder();
       ArrayList<Double> keyArray = new ArrayList<>();
       PrintWriter writer;	     
       File file1 = new File("purity");
		if (!file1.exists()) {
			if (file1.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
       	writer = new PrintWriter(file1+"/purity"+fileNumber+".txt", "UTF-8");
     
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
	// calculate the purity value and then calculate relative ranking i.e purity * relative frequency of item
	public void getPurityForFreqItems(ArrayList<Hashtable<ArrayList<Integer>, Integer>> compFItems, Hashtable<Integer, String> vocabHT) throws FileNotFoundException, UnsupportedEncodingException{
		for(int i = 0; i < compFItems.size(); i++){
			Hashtable<ArrayList<Integer>, Integer> freqTable = new Hashtable<>();
			Hashtable<ArrayList<Integer>, Double> purityResult = new Hashtable<>();
			Hashtable<ArrayList<Integer>, Double> rankedResult = new Hashtable<>();
			freqTable = compFItems.get(i);
			int dt = dTableVal[i][i];
			for(ArrayList<Integer> item : freqTable.keySet()){
				double sum = 0.0;
				int ftp = freqTable.get(item);
				for(int j = 0; j < compFItems.size(); j++){
					if(i != j){
						int dtt = dTableVal[i][j];
						Hashtable<ArrayList<Integer>, Integer> tempTable = new Hashtable<>();
						tempTable = compFItems.get(j);
						int fttp = 0;
						if(tempTable.get(item) != null){
							fttp = tempTable.get(item);
							if(item.size() == 1 && item.get(0) == 382){
								double res1 = (double)(ftp + fttp) / (dtt * 1.0);
							}
						}
						double res = (double)(ftp + fttp) / (dtt * 1.0);
						if(res > sum)
							sum = res;
					}
				}
				double purityVal = Math.log((double)(ftp/ (dt * 1.0))) - Math.log(sum);
				double resPurity = (double) Math.round(purityVal * 10000) / 10000;
				
				// calculate ranking by using measure which is purity * relative frequency of the item
				double resRanking = (double) (resPurity * (ftp/(dt * 1.0)));
				double resRanking1 = (double) Math.round(resRanking * 1000000) / 1000000; // rounding purity to 4 decimal places
				
				purityResult.put(item, resPurity);
				rankedResult.put(item, resRanking1);
			}
			writePurityResultToFile(rankedResult,purityResult, i, vocabHT, freqTable);
		}
	}
}
