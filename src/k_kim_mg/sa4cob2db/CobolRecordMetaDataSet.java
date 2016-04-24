package k_kim_mg.sa4cob2db;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * list of meta data
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolRecordMetaDataSet {
	private List<CobolRecordMetaData> list = new ArrayList<CobolRecordMetaData>();
	private Map<String, CobolRecordMetaData> names = new Hashtable<String, CobolRecordMetaData>();
	private Map<String, Object> others = new Hashtable<String, Object>();

	/**
	 * create COBOL file
	 * 
	 * @param meta meta data
	 * @return COBOL file
	 */
	protected abstract CobolFile createCobolFile(CobolRecordMetaData meta);

	/**
	 * get COBOL file from name
	 * 
	 * @param name meta data name
	 * @return COBOL file
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
	 * get meta data
	 * 
	 * @param name meta data name
	 * @return meta data
	 */
	public CobolRecordMetaData getMetaData(String name) {
		CobolRecordMetaData meta = (CobolRecordMetaData) names.get(name);
		return meta;
	}

	/**
	 * get meta data
	 * 
	 * @param i get to i'th MetaData
	 * @return MetaData
	 */
	public CobolRecordMetaData get(int i) {
		return list.get(i);
	}

	/**
	 * Size
	 * 
	 * @return size
	 */
	public int size() {
		return list.size();
	}

	/**
	 * add meta data
	 * 
	 * @param meta meta data
	 */
	public boolean installMetaData(CobolRecordMetaData meta) {
		boolean ret = list.add(meta);
		names.put(meta.getName(), meta);
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.put(meta.getAlias(i), meta);
		}
		return ret;
	}

	/**
	 * remove meta data
	 * 
	 * @param meta meta data
	 */
	public boolean removeMetaData(CobolRecordMetaData meta) {
		boolean ret = list.remove(meta);
		names.remove(meta.getName());
		for (int i = 0; i < meta.getAliasCount(); i++) {
			names.remove(meta.getAlias(i));
		}
		return ret;
	}

	/**
	 * returns ListIterator
	 * 
	 * @return list iterator
	 */
	public ListIterator<CobolRecordMetaData> listIterator() {
		return list.listIterator();
	}

	/**
	 * to array
	 * 
	 * @return array
	 */
	public CobolRecordMetaData[] toArray() {
		return list.toArray(new CobolRecordMetaData[size()]);
	}

	/**
	 * returns other object
	 * 
	 * @param key key
	 * @return object
	 */
	public Object getOtherObject(String key) {
		return others.get(key);
	}

	/**
	 * put key, value
	 * 
	 * @param key key
	 * @param value value
	 */
	public void putOtherObject(String key, Object value) {
		others.put(key, value);
	}
}
