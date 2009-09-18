package k_kim_mg.sa4cob2db.sql;
/**
 * ���å����˴ؤ����ƥ����󶡤���
 * @author ���줪��
 */
public abstract interface ACMNetSession {
	/** ���ơ������ΥХ���Ĺ */
	public static final int STATUS_LEN = 3;
	public static final int STATUS_MAX = (STATUS_LEN - 1);
	public static final int FILE_IDENT_LEN = 256;
	public static final int FILE_IDENT_MAX = (FILE_IDENT_LEN - 1);
	public static final int FILE_OPTION_LEN = 256;
	public static final int FILE_OPTION_MAX = (FILE_OPTION_LEN - 1);
	public static final int FILE_STARTMODE_LEN = 30;
	public static final int FILE_STARTMODE_MAX = (FILE_STARTMODE_LEN - 1);
	public static final int FILE_INDEXNAME_LEN = 226;
	public static final int FILE_INDEXNAME_MAX = (FILE_INDEXNAME_LEN - 1);
	public static final int HOSTNAME_LEN = 256;
	public static final int HOSTNAME_MAX = (FILE_IDENT_LEN - 1);
	public static final int PORT_LEN = 6;
	public static final int PORT_MAX = (FILE_IDENT_LEN - 1);
	public static final int USERNAME_LEN = 256;
	public static final int USERNAME_MAX = (FILE_IDENT_LEN - 1);
	public static final int PASSWORD_LEN = 256;
	public static final int PASSWORD_MAX = (FILE_IDENT_LEN - 1);
	public static final int RECORD_LEN = 8192;
	public static final int RECORD_MAX = (RECORD_LEN - 1);
	
	public static final String MSG_ASSIGN = "ASSIGN";
	public static final String MSG_CLOSE = "CLOSE";
	public static final String MSG_DELETE = "DELETE";
	public static final String MSG_INITIALIZE = "INITIALIZE";
	public static final String MSG_MOVE = "MOVE";
	public static final String MSG_NEXT = "NEXT";
	public static final String MSG_OPEN = "OPEN";
	public static final String MSG_PASSWORD = "PASSWORD";
	public static final String MSG_PREVIOUS = "PREVIOUS";
	public static final String MSG_READ = "READ";
	public static final String MSG_REWRITE = "REWRITE";
	public static final String MSG_TERMINATE = "TERMINATE";
	public static final String MSG_USERNAME = "USERNAME";
	public static final String MSG_WRITE = "WRITE";
	public static final String STT_IS_GREATER_THAN = "IS_GREATER_THAN";
	public static final String STT_IS_GREATER_THAN_OR_EQUAL_TO = "IS_GREATER_THAN_OR_EQUAL_TO";
	public static final String STT_IS_NOT_LESS_THAN = "IS_NOT_LESS_THAN";
}