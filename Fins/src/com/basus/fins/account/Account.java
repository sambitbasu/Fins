/**
 * 
 */
package com.basus.fins.account;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
//import java.util.HashSet;
//import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.basus.fins.asset.Asset;
import com.basus.fins.asset.AssetData;
import com.basus.fins.data.Data;
import com.basus.fins.data.IndexUpdateTask;
import com.basus.fins.listener.AccountListener;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.ui.UtilUI;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambit
 *
 * This class represents an account in a portfolio. A portfolio can have multiple accounts
 */
public class Account implements AccountData, QuoteListener {
	private static Logger log = Logger.getLogger(Account.class);
	private static LinkedHashSet<Account> accounts = new LinkedHashSet<Account>();
	
	protected String accountName = "";
	protected long lastUpdate = System.currentTimeMillis();
	protected LinkedHashSet<AssetData> assets = new LinkedHashSet<AssetData>();
	protected String instituteName = null;
	protected double buyComm = 0.00;
	protected double saleComm = 0.00;
	protected String baseIndex = DEFAULT_INDEX;
	protected String currency = DEFAULT_CURRENCY;
	protected boolean isWatchlist = false;
	protected String memo = null;
	protected int id = 0;
	protected double currentIndexPrice = 0.00F;
	protected LinkedHashSet<AccountListener> accountListeners = new LinkedHashSet<AccountListener>();
	
	private Object key = new Object();
	
	static {
		try {
			Account.accounts = Data.getAccounts(true);
		}
		catch (SQLException sqlEx) {
			log.error(sqlEx);
			UtilUI.showError("Failed to retrieve Account information", null);
		}
	}
	
	public static LinkedHashSet<Account> getAccounts() {
		return Account.accounts;
	}
	
	public static AccountData getAccountById(int id) {
		Iterator<Account> it = accounts.iterator();
		while (it.hasNext()) {
			Account a = it.next();
			if (a.getAccountId() == id) {
				return a;
			}
		}
		
		return null;
	}
		
	public Account() {
		this("");
	}
	
	public Account(String name) {
		this.accountName = name;
		this.setAccountBuyCommission(DEFAULT_ACCOUNT_BUY_COMMISSION);
		this.setAccountSaleCommission(DEFAULT_ACCOUNT_SALE_COMMISSION);
		this.setAccountInstituteName("");
		Thread t = new Thread(new IndexUpdater(this));
		t.start();
	}
	
	public String toString() {
		return getAccountName();
	}
	
	private void updateLastUpdate() {
		lastUpdate = System.currentTimeMillis(); 
	}
	
	public void setAccount(String name) {
		this.accountName = name;
	}
	
	public void setAccountId(int id) {
		this.id = id;
	}
	
	public void addAccountListener(AccountListener listener) {
		this.accountListeners.add(listener);
	}
	
	public void setAccountName(String name) {
		this.setAccount(name);
	}
	
	public void setAccountInstituteName(String name) {
		this.instituteName = name;
	}
	public void setAccountBuyCommission(double value) {
		this.buyComm = value;
	}
	
	public void setAccountSaleCommission(double value) {
		this.saleComm = value;
	}
	
	public void setBaseIndex(String idx) {
		this.baseIndex = idx;
		if (null == idx || idx.trim().equals("")) {
			this.baseIndex = DEFAULT_INDEX;
		}
	}
	
