package k_kim_mg.sa4cob2db.admin;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * �Ǿ��δ���ǽ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface IMinAdmin extends Remote, Serializable {
	/**
	 * ����åȥ����󤹤�
	 * @param mode �⡼��
	 * @param admin ����ԥ桼����̾
	 * @param password �ѥ����
	 * @return true ����</br>false ����
	 * @throws RemoteException RMI���顼
	 */
	public boolean shutdown(int mode, String admin, String password) throws RemoteException;
}
