//package k_kim_mg.sa4cob2db.sample;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;

/**
 * レコードのメタデータを取得するためだけにサブクラスを定義してみる
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
class SampleMetaDataSet1 extends CobolRecordMetaDataSet {
	/**
	 * なにも返さない
	 */
	protected CobolFile createCobolFile(CobolRecordMetaData meta) {
		return null;
	}
	/**
	 * 配列のラッパを返す
	 * @param metaname	メタデータ名
	 * @return コボルのレコード形式
	 */
	public CobolRecord createRecord (String metaname) {
		CobolRecordMetaData meta = getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}