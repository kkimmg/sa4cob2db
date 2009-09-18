/**
 * 
 */
package k_kim_mg.sa4cob2db.admin;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;

/**
 * �Ǿ��δ�����ǽ
 * @author ���줪��
 */
public class MinAdmin extends UnicastRemoteObject {
	/** �С������ */
    private static final long serialVersionUID = 1L;
	/** �������� */
	private SQLNetServer server;
	/**
	 * ���󥹥ȥ饯��
	 * @param port ���եݡ���
	 * @throws RemoteException RMI���顼
	 */
	public MinAdmin(SQLNetServer server, int port) throws RemoteException {
		super(port);
		this.server = server;
	}
	/**
	 * ���󥹥ȥ饯��
	 * @throws RemoteException RMI���顼
	 */
	public MinAdmin(SQLNetServer server) throws RemoteException {
		super();
		this.server = server;
	}
	/**
	 * ����åȥ����󤹤�
	 * @param mode �⡼��
	 * @param admin �����ԥ桼����̾
	 * @param password �ѥ����
	 * @return true ����</br>false ����
	 * @throws RemoteException RMI���顼
	 */
	public boolean shutdown (int mode, String admin, String password) throws RemoteException {
		boolean ret = server.confirmAdminPassword(admin, password);
		if (ret) {
			try {
				server.shutdown(mode);
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
			}
		}
		return ret;
	}
}
