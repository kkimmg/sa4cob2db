package k_kim_mg.sa4cob2db.sql;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
			System.err.println("java -classpath path_to_jdbc:path_to_acm \"k_kim_mg.sa4cob2db.sql.Acm2Seq\" acmfile outputfile metfile lineout(true/false) display_usage");
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
							properties.setProperty("display_usage", args[4]);
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
	public static void main_too(String acmfile, String outfile, String metafile, String lineout, String display_usage) {
		Acm2Seq.main(new String[] { acmfile, outfile, metafile, lineout, display_usage });
	}
	/** JDBC connection */
	private Connection connection;
	private SQLFileServer fileServer;
	/**
	 * ストリームに出力する
	 * 
	 * @param file コボルファイル
	 * @param stream ストリーム
	 * @param line ライン出力
	 * @throws IOException 例外
	 */
	protected void exportTo(CobolFile file, OutputStream stream, boolean line) throws IOException {
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		byte[] record = new byte[recsize];
		FileStatus stat = file.read(record);
		while (stat.getStatusCode() == FileStatus.STATUS_OK) {
			stream.write(record);
			if (line) {
				// ラインシーケンシャル
				stream.write('\n');
			}
			count++;
			// 次のレコードへ
			stat = file.next();
			if (stat.getStatusCode() == FileStatus.STATUS_OK) {
				// 次のレコードが存在する場合
				stat = file.read(record);
			} else if (stat.getStatusCode() != FileStatus.STATUS_OK && stat.getStatusCode() != FileStatus.STATUS_EOF) {
				// 次への移動でのエラー
			}
		}
		// 終端部分
		stream.flush();
		// 出力結果
		System.err.println("Row Count = " + count);
	}
	/**
	 * 出力する
	 * 
	 * @param properties プロパティ
	 */
	protected void exportTo(Properties properties) {
		// ファイル機能の作成
		fileServer = new SQLFileServer();
		CobolRecordMetaDataSet metaset = fileServer.getMetaDataSet();
		// メタデータfilename
		String metaString = properties.getProperty("metafile", SQLNetServer.DEFAULT_CONFIG);
		String AcmName = properties.getProperty("acmfile", "");
		String OutName = properties.getProperty("outfile", "");
		String LineOut = properties.getProperty("lineout", "false");
		// メタデータの取得
		File metaFile = new File(metaString);
		// メタデータ情報の取得
		NodeReadLoader nodeLoader = new NodeReadLoader();
		CobolFile AcmFile = null;
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
			// ログの設定
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
			// ACMファイル
			AcmFile = getCobolFile(AcmName);
			AcmFile.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_SEQUENCIAL);
			// 出力ファイル
			if (OutName.trim().length() == 0) {
				// 標準出力
				fos = System.out;
			} else {
				// ファイルへ
				fos = new FileOutputStream(OutName);
			}
			// ライン出力
			boolean bool = false;
			bool = Boolean.valueOf(LineOut);
			// 出力処理
			exportTo(AcmFile, fos, bool);
			// 終了処理
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
				AcmFile.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				fos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				connection.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	/**
	 * 名称からコボルファイルを取得する
	 * 
	 * @param name filename
	 * @return コボルファイル
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
}
