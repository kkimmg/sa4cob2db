package k_kim_mg.sa4cob2db.codegen;

/**
 * �������������Υե�����ξ�����ݻ����륪�֥�������
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface FileInfo {
	/**
	 * ���������⡼��
	 * @return	���������⡼��
	 */
	public int getAcessMode();
	/**
	 * �����ե�����̾
	 * @return	�ե�����̾�ʳ����ե�����̾���� <br/>
	 * �������󤹤�ʥե����륷���ƥ��Ρ˥ե�����̾
	 */
	public String getFileName();
	/**
	 * �쥳����̾
	 * @return	�쥳����̾�� <br/>
	 * FD���01��٥���ΰ�̾
	 */
	public String getRecordName();
	/**
	 * �����ե�����̾
	 * @return	�ե�����̾�������ե�����̾���� <br/>
	 * SELECT��μ��Υȡ�����
	 */
	public String getSelectName();
	/**
	 * �����Ͼ���
	 * @return	�����Ͼ��� <br/>
	 * �����Ͼ��֤򼨤��ΰ�̾(File Status [is] XXXXXX)
	 */
	public String getStatus();
}
