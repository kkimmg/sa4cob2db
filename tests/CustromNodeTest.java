import java.util.Properties;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
import k_kim_mg.sa4cob2db.sql.xml.MetaDataNodeProcessor;
import k_kim_mg.sa4cob2db.sql.xml.MetaDataSetNodeProcessor;

import org.w3c.dom.Node;

/**
 * カスタムノードの処理のテスト
 * @author <a mailto="k_kim_mg@mvh.biglobe.ne.jp">Kenji Kimura</a>
 */
public class CustromNodeTest implements MetaDataNodeProcessor, MetaDataSetNodeProcessor {
	@Override
	public void processOtherNode(Node node, CobolRecordMetaData meta) {
		SQLNetServer.logger.warning("Processing...." + node.getNodeName());
	}
	@Override
	public void processOtherNode(Node node, CobolRecordMetaDataSet meta, Properties properties) {
		SQLNetServer.logger.warning("Processing...." + node.getNodeName());
	}
}
