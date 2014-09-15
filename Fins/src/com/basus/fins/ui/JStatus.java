/**
 * 
 */
package com.basus.fins.ui;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.basus.fins.work.WorkQueue;

/**
 * @author sambit
 *
 */
public class JStatus extends JLabel {
	private WorkQueue<Runnable> queue = null;
	private Object lock = new Object();
	
	/**
	 * @param image
	 */
	public JStatus(WorkQueue<Runnable> queue) {
		super();
		this.queue = queue;
	}
	
	/**
	 * @param image
	 */
	public JStatus(Icon image, WorkQueue<Runnable> queue) {
		super(image);
		this.queue = queue;
	}

	/**
	 * @param text
	 * @param horizontalAlignment
	 */
	public JStatus(int horizontalAlignment, WorkQueue<Runnable> queue) {
		super("", horizontalAlignment);
		this.queue = queue;
	}

	/**
	 * @param image
	 * @param horizontalAlignment
	 */
	public JStatus(Icon image, int horizontalAlignment, WorkQueue<Runnable> queue) {
		super(image, horizontalAlignment);
		this.queue = queue;
	}

	public void setStatus(String text, long visibleInMillis) {
		StatusTask task = new StatusTask(text, visibleInMillis);
		queue.add(task);
	}
	
	class StatusTask implements Runnable {
		String text = null;
		long visible = 0;
		
		StatusTask(String text, long visibleInMillis) {
			this.text = text;
			this.visible = visibleInMillis;
		}
		
		@Override
		public void run() {
			setText(text);
			if (0 != visible || visible > 0) {
				try {
					Thread.sleep(visible);
					setText("");
				}
				catch(InterruptedException iEx) {
					setText("");
				}
			}
		}
	}

}
