package k_kim_mg.sa4cob2db;

import java.util.ArrayList;
import java.util.List;

import k_kim_mg.sa4cob2db.event.CobolFileEventListener;

/**
 * recordmeta data
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class DefaultCobolRecordMetaData implements CobolRecordMetaData {
	/** リスナクラス */
	private List<Class<? extends CobolFileEventListener>> listenerClasses = new ArrayList<Class<? extends CobolFileEventListener>>();
	/** 別 name  */
	protected List<String> aliases = new ArrayList<String>();
	/** 全ての column */
	protected List<CobolColumn> columns = new ArrayList<CobolColumn>();
	/**
	 * recordのエンコード
	 */
	protected String encode = "auto";
	/** 読み取りバッファ関連 */
	private int initialSequencialReadBufferSize,
			maximumSequencialReadBufferSize, minimumSequencialReadBufferSize;
	/**  key 読み込みを値で行うかどうか */
	private boolean keyByValue = false;

	/**  key  columnの一覧 */
	protected List<CobolColumn> keys = new ArrayList<CobolColumn>();

	/** meta data name  */
	protected String name = "";

	/** インデクス情報 */
	protected List<CobolIndex> cobolIndexes = new ArrayList<CobolIndex>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#addAlias(java.lang .String)
	 */
	public void addAlias(String alias) {
		aliases.add(alias);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#addColumn(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void addColumn(CobolColumn column) {
		columns.add(column);
	}

	/**
	 *  key  columnの追加
	 * 
	 * @param column
	 *            追加する column
	 */
	public void addKey(CobolColumn column) {
		keys.add(column);
	}

	/**
	 * 属性をコピーする
	 * 
	 * @param copy
	 *            コピー対象
	 * @return コピー対象(引数と同じ object )
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
	 *  columnの作成
	 * 
	 * @return 作成された column
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#findColumn(jp.ne.biglobe
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#findColumn(java.lang .String)
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getAlias()
	 */
	public String getAlias(int i) {
		return aliases.get(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getAliasCount()
	 */
	public int getAliasCount() {
		return aliases.size();
	}

	/**
	 *  index 情報の一覧
	 * 
	 * @return リスト
	 */
	public List<CobolIndex> getCobolIndexes() {
		return cobolIndexes;
	}

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getColumnCount()
	 */
	public int getColumnCount() {
		return columns.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getCustomFileClassName ()
	 */
	public String getCustomFileClassName() {
		return "";
	}

	/**
	 * エンコーディング
	 * 
	 * @return エンコーディング
	 */
	public String getEncode() {
		return encode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * getInitialSequencialReadBufferSize()
	 */
	public int getInitialSequencialReadBufferSize() {
		return initialSequencialReadBufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getKey(int)
	 */
	public CobolColumn getKey(int i) {
		return keys.get(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getKeyCount()
	 */
	public int getKeyCount() {
		return keys.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getListenerClasses()
	 */
	public List<Class<? extends CobolFileEventListener>> getListenerClasses() {
		return listenerClasses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * getMaximumSequencialReadBufferSize()
	 */
	public int getMaximumSequencialReadBufferSize() {
		return maximumSequencialReadBufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * getMinimumSequencialReadBufferSize()
	 */
	public int getMinimumSequencialReadBufferSize() {
		return minimumSequencialReadBufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#getRowSize()
	 */
	public int getRowSize() {
		// return getPhysicalLength();
		int ret = 0;
		for (int i = 0; i < getColumnCount(); i++) {
			int last = getColumn(i).getStart()
					+ getColumn(i).getPhysicalLength();
			if (ret < last) {
				ret = last;
			}
			// SQLNetServer.DebugPrint(i + ":" +
			// getColumn(i).getPhysicalLength());
		}
		return ret;
	}

	/**
	 *  key  columnですかー？
	 * 
	 * @param column
	 *             key かも知れない column
	 * @return true はい</br> false いいえ
	 */
	protected boolean isKey(CobolColumn column) {
		return keys.contains(column);
	}

	@Override
	public boolean isKeyByValue() {

		return keyByValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#removeAlias(java.lang
	 * .String)
	 */
	public void removeAlias(String alias) {
		aliases.remove(alias);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#removeColumn(jp.ne
	 * .biglobe.mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void removeColumn(CobolColumn column) {
		columns.remove(column);
		removeKey(column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#removeColumn(int)
	 */
	public void removeColumn(int index) {
		CobolColumn remove = getColumn(index);
		if (remove != null)
			columns.remove(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#removeKey(jp.ne.biglobe
	 * .mvh.k_kim_mg.acm.CobolColumn)
	 */
	public void removeKey(CobolColumn column) {
		keys.remove(column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#removeKey(int)
	 */
	public void removeKey(int index) {
		keys.remove(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#setEncode(java.lang .String)
	 */
	public void setEncode(String string) {
		encode = string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * setInitialSequencialReadBufferSize(int)
	 */
	public void setInitialSequencialReadBufferSize(int value) {
		initialSequencialReadBufferSize = value;
	}

	/**
	 *  key 読み込みを値で行うかどうか
	 * 
	 * @param keyByValue
	 *             key 読み込みを値で行うかどうか
	 */
	public void setKeyByValue(boolean keyByValue) {
		this.keyByValue = keyByValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * setMaximumSequencialReadBufferSize(int)
	 */
	public void setMaximumSequencialReadBufferSize(int value) {
		maximumSequencialReadBufferSize = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seek_kim_mg.sa4cob2db.CobolRecordMetaData#
	 * setMinimumSequencialReadBufferSize(int)
	 */
	public void setMinimumSequencialReadBufferSize(int value) {
		minimumSequencialReadBufferSize = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolRecordMetaData#setName(java.lang. String)
	 */
	public void setName(String string) {
		name = string;
	}
}
