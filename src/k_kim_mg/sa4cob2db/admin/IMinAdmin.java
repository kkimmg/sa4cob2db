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
	 * @param mode
	 * @param admin
	 * @param password 
	 * @return true </br> false
	 * @throws RemoteException 
	 */
	public boolean shutdown(int mode, String admin, String password) throws RemoteException;
}
