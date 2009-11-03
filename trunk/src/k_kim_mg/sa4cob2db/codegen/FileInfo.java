package k_kim_mg.sa4cob2db.codegen;

/**
 * コード生成時のファイルの情報を保持するオブジェクト
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface FileInfo {
	/**
	 * アクセスモード
	 * @return	アクセスモード
	 */
	public int getAcessMode();
	/**
	 * 外部ファイル名
	 * @return	ファイル名（外部ファイル名？） <br/>
	 * アサインする（ファイルシステム上の）ファイル名
	 */
	public String getFileName();
	/**
	 * レコード名
	 * @return	レコード名？ <br/>
	 * FD句で01レベルの領域名
	 */
	public String getRecordName();
	/**
	 * 内部ファイル名
	 * @return	ファイル名（内部ファイル名？） <br/>
	 * SELECT句の次のトークン
	 */
	public String getSelectName();
	/**
	 * 入出力状態
	 * @return	入出力状態 <br/>
	 * 入出力状態を示す領域名(File Status [is] XXXXXX)
	 */
	public String getStatus();
}
