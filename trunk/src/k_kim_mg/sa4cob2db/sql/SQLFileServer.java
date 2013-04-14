package k_kim_mg.sa4cob2db.sql;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;
/**
 * provides file from meta data
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLFileServer {
	/** connector */
	protected DBConnector connector;
	/** meta data set */
	protected SQLCobolRecordMetaDataSet metaDataSet;
	/** sequence for sessionID */
	protected int sequence = 0;
	/** Constructor */
	public SQLFileServer() {
		connector = new DBConnector();
		metaDataSet = new SQLCobolRecordMetaDataSet();
	}
	/**
	 * create connection
	 * @return connection
	 */
	public Connection createConnection() {
		return createConnection(true);
	}
	/**
	 * create connection
	 * @param what true always new connection<br/>
	 *            false not new
	 * @return connection
	 */
	public Connection createConnection(boolean what) {
		Connection ret = null;
		try {
			SQLNetServer.logger.log(Level.FINEST, getDriverURL() + ":" + getDatabaseURL() + ":" + getUsername() + ":" + getPassword());
			if (what) {
				ret = connector.createConnection(getDriverURL(), getDatabaseURL(), getUsername(), getPassword());
			} else {
				ret = connector.createConnection();
			}
		} catch (ClassNotFoundException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * release connection
	 * @param connection connection
	 */
	public void removeConnection (Connection connection) {
		try {
			connector.removeConnection(connection);
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	/**
	 * get database URL
	 * @return databaseURL
	 */
	private String getDatabaseURL() {
		return metaDataSet.getDatabaseURL();
	}
	/**
	 * get driver URL
	 * @return  driver URL
	 */
	private String getDriverURL() {
		return metaDataSet.getDriverURL();
	}
	/**
	 * get meta data
	 * @param i i'th meta data
	 * @return meta data
	 */
	public CobolRecordMetaData getMetaData(int i) {
		return metaDataSet.get(i);
	}
	/**
	 * get meta data
	 * @param name meta data name
	 * @return meta data
	 */
	public CobolRecordMetaData getMetaData(String name) {
		return metaDataSet.getMetaData(name);
	}
	/** get count of meta data */
	public int getMetaDataCount() {
		return metaDataSet.size();
	}
	/**
	 * get meta data set
	 * @return meta data set
	 */
	public SQLCobolRecordMetaDataSet getMetaDataSet() {
		return metaDataSet;
	}
	/**
	 * get database password
	 * @return database password
	 */
	private String getPassword() {
		return metaDataSet.getPassword();
	}
	/**
	 * get database user name
	 * @return database user name
	 */
	private String getUsername() {
		return metaDataSet.getUsername();
	}
	/**
	 * add meta data
	 * @param meta meta data
	 */
	public void installMetaData(CobolRecordMetaData meta) {
		metaDataSet.installMetaData(meta);
	}
	/**
	 * delete meta data
	 * @param meta meta data
	 */
	public void removeMetaData(CobolRecordMetaData meta) {
		metaDataSet.removeMetaData(meta);
	}
	/**
	 * set meta data set
	 * @param set meta data set
	 */
	public void setMetaDataSet(SQLCobolRecordMetaDataSet set) {
		metaDataSet = set;
	}
}
