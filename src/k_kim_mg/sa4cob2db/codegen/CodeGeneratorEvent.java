package k_kim_mg.sa4cob2db.codegen;

import java.util.EventObject;
/**
 * �����ͥ졼����ȯ���������٥��
 * @author ���줪��
 */
public class CodeGeneratorEvent extends EventObject {
	/**
	 * ��ࡩ
	 */
	private static final long serialVersionUID = 1L;
	/** �ե�����ξ��� */
	private transient FileInfo file;
	/** �����ʡ� */
	private transient GeneratorOwner owner;
	/** �������饹 */
	private transient CodeGenerator generator;
	/** �ԥꥪ�ɤ��ɲä��뤫�ɤ��� */
	private String period;
	/**
	 * ���󥹥ȥ饯��
	 * @param source	�ե��������
	 * @param owner		�����ʡ�
	 * @param generator	�������饹
	 * @param period	�ԥꥪ�ɤ��ɲä��뤫�ɤ���
	 */
	public CodeGeneratorEvent (FileInfo source, GeneratorOwner owner, CodeGenerator generator, String period) {
		super(source);
		this.file = source;
		this.owner = owner;
		this.generator = generator;
		this.period = period;
	}
	/**
	 * �ե�����ξ���
	 * @return �ե�����ξ���
	 */
	public FileInfo getFile() {
		return file;
	}
	/**
	 * �����ͥ졼��
	 * @return �����ͥ졼��
	 */
	public CodeGenerator getGenerator() {
		return generator;
	}
	/**
	 * �����ͥ졼�������ʡ�
	 * @return �����ͥ졼�������ʡ�
	 */
	public GeneratorOwner getOwner() {
		return owner;
	}
	/**
	 * �ԥꥪ�ɤ��ɲä��뤫�ɤ���
	 * @return �ԥꥪ��ʸ����
	 */
	public String getPeriod() {
		return period;
	}
}
