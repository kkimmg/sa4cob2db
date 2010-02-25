package k_kim_mg.sa4cob2db.sql;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.DefaultCobolColumn;

/**
 * SQL列
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLCobolColumn extends DefaultCobolColumn implements CobolColumn {
	/** 規定値 */
	private String defaultString = "";
	/** データベースの列名 */
	private String originalColumnName = "";
	/** 読取、書込無視 */
	private boolean readIgnore = false, rewriteIgnore = false, writeIgnore = false;

	/**
	 * コンストラクタ
	 * 
	 * @param meta
	 */
	public SQLCobolColumn(SQLCobolRecordMetaData meta) {
		super(meta);
	}

	// @Override
	protected CobolColumn copyTo(CobolColumn copy) {
		super.copyTo(copy);
		if (copy instanceof SQLCobolColumn) {
			SQLCobolColumn work = (SQLCobolColumn) copy;
			try {
				work.setDefaultString(getDefaultString());
				work.setOriginalColumnName(getOriginalColumnName());
				work.setReadIgnore(isReadIgnore());
				work.setWriteIgnore(isWriteIgnore());
				work.setRewriteIgnore(isRewriteIgnore());
			} catch (CobolRecordException e) {
				SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
		return copy;
	}

	// @Override
	public CobolColumn createCopy(CobolRecordMetaData meta) {
		CobolColumn ret = null;
		if (meta instanceof SQLCobolRecordMetaData) {
			SQLCobolRecordMetaData work = (SQLCobolRecordMetaData) meta;
			SQLCobolColumn copy = new SQLCobolColumn(work);
			copyTo(copy);
		} else {
			ret = super.createCopy(meta);
		}
		return ret;
	}

	/**
	 * この列が読み込み時無視される場合の値表現
	 * 
	 * @return 設定できるのはとりあえず文字列だけってことで・・・・
	 * @throws CobolRecordException
	 */
	public String getDefaultString() throws CobolRecordException {
		return defaultString;
	}

	/**
	 * オリジナルのレコードの列名
	 * 
	 * @return オリジナルのレコードの列名
	 * @throws CobolRecordException
	 */
	public String getOriginalColumnName() throws CobolRecordException {
		return originalColumnName;
	}

	/**
	 * 読み込み時にこの列を無視するかどうか
	 * 
	 * @return 読み込み時にこの列を無視するかどうか
	 * @throws CobolRecordException
	 */
	public boolean isReadIgnore() throws CobolRecordException {
		return readIgnore;
	}

	/**
	 * 上書き時にこの列を無視するかどうか
	 * 
	 * @return 上書き時にこの列を無視するかどうか
	 * @throws CobolRecordException
	 */
	public boolean isRewriteIgnore() throws CobolRecordException {
		return rewriteIgnore;
	}

	/**
	 * 新規書き込み時にこの列を無視するかどうか
	 * 
	 * @return 新規書き込み時にこの列を無視するかどうか
	 * @throws CobolRecordException
	 */
	public boolean isWriteIgnore() throws CobolRecordException {
		return writeIgnore;
	}

	/**
	 * デフォルトの文字列
	 * 
	 * @param string
	 *            デフォルトの文字列
	 */
	public void setDefaultString(String string) {
		defaultString = string;
	}

	/**
	 * SQL結果セットの列名
	 * 
	 * @param string
	 *            SQL結果セットの列名
	 */
	public void setOriginalColumnName(String string) {
		originalColumnName = string;
	}

	/**
	 * 読み込み時にこの列を無視するかどうか
	 * 
	 * @param b
	 *            読み込み時にこの列を無視するかどうか
	 */
	public void setReadIgnore(boolean b) {
		readIgnore = b;
	}

	/**
	 * コボルレコード形式からJDBC結果セットへの変換
	 * 
	 * @param src
	 *            コボルレコード
	 * @param dst
	 *            JDBC結果セット
	 * @throws SQLException
	 *             SQL例外
	 * @throws CobolRecordException
	 *             コボル例外
	 */
	public void setRecord2ResultSet(CobolRecord src, ResultSet dst) throws SQLException, CobolRecordException {
		String originalName = getOriginalColumnName();
		// ここから
		int sqlColumnIndex = dst.findColumn(originalName);
		int sqlColumnType = dst.getMetaData().getColumnType(sqlColumnIndex);
		switch (sqlColumnType) {
		case Types.DOUBLE:
			dst.updateDouble(originalName, src.getDouble(this));
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			dst.updateBigDecimal(originalName, src.getBigDecimal(this));
			break;
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			dst.updateInt(originalName, src.getInt(this));
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			dst.updateString(originalName, src.getString(this).trim());
			break;
		case Types.DATE:
			Date date = new Date(src.getDate(this).getTime());
			dst.updateDate(originalName, date);
			break;
		case Types.TIME:
			Time time = new Time(src.getDate(this).getTime());
			dst.updateTime(originalName, time);
			break;
		case Types.TIMESTAMP:
			Timestamp timestamp = new Timestamp(src.getDate(this).getTime());
			dst.updateTimestamp(originalName, timestamp);
			break;
		case Types.REAL:
		case Types.FLOAT:
			dst.updateFloat(originalName, src.getFloat(this));
			break;
		case Types.OTHER:
		case Types.ARRAY:
		case Types.BINARY:
		case Types.BIT:
		case Types.BLOB:
		case Types.CLOB:
		case Types.DISTINCT:
		case Types.LONGVARBINARY:
		case Types.JAVA_OBJECT:
		case Types.NULL:
		case Types.REF:
		case Types.STRUCT:
		case Types.VARBINARY:
			dst.updateString(originalName, src.getString(this).trim());
			break;
		default:
			throw new CobolRecordException();
		}
	}

	/**
	 * JDBC結果セットからコボルレコード形式への変換
	 * 
	 * @param src
	 *            JDBC結果セット
	 * @param dst
	 *            コボルレコード
	 * @throws SQLException
	 *             SQL例外
	 * @throws CobolRecordException
	 *             コボル例外
	 */
	public void setResultSet2Record(ResultSet src, CobolRecord dst) throws SQLException, CobolRecordException {
		String originalName = getOriginalColumnName();
		SQLNetServer.logger.fine(originalName + "=" + getType() + "/" + src.getObject(originalName).toString());
		switch (getType()) {
		case CobolColumn.TYPE_INTEGER:
			dst.updateInt(this, src.getInt(originalName));
			break;
		case CobolColumn.TYPE_LONG:
			dst.updateBigDecimal(this, src.getBigDecimal(originalName));
			break;
		case CobolColumn.TYPE_XCHAR:
			dst.updateString(this, src.getString(originalName).trim());
			break;
		case CobolColumn.TYPE_DATE:
			dst.updateDate(this, src.getDate(originalName));
			break;
		case CobolColumn.TYPE_TIME:
			dst.updateDate(this, src.getTime(originalName));
			break;
		case CobolColumn.TYPE_TIMESTAMP:
			dst.updateDate(this, src.getTimestamp(originalName));
			break;
		case CobolColumn.TYPE_DOUBLE:
			dst.updateDouble(this, src.getDouble(originalName));
			break;
		case CobolColumn.TYPE_FLOAT:
			dst.updateFloat(this, src.getFloat(originalName));
			break;
		default:
			SQLNetServer.logger.warning("Unknown Column Type :" + getType());
		}
	}

	/**
	 * 上書き時にこの列を無視するかどうか
	 * 
	 * @param b
	 *            上書き時にこの列を無視するかどうか
	 */
	public void setRewriteIgnore(boolean b) {
		rewriteIgnore = b;
	}

	/**
	 * 新規書き込み時にこの列を無視するかどうか
	 * 
	 * @param b
	 *            新規書き込み時にこの列を無視するかどうか
	 */
	public void setWriteIgnore(boolean b) {
		writeIgnore = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + ":" + defaultString + ":" + originalColumnName + ":" + readIgnore + ":" + rewriteIgnore + ":" + writeIgnore;
	}
}
