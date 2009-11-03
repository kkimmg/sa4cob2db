package k_kim_mg.sa4cob2db.sql.xml;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;

import org.w3c.dom.Node;
/**
 * その他のノードを処理する(メタデータ内)
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface MetaDataNodeProcessor {
	/**
	 * その他のノードを処理する
	 * @param node	ノード
	 * @param meta	メタデータ
	 */
	public void processOtherNode(Node node, CobolRecordMetaData meta);
}
