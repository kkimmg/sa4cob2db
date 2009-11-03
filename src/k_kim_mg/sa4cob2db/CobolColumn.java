/*
 * Created on 2004/06/09 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db;
/**
 * コボルプログラムの列を表すオブジェクト
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobolColumn {
	/** 日付型 */
	public static int TYPE_DATE = 7;
	/** 小数点型 */
	public static int TYPE_DOUBLE = 6;
	/** 小数点型 */
	public static int TYPE_FLOAT = 5;
	/** 整数型 */
	public static int TYPE_INTEGER = 1;
	/** 整数型 */
	public static int TYPE_LONG = 4;
	/** 日本語型 */
	public static int TYPE_NCHAR = 3;
	/** 構造体型 */
	public static int TYPE_STRUCT = 11;
	/** 時刻型 */
	public static int TYPE_TIME = 8;
	/** 日付時刻型 */
	public static int TYPE_TIMESTAMP = 10;
	/** 英数字型 */
	public static int TYPE_XCHAR = 2;
	/**
	 * コピーの作成
	 * @return コピー？
	 */
	public CobolColumn createCopy(CobolRecordMetaData meta);
	/**
	 * レコード情報の取得
	 * @return レコードレイアウト
	 */
	public CobolRecordMetaData getCobolRecordMetaData();
	/**
	 * 列の書式を表す文字列
	 * @return 書式文字列
	 */
	public String getFormat();
	/**
	 * この値ならDBにNULLをセットする
	 * @return 代替の値
	 */
	public String getForNull();
	/**
	 * もしDB上の値がNULLなら
	 * @return 代替の値
	 */
	public String getIfNull();
	/**
	 * 列の長さ
	 * @return 列幅
	 */
	public int getLength();
	/**
	 * 列を識別するための名称
	 * @return 列名
	 */
	public String getName();
	/**
	 * 数値型の列の小数点以下の桁数
	 * @return 数値型の列の小数点以下の桁数
	 */
	public int getNumberOfDecimal();
	/**
	 * この列のベースになる列
	 * @return オリジナル列
	 */
	public CobolColumn getOriginalCobolColumn();
	/**
	 * 列のバイト長を返す
	 * @return 物理長
	 */
	public int getPhysicalLength();
	/**
	 * 列の開始位置<br/>
	 * 0で始まります
	 * @return 開始位置
	 */
	public int getStart();
	/**
	 * 列のデータ型
	 * @return データ型
	 */
	public int getType();
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合
	 * @return 代替の値
	 */
	public Object getValueOfParseError();
	/**
	 * 数値型の列が＋ーの符号を持つかどうか
	 * @return true 符号付き<br/>
	 *         false 符号なし
	 */
	public boolean isSigned();
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合、値を設定する
	 * @return the するかしないか
	 */
	public boolean isUseOnParseError();
	/**
	 * レコード情報の設定
	 * @param cobolRecordMetaData レコードレイアウト
	 */
	public void setCobolRecordMetaData(CobolRecordMetaData cobolRecordMetaData);
	/**
	 * 列の書式を表す文字列
	 * @param format 書式
	 */
	public void setFormat(String format);
	/**
	 * この値ならDBにNULLをセットする
	 * @param forNull 代替の値
	 */
	public void setForNull(String forNull);
	/**
	 * もしDB上の値がNULLなら
	 * @param ifNull 代替の値
	 */
	public void setIfNull(String ifNull);
	/**
	 * 列の長さ
	 * @param length 幅
	 */
	public void setLength(int length);
	/**
	 * 列を識別するための名称
	 * @param name 列名
	 */
	public void setName(String name);
	/**
	 * 数値型の列の小数点以下の桁数
	 * @param decimal 数値型の列の小数点以下の桁数
	 */
	public void setNumberOfDecimal(int decimal);
	/**
	 * この列のベースになる列
	 * @param original オリジナル列
	 */
	public void setOriginalCobolColumn(CobolColumn original);
	/**
	 * 列のバイト長を返す
	 * @param length 物理長
	 */
	public void setPhysicalLength(int length);
	/**
	 * 数値型の列が＋ーの符号を持つかどうか
	 * @param signed 符号あり
	 */
	public void setSigned(boolean signed);
	/**
	 * 列の開始位置<br/>
	 * 0で始まります
	 * @param start 開始位置
	 */
	public void setStart(int start);
	/**
	 * 列のデータ型
	 * @param type データ型
	 */
	public void setType(int type);
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合、値を設定する
	 * @param useOnParseError するかしないか
	 */
	public void setUseOnParseError(boolean useOnParseError);
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合
	 * @param valueOfParseError 代替の値
	 */
	public void setValueOfParseError(Object valueOfParseError);
}
