import k_kim_mg.sa4cob2db.DefaultCobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.ACMNetSession;
/**
 * �ƥ�����
 * @author ���줪��
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
