import k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.ACMNetSession;
/**
 * テスト用
 * @author おれおれ
 */
public class CustomCobolMetaDataTest extends DefaultCobolRecordMetaData {
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData#getCustomFileClassName()
	 */
	@Override
	public String getCustomFileClassName() {
		return "CustomCobolFileTest";
	}

	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData#getRowSize()
     */
    @Override
    public int getRowSize() {
	    return ACMNetSession.RECORD_LEN;
    }
}
