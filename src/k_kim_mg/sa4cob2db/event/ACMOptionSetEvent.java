package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;
import k_kim_mg.sa4cob2db.ACMSession;

/**
 * This event will be occurred when session option value settled.
 * @author kkimmg@gmail.com
 *
 */
public class ACMOptionSetEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private ACMSession session;
	private String key;
	private String value;

	/**
	 * Constructor.
	 * @param source session
	 * @param key option key
	 * @param value option value
	 */
	public ACMOptionSetEvent(ACMSession source, String key, String value) {
		super(source);
		session = source;
		this.key = key;
		this.value = value;
	}

	/**
	 * session.
	 * 
	 * @return session
	 */
	public ACMSession getSession() {
		return session;
	}

	/**
	 * option key.
	 * 
	 * @return key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * option value.
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}
}
