/**
 * 
 */
package com.basus.fins.util;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

import com.basus.fins.ui.UtilUI;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambitb
 *
 */
public class HelperUtil {
	private static final DecimalFormat TWO_DIM_FORM = new DecimalFormat("#.##");
	//private static final NumberFormat PERCENT_FORM = DecimalFormat.getPercentInstance();
	private static final NumberFormat PERCENT_FORM = new DecimalFormat("#.##%");
	
	static {
		TWO_DIM_FORM.setMaximumFractionDigits(2);
		TWO_DIM_FORM.setMinimumFractionDigits(2);
	}
	
	
	/**
	 * Returns Formatted String of current date-time
	 * @return String in MM/dd/yyyy HH:mm:ss format
	 */
	public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

	/**
	 * Creates a LinkedHashMap out of a 2-dimensional array where the key of the LinkedHashMap 
	 * is value of arr[n][keyCol] and value is arr[n]. The number of Key-Value pair can be less 
	 * than the number of rows of the array if some of the key's are repeated.
	 * @param arr
	 * @param keyCol 
	 * @return A LinkedHashMap whose Key is a String and Value is a String[]
	 * @throws NullPointerException If one of arr[n][keyCol] is null
	 */
	public static LinkedHashMap<String, String[]> array2Hashmap(String[][] arr, int keyCol) throws NullPointerException {
		if (null == arr || arr.length == 0) {
			return null;
		}
		
		LinkedHashMap<String, String[]> table = new LinkedHashMap<String, String[]>();
		for (int rCnt = 0; rCnt < arr.length; rCnt++) {
			if (null == arr[rCnt][keyCol]) {
				throw new NullPointerException("Key cannot be null");
			}
			
			table.put(arr[rCnt][keyCol], arr[rCnt]);
		}
		
		return table;
	}

	/**
	 * Creates a Hashtable out of a 2-dimensional array where the key of the Hashtable 
	 * is value of arr[n][keyCol] and value is arr[n][valCol]. The number of Key-Value pair can be less 
	 * than the number of rows of the array if some of the key's are repeated.
	 * @param arr
	 * @param keyCol Column number (0-based) whose value will be used as the key of the Hashtable
	 * @param valCol Column number (0-based) whose value will be the value of the Hashtable
	 * @return A Hastable whose Key is a String and Value is a String
	 * @throws NullPointerException If one of arr[n][keyCol] is null
	 */
	public static LinkedHashMap<String, String> array2Hashmap(String[][] arr, int keyCol, int valCol) throws NullPointerException {
		if (null == arr || arr.length == 0) {
			return null;
		}
		
		LinkedHashMap<String, String> table = new LinkedHashMap<String, String>();
		String val = null;
		for (int rCnt = 0; rCnt < arr.length; rCnt++) {
			if (null == arr[rCnt][keyCol]) {
				throw new NullPointerException("Key cannot be null");
			}
			
			val = arr[rCnt][valCol];
			
			table.put(arr[rCnt][keyCol], val);
		}
		
		return table;
	}

	/**
	 * Appends a String to an array by allocating new array and discarding the old
	 * @param array Original Array
	 * @param s String to append
	 * @return New array with appended String
	 */
	public static String[] appendToArray(String[] array, String s) {
		Vector<String> v = new Vector<String>();
		for (String elem : array) {
			v.add(elem);
		}	
		v.add(s);
		String[] dummy = new String[1];
		return v.toArray(dummy);
	}
	
	/**
	 * Rounds off to 2-decimal place
	 * @param d
	 * @return String representation of Rounded off value
	 */
	public static String roundTwoDecimals(double d) {
		return TWO_DIM_FORM.format(d);
	}
	
	/**
	 * Retruns percentage form
	 * @param d
	 * @return String representation as percentage
	 */
	public static String getPercent(double d) {
		return PERCENT_FORM.format(d);
	}
	
	public static void showSymbolInBrowser(String symbol) {
		String url = YAHOO_QUOTE_PAGE_URL_PREFIX + symbol + YAHOO_QUOTE_PAGE_URL_SUFFIX;
		launchUrl(url);
	}
	
	/**
	 * Launches <code>url</code> is default browser
	 * @param url
	 */
	private static void launchUrl(final String url) {
		String osName = System.getProperty("os.name");
		final String errMsg = "Error attempting to launch web browser";
		try {
			if (osName.startsWith("Mac OS")) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method launchUrl = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
				launchUrl.invoke(null, new Object[] {url});
			}
			else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
			else {
				//assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]}).waitFor() == 0)
						browser = browsers[count];
				if (browser == null)
					throw new Exception("Could not find web browser");
				else
					Runtime.getRuntime().exec(new String[] {browser, url});
				}
		} catch (Exception e) {
			UtilUI.showError(errMsg + ":\n" + e.getLocalizedMessage(), null);
		}
	}
}
