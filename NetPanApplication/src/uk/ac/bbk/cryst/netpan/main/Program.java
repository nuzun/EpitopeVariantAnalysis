package uk.ac.bbk.cryst.netpan.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.common.PropertiesHelper;
import uk.ac.bbk.cryst.netpan.dao.GroupDataDaoImpl;
import uk.ac.bbk.cryst.netpan.model.GroupData;
import uk.ac.bbk.cryst.netpan.util.NetPanCmd;

public class Program {
	
	/**
	 * @param args
	 * Start position in the peptide starts from 0 for CTL.
	 * Start position for a peptide sequence starts from 0 for NetMHCPan
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		PropertiesHelper properties = new PropertiesHelper();
		
		//parameters
		String peptideLength = "9";
		String scoreCode = "0"; //MHC(1) or comb (0) used for CTL only
		String inputPath = properties.getValue("inputPath");
		String outputBasePath = properties.getValue("factorOutputPathCTL");
		PredictionType type = PredictionType.CTL;
		String alleleFileFullPath = inputPath + "testI.csv";
		
		//read the input sequence(s)
		try {
			File sequencePath = new File(properties.getValue("sequencePath"));
		
			for(final File fileEntry : sequencePath.listFiles()){
		 		if(fileEntry.isDirectory()){
		 			continue;
		 		}
		 		
		 		//afp_P02771.fasta
		 		String fileName = fileEntry.getName();
		 		String sequenceName = fileName.substring(0,fileName.indexOf("_")); //afp
		 		String sequenceFileFullPath = fileEntry.getPath(); //full path of afp_P02771

			 	//Read the alleles from region/group of alleles file
				GroupDataDaoImpl groupDao = new GroupDataDaoImpl(alleleFileFullPath);
			 	GroupData groupData = groupDao.getGroupData();
			 		
			 	//creating the output folder: base/afp/selectedAlleles/
			 	String outputPath = outputBasePath + sequenceName + "/" +FilenameUtils.removeExtension(groupData.getSourceFileName());
			 		
		 		File file = new File(outputPath);
		 		if (!file.exists()) {
		 			if (file.mkdir()) {
		 				System.out.println("Directory "+ outputPath +" is created!");
		 			} else {
		 				System.out.println("Failed to create directory: " + outputPath + "!");
		 			}
		 		}
		 		
		 		//for each allele, generate the scores
		 		for(String allele : groupData.getAlleleMap().keySet()){
		 			
		 			String outputFileFullPath = outputPath + "/" + FilenameUtils.removeExtension(fileName) +"_"+ allele + ".txt";
		 			
		 			NetPanCmd.run(type,scoreCode,peptideLength,allele,sequenceFileFullPath,outputFileFullPath);
					
			 	}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
