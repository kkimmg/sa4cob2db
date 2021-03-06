package k_kim_mg.sa4cob2db.test;

import k_kim_mg.sa4cob2db.event.ACMLengthChangedEvent;
import k_kim_mg.sa4cob2db.event.ACMOptionSetEvent;
import k_kim_mg.sa4cob2db.event.ACMSessionEvent;
import k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter;

/**
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * 
 */
public class ACMSessionEventAdapterTest extends ACMSessionEventAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#fileCreated(k_kim_mg.
	 * sa4cob2db.event.ACMSessionEvent)
	 */
	@Override
	public void fileCreated(ACMSessionEvent e) {
		System.err.println("File Created." + e.getFile().getMetaData().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#fileDestroyed(k_kim_mg
	 * .sa4cob2db.event.ACMSessionEvent)
	 */
	@Override
	public void fileDestroyed(ACMSessionEvent e) {
		System.err.println("File Destroyed." + e.getFile().getMetaData().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#optionSetted(k_kim_mg
	 * .sa4cob2db.event.ACMOptionSetEvent)
	 */
	@Override
	public void optionSetted(ACMOptionSetEvent e) {
		System.err.println("Option Set:" + e.getKey() + "=" + e.getValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#fileListPoped(k_kim_mg
	 * .sa4cob2db.event.ACMLengthChangedEvent)
	 */
	@Override
	public void fileListPoped(ACMSessionEvent e) {
		System.err.println("Poped:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#fileListPushed(k_kim_mg
	 * .sa4cob2db.event.ACMLengthChangedEvent)
	 */
	@Override
	public void fileListPushed(ACMSessionEvent e) {
		System.err.println("Pushed:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#lengthChanged(k_kim_mg
	 * .sa4cob2db.event.ACMLengthChangedEvent)
	 */
	@Override
	public void lengthChanged(ACMLengthChangedEvent e) {
		System.err.println("Length Changed:" + e.getOldLength() + "->" + e.getNewLength());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#transactionCommited(k_kim_mg
	 * .sa4cob2db.event.ACMSessionEvent)
	 */
	@Override
	public void transactionCommited(ACMSessionEvent e) {
		System.err.println("Session Commited.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventAdapter#transactionRollbacked
	 * (k_kim_mg.sa4cob2db.event.ACMSessionEvent)
	 */
	@Override
	public void transactionRollbacked(ACMSessionEvent e) {
		System.err.println("Session Rollbacked.");
	}
}
