package k_kim_mg.sa4cob2db.WebSample.RMI;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;

/**
 * Starts RMI Server
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class RMIStarter {
	/** default port */
	public static final int DEFAULT_PORT = 1099;
	/** default name */
	public static final String DEFAULT_NAME = "CobSubServer";
	/** port */
	private int port = DEFAULT_PORT;
	/** name */
	private String name = DEFAULT_NAME;

	/**
	 * unbind this object
	 */
	public void endRMIRegistryServer() {
		try {
			// System.setSecurityManager(new RMISecurityManager());
			LocateRegistry.getRegistry(port).unbind(name);
		} catch (AccessException e1) {
			// e1.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (RemoteException e1) {
			// e1.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (NotBoundException e1) {
			// e1.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
		}
	}

	/**
	 * main
	 * 
	 * @param args arguments [0] name [1] port
	 */
	public static void main(String[] args) {
		String name = DEFAULT_NAME;
		int port = DEFAULT_PORT;
		if (args.length >= 1) {
			name = args[0];
			if (args.length >= 2) {
				try {
					port = Integer.valueOf(args[1]);
				} catch (NumberFormatException e) {
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		RMIStarter starter = new RMIStarter(port, name);
		starter.startRMIRegistryServer();
	}

	/**
	 * Default Constructor
	 */
	public RMIStarter() {
		this(DEFAULT_PORT);
	}

	/**
	 * Constructor
	 * 
	 * @param port server port
	 */
	public RMIStarter(int port) {
		this(port, DEFAULT_NAME);
	}

	/**
	 * Constructor
	 * 
	 * @param name server name
	 */
	public RMIStarter(String name) {
		this(DEFAULT_PORT, name);
	}

	/**
	 * Constructor
	 * 
	 * @param port server port
	 * @param name server name
	 */
	public RMIStarter(int port, String name) {
		this.port = port;
		this.name = name;
	}

	/**
	 * rebind this object
	 * 
	 * @param port port
	 * @param name server name
	 */
	public void startRMIRegistryServer() {
		SQLNetServer.logger.info("RMI Server Starting.");
		Registry reg;
		// System.setSecurityManager(new RMISecurityManager());
		try {
			CobSubServer1 serv = new CobSubServer1Impl();
			// create server
			reg = LocateRegistry.getRegistry();
			// bind
			reg.rebind(name, serv);
			// log
			SQLNetServer.logger.info("RMI Server Started.");
		} catch (AccessException e) {
			// e.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (RemoteException e) {
			// e.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
