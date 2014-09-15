/**
 * 
 */
package com.basus.fins.util;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * @author sambit
 *
 */
public class HistoricCSV extends CSVFile {
	private String symbol = null;
	
	public HistoricCSV(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	public HistoricCSV(String symbol, boolean suppressHeaderLine) {
		super(suppressHeaderLine);
		this.symbol = symbol;
	}
	
	public HistoricCSV(String symbol, byte[] content, boolean suppressHeaderLine) throws IOException {
		super(content, suppressHeaderLine);
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	
	public LinkedHashMap<?, ?> toHashtable() {
		return HelperUtil.array2Hashmap(this.toArray(), 0, 6);
	}
}
