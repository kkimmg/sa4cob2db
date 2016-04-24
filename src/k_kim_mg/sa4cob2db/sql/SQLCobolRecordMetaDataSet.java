package k_kim_mg.sa4cob2db.sql;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;

/**
 * Set of SQLCobolRecordMetaData. This class provides Database connection
 * infomation.
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLCobolRecordMetaDataSet extends CobolRecordMetaDataSet {
	/** JDBC driverURL */
	protected String driverURL;
	/** JDBC database URL */
	protected String databaseURL;
	/** JDBC database user name */
	protected String username;
	/** JDBC database password */
	protected String password;

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaDataSet#createCobolFile
	 * (k_kim_mg.sa4cob2db.CobolRecordMetaData)
	 */
	protected CobolFile createCobolFile(CobolRecordMetaData meta) {
		if (meta instanceof SQLCobolRecordMetaData) {
			return new SQLFile(null, (SQLCobolRecordMetaData) meta);
		}
		return null;
	}

	/**
	 * get database URL
	 * 
	 * @return database URL
	 */
	public String getDatabaseURL() {
		return databaseURL;
	}

	/**
	 * get driver URL
	 * 
	 * @return driver URL
	 */
	public String getDriverURL() {
		return driverURL;
	}

	/**
	 * get database password
	 * 
	 * @return database password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * get database user name
	 * 
	 * @return database user name
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * set database URL
	 * 
	 * @param string database URL
	 */
	public void setDatabaseURL(String string) {
		databaseURL = string;
	}

	/**
	 * set driver URL
	 * 
	 * @param string driver URL
	 */
	public void setDriverURL(String string) {
		driverURL = string;
	}

	/**
	 * set database password
	 * 
	 * @param string database password
	 */
	public void setPassword(String string) {
		password = string;
	}

	/**
	 * set database user name
	 * 
	 * @param string database user name
	 */
	public void setUsername(String string) {
		username = string;
	}
}
