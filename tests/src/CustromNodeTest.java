import java.util.Properties;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
import k_kim_mg.sa4cob2db.sql.xml.MetaDataNodeProcessor;
import k_kim_mg.sa4cob2db.sql.xml.MetaDataSetNodeProcessor;

import org.w3c.dom.Node;

/**
 * 鐃緒申鐃緒申鐃緒申鐃緒申痢鐃緒申匹僚鐃緒申鐃塾テワ申鐃緒申
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
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
