package k_kim_mg.sa4cob2db;
import java.io.Serializable;
import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
/**
 * cobol file
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobolFile extends Serializable {
	/** access mode */
	public static final int ACCESS_DYNAMIC = 1;
	/** access mode */
	public static final int ACCESS_RANDOM = 2;
	/** access mode */
	public static final int ACCESS_SEQUENTIAL = 0;
	/**
	 * IS EQUAL TO IS =
	 */
	public static final int IS_EQUAL_TO = 0;
	/**
	 * IS GREATER THAN IS >
	 */
	public static final int IS_GREATER_THAN = 1;
	/**
	 * IS GREATER THAN OR EQUAL TO IS >=
	 */
	public static final int IS_GREATER_THAN_OR_EQUAL_TO = 3;
	/**
	 * IS NOT LESS THAN IS NOT < IS >=
	 */
	public static final int IS_NOT_LESS_THAN = 3;
	/** open mode */
	public static final int MODE_EXTEND = 3;
	/** open mode */
	public static final int MODE_INPUT = 0;
	/** open mode */
	public static final int MODE_INPUT_OUTPUT = 2;
	/** open mode */
	public static final int MODE_OUTPUT = 1;
	/**
	 * add CobolFileEventListener
	 * 
	 * @param listener CobolFileEventListener
	 */
	public void addCobolFileEventListener(CobolFileEventListener listener);
	/**
	 * add index
	 * 
	 * @param index index
	 * @param file index file
	 */
	public void addIndex(CobolIndex index, CobolFile file);
	/**
	 * bind this file to session
	 * 
	 * @param session session
	 */
	public void bindSession(ACMSession session);
	/**
	 * close file
	 * 
	 * @return status
	 */
	public FileStatus close();
	/**
	 * delete record
	 * 
	 * @param record key
	 * @return file status
	 */
	public FileStatus delete(byte[] record);
	/**
	 * access mode
	 * 
	 * @return access mode
	 */
	public int getAccessMode();
	/**
	 * current row number
	 * 
	 * @return number
	 */
	public int getCurrentRow();
	/**
	 * get meta data
	 * 
	 * @return meta data object
	 */
	public CobolRecordMetaData getMetaData();
	/**
	 * open mode
	 * 
	 * @return open mode
	 */
	public int getOpenMode();
	/**
	 * get row count
	 * 
	 * @return row count
	 */
	public int getRowCount();
	/**
	 * get session
	 * 
	 * @return session
	 */
	public ACMSession getSession();
	/**
	 * is this opened?
	 * 
	 * @return true yes<br>
	 *         false no
	 */
	public boolean isOpened();
	/**
	 * locate to record
	 * 
	 * @param record bytes includes key
	 * @return status
	 */
	public FileStatus move(byte[] record);
	/**
	 * move to next record
	 * 
	 * @return status
	 */
	public FileStatus next();
	/**
	 * file open
	 * 
	 * @param mode mode open mode input/output/expand/input-output
	 * @param accessmode access mode sequential/random/dynamic
	 * @return status
	 */
	public FileStatus open(int mode, int accessmode);
	/**
	 * move to previous record
	 * 
	 * @return status
	 */
	public FileStatus previous();
	/**
	 * move record to bytes
	 * 
	 * @param record bytes
	 * @return status
	 */
	public FileStatus read(byte[] record);
	/**
	 * remove CobolFileEventListener
	 * 
	 * @param listener CobolFileEventListener to remove
	 */
	public void removeCobolFileEventListener(CobolFileEventListener listener);
	/**
	 * rewrite record
	 * 
	 * @param record bytes
	 * @return status
	 */
	public FileStatus rewrite(byte[] record);
	/**
	 * start<br>
	 * move to key location
	 * 
	 * @param mode mode (EQ GT ...)
	 * @param record bytes includes key
	 * @return status
	 */
	public FileStatus start(int mode, byte[] record);
	/**
	 * start<br>
	 * move to key location
	 * 
	 * @param mode mode (EQ GT ...)
	 * @param duplicates with duplicates?
	 * @return status
	 */
	public FileStatus start(int mode, byte[] record, boolean duplicates);
	/**
	 * start by index <br>
	 * move to secondly key location
	 * 
	 * @param IndexName index name
	 * @param mode mode
	 * @param record bytes includes key
	 * @return status
	 * @throws CobolRecordException something happen
	 */
	public FileStatus start(String IndexName, int mode, byte[] record);
	/**
	 * delete all records
	 */
	public void truncate();
	/**
	 * add record<br>
	 * insert record
	 * 
	 * @param record bytes
	 * @return status
	 */
	public FileStatus write(byte[] record);
}