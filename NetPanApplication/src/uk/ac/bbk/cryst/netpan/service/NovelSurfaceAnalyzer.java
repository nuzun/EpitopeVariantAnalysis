package uk.ac.bbk.cryst.netpan.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.common.PropertiesHelper;
import uk.ac.bbk.cryst.netpan.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netpan.model.AlleleGroupData;
import uk.ac.bbk.cryst.netpan.model.MHCIIPeptideData;
import uk.ac.bbk.cryst.netpan.model.NetPanData;
import uk.ac.bbk.cryst.netpan.model.PeptideData;
import uk.ac.bbk.cryst.netpan.util.FileHelper;
import uk.ac.bbk.cryst.netpan.util.NetPanCmd;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceFactory;

/**
 * Assuming initial score files are already generated for the full sequence
 * 
 * @author naz
 *
 */
public class NovelSurfaceAnalyzer {
	// parameters
	static int nMer = 15;
	static String proteinNameAndId = "testProtein_P00451";
	static String scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
	static PredictionType type = PredictionType.MHCIIPAN;
	static String[] variants = { "R-30-C" };

	static PropertiesHelper properties = new PropertiesHelper();
	static SequenceFactory sequenceFactory = new SequenceFactory();

	public static void generateSequenceAndScoreFiles() throws Exception {

		// Read the alleles from region/group of alleles file
		String alleleFileFullPath = properties.getValue("alleleFileFullPath");
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(alleleFileFullPath).getGroupData();

		// Read the sequence file test_P00451.fasta
		File sequenceFile = new File(properties.getValue("sequenceFileFullPath"));
		Sequence inputSequence = sequenceFactory.getSequenceList(sequenceFile, FastaFileType.UNIPROT).get(0);
		String sequenceFileName = sequenceFile.getName(); // test_P00451.fasta
		String variantOutputFullPathMHCIIPan = properties.getValue("variantOutputFullPathMHCIIPan");
		String endogenousOutputFullPathMHCIIPan = properties.getValue("endogenousOutputFullPathMHCIIPan");

		// Work on variants
		for (String variant : variants) {
			String[] parts = variant.split("-");
			String from = parts[0];
			int variantPosition = Integer.valueOf(parts[1]);
			String to = parts[2];

			// calculate variant subSeq file
			String subSequence = inputSequence.getSequence().substring(variantPosition - nMer,
					variantPosition + nMer - 1);
			String variantFilefullContent = ">sp|" + inputSequence.getProteinId() + "|" + variantPosition + " " + "\n"
					+ subSequence;
			String variantFileName = sequenceFileName + "_" + variantPosition; // testProtein_P00451.fasta_20
			File variantSequenceFile = new File(properties.getValue("variantSequencePath") + variantFileName);
			FileHelper.writeToFile(variantSequenceFile, variantFilefullContent);

			// for each allele, generate the scores
			String variantOutputFileFullPath = "";
			for (String allele : groupData.getAlleleMap().keySet()) {

				variantOutputFileFullPath = variantOutputFullPathMHCIIPan + "/"
						+ FilenameUtils.removeExtension(sequenceFileName) + "_" + allele + ".txt" + "_"
						+ variantPosition;
				System.out.println(variantOutputFileFullPath);
				NetPanCmd.run(type, scoreCode, String.valueOf(nMer), allele, variantSequenceFile.getPath(),
						variantOutputFileFullPath);
			}

			// generate endogeneous sequence and score files
			// replace position nMer-1 with the patient's mutation
			Sequence variantSequence = sequenceFactory.getSequenceList(variantSequenceFile, FastaFileType.UNIPROT)
					.get(0);
			StringBuilder endSeq = new StringBuilder(variantSequence.getSequence());
			endSeq.setCharAt(nMer - 1, to.charAt(0));

			String endFilefullContent = ">sp|" + inputSequence.getProteinId() + "|" + variantPosition + " " + from + "_"
					+ to + "\n" + endSeq.toString();

			String endFileName = sequenceFileName + "_" + variantPosition + from + to; // testProtein_P00451.fasta_20AC
			File endSequenceFile = new File(properties.getValue("variantSequencePath") + endFileName);
			FileHelper.writeToFile(endSequenceFile, endFilefullContent);

			String endogeneousOutputFileFullPath = "";
			// for each allele, generate the scores
			for (String allele : groupData.getAlleleMap().keySet()) {

				endogeneousOutputFileFullPath = endogenousOutputFullPathMHCIIPan
						+ FilenameUtils.removeExtension(sequenceFileName) + "_" + allele + ".txt" + "_"
						+ variantPosition + from + to;
				System.out.println(endogeneousOutputFileFullPath);
				NetPanCmd.run(type, scoreCode, String.valueOf(nMer), allele, endSequenceFile.getPath(),
						endogeneousOutputFileFullPath);
			}
		} // variants
	}

	public static void runEliminate() throws Exception {
		// start eliminate // read scorefiles
		// Read the alleles from region/group of alleles file
		String alleleFileFullPath = properties.getValue("alleleFileFullPath");
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(alleleFileFullPath).getGroupData();

		NetPanDataBuilder builder = new NetPanDataBuilder(type);
		String variantOutputFullPathMHCIIPan = properties.getValue("variantOutputFullPathMHCIIPan");
		String endogenousOutputFullPathMHCIIPan = properties.getValue("endogenousOutputFullPathMHCIIPan");

		for (String variant : variants) {
			String[] parts = variant.split("-");
			String from = parts[0];
			int variantPosition = Integer.valueOf(parts[1]);
			String to = parts[2];

			for (String allele : groupData.getAlleleMap().keySet()) {
				String fileName = proteinNameAndId + "_" + allele + ".txt_" + variantPosition;
				NetPanData variantNetPanData = builder
						.buildSingleFileData(new File(variantOutputFullPathMHCIIPan + fileName));
				for (PeptideData peptide : variantNetPanData.getPeptideList()) {
					MHCIIPeptideData newPeptide = (MHCIIPeptideData) peptide;
					int posToCheck = newPeptide.getStartPosition() + newPeptide.getCoreStartPosition();

					if ((posToCheck > (nMer - 10)) && (posToCheck < nMer) && (newPeptide.getIC50Score() < 1000)) {
						// check endo criteria

						String endoFileName = fileName + from + to;
						NetPanData endoNetPanData = builder
								.buildSingleFileData(new File(endogenousOutputFullPathMHCIIPan + endoFileName));
						MHCIIPeptideData endoNewPeptide = (MHCIIPeptideData) (endoNetPanData
								.getSpecificPeptideData(peptide.getStartPosition()));
						if ((endoNewPeptide.getCoreStartPosition() == newPeptide.getCoreStartPosition())) {
							if (endoNewPeptide.getIC50Score() > 1000) {
								// add the newPeptide
								System.out.println(newPeptide.toString());
							}

							else {
								// check MHC/TCR
								int checkPos = endoNewPeptide.getStartPosition()
										+ endoNewPeptide.getCoreStartPosition();
								if (checkPos == (nMer - 9) || checkPos == (nMer - 6) || checkPos == (nMer - 4)
										|| checkPos == (nMer - 1)) {
									// eliminate
								} else {
									// add newPeptide
									System.out.println(newPeptide.toString());
								}

							}
						}
					}

				}

			}
		}
	}

}
