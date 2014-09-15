/**
 * 
 */
package com.basus.fins.ui;

import static com.basus.fins.PortfolioConstants.DEFAULT_INDEX;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.basus.fins.PortfolioConstants;
import com.basus.fins.account.Account;
import com.basus.fins.account.AccountData;
import com.basus.fins.asset.AssetData;
import com.basus.fins.data.Quote;
import com.basus.fins.listener.AccountListener;
import com.basus.fins.listener.AssetListener;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.util.HelperUtil;

/**
 * @author sambitb
 *
 */
public class AccountSummaryTableModel extends DefaultTableModel implements QuoteListener, AccountListener, AssetListener {
	private static final Logger log = Logger.getLogger(AccountSummaryTableModel.class);
	private static final String[] COLUMNS = {"Currency",
											"Active",
											"Current Value",
											"Current Cost",
											"Current Profit",
											"Cumulative Value",
											"Cumulative Cost",
											"Cumulative Profit",
											"Cuurent Index",
											"Cumulative Gain",
											"Index Gain",
											"Gain vs Index"
											};
	private AccountData acct = null;
	LinkedHashSet<AssetData> assets = null;
	private LinkedHashMap<String, String> lastTable;
	private boolean showHolding = PortfolioConstants.DEFAULT_SHOW_HOLDING;
	private boolean showDisposed = PortfolioConstants.DEFAULT_SHOW_DISPOSED;
	TreeMap<String, AccountSummary> sums = new TreeMap<String, AccountSummary>();
	 
	LinkedHashMap<String, String> quote = null;
	
	public AccountSummaryTableModel(AccountData acct) {
		super((Object[])COLUMNS, 0);
		this.acct = acct;
		try {
			this.assets = this.acct.getAssets();
			this.registerAssetListener();
		} catch (ParseException e) {
			PortfolioDialog.showError("Unable to get price information", null);
			log.error(e);
		} catch (SQLException e) {
			PortfolioDialog.showError("Unable to get Asset information", null);
			log.error(e);
		}
	}
	
	public AccountSummaryTableModel(LinkedHashSet<AssetData> assets) {
		super((Object[])COLUMNS, 0);
		this.assets = assets;
	}
	
	public void setAssets(LinkedHashSet<AssetData> assets) {
		this.assets = assets;
		this.registerAssetListener();
	}
	
	void setShowHolding(boolean show) {
		this.showHolding = show;
	}
	
	void setShowDisposed(boolean show) {
		this.showDisposed = show;
	}
	
	boolean getShowHolding() {
		return this.showHolding;
	}
	
	boolean getShowDisposed() {
		return this.showDisposed;
	}
	
	private void registerAssetListener() {
		Iterator<AssetData> it = assets.iterator();
		while (it.hasNext()) {
			AssetData asset = it.next();
			asset.addAssetListener(this);
		}
	}

	protected AssetData getAssetById(int id) {
		if (null == this.assets) {
			return null;
		}
		
		Iterator<AssetData> it = assets.iterator();
		while (it.hasNext()) {
			AssetData asset = it.next();
			if (asset.getId() == id) {
				return asset;
			}
		}
		
		return null;
	}
	
