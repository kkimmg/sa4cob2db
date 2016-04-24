package k_kim_mg.sa4cob2db.sql;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;

/**
 * provides SQL statements
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface SQLCobolRecordMetaData extends CobolRecordMetaData {
	/**
	 * get Select statement
	 * 
	 * @return SELECT Column... From Table Where Column = value .... OrderBy
	 */
	public String getSelectStatement();

	/**
	 * set Select statment
	 * 
	 * @param string SELECT Column... From Table Where Column = value ....
	 *            OrderBy
	 */
	public void setSelectStatement(String string);

	/**
	 * get delete all statement
	 * 
	 * @return delete from .... or
	 */
	public String getTruncateStatement();

	/**
	 * set delete all statement
	 * 
	 * @param statement delete from .... or
	 */
	public void setTruncateStatement(String statement);
}
