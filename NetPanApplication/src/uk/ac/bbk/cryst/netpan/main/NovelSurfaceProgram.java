package uk.ac.bbk.cryst.netpan.main;

import uk.ac.bbk.cryst.netpan.service.NovelSurfaceAnalyzer;

public class NovelSurfaceProgram {

	public static void main(String[] args) throws Exception {
		
		NovelSurfaceAnalyzer novelSurfaceAnalyzer = new NovelSurfaceAnalyzer();
		novelSurfaceAnalyzer.generateSequenceAndScoreFiles();
		novelSurfaceAnalyzer.runEliminate();
		
		/*
		MHCIIPeptideData a = new MHCIIPeptideData();
		a.setIC50Score((float) 5);
		MHCIIPeptideData b = new MHCIIPeptideData();
		b.setIC50Score((float) 2);
		MHCIIPeptideData c = new MHCIIPeptideData();
		c.setIC50Score((float) 3);
		
		List<MHCIIPeptideData> list = new ArrayList<MHCIIPeptideData>();
		list.add(a);
		list.add(b);
		list.add(c);
		
		Collections.sort(list);
		System.out.println(list.get(0));
		*/

	}

}
