//package k_kim_mg.sa4cob2db.sample;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
public class JSampleJniCall1 {
	/** ライブラリ名 */
	public static final String ACM_SAMPLE_LIBRARY_NAME = "sampleJniCall";
	public static final String OPEN_COB_LIBRARY_NAME = "cob";
	static {
		/* ライブラリの事前ロード */
		try {
			System.loadLibrary(OPEN_COB_LIBRARY_NAME);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			System.loadLibrary(ACM_SAMPLE_LIBRARY_NAME);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/** ネイティブメソッドが成功した */
	public static final int JNI_RETURN_OK = 0;
	/** ネイティブメソッドが失敗した */
	public static final int JNI_RETURN_NG = -1;
	/**
	 * ネイティブメソッド コボルサブプログラムをコールするCルーチン
	 * @param progname	プログラム名
	 * @param head		ヘッダ(SPA?)
	 * @param body		ボディ(オンラインメッセージ?)
	 * @return ネイティブメソッドの成功/不成功
	 */
	public native int sampleJniCall1(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);
	/**
	 * ネイティブメソッド コボルサブプログラムをコールするCルーチン
	 * @param libname	ライブラリ名
	 * @param progname	プログラム名
	 * @param head		ヘッダ(SPA?)
	 * @param body		ボディ(オンラインメッセージ?)
	 * @return ネイティブメソッドの成功/不成功
	 */
	public native int sampleJniCall2(String libname, String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);
	/**
	 * ネイティブメソッドの直接のラッパー
	 * @param libname	ライブラリ名
	 * @param progname	プログラム名
	 * @param head		ヘッダ(SPA?)
	 * @param body		ボディ(オンラインメッセージ?)
	 * @return ネイティブメソッドの成功/不成功
	 */
	public int jniCallCobol1(String libname, String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall2(libname, progname, head, bodyIn, bodyOut);
	}
	/**
	 * ネイティブメソッドの直接のラッパー
	 * @param libname	ライブラリ名
	 * @param progname	プログラム名
	 * @param head		ヘッダ(SPA?)
	 * @param body		ボディ(オンラインメッセージ?)
	 * @return ネイティブメソッドの成功/不成功
	 */
	public int jniCallCobol1(String libname,String progname, CobolRecord head, CobolRecord bodyIn, CobolRecord bodyOut) throws CobolRecordException {
		byte[] hbytes = getRecordToBytes(head);
		byte[] ibytes = getRecordToBytes(bodyIn);
		byte[] obytes = getRecordToBytes(bodyOut);
		int ret = sampleJniCall2(libname, progname, hbytes, ibytes, obytes);
		head.setRecord(hbytes);
		bodyIn.setRecord(ibytes);
		bodyOut.setRecord(obytes);
		return ret;
	}
	/**
	 * ネイティブメソッドの直接のラッパー
	 * @param progname	プログラム名
	 * @param head		ヘッダ(SPA?)
	 * @param body		ボディ(オンラインメッセージ?)
	 * @return ネイティブメソッドの成功/不成功
	 */
	public int jniCallCobol1(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall1(progname, head, bodyIn, bodyOut);
	}
	/**
	 * ネイティブメソッドの直接のラッパー
	 * @param progname	プログラム名
	 * @param head		ヘッダ(SPA?)
	 * @param body		ボディ(オンラインメッセージ?)
	 * @return ネイティブメソッドの成功/不成功
	 */
	public int jniCallCobol1(String progname, CobolRecord head, CobolRecord bodyIn, CobolRecord bodyOut) throws CobolRecordException {
		byte[] hbytes = getRecordToBytes(head);
		byte[] ibytes = getRecordToBytes(bodyIn);
		byte[] obytes = getRecordToBytes(bodyOut);
		int ret = sampleJniCall1(progname, hbytes, ibytes, obytes);
		head.setRecord(hbytes);
		bodyIn.setRecord(ibytes);
		bodyOut.setRecord(obytes);
		return ret;
	}
	/**
	 * レコードからバイト配列を取り出す
	 * 
	 * @param record	レコード
	 * @return バイト配列
	 * @throws CobolRecordException	例外
	 */
	private byte[] getRecordToBytes(CobolRecord record) throws CobolRecordException {
		byte[] ret = null;
		CobolRecordMetaData meta = record.getMetaData();
		int size = meta.getRowSize();
		ret = new byte[size];
		record.getRecord(ret);
		return ret;
	}
}
