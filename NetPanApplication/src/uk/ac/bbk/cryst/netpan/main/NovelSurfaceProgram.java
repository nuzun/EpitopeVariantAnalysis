package uk.ac.bbk.cryst.netpan.main;

import uk.ac.bbk.cryst.netpan.service.NovelSurfaceAnalyzer;

public class NovelSurfaceProgram {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		NovelSurfaceAnalyzer.generateSequenceAndScoreFiles();
		NovelSurfaceAnalyzer.runEliminate();

	}

}
