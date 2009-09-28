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
 * リモートサーバーをシャットダウンする
 * @author <a mailto="k_kim_mg@mvh.biglobe.ne.jp">Kenji Kimura</a>
 */
public class RemoteShutdown {
	/**
	 * コンソールから実行する
	 * @param args 設定ファイル名
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
	 * リモートサーバーをシャットダウンする
	 * @param properties 設定情報
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
	 * リモートサーバーをシャットダウンする
	 * @param host ホスト名
	 * @param port ポート
	 * @param name サーバー名
	 * @param user 管理社名
	 * @param password パスワード
	 * @throws RemoteException RMI関連エラー
	 * @throws MalformedURLException RMI関連エラー
	 * @throws NotBoundException RMI関連エラー
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
