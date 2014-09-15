/*
 * YahooQuote.java
 *
 * Created on June 25, 2007, 3:04 PM
 *
 * This file is copyrighted to the author as listed below. If no author is
 * specified, the file is copyrighted to Sambit Basu (sambitBasu@yahoo.com).
 * Unless otherwise specified, the contents of the file can be freely copied,
 * modified and distributed under the terms of Lesser GNU Public License (LGPL).
 */

package com.basus.fins.data;

import com.basus.fins.listener.NetListener;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.ui.PortfolioUI;
import com.basus.fins.util.CSVFile;
import com.basus.fins.util.HelperUtil;
import com.basus.fins.util.HistoricCSV;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;

import sun.rmi.runtime.Log;


/**
 *
 * @author sambit
 */
public class YahooQuote implements Runnable {
	public static final int QUOTE_TYPE_OTHER = 0;
	public static final int QUOTE_TYPE_CURRENT = 1;
	public static final int QUOTE_TYPE_HISTORIC = 2;
	
	private static final char AMP = '&';
	private static final char EQUAL = '=';
	private static Logger log = Logger.getLogger(YahooQuote.class);
	
    private HostConfiguration yahooHost = null;
    private GetMethod method = null;
    private HttpClient client = null;
    
    private CSVFile csvResponse = null;
    private Exception lastEx = null;
    private boolean isException = false;
    private String strQuery = null;
    
    private QuoteParams quoteParams = null;
    private Vector<NetListener> netListeners = new Vector<NetListener>();
    
    private int quoteType = QUOTE_TYPE_OTHER;
    
    static {
        
    }
    
    /** Creates a new instance of YahooQuote */
    public YahooQuote(HashSet<String> symbols) {
    	this.quoteType = QUOTE_TYPE_CURRENT;
    	quoteParams = new YahooCurrentQuoteParams(symbols);
    	csvResponse = new CSVFile();
        initQuote();
    }
    
    public YahooQuote(String symbol) {
    	this.quoteType = QUOTE_TYPE_CURRENT;
    	HashSet<String> set = new HashSet<String>(); 
		set.add(symbol);
    	quoteParams = new YahooCurrentQuoteParams(set);
    	csvResponse = new CSVFile();
    	initQuote();
    }
    
    public YahooQuote(String symbol, Date date, int quoteType) {
    	this(symbol, date, date, quoteType);
    }
    
    public YahooQuote(String symbol, Date startDate, Date endDate, int quoteType) {
    	this.quoteType = quoteType;
    	if (QUOTE_TYPE_HISTORIC == this.quoteType) {
	    	quoteParams = new YahooHistoricQuoteParams(symbol, startDate, endDate);
	    	csvResponse = new HistoricCSV(symbol);
    	}
    	else {
    		HashSet<String> set = new HashSet<String>(); 
    		set.add(symbol);
    		quoteParams = new YahooCurrentQuoteParams(set);
        	csvResponse = new CSVFile();
    	}
    	initQuote();
    }
    
    public boolean addNetListener(NetListener listener) {
		return netListeners.add(listener);
	}
    
    public boolean isException() {
    	return this.isException;
    }
    
    public Exception getLastException() {
    	return this.lastEx;
    }
    
    public void clearException() {
    	this.isException = false;
    	this.lastEx = null;
    }

    public void initQuote() {
    	yahooHost = new HostConfiguration();
    	method = new GetMethod();
        client = new HttpClient();
        
        yahooHost.setHost(quoteParams.getHostUri());
        quoteParams.createQueryString();
        try {
	        method.setPath(URIUtil.encodePathQuery(quoteParams.getPath()));
	        method.setQueryString(URIUtil.encodePathQuery(strQuery));
        }
        catch(URIException uriEx) {
        	log.error(uriEx);
        }
    }
    
    public int getQuoteType() {
    	return quoteType;
    }

