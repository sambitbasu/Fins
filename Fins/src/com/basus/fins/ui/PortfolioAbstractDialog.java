
package com.basus.fins.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JRootPane;

public abstract class PortfolioAbstractDialog extends JPanel implements KeyListener
{		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final JFrame frame = new JFrame();
	JButton m_jbutton1 = new JButton();

   /**
    * Default constructor
    */
   public PortfolioAbstractDialog()
   {
	   // empty
   }

   public void show()
   {
	  initializePanel();
	  this.addKeyListener(this);
	  frame.setLocation(100, 100);
	  //frame.setIconImage(Toolkit.getDefaultToolkit().getImage(FileUtility.getFileUrl(APP_ICON_HUGE)));
      frame.getContentPane().add(this);
	  //BtransEditor.centerComponent(frame);
      frame.setVisible(true);

      frame.addWindowListener( new WindowAdapter()
      {
         public void windowClosing( WindowEvent evt)
         {
			 close();
         }
      });
   }
   
   public void setVisible(boolean b) {
	   if (b) {
		   this.show();
	   }
   }
   
   protected void setTitle(String title) {
	   frame.setTitle(title);   
   }
   
   public void setSize(int width, int height) {
	   frame.setSize(width, height);   
   }
   
   /*
   public void setVisible(boolean b) {
	   frame.setVisible(b);
   }
   */
    
   protected void close() {
		frame.dispose();   
   }
   
   public JRootPane getRootPane() {
	   return frame.getRootPane();
   }
   
   abstract public Component createComponent();
   
   /**
    * Initializer
    */
   protected void initializePanel()
   {
      setLayout(new BorderLayout());
      this.add(createComponent(), BorderLayout.CENTER);
   }
   
   // keyListener implementation
   public void keyTyped(KeyEvent e) {
	   // empty
   }
   
   public void keyPressed(KeyEvent e) {
	   // empty
   }
   
   public void keyReleased(KeyEvent e) {
	   // empty
   }
}
