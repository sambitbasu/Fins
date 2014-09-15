/**
 * 
 */
package com.basus.fins.asset;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.basus.fins.account.Account;
import com.basus.fins.data.Data;
import com.basus.fins.data.IndexUpdateTask;
import com.basus.fins.data.QuoteUpdateTask;
import com.basus.fins.data.YahooQuote;
import com.basus.fins.listener.AssetListener;
import com.basus.fins.listener.NetListener;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.listener.AssetListener.EventType;
import com.basus.fins.ui.PortfolioUI;
import com.basus.fins.util.CSVFile;
import com.basus.fins.util.DataUtil;
import com.basus.fins.work.WorkQueue;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambit
 *
 */
public class Asset implements AssetData, QuoteListener {
	public static final int ASSET_TYPE_UNDEFINED     = 00;
	public static final int ASSET_TYPE_CASH          = 01;
	public static final int ASSET_TYPE_STOCK         = 02;
	public static final int ASSET_TYPE_BOND          = 03;
	public static final int ASSET_TYPE_MUTUAL_FUND   = 04;
	
	private static final long MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000; 
	private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	private static final GregorianCalendar gCal = new GregorianCalendar();
	
	private static final Logger log = Logger.getLogger(AssetData.class);
	
	private int   type            = ASSET_TYPE_UNDEFINED;
	private double acquirePrice   = 0.00F;	// unit price
	private double currentPrice   = 0.00F;	// unit price
	private double disposePrice = 0.00F;
	private String  acquireDate     = null;
	private String  disposeDate   = null;
	private Date dtAcquireDate = null;
	private Date dtDisposeDate = null;
	private String name           = null;
	private double quantity       = 0.00;
	private String symbol         = null;
	private double acquireCommission = 0.00F;
	private double disposeCommission = 0.00F;
	private double tax = 0.00F;
	private double fee = 0.00F;
	private String lastSplitDate;
	private Date dtLastSplitDate = null;
	private double lastSplitRatio;
	private double indexAtAcquire = 0.00F;
	private double indexAtDispose = 0.00F;
	private double indexAtNow = 0.00F;
	private String memo = null;
	private int assetId = 0;
	private int accountId = 0;
	private Account account = null;
	
	private HashSet<AssetListener> assetListeners = new HashSet<AssetListener>();
	
	public Asset() {
		;
	}
	
	public void setAssetType(int assetType) throws AssetException {
		switch (assetType) {
		case ASSET_TYPE_UNDEFINED:
			this.type = assetType;
			break;
		case ASSET_TYPE_CASH:
			this.type = assetType;
			break;
		case ASSET_TYPE_STOCK:
			this.type = assetType;
			break;
		case ASSET_TYPE_BOND:
			this.type = assetType;
			break;
		case ASSET_TYPE_MUTUAL_FUND:
			this.type = assetType;
			break;
		default:
			throw new AssetException("Unknown asset type " + assetType);
		}
	}
	
	public int getAssetType() {
		return type;
	}
	
	public void setCurrentPrice(double price) {
		this.currentPrice = price;
	}
	
	public double getCurrentPrice() {
		return this.currentPrice;
	}
	
	public double getCurrentValue() {
		double currentValue = this.quantity * this.disposePrice - this.disposeCommission - this.fee;
		if (!isSold()) {
			currentValue = this.quantity * this.currentPrice - this.disposeCommission - this.fee;
		}
		
		return currentValue;
	}
	
	public double getProfit() {
		return (this.getCurrentValue() - this.getCostBasis());
	}
	
	public double getGain() {
		return this.getProfit() / this.getCostBasis();
		/*
		double gain = (this.currentPrice - this.acquirePrice) / this.acquirePrice;
		if (this.isSold()) {
			gain = (this.disposePrice - this.acquirePrice) / this.acquirePrice;
		}
		
		return gain;
		*/
	}
	
	public double getAcquireValue() {
		return quantity * acquirePrice;
	}
	
