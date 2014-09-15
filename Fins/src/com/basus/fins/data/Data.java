/**
 * 
 */
package com.basus.fins.data;

import static com.basus.fins.PortfolioConstants.*;

import com.basus.fins.PortfolioException;
import com.basus.fins.account.Account;
import com.basus.fins.account.AccountData;
import com.basus.fins.asset.Asset;
import com.basus.fins.asset.AssetData;
import com.basus.fins.ui.JStatus;
import com.basus.fins.ui.PortfolioDialog;
import com.basus.fins.ui.PortfolioUI;
import com.basus.fins.ui.UtilUI;
import com.basus.fins.util.DbUtil;
import com.basus.fins.work.WorkQueue;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
//import java.util.HashMap;
//import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * This class does database operations 
 * 
 * @author sambit
 *
 */
public class Data {
	private static Logger log = Logger.getLogger(Data.class);
	private static Connection conn = null;
	private static String jdbcUrl;
	private static File f; 
	static {
		try {
			f = new File(DB_DIR + File.separator + DB_NAME);
			if (!f.isDirectory()) {
				f = new File(DEFAULT_DB_DIR + File.separator + DB_NAME);
				if (!f.isDirectory()) {
					String msg = "db dir " + DB_DIR + " or " + DEFAULT_DB_DIR + " cannot be found";
				}
				else {
					jdbcUrl = DEFAULT_JDBC_URL;
				}
			}
			else {
				jdbcUrl = JDBC_URL;
			}
			
			log.debug("Connecting to db: " + jdbcUrl);
			conn = DbUtil.connect(jdbcUrl);
		}
		catch(Exception ex) {
			// try deleting residual lock files
			File[] lockFiles = f.listFiles(new LockFilenameFilter());
			for (File lf : lockFiles) {
				lf.delete();
			}
			
			try {
				// now try conn again
				conn = DbUtil.connect(JDBC_URL);
			}
			catch(Exception e) {
				UtilUI.showError("Cannot connect to the database: " + e.getMessage(), null);
				System.exit(-1);
			}
		}
	}
	
	public static void closeDb() throws SQLException {
		if (!conn.isClosed()) {
			conn.close();
		}
	}
	
	public static String getDbDirectory() {
		return f.getAbsolutePath();
	}
	
	public void finalize() {
		try {
			if (!conn.isClosed()) {
				conn.close();
			}
		}
		catch(SQLException sqlEx) {
			// nothing to do here
			
		}
	}
	
