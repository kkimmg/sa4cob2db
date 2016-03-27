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

/** session */
public class ACMSQLSession implements ACMSession {
	private static final long serialVersionUID = 1L;
	/** SQLConnection */
	protected Connection connection;
	/** files */
	protected Hashtable<String, CobolFile> files;
	/** event listener */
	protected ArrayList<ACMSessionEventListener> listeners = new ArrayList<ACMSessionEventListener>();
	private int maxLength = ACMNetSession.INITIAL_RECORD_LEN;;
	private Properties options;
	/** fileServer */
	private final SQLFileServer server;
	/** sessionID */
	protected String sessionId;

	/**
	 * session
	 * 
	 * @throws Exception exception
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
	 * @see k_kim_mg.sa4cob2db.ACMSession#addACMSessionEventListener(k_kim_mg.sa4cob2db .event.ACMSessionEventListener)
	 */
	public void addACMSessionEventListener(ACMSessionEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * fire event when commit
	 */
	protected void callCommitEvent() {
		ACMSessionEvent e = new ACMSessionEvent(this, null);
		for (ACMSessionEventListener listener : listeners) {
			listener.transactionCommited(e);
		}
	}

	/**
	 * fire event when rollback
	 */
	protected void callRollbackEvent() {
		ACMSessionEvent e = new ACMSessionEvent(this, null);
		for (ACMSessionEventListener listener : listeners) {
			listener.transactionRollbacked(e);
		}
	}

	/**
	 * create custom meta data
	 * 
	 * @param meta meta data
	 * @return file file
	 * @throws Exception exception
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
				if (meta != null) {
					if (meta instanceof SQLCobolRecordMetaData) {
						SQLCobolRecordMetaData sqlmeta = (SQLCobolRecordMetaData) meta;
						ret = createSQLFile(sqlmeta);
					} else if (meta.getCustomFileClassName() != null && meta.getCustomFileClassName().trim().length() > 0) {
						ret = createCustomFile(meta);
					}
				}
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			}
			if (ret != null) {
				ret.bindSession(this);
				files.put(name, ret);
				// evnet listener
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
				// index
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
				// fire events
				ACMSessionEvent e = new ACMSessionEvent(this, ret);
				for (ACMSessionEventListener listener : listeners) {
					listener.fileCreated(e);
				}
			}
		}
		return ret;
	}

	/**
	 * create file object
	 * 
	 * @param meta meta data
	 * @return file
	 */
	private SQLFile createSQLFile(SQLCobolRecordMetaData meta) throws Exception {
		SQLFile ret = null;
		if (meta.getCustomFileClassName() == null || meta.getCustomFileClassName().trim().length() == 0) {
			if (meta instanceof SQLCobolRecordMetaData2) {
				ret = new SQLFile2(getConnection(), (SQLCobolRecordMetaData2)meta);
			} else {
				ret = new SQLFile(getConnection(), meta);
			}
		} else {
			// customfile
			Class<?> claxx = Class.forName(meta.getCustomFileClassName());
			Class<? extends SQLFile> clazz = claxx.asSubclass(SQLFile.class);
			try {
				Constructor<? extends SQLFile> constructor = clazz.getConstructor(new Class<?>[] { Connection.class, CobolRecordMetaData.class });
				ret = constructor.newInstance(new Object[] { getConnection(), meta });
			} catch (NoSuchMethodException e) {
				try {
					Constructor<? extends SQLFile> constructor = clazz.getConstructor(new Class<?>[] { Connection.class, });
					ret = constructor.newInstance(new Object[] { getConnection() });
				} catch (NoSuchMethodException e1) {
					try {
						Constructor<? extends SQLFile> constructor = clazz.getConstructor(new Class<?>[] { CobolRecordMetaData.class });
						ret = constructor.newInstance(new Object[] { meta });
					} catch (NoSuchMethodException e2) {
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
	 * Connection
	 * 
	 * @return Connection
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
	 * list files
	 * 
	 * @return list
	 */
	protected Collection<CobolFile> getFileCollection() {
		return files.values();
	}

	/**
	 * list file names
	 * 
	 * @return list
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
	 * sessionID
	 * 
	 * @return sessionID
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * initialize sessionID
	 */
	private void initializeSessionID() {
		// now 123456789012345
		DecimalFormat df1 = new DecimalFormat("000000000000000");
		long time = System.currentTimeMillis();
		// sequence 12345
		DecimalFormat df2 = new DecimalFormat("00000");
		this.server.sequence++;
		// random 1234567890
		DecimalFormat df3 = new DecimalFormat("0.00000000");
		double rand = Math.random() * 10;
		// text
		String str = df1.format(time) + df2.format(this.server.sequence) + df3.format(rand);
		// bytes
		sessionId = str; // .getBytes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#removeACMSessionEventListener(k_kim_mg. sa4cob2db.event.ACMSessionEventListener)
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