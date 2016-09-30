package uk.ac.bbk.cryst.netpan.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.common.PropertiesHelper;
import uk.ac.bbk.cryst.netpan.model.NetPanData;

public class NetPanDataBuilder {
	
	String pattern;
	PredictionType type;
	
	public NetPanDataBuilder(PredictionType type) throws IOException{
		PropertiesHelper properties = new PropertiesHelper();
		this.type = type;
		
		if(this.type == PredictionType.MHCII){
			pattern = properties.getValue("scoreFileNamePatternMHCII");
		}
		else pattern = properties.getValue("scoreFileNamePatternMHCI");
	}
	
	public List<NetPanData> buildFileData(PredictionType type, File outputDir) throws Exception{
		
		List<NetPanData> netPanDataList = new ArrayList<NetPanData>();
	
		//for each file in the folder check the name
		//if it matches our input, then find the rank of the peptide
		for(final File fileEntry : outputDir.listFiles()){
	 		if(fileEntry.isDirectory()){
	 			//System.exit(0);
	 			continue;
	 		}
	 		
	 		// Create a Pattern object
	 	    Pattern r = Pattern.compile(pattern);
	 	    // Now create matcher object.
	 	    Matcher m = r.matcher(fileEntry.getName());
	 	    
	 	    if (m.find( )) {
	 	    	String foundFileName = m.group(2);
	 	    	String foundAllele = m.group(3);
	 	    	if(StringUtils.isNotEmpty(foundFileName)){
	 	    		//found the file
	 	    		//read the file and find the rank
	 	    		//return the rank with the supertype
	 	    		NetPanFileReader reader = NetPanFileReaderFactory.getReader(type,fileEntry,foundFileName,foundAllele);
	 	    		NetPanData netPanData  = reader.read();
	 	    		netPanDataList.add(netPanData);
	 	    	}

	 	    } else {
	 	         //no match. this is not our filetype  
	 	    	continue;
	 	    }
	 	
	 	}
		
		Collections.sort(netPanDataList);
		return netPanDataList;
	}

	//return single file object with the specified allele and filename
	public  NetPanData buildFileData(PredictionType type,String fastaFileName, String alleleName, File outputDir) throws Exception{
		//for each file in the folder check the name
		//if it matches our input, then find the rank of the peptide
		for(final File fileEntry : outputDir.listFiles()){
	 		if(fileEntry.isDirectory()){
	 			//System.exit(0);
	 			continue;
	 		}
	 		
	 		// Create a Pattern object
	 	    Pattern r = Pattern.compile(pattern);
	 	    // Now create matcher object.
	 	    Matcher m = r.matcher(fileEntry.getName());
	 	    
	 	    if (m.find( )) {
	 	    	String foundFileName = m.group(2);
	 	    	String foundAllele = m.group(3);
	 	    	if(StringUtils.isNotEmpty(foundFileName) && StringUtils.equals(foundFileName, fastaFileName) && 
	 	    			StringUtils.equals(foundAllele, alleleName)){
	 	    		//found the file
	 	    		//read the file and find the rank
	 	    		//return the rank with the supertype
	 	    		NetPanFileReader reader = NetPanFileReaderFactory.getReader(type,fileEntry,foundFileName,foundAllele);
	 	    		NetPanData netPanData  = reader.read();
	 	    		return netPanData;
	 	    	}
	 	    	
	 	    	else if(StringUtils.isNotEmpty(foundFileName) && foundFileName.startsWith(fastaFileName) && 
	 	    			StringUtils.equals(foundAllele, alleleName)){
	 	    		NetPanFileReader reader = NetPanFileReaderFactory.getReader(type,fileEntry,foundFileName,foundAllele);
	 	    		NetPanData netPanData  = reader.read();
	 	    		return netPanData;
	 	    	}

	 	    } else {
	 	         //no match. this is not our filetype  
	 	    	continue;
	 	    }
	 	
	 	}
		
		return null;
	}	
}
