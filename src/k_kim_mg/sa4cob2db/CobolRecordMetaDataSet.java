package k_kim_mg.sa4cob2db;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
/**
 * メタデータの一覧
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolRecordMetaDataSet {
	/** メタデータの一覧 */
	protected List<CobolRecordMetaData> metas = new ArrayList<CobolRecordMetaData>();
	/**
	 * メタデータ（名前の一覧）
	 */
	protected Hashtable<String, CobolRecordMetaData> names = new Hashtable<String, CobolRecordMetaData>();
	/**
	 * コボルファイルの作成
	 * @param meta メタデータ
	 * @return コボルファイル
	 */
	protected abstract CobolFile createCobolFile(CobolRecordMetaData meta);
	/**
	 * コボルファイルの取得
	 * @param name メタデータ名
	 * @return コボルファイル
	 */
	public CobolFile getCobolFile(String name) {
		CobolRecordMetaData meta = names.get(name);
		if (meta != null) {
			return createCobolFile(meta);
		}
		return null;
	}
	/**
	 * メタデータの取得
	 * @param name メタデータ名
	 * @return メタデータ
	 */
	public CobolRecordMetaData getMetaData(String name) {
		CobolRecordMetaData meta = (CobolRecordMetaData) names.get(name);
		return meta;
	}
	/**
	 * メタデータの取得
	 * @param i i番目のめたデータを取得する
	 * @return メタデータ
	 */
	public CobolRecordMetaData getMetaData(int i) {
		return (CobolRecordMetaData) metas.get(i);
	}
	/** 登録されたメタデータの数 */
	public int getMetaDataCount() {
		return metas.size();
	}
	/**
	 * メタデータを登録する
	 * @param meta メタデータ
	 */
	public void installMetaData(CobolRecordMetaData meta) {
		// 登録
		metas.add(meta);
		// 名前で登録
		names.put(meta.getName(), meta);
		// 別名で登録
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.put(meta.getAlias(i), meta);
		}
	}
	/**
	 * メタデータを削除する
	 * @param meta メタデータ
	 */
	public void removeMetaData(CobolRecordMetaData meta) {
		// 削除
		metas.remove(meta);
		// 名前で削除
		names.remove(meta.getName());
		// 別名で削除
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.remove(meta.getAlias(i));
		}
	}
}
