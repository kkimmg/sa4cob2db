package k_kim_mg.sa4cob2db;
import java.util.List;
import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
/**
 * meta data
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobolRecordMetaData {
	/**
	 * add alias
	 * 
	 * @param alias alias
	 */
	public void addAlias(String alias);
	/**
	 * add column
	 * 
	 * @param column column
	 */
	public void addColumn(CobolColumn column);
	/**
	 * add key column
	 * 
	 * @param column key column
	 */
	public void addKey(CobolColumn column);
	/**
	 * create copy
	 * 
	 * @return copy
	 */
	public CobolRecordMetaData createCopy();
	/**
	 * get column index
	 * 
	 * @param column column
	 * @return index
	 * @throws CobolRecordException throw when column not found
	 */
	public int findColumn(CobolColumn column) throws CobolRecordException;
	/**
	 * get column index by name
	 * 
	 * @param name column name
	 * @return column location
	 * @throws CobolRecordException throw when column not found
	 */
	public int findColumn(String name) throws CobolRecordException;
	/**
	 * get alias
	 * 
	 * @param i i'th alias name
	 * @return alias
	 */
	public String getAlias(int i);
	/**
	 * get alias count
	 * 
	 * @return alias count
	 */
	public int getAliasCount();
	/**
	 * get index list
	 * 
	 * @return index list
	 */
	public List<CobolIndex> getCobolIndexes();
	/**
	 * get column
	 * 
	 * @param i column index
	 * @return column
	 */
	public CobolColumn getColumn(int i);
	/**
	 * get column
	 * 
	 * @param name column name
	 * @return column
	 */
	public CobolColumn getColumn(String name) throws CobolRecordException;
	/**
	 * get column count
	 * 
	 * @return column count
	 */
	public int getColumnCount();
	/**
	 * class name to use when create file object by original class
	 * 
	 * @return class name or "" or null
	 */
	public String getCustomFileClassName();
	/**
	 * get encoding string
	 */
	public String getEncode();
	/**
	 * get initial buffer size
	 * 
	 * @return initial buffer size
	 */
	public int getInitialSequencialReadBufferSize();
	/**
	 * get key column
	 * 
	 * @param i i'th key
	 * @return key column
	 */
	public CobolColumn getKey(int i);
	/**
	 * get key column count
	 * 
	 * @return key column count
	 */
	public int getKeyCount();
	/**
	 * event listeners classes list
	 * 
	 * @return list
	 */
	public List<Class<? extends CobolFileEventListener>> getListenerClasses();
	/**
	 * get maximum size of buffer
	 * 
	 * stop buffering when buffered row count reached this value
	 * 
	 * @return size(row count)
	 */
	public int getMaximumSequencialReadBufferSize();
	/**
	 * get minimum size of buffer
	 * 
	 * stop read from buffer until buffered row count reach this value
	 * 
	 * @return size (row count)
	 */
	public int getMinimumSequencialReadBufferSize();
	/**
	 * RecordName
	 * 
	 * @return RecordName
	 */
	public String getName();
	/**
	 * row size/record length
	 * 
	 * @return byte length
	 */
	public int getRowSize();
	/**
	 * compare key method
	 * 
	 * @return true by value<br>
	 *         false by each byte
	 */
	public boolean isKeyByValue();
	/**
	 * remove alias
	 * 
	 * @param alias alias
	 */
	public void removeAlias(String alias);
	/**
	 * remove column
	 * 
	 * @param column column object
	 */
	public void removeColumn(CobolColumn column);
	/**
	 * remove column
	 * 
	 * @param index column index
	 */
	public void removeColumn(int index);
	/**
	 * remove key column
	 * 
	 * @param column column object
	 */
	public void removeKey(CobolColumn column);
	/**
	 * remove key column
	 * 
	 * @param index column index
	 */
	public void removeKey(int index);
	/**
	 * set encoding
	 * 
	 * @param string encoding name
	 */
	public void setEncode(String string);
	/**
	 * set initial buffer size
	 * 
	 * @param value buffer row count
	 */
	public void setInitialSequencialReadBufferSize(int value);
	/**
	 * compare method
	 * 
	 * @param keyByValue true by value<br>
	 *            false by each bytes
	 */
	public void setKeyByValue(boolean keyByValue);
	/**
	 * get maximum size of buffer
	 * 
	 * stop buffering when buffered row count reached this value
	 * 
	 * @param value size(row count)
	 */
	public void setMaximumSequencialReadBufferSize(int value);
	/**
	 * get minimum size of buffer
	 * 
	 * stop read from buffer until buffered row count reach this value
	 * 
	 * @param value size(row count)
	 */
	public void setMinimumSequencialReadBufferSize(int value);
	/**
	 * record name
	 * 
	 * @param name record name
	 */
	public void setName(String name);
	/**
	 * if this value is true, do close and open then No data found.
	 * @return true/false
	 */
	public boolean isReOpenWhenNoDataFound();
	/**
	 * set ReOpenWhenNoDataFound
	 * @param reOpenWhenNoDataFound if this value is true, do close and open then No data found.
	 */
	public void setReOpenWhenNoDataFound(boolean reOpenWhenNoDataFound);
}
