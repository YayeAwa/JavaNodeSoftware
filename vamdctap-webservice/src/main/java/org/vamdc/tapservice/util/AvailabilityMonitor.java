package org.vamdc.tapservice.util;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.tapservice.RequestProcess;
import org.vamdc.tapservice.vss2.VSSParser;
import org.vamdc.tapservice.api.RequestInterface;
import org.vamdc.xsams.XSAMSFactory;

/**
 * Availability monitor daemon.
 * Periodically checks the components of the node software,
 * returns the error status if available. 
 * 
 * @author doronin
 *
 */
//TODO: check the error message on the wrong DB class name
public class AvailabilityMonitor implements Runnable{
	
	private static enum ErrorCode{ 
		OK("All tests passed"),
		DB_UNAVAIL("Database unavailable"),
		DB_EMPTY("Database empty"),
		DB_BADTEST("Database test class is incorrectly set"),
		QP_BROKEN("Broken query parser"),
		CF_NONE("Service not configured"),	
		CF_NOCONTEXT("Service context unavailable"),
		PL_UNAVAIL(""),
		;
		private final String message;
		ErrorCode(String message){
			this.message = message;
		}
		public String getMessage(){return this.message;}
		
	}
	
	private GregorianCalendar upSince=new GregorianCalendar();
	private GregorianCalendar backAt=null;
	private GregorianCalendar downAt=null;
	private ErrorCode lastState=ErrorCode.OK;
	private DatatypeFactory factory;
	
	private Thread monitorThread=null;
	private boolean keepRunning=true;
	
	private ObjectContext CayenneContext;
	
	private Logger logger = LoggerFactory.getLogger(AvailabilityMonitor.class);
	
	private static class MonitorHolder{
		private final static AvailabilityMonitor stored=new AvailabilityMonitor();
	}

	public static AvailabilityMonitor getMonitor(){
		return MonitorHolder.stored;
	}
	
	/**
	 * Monitor constructor
	 */
	public AvailabilityMonitor(){
		logger.debug("Initializing");
		try {
			factory=DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			logger.debug("Can not instantiate the DatatypeFactory",e);
		}

	}
	
	@Override
	public void run() {
		logger.debug("The availability monitor thread had started.");
		//Do self checks periodically
		while (keepRunning){
			ErrorCode currentState=selfCheck(CayenneContext);
			logger.debug("Montor self-check status {}",currentState);
			if (currentState!=lastState){
				updateTimestamps(currentState,lastState);
				lastState=currentState;
			}
			
			long sleeptime=1000L*Setting.selfcheck_interval.getInt();
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
			}
		}
		logger.debug("Monitor thread is dying x[");
	}
	
	private void updateTimestamps(ErrorCode state, ErrorCode lastState) {
		if (state==ErrorCode.OK && lastState!=ErrorCode.OK)
			backAt = new GregorianCalendar();
		else {
			downAt = new GregorianCalendar();
			backAt = null;
		}
	}
	
	private static void checkRunning(){
		ObjectContext context=BaseContext.getThreadObjectContext();
		
		AvailabilityMonitor monitor = getMonitor();
		
		if (monitor.CayenneContext==null && context !=null)
			monitor.CayenneContext=context;
		
		if (monitor.monitorThread==null){
			monitor.monitorThread=new Thread(monitor);
			monitor.monitorThread.start();
		}
		monitor.logger.debug("Started the monitor thread {}",monitor.monitorThread.getId());
	}

	private ErrorCode cayenneCheck(RequestInterface myrequest){
		//Check if we can get something from DB:
		Class<?> daoclass;
		try {
			daoclass = Class.forName(Setting.class_dao_availability.getValue());
			SelectQuery dbQuery=new SelectQuery(daoclass);
		
			dbQuery.setFetchLimit(10);
		
			List<?> testResult = myrequest.getCayenneContext().performQuery(dbQuery);
			//If we got nothing from DB, somethin's wrong
			if (testResult.size()<10|| testResult.size()>10) return ErrorCode.DB_EMPTY;
		}catch(CayenneRuntimeException e){
			logger.debug("Cayenne exception in monitor:"+e.getMessage());
			return ErrorCode.DB_UNAVAIL;
		}catch (ClassNotFoundException e1) {
			return ErrorCode.DB_BADTEST;
		}
		return ErrorCode.OK;
	}
	
	//Do a simple service self-check:
	private ErrorCode selfCheck(ObjectContext context){
		//Check service configuration
		logger.debug("Check the configuration");
		if (!Setting.isConfigured()) return ErrorCode.CF_NONE;
		
		logger.debug("Check the node plugin");
		if (!DBPlugTalker.checkPlugin())
			return ErrorCode.PL_UNAVAIL;
		
		logger.debug("Check the queryParser and restrictables");
		//Check if plugin returns restrictables:
		Collection<Restrictable> keywords = DBPlugTalker.getRestrictables();
		String query = getTestQuery(keywords);
		//query database using DAO and check if we get some results
		//Init request with dummy query
		logger.debug("init request interface");
		RequestInterface myrequest = new RequestProcess(
				XSAMSFactory.getXsamsManager(),
				context,
				VSSParser.parse(query));
		//Check if we parsed the query:
		if (myrequest.getQueryKeywords().size()!=keywords.size()) return ErrorCode.QP_BROKEN;
		logger.debug("Check the Apache Cayenne");
		ErrorCode cayenneStatus = cayenneCheck( myrequest);
		if (cayenneStatus != ErrorCode.OK) return cayenneStatus;
		return ErrorCode.OK;
	}
	
	/**
	 * Generate test query out of the collection of restrictables:
	 * @param restrictables supported
	 * @return query string with all the Restrictables present
	 */
	private static String getTestQuery(Collection<Restrictable> restrictables){
		StringBuilder query = new StringBuilder();
		query.append("select * where ");
		int i=0;
		for (Restrictable keyword:restrictables){
			query.append(keyword.name()).append(" = ").append((i++)).append(" ");
			if (i<restrictables.size()){
				query.append(" AND ");
			}
		}
		return query.toString();
	}
	
	public static boolean getServiceStatus(){
		checkRunning();
		return getMonitor().lastState==ErrorCode.OK;
	}
	
	public static String getStatusNote() {
		checkRunning();
		return "Monitor "+getMonitor().lastState.getMessage()+" plugin "+DBPlugTalker.getErrorMessage();
	}

	public static XMLGregorianCalendar getUpSince() {
		checkRunning();
		AvailabilityMonitor monitor = getMonitor();
		if (monitor.upSince==null || monitor.factory==null) return null;
		return monitor.factory.newXMLGregorianCalendar(monitor.upSince);
	}

	public static XMLGregorianCalendar getBackAt() {
		checkRunning();
		AvailabilityMonitor monitor = getMonitor();
		if (monitor.backAt==null || monitor.factory==null) return null;
		return monitor.factory.newXMLGregorianCalendar(monitor.backAt);
	}

	public static XMLGregorianCalendar getDownAt() {
		checkRunning();
		AvailabilityMonitor monitor = getMonitor();
		if (monitor.downAt==null || monitor.factory==null) return null;
		return monitor.factory.newXMLGregorianCalendar(monitor.downAt);
	}
	
	public static void shutDown(){
		getMonitor().keepRunning=false;
	}

}
