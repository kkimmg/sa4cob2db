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
 * 最小の管理機能
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class MinAdmin extends UnicastRemoteObject implements IMinAdmin {
	/** バージョン */
	private static final long serialVersionUID = 1L;
	/** 管理する */
	private SQLNetServer server;
	/**
	 * コンストラクタ
	 * @param port 受付ポート
	 * @throws RemoteException RMIエラー
	 */
	public MinAdmin(SQLNetServer server, int port) throws RemoteException, AlreadyBoundException {
		super(port);
		this.server = server;
	}
	/**
	 * コンストラクタ
	 * @throws RemoteException RMIエラー
	 */
	public MinAdmin(SQLNetServer server) throws RemoteException, AlreadyBoundException {
		super();
		this.server = server;
	}
	/**
	 * シャットダウンする
	 * @param mode モード
	 * @param admin 管理者ユーザー名
	 * @param password パスワード
	 * @return true 成功</br>false 失敗
	 * @throws RemoteException RMIエラー
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
