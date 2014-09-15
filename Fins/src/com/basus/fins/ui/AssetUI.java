/**
 * 
 */
package com.basus.fins.ui;

import javax.swing.JComponent;

import java.awt.Dimension;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.plaf.SeparatorUI;
import javax.swing.plaf.basic.BasicSeparatorUI;
import javax.swing.plaf.metal.MetalSeparatorUI;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;

import com.basus.fins.account.Account;
import com.basus.fins.asset.AssetData;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambit
 *
 */
public class AssetUI extends JDialog {
	private static Logger log = Logger.getLogger(AssetUI.class);
	
	private JPanel jPanel = null;  //  @jve:decl-index=0:visual-constraint="42,39"
	private JPanel jPanel0 = null;
	private JPanel jPanel1 = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JLabel jLabel = null;
	private JTextField jTextField = null;
	private JLabel jLabel1 = null;
	private JTextField jTextField1 = null;
	private JPanel jPanel4;
	private JPanel jPanel5;
	private JPanel jPanel6;
	private JPanel jPanel7;
	private JLabel lblName;
	private JTextField txtName;
	private JLabel lblSymbol;
	private JTextField txtSymbol;
	private JLabel lblQuantity;
	private JTextField txtQuantity;
	private JLabel lblBuyDate;
	private JXDatePicker dpBuyDate;
	private JLabel lblBuyPrice;
	private JTextField txtBuyPrice;
	private JLabel lblSaleDate;
	private JXDatePicker dpSaleDate;
	private JLabel lblSalePrice;
	private JTextField txtSalePrice;
	private JLabel lblTax;
	private JTextField txtTax;
	private JLabel lblFee;
	private JTextField txtFee;
	private JInternalFrame iFrame;
	private JButton jButtonSave;
	private JButton jButtonCancel;
	
	private AssetData asset = null;
	private JLabel lblBuyCommission;
	private JTextField txtBuyCommission;
	private JLabel lblSaleCommission;
	private JTextField txtSaleCommission;
	private JLabel lblSplitDate;
	private JXDatePicker dpSplitDate;
	private JLabel lblSplitRatio;
	private JTextField txtSplitRatio;

	private JLabel lblMemo;
	private JTextArea txtMemo;
	private JLabel lblMoveAccount;
	private AccountComboBoxModel accountComboBoxModel = null;

	private JComboBox comboAccounts;

	/**
	 * 
	 */
	public AssetUI(AssetData asset) {
		super(null, Dialog.ModalityType.APPLICATION_MODAL);
		this.asset = asset;
		initialize();
	}
				