	public int getHeldDays() throws ParseException {
		Date endDate = sdf.parse(this.disposeDate);
		int diff = 0;
		if (null == this.disposeDate) {
			endDate = new Date(System.currentTimeMillis());
		}
		
		diff = (int) ((endDate.getTime() - (sdf.parse(this.acquireDate)).getTime()) % MILLIS_IN_A_DAY);
		return diff;
	}
	
	public double getCostBasis() {
		return this.getAcquirePrice() * this.getQuantity() + this.getAcquireCommission();
	}
	
	public double getIndexBasis() {
		//return this.getAcquirePrice() * this.getQuantity();
		return this.getCostBasis();
	}
	
	public double getIndexProceed() {
		double indexQuantity = this.getIndexBasis() / this.indexAtAcquire;
		double indexValue = indexQuantity * this.indexAtDispose - this.disposeCommission - this.fee;
		if (!isSold()) {
			indexValue = indexQuantity * this.account.getCurrentIndex() - this.disposeCommission - this.fee;
		}
		
		return indexValue;
	}
	
	public double getIndexGain() {
		return (this.getIndexProceed() - this.getIndexBasis()) / this.getIndexBasis();
		/*
		log.debug("Acct: " + this.account.getAccountName() + ", Index: " + 
				this.account.getBaseIndex() + ", Index price: " + this.account.getCurrentIndex());
		double indexGain = ((this.account.getCurrentIndex() - this.indexAtAcquire) / this.indexAtAcquire);
		if (isSold()) {
			indexGain = ((this.indexAtDispose - this.indexAtAcquire) / this.indexAtAcquire);
		}
		return indexGain;
		*/ 
	}
	
	public boolean isSold() {
		return (null != this.disposeDate || 0 != this.disposePrice);
	}
	
	public void setId(int id) {
		this.assetId = id;
	}
	
	public void setAccountId(int id) {
		this.accountId = id;
		this.account = Account.getAccount(this.accountId);
	}
	
	public void setAccount(Account acct) {
		this.account = acct;
		if (null != acct) {
			this.accountId = acct.getAccountId();
		}
		else {
			this.accountId = 0;
		}
	}
	
	public void moveToAccount(Account account) {
		/*
		int oldId = this.accountId;
		this.accountId = account.getAccountId();
		HashSet<Account> accounts = Account.getAccounts();
		
		account.addAsset(this);
		
		Iterator<Account> it = accounts.iterator();
		Account acct = null;
		while (it.hasNext()) {
			acct = it.next();
			if (acct.getAccountId() == oldId) {
				acct.removeAsset(this);
				this.account = acct;
			}
		}
		
		this.notifyAssetListeners(AssetListener.EVENT_TYPE_CHANGED);
		*/
	}
	
	public void setName(String name) {
		this.name  = name;
	}
	
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public void setAcquireDate(String date) throws ParseException {
		String strDt = null;
		if (null != date && !date.trim().equals("")) {
			// parse date to see if formatting is right
			dtAcquireDate = sdf.parse(date);
			strDt = date;
		}
		else {
			strDt = null;
		}
		this.acquireDate = strDt;
	}
	
	public void setDisposeDate(String date) throws ParseException {
		String strDt = null;
		if (null != date && !date.trim().equals("")) {
			// parse date to see if formatting is right
			dtDisposeDate = sdf.parse(date);
			strDt = date;
		}
		else {
			strDt = null;
		}
		this.disposeDate = strDt;
	}

	@Override
	public void setAcquireDate(Date date) {
		this.dtAcquireDate = date;
	}

	@Override
	public void setDisposeDate(Date date) {
		this.dtDisposeDate = date;
	}

	public void setAcquirePrice(double price) {
		this.acquirePrice = price;
	}
	
	public void setDisposePrice(double price) {
		this.disposePrice = price;
	}
	
	public void setAcquireCommission(double comm) {
		this.acquireCommission = comm;
	}
	
	public void setDisposeCommission(double comm) {
		this.disposeCommission = comm;
	}
	
