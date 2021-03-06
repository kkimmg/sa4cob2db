package k_kim_mg.sa4cob2db.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
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
 * import from sequential file
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class Seq2Acm {
	private int commit;

	/**
	 * display usage
	 * 
	 * @param properties properties
	 */
	private static void displayUsage(Properties properties) {
		String flag = properties.getProperty("display_usage", "true");
		if (Boolean.parseBoolean(flag)) {
			System.err.println("java -classpath path_to_jdbc:path_to_acm \"k_kim_mg.sa4cob2db.sql.Seq2Acm\" acmfile inputfile metafile linein(true/false) extend(true/false) display_usage");
			java.util.Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				String val = properties.getProperty(key);
				System.err.println(key + " = " + val);
			}
		}
	}

	/**
	 * get from environment values
	 * 
	 * @param key key
	 * @param defaultValue default value
	 * @return environment value
	 */
	private static String getEnvValue(String key, String defaultValue) {
		String ret = System.getProperty(key, System.getenv(key));
		if (ret == null) {
			ret = defaultValue;
		}
		if (ret.length() == 0) {
			ret = defaultValue;
		}
		return ret;
	}

	/** main */
	public static void main(String[] args) {
		Properties properties = new Properties();
		// -------------------------
		properties.setProperty("linein", getEnvValue("linein", "false"));
		properties.setProperty("metafile", getEnvValue("metafile", SQLNetServer.DEFAULT_CONFIG));
		properties.setProperty("display_usage", getEnvValue("display_usage", "false"));
		properties.setProperty("extend", "false");
		if (args.length >= 1) {
			properties.setProperty("acmfile", args[0]);
			if (args.length >= 2) {
				properties.setProperty("infile", args[1]);
				if (args.length >= 3) {
					properties.setProperty("metafile", args[2]);
					if (args.length >= 4) {
						properties.setProperty("linein", args[3]);
						if (args.length >= 5) {
							properties.setProperty("extend", args[4]);
							if (args.length >= 6) {
								properties.setProperty("commit", args[5]);
								if (args.length >= 7) {
									properties.setProperty("display_usage", args[6]);
								}
							}
						}
					}
				}
			}
			if (args[0] == null || args[0].trim().length() <= 0) {
				System.err.println("infile required");
				displayUsage(properties);
				return;
			}
		} else {
			System.err.println("infile required");
			displayUsage(properties);
			return;
		}
		// -------------------------
		Seq2Acm obj = new Seq2Acm();
		obj.importTo(properties);
	}

	/**
	 * main
	 * 
	 * @param acmfile file
	 * @param metafile mete data
	 * @param linein is it line sequential file?
	 * @param extend mode<br/>
	 *            true extend<br/>
	 *            false overwrite
	 * @param commit commit count<br>
	 *            commit > 0 commit per this value<br>
	 *            commit <= 0 auto commit
	 * @param display_usage display usage?
	 */
	public static void main_too(String acmfile, String infile, String metafile, String linein, String extend, String commit, String display_usage) {
		Seq2Acm.main(new String[] { acmfile, infile, metafile, linein, extend, commit, display_usage });
	}

	private Connection connection;
	private SQLFileServer fileServer;

	/**
	 * get file from name
	 * 
	 * @param name filename
	 * @return file name
	 */
	protected CobolFile getCobolFile(String name) {
		SQLCobolRecordMetaData meta = (SQLCobolRecordMetaData) fileServer.metaDataSet.getMetaData(name);
		SQLFile file = null;
		if (meta != null) {
			connection = fileServer.createConnection();
			if (commit > 0) {
				try {
					connection.setAutoCommit(false);
				} catch (SQLException e) {
					e.printStackTrace();
					commit = 0;
				}
			}
			file = new SQLFile(connection, meta);
		}
		return file;
	}

	/**
	 * import form line sequential file
	 * 
	 * @param file file
	 * @param stream stream
	 * @throws IOException exception
	 */
	protected void importLineTo(CobolFile file, InputStream stream) throws IOException {
		int insrow = 0;
		int count = 0;
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		String row = br.readLine();
		FileStatus stat;
		while (row != null) {
			stat = file.write(row.getBytes());
			if (stat != FileStatus.OK) {
				// error
			} else {
				count++;
				if (commit > 0) {
					if (insrow >= commit) {
						try {
							connection.commit();
							insrow = -1;
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					insrow++;
				}
			}
			row = br.readLine();
		}
		if (commit > 0) {
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.err.println("Row Count = " + count);
	}

	/**
	 * import from sequential file
	 * 
	 * @param file file
	 * @param stream stream
	 * @throws IOException exception
	 */
	protected void importTo(CobolFile file, InputStream stream) throws IOException {
		int insrow = 0;
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		byte[] record = new byte[recsize];
		FileStatus stat;
		while (stream.read(record) > 0) {
			SQLNetServer.logger.severe(new String(record));
			stat = file.write(record);
			if (stat != FileStatus.OK) {
				// error
			} else {
				count++;
				if (commit > 0) {
					if (insrow >= commit) {
						try {
							connection.commit();
							insrow = -1;
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					insrow++;
				}
			}
		}
		if (commit > 0) {
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.err.println("Row Count = " + count);
	}

	/**
	 * import form (line) sequential file
	 * 
	 * @param file file
	 * @param stream stream
	 * @param line is it line sequential file?
	 * @throws IOException exception
	 */
	protected void importTo(CobolFile file, InputStream stream, boolean line) throws IOException {
		if (line) {
			importLineTo(file, stream);
		} else {
			importTo(file, stream);
		}
	}

	/**
	 * import from file
	 * 
	 * @param properties key-value set
	 */
	protected void importTo(Properties properties) {
		//
		fileServer = new SQLFileServer();
		CobolRecordMetaDataSet metaset = fileServer.getMetaDataSet();
		//
		String metaString = properties.getProperty("metafile", SQLNetServer.DEFAULT_CONFIG);
		String acmName = properties.getProperty("acmfile", "");
		String inName = properties.getProperty("infile", "");
		String lineIn = properties.getProperty("linein", "false");
		String extend_s = properties.getProperty("extend", "false");
		boolean extend = Boolean.parseBoolean(extend_s);
		String commit_s = properties.getProperty("commit", "0");
		try {
			commit = Integer.parseInt(commit_s);
		} catch (Exception ex) {
			commit = 0;
		}
		//
		File metaFile = new File(metaString);
		//
		NodeReadLoader nodeLoader = new NodeReadLoader();
		CobolFile acmFile = null;
		InputStream fis = null;
		try {
			nodeLoader.createMetaDataSet(metaFile, fileServer.getMetaDataSet(), properties);
			if (metaset instanceof SQLCobolRecordMetaDataSet) {
				SQLNetServer.updateProperty(properties, "jdbcdriverurl", "ACM_JDBCDRIVERURL");
				SQLNetServer.updateProperty(properties, "jdbcdatabaseurl", "ACM_JDBCDATABASEURL");
				SQLNetServer.updateProperty(properties, "jdbcusername", "ACM_JDBCUSERNAME");
				SQLNetServer.updateProperty(properties, "jdbcpassword", "ACM_JDBCPASSWORD");

				SQLCobolRecordMetaDataSet sqlset = (SQLCobolRecordMetaDataSet) metaset;
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
			//
			acmFile = getCobolFile(acmName);
			if (acmFile != null) {
				try {
					if (extend) {
						acmFile.open(CobolFile.MODE_EXTEND, CobolFile.ACCESS_SEQUENTIAL);
					} else {
						acmFile.open(CobolFile.MODE_OUTPUT, CobolFile.ACCESS_SEQUENTIAL);
					}
					//
					if (inName.length() == 0) {
						fis = System.in;
					} else {
						fis = new FileInputStream(inName);
					}
					boolean bool = false;
					bool = Boolean.valueOf(lineIn);
					//
					importTo(acmFile, fis, bool);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						acmFile.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						fis.close();
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
}
