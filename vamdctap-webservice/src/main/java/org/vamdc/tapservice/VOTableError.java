package org.vamdc.tapservice;

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
	
	public VOTableError(String errText){
		super();
		Resource res = new Resource();
		res.setType("RESULTS");
		this.getCOOSYSAndGROUPSAndPARAMS().add(res);
		
		Info text = new Info();
		res.getINFOSAndCOOSYSAndGROUPS().add(text);
		text.setInfoValue("ERROR");
		text.setName("QUERY_STATUS");
		text.setValue(errText);

		
	}
}
