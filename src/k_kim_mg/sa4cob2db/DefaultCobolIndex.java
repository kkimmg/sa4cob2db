package k_kim_mg.sa4cob2db;

import java.util.Hashtable;
import java.util.Map;

/**
 * default index
 * 
 * @author kenji
 */
public class DefaultCobolIndex implements CobolIndex {
	private String fileName;
	private String indexKeyName;
	private Map<CobolColumn, CobolColumn> fileKey2IndexColumn = new Hashtable<CobolColumn, CobolColumn>();
	private Map<CobolColumn, CobolColumn> indexKey2FileColumn = new Hashtable<CobolColumn, CobolColumn>();
	private boolean duplicates;

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolIndex#getFileKey2IndexColumn()
	 */
	@Override
	public Map<CobolColumn, CobolColumn> getFileKey2IndexColumn() {
		return fileKey2IndexColumn;
	}

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolIndex#getFileName()
	 */
	@Override
	public String getFileName() {
		return fileName;
	}

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolIndex#getIndexKey2FileColumn()
	 */
	@Override
	public Map<CobolColumn, CobolColumn> getIndexKey2FileColumn() {
		return indexKey2FileColumn;
	}

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolIndex#getIndexKeyName()
	 */
	@Override
	public String getIndexKeyName() {
		return indexKeyName;
	}

	/**
	 * file name<br>
	 * index file name
	 * 
	 * @param fileName file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * set index key name <br>
	 * 
	 * @param indexKeyName index key name
	 */
	public void setIndexKeyName(String indexKeyName) {
		this.indexKeyName = indexKeyName;
	}

	@Override
	public boolean isDuplicates() {
		return duplicates;
	}

	/**
	 * set duplicated index
	 * 
	 * @param duplicates index true yes<br>
	 *            false no
	 */
	public void setDuplicates(boolean duplicates) {
		this.duplicates = duplicates;
	}
}
