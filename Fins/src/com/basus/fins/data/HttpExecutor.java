/**
 * 
 */
package com.basus.fins.data;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

/**
 * @author sambit
 *
 */
public class HttpExecutor implements Runnable {
	HttpClient client = null;
	HttpMethod method = null;
	
	/**
	 * @param params
	 */
	public HttpExecutor(HttpClient client, HttpMethod method) {
		this.client = client;
		this.method = method;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
