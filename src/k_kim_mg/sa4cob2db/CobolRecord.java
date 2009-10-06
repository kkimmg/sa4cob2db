package k_kim_mg.sa4cob2db;
import java.math.BigDecimal;
import java.util.Date;
/**
 * ファイル中のレコード
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * コボルファイル中の1件のレコードに対する値の取得/更新に関する基本的な機能を提供する
 */
public interface CobolRecord {
	/**
	 * 列の順番を取得する
	 * @param columnName 列名
	 * @return 列の順番を表すインデックス
	 */
	public int findColumn(String columnName) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public BigDecimal getBigDecimal(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamからTrueFalseを取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public boolean getBoolean(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public byte getByte(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public byte[] getBytes(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public Date getDate(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public double getDouble(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public float getFloat(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public int getInt(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public long getLong(CobolColumn column) throws CobolRecordException;
	/**
	 * このレコードのメタデータ
	 * @return メタデータ
	 */
	public CobolRecordMetaData getMetaData() throws CobolRecordException;
	/**
	 * レコードのバイトイメージを取得する
	 * @param record レコードを表すバイト配列
	 * @return 転送したバイト数
	 * @throws CobolRecordException
	 */
	public int getRecord(byte[] record) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public short getShort(CobolColumn column) throws CobolRecordException;
	/**
	 * InputStreamから値を取得する
	 * @param column 列の位置を示す列オブジェクト
	 * @return 列の値
	 */
	public String getString(CobolColumn column) throws CobolRecordException;
	/**
	 * JDBC結果セットからレコードに変換する
	 */
	public void initializeRecord();
	/**
	 * レコードのバイトイメージをセットする
	 * @param record record レコードを表すバイト配列
	 * @return 転送したバイト数
	 * @throws CobolRecordException
	 */
	public int setRecord(byte[] record) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateBigDecimal(CobolColumn column, BigDecimal x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateBoolean(CobolColumn column, boolean x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateByte(CobolColumn column, byte x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateBytes(CobolColumn column, byte[] x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateDate(CobolColumn column, Date x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateDouble(CobolColumn column, double x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateFloat(CobolColumn column, float x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateInt(CobolColumn column, int x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateLong(CobolColumn column, long x) throws CobolRecordException;
	/**
	 * OutputStreamにNullをセットする
	 * @param column 列の位置を示す列オブジェクト
	 */
	public void updateNull(CobolColumn column) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateObject(CobolColumn column, Object x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateObject(CobolColumn column, Object x, int scale) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateShort(CobolColumn column, short x) throws CobolRecordException;
	/**
	 * OutputStreamに値をセットする
	 * @param column 列の位置を示す列オブジェクト
	 * @param x セットする値
	 */
	public void updateString(CobolColumn column, String x) throws CobolRecordException;
}