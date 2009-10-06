package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolFile;
/**
 * ACMSessionに関するイベント
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSessionEvent extends EventObject {
	/**
	 * 勝手にできる
	 */
	private static final long serialVersionUID = 1L;
	private ACMSession session;
	private CobolFile file;
	/**
	 * コンストラクタ
	 * @param source	セッション
	 * @param file		ファイル
	 */
	public ACMSessionEvent(ACMSession source, CobolFile file) {
		super(source);
		this.session = source;
		this.file = file;
	}
	/**
	 * イベント発生源のファイル
	 * @return イベント発生源のファイル
	 */
	public CobolFile getFile() {
		return file;
	}
	/**
	 * イベント発生源のセッション
	 * @return イベント発生源のセッション(sourceの代わり)
	 */
	public ACMSession getSession() {
		return session;
	}
}
