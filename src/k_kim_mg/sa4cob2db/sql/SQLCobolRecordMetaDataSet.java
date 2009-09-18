package k_kim_mg.sa4cob2db.sql;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
/**
 * @author おれおれ
 */
public class SQLCobolRecordMetaDataSet extends CobolRecordMetaDataSet {
	/**JDBCドライバーURL*/
	protected String driverURL;
	/**JDBCデータベースURL*/
	protected String  databaseURL;
	/**JDBCデータベースユーザー名*/
	protected String  username;
	/**JDBCデータベースパスワード*/
	protected String  password;
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaDataSet#createCobolFile
	 * (k_kim_mg.sa4cob2db.CobolRecordMetaData)
	 */
	protected CobolFile createCobolFile(CobolRecordMetaData meta) {
		if (meta instanceof SQLCobolRecordMetaData) {
			return new SQLFile(null, (SQLCobolRecordMetaData) meta);
		}
		return null;
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
