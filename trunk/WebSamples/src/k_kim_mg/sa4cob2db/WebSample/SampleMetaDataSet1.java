package k_kim_mg.sa4cob2db.WebSample;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;

/**
 * ���R�[�h�̃��^�f�[�^���擾���邽�߂����ɃT�u�N���X���`���Ă݂�
 * @author ���ꂨ��
 */
class SampleMetaDataSet1 extends CobolRecordMetaDataSet {
	/**
	 * �Ȃɂ��Ԃ��Ȃ�
	 */
	protected CobolFile createCobolFile(CobolRecordMetaData meta) {
		return null;
	}
	/**
	 * �z��̃��b�p��Ԃ�
	 * @param metaname	���^�f�[�^��
	 * @return �R�{���̃��R�[�h�`��
	 */
	public CobolRecord createRecord (String metaname) {
		CobolRecordMetaData meta = getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}