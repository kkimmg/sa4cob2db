package k_kim_mg.sa4cob2db.sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
public class DBConnector {
	private String databaseURL = "";
	private Connection dfCnnct = null;
	private String driverURL = "";
	private List<Connection> openedConnects = new ArrayList<Connection>();
	private String password = "";
	private String username = "";
	/**
	 * Constructor
	 */
	public DBConnector() {
		super();
	}
	/**
	 * Constructor
	 * 
	 * @param driverURL JDBCDriverURL
	 * @param databaseURL JDBCDatasourceURL
	 * @param userName JDBCuser name
	 * @param passWord JDBCpassword
	 */
	public DBConnector(String driverURL, String databaseURL, String userName, String passWord) {
		this();
		this.driverURL = driverURL;
		this.databaseURL = databaseURL;
		this.username = userName;
		this.password = passWord;
	}
	/**
	 * clear all connections
	 */
	public void clearAllConnections() {
		SQLNetServer.logger.log(Level.INFO, "closing all connections");
		try {
			for (int i = 0; i < openedConnects.size(); i++) {
				try {
					Connection wc = openedConnects.get(i);
					if (!(wc.isClosed())) {
						SQLNetServer.logger.log(Level.INFO, "closing...");
						wc.close();
					} else {
						SQLNetServer.logger.log(Level.INFO, "already closed");
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
	 * remove closed connections
	 */
	public void clearConnections() {
		try {
			for (int i = 0; i < openedConnects.size(); i++) {
				try {
					Connection wc = openedConnects.get(i);
					if (wc.isClosed()) {
						SQLNetServer.logger.log(Level.INFO, "removing");
						openedConnects.remove(i);
						i--;
					}
				} catch (SQLException se) {
					SQLNetServer.logger.log(Level.WARNING, se.getMessage());
					se.printStackTrace();
				}
			}
		} catch (ArrayIndexOutOfBoundsException aie) {
			SQLNetServer.logger.log(Level.SEVERE, aie.getMessage());
			aie.printStackTrace();
		} catch (Exception e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * create Connection
	 * 
	 * @return connection
	 * @throws ClassNotFoundException can't find class
	 * @throws SQLException other exception
	 */
	public Connection createConnection() throws ClassNotFoundException, SQLException {
		return createConnection(false);
	}
	/**
	 * create connection
	 * 
	 * @param forth true return new connection<br/>
	 *            false return exist connection
	 * 
	 * @throws ClassNotFoundException can't find class
	 * @throws SQLException sql exception
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
		return retValue;
	}
	/**
	 * create connection
	 * 
	 * @return connection
	 * @param driverURL JDBCdriverURL
	 * @param databaseURL JDBCdatasourceURL
	 * @param userName JDBC user name
	 * @param passWord JDBC password
	 * @throws ClassNotFoundException can't find class
	 * @throws SQLException sql exception
	 */
	public Connection createConnection(String driverURL, String databaseURL, String userName, String passWord) throws ClassNotFoundException, SQLException {
		Connection retValue = null;
		Class.forName(driverURL);
		retValue = DriverManager.getConnection(databaseURL, userName, passWord);
		openedConnects.add(retValue);
		return retValue;
	}
	/**
	 * close Connection
	 * 
	 * @param connection connection
	 */
	public void removeConnection(Connection connection) throws SQLException {
		if (connection == null)
			return;
		if (!connection.isClosed()) {
			connection.close();
		}
		if (openedConnects.contains(connection)) {
			openedConnects.remove(connection);
		}
	}
	/**
	 * close all connection
	 * 
	 * @exception Throwable all
	 */
	protected void finalyze() throws Throwable {
		clearAllConnections();
		super.finalize();
	}
	/**
	 * connections
	 * 
	 * @return connections
	 */
	public Iterator<Connection> getConnections() {
		return openedConnects.listIterator();
	}
	/**
	 * databaseURL
	 * 
	 * @return databaseURL
	 */
	public String getDatabaseURL() {
		return databaseURL;
	}
	/**
	 * driver URL
	 * 
	 * @return driver URL
	 */
	public String getDriverURL() {
		return driverURL;
	}
	/**
	 * database password
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * database user name
	 * 
	 * @return euser name
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * databaseURL
	 * 
	 * @param string databaseURL
	 */
	public void setDatabaseURL(String string) {
		databaseURL = string;
	}
	/**
	 * Driver URL
	 * 
	 * @param string Driver URL
	 */
	public void setDriverURL(String string) {
		driverURL = string;
	}
	/**
	 * database password
	 * 
	 * @param string password
	 */
	public void setPassword(String string) {
		password = string;
	}
	/**
	 * database user name
	 * 
	 * @param string database user name
	 */
	public void setUsername(String string) {
		username = string;
	}
}
