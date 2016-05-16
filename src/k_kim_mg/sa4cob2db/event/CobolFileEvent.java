package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.FileStatus;

/**
 * file event
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CobolFileEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private CobolFile file = null;
	private FileStatus status;

	/**
	 * Constructor.
	 * 
	 * @param source event happened object
	 * @param status File Status
	 */
	public CobolFileEvent(CobolFile source, FileStatus status) {
		super(source);
		this.file = source;
		this.status = status;
	}

	/**
	 * get File Object.
	 * 
	 * @return File Object what occur event.
	 */
	public CobolFile getFile() {
		return file;
	}

	/**
	 * get FileStatus.
	 * 
	 * @return File Status
	 */
	public FileStatus getStatus() {
		return status;
	}

	/**
	 * set FileStatus.
	 * 
	 * @param status File Status
	 */
	public void setStatus(FileStatus status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		return file.getMetaData().getName() + ":" + this.status.toString();
	}
}
