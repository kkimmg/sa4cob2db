package k_kim_mg.sa4cob2db.sql.xml;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolIndex;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolColumn;
import k_kim_mg.sa4cob2db.DefaultCobolIndex;
import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
import k_kim_mg.sa4cob2db.sql.DefaultSQLCobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.SQLCobolColumn;
import k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @author ���줪��
 */
public class NodeReadLoader {
	/** ��������ư���Ρ��ɤ���Ͽ�Ѥߥ��饹�ΰ���(ñ��) */
	private Properties classesOfMeta = new Properties();
	/** ��������ư���Ρ��ɤ���Ͽ�Ѥߥ��饹�ΰ���(����) */
	private Properties classesOfSet = new Properties();
	/**
	 * ��̾�κ���
	 * @param node �Ρ���
	 * @return ��̾
	 */
	protected String createAlias(Node node) {
		// �ҥΡ��ɤ�õ��
		StringBuffer alias = new StringBuffer();
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// ��̾
				alias.append(child.getNodeValue());
			}
		}
		return alias.toString().trim();
	}
	/**
	 * ��κ���
	 * @param node �Ρ���
	 * @param column ��
	 * @return ��
	 */
	protected CobolColumn createCobolColumn(Node node, CobolColumn column) {
		NamedNodeMap map = node.getAttributes();
		// ��̾
		Node name = map.getNamedItem("name");
		if (name != null) {
			column.setName(name.getNodeValue());
		}
		// ��η�
		Node type = map.getNamedItem("type");
		if (type != null) {
			String typestr = type.getNodeValue();
			try {
				int typeint = Integer.parseInt(typestr);
				column.setType(typeint);
			} catch (NumberFormatException e) {
			}
		}
		// ���Ĺ��
		Node start = map.getNamedItem("start");
		if (start != null) {
			String startstr = start.getNodeValue();
			try {
				int startint = Integer.parseInt(startstr);
				column.setStart(startint);
			} catch (NumberFormatException e) {
			}
		}
		// ���Ĺ��
		Node length = map.getNamedItem("length");
		if (length != null) {
			String lengthstr = length.getNodeValue();
			try {
				int lengthint = Integer.parseInt(lengthstr);
				column.setLength(lengthint);
			} catch (NumberFormatException e) {
			}
		}
		// ��������Ĥ��ɤ���
		Node signedNode = map.getNamedItem("signed");
		if (signedNode != null) {
			String signedstr = signedNode.getNodeValue();
			boolean signedbool = Boolean.valueOf(signedstr);
			column.setSigned(signedbool);
		}
		// ��ξ������ʲ��η��
		Node decimal = map.getNamedItem("decimal");
		if (decimal != null) {
			String decimalstr = decimal.getNodeValue();
			try {
				int decimalint = Integer.parseInt(decimalstr);
				column.setNumberOfDecimal(decimalint);
			} catch (NumberFormatException e) {
			}
		}
		// ��ν�
		Node format = map.getNamedItem("format");
		if (format != null) {
			column.setFormat(format.getNodeValue());
		}
		// Null����
		Node ifNull = map.getNamedItem("ifNull");
		if (ifNull != null) {
			column.setIfNull(ifNull.getNodeValue());
		}
		// Null����
		Node forNull = map.getNamedItem("forNull");
		if (forNull != null) {
			column.setForNull(forNull.getNodeValue());
		}
		// �ѡ������顼
		Node useOnParseError = map.getNamedItem("useOnParseError");
		if (useOnParseError != null) {
			String useStr = useOnParseError.getNodeValue();
			boolean useBoolean = Boolean.getBoolean(useStr);
			if (useBoolean) {
				column.setUseOnParseError(useBoolean);
				Node valueOnParseError = map.getNamedItem("valueOnParseError");
				if (valueOnParseError != null) {
					// �Ȥꤢ����ʸ��������ꤹ��
					String vope = valueOnParseError.getNodeValue();
					column.setValueOfParseError(vope);
				}
			}
		}
		return column;
	}
	/**
	 * ����ǥå������������
	 * @param node (������ݻ����Ƥ���)�Ρ���
	 * @param meta ����ǥå������ɲä���ե�����
	 * @param metaset ����ǥå�����������뤿��Υ᥿�ǡ������å�
	 * @return ����ǥå�������
	 * @throws CobolRecordException
	 *             �㳰-����ǥå����ե����뤬���դ���ʤ��ä�������ǥå����˴�Ϣ�����󤬸��դ���ʤ��ä���
	 */
	protected CobolIndex createCobolIndex(Node node, CobolRecordMetaData meta, CobolRecordMetaDataSet metaset) throws CobolRecordException {
		DefaultCobolIndex ret = new DefaultCobolIndex();
		CobolRecordMetaData idmeta = null;
		NamedNodeMap map = node.getAttributes();
		// �ե�����̾
		Node filenamenode = map.getNamedItem("filename");
		if (filenamenode != null) {
			String filename = filenamenode.getNodeValue();
			ret.setFileName(filename);
			idmeta = metaset.getMetaData(filename);
		}
		if (idmeta == null) {
			SQLNetServer.logger.severe("can't find index file.");
			throw new CobolRecordException();
		}
		// ����ǥå���̾
		Node indexnamenode = map.getNamedItem("indexname");
		if (indexnamenode != null) {
			String indexname = indexnamenode.getNodeValue();
			ret.setIndexKeyName(indexname);
		}
		// �����
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		Map<CobolColumn, CobolColumn> fileKey2IndexColumn = ret.getFileKey2IndexColumn();
		Map<CobolColumn, CobolColumn> indexKey2FileColumn = ret.getIndexKey2FileColumn();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeName() == "file2index" || child.getNodeName() == "index2file") {
				NamedNodeMap cld = child.getAttributes();
				Node filecolumnnode = cld.getNamedItem("filecolumn");
				Node indexcolumnnode = cld.getNamedItem("indexcolumn");
				if (filecolumnnode != null && indexcolumnnode != null) {
					CobolColumn filecolumn = meta.getColumn(filecolumnnode.getNodeValue());
					CobolColumn indexcolumn = idmeta.getColumn(indexcolumnnode.getNodeValue());
					if (filecolumn != null && indexcolumn != null) {
						if (child.getNodeName() == "file2index") {
							fileKey2IndexColumn.put(filecolumn, indexcolumn);
						} else if (child.getNodeName() == "index2file") {
							indexKey2FileColumn.put(indexcolumn, filecolumn);
						}
					}
				}
			}
		}
		return ret;
	}
	/**
	 * ������������������
	 * @param node ������ξ����ޤ�Ρ��ɥ��֥�������
	 * @param meta �᥿�ǡ���
	 * @return ���ܥ���
	 */
	protected CobolColumn createCustomCobolColumn(Node node, CobolRecordMetaData meta) {
		CobolColumn ret = null;
		NamedNodeMap map = node.getAttributes();
		try {
			// ��̾
			Node classNameNode = map.getNamedItem("classname");
			if (classNameNode != null) {
				Node constructorType = map.getNamedItem("constructor");
				int constructorTypeNum = 2;
				if (constructorType != null) {
					// ���󥹥ȥ饯���ΰ����ο�����ꤹ��
					String constructorTypeStr = constructorType.getNodeValue();
					if (constructorTypeStr.equals("1")) {
						constructorTypeNum = 1;
					} else if (constructorTypeStr.equals("2")) {
						constructorTypeNum = 2;
					} else {
						constructorTypeNum = 0;
					}
				}
				String className = classNameNode.getNodeValue();
				Class<?> clazz0 = Class.forName(className);
				Class<? extends CobolColumn> clazz = clazz0.asSubclass(CobolColumn.class);
				if (constructorTypeNum == 0) {
					// �����ʤ�
					ret = clazz.newInstance();
				} else if (constructorTypeNum == 1) {
					// �᥿�ǡ����Τߤ�����ˤ���
					try {
						Class<?>[] params = new Class<?>[] { CobolRecordMetaData.class };
						Constructor<? extends CobolColumn> constructor = clazz.getConstructor(params);
						ret = constructor.newInstance(new Object[] { meta });
					} catch (NoSuchMethodException e) {
						SQLNetServer.logger.log(Level.WARNING, "can't find constoructor.", e);
						ret = clazz.newInstance();
					}
				} else if (constructorTypeNum == 2) {
					// XML�Ρ��ɤ����Ѥ���
					try {
						Class<?>[] params = new Class<?>[] { CobolRecordMetaData.class, Node.class };
						Constructor<? extends CobolColumn> constructor = clazz.getConstructor(params);
						ret = constructor.newInstance(new Object[] { meta, node });
					} catch (NoSuchMethodException e) {
						SQLNetServer.logger.log(Level.WARNING, "can't find constoructor.", e);
						try {
							Class<?>[] params = new Class<?>[] { CobolRecordMetaData.class };
							Constructor<? extends CobolColumn> constructor = clazz.getConstructor(params);
							ret = constructor.newInstance(new Object[] { meta });
						} catch (NoSuchMethodException e1) {
							SQLNetServer.logger.log(Level.WARNING, "can't find constoructor.", e1);
							ret = clazz.newInstance();
						}
					}
				}
			}
		} catch (DOMException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (SecurityException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (ClassCastException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (InstantiationException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		}
		if (ret == null) {
			ret = new DefaultCobolColumn(meta);
		}
		if (ret instanceof SQLCobolColumn) {
			createSQLCobolColumn(node, (SQLCobolRecordMetaData) meta, (SQLCobolColumn) ret);
		} else {
			createCobolColumn(node, ret);
		}
		return ret;
	}
	/**
	 * �᥿�ǡ����ο�������
	 * @param className ���Υ᥿�ǡ��������������
	 * @param node �᥿�ǡ����ξ����ޤ�Ρ��ɥ��֥�������
	 * @param parent ���Υ᥿�ǡ�����ޤ�᥿�ǡ������å�
	 * @return �᥿�ǡ���
	 */
	protected CobolRecordMetaData createCustomMetaData(String className, Node node, CobolRecordMetaDataSet parent) throws ClassNotFoundException, SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException,
	        InvocationTargetException, ClassCastException {
		CobolRecordMetaData ret = null;
		Class<?> claxx = Class.forName(className);
		Class<? extends CobolRecordMetaData> clazz = claxx.asSubclass(CobolRecordMetaData.class);
		try {
			Class<?>[] params = new Class<?>[] { Node.class };
			Constructor<? extends CobolRecordMetaData> constructor = clazz.getConstructor(params);
			ret = constructor.newInstance(new Object[] { node });
		} catch (NoSuchMethodException e) {
			ret = clazz.newInstance();
		}
		return ret;
	}
	/**
	 * ������κ���
	 * @param node �Ρ���
	 * @param meta �᥿�ǡ���
	 * @return ������(���顼����null)
	 */
	protected CobolColumn createKeyColumn(Node node, CobolRecordMetaData meta) {
		CobolColumn column = null;
		try {
			// �ҥΡ��ɤ�õ��
			StringBuffer keyName = new StringBuffer();
			NodeList children = node.getChildNodes();
			int size = children.getLength();
			for (int i = 0; i < size; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					// ����̾
					keyName.append(child.getNodeValue());
				}
			}
			String columnName = keyName.toString().trim();
			int index = meta.findColumn(columnName);
			column = meta.getColumn(index);
		} catch (DOMException e) {
			SQLNetServer.logger.log(Level.WARNING, "Key Column Exception.", e);
		} catch (CobolRecordException e) {
			SQLNetServer.logger.log(Level.WARNING, "Key Column Exception.", e);
		}
		return column;
	}
	/**
	 * �᥿�ǡ����ο�������
	 * @param node �Ρ���
	 * @param parent �᥿�ǡ�����ޤ�᥿�ǡ������å�
	 * @return �᥿�ǡ���
	 */
	protected CobolRecordMetaData createMetaData(Node node, CobolRecordMetaDataSet parent) {
		CobolRecordMetaData meta = null;
		// �ޥå�
		NamedNodeMap map = node.getAttributes();
		if (meta == null) {
			Node custom = map.getNamedItem("customClassName");
			if (custom == null || custom.getNodeValue().trim().length() == 0) {
				// �����Υ᥿�ǡ���
				Node base = map.getNamedItem("base");
				if (base != null) {
					String basename = base.getNodeValue().trim();
					CobolRecordMetaData work = parent.getMetaData(basename);
					if (work != null) {
						meta = work.createCopy();
					} else {
						meta = new DefaultSQLCobolRecordMetaData();
					}
				} else {
					// ����١����ˤ��Ƥ��ʤ����
					meta = new DefaultSQLCobolRecordMetaData();
				}
			} else {
				// ��������Υ᥿�ǡ���
				String customName = custom.getNodeValue();
				try {
					meta = createCustomMetaData(customName, node, parent);
				} catch (SecurityException e) {
					SQLNetServer.logger.log(Level.CONFIG, "sumething wrong.", e);
				} catch (IllegalArgumentException e) {
					SQLNetServer.logger.log(Level.CONFIG, "sumething wrong.", e);
				} catch (ClassCastException e) {
					SQLNetServer.logger.log(Level.CONFIG, "sumething wrong.", e);
				} catch (ClassNotFoundException e) {
					SQLNetServer.logger.log(Level.CONFIG, "sumething wrong.", e);
				} catch (InstantiationException e) {
					SQLNetServer.logger.log(Level.CONFIG, "sumething wrong.", e);
				} catch (IllegalAccessException e) {
					SQLNetServer.logger.log(Level.CONFIG, "sumething wrong.", e);
				} catch (InvocationTargetException e) {
					SQLNetServer.logger.log(Level.CONFIG, "sumething wrong.", e);
				} finally {
					if (meta == null) {
						meta = new DefaultSQLCobolRecordMetaData();
					}
				}
			}
		}
		// ���Υ᥿�ǡ�����̾��
		Node name = map.getNamedItem("name");
		if (name != null) {
			meta.setName(name.getNodeValue().trim());
		}
		// ���Υ��󥳡��ǥ���
		Node encode = map.getNamedItem("encode");
		if (encode != null) {
			meta.setEncode(encode.getNodeValue().trim());
		}
		// �Хåե��ν��������
		Node bufinit = map.getNamedItem("bufinit");
		if (bufinit != null) {
			try {
				meta.setInitialSequencialReadBufferSize(Integer.parseInt(bufinit.getNodeValue()));
			} catch (NumberFormatException e) {
				SQLNetServer.logger.log(Level.CONFIG, "can't read bufsize.", e);
			}
		}
		// �Хåե��κǾ�������
		Node bufmin = map.getNamedItem("bufmin");
		if (bufmin != null) {
			try {
				meta.setMinimumSequencialReadBufferSize(Integer.parseInt(bufmin.getNodeValue()));
			} catch (NumberFormatException e) {
				SQLNetServer.logger.log(Level.CONFIG, "can't read bufsize.", e);
			}
		}
		// �Хåե��κ��祵����
		Node bufmax = map.getNamedItem("bufmax");
		if (bufmax != null) {
			try {
				meta.setMaximumSequencialReadBufferSize(Integer.parseInt(bufmax.getNodeValue()));
			} catch (NumberFormatException e) {
				SQLNetServer.logger.log(Level.CONFIG, "can't read bufsize.", e);
			}
		}
		// �ҥΡ��ɤ�õ��
		StringBuffer sql = new StringBuffer();
		String truncate = null;
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals("statement")) {
				// SQL���ơ��ȥ���
				NodeList sqlchildren = child.getChildNodes();
				int sqlsize = sqlchildren.getLength();
				for (int j = 0; j < sqlsize; j++) {
					Node sqlchild = sqlchildren.item(j);
					if (sqlchild.getNodeType() == Node.TEXT_NODE) {
						sql.append(sqlchild.getNodeValue());
					}
				}
			} else if (child.getNodeName().equals("column") && meta instanceof SQLCobolRecordMetaData) {
				// ��������ʤ���äȺ����
				CobolColumn cobolColumn = createSQLCobolColumn(child, (SQLCobolRecordMetaData) meta, null);
				meta.addColumn(cobolColumn);
			} else if (child.getNodeName().equals("sqlcolumn") && meta instanceof SQLCobolRecordMetaData) {
				// SQL��
				CobolColumn cobolColumn = createSQLCobolColumn(child, (SQLCobolRecordMetaData) meta, null);
				meta.addColumn(cobolColumn);
			} else if (child.getNodeName().equals("customcolumn")) {
				// ����������
				CobolColumn cobolColumn = createCustomCobolColumn(child, meta);
				meta.addColumn(cobolColumn);
			} else if (child.getNodeName().equals("keycolumn")) {
				// ������
				CobolColumn cobolColumn = createKeyColumn(child, meta);
				if (cobolColumn != null) {
					meta.addKey(cobolColumn);
				}
			} else if (child.getNodeName().equals("alias")) {
				// ��̾
				String alias = createAlias(child);
				if (alias.length() > 0) {
					meta.addAlias(alias);
				}
			} else if (child.getNodeName().equals("listener")) {
				// ��̾
				String namelist = getNodeText(child);
				if (namelist.length() > 0) {
					String[] classnames = namelist.split(":");
					for (String classname : classnames) {
						try {
							Class<?> clazz0 = Class.forName(classname);
							Class<? extends CobolFileEventListener> clazz = clazz0.asSubclass(CobolFileEventListener.class);
							meta.getListenerClasses().add(clazz);
						} catch (ClassCastException e) {
							SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
						} catch (ClassNotFoundException e) {
							SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
						}
					}
				}
			} else if (child.getNodeName().equals("truncate")) {
				// ���٤ƺ��
				String wtruncate = getNodeText(child);
				if (wtruncate.length() > 0) {
					truncate = wtruncate;
				}
			} else if (child.getNodeName().equals("indexfile")) {
				// ����ǥå���
				try {
					CobolIndex index = createCobolIndex(child, meta, parent);
					if (index != null) {
						meta.getCobolIndexes().add(index);
					}
				} catch (CobolRecordException e) {
					SQLNetServer.logger.log(Level.SEVERE, "can't add index.", e);
				}
			} else {
				// �Ȥꤢ�����ͤ��Ƥʤ�
				proessOtherNodeOfMeta(child, meta);
			}
		}
		if (meta instanceof SQLCobolRecordMetaData) {
			// SQL���ơ��ȥ��Ȥ򥻥åȤ���
			SQLCobolRecordMetaData metasql = (SQLCobolRecordMetaData) meta;
			metasql.setSelectStatement(sql.toString().trim());
			// ���٤ƺ���򥻥åȤ���
			if (truncate != null) {
				if (truncate.trim().length() > 0) {
					metasql.setTruncateStatement(truncate.trim());
				}
			}
		}
		return meta;
	}
	/**
	 * �᥿�ǡ������åȤκ���
	 * @param document �����ޤ�ɥ������
	 * @param meta �᥿�ǡ������å�
	 * @return �᥿�ǡ������å�
	 */
	public CobolRecordMetaDataSet createMetaDataSet(Document document, CobolRecordMetaDataSet meta, Properties properties) {
		if (meta == null) {
			meta = new SQLCobolRecordMetaDataSet();
		}
		Node node = document.getFirstChild();
		while (node != null) {
			if (node.getNodeName().equals("metadataset")) {
				createMetaDataSet(node, meta, properties);
			}
			node = node.getNextSibling();
		}
		return meta;
	}
	/**
	 * �᥿�ǡ������åȤκ���
	 * @param file �����ޤ�ե�����
	 * @param meta �᥿�ǡ������å�
	 * @return �᥿�ǡ������å�
	 */
	public CobolRecordMetaDataSet createMetaDataSet(File file, CobolRecordMetaDataSet meta, Properties properties) throws ParserConfigurationException, FactoryConfigurationError, FactoryConfigurationError, SAXException, IOException {
		Document document1;
		DocumentBuilder docBld;
		docBld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document1 = docBld.parse(file);
		return createMetaDataSet(document1, meta, properties);
	}
	/**
	 * �᥿�ǡ������åȤκ���
	 * @param node �����ޤ�Ρ���
	 * @param meta �᥿�ǡ������å�
	 * @return �᥿�ǡ������å�
	 */
	public CobolRecordMetaDataSet createMetaDataSet(Node node, CobolRecordMetaDataSet meta, Properties properties) {
		if (meta == null) {
			meta = new SQLCobolRecordMetaDataSet();
		}
		//
		if (node.getNodeName().equals("metadataset")) {
			NodeList items = node.getChildNodes();
			int len = items.getLength();
			for (int i = 0; i < len; i++) {
				Node item = items.item(i);
				if (item.getNodeName().equals("metadata")) {
					// �᥿�ǡ���
					CobolRecordMetaData meta1 = createMetaData(item, meta);
					meta.installMetaData(meta1);
					SQLNetServer.logger.log(Level.CONFIG, "metadata:" + meta1.getName());
				} else if (item.getNodeName().equals("property")) {
					// �ޥå�
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						properties.put(wname, wvalu);
					}
				} else if (item.getNodeName().equals("property")) {
					// �ޥå�
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						properties.put(wname, wvalu);
					}
				} else if (item.getNodeName().equals("property")) {
					// �ޥå�
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						properties.put(wname, wvalu);
					}
				} else if (item.getNodeName().equals("otherofset")) {
					// ��������Ρ���(�᥿�ǡ�������)
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						classesOfSet.put(wname, wvalu);
						SQLNetServer.logger.info("adding other processor of metadataset " + wname + ":" + wvalu);
					}
				} else if (item.getNodeName().equals("otherofmeta")) {
					// ��������Ρ���(�᥿�ǡ���ñ��)
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						classesOfMeta.put(wname, wvalu);
						SQLNetServer.logger.info("adding other processor of metadata " + wname + ":" + wvalu);
					}
				} else {
					// �ʤˤ�����������
					proessOtherNodeOfSet(item, meta, properties);
				}
			}
		}
		// �֤�
		return meta;
	}
	/**
	 * SQL��κ���
	 * @param node �Ρ���
	 * @param column SQL��(�����ξ���null)
	 * @return SQL��
	 */
	protected SQLCobolColumn createSQLCobolColumn(Node node, SQLCobolRecordMetaData meta, SQLCobolColumn column) {
		if (column == null) {// ��������
			column = new SQLCobolColumn(meta);
		}
		// ��������
		createCobolColumn(node, column);
		NamedNodeMap map = node.getAttributes();
		// SQL��̾
		Node originalColumnName = map.getNamedItem("originalColumnName");
		if (originalColumnName != null) {
			column.setOriginalColumnName(originalColumnName.getNodeValue());
		}
		// �ǥե���Ȥ�ʸ����
		Node defaultString = map.getNamedItem("defaultString");
		if (defaultString != null) {
			column.setDefaultString(defaultString.getNodeValue());
		}
		// �ɤ߼���Բ�
		Node readIgnore = map.getNamedItem("readIgnore");
		if (readIgnore != null) {
			String readIgnorestr = readIgnore.getNodeValue();
			boolean readIgnoreBool = Boolean.getBoolean(readIgnorestr);
			column.setReadIgnore(readIgnoreBool);
		}
		// ����Բ�
		Node rewriteIgnore = map.getNamedItem("rewriteIgnore");
		if (rewriteIgnore != null) {
			String rewriteIgnorestr = rewriteIgnore.getNodeValue();
			boolean rewriteIgnoreBool = Boolean.getBoolean(rewriteIgnorestr);
			column.setRewriteIgnore(rewriteIgnoreBool);
		}
		// �����񤭹����Բ�
		Node writeIgnore = map.getNamedItem("writeIgnore");
		if (writeIgnore != null) {
			String writeIgnorestr = writeIgnore.getNodeValue();
			boolean writeIgnoreBool = Boolean.getBoolean(writeIgnorestr);
			column.setWriteIgnore(writeIgnoreBool);
		}
		return column;
	}
	/**
	 * �Ρ���ľ���Υƥ����Ȥ��������
	 * @param node �Ρ���
	 * @return ʸ����
	 */
	protected String getNodeString(Node node) {
		StringBuffer retvalue = new StringBuffer();
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// ��̾
				retvalue.append(child.getNodeValue().trim());
			}
		}
		return retvalue.toString().trim();
	}
	/**
	 * �Ρ��ɤΥƥ����Ȥ��������
	 * @param node �Ρ���
	 * @return �Ρ��ɤΥƥ�����
	 */
	protected String getNodeText(Node node) {
		// �ҥΡ��ɤ�õ��
		StringBuffer buffer = new StringBuffer();
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// ��̾
				buffer.append(child.getNodeValue().trim());
			}
		}
		return buffer.toString().trim();
	}
	/**
	 * ����¾�ν����������Ȥꤢ�������⤷�ʤ�
	 * @param node �Ρ���
	 * @param meta �᥿�ǡ���
	 */
	protected void proessOtherNodeOfMeta(Node node, CobolRecordMetaData meta) {
		try {
			if (node.getNodeType() != Node.TEXT_NODE) {
				String name = node.getNodeName();
				if (classesOfMeta.containsKey(name)) {
					String classname = classesOfMeta.getProperty(name);
					Class<? extends MetaDataNodeProcessor> clazz = Class.forName(classname).asSubclass(MetaDataNodeProcessor.class);
					MetaDataNodeProcessor processor = clazz.newInstance();
					processor.processOtherNode(node, meta);
				} else {
					SQLNetServer.logger.warning("class not found for " + name);
				}
			}
		} catch (ClassNotFoundException e) {
			SQLNetServer.logger.warning(e.getMessage());
		} catch (InstantiationException e) {
			SQLNetServer.logger.warning(e.getMessage());
		} catch (IllegalAccessException e) {
			SQLNetServer.logger.warning(e.getMessage());
		}
	}
	/**
	 * ����ʬ����ʤ��Ρ��ɤ��褿��?
	 * @param node �Ρ���
	 * @param meta �᥿�ǡ������å�
	 */
	protected void proessOtherNodeOfSet(Node node, CobolRecordMetaDataSet meta, Properties properties) {
		try {
			if (node.getNodeType() != Node.TEXT_NODE) {
				String name = node.getNodeName();
				if (classesOfSet.containsKey(name)) {
					String classname = classesOfSet.getProperty(name);
					Class<? extends MetaDataSetNodeProcessor> clazz = Class.forName(classname).asSubclass(MetaDataSetNodeProcessor.class);
					MetaDataSetNodeProcessor processor = clazz.newInstance();
					processor.processOtherNode(node, meta, properties);
				} else {
					SQLNetServer.logger.warning("class not found for " + name);
				}
			}
		} catch (ClassNotFoundException e) {
			SQLNetServer.logger.warning(e.getMessage());
		} catch (InstantiationException e) {
			SQLNetServer.logger.warning(e.getMessage());
		} catch (IllegalAccessException e) {
			SQLNetServer.logger.warning(e.getMessage());
		}
	}
}