	/**
	 * Returns all Accounts in the database
	 * @param onlyActives If true, only Active ones are returned
	 * @return Vector of Accounts
	 * @throws SQLException
	 */
	public static LinkedHashSet<Account> getAccounts(boolean onlyActives) throws SQLException {
		StringBuilder query = new StringBuilder("SELECT * FROM T_ACCOUNT");
		if (onlyActives) {
			query.append(" WHERE IS_ACTIVE > 0");
		}
		PreparedStatement ps = conn.prepareStatement(query.toString());
		ResultSet rs = ps.executeQuery();
		Account acct = null;
		LinkedHashSet<Account> accounts = new LinkedHashSet<Account>();
		
		while (rs.next()) {
			acct = new Account(rs.getString("ACCOUNT_NAME"));
			accounts.add(acct);
			acct.setAccountId(rs.getInt("ACCOUNT_ID"));
			acct.setAccountBuyCommission(rs.getDouble("ACCOUNT_BUY_COMMISSION"));
			acct.setAccountSaleCommission(rs.getDouble("ACCOUNT_SELL_COMMISSION"));
			acct.setBaseIndex(rs.getString("ACCOUNT_BASE_INDEX"));
			acct.setCurrency(rs.getString("ACCOUNT_CURRENCY"));
			acct.setIsWatchlist(rs.getBoolean("IS_WATCHLIST"));
			acct.setAccountMemo(rs.getString("ACCOUNT_MEMO"));
		}
		
		return accounts;
	}
	/**
	 * Inserts an Account record. Returns the id of the newly added record.
	 * @param acct
	 * @return int Id of the newly added record
	 * @throws SQLException
	 */
	public static int saveAccount(AccountData acct) throws SQLException {
		StringBuilder query = new StringBuilder();
		int lastId = 0;
		int isWatchlist = acct.getIsWatchlist() == false ? 0 : 1;
		if (0 == acct.getAccountId()) {
			// new account
			query.append("INSERT INTO \"T_ACCOUNT\" (ACCOUNT_NAME, ACCOUNT_INSTITUTE_NAME, ACCOUNT_BUY_COMMISSION, ")
				.append("ACCOUNT_SELL_COMMISSION,ACCOUNT_BASE_INDEX,ACCOUNT_CURRENCY,IS_WATCHLIST,ACCOUNT_MEMO)")
				.append("VALUES ('").append(acct.getAccountName()).append("','")
				.append(acct.getAccountInstituteName()).append("',")
				.append(acct.getAccountBuyCommission()).append(",")
				.append(acct.getAccountSaleCommission()).append(",'")
				.append(acct.getBaseIndex()).append("','")
				.append(acct.getCurrency()).append("',")
				.append(isWatchlist).append(",'")
				.append(acct.getAccountMemo()).append("')");
		}
		else {
			query.append("UPDATE \"T_ACCOUNT\" SET ")
				.append("ACCOUNT_NAME = '").append(acct.getAccountName()).append("', ")
				.append("ACCOUNT_INSTITUTE_NAME = '").append(acct.getAccountInstituteName()).append("', ")
				.append("ACCOUNT_BUY_COMMISSION = ").append(acct.getAccountBuyCommission()).append(", ")
				.append("ACCOUNT_SELL_COMMISSION = ").append(acct.getAccountSaleCommission()).append(", ")
				.append("ACCOUNT_BASE_INDEX = '").append(acct.getBaseIndex()).append("', ")
				.append("ACCOUNT_CURRENCY = '").append(acct.getCurrency()).append("', ")
				.append("IS_WATCHLIST = ").append(isWatchlist).append(", ")
				.append("ACCOUNT_MEMO = '").append(acct.getAccountMemo()).append("' ")
				.append("WHERE ACCOUNT_ID = ").append(acct.getAccountId());
		}
			
		PreparedStatement ps = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
		int result = ps.executeUpdate();
		
		try {
			ResultSet rs = ps.getGeneratedKeys();
			
			if (null != rs) {
				rs.next();
				lastId = rs.getInt(1);
			}
			else {
				lastId = acct.getAccountId();
			}
		}
		catch(SQLException sqlEx) {
			/* TODO: log exception */
			throw new SQLException("Save account did not succeed");
		}
		
		return lastId;
	}
	
