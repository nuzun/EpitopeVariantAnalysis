package uk.ac.bbk.cryst.sequenceanalysis.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import uk.ac.bbk.cryst.sequenceanalysis.common.FastaFileType;
import uk.ac.bbk.cryst.sequenceanalysis.model.MatchData;
import uk.ac.bbk.cryst.sequenceanalysis.model.Sequence;

public class SequenceComparator {

	String printFields = "Protein1,Position1,Peptide1,Protein2,Position2,Peptide2,Seq2Description";
	private final static Logger LOGGER = Logger.getLogger("SequenceAnalysisLog");
	List<Sequence> seq2List = new ArrayList<Sequence>();
	FastaFileType inputFileType;
	FastaFileType compareFileType;

	public SequenceComparator() {

	}

	public FastaFileType getInputFileType() {
		return inputFileType;
	}

	public void setInputFileType(FastaFileType inputFileType) {
		this.inputFileType = inputFileType;
	}

	public FastaFileType getCompareFileType() {
		return compareFileType;
	}

	public void setCompareFileType(FastaFileType compareFileType) {
		this.compareFileType = compareFileType;
	}
	
	
	public List<Sequence> runMatchFinder(File inputFile, String comparePath, List<Integer> positions,
			boolean isMatch, int kmer) throws IOException {
		List<Sequence> matchList = new ArrayList<Sequence>();

		SequenceFactory sequenceFactory = new SequenceFactory();
		// read the dir and the first file as we know there is only one for the
		// first file
		Sequence seq1 = sequenceFactory.getSequenceList(inputFile, inputFileType).get(0);

		// read the compareDir and all the files as there might be more than one
		File compareDir = new File(comparePath);
		for (final File fileEntry : compareDir.listFiles()) {
			if(fileEntry.isDirectory()){
				//ignore the directory and continue, we want one compare file
				continue;
			}
			List<Sequence> tempList = sequenceFactory.getSequenceList(fileEntry, compareFileType);
			seq2List.addAll(tempList);
		}

		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			List<MatchData> matchMap = SequenceComparator.generateMatchMap(seq1, seq2, positions, isMatch, kmer);

			if (SequenceComparator.isIdentical(seq1, seq2)) {
				//LOGGER.info(seq1.getProteinId() + " vs " + seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (matchMap.size() == 0) {
				//LOGGER.info(seq1.getProteinId() + " vs " + seq2.getProteinId() + " = NO MATCHES");
				continue;
			}

			//there is a match
			matchList.add(seq2);

		}
		return matchList;

	}
	

	/*
	 * prints the results to a csv file
	 */
	public void runMatchFinder(File inputFile, String comparePath, String outPath, List<Integer> positions,
			boolean isMatch, int kmer) throws IOException {

		SequenceFactory sequenceFactory = new SequenceFactory();
		// read the dir and the first file as we know there is only one for the
		// first file
		Sequence seq1 = sequenceFactory.getSequenceList(inputFile, inputFileType).get(0);

		// read the compareDir and all the files as there might be more than one
		File compareDir = new File(comparePath);
		for (final File fileEntry : compareDir.listFiles()) {
			List<Sequence> tempList = sequenceFactory.getSequenceList(fileEntry, compareFileType);
			seq2List.addAll(tempList);
		}

		// copy the results to a csv file in the csv path
		// fileNameDescription =
		// FileOperationsHelper.trimFileName(fileNameDescription);
		File outFile = new File(
				outPath + seq1.getProteinId() + "_" + kmer + "_vs_" + generatePositionStr(positions) + ".csv");

		try {
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
		} catch (Exception ex) {
			LOGGER.severe("Exception occurred creating the file:");
			ex.printStackTrace();
			throw new RuntimeException("Exception occurred creating the file");
		}

		// populate the match file
		FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(printFields);
		bw.newLine();

		// compare seq1 to each seq2,
		for (Sequence seq2 : seq2List) {

			List<MatchData> matchMap = SequenceComparator.generateMatchMap(seq1, seq2, positions, isMatch, kmer);

			if (SequenceComparator.isIdentical(seq1, seq2)) {
				//LOGGER.info(seq1.getProteinId() + " vs " + seq2.getProteinId() + " = IDENTICAL");
				continue;
			}

			if (matchMap.size() == 0) {
				//LOGGER.info(seq1.getProteinId() + " vs " + seq2.getProteinId() + " = NO MATCHES");
				continue;
			}

			for (MatchData match : matchMap) {
				bw.write(match.toStringOnlyValues());
				bw.newLine();
				System.out.println(match.toStringOnlyValues());
			}

		}

		bw.close();
	}

	private static String generatePositionStr(List<Integer> positions) {
		String positionToStr = "";
		for (int pos : positions) {
			positionToStr = positionToStr + pos + "_";
		}
		return positionToStr;
	}

	/*
	 * int[] positions starts from 1. sequence positions start from 0. if
	 * condition true then positions have to match and others don't have to if
	 * condition false then positions don't have to match but others have to
	 */

	public static boolean isMatch(String peptide1, String peptide2, List<Integer> positions, boolean condition) {
		//TODO: AJS confirmed exact match for * but the characters like B etc???
		if (condition == true) {
			for (int position : positions) {
				if (peptide1.charAt(position - 1) == peptide2.charAt(position - 1)) {
					continue;
				} else {
					return false;
				}
			}
			return true;
		}

		else {

			for (int i = 0; i < peptide1.length(); i++) {

				if (positions.contains(i + 1)) {
					continue;
				}
				if (peptide1.charAt(i) != peptide2.charAt(i)) {
					return false;
				} else {
					continue;
				}
			}

			return true;

		}
	}

	public static List<MatchData> generateMatchMap(Sequence seq1, Sequence seq2, List<Integer> positions, boolean cond,
			int peptideLength) {

		List<MatchData> matchList = new ArrayList<MatchData>();

		Map<Integer, String> seq1Map = PeptideGenerator.getPositionPeptideMap(seq1, peptideLength);
		Map<Integer, String> seq2Map = PeptideGenerator.getPositionPeptideMap(seq2, peptideLength);

		for (Integer pos1 : seq1Map.keySet()) {
			String peptide1 = seq1Map.get(pos1);

			for (Integer pos2 : seq2Map.keySet()) {
				String peptide2 = seq2Map.get(pos2);

				if (isMatch(peptide1, peptide2, positions, cond)) {
					MatchData newMatch = new MatchData(seq1.getProteinId(), pos1, peptide1, seq2.getProteinId(), pos2,
							peptide2);
					matchList.add(newMatch);
				}
			}
		}

		return matchList;

	}

	public static boolean isIdentical(Sequence seq1, Sequence seq2) {
		if (seq1.length() != seq2.length()) {
			return false;
		}
		for (int i = 0; i < seq1.getSequence().length(); i++) {
			if (seq1.getSequence().toCharArray()[i] == seq2.getSequence().toCharArray()[i]) {
				continue;
			} else {
				// System.out.print(i);
				return false;
			}
		}

		return true;
	}

}
