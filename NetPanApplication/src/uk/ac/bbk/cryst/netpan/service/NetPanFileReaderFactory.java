package uk.ac.bbk.cryst.netpan.service;

import java.io.File;
import java.io.FileNotFoundException;

import uk.ac.bbk.cryst.netpan.common.PredictionType;

public class NetPanFileReaderFactory {

	public static NetPanFileReader getReader(PredictionType type,File netFile, String foundFileName, String foundAllele) throws FileNotFoundException{
		switch (type){
			case CTL:
				return new NetCTLPanReader(netFile,foundFileName,foundAllele);
			case MHCI:
				return new NetMHCPanReader(netFile, foundFileName, foundAllele);
			case MHCII:
				return new NetMHCIIPanReader(netFile, foundFileName, foundAllele);
			default: 
				return null;
		}
	}
}
