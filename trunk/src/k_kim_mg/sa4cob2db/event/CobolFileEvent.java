package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.FileStatus;
/**
 * ���ܥ�ե�����Υ��٥��
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CobolFileEvent extends EventObject {

	/** ��ư�ǤǤ��� */
	private static final long serialVersionUID = 1L;
	/** �ե����� */
	private transient CobolFile file = null;
	/** �ե����륹�ơ����� */
	private transient FileStatus status;
	/**
	 * ���󥹥ȥ饯��
	 * @param source	���٥�Ȥ�ȯ������CobolFile�Υ��֥�������
	 */
	public CobolFileEvent(CobolFile source, FileStatus status) {
		super(source);
		this.file = source;
		this.status = status;
	}
	/**
	 * source�ǥ��㥹�Ȥ�餱�������
	 * @return source��Ʊ���Υե�����	
	 */
	public CobolFile getFile () {
		return file;
	}
	/**
	 * �ե����륹�ơ�����
	 * @return �ե����륹�ơ�����
	 */
	public FileStatus getStatus() {
		return status;
	}
	/**
	 * �ե����륹�ơ�����
	 * @param status �ե����륹�ơ�����
	 */
	public void setStatus(FileStatus status) {
		this.status = status;
	}
	/* (non-Javadoc)
     * @see java.util.EventObject#toString()
     */
    @Override
    public String toString() {
	    return file.getMetaData().getName() + ":" + this.status.toString();
    }
}
