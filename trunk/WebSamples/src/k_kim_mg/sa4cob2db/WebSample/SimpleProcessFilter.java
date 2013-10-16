package k_kim_mg.sa4cob2db.WebSample;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
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
	/** Parameter name of Input layout */
	public static final String INPUT_LAYOUT = "INPUT_LAYOUT";
	/** Init Parameter name of Metadata File */
	public static final String ACM_CONFFILE = "ACM_CONFFILE";
	/** Parameter name of Output layout */
	public static final String OUTPUT_LAYOUT = "OUTPUT_LAYOUT";
	/** Parameter name of Process */
	public static final String PROCESS_NAME = "PROCESS_NAME";

	private CobolRecordMetaDataSet metaset;
	private Map<String, Process> processes = new HashMap<String, Process>();
	private FilterConfig config = null;
	private ServletContext context = null;

	/**
	 * Create Cobol Record
	 * 
	 * @param meta
	 *            metadata
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
	 * @param name
	 *            command
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

	public static void main(String[] argv) {
		(new SimpleProcessFilter()).main();
	}

	public void main() {
		SQLFileServer sqlfileserver = createSQLFileServer();
		metaset = sqlfileserver.getMetaDataSet();
		NodeReadLoader nodeLoader = createNodeReadLoader();
		String filename = "/home/kenji/workspace/sa4cob2db/conf/metafile.xml";
		File metaFile = new File(filename);
		Properties properties = new Properties();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		try {
			String processName = "/home/kenji/workspace/sa4cob2db/tests/cobs/jni/JNIDYNTEST";
			if (processName != null) {
				String inputLayoutName = "SCR_RECORD";
				if (inputLayoutName != null) {
					String outputLayoutName = "SCR_RECORD";
					if (outputLayoutName == null) {
						outputLayoutName = inputLayoutName;
					}
					CobolRecordMetaData inputLayout = metaset.getMetaData(inputLayoutName);
					CobolRecordMetaData outputLayout = metaset.getMetaData(outputLayoutName);
					//
					Process process = processes.get(processName);
					if (process == null) {
						ProcessBuilder builder = createProcessBuilder(processName);
						// //////////////////////////////////////////////////////////////////
						String name, valu;
						// ///////////////////////////////////////////////////////////////
						name = "LD_LIBRARY_PATH";
						valu = "/usr/lib/jvm/java-1.7.0-openjdk-i386/jre/lib/i386/server:/home/kenji/workspace/sa4cob2db/cs:/home/kenji/workspace/sa4cob2db/command:/usr/local/lib";
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "COB_LIBRARY_PATH";
						valu = "/home/kenji/workspace/sa4cob2db/cs:/home/kenji/workspace/sa4cob2db/tests/cobs:/usr/lib/jvm/java-1.7.0-openjdk-i386/jre/lib/i386/server:/home/kenji/workspace/sa4cob2db/cs:/home/kenji/workspace/sa4cob2db/command:/usr/local/lib";
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "CLASSPATH";
						valu = "/home/kenji/workspace/sa4cob2db/sa4cob2db.jar:/home/kenji/workspace/sa4cob2db/cobpp.jar:/usr/share/java/postgresql-jdbc3.jar";
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "ACM_USERNAME";
						valu = "testuser";
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "ACM_PASSWORD";
						valu = "password";
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "ACM_SESSIONLISTENERS";
						valu = "ACMSessionEventAdapterTest";
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						process = builder.start();
						processes.put(processName, process);
					}
					if (process != null) {
						//
						OutputStream out = process.getOutputStream();
						InputStream in = process.getInputStream();
						setRequest2Stream(inputLayout, out);
						setStream2Request(outputLayout, in);
						//
						process.destroy();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void setRequest2Stream(CobolRecordMetaData meta, OutputStream out) {
		CobolRecord rec = createCobolRecord(meta);
		byte[] byt = new byte[meta.getRowSize()];
		for (int i = 0; i < byt.length; i++) {
			byt[i] = ' ';
		}
		try {
			rec.setRecord(byt);
			rec.updateObject(meta.getColumn("SCR_PROC"), "2");
			System.out.println("rs:" + meta.getColumn("SCR_PROC").getName() + ":" + rec.getString(meta.getColumn("SCR_PROC")));
			rec.updateObject(meta.getColumn("SCR_ID"), 2);
			System.out.println("rs:" + meta.getColumn("SCR_ID").getName() + ":" + rec.getString(meta.getColumn("SCR_ID")));
		} catch (CobolRecordException e) {
			e.printStackTrace();
		}

		try {
			PrintWriter pw = new PrintWriter(out);
			System.out.println("rs:" + meta.getColumn("SCR_ID").getName() + ":" + rec.getString(meta.getColumn("SCR_ID")));
			rec.getRecord(byt);
			System.out.println("rs rec:" + byt.length + ":" + new String(byt));
			out.write(byt);
			out.write('\n');
			out.flush();
			pw.println(new String(byt));
		} catch (CobolRecordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void setStream2Request(CobolRecordMetaData meta, InputStream in) {
		CobolRecord rec = createCobolRecord(meta);
		byte[] byt = new byte[meta.getRowSize()];
		try {
			in.read(byt);
			System.out.println("sr rec:" + byt.length + ":" + new String(byt));
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
					System.out.println("sr:" + column.getName() + "=" + str);
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
					if (process == null) {
						ProcessBuilder builder = createProcessBuilder(processName);
						// //////////////////////////////////////////////////////////////////
						String name, valu;
						// ///////////////////////////////////////////////////////////////
						name = "LD_LIBRARY_PATH";
						valu = config.getInitParameter(name);
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "COB_LIBRARY_PATH";
						valu = config.getInitParameter(name);
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "CLASSPATH";
						valu = config.getInitParameter(name);
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "ACM_USERNAME";
						valu = config.getInitParameter(name);
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "ACM_PASSWORD";
						valu = config.getInitParameter(name);
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						name = "ACM_SESSIONLISTENERS";
						valu = config.getInitParameter(name);
						builder.environment().put(name, valu);
						// ///////////////////////////////////////////////////////////////
						process = builder.start();
						processes.put(processName, process);
					}
					if (process != null) {
						//
						OutputStream out = process.getOutputStream();
						InputStream in = process.getInputStream();
						BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						try {
							context.log(err.readLine());
							context.log(err.readLine());
							context.log(err.readLine());
							context.log(err.readLine());
							context.log(err.readLine());
							context.log(err.readLine());
							context.log(err.readLine());
						} catch (Exception ex) {
							context.log("err", ex);
						}
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
	}

	/**
	 * set parameter values to OutputStream
	 * 
	 * @param meta
	 *            metadata
	 * @param req
	 *            request
	 * @param out
	 *            stream
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
	 * @param meta
	 *            metadata
	 * @param in
	 *            stream
	 * @param res
	 *            response
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
