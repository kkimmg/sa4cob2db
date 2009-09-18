package k_kim_mg.sa4cob2db;
import java.text.DecimalFormat;
/**
 * �ե����륹�ơ�����
 * @author ���줪��
 */
public class FileStatus {
	/** ������ꤷ�ʤ������� */
	public static final String NULL_CODE = "     ";
	/** ���˥���������Ƥ��� */
	public static final String STATUS_ALREADY_CLOSED = "42";
	/** ���˥����ץ󤵤�Ƥ��� */
	public static final String STATUS_ALREADY_OPENED = "41";
	/** BOF-�ե�����λ�ü */
	public static final String STATUS_BOF = "91";
	/** DELETE�Ǥ��ʤ��ʥ⡼�ɸ��� */
	public static final String STATUS_CANT_DELETE = "49";
	/** �����ץ�Ǥ��ʤ��ʥ��ݡ��Ȥ���Ƥ��ʤ������ץ�⡼�ɡ� */
	public static final String STATUS_CANT_OPEN = "37";
	/** �꡼�ɤǤ��ʤ��ʥ⡼�ɸ��� */
	public static final String STATUS_CANT_READ = "47";
	/** REWRITE�Ǥ��ʤ��ʥ⡼�ɸ��� */
	public static final String STATUS_CANT_REWRITE = "49";
	/** WRITE�Ǥ��ʤ��ʥ⡼�ɸ��� */
	public static final String STATUS_CANT_WRITE = "48";
	/** ��ʣ���� */
	public static final String STATUS_DUPLICATE_KEY = "22";
	/** ���֥�������ʣ���Ƥ��� */
	public static final String STATUS_DUPLICATE_SUBKEY = "02";
	/** EOF-�ե�����ν�ü */
	public static final String STATUS_EOF = "10";
	/** �ʤ��Τ�󤱤ɼ��Ԥ��� */
	public static final String STATUS_FAILURE = "99";
	/** �ե����륪���С� */
	public static final String STATUS_FILEOVER = "24";
	/** INVALID KEY */
	public static final String STATUS_INVALID_KEY = "23";
	/** �������󤵤�Ƥ��ʤ� */
	public static final String STATUS_NOT_ASSIGND = "93";
	/** �꡼�ɤ���Ƥ��ʤ��ʥ쥳���ɤ�ͭ���ʰ��֤ˤʤ��� */
	public static final String STATUS_NOT_MOVED = "43";
	/** �����ץ󤷤Ƥʤ� */
	public static final String STATUS_NOT_OPENED = "92";
	/** �ե����뤬¸�ߤ��ʤ� */
	public static final String STATUS_NOTEXIST = "05";
	/** OK */
	public static final String STATUS_OK = "00";
	/** �����褷 */
	public static final String STATUS_READY = "90";
	/** ���ݡ��Ȥ���ʤ����ڥ졼����� */
	public static final String STATUS_UNSUPPORTED_METHOD = "98";
	/** OK���ơ����� */
	public static final FileStatus OK = new FileStatus(STATUS_OK, NULL_CODE, 0, "it's ok.");
	/** READY���ơ����� */
	public static final FileStatus READY = new FileStatus(STATUS_READY, "READY", 0, "it's ready.");
	/** �ե����뤬�������󤵤�Ƥ��ʤ� */
	public static final FileStatus NOT_ASSIGNED = new FileStatus(FileStatus.STATUS_NOT_ASSIGND, FileStatus.NULL_CODE, 0, "file is not assigned");
	/** ���餫�ΰ۾� */
	public static final FileStatus FAILURE = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "something failure");
	/** ���ơ����������� */
	private String statusCode = FileStatus.STATUS_OK;
	/** ���ơ�������å����� */
	private String statusMessage = "";
	/** SQL���ơ����� (5��) */
	private String sqlStatus = NULL_CODE;
	/** ���顼������ */
	private int errCode = 0;
	/**
	 * ���󥹥ȥ饯��
	 * @param statusCode ������
	 * @param sqlStatus SQL���ơ�����
	 * @param errCode ���顼������
	 * @param statusMessage ��å�����
	 */
	public FileStatus(String statusCode, String sqlStatus, int errCode, String statusMessage) {
		this.statusCode = statusCode;
		this.sqlStatus = sqlStatus;
		this.errCode = errCode;
		this.statusMessage = statusMessage;
	}
	/**
	 * ���顼�����ɤ��������
	 * @return ���顼������(11��)
	 */
	public int getErrStatus() {
		return errCode;
	}
	/**
	 * ���顼�����ɤ��������
	 * @return ���顼������(11��)
	 */
	public String getErrString() {
		// 12345678901 12345678901
		DecimalFormat format = new DecimalFormat("00000000000;----------0");
		return format.format(getErrStatus());
	}
	/**
	 * SQL���ơ��������������
	 * @return SQL���ơ�����(5��)
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
	 * �����ɤ��������
	 * @return �ե����륹�ơ�����(2��)
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
	 * ��å��������������
	 * @return ��å�����
	 */
	public String getStatusMessage() {
		if (statusMessage == null)
			statusMessage = "";
		if (statusMessage.length() > 233)
			statusMessage = statusMessage.substring(0, 233);
		return statusMessage;
	}
	/**
	 * ���顼�����ɤ򥻥åȤ���
	 * @param errStatus ���顼������
	 */
	protected void setErrStatus(int errStatus) {
		this.errCode = errStatus;
	}
	/**
	 * SQL���ơ������򥻥åȤ���
	 * @param sqlStatus SQL���ơ�����
	 */
	protected void setSqlStatus(String sqlStatus) {
		this.sqlStatus = sqlStatus;
	}
	/**
	 * �����ɤ򥻥åȤ���
	 * @param statusCode ������
	 */
	protected void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * ��å������򥻥åȤ���
	 * @param statusMessage ��å�����
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
