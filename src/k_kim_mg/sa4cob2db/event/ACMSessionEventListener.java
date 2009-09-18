package k_kim_mg.sa4cob2db.event;

import java.util.EventListener;
/**
 * ���å�������ȯ���������٥�Ȥ�������뵡ǽ
 * @author ���줪��
 */
public interface ACMSessionEventListener extends EventListener {
	/**
	 * ���å����ǻ��Ѥ���ե����륪�֥������Ȥ��������줿
	 * @param e	���å����ȥե������ޤ।�٥��
	 */
	public void fileCreated(ACMSessionEvent e);
	/**
	 * ���å����ǻ��Ѥ���ե����륪�֥������Ȥ��Ѵ����줿
	 * @param e	���å����ȥե������ޤ।�٥��
	 */
	public void fileDestroyed(ACMSessionEvent e);
	/**
	 * �ȥ�󥶥�����󤬥��ߥåȤ��줿
	 * @param e	���å�����ޤ।�٥��
	 */
	public void transactionCommited(ACMSessionEvent e);
	/**
	 * �ȥ�󥶥�����󤬥���Хå����줿
	 * @param e	���å�����ޤ।�٥��
	 */
	public void transactionRollbacked(ACMSessionEvent e);
}
