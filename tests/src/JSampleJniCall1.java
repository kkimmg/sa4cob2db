//package k_kim_mg.sa4cob2db.sample;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
public class JSampleJniCall1 {
	/** �饤�֥��̾ */
	public static final String ACM_SAMPLE_LIBRARY_NAME = "sampleJniCall";
	public static final String OPEN_COB_LIBRARY_NAME = "cob";
	static {
		/* �饤�֥��λ������� */
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
	/** �ͥ��ƥ��֥᥽�åɤ��������� */
	public static final int JNI_RETURN_OK = 0;
	/** �ͥ��ƥ��֥᥽�åɤ����Ԥ��� */
	public static final int JNI_RETURN_NG = -1;
	/**
	 * �ͥ��ƥ��֥᥽�å� ���ܥ륵�֥ץ����򥳡��뤹��C�롼����
	 * @param progname	�ץ����̾
	 * @param head		�إå�(SPA?)
	 * @param body		�ܥǥ�(����饤���å�����?)
	 * @return �ͥ��ƥ��֥᥽�åɤ�����/������
	 */
	public native int sampleJniCall1(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);
	/**
	 * �ͥ��ƥ��֥᥽�å� ���ܥ륵�֥ץ����򥳡��뤹��C�롼����
	 * @param libname	�饤�֥��̾
	 * @param progname	�ץ����̾
	 * @param head		�إå�(SPA?)
	 * @param body		�ܥǥ�(����饤���å�����?)
	 * @return �ͥ��ƥ��֥᥽�åɤ�����/������
	 */
	public native int sampleJniCall2(String libname, String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);
	/**
	 * �ͥ��ƥ��֥᥽�åɤ�ľ�ܤΥ�åѡ�
	 * @param libname	�饤�֥��̾
	 * @param progname	�ץ����̾
	 * @param head		�إå�(SPA?)
	 * @param body		�ܥǥ�(����饤���å�����?)
	 * @return �ͥ��ƥ��֥᥽�åɤ�����/������
	 */
	public int jniCallCobol1(String libname, String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall2(libname, progname, head, bodyIn, bodyOut);
	}
	/**
	 * �ͥ��ƥ��֥᥽�åɤ�ľ�ܤΥ�åѡ�
	 * @param libname	�饤�֥��̾
	 * @param progname	�ץ����̾
	 * @param head		�إå�(SPA?)
	 * @param body		�ܥǥ�(����饤���å�����?)
	 * @return �ͥ��ƥ��֥᥽�åɤ�����/������
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
	 * �ͥ��ƥ��֥᥽�åɤ�ľ�ܤΥ�åѡ�
	 * @param progname	�ץ����̾
	 * @param head		�إå�(SPA?)
	 * @param body		�ܥǥ�(����饤���å�����?)
	 * @return �ͥ��ƥ��֥᥽�åɤ�����/������
	 */
	public int jniCallCobol1(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall1(progname, head, bodyIn, bodyOut);
	}
	/**
	 * �ͥ��ƥ��֥᥽�åɤ�ľ�ܤΥ�åѡ�
	 * @param progname	�ץ����̾
	 * @param head		�إå�(SPA?)
	 * @param body		�ܥǥ�(����饤���å�����?)
	 * @return �ͥ��ƥ��֥᥽�åɤ�����/������
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
	 * �쥳���ɤ���Х����������Ф�
	 * 
	 * @param record	�쥳����
	 * @return �Х�������
	 * @throws CobolRecordException	�㳰
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
