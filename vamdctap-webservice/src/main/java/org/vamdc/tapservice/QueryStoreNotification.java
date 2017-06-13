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
import org.vamdc.tapservice.util.Setting;

public class QueryStoreNotification extends Thread{
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
		params.put(Setting.secret.getKey(), Setting.secret.getValue());
		params.put("queryToken", myRequest.getUUID(requestHttp.getMethod().toLowerCase()));
		params.put(Setting.resource.getKey(), Setting.resource.getValue());
		params.put("resourceVersion",myRequest.getLastModified().toString());
		params.put("accessType",requestHttp.getMethod().toLowerCase());
		params.put(Setting.vamdcnode_version.getKey(),Setting.vamdcnode_version.getValue());
		params.put("dataURL",Setting.tap_url.getValue()+URLEncoder.encode(requestHttp.getQueryString(),"UTF-8"));
		params.put("query",URLEncoder.encode(myRequest.getQueryString(),"UTF-8"));
		
		HttpURLConnection connection = null;	
				
		if(res.getStatus()==200 && !requestHttp.getHeader("user-agent").equals(Setting.querystore_agent.getValue())){
			connection = setupConnection(Setting.querystoreurl.getValue(),params);
			System.out.println(connection.getResponseCode());
		}
	}
	
	private HttpURLConnection setupConnection(String queryStoreUrl,Map<String,String> params) throws IOException{
		OutputStreamWriter writer = null;
		
		//encod the request parameters
		StringBuilder data= new StringBuilder();
		for(Entry<String,String> param : params.entrySet()){
			if(data.length()>0)
			   data.append("&");
			data.append(param.getKey().trim());
			data.append("=");
			data.append(param.getValue().trim());
		}

		//create the connection
		URL url = new URL(queryStoreUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
				
		//send the request
		writer = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
		writer.write(data.toString());
		writer.flush();
		writer.close();

		return conn;
	}
}