	@Override
	public void run() {
		if (!quoteParams.isValidParams()) {
			if (log.isDebugEnabled()) {
				log.debug("Invalid parameters for quote request. Aborting");
			}
			return;
		}
        int status = 0;
        
        try {
        	if (log.isDebugEnabled()) {
        		log.debug("Excuting: " + yahooHost.getHostURL() + "/" + method.getPath() + "?" + method.getQueryString());
        	}
        	PortfolioUI.setStatus1("Getting index price from internet", 3000);
        	PortfolioUI.setStatus2("Connected to db " + Data.getDbDirectory(), 0);
			status = client.executeMethod(yahooHost, method);
			
			if (HttpStatus.SC_OK == status) {
	            csvResponse.setContent(method.getResponseBody());
	            notifyNetListeners(QuoteListener.EVENT_TYPE_CHANGED, this);
	            if (log.isDebugEnabled()) {
	            	log.debug("Received HTTP Response:\n" + method.getResponseBodyAsString());
	            }
	            PortfolioUI.setStatus1("Last update: " + HelperUtil.getDateTime(), 0);
	        }
	        else {
	            log.error(yahooHost.getHostURL() + method.getURI() + " returned HTTP status " + status);
	            isException = true;
	            lastEx = new IOException(method.getURI() + " returned HTTP status " + status);
	        }
		} catch (HttpException e) {
			isException = true;
            lastEx = e;
            log.error(e);
		} catch (IOException e) {
			isException = true;
            lastEx = e;
            log.error(e);
		}
	}
	
	public CSVFile getCSVResponse() {
		return this.csvResponse;
	}
	
	private void notifyNetListeners(QuoteListener.EventType eventType, YahooQuote quote) {
		Iterator<NetListener> it = netListeners.iterator();
		NetListener listener = null;
		
		while(it.hasNext()) {
			listener = it.next();
			if (null != listener) {
				if (eventType.equals(QuoteListener.EVENT_TYPE_CHANGED)) {
					listener.updateReceived(quote.getCSVResponse());
				}
			}
		}
	}
	
	private class YahooCurrentQuoteParams implements QuoteParams {
	    private static final String YAHOO_HOST_URI = "download.finance.yahoo.com";
	    private static final String YAHOO_PATH = "/d/quotes.csv";
	    private static final String YAHOO_QS_SYMBOL_PARAM = "s";	// comma-separated multiple symbols
	    private static final String YAHOO_QS_CONSTANT = "&f=sl1d1t1c1ohgv&e=.csv";

	    private HashSet<String> symbols = null;
	    
	    YahooCurrentQuoteParams(HashSet<String> symbols) {
	    	this.symbols = symbols;
	    }
	    
	    public String getHostUri() {
	    	return YAHOO_HOST_URI;
	    }
	    
	    public String getPath() {
	    	return YAHOO_PATH;
	    }
	    
	    public String getSymbolParam() {
	    	return YAHOO_QS_SYMBOL_PARAM;
	    }
	    
	    public String getQuerystringConstant() {
	    	return YAHOO_QS_CONSTANT;
	    }
	    
	    public String createQueryString() {
	        StringBuilder sb = new StringBuilder();
	        Iterator<String> it = symbols.iterator();
	        String symbol;
	        
	        while (it.hasNext()) {
	        	symbol = it.next();
	            sb.append(YAHOO_QS_SYMBOL_PARAM).append("=").append(symbol);
	            
	            sb.append("&");
	        }
	            
	        sb.append(YAHOO_QS_CONSTANT);
	        
	        strQuery = sb.toString();
	        
	        return strQuery;
	    }

		@Override
		public String getEndDateParam() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getEndMonthParam() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getEndYearParam() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getStartDateParam() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getStartMonthParam() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getStartYearParam() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public boolean isValidParams() {
			return symbols != null && symbols.size() > 0;
		}
	}
	
	// http://ichart.finance.yahoo.com/table.csv?s=YHOO&d=7&e=11&f=2010&g=d&a=3&b=12&c=1996&ignore=.csv

