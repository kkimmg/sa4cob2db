package k_kim_mg.sa4cob2db.admin;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface IMinAdmin extends Remote, Serializable {
	/**
	 * シャットダウンする
	 * @param mode モード
	 * @param admin 管理者ユーザー名
	 * @param password パスワード
	 * @return true 成功</br>false 失敗
	 * @throws RemoteException RMIエラー
	 */
	public boolean shutdown(int mode, String admin, String password) throws RemoteException;
}