	/**
	 * Inserts an Asset record. Returns the id of the newly added record.
	 * @param acct
	 * @return int Id of the newly added record
	 * @throws SQLException
	 */
	public static int saveAsset(AssetData asset) throws SQLException {
		StringBuilder query = new StringBuilder();
		int lastId = 0;
		if (0 >= asset.getId()) {
			// new account
			query.append("INSERT INTO \"T_ASSET\" ")
				.append("(ASSET_SYMBOL, ASSET_NAME, ASSET_QUANTITY, ")
				.append("ASSET_BUY_DATE, ASSET_BUY_PRICE, ASSET_BUY_COMMISSION, ")
				.append("ASSET_SELL_DATE, ASSET_SELL_PRICE, ASSET_SELL_COMMISSION, ")
				.append("ASSET_TAX, ASSET_OTHER_COST, ")
				.append("ASSET_LAST_SPLIT_RATIO, ASSET_LAST_SPLIT_DATE, ")
				.append("ACCOUNT_ID, ")
				.append("INDEX_AT_BUY_DATE, INDEX_AT_SELL_DATE, ")
				.append("ASSET_MEMO, ")
				.append("CREATED_ON, MODIFIED_ON, ")
				.append("IS_ACTIVE) ")
				.append("VALUES ('")
				.append(asset.getSymbol()).append("','")
				.append(asset.getName()).append("',")
				.append(asset.getQuantity()).append(",");
			if (null == asset.getAcquireDateAsString()) {
				query.append("null,");
			}
			else {
				query.append("'").append(asset.getAcquireDateAsString()).append("',");
			}
			
			query.append(asset.getAcquirePrice()).append(",")
				.append(asset.getAcquireCommission()).append(",");
				
			if (null == asset.getDisposeDateAsString()) {
				query.append("null,");
			}
			else {
				query.append("'").append(asset.getDisposeDateAsString()).append("',");
			}
			
			query.append(asset.getDisposePrice()).append(",")
				.append(asset.getDisposeCommission()).append(",")
				.append(asset.getTax()).append(",")
				.append(asset.getFee()).append(",")
				.append(asset.getSplitRatio()).append(",");
			if (null == asset.getSplitDateAsString()) {
				query.append("null,");
			}
			else {
				query.append("'").append(asset.getSplitDateAsString()).append("',");
			}
			
			query.append(asset.getAccountId()).append(",")
				.append(asset.getIndexAtAcquire()).append(",")
				.append(asset.getIndexAtDispose()).append(",'")
				.append(asset.getMemo()).append("',")
				.append("CURRENT_TIMESTAMP,").append("CURRENT_TIMESTAMP,")
				.append("1")
				.append(")");
		}
		else {
			query.append("UPDATE \"T_ASSET\" SET ")
				.append("ASSET_SYMBOL = '").append(asset.getSymbol()).append("',")
				.append("ASSET_NAME = '").append(asset.getName()).append("',")
				.append("ASSET_QUANTITY = ").append(asset.getQuantity()).append(",");
				
			if (null == asset.getAcquireDateAsString()) {
				query.append("ASSET_BUY_DATE = null ,");
			}
			else {
				query.append("ASSET_BUY_DATE = '").append(asset.getAcquireDateAsString()).append("',");
			}
			
			query.append("ASSET_BUY_PRICE = ").append(asset.getAcquirePrice()).append(",")
				.append("ASSET_BUY_COMMISSION = ").append(asset.getAcquireCommission()).append(",");
			
			if (null == asset.getDisposeDateAsString()) {
				query.append("ASSET_SELL_DATE = null,");
			}
			else {
				query.append("ASSET_SELL_DATE = '").append(asset.getDisposeDateAsString()).append("',");
			}
			
			query.append("ASSET_SELL_PRICE = ").append(asset.getDisposePrice()).append(",")
				.append("ASSET_SELL_COMMISSION = ").append(asset.getDisposeCommission()).append(",")
				.append("ASSET_TAX = ").append(asset.getTax()).append(",")
				.append("ASSET_OTHER_COST = ").append(asset.getFee()).append(",")
				.append("ASSET_LAST_SPLIT_RATIO = ").append(asset.getSplitRatio()).append(",");
				
			if (null == asset.getSplitDateAsString()) {
				query.append("ASSET_LAST_SPLIT_DATE = null,");
			}
			else {
				query.append("ASSET_LAST_SPLIT_DATE = '").append(asset.getSplitDateAsString()).append("',");
			}
			
			query.append("ACCOUNT_ID = ").append(asset.getAccountId()).append(",")
				.append("INDEX_AT_BUY_DATE = ").append(asset.getIndexAtAcquire()).append(",")
				.append("INDEX_AT_SELL_DATE = ").append(asset.getIndexAtDispose()).append(",")
				.append("ASSET_MEMO = '").append(asset.getMemo()).append("',")
				.append("MODIFIED_ON = ").append("CURRENT_TIMESTAMP ")
				.append("WHERE ASSET_ID = ").append(asset.getId());
		}
		
		log.debug("saveAsset query: " + query.toString());
		PreparedStatement ps = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
		boolean success = ps.execute();
		
		try {
			ResultSet rs = ps.getGeneratedKeys();
			
			if (null != rs) {
				rs.next();
				lastId = rs.getInt(1);
			}
			else {
				lastId = asset.getId();
			}
		}
		catch(SQLException sqlEx) {
			/* TODO: log exception */
			throw new SQLException("Save account did not succeed");
		}
		
		return lastId;
	}
	
