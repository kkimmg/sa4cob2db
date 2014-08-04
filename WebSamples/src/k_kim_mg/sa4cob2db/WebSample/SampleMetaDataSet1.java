package k_kim_mg.sa4cob2db.WebSample;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;

/**
 * Sample 
 * @author kkimmg@gmail.com
 */
class SampleMetaDataSet1 extends CobolRecordMetaDataSet {
	/**
	 * returns null
	 */
	protected CobolFile createCobolFile(CobolRecordMetaData meta) {
		return null;
	}

	/**
	 * returns record
	 * @param metaname metadata name
	 * @return record
	 */
	public CobolRecord createRecord (String metaname) {
		CobolRecordMetaData meta = getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}