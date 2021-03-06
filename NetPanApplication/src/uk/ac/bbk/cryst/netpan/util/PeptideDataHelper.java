package uk.ac.bbk.cryst.netpan.util;

import java.util.Collections;
import java.util.List;

import uk.ac.bbk.cryst.netpan.model.MHCIIPeptideData;
import uk.ac.bbk.cryst.netpan.model.PeptideData;

public class PeptideDataHelper {

	public static PeptideData getTheStrongestBinder(List<PeptideData> list){
		if(list == null || list.isEmpty()){
			return null;
		}
		Collections.sort(list);
		return list.get(0);
	}
	
	public static MHCIIPeptideData getTheStrongestBinderII(List<MHCIIPeptideData> list){
		if(list == null || list.isEmpty()){
			return null;
		}
		Collections.sort(list);
		return list.get(0);
	}
	
	public static boolean IsAllBindEfficiently(List<PeptideData> list){
		boolean b = true;
		for(PeptideData pep: list){
			if(pep.getIC50Score() > 1000){
				b = false;
			}
		}
		return b;
	}
}
