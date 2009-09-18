package k_kim_mg.sa4cob2db.sql.xml;
import java.util.Properties;

import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;

import org.w3c.dom.Node;
/**
 * その他のノードを処理する(メタデータセット全体)
 * @author おれおれ
 */
public interface MetaDataSetNodeProcessor {
	/**
	 * その他のノードを処理する
	 * @param node ノード
	 * @param meta メタデータセット
	 * @param properties プロパティ
	 */
	public void processOtherNode(Node node, CobolRecordMetaDataSet meta, Properties properties);
}
