/**
 * 
 */
package com.basus.fins.work;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author sambitb
 *
 */
public class WorkQueue<E> extends LinkedBlockingQueue<Runnable> {
	private static final WorkQueue<?> currentQuoteQueue = new WorkQueue<Runnable>();
	private static final WorkQueue<?> historicQuoteQueue = new WorkQueue<Runnable>();
	private static final WorkQueue<?> status1Queue = new WorkQueue<Runnable>();
	private static final WorkQueue<?> status2Queue = new WorkQueue<Runnable>();
	
	public static WorkQueue<?> getCurrentQuoteQueue() {
		return currentQuoteQueue;
	}
	
	public static WorkQueue<?> getHistoricQuoteQueue() {
		return historicQuoteQueue;
	}
	
	public static WorkQueue<?> getStatus1Queue() {
		return status1Queue;
	}
	
	public static WorkQueue<?> getStatus2Queue() {
		return status2Queue;
	}
	
	public WorkQueue() {
		super();
	}
}
