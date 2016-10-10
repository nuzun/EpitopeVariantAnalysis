package uk.ac.bbk.cryst.netpan.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import uk.ac.bbk.cryst.netpan.common.PredictionType;
import uk.ac.bbk.cryst.netpan.common.PropertiesHelper;
import uk.ac.bbk.cryst.netpan.dao.AlleleGroupDataDaoImpl;
import uk.ac.bbk.cryst.netpan.model.AlleleGroupData;
import uk.ac.bbk.cryst.netpan.util.NetPanCmd;

public class Program {

	/**
	 * @param args
	 *            Start position in the peptide starts from 0 for CTL. Start
	 *            position for a peptide sequence starts from 0 for NetMHCPan
	 * @throws IOException
	 *             HLA-DRB11101 for MHCII-2.2 DRB1_1101 for MHCIIPan-2.0
	 */
	public static void main(String[] args) throws IOException {

		PropertiesHelper properties = new PropertiesHelper();

		// parameters
		String peptideLength = "15";
		String scoreCode = "0"; // MHC(1) or comb (0) used for CTL only
		String inputPath = properties.getValue("inputPath");
		String outputBasePath = properties.getValue("factorOutputPathMHCIIPan");
		PredictionType type = PredictionType.MHCIIPAN;
		String alleleFileFullPath = inputPath + "testII.csv";

		// Read the alleles from region/group of alleles file
		AlleleGroupData groupData = new AlleleGroupDataDaoImpl(alleleFileFullPath).getGroupData();

		// read the input sequence(s)
		try {
			File sequencePath = new File(properties.getValue("sequencePath"));

			for (final File fileEntry : sequencePath.listFiles()) {

				// testProtein_P00451
				String fileName = FilenameUtils.removeExtension(fileEntry.getName());
				String sequenceFileFullPath = fileEntry.getPath();

				// creating the output folder: base/testProtein_P00451/testII/
				String outputPath = outputBasePath + fileName + "/"
						+ FilenameUtils.removeExtension(groupData.getSourceFileName());

				File file = new File(outputPath);
				if (!file.exists()) {
					if (file.mkdir()) {
						System.out.println("Directory " + outputPath + " is created!");
					} else {
						System.out.println("Failed to create directory: " + outputPath + "!");
					}
				}

				// for each allele, generate the scores
				for (String allele : groupData.getAlleleMap().keySet()) {

					String outputFileFullPath = outputPath + "/" + fileName + "_" + allele + ".txt";

					NetPanCmd.run(type, scoreCode, peptideLength, allele, sequenceFileFullPath, outputFileFullPath);

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
