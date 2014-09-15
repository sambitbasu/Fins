/**
 * 
 */
package com.basus.fins.util;

import static com.basus.fins.PortfolioConstants.BACKUP_FILE_NAME;
import static com.basus.fins.PortfolioConstants.DB_DIR;
import static com.basus.fins.PortfolioConstants.DB_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.basus.fins.data.Data;

/**
 * @author sambit
 *
 */
public class DataUtil {
	private static final Logger log = Logger.getLogger(DataUtil.class);
	public static String backupDb(File destDir) throws IOException, SQLException {
		Data.closeDb();
		File source = new File(DB_DIR + File.separator + DB_NAME);
		compressAndSave(source, destDir, BACKUP_FILE_NAME);
		
		return (destDir.getAbsolutePath() + File.separator + BACKUP_FILE_NAME);
	}
	
	public static String loadDb(File source) throws IOException, SQLException {
		Data.closeDb();
		File dest = new File(DB_DIR + File.separator + DB_NAME);
		loadAndUncompress(source, dest);
		
		return (DB_DIR + File.separator + DB_NAME);
	}
	
	public static void compressAndSave(File source, File destDir, String destFile) throws IOException {
		if (null == source || null == destDir) {
			throw new NullPointerException("Source or destination is null");
		}
		
		if (!destDir.isDirectory()) {
			throw new IllegalArgumentException("Destination must be a directory");
		}
		
		try {
			File outFile = new File(destDir.getAbsolutePath(), destFile);
			FileOutputStream out = new FileOutputStream(outFile);
			ZipOutputStream zip = new ZipOutputStream(out);
			
			DataUtil util = new DataUtil();
			util.createZip(source, zip, source.getAbsolutePath());
			
			zip.finish();
			zip.close();
		}
		catch(FileNotFoundException fnfEx) {
			log.error(fnfEx);
			throw new IllegalArgumentException("Wrong source or destination");
		}
	}
	
	public static void loadAndUncompress(File sourceZip, File destDir) throws IOException {
		ZipFile zip = new ZipFile(sourceZip, ZipFile.OPEN_READ);
		Enumeration<ZipEntry> en = (Enumeration<ZipEntry>)zip.entries();
		byte[] buf = new byte[2048];
		
		while (en.hasMoreElements()) {
			ZipEntry entry = en.nextElement();
			File f = new File(destDir + File.separator + entry.getName());
			if (entry.getName().endsWith("/")) {
				f.mkdirs();
			}
			
			File parent = f.getParentFile();
			if (null != parent) {
				parent.mkdirs();
			}
				
			InputStream in = zip.getInputStream(entry);
			FileOutputStream fOut = new FileOutputStream(f);
			
			
			int read = 0;
			while (-1 != (read = in.read(buf))) {
				fOut.write(buf, 0, read);
			}
			
			if (log.isDebugEnabled()) {
				log.debug("wrote: " + f.getAbsolutePath());
			}
			fOut.close();
			in.close();
		}
		
		zip.close();
	}
	
	private void createZip(File source, ZipOutputStream zip, String rootPath) throws FileNotFoundException, IOException {
		if (source.isDirectory()) {
			File[] files = source.listFiles();
			for (File f : files) {
				createZip(f, zip, rootPath);
			}
		}
		else {
			String relative = new File(rootPath).toURI().relativize(new File(source.getAbsolutePath()).toURI()).getPath();
			ZipEntry entry = new ZipEntry(relative);

			zip.putNextEntry(entry);
			FileInputStream in = new FileInputStream(source);
			byte[] buf = new byte[2048];
			int read = 0;
			
			while (-1 != (read = in.read(buf))) {
				zip.write(buf, 0, read);
			}
		}
	}
}
