/* $Id: Configuration.java,v 1.10 2008/10/24 23:37:21 sambitbasu Exp $ */

/*************************************************************************
*    This file is part of Bongolipi (http://bongolipi.sourceforge.net/)
*
*    Bongolipi is free software: you can redistribute it and/or modify
*    it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as 
*    published by the Free Software Foundation, either version 3 of 
*    the License, or (at your option) any later version.
*
*    Bongolipi is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
*    along with Bongolipi.  If not, see 
*    http://www.gnu.org/licenses/lgpl-3.0.txt.
**************************************************************************/

package com.basus.fins.util;

import com.basus.fins.PortfolioConstants;
import com.basus.fins.PortfolioException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class Configuration extends Properties {
	// static member
	//private static Properties prop = new Properties();
	private static ClassLoader loader = Configuration.class.getClassLoader();
	private static Hashtable<String, Configuration> configs = new Hashtable<String, Configuration>();
	
	// logging
	private static final Logger log = Logger.getLogger(Configuration.class);

	// member
	private String CONFIGURATION_FILE_NAME = ".portfolio.properties";
	private URL url = null;
	private String path = "";
	
	/**
	 * Loads a coniguration file named <code>config file</code>
	 *
	 * @param configFile Configuration file to load. Should have a properties 
	 * name=value file structure.
	 */ 
	private Configuration(String configFile) {
		CONFIGURATION_FILE_NAME = configFile;
		
		load();
	}
	
	private Configuration(Properties prop) {
		super(prop);	
	}
	
	private void load() {
		InputStream is = null;
		
		try {
			loader = Configuration.class.getClassLoader();
			url = loader.getResource(CONFIGURATION_FILE_NAME);
			path = new File(url.toURI()).getParent();
			if (null == path) {
				path = new File(url.toURI()).getAbsolutePath();
			}
			
			log.info("Loading properties file from " + url);
			
			is = url.openStream();
			this.load(is);
		}
		catch (FileNotFoundException fnfEx) {
			log.error("Cannot find configuration file " 
					  + url.toString() 
					  + ". Application cannot be started (" 
					  + fnfEx.getMessage() 
					  + ")");
		}
		catch (Exception ex) {
			log.error("Cannot load configuration file " 
					 + CONFIGURATION_FILE_NAME 
					 + " Reason: " 
					 + ex.getMessage());
		}
		finally {
			if (is != null) {
				try {
					is.close();
				} 
				catch (Exception ignored) {
					// throw away
				}
			}
		}
	}
	
	public void reload() {
		if (log.isInfoEnabled()) {
			log.info("reloading " + CONFIGURATION_FILE_NAME);
		}
		
		this.load();
	}
	
	public static Configuration getConfig(String configFile) {
		Configuration config = configs.get(configFile);
		
		if (null == config) {
			config = new Configuration(configFile);
			configs.put(configFile, config);
		}
		
		return config;
	}
	
	
	/**
	 * Returns the path to the directory where this configuraion files resides
	 */ 
	public String getConfigurationPath() {
		return path;	
	}
	
	/**
	 * Returns the name of the config file that represents this COnfiguration 
	 * object
	 */
	public String getConfigFileName() {
		return CONFIGURATION_FILE_NAME;	
	}
	
	/**
	 * This is implementtion of the same-named method of Properties which 
	 * is introduced in JSDK 1.6
	 */
	public static Set<String> stringPropertyNames(Properties p) {
		if (null == p) {
			return null;	
		}
		
		Enumeration names = p.propertyNames();
		HashSet<String> set = new HashSet<String>();
		
		while (names.hasMoreElements()) {
			set.add(names.nextElement().toString());
		}
		
		return set;
	}
	
	/**
	 * Dumps the content of the key-value pair to system out and to DEBUG log.
	 */
	public String dumpableString() {
		Enumeration<String> keys = (Enumeration<String>)this.propertyNames();
		String k, v;
		StringBuilder bufStr = new StringBuilder();
		
		while (keys.hasMoreElements()) {
			k = keys.nextElement();
			bufStr.append("\n")
				.append(k).append("=").append(this.getProperty(k));
		}
		
		return bufStr.toString();
	}
}