	public double getCurrentIndex() {
		return this.currentIndexPrice;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public void setIsWatchlist(boolean b) {
		this.isWatchlist = b;
	}
	
	public void setAccountMemo(String memo) {
		this.memo = memo;
	}
	
	public void addAsset(AssetData asset) {
		this.assets.add(asset);
	}
	
	public void addAssets(Vector<Asset> assets) {
		this.assets.addAll(assets);
	}
	
	public int getAccountId() {
		return this.id;
	}
	
	public String getAccountName() {
		return this.accountName;
	}
	
	public String getAccountInstituteName() {
		return this.instituteName;
	}
	
	public double getAccountBuyCommission() {
		return this.buyComm;
	}
	
	public double getAccountSaleCommission() {
		return this.saleComm;
	}
	
	public String getBaseIndex() {
		return this.baseIndex;
	}
	
	public String getCurrency() {
		return this.currency;
	}
	
	public boolean getIsWatchlist() {
		return this.isWatchlist;
	}
	
	public String getAccountMemo() {
		return this.memo;
	}
	
	public LinkedHashSet<AssetData> getAssets() throws ParseException, SQLException {
		//if (null == this.assets) {
			loadAssets();
		//}
		return this.assets;
	}
	
	public void removeAsset(Asset asset) {
		assets.remove(asset);
	}
	
	public static Account getAccount(int accountId) {
		if (accountId <= 0) {
			return null;
		}
		
		Iterator<Account> it = accounts.iterator();
		Account acct = null;
		
		while (it.hasNext()) {
			acct = it.next();
			if (acct.id == accountId) {
				return acct;
			}
		}
		
		return acct;
	}
	
	public Account copy() throws SQLException, ParseException {
		log.debug("Copying account: " + this.getAccountName());
		Account nuAccount = new Account("Copy of " + this.getAccountName());

		nuAccount.setAccountBuyCommission(this.getAccountBuyCommission());
		nuAccount.setAccountInstituteName(this.getAccountInstituteName());
		log.debug("'" + this.getAccountMemo() + "'");
		nuAccount.setAccountMemo("This is a dummy account. After setting up your accounts, please delete this account. Else, your performance and other calculations will not reflect actual values.");
		nuAccount.setAccountSaleCommission(this.getAccountSaleCommission());
		nuAccount.setBaseIndex(this.getBaseIndex());
		nuAccount.setCurrency(this.getCurrency());
		nuAccount.setIsWatchlist(this.getIsWatchlist());
		
		if (null != this.getAssets()) {
			Iterator<AssetData> it = this.getAssets().iterator();
			while (it.hasNext()) {
				Asset asset = (Asset)it.next();
				asset.setAccountId(0);
				asset.setAccount(nuAccount);
				nuAccount.addAsset(asset);
			}
		}
		
		return nuAccount;
	}
	
	public int save() throws SQLException {
		int id = Data.saveAccount(this);
		accounts.add(this);
		
		Iterator<AssetData> it = assets.iterator();
		while (it.hasNext()) {
			AssetData asset = it.next();
			asset.updateIndexPrice();
		}
		
		this.notifyListeners(AccountListener.EVENT_TYPE_SAVED);
		this.notifyListeners(AccountListener.EVENT_TYPE_CHANGED);
		return id;
	}
	
	public void remove() throws SQLException {
		Data.removeAccount(this);
		accounts.remove(this);
		
		this.notifyListeners(AccountListener.EVENT_TYPE_DELETED);
	}
	
	private void loadAssets() throws ParseException, SQLException {
		assets = Data.getAssets(this.id, true, assets);
	}
	
	private void notifyListeners(AccountListener.EventType type) {
		Iterator<AccountListener> it = this.accountListeners.iterator();
		
		while (it.hasNext()) {
			AccountListener listener = it.next();
			if (AccountListener.EVENT_TYPE_CHANGED == type) {
				listener.accountChanged(this);
			}
			
			if (AccountListener.EVENT_TYPE_SAVED == type) {
				listener.accountChanged(this);
			}
			
			if (AccountListener.EVENT_TYPE_DELETED == type) {
				listener.accountChanged(this);
			}
		}
	}

	
	@Override
	public void quoteChanged(LinkedHashMap<?, ?> table) {
		synchronized(key) {
			String indexPrice = (String)table.get(this.baseIndex);
			if (null != indexPrice) {
				this.currentIndexPrice = Double.parseDouble((String)table.get(this.baseIndex));
			}
		}
	}
	
	class IndexUpdater implements Runnable {
		Account account = null;
		
		IndexUpdater(Account account) {
			this.account = account;
		}
		
		@Override
		public void run() {
			IndexUpdateTask task;
			while (true) {
				task = new IndexUpdateTask(account.getBaseIndex());
				task.addQuoteListener(account);
				task.submitTask();
				try {
					Thread.sleep(DEFAULT_REFRESH_RATE * 60 * 1000);
				}
				catch (InterruptedException ex) {
					log.error(ex);
				}
			}
		}
	}
}
