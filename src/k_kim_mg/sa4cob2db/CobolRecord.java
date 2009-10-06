package k_kim_mg.sa4cob2db;
import java.math.BigDecimal;
import java.util.Date;
/**
 * �ե�������Υ쥳����
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * ���ܥ�ե��������1��Υ쥳���ɤ��Ф����ͤμ���/�����˴ؤ������Ū�ʵ�ǽ���󶡤���
 */
public interface CobolRecord {
	/**
	 * ��ν��֤��������
	 * @param columnName ��̾
	 * @return ��ν��֤�ɽ������ǥå���
	 */
	public int findColumn(String columnName) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public BigDecimal getBigDecimal(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream����TrueFalse���������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public boolean getBoolean(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public byte getByte(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public byte[] getBytes(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public Date getDate(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public double getDouble(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public float getFloat(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public int getInt(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public long getLong(CobolColumn column) throws CobolRecordException;
	/**
	 * ���Υ쥳���ɤΥ᥿�ǡ���
	 * @return �᥿�ǡ���
	 */
	public CobolRecordMetaData getMetaData() throws CobolRecordException;
	/**
	 * �쥳���ɤΥХ��ȥ��᡼�����������
	 * @param record �쥳���ɤ�ɽ���Х�������
	 * @return ž�������Х��ȿ�
	 * @throws CobolRecordException
	 */
	public int getRecord(byte[] record) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public short getShort(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStream�����ͤ��������
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @return �����
	 */
	public String getString(CobolColumn column) throws CobolRecordException;
	/**
	 * JDBC��̥��åȤ���쥳���ɤ��Ѵ�����
	 */
	public void initializeRecord();
	/**
	 * �쥳���ɤΥХ��ȥ��᡼���򥻥åȤ���
	 * @param record record �쥳���ɤ�ɽ���Х�������
	 * @return ž�������Х��ȿ�
	 * @throws CobolRecordException
	 */
	public int setRecord(byte[] record) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateBigDecimal(CobolColumn column, BigDecimal x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateBoolean(CobolColumn column, boolean x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateByte(CobolColumn column, byte x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateBytes(CobolColumn column, byte[] x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateDate(CobolColumn column, Date x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateDouble(CobolColumn column, double x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateFloat(CobolColumn column, float x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateInt(CobolColumn column, int x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateLong(CobolColumn column, long x) throws CobolRecordException;
	/**
	 * OutputStream��Null�򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 */
	public void updateNull(CobolColumn column) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateObject(CobolColumn column, Object x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateObject(CobolColumn column, Object x, int scale) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateShort(CobolColumn column, short x) throws CobolRecordException;
	/**
	 * OutputStream���ͤ򥻥åȤ���
	 * @param column ��ΰ��֤򼨤��󥪥֥�������
	 * @param x ���åȤ�����
	 */
	public void updateString(CobolColumn column, String x) throws CobolRecordException;
}