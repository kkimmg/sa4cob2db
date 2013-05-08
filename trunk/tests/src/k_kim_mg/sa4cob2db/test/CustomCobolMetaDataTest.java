package k_kim_mg.sa4cob2db.test;
import k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.ACMNetSession;
/**
 * test
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CustomCobolMetaDataTest extends DefaultCobolRecordMetaData {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData#getCustomFileClassName()
	 */
	@Override
	public String getCustomFileClassName() {
		return "CustomCobolFileTest";
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData#getRowSize()
	 */
	@Override
	public int getRowSize() {
		return ACMNetSession.INITIAL_RECORD_LEN;
	}
}
