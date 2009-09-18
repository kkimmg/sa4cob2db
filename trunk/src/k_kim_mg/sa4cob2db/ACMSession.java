package k_kim_mg.sa4cob2db;
import java.io.Serializable;

import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
/** セッション */
public interface ACMSession extends Serializable {
	/**
	 * ファイル(への参照)を作成する
	 * @param name	ファイル名
	 * @return	ファイルオブジェクト
	 */
	public CobolFile createFile(String name);
	/**
	 * ファイル(への参照)を開放する
	 * @param name	ファイル名
	 */
	public void destroyFile(String name);
	/**
	 * セッションID
	 * @return セッションID
	 */
	public String getSessionId();
	/**
	 * 作成済みのファイル(への参照)を取得する
	 * @param name	ファイル名
	 * @return	ファイルオブジェクト
	 */
	public CobolFile getFile(String name);
	/**
	 * イベントリスナを追加する
	 * @param listener イベントリスナ
	 */
	public void addACMSessionEventListener(ACMSessionEventListener listener);
	/**
	 * イベントリスナを削除する
	 * @param listener イベントリスナ
	 */
	public void removeACMSessionEventListener(ACMSessionEventListener listener);
}