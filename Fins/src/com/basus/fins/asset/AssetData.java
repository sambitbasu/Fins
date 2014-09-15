/**
 * 
 */
package com.basus.fins.asset;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import com.basus.fins.account.Account;
import com.basus.fins.listener.AssetListener;

/**
 * @author sambit
 *
 */
public interface AssetData {
	public void setId(int id);
	public void setAccountId(int id);
	public void setAccount(Account acct);
	public void setName(String name);
	public void setQuantity(double quantity);
	public void setSymbol(String symbol);
	public void setAcquireDate(String date) throws ParseException;
	public void setDisposeDate(String date) throws ParseException;
	public void setAcquireDate(Date date);
	public void setDisposeDate(Date date);
	public void setCurrentPrice(double price);
	public void setAcquirePrice(double price);
	public void setDisposePrice(double price);
	public void setAcquireCommission(double comm);
	public void setDisposeCommission(double comm);
	public void setTax(double tax);
	public void setFee(double fee);
	public void setSplitDate(String date) throws ParseException;
	public void setSplitDate(Date date);
	public void setSplitRatio(double ratio);
	public void setIndexAtAcquire(double value);
	public void setIndexAtDispose(double value);
	public void setMemo(String memo);
	
	public int getId();
	public int getAccountId();
	public String getName();
	public double getQuantity();
	public String getSymbol();
	public String getAcquireDateAsString();
	public String getDisposeDateAsString();
	public Date getAcquireDate();
	public Date getDisposeDate();
	public double getCurrentPrice();
	public double getAcquirePrice();
	public double getDisposePrice();
	public double getAcquireCommission();
	public double getDisposeCommission();
	public double getTax();
	public double getFee();
	public String getSplitDateAsString();
	public Date getSplitDate();
	public double getSplitRatio();
	public double getIndexAtAcquire();
	public double getIndexAtDispose();
	public String getMemo();
	
	public void updateIndexPrice();
	public void updatePrice();
	
	public void moveToAccount(Account toAccount);
	
	public int getHeldDays() throws ParseException;
	public double getCurrentValue();
	public double getCostBasis();
	public double getProfit();
	public double getGain();
	public double getIndexBasis();
	public double getIndexProceed();
	public double getIndexGain();
	public boolean isSold();
	
	public void save() throws SQLException;
	public void remove() throws SQLException;
	public AssetData copy() throws ParseException;
	
	public boolean addAssetListener(AssetListener listener);
	public boolean removeAssetListener(AssetListener listener);
}
