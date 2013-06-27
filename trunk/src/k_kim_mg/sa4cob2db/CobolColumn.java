/*
 * Created on 2004/06/09 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db;
/**
 * COBOL column
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
	public static int USAGE_NATIONAL = 3;
	/** USAGE DISPLAY (Index) */
	public static int USAGE_INDEX = 4;

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
	 * use value when value parse error
	 * @return ture:use/false:not
	 */
	public boolean isUseOnParseError();
	/**
	 * record meta data
	 * @param cobolRecordMetaData record meta data
	 */
	public void setCobolRecordMetaData(CobolRecordMetaData cobolRecordMetaData);
	/**
	 * set format/pattern
	 * @param format format/pattern
	 */
	public void setFormat(String format);
	/**
	 * replace value for null
	 * @param forNull value
	 */
	public void setForNull(String forNull);
	/**
	 * replace value for null
	 * @param ifNull value
	 */
	public void setIfNull(String ifNull);
	/**
	 * set logical length
	 * @param length logical length
	 */
	public void setLength(int length);
	/**
	 * column name
	 * @param name  column name
	 */
	public void setName(String name);
	/**
	 * set decimal point
	 * @param decimal decimal point
	 */
	public void setNumberOfDecimal(int decimal);
	/**
	 * set base column
	 * @param original base column
	 */
	public void setOriginalCobolColumn(CobolColumn original);
	/**
	 * set physical length
	 * @param length 
	 */
	public void setPhysicalLength(int length);
	/**
	 * set signed flag
	 * @param signed signed
	 */
	public void setSigned(boolean signed);
	/**
	 * start location<br/>
	 * from 0
	 * @param start location
	 */
	public void setStart(int start);
	/**
	 * data type
	 * @param type type defined in CobolColumn TYPE_...
	 */
	public void setType(int type);
	/**
	 * set Usage
	 * @param usage int value (Display = 0...)
	 */
	public void setUsage(int usage);
	/**
	 * set defined value when parse error?
	 * @param useOnParseError true set, false not
	 */
	public void setUseOnParseError(boolean useOnParseError);
	/**
	 * value when parse error
	 * @param valueOfParseError value
	 */
	public void setValueOfParseError(Object valueOfParseError);
}
