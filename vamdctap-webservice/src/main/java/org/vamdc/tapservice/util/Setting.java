package org.vamdc.tapservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

public enum Setting {
	
	

	baseurls("baseurl","http://host.name:8080/tapservice#http://mirror.host/tapservice"),
	selfcheck_interval("selfcheck_interval","60"),
	limits("limits_enable","false"),
	limit_states("limit_states","-1"),
	limit_processes("limit_processes","-1"),
	xsams_prettyprint("xml_prettyprint","false"),
	xsams_idprefix("xsams_id_prefix","DBNAME"),
	class_dao_availability("dao_test_class","org.vamdc.database.dao.ClassName"),
	class_plugin("database_plug_class","org.vamdc.database.tap.OutputBuilder"),
	test_queries("test_queries","select species where atomsymbol like '%';select * where inchikey='UGFAIRIUMAVXCW-UHFFFAOYSA-N'"),
	returnables("returnables","keyword1;keyword2;..."),
	processors("processors","ivo://vamdc/processor_1#ivo://vamdc/processor2")
	
	;
	
	
	
	private String property;
	private String value;
	private String defValue;
	
	Setting(String propertyName, String defaultValue){
		this.property = propertyName;
		this.defValue = defaultValue;
		this.value = defaultValue;
	}
	
	
	public static void load(ServletContext context, String configPath){
		Properties props = new Properties();
		InputStream confStream = getConfStream(context,configPath);
		try {
			props.load(confStream);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (confStream!=null)
				try {
					confStream.close();
				} catch (IOException e) {}
		}
		
		for (Setting option:Setting.values()){
			option.value = props.getProperty(option.property, option.defValue);
		}
		
	}
	
	public static boolean isConfigured(){
		for (Setting option:Setting.values()){
			if (!option.value.equals(option.defValue))
				return true;
		}
		return false;
	}
	
	private static InputStream getConfStream(ServletContext context,String config){
		if (config == null || config.length()==0)
			config=SettingsFilter.CONFIGURATION_PARAMETER_NAME_DEFAULT;
		InputStream confStream = context.getResourceAsStream(config);
		return confStream;

	}
	
	public String getValue(){
		return this.value;
	}


	public boolean isEnabled() {
		return Boolean.parseBoolean(value);
	}


	public Integer getInt() {
		return Integer.parseInt(value);
	}
	
	public String getKey(){
		return this.property;
	}
	
	public String getDefault(){
		return this.defValue;
	}
	
}
