/**
 * 
 */
package com.basus.fins.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * @author sambit
 *
 */
public class UtilUI {
	static final Dimension APP_DIMENSION = UtilUI.getDefaultAppWindowSize();
	
	public static void showError(String error, Component parent) {
		PortfolioDialog dialog = new PortfolioDialog(error, PortfolioDialog.DIALOG_TYPE_ERROR, parent);
		centerComponent(dialog);
		dialog.setVisible(true);
		if (null != parent) {
			parent.requestFocus();
		}
	}
	
	public static void showInfo(String info, Component parent) {
		PortfolioDialog dialog = new PortfolioDialog(info, PortfolioDialog.DIALOG_TYPE_INFO, parent);
		centerComponent(dialog);
		dialog.setVisible(true);
		if (null != parent) {
			parent.requestFocus();
		}
	}
	
	/** Returns JOptionPane.YES_OPTION, JOptionPane.NO_OPTION or
	 *	JOptionPane.CANCLE_OPTION
	 */
	public static int confirm(String title, String message, Component parent) {
		int n = JOptionPane.showConfirmDialog(parent,
												message,
												title,
												JOptionPane.YES_NO_CANCEL_OPTION);

		return n;
	}
	
	// UI helper
	protected static void centerComponent(Component cmpt) {
		 //Get the screen size
		 Toolkit toolkit = Toolkit.getDefaultToolkit();
		 Dimension screenSize = toolkit.getScreenSize();
		 
		 //Calculate the frame location
		 int x = (screenSize.width - cmpt.getWidth()) / 2;
		 int y = (screenSize.height - cmpt.getHeight()) / 2;

		 //Set the new frame location330
		 cmpt.setLocation(x, y); 	
	}
	
	protected static Dimension getDefaultAppWindowSize() {
		//Get the screen size
		 Toolkit toolkit = Toolkit.getDefaultToolkit();
		 Dimension screenSize = toolkit.getScreenSize();
		 
		 //Calculate the frame location
		 double x = (screenSize.width) * 0.98;
		 double y = (screenSize.height) * 0.95;

		 Dimension dim = new Dimension();
		 dim.setSize(x, y);
		 return dim; 	
	}
}
