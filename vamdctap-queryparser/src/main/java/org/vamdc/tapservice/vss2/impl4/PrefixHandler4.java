package org.vamdc.tapservice.vss2.impl4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.vamdc.dictionary.VSSPrefix;
import org.vamdc.tapservice.vss2.Prefix;
import org.vamdc.tapservice.vss2.RestrictExpression;

/**
 * Prefix handler, keeps all provided prefix index letters and assigns numbers to them.
 *
 */
public class PrefixHandler4 {
	private SortedSet<String> reactIndex = new TreeSet<String>();
	private SortedSet<String> prodIndex = new TreeSet<String>();
	private Set<Prefix> prefixes = new TreeSet<Prefix>();
	
	private List<String> reactList;
	private List<String> prodList;
	
	/**
	 * Constructor that scans a list of available RestrictExpression structures and assigns correct prefixes to them
	 * @param restrictsList
	 */
	public PrefixHandler4(Collection<RestrictExpression> restrictsList){
		for (RestrictExpression rexp:restrictsList){
			if (rexp instanceof RestrictExpression4)
				assignPrefixPass1((RestrictExpression4) rexp);
		}
		if (reactIndex.size()>1 || prodIndex.size()>1){//If we have more than one reactant or product, we need a second pass to assign indexes.
			reactList = new ArrayList<String>(reactIndex);
			prodList = new ArrayList<String>(prodIndex);
			for (RestrictExpression rexp:restrictsList){
				if (rexp instanceof RestrictExpression4)
					assignPrefixPass2((RestrictExpression4) rexp);
			}
		}
		
	}
	
	public Collection<Prefix> getPrefixes(){
		return prefixes;
	}
	
	/**
	 * Assign prefix to RestrictExpression, collect index letters
	 * @param rexp
	 */
	private void assignPrefixPass1(RestrictExpression4 rexp){
		String prefix = rexp.getPrefixStr();
		if (prefix==null || prefix.length()==0) return;
		VSSPrefix pref = null;
		try {
			pref = VSSPrefix.valueOf(prefix.toUpperCase());//Pref would be a prefix without an index.
			Prefix newPrefix =  new Prefix(pref,0);
			rexp.setPrefix(newPrefix);//Set prefix for RestrictExpression
			prefixes.add(newPrefix);//Save prefix for future
		}catch (Exception e){
			try{
				String token = prefix.substring(0, prefix.length()-1).toUpperCase();
				String index = prefix.substring(prefix.length()-1).toUpperCase();
				pref = VSSPrefix.valueOf(token);
				//Here we have an index symbol, but so far we can only add it to the corresponding indexes set.
				Prefix newPrefix =  new Prefix(pref,0);
				rexp.setPrefix( newPrefix);
				prefixes.add(newPrefix);
				switch(pref){
				case REACTANT:
					reactIndex.add(index);
					break;
				case PRODUCT:
					prodIndex.add(index);
					break;
				default:
					break;
				}
			}catch (Exception e2){
			}
		}
	}
	
	/**
	 * Pass 2 for prefixes assignment, re-enumerating prefix indexes for reactants and products
	 * @param rexp
	 */
	private void assignPrefixPass2(RestrictExpression4 rexp){
		Prefix pref = rexp.getPrefix();
		if (pref!=null && (pref.getPrefix()==VSSPrefix.REACTANT || pref.getPrefix() == VSSPrefix.PRODUCT)){
			//Only need to go on if we handle prefixes that are supposed to have indexes.
			//Get what is supposed to be an index of this prefix
			String prefix = rexp.getPrefixStr();
			String index = prefix.toUpperCase().substring(prefix.length()-1);
			List<String> prefList;
			switch (pref.getPrefix()){
			case REACTANT:
				prefList = reactList;
				break;
			case PRODUCT:
				prefList = prodList;
				break;
			default:
				prefList = new ArrayList<String>();
			}
			Prefix newPrefix = new Prefix(pref.getPrefix(),prefList.indexOf(index));
			rexp.setPrefix(newPrefix);
			prefixes.add(newPrefix);//Save prefix for future
			
		}
	}
	

}
