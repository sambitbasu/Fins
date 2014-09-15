/**
 * 
 */
package com.basus.fins.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Iterator;
import java.util.Map;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FontHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;

import com.basus.fins.asset.AssetData;
import com.basus.fins.ui.AssetTableModel.AssetClass;
import com.basus.fins.ui.AssetTableModel.AssetClassTreeTableNode;
import com.basus.fins.ui.AssetTableModel.AssetTreeTableNode;
import com.basus.fins.util.HelperUtil;

/**
 * @author sambitb
 *
 */
public class AssetTreeTable extends JXTreeTable implements MouseListener {
	private static Logger log = Logger.getLogger(AssetTreeTable.class);
	
	/**
	 * 
	 */
	public AssetTreeTable() {
		super();
		init();
	}

	/**
	 * @param treeModel
	 */
	public AssetTreeTable(TreeTableModel treeModel) {
		super(treeModel);
		init();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	@Override
	public boolean isSortable() {
		return true;
	}

	@Override
	protected boolean isSortable(int columnIndex) {
		return true;
	}

	@Override
	public void toggleSortOrder(int columnIndex) {
		log.debug("toggle sort order " + columnIndex);
		log.debug(getTableHeader().getClass()
				.getName());
		super.toggleSortOrder(columnIndex);
	}

	@Override
	public void resetSortOrder() {
		log.debug("reset sort order");
		super.resetSortOrder();
	}
	
	private void init() {
		this.setColumnWidth();
		this.setSortable(true);
		this.setAutoCreateRowSorter(true);
		this.setShowGrid(true);
		this.addMouseListener(this);
		
		Font font = this.getFont();
		Map<TextAttribute, Boolean>  attributes = (Map)font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		Font newFont = new Font(attributes);

		
		// add a highlighter, pretty.
		this.addHighlighter(HighlighterFactory.createAlternateStriping());
		this.addHighlighter(new ColorHighlighter(new PositiveHighlightPredicate(), null, new Color(64, 155, 96)));
		this.addHighlighter(new ColorHighlighter(new NegetiveHighlightPredicate(), null, Color.RED));
		this.addHighlighter(new ColorHighlighter(new UrlHighlightPredicate(), null, Color.BLUE));
		this.addHighlighter(new FontHighlighter(new StrikethroughHighlightPredicate(), newFont));
		//this.addHighlighter(new AssetValueHighlighter());
		//this.setTreeCellRenderer(this.getCellRenderer());
	}
	
	private void setColumnWidth() {
		TableColumn column = null;
		for (int idx = 0; idx < 14; idx++) {
			column = this.getColumnModel().getColumn(idx);
		    switch(idx) {
		    case 0:		// Name
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(160);
		    	column.setMaxWidth(300);
		    	break;
		    case 1:		// Symbol
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(120);
		    	column.setMaxWidth(200);
		    	break;
		    case 2:		// Quantity
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(60);
		    	column.setMaxWidth(100);
		    	break;
		    case 3:		// Acquire Price
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(60);
		    	column.setMaxWidth(120);
		    	break;
		    case 4:		// Discharge Price
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(60);
		    	column.setMaxWidth(100);
		    	break;
		    case 5:		// Cost Basis
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(120);
		    	column.setMaxWidth(100);
		    	break;
		    case 6:		// Proceed
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(120);
		    	column.setMaxWidth(100);
		    	break;
		    case 7:		// Profit
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(120);
		    	column.setMaxWidth(100);
		    	break;
		    case 8:		// Index @ Acquire
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(100);
		    	column.setMaxWidth(120);
		    	break;
		    case 9:		// Index @ Discharge
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(100);
		    	column.setMaxWidth(100);
		    	break;
		    case 10:	// Current Price
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(100);
		    	column.setMaxWidth(100);
		    	break;
		    case 11:	// Asset Gain %-age
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(80);
		    	column.setMaxWidth(100);
		    	break;
		    case 12:	// Index Gain %-age
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(80);
		    	column.setMaxWidth(100);
		    	break;
		    case 13:	// Asset Gain vs Index Gain %-age
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(80);
		    	column.setMaxWidth(100);
		    	break;
		    default:
		    	column.setMinWidth(50);
		    	column.setPreferredWidth(100);
		    	column.setMaxWidth(100);
		    }
		}
	}
		
	private Object getNodeForRow(int row) {
		TreePath path = this.getPathForRow(row);
		Object node = path.getLastPathComponent();
		return node;
	}
	
	private class NegetiveHighlightPredicate implements HighlightPredicate {
		public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
			int rowIndex = adapter.row;
			int colIndex = adapter.column;
			boolean isHighlighted = false;
			
			if (!(7 == colIndex || (colIndex <= 13 && colIndex >= 11))) {
				return isHighlighted;
			}
			
			DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)getNodeForRow(rowIndex);
			if (node instanceof AssetClassTreeTableNode) {
				AssetClassTreeTableNode assetClassNode = (AssetClassTreeTableNode) node;
				String val;
				val = assetClassNode.getValueAt(colIndex);
				if (val.startsWith("-")) {
					isHighlighted = true;
				}
			}
			else if (node instanceof AssetTreeTableNode) {
				AssetTreeTableNode assetNode = (AssetTreeTableNode) node;
				String val;
				val = assetNode.getValueAt(colIndex);
				if (val.startsWith("-")) {
					isHighlighted = true;
				}
			}
			
			return isHighlighted;
		}
	}
	
	private class PositiveHighlightPredicate implements HighlightPredicate {
		public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
			int rowIndex = adapter.row;
			int colIndex = adapter.column;
			boolean isHighlighted = false;
			
			if (!(7 == colIndex || (colIndex <= 13 && colIndex >= 11))) {
				return isHighlighted;
			}
			
			DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)getNodeForRow(rowIndex);
			if (node instanceof AssetClassTreeTableNode) {
				AssetClassTreeTableNode assetClassNode = (AssetClassTreeTableNode) node;
				String val;
				val = assetClassNode.getValueAt(colIndex);
				if (!val.startsWith("-")) {
					isHighlighted = true;
				}
			}
			else if (node instanceof AssetTreeTableNode) {
				AssetTreeTableNode assetNode = (AssetTreeTableNode) node;
				String val;
				val = assetNode.getValueAt(colIndex);
				if (!val.startsWith("-")) {
					isHighlighted = true;
				}
			}
			
			return isHighlighted;
		}
	}
	
	private class StrikethroughHighlightPredicate implements HighlightPredicate {
		public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
			int rowIndex = adapter.row;
			int colIndex = adapter.column;
			boolean isHighlighted = false;
			if (colIndex != 2) {
				return isHighlighted;
			}
			
			DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)getNodeForRow(rowIndex);
			if (node instanceof AssetClassTreeTableNode) {
				AssetClassTreeTableNode assetClassNode = (AssetClassTreeTableNode) node;
				AssetClass assetClass = assetClassNode.getAssetClass();
				Iterator<AssetData> it = assetClass.iterator();
				isHighlighted = true;
				while (it.hasNext() && isHighlighted) {
					isHighlighted = isHighlighted && it.next().isSold();
				}

			}
			else if (node instanceof AssetTreeTableNode) {
				AssetTreeTableNode assetNode = (AssetTreeTableNode) node;
				AssetData asset = assetNode.getAsset();
				isHighlighted = asset.isSold(); 
			}
			
			return isHighlighted;
		}
	}
	
	private class UrlHighlightPredicate implements HighlightPredicate {
		public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
			int rowIndex = adapter.row;
			int colIndex = adapter.column;
			boolean isHighlighted = false;
			if (colIndex == 1) {
				isHighlighted = true;
			}
			
			return isHighlighted;
		}
	}
	
	
	///// MouseListener implemenation
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			mouseSingleClicked(e);
		}
		else if (e.getClickCount() == 2) {
			mouseDoubleClicked(e);
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
	
	/////////////// MouseListener Helper ////////////////////////
	private void mouseSingleClicked(MouseEvent e) {
		int[] rows = this.getSelectedRows();
		int[] cols = this.getSelectedColumns();
		if (rows.length != 1 || cols.length != 1) {
			return;
		}
		
		int row = rows[0];
		int col = cols[0];
		if (col != 1) {
			return;
		}
		
		String symbol = "";
		TreePath path = this.getPathForRow(row);
		DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)getNodeForRow(row);
		if (node instanceof AssetClassTreeTableNode) {
			AssetClassTreeTableNode assetClassNode = (AssetClassTreeTableNode)node;
			symbol = assetClassNode.getAssetClass().getSymbol();
		}
		else if (node instanceof AssetTreeTableNode) {
			AssetTreeTableNode assetNode = (AssetTreeTableNode)node;
			symbol = assetNode.getAsset().getSymbol();
		}
		
		// this is kind of a hack to unselect the colunm selected previously
		this.setColumnSelectionInterval(0, 0);
		log.debug("ShowInBrowser: " + symbol);
		HelperUtil.showSymbolInBrowser(symbol);
	}
	
	private void mouseDoubleClicked(MouseEvent e) {
		int[] rows = this.getSelectedRows();
		if (rows.length != 1) {
			return;
		}
		
		int row = rows[0];
		
		TreePath path = this.getPathForRow(row);
		DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode)getNodeForRow(row);
		if (node instanceof AssetClassTreeTableNode) {
			if (this.isExpanded(row)) {
				this.collapseRow(row);
			}
			else { 
				this.expandRow(row);
			}
		}
	}
	
}