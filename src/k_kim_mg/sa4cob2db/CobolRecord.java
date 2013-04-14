package k_kim_mg.sa4cob2db;
import java.math.BigDecimal;
import java.util.Date;
/**
 * file中のrecord
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * cobol file中の1件のrecordに対する値の取得/更新に関する基本的な機能を提供する
 */
public interface CobolRecord {
	/**
	 *  columnの順番を取得する
	 * @param columnName  column名
	 * @return  columnの順番を表すインデックス
	 */
	public int findColumn(String columnName) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public BigDecimal getBigDecimal(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamからTrueFalseを取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public boolean getBoolean(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public byte getByte(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public byte[] getBytes(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public Date getDate(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public double getDouble(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public float getFloat(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public int getInt(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public long getLong(CobolColumn column) throws CobolRecordException;
	/**
	 * このrecordのmeta data
	 * @return meta data
	 */
	public CobolRecordMetaData getMetaData() throws CobolRecordException;
	/**
	 * recordのバイトイメージを取得する
	 * @param record recordを表すバイト配 column
	 * @return 転送したバイト数
	 * @throws CobolRecordException
	 */
	public int getRecord(byte[] record) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public short getShort(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column  columnの位置を示す columnオブジェクト
	 * @return  columnの値
	 */
	public String getString(CobolColumn column) throws CobolRecordException;
	/**
	 * JDBC結果セットからrecordに変換する
	 */
	public void initializeRecord();
	/**
	 * recordのバイトイメージをセットする
	 * @param record record recordを表すバイト配 column
	 * @return 転送したバイト数
	 * @throws CobolRecordException
	 */
	public int setRecord(byte[] record) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateBigDecimal(CobolColumn column, BigDecimal x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateBoolean(CobolColumn column, boolean x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateByte(CobolColumn column, byte x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateBytes(CobolColumn column, byte[] x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateDate(CobolColumn column, Date x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateDouble(CobolColumn column, double x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateFloat(CobolColumn column, float x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateInt(CobolColumn column, int x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateLong(CobolColumn column, long x) throws CobolRecordException;
	/**
	 * OutputStreamにNullをセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 */
	public void updateNull(CobolColumn column) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateObject(CobolColumn column, Object x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateObject(CobolColumn column, Object x, int scale) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateShort(CobolColumn column, short x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column  columnの位置を示す columnオブジェクト
	 * @param x セットする値
	 */
	public void updateString(CobolColumn column, String x) throws CobolRecordException;
}