package k_kim_mg.sa4cob2db;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
/**
 * �᥿�ǡ����ΰ���
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolRecordMetaDataSet {
	/** �᥿�ǡ����ΰ��� */
	protected List<CobolRecordMetaData> metas = new ArrayList<CobolRecordMetaData>();
	/**
	 * �᥿�ǡ�����̾���ΰ�����
	 */
	protected Hashtable<String, CobolRecordMetaData> names = new Hashtable<String, CobolRecordMetaData>();
	/**
	 * ���ܥ�ե�����κ���
	 * @param meta �᥿�ǡ���
	 * @return ���ܥ�ե�����
	 */
	protected abstract CobolFile createCobolFile(CobolRecordMetaData meta);
	/**
	 * ���ܥ�ե�����μ���
	 * @param name �᥿�ǡ���̾
	 * @return ���ܥ�ե�����
	 */
	public CobolFile getCobolFile(String name) {
		CobolRecordMetaData meta = names.get(name);
		if (meta != null) {
			return createCobolFile(meta);
		}
		return null;
	}
	/**
	 * �᥿�ǡ����μ���
	 * @param name �᥿�ǡ���̾
	 * @return �᥿�ǡ���
	 */
	public CobolRecordMetaData getMetaData(String name) {
		CobolRecordMetaData meta = (CobolRecordMetaData) names.get(name);
		return meta;
	}
	/**
	 * �᥿�ǡ����μ���
	 * @param i i���ܤΤ᤿�ǡ������������
	 * @return �᥿�ǡ���
	 */
	public CobolRecordMetaData getMetaData(int i) {
		return (CobolRecordMetaData) metas.get(i);
	}
	/** ��Ͽ���줿�᥿�ǡ����ο� */
	public int getMetaDataCount() {
		return metas.size();
	}
	/**
	 * �᥿�ǡ�������Ͽ����
	 * @param meta �᥿�ǡ���
	 */
	public void installMetaData(CobolRecordMetaData meta) {
		// ��Ͽ
		metas.add(meta);
		// ̾������Ͽ
		names.put(meta.getName(), meta);
		// ��̾����Ͽ
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.put(meta.getAlias(i), meta);
		}
	}
	/**
	 * �᥿�ǡ�����������
	 * @param meta �᥿�ǡ���
	 */
	public void removeMetaData(CobolRecordMetaData meta) {
		// ���
		metas.remove(meta);
		// ̾���Ǻ��
		names.remove(meta.getName());
		// ��̾�Ǻ��
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.remove(meta.getAlias(i));
		}
	}
}
