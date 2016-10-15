package uk.ac.bbk.cryst.netpan.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MHCIIPanPeptideData extends MHCIIPeptideData{
	
	Float coreRel;
	String expBind;

	public MHCIIPanPeptideData(int rank, int startPosition,String peptide, int coreStartPosition, String corePeptide, 
			Float coreRel, Float mhcScore, Float IC50Score, Float rankPercentage, String expBind, String bindingLevel){
		super(rank,startPosition,peptide,corePeptide,mhcScore,IC50Score,rankPercentage,"",bindingLevel);
		
		this.coreStartPosition = coreStartPosition;
		this.coreRel = coreRel;
		this.expBind = expBind;
		this.epitope = StringUtils.isEmpty(bindingLevel) ? false : true;
	}
	
	public String getBindingLevel() {
		return bindingLevel;
	}

	public void setBindingLevel(String bindingLevel) {
		this.bindingLevel = bindingLevel;
	}

	public Float getCoreRel() {
		return coreRel;
	}

	public void setCoreRel(Float coreRel) {
		this.coreRel = coreRel;
	}

	public String getExpBind() {
		return expBind;
	}

	public void setExpBind(String expBind) {
		this.expBind = expBind;
	}

	@Override
	public String toString() {
		String out = 
				" rank:" + this.getRank() +
				" startPosition:" + this.getStartPosition() +
				" peptide:" + this.getPeptide() +
				" coreStartPos:" + this.getCoreStartPosition() +
				" corePeptide:" + this.getCorePeptide() +
				" core_rel:" + this.getCoreRel() +
				" aff:" + this.getMhcScore()+
				" IC50:" + this.getIC50Score() +
				" rankPercentage:" + this.getRankPercentage() +
				" exp_bind:" + this.getExpBind() +
				" bindingLevel:" + this.getBindingLevel();
		
		return out;
	}

	@Override
	public String toStringNoHeader(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toStringNoHeader() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	 public int hashCode()
	 {
		  HashCodeBuilder builder = new HashCodeBuilder();
	        builder.append(rank);
	        builder.append(startPosition);
	        builder.append(peptide);
	        builder.append(coreStartPosition);
	        builder.append(corePeptide);
	        builder.append(coreRel);
	        builder.append(mhcScore);
	        builder.append(IC50Score);
	        builder.append(rankPercentage);
	        builder.append(expBind);
	        builder.append(bindingLevel);
	        return builder.toHashCode();

	 }
	@Override
	 public boolean equals(Object obj) {
		if (obj instanceof MHCIIPanPeptideData) {
			MHCIIPanPeptideData other = (MHCIIPanPeptideData) obj;
           EqualsBuilder builder = new EqualsBuilder();
           builder.append(this.rank, other.rank);
           builder.append(this.startPosition, other.startPosition);
           builder.append(this.peptide, other.peptide);
           builder.append(this.coreStartPosition, other.coreStartPosition);
           builder.append(this.corePeptide, other.corePeptide);
           builder.append(this.mhcScore, other.mhcScore);
           builder.append(this.IC50Score, other.IC50Score);
           builder.append(this.rankPercentage, other.rankPercentage);
           builder.append(this.expBind, other.expBind);
           builder.append(this.bindingLevel, other.bindingLevel);
           return builder.isEquals();
       }
       return false;

	}

}
