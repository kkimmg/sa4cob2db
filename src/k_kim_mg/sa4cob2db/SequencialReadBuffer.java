package k_kim_mg.sa4cob2db;

/**
 * �������󥷥��ե�������ɤ߹��ߥХåե�
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface SequencialReadBuffer {
	/**
	 * �Хåե����֤��ѹ�����
	 * @return �ե����륹�ơ�����
	 */
	public FileStatus nextBuffer ();
	/**
	 * �Хåե����ɤ߹���<br/>
	 * @param record	�쥳����
	 * @return	�ե����륹�ơ�����
	 */
	public FileStatus readBuffer (byte[] record);
	/**
	 * �Хåե���󥰤γ���
	 */
	public void startBuffering ();
}
