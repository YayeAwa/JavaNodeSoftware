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
		Settings.getSettings();
		Settings.loadConfig(context,conf.getInitParameter(Settings.CONFIGURATION_PARAMETER_NAME));
		
		//Initialize slave configurations
		//XSAMS setting
		XSAMSSettings.idPrefix.setStrValue(Settings.getValue(Settings.SETTING_XSAMSID_PREFIX));
		if (Settings.isTrue(Settings.SETTING_ENABLE_LIMITS)){
			XSAMSSettings.processesLimit.setIntValue(Settings.getIntValue(Settings.SETTING_PROCESSES_LIMIT));
			XSAMSSettings.statesLimit.setIntValue(Settings.getIntValue(Settings.SETTING_STATES_LIMIT));
		}
		
		//Init db plug instance
		DBPlugTalker.getDBPlug();
		DBPlugTalker.loadPlug(Settings.getValue(Settings.SETTING_DATABASE_PLUG_CLASS));
	}

}
