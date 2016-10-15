package uk.ac.bbk.cryst.netpan.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.common.PropertiesHelper;
import uk.ac.bbk.cryst.netpan.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netpan.model.AlleleGroupData;
import uk.ac.bbk.cryst.netpan.model.MHCIIPeptideData;
import uk.ac.bbk.cryst.netpan.model.NetPanData;
import uk.ac.bbk.cryst.netpan.model.NovelPeptideSurface;
import uk.ac.bbk.cryst.netpan.model.PeptideData;
import uk.ac.bbk.cryst.netpan.util.FileHelper;
import uk.ac.bbk.cryst.netpan.util.NetPanCmd;
import uk.ac.bbk.cryst.netpan.util.PeptideDataHelper;
import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.EnsemblPepSequence;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;
import uk.ac.bbk.cryst.sequenceanalysis.service.SequenceComparator;
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

				variantOutputFileFullPath = variantOutputFullPathMHCIIPan
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
				List<MHCIIPeptideData> remainingPeptides = new ArrayList<MHCIIPeptideData>();

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
								//System.out.println(newPeptide.toString());
								remainingPeptides.add(newPeptide);
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
									//System.out.println(newPeptide.toString());
									remainingPeptides.add(newPeptide);

								}

							}
						}
						// TODO :else what to do if the core of the endo and
						// therapeutic is not the same????
					}

				}

				// start proteome check:
				runProteomeCheck(allele, variant, remainingPeptides);

			}
		}
	}

	private static void runProteomeCheck(String allele, String variant, List<MHCIIPeptideData> remainingPeptides)
			throws Exception {
		// TODO Auto-generated method stub
		int coreNMer = 9;
		boolean isMatch = false;// positions below do not have to match so false
		List<Integer> positions = Arrays.asList(1, 4, 6, 9);
		List<Sequence> matchList = new ArrayList<Sequence>();
		NetPanDataBuilder builder = new NetPanDataBuilder(type);

		String comparePath = properties.getValue("comparePath");
		String proteomeOutputFullPathMHCIIPan = properties.getValue("proteomeOutputFullPathMHCIIPan");

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
		sequenceComparator.setCompareFileType(FastaFileType.ENSEMBLPEP);

		NovelPeptideSurface novel = new NovelPeptideSurface();
		novel.setAllele(allele);
		novel.setVariant(variant);

		MHCIIPeptideData pep1 = new MHCIIPeptideData();
		MHCIIPeptideData pep2 = new MHCIIPeptideData();
		
		pep1 = PeptideDataHelper.getTheStrongestBinderII(remainingPeptides);

		Map<MHCIIPeptideData, MHCIIPeptideData> matchMap = new HashMap<MHCIIPeptideData, MHCIIPeptideData>();
		Map<String, MHCIIPeptideData> tempMap = new HashMap<String, MHCIIPeptideData>();

		for (MHCIIPeptideData remaining : remainingPeptides) {

			String tmpSeqFileFullContent = ">sp|" + remaining.getCorePeptide() + "|temp" + "\n"
					+ remaining.getCorePeptide();
			String tmpFileName = remaining.getCorePeptide() + ".fasta"; // testProtein_P00451.fasta_20AC
			File tmpSeqFile = new File(properties.getValue("tmpSequencePath") + tmpFileName);

			if (tmpSeqFile.exists()) {
				matchMap.put(remaining, tempMap.get(remaining.getCorePeptide()));
				continue;
			}

			FileHelper.writeToFile(tmpSeqFile, tmpSeqFileFullContent);

			matchList = sequenceComparator.runMatchFinder(tmpSeqFile, comparePath, positions, isMatch, coreNMer);
			// TODO: if matchList is empty for just one of the remaining do we
			// just ignore it???

			List<MHCIIPeptideData> matchingPeptides = new ArrayList<MHCIIPeptideData>();
			// run predictions on the matching proteome sequences
			for (Sequence seq : matchList) {
				EnsemblPepSequence ensemblPepSeq = (EnsemblPepSequence) seq;
				String proteomeSeqFileFullContent = ">sp|" + ensemblPepSeq.getProteinId() + "\n"
						+ ensemblPepSeq.getSequence();
				String proteomeSeqFileName = ensemblPepSeq.getProteinId() + ".fasta";
				File proteomeSeqFile = new File(properties.getValue("proteomeSequencePath") + proteomeSeqFileName);
				FileHelper.writeToFile(proteomeSeqFile, proteomeSeqFileFullContent);

				String proteomeOutputFileFullPath = proteomeOutputFullPathMHCIIPan
						+ FilenameUtils.removeExtension(proteomeSeqFileName) + "_" + allele + ".txt";
				//System.out.println(proteomeOutputFileFullPath);
				NetPanCmd.run(type, scoreCode, String.valueOf(nMer), allele, proteomeSeqFile.getPath(),
						proteomeOutputFileFullPath);

				NetPanData protNetPanData = builder.buildSingleFileData(new File(proteomeOutputFileFullPath));
				matchingPeptides.addAll(protNetPanData.getSpecificPeptideDataByCore(remaining.getCorePeptide()));
			}

			MHCIIPeptideData bestMatch = (MHCIIPeptideData) PeptideDataHelper.getTheStrongestBinderII(matchingPeptides);
			matchMap.put(remaining, bestMatch);
			tempMap.put(remaining.getCorePeptide(), bestMatch);
		}

		int matchExists = 0;

		for (MHCIIPeptideData key : matchMap.keySet()) {
			MHCIIPeptideData match = matchMap.get(key);
			if (match != null) {
				matchExists = 1;
				if (match.getIC50Score() > 1000) {

					if (pep2.getPeptide() == null) {
						pep2.setIC50Score(key.getIC50Score());
						pep2.setPeptide(key.getCorePeptide());
					} else {
						if (key.getIC50Score() < pep2.getIC50Score()) {
							pep2.setPeptide(key.getCorePeptide());
							pep2.setIC50Score(key.getIC50Score());
						}
					}

				}

			}
		} // for matchmap

		novel.setPeptide1(pep1);
		novel.setPeptide2(pep2);

		if (remainingPeptides.isEmpty()) {
			novel.setColour("black");
		} else {
			if (pep2.getPeptide() == null && matchExists == 1) {
				novel.setColour("pep1color/grey");
			} else if (pep2.getPeptide() == null && matchExists == 0) {
				novel.setColour("pep1color/pep1color");
			} else {
				novel.setColour("pep1color/pep2color");
			}
		}
		
		
		System.out.println(novel.getPeptide1() + "\n" + novel.getPeptide2() + "\n" + novel.getVariant() + "\n" + novel.getAllele() + "\n" + novel.getColour());
		FileUtils.cleanDirectory( new File(properties.getValue("tmpSequencePath")));

	}

}
