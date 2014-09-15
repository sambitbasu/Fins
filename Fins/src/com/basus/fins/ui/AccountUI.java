/**
 * 
 */
package com.basus.fins.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import com.basus.fins.PortfolioConstants;
import com.basus.fins.account.Account;
import com.basus.fins.account.AccountData;
import com.basus.fins.asset.Asset;
import com.basus.fins.asset.AssetData;
import com.basus.fins.data.Data;
import com.basus.fins.listener.AssetListener;
import com.basus.fins.ui.AssetTableModel.AssetClassTreeTableNode;
import com.basus.fins.ui.AssetTableModel.AssetTreeTableNode;

/**
 * @author sambit
 *
 */
public class AccountUI extends JSplitPane implements AssetListener, MouseListener, ChangeListener {
	private static final Logger log = Logger.getLogger(AccountUI.class);
	static Vector<AccountUI> indexedAcctUI = new Vector<AccountUI>();
	
	private PortfolioUI parent = null;
	
	private AssetTreeTable jAssetTable = null;
	private AssetTableModel assetTableModel = null;
	private JTable jSummaryTable = null;
	private AccountSummaryTableModel summaryTableModel = null;
	private JTextArea memoArea = null;
	private JTextField txtAcctName = null;
	private JTextField txtAcctInst = null;
	private JTextField txtBuyComm = null;
	private JTextField txtSaleComm = null;
	private JTextField txtBaseIndex = null;
	private JCheckBox chkIsWatchlist = null;
	private JCheckBox chkShowDispose = null;
	private JCheckBox chkShowHolding = null;
	private JTextField txtCurrency = null;
	
	private JButton btnSave = null;
	private JButton jButtonAddAsset = null;
	private JButton jButtonCopyAsset = null;
	private JButton jButtonDeleteAsset = null;
	
	private AccountData acct = null;
	private JButton jButtonEditAsset;
	JSplitPane ui;

	protected AccountUI(AccountData acct, PortfolioUI parent, Dimension parentDimension) {
		super();
		//////////////////
		this.acct = acct;
		assetTableModel = new AssetTableModel(0, acct);
		assetTableModel.setShowDisposed(PortfolioConstants.DEFAULT_SHOW_DISPOSED);
		summaryTableModel = new AccountSummaryTableModel(acct);
		this.parent = parent;
		///////////////////
		
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		Dimension d = new Dimension();
		if (null != parentDimension) {
			d.setSize(parentDimension.getWidth() * 0.9, parentDimension.getHeight() * 0.9);
			//splitPane.setPreferredSize(d);
		}
		this.setDividerLocation(0.4);
		this.setTopComponent(getAccountInfoPanel(d));
		this.setBottomComponent(getAccountDetailPanel(parentDimension));	
	}
		
	protected void setAccountData(AccountData acct) {
		this.acct = acct;
	}
	
	protected AccountData getAccountData() {
		return this.acct;
	}

	protected void populateAccountData() {
		if (null == acct) {
			return;
		}
		
		this.txtAcctName.setText(acct.getAccountName());
		this.txtAcctInst.setText(acct.getAccountInstituteName());
		this.txtBuyComm.setText(String.valueOf(acct.getAccountBuyCommission()));
		this.txtSaleComm.setText(String.valueOf(acct.getAccountSaleCommission()));
		this.txtBaseIndex.setText(acct.getBaseIndex());
		this.chkIsWatchlist.setSelected(acct.getIsWatchlist());
		this.txtCurrency.setText(acct.getCurrency());
		this.memoArea.setText(acct.getAccountMemo());
		
		try {
			assetTableModel.populateAssetDetails();
		}
		catch (SQLException sqlEx) {
			log.error(sqlEx);
			UtilUI.showError(sqlEx.getMessage(), this);
			return;
		}
		catch(ParseException pEx) {
			log.error(pEx);
			UtilUI.showError(pEx.getMessage(), this);
			return;
		}
	}
	
