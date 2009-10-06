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
 * �������󥷥��ե�����˽��Ϥ���
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class Acm2Seq {
	/**��ư�롼����*/
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
			System.err.println("acmfile�����ꤵ��Ƥ��ޤ���");
		}
		//-------------------------
		Acm2Seq obj = new Acm2Seq();
		obj.exportTo(properties);
		// �Ȥ���������
		displayUsage(properties);
	}
	/**
	 * �Ȥ�������������
	 * @param properties	�ץ�ѥƥ�
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
	 * �Ķ��ѿ����������
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
	/** �����ե����륵���С� */
	private SQLFileServer fileServer;
	/**
	 *  ���ȥ꡼��˽��Ϥ���
	 * @param file		���ܥ�ե�����
	 * @param stream	���ȥ꡼��
	 * @param line		�饤�����
	 * @throws IOException	�㳰
	 */
	protected void exportTo (CobolFile file, OutputStream stream, boolean line) throws IOException {
		int count = 0;
		int recsize = file.getMetaData().getRowSize();
		FileStatus stat = file.next();
		byte[] record = new byte[recsize];
		while (stat.getStatusCode() == FileStatus.STATUS_OK) {
			stat = file.read(record);
			if (stat.getStatusCode() == FileStatus.STATUS_OK) {
				// �ǡ������ȥ꡼��ؽ���
				stream.write(record);
				if (line) {
					// �饤�󥷡����󥷥��
					stream.write('\n');
				}
				count++;
				// ���Υ쥳���ɤ�
				stat = file.next();
				if (stat.getStatusCode() != FileStatus.STATUS_OK &&
					stat.getStatusCode() != FileStatus.STATUS_EOF) {
					// ���ؤΰ�ư�ǤΥ��顼
				} 
			} else {
				// �ɼ襨�顼
			}
		}
		// ��ü��ʬ
		stream.flush();
		// ���Ϸ��
		System.err.println("Row Count = " + count);
	}
	/**
	 * ���Ϥ���
	 * @param properties �ץ�ѥƥ�
	 */
	protected void exportTo (Properties properties) {
		// �ե����뵡ǽ�κ���
		fileServer = new SQLFileServer();
		// �᥿�ǡ����ե�����̾
		String metaString = properties.getProperty("metafile", SQLNetServer.DEFAULT_CONFIG);
		String AcmName = properties.getProperty("acmfile", "");
		String OutName = properties.getProperty("outfile", "");
		String LineOut = properties.getProperty("lineout", "false");
		// �᥿�ǡ����μ���
		File metaFile = new File(metaString);
		// �᥿�ǡ�������μ���
		NodeReadLoader nodeLoader = new NodeReadLoader();
		CobolFile AcmFile = null;
		OutputStream fos = null;
		try {
			nodeLoader.createMetaDataSet(metaFile, fileServer.getMetaDataSet(), properties);
			// ACM�ե�����
			AcmFile = getCobolFile(AcmName);
			AcmFile.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_SEQUENCIAL);
			// ���ϥե�����
			if (OutName.length() == 0) {
				// ɸ�����
				fos = System.out;
			} else {
				// �ե������
				fos = new FileOutputStream(OutName);
			}
			// �饤�����
			boolean bool = false;
			bool = Boolean.valueOf(LineOut);
			// ���Ͻ���
			exportTo(AcmFile, fos, bool);
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
				fos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	/**
	 * ̾�Τ��饳�ܥ�ե�������������
	 * @param name	�ե�����̾
	 * @return	���ܥ�ե�����
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