	public static void updateAssetIndex(AssetData asset) throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE \"T_ASSET\" SET ")
			.append("INDEX_AT_BUY_DATE = ").append(asset.getIndexAtAcquire()).append(", ")
			.append("INDEX_AT_SELL_DATE = ").append(asset.getIndexAtDispose()).append(", ")
			.append("MODIFIED_ON = CURRENT_TIMESTAMP ")
			.append("WHERE ASSET_ID = ").append(asset.getId());
		PreparedStatement ps = conn.prepareStatement(query.toString());
		ps.execute();
	}
	
	public static void removeAccount(AccountData acct) throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE \"T_ACCOUNT\" SET ")
			.append("MODIFIED_ON = CURRENT_TIMESTAMP, ")
			.append("IS_ACTIVE = 0 ")
			.append("WHERE ACCOUNT_ID = ").append(acct.getAccountId());
		PreparedStatement ps = conn.prepareStatement(query.toString());
		ps.execute();
	}
	
	public static void removeAsset(AssetData asset) throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE \"T_ASSET\" SET ")
			.append("MODIFIED_ON = CURRENT_TIMESTAMP, ")
			.append("IS_ACTIVE = 0 ")
			.append("WHERE ASSET_ID = ").append(asset.getId());
		PreparedStatement ps = conn.prepareStatement(query.toString());
		ps.execute();
	}
	
	/**
	 * Returns Assets for all account except Watchlist
	 * @param onlyActives If true, returns only the active assets
	 * @return Vector of AssetData
	 * @throws SQLException
	 */
	public static LinkedHashSet<AssetData> getAllAssets(boolean onlyActives, LinkedHashSet<AssetData> assets) throws ParseException, SQLException {
		StringBuilder query = new StringBuilder("SELECT ACCOUNT_ID FROM T_ACCOUNT WHERE ACCOUNT_NAME <> '" + 
				WATCHLIST_ACCOUNT_NAME + "'");
		PreparedStatement psAccount = conn.prepareStatement(query.toString());
		ResultSet rsAccount = psAccount.executeQuery();
		
		int accountId = 0;
		if (null == assets) {
			LinkedHashSet<AssetData> newAssets = new LinkedHashSet<AssetData>();
			assets = newAssets;
		}
		
		while (rsAccount.next()) {
			accountId = rsAccount.getInt("ACCOUNT_ID");
			assets = getAssets(accountId, onlyActives, assets);
		}
		
		return assets;
	}
	
	/**
	 * Returns Assets for an account
	 * @param accountId Id for the account
	 * @param onlyActives If true, returns only the active assets
	 * @return Vector of AssetData
	 * @throws SQLException
	 */
	public static LinkedHashSet<AssetData> getAssets(int accountId, boolean onlyActives, LinkedHashSet<AssetData> assets) throws ParseException, SQLException {
		StringBuilder query = new StringBuilder("SELECT * FROM T_ASSET WHERE ACCOUNT_ID = " + accountId);
		if (onlyActives) {
			query.append(" AND IS_ACTIVE > 0");
		}
		query.append(" ORDER BY ").append(DEFAULT_ASSET_SORT_FIELD_NAME);
		PreparedStatement ps = conn.prepareStatement(query.toString());
		ResultSet rs = ps.executeQuery();
		Asset asset = null;
		
		if (null == assets) {
			LinkedHashSet<AssetData> newAssets = new LinkedHashSet<AssetData>();
			assets = newAssets;
		}
		
		LinkedHashMap<Integer, AssetData> assetTable = new LinkedHashMap<Integer, AssetData>();
		Iterator<AssetData> it = assets.iterator();
		while(it.hasNext()) {
			AssetData a = it.next();
			assetTable.put(a.getId(), a);
		}
		
		int assetId = 0;
		while (rs.next()) {
			assetId = rs.getInt("ASSET_ID");
			asset = (Asset)assetTable.get(assetId);
			if (null == asset) {
				asset = new Asset();
				assets.add(asset);
				assetTable.put(assetId, asset);
			}
			asset.setId(assetId);
			asset.setAccountId(accountId);
			asset.setName(rs.getString("ASSET_NAME"));
			asset.setSymbol(rs.getString("ASSET_SYMBOL"));
			asset.setQuantity(rs.getDouble("ASSET_QUANTITY"));
			asset.setAcquireDate(rs.getString("ASSET_BUY_DATE"));
			asset.setAcquirePrice(rs.getDouble("ASSET_BUY_PRICE"));
			asset.setAcquireCommission(rs.getDouble("ASSET_BUY_COMMISSION"));
			asset.setDisposeDate(rs.getString("ASSET_SELL_DATE"));
			asset.setDisposePrice(rs.getDouble("ASSET_SELL_PRICE"));
			asset.setDisposeCommission(rs.getDouble("ASSET_SELL_COMMISSION"));
			asset.setTax(rs.getDouble("ASSET_TAX"));
			asset.setFee(rs.getDouble("ASSET_OTHER_COST"));
			asset.setSplitRatio(rs.getDouble("ASSET_LAST_SPLIT_RATIO"));
			asset.setSplitDate(rs.getString("ASSET_LAST_SPLIT_DATE"));
			asset.setIndexAtAcquire(rs.getDouble("INDEX_AT_BUY_DATE"));
			asset.setIndexAtDispose(rs.getDouble("INDEX_AT_SELL_DATE"));
			asset.setMemo(rs.getString("ASSET_MEMO"));
		}
		
		return assets;
	}

	public static LinkedHashSet<String> getActiveAssetSymbols() throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append("SELECT ASSET_SYMBOL FROM \"T_ASSET\" ")
			.append("WHERE IS_ACTIVE = 1 ORDER BY ").append(DEFAULT_ASSET_SORT_FIELD_NAME);
		PreparedStatement ps = conn.prepareStatement(query.toString(), 
													ResultSet.TYPE_SCROLL_INSENSITIVE,
													ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = ps.executeQuery();
		LinkedHashSet<String> symbols = new LinkedHashSet<String>();
		
		while (rs.next()) {
			symbols.add(rs.getString("ASSET_SYMBOL"));
		}
		
		return symbols;
	}

	public static int getAccountIdByName(String acctName) {
		StringBuilder query = new StringBuilder("SELECT ACCOUNT_ID FROM T_ACCOUNT WHERE ACCOUNT_NAME = '" + acctName + "'");
		int accountId = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				accountId = rs.getInt("ACCOUNT_ID");
			}
		}
		catch(SQLException ex) {
			log.error(ex);
		}
		
		return accountId;
	}
	
	public static String[] getAllBaseIndices() {
		StringBuilder query = new StringBuilder("SELECT DISTINCT ACCOUNT_BASE_INDEX FROM T_ACCOUNT WHERE IS_ACTIVE = 1");
		Vector<String> vIndices = new Vector<String>();
		String[] indices = new String[0];
		try {
			PreparedStatement ps = conn.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				vIndices.add(rs.getString("ACCOUNT_BASE_INDEX"));
			}
		}
		catch(SQLException ex) {
			log.error(ex);
		}
		
		return vIndices.toArray(indices);
	}
}



class LockFilenameFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		if (name.endsWith(".lck")) {
			return true;
		}
		
		return false;
	}
}
