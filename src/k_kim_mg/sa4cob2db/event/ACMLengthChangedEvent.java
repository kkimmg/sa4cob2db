package k_kim_mg.sa4cob2db.event;

import k_kim_mg.sa4cob2db.ACMSession;

import java.util.EventObject;

/**
 * Max Record Length Changed Event
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMLengthChangedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int oldLength;
	private int newLength;

	/**
	 * Create Event Object.
	 * 
	 * @param source session object
	 * @param oldLength old length
	 * @param newLength new length
	 */
	public ACMLengthChangedEvent(ACMSession source, int oldLength, int newLength) {
		super(source);
		this.oldLength = oldLength;
		this.newLength = newLength;
	}

	/**
	 * get Old Length.
	 * 
	 * @return old length
	 */
	public int getOldLength() {
		return oldLength;
	}

	/**
	 * get New length.
	 * 
	 * @return new length
	 */
	public int getNewLength() {
		return newLength;
	}
}