	protected boolean saveUIData() {
		if (null == acct) {
			return false;
		}
		
		String acctName = this.txtAcctName.getText();
		if (null == acctName || acctName.trim().equals("")) {
			UtilUI.showError("Account name cannot be empty", this);
		}
		
		double buyComm = 0.00;
		try {
			buyComm = Double.parseDouble(this.txtBuyComm.getText());
		}
		catch(NumberFormatException ex) {
			UtilUI.showError("Require number - " + ex.getMessage(), this);
			return false;
		}
		
		double saleComm = 0.00;
		try {
			saleComm = Double.parseDouble(this.txtSaleComm.getText());
		}
		catch(NumberFormatException ex) {
			UtilUI.showError("Require number - " + ex.getMessage(), this);
			return false;
		}
		
		acct.setAccountName(acctName);
		acct.setAccountInstituteName(this.txtAcctInst.getText());
		acct.setAccountBuyCommission(buyComm);
		acct.setAccountSaleCommission(saleComm);
		acct.setBaseIndex(this.txtBaseIndex.getText());
		acct.setCurrency(this.txtCurrency.getText());
		acct.setIsWatchlist(this.chkIsWatchlist.isSelected());
		acct.setAccountMemo(this.memoArea.getText());
		
		try {
		     acct.setAccountId(acct.save());
		}
		catch(SQLException sqlEx) {
			/* TODO: log exception */
			System.err.println(sqlEx);
			UtilUI.showError("Failed to save Accountdata", this);
			return false;
		}
		
		UtilUI.showInfo("Account saved", this);
		return true;
	}
	
	protected boolean removeUIData() {
		if (null == acct) {
			return false;
		}
		
		try {
		     acct.remove();
		}
		catch(SQLException sqlEx) {
			System.err.println(sqlEx);
			UtilUI.showError("Failed to delete Account", this);
			return false;
		}
		
		UtilUI.showInfo("Account deleted", this);
		return true; 
	}
	
	protected AssetTableModel getAssetTableModel() {
		return assetTableModel;
	}
	
