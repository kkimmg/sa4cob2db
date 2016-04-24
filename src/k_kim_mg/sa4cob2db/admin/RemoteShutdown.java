package k_kim_mg.sa4cob2db.admin;

import java.io.FileReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.Properties;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;

/**
 * Remote shutdown
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class RemoteShutdown {
	/**
	 * shutdown remote server
	 * 
	 * @param args name of property file
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("No FileName.");
			return;
		} else {
			try {
				FileReader reader = new FileReader(args[0]);
				Properties properties = new Properties();
				properties.load(reader);
				RemoteShutdown.shutdown(properties);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * shutdown remote server
	 * 
	 * @param properties Properties
	 */
	public static void shutdown(Properties properties) throws RemoteException, MalformedURLException, NotBoundException {
		String host = properties.getProperty("host", "localhost");
		String port = properties.getProperty("port", MinAdminAdapter.DefaultRMIPort);
		String name = properties.getProperty("name", MinAdminAdapter.DefaultServerName);
		String user = properties.getProperty("user", "");
		String password = properties.getProperty("password", "");
		RemoteShutdown.shutdown(host, port, name, user, password);
	}

	/**
	 * shutdown remote server
	 * 
	 * @param host hostname
	 * @param port server port
	 * @param name server name
	 * @param user administrator name
	 * @param password administrator password
	 * @throws RemoteException RMI Exception
	 * @throws MalformedURLException Invalid URL
	 * @throws NotBoundException Server Not Found
	 */
	public static void shutdown(String host, String port, String name, String user, String password) throws RemoteException, MalformedURLException, NotBoundException {
		try {
			String uri = "rmi://" + host + ":" + port + "/" + name;
			Object remote = Naming.lookup(uri);
			IMinAdmin admin = (IMinAdmin) remote;
			if (admin.shutdown(SQLNetServer.SHUTDOWN_NORMAL, user, password)) {
				System.out.println("shutdoun OK");
			} else {
				System.out.println("shutdoun NG(" + user + "/" + password + ")");
			}
		} catch (UnmarshalException ue) {
			System.out.println("shutdoun OK");
		}
	}
}
