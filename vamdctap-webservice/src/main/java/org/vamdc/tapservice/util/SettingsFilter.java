package org.vamdc.tapservice.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.vamdc.xsams.util.XSAMSSettings;

public class SettingsFilter implements Filter{

	public final static String CONFIGURATION_PARAMETER_NAME="tapservice.configuration";
	public final static String CONFIGURATION_PARAMETER_NAME_DEFAULT="/WEB-INF/config/tapservice.properties";

	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter((HttpServletRequest)request, (HttpServletResponse)response);		
	}

	@Override
	public void init(FilterConfig conf) throws ServletException {
		ServletContext context = conf.getServletContext();
		Setting.load(context, conf.getInitParameter(CONFIGURATION_PARAMETER_NAME));
		
		//Initialize slave configurations
		//XSAMS setting
		XSAMSSettings.idPrefix.setStrValue(Setting.xsams_idprefix.getValue());
		if (Setting.limits.isEnabled()){
			XSAMSSettings.processesLimit.setIntValue(Setting.limit_processes.getInt());
			XSAMSSettings.statesLimit.setIntValue(Setting.limit_states.getInt());
		}
		
		//Init db plug instance
		DBPlugTalker.getDBPlug();
		DBPlugTalker.loadPlug(Setting.class_plugin.getValue());
	}

}
