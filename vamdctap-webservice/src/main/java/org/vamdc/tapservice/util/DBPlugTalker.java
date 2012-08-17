package org.vamdc.tapservice.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.api.DatabasePlugin;
import org.vamdc.tapservice.api.RequestInterface;

public class DBPlugTalker{
	
	private DatabasePlugin plug;
	
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
			
			DatabasePlugin dbPlugInst = (DatabasePlugin) Class.forName(className).newInstance();
			plug.setPlugin(dbPlugInst);
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

	public DatabasePlugin getPlugin(){
		return this.plug;
	}
	
	public void setPlugin(DatabasePlugin plug) {
		this.plug = plug;
	}

	public static void buildXSAMS(RequestInterface userRequest) {
		if (getDBPlug().getPlugin()!=null)
			getDBPlug().getPlugin().buildXSAMS(userRequest);
	}
	
	public static Map<HeaderMetrics,Integer> getMetrics(RequestInterface userRequest) {
		if (getDBPlug().getPlugin()!=null)
			return getDBPlug().getPlugin().getMetrics(userRequest);
		
		return new HashMap<HeaderMetrics,Integer>();
	}

	public static Collection<Restrictable> getRestrictables() {
		if (getDBPlug().getPlugin()!=null)
			return getDBPlug().getPlugin().getRestrictables();
		else 
			return new ArrayList<Restrictable>();
	}
	
	public static boolean checkPlugin(){
		return getDBPlug().getPlugin()!=null && getDBPlug().getPlugin().isAvailable();
	}
	
	public static String getErrorMessage(){
		if (getDBPlug().getPlugin()!=null)
			return getDBPlug().getPlugin().getErrorMessage();
		return "Database plugin not loaded";
	}
	
}
