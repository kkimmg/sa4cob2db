package k_kim_mg.sa4cob2db.sql;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
/**
 * �ǡ����١������������˴ؤ���SQLʸ���󶡤��뵡ǽ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface SQLCobolRecordMetaData extends CobolRecordMetaData {
	/**
	 * Selectʸ
	 * @return SELECT Column... From Table Where Column = value .... OrderBy
	 *         .... ������Selectʸ
	 */
	public String getSelectStatement();
	/**
	 * Selectʸ
	 * @param string SELECT Column... From Table Where Column = value ....
	 *            OrderBy .... ������Selectʸ
	 */
	public void setSelectStatement(String string);
	/**
	 * "���٤ƺ��"���ơ��ȥ��Ȥμ���
	 * @return	"���٤ƺ��"���ơ��ȥ���
	 */
	public String getTruncateStatement();
	/**
	 * "���٤ƺ��"���ơ��ȥ��Ȥ򥻥åȤ���
	 * @param statement	"���٤ƺ��"���ơ��ȥ���
	 */
	public void setTruncateStatement(String statement);
}
