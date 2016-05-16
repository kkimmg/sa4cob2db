package k_kim_mg.sa4cob2db.test;

import java.util.Properties;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
import k_kim_mg.sa4cob2db.sql.xml.MetaDataNodeProcessor;
import k_kim_mg.sa4cob2db.sql.xml.MetaDataSetNodeProcessor;

import org.w3c.dom.Node;

/**
 * custom node test
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CustromNodeTest implements MetaDataNodeProcessor, MetaDataSetNodeProcessor {
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.sql.xml.MetaDataNodeProcessor#processOtherNode(org.w3c.dom.Node, k_kim_mg.sa4cob2db.CobolRecordMetaData)
	 */
	@Override
	public void processOtherNode(Node node, CobolRecordMetaData meta) {
		SQLNetServer.logger.warning("Processing...." + node.getNodeName());
	}

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.sql.xml.MetaDataSetNodeProcessor#processOtherNode(org.w3c.dom.Node, k_kim_mg.sa4cob2db.CobolRecordMetaDataSet, java.util.Properties)
	 */
	@Override
	public void processOtherNode(Node node, CobolRecordMetaDataSet meta, Properties properties) {
		SQLNetServer.logger.warning("Processing...." + node.getNodeName());
	}
}
