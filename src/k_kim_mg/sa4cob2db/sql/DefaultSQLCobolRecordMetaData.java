package k_kim_mg.sa4cob2db.sql;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData;

public class DefaultSQLCobolRecordMetaData extends DefaultCobolRecordMetaData implements SQLCobolRecordMetaData {
	/**
	 * SELECT statement
	 */
	protected String selectStatement;
	/** delete all data */
	protected String truncateStatement;

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData#getSelectStatement()
	 */
	public String getSelectStatement() {
		return selectStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData#setSelectStatement(java
	 * .lang.String)
	 */
	public void setSelectStatement(String string) {
		selectStatement = string;
	}

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData#copyTo(k_kim_mg.sa4cob2db.CobolRecordMetaData)
	 */
	@Override
	protected CobolRecordMetaData copyTo(CobolRecordMetaData copy) {
		super.copyTo(copy);
		if (copy instanceof DefaultSQLCobolRecordMetaData) {
			DefaultSQLCobolRecordMetaData work = (DefaultSQLCobolRecordMetaData) copy;
			work.setSelectStatement(getSelectStatement());
		}
		return copy;
	}

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData#createCopy()
	 */
	@Override
	public CobolRecordMetaData createCopy() {
		DefaultSQLCobolRecordMetaData copy = new DefaultSQLCobolRecordMetaData();
		copyTo(copy);
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData#getTruncateStatement()
	 */
	public String getTruncateStatement() {
		return truncateStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData#setTruncateStatement(java
	 * .lang.String)
	 */
	public void setTruncateStatement(String statement) {
		truncateStatement = statement;
	}
}