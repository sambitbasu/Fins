/**
 * 
 */
package com.basus.fins.work;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @author sambitb
 *
 */
public class Worker extends ThreadPoolExecutor {
	private static Logger log = Logger.getLogger(Worker.class);

	public Worker(WorkQueue<Runnable> queue) {
		super(5, 10, 1200, TimeUnit.MILLISECONDS, queue);
		this.prestartCoreThread();
	}

	public boolean isTerminating() {
		if (log.isDebugEnabled()) {
			log.debug("Worker is terminating ...");
		}
		
		return super.isTerminating();
	}
}
