package k_kim_mg.sa4cob2db.sql;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolIndex;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.event.ACMSessionEvent;
import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
/** ���å���� */
public class ACMSQLSession implements ACMSession {
    private static final long serialVersionUID = 1L;
	/** SQL���ͥ������ */
	protected transient Connection connection;
	/** �������ե����� */
	protected Hashtable<String, CobolFile> files;
	/** ���٥�ȥꥹ�� */
	protected ArrayList<ACMSessionEventListener> listeners = new ArrayList<ACMSessionEventListener>();
	/** �ե����륵���С� */
	private final transient SQLFileServer server;
	/** ���å����ID */
	protected String sessionId;
	/**
	 * ���å����κ���
	 * @throws Exception �㳰
	 */
	public ACMSQLSession(SQLFileServer server) throws Exception {
		super();
		this.server = server;
		initializeSessionID();
		files = new Hashtable<String, CobolFile>();
		connection = server.createConnection();
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.ACMSession#addACMSessionEventListener(k_kim_mg.sa4cob2db.event.ACMSessionEventListener)
	 */
	public void addACMSessionEventListener(ACMSessionEventListener listener) {
		listeners.add(listener);
	}
	/**
	 * ���ߥåȤ�ȯ�������Ȥ��˥��٥�Ȥ�ȯ��������
	 */
	protected void callCommitEvent () {
		// ���å����Υ��٥��ȯ��
		ACMSessionEvent e = new ACMSessionEvent(this, null);
		for (ACMSessionEventListener listener : listeners) {
            listener.transactionCommited(e);
		}
	}	
	/**
	 * ����Хå���ȯ�������Ȥ��˥��٥�Ȥ�ȯ��������
	 */
	protected void callRollbackEvent () {
		// ���å����Υ��٥��ȯ��
		ACMSessionEvent e = new ACMSessionEvent(this, null);
		for (ACMSessionEventListener listener : listeners) {
            listener.transactionRollbacked(e);
		}
	}	
	/**
	 * �������෿�Υե�������������
	 * @param meta �᥿�ǡ���
	 * @return �ե�����
	 * @throws Exception �㳰
	 */
	private CobolFile createCustomFile (CobolRecordMetaData meta) throws Exception {
		CobolFile ret = null;
		Class<?> claxx = Class.forName(meta.getCustomFileClassName());
		Class<? extends CobolFile> clazz = claxx.asSubclass(CobolFile.class);
		try {
			Constructor<? extends CobolFile> constructor =
				clazz.getConstructor(new Class<?>[] {CobolRecordMetaData.class});
			ret = constructor.newInstance(new Object[] {meta});
		} catch (NoSuchMethodException e) {
			ret = clazz.newInstance();
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.ACMSession#createFile(java.lang.String)
	 */
	public CobolFile createFile(String name) {
		CobolFile ret = null;
		CobolRecordMetaData meta = server.metaDataSet.getMetaData(name);
		try {
			if (meta instanceof SQLCobolRecordMetaData) {
				SQLCobolRecordMetaData sqlmeta = (SQLCobolRecordMetaData) meta;
				ret = createSQLFile(sqlmeta);
			} else 	if (meta.getCustomFileClassName() != null && meta.getCustomFileClassName().trim().length() > 0) {
				// �������९�饹�����Ѥ���
				ret = createCustomFile(meta);
			}
		} catch (Exception e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
		if (ret != null) {
			ret.bindSession(this);
			files.put(name, ret);
			// ���٥�ȥꥹ�ʤ���Ͽ����
			List<Class<? extends CobolFileEventListener>> listenerClasses = meta.getListenerClasses();
			for (Class<? extends CobolFileEventListener> listenerClass : listenerClasses) {
				CobolFileEventListener listener;
				try {
					listener = listenerClass.newInstance();
					ret.addCobolFileEventListener(listener);
				} catch (Exception e) {
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			// ����ǥå������������
			List<CobolIndex> indexes = meta.getCobolIndexes();
			if (indexes != null) {
				for (CobolIndex index : indexes) {
					try {
						String indexFileName = index.getFileName();
						CobolFile indexFile = createFile(indexFileName);
						ret.addIndex(index, indexFile);
					} catch (Exception e) {
						SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
					} 
				}
			}
		}
		// ���å����Υ��٥��ȯ��
		ACMSessionEvent e = new ACMSessionEvent(this, ret);
		for (ACMSessionEventListener listener : listeners) {
            listener.fileCreated(e);
		}
		return ret;
	}
	/**
	 * SQLFile���֥������Ȥκ���
	 * @param meta �᥿�ǡ���
	 * @return SQLFile���֥�������
	 */
	private SQLFile createSQLFile(SQLCobolRecordMetaData meta) throws Exception {
		SQLFile ret = null;
		if (meta.getCustomFileClassName() == null ||
			meta.getCustomFileClassName() == "") {
			// �̾��SQL�ե�����
			ret = new SQLFile(getConnection(), meta);
		} else {
			// ��������ե�����
			Class<?> claxx = Class.forName(meta.getCustomFileClassName());
			Class<? extends SQLFile> clazz = claxx.asSubclass(SQLFile.class);
			try {
				// ��³, �᥿�ǡ���
				Constructor<? extends SQLFile> constructor =
					clazz.getConstructor(new Class<?>[] {
							Connection.class,
							CobolRecordMetaData.class
							});
				ret = constructor.newInstance(new Object[] {getConnection(), meta});
			} catch (NoSuchMethodException e) {
				try {
					// ��³�Τ�
					Constructor<? extends SQLFile> constructor =
						clazz.getConstructor(new Class<?>[] {
								Connection.class,
								});
					ret = constructor.newInstance(new Object[] {getConnection()});
				} catch (NoSuchMethodException e1) {
					try {
						// �᥿�ǡ����Τ�
						Constructor<? extends SQLFile> constructor =
							clazz.getConstructor(new Class<?>[] {
									CobolRecordMetaData.class
									});
						ret = constructor.newInstance(new Object[] {meta});
					} catch (NoSuchMethodException e2) {
						// �ѥ�᡼���ʤ�
						ret = clazz.newInstance();
					}
				}
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.ACMSession#destroyFile(java.lang.String)
	 */
	public void destroyFile(String name) {
		CobolFile file = files.get(name);
		if (file != null) {
			if (file.isOpened()) {
				file.close();
			}
			ACMSessionEvent e = new ACMSessionEvent(this, file);
			for (ACMSessionEventListener listener : listeners) {
				listener.fileDestroyed(e);
			}
		}
		files.remove(name);
	}
	/**
	 * ���ͥ������
	 * @return ���ͥ������
	 */
	public Connection getConnection() {
		return connection;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.ACMSession#getFile(java.lang.String)
	 */
	public CobolFile getFile(String name) {
		return files.get(name);
	}
	/**
	 * ���å����ID
	 * @return ���å����ID
	 */
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * ���å����ID�ν����
	 */
	private void initializeSessionID() {
		// ���߻��� 123456789012345
		DecimalFormat df1 = new DecimalFormat("000000000000000");
		long time = System.currentTimeMillis();
		// �������� 12345
		DecimalFormat df2 = new DecimalFormat("00000");
		this.server.sequence++;
		// ������ 1234567890
		DecimalFormat df3 = new DecimalFormat("0.00000000");
		double rand = Math.random() * 10;
		// ʸ����
		String str = df1.format(time) + df2.format(this.server.sequence) + df3.format(rand);
		// �Х�������
		sessionId = str; // .getBytes();
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.ACMSession#removeACMSessionEventListener(k_kim_mg.sa4cob2db.event.ACMSessionEventListener)
	 */
	public void removeACMSessionEventListener(ACMSessionEventListener listener) {
		listeners.remove(listener);
	}
	/**
	 * ���å����ν�λ
	 */
	protected void terminate () {
		server.removeConnection(connection);
		connection = null;
	}
}