package k_kim_mg.sa4cob2db.sql;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolIndex;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.event.ACMLengthChangedEvent;
import k_kim_mg.sa4cob2db.event.ACMOptionSetEvent;
import k_kim_mg.sa4cob2db.event.ACMSessionEvent;
import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
/** セッション */
public class ACMSQLSession implements ACMSession {
	private static final long serialVersionUID = 1L;
	/** SQLコネクション */
	protected transient Connection connection;
	/** 開いたファイル */
	protected Hashtable<String, CobolFile> files;
	/** イベントリスナ */
	protected ArrayList<ACMSessionEventListener> listeners = new ArrayList<ACMSessionEventListener>();
	private int maxLength;
	private Properties options;
	/** ファイルサーバー */
	private final transient SQLFileServer server;
	/** セッションID */
	protected String sessionId;
	/**
	 * セッションの作成
	 * 
	 * @throws Exception 例外
	 */
	public ACMSQLSession(SQLFileServer server) throws Exception {
		super();
		this.server = server;
		initializeSessionID();
		files = new Hashtable<String, CobolFile>();
		connection = server.createConnection();
		options = new Properties();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.ACMSession#addACMSessionEventListener(k_kim_mg.sa4cob2db
	 * .event.ACMSessionEventListener)
	 */
	public void addACMSessionEventListener(ACMSessionEventListener listener) {
		listeners.add(listener);
	}
	/**
	 * コミットが発生したときにイベントを発生させる
	 */
	protected void callCommitEvent() {
		// セッションのイベント発生
		ACMSessionEvent e = new ACMSessionEvent(this, null);
		for (ACMSessionEventListener listener : listeners) {
			listener.transactionCommited(e);
		}
	}
	/**
	 * ロールバックが発生したときにイベントを発生させる
	 */
	protected void callRollbackEvent() {
		// セッションのイベント発生
		ACMSessionEvent e = new ACMSessionEvent(this, null);
		for (ACMSessionEventListener listener : listeners) {
			listener.transactionRollbacked(e);
		}
	}
	/**
	 * カスタム型のファイルを作成する
	 * 
	 * @param meta メタデータ
	 * @return ファイル
	 * @throws Exception 例外
	 */
	private CobolFile createCustomFile(CobolRecordMetaData meta) throws Exception {
		CobolFile ret = null;
		Class<?> claxx = Class.forName(meta.getCustomFileClassName());
		Class<? extends CobolFile> clazz = claxx.asSubclass(CobolFile.class);
		try {
			Constructor<? extends CobolFile> constructor = clazz.getConstructor(new Class<?>[] { CobolRecordMetaData.class });
			ret = constructor.newInstance(new Object[] { meta });
		} catch (NoSuchMethodException e) {
			ret = clazz.newInstance();
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#createFile(java.lang.String)
	 */
	public CobolFile createFile(String name) {
		CobolFile ret = null;
		if (files.containsKey(name)) {
			ret = files.get(name);
		} else {
			CobolRecordMetaData meta = server.metaDataSet.getMetaData(name);
			try {
				if (meta instanceof SQLCobolRecordMetaData) {
					SQLCobolRecordMetaData sqlmeta = (SQLCobolRecordMetaData) meta;
					ret = createSQLFile(sqlmeta);
				} else if (meta.getCustomFileClassName() != null && meta.getCustomFileClassName().trim().length() > 0) {
					// カスタムクラスを利用する
					ret = createCustomFile(meta);
				}
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			}
			if (ret != null) {
				ret.bindSession(this);
				files.put(name, ret);
				// イベントリスナを登録する
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
				// インデックスを作成する
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
			// セッションのイベント発生
			ACMSessionEvent e = new ACMSessionEvent(this, ret);
			for (ACMSessionEventListener listener : listeners) {
				listener.fileCreated(e);
			}
		}
		return ret;
	}
	/**
	 * SQLFileオブジェクトの作成
	 * 
	 * @param meta メタデータ
	 * @return SQLFileオブジェクト
	 */
	private SQLFile createSQLFile(SQLCobolRecordMetaData meta) throws Exception {
		SQLFile ret = null;
		if (meta.getCustomFileClassName() == null || meta.getCustomFileClassName() == "") {
			// 通常のSQLファイル
			ret = new SQLFile(getConnection(), meta);
		} else {
			// カスタムファイル
			Class<?> claxx = Class.forName(meta.getCustomFileClassName());
			Class<? extends SQLFile> clazz = claxx.asSubclass(SQLFile.class);
			try {
				// 接続, メタデータ
				Constructor<? extends SQLFile> constructor = clazz.getConstructor(new Class<?>[] { Connection.class, CobolRecordMetaData.class });
				ret = constructor.newInstance(new Object[] { getConnection(), meta });
			} catch (NoSuchMethodException e) {
				try {
					// 接続のみ
					Constructor<? extends SQLFile> constructor = clazz.getConstructor(new Class<?>[] { Connection.class, });
					ret = constructor.newInstance(new Object[] { getConnection() });
				} catch (NoSuchMethodException e1) {
					try {
						// メタデータのみ
						Constructor<? extends SQLFile> constructor = clazz.getConstructor(new Class<?>[] { CobolRecordMetaData.class });
						ret = constructor.newInstance(new Object[] { meta });
					} catch (NoSuchMethodException e2) {
						// パラメータなし
						ret = clazz.newInstance();
					}
				}
			}
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * 
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
	@Override
	public String getACMOption(String key) {
		return options.getProperty(key, "");
	}
	/**
	 * コネクション
	 * 
	 * @return コネクション
	 */
	public Connection getConnection() {
		return connection;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#getFile(java.lang.String)
	 */
	public CobolFile getFile(String name) {
		return files.get(name);
	}
	/**
	 * ファイルの一覧
	 * 
	 * @return ファイルの一覧
	 */
	protected Collection<CobolFile> getFileCollection() {
		return files.values();
	}
	/**
	 * ファイル名の一覧
	 * 
	 * @return ファイル名の一覧
	 */
	protected Set<String> getFileNames() {
		return files.keySet();
	}
	@Override
	public int getMaxLength() {
		if (maxLength <= 0) {
			maxLength = ACMNetSession.INITIAL_RECORD_LEN;
		}
		return maxLength;
	}
	/**
	 * セッションID
	 * 
	 * @return セッションID
	 */
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * セッションIDの初期化
	 */
	private void initializeSessionID() {
		// 現在時刻 123456789012345
		DecimalFormat df1 = new DecimalFormat("000000000000000");
		long time = System.currentTimeMillis();
		// シーケンス 12345
		DecimalFormat df2 = new DecimalFormat("00000");
		this.server.sequence++;
		// ランダム 1234567890
		DecimalFormat df3 = new DecimalFormat("0.00000000");
		double rand = Math.random() * 10;
		// 文字列
		String str = df1.format(time) + df2.format(this.server.sequence) + df3.format(rand);
		// バイト配列
		sessionId = str; // .getBytes();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.ACMSession#removeACMSessionEventListener(k_kim_mg.
	 * sa4cob2db.event.ACMSessionEventListener)
	 */
	public void removeACMSessionEventListener(ACMSessionEventListener listener) {
		listeners.remove(listener);
	}
	@Override
	public void setACMOption(String key, String value) {
		options.setProperty(key, value);
		ACMOptionSetEvent e = new ACMOptionSetEvent(this, key, value);
		for (ACMSessionEventListener listener : listeners) {
			listener.optionSetted(e);
		}
	}
	@Override
	public void setMaxLength(int length) {
		int oldLength = getMaxLength();
		if (length <= 0) {
			maxLength = ACMNetSession.INITIAL_RECORD_LEN;
		} else {
			maxLength = length;
		}
		ACMLengthChangedEvent e = new ACMLengthChangedEvent(this, oldLength, maxLength);
		for (ACMSessionEventListener listener : listeners) {
			listener.lengthChanged(e);
		}
	}
	/**
	 * terminate session
	 */
	protected void terminate() {
		server.removeConnection(connection);
		connection = null;
	}
}