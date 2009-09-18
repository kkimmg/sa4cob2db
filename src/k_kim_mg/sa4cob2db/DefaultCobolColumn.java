package k_kim_mg.sa4cob2db;
/**
 * �ǥե���ȤΥ��ܥ���
 * @author ���줪��
 */
public class DefaultCobolColumn implements CobolColumn {
	/**
	 * �ǡ�������ʸ��ɽ���ʤ����󡦡�����
	 * @param type �ǡ�����
	 * @return ʸ��ɽ��
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
	/** �᥿�ǡ��� */
	private CobolRecordMetaData cobolRecordMetaData;
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolColumn#getFormat()
	 */
	private String format;
	/** �����ͤʤ�DB��NULL�򥻥åȤ��� */
	private String forNull = null;
	/** �⤷DB����ͤ�NULL�ʤ� */
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
	/** �������ʲ��η�� */
	private int numberOfDecimal = 0;
	/** ���ꥸ�ʥ� */
	private CobolColumn original = this;
	/** �����������դ����� */
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
	/** �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ������ */
	private boolean useOnParseError = false;
	/** �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ������ */
	private Object valueOfParseError = null;
	/**
	 * ���󥹥ȥ饯��
	 * @param meta �᥿�ǡ���
	 */
	public DefaultCobolColumn(CobolRecordMetaData meta) {
		this.cobolRecordMetaData = meta;
	}
	/**
	 * °���򥳥ԡ�����
	 * @param copy ���ԡ��о�
	 * @return ���ԡ��о�
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
	 * �����ͤʤ�DB��NULL�򥻥åȤ���
	 * @return ���ؤ���
	 */
	public String getForNull() {
		return forNull;
	}
	/**
	 * �⤷DB����ͤ�NULL�ʤ�
	 * @return ���ؤ���
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
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ������
	 * @return ���ؤ���
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
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ�����硢�ͤ����ꤹ��
	 * @return the ���뤫���ʤ���
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
	 * �����ͤʤ�DB��NULL�򥻥åȤ���
	 * @param forNull ���ؤ���
	 */
	public void setForNull(String forNull) {
		this.forNull = forNull;
	}
	/**
	 * �⤷DB����ͤ�NULL�ʤ�
	 * @param ifNull ���ؤ���
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
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ�����硢�ͤ����ꤹ��
	 * @param useOnParseError ���뤫���ʤ���
	 */
	public void setUseOnParseError(boolean useOnParseError) {
		this.useOnParseError = useOnParseError;
	}
	/**
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ������
	 * @param valueOfParseError ���ؤ���
	 */
	public void setValueOfParseError(Object valueOfParseError) {
		this.valueOfParseError = valueOfParseError;
	}
}