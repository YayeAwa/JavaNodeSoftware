package org.vamdc.tapservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Singleton for keeping service configuration;
 * @author doronin
 */

public class Settings {
	//TODO: rewrite with enum
	public final static String CONFIGURATION_PARAMETER_NAME="tapservice.configuration";
	public final static String CONFIGURATION_PARAMETER_NAME_DEFAULT="/WEB-INF/config/tapservice.properties";

	/*
	 * Setting names
	 * */
	public final static String SETTING_BASEURL="baseurl";//Service base url for Capabilities
	public final static String SETTING_SELFCHECK_INTERVAL="selfcheck_interval";//Service selfcheck interval in seconds
	public final static String SETTING_ENABLE_LIMITS="limits_enable";
	public final static String SETTING_STATES_LIMIT="limit_states";
	public static final String SETTING_PROCESSES_LIMIT = "limit_processes";
	public static final String SETTING_ALWAYS_ADD_SOURCES = "force_sources";//Always add 'sources' branch of XSAMS
	public static final String SETTING_OUTPUT_PRETTYPRINT = "xml_prettyprint";//Enable XML pretty printing
	public final static String SETTING_XSAMSID_PREFIX = "xsams_id_prefix";//XSAMS ID prefix
	public final static String SETTING_DAO_AVAILABILITY_CLASS = "dao_test_class";//Class to select with Cayenne to check if it's working
	public final static String SETTING_DATABASE_PLUG_CLASS = "database_plug_class";//Class implementing DatabasePlug interface
	public final static String SETTING_TEST_QUERIES = "test_queries";//Semicolon-separated list of test queries
	

	@SuppressWarnings("serial")
	public static final Map<String,String> defaultConfiguration = new HashMap<String,String>(){{
		put(SETTING_BASEURL,"http://host.name:8080/tapservice");
		put(SETTING_SELFCHECK_INTERVAL,"60");
		put(SETTING_ENABLE_LIMITS,"false");
		put(SETTING_PROCESSES_LIMIT,"-1");
		put(SETTING_STATES_LIMIT,"-1");
		put(SETTING_ALWAYS_ADD_SOURCES,"true");
		put(SETTING_OUTPUT_PRETTYPRINT,"false");
		put(SETTING_XSAMSID_PREFIX,"DBNAME");
		put(SETTING_DAO_AVAILABILITY_CLASS,"org.vamdc.database.dao.ClassName");
		put(SETTING_DATABASE_PLUG_CLASS,"org.vamdc.database.builders.ClassName");
		put(SETTING_TEST_QUERIES,"select species where atomsymbol like '%';");
	}};


	private Properties props;
	private boolean isConfigured;


	private static class PrefsHolder{
		private final static Settings stored=new Settings();
	}

	/**
	 * @param context servlet context to retrieve configuration location from
	 * @return URL with configuration, to call openStream() on it later
	 */
	private static InputStream getConfStream(ServletContext context,String config){
		Logger logger = LoggerFactory.getLogger("org.vamdc.tapservice");
		if (config == null || config.length()==0)
			config=CONFIGURATION_PARAMETER_NAME_DEFAULT;
		logger.debug("Config path is "+config);
		InputStream confStream = context.getResourceAsStream(config);
		logger.debug("Result URL is "+confStream);
		return confStream;

	}

	/**
	 * Default constructor
	 */
	public Settings(){
		props = new Properties();
		isConfigured=false;
		Logger logger = LoggerFactory.getLogger("org.vamdc.tapservice");
		logger.debug("Instantiated Settings object");
	}

	/**
	 * Configure myself
	 * @param context servlet context to retrieve configuration location from
	 * @return true if loaded config successfully
	 */
	public static boolean loadConfig(ServletContext context, String configPath) {
		Logger logger = LoggerFactory.getLogger("org.vamdc.tapservice");

		if (getSettings().isConfigured) {
			logger.debug("TAPService Already configured");
			return true;//Reject to reconfigure if we are already configured
		}

		Properties props = getSettings().props;
		boolean result = false;
		try{
			InputStream confStream = getConfStream(context,configPath);
			props.load(confStream);
			confStream.close();
			logger.debug("Loaded TAPService configuration");
		} catch (IOException e) {
			logger.debug("Exception while loading configuration: "+e.getMessage());
		} finally{
			result=(props.size()>0);
		}
		if (!result){
			setDefaults();
			logger.debug("Used default configuration");
		}
		getSettings().isConfigured=result;
		return result;
	}

	public static Set<String> getKeys(){
		Set<String> keys = new HashSet<String>();
		for (Object key:getSettings().props.keySet()){
			keys.add((String) key);
		}
		return keys;
	}

	/**
	 * Reset configuration to default
	 */
	private static void setDefaults(){
		getSettings().props.clear();
		getSettings().props.putAll(defaultConfiguration);
	}



	public static Settings getSettings(){
		return PrefsHolder.stored;
	}

	public static Settings getSettings(ServletContext context){
		return PrefsHolder.stored;
	}

	public static String getValue(String key){
		return getSettings().props.getProperty(key,defaultConfiguration.get(key));
	}

	public static Integer getIntValue(String key){
		return Integer.parseInt(getValue(key));
	}

	public static Double getDoubleValue(String key){
		return Double.parseDouble(getValue(key));
	}

	public static boolean isTrue(String key){
		return Boolean.parseBoolean(getValue(key));
	}

	public static void setValue(String key,String value){
		getSettings().props.setProperty(key, value);
	}

	public static boolean isConfigured() {
		return getSettings().isConfigured;
	}

}