	protected AccountSummaryTableModel getSummaryTableModel() {
		return summaryTableModel;
	}
	
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1(Dimension parentDim) {
		JScrollPane jScrollPane = null;
		
		if (jScrollPane == null) {
			Dimension paneSize = new Dimension((int)(parentDim.getWidth()), (int)(parentDim.getHeight() * 0.9));
			JTable jTable = getAssestTable(paneSize);
			
			jScrollPane = new JScrollPane(jTable);
			
			jScrollPane.setPreferredSize(paneSize);
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPane;
	}
	
	private JScrollPane getJScrollPane2(Dimension parentDim) {
		JScrollPane jScrollPane = null;
		
		if (jScrollPane == null) {
			Dimension paneSize = new Dimension((int)(parentDim.getWidth()), (int)(parentDim.getHeight() * 0.1));
			JTable jTable = getSummaryTable(paneSize);
			
			jScrollPane = new JScrollPane(jTable);
			
			jScrollPane.setPreferredSize(paneSize);
		}
		return jScrollPane;
	}
	
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JXTreeTable getAssestTable(Dimension parentDim) {
		if (jAssetTable  == null) {
			jAssetTable = new AssetTreeTable(assetTableModel);
			
			assetTableModel.setTreeTable(this.jAssetTable);
			Dimension tableDim = new Dimension(1500, 
					(int)(parentDim.getHeight() * 0.8));
			jAssetTable.setSize(tableDim);
			
			jAssetTable.addMouseListener(this);
		}
		return jAssetTable;
	}
	
	/****************/
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getSummaryTable(Dimension parentDim) {
		if (jSummaryTable  == null) {
			jSummaryTable = new JTable(summaryTableModel) {
				public boolean isCellEditable(int row, int col) {
					return false;
				}
			};
			
			Dimension summaryTableDim = new Dimension(1500, 
					(int)(parentDim.getHeight() * 0.2));
			jSummaryTable.setSize(summaryTableDim);
			
			TableColumn column = null;
			for (int idx = 0; idx < 11; idx++) {
				column = jSummaryTable.getColumnModel().getColumn(idx);
			    switch(idx) {
			    case 0:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(80);
			    	column.setMaxWidth(120);
			    	break;
			    case 1:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(50);
			    	column.setMaxWidth(100);
			    	break;
			    case 2:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(180);
			    	column.setMaxWidth(220);
			    	break;
			    case 3:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(180);
			    	column.setMaxWidth(220);
			    	break;
			    case 4:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(180);
			    	column.setMaxWidth(220);
			    	break;
			    case 5:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(180);
			    	column.setMaxWidth(220);
			    	break;
			    case 6:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(180);
			    	column.setMaxWidth(220);
			    	break;
			    case 7:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(180);
			    	column.setMaxWidth(220);
			    	break;
			    case 8:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(180);
			    	column.setMaxWidth(220);
			    	break;
			    case 9:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(80);
			    	column.setMaxWidth(120);
			    	break;
			    case 10:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(80);
			    	column.setMaxWidth(120);
			    	break;
			    case 11:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(80);
			    	column.setMaxWidth(120);
			    	break;
			    default:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(150);
			    	column.setMaxWidth(220);
			    }
			}
			
			jSummaryTable.setShowGrid(true);
			jSummaryTable.addMouseListener(this);
		}
		return jSummaryTable;
	}
	
	/****************/
	/*
	public JSplitPane getUIContent(Dimension parentDimension) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		Dimension d = new Dimension();
		if (null != parentDimension) {
			d.setSize(parentDimension.getWidth() * 0.9, parentDimension.getHeight() * 0.9);
			//splitPane.setPreferredSize(d);
		}
		splitPane.setDividerLocation(0.4);
		splitPane.setTopComponent(getAccountInfoPanel(d));
		splitPane.setBottomComponent(getAccountDetailPanel(parentDimension));
		
		return splitPane;
	}
	*/
	
	private JPanel getAccountInfoPanel(Dimension parentDim) {
		JPanel acctInfoPanel = new JPanel();
		acctInfoPanel.setPreferredSize(parentDim);
		//acctInfoPanel.setLayout(new GridLayout());
		acctInfoPanel.setLayout(new BoxLayout(acctInfoPanel, BoxLayout.X_AXIS));
		
		JPanel exLeftPanel = new JPanel();
		Dimension exLeftPanelDim = new Dimension((int)(parentDim.getWidth() * 0.1), 
				(int)(parentDim.getHeight() * 0.95));
		exLeftPanel.setPreferredSize(exLeftPanelDim);
		exLeftPanel.setLayout(new BoxLayout(exLeftPanel, BoxLayout.Y_AXIS));
		
		JPanel leftPanel = new JPanel();
		Dimension leftPanelDim = new Dimension((int)(parentDim.getWidth() * 0.4), 
				(int)(parentDim.getHeight() * 0.95));
		leftPanel.setPreferredSize(leftPanelDim);
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		
		JPanel rightPanel = new JPanel();
		Dimension rightPanelDim = new Dimension((int)(parentDim.getWidth() * 0.5), 
				(int)(parentDim.getHeight() * 0.95));
		rightPanel.setPreferredSize(rightPanelDim);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		
		JPanel buttonPanel = new JPanel();
		Dimension buttonPanelDim = new Dimension((int)(parentDim.getWidth() * 0.2), 
				(int)(parentDim.getHeight() * 0.95));
		buttonPanel.setPreferredSize(buttonPanelDim);
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(getBtnSave());
		
		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
		viewPanel.add(this.getShowHoldingChkBox());
		viewPanel.add(this.getShowDisposedChkBox());
		
		exLeftPanel.add(buttonPanel);
		exLeftPanel.add(viewPanel);
		
		JPanel pName = new JPanel();
		pName.setLayout(new GridBagLayout());
		pName.add(new JLabel("Account Name"));
		pName.add(Box.createHorizontalStrut(35));
		pName.add(txtAcctName = new JTextField(16));
		
		
		JPanel iName = new JPanel();
		iName.setLayout(new GridBagLayout());
		iName.add(new JLabel("Institution Name"));
		iName.add(Box.createHorizontalStrut(20));
		iName.add(txtAcctInst = new JTextField(16));
		
		JPanel buyComm = new JPanel();
		buyComm.setLayout(new GridBagLayout());
		buyComm.add(new JLabel("Buy Commission"));
		buyComm.add(Box.createHorizontalStrut(20));
		buyComm.add(txtBuyComm = new JTextField(16));
		
		JPanel saleComm = new JPanel();
		saleComm.setLayout(new GridBagLayout());
		saleComm.add(new JLabel("Sale Commission"));
		saleComm.add(Box.createHorizontalStrut(20));
		saleComm.add(txtSaleComm = new JTextField(16));
		
		JPanel baseIndex = new JPanel();
		baseIndex.setLayout(new GridBagLayout());
		baseIndex.add(new JLabel("Base Index"));
		baseIndex.add(Box.createHorizontalStrut(60));
		baseIndex.add(txtBaseIndex = new JTextField(16));
		
		JPanel watchlistCurrency = new JPanel();
		watchlistCurrency.setLayout(new GridBagLayout());
		watchlistCurrency.add(new JLabel("Watchlist"));
		watchlistCurrency.add(Box.createHorizontalStrut(20));
		watchlistCurrency.add(chkIsWatchlist = new JCheckBox());
		watchlistCurrency.add(Box.createHorizontalStrut(40));
		watchlistCurrency.add(new JLabel("Currency"));
		watchlistCurrency.add(Box.createHorizontalStrut(20));
		watchlistCurrency.add(txtCurrency = new JTextField(8));
		
		leftPanel.add(pName);
		leftPanel.add(iName);
		leftPanel.add(buyComm);
		leftPanel.add(saleComm);
		leftPanel.add(baseIndex);
		leftPanel.add(watchlistCurrency);
		
		JPanel acctMemo = new JPanel();
		Dimension acctMemoDim = new Dimension((int)(parentDim.getWidth() * 0.5), 
				(int)(parentDim.getHeight() * 0.95));
		acctMemo.setPreferredSize(acctMemoDim);
		acctMemo.setLayout(new BoxLayout(acctMemo, BoxLayout.Y_AXIS));
		acctMemo.add(new JLabel("Memo"));
		acctMemo.add(Box.createVerticalStrut(10));
		acctMemo.add(new JScrollPane(memoArea  = new JTextArea(4, 20)));
		memoArea.setWrapStyleWord(true);
		memoArea.setLineWrap(true);
		memoArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		rightPanel.add(acctMemo);
		
		acctInfoPanel.add(exLeftPanel);
		acctInfoPanel.add(leftPanel);
		acctInfoPanel.add(rightPanel);
		return acctInfoPanel;
	}

	private JPanel getAccountDetailPanel(Dimension parentDim) {
		JPanel acctDetailPanel = new JPanel();
		acctDetailPanel.setLayout(new BoxLayout(acctDetailPanel, BoxLayout.Y_AXIS));

		JPanel acctDetail = new JPanel();
		BoxLayout acctDetailLayout = new BoxLayout(acctDetail, BoxLayout.Y_AXIS);
		acctDetail.setLayout(acctDetailLayout);
		acctDetail.setPreferredSize(parentDim);
		acctDetail.add(getJScrollPane1(parentDim));
		acctDetail.add(getJScrollPane2(parentDim));
		
		JPanel detailButtonsPanel = new JPanel();
		detailButtonsPanel.setLayout(new GridBagLayout());
		detailButtonsPanel.add(getAddAssetButton());
		detailButtonsPanel.add(Box.createHorizontalStrut(20));
		detailButtonsPanel.add(getCopyAssetButton());
		detailButtonsPanel.add(Box.createHorizontalStrut(20));
		detailButtonsPanel.add(getEditAssetButton());
		detailButtonsPanel.add(Box.createHorizontalStrut(20));
		detailButtonsPanel.add(getDeleteAssetButton());
		
		acctDetailPanel.add(detailButtonsPanel);
		acctDetailPanel.add(acctDetail);
		
		return acctDetailPanel;
	}
	
	/**
	 * This method initializes btnSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnSave() {
		if (btnSave == null) {
			SaveAccountAction action = new SaveAccountAction("Save Account");
			btnSave = new JButton(action);
		}
		return btnSave;
	}

	
	private JButton getAddAssetButton() {
		if (null == jButtonAddAsset) {
			jButtonAddAsset = new JButton();
			AddAssetAction addAssetAction = new AddAssetAction("Add asset");
			jButtonAddAsset.setAction(addAssetAction);
		}
		return jButtonAddAsset;
	}
	
	private JButton getCopyAssetButton() {
		if (null == jButtonCopyAsset) {
			jButtonCopyAsset = new JButton();
			CopyAssetAction copyAssetAction = new CopyAssetAction("Copy asset");
			jButtonCopyAsset.setAction(copyAssetAction);
		}
		return jButtonCopyAsset;
	}
	
	private JButton getEditAssetButton() {
		if (null == jButtonEditAsset) {
			jButtonEditAsset = new JButton();
			EditAssetAction editAssetAction = new EditAssetAction("Edit asset");
			jButtonEditAsset.setAction(editAssetAction);
		}
		return jButtonEditAsset;
	}
	
	private JButton getDeleteAssetButton() {
		if (null == jButtonDeleteAsset) {
			jButtonDeleteAsset = new JButton();
			DeleteAssetAction deleteAssetAction = new DeleteAssetAction("Delete asset");
			jButtonDeleteAsset.setAction(deleteAssetAction);
		}
		return jButtonDeleteAsset;
	}
	
	private JCheckBox getShowHoldingChkBox() {
		if (null == this.chkShowHolding) {
			this.chkShowHolding = new JCheckBox();
			ShowHoldingAction showHoldingAction = new ShowHoldingAction("Show held assets");
			this.chkShowHolding.setAction(showHoldingAction);
			this.chkShowHolding.setSelected(PortfolioConstants.DEFAULT_SHOW_HOLDING);
		}
		return this.chkShowHolding;
	}
	
	private JCheckBox getShowDisposedChkBox() {
		if (null == this.chkShowDispose) {
			this.chkShowDispose = new JCheckBox();
			ShowDisposedAction showDisposedAction = new ShowDisposedAction("Show disposed assets");
			this.chkShowDispose.setAction(showDisposedAction);
			this.chkShowDispose.setSelected(PortfolioConstants.DEFAULT_SHOW_DISPOSED);
		}
		return this.chkShowDispose;
	}
	
	void updateAssetView() {
		try {
			assetTableModel.populateAssetDetails();
		}
		catch(Exception ex) {
			log.error(ex);
			UtilUI.showInfo("Failed to update Asset view", this);
		}
	}
	
	void updateSummary() {
		try {
			summaryTableModel.populateSummary();
		}
		catch(Exception ex) {
			log.error(ex);
			UtilUI.showInfo("Failed to update Summary", null);
		}
	}
	
	private void saveAccount() {
		boolean success = this.saveUIData();
		if (success) {
			indexedAcctUI.add(this);
			parent.addAccountUI(this);
		}
	}
	
	protected static AccountUI getAccountUI(int accountId) {
		Iterator<AccountUI> it = indexedAcctUI.iterator();
		while (it.hasNext()) {
			AccountUI ui = it.next();
			if (ui.getAccountData().getAccountId() == accountId) {
				return ui;
			}
		}
		
		return null;
	}
	
	protected static AssetTableModel getAssetTableModelForUI(int accountId) {
		AccountUI ui = AccountUI.getAccountUI(accountId);
		if (null == ui) {
			return null;
		}
		
		return ui.getAssetTableModel();
	}
	
	/**
	 * Appends a row at the end of the table. Returns the index of the appended row. 
	 * @return int Index of the appended row
	 */
	private void addAsset() {
		// Block adding asset for an unsaved account
		if (this.acct.getAccountId() <= 0) {
			UtilUI.showInfo("Please save the Account before adding Assets", this);
			return;
		}
		Asset asset = new Asset();
		asset.setAccountId(this.acct.getAccountId());
		asset.addAssetListener(this);
		asset.addAssetListener(this.assetTableModel);
		AssetUI assetui = new AssetUI(asset);
	}
	
	private AssetData getAssetAtSelectedRow() {
		int rowIdx = jAssetTable.getSelectedRow();
		
		if (-1 == rowIdx) {
			return null;
		}
		
		TreePath path = jAssetTable.getPathForRow(rowIdx);
		DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)path.getLastPathComponent();
		AssetData asset = null;
		
		if (!(node instanceof AssetTreeTableNode)) {
			return null;
		}
		
		AssetTreeTableNode assetNode = (AssetTreeTableNode) node;
		asset = assetNode.getAsset();
		return asset;
	}
	
