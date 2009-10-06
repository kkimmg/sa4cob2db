package k_kim_mg.sa4cob2db.sql;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.FileStatus;
import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

import org.xml.sax.SAXException;
/**
 * JNI�١����Υ����С���ǽ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSQLJNISession implements ACMSession {
	static final String NOT_ASSIGNED = FileStatus.NOT_ASSIGNED.toString();
	private static final long serialVersionUID = 1L;
	/** SQL���å���� */
	ACMSQLSession superobj;
	/** �ɤ߹����оݥ쥳���� */
	byte[] readingRecord = new byte[8192];
	/** �ե����륹�ơ����� */
	byte[] status = new byte[255];
	/**
	 * @return the readingRecord
	 */
	public byte[] getReadingRecord() {
		return readingRecord;
	}
	/**
	 * @return the status
	 */
	public byte[] getStatus() {
		return status;
	}
	/** ���󥹥ȥ饯�� */
	public ACMSQLJNISession() throws Exception {
		super();
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.ACMSession#addACMSessionEventListener(
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener)
	 */
	@Override
	public void addACMSessionEventListener(ACMSessionEventListener listener) {
		superobj.addACMSessionEventListener(listener);
	}
	/**
	 * �ե�����򥢥����󤹤�
	 * @throws IOException �����㳰
	 */
	public void assign(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = createFile(FileName);
		if (file != null) {
			ret = FileStatus.OK;
		} else {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, fileName + "is unknown file.");
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �ե�����򥯥�������
	 */
	public void close(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.close();
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �ȥ�󥶥������򥳥ߥåȤ���
	 * @throws IOException �������㳰
	 */
	public void commitTransaction() {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().commit();
			superobj.callCommitEvent();
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.ACMSession#createFile(java.lang.String)
	 */
	@Override
	public CobolFile createFile(String name) {
		return superobj.createFile(name);
	}
	/**
	 * ���߹Ԥκ��
	 */
	public void delete(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.delete(record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.ACMSession#destroyFile(java.lang.String)
	 */
	@Override
	public void destroyFile(String name) {
		superobj.destroyFile(name);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.ACMSession#getFile(java.lang.String)
	 */
	@Override
	public CobolFile getFile(String name) {
		return superobj.getFile(name);
	}
	/**
	 * �⡼��ʸ���󤫤�int�ͤ�����
	 * @param bytes �⡼�ɤ�ɽ��ʸ����ΥХ�������
	 * @return
	 */
	int getModeBytes2ModeInt(byte[] bmode) {
		String smode = new String(bmode);
		int mode = CobolFile.IS_EQUAL_TO;
		if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN)) {
			mode = CobolFile.IS_GREATER_THAN;
		} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN_OR_EQUAL_TO)) {
			mode = CobolFile.IS_GREATER_THAN_OR_EQUAL_TO;
		} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_NOT_LESS_THAN)) {
			mode = CobolFile.IS_NOT_LESS_THAN;
		} else {
			mode = CobolFile.IS_EQUAL_TO;
		}
		return mode;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.ACMSession#getSessionId()
	 */
	@Override
	public String getSessionId() {
		return superobj.getSessionId();
	}
	/**
	 * ��������ƥ����С�����Ͽ����
	 */
	public void initialize(byte[] acmUsername, byte[] acmPassword) {
		FileStatus ret = FileStatus.OK;
		SQLFileServer sqlfileserver = new SQLFileServer();
		try {
			String filename = SQLNetServer.getEnvValue("ACM_CONFFILE", SQLNetServer.DEFAULT_CONFIG);
			NodeReadLoader nodeLoader = new NodeReadLoader();
			CobolRecordMetaDataSet metaset = sqlfileserver.getMetaDataSet();
			File metaFile = new File(filename);
			Properties properties = new Properties();
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
			// ////////////////////////////////////////////////////////
			// �ѥ���ɤ�����
			Properties users = new Properties();
			String userFile = properties.getProperty("authfile", "");
			if (userFile == "") {
				users.put("", "");// �ǥե���ȥѥ����
				SQLNetServer.logger.log(Level.CONFIG, "authfile is null. using default password.");
			} else {
				SQLNetServer.logger.log(Level.CONFIG, "Loading authfile " + userFile + ".");
				FileInputStream fio = new FileInputStream(userFile);
				users.load(fio);
			}
			superobj = new ACMSQLSession(sqlfileserver);
		} catch (ParserConfigurationException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (FactoryConfigurationError e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (SAXException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (IOException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (Exception e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �����ȥ쥳���ɤΰ�ư
	 */
	public void move(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.move(record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * ���Υ쥳���ɤ�
	 */
	public void next(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.next();
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �ե�����򳫤�
	 */
	public void open(byte[] fileName, byte[] bmode, byte[] baccessmode) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			// ////////////////////////////////////////////////////
			// �����ץ�⡼��
			int mode = -1;
			String modeString = new String(bmode).trim();
			if (modeString.equalsIgnoreCase("INPUT")) {
				// �ɤ߹��ߥ⡼��
				mode = CobolFile.MODE_INPUT;
			} else if (modeString.equalsIgnoreCase("OUTPUT")) {
				// �񤭹��ߥ⡼��
				mode = CobolFile.MODE_OUTPUT;
			} else if (modeString.equalsIgnoreCase("EXTEND")) {
				// �ɵ��⡼��
				mode = CobolFile.MODE_EXTEND;
			} else if (modeString.equalsIgnoreCase("IO")) {
				// �����ϥ⡼��
				mode = CobolFile.MODE_INPUT_OUTPUT;
			}
			// //////////////////////////////////////////////////////
			// ���������⡼��
			int accessmode = -1;
			String accessmodeString = new String(baccessmode).trim();
			if (accessmodeString.equalsIgnoreCase("SEQUENC")) {
				// �祢������
				accessmode = CobolFile.ACCESS_SEQUENCIAL;
			} else if (accessmodeString.equalsIgnoreCase("RANDOM")) {
				// ưŪ��������
				accessmode = CobolFile.ACCESS_RANDOM;
			} else if (accessmodeString.equalsIgnoreCase("DYNAMIC")) {
				// �𥢥�����
				accessmode = CobolFile.ACCESS_DYNAMIC;
			}
			// ////////////////////////////////////////////////////
			// �����ץ�⡼��
			if (mode == CobolFile.MODE_INPUT || mode == CobolFile.MODE_OUTPUT || mode == CobolFile.MODE_EXTEND || mode == CobolFile.MODE_INPUT_OUTPUT) {
				// �⡼�ɤ�������
			} else {
				// �⡼������
				ret = new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file");
				setFileStatus2Bytes(ret, status);
			}
			// //////////////////////////////////////////////////////
			// ���������⡼��
			if (accessmode == CobolFile.ACCESS_SEQUENCIAL || accessmode == CobolFile.ACCESS_DYNAMIC || accessmode == CobolFile.ACCESS_RANDOM) {
				// �⡼�ɤ�������
				ret = file.open(mode, accessmode);
			} else {
				// �⡼������
				ret = new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file");
				setFileStatus2Bytes(ret, status);
			}
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * ���Υ쥳���ɤ�
	 */
	public void previous(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.previous();
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �꡼��
	 */
	public void read(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			// �꡼�ɽ���
			ret = file.read(readingRecord);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * ���Υ쥳���ɤ�
	 * @param fileName
	 * @return
	 */
	public void readNext(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			;
			// �꡼�ɽ���
			ret = file.read(readingRecord);
			if (ret.getStatusCode().equals(FileStatus.STATUS_OK)) {
				// ������
				file.next();
			}
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.ACMSession#removeACMSessionEventListener
	 * (k_kim_mg.sa4cob2db.event.ACMSessionEventListener)
	 */
	@Override
	public void removeACMSessionEventListener(ACMSessionEventListener listener) {
		superobj.removeACMSessionEventListener(listener);
	}
	/**
	 * ����
	 */
	public void rewrite(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.rewrite(record);
		} else {
			// readLine();// ���ߡ��꡼��
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �ȥ�󥶥����������Хå����� �������㳰
	 */
	public void rollbackTransaction() {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().rollback();
			superobj.callRollbackEvent();
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �����ȥ��ߥåȤ����ꤹ��
	 * @throws IOException �������㳰
	 */
	public void setAutoCommit(byte[] commitBytes) {
		String commitString = new String(commitBytes).trim();
		boolean autoCommit = Boolean.parseBoolean(commitString);
		setAutoCommit(autoCommit);
	}
	/**
	 * �����ȥ��ߥåȤ����ꤹ��
	 * @param autoCommit ���ߥåȥ⡼�� �������㳰
	 */
	public void setAutoCommit(boolean autoCommit) {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().setAutoCommit(autoCommit);
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �ե����륹�ơ�������Х��������ž������
	 * @param source �ե����륹�ơ�����
	 * @param dist ���ơ�����
	 */
	void setFileStatus2Bytes(FileStatus source, byte[] dest) {
		String string = source.toString();
		byte[] bytes = string.getBytes();
		System.arraycopy(bytes, 0, dest, 0, (bytes.length < dest.length ? bytes.length : dest.length));
	}
	/**
	 * �ȥ�󥶥������򳫻Ϥ���
	 * @throws IOException �������㳰
	 */
	public void setTransactionLevel(byte[] levelBytes) {
		String levelString = new String(levelBytes).trim();
		int level = Connection.TRANSACTION_NONE;
		if (levelString.equalsIgnoreCase("TRANSACTION_READ_COMMITTED")) {
			level = Connection.TRANSACTION_READ_COMMITTED;
		} else if (levelString.equalsIgnoreCase("TRANSACTION_READ_UNCOMMITTED")) {
			level = Connection.TRANSACTION_READ_UNCOMMITTED;
		} else if (levelString.equalsIgnoreCase("TRANSACTION_REPEATABLE_READ")) {
			level = Connection.TRANSACTION_REPEATABLE_READ;
		} else if (levelString.equalsIgnoreCase("TRANSACTION_SERIALIZABLE")) {
			level = Connection.TRANSACTION_SERIALIZABLE;
		}
		setTransactionLevel(level);
	}
	/**
	 * ���ꤷ���ȥ�󥶥��������ǥ�٥�ǥȥ�󥶥������򳫻Ϥ���
	 * @param level �ȥ�󥶥��������ǥ�٥� �������㳰
	 */
	public void setTransactionLevel(int level) {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().setTransactionIsolation(level);
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �����դ�
	 */
	public void start(byte[] fileName, byte[] modeBytes, byte[] record) {
		int mode = getModeBytes2ModeInt(modeBytes);
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.start(mode, record);
		} else {
			// readLine();// ���ߡ��꡼��
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * �����դ�
	 */
	public void startWith(byte[] fileName, byte[] skey, byte[] modeBytes, byte[] record) {
		int mode = getModeBytes2ModeInt(modeBytes);
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			String skeyName = new String(skey).trim();
			ret = file.start(skeyName, mode, record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * ���å����ν�λ
	 */
	public void terminate() {
		try {
			Enumeration<String> keys = superobj.files.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				destroyFile(key);
				SQLNetServer.logger.log(Level.INFO, "closed..." + key);
			}
		} catch (Exception e3) {
			SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e3);
			setFileStatus2Bytes(FileStatus.FAILURE, status);
			return;
		}
		// ���å�����������ˤ���
		// server.deleteSession(this);
		// ��̥��饹�ν�λ����
		superobj.terminate();
		setFileStatus2Bytes(FileStatus.OK, status);
	}
	/**
	 * �񤭹���
	 */
	public void write(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.write(record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
}
