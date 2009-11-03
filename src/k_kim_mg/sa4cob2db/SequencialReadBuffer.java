package k_kim_mg.sa4cob2db;

/**
 * シーケンシャルファイルの読み込みバッファ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface SequencialReadBuffer {
	/**
	 * バッファ位置を変更する
	 * @return ファイルステータス
	 */
	public FileStatus nextBuffer ();
	/**
	 * バッファを読み込む<br/>
	 * @param record	レコード
	 * @return	ファイルステータス
	 */
	public FileStatus readBuffer (byte[] record);
	/**
	 * バッファリングの開始
	 */
	public void startBuffering ();
}
