/**
 * 
 */
package com.basus.fins.account;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Vector;

import com.basus.fins.asset.Asset;
import com.basus.fins.asset.AssetData;

/**
 * @author sambit
 *
 */
public interface AccountData {
	public void setAccountId(int id);
	public void setAccount(String name);
	public void setAccountName(String name);
	public void setAccountInstituteName(String name);
	public void setAccountBuyCommission(double value);
	public void setAccountSaleCommission(double value);
	public void setBaseIndex(String idx);
	public void setCurrency(String currency);
	public void setIsWatchlist(boolean b);
	public void setAccountMemo(String memo);
	public void addAsset(AssetData asset);
	public void removeAsset(Asset asset);
	public void addAssets(Vector<Asset> assets);
	
	public int getAccountId();
	public String getAccountName();
	public String getAccountInstituteName();
	public double getAccountBuyCommission();
	public double getAccountSaleCommission();
	public String getBaseIndex();
	public double getCurrentIndex();
	public String getCurrency();
	public boolean getIsWatchlist();
	public String getAccountMemo();
	public LinkedHashSet<AssetData> getAssets() throws ParseException, SQLException;
	
	public Account copy() throws SQLException, ParseException;
	public int save() throws SQLException;
	public void remove() throws SQLException;
}
