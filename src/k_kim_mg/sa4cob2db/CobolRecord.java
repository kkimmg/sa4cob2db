package k_kim_mg.sa4cob2db;

import java.math.BigDecimal;
import java.util.Date;

/**
 * record
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a> wraps bytes object
 */
public interface CobolRecord {
	/**
	 * find column by name
	 * 
	 * @param columnName column name
	 * @return index
	 */
	public int findColumn(String columnName) throws CobolRecordException;

	/**
	 * get decimal value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public BigDecimal getBigDecimal(CobolColumn column) throws CobolRecordException;

	/**
	 * get boolean value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public boolean getBoolean(CobolColumn column) throws CobolRecordException;

	/**
	 * get byte value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public byte getByte(CobolColumn column) throws CobolRecordException;

	/**
	 * get bytes from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public byte[] getBytes(CobolColumn column) throws CobolRecordException;

	/**
	 * get Date value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public Date getDate(CobolColumn column) throws CobolRecordException;

	/**
	 * get double value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public double getDouble(CobolColumn column) throws CobolRecordException;

	/**
	 * get float value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public float getFloat(CobolColumn column) throws CobolRecordException;

	/**
	 * get int value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public int getInt(CobolColumn column) throws CobolRecordException;

	/**
	 * get long value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public long getLong(CobolColumn column) throws CobolRecordException;

	/**
	 * get meta data
	 * 
	 * @return meta data
	 */
	public CobolRecordMetaData getMetaData() throws CobolRecordException;

	/**
	 * move record to bytes
	 * 
	 * @param record bytes
	 * @return record physical length
	 * @throws CobolRecordException CobolRecordException
	 */
	public int getRecord(byte[] record) throws CobolRecordException;

	/**
	 * get short value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public short getShort(CobolColumn column) throws CobolRecordException;

	/**
	 * get String value from record
	 * 
	 * @param column column object
	 * @return value
	 */
	public String getString(CobolColumn column) throws CobolRecordException;

	/**
	 * initialize
	 */
	public void initializeRecord();

	/**
	 * move bytes to record
	 * 
	 * @param record bytes
	 * @return bytes length
	 * @throws CobolRecordException CobolRecordException
	 */
	public int setRecord(byte[] record) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateBigDecimal(CobolColumn column, BigDecimal x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateBoolean(CobolColumn column, boolean x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateByte(CobolColumn column, byte x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateBytes(CobolColumn column, byte[] x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateDate(CobolColumn column, Date x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateDouble(CobolColumn column, double x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateFloat(CobolColumn column, float x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateInt(CobolColumn column, int x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateLong(CobolColumn column, long x) throws CobolRecordException;

	/**
	 * set null
	 * 
	 * @param column column object
	 */
	public void updateNull(CobolColumn column) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateObject(CobolColumn column, Object x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateObject(CobolColumn column, Object x, int scale) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateShort(CobolColumn column, short x) throws CobolRecordException;

	/**
	 * set value
	 * 
	 * @param column column object
	 * @param x value
	 */
	public void updateString(CobolColumn column, String x) throws CobolRecordException;
}