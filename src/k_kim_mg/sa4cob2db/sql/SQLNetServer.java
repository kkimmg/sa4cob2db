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
 * �����åȥ١����Υ����С���ǽ
 * @author ���줪��
 */
public class SQLNetServer {
	/**
	 * �����åȤ��Ȥμ��յ�ǽ
	 * @author ���줪��
	 */
	class SocketAccepter implements Runnable {
		/** �����С� */
		private SQLNetServer server;
		/** �����С������å� */
		private ServerSocket servsock;
		/**
		 * ���󥹥ȥ饯��
		 * @param server �����С�
		 * @param servsock ���ե��ɥ쥹��ɽ�������С������å�
		 */
		public SocketAccepter(SQLNetServer server, ServerSocket servsock) {
			this.server = server;
			this.servsock = servsock;
		}
		/*
		 * (non-Javadoc)
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
	/** �ǥե���Ȥ�����ե�����̾ */
	public static final String DEFAULT_CONFIG = "/usr/local/etc/acm/metafile.xml";
	/** �����ϥ��֥������� */
	public static final Logger logger;
	/** �������� */
	public static final int LOGIN_FAILURE = 1;
	/** ���������� */
	public static final int LOGIN_OK = 0;
	/** ��������(���å����������С�) */
	public static final int LOGIN_OVERLOAD = 2;
	/** ��̾�� */
	public static final String LOGNAME = "ACMNETSERVERLOG";
	/** ͱͽ�ʤ��ǥ���åȥ����� */
	public static final int SHUTDOWN_ABORT = 1;
	/** �̾�Υ���åȥ����� */
	public static final int SHUTDOWN_NORMAL = 0;
	static {
		logger = Logger.getLogger(LOGNAME);
		logger.setLevel(Level.ALL);
	}
	/**
	 * �Ķ��ѿ����������
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	static String getEnvValue(String key, String defaultValue) {
		String ret = System.getProperty(key, System.getenv(key));
		if (ret == null)
			ret = defaultValue;
		if (ret.length() == 0)
			ret = defaultValue;
		return ret;
	}
	/** ��ư�롼���� */
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
	 * ʸ��������󤫤�ץ�ѥƥ��ؤ��Ѵ�
	 * @param args ʸ���������
	 * @param properties �ץ�ѥƥ�
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
	 * �����ץ�ѥƥ���Ķ��ѿ��ǹ�������
	 * @param properties �����ץ�ѥƥ�
	 * @param key ����
	 * @param envkey �Ķ�����
	 */
	static void updateProperty(Properties properties, String key, String envkey) {
		String env = getEnvValue(envkey, "");
		if (env.length() > 0) {
			properties.setProperty(key, env);
		}
	}
	/** ���ɥ쥹 */
	private List<InetAddress> addresses = new ArrayList<InetAddress>();
	/** �����Ԥΰ��� */
	private Properties adminusers = new Properties();
	/** �Хå��� */
	private int backlog = 0;
	/** ��³���뤫�ɤ��� */
	private boolean cont = true;
	/** �ե����뵡ǽ */
	private SQLFileServer fileServer;
	/** ���å����κ���� */
	private int maxSessions = 0;
	/** �᥿�ǡ����Υ��å� */
	private CobolRecordMetaDataSet metaDataSet;
	/** �ݡ��� */
	private int port = 12345;
	/** ����¾���� */
	private Properties properties;
	/** �����С����٥�ȥꥹ�� */
	private List<ACMServerEventListener> serverListeners = new ArrayList<ACMServerEventListener>();
	/** ���å���󥤥٥�ȥꥹ�� */
	private List<Class<? extends ACMSessionEventListener>> sessionListeners = new ArrayList<Class<? extends ACMSessionEventListener>>();
	/** ���å����ΰ��� */
	private List<ACMSession> sessions = Collections.synchronizedList(new ArrayList<ACMSession>());
	/** �ѥ���ɥ��å� */
	private Properties users;
	/**
	 * ���󥹥ȥ饯��
	 * @param properties �����С��ץ�ѥƥ�
	 */
	public SQLNetServer(Properties propertie, Properties passwords) throws NumberFormatException, UnknownHostException, IOException, SecurityException {
		super();
		properties = propertie;
		users = passwords;
		// //////////////////////////////////////////////////////////
		// �ե����뵡ǽ�κ���
		fileServer = new SQLFileServer();
		// �᥿�ǡ����ե�����̾
		String metaString = getEnvValue("ACM_CONFFILE", DEFAULT_CONFIG);
		File metaFile = new File(metaString);
		SQLNetServer.logger.log(Level.INFO, metaFile.getAbsolutePath());
		// �᥿�ǡ�������μ���
		NodeReadLoader nodeLoader = new NodeReadLoader();
		metaDataSet = fileServer.getMetaDataSet();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaDataSet, properties);
			// �Ķ��ѿ��Ǿ�񤭤���
			// �ǡ����١�����Ϣ
			updateProperty(properties, "jdbcdriverurl", "ACM_JDBCDRIVERURL");
			updateProperty(properties, "jdbcdatabaseurl", "ACM_JDBCDATABASEURL");
			updateProperty(properties, "jdbcusername", "ACM_JDBCUSERNAME");
			updateProperty(properties, "jdbcpassword", "ACM_JDBCPASSWORD");
			// �ͥåȥ����Ϣ
			updateProperty(properties, "port", "ACM_HOSTPORT");
			updateProperty(properties, "backlog", "ACM_BACKLOG");
			updateProperty(properties, "address", "ACM_HOSTNAME");
			updateProperty(properties, "maxsessions", "ACM_MAXSESSIONS");
			// ���ե�����
			updateProperty(properties, "log", "ACM_LOGFILE");
			// ǧ�ڥե�����
			updateProperty(properties, "authfile", "ACM_AUTHFILE");
			updateProperty(properties, "adminfile", "ACM_ADMINFILE");
			// ���٥�Ƚ���
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
		// ��������
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
		// �ǡ����١�����Ϣ������
		if (metaDataSet instanceof SQLCobolRecordMetaDataSet) {
			SQLCobolRecordMetaDataSet sqlset = (SQLCobolRecordMetaDataSet) metaDataSet;
			sqlset.setDriverURL(properties.getProperty("jdbcdriverurl"));
			sqlset.setDatabaseURL(properties.getProperty("jdbcdatabaseurl"));
			sqlset.setUsername(properties.getProperty("jdbcusername"));
			sqlset.setPassword(properties.getProperty("jdbcpassword"));
		}
		// /////////////////////////////////////////////////////////
		// �ͥåȥ��������
		// �ݡ���
		String portString = properties.getProperty("port", "12345");
		port = Integer.parseInt(portString);
		// �Хå���
		String backString = properties.getProperty("backlog", "0");
		backlog = Integer.parseInt(backString);
		// ���ե��ɥ쥹
		String addrString = properties.getProperty("address", "");
		logger.log(Level.FINEST, "address setting is " + addrString);
		if (addrString.trim().length() <= 0) {
			// ������ۥ���
			InetAddress address = InetAddress.getLocalHost();
			logger.log(Level.FINEST, "Adding localhost " + address.getCanonicalHostName());
			addresses.add(address);
		} else {
			String[] addrStrings = addrString.split(",");
			for (int i = 0; i < addrStrings.length; i++) {
				logger.log(Level.FINEST, "Adding address " + addrStrings[i]);
				// ̾���ǻ��ꤹ��
				InetAddress address = InetAddress.getByName(addrStrings[i]);
				addresses.add(address);
			}
		}
		// ���å����κ����
		String maxssessionsString = properties.getProperty("maxsessions", "0");
		int maxsessions = Integer.parseInt(maxssessionsString);
		setMaxSessions(maxsessions);
		// ////////////////////////////////////////////////////////
		// �ѥ���ɤ�����
		String userFile = properties.getProperty("authfile", "");
		if (userFile == "") {
			users.put("", "");// �ǥե���ȥѥ����
			logger.log(Level.CONFIG, "authfile is null. using default password.");
		} else {
			logger.log(Level.CONFIG, "Loading authfile " + userFile + ".");
			FileInputStream fio = new FileInputStream(userFile);
			users.load(fio);
		}
		// ////////////////////////////////////////////////////////
		// �����ԥѥ���ɤ�����
		String adminFile = properties.getProperty("adminfile", "");
		if (userFile == "") {
			logger.log(Level.CONFIG, "adminfile is null.");
		} else {
			logger.log(Level.CONFIG, "Loading adminfile " + adminFile + ".");
			FileInputStream fio = new FileInputStream(adminFile);
			adminusers.load(fio);
		}
		// ////////////////////////////////////////////////////////
		// ���å���󥤥٥�Ȥ�����
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
		// �����С����٥�Ȥ�����
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
		// �����С����٥�Ȥμ¹�
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			try {
				listener.serverStarted(ev);
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
		// ////////////////////////////////////////////////////////
		// �����С���λ����Ͽ
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdownHook();
			}
		});
	}
	/**
	 * ���å������ɲ�
	 * @param session ���å����
	 * @return �����������������ɤ����򼨤�int��
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
				// ���å����¿������
				ret = LOGIN_OVERLOAD;
			}
		} else {
			// ����åȥ�������
			ret = LOGIN_FAILURE;
		}
		return ret;
	}
	/**
	 * �桼����̾�ȥѥ���ɤ��ǧ���ƥ��å������ɲ�
	 * @param session ���å����
	 * @param user �桼����̾
	 * @param password �ѥ����
	 * @return �����������������ɤ����򼨤�int��
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
	 * �����Ԥ�ǧ�ڤ���
	 * @param user �桼����̾
	 * @param password �ѥ����
	 * @return true ����<br>
	 *         false ����
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
	 * ǧ�ڤ���
	 * @param user �桼����̾
	 * @param password �ѥ����
	 * @return true ����<br>
	 *         false ����
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
	 * ���٤ƤΥ��å�����������
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
	 * ���å����κ��
	 * @param session ���å����κ��
	 */
	protected void deleteSession(ACMSession session) {
		sessions.remove(session);
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			listener.sessionRemoved(ev, session);
		}
	}
	/**
	 * �ե������󶡵�ǽ
	 * @return �ե������󶡵�ǽ
	 */
	public SQLFileServer getFileServer() {
		return fileServer;
	}
	/**
	 * ���å����κ����
	 * @return ���å����κ����
	 */
	public int getMaxSessions() {
		return maxSessions;
	}
	/**
	 * �᥿�ǡ����Υ��å�
	 * @return �᥿�ǡ����Υ��å�
	 */
	public CobolRecordMetaDataSet getMetaDataSet() {
		return metaDataSet;
	}
	/**
	 * ���Υ����С��Τ���¾����
	 * @param key ����
	 * @return ��
	 */
	public String getPropertie(String key) {
		return properties.getProperty(key);
	}
	/**
	 * ���Υ����С��Τ���¾����
	 * @param key ����
	 * @param defaultValue �ǥե������
	 * @return ��
	 */
	public String getPropertie(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	/**
	 * ������³����Ƥ��륻�å����ο����������
	 * @return ���å�����
	 */
	int getSessionCount() {
		return sessions.size();
	}
	/**
	 * �����С���λ���٥�Ȥ�ȯ�Ԥ���
	 */
	void serverEnding() {
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			listener.serverEnding(ev);
		}
	}
	/**
	 * ���å����κ����
	 * @param maxSessions ���å����κ����
	 */
	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}
	/**
	 * ���Υ����С��Τ���¾����
	 * @param key ����
	 * @param value ��
	 */
	public void setPropertie(String key, String value) {
		properties.setProperty(key, value);
	}
	/**
	 * ����åȥ����󤹤�
	 * @param mode �⡼��
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
					SQLNetServer.logger.log(Level.WARNING, e.getMessage());
				}
			}
			System.exit(0);
		}
	}
	/**
	 * ������λ����óݤ���
	 */
	void shutdownHook() {
		SQLNetServer.logger.log(Level.INFO, "Now Server Is Ending...");
		deleteAllSessions();
		serverEnding();
	}
	/**
	 * ���Ͻ���
	 * @throws IOException �������㳰
	 */
	public void startServer() throws IOException {
		for (int i = 0; i < addresses.size(); i++) {
			InetAddress address = addresses.get(i);
			// �����С������åȤκ���
			ServerSocket servsock = new ServerSocket(port, backlog, address);
			SocketAccepter accepter = new SocketAccepter(this, servsock);
			Thread th = new Thread(accepter);
			th.start();
			SQLNetServer.logger.log(Level.INFO, "Net Server Stand By..... Port:" + port + " Address:" + servsock.getInetAddress());
		}
	}
}
