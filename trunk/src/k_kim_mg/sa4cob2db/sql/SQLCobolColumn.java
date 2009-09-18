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
 * SQL��
 * @author ���줪��
 */
public class SQLCobolColumn extends DefaultCobolColumn implements CobolColumn {
	/** ������ */
	private String defaultString = "";
	/** �ǡ����١�������̾ */
	private String originalColumnName = "";
	/** �ɼ衢���̵�� */
	private boolean readIgnore = false, rewriteIgnore = false, writeIgnore = false;
	/**
	 * ���󥹥ȥ饯��
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
	 * �������ɤ߹��߻�̵�뤵��������ɽ��
	 * @return ����Ǥ���ΤϤȤꤢ����ʸ��������äƤ��Ȥǡ�������
	 * @throws CobolRecordException
	 */
	public String getDefaultString() throws CobolRecordException {
		return defaultString;
	}
	/**
	 * ���ꥸ�ʥ�Υ쥳���ɤ���̾
	 * @return ���ꥸ�ʥ�Υ쥳���ɤ���̾
	 * @throws CobolRecordException
	 */
	public String getOriginalColumnName() throws CobolRecordException {
		return originalColumnName;
	}
	/**
	 * �ɤ߹��߻��ˤ������̵�뤹�뤫�ɤ���
	 * @return �ɤ߹��߻��ˤ������̵�뤹�뤫�ɤ���
	 * @throws CobolRecordException
	 */
	public boolean isReadIgnore() throws CobolRecordException {
		return readIgnore;
	}
	/**
	 * ��񤭻��ˤ������̵�뤹�뤫�ɤ���
	 * @return ��񤭻��ˤ������̵�뤹�뤫�ɤ���
	 * @throws CobolRecordException
	 */
	public boolean isRewriteIgnore() throws CobolRecordException {
		return rewriteIgnore;
	}
	/**
	 * �����񤭹��߻��ˤ������̵�뤹�뤫�ɤ���
	 * @return �����񤭹��߻��ˤ������̵�뤹�뤫�ɤ���
	 * @throws CobolRecordException
	 */
	public boolean isWriteIgnore() throws CobolRecordException {
		return writeIgnore;
	}
	/**
	 * �ǥե���Ȥ�ʸ����
	 * @param string �ǥե���Ȥ�ʸ����
	 */
	public void setDefaultString(String string) {
		defaultString = string;
	}
	/**
	 * SQL��̥��åȤ���̾
	 * @param string SQL��̥��åȤ���̾
	 */
	public void setOriginalColumnName(String string) {
		originalColumnName = string;
	}
	/**
	 * �ɤ߹��߻��ˤ������̵�뤹�뤫�ɤ���
	 * @param b �ɤ߹��߻��ˤ������̵�뤹�뤫�ɤ���
	 */
	public void setReadIgnore(boolean b) {
		readIgnore = b;
	}
	/**
	 * ���ܥ�쥳���ɷ�������JDBC��̥��åȤؤ��Ѵ�
	 * @param src ���ܥ�쥳����
	 * @param dst JDBC��̥��å�
	 * @throws SQLException SQL�㳰
	 * @throws CobolRecordException ���ܥ��㳰
	 */
	public void setRecord2ResultSet(CobolRecord src, ResultSet dst) throws SQLException, CobolRecordException {
		String originalName = getOriginalColumnName();
		// ��������
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
	 * JDBC��̥��åȤ��饳�ܥ�쥳���ɷ����ؤ��Ѵ�
	 * @param src JDBC��̥��å�
	 * @param dst ���ܥ�쥳����
	 * @throws SQLException SQL�㳰
	 * @throws CobolRecordException ���ܥ��㳰
	 */
	public void setResultSet2Record(ResultSet src, CobolRecord dst) throws SQLException, CobolRecordException {
		String originalName = getOriginalColumnName();
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
		// default :
		// this.SetResultSet2Record(src, this);
		}
	}
	/**
	 * ��񤭻��ˤ������̵�뤹�뤫�ɤ���
	 * @param b ��񤭻��ˤ������̵�뤹�뤫�ɤ���
	 */
	public void setRewriteIgnore(boolean b) {
		rewriteIgnore = b;
	}
	/**
	 * �����񤭹��߻��ˤ������̵�뤹�뤫�ɤ���
	 * @param b �����񤭹��߻��ˤ������̵�뤹�뤫�ɤ���
	 */
	public void setWriteIgnore(boolean b) {
		writeIgnore = b;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + ":" + defaultString + ":" + originalColumnName + ":" + readIgnore + ":" + rewriteIgnore + ":" + writeIgnore;
	}
}
