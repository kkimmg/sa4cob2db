package k_kim_mg.sa4cob2db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Create XML Node
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class MetaCobolRecordMetaData {
	private MetadataCobolColumn dummy = new MetadataCobolColumn(this) {
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.utils.MetadataCobolColumn#getLevel()
		 */
		public int getLevel() {
			return -1;
		}
	};
	private MetadataCobolColumn root;
	private MetadataCobolColumn parent;
	private MetadataCobolColumn previous = dummy;
	private Map<String, MetadataCobolColumn> map = new HashMap<String, MetadataCobolColumn>();
	private List<MetadataCobolColumn> firsts = new ArrayList<MetadataCobolColumn>();
	private List<MetadataCobolColumn> list = new ArrayList<MetadataCobolColumn>();
	private Stack<MetadataCobolColumn> stack = new Stack<MetadataCobolColumn>();

	/**
	 * Create Nodes
	 * 
	 * @param document Dom Document Object
	 * @param rootNode Root Node
	 * @return Next location
	 */
	public int exportToNode(Document document, Node rootNode) {
		int ret = 0;

		for (MetadataCobolColumn x : firsts) {
			Node node = document.createElement("metadata");
			NamedNodeMap map = node.getAttributes();
			Attr attr = document.createAttribute("name");
			attr.setValue(root.getOriginalColumnName());
			map.setNamedItem(attr);
			rootNode.appendChild(node);
			Node stmt = document.createElement("statement");
			Node sql = document.createTextNode("select * from " + root.getOriginalColumnName() + ";");
			stmt.appendChild(sql);
			node.appendChild(stmt);
			ret = x.exportToNode(document, node, ret, "");
		}
		return ret;
	}

	/**
	 * Parse Text
	 * 
	 * @param txt Test
	 */
	public void parse(String txt) {
		MetadataCobolColumn work = new MetadataCobolColumn(this);
		int i = work.parce(txt);
		if (i == 1) {
			if (root == null) {
				root = work;
			} else {
				work.setRedefines(root.getName());
			}
			stack.clear();
			stack.push(root);
			parent = work;
			work.setStart(0);
			map.put(work.getName(), work);
			firsts.add(work);
			list.add(work);
			stack.add(work);
		} else if (i > 1) {
			list.add(work);
			map.put(work.getName(), work);
			if (work.getLevel() > previous.getLevel()) {
				stack.push(parent);
				parent = previous;
			} else if (work.getLevel() < previous.getLevel()) {
				while (work.getLevel() <= parent.getLevel()) {
					parent = stack.pop();
				}
			}
			parent.add(work);
		}
		previous = work;
	}

	/**
	 * Key is Already parsed?
	 * 
	 * @param key Key
	 * @return true/false
	 */
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	/**
	 * Get Column
	 * 
	 * @param key Key
	 * @return Column
	 */
	public MetadataCobolColumn get(String key) {
		return map.get(key);
	}
}
