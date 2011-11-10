package k_kim_mg.sa4cob2db.codegen;
import java.util.EventListener;
/**
 * listener of CodeGeneratorEvent
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CodeGeneratorListener extends EventListener {
	/**
	 * After CLOSE
	 * @param e Event
	 */
	public void postClose(CodeGeneratorEvent e);
	/**
	 * After COMMIT
	 * @param e Event
	 */
	public void postCommit(CodeGeneratorEvent e);
	/**
	 * After DELETE
	 * @param e Event
	 */
	public void postDelete(CodeGeneratorEvent e);
	/**
	 * After Initialize
	 * @param e Event
	 */
	public void postInitialize(CodeGeneratorEvent e);
	/**
	 * After READ
	 * @param e Event
	 */
	public void postMoveRead(CodeGeneratorEvent e);
	/**
	 * After OPEN
	 * @param e Event
	 */
	public void postOpen(CodeGeneratorEvent e);
	/**
	 * After READ NEXT
	 * @param e Event
	 */
	public void postReadNext(CodeGeneratorEvent e);
	/**
	 * After REWRITE
	 * @param e Event
	 */
	public void postRewrite(CodeGeneratorEvent e);
	/**
	 * After ROLLBACK
	 * @param e Event
	 */
	public void postRollback(CodeGeneratorEvent e);
	/**
	 * After START
	 * @param e Event
	 */
	public void postStart(CodeGeneratorEvent e);
	/**
	 * After Terminate
	 * @param e Event
	 */
	public void postTerminate(CodeGeneratorEvent e);
	/**
	 * After WRITE
	 * @param e Event
	 */
	public void postWrite(CodeGeneratorEvent e);
	/**
	 * Before CLOSE
	 * @param e Event
	 */
	public void preClose(CodeGeneratorEvent e);
	/**
	 * Before COMMIT
	 * @param e Event
	 */
	public void preCommit(CodeGeneratorEvent e);
	/**
	 * Before DELETE
	 * @param e Event
	 */
	public void preDelete(CodeGeneratorEvent e);
	/**
	 * Before Initialize
	 * @param e Event
	 */
	public void preInitialize(CodeGeneratorEvent e);
	/**
	 * Before READ
	 * @param e Event
	 */
	public void preMoveRead(CodeGeneratorEvent e);
	/**
	 * Before OPEN
	 * @param e Event
	 */
	public void preOpen(CodeGeneratorEvent e);
	/**
	 * Before READ NEXT
	 * @param e Event
	 */
	public void preReadNext(CodeGeneratorEvent e);
	/**
	 * Before REWRITE
	 * @param e Event
	 */
	public void preRewrite(CodeGeneratorEvent e);
	/**
	 * Before ROLLBACK
	 * @param e Event
	 */
	public void preRollback(CodeGeneratorEvent e);
	/**
	 * Before START
	 * @param e Event
	 */
	public void preStart(CodeGeneratorEvent e);
	/**
	 * Before Terminate
	 * @param e Event
	 */
	public void preTerminate(CodeGeneratorEvent e);
	/**
	 * Before WRITE
	 * @param e Event
	 */
	public void preWrite(CodeGeneratorEvent e);
}
