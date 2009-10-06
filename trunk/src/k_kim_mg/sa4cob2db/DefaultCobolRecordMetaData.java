package k_kim_mg.sa4cob2db;
import java.util.ArrayList;
import java.util.List;

import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
/**
 * レコードメタデータ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class DefaultCobolRecordMetaData implements CobolRecordMetaData {
	/** リスナクラス */
	private List<Class<? extends CobolFileEventListener>> listenerClasses = new ArrayList<Class<? extends CobolFileEventListener>>();
	/** 別名 */
	protected List<String> aliases = new ArrayList<String>();
	/** 全ての列 */
	protected List<CobolColumn> columns = new ArrayList<CobolColumn>();
	/**
	 * レコードのエンコード
	 */
	protected String encode = "auto";
	/** 読み取りバッファ関連 */
	private int initialSequencialReadBufferSize, maximumSequencialReadBufferSize, minimumSequencialReadBufferSize;
	/** キー列の一覧 */
	protected List<CobolColumn> keys = new ArrayList<CobolColumn>();
	/** メタデータ名 */
	protected String name = "";
	/** インデクス情報 */
	protected List<CobolIndex> cobolIndexes = new ArrayList<CobolIndex>();
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#addAlias(java.lang
	 * .String)
	 */
	public void addAlias(String alias) {
		aliases.add(alias);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#addColumn(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void addColumn(CobolColumn column) {
		columns.add(column);
	}
	/**
	 * キー列の追加
	 * @param column 追加する列
	 */
	public void addKey(CobolColumn column) {
		keys.add(column);
	}
	/**
	 * 属性をコピーする
	 * @param copy コピー対象
	 * @return コピー対象(引数と同じオブジェクト)
	 */
	protected CobolRecordMetaData copyTo(CobolRecordMetaData copy) {
		copy.setName(getName());
		copy.setEncode(getEncode());
		int size = getColumnCount();
		for (int i = 0; i < size; i++) {
			CobolColumn work = getColumn(i);
			CobolColumn column = work.createCopy(copy);
			copy.addColumn(column);
			if (isKey(column)) {
				copy.addKey(column);
			}
		}
		return copy;
	}
	/**
	 * 列の作成
	 * @return 作成された列
	 */
	public CobolColumn createColumn() {
		CobolColumn ret = new DefaultCobolColumn(this);
		return ret;
	}
	// @Override
	public CobolRecordMetaData createCopy() {
		CobolRecordMetaData copy = new DefaultCobolRecordMetaData();
		copyTo(copy);
		return copy;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#findColumn(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn)
	 */
	public int findColumn(CobolColumn column) throws CobolRecordException {
		int ret = -1;
		int count = getColumnCount();
		for (int i = 0; i < count; i++) {
			if (column == getColumn(i)) {
				ret = i;
				break;
			}
		}
		if (ret < 0) {
			throw new CobolRecordException();
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#findColumn(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn, int)
	 */
	public int findColumn(CobolColumn column, int index) throws CobolRecordException {
		// TODO Auto-generated method stub
		return 0;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#findColumn(java.lang
	 * .String)
	 */
	public int findColumn(String name) throws CobolRecordException {
		int ret = -1;
		int count = getColumnCount();
		for (int i = 0; i < count; i++) {
			if (name.equalsIgnoreCase(getColumn(i).getName())) {
				ret = i;
				break;
			}
		}
		if (ret < 0) {
			throw new CobolRecordException();
		}
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getAlias()
	 */
	public String getAlias(int i) {
		return aliases.get(i);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getAliasCount()
	 */
	public int getAliasCount() {
		return aliases.size();
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getColumn(int)
	 */
	public CobolColumn getColumn(int i) {
		return (CobolColumn) columns.get(i);
	}
	public CobolColumn getColumn(String name) throws CobolRecordException {
		return getColumn(findColumn(name));
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getColumnCount()
	 */
	public int getColumnCount() {
		return columns.size();
	}
	/**
	 * エンコーディング
	 * @return エンコーディング
	 */
	public String getEncode() {
		return encode;
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * getInitialSequencialReadBufferSize()
	 */
	public int getInitialSequencialReadBufferSize() {
		return initialSequencialReadBufferSize;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getKey(int)
	 */
	public CobolColumn getKey(int i) {
		return keys.get(i);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getKeyCount()
	 */
	public int getKeyCount() {
		return keys.size();
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * getMaximumSequencialReadBufferSize()
	 */
	public int getMaximumSequencialReadBufferSize() {
		return maximumSequencialReadBufferSize;
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * getMinimumSequencialReadBufferSize()
	 */
	public int getMinimumSequencialReadBufferSize() {
		return minimumSequencialReadBufferSize;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getName()
	 */
	public String getName() {
		return name;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getRowSize()
	 */
	public int getRowSize() {
		// return getPhysicalLength();
		int ret = 0;
		for (int i = 0; i < getColumnCount(); i++) {
			int last = getColumn(i).getStart() + getColumn(i).getPhysicalLength();
			if (ret < last) {
				ret = last;
			}
			// SQLNetServer.DebugPrint(i + ":" +
			// getColumn(i).getPhysicalLength());
		}
		return ret;
	}
	/**
	 * キー列ですかー？
	 * @param column キーかも知れない列
	 * @return true はい</br> false いいえ
	 */
	protected boolean isKey(CobolColumn column) {
		return keys.contains(column);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#removeAlias(java.lang
	 * .String)
	 */
	public void removeAlias(String alias) {
		aliases.remove(alias);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#removeColumn(jp.ne
	 * .biglobe.mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void removeColumn(CobolColumn column) {
		columns.remove(column);
		removeKey(column);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#removeColumn(int)
	 */
	public void removeColumn(int index) {
		CobolColumn remove = getColumn(index);
		if (remove != null)
			columns.remove(index);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#removeKey(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void removeKey(CobolColumn column) {
		keys.remove(column);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#removeKey(int)
	 */
	public void removeKey(int index) {
		keys.remove(index);
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#setEncode(java.lang
	 * .String)
	 */
	public void setEncode(String string) {
		encode = string;
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * setInitialSequencialReadBufferSize(int)
	 */
	public void setInitialSequencialReadBufferSize(int value) {
		initialSequencialReadBufferSize = value;
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * setMaximumSequencialReadBufferSize(int)
	 */
	public void setMaximumSequencialReadBufferSize(int value) {
		maximumSequencialReadBufferSize = value;
	}
	/*
	 * (non-Javadoc)
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * setMinimumSequencialReadBufferSize(int)
	 */
	public void setMinimumSequencialReadBufferSize(int value) {
		minimumSequencialReadBufferSize = value;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#setName(java.lang.
	 * String)
	 */
	public void setName(String string) {
		name = string;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#getListenerClasses()
	 */
	public List<Class<? extends CobolFileEventListener>> getListenerClasses() {
		return listenerClasses;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.CobolRecordMetaData#getCustomFileClassName
	 * ()
	 */
	public String getCustomFileClassName() {
		return "";
	}
	/**
	 * インデックス情報の一覧
	 * @return リスト
	 */
	public List<CobolIndex> getCobolIndexes() {
		return cobolIndexes;
	}
}
