package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * �����С��ǲ��������ä��ߤ�����
 * @author ���줪��
 */
public class ACMServerEvent extends EventObject {
	/**
	 * �ǥե���ȤΥС������ID
	 */
	private static final long serialVersionUID = 1L;
	/** ���٥�Ȥ�ȯ���� */
	private transient SQLNetServer server;
	/**
	 * ���󥹥ȥ饯��
	 * @param server	���٥�Ȥ�ȯ����(source)
	 */
	public ACMServerEvent (SQLNetServer server) {
		super(server);
		this.server = server;
	}
	/**
	 * �����С�
	 * @return �����С������٥�Ȥ�ȯ����
	 */
	public SQLNetServer getServer() {
		return server;
	}
}
