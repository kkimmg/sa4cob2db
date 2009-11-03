package k_kim_mg.sa4cob2db.event;

import java.util.EventListener;
/**
 * セッション中に発生したイベントを処理する機能
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface ACMSessionEventListener extends EventListener {
	/**
	 * セッションで使用するファイルオブジェクトが作成された
	 * @param e	セッションとファイルを含むイベント
	 */
	public void fileCreated(ACMSessionEvent e);
	/**
	 * セッションで使用するファイルオブジェクトが廃棄された
	 * @param e	セッションとファイルを含むイベント
	 */
	public void fileDestroyed(ACMSessionEvent e);
	/**
	 * トランザクションがコミットされた
	 * @param e	セッションを含むイベント
	 */
	public void transactionCommited(ACMSessionEvent e);
	/**
	 * トランザクションがロールバックされた
	 * @param e	セッションを含むイベント
	 */
	public void transactionRollbacked(ACMSessionEvent e);
}
