package k_kim_mg.sa4cob2db.WebSample;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.sql.SQLFileServer;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;
public class SimpleProcessFilter implements Filter {
	/** Init Parameter name of Metadata File */
	public static final String ACM_CONFFILE = "ACM_CONFFILE";
	/** Parameter name of Input layout */
	public static final String INPUT_LAYOUT = "INPUT_LAYOUT";
	/** Parameter name of Output layout */
	public static final String OUTPUT_LAYOUT = "OUTPUT_LAYOUT";
	/** Parameter name of Process */
	public static final String PROCESS_NAME = "PROCESS_NAME";
	/** Environment values */
	public static final String PRPFNAME = "environment.properties";
	private FilterConfig config = null;
	private ServletContext context = null;
	private CobolRecordMetaDataSet metaset;
	private Map<String, Process> processes = new HashMap<String, Process>();
	private Properties prop;
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		for (Process process : processes.values()) {
			process.destroy();
		}
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
			String processName = req.getParameter(PROCESS_NAME);
			if (processName != null) {
				String inputLayoutName = req.getParameter(INPUT_LAYOUT);
				if (inputLayoutName != null) {
					String outputLayoutName = req.getParameter(OUTPUT_LAYOUT);
					if (outputLayoutName == null) {
						outputLayoutName = inputLayoutName;
					}
					CobolRecordMetaData inputLayout = metaset.getMetaData(inputLayoutName);
					CobolRecordMetaData outputLayout = metaset.getMetaData(outputLayoutName);
					//
					Process process = processes.get(processName);
					if (process != null) {
						try {
							process.exitValue();
							process = null;
							processes.remove(process);
						} catch (Exception e) {
						}
					}
					if (process == null) {
						ProcessBuilder builder = createProcessBuilder(processName);
						// //////////////////////////////////////////////////////////////////
						String name, valu;
						Enumeration<Object> keys = prop.keys();
						while (keys.hasMoreElements()) {
							name = keys.nextElement().toString();
							valu = prop.getProperty(name);
							builder.environment().put(name, valu);
						}
						process = builder.start();
						processes.put(processName, process);
					}
					if (process != null) {
						//
						OutputStream out = process.getOutputStream();
						InputStream in = process.getInputStream();
						BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						setRequest2Stream(inputLayout, req, out);
						setStream2Request(outputLayout, in, req);
					}
				}
			}
		} catch (Exception ex) {
			context.log("doFilter", ex);
		}
		//
		chain.doFilter(req, res);
	}
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
		String filename = config.getInitParameter(ACM_CONFFILE);
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
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PRPFNAME);
		try {
			prop.load(in);
		} catch (IOException iex) {
			iex.printStackTrace();
		}
	}
	/**
	 * set parameter values to OutputStream
	 * 
	 * @param meta metadata
	 * @param req request
	 * @param out stream
	 */
	protected void setRequest2Stream(CobolRecordMetaData meta, ServletRequest req, OutputStream out) {
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
				}
			}
		}
		try {
			rec.getRecord(byt);
			context.log("rs rec:" + byt.length + ":" + new String(byt));
			out.write(byt);
			out.write('\n');
			out.flush();
		} catch (CobolRecordException e) {
			context.log("ERROR", e);
		} catch (IOException e) {
			context.log("ERROR", e);
		}
	}
	/**
	 * set stream value to response
	 * 
	 * @param meta metadata
	 * @param in stream
	 * @param res response
	 */
	protected void setStream2Request(CobolRecordMetaData meta, InputStream in, ServletRequest req) {
		// InputStreamReader r = new InputStreamReader(in);
		// BufferedReader br = new BufferedReader(r);
		// try {
		// while (true) {
		// context.log("BR:" + br.readLine());
		// }
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		CobolRecord rec = createCobolRecord(meta);
		byte[] byt = new byte[meta.getRowSize()];
		try {
			in.read(byt);
			context.log("sr rec:" + byt.length + ":" + new String(byt));
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
				}
			}
		} catch (CobolRecordException e) {
			context.log("ERROR", e);
		} catch (IOException e) {
			context.log("ERROR", e);
		}
	}
}
