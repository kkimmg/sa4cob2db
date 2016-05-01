package k_kim_mg.sa4cob2db.utils;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class MetadataCobolColumn extends ArrayList<MetadataCobolColumn> {
	private static final long serialVersionUID = 1L;
	public static Properties reps;

	static {
		reps = new Properties();
		reps.setProperty("-", "_");
	}

	static final int CUR_NONE = 0;
	static final int CUR_LEVEL = 1;
	static final int CUR_NAME = 2;
	static final int CUR_PIC = 4;
	static final int CUR_OCCURS = 16;
	static final int CUR_USAGE = 32;
	static final int CUR_REDEFINES = 64;
	static final int CUR_OTHER = 9999;
	public static String HF = "_";
	static final int TYPE_NONE = 0;
	static final int TYPE_X = 1;
	static final int TYPE_9 = 2;
	static final int TYPE_Z = 4;
	static final int TYPE_S = 8;
	static final int TYPE_V = 16;
	static final int TYPE_N = 32;
	private int level = 0;
	private int occurs = 0;
	private String format;
	private String forNull;
	private String ifNull;
	private int length;
	private int usage = 0;
	private String name;
	private int numberOfDecimal;
	private int start;
	private int type;
	private boolean signed;
	private String redefines;
	private boolean validColumn = false;
	private boolean key = false;
	private MetadataCobolColumn parent;
	private static ArrayList<String> keyregs = new ArrayList<String>();

	public static ArrayList<String> getKeyRegs() {
		return keyregs;
	}

	/**
	 * Is the name key?
	 * 
	 * @param name column name
	 * @return true/false
	 */
	public boolean isKeyName(String name) {
		boolean ret = false;
		for (String s : getKeyRegs()) {
			Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(name);
			ret |= matcher.find();
		}
		return ret;
	}

	/** meta data */
	MetaCobolRecordMetaData meta;

	/**
	 * Constructor
	 * 
	 * @param meta meta data
	 */
	public MetadataCobolColumn(MetaCobolRecordMetaData meta) {
		this.meta = meta;
	}

	/**
	 * Export to XML Node
	 * 
	 * @param document document
	 * @param parent node that indicates parent or record.
	 * @param start start index
	 * @param fix fix string
	 * @return Next location
	 */
	public int exportToNode(Document document, Node parent, int start, String fix) {
		int ret = start;
		if (getOccurs() == 0) {
			ret = exportToNode1(document, parent, ret, fix);
		} else {
			for (int i = 0; i < getOccurs(); i++) {
				String fix1 = fix + HF + i;
				ret = exportToNode1(document, parent, ret, fix1);
			}
		}
		return ret;
	}

	/**
	 * Export to XML Node
	 * 
	 * @param document document
	 * @param parent node that indicates parent or record.
	 * @param start start index
	 * @param fix fix string
	 * @return Next location
	 */
	protected int exportToNode1(Document document, Node parent, int start, String fix) {
		int ret = start;
		Node node;
		String l_redefines = getRedefines();
		if (l_redefines != null && l_redefines.length() > 0) {
			if (meta.containsKey(l_redefines)) {
				MetadataCobolColumn src = meta.get(l_redefines);
				if (src != null) {
					start = src.getStart();
					ret = start;
				}
			}
		}
		this.start = start;
		int l_length = getLength();
		if (l_length > 0) {
			if (l_length <= 4) {
				l_length = 2;
			} else if (5 <= l_length && l_length <= 9) {
				l_length = 4;
			} else if (10 <= l_length && l_length <= 18) {
				l_length = 8;
			}

			node = document.createElement("sqlcolumn");
			NamedNodeMap map = node.getAttributes();
			map.setNamedItem(setNodeAttribute(document, "name", getName() + fix));
			map.setNamedItem(setNodeAttribute(document, "originalColumnName", getOriginalColumnName() + fix));
			map.setNamedItem(setNodeAttribute(document, "type", getType()));
			map.setNamedItem(setNodeAttribute(document, "start", start));
			map.setNamedItem(setNodeAttribute(document, "length", l_length));
			if (isSigned()) {
				map.setNamedItem(setNodeAttribute(document, "signed", isSigned()));
			}
			String l_format = getFormat();
			if (l_format != null && l_format.length() > 0) {
				map.setNamedItem(setNodeAttribute(document, "format", l_format));
			}
			String l_forNull = getForNull();
			if (l_forNull != null && l_forNull.length() > 0) {
				map.setNamedItem(setNodeAttribute(document, "forNull", l_forNull));
			}
			String l_ifNull = getIfNull();
			if (l_ifNull != null && l_ifNull.length() > 0) {
				map.setNamedItem(setNodeAttribute(document, "ifNull", l_ifNull));
			}
			int decimal = getNumberOfDecimal();
			if (decimal > 0) {
				map.setNamedItem(setNodeAttribute(document, "decimal", String.valueOf(decimal)));
			}
			int usage = getUsage();
			if (usage > 0) {
				map.setNamedItem(setNodeAttribute(document, "usage", String.valueOf(usage)));
			}
			if (isKey()) {
				map.setNamedItem(setNodeAttribute(document, "key", String.valueOf(true)));
			}
			parent.appendChild(node);

			ret = start + l_length;
		}
		for (MetadataCobolColumn x : this) {
			ret = x.exportToNode(document, parent, ret, fix);
		}
		return ret;
	}

	public MetaCobolRecordMetaData getCobolRecordMetaData() {
		return meta;
	}

	public String getFormat() {
		return format;
	}

	public String getForNull() {
		return forNull;
	}

	public String getIfNull() {
		return ifNull;
	}

	public int getLength() {
		return length;
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public int getNumberOfDecimal() {
		return numberOfDecimal;
	}

	public int getOccurs() {
		return occurs;
	}

	/**
	 * get column name
	 * 
	 * @return DB Column name
	 */
	public String getOriginalColumnName() {
		String ret = getName();
		for (String x : reps.stringPropertyNames()) {
			String val = reps.getProperty(x);
			ret = ret.replaceAll(x, val);
		}
		return ret;
	}

	/**
	 * Get parent column
	 * 
	 * @return the parent
	 */
	public MetadataCobolColumn getParent() {
		return parent;
	}

	public int getPhysicalLength() {
		return getLength();
	}

	public String getRedefines() {
		return redefines;
	}

	public int getStart() {
		return start;
	}

	public int getType() {
		return type;
	}

	public int getUsage() {
		return usage;
	}

	/**
	 * Is this column key?
	 * 
	 * @return the key
	 */
	public boolean isKey() {
		boolean ret = key | isKeyName(getName());
		if (getParent() != null) {
			ret = getParent().isKey();
		}
		return ret;
	}

	public boolean isSigned() {
		return signed;
	}

	public boolean isValidColumn() {
		return validColumn;
	}

	public int parce(String logical) {
		int status = CUR_NONE;
		StringTokenizer tokenizer = new StringTokenizer(logical);
		String curr = "";
		try {
			while (tokenizer.hasMoreTokens()) {
				String curr1 = tokenizer.nextToken();
				curr = trimToken(curr1);
				switch (status) {
				case CUR_NONE:
					setLevel(Integer.parseInt(curr));
					status = CUR_LEVEL;
					setValidColumn(true);
					break;
				case CUR_LEVEL:
					setName(curr);
					status = CUR_NAME;
					break;
				case CUR_NAME:
					if (curr.equals("PIC") || curr.equals("PICTURE")) {
						status = CUR_PIC;
					}
					if (curr.equals("OCCURS")) {
						status = CUR_OCCURS;
					}
					if (curr.equals("REDEFINES")) {
						status = CUR_REDEFINES;
					}
					break;
				case CUR_PIC:
					status = parcePicture(curr);
					break;
				case CUR_OCCURS:
					setOccurs(Integer.parseInt(curr));
					status = CUR_OTHER;
					break;
				case CUR_USAGE:
					parceUsage(curr);
					status = CUR_OTHER;
					break;
				case CUR_REDEFINES:
					redefines = curr;
					status = CUR_OTHER;
					break;
				case CUR_OTHER:
					if (curr.equals("OCCURS")) {
						status = CUR_OCCURS;
					} else if (curr.equals("USAGE")) {
						status = CUR_USAGE;
					}
					if (curr.equals("REDEFINES")) {
						status = CUR_REDEFINES;
					}
					break;
				}
			}
		} catch (NumberFormatException e) {
			setLevel(0);
		}
		return getLevel();
	}

	int parcePicture(String token) {
		String picture = token.trim();
		int l_type = TYPE_NONE;
		int l_length = 0;
		int l_decimal = 0;
		StringBuffer l_format = new StringBuffer();
		char t = 0;
		char c;
		StringBuffer buf = new StringBuffer();
		boolean inKakko = false;
		// boolean withKakko = false;
		for (int i = 0; i < picture.length(); i++) {
			c = picture.charAt(i);
			if (inKakko) {
				if (c == ')') {
					String text = buf.toString();
					int temp = Integer.parseInt(text);
					l_length += temp;
					inKakko = false;
					// break;
				} else {
					buf.append(c);
				}
			} else {
				switch (c) {
				case '(':
					l_length--;
					inKakko = true;
					// withKakko = true;
					buf = new StringBuffer();
					if (l_format.length() > 0) {
						l_format.deleteCharAt(l_format.length() - 1);
					}
					break;
				case 'V':
				case 'v':
					l_decimal = l_length;
					l_type |= TYPE_V;
					break;
				case 'A':
				case 'a':
				case 'X':
				case 'x':
					t = c;
					l_type |= TYPE_X;
					l_length++;
					break;
				case 'N':
				case 'n':
					t = c;
					l_type |= TYPE_X;
					l_type |= TYPE_N;
					l_length += 2;
					break;
				case 'S':
				case 's':
					t = c;
					l_type |= TYPE_9;
					l_type |= TYPE_S;
					break;
				case '9':
					t = c;
					l_type |= TYPE_9;
					if (t != 'S' && t != 's') {
						l_length++;
						l_format.append('0');
					}
					break;
				case '0':
					l_length++;
					if ((l_type & TYPE_9) == TYPE_9) {
						l_format.append('0');
					}
					break;
				case 'Z':
				case 'z':
					t = c;
					l_type |= TYPE_9;
					l_type |= TYPE_Z;
					l_length++;
					l_format.append('#');
					break;
				case '.':
					if (i < picture.length()) {
						l_type |= TYPE_V;
						l_decimal = l_length;
						l_length++;
						l_format.append(".");
					}
					break;
				case 'P':
				case 'p':
					// 0 length
					break;
				case 'B':
				case 'b':
					switch (t) {
					case 'D':
					case 'd':
						t = c;
						l_length++;
						l_format.append(c);
						break;
					case 'X':
					case 'x':
						l_length++;
						break;
					case 'N':
					case 'n':
						l_length += 2;
						break;
					}
					break;
				case '*':
				case '\\':
				case ',':
				case '/':
				case '+':
				case '-':
				case 'C':
				case 'c':
				case 'D':
				case 'd':
					t = c;
					l_length++;
					l_format.append(c);
					break;
				case 'R':
				case 'r':
					switch (t) {
					case 'C':
					case 'c':
						t = c;
						l_length++;
						l_format.append(c);
						break;
					}
					break;
				default:
					l_length++;
					break;
				}
			}
		}
		setLength(l_length);
		if (((l_type & TYPE_X) == TYPE_X || l_type == TYPE_NONE)) {
			setType(CobolColumn.TYPE_XCHAR);
		} else {
			if ((l_type & TYPE_9) == TYPE_9) {
				if ((l_type & TYPE_V) == TYPE_V) {
					if ((Math.pow(10, l_length) - 1) > Float.MAX_VALUE) {
						setType(CobolColumn.TYPE_DOUBLE);
					} else {
						setType(CobolColumn.TYPE_FLOAT);
					}
					setNumberOfDecimal(l_decimal);
				} else {
					if ((int) (Math.pow(10, l_length) - 1) > Integer.MAX_VALUE) {
						setType(CobolColumn.TYPE_LONG);
					} else {
						setType(CobolColumn.TYPE_INTEGER);
					}
				}
				if (l_format.length() > 0) {
					setFormat(l_format.toString());
				}
			}
			if ((l_type & TYPE_S) == TYPE_S) {
				setSigned(true);
			}
		}
		return CUR_OTHER;
	}

	void parceUsage(String token) {
		String l_usage = token.trim();
		if (l_usage.equals("BINARY") || l_usage.equals("BINARY.") || l_usage.equals("COMP") || l_usage.equals("COMP.") || l_usage.equals("COMPUTATIONAL") || l_usage.equals("COMPUTATIONAL.")) {
			usage = CobolColumn.USAGE_BINARY;
		} else if (l_usage.equals("PACKED-DECIMAL") || l_usage.equals("PACKED-DECIMAL.") || l_usage.equals("COMP-3") || l_usage.equals("COMP-3.") || l_usage.equals("COMPUTATIONAL-3") || l_usage.equals("COMPUTATIONAL-3.")) {
			usage = CobolColumn.USAGE_COMP_3;
		} else if (l_usage.equals("NATIONAL") || l_usage.equals("NATIONAL.")) {
			usage = CobolColumn.USAGE_NATIONAL;
		} else if (l_usage.equals("INDEX") || l_usage.equals("INDEX.")) {
			usage = CobolColumn.USAGE_INDEX;
		}
	}

	public void setCobolRecordMetaData(CobolRecordMetaData cobolRecordMetaData) {
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setForNull(String forNull) {
		this.forNull = forNull;
	}

	public void setIfNull(String ifNull) {
		this.ifNull = ifNull;
	}

	/**
	 * Is this column key?
	 * 
	 * @param key the key to set
	 */
	public void setKey(boolean key) {
		this.key = key;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setName(String name) {
		this.name = name;
	}

	Attr setNodeAttribute(Document document, String name, boolean value) {
		return setNodeAttribute(document, name, String.valueOf(value));
	}

	Attr setNodeAttribute(Document document, String name, int value) {
		return setNodeAttribute(document, name, String.valueOf(value));
	}

	Attr setNodeAttribute(Document document, String name, String value) {
		Attr attr = document.createAttribute(name);
		attr.setValue(value);
		return attr;
	}

	public void setNumberOfDecimal(int decimal) {
		this.numberOfDecimal = decimal;
	}

	public void setOccurs(int occurs) {
		if (occurs < 0) {
			return;
		}
		this.occurs = occurs;
	}

	/**
	 * Set parent column
	 * 
	 * @param parent the parent to set
	 */
	public void setParent(MetadataCobolColumn parent) {
		this.parent = parent;
	}

	public void setRedefines(String redefines) {
		this.redefines = redefines;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * set ValidColumn
	 * 
	 * @param validColumn true/false
	 */
	public void setValidColumn(boolean validColumn) {
		this.validColumn = validColumn;
	}

	private String trimToken(String token) {
		String ret = token;
		if (token.length() > 1) {
			String r1 = token.substring(token.length() - 1, token.length() - 0);
			if (r1.equals(".")) {
				String r2 = token.substring(0, token.length() - 1);
				ret = trimToken(r2);
			}
		}
		return ret.toUpperCase();
	}
}
