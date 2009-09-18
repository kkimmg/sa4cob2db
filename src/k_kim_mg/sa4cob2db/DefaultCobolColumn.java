package k_kim_mg.sa4cob2db;
/**
 * デフォルトのコボル列
 * @author おれおれ
 */
public class DefaultCobolColumn implements CobolColumn {
	/**
	 * データ型の文字表現（うーん・・・）
	 * @param type データ型
	 * @return 文字表現
	 */
	public static String getTypeString(int type) {
		String ret = "UNKNOWN";
		switch (type) {
		case CobolColumn.TYPE_DATE:
			ret = "DATE";
			break;
		case CobolColumn.TYPE_DOUBLE:
			ret = "DOUBLE";
			break;
		case CobolColumn.TYPE_FLOAT:
			ret = "FLOAT";
			break;
		case CobolColumn.TYPE_INTEGER:
			ret = "INTEGER";
			break;
		case CobolColumn.TYPE_LONG:
			ret = "LONG";
			break;
		case CobolColumn.TYPE_NCHAR:
			ret = "NCHAR";
			break;
		case CobolColumn.TYPE_TIME:
			ret = "TIME";
			break;
		case CobolColumn.TYPE_TIMESTAMP:
			ret = "TIMESTAMP";
			break;
		case CobolColumn.TYPE_XCHAR:
			ret = "XCHAR";
			break;
		case CobolColumn.TYPE_STRUCT:
			ret = "STRUCT";
			break;
		}
		return ret;
	}
	/** メタデータ */
	private CobolRecordMetaData cobolRecordMetaData;
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getFormat()
	 */
	private String format;
	/** この値ならDBにNULLをセットする */
	private String forNull = null;
	/** もしDB上の値がNULLなら */
	private String ifNull = null;
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getLength
	 */
	private int length;
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getName
	 */
	private String name;
	/** 小数点以下の桁数 */
	private int numberOfDecimal = 0;
	/** オリジナル */
	private CobolColumn original = this;
	/** この列は符号付きか？ */
	private boolean signed = false;
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getStart
	 */
	private int start;
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getType
	 */
	private int type;
	/** バイト配列→オブジェクトの変換に失敗した場合 */
	private boolean useOnParseError = false;
	/** バイト配列→オブジェクトの変換に失敗した場合 */
	private Object valueOfParseError = null;
	/**
	 * コンストラクタ
	 * @param meta メタデータ
	 */
	public DefaultCobolColumn(CobolRecordMetaData meta) {
		this.cobolRecordMetaData = meta;
	}
	/**
	 * 属性をコピーする
	 * @param copy コピー対象
	 * @return コピー対象
	 */
	protected CobolColumn copyTo(CobolColumn copy) {
		copy.setName(getName());
		copy.setType(getType());
		copy.setStart(getStart());
		copy.setLength(getLength());
		copy.setFormat(getFormat());
		copy.setOriginalCobolColumn(getOriginalCobolColumn());
		copy.setForNull(getForNull());
		copy.setIfNull(getIfNull());
		copy.setUseOnParseError(isUseOnParseError());
		copy.setValueOfParseError(getValueOfParseError());
		return copy;
	}
	// @Override
	public CobolColumn createCopy(CobolRecordMetaData meta) {
		CobolColumn copy = new DefaultCobolColumn(meta);
		copyTo(copy);
		return copy;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getCobolRecordMetaData()
	 */
	public CobolRecordMetaData getCobolRecordMetaData() {
		return cobolRecordMetaData;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getFormat()
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * この値ならDBにNULLをセットする
	 * @return 代替の値
	 */
	public String getForNull() {
		return forNull;
	}
	/**
	 * もしDB上の値がNULLなら
	 * @return 代替の値
	 */
	public String getIfNull() {
		return ifNull;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getLength()
	 */
	public int getLength() {
		return length;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getName()
	 */
	public String getName() {
		return name;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getNumberOfDecimal()
	 */
	public int getNumberOfDecimal() {
		return numberOfDecimal;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getOriginalCobolColumn()
	 */
	public CobolColumn getOriginalCobolColumn() {
		return original;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getPhysicalLength()
	 */
	public int getPhysicalLength() {
		int ret = getLength();
		if (getType() == CobolColumn.TYPE_NCHAR) {
			ret *= 2;
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getStart()
	 */
	public int getStart() {
		return start;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getType()
	 */
	public int getType() {
		return type;
	}
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合
	 * @return 代替の値
	 */
	public Object getValueOfParseError() {
		return valueOfParseError;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#isSigned()
	 */
	public boolean isSigned() {
		return signed;
	}
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合、値を設定する
	 * @return the するかしないか
	 */
	public boolean isUseOnParseError() {
		return useOnParseError;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolColumn#setCobolRecordMetaData(jp.
	 * ne.biglobe.mvh.k_kim_mg.acm.CobolRecordMetaData)
	 */
	public void setCobolRecordMetaData(CobolRecordMetaData cobolRecordMetaData) {
		this.cobolRecordMetaData = cobolRecordMetaData;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolColumn#setFormat(java.lang.String)
	 */
	public void setFormat(String string) {
		format = string;
	}
	/**
	 * この値ならDBにNULLをセットする
	 * @param forNull 代替の値
	 */
	public void setForNull(String forNull) {
		this.forNull = forNull;
	}
	/**
	 * もしDB上の値がNULLなら
	 * @param ifNull 代替の値
	 */
	public void setIfNull(String ifNull) {
		this.ifNull = ifNull;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setLength(int)
	 */
	public void setLength(int i) {
		length = i;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setName(java.lang.String)
	 */
	public void setName(String string) {
		name = string;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setNumberOfDecimal(int)
	 */
	public void setNumberOfDecimal(int numberOfDecimal) {
		this.numberOfDecimal = numberOfDecimal;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolColumn#setOriginalCobolColumn(jp.
	 * ne.biglobe.mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void setOriginalCobolColumn(CobolColumn original) {
		this.original = original;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setPhysicalLength(int)
	 */
	public void setPhysicalLength(int length) {
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setSigned(boolean)
	 */
	public void setSigned(boolean signed) {
		this.signed = signed;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setStart(int)
	 */
	public void setStart(int i) {
		start = i;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setType(int)
	 */
	public void setType(int i) {
		type = i;
	}
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合、値を設定する
	 * @param useOnParseError するかしないか
	 */
	public void setUseOnParseError(boolean useOnParseError) {
		this.useOnParseError = useOnParseError;
	}
	/**
	 * バイト配列→オブジェクトの変換に失敗した場合
	 * @param valueOfParseError 代替の値
	 */
	public void setValueOfParseError(Object valueOfParseError) {
		this.valueOfParseError = valueOfParseError;
	}
}