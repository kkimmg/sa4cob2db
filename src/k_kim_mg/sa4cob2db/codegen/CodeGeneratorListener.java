package k_kim_mg.sa4cob2db.codegen;
import java.util.EventListener;
/**
 * �������������Υ��٥��
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CodeGeneratorListener extends EventListener {
	/**
	 * CLOSE�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postClose(CodeGeneratorEvent e);
	/**
	 * COMMIT�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postCommit(CodeGeneratorEvent e);
	/**
	 * DELETE�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postDelete(CodeGeneratorEvent e);
	/**
	 * ����������θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postInitialize(CodeGeneratorEvent e);
	/**
	 * READ�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postMoveRead(CodeGeneratorEvent e);
	/**
	 * OPEN�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postOpen(CodeGeneratorEvent e);
	/**
	 * READ NEXT�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postReadNext(CodeGeneratorEvent e);
	/**
	 * REWRITE�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postRewrite(CodeGeneratorEvent e);
	/**
	 * ROLLBACK�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postRollback(CodeGeneratorEvent e);
	/**
	 * START�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postStart(CodeGeneratorEvent e);
	/**
	 * ��λ�����θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postTerminate(CodeGeneratorEvent e);
	/**
	 * WRITE�θ�
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void postWrite(CodeGeneratorEvent e);
	/**
	 * CLOSE����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preClose(CodeGeneratorEvent e);
	/**
	 * COMMIT����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preCommit(CodeGeneratorEvent e);
	/**
	 * DELETE����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preDelete(CodeGeneratorEvent e);
	/**
	 * �������������
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preInitialize(CodeGeneratorEvent e);
	/**
	 * READ����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preMoveRead(CodeGeneratorEvent e);
	/**
	 * OPEN����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preOpen(CodeGeneratorEvent e);
	/**
	 * READ NEXT����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preReadNext(CodeGeneratorEvent e);
	/**
	 * REWRITE����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preRewrite(CodeGeneratorEvent e);
	/**
	 * ROLLBACK����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preRollback(CodeGeneratorEvent e);
	/**
	 * START����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preStart(CodeGeneratorEvent e);
	/**
	 * ��λ��������
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preTerminate(CodeGeneratorEvent e);
	/**
	 * WRITE����
	 * @param e �����ͥ졼����ȯ���������٥��
	 */
	public void preWrite(CodeGeneratorEvent e);
}
