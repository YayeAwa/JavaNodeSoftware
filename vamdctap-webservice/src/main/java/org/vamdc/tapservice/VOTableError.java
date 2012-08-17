package org.vamdc.tapservice;

import java.util.Collection;

import net.ivoa.xml.votable.v1.Info;
import net.ivoa.xml.votable.v1.Resource;
import net.ivoa.xml.votable.v1.VOTABLE;

/**
 * Class providing error messages in VOTable format 
 * @author doronin
 */
public class VOTableError extends VOTABLE{
	
	public VOTableError(){
		super();
	}
	
	public VOTableError(Collection<String> errors){
		super();
		Resource res = new Resource();
		res.setType("RESULTS");
		this.getRESOURCE().add(res);
		
		for (String error:errors){
			res.getINFO().add(getErrorInfo(error));
		}		
	}
	
	private Info getErrorInfo(String text){
		Info result = new Info();
		result.setValueAttribute("ERROR");
		result.setName("QUERY_STATUS");
		result.setValue(text);
		return result;
	}
}
