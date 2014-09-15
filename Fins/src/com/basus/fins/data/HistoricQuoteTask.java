/**
 * 
 */
package com.basus.fins.data;

import java.util.Date;

import com.basus.fins.work.WorkQueue;

/**
 * @author sambitb
 *
 */
public class HistoricQuoteTask extends QuoteUpdateTask {
	private String symbol = null;
	private Date startDate = null;
	private Date endDate = null;
	
	public HistoricQuoteTask(String symbol, Date startDate, Date endDate) {
		this.symbol = symbol;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	private void makeTask() {
		YahooQuote quoteTask = new YahooQuote(this.symbol, this.startDate, this.endDate, YahooQuote.QUOTE_TYPE_HISTORIC);
		quoteTask.addNetListener(this);
		WorkQueue<Runnable> queue = (WorkQueue<Runnable>)WorkQueue.getHistoricQuoteQueue();
		queue.add(quoteTask);
	}
}
