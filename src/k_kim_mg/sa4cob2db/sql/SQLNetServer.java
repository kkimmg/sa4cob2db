package k_kim_mg.sa4cob2db.sql;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.event.ACMServerEvent;
import k_kim_mg.sa4cob2db.event.ACMServerEventListener;
import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;
import org.xml.sax.SAXException;
/**
 * provides files by tcp
 * (service / daemon)
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLNetServer {
	/**
	 * accept Socket
	 * 
	 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
	 */
	class SocketAccepter implements Runnable {
		/** Server */
		private SQLNetServer server;
		/** ServerSocket */
		private ServerSocket servsock;
		/**
		 * Constructor
		 * 
		 * @param server Server
		 * @param servsock ServerSocket
		 */
		public SocketAccepter(SQLNetServer server, ServerSocket servsock) {
			this.server = server;
			this.servsock = servsock;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			SQLNetServer.logger.log(Level.INFO, "Now Server Is Running......");
			while (cont) {
				try {
					Socket sock = servsock.accept();
					try {
						ACMSQLNetSession session = new ACMSQLNetSession(server, sock);
						for (Class<? extends ACMSessionEventListener> clazz : sessionListeners) {
							try {
								ACMSessionEventListener listener = clazz.newInstance();
								session.addACMSessionEventListener(listener);
							} catch (Exception e2) {
								SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e2);
							}
						}
						Thread th = new Thread(session);
						th.start();
					} catch (Exception e1) {
						SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e1);
					}
				} catch (IOException e) {
					SQLNetServer.logger.log(Level.SEVERE, "Can't Accept..", e);
					break;
				}
			}
			SQLNetServer.logger.log(Level.INFO, "Now Server Is Ending......");
		}
	}
	/** default filename fo metafile.xml */
	public static final String DEFAULT_CONFIG = "/opt/sa4cob2db/conf/metafile.xml";
	/** Logger */
	public static final Logger logger;
	/** login failure */
	public static final int LOGIN_FAILURE = 1;
	/** login success */
	public static final int LOGIN_OK = 0;
	/** login failure (too many sessions) */
	public static final int LOGIN_OVERLOAD = 2;
	/** LogName */
	public static final String LOGNAME = "ACMNETSERVERLOG";
	/** shutdown abort */
	public static final int SHUTDOWN_ABORT = 1;
	/** shutdown normal */
	public static final int SHUTDOWN_NORMAL = 0;
	static {
		logger = Logger.getLogger(LOGNAME);
		logger.setLevel(Level.SEVERE);
	}
	/**
	 * get environment value
	 * 
	 * @param key key
	 * @param defaultValue default
	 * @return value
	 */
	static String getEnvValue(String key, String defaultValue) {
		String ret = System.getProperty(key, System.getenv(key));
		if (ret == null)
			ret = defaultValue;
		if (ret.length() == 0)
			ret = defaultValue;
		return ret;
	}
	/** Main */
	public static void main(String[] args) {
		Properties properties = new Properties();
		SQLNetServer.setupArguments(args, properties);
		Properties passwords = new Properties();
		try {
			SQLNetServer server = new SQLNetServer(properties, passwords);
			server.startServer();
		} catch (NumberFormatException e) {
			SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
		} catch (UnknownHostException e) {
			SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
		} catch (SecurityException e) {
			SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
		} catch (IOException e) {
			SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
		}
	}
	/**
	 * Main
	 * 
	 * @param metaFile filename
	 */
	public static void main_too(String metaFile) {
		System.setProperty("ACM_CONFFILE", metaFile);
		SQLNetServer.main(new String[] {});
	}
	/**
	 * move String[] to Properties 
	 * 
	 * @param args String[]
	 * @param properties Properties
	 */
	public static void setupArguments(String[] args, Properties properties) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String[] temp = arg.split("=", 2);
			if (temp.length > 0) {
				String left = temp[0];
				if (temp.length > 1) {
					String right = temp[1];
					properties.put(left, right);
					SQLNetServer.logger.log(Level.INFO, "Property:" + left + "=" + right);
				} else {
					properties.put(left, "");
					SQLNetServer.logger.log(Level.INFO, "Property:" + left + "=");
				}
			}
		}
	}
	/**
	 * update inner properties with environment value
	 * 
	 * @param properties Properties
	 * @param key key
	 * @param envkey environment values key
	 */
	public static void updateProperty(Properties properties, String key, String envkey) {
		String env = getEnvValue(envkey, "");
		if (env.length() > 0) {
			properties.setProperty(key, env);
		}
	}
	private List<InetAddress> addresses = new ArrayList<InetAddress>();
	private Properties adminusers = new Properties();
	private int backlog = 0;
	private boolean cont = true;
	private SQLFileServer fileServer;
	private int maxSessions = 0;
	private CobolRecordMetaDataSet metaDataSet;
	private int port = 12345;
	private Properties properties;
	private List<ACMServerEventListener> serverListeners = new ArrayList<ACMServerEventListener>();
	private List<Class<? extends ACMSessionEventListener>> sessionListeners = new ArrayList<Class<? extends ACMSessionEventListener>>();
	private List<ACMSession> sessions = Collections.synchronizedList(new ArrayList<ACMSession>());
	private Properties users;
	/**
	 * Constructor
	 * 
	 * @param propertie Properties
	 * @param passwords password values
	 */
	public SQLNetServer(Properties propertie, Properties passwords) throws NumberFormatException, UnknownHostException, IOException, SecurityException {
		super();
		properties = propertie;
		users = passwords;
		// //////////////////////////////////////////////////////////
		fileServer = new SQLFileServer();
		// meta data filename
		String metaString = getEnvValue("ACM_CONFFILE", DEFAULT_CONFIG);
		File metaFile = new File(metaString);
		SQLNetServer.logger.log(Level.INFO, metaFile.getAbsolutePath());
		// meta data
		NodeReadLoader nodeLoader = new NodeReadLoader();
		metaDataSet = fileServer.getMetaDataSet();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaDataSet, properties);
			// database
			updateProperty(properties, "jdbcdriverurl", "ACM_JDBCDRIVERURL");
			updateProperty(properties, "jdbcdatabaseurl", "ACM_JDBCDATABASEURL");
			updateProperty(properties, "jdbcusername", "ACM_JDBCUSERNAME");
			updateProperty(properties, "jdbcpassword", "ACM_JDBCPASSWORD");
			// net
			updateProperty(properties, "port", "ACM_HOSTPORT");
			updateProperty(properties, "backlog", "ACM_BACKLOG");
			updateProperty(properties, "address", "ACM_HOSTNAME");
			updateProperty(properties, "maxsessions", "ACM_MAXSESSIONS");
			// log
			updateProperty(properties, "log", "ACM_LOGFILE");
			// auth
			updateProperty(properties, "authfile", "ACM_AUTHFILE");
			updateProperty(properties, "adminfile", "ACM_ADMINFILE");
			// event
			updateProperty(properties, "sessionlisteners", "ACM_SESSIONLISTENERS");
			updateProperty(properties, "serverlisteners", "ACM_SERVERLISTENERS");
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (FactoryConfigurationError e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (SAXException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		// /////////////////////////////////////////////////////////
		// log
		String logSetting = properties.getProperty("log", "");
		if (logSetting.trim().length() > 0) {
			try {
				logger.log(Level.CONFIG, "Reading Log Setting From " + logSetting);
				InputStream stream = new FileInputStream(logSetting);
				LogManager manager = LogManager.getLogManager();
				manager.readConfiguration(stream);
			} catch (FileNotFoundException fnfe) {
				logger.log(Level.WARNING, "File Not Found " + logSetting, fnfe);
			}
		}
		// /////////////////////////////////////////////////////////
		// database
		if (metaDataSet instanceof SQLCobolRecordMetaDataSet) {
			SQLCobolRecordMetaDataSet sqlset = (SQLCobolRecordMetaDataSet) metaDataSet;
			sqlset.setDriverURL(properties.getProperty("jdbcdriverurl"));
			sqlset.setDatabaseURL(properties.getProperty("jdbcdatabaseurl"));
			sqlset.setUsername(properties.getProperty("jdbcusername"));
			sqlset.setPassword(properties.getProperty("jdbcpassword"));
		}
		// /////////////////////////////////////////////////////////
		// net
		String portString = properties.getProperty("port", "12345");
		port = Integer.parseInt(portString);
		String backString = properties.getProperty("backlog", "0");
		backlog = Integer.parseInt(backString);
		String addrString = properties.getProperty("address", "");
		logger.log(Level.FINEST, "address setting is " + addrString);
		if (addrString.trim().length() <= 0) {
			// local
			InetAddress address = InetAddress.getLocalHost();
			logger.log(Level.FINEST, "Adding localhost " + address.getCanonicalHostName());
			addresses.add(address);
		} else {
			String[] addrStrings = addrString.split(",");
			for (int i = 0; i < addrStrings.length; i++) {
				logger.log(Level.FINEST, "Adding address " + addrStrings[i]);
				InetAddress address = InetAddress.getByName(addrStrings[i]);
				addresses.add(address);
			}
		}
		// 
		String maxssessionsString = properties.getProperty("maxsessions", "0");
		int maxsessions = Integer.parseInt(maxssessionsString);
		setMaxSessions(maxsessions);
		// ////////////////////////////////////////////////////////
		// password
		String userFile = properties.getProperty("authfile", "");
		if (userFile == "") {
			users.put("", "");// default password
			logger.log(Level.CONFIG, "authfile is null. using default password.");
		} else {
			logger.log(Level.CONFIG, "Loading authfile " + userFile + ".");
			FileInputStream fio = new FileInputStream(userFile);
			users.load(fio);
		}
		// ////////////////////////////////////////////////////////
		// admins
		String adminFile = properties.getProperty("adminfile", "");
		if (adminFile == "") {
			adminusers.put("", "");
			logger.log(Level.CONFIG, "adminfile is null.");
		} else {
			logger.log(Level.CONFIG, "Loading adminfile " + adminFile + ".");
			FileInputStream fio = new FileInputStream(adminFile);
			adminusers.load(fio);
		}
		// ////////////////////////////////////////////////////////
		// sessione vent
		String sessionlisteners = properties.getProperty("sessionlisteners", "");
		logger.log(Level.CONFIG, "sessionlisteners:" + sessionlisteners + ".");
		if (sessionlisteners != "") {
			String[] classnames = sessionlisteners.split(":");
			for (String classname : classnames) {
				try {
					Class<? extends ACMSessionEventListener> clazz = Class.forName(classname).asSubclass(ACMSessionEventListener.class);
					sessionListeners.add(clazz);
				} catch (ClassNotFoundException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
				} catch (ClassCastException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
		// ////////////////////////////////////////////////////////
		// Server event
		String serverlisteners = properties.getProperty("serverlisteners", "");
		logger.log(Level.CONFIG, "serverlisteners:" + serverlisteners + ".");
		if (serverlisteners != "") {
			String[] classnames = serverlisteners.split(":");
			for (String classname : classnames) {
				try {
					Class<? extends ACMServerEventListener> clazz = Class.forName(classname).asSubclass(ACMServerEventListener.class);
					ACMServerEventListener listener = clazz.newInstance();
					serverListeners.add(listener);
				} catch (ClassNotFoundException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
				} catch (ClassCastException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
				} catch (InstantiationException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
				} catch (IllegalAccessException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
		// ////////////////////////////////////////////////////////
		// Serverevent
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			try {
				listener.serverStarted(ev);
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
		// ////////////////////////////////////////////////////////
		// Server event
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdownHook();
			}
		});
	}
	/**
	 * add session
	 * 
	 * @param session session
	 * @return login status
	 */
	int addSession(ACMSession session) {
		int ret = LOGIN_OK;
		int max = getMaxSessions();
		if (cont) {
			if (max <= 0 || getSessionCount() < max) {
				sessions.add(session);
				ACMServerEvent ev = new ACMServerEvent(this);
				for (ACMServerEventListener listener : serverListeners) {
					listener.sessionAdded(ev, session);
				}
			} else {
				// too many session
				ret = LOGIN_OVERLOAD;
			}
		} else {
			// now shutting down
			ret = LOGIN_FAILURE;
		}
		return ret;
	}
	/**
	 * authenthification and add session 
	 * 
	 * @param session session
	 * @param user user name
	 * @param password password
	 * @return login status
	 */
	public int addSession(ACMSession session, String user, String password) {
		int ret = LOGIN_OK;
		boolean login = confirmPassword(user, password);
		if (login) {
			ret = addSession(session);
		} else {
			ret = LOGIN_FAILURE;
		}
		return ret;
	}
	/**
	 * auth admin
	 * 
	 * @param user user name
	 * @param password password
	 * @return true success <br>
	 *         false failure
	 */
	public boolean confirmAdminPassword(String user, String password) {
		boolean ret = false;
		String pass = adminusers.getProperty(user);
		if (pass != null) {
			ret = (pass.equals(password));
		}
		return ret;
	}
	/**
	 * auth
	 * 
	 * @param user user name
	 * @param password password
	 * @return true success <br>
	 *         false failure
	 */
	public boolean confirmPassword(String user, String password) {
		boolean ret = false;
		String pass = users.getProperty(user);
		if (pass != null) {
			ret = (pass.equals(password));
		}
		return ret;
	}
	/**
	 * delete all sessions
	 */
	void deleteAllSessions() {
		List<ACMSession> works = new ArrayList<ACMSession>();
		for (ACMSession session : sessions) {
			works.add(session);
		}
		for (ACMSession session : works) {
			deleteSession(session);
		}
	}
	/**
	 * delete session
	 * 
	 * @param session session
	 */
	protected void deleteSession(ACMSession session) {
		sessions.remove(session);
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			listener.sessionRemoved(ev, session);
		}
	}
	/**
	 * file提供機能
	 * 
	 * @return file提供機能
	 */
	public SQLFileServer getFileServer() {
		return fileServer;
	}
	/**
	 * sessionの最大数
	 * 
	 * @return sessionの最大数
	 */
	public int getMaxSessions() {
		return maxSessions;
	}
	/**
	 * meta dataのセット
	 * 
	 * @return meta dataのセット
	 */
	public CobolRecordMetaDataSet getMetaDataSet() {
		return metaDataSet;
	}
	/**
	 * このServerのその他の値
	 * 
	 * @param key キー
	 * @return 値
	 */
	public String getPropertie(String key) {
		return properties.getProperty(key);
	}
	/**
	 * このServerのその他の値
	 * 
	 * @param key キー
	 * @param defaultValue default 値
	 * @return 値
	 */
	public String getPropertie(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	/**
	 * 現在connectionされているsessionの数を取得する
	 * 
	 * @return session数
	 */
	int getSessionCount() {
		return sessions.size();
	}
	/**
	 * Server終了eventを発行する
	 */
	void serverEnding() {
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			listener.serverEnding(ev);
		}
	}
	/**
	 * sessionの最大数
	 * 
	 * @param maxSessions sessionの最大数
	 */
	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}
	/**
	 * このServerのその他の値
	 * 
	 * @param key キー
	 * @param value 値
	 */
	public void setPropertie(String key, String value) {
		properties.setProperty(key, value);
	}
	/**
	 * シャットダウンする
	 * 
	 * @param mode モード
	 */
	public void shutdown(int mode) {
		if (mode == SHUTDOWN_ABORT) {
			System.exit(0);
		} else {
			cont = false;
			while (sessions.size() > 0) {
				try {
					wait(1000);
				} catch (InterruptedException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
			System.exit(0);
		}
	}
	/**
	 * 強制終了を引っ掛ける
	 */
	void shutdownHook() {
		SQLNetServer.logger.log(Level.INFO, "Now Server Is Ending...");
		deleteAllSessions();
		serverEnding();
	}
	/**
	 * 開始処理
	 * 
	 * @throws IOException IO Exception
	 */
	public void startServer() throws IOException {
		for (int i = 0; i < addresses.size(); i++) {
			InetAddress address = addresses.get(i);
			// ServerSocketの作成
			ServerSocket servsock = new ServerSocket(port, backlog, address);
			SocketAccepter accepter = new SocketAccepter(this, servsock);
			Thread th = new Thread(accepter);
			th.start();
			SQLNetServer.logger.log(Level.INFO, "Net Server Stand By..... Port:" + port + " Address:" + servsock.getInetAddress());
		}
	}
}
