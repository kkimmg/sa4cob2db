/**
 * 
 */
package k_kim_mg.sa4cob2db.sql;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.FileStatus;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

import org.xml.sax.SAXException;
/**
 * �������󥷥��ե����뤫�����Ϥ���
 * @author ���줪��
 */
public class Seq2Acm {
	/** ��ư�롼���� */
	public static void main(String[] args) {
		Properties properties = new Properties();
		// -------------------------
		properties.setProperty("linein", getEnvValue("linein", "false"));
		properties.setProperty("metafile", getEnvValue("metafile", SQLNetServer.DEFAULT_CONFIG));
		properties.setProperty("display_usage", getEnvValue("display_usage", "true"));
		if (args.length >= 1) {
			properties.setProperty("acmfile", args[0]);
			if (args.length >= 2) {
				properties.setProperty("infile", args[1]);
			}
		} else {
			System.err.println("acmfile�����ꤵ��Ƥ��ޤ���");
		}
		// -------------------------
		Seq2Acm obj = new Seq2Acm();
		obj.importTo(properties);
		// �Ȥ���������
		displayUsage(properties);
	}
	/**
	 * �Ȥ�������������
	 * @param properties �ץ�ѥƥ�
	 */
	private static void displayUsage(Properties properties) {
		String flag = properties.getProperty("display_usage", "true");
		if (Boolean.parseBoolean(flag)) {
			System.err.println("java -classpath path_to_jdbc:path_to_acm \"k_kim_mg.sa4cob2db.sql.Seq2Acm\" acmfile inputfile");
			java.util.Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				String val = properties.getProperty(key);
				System.err.println(key + " = " + val);
			}
		}
	}
	/**
	 * �Ķ��ѿ����������
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
	/** �����ե����륵���С� */
	private SQLFileServer fileServer;
	/**
	 * ���ȥ꡼��˽��Ϥ���
	 * @param file ���ܥ�ե�����
	 * @param stream ���ȥ꡼��
	 * @param line �饤�����
	 * @throws IOException �㳰
	 */
	protected void importTo(CobolFile file, InputStream stream, boolean line) throws IOException {
		if (line) {
			importLineTo(file, stream);
		} else {
			importTo(file, stream);
		}
	}
	/**
	 * ���ȥ꡼��˽��Ϥ���
	 * @param file ���ܥ�ե�����
	 * @param stream ���ȥ꡼��
	 * @throws IOException �㳰
	 */
	protected void importTo(CobolFile file, InputStream stream) throws IOException {
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		byte[] record = new byte[recsize];
		FileStatus stat;
		while (stream.read(record) > 0) {
			stat = file.write(record);
			if (stat != FileStatus.OK) {
				// ���顼�ˤʤä�
			} else {
				count++;
			}
		}
		// ��ü��ʬ
		// ���Ϸ��
		System.err.println("Row Count = " + count);
	}
	/**
	 * ���ȥ꡼��˽��Ϥ���
	 * @param file ���ܥ�ե�����
	 * @param stream ���ȥ꡼��
	 * @throws IOException �㳰
	 */
	protected void importLineTo(CobolFile file, InputStream stream) throws IOException {
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		String row = br.readLine();
		byte[] record = new byte[recsize];
		FileStatus stat;
		while (row != null) {
			stat = file.write(record);
			if (stat != FileStatus.OK) {
				// ���顼�ˤʤä�
			} else {
				count++;
			}
			row = br.readLine();
		}
		// ��ü��ʬ
		// ���Ϸ��
		System.err.println("Row Count = " + count);
	}
	/**
	 * ���Ϥ���
	 * @param properties �ץ�ѥƥ�
	 */
	protected void importTo(Properties properties) {
		// �ե����뵡ǽ�κ���
		fileServer = new SQLFileServer();
		// �᥿�ǡ����ե�����̾
		String metaString = properties.getProperty("metafile", SQLNetServer.DEFAULT_CONFIG);
		String AcmName = properties.getProperty("acmfile", "");
		String InName = properties.getProperty("infile", "");
		String LineIn = properties.getProperty("linein", "false");
		// �᥿�ǡ����μ���
		File metaFile = new File(metaString);
		// �᥿�ǡ�������μ���
		NodeReadLoader nodeLoader = new NodeReadLoader();
		CobolFile AcmFile = null;
		InputStream fis = null;
		try {
			nodeLoader.createMetaDataSet(metaFile, fileServer.getMetaDataSet(), properties);
			// ACM�ե�����
			AcmFile = getCobolFile(AcmName);
			AcmFile.open(CobolFile.MODE_OUTPUT, CobolFile.ACCESS_SEQUENCIAL);
			// ���ϥե�����
			if (InName.length() == 0) {
				// ɸ�����
				fis = System.in;
			} else {
				// �ե������
				fis = new FileInputStream(InName);
			}
			// �饤�����
			boolean bool = false;
			bool = Boolean.valueOf(LineIn);
			// ���Ͻ���
			importTo(AcmFile, fis, bool);
			// ��λ����
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
	/**
	 * ̾�Τ��饳�ܥ�ե�������������
	 * @param name �ե�����̾
	 * @return ���ܥ�ե�����
	 */
	protected CobolFile getCobolFile(String name) {
		SQLCobolRecordMetaData meta = (SQLCobolRecordMetaData) fileServer.metaDataSet.getMetaData(name);
		SQLFile file = null;
		if (meta != null) {
			file = new SQLFile(fileServer.createConnection(), meta);
		}
		return file;
	}
}
