// package k_kim_mg.sa4cob2db.sample;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

import org.xml.sax.SAXException;
public class ServletSampleTool1 {
	/** ����ƥ�� */
	protected Hashtable<String, WebInterface1> contents;
	/** �᥿�ǡ������å� */
	protected CobolRecordMetaDataSet metaSet;
	/** ���å����δ�����Ԥ� */
	protected Hashtable<String, CobolRecord> sessions;
	/** ���֥롼���� */
	protected Hashtable<String, WebInterface1> subroutines;
	/** Web���󥿡��ե����� */
	protected Hashtable<String, WebInterface1> webinterfaces;
	/** �إå���̾ */
	public String MSGHEADNAME;
	/**
	 * ���󥹥ȥ饯��
	 */
	public ServletSampleTool1(String metaString) {
		super();
		MSGHEADNAME = "msghead";
		// �����
		webinterfaces = new Hashtable<String, WebInterface1>();
		subroutines = new Hashtable<String, WebInterface1>();
		contents = new Hashtable<String, WebInterface1>();
		// �᥿�ǡ����ե�����̾
		Properties properties = new Properties();
		// properties.setProperty("metafile", "test.xml");
		// String metaString = properties.getProperty("metafile",
		// DEFAUT_CONFIG);
		File metaFile = new File(metaString);
		System.err.println(metaFile.getAbsolutePath());
		// �᥿�ǡ�������μ���
		NodeReadLoader nodeLoader = new NodeReaderPlus1(this);
		metaSet = new SampleMetaDataSet1();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaSet, properties);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ���å��������ơ��֥�κ���
		sessions = new Hashtable<String, CobolRecord>();
	}
	/** Web���󥿡��ե��������ɲ� */
	protected void addWebInterface(WebInterface1 face1) {
		webinterfaces.put(face1.getName(), face1);
		subroutines.put(face1.getSubroutine(), face1);
		contents.put(face1.getContent(), face1);
	}
	/** ����ƥ�Ĥ������ */
	public WebInterface1 getWebInterfaceByContent(String content) {
		return contents.get(content);
	}
	/** ̾��������� */
	public WebInterface1 getWebInterfaceByName(String name) {
		return webinterfaces.get(name);
	}
	/** ���֥롼���󤫤���� */
	public WebInterface1 getWebInterfaceBySubroutine(String subroutine) {
		return subroutines.get(subroutine);
	}
	/**
	 * �쥳���ɤ���ꥯ�����Ȥؤ��Ѵ�
	 * @param record �쥳����
	 * @param request �ꥯ�����ȡʥꥯ�����Ȥ�°�����ͤ򥻥åȤ����
	 * @throws ServletException �㳰��
	 * @throws IOException �㳰��
	 * @throws CobolRecordException �㳰��
	 */
	protected void setRecordToRequest(CobolRecord record, HttpServletRequest request) throws ServletException, IOException, CobolRecordException {
		CobolRecordMetaData meta = record.getMetaData();
		for (int i = 0; i < meta.getColumnCount(); i++) {
			CobolColumn column = meta.getColumn(i);
			String columnValue = record.getString(column);
			request.setAttribute(column.getName(), columnValue);
		}
	}
	/**
	 * �ꥯ�����Ȥ���쥳���ɷ����ؤ��Ѵ�
	 * @param request �ꥯ������
	 * @param record �쥳����
	 * @throws ServletException �㳰��
	 * @throws IOException �㳰��
	 */
	protected void setRequestToRecord(HttpServletRequest request, CobolRecord record) throws ServletException, IOException {
		CobolRecordMetaData meta = null;
		try {
			meta = record.getMetaData();
		} catch (CobolRecordException ex) {
			return;
		}
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = request.getParameter(key);
			try {
				int id = meta.findColumn(key);
				CobolColumn column = meta.getColumn(id);
				record.updateObject(column, value);
			} catch (CobolRecordException e) {
				// �󤬸��դ���ʤ��ä����ʤ�
				// e.printStackTrace();
			}
		}
	}
	/**
	 * �إå����쥳���ɤμ���
	 * @param request �ꥯ������
	 * @return �إå����쥳����
	 */
	public CobolRecord getHeaderRecord(HttpServletRequest request) {
		Object wrk = null;
		CobolRecord ret = null;
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		wrk = sessions.get(sessionId);
		if (wrk == null) {
			ret = createRecord(MSGHEADNAME);
			sessions.put(sessionId, ret);
		} else {
			ret = (CobolRecord) wrk;
		}
		return ret;
	}
	/**
	 * ����Υ�åѤ��֤�
	 * @param metaname �᥿�ǡ���̾
	 * @return ���ܥ�Υ쥳���ɷ���
	 */
	public CobolRecord createRecord(String metaname) {
		CobolRecordMetaData meta = metaSet.getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}
