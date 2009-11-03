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
 * 鐃緒申癲種申肇鐃緒申鐃緒申弌鐃緒申鬟轡鐃獣トワ申鐃緒申鐃藷すわ申
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class RemoteShutdown {
	/**
	 * 鐃緒申鐃藷ソ¥申鐃暑か鐃緒申孫圓鐃緒申鐃	 * @param args ����ե�����̾
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
	 * ��⡼�ȥ����С���åȥ�����
	 * @param properties �������
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
	 * ��⡼�ȥ����С���åȥ�����
	 * @param host �ۥ���̾
	 * @param port �ݡ���
	 * @param name �����С�̾
	 * @param user �����̾
	 * @param password �ѥ����
	 * @throws RemoteException RMI��Ϣ���顼
	 * @throws MalformedURLException RMI��Ϣ���顼
	 * @throws NotBoundException RMI��Ϣ���顼
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
