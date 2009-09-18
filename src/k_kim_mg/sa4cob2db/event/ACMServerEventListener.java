package k_kim_mg.sa4cob2db.event;
import k_kim_mg.sa4cob2db.ACMSession;
/**
 * SQLNetServer�Υ��٥�Ȥ���ª����
 * @author kenji
 */
public interface ACMServerEventListener {
	/**
	 * �����С������Ϥ��줿
	 * @param e ���٥�ȥ��֥�������
	 */
	public void serverStarted(ACMServerEvent e);
	/**
	 * �����С�����λ����
	 * @param e ���٥�ȥ��֥�������
	 */
	public void serverEnding(ACMServerEvent e);
	/**
	 * ���å�����ɲä��줿
	 * @param e �����Х��֥�������
	 * @param session �ɲä��줿���å����
	 */
	public void sessionAdded(ACMServerEvent e, ACMSession session);
	/**
	 * ���å���󤬺�����줿
	 * @param e �����Х��֥�������
	 * @param session ������줿���å����
	 */
	public void sessionRemoved(ACMServerEvent e, ACMSession session);
}
