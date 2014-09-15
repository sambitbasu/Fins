/**
 * 
 */
package com.basus.fins.ui;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.basus.fins.PortfolioConstants;
import com.basus.fins.account.Account;
import com.basus.fins.account.AccountData;
import com.basus.fins.asset.Asset;
import com.basus.fins.asset.AssetData;
import com.basus.fins.data.Data;
import com.basus.fins.data.Quote;
import com.basus.fins.data.QuoteUpdateTask;
import com.basus.fins.listener.AssetListener;
import com.basus.fins.listener.QuoteListener;
import com.basus.fins.util.CSVFile;
import com.basus.fins.util.HelperUtil;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambit
 *
 */
public class AssetTableModel extends DefaultTreeTableModel 
	implements QuoteListener, AssetListener  {
	private static final Logger log = Logger.getLogger(AssetTableModel.class);
	private static final String[] COLUMNS = { "Name",
											  "Symbol",
											  "Quantity",
											  "Acquired at (Price)",
											  "Disposed at (Price)",
											  "Cost Basis",
											  "Proceeds",
											  "Profit",
											  "Index at acquire date",
											  "Index at dispose date",
											  "Price now",
											  "Gain",
											  "Index Gain",
											  "vs. Index"};
	private static final int COLUMN_COUNT = COLUMNS.length;
	private AccountData acct = null;
	private JXTreeTable tree = null;
	LinkedHashSet<AssetData> assets = null;
	Quote quote = Quote.getInstance();
	private LinkedHashMap<String, String> lastTable;
	private LinkedHashMap<String, AssetClass> assetTable = new LinkedHashMap<String, AssetClass>();
	private boolean showHolding = PortfolioConstants.DEFAULT_SHOW_HOLDING;
	private boolean showDisposed = PortfolioConstants.DEFAULT_SHOW_DISPOSED;
	/**
	 * 
	 */
	public AssetTableModel(AccountData acct) {
		super(null, Arrays.asList(COLUMNS));
		this.acct = acct;
		init();
	}

	/**
	 * @param columnNames
	 * @param rowCount
	 */
	public AssetTableModel(int rowCount, AccountData acct) {
		super(null, Arrays.asList(COLUMNS));
		this.acct = acct;
		init();
	}
	
	public AssetTableModel(int rowCount, LinkedHashSet<AssetData> assets) {
		super(null, Arrays.asList(COLUMNS));
		this.assets = assets;
		this.registerAssetListener();
	}
	
	public void setAssets(LinkedHashSet<AssetData> assets) {
		this.assets = assets;
		this.registerAssetListener();
	}
	
	void setTreeTable(JXTreeTable tree) {
		this.tree = tree;
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
	
	private void init() {
		int acctId = this.acct.getAccountId();
		try {
			this.assets = this.acct.getAssets();
		} catch (ParseException e) {
			PortfolioDialog.showError("Could not understant Asset data", null);
			log.error(e);
		} catch (SQLException e) {
			PortfolioDialog.showError("Could not retrive Asset data", null);
			log.error(e);
		}
		
		this.registerAssetListener();
	}
	
	private void registerAssetListener() {
		Iterator<AssetData> it = assets.iterator();
		while (it.hasNext()) {
			it.next().addAssetListener(this);
		}
	}
	
	protected AssetData getAssetBySymbol(String symbol) {
		if (null == this.assets) {
			return null;
		}
		
		Iterator<AssetData> it = assets.iterator();
		while (it.hasNext()) {
			AssetData asset = it.next();
			if (asset.getSymbol().trim().equalsIgnoreCase(symbol.trim().toLowerCase())) {
				return asset;
			}
		}
		
		return null;
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
	
	protected synchronized void populateAssetDetails() throws ParseException, SQLException {
		assetTable.clear();
		Vector<AssetData> toAdd = new Vector<AssetData>();
		for (AssetData asset : assets) {
			if ((null != asset.getAcquireDateAsString() && 0 == asset.getIndexAtAcquire()) || // Index@Acquire not saved
					(null != asset.getDisposeDateAsString() && 0 == asset.getIndexAtDispose())) {
				asset.updateIndexPrice();
			}
			
			AssetClass assetClass = assetTable.get(asset.getSymbol());
			if (this.showDisposed && asset.isSold()) {	// show all
				if (null == assetClass) {
					assetClass = new AssetClass(asset.getSymbol());
					assetTable.put(asset.getSymbol(), assetClass);
				}
				//toAdd.add(asset);
				assetClass.add(asset);
			}
			
			if (this.showHolding && !asset.isSold()) {	// show only active
					if (null == assetClass) {
						assetClass = new AssetClass(asset.getSymbol());
						assetTable.put(asset.getSymbol(), assetClass);
					}
					//toAdd.add(asset);
					assetClass.add(asset);
			}
		}
		
		DefaultMutableTreeTableNode theRoot = new DefaultMutableTreeTableNode();
		Set<String> set = assetTable.keySet();
		Iterator<String> sIt = set.iterator();
		while (sIt.hasNext()) {
			String symbol = sIt.next();
			AssetClass assetClass = assetTable.get(symbol);
			DefaultMutableTreeTableNode acRoot = new AssetClassTreeTableNode(assetClass);
			Iterator<AssetData> it = assetClass.iterator();
			while (it.hasNext()) {
				AssetData asset = it.next();
				AssetTreeTableNode aNode = new AssetTreeTableNode(asset);
				acRoot.add(aNode);
			}
			theRoot.add(acRoot);
		}
		
		//updatePrice();
		this.setRoot(theRoot);
		if (null != tree) {
			tree.updateUI();
		}
	}
	
	/*
	protected void deleteRow(int row) throws SQLException {
		this.removeRow(row);
		AssetData asset = assets.get(row);
		asset.remove();
	}
	*/

	/*
	private void updatePrice() throws ParseException {
		if (null == lastTable) {
			return;
		}
		
		log.debug(lastTable);
		String symbol = null;
		String price = null;
		int rowCnt = this.getRowCount();
		double basis = 0.00F;
		double proceed = 0.00F;
		double gain = 0.00F;
		double indexGain = 0.00F;
		double currentIndex = Double.parseDouble(lastTable.get(DEFAULT_INDEX));
		
		for (int cnt = 0; cnt < rowCnt; cnt++) {
			symbol = (String)this.getValueAt(cnt, 1);
			AssetData asset = this.getAssetBySymbol(symbol);
			if (null != (price = lastTable.get(symbol))) {
				asset.setCurrentPrice(Double.parseDouble(price));
				this.setValueAt(price, cnt, 10);
	
				basis = asset.getAcquirePrice() * asset.getQuantity() + asset.getAcquireCommission();
				if (null == asset.getDischargeDate() && 0 == asset.getDischargePrice()) {
					proceed = asset.getCurrentPrice() * asset.getQuantity() 
									- asset.getTax() - asset.getFee();
					indexGain = (currentIndex - asset.getIndexAtAcquire()) * 100 /
									asset.getIndexAtAcquire();
				}
				else {
					proceed = asset.getDischargePrice() * asset.getQuantity() 
								- asset.getTax() - asset.getFee();
					indexGain = (asset.getIndexAtDischarge() - asset.getIndexAtAcquire()) * 100 /
								asset.getIndexAtAcquire();
				}
				
				gain = (proceed - basis) * 100 / basis;
				
				this.setValueAt(HelperUtil.roundTwoDecimals(proceed), cnt, 6);
				this.setValueAt(HelperUtil.roundTwoDecimals(proceed - basis), cnt, 7);
				this.setValueAt(HelperUtil.roundTwoDecimals(gain) + "%", cnt, 11);
				this.setValueAt(HelperUtil.roundTwoDecimals(indexGain) + "%", cnt, 12);
				this.setValueAt(HelperUtil.roundTwoDecimals(gain - indexGain) + "%", cnt, 13);
				
			}
		}
	}
	*/
	
	private void updatePrice() throws ParseException {
		if (null == lastTable) {
			return;
		}
		
		log.debug(lastTable);
		String symbol = null;
		String price = null;
		//int rowCnt = this.getRowCount();
		double basis = 0.00F;
		double proceed = 0.00F;
		double gain = 0.00F;
		double indexGain = 0.00F;
		String indexPrice = lastTable.get(DEFAULT_INDEX);
		double currentIndex = null == indexPrice ? 0.00 : Double.parseDouble(indexPrice);
		Set<String> set = assetTable.keySet();
		Iterator<String> sIt = set.iterator();
		Iterator<AssetData> it = assets.iterator();
		int cnt = 0;
		
		while (sIt.hasNext()) {
			symbol = sIt.next();
			AssetClass assetClass = assetTable.get(symbol);
			it = assetClass.iterator();
			while (it.hasNext()) {
				AssetData asset = it.next();
				symbol = asset.getSymbol();
				if (null != (price = lastTable.get(symbol))) {
					asset.setCurrentPrice(Double.parseDouble(price));
					assetClass.setCurrentPrice(asset.getCurrentPrice());
		
					/*
					basis = asset.getAcquirePrice() * asset.getQuantity() + asset.getAcquireCommission();
					if (null == asset.getDischargeDate() && 0 == asset.getDischargePrice()) {
						proceed = asset.getCurrentPrice() * asset.getQuantity() 
										- asset.getTax() - asset.getFee();
						indexGain = (currentIndex - asset.getIndexAtAcquire()) * 100 /
										asset.getIndexAtAcquire();
					}
					else {
						proceed = asset.getDischargePrice() * asset.getQuantity() 
									- asset.getTax() - asset.getFee();
						indexGain = (asset.getIndexAtDischarge() - asset.getIndexAtAcquire()) * 100 /
									asset.getIndexAtAcquire();
					}
					
					gain = (proceed - basis) * 100 / basis;	
					
					this.setValueAt(HelperUtil.roundTwoDecimals(proceed), cnt, 6);
					this.setValueAt(HelperUtil.roundTwoDecimals(proceed - basis), cnt, 7);
					this.setValueAt(HelperUtil.roundTwoDecimals(gain) + "%", cnt, 11);
					this.setValueAt(HelperUtil.roundTwoDecimals(indexGain) + "%", cnt, 12);
					this.setValueAt(HelperUtil.roundTwoDecimals(gain - indexGain) + "%", cnt, 13);
					
					cnt++;
					*/
				}
				assetClass.update();
			}
		}
	}
	
	// QuoteListener implementation
	@Override
	public void quoteChanged(LinkedHashMap<?, ?> table) {
		lastTable = (LinkedHashMap<String, String>)table;
		try {
			updatePrice();
		} catch (ParseException e) {
			PortfolioDialog.showError("Unable to get data", null);
			log.error(e);
		}
	}

	@Override
	public void assetChanged(AssetData asset) {
		try {
			if (null == this.acct && assets.contains(asset)) {
				if (Account.getAccountById(asset.getAccountId()).getIsWatchlist()) {
					this.removeAssetFromView(asset);
				}
				else {
					tree.updateUI();
				}
				
				return;
			}
			else if (null == this.acct && !assets.contains(asset)) {
				if (Account.getAccountById(asset.getAccountId()).getIsWatchlist()) {
					tree.updateUI();					
				}
				else {
					this.addAssetToView(asset);
				}
				
				return;
			}
			
			if (assets.contains(asset) && (asset.getAccountId() != this.acct.getAccountId())) {
				this.removeAssetFromView(asset);
			}
			if (!assets.contains(asset) && (asset.getAccountId() == this.acct.getAccountId())) {
				this.addAssetToView(asset);
			}
			
			//asset.updatePrice();
		}
		catch (Exception ex) {
			PortfolioDialog.showError("Could not update Asset table", null);
			log.error(ex);
		}
	}

	@Override
	public void assetDeleted(AssetData asset) {
		/* we add Watchlist assets only to watchlist account */
		if (null == this.acct && Account.getAccountById(asset.getAccountId()).getIsWatchlist()) {
			return;
		}
		
		this.removeAssetFromView(asset);
			
	}

	@Override
	public void assetSaved(AssetData asset) {
		/* we add Watchlist assets only to watchlist account */
		if (null == this.acct && Account.getAccountById(asset.getAccountId()).getIsWatchlist()) {
			return;
		}
		
		this.addAssetToView(asset);
 	}
	
	private synchronized void addAssetToView(AssetData asset) {
		try {
			AssetData a = this.getAssetById(asset.getId());
			if (null == a) {
				//asset.updatePrice();
				assets.add(asset);
			}
			AssetClass assetClass = assetTable.get(asset.getSymbol());
			if (null != assetClass) { 
				AssetData oldAsset = assetClass.getAssetById(asset.getId());
				if (null == oldAsset) {
					// new asset in existing asset class
					assetClass.add(asset);
					/* TODO: insert asset node */
					TreeTableNode root = this.getRoot();
					Enumeration en = root.children();
					while (en.hasMoreElements()) {
						AssetClassTreeTableNode assetClassNode = (AssetClassTreeTableNode)en.nextElement();
						if (assetClassNode.getAssetClass().getSymbol().equalsIgnoreCase(asset.getSymbol())) {
							AssetTreeTableNode assetNode = new AssetTreeTableNode(asset);
							assetClassNode.insert(assetNode, assetClassNode.getChildCount());
							break;
							//assetClassNode.add(assetNode);
						}
					}
				} else { // saving an existing asset
					/* TODO: what happens if the Symbol changes? */ 
					
				}
			}
			else {
				// new asset class
				assetClass = new AssetClass(asset.getSymbol());
				assetClass.add(asset);
				assetTable.put(asset.getSymbol(), assetClass);
				/* TODO: insert assetClass node and asset node */
				DefaultMutableTreeTableNode root = (DefaultMutableTreeTableNode)this.getRoot();
				AssetClassTreeTableNode assetClassNode = new AssetClassTreeTableNode(assetClass);
				AssetTreeTableNode assetNode = new AssetTreeTableNode(asset);
				assetClassNode.insert(assetNode, assetClassNode.getChildCount());
				root.insert(assetClassNode, root.getChildCount());
			}
			
			
			try {
				tree.updateUI();
			}
			catch(IllegalArgumentException ex) {
				// a bug in the method throws an IllegalArgumentException
				// however, method seems to work otherwise
				// ignoring ....
			}
		}
		catch (Exception ex) {
			PortfolioDialog.showError("Could not update Asset table", null);
			log.error(ex);
		}
	}
	
	private synchronized void removeAssetFromView(AssetData asset) {
		try {
			assets.remove(asset);
			AssetClass assetClass = assetTable.get(asset.getSymbol());
			TreeTableNode root = null;
			AssetClassTreeTableNode classNodeToRemove = null;
			AssetTreeTableNode assetNodeToRemove = null;
			if (null != assetClass) {
				assetClass.remove(asset);
				root = this.getRoot();
				Enumeration en = root.children();
				while (en.hasMoreElements()) {
					AssetClassTreeTableNode assetClassNode = (AssetClassTreeTableNode)en.nextElement();
					if (assetClassNode.getAssetClass().getSymbol().equalsIgnoreCase(asset.getSymbol())) {
						Enumeration en2 = assetClassNode.children();
						while (en2.hasMoreElements()) {
							AssetTreeTableNode assetNode = (AssetTreeTableNode)en2.nextElement();
							if (assetNode.getAsset() == asset) {
								//assetNode.removeFromParent();
								assetNodeToRemove = assetNode;
								if (assetClassNode.getChildCount() == 1) {
									// asset-node hasn't been removed yet, but it's the last node
									// hence the assetclass node needs to be removed too
									classNodeToRemove = assetClassNode;
								}
								break;
							}
						}
						break;
					}
				}
			}
			else {
				// error: No assetClass
			}
			
			
			if (null != assetNodeToRemove) {
				AssetClassTreeTableNode classNode = (AssetClassTreeTableNode)assetNodeToRemove.getParent();
				classNode.remove(assetNodeToRemove);
				assetNodeToRemove = null;
			}
			
			if (null != classNodeToRemove) {
				classNodeToRemove.removeFromParent();
				AssetClass removeClass = assetTable.remove(classNodeToRemove.getAssetClass().getSymbol());
				classNodeToRemove = null;
				removeClass = null;
			}
			
			try {
				tree.updateUI();
			}
			catch(IllegalArgumentException ex) {
				// a bug in the method throws an IllegalArgumentException
				// however, method seems to work otherwise
				// ignoring ....
			}
			//this.populateAssetDetails();
		}
		catch (Exception ex) {
			PortfolioDialog.showError(this.acct + ": " + "Could not update Asset table", null);
			log.error(ex);
		}
	}
	
	class AssetClass {
		String name;
		String symbol;
		double quantity = 0.00F;
		double basis = 0.00F;
		double proceed = 0.00F;
		double indexBasis = 0.00F;
		double indexProceed = 0.00F;
		double currentPrice = 0.00F;
		double averagePrice = 0.00F;
		double averageDisposePrice = 0.00F;
		boolean isSold = false;
		boolean isPartiallySold = false;
		//Vector<AssetData> assets = new Vector<AssetData>();
		LinkedHashMap<Integer, AssetData> assets = new LinkedHashMap<Integer, AssetData>();
		
		AssetClass(String symbol) {
			this.symbol = symbol;
		}
		
		String getSymbol() {
			return this.symbol;
		}
		
		void setName(String name) {
			if (null != name && null == this.name) {
				this.name = name;
			}
		}
		
		String getName() {
			return this.name;
		}
		
		void setQuantity(double quantity) {
			this.quantity = quantity;
		}
		
		double getQuantity() {
			return this.quantity;
		}
		
		double getAveragePrice() {
			return this.averagePrice;
		}
		
		double getAverageDisposePrice() {
			return this.averageDisposePrice;
		}
		
		boolean isSold() {
			return this.isSold();
		}
		
		boolean isPartiallySold() {
			return this.isPartiallySold;
		}
		
		void setCurrentPrice(double price) {
			this.currentPrice = price;
		}
		
		double getCurrentPrice() {
			return this.currentPrice;
		}
		
		void setCostBasis(double basis) {
			this.basis = basis;
		}
		
		double getCostBasis() {
			return this.basis;
		}
		
		void setProceed(double proceed) {
			this.proceed = proceed;
		}
		
		double getProceed() {
			return this.proceed;
		}
		
		void setIndexBasis(double basis) {
			this.indexBasis = basis;
		}
		
		double getIndexBasis() {
			return this.indexBasis;
		}
		
		void setIndexProceed(double proceed) {
			this.indexProceed = proceed;
		}
		
		double getIndexProceed() {
			return this.indexProceed;
		}
		
		double getProfit() {
			return this.proceed - this.basis;
		}
		
		double getGain() {
			return this.getProfit() / this.basis;
		}
		
		double getIndexProfit() {
			return this.indexProceed - this.indexBasis;
		}
		
		double getIndexGain() {
			return this.getIndexProfit() / this.indexBasis;
		}
		
		public boolean equals(AssetClass obj) {
			return obj.getSymbol().equals(this.symbol);
		}
		
		//////////////////////////////////
		void add(AssetData asset) {
			assets.put(asset.getId(), asset);
			this.update();
		}
		
		void addAll(Vector<AssetData> assets) {
			Iterator<AssetData> it = assets.iterator();
			while (it.hasNext()) {
				this.add(it.next());
			}
		}
		
		void remove(AssetData asset) {
			assets.remove(asset);
			this.update();
		}
		
		Iterator<AssetData> iterator() {
			return assets.values().iterator();
		}
		
		public AssetData getAssetById(int id) {
			Iterator<AssetData> it = this.iterator();
			while (it.hasNext()) {
				AssetData a = it.next();
				if (a.getId() == id) {
					return a;
				}
			}
			
			return null;
		}
		
		////////////////////////////////
		void update() {
			this.resetValues();
			Iterator<AssetData> it = assets.values().iterator();
			int assetCount = assets.size();
			int cnt = 0;
			while (it.hasNext()) {
				AssetData asset = it.next();
				this.setName(asset.getName());
				this.averagePrice = (this.averagePrice * this.quantity + asset.getAcquirePrice() * asset.getQuantity()) / (this.quantity + asset.getQuantity());
				this.quantity += asset.getQuantity();
				this.basis += asset.getCostBasis();
				this.proceed += asset.getCurrentValue();
				this.indexBasis += asset.getIndexBasis();
				this.indexProceed += asset.getIndexProceed();
				this.currentPrice = asset.getCurrentPrice();
				this.averageDisposePrice = (this.averageDisposePrice * cnt + asset.getDisposePrice()) / (cnt + 1);
				this.isSold = (0 == cnt) ? asset.isSold() : this.isSold && asset.isSold();
				this.isPartiallySold = this.isPartiallySold || asset.isSold();
				cnt++;
			}
		}
		
		private void resetValues() {
			this.quantity = 0.00F;
			this.basis = 0.00F;
			this.proceed = 0.00F;
			this.indexBasis = 0.00F;
			this.indexProceed = 0.00F;
			this.averagePrice = 0.00F;
			this.averageDisposePrice = 0.00F;
		}
	}
	
	class AssetClassTreeTableNode extends DefaultMutableTreeTableNode {
		AssetClass assetClass = null;
		
		AssetClassTreeTableNode(AssetClass userObject) {
			super(userObject);
			this.assetClass = userObject;
		}
		
		@Override
		/**
		 * Tells if a column can be edited.
		 */
		public boolean isEditable(int column) {
			return false;
		}
		
		public AssetClass getAssetClass() {
			return assetClass;
		}
		
		@Override
		/**
		 * must override this for setValue from {@link DefaultTreeTableModel} 
		 * to work properly!
		 */
		public int getColumnCount() {
			return COLUMNS.length;
		}
		
		@Override
		public String getValueAt(int column) {
			String display = "";
			if (getUserObject() instanceof AssetClass) {
				AssetClass assetClass = (AssetClass) getUserObject();
				switch(column) {
				case 0:
					String name = "";
					display = null == (name = assetClass.getName()) ? "" : name;
					break;
				case 1:
					display = assetClass.getSymbol();
					break;
				case 2:
					display = HelperUtil.roundTwoDecimals(assetClass.getQuantity());
					break;
				case 3:
					display = HelperUtil.roundTwoDecimals(assetClass.getAveragePrice());
					break;
				case 4:
					if (assetClass.isPartiallySold()) {
						display = HelperUtil.roundTwoDecimals(assetClass.getAverageDisposePrice());
					}
					break;
				case 5:
					display = HelperUtil.roundTwoDecimals(assetClass.getCostBasis());
					break;
				case 6:
					display = HelperUtil.roundTwoDecimals(assetClass.getProceed());
					break;
				case 7:
					display = HelperUtil.roundTwoDecimals(assetClass.getProfit());
					break;	
				case 10:
					display = String.valueOf(assetClass.getCurrentPrice());
					break;	
				case 11:
					display = HelperUtil.getPercent(assetClass.getGain());
					break;	
				case 12:
					display = HelperUtil.getPercent(assetClass.getIndexGain());
					break;
				case 13:
					display = HelperUtil.getPercent(assetClass.getGain() - assetClass.getIndexGain());
					break;
				}
			}
			
			return display;
		}
	}
	
	class AssetTreeTableNode extends DefaultMutableTreeTableNode {
		AssetData asset = null;
		
		AssetTreeTableNode(AssetData userObject) {
			super(userObject);
			this.asset = userObject;
		}
		
		@Override
		/**
		 * Tells if a column can be edited.
		 */
		public boolean isEditable(int column) {
			return false;
		}
		
		@Override
		/**
		 * must override this for setValue from {@link DefaultTreeTableModel} 
		 * to work properly!
		 */
		public int getColumnCount() {
			return COLUMNS.length;
		}
		
		public AssetData getAsset() {
			return asset;
		}
		
		@Override
		public String getValueAt(int column) {
			String display = "";
			if (getUserObject() instanceof AssetData) {
				AssetData asset = (AssetData) getUserObject();
				switch(column) {
				case 0:
					String name = "";
					display = null == (name = asset.getName()) ? "" : name;
					break;
				case 1:
					display = asset.getSymbol();
					break;
				case 2:
					display = HelperUtil.roundTwoDecimals(asset.getQuantity());
					break;
				case 3:
					display = HelperUtil.roundTwoDecimals(asset.getAcquirePrice());
					break;
				case 4:
					display = HelperUtil.roundTwoDecimals(asset.getDisposePrice());
					break;
				case 5:
					display = HelperUtil.roundTwoDecimals(asset.getCostBasis());
					break;
				case 6:
					display = HelperUtil.roundTwoDecimals(asset.getCurrentValue());
					break;
				case 7:
					display = HelperUtil.roundTwoDecimals(asset.getProfit());
					break;	
				case 8:
					display = HelperUtil.roundTwoDecimals(asset.getIndexAtAcquire());
					break;	
				case 9:
					display = HelperUtil.roundTwoDecimals(asset.getIndexAtDispose());
					break;
				case 10:
					display = String.valueOf(asset.getCurrentPrice());
					break;	
				case 11:
					display = HelperUtil.getPercent(asset.getGain());
					break;	
				case 12:
					display = HelperUtil.getPercent(asset.getIndexGain());
					break;
				case 13:
					display = HelperUtil.getPercent(asset.getGain() - asset.getIndexGain());
					break;
				}
			}
			
			return display;
		}
	}
}
