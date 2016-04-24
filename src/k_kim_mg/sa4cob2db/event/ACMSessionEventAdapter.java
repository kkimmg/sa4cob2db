/**
 * 
 */
package k_kim_mg.sa4cob2db.event;

/**
 * default implement of ACMSessionEventListener
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSessionEventAdapter implements ACMSessionEventListener {
	@Override
	public void fileCreated(ACMSessionEvent e) {
	}

	@Override
	public void fileDestroyed(ACMSessionEvent e) {
	}

	@Override
	public void transactionCommited(ACMSessionEvent e) {
	}

	@Override
	public void transactionRollbacked(ACMSessionEvent e) {
	}

	@Override
	public void optionSetted(ACMOptionSetEvent e) {
	}

	@Override
	public void lengthChanged(ACMLengthChangedEvent e) {
	}
}
