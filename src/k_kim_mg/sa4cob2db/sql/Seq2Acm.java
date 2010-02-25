/**
 * 
 */
package k_kim_mg.sa4cob2db.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * シーケンシャルファイルから入力する
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class Seq2Acm {
	/**
	 * 使い方を説明する
	 * 
	 * @param properties
	 *            プロパティ
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
	 * 環境変数を取得する
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static String getEnvValue(String key, String defaultValue) {
		String ret = System.getProperty(key, System.getenv(key));
		if (ret == null)
			ret = defaultValue;
		if (ret.length() == 0)
			ret = defaultValue;
		return ret;
	}

	/** 起動ルーチン */
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
								properties.setProperty("display_usage", args[5]);
							}
						}
					}
				}
			}
		} else {
			System.err.println("acmfileが指定されていません。");
		}
		// -------------------------
		Seq2Acm obj = new Seq2Acm();
		obj.importTo(properties);
		// 使い方の説明
		displayUsage(properties);
	}

	/**
	 * 起動ルーチン
	 * 
	 * @param acmfile
	 *            入力ファイル
	 * @param outfile
	 *            出力ファイル
	 * @param metafile
	 *            メタデータファイル
	 * @param linein
	 *            改行を含むかどうか true or false
	 * @param extend
	 *            オープンモード<br/>
	 *            true 追記モード<br/>
	 *            false 上書モード
	 * @param display_usage
	 *            使い方を表示するかどうか
	 */
	public static void main_too(String acmfile, String infile, String metafile, String linein, String extend, String display_usage) {
		Seq2Acm.main(new String[] { acmfile, infile, metafile, linein, extend, display_usage });
	}

	/** JDBCコネクション */
	private Connection connection;

	/** 内部ファイルサーバー */
	private SQLFileServer fileServer;

	/**
	 * 名称からコボルファイルを取得する
	 * 
	 * @param name
	 *            ファイル名
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

	/**
	 * ストリームに出力する
	 * 
	 * @param file
	 *            コボルファイル
	 * @param stream
	 *            ストリーム
	 * @throws IOException
	 *             例外
	 */
	protected void importLineTo(CobolFile file, InputStream stream) throws IOException {
		int count = 0;
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		String row = br.readLine();
		FileStatus stat;
		while (row != null) {
			stat = file.write(row.getBytes());
			if (stat != FileStatus.OK) {
				// エラーになった
			} else {
				count++;
			}
			row = br.readLine();
		}
		// 終端部分
		// 出力結果
		System.err.println("Row Count = " + count);
	}

	/**
	 * ストリームに出力する
	 * 
	 * @param file
	 *            コボルファイル
	 * @param stream
	 *            ストリーム
	 * @throws IOException
	 *             例外
	 */
	protected void importTo(CobolFile file, InputStream stream) throws IOException {
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		byte[] record = new byte[recsize];
		FileStatus stat;
		while (stream.read(record) > 0) {
			SQLNetServer.logger.severe(new String(record));
			stat = file.write(record);
			if (stat != FileStatus.OK) {
				// エラーになった
			} else {
				count++;
			}
		}
		// 終端部分
		// 出力結果
		System.err.println("Row Count = " + count);
	}

	/**
	 * ストリームに出力する
	 * 
	 * @param file
	 *            コボルファイル
	 * @param stream
	 *            ストリーム
	 * @param line
	 *            ライン出力
	 * @throws IOException
	 *             例外
	 */
	protected void importTo(CobolFile file, InputStream stream, boolean line) throws IOException {
		if (line) {
			importLineTo(file, stream);
		} else {
			importTo(file, stream);
		}
	}

	/**
	 * 出力する
	 * 
	 * @param properties
	 *            プロパティ
	 */
	protected void importTo(Properties properties) {
		// ファイル機能の作成
		fileServer = new SQLFileServer();
		CobolRecordMetaDataSet metaset = fileServer.getMetaDataSet();
		// メタデータファイル名
		String metaString = properties.getProperty("metafile", SQLNetServer.DEFAULT_CONFIG);
		String AcmName = properties.getProperty("acmfile", "");
		String InName = properties.getProperty("infile", "");
		String LineIn = properties.getProperty("linein", "false");
		String Extend = properties.getProperty("extend", "false");
		boolean extend = Boolean.parseBoolean(Extend);
		// メタデータの取得
		File metaFile = new File(metaString);
		// メタデータ情報の取得
		NodeReadLoader nodeLoader = new NodeReadLoader();
		CobolFile AcmFile = null;
		InputStream fis = null;
		try {
			nodeLoader.createMetaDataSet(metaFile, fileServer.getMetaDataSet(), properties);
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
			if (extend) {
				// 追記モード
				AcmFile.open(CobolFile.MODE_EXTEND, CobolFile.ACCESS_SEQUENCIAL);
			} else {
				// 通常モード
				AcmFile.open(CobolFile.MODE_OUTPUT, CobolFile.ACCESS_SEQUENCIAL);
			}
			// 出力ファイル
			if (InName.length() == 0) {
				// 標準出力
				fis = System.in;
			} else {
				// ファイルへ
				fis = new FileInputStream(InName);
			}
			// ライン出力
			boolean bool = false;
			bool = Boolean.valueOf(LineIn);
			// 出力処理
			importTo(AcmFile, fis, bool);
			// 終了処理
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
				AcmFile.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
