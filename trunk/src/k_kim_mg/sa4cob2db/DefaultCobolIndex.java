package k_kim_mg.sa4cob2db;
import java.util.Hashtable;
import java.util.Map;
/**
 * default index 
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
	 * filename<br>
	 * index file name
	 * @param fileName  index のfilename
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 *  index  key  name <br>
	 * start命令でどの index を使用するか識別するための index  name 
	 * @param indexKeyName  index  key  name 
	 */
	public void setIndexKeyName(String indexKeyName) {
		this.indexKeyName = indexKeyName;
	}
	@Override
	public boolean isDuplicates() {
		return duplicates;
	}
	/**
	 * この index は重複するか
	 * @param duplicates  index が重複するかどうか
	 */
	public void setDuplicates(boolean duplicates) {
		this.duplicates = duplicates;
	}
}
