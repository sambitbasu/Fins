/**
 * 
 */
package com.basus.fins.listener;

import com.basus.fins.asset.AssetData;
import com.basus.fins.util.CSVFile;
/**
 * @author sambitb
 *
 */
public interface NetListener {
	public void updateReceived(CSVFile csv);
}
