package com.zarodha.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Jai1.Kumar
 *
 */
public class CSVReader {

	public List<String> getInstruments(String csvFile){
		
		List<String> instrumentsList = new ArrayList<>();
        String line = "";
        String cvsSplitBy = ",";
        int i = 0;
        try (
        		BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator for multiple values
            	if(i != 0){
                String[] instruments = line.split(cvsSplitBy);
                if(instruments[2] != null)
                		instrumentsList.add(instruments[11]+":"+instruments[2]);
            	} else {
            		i++;
            	}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return instrumentsList;
	}
}
