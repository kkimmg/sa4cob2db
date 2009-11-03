package k_kim_mg.sa4cob2db.codegen;
import java.util.EventListener;
/**
 * コード生成持のイベント
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CodeGeneratorListener extends EventListener {
	/**
	 * CLOSEの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postClose(CodeGeneratorEvent e);
	/**
	 * COMMITの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postCommit(CodeGeneratorEvent e);
	/**
	 * DELETEの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postDelete(CodeGeneratorEvent e);
	/**
	 * 初期化処理の後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postInitialize(CodeGeneratorEvent e);
	/**
	 * READの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postMoveRead(CodeGeneratorEvent e);
	/**
	 * OPENの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postOpen(CodeGeneratorEvent e);
	/**
	 * READ NEXTの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postReadNext(CodeGeneratorEvent e);
	/**
	 * REWRITEの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postRewrite(CodeGeneratorEvent e);
	/**
	 * ROLLBACKの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postRollback(CodeGeneratorEvent e);
	/**
	 * STARTの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postStart(CodeGeneratorEvent e);
	/**
	 * 終了処理の後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postTerminate(CodeGeneratorEvent e);
	/**
	 * WRITEの後
	 * @param e ジェネレータで発生したイベント
	 */
	public void postWrite(CodeGeneratorEvent e);
	/**
	 * CLOSEの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preClose(CodeGeneratorEvent e);
	/**
	 * COMMITの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preCommit(CodeGeneratorEvent e);
	/**
	 * DELETEの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preDelete(CodeGeneratorEvent e);
	/**
	 * 初期化処理の前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preInitialize(CodeGeneratorEvent e);
	/**
	 * READの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preMoveRead(CodeGeneratorEvent e);
	/**
	 * OPENの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preOpen(CodeGeneratorEvent e);
	/**
	 * READ NEXTの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preReadNext(CodeGeneratorEvent e);
	/**
	 * REWRITEの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preRewrite(CodeGeneratorEvent e);
	/**
	 * ROLLBACKの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preRollback(CodeGeneratorEvent e);
	/**
	 * STARTの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preStart(CodeGeneratorEvent e);
	/**
	 * 終了処理の前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preTerminate(CodeGeneratorEvent e);
	/**
	 * WRITEの前
	 * @param e ジェネレータで発生したイベント
	 */
	public void preWrite(CodeGeneratorEvent e);
}
