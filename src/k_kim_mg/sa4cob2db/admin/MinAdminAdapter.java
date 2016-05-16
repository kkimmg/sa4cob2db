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
 * Start Remote shutdown function with SqlNetServer starting.
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class MinAdminAdapter extends ACMServerEventAdapter implements ACMServerEventListener {
  /** RMI Name. */
  public static String DefaultServerName = "ACMSERVADMIN";
  /** Default RMI Port Number. */
  public static String DefaultRMIPort = "12346";

  /** Constructor. */
  public MinAdminAdapter() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#serverStarted(k_kim_mg
   * .sa4cob2db.event.ACMServerEvent)
   */
  @Override
  public void serverStarted(ACMServerEvent e) {
    try {
      SQLNetServer server = e.getServer();
      String ServerName = server.getPropertie("ACMSRVADMIN", DefaultServerName);
      String ServerPort = server.getPropertie("ACMRMIPORT", DefaultRMIPort);
      int RMIPort = Integer.parseInt(ServerPort);
      IMinAdmin minAdmin = new MinAdmin(server);
      Registry reg = LocateRegistry.createRegistry(RMIPort);
      reg.bind(ServerName, minAdmin);
      SQLNetServer.logger.log(Level.INFO, "Admin Started.");
    } catch (NumberFormatException e1) {
      SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
    } catch (AccessException e1) {
      SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
    } catch (RemoteException e1) {
      SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
    } catch (AlreadyBoundException e1) {
      SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
    }
  }
}
