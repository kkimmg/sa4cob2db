package k_kim_mg.sa4cob2db.sql;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;
/**
 * ��Ͽ�ѤߤΥ᥿�ǡ�������ե����륪�֥������Ȥ��ä��ꤹ�뵡ǽ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLFileServer {
	/** SQL���ͥ�������ѥ桼�ƥ���ƥ� */
	protected DBConnector connector;
	/** �᥿�ǡ����Υ��å� */
	protected SQLCobolRecordMetaDataSet metaDataSet;
	/** ���å����ID�Τ���Υ������� */
	protected/* synchronized */
	/*  ��� */
	int sequence = 0;
	/** ���󥹥ȥ饯�� */
	public SQLFileServer() {
		connector = new DBConnector();
		metaDataSet = new SQLCobolRecordMetaDataSet();
	}
	/**
	 * ��³�κ���
	 * @return ��³
	 */
	public Connection createConnection() {
		return createConnection(true);
	}
	/**
	 * ��³�κ���
	 * @param what true ��˿�������³���������<br/>
	 *            false ������³��¸�ߤ����鿷������³��������ʤ���
	 * @return ��³
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
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
		}
		return ret;
	}
	/**
	 * ��³�γ���
	 * @param connection ����������³
	 */
	public void removeConnection (Connection connection) {
		try {
			connector.removeConnection(connection);
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
		}
	}
	/**
	 * �ǡ����١���URL
	 * @return �ǡ����١���URL
	 */
	private String getDatabaseURL() {
		return metaDataSet.getDatabaseURL();
	}
	/**
	 * �ɥ饤��URL
	 * @return �ɥ饤��URL
	 */
	private String getDriverURL() {
		return metaDataSet.getDriverURL();
	}
	/**
	 * �᥿�ǡ����μ���
	 * @param i i���ܤΤ᤿�ǡ������������
	 * @return �᥿�ǡ���
	 */
	public CobolRecordMetaData getMetaData(int i) {
		return metaDataSet.getMetaData(i);
	}
	/**
	 * �᥿�ǡ����μ���
	 * @param name �᥿�ǡ���̾
	 * @return �᥿�ǡ���
	 */
	public CobolRecordMetaData getMetaData(String name) {
		return metaDataSet.getMetaData(name);
	}
	/** ��Ͽ���줿�᥿�ǡ����ο� */
	public int getMetaDataCount() {
		return metaDataSet.getMetaDataCount();
	}
	/**
	 * �᥿�ǡ������������
	 * @return �᥿�ǡ���
	 */
	public SQLCobolRecordMetaDataSet getMetaDataSet() {
		return metaDataSet;
	}
	/**
	 * �ǡ����١����ѥ����
	 * @return �ǡ����١����ѥ����
	 */
	private String getPassword() {
		return metaDataSet.getPassword();
	}
	/**
	 * �ǡ����١����桼����̾
	 * @return �ǡ����١����桼����̾
	 */
	private String getUsername() {
		return metaDataSet.getUsername();
	}
	/**
	 * �᥿�ǡ�������Ͽ����
	 * @param meta �᥿�ǡ���
	 */
	public void installMetaData(CobolRecordMetaData meta) {
		metaDataSet.installMetaData(meta);
	}
	/**
	 * �᥿�ǡ�����������
	 * @param meta �᥿�ǡ���
	 */
	public void removeMetaData(CobolRecordMetaData meta) {
		metaDataSet.removeMetaData(meta);
	}
	/**
	 * �᥿�ǡ����򥻥åȤ���
	 * @param set �᥿�ǡ���
	 */
	public void setMetaDataSet(SQLCobolRecordMetaDataSet set) {
		metaDataSet = set;
	}
}
