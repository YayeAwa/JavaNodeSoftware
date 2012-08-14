package org.vamdc.tapservice.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.api.DatabasePlug;
import org.vamdc.tapservice.api.RequestInterface;

public class DBPlugTalker{
	
	private DatabasePlug plug;
	
	private static class PlugHolder{
		private final static DBPlugTalker stored=new DBPlugTalker();
	}
	
	public DBPlugTalker(){
	}
	
	public static DBPlugTalker getDBPlug(){
		return PlugHolder.stored;
	}
	
	public static void loadPlug(String className){
		DBPlugTalker plug = getDBPlug();
		Logger logger = LoggerFactory.getLogger("org.vamdc.tapservice");
		
		try {
			
			DatabasePlug dbPlugInst = (DatabasePlug) Class.forName(className).newInstance();
			plug.setPlug(dbPlugInst);
			logger.debug("Instantiated db plug");
		} catch (ClassNotFoundException e) {
			logger.error("db plug class not found "+e.getMessage());
			e.printStackTrace();
		} catch (SecurityException e) {
			logger.error("db plug security exception "+e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			logger.error("db plug illegal arguments "+e.getMessage());
			e.printStackTrace();
		} catch (InstantiationException e) {
			logger.error("db plug instantiation exception "+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error("db plug visibility error "+e.getMessage());
			e.printStackTrace();
		}
	}

	public DatabasePlug getPlug(){
		return this.plug;
	}
	
	public void setPlug(DatabasePlug plug) {
		this.plug = plug;
	}

	public static void buildXSAMS(RequestInterface userRequest) {
		if (getDBPlug().getPlug()!=null)
			getDBPlug().getPlug().buildXSAMS(userRequest);
	}
	
	public static Map<HeaderMetrics,Integer> getMetrics(RequestInterface userRequest) {
		if (getDBPlug().getPlug()!=null)
			return getDBPlug().getPlug().getMetrics(userRequest);
		
		return new HashMap<HeaderMetrics,Integer>();
	}

	public static Collection<Restrictable> getRestrictables() {
		if (getDBPlug().getPlug()!=null)
			return getDBPlug().getPlug().getRestrictables();
		else 
			return new ArrayList<Restrictable>();
	}
	
	public static boolean checkPlugin(){
		return getDBPlug().getPlug()!=null && getDBPlug().getPlug().isAvailable();
	}
	
	public static String getErrorMessage(){
		if (getDBPlug().getPlug()!=null)
			return getDBPlug().getPlug().getErrorMessage();
		return "Database plugin not loaded";
	}
	
}
