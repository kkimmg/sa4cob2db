package k_kim_mg.sa4cob2db;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
/**
 * meta dataの一覧
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolRecordMetaDataSet {
	/**
	 * meta dataの一覧
	 */
	private List<CobolRecordMetaData> list = new ArrayList<CobolRecordMetaData>();
	/**
	 * meta data（名前の一覧）
	 */
	protected Hashtable<String, CobolRecordMetaData> names = new Hashtable<String, CobolRecordMetaData>();
	/**
	 * cobol fileの作成
	 * 
	 * @param meta meta data
	 * @return cobol file
	 */
	protected abstract CobolFile createCobolFile(CobolRecordMetaData meta);
	/**
	 * cobol fileの取得
	 * 
	 * @param name meta data名
	 * @return cobol file
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
	 * meta dataの取得
	 * 
	 * @param name meta data名
	 * @return meta data
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
	 * meta dataを登録する
	 * 
	 * @param meta meta data
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
	 * meta dataを削除する
	 * 
	 * @param meta meta data
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
