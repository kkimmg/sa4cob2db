package k_kim_mg.sa4cob2db.WebSample.RMI;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.FactoryConfigurationError;
import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.WebSample.SimpleProcessFilter;
import k_kim_mg.sa4cob2db.WebSample.WebInterface1;
import k_kim_mg.sa4cob2db.sql.SQLFileServer;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;
public class SimpleRMIFilter implements Filter {
	@SuppressWarnings("unused")
	private FilterConfig config = null;
	private ServletContext context = null;
	private CobolRecordMetaDataSet metaset;
	private Properties prop;
	private CobSubServer1 stub;
	/** ヘッダー名 */
	public String MSGHEADNAME;
	/** セッションの管理を行う */
	protected Hashtable<String, CobolRecord> sessions;
	/**
	 * Create Cobol Record
	 * 
	 * @param meta metadata
	 * @return record
	 */
	protected CobolRecord createCobolRecord(CobolRecordMetaData meta) {
		return new DefaultCobolRecord(meta);
	}
	/**
	 * Create NodeReadLoader
	 * 
	 * @return NodeReadLoader to read Metadata info
	 */
	protected NodeReadLoader createNodeReadLoader() {
		return new NodeReadLoader();
	}
	/**
	 * Create ProcessBuilder
	 * 
	 * @param name command
	 * @return ProcessBuilder
	 */
	protected ProcessBuilder createProcessBuilder(String name) {
		return new ProcessBuilder(name);
	}
	/**
	 * Create SQLFileServer
	 * 
	 * @return SQLFileServer to create CobolRecordMetaDataSet
	 */
	protected SQLFileServer createSQLFileServer() {
		return new SQLFileServer();
	}
	// private Map
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		try {
			String processName = req.getParameter(SimpleProcessFilter.PROCESS_NAME);
			if (processName != null) {
				String inputLayoutName = req.getParameter(SimpleProcessFilter.INPUT_LAYOUT);
				if (inputLayoutName != null) {
					String outputLayoutName = req.getParameter(SimpleProcessFilter.OUTPUT_LAYOUT);
					if (outputLayoutName == null) {
						outputLayoutName = inputLayoutName;
					}
					CobolRecordMetaData inputLayout = metaset.getMetaData(inputLayoutName);
					CobolRecordMetaData outputLayout = metaset.getMetaData(outputLayoutName);
					//
					CobolRecord hrec = getHeaderRecord((HttpServletRequest) req);
					byte[] head = new byte[hrec.getMetaData().getRowSize() + 1];
					hrec.getRecord(head);
					//
					byte[] in = setRequest2Bytes(inputLayout, req);
					byte[] out = new byte[outputLayout.getRowSize() + 1];
					//
					stub.callCobSub(processName, head, in, out);
					//
					hrec.setRecord(head);
					setByte2Request(outputLayout, out, req);
				}
			}
		} catch (Exception ex) {
			context.log("doFilter", ex);
			addExceptionMessage(req, ex);
		}
		req.setAttribute(SimpleProcessFilter.ACM_ERRORS, req.getAttribute(SimpleProcessFilter.ACM_ERRORS));
		//
		chain.doFilter(req, res);
	}
	private void addExceptionMessage(ServletRequest req, Exception ex) {
		req.setAttribute(SimpleProcessFilter.ACM_ERROR, ex.getMessage());
		req.setAttribute(SimpleProcessFilter.ACM_ERRORS, req.getAttribute(SimpleProcessFilter.ACM_ERRORS) + ex.getMessage());
	}
	/** RMI Host Name */
	public static final String RMI_HOST = "RMI_HOST";
	/** RMI Host Name */
	public static final String SRV_NAME = "RMI_HOST";
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		this.context = config.getServletContext();
		SQLFileServer sqlfileserver = createSQLFileServer();
		metaset = sqlfileserver.getMetaDataSet();
		NodeReadLoader nodeLoader = createNodeReadLoader();
		String filename = config.getInitParameter(SimpleProcessFilter.ACM_CONFFILE);
		File metaFile = new File(filename);
		Properties properties = new Properties();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
		} catch (Exception e) {
			context.log("ERROR", e);
		} catch (FactoryConfigurationError e) {
			context.log("ERROR", e);
		}
		prop = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(SimpleProcessFilter.PRPFNAME);
		try {
			prop.load(in);
		} catch (IOException iex) {
			iex.printStackTrace();
		}
		//
		String host = config.getInitParameter(RMI_HOST);
		if (host == null) {
			host = "localhost";
		}
		String name = config.getInitParameter(SRV_NAME);
		if (name == null) {
			name = RMIStarter.DEFAULT_NAME;
		}
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			stub = (CobSubServer1) registry.lookup(name);
		} catch (Exception e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}
		//
		MSGHEADNAME = "msghead";
		sessions = new Hashtable<String, CobolRecord>();
	}
	/**
	 * レコードからリクエストへの変換
	 * 
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
	 * 
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
		@SuppressWarnings("unchecked")
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = request.getParameter(key);
			try {
				int id = meta.findColumn(key);
				CobolColumn column = meta.getColumn(id);
				record.updateObject(column, value);
			} catch (CobolRecordException e) {
				// e.printStackTrace();
			}
		}
	}
	/**
	 * ヘッダーレコードの取得
	 * 
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
	 * 
	 * @param metaname メタデータ名
	 * @return コボルのレコード形式
	 */
	public CobolRecord createRecord(String metaname) {
		CobolRecordMetaData meta = metaset.getMetaData(metaname);
		return createCobolRecord(meta);
	}
	@Override
	public void destroy() {
	}
	/**
	 * set parameter values to OutputStream
	 * 
	 * @param meta metadata
	 * @param req request
	 * @param out stream
	 */
	protected byte[] setRequest2Bytes(CobolRecordMetaData meta, ServletRequest req) {
		CobolRecord rec = createCobolRecord(meta);
		byte[] byt = new byte[meta.getRowSize()];
		for (int i = 0; i < byt.length; i++) {
			byt[i] = ' ';
		}
		try {
			rec.setRecord(byt);
		} catch (CobolRecordException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < meta.getColumnCount(); i++) {
			CobolColumn column = meta.getColumn(i);
			String val = req.getParameter(column.getName());
			if (val != null) {
				try {
					rec.updateObject(column, val);
					context.log("rs:" + column.getName() + "=" + val + "/" + rec.getString(column));
				} catch (CobolRecordException e) {
					context.log("ERROR", e);
					addExceptionMessage(req, e);
				} catch (NumberFormatException e) {
					context.log("WARNING", e);
					addExceptionMessage(req, e);
				}
			}
		}
		try {
			rec.getRecord(byt);
			context.log("rs rec:" + byt.length + ":" + new String(byt));
			req.setAttribute("RS_REC", "rs rec:" + byt.length + ":" + new String(byt) + ":");
		} catch (CobolRecordException e) {
			context.log("ERROR", e);
			addExceptionMessage(req, e);
		}
		return byt;
	}
	/**
	 * set stream value to response
	 * 
	 * @param meta metadata
	 * @param in stream
	 * @param res response
	 */
	protected void setByte2Request(CobolRecordMetaData meta, byte[] byt, ServletRequest req) {
		StringBuffer buf = new StringBuffer();
		CobolRecord rec = createCobolRecord(meta);
		try {
			rec.setRecord(byt);
			for (int i = 0; i < meta.getColumnCount(); i++) {
				String str = "";
				CobolColumn column = meta.getColumn(i);
				try {
					switch (column.getType()) {
					case CobolColumn.TYPE_DOUBLE:
					case CobolColumn.TYPE_FLOAT:
						double d = rec.getDouble(column);
						str = String.valueOf(d);
					case CobolColumn.TYPE_LONG:
					case CobolColumn.TYPE_INTEGER:
						long l = rec.getLong(column);
						str = String.valueOf(l);
					default:
						str = rec.getString(column);
					}
					req.setAttribute(column.getName(), str);
					context.log("sr:" + column.getName() + "=" + str);
				} catch (CobolRecordException e) {
					context.log("ERROR", e);
					buf.append('\n');
					buf.append(e.getMessage());
				}
			}
		} catch (CobolRecordException e) {
			context.log("ERROR", e);
			buf.append("CobolRecordException:");
			buf.append(e.getMessage());
			buf.append("<br>\n");
		}
		String errors = req.getAttribute(SimpleProcessFilter.ACM_ERRORS).toString();
		req.setAttribute(SimpleProcessFilter.ACM_ERRORS, (errors == null ? buf.toString() : errors + "<br>\n" + buf.toString()));
	}
}
