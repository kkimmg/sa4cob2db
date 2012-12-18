package k_kim_mg.sa4cob2db;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
/**
 * メタデータの一覧
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolRecordMetaDataSet {
	/**
	 * メタデータの一覧
	 */
	private List<CobolRecordMetaData> list = new ArrayList<CobolRecordMetaData>();
	/**
	 * メタデータ（名前の一覧）
	 */
	protected Hashtable<String, CobolRecordMetaData> names = new Hashtable<String, CobolRecordMetaData>();
	/**
	 * コボルファイルの作成
	 * 
	 * @param meta メタデータ
	 * @return コボルファイル
	 */
	protected abstract CobolFile createCobolFile(CobolRecordMetaData meta);
	/**
	 * コボルファイルの取得
	 * 
	 * @param name メタデータ名
	 * @return コボルファイル
	 */
	public CobolFile getCobolFile(String name) {
		CobolFile ret = null;
		CobolRecordMetaData meta = names.get(name);
		if (meta != null) {
			ret = createCobolFile(meta);
		}
		return ret;
	}
	/**
	 * メタデータの取得
	 * 
	 * @param name メタデータ名
	 * @return メタデータ
	 */
	public CobolRecordMetaData getMetaData(String name) {
		CobolRecordMetaData meta = (CobolRecordMetaData) names.get(name);
		return meta;
	}
	/**
	 * get MetaData
	 * @param i get to i'th MetaData
	 * @return MetaData
	 */
	public CobolRecordMetaData get( int i ) {
		return list.get(i);
	}
	/**
	 * Size
	 * @return size
	 */
	public int size() {
		return list.size();
	}
	/**
	 * メタデータを登録する
	 * 
	 * @param meta メタデータ
	 */
	public boolean installMetaData(CobolRecordMetaData meta) {
		// 登録
		boolean ret = list.add(meta);
		// 名前で登録
		names.put(meta.getName(), meta);
		// 別名で登録
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.put(meta.getAlias(i), meta);
		}
		return ret;
	}
	/**
	 * メタデータを削除する
	 * 
	 * @param meta メタデータ
	 */
	public boolean removeMetaData(CobolRecordMetaData meta) {
		// 削除
		boolean ret = list.remove(meta);
		// 名前で削除
		names.remove(meta.getName());
		// 別名で削除
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.remove(meta.getAlias(i));
		}
		return ret;
	}
	/**
	 * returns ListIterator
	 * @return list iterator
	 */
	public ListIterator<CobolRecordMetaData> listIterator () {
		return list.listIterator();
	}
	/**
	 * to array
	 * @return array
	 */
	public CobolRecordMetaData[] toArray() {
		return list.toArray(new CobolRecordMetaData[size()]);
	}
}
