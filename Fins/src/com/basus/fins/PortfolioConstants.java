/**
 * 
 */
package com.basus.fins;

import java.awt.Dimension;
import java.text.SimpleDateFormat;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.basus.fins.data.Data;
import com.basus.fins.util.Configuration;

/**
 * @author sambit
 *
 */
public interface PortfolioConstants {
	//////////////////////   F I L E S   ////////////////////////////////
	public static final String CONFIG_FILE_NAME = ".portfolio.properties";
	
	///////////////////   C O N F I G   P A R A M S   ///////////////////
	public static final String BASE_DIR_CONFIG_NAME = "dir.base";
	public static final String DB_DIR_CONFIG_NAME = "db.dir";
	public static final String BACKUP_FILE_CONFIG_NAME = "backup.file.name";
	
	//////////////////////  S T R I N G S   /////////////////////////////
	public static final String DB_DIR = Configuration.getConfig(CONFIG_FILE_NAME).getProperty(DB_DIR_CONFIG_NAME) + "/data";
	public static final String DB_NAME = "portfoliodb";
	public static final String JDBC_DRIVER_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String JDBC_URL = "jdbc:derby:" + DB_DIR + "/" + DB_NAME + ";";
	public static final String BACKUP_FILE_NAME = Configuration.getConfig(CONFIG_FILE_NAME).getProperty(BACKUP_FILE_CONFIG_NAME);
	
	public static final String WATCHLIST_ACCOUNT_NAME = "Watchlist";
	//public static final int WATCHLIST_ACCOUNT_ID = Data.getAccountIdByName(WATCHLIST_ACCOUNT_NAME);
	public static final int WATCHLIST_ACCOUNT_ID = 1;
	
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String PORTFOLIO_SUMMARY_TAB_TITLE = "Summary";
	
	///////////////////////  S Y M B O L S ///////////////////////////////
	public static final String INDEX_SP500 = "^GSPC";
	public static final String INDEX_DOW = "^DJI";
	public static final String INDEX_NASDAQ = "^IXIC";
	public static final String INDEX_RUSSEL2000 = "^RUT";
	public static final String INDEX_BSESENSEX = "^BSESN";
	public static final String INDEX_NIFTY = "^NSEI";
	
	/////////////////////// C U R R E N C Y ////////////////////////////
	public static final String CURRENCY_USD = "USD";
	public static final String CURRENCY_INR = "INR";
	
	//////////////////////// D E F A U L T S  ///////////////////////////
	public static final double DEFAULT_ACCOUNT_BUY_COMMISSION = 0.00;
	public static final double DEFAULT_ACCOUNT_SALE_COMMISSION = 0.00;
	public static final String DEFAULT_INDEX = INDEX_SP500;
	public static final String DEFAULT_CURRENCY = CURRENCY_USD;
	public static final int    DEFAULT_REFRESH_RATE = 5;	// in minutes
	public static final String DEFAULT_DB_DIR = Configuration.getConfig(CONFIG_FILE_NAME).getProperty(BASE_DIR_CONFIG_NAME) + "/data";
	public static final String DEFAULT_JDBC_URL = "jdbc:derby:" + DEFAULT_DB_DIR + "/" + DB_NAME + ";";
	public static final boolean DEFAULT_SHOW_DISPOSED = false;
	public static final boolean DEFAULT_SHOW_HOLDING = true;
	public static final String DEFAULT_ASSET_SORT_FIELD_NAME = "ASSET_SYMBOL";
	
	/////////////////////////// F O R M A T T E R S ////////////////////////
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
	
	////////////////////////////// U I //////////////////////////////////
	public static final Dimension MIN_TEXT_FIELD_DIM = new Dimension(120, 20);
	public static final Dimension MAX_TEXT_FIELD_DIM = new Dimension(160, 20);
	public static final Dimension PREFERRED_TEXT_FIELD_DIM = new Dimension(120, 20);
	public static final Dimension MIN_DATE_FIELD_DIM = new Dimension(150, 30);
	public static final Dimension MAX_DATE_FIELD_DIM = new Dimension(190, 30);
	public static final Dimension PREFERRED_DATE_FIELD_DIM = new Dimension(150, 30);
	public static final Border DEFAULT_TEXT_BORDER = new BevelBorder(BevelBorder.LOWERED);
	
	///////////////////////  U  R  L  s  ////////////////////////////////
	public static final String YAHOO_QUOTE_PAGE_URL_PREFIX = "http://finance.yahoo.com/q?s=";
	public static final String YAHOO_QUOTE_PAGE_URL_SUFFIX = "&ql=1";
	
}
