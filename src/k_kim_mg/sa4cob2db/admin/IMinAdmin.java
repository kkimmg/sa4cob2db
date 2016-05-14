package k_kim_mg.sa4cob2db.admin;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface IMinAdmin extends Remote, Serializable {
	/**
	 * @param mode mode
	 * @param admin user name
	 * @param password password
	 * @return true success false failure
	 * @throws RemoteException Exception
	 */
	public boolean shutdown(int mode, String admin, String password) throws RemoteException;
}
