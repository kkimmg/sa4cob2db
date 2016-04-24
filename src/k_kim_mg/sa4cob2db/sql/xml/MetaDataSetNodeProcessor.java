package k_kim_mg.sa4cob2db.sql.xml;

import java.util.Properties;

import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;

import org.w3c.dom.Node;

/**
 * Process metadata set in node
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface MetaDataSetNodeProcessor {
	/**
	 * Process metadata set in node
	 * 
	 * @param node xml node
	 * @param meta metadata set
	 * @param properties properties
	 */
	public void processOtherNode(Node node, CobolRecordMetaDataSet meta, Properties properties);
}