	private void calculateSummary() throws ParseException, SQLException {
		if (null == quote) {
			return;
		}
		
		double currentIndex = 0.00F;
		Iterator<AssetData> it = assets.iterator();
		
		double virtualIndexQuantity = 0.0F;
		AccountSummary summary = null;
		String strCurrentIndex = null;
		while (it.hasNext()) {
			AssetData asset = it.next();
			
			if (this.showHolding && !this.showDisposed && asset.isSold()) {
				continue;
			}
			
			if (this.showDisposed && !this.showHolding && !asset.isSold()) {
				continue;
			}
			
			
			AccountData assAcct = Account.getAccountById(asset.getAccountId());
			if (null == assAcct) {
				continue;
			}
			String currency = assAcct.getCurrency();
			
			strCurrentIndex = quote.get(assAcct.getBaseIndex());
			strCurrentIndex = null == strCurrentIndex ? quote.get(DEFAULT_INDEX) : strCurrentIndex; 
			if (null != strCurrentIndex) {
				currentIndex = Double.parseDouble(strCurrentIndex);
			}
			
			summary = sums.get(currency);
			if (null == summary) {
				summary = new AccountSummary();
				sums.put(currency, summary);
			}
			
			summary.setCurrency(currency);
			summary.setCurrentIndex(currentIndex);
			virtualIndexQuantity = (asset.getQuantity() * asset.getAcquirePrice()) / asset.getIndexAtAcquire();
			summary.setCumulativeValue(summary.getCumulativeValue() + asset.getCurrentValue());
			summary.setCumulativeCost(summary.getCumulativeCost() + asset.getAcquirePrice() * asset.getQuantity() + asset.getAcquireCommission());
			summary.setCumulativeProfit(summary.getCumulativeProfit() + asset.getProfit());
			summary.setIdxCost(summary.getIdxCost() + asset.getIndexAtAcquire() * virtualIndexQuantity);
			
			if (!asset.isSold()) {
				summary.setActiveSymbol(asset.getSymbol());
				summary.setCurrentValue(summary.getCurrentValue() + asset.getCurrentValue());
				summary.setCurrentCost(summary.getCurrentCost() + asset.getAcquirePrice() * asset.getQuantity() + asset.getAcquireCommission());
				summary.setCurrentProfit(summary.getCurrentProfit() + asset.getProfit());
				summary.setIdxValue(summary.getIdxValue() + virtualIndexQuantity * summary.getCurrentIndex());
			}
			else {
				summary.setIdxValue(summary.getIdxValue() + virtualIndexQuantity * asset.getIndexAtDispose());
				summary.setCumulativeCost(summary.getCumulativeCost() - asset.getCostBasis() - asset.getProfit());
			}
		}
	}
	
	protected void populateSummary() throws ParseException, SQLException {
		calculateSummary();
		this.setNumRows(0);
		NavigableSet<String> nav = sums.descendingKeySet();
		Iterator<String> it = nav.descendingIterator();
		
		while (it.hasNext()) {
			AccountSummary summary = sums.get(it.next());
			Object[] row = new Object[COLUMNS.length];
			
			summary.setCurrentGain(summary.getCurrentProfit() / summary.getCurrentCost());
			summary.setCumulativeGain(summary.getCumulativeProfit() / summary.getCumulativeCost());
			summary.setIdxGain((summary.getIdxValue() - summary.getIdxCost()) / summary.getIdxCost());
			summary.setGainVsIdx(summary.getCumulativeGain() - summary.getIdxGain());
			
			row[0] = summary.getCurrency();
			row[1] = summary.getActiveCount();
			row[2] = HelperUtil.roundTwoDecimals(summary.getCurrentValue());
			row[3] = HelperUtil.roundTwoDecimals(summary.getCurrentCost());
			row[4] = HelperUtil.roundTwoDecimals(summary.getCurrentProfit());
			row[5] = HelperUtil.roundTwoDecimals(summary.getCumulativeValue());
			row[6] = HelperUtil.roundTwoDecimals(summary.getCumulativeCost());
			row[7] = HelperUtil.roundTwoDecimals(summary.getCumulativeProfit());
			row[8] = HelperUtil.roundTwoDecimals(summary.getCurrentIndex());
			row[9] = HelperUtil.getPercent(summary.getCumulativeGain());
			row[10] = HelperUtil.getPercent(summary.getIdxGain());
			row[11] = HelperUtil.getPercent(summary.getGainVsIdx());
			
			this.addRow(row);
		}
		
		sums.clear();
	}
	
	// QuoteListener implementation
	@Override
	public void quoteChanged(LinkedHashMap<?, ?> table) {
		quote = (LinkedHashMap<String, String>)table;
		Iterator<AssetData> it = assets.iterator();
		
		while (it.hasNext()) {
			AssetData asset = it.next();
			asset.setCurrentPrice(Double.parseDouble(quote.get(asset.getSymbol())));
		}
		
		try {
			populateSummary();
		} catch (ParseException e) {
			log.info("ParseException: " + e.getMessage());
		} catch (SQLException e) {
			log.info("SQLException: " + e.getMessage());
		}
	}

	@Override
	public void assetChanged(AssetData asset) {
		try {
			populateSummary();
		} catch (ParseException e) {
			log.info("ParseException: " + e.getMessage());
		} catch (SQLException e) {
			log.info("SQLException: " + e.getMessage());
		}
	}

