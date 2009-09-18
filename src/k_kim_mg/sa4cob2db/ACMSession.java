package k_kim_mg.sa4cob2db;
import java.io.Serializable;

import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
/** ���å���� */
public interface ACMSession extends Serializable {
	/**
	 * �ե�����(�ؤλ���)���������
	 * @param name	�ե�����̾
	 * @return	�ե����륪�֥�������
	 */
	public CobolFile createFile(String name);
	/**
	 * �ե�����(�ؤλ���)��������
	 * @param name	�ե�����̾
	 */
	public void destroyFile(String name);
	/**
	 * ���å����ID
	 * @return ���å����ID
	 */
	public String getSessionId();
	/**
	 * �����ѤߤΥե�����(�ؤλ���)���������
	 * @param name	�ե�����̾
	 * @return	�ե����륪�֥�������
	 */
	public CobolFile getFile(String name);
	/**
	 * ���٥�ȥꥹ�ʤ��ɲä���
	 * @param listener ���٥�ȥꥹ��
	 */
	public void addACMSessionEventListener(ACMSessionEventListener listener);
	/**
	 * ���٥�ȥꥹ�ʤ�������
	 * @param listener ���٥�ȥꥹ��
	 */
	public void removeACMSessionEventListener(ACMSessionEventListener listener);
}