package k_kim_mg.sa4cob2db.sql.xml;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;

import org.w3c.dom.Node;

/**
 * Process metadata in node
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface MetaDataNodeProcessor {
	/**
	 * Process metadata in node
	 * 
	 * @param node node
	 * @param meta metadata
	 */
	public void processOtherNode(Node node, CobolRecordMetaData meta);
}
