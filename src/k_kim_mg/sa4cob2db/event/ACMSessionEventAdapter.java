package k_kim_mg.sa4cob2db.event;

/**
 * default implement of ACMSessionEventListener
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSessionEventAdapter implements ACMSessionEventListener {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#fileCreated(k_kim_mg
	 * .sa4cob2db.event. ACMSessionEvent)
	 */
	@Override
	public void fileCreated(ACMSessionEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#fileDestroyed(k_kim_mg
	 * .sa4cob2db.event. ACMSessionEvent)
	 */
	@Override
	public void fileDestroyed(ACMSessionEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#fileListPoped(k_kim_mg
	 * .sa4cob2db.event.ACMLengthChangedEvent)
	 */
	@Override
	public void fileListPoped(ACMSessionEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#fileListPushed(k_kim_mg
	 * .sa4cob2db.event.ACMLengthChangedEvent)
	 */
	@Override
	public void fileListPushed(ACMSessionEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#lengthChanged(k_kim_mg
	 * .sa4cob2db.event. ACMLengthChangedEvent)
	 */
	@Override
	public void lengthChanged(ACMLengthChangedEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#optionSetted(k_kim_mg
	 * .sa4cob2db.event. ACMOptionSetEvent)
	 */
	@Override
	public void optionSetted(ACMOptionSetEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#transactionCommited(
	 * k_kim_mg.sa4cob2db.event .ACMSessionEvent)
	 */
	@Override
	public void transactionCommited(ACMSessionEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener#transactionRollbacked
	 * (k_kim_mg.sa4cob2db.event .ACMSessionEvent)
	 */
	@Override
	public void transactionRollbacked(ACMSessionEvent e) {
	}
}
