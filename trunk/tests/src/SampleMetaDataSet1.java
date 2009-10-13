//package k_kim_mg.sa4cob2db.sample;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;

/**
 * �쥳���ɤΥ᥿�ǡ�����������뤿������˥��֥��饹��������Ƥߤ�
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
class SampleMetaDataSet1 extends CobolRecordMetaDataSet {
	/**
	 * �ʤˤ��֤��ʤ�
	 */
	protected CobolFile createCobolFile(CobolRecordMetaData meta) {
		return null;
	}
	/**
	 * ����Υ�åѤ��֤�
	 * @param metaname	�᥿�ǡ���̾
	 * @return ���ܥ�Υ쥳���ɷ���
	 */
	public CobolRecord createRecord (String metaname) {
		CobolRecordMetaData meta = getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}