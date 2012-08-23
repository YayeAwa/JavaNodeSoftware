package org.vamdc.tapservice.vss2;
import org.vamdc.dictionary.VSSPrefix;

/**
 * VSS2 restriction keyword prefix holder, with index
 * @author doronin
 */
public class Prefix implements Comparable<Prefix>{

	private int index;
	private VSSPrefix value;
	
	public Prefix(VSSPrefix value, int index){
		this.index = index;
		this.value = value;
	}
	
	public VSSPrefix getPrefix(){
		return this.value;
	}
	
	public int getIndex(){
		return index;
	}
	
	public String toString(){
		if (value!=null)
			return value.name()+Integer.toString(index);
		return "";
	}

	@Override
	public boolean equals(Object o){
		if (!(o instanceof Prefix))
			return false;
		Prefix pref = (Prefix)o;
		if (pref!=null && pref.getPrefix()==null && value==null) 
			return true;
		else if (pref==null || pref.getPrefix()==null || value==null)
			return false;
		return pref.getPrefix().equals(value) && pref.getIndex()==index; 
	}

	@Override
	public int compareTo(Prefix pref) {
		if (!pref.getPrefix().equals(value))
			return value.compareTo(pref.getPrefix());
		else
			return Integer.valueOf(index).compareTo(Integer.valueOf(pref.getIndex()));
	}
	
}
