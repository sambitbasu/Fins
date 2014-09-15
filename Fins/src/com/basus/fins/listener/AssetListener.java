/**
 * 
 */
package com.basus.fins.listener;

import com.basus.fins.asset.AssetData;
/**
 * @author sambitb
 *
 */
public interface AssetListener {
	public static final EventType EVENT_TYPE_UNKNOWN = new EventType(0);
	public static final EventType EVENT_TYPE_CHANGED = new EventType(1);
	public static final EventType EVENT_TYPE_SAVED = new EventType(2);
	public static final EventType EVENT_TYPE_DELETED = new EventType(3);
	
	public void assetChanged(AssetData asset);
	public void assetSaved(AssetData asset);
	public void assetDeleted(AssetData asset);
	
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
