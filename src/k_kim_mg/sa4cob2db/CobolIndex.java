package k_kim_mg.sa4cob2db;

import java.util.Map;

/**
 * ����ǥå��������Ѥ���ޥå�
 */
public interface CobolIndex {
	/**
	 * ����ǥå��������󢪥ե�������
	 * @return	�ޥå�
	 */
	public Map<CobolColumn, CobolColumn> getIndexKey2FileColumn ();
	/**
	 * �ե����륭���󢪥���ǥå����ե��������
	 * @return	�ޥå�
	 */
	public Map<CobolColumn, CobolColumn> getFileKey2IndexColumn ();
	/**
	 * ����ǥå����ե�����μ���
	 * @return	����ǥå����ե�����
	 */
	public String getFileName ();
	/**
	 * Start̿����˻��Ѥ��륤��ǥå��������ꤹ�뤿��Υ���̾
	 * @return	����̾(relative key)
	 */
	public String getIndexKeyName ();
	/**
	 * ���Υ���ǥå����Ͻ�ʣ���뤫
	 * @return	��ʣ���뤫�ɤ���
	 */
	public boolean isDuplicates ();
}