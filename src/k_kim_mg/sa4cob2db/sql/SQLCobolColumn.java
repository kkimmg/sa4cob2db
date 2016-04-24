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
 * SQL column
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLCobolColumn extends DefaultCobolColumn implements CobolColumn {
	private String defaultString = "";
	private String originalColumnName = "";
	private boolean readIgnore = false;
	private boolean rewriteIgnore = false;
	private boolean writeIgnore = false;

	/**
	 * Constructor
	 * 
	 * @param meta meta data
	 */
	public SQLCobolColumn(SQLCobolRecordMetaData meta) {
		super(meta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.DefaultCobolColumn#copyTo(k_kim_mg.sa4cob2db.CobolColumn
	 * )
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.DefaultCobolColumn#createCopy(k_kim_mg.sa4cob2db.
	 * CobolRecordMetaData)
	 */
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
	 * default string
	 * 
	 * @return default value
	 * @throws CobolRecordException exception
	 */
	public String getDefaultString() throws CobolRecordException {
		return defaultString;
	}

	/**
	 * get SQL column name
	 * 
	 * @return column name
	 * @throws CobolRecordException exception
	 */
	public String getOriginalColumnName() throws CobolRecordException {
		return (originalColumnName != null && originalColumnName.length() > 0 ? originalColumnName : getName());
	}

	/**
	 * ignore when reading record?
	 * 
	 * @return ignore:true / no:false
	 * @throws CobolRecordException exception
	 */
	public boolean isReadIgnore() throws CobolRecordException {
		return readIgnore;
	}

	/**
	 * ignore when rewrite/update record?
	 * 
	 * @return ignore:true / no:false
	 * @throws CobolRecordException exception
	 */
	public boolean isRewriteIgnore() throws CobolRecordException {
		return rewriteIgnore;
	}

	/**
	 * ignore when write/insert record?
	 * 
	 * @return ignore:true / no:false
	 * @throws CobolRecordException exception
	 */
	public boolean isWriteIgnore() throws CobolRecordException {
		return writeIgnore;
	}

	/**
	 * set default string
	 * 
	 * @param string default string
	 */
	public void setDefaultString(String string) {
		defaultString = string;
	}

	/**
	 * set SQL column name
	 * 
	 * @param name column name
	 */
	public void setOriginalColumnName(String name) {
		originalColumnName = name;
	}

	/**
	 * ignore when reading record?
	 * 
	 * @param b ignore:true / no:false
	 */
	public void setReadIgnore(boolean b) {
		readIgnore = b;
	}

	/**
	 * move COBOL record to SQL result set
	 * 
	 * @param src COBOL record
	 * @param dst SQL result set
	 * @throws SQLException SQLexception
	 * @throws CobolRecordException COBOL exception
	 */
	public void setRecord2ResultSet(CobolRecord src, ResultSet dst) throws SQLException, CobolRecordException {
		String originalName = getOriginalColumnName();
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
	 * move result set to COBOL record
	 * 
	 * @param src result set
	 * @param dst COBOL record
	 * @throws SQLException SQLexception
	 * @throws CobolRecordException COBOL exception
	 */
	public void setResultSet2Record(ResultSet src, CobolRecord dst) throws SQLException, CobolRecordException {
		if (src == null) {
			SQLNetServer.logger.warning("src is null:" + this.getName());
			throw new CobolRecordException();
		}
		if (dst == null) {
			SQLNetServer.logger.warning("dst is null:" + this.getName());
			throw new CobolRecordException();
		}
		String originalName = getOriginalColumnName();
		if (originalName == null) {
			SQLNetServer.logger.warning("originalName is null:" + this.getName());
			throw new CobolRecordException();
		}
		switch (getType()) {
		case CobolColumn.TYPE_INTEGER:
			dst.updateInt(this, src.getInt(originalName));
			break;
		case CobolColumn.TYPE_LONG:
			dst.updateBigDecimal(this, src.getBigDecimal(originalName));
			break;
		case CobolColumn.TYPE_XCHAR:
			dst.updateString(this, src.getString(originalName));
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
		case CobolColumn.TYPE_DECIMAL:
			dst.updateBigDecimal(this, src.getBigDecimal(originalName));
			break;
		default:
			SQLNetServer.logger.warning("Unknown Column Type :" + getType());
		}
	}

	/**
	 * ignore when rewrite/update record?
	 * 
	 * @param b ignore:true / no:false
	 */
	public void setRewriteIgnore(boolean b) {
		rewriteIgnore = b;
	}

	/**
	 * ignore when write/insert record?
	 * 
	 * @param b ignore:true / no:false
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
