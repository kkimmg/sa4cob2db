/*
 * Created on 2004/06/09 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db;
/**
 * ���ܥ�ץ��������ɽ�����֥�������
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobolColumn {
	/** ���շ� */
	public static int TYPE_DATE = 7;
	/** �������� */
	public static int TYPE_DOUBLE = 6;
	/** �������� */
	public static int TYPE_FLOAT = 5;
	/** ������ */
	public static int TYPE_INTEGER = 1;
	/** ������ */
	public static int TYPE_LONG = 4;
	/** ���ܸ췿 */
	public static int TYPE_NCHAR = 3;
	/** ��¤�η� */
	public static int TYPE_STRUCT = 11;
	/** ���﷿ */
	public static int TYPE_TIME = 8;
	/** ���ջ��﷿ */
	public static int TYPE_TIMESTAMP = 10;
	/** �ѿ����� */
	public static int TYPE_XCHAR = 2;
	/**
	 * ���ԡ��κ���
	 * @return ���ԡ���
	 */
	public CobolColumn createCopy(CobolRecordMetaData meta);
	/**
	 * �쥳���ɾ���μ���
	 * @return �쥳���ɥ쥤������
	 */
	public CobolRecordMetaData getCobolRecordMetaData();
	/**
	 * ��ν񼰤�ɽ��ʸ����
	 * @return ��ʸ����
	 */
	public String getFormat();
	/**
	 * �����ͤʤ�DB��NULL�򥻥åȤ���
	 * @return ���ؤ���
	 */
	public String getForNull();
	/**
	 * �⤷DB����ͤ�NULL�ʤ�
	 * @return ���ؤ���
	 */
	public String getIfNull();
	/**
	 * ���Ĺ��
	 * @return ����
	 */
	public int getLength();
	/**
	 * ����̤��뤿���̾��
	 * @return ��̾
	 */
	public String getName();
	/**
	 * ���ͷ�����ξ������ʲ��η��
	 * @return ���ͷ�����ξ������ʲ��η��
	 */
	public int getNumberOfDecimal();
	/**
	 * ������Υ١����ˤʤ���
	 * @return ���ꥸ�ʥ���
	 */
	public CobolColumn getOriginalCobolColumn();
	/**
	 * ��ΥХ���Ĺ���֤�
	 * @return ʪ��Ĺ
	 */
	public int getPhysicalLength();
	/**
	 * ��γ��ϰ���<br/>
	 * 0�ǻϤޤ�ޤ�
	 * @return ���ϰ���
	 */
	public int getStart();
	/**
	 * ��Υǡ�����
	 * @return �ǡ�����
	 */
	public int getType();
	/**
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ������
	 * @return ���ؤ���
	 */
	public Object getValueOfParseError();
	/**
	 * ���ͷ����󤬡ܡ���������Ĥ��ɤ���
	 * @return true ����դ�<br/>
	 *         false ���ʤ�
	 */
	public boolean isSigned();
	/**
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ�����硢�ͤ����ꤹ��
	 * @return the ���뤫���ʤ���
	 */
	public boolean isUseOnParseError();
	/**
	 * �쥳���ɾ��������
	 * @param cobolRecordMetaData �쥳���ɥ쥤������
	 */
	public void setCobolRecordMetaData(CobolRecordMetaData cobolRecordMetaData);
	/**
	 * ��ν񼰤�ɽ��ʸ����
	 * @param format ��
	 */
	public void setFormat(String format);
	/**
	 * �����ͤʤ�DB��NULL�򥻥åȤ���
	 * @param forNull ���ؤ���
	 */
	public void setForNull(String forNull);
	/**
	 * �⤷DB����ͤ�NULL�ʤ�
	 * @param ifNull ���ؤ���
	 */
	public void setIfNull(String ifNull);
	/**
	 * ���Ĺ��
	 * @param length ��
	 */
	public void setLength(int length);
	/**
	 * ����̤��뤿���̾��
	 * @param name ��̾
	 */
	public void setName(String name);
	/**
	 * ���ͷ�����ξ������ʲ��η��
	 * @param decimal ���ͷ�����ξ������ʲ��η��
	 */
	public void setNumberOfDecimal(int decimal);
	/**
	 * ������Υ١����ˤʤ���
	 * @param original ���ꥸ�ʥ���
	 */
	public void setOriginalCobolColumn(CobolColumn original);
	/**
	 * ��ΥХ���Ĺ���֤�
	 * @param length ʪ��Ĺ
	 */
	public void setPhysicalLength(int length);
	/**
	 * ���ͷ����󤬡ܡ���������Ĥ��ɤ���
	 * @param signed ��椢��
	 */
	public void setSigned(boolean signed);
	/**
	 * ��γ��ϰ���<br/>
	 * 0�ǻϤޤ�ޤ�
	 * @param start ���ϰ���
	 */
	public void setStart(int start);
	/**
	 * ��Υǡ�����
	 * @param type �ǡ�����
	 */
	public void setType(int type);
	/**
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ�����硢�ͤ����ꤹ��
	 * @param useOnParseError ���뤫���ʤ���
	 */
	public void setUseOnParseError(boolean useOnParseError);
	/**
	 * �Х������󢪥��֥������Ȥ��Ѵ��˼��Ԥ������
	 * @param valueOfParseError ���ؤ���
	 */
	public void setValueOfParseError(Object valueOfParseError);
}
