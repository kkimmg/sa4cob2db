package k_kim_mg.sa4cob2db;

import java.io.Serializable;

import k_kim_mg.sa4cob2db.event.CobolFileEventListener;

/**
 * cobol file
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobolFile extends Serializable {
	/** アクセスモード */
	public static final int ACCESS_DYNAMIC = 1;
	/** アクセスモード */
	public static final int ACCESS_RANDOM = 2;
	/** アクセスモード */
	public static final int ACCESS_SEQUENTIAL = 0;
	/**
	 * IS EQUAL TO IS =
	 */
	public static final int IS_EQUAL_TO = 0;
	/**
	 * IS GREATER THAN IS >
	 */
	public static final int IS_GREATER_THAN = 1;
	/**
	 * IS GREATER THAN OR EQUAL TO IS >=
	 */
	public static final int IS_GREATER_THAN_OR_EQUAL_TO = 3;
	/**
	 * IS NOT LESS THAN IS NOT < IS >=
	 */
	public static final int IS_NOT_LESS_THAN = 3;
	/** オープンモード */
	public static final int MODE_EXTEND = 3;
	/** オープンモード */
	public static final int MODE_INPUT = 0;
	/** オープンモード */
	public static final int MODE_INPUT_OUTPUT = 2;
	/** オープンモード */
	public static final int MODE_OUTPUT = 1;

	/**
	 * CobolFileEventListenerを追加する
	 * 
	 * @param listener
	 *            追加するCobolFileEventListener
	 */
	public void addCobolFileEventListener(CobolFileEventListener listener);

	/**
	 * インデックスの追加
	 * 
	 * @param index
	 *            インデックス用の columnマッパ
	 * @param file
	 *            インデックスfile
	 */
	public void addIndex(CobolIndex index, CobolFile file);

	/**
	 * sessionの関連付け
	 * 
	 * @param session
	 *            関連付けるsession
	 */
	public void bindSession(ACMSession session);

	/**
	 * fileを閉じる
	 * 
	 * @return status
	 */
	public FileStatus close();

	/**
	 * recordの削除
	 * 
	 * @param record
	 *            削除キー
	 * @return filestatus
	 */
	public FileStatus delete(byte[] record);

	/**
	 * アクセスモード
	 * 
	 * @return アクセスモード
	 */
	public int getAccessMode();

	/**
	 * 現在の行
	 * 
	 * @return 現在の行
	 */
	public int getCurrentRow();

	/**
	 * このfileのmeta data
	 * 
	 * @return meta dataオブジェクト
	 */
	public CobolRecordMetaData getMetaData();

	/**
	 * オープンモード
	 * 
	 * @return オープンモード
	 */
	public int getOpenMode();

	/**
	 * fileに含まれるrecordの行数または現在までに読み込まれた行数
	 * 
	 * @return 行数
	 */
	public int getRowCount();

	/**
	 * 関連付けられたsession
	 * 
	 * @return session
	 */
	public ACMSession getSession();

	/**
	 * このfileは現在開かれているか？
	 * 
	 * @return true 開かれている<br>
	 *         false 閉じている
	 */
	public boolean isOpened();

	/**
	 * 位置づけ処理
	 * 
	 * @param record
	 *            キーを含むバイト配 column
	 * @return status
	 */
	public FileStatus move(byte[] record);

	/**
	 * 次のrecordへ移動する
	 * 
	 * @return status
	 */
	public FileStatus next();

	/**
	 * fileオープン
	 * 
	 * @param mode
	 *            モード オープンモード input/output/expant/input-output
	 * @param accessmode
	 *            アクセスモードまたはfile構成 sequencial/random/dynamic
	 * @return status
	 */
	public FileStatus open(int mode, int accessmode);

	/**
	 * 前のrecordへ移動する
	 * 
	 * @return status
	 */
	public FileStatus previous();

	/**
	 * 現在位置づいているrecordからバイトコードを作成する
	 * 
	 * @param record
	 *            読み込みrecord
	 * @return status
	 */
	public FileStatus read(byte[] record);

	/**
	 * CobolFileEventListenerを削除する
	 * 
	 * @param listener
	 *            削除するCobolFileEventListener
	 */
	public void removeCobolFileEventListener(CobolFileEventListener listener);

	/**
	 * 現在位置づいているrecordをバイトコードで上書きする
	 * 
	 * @param record
	 *            書き込みrecord
	 * @return status
	 */
	public FileStatus rewrite(byte[] record);

	/**
	 * 位置付けする
	 * 
	 * @param mode
	 *            モード(EQ GT など)
	 * @param record
	 *            キーを含むrecord
	 * @return status
	 */
	public FileStatus start(int mode, byte[] record);

	/**
	 * 位置付けする
	 * 
	 * @param mode
	 *            モード(EQ GT など)
	 * @param record
	 *            キーを含むrecord
	 * @param duplicates
	 *            キーが重複しているかどうか
	 * @return status
	 */
	public FileStatus start(int mode, byte[] record, boolean duplicates);

	/**
	 * インデックスによる検索
	 * 
	 * @param IndexName
	 *            インデックス名
	 * @param mode
	 *            スタートモード
	 * @param record
	 *            検索record
	 * @return status
	 * @throws CobolRecordException
	 *             エラー発生時
	 */
	public FileStatus start(String IndexName, int mode, byte[] record);

	/**
	 * すべてさ駆除する
	 */
	public void truncate();

	/**
	 * fileにrecordを追加する
	 * 
	 * @param record
	 *            書き込みrecord
	 * @return status
	 */
	public FileStatus write(byte[] record);
}