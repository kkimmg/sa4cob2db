package k_kim_mg.sa4cob2db.sql;

/**
 * provides SQL statements from entity name. This interface doesn't support
 * "Start Less Equal" and "Less Than".
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * 
 */
public interface SQLCobolRecordMetaData2 extends SQLCobolRecordMetaData {
	/**
	 * get entity name
	 * 
	 * @return entity name
	 */
	public String getEntityName();

	/**
	 * set entity name
	 * 
	 * @param entityName entity name
	 */
	public void setEntityName(String entityName);

	/**
	 * set SELECT statement for READ
	 * 
	 * @param keyReadStatement SELECT statement
	 */
	public void setKeyReadStatement(String keyReadStatement);

	/**
	 * get SELECT statement for READ
	 * 
	 * @return SELECT statement
	 */
	public String getKeyReadStatement();

	/**
	 * set SELECT statement for Greater Than or Equal
	 * 
	 * @param startGEStatement SELECT statement
	 */
	public void setStartGEStatement(String startGEStatement);

	/**
	 * get SELECT statement for Greater Than or Equal
	 * 
	 * @return SELECT statement
	 */
	public String getStartGEStatement();

	/**
	 * set SELECT statement for Greater Than
	 * 
	 * @param startGTStatement SELECT statement
	 */
	public void setStartGTStatement(String startGTStatement);

	/**
	 * get SELECT statement for Greater Than
	 * 
	 * @return SELECT statement
	 */
	public String getStartGTStatement();

	/*
	 * public void setStartLEStatement(String startLEStatement);
	 * 
	 * public String getStartLEStatement();
	 * 
	 * public void setStartLTStatement(String startLTStatement);
	 * 
	 * public String getStartLTStatement();
	 */
}
