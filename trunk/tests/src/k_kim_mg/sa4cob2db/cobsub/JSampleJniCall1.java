package k_kim_mg.sa4cob2db.cobsub;

import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;

public class JSampleJniCall1 {
	/** Singleton instance */
	private static JSampleJniCall1 singleton = new JSampleJniCall1();
	/** C library name */
	public static final String ACM_SAMPLE_LIBRARY_NAME = "sampleJniCall";
	/** COBOL library name */
	public static final String OPEN_COB_LIBRARY_NAME = "cob";
	static {
		/* loading libraries */
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
	/** Return value of java method (Success) */
	public static final int JNI_RETURN_OK = 0;
	/** Return value of java method (Failure) */
	public static final int JNI_RETURN_NG = -1;

	/** Get singleton instance */
	public static JSampleJniCall1 getSingletonInstance() {
		return singleton;
	}

	/**
	 * Record to Bytes
	 * 
	 * @param record Record
	 * @return Bytes
	 * @throws CobolRecordException Exception
	 */
	private byte[] getRecordToBytes(CobolRecord record) throws CobolRecordException {
		byte[] ret = null;
		CobolRecordMetaData meta = record.getMetaData();
		int size = meta.getRowSize();
		ret = new byte[size];
		record.getRecord(ret);
		return ret;
	}

	/**
	 * Wrapper of native method
	 * 
	 * @param progname Program Name
	 * @param head header
	 * @param bodyIn InputBody
	 * @param bodyOut OutputBody
	 * @return JIN_RETURN_OK/NG
	 */
	public int jniCallCobol_fork(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall_fork(progname, head, bodyIn, bodyOut);
	}

	/**
	 * Wrapper of native method
	 * 
	 * @param progname Program Name
	 * @param head header
	 * @param bodyIn InputBody
	 * @param bodyOut OutputBody
	 * @return JIN_RETURN_OK/NG
	 */
	public int jniCallCobol_fork(String progname, CobolRecord head, CobolRecord bodyIn, CobolRecord bodyOut) throws CobolRecordException {
		byte[] hbytes = getRecordToBytes(head);
		byte[] ibytes = getRecordToBytes(bodyIn);
		byte[] obytes = getRecordToBytes(bodyOut);
		int ret = sampleJniCall_fork(progname, hbytes, ibytes, obytes);
		head.setRecord(hbytes);
		bodyIn.setRecord(ibytes);
		bodyOut.setRecord(obytes);
		return ret;
	}

	/**
	 * Wrapper of native method
	 * 
	 * @param progname Program Name
	 * @param head header
	 * @param bodyIn InputBody
	 * @param bodyOut OutputBody
	 * @return JIN_RETURN_OK/NG
	 */
	public int jniCallCobol_nonfork(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut) {
		return sampleJniCall_nonfork(progname, head, bodyIn, bodyOut);
	}

	/**
	 * Wrapper of native method
	 * 
	 * @param libname Library Name
	 * @param progname Program Name
	 * @param head header
	 * @param bodyIn InputBody
	 * @param bodyOut OutputBody
	 * @return JIN_RETURN_OK/NG
	 */
	public int jniCallCobol_nonfork(String libname, String progname, CobolRecord head, CobolRecord bodyIn, CobolRecord bodyOut) throws CobolRecordException {
		byte[] hbytes = getRecordToBytes(head);
		byte[] ibytes = getRecordToBytes(bodyIn);
		byte[] obytes = getRecordToBytes(bodyOut);
		int ret = sampleJniCall_nonfork(progname, hbytes, ibytes, obytes);
		head.setRecord(hbytes);
		bodyIn.setRecord(ibytes);
		bodyOut.setRecord(obytes);
		return ret;
	}

	/**
	 * Calling COBOL SubProgram
	 * 
	 * @param progname Program Name
	 * @param head header
	 * @param bodyIn InputBody
	 * @param bodyOut OutputBody
	 * @return JIN_RETURN_OK/NG
	 */
	public native int sampleJniCall_fork(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);

	/**
	 * Calling COBOL SubProgram
	 * 
	 * @param libname Library Name
	 * @param progname Program Name
	 * @param head header
	 * @param bodyIn InputBody
	 * @param bodyOut OutputBody
	 * @return JIN_RETURN_OK/NG
	 */
	public native int sampleJniCall_nonfork(String progname, byte[] head, byte[] bodyIn, byte[] bodyOut);
}
