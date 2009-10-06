/**
 * 
 */
package k_kim_mg.sa4cob2db.admin;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.event.ACMServerEvent;
import k_kim_mg.sa4cob2db.event.ACMServerEventAdapter;
import k_kim_mg.sa4cob2db.event.ACMServerEventListener;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * ����ǽ��SQLNetServer����³����
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class MinAdminAdapter extends ACMServerEventAdapter implements ACMServerEventListener {
	/**RMI̾*/
	public static String DefaultServerName = "ACMSERVADMIN";
	/**RMI�ݡ���*/
	public static String DefaultRMIPort = "12346";
	/** ���󥹥ȥ饯���� */
	public MinAdminAdapter() {
		super();
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#serverStarted(k_kim_mg
	 * .sa4cob2db.event.ACMServerEvent)
	 */
	@Override
	public void serverStarted(ACMServerEvent e) {
		try {
			SQLNetServer server = e.getServer();
			String ServerName = server.getPropertie("ACMSRVADMIN", DefaultServerName);
			String ServerPort = server.getPropertie("ACMRMIPORT", DefaultRMIPort);
			int RMIPort = Integer.parseInt(ServerPort);
			// ����ǽ�򳫻�
			IMinAdmin minAdmin = new MinAdmin(server);
			// RMI�򳫻�
			Registry reg = LocateRegistry.createRegistry(RMIPort);
			reg.bind(ServerName, minAdmin);
			// �?����
			SQLNetServer.logger.log(Level.INFO, "Admin Started.");
		} catch (NumberFormatException e1) {
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage());
		} catch (AccessException e1) {
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage());
		} catch (RemoteException e1) {
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage());
		} catch (AlreadyBoundException e1) {
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage());
		}
	}
}
