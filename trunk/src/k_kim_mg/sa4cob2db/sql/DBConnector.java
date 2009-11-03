package k_kim_mg.sa4cob2db.sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
public class DBConnector {
	/** データベースURL */
	private String databaseURL = "";
	/** デフォルトのコネクション */
	private Connection dfCnnct = null;
	/** ドライバURL */
	private String driverURL = "";
	/** これまでに開いたコネクション */
	private List<Connection> openedConnects = new ArrayList<Connection>();
	/** パスワード */
	private String password = "";
	/** ユーザー名 */
	private String username = "";
	/**
	 * コンストラクタ
	 */
	public DBConnector() {
		super();
	}
	/**
	 * コンストラクタ
	 * @param driverURL JDBCドライバのURL
	 * @param databaseURL JDBCデータソースのURL
	 * @param userName JDBCユーザー名
	 * @param passWord JDBCパスワード
	 */
	public DBConnector(String driverURL, String databaseURL, String userName, String passWord) {
		this();
		this.driverURL = driverURL;
		this.databaseURL = databaseURL;
		this.username = userName;
		this.password = passWord;
	}
	/**
	 * 全ての接続を削除
	 */
	public void clearAllConnections() {
		SQLNetServer.logger.log(Level.INFO, "全ての接続を削除します");
		try {
			for (int i = 0; i < openedConnects.size(); i++) {
				try {
					Connection wc = openedConnects.get(i);
					if (!(wc.isClosed())) {
						SQLNetServer.logger.log(Level.INFO, "接続を解除します");
						wc.close();
					} else {
						SQLNetServer.logger.log(Level.INFO, "すでに解除されています");
					}
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clearConnections();
		}
	}
	/**
	 * 無駄な接続の削除
	 */
	public void clearConnections() {
		try {
			for (int i = 0; i < openedConnects.size(); i++) {
				try {
					Connection wc = openedConnects.get(i);
					if (wc.isClosed()) {
						SQLNetServer.logger.log(Level.INFO, "接続を削除します");
						openedConnects.remove(i);
						i--;
					}
				} catch (SQLException se) {
					SQLNetServer.logger.log(Level.WARNING, "接続状態を確認できません");
					se.printStackTrace();
				}
			}
		} catch (ArrayIndexOutOfBoundsException aie) {
			SQLNetServer.logger.log(Level.SEVERE, "ロジックエラー");
			aie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 接続の作成
	 * @return データベースへの接続
	 * @exception 例外の詳細な定義は行わない
	 */
	public Connection createConnection() throws ClassNotFoundException, SQLException {
		return createConnection(false);
	}
	/**
	 * 接続の作成
	 * @param forth true 常に新たに接続を作成する<br/>
	 *               false 既に接続が存在したら新たに接続を作成しない。
	 * @return データベースへの接続
	 * @exception 例外の詳細な定義は行わない
	 */
	public Connection createConnection(boolean forth) throws ClassNotFoundException, SQLException {
		Connection retValue = null;
		if (!(forth)) {
			if (dfCnnct == null) {
				dfCnnct = createConnection(driverURL, databaseURL, username, password);
			} else if (dfCnnct.isClosed()) {
				dfCnnct = createConnection(driverURL, databaseURL, username, password);
			}
			retValue = dfCnnct;
		} else {
			retValue = createConnection(driverURL, databaseURL, username, password);
		}
		SQLNetServer.logger.log(Level.INFO, "Connecting:" + driverURL + ":" + databaseURL + ":" + username + ":" + password + ":" + forth);
		return retValue;
	}
	/**
	 * 接続の作成
	 * @return データベースへの接続
	 * @param driverURL JDBCドライバのURL
	 * @param databaseURL JDBCデータソースのURL
	 * @param userName JDBCユーザー名
	 * @param passWord JDBCパスワード
	 * @exception 例外の詳細な定義は行わない
	 */
	public Connection createConnection(String driverURL, String databaseURL, String userName, String passWord) throws ClassNotFoundException, SQLException {
		Connection retValue = null;
		// ドライバの読み込み
		Class.forName(driverURL);
		// データベースへの接続
		retValue = DriverManager.getConnection(databaseURL, userName, passWord);
		// 接続を保持する
		openedConnects.add(retValue);
		SQLNetServer.logger.log(Level.INFO, "Connecting:" + driverURL + ":" + databaseURL + ":" + username + ":" + password);
		// 値を返す
		return retValue;
	}
	/**
	 * コネクションを開放する
	 * @param connection 開放する接続
	 */
	public void removeConnection (Connection connection) throws SQLException {
		if (connection == null) return;
		if (!connection.isClosed()) {
			connection.close();
		}
		if (openedConnects.contains(connection)) {
			openedConnects.remove(connection);
		}
	}
	/**
	 * これはやっていいのか?
	 * @exception Throwable 親クラスで宣言されている
	 */
	protected void finalyze() throws Throwable {
		clearAllConnections();
		super.finalize();
	}
	/**
	 * 接続をすべて取得する
	 * @return 接続すべて
	 */
	public Iterator<Connection> getConnections() {
		return openedConnects.listIterator();
	}
	/**
	 * データベースURL
	 * @return データベースURL
	 */
	public String getDatabaseURL() {
		return databaseURL;
	}
	/**
	 * ドライバURL
	 * @return ドライバURL
	 */
	public String getDriverURL() {
		return driverURL;
	}
	/**
	 * データベースパスワード
	 * @return データベースパスワード
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * データベースユーザー名
	 * @return データベースユーザー名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * データベースURL
	 * @param string データベースURL
	 */
	public void setDatabaseURL(String string) {
		databaseURL = string;
	}
	/**
	 * ドライバURL
	 * @param string ドライバURL
	 */
	public void setDriverURL(String string) {
		driverURL = string;
	}
	/**
	 * データベースパスワード
	 * @param string データベースパスワード
	 */
	public void setPassword(String string) {
		password = string;
	}
	/**
	 * データベースユーザー名
	 * @param string データベースユーザー名
	 */
	public void setUsername(String string) {
		username = string;
	}
}
