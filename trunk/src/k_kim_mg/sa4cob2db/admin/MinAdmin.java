/**
 * 
 */
package k_kim_mg.sa4cob2db.admin;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * Smallest administrative functions
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class MinAdmin extends UnicastRemoteObject implements IMinAdmin {
	/** Version */
	private static final long serialVersionUID = 1L;
	/** Managing server */
	private SQLNetServer server;
	/**
	 * Constructor
	 * @param port RMI Port
	 * @throws RemoteException RemoteException
	 */
	public MinAdmin(SQLNetServer server, int port) throws RemoteException, AlreadyBoundException {
		super(port);
		this.server = server;
	}
	/**
	 * Constructor
	 * @throws RemoteException RemoteException
	 */
	public MinAdmin(SQLNetServer server) throws RemoteException, AlreadyBoundException {
		super();
		this.server = server;
	}
	/**
	 * Shutdown Server
	 * @param mode Mode
	 * @param admin Admin Username
	 * @param password Admin Password
	 * @return true Success</br>false Fault
	 * @throws RemoteException Remote Exception
	 */
	public boolean shutdown(int mode, String admin, String password) throws RemoteException {
		boolean ret = server.confirmAdminPassword(admin, password);
		if (ret) {
			try {
				SQLNetServer.logger.log(Level.INFO, "Now Shutdown...");
				server.shutdown(mode);
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return ret;
	}
}
