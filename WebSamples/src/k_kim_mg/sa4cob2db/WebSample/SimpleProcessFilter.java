package k_kim_mg.sa4cob2db.WebSample;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.sql.SQLFileServer;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

public class SimpleProcessFilter implements Filter {
	private class ErrorReader implements Runnable {
		private InputStream err;
		private ServletRequest req;

		public ErrorReader(ServletRequest req, InputStream err) {
			this.err = err;
			this.req = req;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			try {
				boolean cont = true;
				BufferedReader reader = new BufferedReader(new InputStreamReader(err));
				while (cont && reader.ready()) {
					req.setAttribute(ACM_ERRORS, req.getAttribute(ACM_ERRORS) + "<br>\n" + reader.readLine());
				}
			} catch (Exception e) {
				try {
					context.log("ERROR", e);
					if (req != null) {
						req.setAttribute(ACM_ERRORS, req.getAttribute(ACM_ERRORS) + "\n<br>" + e.getMessage());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
	}

	private class StreamWrapper implements Runnable {
		private StringBuffer buf;
		private byte[] byt;
		private InputStream in;

		public StreamWrapper(InputStream in, byte[] byt, StringBuffer buf) {
			this.in = in;
			this.byt = byt;
			this.buf = buf;
		}

		@Override
		public void run() {
			try {
				in.read(byt);
			} catch (IOException e) {
				context.log("ERROR", e);
				buf.append("IOException:");
				buf.append(e.getMessage());
				buf.append("\n<br>");
			}
		}

	}

	/** Init Parameter name of Metadata File */
	public static final String ACM_CONFFILE = "ACM_CONFFILE";
	public static String ACM_ERROR = "ACM_ERROR";
	public static String ACM_ERRORS = "ACM_ERRORS";
	/** Parameter name of Input layout */
	public static final String INPUT_LAYOUT = "INPUT_LAYOUT";
	/** Parameter name of Output layout */
	public static final String OUTPUT_LAYOUT = "OUTPUT_LAYOUT";
	/** Parameter name of Process */
	public static final String PROCESS_NAME = "PROCESS_NAME";
	/** Environment values */
	public static final String PRPFNAME = "environment.properties";
	public static final int WAIT_MILLS = 1000;
	public static final String INIT_PROCESS = "INITPROCESS";

	@SuppressWarnings("unused")
	private FilterConfig config = null;

	private ServletContext context = null;

	private CobolRecordMetaDataSet metaset;

	private Map<String, Process> processes = new HashMap<String, Process>();

	private Properties prop;

	private void addExceptionMessage(ServletRequest req, Exception ex) {
		req.setAttribute(ACM_ERROR, ex.getMessage());
		req.setAttribute(ACM_ERRORS, req.getAttribute(ACM_ERRORS) + ex.getMessage());
	}

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
	 * Creates Process
	 * 
	 * @param processName name
	 * @return process
	 * @throws IOException IOException
	 */
	private Process createProcess(String processName) throws IOException {
		ProcessBuilder builder = createProcessBuilder(processName);
		// //////////////////////////////////////////////////////////////////
		String name;
		String valu;
		Enumeration<Object> keys = prop.keys();
		while (keys.hasMoreElements()) {
			name = keys.nextElement().toString();
			valu = prop.getProperty(name);
			builder.environment().put(name, valu);
		}
		Process process = builder.start();
		processes.put(processName, process);
		return process;
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
							processes.remove(processName);
						} catch (Exception e) {
							req.setAttribute(ACM_ERRORS, e.getMessage());
						}
					}
					if (process == null) {
						process = createProcess(processName);
					}
					if (process != null) {
						//
						OutputStream out = process.getOutputStream();
						setRequest2Stream(inputLayout, req, out);
						InputStream err = process.getErrorStream();
						readError(req, err);

						InputStream in = process.getInputStream();
						setStream2Request(outputLayout, in, req);
						readError(req, err);
					}

				}
			}
		} catch (Exception ex) {
			context.log("doFilter", ex);
			addExceptionMessage(req, ex);
		}
		req.setAttribute(ACM_ERRORS, req.getAttribute(ACM_ERRORS));
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
		String processNames = config.getInitParameter(INIT_PROCESS);
		if (processNames != null && processNames.trim().length() > 0) {
			String[] processNameArray = processNames.split(":");
			for (String processName : processNameArray) {
				try {
					createProcess(processName);
				} catch (IOException e) {
					context.log("INIT", e);
				}
			}
		}
	}

	private void readError(ServletRequest req, InputStream err) {
		Thread th = new Thread(new ErrorReader(req, err));
		try {
			th.start();
			th.join(WAIT_MILLS);
		} catch (Exception ex) {
			context.log("doFilter", ex);
			addExceptionMessage(req, ex);
			try {
				th.interrupt();
			} catch (Exception e2) {
				// Do Nothing
			}
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
			out.write(byt);
			out.write('\n');
			out.flush();
		} catch (CobolRecordException e) {
			context.log("ERROR", e);
			addExceptionMessage(req, e);
		} catch (IOException e) {
			context.log("ERROR", e);
			addExceptionMessage(req, e);
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
		StringBuffer buf = new StringBuffer();
		CobolRecord rec = createCobolRecord(meta);

		byte[] byt = new byte[meta.getRowSize() + 1];

		Thread th = new Thread(new StreamWrapper(in, byt, buf));

		try {

			th.start();

			th.join(WAIT_MILLS);

			context.log("sr rec:" + byt.length + ":" + new String(byt));
			req.setAttribute("SR_REC", "sr rec:" + byt.length + ":" + new String(byt) + ":");

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
						break;
					case CobolColumn.TYPE_LONG:
					case CobolColumn.TYPE_INTEGER:
						long l = rec.getLong(column);
						str = String.valueOf(l);
						break;
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
		} catch (InterruptedException e) {
			context.log("ERROR", e);
			buf.append("InterruptedException:");
			buf.append(e.getMessage());
			buf.append("<br>\n");
			try {
				th.interrupt();
			} catch (Exception ex) {
				context.log("ERROR", e);
				buf.append("Exception:");
				buf.append(ex.getMessage());
				buf.append("<br>\n");
			}
		}
		String errors = req.getAttribute(ACM_ERRORS).toString();
		req.setAttribute(ACM_ERRORS, (errors == null ? buf.toString() : errors + "<br>\n" + buf.toString()));
	}
}
