package k_kim_mg.sa4cob2db.test;
import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.event.ACMServerEvent;
import k_kim_mg.sa4cob2db.event.ACMServerEventAdapter;
/**
 * test print methods
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMServerEventAdapterTest extends ACMServerEventAdapter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#serverEnding(k_kim_mg.sa4cob2db.event.ACMServerEvent)
	 */
	@Override
	public void serverEnding(ACMServerEvent e) {
		System.err.println("serverEnding(e)");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#serverStarted(k_kim_mg.sa4cob2db.event.ACMServerEvent)
	 */
	@Override
	public void serverStarted(ACMServerEvent e) {
		System.err.println("serverStarted(e)");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#sessionAdded(k_kim_mg.sa4cob2db.event.ACMServerEvent,
	 *      k_kim_mg.sa4cob2db.ACMSession)
	 */
	@Override
	public void sessionAdded(ACMServerEvent e, ACMSession session) {
		System.err.println("sessionAdded(e, session)");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.event.ACMServerEventAdapter#sessionRemoved(k_kim_mg.sa4cob2db.event.ACMServerEvent,
	 *      k_kim_mg.sa4cob2db.ACMSession)
	 */
	@Override
	public void sessionRemoved(ACMServerEvent e, ACMSession session) {
		System.err.println("sessionRemoved(e, session)");
	}
}
