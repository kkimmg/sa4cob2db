package k_kim_mg.sa4cob2db.event;

import java.util.EventObject;

import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.FileStatus;
/**
 * コボルファイルのイベント
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CobolFileEvent extends EventObject {

	/** 自動でできた */
	private static final long serialVersionUID = 1L;
	/** ファイル */
	private transient CobolFile file = null;
	/** ファイルステータス */
	private transient FileStatus status;
	/**
	 * Constructor
	 * @param source	イベントの発生したCobolFileのオブジェクト
	 */
	public CobolFileEvent(CobolFile source, FileStatus status) {
		super(source);
		this.file = source;
		this.status = status;
	}
	/**
	 * sourceでキャストを咲けたい場合
	 * @return sourceと同等のファイル	
	 */
	public CobolFile getFile () {
		return file;
	}
	/**
	 * ファイルステータス
	 * @return ファイルステータス
	 */
	public FileStatus getStatus() {
		return status;
	}
	/**
	 * ファイルステータス
	 * @param status ファイルステータス
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
