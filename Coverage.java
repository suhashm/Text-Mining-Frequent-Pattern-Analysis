

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Coverage {
	
	// Calculate coverage for each of the frequent items in topic and then re rank
	public void calculateCoverage(Hashtable<ArrayList<Integer>, Integer> freqItems, int numOfLines, int fno, Hashtable<Integer, String> vocabHT) throws FileNotFoundException, UnsupportedEncodingException{
		Hashtable<ArrayList<Integer>, Double> coverageResult = new Hashtable<>();
		for(ArrayList<Integer> fItems: freqItems.keySet()){
			double res = 0.0;
			res = (double) freqItems.get(fItems) / numOfLines * 1.0;
			// rounding off to 4 digits
			coverageResult.put(fItems, (double) Math.round(res * 10000) / 10000);
		}
		Phraseness phr = new Phraseness();
		phr.writePhraseCoverageToFile(coverageResult, fno, vocabHT, "coverage");
	}
}
