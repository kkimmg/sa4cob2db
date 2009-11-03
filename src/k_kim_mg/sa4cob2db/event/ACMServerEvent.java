package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * サーバーで何か起こったみたいだ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMServerEvent extends EventObject {
	/**
	 * デフォルトのバージョンID
	 */
	private static final long serialVersionUID = 1L;
	/** イベントの発生源 */
	private transient SQLNetServer server;
	/**
	 * コンストラクタ
	 * @param server	イベントの発生源(source)
	 */
	public ACMServerEvent (SQLNetServer server) {
		super(server);
		this.server = server;
	}
	/**
	 * サーバー
	 * @return サーバー・イベントの発生源
	 */
	public SQLNetServer getServer() {
		return server;
	}
}
