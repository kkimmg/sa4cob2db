/**
 * 
 */
package k_kim_mg.sa4cob2db.event;
/**
 * ACMSessionEventListenerのデフォルト実装<br/>
 * これといってなにかするわけじゃない　
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSessionEventAdapter implements ACMSessionEventListener {
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#fileCreated
	 * (k_kim_mg.sa4cob2db.event.ACMSessionEvent)
	 */
	public void fileCreated(ACMSessionEvent e) {
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#fileDestroyed
	 * (k_kim_mg.sa4cob2db.event.ACMSessionEvent)
	 */
	public void fileDestroyed(ACMSessionEvent e) {
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.event.ACMSessionEventListener#
	 * transactionCommited(k_kim_mg.sa4cob2db.event.ACMSessionEvent)
	 */
	public void transactionCommited(ACMSessionEvent e) {
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.event.ACMSessionEventListener#
	 * transactionRollbacked
	 * (k_kim_mg.sa4cob2db.event.ACMSessionEvent)
	 */
	public void transactionRollbacked(ACMSessionEvent e) {
	}
}
