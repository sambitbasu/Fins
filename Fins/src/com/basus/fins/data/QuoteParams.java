package com.basus.fins.data;

interface QuoteParams {
	String getHostUri();
    String getPath();
    String getSymbolParam();
    String getQuerystringConstant();
    String getStartDateParam();
    String getStartMonthParam();
    String getStartYearParam();
    String getEndDateParam();
    String getEndMonthParam();
    String getEndYearParam();
    String createQueryString();
    boolean isValidParams();
}