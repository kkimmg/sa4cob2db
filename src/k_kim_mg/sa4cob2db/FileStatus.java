package k_kim_mg.sa4cob2db;
import java.text.DecimalFormat;
/**
 * file status
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class FileStatus {
	/** NULL CODE */
	public static final String NULL_CODE = "     ";
	/** status code */
	public static final String STATUS_SUCCESS = "00";
	/** status code */
	public static final String STATUS_SUCCESS_DUPLICATE = "02";
	/** status code */
	public static final String STATUS_SUCCESS_OPTIONAL = "05";
	/** status code */
	public static final String STATUS_END_OF_FILE = "10";
	/** status code */
	public static final String STATUS_KEY_EXISTS = "22";
	/** status code */
	public static final String STATUS_KEY_NOT_EXISTS = "23";
	/** status code */
	public static final String STATUS_FILEOVER = "24";
	/** status code */
	public static final String STATUS_PERMISSION_DENIED = "37";
	/** status code */
	public static final String STATUS_ALREADY_OPEN = "41";
	/** status code */
	public static final String STATUS_NOT_OPEN = "42";
	/** status code */
	public static final String STATUS_READ_NOT_DONE = "43";
	/** status code */
	public static final String STATUS_INPUT_DENIED = "47";
	/** status code */
	public static final String STATUS_OUTPUT_DENIED = "48";
	/** status code */
	public static final String STATUS_I_O_DENIED = "49";
	/** status code */
	public static final String STATUS_READY = "90";
	/** status code */
	public static final String STATUS_NOT_AVAILABLE = "91";
	/** status code */
	public static final String STATUS_92_NOT_OPENED = "92";
	/** status code */
	public static final String STATUS_93_NOT_ASSIGND = "93";
	/** status code */
	public static final String STATUS_98_UNSUPPORTED_METHOD = "98";
	/** status code */
	public static final String STATUS_99_FAILURE = "99";
	/** error code */
	private long errCode = 0;
	/** SQLstatus */
	private String sqlStatus = NULL_CODE;
	/** status code */
	private String statusCode = FileStatus.STATUS_SUCCESS;
	/** status message */
	private String statusMessage = "";
	/** OK status */
	public static final FileStatus OK = new FileStatus(STATUS_SUCCESS, NULL_CODE, 0, "it's ok.");
	/** READY status */
	public static final FileStatus READY = new FileStatus(STATUS_READY, "READY", 0, "it's ready.");
	/** something failure */
	public static final FileStatus FAILURE = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "something failure");
	/** file is not assigned */
	public static final FileStatus NOT_ASSIGNED = new FileStatus(FileStatus.STATUS_93_NOT_ASSIGND, FileStatus.NULL_CODE, 0, "file is not assigned");
	/**
	 * Constructor
	 * 
	 * @param statusCode status code
	 * @param sqlStatus SQLstatus
	 * @param errCode error code
	 * @param statusMessage message
	 */
	public FileStatus(String statusCode, String sqlStatus, long errCode, String statusMessage) {
		this.statusCode = statusCode;
		this.sqlStatus = sqlStatus;
		this.errCode = errCode;
		this.statusMessage = statusMessage;
	}
	/**
	 * get error code
	 * 
	 * @return error code(11 digit number)
	 */
	public long getErrStatus() {
		return errCode;
	}
	/**
	 * get error code
	 * 
	 * @return error code(11 digit number)
	 */
	public String getErrString() {
		// 12345678901 12345678901
		DecimalFormat format = new DecimalFormat("00000000000;----------0");
		return format.format(getErrStatus());
	}
	/**
	 * get SQL status
	 * 
	 * @return SQL status(5-digit number)
	 */
	public String getSqlStatus() {
		if (sqlStatus == null)
			sqlStatus = STATUS_99_FAILURE;
		if (sqlStatus.length() < 5)
			sqlStatus = STATUS_99_FAILURE;
		if (sqlStatus.length() > 5)
			sqlStatus = sqlStatus.substring(0, 5);
		return sqlStatus;
	}
	/**
	 * get file status
	 * 
	 * @return file status(2-digit number)
	 */
	public String getStatusCode() {
		if (statusCode == null)
			statusCode = STATUS_99_FAILURE;
		if (statusCode.length() < 2)
			statusCode = STATUS_99_FAILURE;
		if (statusCode.length() > 2)
			statusCode = statusCode.substring(0, 2);
		return statusCode;
	}
	/**
	 * get error message
	 * 
	 * @return message
	 */
	public String getStatusMessage() {
		if (statusMessage == null)
			statusMessage = "";
		if (statusMessage.length() > 233)
			statusMessage = statusMessage.substring(0, 233);
		return statusMessage;
	}
	/**
	 * set error status
	 * 
	 * @param errStatus status(11-digit number)
	 */
	protected void setErrStatus(long errStatus) {
		assert errStatus >= 0;
		assert errStatus <= 99999999999L;
		this.errCode = errStatus;
	}
	/**
	 * set SQL status
	 * 
	 * @param sqlStatus SQLstatus
	 */
	protected void setSqlStatus(String sqlStatus) {
		this.sqlStatus = sqlStatus;
	}
	/**
	 * set file status
	 * 
	 * @param statusCode file status
	 */
	protected void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * set message
	 * 
	 * @param statusMessage message
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
