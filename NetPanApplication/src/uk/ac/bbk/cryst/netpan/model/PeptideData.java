package uk.ac.bbk.cryst.netpan.model;

public abstract class PeptideData implements Comparable<PeptideData> {

	int rank;
	int startPosition;
	String peptide;
	Float mhcScore;
	Float IC50Score;
	boolean epitope;
	
	public int getRank() {
		return rank;
	}
	public int getStartPosition() {
		return startPosition;
	}
	public String getPeptide() {
		return peptide;
	}
	public Float getMhcScore() {
		return mhcScore;
	}
	public Float getIC50Score() {
		return IC50Score;
	}
	public boolean isEpitope() {
		return epitope;
	}

	public abstract String toString();
	
	public abstract String toStringNoHeader(String string);
	
	public abstract String toStringNoHeader();
	
}
