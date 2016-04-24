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
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLCobolRecord extends DefaultCobolRecord {
	/** rewriting */
	public static final int REWRITE = 1;
	/** writing */
	public static final int WRITE = 0;
	/** JDBC Connection */
	private Connection connection;
	/** jdbc result set */
	private ResultSet resultSet;

	/**
	 * Constructor
	 * 
	 * @param meta meta data
	 * @param result result set
	 */
	public SQLCobolRecord(SQLCobolRecordMetaData meta, Connection connection, ResultSet result) {
		super(meta);
		this.connection = connection;
		this.resultSet = result;
	}

	/**
	 * get Connection
	 * 
	 * @return JDBCConnection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * get result set
	 * 
	 * @return result set
	 */
	protected ResultSet getResultSet() {
		return resultSet;
	}

	/**
	 * move record to SQL result
	 * 
	 * @param type SQLCobolRecord.WRITE or SQLCobolRecord.REWRITE
	 * @throws CobolRecordException CobolRecordException
	 * @throws SQLException SQLException
	 */
	public void record2result(int type) throws CobolRecordException, SQLException {
		record2result(resultSet, type);
	}

	/**
	 * move values to SQL result
	 * 
	 * @param result SQL result
	 * @param type WRITE/REWRITE
	 * @throws CobolRecordException exception
	 * @throws SQLException exception
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
	 * move values from SQL result
	 * 
	 * @param result SQL result
	 * @param column column
	 */
	protected void result2column(ResultSet result, SQLCobolColumn column) throws SQLException {
		if (!column.isReadIgnore()) {
			column.setResultSet2Record(result, this);
		}
	}

	/**
	 * move values from SQL result
	 */
	public void result2record() throws CobolRecordException, SQLException {
		result2record(resultSet);
	}

	/**
	 * move values from SQL result
	 * 
	 * @param result SQL result
	 */
	protected void result2record(ResultSet result) throws CobolRecordException, SQLException {
		initializeRecord();
		int count = getMetaData().getColumnCount();
		for (int i = 0; i < count; i++) {
			CobolColumn columnCheck = getMetaData().getColumn(i);
			if (columnCheck instanceof SQLCobolColumn) {
				SQLCobolColumn column = (SQLCobolColumn) columnCheck;
				result2column(result, column);
			}
		}
	}

	/**
	 * set SQL result
	 * 
	 * @param resultSet SQL result
	 */
	protected void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
}
