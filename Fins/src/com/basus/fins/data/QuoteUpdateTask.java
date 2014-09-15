/**
 * 
 */
package com.basus.fins.data;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.basus.fins.listener.NetListener;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.util.CSVFile;
import com.basus.fins.util.HelperUtil;
import com.basus.fins.work.WorkQueue;

import static com.basus.fins.PortfolioConstants.*;
/**
 * @author sambit
 *
 */
public class QuoteUpdateTask implements Runnable, NetListener {
	private static Logger log = Logger.getLogger(QuoteUpdateTask.class);
	private static LinkedHashSet<QuoteListener> quoteListeners = new LinkedHashSet<QuoteListener>();
	
	private YahooQuote quoteTask = null;
	private boolean isImmediate = false;
	/**
	 * 
	 */
	public QuoteUpdateTask() {

	}
	
	public QuoteUpdateTask(boolean immediate) {
		this.isImmediate = immediate;
	}
	
	public static boolean addQuoteListener(QuoteListener listener) {
		return quoteListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			makeTask();
			/* TODO: sleep time should be configurable */
			try {
				if (!isImmediate) {
					Thread.sleep(DEFAULT_REFRESH_RATE * 60 * 1000);
					isImmediate = false;
				}
			}
			catch(InterruptedException iEx) {
				log.error(iEx);
			}
		}

	}

	public void submitTask() {
		this.makeTask();
	}
	
	private void makeTask() {
		try {
			LinkedHashSet<String> symbols = Data.getActiveAssetSymbols();
			String[] baseIndices = Data.getAllBaseIndices();
			for (String index : baseIndices) {
				symbols.add(index);
			}
			quoteTask = new YahooQuote(symbols);
			quoteTask.addNetListener(this);
			WorkQueue<Runnable> queue = (WorkQueue<Runnable>)WorkQueue.getCurrentQuoteQueue();
			queue.add(quoteTask);
		}
		catch(SQLException sqlEx) {
			log.error("Failed to get Asset symbols\n" + sqlEx);
		}
	}
	
	private void notifyListeners(QuoteListener.EventType eventType, YahooQuote quote) {
		Iterator<QuoteListener> it = quoteListeners.iterator();
		QuoteListener listener = null;
		
		while(it.hasNext()) {
			listener = it.next();
			if (null != listener) {
				if (eventType.equals(QuoteListener.EVENT_TYPE_CHANGED)) {
					listener.quoteChanged(HelperUtil.array2Hashmap(quote.getCSVResponse().toArray(), 0, 1));
				}
			}
		}
	}

	// NetListener implementation
	@Override
	public void updateReceived(CSVFile csv) {
		notifyListeners(QuoteListener.EVENT_TYPE_CHANGED, quoteTask);
	}
}
