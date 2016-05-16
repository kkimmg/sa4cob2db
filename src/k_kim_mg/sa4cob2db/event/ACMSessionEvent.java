package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolFile;

/**
 * event related session.
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSessionEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private ACMSession session;
	private CobolFile file;

	/**
	 * Constructor.
	 * 
	 * @param source session
	 * @param file file
	 */
	public ACMSessionEvent(ACMSession source, CobolFile file) {
		super(source);
		this.session = source;
		this.file = file;
	}

	/**
	 * file.
	 * 
	 * @return file
	 */
	public CobolFile getFile() {
		return file;
	}

	/**
	 * session.
	 * 
	 * @return session
	 */
	public ACMSession getSession() {
		return session;
	}
}
