package k_kim_mg.sa4cob2db;
import java.util.List;

import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
/**
 * �᥿�ǡ���
 * @author ���줪��
 * @version 1.0
 * ���ܥ�ե�����Υ쥳���ɥ쥤�����Ȥ˴ؤ��������Ǽ����
 */
public interface CobolRecordMetaData {
	/**
	 * ��̾���ɲ�
	 * @param alias �ɲä�����̾
	 */
	public void addAlias(String alias);
	/**
	 * ����ɲ�
	 * @param column �ɲä�����
	 */
	public void addColumn(CobolColumn column);
	/**
	 * ��������ɲ�
	 * @param column �ɲä�����
	 */
	public void addKey(CobolColumn column);
	/**
	 * ���ԡ��κ���
	 * @return ���ԡ�
	 */
	public CobolRecordMetaData createCopy();
	/**
	 * ��ΰ��֤��������
	 * @param column ��
	 * @return ��ΰ���
	 * @throws CobolRecordException �󤬸��Ĥ���ʤ��ä��Ȥ�
	 */
	public int findColumn(CobolColumn column) throws CobolRecordException;
	/**
	 * ����֤μ���
	 * @param column ���֤����������
	 * @param index ������ΰ���
	 * @return ��ΰ���
	 */
	public int findColumn(CobolColumn column, int index) throws CobolRecordException;
	/**
	 * ̾��������ΰ��֤��������
	 * @param name ��̾
	 * @return ��ΰ���
	 * @throws CobolRecordException �󤬸��Ĥ���ʤ��ä��Ȥ�
	 */
	public int findColumn(String name) throws CobolRecordException;
	/**
	 * ��̾�μ���
	 * @param i i���ܤ���̾
	 * @return ��̾
	 */
	public String getAlias(int i);
	/**
	 * ���Υ쥳���ɤ����äƤ�����̾�ο�
	 * @return ��̾�ο�
	 */
	public int getAliasCount();
	/**
	 * ����ǥå�������ΰ���
	 * @return ����ǥå�������Υꥹ��
	 */
	public List<CobolIndex> getCobolIndexes();
	/**
	 * ���Υ쥳���ɤ���Ͽ����Ƥ�����
	 * @param i �󥤥�ǥå���
	 * @return ��
	 */
	public CobolColumn getColumn(int i);
	/**
	 * ���Υ쥳���ɤ���Ͽ����Ƥ�����
	 * @param name ��̾
	 * @return ��
	 */
	public CobolColumn getColumn(String name) throws CobolRecordException;
	/**
	 * ���Υ쥳���ɤ���Ͽ����Ƥ�����ο�
	 * @return ���Υ쥳���ɤ���Ͽ����Ƥ�����ο�
	 */
	public int getColumnCount();
	/**
	 * ���Υ᥿�ǡ��������̤ʥ��饹���饤�󥹥��󥹤��������
	 * @return ���饹̾�����ꤷ�ʤ�����""�ޤ���null
	 */
	public String getCustomFileClassName();
	/**
	 * �쥳���ɤΥ��󥳡���
	 */
	public String getEncode();
	/**
	 * ��ե�������ɤ߼��Хåե��ν��������
	 * @return ��ե�������ɤ߼��Хåե��ν��������
	 */
	public int getInitialSequencialReadBufferSize();
	/**
	 * �������ꤵ�줿��
	 * @param i ���ꤵ�줿����
	 * @return ������
	 */
	public CobolColumn getKey(int i);
	/**
	 * �������ꤵ�줿��ο����֤�
	 * @return �������ꤵ�줿��ο�
	 */
	public int getKeyCount();
	/**
	 * ���٥�ȥꥹ�ʤΥ��饹�Υꥹ��
	 * @return �ꥹ��
	 */
	public List<Class<? extends CobolFileEventListener>> getListenerClasses();
	/**
	 * ��ե�������ɤ߼��Хåե��ν��������
	 * @return ��ե�������ɤ߼��Хåե��ν��������
	 */
	public int getMaximumSequencialReadBufferSize();
	/**
	 * ��ե�������ɤ߼��Хåե��κǾ�������
	 * @return ��ե�������ɤ߼��Хåե��κǾ�������
	 */
	public int getMinimumSequencialReadBufferSize();
	/**
	 * �쥳����̾
	 * @return �쥳����̾
	 */
	public String getName();
	/**
	 * ���Υ쥳���ɤ�1�쥳���ɤ�����ΥХ��ȿ����֤��ޤ�
	 * @return ���Υ쥳���ɤ�1�쥳���ɤ�����ΥХ��ȿ�
	 */
	public int getRowSize();
	/**
	 * ��̾�κ��
	 * @param alias ���������̾
	 */
	public void removeAlias(String alias);
	/**
	 * ��κ��
	 * @param column ���������
	 */
	public void removeColumn(CobolColumn column);
	/**
	 * ��κ��
	 * @param index ��������󥤥�ǥå���
	 */
	public void removeColumn(int index);
	/**
	 * ������κ��
	 * @param column ������륭����
	 */
	public void removeKey(CobolColumn column);
	/**
	 * ������κ��
	 * @param index ������륭���󥤥�ǥå���
	 */
	public void removeKey(int index);
	/**
	 * ���󥳡��ǥ���̾������
	 * @param string ���󥳡��ǥ���̾
	 */
	public void setEncode(String string);
	/**
	 * ��ե�������ɤ߼��Хåե��ν��������
	 * @param value ��ե�������ɤ߼��Хåե��ν��������
	 */
	public void setInitialSequencialReadBufferSize(int value);
	/**
	 * ��ե�������ɤ߼��Хåե��κ��祵����
	 * @param value ��ե�������ɤ߼��Хåե��κ��祵����
	 */
	public void setMaximumSequencialReadBufferSize(int value);
	/**
	 * ��ե�������ɤ߼��Хåե��κǾ�������
	 * @param value ��ե�������ɤ߼��Хåե��κǾ�������
	 */
	public void setMinimumSequencialReadBufferSize(int value);
	/**
	 * �쥳����̾
	 * @param name �쥳����̾
	 */
	public void setName(String name);
}
