package k_kim_mg.sa4cob2db;
import java.io.Serializable;

import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
/**
 * ���ܥ�ե�����
 * @author ���줪��
 */
public interface CobolFile extends Serializable {
	/** ���������⡼�� */
	public static final int ACCESS_DYNAMIC = 1;
	/** ���������⡼�� */
	public static final int ACCESS_RANDOM = 2;
	/** ���������⡼�� */
	public static final int ACCESS_SEQUENCIAL = 0;
	/**
	 * IS EQUAL TO IS =
	 */
	public static final int IS_EQUAL_TO = 0;
	/**
	 * IS GREATER THAN IS >
	 */
	public static final int IS_GREATER_THAN = 1;
	/**
	 * IS GREATER THAN OR EQUAL TO IS >=
	 */
	public static final int IS_GREATER_THAN_OR_EQUAL_TO = 3;
	/**
	 * IS NOT LESS THAN IS NOT < IS >=
	 */
	public static final int IS_NOT_LESS_THAN = 3;
	/** �����ץ�⡼�� */
	public static final int MODE_EXTEND = 3;
	/** �����ץ�⡼�� */
	public static final int MODE_INPUT = 0;
	/** �����ץ�⡼�� */
	public static final int MODE_INPUT_OUTPUT = 2;
	/** �����ץ�⡼�� */
	public static final int MODE_OUTPUT = 1;
	/**
	 * CobolFileEventListener���ɲä���
	 * @param listener �ɲä���CobolFileEventListener
	 */
	public void addCobolFileEventListener(CobolFileEventListener listener);
	/**
	 * ����ǥå������ɲ�
	 * @param index ����ǥå����Ѥ���ޥå�
	 * @param file ����ǥå����ե�����
	 */
	public void addIndex(CobolIndex index, CobolFile file);
	/**
	 * ���å����δ�Ϣ�դ�
	 * @param session ��Ϣ�դ��륻�å����
	 */
	public void bindSession(ACMSession session);
	/**
	 * �ե�������Ĥ���
	 * @return ���ơ�����
	 */
	public FileStatus close();
	/**
	 * �쥳���ɤκ��
	 * @param record �������
	 * @return �ե����륹�ơ�����
	 */
	public FileStatus delete(byte[] record);
	/**
	 * ���������⡼��
	 * @return ���������⡼��
	 */
	public int getAccessMode();
	/**
	 * ���ߤι�
	 * @return ���ߤι�
	 */
	public int getCurrentRow();
	/**
	 * ���Υե�����Υ᥿�ǡ���
	 * @return �᥿�ǡ������֥�������
	 */
	public CobolRecordMetaData getMetaData();
	/**
	 * �����ץ�⡼��
	 * @return �����ץ�⡼��
	 */
	public int getOpenMode();
	/**
	 * �ե�����˴ޤޤ��쥳���ɤιԿ��ޤ��ϸ��ߤޤǤ��ɤ߹��ޤ줿�Կ�
	 * @return �Կ�
	 */
	public int getRowCount();
	/**
	 * ��Ϣ�դ���줿���å����
	 * @return ���å����
	 */
	public ACMSession getSession();
	/**
	 * ���֤Ť�����
	 * @param record ������ޤ�Х�������
	 * @return ���ơ�����
	 */
	public FileStatus move(byte[] record);
	/**
	 * ���Υ쥳���ɤذ�ư����
	 * @return ���ơ�����
	 */
	public FileStatus next();
	/**
	 * �ե����륪���ץ�
	 * @param mode �⡼�� �����ץ�⡼�� input/output/expant/input-output
	 * @param accessmode ���������⡼�ɤޤ��ϥե����빽�� sequencial/random/dynamic
	 * @return ���ơ�����
	 */
	public FileStatus open(int mode, int accessmode);
	/**
	 * ���Υ쥳���ɤذ�ư����
	 * @return ���ơ�����
	 */
	public FileStatus previous();
	/**
	 * ���߰��֤Ť��Ƥ���쥳���ɤ���Х��ȥ����ɤ��������
	 * @param record �ɤ߹��ߥ쥳����
	 * @return ���ơ�����
	 */
	public FileStatus read(byte[] record);
	/**
	 * CobolFileEventListener��������
	 * @param listener �������CobolFileEventListener
	 */
	public void removeCobolFileEventListener(CobolFileEventListener listener);
	/**
	 * ���߰��֤Ť��Ƥ���쥳���ɤ�Х��ȥ����ɤǾ�񤭤���
	 * @param record �񤭹��ߥ쥳����
	 * @return ���ơ�����
	 */
	public FileStatus rewrite(byte[] record);
	/**
	 * �����դ�����
	 * @param mode �⡼��(EQ GT �ʤ�)
	 * @param record ������ޤ�쥳����
	 * @return ���ơ�����
	 */
	public FileStatus start(int mode, byte[] record);
	/**
	 * ����ǥå����ˤ�븡��
	 * @param IndexName ����ǥå���̾
	 * @param mode �������ȥ⡼��
	 * @param record �����쥳����
	 * @return ���ơ�����
	 * @throws CobolRecordException ���顼ȯ����
	 */
	public FileStatus start(String IndexName, int mode, byte[] record);
	/**
	 * �����դ�����
	 * @param mode �⡼��(EQ GT �ʤ�)
	 * @param record ������ޤ�쥳����
	 * @param duplicates ��������ʣ���Ƥ��뤫�ɤ���
	 * @return ���ơ�����
	 */
	public FileStatus start(int mode, byte[] record, boolean duplicates);
	/**
	 * �ե�����˥쥳���ɤ��ɲä���
	 * @param record �񤭹��ߥ쥳����
	 * @return ���ơ�����
	 */
	public FileStatus write(byte[] record);
	/**
	 * ���Υե�����ϸ��߳�����Ƥ��뤫��
	 * @return true ������Ƥ���<br>
	 *         false �Ĥ��Ƥ���
	 */
	public boolean isOpened();
}