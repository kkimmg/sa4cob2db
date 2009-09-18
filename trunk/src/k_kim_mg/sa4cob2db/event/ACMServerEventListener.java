package k_kim_mg.sa4cob2db.event;
import k_kim_mg.sa4cob2db.ACMSession;
/**
 * SQLNetServerのイベントを捕捉する
 * @author kenji
 */
public interface ACMServerEventListener {
	/**
	 * サーバーが開始された
	 * @param e イベントオブジェクト
	 */
	public void serverStarted(ACMServerEvent e);
	/**
	 * サーバーが終了する
	 * @param e イベントオブジェクト
	 */
	public void serverEnding(ACMServerEvent e);
	/**
	 * セッションが追加された
	 * @param e サーバオブジェクト
	 * @param session 追加されたセッション
	 */
	public void sessionAdded(ACMServerEvent e, ACMSession session);
	/**
	 * セッションが削除された
	 * @param e サーバオブジェクト
	 * @param session 削除されたセッション
	 */
	public void sessionRemoved(ACMServerEvent e, ACMSession session);
}
