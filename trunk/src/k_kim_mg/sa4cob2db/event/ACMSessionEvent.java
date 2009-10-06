package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolFile;
/**
 * ACMSession�˴ؤ��륤�٥��
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSessionEvent extends EventObject {
	/**
	 * ����ˤǤ���
	 */
	private static final long serialVersionUID = 1L;
	private ACMSession session;
	private CobolFile file;
	/**
	 * ���󥹥ȥ饯��
	 * @param source	���å����
	 * @param file		�ե�����
	 */
	public ACMSessionEvent(ACMSession source, CobolFile file) {
		super(source);
		this.session = source;
		this.file = file;
	}
	/**
	 * ���٥��ȯ�����Υե�����
	 * @return ���٥��ȯ�����Υե�����
	 */
	public CobolFile getFile() {
		return file;
	}
	/**
	 * ���٥��ȯ�����Υ��å����
	 * @return ���٥��ȯ�����Υ��å����(source������)
	 */
	public ACMSession getSession() {
		return session;
	}
}
