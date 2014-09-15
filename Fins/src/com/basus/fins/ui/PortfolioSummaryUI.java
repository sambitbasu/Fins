/**
 * 
 */
package com.basus.fins.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
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
import java.util.LinkedHashSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;

import com.basus.fins.account.Account;
import com.basus.fins.account.AccountData;
import com.basus.fins.asset.Asset;
import com.basus.fins.asset.AssetData;
import com.basus.fins.data.Data;
import com.basus.fins.listener.AssetListener;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambit
 *
 */
public class PortfolioSummaryUI extends JDialog implements MouseListener, ChangeListener {
	private static final Logger log = Logger.getLogger(PortfolioSummaryUI.class);
	private static PortfolioSummaryUI instance = null;
	
	private PortfolioUI parent = null;
	
	private AssetTreeTable assetTreeTable = null;
	private LinkedHashSet<Account> accounts = Account.getAccounts();
	private LinkedHashSet<AssetData> allAssets = new LinkedHashSet<AssetData>(); 
	private AssetTableModel assetTableModel = null;
	private JTable jSummaryTable = null;
	private AccountSummaryTableModel summaryTableModel = null;
	

	private PortfolioSummaryUI(PortfolioUI parent) {
		this.refreshAssets();
		this.assetTableModel = new AssetTableModel(0, allAssets);
		this.summaryTableModel = new AccountSummaryTableModel(allAssets);
		this.parent = parent;
		//this.registerAssetListener();
	}
	
	public static PortfolioSummaryUI getInstance(PortfolioUI parent) {
		instance = new PortfolioSummaryUI(parent);
		return instance;
	}
	
	public static PortfolioSummaryUI getInstance() {
		if (null == instance) {
			instance = new PortfolioSummaryUI(null);
		}
		return instance;
	}
	
	private void refreshAssets() {
		accounts = Account.getAccounts();
		try {
			Iterator<Account> it = accounts.iterator();
			Account acc;
			allAssets.clear();
			while (it.hasNext()) {
				acc = it.next();
				if (WATCHLIST_ACCOUNT_ID == acc.getAccountId()) {
					continue;
				}
				allAssets.addAll(acc.getAssets());
			}
		} catch (ParseException e) {
			UtilUI.showError("An data parsing error occurred", this);
			log.error(e);
		} catch (SQLException e) {
			UtilUI.showError("An database error occurred", this);
			log.error(e);
		}
	}
	
	private void registerAssetListener() {
		Iterator<AssetData> it = allAssets.iterator();
		AssetData asset;
		while (it.hasNext()) {
			asset = it.next();
			asset.addAssetListener(this.assetTableModel);
			asset.addAssetListener(this.summaryTableModel);
		}
	}
	
	protected void resetTableModels() {
		this.assetTableModel.setAssets(allAssets);
		this.summaryTableModel.setAssets(allAssets);
	}
	
	protected AssetTableModel getAssetTableModel() {
		return assetTableModel;
	}
	
	protected AccountSummaryTableModel getSummaryTableModel() {
		return summaryTableModel;
	}
	
	protected void populateData() {
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
	
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1(Dimension parentDim) {
		JScrollPane jScrollPane = null;
		
		if (jScrollPane == null) {
			Dimension paneSize = new Dimension((int)(parentDim.getWidth()), 
					(int)(parentDim.getHeight() * 0.95));
			JXTreeTable jTable = getAssetTreeTable(paneSize);
			
			jScrollPane = new JScrollPane(jTable);
			
			jScrollPane.setPreferredSize(paneSize);
			//jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPane;
	}
	
	private JScrollPane getJScrollPane2(Dimension parentDim) {
		JScrollPane jScrollPane = null;
		
		if (jScrollPane == null) {
			Dimension paneSize = new Dimension((int)(parentDim.getWidth()), (int)(parentDim.getHeight() * 0.05));
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
	private JXTreeTable getAssetTreeTable(Dimension parentDim) {
		if (this.assetTreeTable  == null) {
			this.assetTreeTable = new AssetTreeTable(assetTableModel);
			
			assetTableModel.setTreeTable(this.assetTreeTable);
			Dimension tableDim = new Dimension(1500, 
					(int)(parentDim.getHeight() * 0.9));
			assetTreeTable.setSize(tableDim);
			
			assetTreeTable.addMouseListener(this);
		}
		return assetTreeTable;
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
					(int)(parentDim.getHeight() * 0.9));
			jSummaryTable.setSize(summaryTableDim);
			
			TableColumn column = null;
			for (int idx = 0; idx < 8; idx++) {
				column = jSummaryTable.getColumnModel().getColumn(idx);
			    switch(idx) {
			    case 0:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(150);
			    	column.setMaxWidth(300);
			    	break;
			    case 1:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(120);
			    	column.setMaxWidth(200);
			    	break;
			    case 2:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(60);
			    	column.setMaxWidth(100);
			    	break;
			    case 3:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(100);
			    	column.setMaxWidth(120);
			    	break;
			    default:
			    	column.setMinWidth(50);
			    	column.setPreferredWidth(100);
			    	column.setMaxWidth(100);
			    }
			}
			
			jSummaryTable.setShowGrid(true);
			jSummaryTable.addMouseListener(this);
		}
		return jSummaryTable;
	}
	
	/****************/
	public JSplitPane getUIContent(Dimension parentDimension) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		Dimension d = new Dimension();
		if (null != parentDimension) {
			d.setSize(parentDimension.getWidth() * 0.9, parentDimension.getHeight() * 0.9);
			//splitPane.setPreferredSize(d);
		}
		splitPane.setDividerLocation(0.4);
		//splitPane.setTopComponent(getAccountInfoPanel(d));
		splitPane.setBottomComponent(getAccountDetailPanel(parentDimension));
		
		return splitPane;
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
		
		acctDetailPanel.add(acctDetail);
		
		return acctDetailPanel;
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
			UtilUI.showInfo("Failed to update Summary", this);
		}
	}
		
	///////////////////
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
	
	// ChangeListener implementation
	@Override
	public void stateChanged(ChangeEvent event) {
		//this.updateAssetView();
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
