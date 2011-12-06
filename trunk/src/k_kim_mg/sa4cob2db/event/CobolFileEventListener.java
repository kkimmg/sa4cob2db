package k_kim_mg.sa4cob2db.event;
import java.util.EventListener;
/**
 * listener interface
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobolFileEventListener extends EventListener {
	/**
	 * after CLOSE
	 * @param e CobolFile
	 */
	public void postClose(CobolFileEvent e);
	/**
	 * after DELETE
	 * @param e CobolFile
	 */
	public void postDelete(CobolFileEvent e);
	/**
	 * after READ
	 * @param e CobolFile
	 */
	public void postMove(CobolFileEvent e);
	/**
	 * after NEXT
	 * @param e CobolFile
	 */
	public void postNext(CobolFileEvent e);
	/**
	 * after OPEN
	 * @param e CobolFile
	 */
	public void postOpen(CobolFileEvent e);
	/**
	 * after PREVIOUS
	 * @param e CobolFile
	 */
	public void postPrevious(CobolFileEvent e);
	/**
	 * after READ
	 * @param e CobolFile
	 * @param record reading record
	 */
	public void postRead(CobolFileEvent e, byte[] record);
	/**
	 * after REWRITE
	 * @param e CobolFile
	 */
	public void postRewrite(CobolFileEvent e);
	/**
	 * after START
	 * @param e CobolFile
	 */
	public void postStart(CobolFileEvent e);
	/**
	 * after START
	 * @param e CobolFile
	 * @param indexname index name
	 */
	public void postStart(CobolFileEvent e, String indexname);
	/**
	 * after WRITE
	 * @param e CobolFile
	 */
	public void postWrite(CobolFileEvent e);
	/**
	 * before CLOSE
	 * @param e CobolFile
	 */
	public void preClose(CobolFileEvent e);
	/**
	 * before DELETE
	 * @param e CobolFile
	 */
	public void preDelete(CobolFileEvent e);
	/**
	 * before READ
	 * @param e CobolFile
	 * @param record key record
	 */
	public void preMove(CobolFileEvent e, byte[] record);
	/**
	 * before NEXT
	 * @param e CobolFile
	 */
	public void preNext(CobolFileEvent e);
	/**
	 * before OPEN
	 * @param e CobolFile
	 */
	public void preOpen(CobolFileEvent e);
	/**
	 * before PREVIOUS
	 * @param e CobolFile
	 */
	public void prePrevious(CobolFileEvent e);
	/**
	 * before READ
	 * @param e CobolFile
	 */
	public void preRead(CobolFileEvent e);
	/**
	 * before REWRITE
	 * @param e CobolFile
	 * @param record writing record
	 */
	public void preRewrite(CobolFileEvent e, byte[] record);
	/**
	 * before START
	 * @param e CobolFile
	 * @param record key record
	 */
	public void preStart(CobolFileEvent e, byte[] record);
	/**
	 * before START
	 * @param e CobolFile
	 * @param indexname indexname
	 * @param record key record
	 */
	public void preStart(CobolFileEvent e, String indexname, byte[] record);
	/**
	 * before WRITE
	 * @param e CobolFile
	 * @param record writing record
	 */
	public void preWrite(CobolFileEvent e, byte[] record);
}
