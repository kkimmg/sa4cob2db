package k_kim_mg.sa4cob2db.event;
import java.util.EventListener;
/**
 * ���ܥ�ե�����Υ���������ȯ���������٥�Ȥ�������뵡ǽ
 * @author ���줪��
 */
public interface CobolFileEventListener extends EventListener {
	/**
	 * CLOSE�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postClose(CobolFileEvent e);
	/**
	 * DELETE�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postDelete(CobolFileEvent e);
	/**
	 * READ�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postMove(CobolFileEvent e);
	/**
	 * NEXT�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postNext(CobolFileEvent e);
	/**
	 * OPEN�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postOpen(CobolFileEvent e);
	/**
	 * PREVIOUS�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postPrevious(CobolFileEvent e);
	/**
	 * READ�θ�
	 * @param e CobolFile��ȯ���������٥��
	 * @param record �ɤ߹�����쥳����
	 */
	public void postRead(CobolFileEvent e, byte[] record);
	/**
	 * REWRITE�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postRewrite(CobolFileEvent e);
	/**
	 * START�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postStart(CobolFileEvent e);
	/**
	 * START�θ�
	 * @param e CobolFile��ȯ���������٥��
	 * @param indexname ����ǥå�����̾��
	 */
	public void postStart(CobolFileEvent e, String indexname);
	/**
	 * WRITE�θ�
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void postWrite(CobolFileEvent e);
	/**
	 * CLOSE����
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void preClose(CobolFileEvent e);
	/**
	 * DELETE����
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void preDelete(CobolFileEvent e);
	/**
	 * READ����
	 * @param e CobolFile��ȯ���������٥��
	 * @param record �����쥳����
	 */
	public void preMove(CobolFileEvent e, byte[] record);
	/**
	 * NEXT����
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void preNext(CobolFileEvent e);
	/**
	 * OPEN����
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void preOpen(CobolFileEvent e);
	/**
	 * PREVIOUS����
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void prePrevious(CobolFileEvent e);
	/**
	 * READ����
	 * @param e CobolFile��ȯ���������٥��
	 */
	public void preRead(CobolFileEvent e);
	/**
	 * REWRITE����
	 * @param e CobolFile��ȯ���������٥��
	 * @param record �񤭹��ߥ쥳����
	 */
	public void preRewrite(CobolFileEvent e, byte[] record);
	/**
	 * START����
	 * @param e CobolFile��ȯ���������٥��
	 * @param record �����쥳����
	 */
	public void preStart(CobolFileEvent e, byte[] record);
	/**
	 * START����
	 * @param e CobolFile��ȯ���������٥��
	 * @param indexname ����ǥå�����̾��
	 * @param record �����쥳����
	 */
	public void preStart(CobolFileEvent e, String indexname, byte[] record);
	/**
	 * WRITE����
	 * @param e CobolFile��ȯ���������٥��
	 * @param record �񤭹��ߥ쥳����
	 */
	public void preWrite(CobolFileEvent e, byte[] record);
}
