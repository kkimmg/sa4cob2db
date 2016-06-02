package k_kim_mg.sa4cob2db.event;

import java.util.EventListener;

/**
 * listen session event.
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface ACMSessionEventListener extends EventListener {
	/**
	 * file created.
	 * 
	 * @param e event
	 */
	public void fileCreated(ACMSessionEvent e);

	/**
	 * file destroyed.
	 * 
	 * @param e event
	 */
	public void fileDestroyed(ACMSessionEvent e);

	/**
	 * FileList Pop Event
	 * 
	 * @param e event
	 */
	public void fileListPoped(ACMSessionEvent e);

	/**
	 * FileList Push Event
	 * 
	 * @param e event
	 */
	public void fileListPushed(ACMSessionEvent e);

	/**
	 * max record length changed.
	 * 
	 * @param e event
	 */
	public void lengthChanged(ACMLengthChangedEvent e);

	/**
	 * option set event.
	 * 
	 * @param e event
	 */
	public void optionSetted(ACMOptionSetEvent e);

	/**
	 * transaction commited.
	 * 
	 * @param e event
	 */
	public void transactionCommited(ACMSessionEvent e);

	/**
	 * transaction rollbacked.
	 * 
	 * @param e event
	 */
	public void transactionRollbacked(ACMSessionEvent e);
}
