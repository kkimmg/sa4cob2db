package k_kim_mg.sa4cob2db;

import java.util.Map;

/**
 * インデックス処理用の列マッパ
 */
public interface CobolIndex {
	/**
	 * インデックスキー列→ファイル列
	 * @return	マップ
	 */
	public Map<CobolColumn, CobolColumn> getIndexKey2FileColumn ();
	/**
	 * ファイルキー列→インデックスファイルの列
	 * @return	マップ
	 */
	public Map<CobolColumn, CobolColumn> getFileKey2IndexColumn ();
	/**
	 * インデックスファイルの取得
	 * @return	インデックスファイル
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