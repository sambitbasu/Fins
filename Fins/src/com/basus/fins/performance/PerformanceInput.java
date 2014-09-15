/**
 * 
 */
package com.basus.fins.performance;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.basus.fins.account.Account;

import static com.basus.fins.PortfolioConstants.*;

/**
 * @author sambitb
 *
 */
public class PerformanceInput {
	private static PerformanceInput instance = new PerformanceInput();
	
	private PerformanceInput() {
		// no-arg private constructor
	}
	
	public static PerformanceInput.DatedPerformanceInput getDatedPerformanceInput() {
		return instance.new DatedPerformanceInput();
	}
	
	
	public class DatedPerformanceInput implements TypedPerformanceInput {
		LinkedHashSet<Account> accounts = new LinkedHashSet<Account>();
		Date startDate = null;
		Date endDate = null;
		String compSymbol = null;
		int type = TYPE_DATED_PERFORMANCE;
		
		public int getType() {
			return this.type;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#setAccounts(java.util.HashSet)
		 */
		public void setAccounts(LinkedHashSet<Account> accounts) {
			this.accounts = accounts;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#setAccount(com.basus.portfolio.account.Account)
		 */
		public void setAccount(Account account) {
			this.accounts.add(account);
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#getAccounts()
		 */
		public LinkedHashSet<Account> getAccounts() {
			return this.accounts;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#setStartDate(java.util.Date)
		 */
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#getStartDate()
		 */
		public Date getStartDate() {
			return this.startDate;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#setEndDate(java.util.Date)
		 */
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#getEndDate()
		 */
		public Date getEndDate() {
			return this.endDate;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#setComparingSymbol(java.lang.String)
		 */
		public void setComparingSymbol(String symbol) {
			this.compSymbol = symbol;
		}
		
		/* (non-Javadoc)
		 * @see com.basus.portfolio.performance.TypedPerformanceInput#getComparingSymbol()
		 */
		public String getComparingSymbol() {
			return this.compSymbol;
		}

		@Override
		public String getEndDateAsString() {
			String strEndDate = "";
			if (null != this.endDate) {
				strEndDate = DATE_FORMATTER.format(endDate);
			}
			
			return strEndDate;
		}

		@Override
		public String getStartDateAsString() {
			String strStartDate = "";
			if (null != this.startDate) {
				strStartDate = DATE_FORMATTER.format(startDate);
			}
			
			return strStartDate;
		}

		@Override
		public void setEndDate(String endDate) throws ParseException {
			this.endDate = DATE_FORMATTER.parse(endDate);
				
		}

		@Override
		public void setStartDate(String startDate) throws ParseException {
			this.startDate = DATE_FORMATTER.parse(startDate);
		}
	}
}