	/**
	 * Copies an asset 
	 * @return int Index of the appended row
	 */
	private void copyAsset() {
		// Block adding asset for an unsaved account
		if (this.acct.getAccountId() <= 0) {
			UtilUI.showInfo("Please save the Account before copying Assets", this);
			return;
		}
		AssetData selectedAsset = this.getAssetAtSelectedRow();
		if (null == selectedAsset) {
			UtilUI.showError("Please select an asset to copy", this);
		}
		
		AssetData asset = null;
		try {
			asset = selectedAsset.copy();
		}
		catch(ParseException pEx) {
			UtilUI.showError("An error occured while copying the asset", this);
			log.error(pEx);
			return;
		}
		
		
		asset.addAssetListener(this);
		asset.addAssetListener(this.assetTableModel);
		AssetUI assetui = new AssetUI(asset);
	}
	
	/**
	 * Edits the asset at the selected row. Returns the index of the appended row. 
	 * @return int Index of the edited row
	 */
	private int editAsset() {
		AssetData asset = this.getAssetAtSelectedRow();
		if (null == asset) {
			return 0;
		}
		
		asset.addAssetListener(this);
		AssetUI assetui = new AssetUI(asset);
		return asset.getId();
	}
	
	/**
	 * Removes the selected row. 
	 */
	private void deleteAsset() {
		//int rowIdx = this.jTable.getSelectedRow();
		TreePath selectionPath = jAssetTable.getTreeSelectionModel().getSelectionPath();
		AssetData asset = null;
		
		
		DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)selectionPath.getLastPathComponent();
		AssetTreeTableNode assetNode = null;
		if (node instanceof AssetTreeTableNode) {
			assetNode = (AssetTreeTableNode)node;
			asset = assetNode.getAsset();
			
			int confirmed = UtilUI.confirm("Delete Asset", 
					"Are you sure you want to delete this asset (" + asset.getSymbol() + ")?", 
					this);
			
			if (JOptionPane.YES_OPTION != confirmed) {
				return;
			}
		}
		else {
			UtilUI.showError("You must select an Asset", this);
			return;
		}
		
