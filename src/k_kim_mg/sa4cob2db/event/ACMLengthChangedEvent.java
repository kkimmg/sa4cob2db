package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;
import k_kim_mg.sa4cob2db.ACMSession;

/**
 * Max Record Length Changed Event
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMLengthChangedEvent extends EventObject {
	
	private static final long serialVersionUID = 1L;

	private int oldLength, newLength;
	private ACMSession session;
	/**
	 * Create Event Object
	 * @param source session object
	 * @param oldLenth old length
	 * @param newLength new length
	 */
	public ACMLengthChangedEvent(ACMSession source, int oldLength, int newLength) {
		super(source);
		this.source = source;
		this.oldLength = oldLength;
		this.newLength = newLength;
	}
	
	/**
	 * get old length
	 * @return old length
	 */
	public int getOldLength() {
		return oldLength;
	}
	/**
	 * new old length
	 * @return new length
	 */
	public int getNewLength() {
		return newLength;
	}
	/**
	 * session
	 * @return session
	 */
	public ACMSession getSession() {
		return session;
	}
}
