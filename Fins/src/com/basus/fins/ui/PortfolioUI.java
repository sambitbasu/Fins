package com.basus.fins.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
//import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import com.basus.fins.account.Account;
import com.basus.fins.account.AccountData;
import com.basus.fins.data.Data;
import com.basus.fins.data.QuoteUpdateTask;
import com.basus.fins.performance.PerformanceAccount;
import com.basus.fins.performance.PerformanceInput;
import com.basus.fins.performance.TypedPerformanceInput;
import com.basus.fins.util.DataUtil;
import com.basus.fins.work.WorkQueue;

import static com.basus.fins.PortfolioConstants.*;

public class PortfolioUI extends JFrame implements ActionListener, MouseListener, WindowListener, MenuListener {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PortfolioUI.class);
	private static final int FILE_CHOOSER_MODE_OPEN = 1;
	private static final int FILE_CHOOSER_MODE_SAVE = 2;
	
	private JMenuBar jJMenuBar = null;
	private JMenu menuFile = null;
	private JMenu menuEdit = null;
	private JMenu menuAccount = null;
	private JMenu menuTools = null;
	private JMenu menuHelp = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JPanel jPanel2 = null;
	private static JStatus labelStatus = null;
	private JPanel jPanel3 = null;
	private JPanel jPanel4 = null;
	private static JStatus labelStatus2 = null;
	private JButton btnUpdate = null;
	private JButton btnRefresh = null;
	private JPanel jPanel5 = null;
	private JPanel jPanel22;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private Action addAccountAction;
	private Action copyAccountAction;
	private Action deleteAccountAction;
	private Action datedPerformanceAction;
	
	private QuoteUpdateTask task;
	private BackupAction backupAction;
	private LoadDataAction loadAction;
	private ExitAction exitAction;
	
	
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			
			jPanel = new JPanel();
			Dimension appSize = UtilUI.APP_DIMENSION;
			Dimension panelSize = new Dimension();
			panelSize.setSize(appSize.width * 0.95, appSize.height * 0.95);
			jPanel.setPreferredSize(panelSize);
			jPanel.add(getJPanel5(panelSize), null);
			GridBagLayout gbl = new GridBagLayout();
			gbl.setConstraints(jPanel, gridBagConstraints4);
		}
		return jPanel;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getMenuFile());
			jJMenuBar.add(getMenuEdit());
			jJMenuBar.add(getMenuAccount());
			jJMenuBar.add(getMenuTools());
			jJMenuBar.add(getMenuHelp());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes menuFile	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenuFile() {
		if (menuFile == null) {
			menuFile = new JMenu();
			menuFile.setText("File");
			
			JMenuItem miBackupData = new JMenuItem();
			miBackupData.setAction(backupAction = new BackupAction("Backup data ..."));
			menuFile.add(miBackupData);
			
			JMenuItem miLoadData = new JMenuItem();
			miLoadData.setAction(loadAction = new LoadDataAction("Load data ..."));
			menuFile.add(miLoadData);
			
			menuFile.add(new JSeparator());
			
			JMenuItem miExit = new JMenuItem();
			miExit.setAction(exitAction = new ExitAction("Exit"));
			menuFile.add(miExit);
		}
		return menuFile;
	}

	/**
	 * This method initializes menuEdit	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenuEdit() {
		if (menuEdit == null) {
			menuEdit = new JMenu();
			menuEdit.setText("Edit");
		}
		
		return menuEdit;
	}

	/**
	 * This method initializes menuEdit	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenuAccount() {
		if (menuAccount  == null) {
			menuAccount = new JMenu();
			menuAccount.setText("Account");
			
			JMenuItem miAddAccount = new JMenuItem("Add account");
			miAddAccount.setAction(addAccountAction = new AddAccountAction("Add account"));
			menuAccount.add(miAddAccount);
			
			JMenuItem miCopyAccount = new JMenuItem("Copy account");
			miCopyAccount.setAction(copyAccountAction = new CopyAccountAction("Copy account"));
			menuAccount.add(miCopyAccount);
			
			JMenuItem miDeleteAccount = new JMenuItem("Delete account");
			miDeleteAccount.setAction(deleteAccountAction = new DeleteAccountAction("Delete account"));
			menuAccount.add(miDeleteAccount);
		}
		
		
		return menuAccount;
	}
	
	/**
	 * This method initializes menuTools	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenuTools() {
		if (menuTools == null) {
			menuTools = new JMenu();
			menuTools.setText("Tools");
			
			JMenuItem miDatedPerf = new JMenuItem("Perfromance between ...");
			miDatedPerf.setAction(datedPerformanceAction = new DatedPerformanceAction("Performance between ..."));
			menuTools.add(miDatedPerf);
		}
		return menuTools;
	}

	/**
	 * This method initializes menuHelp	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMenuHelp() {
		if (menuHelp == null) {
			menuHelp = new JMenu();
			menuHelp.setText("Help");
		}
		return menuHelp;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane(Dimension parentDim) {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			Dimension paneSize = new Dimension();
			paneSize.setSize(parentDim.getWidth() * 0.95, parentDim.getHeight() * 0.95);
			//jTabbedPane.setSize(paneSize);
			jTabbedPane.setPreferredSize(parentDim);
			
			//jTabbedPane.setPreferredSize(new Dimension(1000, 600));
			//jTabbedPane.addTab(null, null, getJScrollPane1(), null);
			
			LinkedHashSet<Account> accounts = Account.getAccounts();
			
			if (accounts.size() > 0) {
				Vector<Account> watchlists = new Vector<Account>();
				for (Account acct : accounts) {
					if (acct.getIsWatchlist()) {
						watchlists.add(acct);
						continue;
					}
					AccountUI accountUI = new AccountUI(acct, this, paneSize);
					jTabbedPane.addTab(acct.getAccountName(), 
										null, 
										//accountUI.getUIContent(paneSize),
										accountUI,
										acct.getAccountName());
					jTabbedPane.addChangeListener(accountUI);
					QuoteUpdateTask.addQuoteListener(accountUI.getAssetTableModel());
					QuoteUpdateTask.addQuoteListener(accountUI.getSummaryTableModel());
					accountUI.populateAccountData();
					AccountUI.indexedAcctUI.add(accountUI);
					jTabbedPane.setForeground(Color.BLUE);
				}
				Iterator<Account> it = watchlists.iterator();
				while (it.hasNext()) {
					Account acct = it.next();
					AccountUI accountUI = new AccountUI(acct, this, paneSize);
					jTabbedPane.addTab(acct.getAccountName(), 
										null, 
										//accountUI.getUIContent(paneSize),
										accountUI,
										acct.getAccountName());
					jTabbedPane.addChangeListener(accountUI);
					QuoteUpdateTask.addQuoteListener(accountUI.getAssetTableModel());
					QuoteUpdateTask.addQuoteListener(accountUI.getSummaryTableModel());
					accountUI.populateAccountData();
					AccountUI.indexedAcctUI.add(accountUI);
					jTabbedPane.setForeground(Color.GRAY);
				}
			}
			else {
				Account acct = new Account("Untitled");
				AccountUI accountUI = new AccountUI(acct, this, jTabbedPane.getSize());
				jTabbedPane.addTab(null, 
									null, 
									//accountUI.getUIContent(jTabbedPane.getSize()),
									accountUI,
									"Untitled");
				AccountUI.indexedAcctUI.add(accountUI);
			}
			
			// summary tab
			PortfolioSummaryUI summaryUI = PortfolioSummaryUI.getInstance(this);
			jTabbedPane.addTab(PORTFOLIO_SUMMARY_TAB_TITLE, 
								null, 
								summaryUI.getUIContent(jTabbedPane.getSize()), 
								"Summary of all accounts");
			jTabbedPane.setBackgroundAt(jTabbedPane.getTabCount() - 1, Color.GREEN);
			//AccountUI.indexedAcctUI.add(summaryUI);
			QuoteUpdateTask.addQuoteListener(summaryUI.getAssetTableModel());
			QuoteUpdateTask.addQuoteListener(summaryUI.getSummaryTableModel());
			summaryUI.populateData();
		}
		
		return jTabbedPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2(Dimension parentSize) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 2, 0);
		gridBagConstraints.gridy = 2;
		gridBagConstraints.ipadx = 848;
		gridBagConstraints.gridx = 0;
		
		jPanel2 = new JPanel();
		Dimension panelSize = new Dimension((int)(parentSize.getWidth() * 1.0), 
				(int)(parentSize.getHeight() * 0.9));
		jPanel2.setPreferredSize(panelSize);
		
		jPanel2.add(getJTabbedPane(panelSize), null);
		//jPanel2.add(getJPanel22(), null);
		
		GridBagLayout gbl2 = new GridBagLayout();
		gbl2.setConstraints(jPanel2, gridBagConstraints);
		return jPanel2;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	@SuppressWarnings("unchecked")
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			WorkQueue<Runnable> queue = (WorkQueue<Runnable>) WorkQueue.getStatus1Queue();
			labelStatus = new JStatus(SwingConstants.LEADING, queue);
			jPanel1 = new JPanel();
			jPanel1.setLayout(new FlowLayout(FlowLayout.LEADING));
			jPanel1.add(labelStatus, null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel22() {
		if (jPanel22 == null) {
			jPanel22 = new JPanel();
			Dimension appSize = UtilUI.APP_DIMENSION;
			Dimension panelSize = new Dimension();
			panelSize.setSize(jPanel2.getWidth() * 0.1, jPanel2.getHeight());
			//jPanel22.setPreferredSize(panelSize);
			jPanel22.setLayout(new BoxLayout(jPanel22, BoxLayout.Y_AXIS));
			jPanel22.add(getBtnUpdate(), null);
			jPanel22.add(Box.createVerticalStrut(20), null);
			jPanel22.add(getBtnRefresh(), null);
			//jPanel22.add(Box.createVerticalStrut(20), null);
			//jPanel22.add(getBtnSave(), null);
		}
		return jPanel22;
	}

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel3(Dimension parentDim) {
		if (jPanel3 == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = -1;
			gridBagConstraints1.gridy = -1;
			jPanel3 = new JPanel();
			Dimension panelSize = new Dimension();
			panelSize.setSize(parentDim.getWidth(), parentDim.getHeight() * 0.06);
			jPanel3.setPreferredSize(panelSize);
			jPanel3.setLayout(new GridLayout(1, 2));
			jPanel3.add(getJPanel1(), null);
			jPanel3.add(getJPanel4(), null);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jPanel4	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	@SuppressWarnings("unchecked")
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			WorkQueue<Runnable> queue = (WorkQueue<Runnable>) WorkQueue.getStatus2Queue();
			labelStatus2 = new JStatus(SwingConstants.RIGHT, queue);
			jPanel4 = new JPanel();
			jPanel4.setLayout(new FlowLayout(FlowLayout.RIGHT));
			jPanel4.add(labelStatus2, null);
		}
		return jPanel4;
	}

	/**
	 * This method initializes btnUpdate	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnUpdate() {
		if (btnUpdate == null) {
			btnUpdate = new JButton();
			btnUpdate.setText("Update");
			btnUpdate.addMouseListener(this);
		}
		return btnUpdate;
	}

	/**
	 * This method initializes btnRefresh	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnRefresh() {
		if (btnRefresh == null) {
			RefreshViewAction action = new RefreshViewAction("Refresh View");
			btnRefresh = new JButton(action);
		}
		return btnRefresh;
	}

	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Portfolio thisClass = new Portfolio();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}
	*/

	public static void boot() {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PortfolioUI thisClass = new PortfolioUI();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}
	
	/**
	 * This is the default constructor
	 */
	public PortfolioUI() {
		super();
		initialize();
	}

	public static void setStatus1(String text, long visibleInMillis) {
		// this may be called before the UI is loaded
		if (null == labelStatus) {
			return;
		}
		labelStatus.setStatus(text, visibleInMillis);
	}
	
	public static void setStatus2(String text, long visibleInMillis) {
		// this may be called before the UI is loaded
		if (null == labelStatus2) {
			return;
		}
		labelStatus2.setStatus(text, visibleInMillis);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		task = new QuoteUpdateTask();
		
		this.setPreferredSize(UtilUI.APP_DIMENSION);
		this.addMouseListener(this);
		this.setTitle("Portfolio - Asset Management");
		////////////////
		this.add(getJJMenuBar(), BorderLayout.NORTH);
		this.add(getJPanel());
		////////////////
		/*
		this.setContentPane(getJPanel());
		this.setJMenuBar(getJJMenuBar());
		*/
		
		this.pack();
		UtilUI.centerComponent(this);
		
		Thread th = new Thread(task);
		th.start();
	}

	/**
	 * This method initializes jPanel5	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel5(Dimension parentDim) {
		if (jPanel5 == null) {
			jPanel5 = new JPanel();
			//jPanel5.setLayout(new GridBagLayout());
			Dimension panelSize = new Dimension();
			panelSize.setSize(parentDim.getWidth(), parentDim.getHeight());
			
			jPanel5.setPreferredSize(panelSize);
			jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.Y_AXIS));
			/*
			jPanel5.add(getJPanel2(), new GridBagConstraints());
			jPanel5.add(getJPanel3(), new GridBagConstraints());
			*/
			jPanel5.add(getJPanel2(panelSize));
			jPanel5.add(getJPanel3(panelSize));
		}
		return jPanel5;
	}

	AccountUI getSelectedAccountUI() {
		AccountUI acctUI = (AccountUI)AccountUI.indexedAcctUI.get(jTabbedPane.getSelectedIndex());
		
		return acctUI;
	}
	
	private void addNewAccount(Account acct) {
		if (null == acct) {
			acct = new Account("Untitled");
		}
		AccountUI accountUI = new AccountUI(acct, this, jTabbedPane.getSize());
		jTabbedPane.insertTab(acct.getAccountName(), 
							null, 
							accountUI,
							null,
							jTabbedPane.getTabCount() - 1);			// the last tab is always summary
		jTabbedPane.setSelectedIndex(jTabbedPane.getTabCount() - 2);
		accountUI.populateAccountData();
		AccountUI.indexedAcctUI.add(accountUI);
		if (acct.getIsWatchlist()) {
			jTabbedPane.setForeground(Color.GRAY);
		}
		else {
			jTabbedPane.setForeground(Color.BLUE);
		}
	}
	
	private void copyAccount() {
		AccountUI acctUI = (AccountUI)jTabbedPane.getSelectedComponent();
		AccountData acct = acctUI.getAccountData();
		try {
			Account cpyAcct = acct.copy();
			this.addNewAccount(cpyAcct);
			
			UtilUI.showInfo("Save Account and then each Asset to make changes permanent", this);
		}
		catch(Exception ex) {
			if (log.isDebugEnabled()) {
				ex.printStackTrace();
			}
			log.info("Copying error: " + ex);
			UtilUI.showError("Failed to copy account " + acct.getAccountName(), null);
		}
	}
	
	private void deleteAccount() {
		AccountUI acctUI = this.getSelectedAccountUI();
		
		if (acctUI.getAccountData().getAccountName().equalsIgnoreCase(WATCHLIST_ACCOUNT_NAME)) {
			UtilUI.showError(WATCHLIST_ACCOUNT_NAME + " cannot be deleted", this);
			return;
		}
		
		int confirmed = UtilUI.confirm("Confirm Delete Account", 
				"Are you sure to delete '" + acctUI.getAccountData().getAccountName() + "'?", 
				this);
		
		if (JOptionPane.YES_OPTION != confirmed) {
			return;
		}
		
		boolean success = acctUI.removeUIData();
		if (success) {
			AccountUI.indexedAcctUI.remove(jTabbedPane.getSelectedIndex());
			jTabbedPane.remove(jTabbedPane.getSelectedIndex());
		}
	}
		
	private void refreshView() {
		AccountUI acctUI = this.getSelectedAccountUI();
		
		/* TODO: Update Account info - needed? */
		acctUI.updateAssetView();
	}
	
	void addAccountUI(AccountUI acctUI) {
		jTabbedPane.setTitleAt(jTabbedPane.getSelectedIndex(), acctUI.getAccountData().getAccountName());
	}
	
	private void backup() {
		File destDir = getSelectedDirectory("Choose Directory for saving backup", FILE_CHOOSER_MODE_SAVE);
		log.info("Backup directory: " + destDir.getPath());
		String saveLoc = null;
		
		try {
			saveLoc = DataUtil.backupDb(destDir);
		} catch (Exception e) {
			UtilUI.showError("An error occurred saving backup", this);
			log.error(e);
			return;
		}
		
		UtilUI.showInfo("Backup saved in file " + saveLoc, this);
	}
	
	private void loadData() {
		File loadFile = getSelectedDirectory("Choose backup file", FILE_CHOOSER_MODE_OPEN);
		if (!loadFile.isFile()) {
			UtilUI.showError("Please select the backup file", this);
			return;
		}
		log.info("Load file: " + loadFile.getPath());

		String loadedLoc = null;
		try {
			loadedLoc = DataUtil.loadDb(loadFile);
		}
		catch (Exception e) {
			UtilUI.showError("An error occurred loading data from " + loadFile.getAbsolutePath(), this);
			log.error(e);
			return;
		}
		
		UtilUI.showInfo("Successfully loaded data from " + loadFile.getAbsolutePath(), this);
	}
	
	private void exit() {
		System.exit(0);
	}
	
	private void doDatedPerformance() {
		TypedPerformanceInput datedPerfInput = PerformanceInput.getDatedPerformanceInput();
		PerformanceInputUI perfInput = new PerformanceInputUI(this, datedPerfInput);
		perfInput.addActionListener(this);
		perfInput.setVisible(true);
	}
	
	private File getSelectedDirectory(String title, int mode) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		
		int ret = 0;
		if (FILE_CHOOSER_MODE_SAVE == mode) {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			ret = fileChooser.showSaveDialog(this);
		}
		else if (FILE_CHOOSER_MODE_OPEN == mode) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "zip file", "zip");
			fileChooser.setFileFilter(filter);
			ret = fileChooser.showOpenDialog(this);
		}
		
		if(ret == JFileChooser.APPROVE_OPTION) {
            File dir = fileChooser.getSelectedFile();
            return dir;
	    }
		
		return null;

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug("ActionEvent source: " + e.getSource());
		log.debug("ActionEvent source: " + e.getActionCommand());
		
		if (e.getSource() instanceof PerformanceInputUI) {
			PerformanceInputUI inputUI = (PerformanceInputUI)e.getSource();
			if (e.getActionCommand().equals("Show")) {
				TypedPerformanceInput input = inputUI.getPerformanceInput();
				LinkedHashSet<Account> set = input.getAccounts();
				Iterator<Account> it = set.iterator();
				Account acct = null;
				if (it.hasNext()) {
					acct = it.next();
				}
				if (null == acct) {
					return;
				}
				// add new tabShowAction
				try {
					this.addNewAccount(new PerformanceAccount(acct, input));
				}
				catch(Exception ex) {
					log.error(ex);
					UtilUI.showError("Failed to copy account " + acct.getAccountName(), null);
				}
			}
		}
	}
	
	// MouseListener implementation
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
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

	// WindowListener implementation
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		try {
			Data.closeDb();
		}
		catch(SQLException sqlEx) {
			// nothing to do here
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	// MenuListener implementation
	@Override
	public void menuCanceled(MenuEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void menuDeselected(MenuEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void menuSelected(MenuEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem srcMenuItem = (JMenuItem)e.getSource();
			UtilUI.showInfo(srcMenuItem.getText(), this);
		}
		
	}
	
	class AddAccountAction extends AbstractAction {
		AddAccountAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			addNewAccount(null);
		}
		
	}
	
	class CopyAccountAction extends AbstractAction {
		CopyAccountAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			copyAccount();
		}
		
	}
	
	class DeleteAccountAction extends AbstractAction {
		DeleteAccountAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			deleteAccount();
		}
		
	}
	
	class RefreshViewAction extends AbstractAction {
		RefreshViewAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			refreshView();
		}
		
	}
	
	class BackupAction extends AbstractAction {
		BackupAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			backup();
		}
	}
	
	class LoadDataAction extends AbstractAction {
		LoadDataAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			loadData();
		}
	}
	
	class ExitAction extends AbstractAction {
		ExitAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			exit();
		}
	}
	
	class DatedPerformanceAction extends AbstractAction {
		DatedPerformanceAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			doDatedPerformance();
		}
		
	}	
}  //  @jve:decl-index=0:visual-constraint="10,10"
