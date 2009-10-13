// package k_kim_mg.sa4cob2db.sample;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

import org.xml.sax.SAXException;
public class ServletSampleTool1 {
	/** コンテンツ */
	protected Hashtable<String, WebInterface1> contents;
	/** メタデータセット */
	protected CobolRecordMetaDataSet metaSet;
	/** セッションの管理を行う */
	protected Hashtable<String, CobolRecord> sessions;
	/** サブルーチン */
	protected Hashtable<String, WebInterface1> subroutines;
	/** Webインターフェース */
	protected Hashtable<String, WebInterface1> webinterfaces;
	/** ヘッダー名 */
	public String MSGHEADNAME;
	/**
	 * コンストラクタ
	 */
	public ServletSampleTool1(String metaString) {
		super();
		MSGHEADNAME = "msghead";
		// 初期化
		webinterfaces = new Hashtable<String, WebInterface1>();
		subroutines = new Hashtable<String, WebInterface1>();
		contents = new Hashtable<String, WebInterface1>();
		// メタデータファイル名
		Properties properties = new Properties();
		// properties.setProperty("metafile", "test.xml");
		// String metaString = properties.getProperty("metafile",
		// DEFAUT_CONFIG);
		File metaFile = new File(metaString);
		System.err.println(metaFile.getAbsolutePath());
		// メタデータ情報の取得
		NodeReadLoader nodeLoader = new NodeReaderPlus1(this);
		metaSet = new SampleMetaDataSet1();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaSet, properties);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// セッション管理テーブルの作成
		sessions = new Hashtable<String, CobolRecord>();
	}
	/** Webインターフェースの追加 */
	protected void addWebInterface(WebInterface1 face1) {
		webinterfaces.put(face1.getName(), face1);
		subroutines.put(face1.getSubroutine(), face1);
		contents.put(face1.getContent(), face1);
	}
	/** コンテンツから取得 */
	public WebInterface1 getWebInterfaceByContent(String content) {
		return contents.get(content);
	}
	/** 名前から取得 */
	public WebInterface1 getWebInterfaceByName(String name) {
		return webinterfaces.get(name);
	}
	/** サブルーチンから取得 */
	public WebInterface1 getWebInterfaceBySubroutine(String subroutine) {
		return subroutines.get(subroutine);
	}
	/**
	 * レコードからリクエストへの変換
	 * @param record レコード
	 * @param request リクエスト（リクエストの属性に値をセットする）
	 * @throws ServletException 例外１
	 * @throws IOException 例外２
	 * @throws CobolRecordException 例外３
	 */
	protected void setRecordToRequest(CobolRecord record, HttpServletRequest request) throws ServletException, IOException, CobolRecordException {
		CobolRecordMetaData meta = record.getMetaData();
		for (int i = 0; i < meta.getColumnCount(); i++) {
			CobolColumn column = meta.getColumn(i);
			String columnValue = record.getString(column);
			request.setAttribute(column.getName(), columnValue);
		}
	}
	/**
	 * リクエストからレコード形式への変換
	 * @param request リクエスト
	 * @param record レコード
	 * @throws ServletException 例外１
	 * @throws IOException 例外２
	 */
	protected void setRequestToRecord(HttpServletRequest request, CobolRecord record) throws ServletException, IOException {
		CobolRecordMetaData meta = null;
		try {
			meta = record.getMetaData();
		} catch (CobolRecordException ex) {
			return;
		}
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = request.getParameter(key);
			try {
				int id = meta.findColumn(key);
				CobolColumn column = meta.getColumn(id);
				record.updateObject(column, value);
			} catch (CobolRecordException e) {
				// 列が見付からなかった場合など
				// e.printStackTrace();
			}
		}
	}
	/**
	 * ヘッダーレコードの取得
	 * @param request リクエスト
	 * @return ヘッダーレコード
	 */
	public CobolRecord getHeaderRecord(HttpServletRequest request) {
		Object wrk = null;
		CobolRecord ret = null;
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		wrk = sessions.get(sessionId);
		if (wrk == null) {
			ret = createRecord(MSGHEADNAME);
			sessions.put(sessionId, ret);
		} else {
			ret = (CobolRecord) wrk;
		}
		return ret;
	}
	/**
	 * 配列のラッパを返す
	 * @param metaname メタデータ名
	 * @return コボルのレコード形式
	 */
	public CobolRecord createRecord(String metaname) {
		CobolRecordMetaData meta = metaSet.getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}