	public void setTax(double tax) {
		this.tax = tax;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public void setSplitDate(String date) throws ParseException {
		String strDt = null;
		if (null != date && !date.trim().equals("")) {
			// parse date to see if formatting is right
			dtLastSplitDate = sdf.parse(date);
			strDt = date;
		}
		else {
			strDt = null;
		}
		this.lastSplitDate = strDt;
	}
	
	@Override
	public void setSplitDate(Date date) {
		this.dtLastSplitDate = date;
	}
	
	public void setSplitRatio(double ratio) {
		this.lastSplitRatio = ratio;
	}
	public void setIndexAtAcquire(double value) {
		this.indexAtAcquire = value;
	}
	public void setIndexAtDispose(double value) {
		this.indexAtDispose = value;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public int getId() {
		return this.assetId;
	}
	public int getAccountId() {
		return this.accountId;
	}
	public String getName() {
		return (null == this.name ? "" : this.name);
	}
	public double getQuantity() {
		return this.quantity;
	}
	public String getSymbol() {
		return (null == this.symbol ? "" : this.symbol);
	}
	public String getAcquireDateAsString() {
		return this.acquireDate;
	}
	public String getDisposeDateAsString() {
		return this.disposeDate;
	}
	@Override
	public Date getAcquireDate() {
		return this.dtAcquireDate;
	}
	@Override
	public Date getDisposeDate() {
		return this.dtDisposeDate;
	}
	public double getAcquirePrice() {
		return this.acquirePrice;
	}
	public double getDisposePrice() {
		return this.disposePrice;
	}
	public double getAcquireCommission() {
		return this.acquireCommission;
	}
	public double getDisposeCommission() {
		return this.disposeCommission;
	}
	public double getTax() {
		return this.tax;
	}
	public double getFee() {
		return this.fee;
	}
	public String getSplitDateAsString() {
		return this.lastSplitDate;
	}
	@Override
	public Date getSplitDate() {
		return this.dtLastSplitDate;
	}
	public double getSplitRatio() {
		return this.lastSplitRatio;
	}
	public double getIndexAtAcquire() {
		return this.indexAtAcquire;
	}
	public double getIndexAtDispose() {
		return this.indexAtDispose;
	}
	public String getMemo() {
		return (null == this.memo ? "" : this.memo);
	}
	
	public void save() throws SQLException {
		AssetListener.EventType eventType = AssetListener.EVENT_TYPE_UNKNOWN;
		if (0 == this.assetId) {
			// new asset
			eventType = AssetListener.EVENT_TYPE_SAVED;
		}
		else {
			// existing asset
			eventType = AssetListener.EVENT_TYPE_CHANGED;
		}
		
		int id = Data.saveAsset(this);
		this.setId(id);
		
		// update index price
		this.updateIndexPrice();
		
		// update price
		this.updatePrice();
		
		notifyAssetListeners(eventType);
	}
	
	public void remove() throws SQLException {
		Data.removeAsset(this);
		notifyAssetListeners(AssetListener.EVENT_TYPE_DELETED);
	}
	
	public AssetData copy() throws ParseException {
		AssetData nuAsset = new Asset();
		nuAsset.setAccountId(this.getAccountId());
		nuAsset.setAcquireCommission(this.getAcquireCommission());
		nuAsset.setAcquireDate(this.getAcquireDateAsString());
		nuAsset.setAcquirePrice(this.getAcquirePrice());
		nuAsset.setCurrentPrice(this.getCurrentPrice());
		nuAsset.setDisposeCommission(this.getDisposeCommission());
		nuAsset.setDisposeDate(this.getDisposeDateAsString());
		nuAsset.setDisposePrice(this.getDisposePrice());
		nuAsset.setFee(this.getFee());
		nuAsset.setId(0);		// set assetId as 0
		nuAsset.setIndexAtAcquire(this.getIndexAtAcquire());
		nuAsset.setIndexAtDispose(this.getIndexAtDispose());
		nuAsset.setMemo(this.getMemo());
		nuAsset.setName(this.getName());
		nuAsset.setQuantity(this.getQuantity());
		nuAsset.setSplitDate(this.getSplitDateAsString());
		nuAsset.setSplitRatio(this.getSplitRatio());
		nuAsset.setSymbol(this.getSymbol());
		nuAsset.setTax(this.getTax());
		
		return nuAsset;
	}
	
	public boolean addAssetListener(AssetListener listener) {
		return assetListeners.add(listener);
	}
	
	public boolean removeAssetListener(AssetListener listener) {
		return assetListeners.remove(listener);
	}
	
	public void updateIndexPrice() {
		IndexUpdateTask task = null;
		String acquireDate = this.getAcquireDateAsString();
		Account acct = Account.getAccount(this.accountId);
		String acctIndex = null; 
		
		acctIndex = null == acct ? null : acct.getBaseIndex();
		acctIndex = null == acctIndex ? DEFAULT_INDEX : acctIndex;
		if (null != acquireDate && !acquireDate.trim().equals("")) {
			try {
				task = new IndexUpdateTask(acctIndex, sdf.parse(acquireDate));
				task.addQuoteListener(this);
				task.submitTask();
			}
			catch(ParseException pEx) {
				// The date value has already been validated. We should not have this here
				log.error("ParseExcepion on a already validated date-string\n" + pEx);
			}
		}
		
		String dischargeDate = this.getDisposeDateAsString();
		if (null != dischargeDate && !dischargeDate.trim().equals("")) {
			try {
				task = new IndexUpdateTask(acctIndex, sdf.parse(dischargeDate));
				task.addQuoteListener(this);
				task.submitTask();
			}
			catch(ParseException pEx) {
				// The date value has already been validated. We should not have this here
				log.error("ParseExcepion on a already validated date-string\n" + pEx);
			}
		}
	}
	
	public void updatePrice() {
		QuoteUpdateTask task = new QuoteUpdateTask(true);
		QuoteUpdateTask.addQuoteListener(this);
		task.submitTask();
	}
	
	private void notifyAssetListeners(AssetListener.EventType eventType) {
		Iterator<AssetListener> it = assetListeners.iterator();
		AssetListener listener = null;
		
		while(it.hasNext()) {
			listener = it.next();
			if (null != listener) {
				if (eventType.equals(AssetListener.EVENT_TYPE_CHANGED)) {
					listener.assetChanged(this);
				}
				
				if (eventType.equals(AssetListener.EVENT_TYPE_SAVED)) {
					listener.assetSaved(this);
				}
				
				if (eventType.equals(AssetListener.EVENT_TYPE_DELETED)) {
					listener.assetDeleted(this);
				}
			}
		}
	}

	@Override
	public void quoteChanged(LinkedHashMap<?, ?> table) {
		String indexAtAcquire = null;
		String indexAtDischarge = null;
		boolean changed = false;
		
		if (null == this.acquireDate || this.acquireDate.trim().equals("")) {
			indexAtAcquire = null;
			this.indexAtAcquire = 0.00;
		}
		else {
			indexAtAcquire = (String)(table.get(this.acquireDate));
		}
		
		if (null == this.disposeDate || this.disposeDate.trim().equals("")) {
			indexAtDischarge = null;
			this.indexAtDispose = 0.00;
		}
		else {
			indexAtDischarge = (String)(table.get(this.disposeDate));
		}
		
		if (null != indexAtAcquire) {
			this.setIndexAtAcquire(Double.parseDouble(indexAtAcquire));
			changed = true;
		}
		
		if (null != indexAtDischarge) {
			this.setIndexAtDispose(Double.parseDouble(indexAtDischarge));
			changed = true;
		}
	
		if (changed) {
			this.notifyAssetListeners(AssetListener.EVENT_TYPE_CHANGED);
		}
		
		try {
			Data.updateAssetIndex(this);
		}
		catch(SQLException sqlEx) {
			log.error("Failed to update database: " + sqlEx);
		}
	}
}
