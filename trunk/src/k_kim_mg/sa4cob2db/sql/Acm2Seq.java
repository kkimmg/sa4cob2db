package k_kim_mg.sa4cob2db.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.FileStatus;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

import org.xml.sax.SAXException;

/**
 * シーケンシャルファイルに出力する
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class Acm2Seq {
	/**起動ルーチン*/
	public static void main(String[] args) {
		Properties properties = new Properties();
		//-------------------------
		properties.setProperty("lineout", getEnvValue("lineout", "false"));
		properties.setProperty("metafile", getEnvValue("metafile", SQLNetServer.DEFAULT_CONFIG));
		if (args.length >= 1) {
			properties.setProperty("acmfile", args[0]);
			if (args.length >= 2) {
				properties.setProperty("outfile", args[1]);
			}
		} else {
			System.err.println("acmfileが指定されていません。");
		}
		//-------------------------
		Acm2Seq obj = new Acm2Seq();
		obj.exportTo(properties);
		// 使い方の説明
		displayUsage(properties);
	}
	/**
	 * 使い方を説明する
	 * @param properties	プロパティ
	 */
	private static void displayUsage (Properties properties) {
		String flag = properties.getProperty("display_usage", "true");
		if (Boolean.parseBoolean(flag)) {
			System.err.println("java -classpath path_to_jdbc:path_to_acm \"k_kim_mg.sa4cob2db.sql.Acm2Seq\" acmfile outputfile");
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
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private static String getEnvValue (String key, String defaultValue) {
		String ret = System.getProperty(key, System.getenv(key));
		if (ret == null) ret = defaultValue;
		if (ret.length() == 0) ret = defaultValue;
		return ret;
	}
	/** 内部ファイルサーバー */
	private SQLFileServer fileServer;
	/**
	 *  ストリームに出力する
	 * @param file		コボルファイル
	 * @param stream	ストリーム
	 * @param line		ライン出力
	 * @throws IOException	例外
	 */
	protected void exportTo (CobolFile file, OutputStream stream, boolean line) throws IOException {
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		FileStatus stat = file.next();
		byte[] record = new byte[recsize];
		while (stat.getStatusCode() == FileStatus.STATUS_OK) {
			stat = file.read(record);
			if (stat.getStatusCode() == FileStatus.STATUS_OK) {
				// データストリームへ出力
				stream.write(record);
				if (line) {
					// ラインシーケンシャル
					stream.write('\n');
				}
				count++;
				// 次のレコードへ
				stat = file.next();
				if (stat.getStatusCode() != FileStatus.STATUS_OK &&
					stat.getStatusCode() != FileStatus.STATUS_EOF) {
					// 次への移動でのエラー
				} 
			} else {
				// 読取エラー
			}
		}
		// 終端部分
		stream.flush();
		// 出力結果
		System.err.println("Row Count = " + count);
	}
	/**
	 * 出力する
	 * @param properties プロパティ
	 */
	protected void exportTo (Properties properties) {
		// ファイル機能の作成
		fileServer = new SQLFileServer();
		// メタデータファイル名
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
			nodeLoader.createMetaDataSet(metaFile, fileServer.getMetaDataSet(), properties);
			// ACMファイル
			AcmFile = getCobolFile(AcmName);
			AcmFile.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_SEQUENCIAL);
			// 出力ファイル
			if (OutName.length() == 0) {
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
		}
	}
	/**
	 * 名称からコボルファイルを取得する
	 * @param name	ファイル名
	 * @return	コボルファイル
	 */
	protected CobolFile getCobolFile (String name) {
		SQLCobolRecordMetaData meta = (SQLCobolRecordMetaData) fileServer.metaDataSet.getMetaData(name);
		SQLFile file = null;
		if (meta != null) {
			file = new SQLFile(fileServer.createConnection(), meta);
		}
		return file;
	}
}
