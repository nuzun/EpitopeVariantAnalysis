package uk.ac.bbk.cryst.netpan.model;

import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.rowset.Predicate;

import org.apache.commons.lang3.StringUtils;



public abstract class PeptideData implements Comparable<PeptideData> {
	
	int rank;
	int startPosition;
	String peptide;
	Float mhcScore;
	Float IC50Score;
	boolean epitope;
	String bindingLevel;
	
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
	
	public String getBindingLevel() {
		return bindingLevel;
	}

	public boolean isStrongBinder() {
		return StringUtils.equals(this.getBindingLevel(), "SB") ? true : false;
	}

	public boolean isWeakBinder() {
		return (StringUtils.equals(this.getBindingLevel(), "WB") || this.getBindingLevel() == "") ? true : false;
	}
	
	public abstract String toString();
	
	public abstract String toStringNoHeader(String string);
	
	public abstract String toStringNoHeader();
	
}
