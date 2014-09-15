/**
 * 
 */
package com.basus.fins.performance;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.basus.fins.account.Account;
import com.basus.fins.asset.Asset;
import com.basus.fins.asset.AssetData;
import com.basus.fins.data.HistoricQuoteTask;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.ui.UtilUI;

/**
 * @author sambitb
 *
 */
public class PerformanceAccount extends Account implements QuoteListener {
	private static Logger log = Logger.getLogger(PerformanceAccount.class);
	TypedPerformanceInput input = null;
	
	public PerformanceAccount(Account baseAcct, TypedPerformanceInput input) {
		super();
		this.input = input;
		this.copyAccountParams(baseAcct);
		if (input.getType() == input.TYPE_DATED_PERFORMANCE) {
			String title = baseAcct.getAccountName() + "(" + input.getStartDateAsString() + " - " + input.getEndDateAsString() + ")";
			super.setAccountName(title);
		}
	}
	
	private void copyAccountParams(Account acct) {
		this.setCurrency(this.getCurrency());
		
		Vector<AssetData> toRemove = new Vector<AssetData>();
		try {
			HashSet<AssetData> baseAssets = acct.getAssets(); 
			
			if (null != baseAssets) {
				Iterator<AssetData> it = baseAssets.iterator();
				while (it.hasNext()) {
					Asset baseAsset = (Asset)it.next();
					AssetData asset = baseAsset.copy();
					asset.setAccountId(0);
					asset.setAccount(this);
					
					if (null != asset.getAcquireDate() && asset.getAcquireDate().compareTo(input.getStartDate()) <= 0) {
						// asset bought before or on start date
						asset.setAcquireDate(input.getStartDateAsString());
					}
					else {
						if (null == asset.getAcquireDate() || asset.getAcquireDate().compareTo(input.getEndDate()) > 0) {
							// asset bought after end date. remove from list
							toRemove.add(asset);
							continue;
						}
					}
				
					if (!asset.isSold() || asset.getDisposeDate().compareTo(input.getEndDate()) >= 0) {
						// asset sold on or after end date
						asset.setDisposeDate(input.getEndDateAsString());
					}
					else {
						if (asset.isSold() && asset.getDisposeDate().compareTo(input.getStartDate()) < 0) {
							// asset sold before start date. remove from list
							toRemove.add(asset);
							continue;
						}
					}
					
					
					this.addAsset(asset);
				}
			}
		}
		catch(Exception ex) {
			log.error(ex);
			UtilUI.showError("An error occurred in reading assets", null);
		}
		
		Iterator<AssetData> it = toRemove.iterator();
		while (it.hasNext()) {
			assets.remove(it.next());
		}
	}
	
	private void updatePrice() {
		Iterator<AssetData> it = assets.iterator();
		while (it.hasNext()) {
			AssetData asset = it.next();
			
			HistoricQuoteTask task = new HistoricQuoteTask(asset.getSymbol(), 
									asset.getAcquireDate(), asset.getDisposeDate());
			task.addQuoteListener(this);
			task.submitTask();
		}
	}
}
