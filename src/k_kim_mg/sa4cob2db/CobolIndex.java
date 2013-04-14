package k_kim_mg.sa4cob2db;

import java.util.Map;

/**
 * インデックス処理用の columnマッパ
 */
public interface CobolIndex {
	/**
	 * インデックスキー column→file column
	 * @return	マップ
	 */
	public Map<CobolColumn, CobolColumn> getIndexKey2FileColumn ();
	/**
	 * fileキー column→インデックスfileの column
	 * @return	マップ
	 */
	public Map<CobolColumn, CobolColumn> getFileKey2IndexColumn ();
	/**
	 * インデックスfileの取得
	 * @return	インデックスfile
	 */
	public String getFileName ();
	/**
	 * Start命令時に使用するインデックスを特定するためのキー名
	 * @return	キー名(relative key)
	 */
	public String getIndexKeyName ();
	/**
	 * このインデックスは重複するか
	 * @return	重複するかどうか
	 */
	public boolean isDuplicates ();
}