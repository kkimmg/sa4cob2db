package k_kim_mg.sa4cob2db.WebSample;
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
	/** �R���e���c */
	protected Hashtable<String, WebInterface1> contents;
	/** ���^�f�[�^�Z�b�g */
	protected CobolRecordMetaDataSet metaSet;
	/** �Z�b�V�����̊Ǘ����s�� */
	protected Hashtable<String, CobolRecord> sessions;
	/** �T�u���[�`�� */
	protected Hashtable<String, WebInterface1> subroutines;
	/** Web�C���^�[�t�F�[�X */
	protected Hashtable<String, WebInterface1> webinterfaces;
	/** �w�b�_�[�� */
	public String MSGHEADNAME;
	/**
	 * �R���X�g���N�^
	 */
	public ServletSampleTool1(String metaString) {
		super();
		MSGHEADNAME = "msghead";
		// ������
		webinterfaces = new Hashtable<String, WebInterface1>();
		subroutines = new Hashtable<String, WebInterface1>();
		contents = new Hashtable<String, WebInterface1>();
		// ���^�f�[�^�t�@�C����
		Properties properties = new Properties();
		// properties.setProperty("metafile", "test.xml");
		// String metaString = properties.getProperty("metafile",
		// DEFAUT_CONFIG);
		File metaFile = new File(metaString);
		System.err.println(metaFile.getAbsolutePath());
		// ���^�f�[�^���̎擾
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
		// �Z�b�V�����Ǘ��e�[�u���̍쐬
		sessions = new Hashtable<String, CobolRecord>();
	}
	/** Web�C���^�[�t�F�[�X�̒ǉ� */
	protected void addWebInterface(WebInterface1 face1) {
		webinterfaces.put(face1.getName(), face1);
		subroutines.put(face1.getSubroutine(), face1);
		contents.put(face1.getContent(), face1);
	}
	/** �R���e���c����擾 */
	public WebInterface1 getWebInterfaceByContent(String content) {
		return contents.get(content);
	}
	/** ���O����擾 */
	public WebInterface1 getWebInterfaceByName(String name) {
		return webinterfaces.get(name);
	}
	/** �T�u���[�`������擾 */
	public WebInterface1 getWebInterfaceBySubroutine(String subroutine) {
		return subroutines.get(subroutine);
	}
	/**
	 * ���R�[�h���烊�N�G�X�g�ւ̕ϊ�
	 * @param record ���R�[�h
	 * @param request ���N�G�X�g�i���N�G�X�g�̑����ɒl���Z�b�g����j
	 * @throws ServletException ��O�P
	 * @throws IOException ��O�Q
	 * @throws CobolRecordException ��O�R
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
	 * ���N�G�X�g���烌�R�[�h�`���ւ̕ϊ�
	 * @param request ���N�G�X�g
	 * @param record ���R�[�h
	 * @throws ServletException ��O�P
	 * @throws IOException ��O�Q
	 */
	protected void setRequestToRecord(HttpServletRequest request, CobolRecord record) throws ServletException, IOException {
		CobolRecordMetaData meta = null;
		try {
			meta = record.getMetaData();
		} catch (CobolRecordException ex) {
			return;
		}
		@SuppressWarnings("unchecked")
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = request.getParameter(key);
			try {
				int id = meta.findColumn(key);
				CobolColumn column = meta.getColumn(id);
				record.updateObject(column, value);
			} catch (CobolRecordException e) {
				// �񂪌��t����Ȃ������ꍇ�Ȃ�
				// e.printStackTrace();
			}
		}
	}
	/**
	 * �w�b�_�[���R�[�h�̎擾
	 * @param request ���N�G�X�g
	 * @return �w�b�_�[���R�[�h
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
	 * �z��̃��b�p��Ԃ�
	 * @param metaname ���^�f�[�^��
	 * @return �R�{���̃��R�[�h�`��
	 */
	public CobolRecord createRecord(String metaname) {
		CobolRecordMetaData meta = metaSet.getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}
