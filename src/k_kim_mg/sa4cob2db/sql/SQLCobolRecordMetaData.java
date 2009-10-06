package k_kim_mg.sa4cob2db.sql;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
/**
 * データベースアクセスに関するSQL文を提供する機能
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface SQLCobolRecordMetaData extends CobolRecordMetaData {
	/**
	 * Select文
	 * @return SELECT Column... From Table Where Column = value .... OrderBy
	 *         .... 形式のSelect文
	 */
	public String getSelectStatement();
	/**
	 * Select文
	 * @param string SELECT Column... From Table Where Column = value ....
	 *            OrderBy .... 形式のSelect文
	 */
	public void setSelectStatement(String string);
	/**
	 * "すべて削除"ステートメントの取得
	 * @return	"すべて削除"ステートメント
	 */
	public String getTruncateStatement();
	/**
	 * "すべて削除"ステートメントをセットする
	 * @param statement	"すべて削除"ステートメント
	 */
	public void setTruncateStatement(String statement);
}
