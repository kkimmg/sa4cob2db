package k_kim_mg.sa4cob2db.event;

import java.util.EventListener;
/**
 * lisson session event
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface ACMSessionEventListener extends EventListener {
	/**
	 * file created
	 * @param e	event
	 */
	public void fileCreated(ACMSessionEvent e);
	/**
	 * file destroyed
	 * @param e	event
	 */
	public void fileDestroyed(ACMSessionEvent e);
	/**
	 * transaction commited
	 * @param e	event
	 */
	public void transactionCommited(ACMSessionEvent e);
	/**
	 * transaction rollbacked
	 * @param e	event
	 */
	public void transactionRollbacked(ACMSessionEvent e);
	/**
	 * option set event
	 * @param e event
	 */
	public void optionSetted (ACMOptionSetEvent e);
	/**
	 * max record length changed
	 * @param e evnet
	 */
	public void lengthChanged (ACMLengthChangedEvent e);
}
