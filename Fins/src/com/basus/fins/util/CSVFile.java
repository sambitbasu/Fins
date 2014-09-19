/*
 * CSVFile.java
 *
 * Created on June 22, 2007, 3:59 PM
 *
 * This file is copyrighted to the author as listed below. If no author is
 * specified, the file is copyrighted to Sambit Basu (sambitBasu@yahoo.com).
 * Unless otherwise specified, the contents of the file can be freely copied, 
 * modified and distributed under the terms of Lesser GNU Public License (LGPL).
 */

package com.basus.fins.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * This utility class is to parse and give access to a CSV file.
 *
 * @author sambit
 */
public class CSVFile {
    /**
     * Default delimiter
     */
    public static final String DEFAULT_DELIMITER = ",";    // default delimiter is comma (",")
    
    private File csv = null;
    private byte[] content = null;
    private int lineCnt = 0;
    private Vector<Line> vLines = new Vector<Line>();
    
    // a 2-D array representation of the CSV file
    private String[][] aCSV = null;
    private String delim = DEFAULT_DELIMITER;
    private BufferedReader reader = null;

	private boolean suppressHeaderLine = false;
    
	public CSVFile() {
		// no-arg constructor
		this.delim = DEFAULT_DELIMITER;
		this.suppressHeaderLine = false;
	}
	
	public CSVFile(String delimiter) {
		// no-arg constructor
		this.delim = delimiter;
		this.suppressHeaderLine = false;
	}
	
	public CSVFile(boolean suppressHeaderLine) {
		// no-arg constructor
		this.delim = DEFAULT_DELIMITER;
		this.suppressHeaderLine = suppressHeaderLine;
	}
	
    /**
     * Creates a new instance of CSVFile pointed by the <code>path</code>
     */
    public CSVFile(String path, boolean suppressHeaderLine) throws FileNotFoundException, IOException {
        this(path, DEFAULT_DELIMITER, suppressHeaderLine);
    }
    
    /**
     * Creates a new instance of CSVFile pointed by <code>file</code>
     */
    public CSVFile(File file, boolean suppressHeaderLine) throws FileNotFoundException, IOException {
        this(file, DEFAULT_DELIMITER, suppressHeaderLine);
    }
    
    /**
     * Creates a new instance of CSVFile pointed by <code>path</code> and 
     * delimited <code>delimiter</code>
     */
    public CSVFile(String path, String delimiter, boolean suppressHeaderLine) throws FileNotFoundException, IOException {
        this(new File(path), delimiter, suppressHeaderLine);
    }
    
    /**
     * Creates a new instance of CSVFile pointed by <code>file</code> and 
     * delimited <code>delimiter</code>
     */
    public CSVFile(File file, String delimiter, boolean suppressHeaderLine) throws FileNotFoundException, IOException {
        this.csv = file;
        this.delim = delimiter;
        this.suppressHeaderLine = suppressHeaderLine;
        reader = new BufferedReader(new FileReader(csv));
        this.init();
    }
    
    /**
     * Creates a new instance of CSVFile whose content is <code>content</code>
     */
    public CSVFile(byte[] content, boolean suppressHeaderLine) throws IOException {
        this(content, DEFAULT_DELIMITER, suppressHeaderLine);
    }
    
    /**
     * Creates a new instance of CSVFile whose content is <code>content</code> and 
     * delimited <code>delimiter</code>
     */
    public CSVFile(byte[] content, String delimiter, boolean suppressHeaderLine) throws IOException {
        this.content = content;
        this.delim = delimiter;
        this.suppressHeaderLine = suppressHeaderLine;
        reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
        this.init();
    }
    
    /**
     * Initializes the class
     */
    private void init() throws IOException {
        this.parse();
        this.makeArray();
        this.stripQuotes();
    }
    
    /**
     * Parses the file
     */
    private void parse() throws IOException { 
    	String rawLine = null;
    	while (null != (rawLine = reader.readLine())) {
	        Line line = new Line(rawLine, delim);
	        vLines.add(line);
    	}
    }
    
    /**
     * Creates an array representation of the CSV file
     */
    private void makeArray() {
    	String[] dummy = { };
        String[] lineArray = null;
        int lineCnt = vLines.size();
        int count = 0;
        
        if (this.suppressHeaderLine) {
        	count = 1;
        }
        
        aCSV = new String[lineCnt - count][];
        int csvCount = 0;
        for (; count < lineCnt; count++) {
        	Line l = vLines.elementAt(count);
            lineArray = l.toArray(dummy);
            aCSV[csvCount++] = lineArray;
        }
    }
    
    /**
     * Strips of all quotes
     */
    private void stripQuotes() {
    	String cell;
    	for (String[] line : aCSV) {
    		for (int cnt = 0; cnt < line.length; cnt++) {
    			cell = line[cnt];
    			if (null == cell) {
    				continue;
    			}
    			line[cnt] = cell.replace("\"", "");
    		}
    	}
    }
    
    public void setContent(byte[] content) throws IOException {
    	reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
    	this.init();
    }
    
    public void setDilimiter(String delimiter) throws IOException {
    	this.delim = delimiter;
    	this.init();
    }
    
    public void setSuppressHeaderLine(boolean suppress) throws IOException {
    	this.suppressHeaderLine = suppress;
    	this.init();
    }
    
    /**
     * Returns Hastable representation keyed on the 0-the (first) column.
     * @return hashtable
     */
    public LinkedHashMap<?, ?> toHashtable() {
    	return HelperUtil.array2Hashmap(aCSV, 0);
    }
    
    /**
     * Returns a 2-D array representation of the CSV file
     */
    public String[][] toArray() {
    	return aCSV;
    }
    
    /**
     * Gets a particular element of the file
     */
    public String getElement(int line, int field) {
        return (String)vLines.elementAt(line).elementAt(field);
    }
    
    /**
     * Returns the number of lines in the CSV file
     *
     * @returns Number of lines
     */
    public int getLineCount() {
        return vLines.size();
    }

    /**
     * Returns the number for the line <code>lineNumber</code> in the CSV file.
     *
     * @returns Field count
     */
    public int getFieldCount(int lineNumber) {
        Line line = (Line)vLines.elementAt(lineNumber);
        if (line != null) {
            return line.size();
        } 
        
        return 0;
    }
    
    public String getContent() {
    	return new String(content);
    }
    
    public String toString() {
    	if (null == aCSV) {
    		return "";
    	}
    	
    	StringBuilder out = new StringBuilder("");
    	
    	for (int row = 0; row < aCSV.length; row++) {
    		for (int col = 0; col < aCSV[row].length; col++) {
				 out.append(aCSV[row][col]).append(delim);
    		}
    		out.append(System.lineSeparator());
    	}
    	
    	return out.toString();
    }
    
    /**
     * Represents a line of the file
     */
    private class Line extends Vector<String> {
        Line(String line, String delimiter) throws IOException {
            if (null == line) {
                throw new IOException("null input line in CSV");
            }
            
            if (delimiter == null) {
                throw new IOException("null delimiter line in CSV");              
            }
            
            String[] elems = line.split(delimiter);
            
            for (int count = 0; count < elems.length; count++) {
                this.add(elems[count]);
            }
        }
    }
}
