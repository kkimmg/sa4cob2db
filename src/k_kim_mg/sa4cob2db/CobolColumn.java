/*
 * Created on 2004/06/09 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db;
/**
 * cobol プログラムの columnを表すオブジェクト
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobolColumn {
	/** Date */
	public static int TYPE_DATE = 7;
	/** Double */
	public static int TYPE_DOUBLE = 6;
	/** Float */
	public static int TYPE_FLOAT = 5;
	/** Integer */
	public static int TYPE_INTEGER = 1;
	/** Long */
	public static int TYPE_LONG = 4;
	/** Graphic Characters */
	public static int TYPE_NCHAR = 3;
	/** Struct */
	public static int TYPE_STRUCT = 11;
	/** Time */
	public static int TYPE_TIME = 8;
	/** TimeStamp */
	public static int TYPE_TIMESTAMP = 10;
	/** Characters */
	public static int TYPE_XCHAR = 2;
	/** USAGE DISPLAY (Default) */
	public static int USAGE_DISPLAY = 0;
	/** USAGE DISPLAY (Binary Or Computational) */
	public static int USAGE_BINARY = 1;
	/** USAGE DISPLAY (Packed Decimal or Comp-3) */
	public static int USAGE_COMP_3 = 2;
	/** USAGE DISPLAY (National Characters) */
	public static int USAGE_NATIONAL = 4;
	/** USAGE DISPLAY (Index) */
	public static int USAGE_INDEX = 8;

	/**
	 * create copy
	 * @return copy
	 */
	public CobolColumn createCopy(CobolRecordMetaData meta);
	/**
	 * get meta data
	 * @return meta data
	 */
	public CobolRecordMetaData getCobolRecordMetaData();
	/**
	 * get format
	 * @return format
	 */
	public String getFormat();
	/**
	 * This value sets NULL in DB
	 * @return value
	 */
	public String getForNull();
	/**
	 * return this value if DB value is NULL
	 * @return value
	 */
	public String getIfNull();
	/**
	 * length
	 * @return length
	 */
	public int getLength();
	/**
	 * name
	 * @return name
	 */
	public String getName();
	/**
	 * get decimal digit
	 * @return decimal digit
	 */
	public int getNumberOfDecimal();
	/**
	 * column what copied from
	 * @return base column
	 */
	public CobolColumn getOriginalCobolColumn();
	/**
	 * get physical length
	 * @return physical length
	 */
	public int getPhysicalLength();
	/**
	 * get start location<br/>
	 * starts from 0
	 * @return start
	 */
	public int getStart();
	/**
	 * get data type
	 * @return data type
	 */
	public int getType();
	/**
	 * get Usage
	 * @return Usage int value (Display = 0...)
	 */
	public int getUsage();
	/**
	 * set this value when parse error
	 * @return value
	 */
	public Object getValueOfParseError();
	/**
	 * is S9
	 * @return true signed<br/>
	 *         false not
	 */
	public boolean isSigned();
	/**
	 * バイト配 column→オブジェクトの変換に失敗した場合、値を設定する
	 * @return the するかしないか
	 */
	public boolean isUseOnParseError();
	/**
	 * record情報の設定
	 * @param cobolRecordMetaData recordレイアウト
	 */
	public void setCobolRecordMetaData(CobolRecordMetaData cobolRecordMetaData);
	/**
	 *  columnの書式を表す文字 column
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
	 *  columnの長さ
	 * @param length 幅
	 */
	public void setLength(int length);
	/**
	 *  columnを識別するための名称
	 * @param name  column名
	 */
	public void setName(String name);
	/**
	 * 数値型の columnの小数点以下の桁数
	 * @param decimal 数値型の columnの小数点以下の桁数
	 */
	public void setNumberOfDecimal(int decimal);
	/**
	 * この columnのベースになる column
	 * @param original オリジナル column
	 */
	public void setOriginalCobolColumn(CobolColumn original);
	/**
	 *  columnのバイト長を返す
	 * @param length 物理長
	 */
	public void setPhysicalLength(int length);
	/**
	 * 数値型の columnが＋ーの符号を持つかどうか
	 * @param signed 符号あり
	 */
	public void setSigned(boolean signed);
	/**
	 *  columnの開始位置<br/>
	 * 0で始まります
	 * @param start 開始位置
	 */
	public void setStart(int start);
	/**
	 *  columnのデータ型
	 * @param type データ型
	 */
	public void setType(int type);
	/**
	 * set Usage
	 * @param usage int value (Display = 0...)
	 */
	public void setUsage(int usage);
	/**
	 * バイト配 column→オブジェクトの変換に失敗した場合、値を設定する
	 * @param useOnParseError するかしないか
	 */
	public void setUseOnParseError(boolean useOnParseError);
	/**
	 * バイト配 column→オブジェクトの変換に失敗した場合
	 * @param valueOfParseError 代替の値
	 */
	public void setValueOfParseError(Object valueOfParseError);
}
