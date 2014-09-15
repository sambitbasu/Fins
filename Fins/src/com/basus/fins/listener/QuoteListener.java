/**
 * 
 */
package com.basus.fins.listener;

import java.util.LinkedHashMap;
/**
 * @author sambitb
 *
 */
public interface QuoteListener {
	public static final EventType EVENT_TYPE_UNKNOWN = new EventType(0);
	public static final EventType EVENT_TYPE_CHANGED = new EventType(1);
	
	public void quoteChanged(LinkedHashMap<?, ?> table);
	
	class EventType {
		private int type = 0;
		
		EventType(int type) {
			this.type = type;
		}
		
		boolean equals(EventType et) {
			return et.type == this.type;
		}
	}
}
