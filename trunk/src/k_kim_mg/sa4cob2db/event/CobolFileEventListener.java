package k_kim_mg.sa4cob2db.event;
import java.util.EventListener;
/**
 * コボルファイルのアクセスで発生したイベントを処理する機能
 * @author おれおれ
 */
public interface CobolFileEventListener extends EventListener {
	/**
	 * CLOSEの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postClose(CobolFileEvent e);
	/**
	 * DELETEの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postDelete(CobolFileEvent e);
	/**
	 * READの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postMove(CobolFileEvent e);
	/**
	 * NEXTの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postNext(CobolFileEvent e);
	/**
	 * OPENの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postOpen(CobolFileEvent e);
	/**
	 * PREVIOUSの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postPrevious(CobolFileEvent e);
	/**
	 * READの後
	 * @param e CobolFileで発生したイベント
	 * @param record 読み込んだレコード
	 */
	public void postRead(CobolFileEvent e, byte[] record);
	/**
	 * REWRITEの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postRewrite(CobolFileEvent e);
	/**
	 * STARTの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postStart(CobolFileEvent e);
	/**
	 * STARTの後
	 * @param e CobolFileで発生したイベント
	 * @param indexname インデックスの名称
	 */
	public void postStart(CobolFileEvent e, String indexname);
	/**
	 * WRITEの後
	 * @param e CobolFileで発生したイベント
	 */
	public void postWrite(CobolFileEvent e);
	/**
	 * CLOSEの前
	 * @param e CobolFileで発生したイベント
	 */
	public void preClose(CobolFileEvent e);
	/**
	 * DELETEの前
	 * @param e CobolFileで発生したイベント
	 */
	public void preDelete(CobolFileEvent e);
	/**
	 * READの前
	 * @param e CobolFileで発生したイベント
	 * @param record キーレコード
	 */
	public void preMove(CobolFileEvent e, byte[] record);
	/**
	 * NEXTの前
	 * @param e CobolFileで発生したイベント
	 */
	public void preNext(CobolFileEvent e);
	/**
	 * OPENの前
	 * @param e CobolFileで発生したイベント
	 */
	public void preOpen(CobolFileEvent e);
	/**
	 * PREVIOUSの前
	 * @param e CobolFileで発生したイベント
	 */
	public void prePrevious(CobolFileEvent e);
	/**
	 * READの前
	 * @param e CobolFileで発生したイベント
	 */
	public void preRead(CobolFileEvent e);
	/**
	 * REWRITEの前
	 * @param e CobolFileで発生したイベント
	 * @param record 書き込みレコード
	 */
	public void preRewrite(CobolFileEvent e, byte[] record);
	/**
	 * STARTの前
	 * @param e CobolFileで発生したイベント
	 * @param record キーレコード
	 */
	public void preStart(CobolFileEvent e, byte[] record);
	/**
	 * STARTの前
	 * @param e CobolFileで発生したイベント
	 * @param indexname インデックスの名称
	 * @param record キーレコード
	 */
	public void preStart(CobolFileEvent e, String indexname, byte[] record);
	/**
	 * WRITEの前
	 * @param e CobolFileで発生したイベント
	 * @param record 書き込みレコード
	 */
	public void preWrite(CobolFileEvent e, byte[] record);
}
