package k_kim_mg.sa4cob2db.WebSample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.sql.SQLFileServer;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;
import org.xml.sax.SAXException;

public class SimpleProcessFilter implements Filter {
	/**Parameter name of Input layout*/
	public static final String INPUT_LAYOUT = "INPUT_LAYOUT";
 	/** Init Parameter name of Metadata File*/
	public static final String META_FILE = "META_FILE";
	/**Parameter name of Output layout*/
	public static final String OUTPUT_LAYOUT = "OUTPUT_LAYOUT";
	/**Parameter name of Process*/
	public static final String PROCESS_NAME = "PROCESS_NAME";

	private CobolRecordMetaDataSet metaset;
	private Map<String, Process> processes = new HashMap<String, Process> ();
	/**
	 * Create Cobol Record
	 * @param meta metadata
	 * @return record
	 */
	protected CobolRecord createCobolRecord (CobolRecordMetaData meta) {
		return new DefaultCobolRecord(meta);
	}
	/**
	 * Create NodeReadLoader
	 * @return NodeReadLoader to read Metadata info 
	 */
	protected NodeReadLoader createNodeReadLoader () {
		return new NodeReadLoader();
	}
	/**
	 * Create ProcessBuilder
	 * @param name command
	 * @return ProcessBuilder
	 */
	protected ProcessBuilder createProcessBuilder (String name) {
		return new ProcessBuilder(name);
	}
	/**
	 * Create SQLFileServer
	 * @return SQLFileServer to create CobolRecordMetaDataSet
	 */
	protected SQLFileServer createSQLFileServer () {
		return new SQLFileServer();
	}
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		for (Process process : processes.values()) {
			process.destroy();
		}
	}
	//private Map
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		String processName = req.getParameter(PROCESS_NAME);
		String inputLayoutName = req.getParameter(INPUT_LAYOUT);
		String outputLayoutName = req.getParameter(OUTPUT_LAYOUT);
		if (outputLayoutName == null) {
			outputLayoutName = inputLayoutName;
		}
		CobolRecordMetaData inputLayout = metaset.getMetaData(inputLayoutName);
		CobolRecordMetaData outputLayout = metaset.getMetaData(outputLayoutName);
		//
		Process process = processes.get(processName);
		if (process == null) {
			ProcessBuilder builder = createProcessBuilder(processName);
			process = builder.start();
			processes.put(processName, process);
		}
		//
		OutputStream out = process.getOutputStream();
		setRequest2Stream(inputLayout, req, out);
		//
		InputStream in = process.getInputStream();
		setStream2Request(outputLayout, in, req);
		//
		chain.doFilter(req, res);
	}
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		SQLFileServer sqlfileserver = createSQLFileServer();
		metaset = sqlfileserver.getMetaDataSet();
		NodeReadLoader nodeLoader = createNodeReadLoader();
		String filename = config.getInitParameter(META_FILE);
		File metaFile = new File(filename);
		Properties properties = new Properties();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * set parameter values to OutputStream
	 * @param meta metadata
	 * @param req request
	 * @param out stream
	 */
	protected void setRequest2Stream (CobolRecordMetaData meta, ServletRequest req, OutputStream out) {
		CobolRecord rec = createCobolRecord(meta);
		byte[] byt = new byte[meta.getRowSize()];
		for (int i = 0; i < meta.getColumnCount(); i++) {
			CobolColumn column = meta.getColumn(i);
			String val = req.getParameter(column.getName());
			if (val != null) {
				try {
					rec.updateObject(column, val);
				} catch (CobolRecordException e) {
					// ignore
					SQLNetServer.logger.log(Level.SEVERE, "ERROR", e);
				}
			}
		}
		try {
			rec.getRecord(byt);
			out.write(byt);
			out.write('\n');
			out.flush();
		} catch (CobolRecordException e) {
			// ignore
			SQLNetServer.logger.log(Level.SEVERE, "ERROR", e);
		} catch (IOException e) {
			// ignore
			SQLNetServer.logger.log(Level.SEVERE, "ERROR", e);
		}		
	}
	/**
	 * set stream value to response
	 * @param meta metadata
	 * @param in stream
	 * @param res response
	 */
	protected void setStream2Request(CobolRecordMetaData meta, InputStream in, ServletRequest req) {
		CobolRecord rec = createCobolRecord(meta);
		byte[] byt = new byte[meta.getRowSize()];
		try {
			in.read(byt);
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
				} catch (CobolRecordException e) {
					// ignore
					SQLNetServer.logger.log(Level.SEVERE, "ERROR", e);
				}
			}
		} catch (CobolRecordException e) {
			// ignore
			SQLNetServer.logger.log(Level.SEVERE, "ERROR", e);
		} catch (IOException e) {
			// ignore
			SQLNetServer.logger.log(Level.SEVERE, "ERROR", e);
		}
	}
}
