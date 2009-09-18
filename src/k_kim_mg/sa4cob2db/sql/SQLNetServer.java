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
 * ソケットベースのサーバー機能
 * @author おれおれ
 */
public class SQLNetServer {
	/**
	 * ソケットごとの受付機能
	 * @author おれおれ
	 */
	class SocketAccepter implements Runnable {
		/** サーバー */
		private SQLNetServer server;
		/** サーバーソケット */
		private ServerSocket servsock;
		/**
		 * コンストラクタ
		 * @param server サーバー
		 * @param servsock 受付アドレスを表すサーバーソケット
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
	/** デフォルトの設定ファイル名 */
	public static final String DEFAULT_CONFIG = "/usr/local/etc/acm/metafile.xml";
	/** ログ出力オブジェクト */
	public static final Logger logger;
	/** ログイン失敗 */
	public static final int LOGIN_FAILURE = 1;
	/** ログイン成功 */
	public static final int LOGIN_OK = 0;
	/** ログイン失敗(セッション数オーバー) */
	public static final int LOGIN_OVERLOAD = 2;
	/** ログ名称 */
	public static final String LOGNAME = "ACMNETSERVERLOG";
	/** 猶予なしでシャットダウン */
	public static final int SHUTDOWN_ABORT = 1;
	/** 通常のシャットダウン */
	public static final int SHUTDOWN_NORMAL = 0;
	static {
		logger = Logger.getLogger(LOGNAME);
		logger.setLevel(Level.ALL);
	}
	/**
	 * 環境変数を取得する
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
	/** 起動ルーチン */
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
	 * 文字列の配列からプロパティへの変換
	 * @param args 文字列の配列
	 * @param properties プロパティ
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
	 * 内部プロパティを環境変数で更新する
	 * @param properties 内部プロパティ
	 * @param key キー
	 * @param envkey 環境キー
	 */
	static void updateProperty(Properties properties, String key, String envkey) {
		String env = getEnvValue(envkey, "");
		if (env.length() > 0) {
			properties.setProperty(key, env);
		}
	}
	/** アドレス */
	private List<InetAddress> addresses = new ArrayList<InetAddress>();
	/** 管理者の一覧 */
	private Properties adminusers = new Properties();
	/** バックログ */
	private int backlog = 0;
	/** 継続するかどうか */
	private boolean cont = true;
	/** ファイル機能 */
	private SQLFileServer fileServer;
	/** セッションの最大数 */
	private int maxSessions = 0;
	/** メタデータのセット */
	private CobolRecordMetaDataSet metaDataSet;
	/** ポート */
	private int port = 12345;
	/** その他の値 */
	private Properties properties;
	/** サーバーイベントリスナ */
	private List<ACMServerEventListener> serverListeners = new ArrayList<ACMServerEventListener>();
	/** セッションイベントリスナ */
	private List<Class<? extends ACMSessionEventListener>> sessionListeners = new ArrayList<Class<? extends ACMSessionEventListener>>();
	/** セッションの一覧 */
	private List<ACMSession> sessions = Collections.synchronizedList(new ArrayList<ACMSession>());
	/** パスワードセット */
	private Properties users;
	/**
	 * コンストラクタ
	 * @param properties サーバープロパティ
	 */
	public SQLNetServer(Properties propertie, Properties passwords) throws NumberFormatException, UnknownHostException, IOException, SecurityException {
		super();
		properties = propertie;
		users = passwords;
		// //////////////////////////////////////////////////////////
		// ファイル機能の作成
		fileServer = new SQLFileServer();
		// メタデータファイル名
		String metaString = getEnvValue("ACM_CONFFILE", DEFAULT_CONFIG);
		File metaFile = new File(metaString);
		SQLNetServer.logger.log(Level.INFO, metaFile.getAbsolutePath());
		// メタデータ情報の取得
		NodeReadLoader nodeLoader = new NodeReadLoader();
		metaDataSet = fileServer.getMetaDataSet();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaDataSet, properties);
			// 環境変数で上書きする
			// データベース関連
			updateProperty(properties, "jdbcdriverurl", "ACM_JDBCDRIVERURL");
			updateProperty(properties, "jdbcdatabaseurl", "ACM_JDBCDATABASEURL");
			updateProperty(properties, "jdbcusername", "ACM_JDBCUSERNAME");
			updateProperty(properties, "jdbcpassword", "ACM_JDBCPASSWORD");
			// ネットワーク関連
			updateProperty(properties, "port", "ACM_HOSTPORT");
			updateProperty(properties, "backlog", "ACM_BACKLOG");
			updateProperty(properties, "address", "ACM_HOSTNAME");
			updateProperty(properties, "maxsessions", "ACM_MAXSESSIONS");
			// ログファイル
			updateProperty(properties, "log", "ACM_LOGFILE");
			// 認証ファイル
			updateProperty(properties, "authfile", "ACM_AUTHFILE");
			updateProperty(properties, "adminfile", "ACM_ADMINFILE");
			// イベント処理
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
		// ログの設定
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
		// データベース関連の設定
		if (metaDataSet instanceof SQLCobolRecordMetaDataSet) {
			SQLCobolRecordMetaDataSet sqlset = (SQLCobolRecordMetaDataSet) metaDataSet;
			sqlset.setDriverURL(properties.getProperty("jdbcdriverurl"));
			sqlset.setDatabaseURL(properties.getProperty("jdbcdatabaseurl"));
			sqlset.setUsername(properties.getProperty("jdbcusername"));
			sqlset.setPassword(properties.getProperty("jdbcpassword"));
		}
		// /////////////////////////////////////////////////////////
		// ネットワークの設定
		// ポート
		String portString = properties.getProperty("port", "12345");
		port = Integer.parseInt(portString);
		// バックログ
		String backString = properties.getProperty("backlog", "0");
		backlog = Integer.parseInt(backString);
		// 受付アドレス
		String addrString = properties.getProperty("address", "");
		logger.log(Level.FINEST, "address setting is " + addrString);
		if (addrString.trim().length() <= 0) {
			// ローカルホスト
			InetAddress address = InetAddress.getLocalHost();
			logger.log(Level.FINEST, "Adding localhost " + address.getCanonicalHostName());
			addresses.add(address);
		} else {
			String[] addrStrings = addrString.split(",");
			for (int i = 0; i < addrStrings.length; i++) {
				logger.log(Level.FINEST, "Adding address " + addrStrings[i]);
				// 名前で指定する
				InetAddress address = InetAddress.getByName(addrStrings[i]);
				addresses.add(address);
			}
		}
		// セッションの最大数
		String maxssessionsString = properties.getProperty("maxsessions", "0");
		int maxsessions = Integer.parseInt(maxssessionsString);
		setMaxSessions(maxsessions);
		// ////////////////////////////////////////////////////////
		// パスワードの設定
		String userFile = properties.getProperty("authfile", "");
		if (userFile == "") {
			users.put("", "");// デフォルトパスワード
			logger.log(Level.CONFIG, "authfile is null. using default password.");
		} else {
			logger.log(Level.CONFIG, "Loading authfile " + userFile + ".");
			FileInputStream fio = new FileInputStream(userFile);
			users.load(fio);
		}
		// ////////////////////////////////////////////////////////
		// 管理者パスワードの設定
		String adminFile = properties.getProperty("adminfile", "");
		if (userFile == "") {
			logger.log(Level.CONFIG, "adminfile is null.");
		} else {
			logger.log(Level.CONFIG, "Loading adminfile " + adminFile + ".");
			FileInputStream fio = new FileInputStream(adminFile);
			adminusers.load(fio);
		}
		// ////////////////////////////////////////////////////////
		// セッションイベントの設定
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
		// サーバーイベントの設定
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
		// サーバーイベントの実行
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			try {
				listener.serverStarted(ev);
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
		// ////////////////////////////////////////////////////////
		// サーバー終了の登録
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdownHook();
			}
		});
	}
	/**
	 * セッションの追加
	 * @param session セッション
	 * @return ログインが成功したかどうかを示すint値
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
				// セッションが多すぎる
				ret = LOGIN_OVERLOAD;
			}
		} else {
			// シャットダウン中
			ret = LOGIN_FAILURE;
		}
		return ret;
	}
	/**
	 * ユーザー名とパスワードを確認してセッションの追加
	 * @param session セッション
	 * @param user ユーザー名
	 * @param password パスワード
	 * @return ログインが成功したかどうかを示すint値
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
	 * 管理者を認証する
	 * @param user ユーザー名
	 * @param password パスワード
	 * @return true 成功<br>
	 *         false 失敗
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
	 * 認証する
	 * @param user ユーザー名
	 * @param password パスワード
	 * @return true 成功<br>
	 *         false 失敗
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
	 * すべてのセッションを削除する
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
	 * セッションの削除
	 * @param session セッションの削除
	 */
	protected void deleteSession(ACMSession session) {
		sessions.remove(session);
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			listener.sessionRemoved(ev, session);
		}
	}
	/**
	 * ファイル提供機能
	 * @return ファイル提供機能
	 */
	public SQLFileServer getFileServer() {
		return fileServer;
	}
	/**
	 * セッションの最大数
	 * @return セッションの最大数
	 */
	public int getMaxSessions() {
		return maxSessions;
	}
	/**
	 * メタデータのセット
	 * @return メタデータのセット
	 */
	public CobolRecordMetaDataSet getMetaDataSet() {
		return metaDataSet;
	}
	/**
	 * このサーバーのその他の値
	 * @param key キー
	 * @return 値
	 */
	public String getPropertie(String key) {
		return properties.getProperty(key);
	}
	/**
	 * このサーバーのその他の値
	 * @param key キー
	 * @param defaultValue デフォルト値
	 * @return 値
	 */
	public String getPropertie(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	/**
	 * 現在接続されているセッションの数を取得する
	 * @return セッション数
	 */
	int getSessionCount() {
		return sessions.size();
	}
	/**
	 * サーバー終了イベントを発行する
	 */
	void serverEnding() {
		ACMServerEvent ev = new ACMServerEvent(this);
		for (ACMServerEventListener listener : serverListeners) {
			listener.serverEnding(ev);
		}
	}
	/**
	 * セッションの最大数
	 * @param maxSessions セッションの最大数
	 */
	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}
	/**
	 * このサーバーのその他の値
	 * @param key キー
	 * @param value 値
	 */
	public void setPropertie(String key, String value) {
		properties.setProperty(key, value);
	}
	/**
	 * シャットダウンする
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
					SQLNetServer.logger.log(Level.WARNING, e.getMessage());
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
	 * @throws IOException 入出力例外
	 */
	public void startServer() throws IOException {
		for (int i = 0; i < addresses.size(); i++) {
			InetAddress address = addresses.get(i);
			// サーバーソケットの作成
			ServerSocket servsock = new ServerSocket(port, backlog, address);
			SocketAccepter accepter = new SocketAccepter(this, servsock);
			Thread th = new Thread(accepter);
			th.start();
			SQLNetServer.logger.log(Level.INFO, "Net Server Stand By..... Port:" + port + " Address:" + servsock.getInetAddress());
		}
	}
}
