package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.FileStatus;
/**
 * file event
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CobolFileEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private transient CobolFile file = null;
	private transient FileStatus status;
	/**
	 * Constructor
	 * @param source	event happened object
	 * @param status	filestatus
	 */
	public CobolFileEvent(CobolFile source, FileStatus status) {
		super(source);
		this.file = source;
		this.status = status;
	}
	/**
	 * be equal to source
	 * @return source	
	 */
	public CobolFile getFile () {
		return file;
	}
	/**
	 * filestatus
	 * @return filestatus
	 */
	public FileStatus getStatus() {
		return status;
	}
	/**
	 * filestatus
	 * @param status filestatus
	 */
	public void setStatus(FileStatus status) {
		this.status = status;
	}
	/* (non-Javadoc)
     * @see java.util.EventObject#toString()
     */
    @Override
    public String toString() {
	    return file.getMetaData().getName() + ":" + this.status.toString();
    }
}
