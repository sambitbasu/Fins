/**
 * 
 */
package com.basus.fins.data;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambitb
 *
 */
public class Quote extends LinkedHashMap<String, String> {
	private static Quote instance = new Quote();
	/**
	 * 
	 */
	private Quote() {
		
	}
	
	public void replaceWith(LinkedHashMap<String, String> table) {
		instance.clear();
		Set<String> set = table.keySet();
		Iterator<String> sIt = set.iterator();
		
		while (sIt.hasNext()) {
			String key = sIt.next();
			instance.put(key, table.get(key));
		}
	}
	
	public static Quote getInstance() {
		return instance;
	}
}
