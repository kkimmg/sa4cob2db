/**
 * 
 */
package k_kim_mg.sa4cob2db.WebSample;

import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NodeReaderPlus1 extends NodeReadLoader {
	/**
	 * �Z�b�g������e
	 */
	ServletSampleTool1 servletSampleTool1;
	/**
	 * �R���X�g���N�^
	 * @param servletSampleTool1	�Z�b�g������e
	 */
	public NodeReaderPlus1 (ServletSampleTool1 servletSampleTool1) {
		super();
		this.servletSampleTool1 = servletSampleTool1;
	}
	/* (�� Javadoc)
	 * @see k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader#createSomething(org.w3c.dom.Node, k_kim_mg.sa4cob2db.CobolRecordMetaDataSet)
	 */
	protected void createSomething(Node node, CobolRecordMetaDataSet meta) {
		if (node.getNodeName().equals("webinterface")) {
			NamedNodeMap map = node.getAttributes();
			// �C���^�[�t�F�[�X�錾
			WebInterface1 face1 = new WebInterface1();
			// �����̃Z�b�g
			Node att;
			att = map.getNamedItem("name");
			if (att != null) {
				face1.setName(att.getNodeValue());
			}
			att = map.getNamedItem("content");
			if (att != null) {
				face1.setContent(att.getNodeValue());
			}
			att = map.getNamedItem("subroutine");
			if (att != null) {
				face1.setSubroutine(att.getNodeValue());
			}
			att = map.getNamedItem("informat");
			if (att != null) {
				face1.setInFormat(att.getNodeValue());
			}
			att = map.getNamedItem("outformat");
			if (att != null) {
				face1.setOutFormat(att.getNodeValue());
			}
			servletSampleTool1.addWebInterface(face1);
		} else if (node.getNodeName().equals("MessageHeaderName")) {
			// �w�b�_�[���R�[�h��`
			String headername = getNodeString(node);
			servletSampleTool1.MSGHEADNAME = headername;
		}
	}
}