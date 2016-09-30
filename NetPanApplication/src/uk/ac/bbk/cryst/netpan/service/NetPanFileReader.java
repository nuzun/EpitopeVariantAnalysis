package uk.ac.bbk.cryst.netpan.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.model.NetPanData;

public abstract class NetPanFileReader {

	Scanner scanner;
	File netFile;
	String fastaFileName;
	String allele;
	PredictionType type;
	
	public NetPanFileReader(PredictionType type,File netFile, String foundFileName, String foundAllele) throws FileNotFoundException{
		this.type = type;
		this.netFile = netFile;
		this.fastaFileName = foundFileName;
		this.allele = foundAllele;
		this.scanner = new Scanner(new FileReader(netFile));
	}
	
	protected abstract NetPanData read() throws Exception;
}
