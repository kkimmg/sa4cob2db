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
 * @author おれおれ
 */
public class SQLCobolRecord extends DefaultCobolRecord {
	/** 更新モード */
	public static final int REWRITE = 1;
	/** 追記モード */
	public static final int WRITE = 0;
	/** JDBCコネクション */
	private Connection connection;
	/** 内部欠カセット */
	private ResultSet resultSet;
	/**
	 * コンストラクタ
	 * @param meta メタデータ
	 * @param result 結果セット
	 */
	public SQLCobolRecord(SQLCobolRecordMetaData meta, Connection connection, ResultSet result) {
		super(meta);
		this.connection = connection;
		this.resultSet = result;
	}
	/**
	 * JDBCコネクションの取得
	 * @return JDBCコネクション
	 */
	public Connection getConnection() {
		return connection;
	}
	/**
	 * 結果セットの取得
	 * @return 内部結果セット
	 */
	protected ResultSet getResultSet() {
		return resultSet;
	}
	/**
	 * レコードからJDBC結果セットへ変換する
	 */
	public void record2result(int type) throws CobolRecordException, SQLException {
		record2result(resultSet, type);
	}
	/**
	 * レコードからJDBC結果セットへ変換する
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
	 * JDBC結果セットからレコードに変換する
	 * @param result 結果セット
	 * @param column 列
	 */
	protected void result2column(ResultSet result, SQLCobolColumn column) throws SQLException {
		if (!column.isReadIgnore()) {
			column.setResultSet2Record(result, this);
		}
	}
	/**
	 * JDBC結果セットからレコードに変換する
	 */
	public void result2record() throws CobolRecordException, SQLException {
		result2record(resultSet);
	}
	/**
	 * JDBC結果セットからレコードに変換する
	 * @param result 結果セット
	 */
	protected void result2record(ResultSet result) throws CobolRecordException, SQLException {
		initializeRecord();
		int count = getMetaData().getColumnCount();
		for (int i = 0; i < count; i++) {
			CobolColumn columnCheck = getMetaData().getColumn(i);
			if (columnCheck instanceof SQLCobolColumn) {
				// 普通の列
				SQLCobolColumn column = (SQLCobolColumn) columnCheck;
				result2column(result, column);
			}
		}
	}
	/**
	 * 結果セットの設定
	 * @param resultSet 内部に格納する結果セット
	 */
	protected void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
}
