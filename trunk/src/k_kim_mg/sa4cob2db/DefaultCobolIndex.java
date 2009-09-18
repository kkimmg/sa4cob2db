package k_kim_mg.sa4cob2db;
import java.util.Hashtable;
import java.util.Map;
/**
 * �ǥե���ȤΥ���ǥå���
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
	 * �ե�����̾<br>
	 * ����ǥå�������������CobolFile�Υե�����̾
	 * @param fileName ����ǥå����Υե�����̾
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * ����ǥå�������̾<br>
	 * start̿��ǤɤΥ���ǥå�������Ѥ��뤫���̤��뤿��Υ���ǥå���̾
	 * @param indexKeyName ����ǥå�������̾
	 */
	public void setIndexKeyName(String indexKeyName) {
		this.indexKeyName = indexKeyName;
	}
	@Override
	public boolean isDuplicates() {
		return duplicates;
	}
	/**
	 * ���Υ���ǥå����Ͻ�ʣ���뤫
	 * @param duplicates ����ǥå�������ʣ���뤫�ɤ���
	 */
	public void setDuplicates(boolean duplicates) {
		this.duplicates = duplicates;
	}
}
