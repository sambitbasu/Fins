/**
 * 
 */
package com.basus.fins.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;

import com.basus.fins.account.Account;
import com.basus.fins.performance.TypedPerformanceInput;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambitb
 *
 */
public class PerformanceInputUI extends JDialog {
	private static final String TITLE = "Performance Input";
	
	private static Logger log = Logger.getLogger(PerformanceInputUI.class);
	
	protected JPanel jPanel = null;
	protected JPanel jPanel0 = null;
	protected JPanel jPanel2 = null;
	protected JPanel jPanel4 = null;
	
	protected JLabel lblAccount = null;
	protected JLabel lblCompare = null;
	protected JComboBox comboAccounts = null;
	protected JTextField txtCompare = null;
	protected JLabel lblStartDate = null;
	protected JLabel lblEndDate = null;
	protected JXDatePicker dpStartDate = null;
	protected JXDatePicker dpEndDate = null;
	
	protected JButton jButtonShow = null;
	protected JButton jButtonCancel = null;
	
	protected AccountComboBoxModel accountComboBoxModel = null;
	private TypedPerformanceInput perfInput = null;
	
	private Vector<ActionListener> listeners = new Vector<ActionListener>();
	
	public PerformanceInputUI(JFrame parent, TypedPerformanceInput perfInput) {
		super(parent, TITLE);
		this.perfInput = perfInput;
		this.init();
	}
	
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	public TypedPerformanceInput getPerformanceInput() {
		return this.perfInput;
	}
	
	private void init() {
		accountComboBoxModel = new AccountComboBoxModel(Account.getAccounts(), 0);
		this.setTitle(TITLE);
        this.setSize(new Dimension(650, 140));
        this.setResizable(false);
        this.setContentPane(getJPanel());
        UtilUI.centerComponent(this);
		this.setVisible(true);
	}
	
