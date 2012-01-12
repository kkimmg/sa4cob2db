package k_kim_mg.sa4cob2db;

import java.util.List;

import k_kim_mg.sa4cob2db.event.CobolFileEventListener;

/**
 * メタデータ
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * @version 1.0 コボルファイルのレコードレイアウトに関する情報を格納する
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
	 * 列の追加
	 * 
	 * @param column
	 *            追加する列
	 */
	public void addColumn(CobolColumn column);

	/**
	 * キー列の追加
	 * 
	 * @param column
	 *            追加する列
	 */
	public void addKey(CobolColumn column);

	/**
	 * コピーの作成
	 * 
	 * @return コピー
	 */
	public CobolRecordMetaData createCopy();

	/**
	 * 列の位置を取得する
	 * 
	 * @param column
	 *            列
	 * @return 列の位置
	 * @throws CobolRecordException
	 *             列が見つからなかったとき
	 */
	public int findColumn(CobolColumn column) throws CobolRecordException;

	/**
	 * 名前から列の位置を取得する
	 * 
	 * @param name
	 *            列名
	 * @return 列の位置
	 * @throws CobolRecordException
	 *             列が見つからなかったとき
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
	 * このレコードが持っている別名の数
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
	 * このレコードに登録されている列
	 * 
	 * @param i
	 *            列インデックス
	 * @return 列
	 */
	public CobolColumn getColumn(int i);

	/**
	 * このレコードに登録されている列
	 * 
	 * @param name
	 *            列名
	 * @return 列
	 */
	public CobolColumn getColumn(String name) throws CobolRecordException;

	/**
	 * このレコードに登録されている列の数
	 * 
	 * @return このレコードに登録されている列の数
	 */
	public int getColumnCount();

	/**
	 * このメタデータは特別なクラスからインスタンスを作成する
	 * 
	 * @return クラス名、指定しない場合は""またはnull
	 */
	public String getCustomFileClassName();

	/**
	 * レコードのエンコード
	 */
	public String getEncode();

	/**
	 * 順ファイルの読み取りバッファの初期サイズ
	 * 
	 * @return 順ファイルの読み取りバッファの初期サイズ
	 */
	public int getInitialSequencialReadBufferSize();

	/**
	 * キー指定された列
	 * 
	 * @param i
	 *            指定された順番
	 * @return キー列
	 */
	public CobolColumn getKey(int i);

	/**
	 * キー指定された列の数を返す
	 * 
	 * @return キー指定された列の数
	 */
	public int getKeyCount();

	/**
	 * イベントリスナのクラスのリスト
	 * 
	 * @return リスト
	 */
	public List<Class<? extends CobolFileEventListener>> getListenerClasses();

	/**
	 * 順ファイルの読み取りバッファの初期サイズ
	 * 
	 * @return 順ファイルの読み取りバッファの初期サイズ
	 */
	public int getMaximumSequencialReadBufferSize();

	/**
	 * 順ファイルの読み取りバッファの最小サイズ
	 * 
	 * @return 順ファイルの読み取りバッファの最小サイズ
	 */
	public int getMinimumSequencialReadBufferSize();

	/**
	 * RecordName
	 * 
	 * @return RecordName
	 */
	public String getName();

	/**
	 * このレコードの1レコードあたりのバイト数を返します
	 * 
	 * @return このレコードの1レコードあたりのバイト数
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
	 * 列の削除
	 * 
	 * @param column
	 *            削除する列
	 */
	public void removeColumn(CobolColumn column);

	/**
	 * 列の削除
	 * 
	 * @param index
	 *            削除する列インデックス
	 */
	public void removeColumn(int index);

	/**
	 * キー列の削除
	 * 
	 * @param column
	 *            削除するキー列
	 */
	public void removeKey(CobolColumn column);

	/**
	 * キー列の削除
	 * 
	 * @param index
	 *            削除するキー列インデックス
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
	 * 順ファイルの読み取りバッファの初期サイズ
	 * 
	 * @param value
	 *            順ファイルの読み取りバッファの初期サイズ
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
	 * 順ファイルの読み取りバッファの最大サイズ
	 * 
	 * @param value
	 *            順ファイルの読み取りバッファの最大サイズ
	 */
	public void setMaximumSequencialReadBufferSize(int value);

	/**
	 * 順ファイルの読み取りバッファの最小サイズ
	 * 
	 * @param value
	 *            順ファイルの読み取りバッファの最小サイズ
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
