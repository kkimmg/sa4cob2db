package k_kim_mg.sa4cob2db.utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.SQLCobolColumn;
import k_kim_mg.sa4cob2db.sql.SQLFileServer;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;
import org.xml.sax.SAXException;
/**
 * シーケンシャルファイルに出力する
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class MetaData2SQL {
	/**
	 * 使い方を説明する
	 * 
	 * @param properties プロパティ
	 */
	private static void displayUsage(Properties properties) {
		String flag = properties.getProperty("display_usage", "false");
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
		properties.setProperty("metafile", getEnvValue("metafile", SQLNetServer.DEFAULT_CONFIG));
		if (args.length >= 1) {
			properties.setProperty("metafile", args[0]);
			if (args.length >= 2) {
				properties.setProperty("outfile", args[1]);
			}
		} else {
			System.err.println("metafileが指定されていません。");
		}
		// -------------------------
		MetaData2SQL obj = new MetaData2SQL();
		obj.exportTo(properties);
		// 使い方の説明
		displayUsage(properties);
	}
	/**
	 * 起動ルーチン
	 * 
	 * @param acmfile 入力ファイル
	 * @param outfile 出力ファイル
	 * @param metafile メタデータファイル
	 */
	public static void main_too(String metafile, String outfile) {
		MetaData2SQL.main(new String[] { metafile, outfile, });
	}
	/** Internalファイルサーバー */
	private SQLFileServer fileServer;
	/**
	 * ストリームに出力する
	 * 
	 * @param file コボルファイル
	 * @param stream ストリーム
	 * @param line ライン出力
	 * @throws IOException 例外
	 * @throws CobolRecordException
	 */
	protected void exportTo(CobolRecordMetaData meta, OutputStream stream) throws IOException {
		boolean first = true;
		String name = meta.getName();
		Writer writer = new OutputStreamWriter(stream);
		writer.write("CREATE TABLE " + name + " (\n");
		int count = meta.getColumnCount();
		for (int i = 0; i < count; i++) {
			CobolColumn column = meta.getColumn(i);
			if (column instanceof SQLCobolColumn) {
				try {
					if (first) {
						first = false;
					} else {
						writer.write(",\n");
					}
					StringBuffer buf = new StringBuffer();
					SQLCobolColumn sqlcolumn = (SQLCobolColumn) column;
					buf.append("\t");
					buf.append(sqlcolumn.getOriginalColumnName());
					buf.append("\t");
					int type = sqlcolumn.getType();
					int length = sqlcolumn.getLength();
					switch (type) {
					case CobolColumn.TYPE_INTEGER:
					case CobolColumn.TYPE_LONG:
						buf.append("NUMERIC(" + sqlcolumn.getLength() + ")");
						break;
					case CobolColumn.TYPE_FLOAT:
					case CobolColumn.TYPE_DOUBLE:
						buf.append("NUMERIC(" + sqlcolumn.getLength() + "," + (sqlcolumn.getLength() - sqlcolumn.getNumberOfDecimal()) + ")");
						break;
					case CobolColumn.TYPE_DATE:
						buf.append("DATE");
						break;
					case CobolColumn.TYPE_TIME:
						buf.append("TIME");
						break;
					case CobolColumn.TYPE_TIMESTAMP:
						buf.append("TIMESTAMP");
						break;
					case CobolColumn.TYPE_XCHAR:
					case CobolColumn.TYPE_NCHAR:
						buf.append("VARCHAR" + "(" + String.valueOf(length) + ")");
						break;
					}
					writer.write(buf.toString());
				} catch (CobolRecordException e) {
					e.printStackTrace();
				}
			}
		}
		boolean first2 = true;
		int count2 = meta.getKeyCount();
		if (count2 > 0) {
			StringBuffer keysBuf = new StringBuffer();
			for (int i = 0; i < count2; i++) {
				if (first2) {
					first2 = false;
				} else {
					keysBuf.append(",");
				}
				CobolColumn column = meta.getKey(i);
				if (column instanceof SQLCobolColumn) {
					SQLCobolColumn sqlcolumn = (SQLCobolColumn)column;
					try {
						keysBuf.append(sqlcolumn.getOriginalColumnName());
					} catch (CobolRecordException e) {
						keysBuf.append(sqlcolumn.getName());;
					}
				}
			}
			if (first) {
				first = false;
			} else {
				writer.write(",\n");
			}
			writer.write("\tCONSTRAINT PKEY PRIMARY KEY (" + keysBuf.toString() + ")");
		}
		// 終端部分
		writer.write("\n);");
		writer.flush();
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
		String OutName = properties.getProperty("outfile", metaString + ".sql");
		// メタデータの取得
		File metaFile = new File(metaString);
		// メタデータ情報の取得
		NodeReadLoader nodeLoader = new NodeReadLoader();
		// CobolFile AcmFile = null;
		OutputStream fos = null;
		try {
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
			// /////////////////////////////////////////////////////////
			// ACMファイル
			// AcmFile = getCobolFile(AcmName);
			// AcmFile.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_SEQUENCIAL);
			// 出力ファイル
			if (OutName.trim().length() == 0) {
				// 標準出力
				fos = System.out;
			} else {
				// ファイルへ
				fos = new FileOutputStream(OutName);
			}
			// 出力処理
			for (CobolRecordMetaData meta : metaset.toArray()) {
				exportTo(meta, fos);
			}
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
				fos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
