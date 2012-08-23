package org.vamdc.tapservice.vss2;

import java.util.Collection;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.vss2.impl.QueryImpl;

/**
 * Factory class for VSSParser library
 * @author doronin
 *
 */
public class VSSParser {

	public static Query parse(String query){
		return new QueryImpl(query);
	}
	
	public static Query parse(String query,Collection<Restrictable> filter){
		return new QueryImpl(query,filter);
	}
	
	static Query parseDebug(String query,Collection<Restrictable> filter){
		return new QueryImpl(query,filter,true);
		
	}
	
}
