package k_kim_mg.sa4cob2db.codegen;
/**
 * �����������ɤ��������륤�󥿡��ե�����
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CodeGenerator {
	/**
	 * �����������ɤ���������
	 * @param row	��
	 */
	public void parse(String row);
	/**
	 * ���ꥢ����
	 * ���Ѥ����Ԥ򥪡��ʡ��������Ϥ��ƥ��ꥢ����
	 */
	public void clear();
	/**
	 * �������������Υ��٥�Ƚ������ɲä���
	 * @param listener	�������������Υ��٥�Ƚ���
	 */
	public void addCodeGeneratorListener(CodeGeneratorListener listener);
}