		try {
			asset.remove();
			UtilUI.showInfo("Deleted the asset \"" + asset.getSymbol() + "\"", this);
			asset = null;
		} catch(SQLException sqlEx) {
			log.error(sqlEx);
			UtilUI.showError("Could not delete this asset", this);
			return;
		}
		
		/*
		try {
			this.assetTableModel.deleteRow(rowIdx);
			UtilUI.showInfo("Deleted the asset \"" + asset.getSymbol() + "\"", this);
			asset = null;
		}
		catch(SQLException sqlEx) {
			log.error(sqlEx);
			UtilUI.showError("Could not delete this asset", this);
			return;
		}
		*/
	}
	
	private void viewChanged() {
		assetTableModel.setShowHolding(this.chkShowHolding.isSelected());
		assetTableModel.setShowDisposed(this.chkShowDispose.isSelected());
		summaryTableModel.setShowHolding(this.chkShowHolding.isSelected());
		summaryTableModel.setShowDisposed(this.chkShowDispose.isSelected());
		updateAssetView();
		updateSummary();
	}
	
	///////////////////
	// AssetListener implementation
	@Override
	public void assetChanged(AssetData asset) {
		this.updateAssetView();
		this.updateSummary();
	}

	@Override
	public void assetDeleted(AssetData asset) {
		// TODO Auto-generated method stub
		this.updateSummary();
	}

	@Override
	public void assetSaved(AssetData asset) {
		this.updateAssetView();
		this.updateSummary();
	}
	
	// MouseListener implementation
	@Override
	public void mouseClicked(MouseEvent e) {
		// double click edits asset
		if (e.getClickCount() == 2) {
			this.editAsset();
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	//////////////////
	class ShowHoldingAction extends AbstractAction {
		ShowHoldingAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			viewChanged();
		}
		
	}
	
	class ShowDisposedAction extends AbstractAction {
		ShowDisposedAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			viewChanged();
		}
		
	}
	
	class SaveAccountAction extends AbstractAction {
		SaveAccountAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			saveAccount();
		}
		
	}
	
	class AddAssetAction extends AbstractAction {
		AddAssetAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			addAsset();
		}
	}
	
	class CopyAssetAction extends AbstractAction {
		CopyAssetAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			copyAsset();
		}
	}
	
	class EditAssetAction extends AbstractAction {
		EditAssetAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			editAsset();
		}
	}
		
	class DeleteAssetAction extends AbstractAction {
		DeleteAssetAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			deleteAsset();
		}
	}

	// ChangeListener implementation
	@Override
	public void stateChanged(ChangeEvent event) {
		this.updateAssetView();
		this.updateSummary();
	}
	
	///////////////////////////////////
	public JTable autoResizeColWidth(JTable table, DefaultTableModel model) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setModel(model);
 
        int margin = 5;
 
        for (int i = 0; i < table.getColumnCount(); i++) {
            int                     vColIndex = i;
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(vColIndex);
            int                     width     = 0;
 
            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();
 
            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }
 
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
 
            width = comp.getPreferredSize().width;
 
            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }
 
            // Add margin
            width += 2 * margin;
 
            // Set the width
            col.setPreferredWidth(width);
        }
 
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
            SwingConstants.LEFT);
 
        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
 
        return table;
    }
}
