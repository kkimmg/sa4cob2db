package k_kim_mg.sa4cob2db;
import java.util.Hashtable;
import java.util.Map;
/**
 * デフォルトのインデックス
 * @author kenji
 */
public class DefaultCobolIndex implements CobolIndex {
	private String fileName;
	private String indexKeyName;
	private Map<CobolColumn, CobolColumn> fileKey2IndexColumn = new Hashtable<CobolColumn, CobolColumn>();
	private Map<CobolColumn, CobolColumn> indexKey2FileColumn = new Hashtable<CobolColumn, CobolColumn>();
	private boolean duplicates;
	@Override
	public Map<CobolColumn, CobolColumn> getFileKey2IndexColumn() {
		return fileKey2IndexColumn;
	}
	@Override
	public String getFileName() {
		return fileName;
	}
	@Override
	public Map<CobolColumn, CobolColumn> getIndexKey2FileColumn() {
		return indexKey2FileColumn;
	}
	@Override
	public String getIndexKeyName() {
		return indexKeyName;
	}
	/**
	 * ファイル名<br>
	 * インデックスに相当するCobolFileのファイル名
	 * @param fileName インデックスのファイル名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * インデックスキー名<br>
	 * start命令でどのインデックスを使用するか識別するためのインデックス名
	 * @param indexKeyName インデックスキー名
	 */
	public void setIndexKeyName(String indexKeyName) {
		this.indexKeyName = indexKeyName;
	}
	@Override
	public boolean isDuplicates() {
		return duplicates;
	}
	/**
	 * このインデックスは重複するか
	 * @param duplicates インデックスが重複するかどうか
	 */
	public void setDuplicates(boolean duplicates) {
		this.duplicates = duplicates;
	}
}
