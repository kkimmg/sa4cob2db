package k_kim_mg.sa4cob2db;

import java.util.List;

import k_kim_mg.sa4cob2db.event.CobolFileEventListener;

/**
 * meta data
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * @version 1.0 cobol fileのrecordレイアウトに関する情報を格納する
 */
public interface CobolRecordMetaData {
	/**
	 * 別名の追加
	 * 
	 * @param alias
	 *            追加する別名
	 */
	public void addAlias(String alias);

	/**
	 *  columnの追加
	 * 
	 * @param column
	 *            追加する column
	 */
	public void addColumn(CobolColumn column);

	/**
	 * キー columnの追加
	 * 
	 * @param column
	 *            追加する column
	 */
	public void addKey(CobolColumn column);

	/**
	 * コピーの作成
	 * 
	 * @return コピー
	 */
	public CobolRecordMetaData createCopy();

	/**
	 *  columnの位置を取得する
	 * 
	 * @param column
	 *             column
	 * @return  columnの位置
	 * @throws CobolRecordException
	 *              columnが見つからなかったとき
	 */
	public int findColumn(CobolColumn column) throws CobolRecordException;

	/**
	 * 名前から columnの位置を取得する
	 * 
	 * @param name
	 *             column名
	 * @return  columnの位置
	 * @throws CobolRecordException
	 *              columnが見つからなかったとき
	 */
	public int findColumn(String name) throws CobolRecordException;

	/**
	 * 別名の取得
	 * 
	 * @param i
	 *            i番目の別名
	 * @return 別名
	 */
	public String getAlias(int i);

	/**
	 * このrecordが持っている別名の数
	 * 
	 * @return 別名の数
	 */
	public int getAliasCount();

	/**
	 * インデックス情報の一覧
	 * 
	 * @return インデックス情報のリスト
	 */
	public List<CobolIndex> getCobolIndexes();

	/**
	 * このrecordに登録されている column
	 * 
	 * @param i
	 *             columnインデックス
	 * @return  column
	 */
	public CobolColumn getColumn(int i);

	/**
	 * このrecordに登録されている column
	 * 
	 * @param name
	 *             column名
	 * @return  column
	 */
	public CobolColumn getColumn(String name) throws CobolRecordException;

	/**
	 * このrecordに登録されている columnの数
	 * 
	 * @return このrecordに登録されている columnの数
	 */
	public int getColumnCount();

	/**
	 * このmeta dataは特別なクラスからインスタンスを作成する
	 * 
	 * @return クラス名、指定しない場合は""またはnull
	 */
	public String getCustomFileClassName();

	/**
	 * recordのエンコード
	 */
	public String getEncode();

	/**
	 * 順fileの読み取りバッファの初期サイズ
	 * 
	 * @return 順fileの読み取りバッファの初期サイズ
	 */
	public int getInitialSequencialReadBufferSize();

	/**
	 * キー指定された column
	 * 
	 * @param i
	 *            指定された順番
	 * @return キー column
	 */
	public CobolColumn getKey(int i);

	/**
	 * キー指定された columnの数を返す
	 * 
	 * @return キー指定された columnの数
	 */
	public int getKeyCount();

	/**
	 * event listenerのクラスのリスト
	 * 
	 * @return リスト
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
	 * キー比較を値で行うかどうか
	 * 
	 * @return true 行う<br/>
	 *         false 行わない
	 */
	public boolean isKeyByValue();

	/**
	 * 別名の削除
	 * 
	 * @param alias
	 *            削除する別名
	 */
	public void removeAlias(String alias);

	/**
	 *  columnの削除
	 * 
	 * @param column
	 *            削除する column
	 */
	public void removeColumn(CobolColumn column);

	/**
	 *  columnの削除
	 * 
	 * @param index
	 *            削除する columnインデックス
	 */
	public void removeColumn(int index);

	/**
	 * キー columnの削除
	 * 
	 * @param column
	 *            削除するキー column
	 */
	public void removeKey(CobolColumn column);

	/**
	 * キー columnの削除
	 * 
	 * @param index
	 *            削除するキー columnインデックス
	 */
	public void removeKey(int index);

	/**
	 * エンコーディング名の設定
	 * 
	 * @param string
	 *            エンコーディング名
	 */
	public void setEncode(String string);

	/**
	 * 順fileの読み取りバッファの初期サイズ
	 * 
	 * @param value
	 *            順fileの読み取りバッファの初期サイズ
	 */
	public void setInitialSequencialReadBufferSize(int value);

	/**
	 * キー読み込みを値で行うかどうか
	 * 
	 * @param keyByValue
	 *            キー読み込みを値で行うかどうか
	 */
	public void setKeyByValue(boolean keyByValue);

	/**
	 * 順fileの読み取りバッファの最大サイズ
	 * 
	 * @param value
	 *            順fileの読み取りバッファの最大サイズ
	 */
	public void setMaximumSequencialReadBufferSize(int value);

	/**
	 * 順fileの読み取りバッファの最小サイズ
	 * 
	 * @param value
	 *            順fileの読み取りバッファの最小サイズ
	 */
	public void setMinimumSequencialReadBufferSize(int value);

	/**
	 * RecordName
	 * 
	 * @param name
	 *            RecordName
	 */
	public void setName(String name);
}
