/**
 * 
 */
package com.basus.fins.data;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.basus.fins.listener.AssetListener;
import com.basus.fins.listener.NetListener;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.listener.AssetListener.EventType;
import com.basus.fins.util.CSVFile;
import com.basus.fins.util.HelperUtil;
import com.basus.fins.work.WorkQueue;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambit
 *
 */
public class IndexUpdateTask implements NetListener {
	private static Logger log = Logger.getLogger(IndexUpdateTask.class);
	
	private LinkedHashSet<QuoteListener> quoteListeners = new LinkedHashSet<QuoteListener>();
	private YahooQuote quoteTask = null;
	private String index = DEFAULT_INDEX;
	private Date date = null;
	/**
	 * 
	 */
	public IndexUpdateTask(String indexSymbol, Date date) {
		this.index = indexSymbol;
		this.date = date;
	}
	
	public IndexUpdateTask(String indexSymbol) {
		this.index = indexSymbol;
		this.date = null;
	}
	
	public boolean addQuoteListener(QuoteListener listener) {
		return quoteListeners.add(listener);
	}

	public void submitTask() {
		this.makeTask();
	}
	
	private void makeTask() {
		WorkQueue<Runnable> queue = null;
		if (null == this.date) {
			quoteTask = new YahooQuote(this.index);
			quoteTask.addNetListener(this);
			queue = (WorkQueue<Runnable>)WorkQueue.getCurrentQuoteQueue();
		}
		else {
			quoteTask = new YahooQuote(this.index, this.date, YahooQuote.QUOTE_TYPE_HISTORIC);
			quoteTask.addNetListener(this);
			queue = (WorkQueue<Runnable>)WorkQueue.getHistoricQuoteQueue();
		}
		queue.add(quoteTask);
	}
	
	@SuppressWarnings("unchecked")
	private void notifyQuoteListeners(QuoteListener.EventType eventType, YahooQuote quote) {
		Iterator<QuoteListener> it = quoteListeners.iterator();
		QuoteListener listener = null;
		Quote quoteTable = Quote.getInstance();
		
		while(it.hasNext()) {
			listener = it.next();
			if (null != listener) {
				if (eventType.equals(QuoteListener.EVENT_TYPE_CHANGED)) {
					quoteTable.replaceWith(HelperUtil.array2Hashmap(quote.getCSVResponse().toArray(), 0, 1));
					listener.quoteChanged(quoteTable);
				}
			}
		}
	}

	// NetListener implementation
	@Override
	public void updateReceived(CSVFile csv) {
		notifyQuoteListeners(QuoteListener.EVENT_TYPE_CHANGED, quoteTask);
	}
}
