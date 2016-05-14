package k_kim_mg.sa4cob2db.WebSample.RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;

public class RMITester {

	/**
	 * RMI Server Main
	 * 
	 * @param args arguments
	 */
	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.getRegistry();
			// CobSubServer1 stub = (CobSubServer1)
			// registry.lookup("CobSubServer");
		} catch (Exception e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}

	}

}
