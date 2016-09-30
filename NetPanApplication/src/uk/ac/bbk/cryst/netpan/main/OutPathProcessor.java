package uk.ac.bbk.cryst.netpan.main;

import java.io.File;
import java.util.List;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.common.PropertiesHelper;
import uk.ac.bbk.cryst.netpan.model.NetPanData;
import uk.ac.bbk.cryst.netpan.model.PeptideData;
import uk.ac.bbk.cryst.netpan.service.NetPanDataBuilder;

public class OutPathProcessor {
	static PropertiesHelper properties = new PropertiesHelper();
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//parameters
		File pathToRead = new File(properties.getValue("factorOutputPathCTL"));
		PredictionType type = PredictionType.CTL;
		NetPanDataBuilder builder = new NetPanDataBuilder(type);
		
		//read the afp directory
		for(final File fileEntry : pathToRead.listFiles()){
			if(fileEntry.isDirectory()){
				//read the allele group directory: selectedAlleles
				for(final File groupPath : fileEntry.listFiles()){
					String groupName = groupPath.getName();
					//read prediction files in the group:albumin_P02768_HLA-A01:01.txt 
					List<NetPanData> netPanDataList = builder.buildFileData(type,groupPath);
					printEpitopes(netPanDataList);
				
				}//for
			}//if
		}//for
		
	}

	private static void printEpitopes(List<NetPanData> netPanDataList) {
		for(NetPanData netPanData : netPanDataList){
			List<PeptideData> peptideList = netPanData.getEpitopes();
			System.out.println(netPanData.getFastaFileName() +" " +netPanData.getAllele());
			for(PeptideData peptide : peptideList){
				System.out.println(peptide.toString());
			}
		}
	}

}
