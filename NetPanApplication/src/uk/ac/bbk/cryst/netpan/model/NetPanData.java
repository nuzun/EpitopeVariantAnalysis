package uk.ac.bbk.cryst.netpan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import uk.ac.bbk.cryst.netpan.util.PeptideHelper;

public abstract class NetPanData implements Comparable<NetPanData> {

	List<PeptideData> peptideList;
	String allele;
	String fastaFileName;
	int identifiedEpitopes;
	
	public NetPanData(String allele, String fastaFileName){
		this.allele = allele;
		this.fastaFileName = fastaFileName;
	}

	public List<PeptideData> getPeptideList() {
		return peptideList;
	}


	public void setPeptideList(List<PeptideData> peptideList) {
		this.peptideList = peptideList;
	}


	public String getAllele() {
		return allele;
	}


	public void setAllele(String allele) {
		this.allele = allele;
	}


	public String getFastaFileName() {
		return fastaFileName;
	}


	public void setFastaFileName(String fastaFileName) {
		this.fastaFileName = fastaFileName;
	}


	public int getIdentifiedEpitopes() {
		return identifiedEpitopes;
	}


	public void setIdentifiedEpitopes(int identifiedEpitopes) {
		this.identifiedEpitopes = identifiedEpitopes;
	}


	public List<PeptideData>  getSpecificPeptideData(String peptideStr){
		List<PeptideData> peptideDataList = new ArrayList<PeptideData>();
		
		for(PeptideData peptideData: this.peptideList){
			if(StringUtils.equals(peptideData.getPeptide(),StringUtils.trim(peptideStr))){
				peptideDataList.add(peptideData);
			}
			else if(PeptideHelper.isSubSequence(peptideData.getPeptide(),StringUtils.trim(peptideStr))){
				if(!isDuplicate(peptideDataList,peptideData)){
					peptideDataList.add(peptideData);
				}
			}
		}
		return peptideDataList;
	}
	
	private boolean isDuplicate(List<PeptideData> peptideDataList,
			PeptideData peptideData) {
		
		for (PeptideData item : peptideDataList){
			if(StringUtils.equals(item.getPeptide(), peptideData.getPeptide())){
				return true;
			}
		}
		
		return false;
	}
	
	public List<PeptideData> getEpitopes(){
		List<PeptideData> epitopes = new ArrayList<PeptideData>();
		
		for(PeptideData peptideData : this.getPeptideList()){
			if(peptideData.isEpitope()){
				epitopes.add(peptideData);
			}
		}
		
		return epitopes;
	}
	
	public List<PeptideData> getStrongBinderPeptides(){
		List<PeptideData> binders = new ArrayList<PeptideData>();
		
		for(PeptideData peptideData : this.getPeptideList()){
			if(peptideData.isStrongBinder()){
				binders.add(peptideData);
			}
		}
		
		return binders;
	}
	
	public List<PeptideData> getWeakBinderPeptides(){
		List<PeptideData> binders = new ArrayList<PeptideData>();
		
		for(PeptideData peptideData : this.getPeptideList()){
			if(peptideData.isWeakBinder()){
				binders.add(peptideData);
			}
		}
		
		return binders;
	}
	
	public List<PeptideData> getTopNBinders(int n){
		List<PeptideData> binders = new ArrayList<PeptideData>();
		binders.addAll(this.getPeptideList());
		Collections.sort(binders);
		
		return binders.subList(0, n);
	}
	
	public PeptideData getSpecificPeptideData(String peptide, int position){
		
		for(PeptideData peptideData: this.getPeptideList()){
			if(StringUtils.equals(peptideData.getPeptide(), peptide) && peptideData.getStartPosition() == position){
				return peptideData;
			}
		}
		
		return null;
	}
	
	@Override
	public int compareTo(NetPanData other) {
		int last = this.allele.compareTo(other.allele);
        return last == 0 ? this.fastaFileName.compareTo(other.fastaFileName) : last;
	}
	
	public String toString(){
		
		String out = " ALLELE:"+ this.getAllele() +
					 " SEQUENCE:"+this.getFastaFileName() +
				     " IDENTIFIED_EPITOPES:" + this.getIdentifiedEpitopes();
		return out;
	}
}
