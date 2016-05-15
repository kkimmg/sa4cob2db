package k_kim_mg.sa4cob2db;

/**
 * Default COBOL Column
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class DefaultCobolColumn implements CobolColumn {
	/**
	 * data types
	 * 
	 * @param type data type
	 * @return name
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
		default:
			ret = "UNKNOWN";
			break;
		}
		return ret;
	}

	private CobolRecordMetaData cobolRecordMetaData;
	private String format;
	private String forNull = null;
	private String ifNull = null;
	private int length;
	private String name;
	private int numberOfDecimal = 0;
	private CobolColumn original = this;
	private boolean signed = false;
	private int start;
	private int type;
	private boolean useOnParseError = false;
	private Object valueOfParseError = null;
	private int usage = CobolColumn.USAGE_DISPLAY;
	private boolean key = false;

	/**
	 * Constructor
	 * 
	 * @param meta meta data
	 */
	public DefaultCobolColumn(CobolRecordMetaData meta) {
		this.cobolRecordMetaData = meta;
	}

	/**
	 * copy attributes to CobolColumn object
	 * 
	 * @param copy target
	 * @return target
	 */
	protected CobolColumn copyTo(CobolColumn copy) {
		copy.setName(getName());
		copy.setType(getType());
		copy.setStart(getStart());
		copy.setLength(getLength());
		copy.setSigned(isSigned());
		copy.setFormat(getFormat());
		copy.setOriginalCobolColumn(this);
		copy.setForNull(getForNull());
		copy.setIfNull(getIfNull());
		copy.setUseOnParseError(isUseOnParseError());
		copy.setValueOfParseError(getValueOfParseError());
		return copy;
	}

	@Override
	public CobolColumn createCopy(CobolRecordMetaData meta) {
		CobolColumn copy = new DefaultCobolColumn(meta);
		copyTo(copy);
		return copy;
	}

	@Override
	public CobolRecordMetaData getCobolRecordMetaData() {
		return cobolRecordMetaData;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public String getForNull() {
		return forNull;
	}

	@Override
	public String getIfNull() {
		return ifNull;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getLength()
	 */
	public int getLength() {
		return length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getNumberOfDecimal()
	 */
	public int getNumberOfDecimal() {
		return numberOfDecimal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getOriginalCobolColumn()
	 */
	public CobolColumn getOriginalCobolColumn() {
		return original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getPhysicalLength()
	 */
	@Override
	public int getPhysicalLength() {
		int ret = getLength();
		if (getType() == CobolColumn.TYPE_NCHAR) {
			ret *= 2;
		}
		if (getUsage() == CobolColumn.USAGE_COMP_3) {
			ret = ret / 2 + 1;
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getStart()
	 */
	public int getStart() {
		return start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getType()
	 */
	public int getType() {
		return type;
	}

	@Override
	public int getUsage() {
		int ret = this.usage;
		switch (getType()) {
		case CobolColumn.TYPE_DOUBLE:
		case CobolColumn.TYPE_FLOAT:
		case CobolColumn.TYPE_INTEGER:
		case CobolColumn.TYPE_LONG:
			// Do Nothing
			break;
		default:
			ret = CobolColumn.USAGE_DISPLAY;
			break;
		}
		return ret;
	}

	/**
	 * get value to use when parse error of record value
	 * 
	 * @return value
	 */
	public Object getValueOfParseError() {
		return valueOfParseError;
	}

	@Override
	public boolean isKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#isSigned()
	 */
	public boolean isSigned() {
		return signed;
	}

	/**
	 * use value when parse error ?
	 * 
	 * @return true use<br>
	 *         false don't
	 */
	public boolean isUseOnParseError() {
		return useOnParseError;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setCobolRecordMetaData(jp.
	 * ne.biglobe.mvh.k_kim_mg.acm.CobolRecordMetaData)
	 */
	public void setCobolRecordMetaData(CobolRecordMetaData cobolRecordMetaData) {
		this.cobolRecordMetaData = cobolRecordMetaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setFormat(java.lang.String)
	 */
	public void setFormat(String string) {
		format = string;
	}

	/**
	 * set value to use when record value is null
	 * 
	 * @param forNull value
	 */
	public void setForNull(String forNull) {
		this.forNull = forNull;
	}

	/**
	 * set value to use when table value is null
	 * 
	 * @param ifNull value
	 */
	public void setIfNull(String ifNull) {
		this.ifNull = ifNull;
	}

	@Override
	public void setKey(boolean key) {
		this.key = key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setLength(int)
	 */
	public void setLength(int i) {
		length = i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setName(java.lang.String)
	 */
	public void setName(String string) {
		name = string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setNumberOfDecimal(int)
	 */
	public void setNumberOfDecimal(int numberOfDecimal) {
		this.numberOfDecimal = numberOfDecimal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setOriginalCobolColumn(jp.
	 * ne.biglobe.mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void setOriginalCobolColumn(CobolColumn original) {
		this.original = original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setPhysicalLength(int)
	 */
	public void setPhysicalLength(int length) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setSigned(boolean)
	 */
	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setStart(int)
	 */
	public void setStart(int i) {
		start = i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolColumn#setType(int)
	 */
	public void setType(int i) {
		type = i;
	}

	@Override
	public void setUsage(int usage) {
		this.usage = usage;
	}

	/**
	 * use value when parse error ?
	 * 
	 * @param useOnParseError true use<br>
	 *            false don't
	 */
	public void setUseOnParseError(boolean useOnParseError) {
		this.useOnParseError = useOnParseError;
	}

	/**
	 * set value to use when parse error of record value
	 * 
	 * @param valueOfParseError value
	 */
	public void setValueOfParseError(Object valueOfParseError) {
		this.valueOfParseError = valueOfParseError;
	}
}