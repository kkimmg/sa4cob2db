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
 * generate create table statement
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class MetaData2SQL {
	/** main */
	public static void main(String[] args) {
		Properties properties = new Properties();
		// -------------------------
		if (args.length >= 1) {
			properties.setProperty("metafile", args[0]);
			if (args.length >= 2) {
				properties.setProperty("outfile", args[1]);
			}
		} else {
			System.err.println("not defined infile");
		}
		// -------------------------
		MetaData2SQL obj = new MetaData2SQL();
		obj.exportTo(properties);
	}
	/**
	 * usage
	 * 
	 * @param metafile meta data file
	 * @param outfile sql file
	 */
	public static void main_too(String metafile, String outfile) {
		MetaData2SQL.main(new String[] { metafile, outfile, });
	}
	/** Internalファイルサーバー */
	private SQLFileServer fileServer;
	/**
	 * ストリームに出力する
	 * 
	 * @param meta meta data
	 * @param stream output stream
	 * @throws IOException io exception
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
					SQLCobolColumn sqlcolumn = (SQLCobolColumn) column;
					try {
						keysBuf.append(sqlcolumn.getOriginalColumnName());
					} catch (CobolRecordException e) {
						keysBuf.append(sqlcolumn.getName());
						;
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
		// 
		writer.write("\n);");
		writer.flush();
	}
	/**
	 * export
	 * 
	 * @param properties properties
	 */
	protected void exportTo(Properties properties) {
		fileServer = new SQLFileServer();
		CobolRecordMetaDataSet metaset = fileServer.getMetaDataSet();
		String metaString = properties.getProperty("metafile", SQLNetServer.DEFAULT_CONFIG);
		String OutName = properties.getProperty("outfile", metaString + ".sql");
		File metaFile = new File(metaString);
		NodeReadLoader nodeLoader = new NodeReadLoader();
		OutputStream fos = null;
		try {
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
			// out file
			if (OutName.trim().length() == 0) {
				fos = System.out;
			} else {
				fos = new FileOutputStream(OutName);
			}
			// export
			for (CobolRecordMetaData meta : metaset.toArray()) {
				exportTo(meta, fos);
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
				fos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
