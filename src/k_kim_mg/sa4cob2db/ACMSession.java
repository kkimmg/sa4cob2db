package k_kim_mg.sa4cob2db;
import java.io.Serializable;
import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
/** セッション */
public interface ACMSession extends Serializable {
	/**
	 * イベントリスナを追加する
	 * 
	 * @param listener イベントリスナ
	 */
	public void addACMSessionEventListener(ACMSessionEventListener listener);
	/**
	 * ファイル(への参照)を作成する
	 * 
	 * @param name filename
	 * @return ファイルオブジェクト
	 */
	public CobolFile createFile(String name);
	/**
	 * ファイル(への参照)を開放する
	 * 
	 * @param name filename
	 */
	public void destroyFile(String name);
	/**
	 * get option value
	 * 
	 * @param key key
	 * @return option value
	 */
	public String getACMOption(String key);
	/**
	 * 作成済みのファイル(への参照)を取得する
	 * 
	 * @param name filename
	 * @return ファイルオブジェクト
	 */
	public CobolFile getFile(String name);
	/**
	 * get max record length
	 * 
	 * @return length
	 */
	public int getMaxLength();
	/**
	 * セッションID
	 * 
	 * @return セッションID
	 */
	public String getSessionId();
	/**
	 * イベントリスナを削除する
	 * 
	 * @param listener イベントリスナ
	 */
	public void removeACMSessionEventListener(ACMSessionEventListener listener);
	/**
	 * set option value
	 * 
	 * @param key key
	 * @param value value
	 */
	public void setACMOption(String key, String value);
	/**
	 * set max record length
	 * 
	 * @param length length
	 */
	public void setMaxLength(int length);
}