/*
 * Created on 2004/01/17 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.sql;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
// import jp.ngskssb.kkmr.utils.HankakuZenkaku;
/**
 * @author ���줪��
 */
public class SQLCobolRecord extends DefaultCobolRecord {
	/** �����⡼�� */
	public static final int REWRITE = 1;
	/** �ɵ��⡼�� */
	public static final int WRITE = 0;
	/** JDBC���ͥ������ */
	private Connection connection;
	/** �����祫���å� */
	private ResultSet resultSet;
	/**
	 * ���󥹥ȥ饯��
	 * @param meta �᥿�ǡ���
	 * @param result ��̥��å�
	 */
	public SQLCobolRecord(SQLCobolRecordMetaData meta, Connection connection, ResultSet result) {
		super(meta);
		this.connection = connection;
		this.resultSet = result;
	}
	/**
	 * JDBC���ͥ������μ���
	 * @return JDBC���ͥ������
	 */
	public Connection getConnection() {
		return connection;
	}
	/**
	 * ��̥��åȤμ���
	 * @return ������̥��å�
	 */
	protected ResultSet getResultSet() {
		return resultSet;
	}
	/**
	 * �쥳���ɤ���JDBC��̥��åȤ��Ѵ�����
	 */
	public void record2result(int type) throws CobolRecordException, SQLException {
		record2result(resultSet, type);
	}
	/**
	 * �쥳���ɤ���JDBC��̥��åȤ��Ѵ�����
	 */
	protected void record2result(ResultSet result, int type) throws CobolRecordException, SQLException {
		int count = getMetaData().getColumnCount();
		for (int i = 0; i < count; i++) {
			CobolColumn columnCheck = getMetaData().getColumn(i);
			if (columnCheck instanceof SQLCobolColumn) {
				SQLCobolColumn column = (SQLCobolColumn) columnCheck;
				if ((!column.isWriteIgnore() && type == WRITE) || (!column.isRewriteIgnore() && type == REWRITE)) {
					column.setRecord2ResultSet(this, result);
				}
			}
		}
	}
	/**
	 * JDBC��̥��åȤ���쥳���ɤ��Ѵ�����
	 * @param result ��̥��å�
	 * @param column ��
	 */
	protected void result2column(ResultSet result, SQLCobolColumn column) throws SQLException {
		if (!column.isReadIgnore()) {
			column.setResultSet2Record(result, this);
		}
	}
	/**
	 * JDBC��̥��åȤ���쥳���ɤ��Ѵ�����
	 */
	public void result2record() throws CobolRecordException, SQLException {
		result2record(resultSet);
	}
	/**
	 * JDBC��̥��åȤ���쥳���ɤ��Ѵ�����
	 * @param result ��̥��å�
	 */
	protected void result2record(ResultSet result) throws CobolRecordException, SQLException {
		initializeRecord();
		int count = getMetaData().getColumnCount();
		for (int i = 0; i < count; i++) {
			CobolColumn columnCheck = getMetaData().getColumn(i);
			if (columnCheck instanceof SQLCobolColumn) {
				// ���̤���
				SQLCobolColumn column = (SQLCobolColumn) columnCheck;
				result2column(result, column);
			}
		}
	}
	/**
	 * ��̥��åȤ�����
	 * @param resultSet �����˳�Ǽ�����̥��å�
	 */
	protected void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
}
