package org.vamdc.tapservice;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.vamdc.tapservice.api.RequestInterface;

public class QueryStoreNotification extends Thread{
	static final String QUERY_STORE_URL = "http://querystore.vamdc.eu/NotificationListener?";
	static final String VAMDC_NODE_VERSION = "12.07";
	static final String QS_USER_AGENT = "VAMDC Query store";
	static final String TAP_URL = "http://basecol2015.vamdc.eu/12_07/TAP/sync?";
	static final String RESOURCE = "http://basecol2015.vamdc.org/12_07/";
	
	
	private RequestInterface myRequest;
	private HttpServletRequest requestHttp;
	
	public QueryStoreNotification(HttpServletRequest requestHttp, Response res , RequestProcess myRequest){
		this.requestHttp = requestHttp;
		this.myRequest = myRequest;
		
		try {
			callPostRequest(res);
		} catch (IOException e) {
			System.out.println("Problem during post request");
			e.printStackTrace();
		}
		
	}
	
	private void callPostRequest(Response res) throws IOException{	
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("queryToken", myRequest.getUUID(requestHttp.getMethod().toLowerCase()));
		params.put("accededResource", RESOURCE);
		params.put("resourceVersion",myRequest.getLastModified().toString());
		params.put("accessType",requestHttp.getMethod().toLowerCase());
		params.put("outputFormatVersion",VAMDC_NODE_VERSION);
		params.put("dataURL",TAP_URL+URLEncoder.encode(requestHttp.getQueryString(),"UTF-8"));
		params.put("query",URLEncoder.encode(myRequest.getQueryString(),"UTF-8"));
		
		HttpURLConnection connection = null;	
				
		if(res.getStatus()==200 && !requestHttp.getHeader("user-agent").equals(QS_USER_AGENT)){
			connection = setupConnection(QUERY_STORE_URL,params);
			System.out.println(connection.getResponseCode());
		}
	}
	
	private HttpURLConnection setupConnection(String queryStoreUrl,Map<String,String> params) throws IOException{
		OutputStreamWriter writer = null;
		
		//encodage des paramètres de la requête
		StringBuilder data= new StringBuilder();
		for(Entry<String,String> param : params.entrySet()){
			if(data.length()>0)
			   data.append("&");
			data.append(param.getKey().trim());
			data.append("=");
			data.append(param.getValue().trim());
		}

		//création de la connection
		URL url = new URL(queryStoreUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
				
		//envoi de la requête
		writer = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
		writer.write(data.toString());
		writer.flush();
		writer.close();

		return conn;
	}
}