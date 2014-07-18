package k_kim_mg.sa4cob2db.WebSample.RMI;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.logging.Level;
import k_kim_mg.sa4cob2db.event.ACMServerEvent;
import k_kim_mg.sa4cob2db.event.ACMServerEventAdapter;
import k_kim_mg.sa4cob2db.event.ACMServerEventListener;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * Starts RMI Server
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class RMIStarter extends ACMServerEventAdapter implements ACMServerEventListener {
	/** rmiregistry */
	private Process rmiregistry = null;
	/** default port */
	public static final int DEFAULT_PORT = 1099;
	/** default name */
	public static final String DEFAULT_NAME = "CobSubServer";
	/** port */
	private int serverPort = DEFAULT_PORT;
	/** name */
	private String serverName = DEFAULT_NAME;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#serverEnding(k_kim_mg.
	 * sa4cob2db.event.ACMServerEvent)
	 */
	@Override
	public void serverEnding(ACMServerEvent e) {
		try {
			LocateRegistry.getRegistry(serverPort).unbind(serverName);
		} catch (AccessException e1) {
			e1.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (RemoteException e1) {
			e1.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (NotBoundException e1) {
			e1.printStackTrace();
			SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
		}
		if (rmiregistry != null) {
			try {
				rmiregistry.destroy();
			} catch (Exception e1) {
				SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#serverStarted(k_kim_mg
	 * .sa4cob2db.event.ACMServerEvent)
	 */
	@Override
	public void serverStarted(ACMServerEvent e) {
		int wPort = DEFAULT_PORT;
		String wName = DEFAULT_NAME;
		// Port
		String portTxt = e.getServer().getPropertie("RMI_SERVER_PORT");
		if (portTxt != null) {
			try {
				wPort = Integer.valueOf(portTxt);
			} catch (NumberFormatException e1) {
				SQLNetServer.logger.log(Level.WARNING, e1.getMessage(), e1);
			}
		}
		// Name
		String nextTxt = e.getServer().getPropertie("RMI_SERVER_NAME");
		if (nextTxt != null) {
			wName = nextTxt;
		}
		// start registry
		String rmicommand = e.getServer().getPropertie("RMIREGISTRY_COMMAND");
		if (rmicommand != null) {
			String rmioption = e.getServer().getPropertie("RMIREGISTRY_OPTION");
			try {
				rmiregistry = createProcess(rmicommand, wPort, rmioption);
				SQLNetServer.logger.log(Level.INFO, "rmiregistry:" + rmicommand + " " + wPort + " " + rmioption);
			} catch (IOException e1) {
				SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
		}
		// start server
		startRMIRegistryServer(wPort, wName);
	}
	/**
	 * Creates Process
	 * 
	 * @param processName name
	 * @param port port
	 * @param option option Please do not omit the "-J" at the beginning.
	 * @return process
	 * @throws IOException IOException
	 */
	private Process createProcess(String processName, int port, String option) throws IOException {
		ProcessBuilder builder = null;
		if (option == null || option.trim().length() == 0) {
			builder = new ProcessBuilder(processName, String.valueOf(port));
		} else {
			builder = new ProcessBuilder(processName, String.valueOf(port), option);
		}
		//
		Process process = builder.start();
		return process;
	}
	private Thread th;
	/**
	 * start RMI Registry Server
	 * 
	 * @param port port
	 * @param name server name
	 */
	protected void startRMIRegistryServer(int pport, String name) {
		final int port = pport;
		Runnable run = new Runnable() {
			public void run() {
				SQLNetServer.logger.info("RMI Server Starting.");
				Registry reg;
				System.setSecurityManager(new RMISecurityManager());
				try {
					CobSubServer1 serv = new CobSubServer1Impl();
					//
					CobSubServer1 stub = (CobSubServer1) UnicastRemoteObject.exportObject(serv, port);
					// create server
					reg = LocateRegistry.createRegistry(port);
					// bind
					reg.bind(DEFAULT_NAME, stub);
					// log
					SQLNetServer.logger.info("RMI Server Started.");
				} catch (AccessException e) {
					e.printStackTrace();
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				} catch (RemoteException e) {
					e.printStackTrace();
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				} catch (AlreadyBoundException e) {
					e.printStackTrace();
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		};
		th = new Thread(run);
		th.start();
	}
}
