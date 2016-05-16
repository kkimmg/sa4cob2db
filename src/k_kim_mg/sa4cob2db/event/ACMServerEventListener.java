package k_kim_mg.sa4cob2db.event;

import k_kim_mg.sa4cob2db.ACMSession;

/**
 * catch server event.
 * 
 * @author kenji
 */
public interface ACMServerEventListener {
	/**
	 * server started.
	 * 
	 * @param e event
	 */
	public void serverStarted(ACMServerEvent e);

	/**
	 * server ending.
	 * 
	 * @param e event
	 */
	public void serverEnding(ACMServerEvent e);

	/**
	 * session added.
	 * 
	 * @param e event
	 * @param session added session
	 */
	public void sessionAdded(ACMServerEvent e, ACMSession session);

	/**
	 * session removed.
	 * 
	 * @param e event
	 * @param session removed session
	 */
	public void sessionRemoved(ACMServerEvent e, ACMSession session);
}
