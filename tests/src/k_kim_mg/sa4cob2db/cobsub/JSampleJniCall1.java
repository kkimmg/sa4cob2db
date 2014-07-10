package k_kim_mg.sa4cob2db.cobsub;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
public class JSampleJniCall1 {
	/** ���C�u������ */
	public static final String ACM_SAMPLE_LIBRARY_NAME = "sampleJniCall";
	public static final String OPEN_COB_LIBRARY_NAME = "cob";
	static {
		/* ���C�u�����̎��O���[�h */
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
	/** �l�C�e�B�u���\�b�h���������� */
	public static final int JNI_RETURN_OK = 0;
	/** �l�C�e�B�u���\�b�h�����s���� */
	public static final int JNI_RETURN_NG = -1;
	/**
	 * �l�C�e�B�u���\�b�h �R�{���T�u�v���O�������R�[������C���[�`��
	 * @param progname	�v���O������
	 * @param head		�w�b�_(SPA?)
	 * @param body		�{�f�B(�I�����C�����b�Z�[�W?)
	 * @return �l�C�e�B�u���\�b�h�̐���/�s����
	 */
	public native int sampleJniCall1(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);
	/**
	 * �l�C�e�B�u���\�b�h �R�{���T�u�v���O�������R�[������C���[�`��
	 * @param libname	���C�u������
	 * @param progname	�v���O������
	 * @param head		�w�b�_(SPA?)
	 * @param body		�{�f�B(�I�����C�����b�Z�[�W?)
	 * @return �l�C�e�B�u���\�b�h�̐���/�s����
	 */
	public native int sampleJniCall2(String libname, String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);
	/**
	 * �l�C�e�B�u���\�b�h�̒��ڂ̃��b�p�[
	 * @param libname	���C�u������
	 * @param progname	�v���O������
	 * @param head		�w�b�_(SPA?)
	 * @param body		�{�f�B(�I�����C�����b�Z�[�W?)
	 * @return �l�C�e�B�u���\�b�h�̐���/�s����
	 */
	public int jniCallCobol1(String libname, String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall2(libname, progname, head, bodyIn, bodyOut);
	}
	/**
	 * �l�C�e�B�u���\�b�h�̒��ڂ̃��b�p�[
	 * @param libname	���C�u������
	 * @param progname	�v���O������
	 * @param head		�w�b�_(SPA?)
	 * @param body		�{�f�B(�I�����C�����b�Z�[�W?)
	 * @return �l�C�e�B�u���\�b�h�̐���/�s����
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
	 * �l�C�e�B�u���\�b�h�̒��ڂ̃��b�p�[
	 * @param progname	�v���O������
	 * @param head		�w�b�_(SPA?)
	 * @param body		�{�f�B(�I�����C�����b�Z�[�W?)
	 * @return �l�C�e�B�u���\�b�h�̐���/�s����
	 */
	public int jniCallCobol1(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall1(progname, head, bodyIn, bodyOut);
	}
	/**
	 * �l�C�e�B�u���\�b�h�̒��ڂ̃��b�p�[
	 * @param progname	�v���O������
	 * @param head		�w�b�_(SPA?)
	 * @param body		�{�f�B(�I�����C�����b�Z�[�W?)
	 * @return �l�C�e�B�u���\�b�h�̐���/�s����
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
	 * ���R�[�h����o�C�g�z������o��
	 * 
	 * @param record	���R�[�h
	 * @return �o�C�g�z��
	 * @throws CobolRecordException	��O
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
