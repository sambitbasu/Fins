/**
 * 
 */
package com.basus.fins.listener;

import com.basus.fins.account.AccountData;
import com.basus.fins.asset.AssetData;
/**
 * @author sambitb
 *
 */
public interface AccountListener {
	public static final EventType EVENT_TYPE_UNKNOWN = new EventType(0);
	public static final EventType EVENT_TYPE_CHANGED = new EventType(1);
	public static final EventType EVENT_TYPE_SAVED = new EventType(2);
	public static final EventType EVENT_TYPE_DELETED = new EventType(3);
	
	public void accountChanged(AccountData account);
	public void accountSaved(AccountData account);
	public void accountDeleted(AccountData account);
	
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
