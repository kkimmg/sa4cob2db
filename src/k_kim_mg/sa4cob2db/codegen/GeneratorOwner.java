/*
 * Created on 2004/05/23
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.codegen;

import java.util.ArrayList;

/**
 * CodeGenerator��ƤӽФ���ǽ
 * @author ���줪��
 */
public interface GeneratorOwner {
	/**
	 * �����������ɤ���������
	 * @param text	�������륽����������
	 */
	public void generate(String text);
	/**
	 * ���ԡ��礬�褿�ΤǤʤ�Ȥ����Ƥ�������
	 * @param statement	���ԡ���
	 */
	public void callBackCopyStatement (ArrayList<String> statement);
	/**
	 * ���ԡ����Ÿ�����뤫�ɤ���
	 * @return true	Ÿ������</br>
	 *          false	Ÿ�����ʤ�
	 */
	public boolean isExpandCopy();
}
