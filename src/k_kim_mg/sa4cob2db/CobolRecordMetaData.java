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
	 * 順fileの読み取りバッファの初期サイズ
	 * 
	 * @return 順fileの読み取りバッファの初期サイズ
	 */
	public int getMaximumSequencialReadBufferSize();
	/**
	 * 順fileの読み取りバッファの最小サイズ
	 * 
	 * @return 順fileの読み取りバッファの最小サイズ
	 */
	public int getMinimumSequencialReadBufferSize();
	/**
	 * RecordName
	 * 
	 * @return RecordName
	 */
	public String getName();
	/**
	 * このrecordの1recordあたりのバイト数を返します
	 * 
	 * @return このrecordの1recordあたりのバイト数
	 */
	public int getRowSize();
	/**
	 * key 比較を値で行うかどうか
	 * 
	 * @return true 行う<br/>
	 *         false 行わない
	 */
	public boolean isKeyByValue();
	/**
	 * 別 name の削除
	 * 
	 * @param alias 削除する別 name
	 */
	public void removeAlias(String alias);
	/**
	 * columnの削除
	 * 
	 * @param column 削除する column
	 */
	public void removeColumn(CobolColumn column);
	/**
	 * columnの削除
	 * 
	 * @param index 削除する column index
	 */
	public void removeColumn(int index);
	/**
	 * key columnの削除
	 * 
	 * @param column 削除する key column
	 */
	public void removeKey(CobolColumn column);
	/**
	 * key columnの削除
	 * 
	 * @param index 削除する key column index
	 */
	public void removeKey(int index);
	/**
	 * エンコーディング name の設定
	 * 
	 * @param string エンコーディング name
	 */
	public void setEncode(String string);
	/**
	 * 順fileの読み取りバッファの初期サイズ
	 * 
	 * @param value 順fileの読み取りバッファの初期サイズ
	 */
	public void setInitialSequencialReadBufferSize(int value);
	/**
	 * key 読み込みを値で行うかどうか
	 * 
	 * @param keyByValue key 読み込みを値で行うかどうか
	 */
	public void setKeyByValue(boolean keyByValue);
	/**
	 * 順fileの読み取りバッファの最大サイズ
	 * 
	 * @param value 順fileの読み取りバッファの最大サイズ
	 */
	public void setMaximumSequencialReadBufferSize(int value);
	/**
	 * 順fileの読み取りバッファの最小サイズ
	 * 
	 * @param value 順fileの読み取りバッファの最小サイズ
	 */
	public void setMinimumSequencialReadBufferSize(int value);
	/**
	 * RecordName
	 * 
	 * @param name RecordName
	 */
	public void setName(String name);
}
