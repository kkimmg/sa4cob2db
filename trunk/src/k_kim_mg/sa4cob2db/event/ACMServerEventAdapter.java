/**
 * 
 */
package k_kim_mg.sa4cob2db.event;
import k_kim_mg.sa4cob2db.ACMSession;
/**
 * ACMServerEventListenerのデフォルト実装<br/>
 * 実際には何もしない
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMServerEventAdapter implements ACMServerEventListener {
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMServerEventListener#serverEnding
	 * (k_kim_mg.sa4cob2db.event.ACMServerEvent)
	 */
	public void serverEnding(ACMServerEvent e) {
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMServerEventListener#serverStarted
	 * (k_kim_mg.sa4cob2db.event.ACMServerEvent)
	 */
	public void serverStarted(ACMServerEvent e) {
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMServerEventListener#sessionAdded
	 * (k_kim_mg.sa4cob2db.event.ACMServerEvent,
	 * k_kim_mg.sa4cob2db.ACMSession)
	 */
	public void sessionAdded(ACMServerEvent e, ACMSession session) {
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.event.ACMServerEventListener#sessionRemoved
	 * (k_kim_mg.sa4cob2db.event.ACMServerEvent,
	 * k_kim_mg.sa4cob2db.ACMSession)
	 */
	public void sessionRemoved(ACMServerEvent e, ACMSession session) {
	}
}
