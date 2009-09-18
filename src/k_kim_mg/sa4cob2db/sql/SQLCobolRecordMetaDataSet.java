package k_kim_mg.sa4cob2db.sql;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
/**
 * @author ���줪��
 */
public class SQLCobolRecordMetaDataSet extends CobolRecordMetaDataSet {
	/**JDBC�ɥ饤�С�URL*/
	protected String driverURL;
	/**JDBC�ǡ����١���URL*/
	protected String  databaseURL;
	/**JDBC�ǡ����١����桼����̾*/
	protected String  username;
	/**JDBC�ǡ����١����ѥ����*/
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
