package k_kim_mg.sa4cob2db.sql.xml;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;

import org.w3c.dom.Node;
/**
 * ����¾�ΥΡ��ɤ��������(�᥿�ǡ�����)
 * @author ���줪��
 */
public interface MetaDataNodeProcessor {
	/**
	 * ����¾�ΥΡ��ɤ��������
	 * @param node	�Ρ���
	 * @param meta	�᥿�ǡ���
	 */
	public void processOtherNode(Node node, CobolRecordMetaData meta);
}
