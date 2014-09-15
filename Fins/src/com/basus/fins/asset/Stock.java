/**
 * 
 */
package com.basus.fins.asset;

/**
 * @author sambit
 *
 * Object representation of a stock asset
 */
public class Stock extends Asset {
	private String symbol = "";
	
	public Stock(String symbol) {
		this.symbol = symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	
}
