package k_kim_mg.sa4cob2db.sql.xml;
import java.util.Properties;

import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;

import org.w3c.dom.Node;
/**
 * ����¾�ΥΡ��ɤ��������(�᥿�ǡ������å�����)
 * @author ���줪��
 */
public interface MetaDataSetNodeProcessor {
	/**
	 * ����¾�ΥΡ��ɤ��������
	 * @param node �Ρ���
	 * @param meta �᥿�ǡ������å�
	 * @param properties �ץ�ѥƥ�
	 */
	public void processOtherNode(Node node, CobolRecordMetaDataSet meta, Properties properties);
}
