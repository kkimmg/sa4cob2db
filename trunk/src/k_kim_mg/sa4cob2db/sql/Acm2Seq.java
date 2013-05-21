package k_kim_mg.sa4cob2db.sql;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.FileStatus;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;
import org.xml.sax.SAXException;
/**
 * create sequecial file
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class Acm2Seq {
	/**
	 * display usage
	 * 
	 * @param properties properties
	 */
	private static void displayUsage(Properties properties) {
		String flag = properties.getProperty("display_usage", "true");
		if (Boolean.parseBoolean(flag)) {
			System.err.println("java -classpath path_to_jdbc:path_to_acm \"k_kim_mg.sa4cob2db.sql.Acm2Seq\" acmfile outputfile metfile lineout(true/false) sql sqlin(true/false) display_usage");
			java.util.Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				String val = properties.getProperty(key);
				System.err.println(key + " = " + val);
			}
		}
	}
	/**
	 * get environment values
	 * 
	 * @param key env key
	 * @param defaultValue default value
	 * @return value
	 */
	private static String getEnvValue(String key, String defaultValue) {
		String ret = System.getProperty(key, System.getenv(key));
		if (ret == null)
			ret = defaultValue;
		if (ret.length() == 0)
			ret = defaultValue;
		return ret;
	}
	/**
	 * main
	 * 
	 * @param args arguments
	 */
	public static void main(String[] args) {
		Properties properties = new Properties();
		// -------------------------
		properties.setProperty("lineout", getEnvValue("lineout", "false"));
		properties.setProperty("metafile", getEnvValue("metafile", SQLNetServer.DEFAULT_CONFIG));
		properties.setProperty("sql", getEnvValue("sql", ""));
		properties.setProperty("sqlin", getEnvValue("sqlin", "false"));
		properties.setProperty("display_usage", getEnvValue("display_usage", "false"));
		if (args.length >= 1) {
			properties.setProperty("acmfile", args[0]);
			if (args.length >= 2) {
				properties.setProperty("outfile", args[1]);
				if (args.length >= 3) {
					properties.setProperty("metafile", args[2]);
					if (args.length >= 4) {
						properties.setProperty("lineout", args[3]);
						if (args.length >= 5) {
							properties.setProperty("sql", args[4]);
							if (args.length >= 6) {
								properties.setProperty("sqlin", args[5]);
								if (args.length >= 7) {
									properties.setProperty("display_usage", args[6]);
								}
							}
						}
					}
				}
			}
		} else {
			System.err.println("needs acmfile.");
		}
		// -------------------------
		Acm2Seq obj = new Acm2Seq();
		obj.exportTo(properties);
		// display
		displayUsage(properties);
	}
	/**
	 * main
	 * 
	 * @param acmfile input filename
	 * @param outfile output file
	 * @param metafile meta data
	 * @param lineout it's line seq?/true or false
	 * @param display_usage display usage?/true or false
	 */
	public static void main_too(String acmfile, String outfile, String metafile, String lineout, String sql, String sqlin, String display_usage) {
		Acm2Seq.main(new String[] { acmfile, outfile, metafile, lineout, sql, sqlin, display_usage });
	}
	/** JDBC connection */
	private Connection connection;
	private SQLFileServer fileServer;
	/**
	 * export
	 * 
	 * @param file COBOL file
	 * @param stream stream
	 * @param line line out
	 * @throws IOException exception
	 */
	protected void exportTo(CobolFile file, OutputStream stream, boolean line) throws IOException {
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		byte[] record = new byte[recsize];
		FileStatus stat = file.read(record);
		while (stat.getStatusCode() == FileStatus.STATUS_SUCCESS) {
			stream.write(record);
			if (line) {
				// line sequencial
				stream.write('\n');
			}
			count++;
			// next
			stat = file.next();
			if (stat.getStatusCode() == FileStatus.STATUS_SUCCESS) {
				stat = file.read(record);
			} else if (stat.getStatusCode() != FileStatus.STATUS_SUCCESS && stat.getStatusCode() != FileStatus.STATUS_END_OF_FILE) {
				// do nothing
			}
		}
		stream.flush();
		System.err.println("Row Count = " + count);
	}
	/**
	 * export
	 * 
	 * @param properties key-value set
	 */
	protected void exportTo(Properties properties) {
		fileServer = new SQLFileServer();
		CobolRecordMetaDataSet metaset = fileServer.getMetaDataSet();
		String metaString = properties.getProperty("metafile", SQLNetServer.DEFAULT_CONFIG);
		String acmName = properties.getProperty("acmfile", "");
		String outName = properties.getProperty("outfile", "");
		String sql = properties.getProperty("sql", "");
		String sqlin = properties.getProperty("sqlin", "false");
		String lineOut = properties.getProperty("lineout", "false");
		File metaFile = new File(metaString);
		NodeReadLoader nodeLoader = new NodeReadLoader();
		CobolFile acmFile = null;
		OutputStream fos = null;
		try {
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
			if (metaset instanceof SQLCobolRecordMetaDataSet) {
				SQLCobolRecordMetaDataSet sqlset = (SQLCobolRecordMetaDataSet) metaset;
				SQLNetServer.updateProperty(properties, "jdbcdriverurl", "ACM_JDBCDRIVERURL");
				SQLNetServer.updateProperty(properties, "jdbcdatabaseurl", "ACM_JDBCDATABASEURL");
				SQLNetServer.updateProperty(properties, "jdbcusername", "ACM_JDBCUSERNAME");
				SQLNetServer.updateProperty(properties, "jdbcpassword", "ACM_JDBCPASSWORD");
				sqlset.setDriverURL(properties.getProperty("jdbcdriverurl"));
				sqlset.setDatabaseURL(properties.getProperty("jdbcdatabaseurl"));
				sqlset.setUsername(properties.getProperty("jdbcusername"));
				sqlset.setPassword(properties.getProperty("jdbcpassword"));
			}
			// /////////////////////////////////////////////////////////
			String logSetting = properties.getProperty("log", "");
			if (logSetting.trim().length() > 0) {
				try {
					SQLNetServer.logger.log(Level.CONFIG, "Reading Log Setting From " + logSetting);
					InputStream stream = new FileInputStream(logSetting);
					LogManager manager = LogManager.getLogManager();
					manager.readConfiguration(stream);
				} catch (FileNotFoundException fnfe) {
					SQLNetServer.logger.log(Level.WARNING, "File Not Found " + logSetting, fnfe);
				}
			}
			// sqlin
			if (Boolean.parseBoolean(sqlin)) {
				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader br = new BufferedReader(isr);
				String row = br.readLine();
				StringBuffer buf = new StringBuffer();
				while (row != null) {
					buf.append(row);
					row = br.readLine();
				}
				sql = buf.toString();
			}
			// file
			if (sql != null && sql.trim().length() > 0) {
				acmFile = getCobolFile(acmName, sql);
			} else {
				acmFile = getCobolFile(acmName);
			}
			//
			if (acmFile != null) {
				try {
					acmFile.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_SEQUENTIAL);
					//
					if (outName.trim().length() == 0) {
						// stdout
						fos = System.out;
					} else {
						// to file
						fos = new FileOutputStream(outName);
					}
					// line out
					boolean bool = false;
					bool = Boolean.valueOf(lineOut);
					//
					exportTo(acmFile, fos, bool);
					//
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						acmFile.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						fos.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {
				System.err.println("can't find " + acmName + ".");
				SQLNetServer.logger.log(Level.SEVERE, "can't find " + acmName + ".");
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	/**
	 * get COBOL file from name
	 * 
	 * @param name filename
	 * @return COBOL file
	 */
	protected CobolFile getCobolFile(String name) {
		SQLCobolRecordMetaData meta = (SQLCobolRecordMetaData) fileServer.metaDataSet.getMetaData(name);
		SQLFile file = null;
		if (meta != null) {
			connection = fileServer.createConnection();
			file = new SQLFile(connection, meta);
		}
		return file;
	}
	/**
	 * get COBOL file from name
	 * 
	 * @param name filename
	 * @param sql select statement
	 * @return COBOL file
	 */
	protected CobolFile getCobolFile(String name, String sql) {
		SQLCobolRecordMetaData meta = (SQLCobolRecordMetaData) fileServer.metaDataSet.getMetaData(name);
		meta.setSelectStatement(sql);
		SQLFile file = null;
		if (meta != null) {
			connection = fileServer.createConnection();
			file = new SQLFile(connection, meta);
		}
		return file;
	}
}