	@Override
	public void assetDeleted(AssetData asset) {
		try {
			assets.remove(asset);
			populateSummary();
		} catch (ParseException e) {
			log.info("ParseException: " + e.getMessage());
		} catch (SQLException e) {
			log.info("SQLException: " + e.getMessage());
		}
	}

	@Override
	public void assetSaved(AssetData asset) {
		try {
			AssetData a = this.getAssetById(asset.getId());
			if (null == a) {
				//asset.updatePrice();
				assets.add(asset);
			}
			populateSummary();
		} catch (ParseException e) {
			log.info("ParseException: " + e.getMessage());
		} catch (SQLException e) {
			log.info("SQLException: " + e.getMessage());
		}
	}

	@Override
	public void accountChanged(AccountData account) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountDeleted(AccountData account) {
		// TODO Auto-generated method stub
	}

	@Override
	public void accountSaved(AccountData account) {
		// TODO Auto-generated method stub
		
	}
	
	class AccountSummary {
		String currency = null;
		int active = 0;
		double currentValue = 0.00F;
		double currentCost = 0.00F;
		double cumValue = 0.00F;
		double cumCost = 0.00F;
		double currentProfit = 0.00F;
		double cumProfit = 0.00F;
		double idxCost = 0.00F; // if quantity amount of index were bought
		double idxValue = 0.00F; // Current value, if quantity amount of index were bought
		double currentGain = 0.00F;
		double cumGain = 0.00F;
		double idxGain = 0.00F;
		double gainVsIdx = 0.00F;
		double currentIndex = 0.00F;
		LinkedHashSet<String> symbols = new LinkedHashSet<String>();
		
		AccountSummary() {
			
		}
		
		void setCurrency(String currency) {
			this.currency = currency;
		}
		
		String getCurrency() {
			return this.currency;
		}
		
		void setActiveSymbol(String symbol) {
			this.symbols.add(symbol);
		}
		
		/*
		void setActiveCount(int count) {
			this.active = count;
		}
		*/
		
		int getActiveCount() {
			return this.symbols.size();
		}
		
		void setCurrentValue(double value) {
			this.currentValue = value;
		}
		
		double getCurrentValue() {
			return this.currentValue;
		}
		
		void setCumulativeValue(double value) {
			this.cumValue = value;
		}
		
		double getCumulativeValue() {
			return this.cumValue;
		}
		
		void setCurrentCost(double cost) {
			this.currentCost = cost;
		}
		
		double getCurrentCost() {
			return this.currentCost;
		}
		
		void setCumulativeCost(double cost) {
			this.cumCost = cost;
		}
		
		double getCumulativeCost() {
			return this.cumCost;
		}
		
		void setCurrentProfit(double profit) {
			this.currentProfit = profit;
		}
		
		double getCurrentProfit() {
			return this.currentProfit;
		}
		
		void setCumulativeProfit(double profit) {
			this.cumProfit = profit;
		}
		
		double getCumulativeProfit() {
			return this.cumProfit;
		}
		
		void setIdxCost(double idxCost) {
			this.idxCost = idxCost;
		}
		
		double getIdxCost() {
			return this.idxCost;
		}
		
		void setIdxValue(double idxValue) {
			this.idxValue = idxValue;
		}
		
		double getIdxValue() {
			return this.idxValue;
		}
		
		void setCurrentGain(double gain) {
			this.currentGain = gain;
		}
		
		double getCurrentGain() {
			return this.currentGain;
		}
		
		void setCumulativeGain(double gain) {
			this.cumGain = gain;
		}
		
		double getCumulativeGain() {
			return this.cumGain;
		}
		
		void setIdxGain(double idxGain) {
			this.idxGain = idxGain;
		}
		
		double getIdxGain() {
			return this.idxGain;
		}
		
		void setGainVsIdx(double gainVsIdx) {
			this.gainVsIdx = gainVsIdx;
		}
		
		double getGainVsIdx() {
			return this.gainVsIdx;
		}
		
		void setCurrentIndex(double index) {
			this.currentIndex = index;
		}
		
		double getCurrentIndex() {
			return this.currentIndex;
		}
	}
}
