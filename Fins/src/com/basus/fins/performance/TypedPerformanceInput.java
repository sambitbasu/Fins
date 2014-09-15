package com.basus.fins.performance;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashSet;

import com.basus.fins.account.Account;

public interface TypedPerformanceInput {
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_DATED_PERFORMANCE = 1;
	
	public int getType();
	
	public void setAccounts(LinkedHashSet<Account> accounts);

	public void setAccount(Account account);

	public LinkedHashSet<Account> getAccounts();

	public void setStartDate(Date startDate);
	
	public void setStartDate(String startDate) throws ParseException;

	public Date getStartDate();
	
	public String getStartDateAsString();

	public void setEndDate(Date endDate);
	
	public void setEndDate(String endDate) throws ParseException;

	public Date getEndDate();
	
	public String getEndDateAsString();

	public void setComparingSymbol(String symbol);

	public String getComparingSymbol();

}