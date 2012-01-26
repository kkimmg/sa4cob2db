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
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class NodeReadLoader {
	private Properties classesOfMeta = new Properties();
	private Properties classesOfSet = new Properties();
	/**
	 * create alias
	 * 
	 * @param node node
	 * @return alias text
	 */
	protected String createAlias(Node node) {
		StringBuffer alias = new StringBuffer();
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				alias.append(child.getNodeValue());
			}
		}
		return alias.toString().trim();
	}
	/**
	 * create column
	 * 
	 * @param node node
	 * @param column column
	 * @return column
	 */
	protected CobolColumn createCobolColumn(Node node, CobolColumn column) {
		NamedNodeMap map = node.getAttributes();
		// name
		Node name = map.getNamedItem("name");
		if (name != null) {
			column.setName(name.getNodeValue());
		}
		// type
		Node type = map.getNamedItem("type");
		if (type != null) {
			String typestr = type.getNodeValue();
			try {
				int typeint = Integer.parseInt(typestr);
				column.setType(typeint);
			} catch (NumberFormatException e) {
			}
		}
		// start position
		Node start = map.getNamedItem("start");
		if (start != null) {
			String startstr = start.getNodeValue();
			try {
				int startint = Integer.parseInt(startstr);
				column.setStart(startint);
			} catch (NumberFormatException e) {
			}
		}
		// length
		Node length = map.getNamedItem("length");
		if (length != null) {
			String lengthstr = length.getNodeValue();
			try {
				int lengthint = Integer.parseInt(lengthstr);
				column.setLength(lengthint);
			} catch (NumberFormatException e) {
			}
		}
		// signed?
		Node signedNode = map.getNamedItem("signed");
		if (signedNode != null) {
			String signedstr = signedNode.getNodeValue();
			boolean signedbool = Boolean.valueOf(signedstr);
			column.setSigned(signedbool);
		}
		// decimal size
		Node decimal = map.getNamedItem("decimal");
		if (decimal != null) {
			String decimalstr = decimal.getNodeValue();
			try {
				int decimalint = Integer.parseInt(decimalstr);
				column.setNumberOfDecimal(decimalint);
			} catch (NumberFormatException e) {
			}
		}
		// format
		Node format = map.getNamedItem("format");
		if (format != null) {
			column.setFormat(format.getNodeValue());
		}
		// nvl
		Node ifNull = map.getNamedItem("ifNull");
		if (ifNull != null) {
			column.setIfNull(ifNull.getNodeValue());
		}
		// to null
		Node forNull = map.getNamedItem("forNull");
		if (forNull != null) {
			column.setForNull(forNull.getNodeValue());
		}
		// value on error
		Node useOnParseError = map.getNamedItem("useOnParseError");
		if (useOnParseError != null) {
			String useStr = useOnParseError.getNodeValue();
			boolean useBoolean = Boolean.getBoolean(useStr);
			if (useBoolean) {
				column.setUseOnParseError(useBoolean);
				Node valueOnParseError = map.getNamedItem("valueOnParseError");
				if (valueOnParseError != null) {
					String vope = valueOnParseError.getNodeValue();
					column.setValueOfParseError(vope);
				}
			}
		}
		return column;
	}
	/**
	 * create index
	 * 
	 * @param node node
	 * @param meta file to index
	 * @param metaset metadata set includes index
	 * @return index
	 * @throws CobolRecordException can't find index/column
	 */
	protected CobolIndex createCobolIndex(Node node, CobolRecordMetaData meta, CobolRecordMetaDataSet metaset) throws CobolRecordException {
		DefaultCobolIndex ret = new DefaultCobolIndex();
		CobolRecordMetaData idmeta = null;
		NamedNodeMap map = node.getAttributes();
		// filename
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
		// name
		Node indexnamenode = map.getNamedItem("indexname");
		if (indexnamenode != null) {
			String indexname = indexnamenode.getNodeValue();
			ret.setIndexKeyName(indexname);
		}
		// columns
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
	 * create cobol column custom class
	 * 
	 * @param node Node
	 * @param meta parent
	 * @return column
	 */
	protected CobolColumn createCustomCobolColumn(Node node, CobolRecordMetaData meta) {
		CobolColumn ret = null;
		NamedNodeMap map = node.getAttributes();
		try {
			// classname
			Node classNameNode = map.getNamedItem("classname");
			if (classNameNode != null) {
				Node constructorType = map.getNamedItem("constructor");
				int constructorTypeNum = 2;
				if (constructorType != null) {
					// type of Constructor
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
					// no param
					ret = clazz.newInstance();
				} else if (constructorTypeNum == 1) {
					// only parent
					try {
						Class<?>[] params = new Class<?>[] { CobolRecordMetaData.class };
						Constructor<? extends CobolColumn> constructor = clazz.getConstructor(params);
						ret = constructor.newInstance(new Object[] { meta });
					} catch (NoSuchMethodException e) {
						SQLNetServer.logger.log(Level.WARNING, "can't find constoructor.", e);
						ret = clazz.newInstance();
					}
				} else if (constructorTypeNum == 2) {
					// with node
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
	 * メタデータの新規作成
	 * 
	 * @param className このメタデータを定義する列
	 * @param node メタデータの情報を含むノードオブジェクト
	 * @param parent このメタデータを含むメタデータセット
	 * @return メタデータ
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
	 * キー列の作成
	 * 
	 * @param node ノード
	 * @param meta メタデータ
	 * @return キー列(エラー時はnull)
	 */
	protected CobolColumn createKeyColumn(Node node, CobolRecordMetaData meta) {
		CobolColumn column = null;
		try {
			// 子ノードの探索
			StringBuffer keyName = new StringBuffer();
			NodeList children = node.getChildNodes();
			int size = children.getLength();
			for (int i = 0; i < size; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					// キー名
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
	 * メタデータの新規作成
	 * 
	 * @param node ノード
	 * @param parent メタデータを含むメタデータセット
	 * @return メタデータ
	 */
	protected CobolRecordMetaData createMetaData(Node node, CobolRecordMetaDataSet parent) {
		//
		CobolRecordMetaData meta = null;
		// マップ
		NamedNodeMap map = node.getAttributes();
		// このメタデータの名前
		Node name = map.getNamedItem("name");
		if (name != null) {
			String nm = name.getNodeValue().trim();
			meta = parent.getMetaData(nm);
		}
		//
		if (meta == null) {
			Node custom = map.getNamedItem("customClassName");
			if (custom == null || custom.getNodeValue().trim().length() == 0) {
				// 新規のメタデータ
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
					// 何もベースにしていない場合
					meta = new DefaultSQLCobolRecordMetaData();
				}
			} else {
				// カスタムのメタデータ
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
		} else {
			SQLNetServer.logger.log(Level.CONFIG, "Duplicates metadata name.");
			Node custom = map.getNamedItem("customClassName");
			if (custom != null && custom.getNodeValue().trim().length() > 0) {
				SQLNetServer.logger.log(Level.CONFIG, "Duplicates metadata name.");
			}
		}
		if (name != null) {
			meta.setName(name.getNodeValue().trim());
		}
		// このエンコーディング
		Node encode = map.getNamedItem("encode");
		if (encode != null) {
			meta.setEncode(encode.getNodeValue().trim());
		}
		// バッファの初期サイズ
		Node bufinit = map.getNamedItem("bufinit");
		if (bufinit != null) {
			try {
				meta.setInitialSequencialReadBufferSize(Integer.parseInt(bufinit.getNodeValue()));
			} catch (NumberFormatException e) {
				SQLNetServer.logger.log(Level.CONFIG, "can't read bufsize.", e);
			}
		}
		// バッファの最小サイズ
		Node bufmin = map.getNamedItem("bufmin");
		if (bufmin != null) {
			try {
				meta.setMinimumSequencialReadBufferSize(Integer.parseInt(bufmin.getNodeValue()));
			} catch (NumberFormatException e) {
				SQLNetServer.logger.log(Level.CONFIG, "can't read bufsize.", e);
			}
		}
		// バッファの最大サイズ
		Node bufmax = map.getNamedItem("bufmax");
		if (bufmax != null) {
			try {
				meta.setMaximumSequencialReadBufferSize(Integer.parseInt(bufmax.getNodeValue()));
			} catch (NumberFormatException e) {
				SQLNetServer.logger.log(Level.CONFIG, "can't read bufsize.", e);
			}
		}
		// 値によるキー比較
		Node kbv = map.getNamedItem("keyval");
		if (kbv != null) {
			String keyval = kbv.getNodeValue().trim();
			meta.setKeyByValue(Boolean.valueOf(keyval));
		} else {
			meta.setKeyByValue(false);
		}
		// 子ノードの探索
		StringBuffer sql = new StringBuffer();
		String truncate = null;
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals("statement")) {
				// SQLステートメント
				NodeList sqlchildren = child.getChildNodes();
				int sqlsize = sqlchildren.getLength();
				for (int j = 0; j < sqlsize; j++) {
					Node sqlchild = sqlchildren.item(j);
					if (sqlchild.getNodeType() == Node.TEXT_NODE) {
						sql.append(sqlchild.getNodeValue());
					}
				}
			} else if (child.getNodeName().equals("column") && meta instanceof SQLCobolRecordMetaData) {
				// ただの列（ちょっと困る）
				CobolColumn cobolColumn = createSQLCobolColumn(child, (SQLCobolRecordMetaData) meta, null);
				meta.addColumn(cobolColumn);
			} else if (child.getNodeName().equals("sqlcolumn") && meta instanceof SQLCobolRecordMetaData) {
				// SQL列
				CobolColumn cobolColumn = createSQLCobolColumn(child, (SQLCobolRecordMetaData) meta, null);
				meta.addColumn(cobolColumn);
			} else if (child.getNodeName().equals("customcolumn")) {
				// カスタム列
				CobolColumn cobolColumn = createCustomCobolColumn(child, meta);
				meta.addColumn(cobolColumn);
			} else if (child.getNodeName().equals("keycolumn")) {
				// キー列
				CobolColumn cobolColumn = createKeyColumn(child, meta);
				if (cobolColumn != null) {
					meta.addKey(cobolColumn);
				}
			} else if (child.getNodeName().equals("alias")) {
				// 別名
				String alias = createAlias(child);
				if (alias.length() > 0) {
					meta.addAlias(alias);
				}
			} else if (child.getNodeName().equals("listener")) {
				// 別名
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
				// すべて削除
				String wtruncate = getNodeText(child);
				if (wtruncate.length() > 0) {
					truncate = wtruncate;
				}
			} else if (child.getNodeName().equals("indexfile")) {
				// インデックス
				try {
					CobolIndex index = createCobolIndex(child, meta, parent);
					if (index != null) {
						meta.getCobolIndexes().add(index);
					}
				} catch (CobolRecordException e) {
					SQLNetServer.logger.log(Level.SEVERE, "can't add index.", e);
				}
			} else {
				// とりあえず考えてない
				proessOtherNodeOfMeta(child, meta);
			}
		}
		if (meta instanceof SQLCobolRecordMetaData) {
			// SQLステートメントをセットする
			SQLCobolRecordMetaData metasql = (SQLCobolRecordMetaData) meta;
			metasql.setSelectStatement(sql.toString().trim());
			// すべて削除をセットする
			if (truncate != null) {
				if (truncate.trim().length() > 0) {
					metasql.setTruncateStatement(truncate.trim());
				}
			}
		}
		return meta;
	}
	/**
	 * メタデータセットの作成
	 * 
	 * @param document 情報を含むドキュメント
	 * @param meta メタデータセット
	 * @return メタデータセット
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
	 * メタデータセットの作成
	 * 
	 * @param file 情報を含むファイル
	 * @param meta メタデータセット
	 * @return メタデータセット
	 */
	public CobolRecordMetaDataSet createMetaDataSet(File file, CobolRecordMetaDataSet meta, Properties properties) throws ParserConfigurationException, FactoryConfigurationError, FactoryConfigurationError, SAXException, IOException {
		Document document1;
		DocumentBuilder docBld;
		docBld = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document1 = docBld.parse(file);
		return createMetaDataSet(document1, meta, properties);
	}
	/**
	 * メタデータセットの作成
	 * 
	 * @param node 情報を含むノード
	 * @param meta メタデータセット
	 * @return メタデータセット
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
					// メタデータ
					CobolRecordMetaData meta1 = createMetaData(item, meta);
					meta.installMetaData(meta1);
					SQLNetServer.logger.log(Level.CONFIG, "metadata:" + meta1.getName());
				} else if (item.getNodeName().equals("property")) {
					// マップ
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						properties.put(wname, wvalu);
					}
				} else if (item.getNodeName().equals("otherofset")) {
					// カスタムノード(メタデータ全体)
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						classesOfSet.put(wname, wvalu);
						SQLNetServer.logger.info("adding other processor of metadataset " + wname + ":" + wvalu);
					}
				} else if (item.getNodeName().equals("otherofmeta")) {
					// カスタムノード(メタデータ単体)
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("name");
					if (work != null) {
						String wname = work.getNodeValue();
						String wvalu = getNodeString(item);
						classesOfMeta.put(wname, wvalu);
						SQLNetServer.logger.info("adding other processor of metadata " + wname + ":" + wvalu);
					}
				} else if (item.getNodeName().equals("include")) {
					// Externalのファイルをもとにこのメタデータを更新する
					NamedNodeMap map = item.getAttributes();
					Node work = map.getNamedItem("file");
					if (work != null) {
						String fname = work.getNodeValue();
						File wfile = new File(fname);
						if (wfile.exists() && wfile.canRead()) {
							NodeReadLoader child = new NodeReadLoader();
							try {
								SQLNetServer.logger.info("including metadata " + fname);
								child.createMetaDataSet(wfile, meta, properties);
							} catch (ParserConfigurationException e) {
								SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
							} catch (FactoryConfigurationError e) {
								SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
							} catch (SAXException e) {
								SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
							} catch (IOException e) {
								SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
							}
						}
					}
				} else {
					// なにか新しい要素
					proessOtherNodeOfSet(item, meta, properties);
				}
			}
		}
		// 返す
		return meta;
	}
	/**
	 * SQL列の作成
	 * 
	 * @param node ノード
	 * @param column SQL列(新規の場合はnull)
	 * @return SQL列
	 */
	protected SQLCobolColumn createSQLCobolColumn(Node node, SQLCobolRecordMetaData meta, SQLCobolColumn column) {
		NamedNodeMap map = node.getAttributes();
		if (column == null) {// 新規の列
			// name
			Node name = map.getNamedItem("name");
			if (name != null) {
				String namestr = name.getNodeValue().trim();
				try {
					CobolColumn work = meta.getColumn(namestr);
					if (work instanceof SQLCobolColumn) {
						column = (SQLCobolColumn) work;
					}
				} catch (CobolRecordException e) {
					// Not Operation
				}
			}
			if (column == null) {
				column = new SQLCobolColumn(meta);
			}
		}
		// 基本設定
		createCobolColumn(node, column);
		// SQL列名
		Node originalColumnName = map.getNamedItem("originalColumnName");
		if (originalColumnName != null) {
			column.setOriginalColumnName(originalColumnName.getNodeValue());
		}
		// デフォルトの文字列
		Node defaultString = map.getNamedItem("defaultString");
		if (defaultString != null) {
			column.setDefaultString(defaultString.getNodeValue());
		}
		// 読み取り不可
		Node readIgnore = map.getNamedItem("readIgnore");
		if (readIgnore != null) {
			String readIgnorestr = readIgnore.getNodeValue();
			boolean readIgnoreBool = Boolean.getBoolean(readIgnorestr);
			column.setReadIgnore(readIgnoreBool);
		}
		// 上書き不可
		Node rewriteIgnore = map.getNamedItem("rewriteIgnore");
		if (rewriteIgnore != null) {
			String rewriteIgnorestr = rewriteIgnore.getNodeValue();
			boolean rewriteIgnoreBool = Boolean.getBoolean(rewriteIgnorestr);
			column.setRewriteIgnore(rewriteIgnoreBool);
		}
		// 新規書き込み不可
		Node writeIgnore = map.getNamedItem("writeIgnore");
		if (writeIgnore != null) {
			String writeIgnorestr = writeIgnore.getNodeValue();
			boolean writeIgnoreBool = Boolean.getBoolean(writeIgnorestr);
			column.setWriteIgnore(writeIgnoreBool);
		}
		return column;
	}
	/**
	 * ノード直下のテキストを取得する
	 * 
	 * @param node ノード
	 * @return 文字列
	 */
	protected String getNodeString(Node node) {
		StringBuffer retvalue = new StringBuffer();
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// 別名
				retvalue.append(child.getNodeValue().trim());
			}
		}
		return retvalue.toString().trim();
	}
	/**
	 * ノードのテキストを取得する
	 * 
	 * @param node ノード
	 * @return ノードのテキスト
	 */
	protected String getNodeText(Node node) {
		// 子ノードの探索
		StringBuffer buffer = new StringBuffer();
		NodeList children = node.getChildNodes();
		int size = children.getLength();
		for (int i = 0; i < size; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// 別名
				buffer.append(child.getNodeValue().trim());
			}
		}
		return buffer.toString().trim();
	}
	/**
	 * その他の処理・・・とりあえず何もしない
	 * 
	 * @param node ノード
	 * @param meta メタデータ
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
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (InstantiationException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
		}
	}
	/**
	 * 何か分からないノードが来たよ?
	 * 
	 * @param node ノード
	 * @param meta メタデータセット
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
