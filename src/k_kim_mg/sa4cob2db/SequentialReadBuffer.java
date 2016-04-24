package k_kim_mg.sa4cob2db;

/**
 * buffer for sequential read file
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface SequentialReadBuffer {
	/**
	 * move to next buffer
	 * 
	 * @return status
	 */
	public FileStatus nextBuffer();

	/**
	 * read current buffer record
	 * 
	 * @param record record
	 * @return status
	 */
	public FileStatus readBuffer(byte[] record);

	/**
	 * start buffering
	 */
	public void startBuffering();
}
