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
import org.vamdc.tapservice.vss2.impl.QueryImpl;
import org.vamdc.tapservice.api.RequestInterface;
import org.vamdc.xsams.XSAMSFactory;

/**
 * Availability monitor daemon
 * Checks periodically, is able return availability status
 * 
 * @author doronin
 *
 */
//TODO: check error message on wrong DB class name
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
	
	private GregorianCalendar upSince;
	private GregorianCalendar backAt;
	private GregorianCalendar downAt;
	private ErrorCode lastState;
	private DatatypeFactory factory;
	
	private Thread monitorThread;
	private boolean running;
	
	private ObjectContext CayenneContext;
	
	private Logger logger;
	
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
		running=true;
		upSince=new GregorianCalendar();
		backAt=null;
		downAt=null;
		lastState=ErrorCode.OK;
		monitorThread=null;
		logger = LoggerFactory.getLogger("org.vamdc.tapservice");
		logger.debug("Initializing "+this.getClass().toString());
		try {
			factory=DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			logger.debug("Can't instantiaze datatype factory");
		}

	}
	
	@Override
	public void run() {
		logger.debug("In thread! :)");
		//Do self checks periodically
		while (running){
			ErrorCode currentState=selfCheck(CayenneContext);
			logger.debug("Montor self-check with status "+currentState);
			if (currentState!=lastState){
				updateTimestamps(currentState,lastState);
			}
			lastState=currentState;
			
			long sleeptime=1000L*Setting.selfcheck_interval.getInt();
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
			}
		}
		logger.debug("Monitor thread dying x[");
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
		AvailabilityMonitor mon = getMonitor();
		if (mon.CayenneContext==null && context !=null)
			mon.CayenneContext=context;
		if (mon.monitorThread==null){
			mon.monitorThread=new Thread(mon);
			mon.monitorThread.start();
		}
		mon.logger.debug("Thread "+mon.monitorThread.getId());
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
		logger.debug("Check config");
		if (!Setting.isConfigured()) return ErrorCode.CF_NONE;
		
		logger.debug("check plugin");
		if (!DBPlugTalker.checkPlugin())
			return ErrorCode.PL_UNAVAIL;
		
		logger.debug("check restrictables");
		//Check if plugin returns restrictables:
		Collection<Restrictable> keywords = DBPlugTalker.getRestrictables();
		String query = getTestQuery(keywords);
		//query database using DAO and check if we get some results
		//Init request with dummy query
		logger.debug("init request interface");
		RequestInterface myrequest = new RequestProcess(
				XSAMSFactory.getXsamsManager(),
				context,
				new QueryImpl(query, null));
		//Check if we parsed query:
		if (myrequest.getRestricts().size()!=keywords.size()) return ErrorCode.QP_BROKEN;
		logger.debug("check cayenne");
		ErrorCode cayenneStatus = cayenneCheck( myrequest);
		logger.debug("done!");
		if (cayenneStatus != ErrorCode.OK) return cayenneStatus;
		

		
		return ErrorCode.OK;
	}
	
	/**
	 * Generate test query out of array of restrictables:
	 * @param restrictables array of allowed column names
	 * @return query string with all column names present
	 */
	private static String getTestQuery(Collection<Restrictable> restrictables){
		String query = "select * where ";
		int i=0;
		for (Restrictable keyword:restrictables){
			query = query.concat(keyword.name()+" = "+(i++)+" ");
			if (i<restrictables.size()){
				query = query.concat(" AND ");
			}
		}
		return query;
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
		AvailabilityMonitor mon = getMonitor();
		if (mon.upSince==null || mon.factory==null) return null;
		return mon.factory.newXMLGregorianCalendar(mon.upSince);
	}

	public static XMLGregorianCalendar getBackAt() {
		checkRunning();
		AvailabilityMonitor mon = getMonitor();
		if (mon.backAt==null || mon.factory==null) return null;
		return mon.factory.newXMLGregorianCalendar(mon.backAt);
	}

	public static XMLGregorianCalendar getDownAt() {
		checkRunning();
		AvailabilityMonitor mon = getMonitor();
		if (mon.downAt==null || mon.factory==null) return null;
		return mon.factory.newXMLGregorianCalendar(mon.downAt);
	}
	
	public static void shutDown(){
		getMonitor().running=false;
	}

}
