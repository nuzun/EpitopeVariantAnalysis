package uk.ac.bbk.cryst.netpan.service;

import java.io.File;
import java.io.FileNotFoundException;

import uk.ac.bbk.cryst.netpan.common.PredictionType;

public class NetPanFileReaderFactory {

	public static NetPanFileReader getReader(PredictionType type,File netFile, String foundFileName, String foundAllele) throws FileNotFoundException{
		switch (type){
			case CTL:
			case CTLPAN:
				return new NetCTLPanReader(netFile,foundFileName,foundAllele);
			case MHCI:
			case MHCIPAN:
				return new NetMHCPanReader(netFile, foundFileName, foundAllele);
			case MHCII:
				return new NetMHCIIReader(netFile, foundFileName, foundAllele);
			case MHCIIPAN:
				return new NetMHCIIPanReader(netFile, foundFileName, foundAllele);
			default: 
				return null;
		}
	}
}
