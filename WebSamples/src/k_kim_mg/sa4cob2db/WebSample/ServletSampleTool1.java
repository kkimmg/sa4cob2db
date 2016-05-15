package k_kim_mg.sa4cob2db.WebSample;

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
	/** Map */
	protected Hashtable<String, WebInterface1> contents;
	/** Metadata Set */
	protected CobolRecordMetaDataSet metaSet;
	/** Map */
	protected Hashtable<String, CobolRecord> sessions;
	/** Map */
	protected Hashtable<String, WebInterface1> subroutines;
	/** Map */
	protected Hashtable<String, WebInterface1> webinterfaces;
	/** Header layout name */
	public String MSGHEADNAME;

	/**
	 * Constructor
	 */
	public ServletSampleTool1(String metaString) {
		super();
		MSGHEADNAME = "msghead";
		// maps
		webinterfaces = new Hashtable<String, WebInterface1>();
		subroutines = new Hashtable<String, WebInterface1>();
		contents = new Hashtable<String, WebInterface1>();
		// properties
		Properties properties = new Properties();
		// properties.setProperty("metafile", "test.xml");
		// String metaString = properties.getProperty("metafile",
		// DEFAUT_CONFIG);
		File metaFile = new File(metaString);
		System.err.println(metaFile.getAbsolutePath());
		// meta
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
		// sessions
		sessions = new Hashtable<String, CobolRecord>();
	}

	/** Map */
	protected void addWebInterface(WebInterface1 face1) {
		webinterfaces.put(face1.getName(), face1);
		subroutines.put(face1.getSubroutine(), face1);
		contents.put(face1.getContent(), face1);
	}

	/** from Map */
	public WebInterface1 getWebInterfaceByContent(String content) {
		return contents.get(content);
	}

	/** from Map */
	public WebInterface1 getWebInterfaceByName(String name) {
		return webinterfaces.get(name);
	}

	/** from Map */
	public WebInterface1 getWebInterfaceBySubroutine(String subroutine) {
		return subroutines.get(subroutine);
	}

	protected void setRecordToRequest(CobolRecord record, HttpServletRequest request) throws ServletException, IOException, CobolRecordException {
		CobolRecordMetaData meta = record.getMetaData();
		for (int i = 0; i < meta.getColumnCount(); i++) {
			CobolColumn column = meta.getColumn(i);
			String columnValue = record.getString(column);
			request.setAttribute(column.getName(), columnValue);
		}
	}

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
				// Do Nothing
			}
		}
	}

	/**
	 * get Header Record. 
	 * @param request Request
	 * @return CobolRecord
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
	 * Create Record.
	 * @param metaname Metadata Name
	 * @return CobolRecord
	 */
	public CobolRecord createRecord(String metaname) {
		CobolRecordMetaData meta = metaSet.getMetaData(metaname);
		CobolRecord ret = new DefaultCobolRecord(meta);
		return ret;
	}
}
