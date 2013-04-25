package k_kim_mg.sa4cob2db;
import java.util.Map;
/**
 * index
 */
public interface CobolIndex {
	/**
	 * index key column -> main file column
	 * 
	 * @return map
	 */
	public Map<CobolColumn, CobolColumn> getIndexKey2FileColumn();
	/**
	 * main file key column -> index file column
	 * 
	 * @return map
	 */
	public Map<CobolColumn, CobolColumn> getFileKey2IndexColumn();
	/**
	 * get index file
	 * 
	 * @return index file
	 */
	public String getFileName();
	/**
	 * get index key name
	 * 
	 * @return key name (relative key)
	 */
	public String getIndexKeyName();
	/**
	 * with duplicates?
	 * 
	 * @return true/false
	 */
	public boolean isDuplicates();
}