	private void initialize() {
		Account acct = Account.getAccount(asset.getAccountId());
		accountComboBoxModel = new AccountComboBoxModel(Account.getAccounts(), this.asset.getAccountId());
		this.setTitle(null == acct ? "Account: Unknown" : "Account: " + acct.getAccountName());
        this.setSize(new Dimension(850, 340));
        this.setResizable(false);
        this.setContentPane(getJPanel());
        //this.pack();
        this.populateAssetData();
        UtilUI.centerComponent(this);
		this.setVisible(true);
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			int idx = 0;
			jPanel = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
			jPanel.setLayout(bl);
			jPanel.add(getJPanel0(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel1(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel2(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel3(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel4(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel5(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel6(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel7(), idx++);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel0	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel0() {
		if (jPanel0 == null) {
			jPanel0 = new JPanel();
			
			BoxLayout bl = new BoxLayout(jPanel0, BoxLayout.X_AXIS);
			jPanel0.setLayout(bl);
			
			lblMoveAccount = new JLabel("Move to Account ");
			comboAccounts = new JComboBox(accountComboBoxModel);
			comboAccounts.setBackground(Color.WHITE);
			comboAccounts.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel0.add(Box.createHorizontalStrut(60), null);
			jPanel0.add(lblMoveAccount);
			jPanel0.add(Box.createHorizontalStrut(20), null);
			jPanel0.add(comboAccounts);
			jPanel0.add(Box.createHorizontalStrut(60), null);
		}
		return jPanel0;
	}
	
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel1, BoxLayout.X_AXIS);
			jPanel1.setLayout(bl);
			
			lblName = new JLabel("Name ");
			txtName = new JTextField();
			txtName.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtName.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtName.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtName.setBorder(DEFAULT_TEXT_BORDER);
			
			lblSymbol = new JLabel("Symbol ");
			txtSymbol = new JTextField();
			txtSymbol.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtSymbol.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtSymbol.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			lblQuantity = new JLabel("Quantity ");
			txtQuantity = new JTextField();
			txtQuantity.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtQuantity.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtQuantity.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtQuantity.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel1.add(Box.createHorizontalStrut(20), null);
			jPanel1.add(lblName);
			jPanel1.add(txtName);
			jPanel1.add(Box.createHorizontalStrut(20), null);
			jPanel1.add(lblSymbol);
			jPanel1.add(txtSymbol);
			jPanel1.add(Box.createHorizontalStrut(20), null);
			jPanel1.add(lblQuantity);
			jPanel1.add(txtQuantity);
			jPanel1.add(Box.createHorizontalStrut(20), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel2, BoxLayout.X_AXIS);
			jPanel2.setLayout(bl);
			
			lblBuyDate = new JLabel("Bought on ");
			dpBuyDate = new JXDatePicker();
			dpBuyDate.setMinimumSize(MIN_DATE_FIELD_DIM);
			dpBuyDate.setMaximumSize(MAX_DATE_FIELD_DIM);
			dpBuyDate.setPreferredSize(PREFERRED_DATE_FIELD_DIM);
			dpBuyDate.setBorder(DEFAULT_TEXT_BORDER);
			
			lblBuyPrice = new JLabel("Bought at (Price per unit) ");
			txtBuyPrice = new JTextField();
			txtBuyPrice.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtBuyPrice.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtBuyPrice.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtBuyPrice.setBorder(DEFAULT_TEXT_BORDER);
			
			lblBuyCommission = new JLabel("Commission at Buy ");
			txtBuyCommission = new JTextField();
			txtBuyCommission.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtBuyCommission.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtBuyCommission.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtBuyCommission.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel2.add(Box.createHorizontalStrut(20), null);
			jPanel2.add(lblBuyDate);
			jPanel2.add(dpBuyDate);
			jPanel2.add(Box.createHorizontalStrut(20), null);
			jPanel2.add(lblBuyPrice);
			jPanel2.add(txtBuyPrice);
			jPanel2.add(Box.createHorizontalStrut(20), null);
			jPanel2.add(lblBuyCommission);
			jPanel2.add(txtBuyCommission);
			jPanel2.add(Box.createHorizontalStrut(20), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel3, BoxLayout.X_AXIS);
			jPanel3.setLayout(bl);
			
			lblSaleDate = new JLabel("Sold on ");
			dpSaleDate = new JXDatePicker();
			dpSaleDate.setMinimumSize(MIN_DATE_FIELD_DIM);
			dpSaleDate.setMaximumSize(MAX_DATE_FIELD_DIM);
			dpSaleDate.setPreferredSize(PREFERRED_DATE_FIELD_DIM);
			dpSaleDate.setBorder(DEFAULT_TEXT_BORDER);
			
			lblSalePrice = new JLabel("Sold at (Price per unit) ");
			txtSalePrice = new JTextField();
			txtSalePrice.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtSalePrice.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtSalePrice.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtSalePrice.setBorder(DEFAULT_TEXT_BORDER);
			
			lblSaleCommission = new JLabel("Commission at Sell ");
			txtSaleCommission = new JTextField();
			txtSaleCommission.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtSaleCommission.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtSaleCommission.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtSaleCommission.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel3.add(Box.createHorizontalStrut(20), null);
			jPanel3.add(lblSaleDate);
			jPanel3.add(dpSaleDate);
			jPanel3.add(Box.createHorizontalStrut(20), null);
			jPanel3.add(lblSalePrice);
			jPanel3.add(txtSalePrice);
			jPanel3.add(Box.createHorizontalStrut(20), null);
			jPanel3.add(lblSaleCommission);
			jPanel3.add(txtSaleCommission);
			jPanel3.add(Box.createHorizontalStrut(20), null);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jPanel4	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel4, BoxLayout.X_AXIS);
			jPanel4.setLayout(bl);
			
			lblTax = new JLabel("Total Tax ");
			txtTax = new JTextField();
			txtTax.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtTax.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtTax.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtTax.setBorder(DEFAULT_TEXT_BORDER);
			
			lblFee = new JLabel("Other Fees ");
			txtFee = new JTextField();
			txtFee.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtFee.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtFee.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtFee.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel4.add(Box.createHorizontalStrut(20), null);
			jPanel4.add(lblTax);
			jPanel4.add(txtTax);
			jPanel4.add(Box.createHorizontalStrut(20), null);
			jPanel4.add(lblFee);
			jPanel4.add(txtFee);
			jPanel4.add(Box.createHorizontalStrut(20), null);
		}
		return jPanel4;
	}
	
	/**
	 * This method initializes jPanel5
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			jPanel5 = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel5, BoxLayout.X_AXIS);
			jPanel5.setLayout(bl);
			
			lblSplitDate = new JLabel("Last Split Date ");
			dpSplitDate = new JXDatePicker();
			dpSplitDate.setMinimumSize(MIN_DATE_FIELD_DIM);
			dpSplitDate.setMaximumSize(MAX_DATE_FIELD_DIM);
			dpSplitDate.setPreferredSize(PREFERRED_DATE_FIELD_DIM);
			dpSplitDate.setBorder(DEFAULT_TEXT_BORDER);
			
			lblSplitRatio = new JLabel("Last Split Ratio ");
			txtSplitRatio = new JTextField();
			txtSplitRatio.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtSplitRatio.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtSplitRatio.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtSplitRatio.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel5.add(Box.createHorizontalStrut(20), null);
			jPanel5.add(lblSplitDate);
			jPanel5.add(dpSplitDate);
			jPanel5.add(Box.createHorizontalStrut(20), null);
			jPanel5.add(lblSplitRatio);
			jPanel5.add(txtSplitRatio);
			jPanel5.add(Box.createHorizontalStrut(20), null);
		}
		return jPanel5;
	}
	
	/**
	 * This method initializes jPanel6	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel6() {
		if (jPanel6 == null) {
			jPanel6 = new JPanel();
			
			BoxLayout bl = new BoxLayout(jPanel6, BoxLayout.X_AXIS);
			jPanel6.setLayout(bl);
			
			lblMemo = new JLabel("Memo ");
			txtMemo = new JTextArea("");
			txtMemo.setMinimumSize(new Dimension(420, 80));
			txtMemo.setPreferredSize(new Dimension(420, 80));
			txtMemo.setMaximumSize(new Dimension(600, 80));
			txtMemo.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel6.add(Box.createHorizontalStrut(60), null);
			jPanel6.add(lblMemo);
			jPanel6.add(Box.createHorizontalStrut(20), null);
			jPanel6.add(txtMemo);
			jPanel6.add(Box.createHorizontalStrut(60), null);
		}
		return jPanel6;
	}
	
	/**
	 * This method initializes jPanel7
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel7() {
		if (jPanel7 == null) {
			jPanel7 = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel7, BoxLayout.X_AXIS);
			jPanel7.setLayout(bl);
			
			jPanel4.add(Box.createHorizontalStrut(60), null);
			jPanel7.add(getBtnSave());
			jPanel4.add(Box.createHorizontalStrut(20), null);
			jPanel7.add(getBtnCancel());
			jPanel4.add(Box.createHorizontalStrut(60), null);
		}
		return jPanel7;
	}
	
	private JButton getBtnSave() {
		if (null == jButtonSave) {
			jButtonSave = new JButton();
			jButtonSave.setAction(new SaveAssetAction("Save Asset"));
		}
		
		return jButtonSave;
	}
	
	private void saveAsset() {
		if (null == asset) {
			return;
		}
		
		PortfolioSummaryUI summaryUI = PortfolioSummaryUI.getInstance();
		AssetTableModel oldModel = AccountUI.getAssetTableModelForUI(asset.getAccountId());
		Account acct = (Account)accountComboBoxModel.getSelectedItem();
		AssetTableModel newModel = AccountUI.getAssetTableModelForUI(acct.getAccountId());
		try {
			if (validateInput()) {
				readFromUI();
				asset.addAssetListener(oldModel);
				asset.addAssetListener(newModel);
				asset.addAssetListener(summaryUI.getAssetTableModel());
				asset.addAssetListener(summaryUI.getSummaryTableModel());
				asset.save();
			}
		}
		catch(ParseException pEx) {
			log.error(pEx.getClass().getName() + ": " + pEx.getMessage());
			UtilUI.showError("Please provide a valid date (" + DATE_FORMAT + ")", this);
			return;
		}
		catch(NumberFormatException nfEx) {
			log.error(nfEx.getClass().getName() + ": " + nfEx.getMessage());
			UtilUI.showError("Please provide a valid Number", this);
			return;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.error(ex.getClass().getName() + ": " + ex.getMessage());
			UtilUI.showError("Saving Asset data failed", this);
			return;
		}
		
		UtilUI.showInfo("Asset " + asset.getSymbol() + " saved for '" + Account.getAccount(asset.getAccountId()).getAccountName() + "'", this);
		this.dispose();
		return;
	}
	
	private void cancel() {
		this.dispose();
	}
	
	private void populateAssetData() {
		if (null == asset) {
			return;
		}
		
		// don't let the Symbol change
		if (!asset.getSymbol().trim().equals("")) {
			this.txtSymbol.setEditable(false);
		}
		
		this.txtName.setText(asset.getName());
		this.txtSymbol.setText(asset.getSymbol());
		this.txtQuantity.setText(String.valueOf(asset.getQuantity()));
		
		try {
			String acquireDate = asset.getAcquireDateAsString();
			Date dtAcquire = null;
			if (null != acquireDate && !acquireDate.trim().equals("")) {
				dtAcquire = DATE_FORMATTER.parse(acquireDate); 
			}
			this.dpBuyDate.setDate(dtAcquire);
		}
		catch(ParseException ex) {
			log.error(ex);
			UtilUI.showError("Could not understand Acquire Date", this);
		}
		this.txtBuyPrice.setText(String.valueOf(asset.getAcquirePrice()));
		this.txtBuyCommission.setText(String.valueOf(asset.getAcquireCommission()));

		try {
			String disposeDate = asset.getDisposeDateAsString();
			Date dtDispose = null;
			if (null != disposeDate && !disposeDate.trim().equals("")) {
				dtDispose = DATE_FORMATTER.parse(disposeDate); 
			}
			this.dpSaleDate.setDate(dtDispose);
		}
		catch(ParseException ex) {
			log.error(ex);
			UtilUI.showError("Could not understand Sale Date", this);
		}
		this.txtSalePrice.setText(String.valueOf(asset.getDisposePrice()));
		this.txtSaleCommission.setText(String.valueOf(asset.getDisposeCommission()));
		
		this.txtTax.setText(String.valueOf(asset.getTax()));
		this.txtFee.setText(String.valueOf(asset.getFee()));
		try {
			String splitDate = asset.getSplitDateAsString();
			Date dtSplit = null;
			if (null != splitDate && !splitDate.trim().equals("")) {
				dtSplit = DATE_FORMATTER.parse(splitDate); 
			}
			this.dpSplitDate.setDate(dtSplit);
		}
		catch(ParseException ex) {
			log.error(ex);
			UtilUI.showError("Could not understand Acquire Date", this);
		}
		this.txtSplitRatio.setText(String.valueOf(asset.getSplitRatio()));
		
		this.txtMemo.setText(asset.getMemo());
	}
	
	private void readFromUI() throws ParseException, NumberFormatException {
		asset.setName(this.txtName.getText());
		asset.setSymbol(this.txtSymbol.getText());
		asset.setQuantity(Double.parseDouble(this.txtQuantity.getText()));
		
		Date acquireDate = this.dpBuyDate.getDate();
		if (null != acquireDate) {
			asset.setAcquireDate(DATE_FORMATTER.format(acquireDate));
		}
		else {
			asset.setAcquireDate("");
		}
		asset.setAcquirePrice(Double.parseDouble(this.txtBuyPrice.getText()));
		asset.setAcquireCommission(Double.parseDouble(this.txtBuyCommission.getText()));
		
		Date disposeDate = this.dpSaleDate.getDate();
		if (null != disposeDate) {
			asset.setDisposeDate(DATE_FORMATTER.format(disposeDate));
		}
		else {
			asset.setDisposeDate("");
		}
		
		asset.setDisposePrice(Double.parseDouble(this.txtSalePrice.getText()));
		asset.setDisposeCommission(Double.parseDouble(this.txtSaleCommission.getText()));
		asset.setTax(Double.parseDouble(this.txtTax.getText()));
		asset.setFee(Double.parseDouble(this.txtFee.getText()));
		
		Date splitDate = this.dpSplitDate.getDate();
		if (null != splitDate) {
			asset.setSplitDate(DATE_FORMATTER.format(splitDate));
		}
		else {
			asset.setSplitDate("");
		}
		asset.setSplitRatio(Double.parseDouble(this.txtSplitRatio.getText()));
		asset.setMemo(this.txtMemo.getText());
		
		Account acct = (Account)accountComboBoxModel.getSelectedItem();
		if (acct.getAccountId() == asset.getAccountId()) {
			return;
		}
		
		asset.setAccountId(acct.getAccountId());
		//asset.moveToAccount(acct);
	}
	
	private boolean validateInput() {
		// check symbol
		String symbol = this.txtSymbol.getText();
		if (null == symbol || symbol.trim().equals("")) {
			UtilUI.showError("Symbol cannot be empty", null);
			this.txtSymbol.grabFocus();
			return false;
		}
		
		return true;
	}
	
	private JButton getBtnCancel() {
		if (null == jButtonCancel) {
			jButtonCancel = new JButton();
			jButtonCancel.setAction(new CancelAction("Cancel"));
		}
		
		return jButtonCancel;
	}
	
	class SaveAssetAction extends AbstractAction {
		SaveAssetAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			saveAsset();
		}
	}
	
	class CancelAction extends AbstractAction {
		CancelAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			cancel();
		}
	}
}  //  @jve:decl-index=0:visual-constraint="38,33"
