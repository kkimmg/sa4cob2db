package k_kim_mg.sa4cob2db.test;
import k_kim_mg.sa4cob2db.AbstractCobolFile;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.FileStatus;

/**
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CustomCobolFileTest extends AbstractCobolFile {
	private static final long serialVersionUID = 1L;
	/** 実装されていないオペレーション */
	private static FileStatus UNSUPPORTED_METHOD = new FileStatus(FileStatus.STATUS_UNSUPPORTED_METHOD, FileStatus.NULL_CODE, 0, "unsupported operation");
	/** meta data */
	private CobolRecordMetaData meta = null;
	/** fileが開かれたかどうか */
	private boolean opened = false;
	/** 何かの値 */
	private int value = 0;

	public CustomCobolFileTest(CobolRecordMetaData meta) {
		this.meta = meta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#close()
	 */
	public FileStatus close() {
		return FileStatus.OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#delete()
	 */
	public FileStatus delete() {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#getCurrentRow()
	 */
	public int getCurrentRow() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#getMetaData()
	 */
	public CobolRecordMetaData getMetaData() {
		return meta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#getRowCount()
	 */
	public int getRowCount() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#isLastMoved()
	 */
	@Override
	public boolean isLastMoved() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#isOpened()
	 */
	public boolean isOpened() {
		return opened;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#move(byte[])
	 */
	public FileStatus move(byte[] record) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#move(int)
	 */
	@Override
	public FileStatus move(int row) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#moveFirst()
	 */
	@Override
	public FileStatus moveFirst() {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#moveLast()
	 */
	@Override
	public FileStatus moveLast() {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#next()
	 */
	@Override
	public FileStatus next() {
		value++;
		return FileStatus.OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#next(int)
	 */
	@Override
	public FileStatus next(int row) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#nextOnFile()
	 */
	@Override
	protected FileStatus nextOnFile() {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#open(int, int)
	 */
	public FileStatus open(int mode, int accessmode) {
		opened = true;
		return FileStatus.OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#previous()
	 */
	public FileStatus previous() {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#previous(int)
	 */
	@Override
	public FileStatus previous(int row) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#read(byte[])
	 */
	@Override
	public FileStatus read(byte[] record) {
		String string = Integer.toString(value);
		byte[] bytes = string.getBytes();
		System.arraycopy(bytes, 0, record, 0, (record.length > bytes.length ? bytes.length : record.length));
		return FileStatus.OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#readFromFile(byte[])
	 */
	@Override
	protected FileStatus readFromFile(byte[] record) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#rewrite(byte[])
	 */
	public FileStatus rewrite(byte[] record) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(int, byte[])
	 */
	public FileStatus start(int mode, byte[] record) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(int, byte[], boolean)
	 */
	public FileStatus start(int mode, byte[] record, boolean duplicates) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(java.lang.String, int, byte[],
	 * boolean)
	 */
	public FileStatus start(String IndexName, int mode, byte[] record, boolean duplicates) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#write(byte[])
	 */
	public FileStatus write(byte[] record) {
		return UNSUPPORTED_METHOD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#startDuplicates(int, byte[])
	 */
	@Override
	public FileStatus startDuplicates(int mode, byte[] record) {
		return UNSUPPORTED_METHOD;
	}

	public FileStatus delete(byte[] record) {
		return UNSUPPORTED_METHOD;
	}

	@Override
	public void truncate() {
		// do nothing
	}
}
