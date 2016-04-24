package k_kim_mg.sa4cob2db.test;

import k_kim_mg.sa4cob2db.event.CobolFileEvent;
import k_kim_mg.sa4cob2db.event.CobolFileEventAdapter;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;

/**
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CobolFileEventAdapterTest extends CobolFileEventAdapter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postClose(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postClose(CobolFileEvent e) {
		SQLNetServer.logger.finest("postClose(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postDelete(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postDelete(CobolFileEvent e) {
		SQLNetServer.logger.finest("postDelete(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postMove(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postMove(CobolFileEvent e) {
		SQLNetServer.logger.finest("postMove(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postNext(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postNext(CobolFileEvent e) {
		SQLNetServer.logger.finest("postNext(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postOpen(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postOpen(CobolFileEvent e) {
		SQLNetServer.logger.finest("postOpen(" + e + ")");
		// CobolFile file = e.getFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postPrevious(k_kim_mg.
	 * sa4cob2db.event.CobolFileEvent)
	 */
	@Override
	public void postPrevious(CobolFileEvent e) {
		SQLNetServer.logger.finest("postPrevious(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postRead(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent, byte[])
	 */
	@Override
	public void postRead(CobolFileEvent e, byte[] record) {
		SQLNetServer.logger.finest("postRead(e, record)" + new String(record).trim() + ":");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postRewrite(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postRewrite(CobolFileEvent e) {
		SQLNetServer.logger.finest("postRewrite(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postStart(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postStart(CobolFileEvent e) {
		SQLNetServer.logger.finest("postStart(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postStart(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent, java.lang.String)
	 */
	@Override
	public void postStart(CobolFileEvent e, String indexname) {
		SQLNetServer.logger.finest("postStart(" + e + ":" + indexname + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#postWrite(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void postWrite(CobolFileEvent e) {
		SQLNetServer.logger.finest("postWrite(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preClose(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void preClose(CobolFileEvent e) {
		SQLNetServer.logger.finest("preClose(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preDelete(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void preDelete(CobolFileEvent e) {
		SQLNetServer.logger.finest("preDelete(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preMove(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent, byte[])
	 */
	@Override
	public void preMove(CobolFileEvent e, byte[] record) {
		SQLNetServer.logger.finest("preMove(e, record)" + new String(record).trim() + ":");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preNext(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void preNext(CobolFileEvent e) {
		SQLNetServer.logger.finest("preNext(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preOpen(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void preOpen(CobolFileEvent e) {
		SQLNetServer.logger.finest("preOpen(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#prePrevious(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void prePrevious(CobolFileEvent e) {
		SQLNetServer.logger.finest("prePrevious(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preRead(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent)
	 */
	@Override
	public void preRead(CobolFileEvent e) {
		SQLNetServer.logger.finest("preRead(" + e + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preRewrite(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent, byte[])
	 */
	@Override
	public void preRewrite(CobolFileEvent e, byte[] record) {
		SQLNetServer.logger.finest("preRewrite(" + e + ")" + ", record)" + new String(record).trim() + ":");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preStart(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent, byte[])
	 */
	@Override
	public void preStart(CobolFileEvent e, byte[] record) {
		SQLNetServer.logger.finest("preStart(" + e + ")" + ", record)" + new String(record).trim() + ":");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preStart(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent, java.lang.String, byte[])
	 */
	@Override
	public void preStart(CobolFileEvent e, String indexname, byte[] record) {
		SQLNetServer.logger.finest("preStart(" + e + new String(record).trim() + ":" + indexname + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.event.CobolFileEventAdapter#preWrite(k_kim_mg.sa4cob2db
	 * .event.CobolFileEvent, byte[])
	 */
	@Override
	public void preWrite(CobolFileEvent e, byte[] record) {
		SQLNetServer.logger.finest("preWrite(" + e + ")" + ", record)" + new String(record).trim() + ":");
	}
}
