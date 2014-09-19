/*
 * Main.java
 *
 * Created on June 22, 2007, 3:50 PM
 *
 * This file is copyrighted to the author as listed below. If no author is
 * specified, the file is copyrighted to Sambit Basu (sambitBasu@yahoo.com).
 * Unless otherwise specified, the contents of the file can be freely copied, 
 * modified and distributed under the terms of Lesser GNU Public License (LGPL).
 */

package com.basus.fins;


import com.basus.fins.data.YahooQuote;
import com.basus.fins.ui.PortfolioUI;
import com.basus.fins.ui.UtilUI;
import com.basus.fins.util.CSVFile;
import com.basus.fins.work.WorkQueue;
import com.basus.fins.work.Worker;

import java.io.IOException;

import javax.swing.UIManager;

import org.jdesktop.swingx.JXErrorPane;

/**
 *
 * @author sambit
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) {
        // TODO code application logic here
        YahooQuote yquote = new YahooQuote();
        CSVFile quote = null;
        try {
            quote = yquote.getQuote("BWLD");
        } catch (IOException ex) {
            // add logging
        }
        
        for (int count = 0; count < quote.getLineCount(); count++) {
            System.out.println("Line: " + count);
            int fieldCnt = quote.getFieldCount(count);
            for (int inCnt = 0; inCnt < fieldCnt; inCnt++) {
                System.out.println("Field " + inCnt + ": " + quote.getElement(count, inCnt));
            }
        }
    }
    */
    
    public static void main(String[] args) {
    	//PortfolioUI ui = new PortfolioUI();
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			UtilUI.showInfo("Couldn't get Look-and-Feel", null);
		}
    	PortfolioUI.boot();
    	Worker quoteWorker = new Worker((WorkQueue<Runnable>)WorkQueue.getCurrentQuoteQueue());
    	Worker indexWorker = new Worker((WorkQueue<Runnable>)WorkQueue.getHistoricQuoteQueue());
    	Worker status1Worker = new Worker((WorkQueue<Runnable>)WorkQueue.getStatus1Queue());
    	Worker status2Worker = new Worker((WorkQueue<Runnable>)WorkQueue.getStatus2Queue());
    	Worker task1Worker = new Worker((WorkQueue<Runnable>)WorkQueue.getTask1Queue());
    	Worker task2Worker = new Worker((WorkQueue<Runnable>)WorkQueue.getTask2Queue());
    }
    
}
