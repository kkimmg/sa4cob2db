package k_kim_mg.sa4cob2db;
import java.text.DecimalFormat;
/**
 * ファイルステータス
 * @author おれおれ
 */
public class FileStatus {
	/** 何も指定しないコード */
	public static final String NULL_CODE = "     ";
	/** 既にクローズされている */
	public static final String STATUS_ALREADY_CLOSED = "42";
	/** 既にオープンされている */
	public static final String STATUS_ALREADY_OPENED = "41";
	/** BOF-ファイルの始端 */
	public static final String STATUS_BOF = "91";
	/** DELETEできない（モード誤り） */
	public static final String STATUS_CANT_DELETE = "49";
	/** オープンできない（サポートされていないオープンモード） */
	public static final String STATUS_CANT_OPEN = "37";
	/** リードできない（モード誤り） */
	public static final String STATUS_CANT_READ = "47";
	/** REWRITEできない（モード誤り） */
	public static final String STATUS_CANT_REWRITE = "49";
	/** WRITEできない（モード誤り） */
	public static final String STATUS_CANT_WRITE = "48";
	/** 重複キー */
	public static final String STATUS_DUPLICATE_KEY = "22";
	/** サブキーが重複している */
	public static final String STATUS_DUPLICATE_SUBKEY = "02";
	/** EOF-ファイルの終端 */
	public static final String STATUS_EOF = "10";
	/** なんか知らんけど失敗した */
	public static final String STATUS_FAILURE = "99";
	/** ファイルオーバー */
	public static final String STATUS_FILEOVER = "24";
	/** INVALID KEY */
	public static final String STATUS_INVALID_KEY = "23";
	/** アサインされていない */
	public static final String STATUS_NOT_ASSIGND = "93";
	/** リードされていない（レコードが有効な位置にない） */
	public static final String STATUS_NOT_MOVED = "43";
	/** オープンしてない */
	public static final String STATUS_NOT_OPENED = "92";
	/** ファイルが存在しない */
	public static final String STATUS_NOTEXIST = "05";
	/** OK */
	public static final String STATUS_OK = "00";
	/** 準備よし */
	public static final String STATUS_READY = "90";
	/** サポートされないオペレーション */
	public static final String STATUS_UNSUPPORTED_METHOD = "98";
	/** OKステータス */
	public static final FileStatus OK = new FileStatus(STATUS_OK, NULL_CODE, 0, "it's ok.");
	/** READYステータス */
	public static final FileStatus READY = new FileStatus(STATUS_READY, "READY", 0, "it's ready.");
	/** ファイルがアサインされていない */
	public static final FileStatus NOT_ASSIGNED = new FileStatus(FileStatus.STATUS_NOT_ASSIGND, FileStatus.NULL_CODE, 0, "file is not assigned");
	/** 何らかの異常 */
	public static final FileStatus FAILURE = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "something failure");
	/** ステータスコード */
	private String statusCode = FileStatus.STATUS_OK;
	/** ステータスメッセージ */
	private String statusMessage = "";
	/** SQLステータス (5桁) */
	private String sqlStatus = NULL_CODE;
	/** エラーコード */
	private int errCode = 0;
	/**
	 * コンストラクタ
	 * @param statusCode コード
	 * @param sqlStatus SQLステータス
	 * @param errCode エラーコード
	 * @param statusMessage メッセージ
	 */
	public FileStatus(String statusCode, String sqlStatus, int errCode, String statusMessage) {
		this.statusCode = statusCode;
		this.sqlStatus = sqlStatus;
		this.errCode = errCode;
		this.statusMessage = statusMessage;
	}
	/**
	 * エラーコードを取得する
	 * @return エラーコード(11桁)
	 */
	public int getErrStatus() {
		return errCode;
	}
	/**
	 * エラーコードを取得する
	 * @return エラーコード(11桁)
	 */
	public String getErrString() {
		// 12345678901 12345678901
		DecimalFormat format = new DecimalFormat("00000000000;----------0");
		return format.format(getErrStatus());
	}
	/**
	 * SQLステータスを取得する
	 * @return SQLステータス(5桁)
	 */
	public String getSqlStatus() {
		if (sqlStatus == null)
			sqlStatus = STATUS_FAILURE;
		if (sqlStatus.length() < 5)
			sqlStatus = STATUS_FAILURE;
		if (sqlStatus.length() > 5)
			sqlStatus = sqlStatus.substring(0, 5);
		return sqlStatus;
	}
	/**
	 * コードを取得する
	 * @return ファイルステータス(2桁)
	 */
	public String getStatusCode() {
		if (statusCode == null)
			statusCode = STATUS_FAILURE;
		if (statusCode.length() < 2)
			statusCode = STATUS_FAILURE;
		if (statusCode.length() > 2)
			statusCode = statusCode.substring(0, 2);
		return statusCode;
	}
	/**
	 * メッセージを取得する
	 * @return メッセージ
	 */
	public String getStatusMessage() {
		if (statusMessage == null)
			statusMessage = "";
		if (statusMessage.length() > 233)
			statusMessage = statusMessage.substring(0, 233);
		return statusMessage;
	}
	/**
	 * エラーコードをセットする
	 * @param errStatus エラーコード
	 */
	protected void setErrStatus(int errStatus) {
		this.errCode = errStatus;
	}
	/**
	 * SQLステータスをセットする
	 * @param sqlStatus SQLステータス
	 */
	protected void setSqlStatus(String sqlStatus) {
		this.sqlStatus = sqlStatus;
	}
	/**
	 * コードをセットする
	 * @param statusCode コード
	 */
	protected void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * メッセージをセットする
	 * @param statusMessage メッセージ
	 */
	protected void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getStatusCode());
		buf.append(" ");
		buf.append(getSqlStatus());
		buf.append(" ");
		buf.append(getErrString());
		buf.append(" ");
		buf.append(getStatusMessage());
		buf.append(" ");
		return buf.toString();
	}
}