	private class YahooHistoricQuoteParams implements QuoteParams {
	    private static final String YAHOO_HOST_URI = "ichart.finance.yahoo.com";
	    private static final String YAHOO_PATH = "/table.csv";
	    private static final String YAHOO_QS_SYMBOL_PARAM = "s";	// comma-separated multiple symbols
	    private static final String YAHOO_QS_START_DATE_PARAM = "b";
	    private static final String YAHOO_QS_START_MONTH_PARAM = "a";	// month is 0-based
	    private static final String YAHOO_QS_START_YEAR_PARAM = "c";
	    private static final String YAHOO_QS_END_DATE_PARAM = "e";
	    private static final String YAHOO_QS_END_MONTH_PARAM = "d";	// month is 0-based
	    private static final String YAHOO_QS_END_YEAR_PARAM = "f";
	    private static final String YAHOO_QS_CONSTANT = "ignore=.csv";
	    
	    private String symbol = null;
	    private GregorianCalendar startCal = new GregorianCalendar();
	    private GregorianCalendar endCal = new GregorianCalendar();
	    

	    public YahooHistoricQuoteParams(String symbol, Date date) {
	    	this(symbol, date, date);
	    }
	    
	    public YahooHistoricQuoteParams(String symbol, Date startDate, Date endDate) {
	    	this.symbol = symbol;
	    	this.startCal.setTime(startDate);
	    	this.endCal.setTime(endDate);
	    }
	    
	    public String getHostUri() {
	    	return YAHOO_HOST_URI;
	    }
	    
	    public String getPath() {
	    	return YAHOO_PATH;
	    }
	    
	    public String getSymbolParam() {
	    	return YAHOO_QS_SYMBOL_PARAM;
	    }
	    
	    public String getQuerystringConstant() {
	    	return YAHOO_QS_CONSTANT;
	    }
	    
	    public String getStartDateParam() {
	    	return YAHOO_QS_START_DATE_PARAM;
	    }
	    
	    public String getStartMonthParam() {
	    	return YAHOO_QS_START_MONTH_PARAM;
	    }
	    
	    public String getStartYearParam() {
	    	return YAHOO_QS_START_YEAR_PARAM;
	    }
	    
	    public String getEndDateParam() {
	    	return YAHOO_QS_END_DATE_PARAM;
	    }
	    
	    public String getEndMonthParam() {
	    	return YAHOO_QS_END_MONTH_PARAM;
	    }
	    
	    public String getEndYearParam() {
	    	return YAHOO_QS_END_YEAR_PARAM;
	    }

		@Override
		public String createQueryString() {
			// http://ichart.finance.yahoo.com/table.csv?s=YHOO&d=7&e=11&f=2010&g=d&a=3&b=12&c=1996&ignore=.csv
			StringBuilder sb = new StringBuilder();
	        
	        sb.append(YAHOO_QS_SYMBOL_PARAM).append(EQUAL).append(symbol)
	        	.append(AMP).append(YAHOO_QS_END_MONTH_PARAM).append(EQUAL).append(endCal.get(Calendar.MONTH)) // since month is 0-based
	        	.append(AMP).append(YAHOO_QS_END_DATE_PARAM).append(EQUAL).append(endCal.get(Calendar.DATE))
	        	.append(AMP).append(YAHOO_QS_END_YEAR_PARAM).append(EQUAL).append(endCal.get(Calendar.YEAR))
	        	.append(AMP).append(YAHOO_QS_START_MONTH_PARAM).append(EQUAL).append(startCal.get(Calendar.MONTH)) // since month is 0-based
	        	.append(AMP).append(YAHOO_QS_START_DATE_PARAM).append(EQUAL).append(startCal.get(Calendar.DATE))
	        	.append(AMP).append(YAHOO_QS_START_YEAR_PARAM).append(EQUAL).append(startCal.get(Calendar.YEAR))
	         	.append(AMP).append(YAHOO_QS_CONSTANT);
	        
	        strQuery = sb.toString();

	        return strQuery;
		}
		
		public boolean isValidParams() {
			if (null == this.symbol || symbol.trim().equals("")) {
				return false;
			}
			
			return true;
		}
	}
}
