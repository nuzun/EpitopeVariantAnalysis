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
	int nMer;
	String sequenceFileName;
	String scoreCode;
	PredictionType type;
	List<String> variants;

	PropertiesHelper properties;
	SequenceFactory sequenceFactory;

	String alleleFileFullPath;
	String sequenceFileFullPath;
	String variantOutputFullPathMHCIIPan;
	String endogenousOutputFullPathMHCIIPan;

	String variantSequencePath;
	String comparePath;

	String proteomeOutputFullPathMHCIIPan;
	String tmpSequencePath;
	String proteomeSequencePath;

	public String getProteomeSequencePath() {
		return proteomeSequencePath;
	}

	public String getTmpSequencePath() {
		return tmpSequencePath;
	}

	public String getProteomeOutputFullPathMHCIIPan() {
		return proteomeOutputFullPathMHCIIPan;
	}

	public String getComparePath() {
		return comparePath;
	}

	public String getVariantSequencePath() {
		//not used at the moment
		return variantSequencePath;
	}

	public String getAlleleFileFullPath() {
		return alleleFileFullPath;
	}

	public String getSequenceFileFullPath() {
		return sequenceFileFullPath;
	}

	public String getVariantOutputFullPathMHCIIPan() {
		return variantOutputFullPathMHCIIPan;
	}

	public String getEndogenousOutputFullPathMHCIIPan() {
		return endogenousOutputFullPathMHCIIPan;
	}

	public int getnMer() {
		return nMer;
	}

	public String getSequenceFileName() {
		return sequenceFileName;
	}

	public String getScoreCode() {
		return scoreCode;
	}

	public PredictionType getType() {
		return type;
	}

	public List<String> getVariants() {
		return variants;
	}

	public PropertiesHelper getProperties() {
		return properties;
	}

	public SequenceFactory getSequenceFactory() {
		return sequenceFactory;
	}

	public NovelSurfaceAnalyzer() throws IOException {
		nMer = 15;
		sequenceFileName = "testProtein_P00451.fasta";
		scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
		type = PredictionType.MHCIIPAN;
		variants = new ArrayList<String>();
		variants.add("R-30-C");

		properties = new PropertiesHelper();
		sequenceFactory = new SequenceFactory();

		alleleFileFullPath = properties.getValue("alleleFileFullPath");
		sequenceFileFullPath = properties.getValue("sequenceFileFullPath");
		comparePath = properties.getValue("comparePath");
		tmpSequencePath = properties.getValue("tmpSequencePath");
		proteomeSequencePath = properties.getValue("proteomeSequencePath");

		variantOutputFullPathMHCIIPan = properties.getValue("variantOutputFullPathMHCIIPan");
		endogenousOutputFullPathMHCIIPan = properties.getValue("endogenousOutputFullPathMHCIIPan");
		proteomeOutputFullPathMHCIIPan = properties.getValue("proteomeOutputFullPathMHCIIPan");

	}

	public void generateSequenceAndScoreFiles() throws Exception {

		// Read the alleles straight from region/group of alleles file
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

		// Read the sequence file test_P00451.fasta
		File sequenceFile = new File(this.getSequenceFileFullPath());
		Sequence inputSequence = this.getSequenceFactory().getSequenceList(sequenceFile, FastaFileType.UNIPROT).get(0);

		// Work on variants
		for (String variant : this.getVariants()) {
			String[] parts = variant.split("-");
			String from = parts[0];
			int variantPosition = Integer.valueOf(parts[1]);
			String to = parts[2];

			// calculate variant subSeq file
			String subSequence = inputSequence.getSequence().substring(variantPosition - this.getnMer(),
					variantPosition + this.getnMer() - 1);
			String variantFilefullContent = ">sp|" + inputSequence.getProteinId() + "|" + variantPosition + " " + "\n"
					+ subSequence;
			String variantFileName = this.getSequenceFileName() + "_" + variantPosition; // testProtein_P00451.fasta_20
			File variantSequenceFile = new File(this.getVariantSequencePath() + variantFileName);
			FileHelper.writeToFile(variantSequenceFile, variantFilefullContent);

			// for each allele, generate the scores for variants
			String variantOutputFileFullPath = "";
			for (String allele : groupData.getAlleleMap().keySet()) {

				variantOutputFileFullPath = this.getVariantOutputFullPathMHCIIPan()
						+ FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt" + "_"
						+ variantPosition;
				System.out.println(variantOutputFileFullPath);
				NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
						variantSequenceFile.getPath(), variantOutputFileFullPath);
			}

			/************************************************************************/

			// generate endogeneous sequence file
			Sequence variantSequence = this.getSequenceFactory()
					.getSequenceList(variantSequenceFile, FastaFileType.UNIPROT).get(0);
			StringBuilder endSeq = new StringBuilder(variantSequence.getSequence());
			endSeq.setCharAt(this.getnMer() - 1, to.charAt(0));
			String endFilefullContent = ">sp|" + inputSequence.getProteinId() + "|" + variantPosition + " " + from + "_"
					+ to + "\n" + endSeq.toString();
			String endFileName = this.getSequenceFileName() + "_" + variantPosition + from + to; // testProtein_P00451.fasta_20AC
			File endSequenceFile = new File(this.getVariantSequencePath() + endFileName);
			FileHelper.writeToFile(endSequenceFile, endFilefullContent);

			// for each allele, generate the scores for endogeneous
			String endogeneousOutputFileFullPath = "";
			for (String allele : groupData.getAlleleMap().keySet()) {

				endogeneousOutputFileFullPath = this.getEndogenousOutputFullPathMHCIIPan()
						+ FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt" + "_"
						+ variantPosition + from + to;
				System.out.println(endogeneousOutputFileFullPath);
				NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
						endSequenceFile.getPath(), endogeneousOutputFileFullPath);

			}
		} // variants
	}

	public void runEliminate() throws Exception {

		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(this.getAlleleFileFullPath()).getGroupData();

		NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());

		for (String variant : this.getVariants()) {
			String[] parts = variant.split("-");
			String from = parts[0];
			int variantPosition = Integer.valueOf(parts[1]);
			String to = parts[2];

			for (String allele : groupData.getAlleleMap().keySet()) {
				List<MHCIIPeptideData> remainingPeptides = new ArrayList<MHCIIPeptideData>();

				String fileName = FilenameUtils.removeExtension(this.getSequenceFileName()) + "_" + allele + ".txt_"
						+ variantPosition;
				NetPanData variantNetPanData = builder
						.buildSingleFileData(new File(this.getVariantOutputFullPathMHCIIPan() + fileName));

				for (PeptideData peptide : variantNetPanData.getPeptideList()) {
					MHCIIPeptideData newPeptide = (MHCIIPeptideData) peptide;
					int posToCheck = newPeptide.getStartPosition() + newPeptide.getCoreStartPosition();

					// continue if the core contains the variant and binds
					// efficiently
					if ((posToCheck > (this.getnMer() - 10)) && (posToCheck < this.getnMer())
							&& (newPeptide.getIC50Score() < 1000)) {

						// check endo criteria
						String endoFileName = fileName + from + to;
						NetPanData endoNetPanData = builder.buildSingleFileData(
								new File(this.getEndogenousOutputFullPathMHCIIPan() + endoFileName));
						MHCIIPeptideData endoNewPeptide = (MHCIIPeptideData) (endoNetPanData
								.getSpecificPeptideData(peptide.getStartPosition()));

						// continue if the core is the same
						if ((endoNewPeptide.getCoreStartPosition() == newPeptide.getCoreStartPosition())) {

							if (endoNewPeptide.getIC50Score() > 1000) {
								// add the newPeptide to the list for proteome
								// check
								// System.out.println(newPeptide.toString());
								remainingPeptides.add(newPeptide);
							}

							else {
								// check MHC/TCR
								int checkPos = endoNewPeptide.getStartPosition()
										+ endoNewPeptide.getCoreStartPosition();
								if (checkPos == (this.getnMer() - 9) || checkPos == (this.getnMer() - 6)
										|| checkPos == (this.getnMer() - 4) || checkPos == (this.getnMer() - 1)) {
									// eliminate it is not novel
								} else {
									// add newPeptide to the list for proteome
									// check
									// System.out.println(newPeptide.toString());
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
		} // variant
	}

	private void runProteomeCheck(String allele, String variant, List<MHCIIPeptideData> remainingPeptides)
			throws Exception {

		int coreNMer = 9;
		boolean isMatch = false;// positions do not have to match so false
		List<Integer> positions = Arrays.asList(1, 4, 6, 9);

		List<Sequence> matchList = new ArrayList<Sequence>();
		NetPanDataBuilder builder = new NetPanDataBuilder(this.getType());
		Map<MHCIIPeptideData, MHCIIPeptideData> matchMap = new HashMap<MHCIIPeptideData, MHCIIPeptideData>();
		Map<String, MHCIIPeptideData> tempMap = new HashMap<String, MHCIIPeptideData>();

		SequenceComparator sequenceComparator = new SequenceComparator();
		sequenceComparator.setInputFileType(FastaFileType.UNIPROT);
		sequenceComparator.setCompareFileType(FastaFileType.ENSEMBLPEP);

		NovelPeptideSurface novel = new NovelPeptideSurface();
		novel.setAllele(allele);
		novel.setVariant(variant);

		MHCIIPeptideData pep1 = new MHCIIPeptideData();
		MHCIIPeptideData pep2 = new MHCIIPeptideData();

		pep1 = PeptideDataHelper.getTheStrongestBinderII(remainingPeptides);

		for (MHCIIPeptideData remaining : remainingPeptides) {

			// create a temporary fasta file from peptides
			String tmpSeqFileFullContent = ">sp|" + remaining.getCorePeptide() + "|temp" + "\n"
					+ remaining.getCorePeptide();
			String tmpFileName = remaining.getCorePeptide() + ".fasta"; // testProtein_P00451.fasta_20AC
			File tmpSeqFile = new File(this.getTmpSequencePath() + tmpFileName);

			if (tmpSeqFile.exists()) {
				matchMap.put(remaining, tempMap.get(remaining.getCorePeptide()));
				continue;
			}

			FileHelper.writeToFile(tmpSeqFile, tmpSeqFileFullContent);

			matchList = sequenceComparator.runMatchFinder(tmpSeqFile, this.getComparePath(), positions, isMatch,
					coreNMer);
			// TODO: if matchList is empty for just one of the remaining do we
			// just ignore it???

			List<MHCIIPeptideData> matchingPeptides = new ArrayList<MHCIIPeptideData>();
			// run predictions on the matching proteome sequences
			for (Sequence seq : matchList) {
				EnsemblPepSequence ensemblPepSeq = (EnsemblPepSequence) seq;
				String proteomeSeqFileFullContent = ">sp|" + ensemblPepSeq.getProteinId() + "\n"
						+ ensemblPepSeq.getSequence();
				String proteomeSeqFileName = ensemblPepSeq.getProteinId() + ".fasta";
				File proteomeSeqFile = new File(this.getProteomeSequencePath() + proteomeSeqFileName);
				FileHelper.writeToFile(proteomeSeqFile, proteomeSeqFileFullContent);

				String proteomeOutputFileFullPath = this.getProteomeOutputFullPathMHCIIPan()
						+ FilenameUtils.removeExtension(proteomeSeqFileName) + "_" + allele + ".txt";
				// System.out.println(proteomeOutputFileFullPath);
				NetPanCmd.run(this.getType(), this.getScoreCode(), String.valueOf(this.getnMer()), allele,
						proteomeSeqFile.getPath(), proteomeOutputFileFullPath);

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

		System.out.println(novel.getPeptide1() + "\n" + novel.getPeptide2() + "\n" + novel.getVariant() + "\n"
				+ novel.getAllele() + "\n" + novel.getColour());
		FileUtils.cleanDirectory(new File(this.getTmpSequencePath()));

	}

}
