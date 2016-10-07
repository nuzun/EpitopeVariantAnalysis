package uk.ac.bbk.cryst.netpan.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.Predicate;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.common.PropertiesHelper;
import uk.ac.bbk.cryst.netpan.dao.GroupDataDaoImpl;
import uk.ac.bbk.cryst.netpan.model.GroupData;
import uk.ac.bbk.cryst.netpan.model.MHCIIPeptideData;
import uk.ac.bbk.cryst.netpan.model.NetPanData;
import uk.ac.bbk.cryst.netpan.model.PeptideData;
import uk.ac.bbk.cryst.netpan.service.NetPanDataBuilder;
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

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PropertiesHelper properties = new PropertiesHelper();
		SequenceFactory sequenceFactory = new SequenceFactory();

		// parameters
		int nMer = 15;
		String scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
		int variant_pos = 20;
		PredictionType type = PredictionType.MHCIIPAN;

		// Read the alleles from region/group of alleles file
		String alleleFileFullPath = properties.getValue("inputPath") + "testII.csv";
		GroupData groupData = new GroupDataDaoImpl(alleleFileFullPath).getGroupData();

		String outputBasePath = properties.getValue("factorOutputPathMHCIIPan");
		String variantOutputBasePath = properties.getValue("variantOutputPathMHCIIPan");

		// Read the sequence file test_P00451.fasta
		File sequenceFile = new File(properties.getValue("sequencePath")).listFiles()[0];
		String sequenceFileName = sequenceFile.getName();
		Sequence inputSequence = sequenceFactory.getSequenceList(sequenceFile, FastaFileType.UNIPROT).get(0);

		// calculate variant subSeq file
		String subSequence = inputSequence.getSequence().substring(variant_pos - nMer, variant_pos + nMer - 1);
		String variantFilefullContent = ">sp|" + inputSequence.getProteinId() + "|" + variant_pos + "\n" + subSequence;

		String variantFileName = sequenceFileName + "_" + variant_pos; // testProtein_P00451.fasta_20
		File variantSequenceFile = new File(properties.getValue("variantSequencePath") + variantFileName);
		if (!variantSequenceFile.exists()) {
			variantSequenceFile.createNewFile();
		}

		FileWriter fw = new FileWriter(variantSequenceFile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(variantFilefullContent);
		bw.close();

		// calculate score file /variantMHCIIPan/testProtein_P00451/testII
		String variantOutputPath = variantOutputBasePath + FilenameUtils.removeExtension(sequenceFileName) + "/"
				+ FilenameUtils.removeExtension(groupData.getSourceFileName());

		File file = new File(variantOutputPath);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory " + variantOutputPath + " is created!");
			} else {
				System.out.println("Failed to create directory: " + variantOutputPath + "!");
			}
		}

		// for each allele, generate the scores
		String variantOutputFileFullPath = "";
		for (String allele : groupData.getAlleleMap().keySet()) {

			variantOutputFileFullPath = variantOutputPath + "/" + FilenameUtils.removeExtension(sequenceFileName) + "_"
					+ allele + ".txt" + "_" + variant_pos;
			System.out.println(variantOutputFileFullPath);
			NetPanCmd.run(type, scoreCode, String.valueOf(nMer), allele, variantSequenceFile.getPath(),
					variantOutputFileFullPath);
		}

		// read score files
		NetPanDataBuilder builder = new NetPanDataBuilder(type);
		List<NetPanData> netPanDataList = builder.buildFileData(new File(variantOutputPath));

		// eliminate the peptides with core not containing the variant pos
		for (NetPanData netPanData : netPanDataList) {
			System.out.println(netPanData.getFastaFileName() + " " + netPanData.getAllele());
			List<MHCIIPeptideData> remaining = new ArrayList<MHCIIPeptideData>();

			for (PeptideData peptide : netPanData.getPeptideList()) {
				// System.out.println(peptide.toString());
				MHCIIPeptideData newPeptide = (MHCIIPeptideData) peptide;
				int posToCheck = newPeptide.getStartPosition() + newPeptide.getCoreStartPosition();

				// check if core contains the position
				if ((posToCheck > (nMer - 10)) && (posToCheck < nMer) && (newPeptide.getIC50Score() < 1000)) {
					remaining.add(newPeptide);
				}

			}
			for (MHCIIPeptideData p : remaining) {
				System.out.println(p.toString());
			}
		}

	}

}