	private JPanel getJPanel() {
		if (jPanel == null) {
			int idx = 0;
			jPanel = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
			jPanel.setLayout(bl);
			jPanel.add(getJPanel0(), idx++);
			jPanel.add(Box.createVerticalStrut(60), null);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(Box.createVerticalStrut(60), null);
			jPanel.add(getJPanel2(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
			jPanel.add(getJPanel4(), idx++);
			jPanel.add(new JSeparator(JSeparator.HORIZONTAL), idx++);
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
			
			lblAccount = new JLabel("Account ");
			comboAccounts = new JComboBox(accountComboBoxModel);
			comboAccounts.setBackground(Color.WHITE);
			comboAccounts.setBorder(DEFAULT_TEXT_BORDER);
			
			lblCompare = new JLabel("Compare with ");
			txtCompare = new JTextField();
			txtCompare.setMinimumSize(MIN_TEXT_FIELD_DIM);
			txtCompare.setMaximumSize(MAX_TEXT_FIELD_DIM);
			txtCompare.setPreferredSize(PREFERRED_TEXT_FIELD_DIM);
			txtCompare.setBackground(Color.WHITE);
			txtCompare.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel0.add(Box.createHorizontalStrut(60), null);
			jPanel0.add(lblAccount);
			jPanel0.add(Box.createHorizontalStrut(20), null);
			jPanel0.add(comboAccounts);
			jPanel0.add(Box.createHorizontalStrut(40), null);
			jPanel0.add(lblCompare);
			jPanel0.add(Box.createHorizontalStrut(20), null);
			jPanel0.add(txtCompare);
			jPanel0.add(Box.createHorizontalStrut(60), null);
		}
		return jPanel0;
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
			
			lblStartDate = new JLabel("Start date ");
			dpStartDate = new JXDatePicker();
			dpStartDate.setMinimumSize(MIN_DATE_FIELD_DIM);
			dpStartDate.setMaximumSize(MAX_DATE_FIELD_DIM);
			dpStartDate.setPreferredSize(PREFERRED_DATE_FIELD_DIM);
			dpStartDate.setBorder(DEFAULT_TEXT_BORDER);
			
			lblEndDate = new JLabel("End date ");
			dpEndDate = new JXDatePicker();
			dpEndDate.setMinimumSize(MIN_DATE_FIELD_DIM);
			dpEndDate.setMaximumSize(MAX_DATE_FIELD_DIM);
			dpEndDate.setPreferredSize(PREFERRED_DATE_FIELD_DIM);
			dpEndDate.setBorder(DEFAULT_TEXT_BORDER);
			
			jPanel2.add(Box.createHorizontalStrut(60), null);
			jPanel2.add(lblStartDate);
			jPanel2.add(Box.createHorizontalStrut(40), null);
			jPanel2.add(dpStartDate);
			jPanel2.add(Box.createHorizontalStrut(40), null);
			jPanel2.add(lblEndDate);
			jPanel2.add(Box.createHorizontalStrut(40), null);
			jPanel2.add(dpEndDate);
			jPanel2.add(Box.createHorizontalStrut(60), null);
		}
		return jPanel2;
	}
	
	/**
	 * This method initializes jPanel7
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
			BoxLayout bl = new BoxLayout(jPanel4, BoxLayout.X_AXIS);
			jPanel4.setLayout(bl);
			
			jPanel4.add(Box.createHorizontalStrut(60), null);
			jPanel4.add(getBtnShow());
			jPanel4.add(Box.createHorizontalStrut(20), null);
			jPanel4.add(getBtnCancel());
			jPanel4.add(Box.createHorizontalStrut(60), null);
		}
		return jPanel4;
	}
	
	private JButton getBtnShow() {
		if (null == jButtonShow) {
			jButtonShow = new JButton();
			jButtonShow.setAction(new ShowAction("Show"));
		}
		
		return jButtonShow;
	}
	
	private JButton getBtnCancel() {
		if (null == jButtonCancel) {
			jButtonCancel = new JButton();
			jButtonCancel.setAction(new CancelAction("Cancel"));
		}
		
		return jButtonCancel;
	}
	
	private void cancel() {
		this.dispose();
	}
	
	private void showPerformance() {
		PortfolioSummaryUI summaryUI = PortfolioSummaryUI.getInstance();
		//AssetTableModel oldModel = AccountUI.getAssetTableModelForUI(asset.getAccountId());
		Account acct = (Account)accountComboBoxModel.getSelectedItem();
		AssetTableModel newModel = AccountUI.getAssetTableModelForUI(acct.getAccountId());
		try {
			if (validateInput()) {
				readFromUI();
				/*
				asset.addAssetListener(oldModel);
				asset.addAssetListener(newModel);
				asset.addAssetListener(summaryUI.getAssetTableModel());
				asset.addAssetListener(summaryUI.getSummaryTableModel());
				asset.save();
				*/
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
		
		//UtilUI.showInfo("Asset " + asset.getSymbol() + " saved for '" + Account.getAccount(asset.getAccountId()).getAccountName() + "'", this);
		this.dispose();
		return;
	}
	
	private boolean validateInput() {
		// no validation for now
		return true;
	}
	
	private void readFromUI() throws ParseException, NumberFormatException {
		Account acct = (Account)accountComboBoxModel.getSelectedItem();
		String compareSymbol = this.txtCompare.getText().trim();
		
		String strStartDate = "";
		String strEndDate = "";
		
		Date startDate = this.dpStartDate.getDate();
		
		Date endDate = this.dpEndDate.getDate();
		if (null != endDate) {
			strEndDate = DATE_FORMATTER.format(endDate);
		}

		perfInput.setAccount(acct);
		perfInput.setComparingSymbol(compareSymbol);
		perfInput.setStartDate(startDate);
		perfInput.setEndDate(endDate);
	}
	
	private void notifyListeners(ActionEvent e) {
		Iterator<ActionListener> it = listeners.iterator();
		while (it.hasNext()) {
			ActionListener listener = it.next();
			listener.actionPerformed(e);
		}
	}
	
	class ShowAction extends AbstractAction {
		ShowAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			showPerformance();
			e.setSource(PerformanceInputUI.this);
			notifyListeners(e);
		}
	}
	
	class CancelAction extends AbstractAction {
		CancelAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			cancel();
			e.setSource(PerformanceInputUI.this);
			notifyListeners(e);
		}
	}
}
