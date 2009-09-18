package k_kim_mg.sa4cob2db.sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
public class DBConnector {
	/** �ǡ����١���URL */
	private String databaseURL = "";
	/** �ǥե���ȤΥ��ͥ������ */
	private Connection dfCnnct = null;
	/** �ɥ饤��URL */
	private String driverURL = "";
	/** ����ޤǤ˳��������ͥ������ */
	private List<Connection> openedConnects = new ArrayList<Connection>();
	/** �ѥ���� */
	private String password = "";
	/** �桼����̾ */
	private String username = "";
	/**
	 * ���󥹥ȥ饯��
	 */
	public DBConnector() {
		super();
	}
	/**
	 * ���󥹥ȥ饯��
	 * @param driverURL JDBC�ɥ饤�Ф�URL
	 * @param databaseURL JDBC�ǡ�����������URL
	 * @param userName JDBC�桼����̾
	 * @param passWord JDBC�ѥ����
	 */
	public DBConnector(String driverURL, String databaseURL, String userName, String passWord) {
		this();
		this.driverURL = driverURL;
		this.databaseURL = databaseURL;
		this.username = userName;
		this.password = passWord;
	}
	/**
	 * ���Ƥ���³����
	 */
	public void clearAllConnections() {
		SQLNetServer.logger.log(Level.INFO, "���Ƥ���³�������ޤ�");
		try {
			for (int i = 0; i < openedConnects.size(); i++) {
				try {
					Connection wc = openedConnects.get(i);
					if (!(wc.isClosed())) {
						SQLNetServer.logger.log(Level.INFO, "��³�������ޤ�");
						wc.close();
					} else {
						SQLNetServer.logger.log(Level.INFO, "���Ǥ˲������Ƥ��ޤ�");
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
	 * ̵�̤���³�κ��
	 */
	public void clearConnections() {
		try {
			for (int i = 0; i < openedConnects.size(); i++) {
				try {
					Connection wc = openedConnects.get(i);
					if (wc.isClosed()) {
						SQLNetServer.logger.log(Level.INFO, "��³�������ޤ�");
						openedConnects.remove(i);
						i--;
					}
				} catch (SQLException se) {
					SQLNetServer.logger.log(Level.WARNING, "��³���֤��ǧ�Ǥ��ޤ���");
					se.printStackTrace();
				}
			}
		} catch (ArrayIndexOutOfBoundsException aie) {
			SQLNetServer.logger.log(Level.SEVERE, "���å����顼");
			aie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��³�κ���
	 * @return �ǡ����١����ؤ���³
	 * @exception �㳰�ξܺ٤�����ϹԤ�ʤ�
	 */
	public Connection createConnection() throws ClassNotFoundException, SQLException {
		return createConnection(false);
	}
	/**
	 * ��³�κ���
	 * @param forth true ��˿�������³���������<br/>
	 *               false ������³��¸�ߤ����鿷������³��������ʤ���
	 * @return �ǡ����١����ؤ���³
	 * @exception �㳰�ξܺ٤�����ϹԤ�ʤ�
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
	 * ��³�κ���
	 * @return �ǡ����١����ؤ���³
	 * @param driverURL JDBC�ɥ饤�Ф�URL
	 * @param databaseURL JDBC�ǡ�����������URL
	 * @param userName JDBC�桼����̾
	 * @param passWord JDBC�ѥ����
	 * @exception �㳰�ξܺ٤�����ϹԤ�ʤ�
	 */
	public Connection createConnection(String driverURL, String databaseURL, String userName, String passWord) throws ClassNotFoundException, SQLException {
		Connection retValue = null;
		// �ɥ饤�Ф��ɤ߹���
		Class.forName(driverURL);
		// �ǡ����١����ؤ���³
		retValue = DriverManager.getConnection(databaseURL, userName, passWord);
		// ��³���ݻ�����
		openedConnects.add(retValue);
		SQLNetServer.logger.log(Level.INFO, "Connecting:" + driverURL + ":" + databaseURL + ":" + username + ":" + password);
		// �ͤ��֤�
		return retValue;
	}
	/**
	 * ���ͥ�������������
	 * @param connection ����������³
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
	 * ����Ϥ�äƤ����Τ�?
	 * @exception Throwable �ƥ��饹���������Ƥ���
	 */
	protected void finalyze() throws Throwable {
		clearAllConnections();
		super.finalize();
	}
	/**
	 * ��³�򤹤٤Ƽ�������
	 * @return ��³���٤�
	 */
	public Iterator<Connection> getConnections() {
		return openedConnects.listIterator();
	}
	/**
	 * �ǡ����١���URL
	 * @return �ǡ����١���URL
	 */
	public String getDatabaseURL() {
		return databaseURL;
	}
	/**
	 * �ɥ饤��URL
	 * @return �ɥ饤��URL
	 */
	public String getDriverURL() {
		return driverURL;
	}
	/**
	 * �ǡ����١����ѥ����
	 * @return �ǡ����١����ѥ����
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * �ǡ����١����桼����̾
	 * @return �ǡ����١����桼����̾
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * �ǡ����١���URL
	 * @param string �ǡ����١���URL
	 */
	public void setDatabaseURL(String string) {
		databaseURL = string;
	}
	/**
	 * �ɥ饤��URL
	 * @param string �ɥ饤��URL
	 */
	public void setDriverURL(String string) {
		driverURL = string;
	}
	/**
	 * �ǡ����١����ѥ����
	 * @param string �ǡ����١����ѥ����
	 */
	public void setPassword(String string) {
		password = string;
	}
	/**
	 * �ǡ����١����桼����̾
	 * @param string �ǡ����١����桼����̾
	 */
	public void setUsername(String string) {
		username = string;
	}
}
