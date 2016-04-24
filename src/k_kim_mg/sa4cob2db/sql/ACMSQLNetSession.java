package k_kim_mg.sa4cob2db.sql;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.FileStatus;

/**
 * session based on Socket
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSQLNetSession extends ACMSQLSession implements Runnable {
	private static final long serialVersionUID = 1L;
	protected BufferedReader bufferedReader;
	protected BufferedOutputStream bufput;
	protected InputStream input;
	protected OutputStream output;
	protected Map<CobolFile, byte[]> recordBytes = new Hashtable<CobolFile, byte[]>();
	private final SQLNetServer server;
	protected Socket sock;
	protected InputStreamReader streamReader;
	protected OutputStreamWriter streamWriter;
	private boolean initialized = false;

	/**
	 * Server
	 * 
	 * @param server Server
	 * @param sock Socket
	 * @throws Exception Exception
	 */
	public ACMSQLNetSession(SQLNetServer server, Socket sock) throws Exception {
		super(server.getFileServer());
		this.server = server;
		this.sock = sock;
	}

	/**
	 * assign
	 * 
	 * @throws IOException IO Exception
	 */
	protected void assign() throws IOException {
		String name = readTrim();
		CobolFile file = createFile(name);
		if (file != null) {
			writeLine(FileStatus.OK);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * close
	 * 
	 * @throws IOException IO Exception
	 */
	protected void close() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			FileStatus status = file.close();
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * commit
	 * 
	 * @throws IOException IO Exception
	 */
	protected void commitTransaction() throws IOException {
		try {
			getConnection().commit();
			callCommitEvent();
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}

	/**
	 * delete
	 * 
	 * @throws IOException IO Exception
	 */
	protected void delete() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			writeLine(FileStatus.READY);
			byte[] record = readBytes();
			FileStatus status = file.delete(record);
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * get file
	 * 
	 * @return file or null
	 */
	protected CobolFile getFileFromLine() {
		CobolFile file = null;
		try {
			String name = readTrim();
			file = getFile(name);
		} catch (IOException e) {
			//
			SQLNetServer.logger.log(Level.SEVERE, "Can't Write.", e);
		}
		return file;
	}

	/**
	 * get option
	 * 
	 * @throws IOException IOException
	 */
	protected void getTCPOption() throws IOException {
		String name = readTrim();
		if (name.length() != 0) {
			String value = getACMOption(name);
			writeLine(value);
		} else {
			writeLine("");
		}
		writeLine(FileStatus.OK);
	}

	/**
	 * initialize
	 */
	protected void initialize() {
		try {
			writeLine(ACMNetSession.MSG_USERNAME);
			String username = readTrim();
			writeLine(ACMNetSession.MSG_PASSWORD);
			String password = readTrim();
			int login = server.addSession(this, username, password);
			if (login == SQLNetServer.LOGIN_OK) {
				try {
					setInitialized(true);
					writeLine(FileStatus.OK);
				} catch (IOException e) {
					SQLNetServer.logger.log(Level.SEVERE, "Can't Write.", e);
				}
			} else {
				String message = "Something Wrong.";
				switch (login) {
				case SQLNetServer.LOGIN_FAILURE:
					message = "login failure";
					break;
				case SQLNetServer.LOGIN_OVERLOAD:
					message = "too many sessions";
					break;
				}
				SQLNetServer.logger.log(Level.WARNING, message);
				writeLine(new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, message));
				terminate();
			}
		} catch (IOException e) {
			SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e);
		}
	}

	/**
	 * is initailized
	 * 
	 * @return true/false
	 */
	protected boolean isInitialized() {
		return initialized;
	}

	/**
	 * move to key value
	 * 
	 * @throws IOException IOException
	 */
	protected void move() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			writeLine(FileStatus.READY);
			byte[] record = readBytes();
			FileStatus status = file.move(record);
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * next
	 * 
	 * @throws IOException IO Exception
	 */
	protected void next() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			FileStatus status = file.next();
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * open
	 * 
	 * @throws IOException IO Exception
	 */
	protected void open() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			// ////////////////////////////////////////////////////
			writeLine(FileStatus.READY);
			int mode = -1;
			String modeString = readTrim();
			if (modeString.equalsIgnoreCase("INPUT")) {
				mode = CobolFile.MODE_INPUT;
			} else if (modeString.equalsIgnoreCase("OUTPUT")) {
				mode = CobolFile.MODE_OUTPUT;
			} else if (modeString.equalsIgnoreCase("EXTEND")) {
				mode = CobolFile.MODE_EXTEND;
			} else if (modeString.equalsIgnoreCase("IO")) {
				mode = CobolFile.MODE_INPUT_OUTPUT;
			}
			if (mode == CobolFile.MODE_INPUT || mode == CobolFile.MODE_OUTPUT || mode == CobolFile.MODE_EXTEND || mode == CobolFile.MODE_INPUT_OUTPUT) {
				writeLine(FileStatus.READY);
			} else {
				writeLine(new FileStatus(FileStatus.STATUS_PERMISSION_DENIED, FileStatus.NULL_CODE, 0, "can't open file"));
				return;
			}
			// //////////////////////////////////////////////////////
			int accessmode = -1;
			String accessmodeString = readTrim();
			if (accessmodeString.equalsIgnoreCase("SEQUENC")) {
				accessmode = CobolFile.ACCESS_SEQUENTIAL;
			} else if (accessmodeString.equalsIgnoreCase("RANDOM")) {
				accessmode = CobolFile.ACCESS_RANDOM;
			} else if (accessmodeString.equalsIgnoreCase("DYNAMIC")) {
				accessmode = CobolFile.ACCESS_DYNAMIC;
			}
			if (accessmode == CobolFile.ACCESS_SEQUENTIAL || accessmode == CobolFile.ACCESS_DYNAMIC || accessmode == CobolFile.ACCESS_RANDOM) {
				FileStatus status = file.open(mode, accessmode);
				writeLine(status);
			} else {
				writeLine(new FileStatus(FileStatus.STATUS_PERMISSION_DENIED, FileStatus.NULL_CODE, 0, "can't open file"));
				return;
			}
		} else {
			readLine();
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * move to previous record
	 * 
	 * @throws IOException IO Exception
	 */
	protected void previous() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			FileStatus status = file.previous();
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * front
	 * 
	 * @param method method name
	 * @throws IOException IO Exception
	 */
	protected void processMethod(String method) throws IOException {
		SQLNetServer.logger.log(Level.FINEST, "CALLED... " + method);
		if (method.equals("ASSIGN")) {
			writeLine(FileStatus.READY);
			assign();
		} else if (method.equals("CLOSE")) {
			writeLine(FileStatus.READY);
			close();
		} else if (method.equals("DELETE")) {
			writeLine(FileStatus.READY);
			delete();
		} else if (method.equals("MOVE")) {
			writeLine(FileStatus.READY);
			move();
		} else if (method.equals("NEXT")) {
			writeLine(FileStatus.READY);
			next();
		} else if (method.equals("OPEN")) {
			writeLine(FileStatus.READY);
			open();
		} else if (method.equals("PREVIOUS")) {
			writeLine(FileStatus.READY);
			previous();
		} else if (method.equals("READ")) {
			writeLine(FileStatus.READY);
			read();
		} else if (method.equals("READNXT")) {
			writeLine(FileStatus.READY);
			readNext();
		} else if (method.equals("REWRITE")) {
			writeLine(FileStatus.READY);
			rewrite();
		} else if (method.equals("START")) {
			writeLine(FileStatus.READY);
			start();
		} else if (method.equals("STRTWITH")) {
			writeLine(FileStatus.READY);
			startWith();
		} else if (method.equals("WRITE")) {
			writeLine(FileStatus.READY);
			write();
		} else if (method.equals("SETTRNS")) {
			writeLine(FileStatus.READY);
			setTransactionLevel();
		} else if (method.equals("SETAUTO")) {
			writeLine(FileStatus.READY);
			setAutoCommit();
		} else if (method.equals("SETOPTION")) {
			writeLine(FileStatus.READY);
			setTCPOption();
		} else if (method.equals("SETLENGTH")) {
			writeLine(FileStatus.READY);
			setMaxLength();
		} else if (method.equals("GETOPTION")) {
			writeLine(FileStatus.READY);
			getTCPOption();
		} else if (method.equals("COMMIT")) {
			commitTransaction();
		} else if (method.equals("ROLLBACK")) {
			rollbackTransaction();
		} else {
			writeLine(new FileStatus(FileStatus.STATUS_98_UNSUPPORTED_METHOD, FileStatus.NULL_CODE, 0, method + " is not supported."));
		}
	}

	/**
	 * read read current record
	 * 
	 * @throws IOException IO Exception
	 */
	protected void read() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			byte[] readingRecord;
			if (recordBytes.containsKey(file)) {
				readingRecord = recordBytes.get(file);
			} else {
				int recordLength = (file.getMetaData() == null ? getMaxLength() : file.getMetaData().getRowSize());
				if (recordLength > getMaxLength()) {
					recordLength = getMaxLength();
				}
				readingRecord = new byte[recordLength];
			}
			//
			FileStatus status = file.read(readingRecord);
			//
			bufput.write(readingRecord);
			bufput.flush();
			//
			writeLine(status);
		} else {
			writeLine("");
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * read bytes from socket
	 * 
	 * @return bytes
	 * @throws IOException IO Exception
	 */
	protected byte[] readBytes() throws IOException {
		byte[] bytes = new byte[getMaxLength()];
		int size = input.read(bytes, 0, bytes.length);
		if (bytes.length != size) {
			SQLNetServer.logger.warning("unmatch length." + bytes.length + ":" + size);
		}
		return bytes;
	}

	/**
	 * read text from socket
	 * 
	 * @return text
	 * @throws IOException IO Exception
	 */
	protected String readLine() throws IOException {
		char[] chars = new char[ACMNetSession.COMMANDLINE_MAX];
		int size = streamReader.read(chars, 0, chars.length);
		String line = new String(chars);
		SQLNetServer.logger.log(Level.FINEST, "RECIEVE:" + line.trim() + ":" + line.length());
		if (chars.length != size) {
			SQLNetServer.logger.warning("unmatch length." + chars.length + ":" + size);
		}
		return line;
	}

	/**
	 * read and next record
	 * 
	 * @throws IOException IO Exception
	 */
	protected void readNext() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			byte[] readingRecord;
			if (recordBytes.containsKey(file)) {
				readingRecord = recordBytes.get(file);
				SQLNetServer.logger.warning(new String(readingRecord));
			} else {
				int recordLength = (file.getMetaData() == null ? getMaxLength() : file.getMetaData().getRowSize());
				if (recordLength > getMaxLength()) {
					recordLength = getMaxLength();
				}
				readingRecord = new byte[recordLength];
			}
			FileStatus status = file.read(readingRecord);
			bufput.write(readingRecord);
			bufput.flush();
			writeLine(status);
			file.next();
		} else {
			writeLine("");
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * read text from socket
	 * 
	 * @return text
	 * @throws IOException IO Exception
	 */
	protected String readTrim() throws IOException {
		String ret = readLine();
		return (ret == null ? "" : ret.trim());
	}

	/**
	 * rewrite / update
	 * 
	 * @throws IOException IO Exception
	 */
	protected void rewrite() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			writeLine(FileStatus.READY);
			byte[] record = readBytes();
			FileStatus status = file.rewrite(record);
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * rollback
	 * 
	 * @throws IOException IO Exception
	 */
	protected void rollbackTransaction() throws IOException {
		try {
			getConnection().rollback();
			callRollbackEvent();
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			input = sock.getInputStream();
			streamReader = new InputStreamReader(input);
			bufferedReader = new BufferedReader(streamReader);
			output = sock.getOutputStream();
			bufput = new BufferedOutputStream(output, getMaxLength());
			// boutput = new BufferedOutputStream(output);
			streamWriter = new OutputStreamWriter(output);
			// bufferedWriter = new BufferedWriter(streamWriter);
			while (sock.isConnected() && !sock.isClosed()) {
				try {
					String method = readTrim();
					if (method.equals(ACMNetSession.MSG_INITIALIZE)) {
						initialize();
					} else {
						if (isInitialized()) {
							if (method.equals(ACMNetSession.MSG_TERMINATE)) {
								terminate();
							} else {
								processMethod(method);
							}
						} else {
							writeLine(new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "not initialized."));
							terminate();
						}
					}
				} catch (Exception e) {
					SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e);
					terminate();
					break;
				}
			}
		} catch (Exception e) {
			SQLNetServer.logger.log(Level.SEVERE, "Can't Write.", e);
			terminate();
		} finally {
			SQLNetServer.logger.log(Level.INFO, getSessionId() + " is Now Terminating.....");
			try {
				sock.close();
			} catch (IOException e1) {
				SQLNetServer.logger.log(Level.SEVERE, "Can't Close.", e1);
			}
		}
	}

	/**
	 * set auto commit mode
	 * 
	 * @throws IOException IO Exception
	 */
	protected void setAutoCommit() throws IOException {
		String commitString = readTrim();
		boolean autoCommit = Boolean.parseBoolean(commitString);
		setAutoCommit(autoCommit);
	}

	/**
	 * set auto commit mode
	 * 
	 * @param autoCommit mode auto:true not auto:false
	 * @throws IOException IO Exception
	 */
	protected void setAutoCommit(boolean autoCommit) throws IOException {
		try {
			getConnection().setAutoCommit(autoCommit);
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}

	/**
	 * set initialized flag
	 * 
	 * @param initialized flag
	 */
	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	/**
	 * set max length
	 * 
	 * @throws IOException IO Exception
	 */
	protected void setMaxLength() throws IOException {
		String s_length = readTrim();
		try {
			int length = Integer.parseInt(s_length);
			setMaxLength(length);
			if (length == getMaxLength()) {
				writeLine(FileStatus.OK);
			} else {
				writeLine(FileStatus.FAILURE);
			}
		} catch (NumberFormatException e) {
			writeLine(FileStatus.FAILURE);
		}
	}

	/**
	 * set option
	 * 
	 * @throws IOException IOException
	 */
	protected void setTCPOption() throws IOException {
		String name = readTrim();
		writeLine(FileStatus.READY);
		String value = readLine();
		setACMOption(name, value);
		writeLine(FileStatus.OK);
	}

	/**
	 * start transaction
	 * 
	 * @throws IOException IO Exception
	 */
	protected void setTransactionLevel() throws IOException {
		String levelString = readTrim();
		int level = Connection.TRANSACTION_NONE;
		if (levelString.equalsIgnoreCase("TRANSACTION_READ_COMMITTED")) {
			level = Connection.TRANSACTION_READ_COMMITTED;
		} else if (levelString.equalsIgnoreCase("TRANSACTION_READ_UNCOMMITTED")) {
			level = Connection.TRANSACTION_READ_UNCOMMITTED;
		} else if (levelString.equalsIgnoreCase("TRANSACTION_REPEATABLE_READ")) {
			level = Connection.TRANSACTION_REPEATABLE_READ;
		} else if (levelString.equalsIgnoreCase("TRANSACTION_SERIALIZABLE")) {
			level = Connection.TRANSACTION_SERIALIZABLE;
		}
		setTransactionLevel(level);
	}

	/**
	 * start transaction
	 * 
	 * @param level transaction level
	 * @throws IOException IO Exception
	 */
	protected void setTransactionLevel(int level) throws IOException {
		try {
			getConnection().setTransactionIsolation(level);
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}

	/**
	 * start
	 * 
	 * @throws IOException IO Exception
	 */
	protected void start() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			writeLine(FileStatus.READY);
			int mode = 0;
			String smode = readLine();
			if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN)) {
				mode = CobolFile.IS_GREATER_THAN;
			} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN_OR_EQUAL_TO)) {
				mode = CobolFile.IS_GREATER_THAN_OR_EQUAL_TO;
			} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_NOT_LESS_THAN)) {
				mode = CobolFile.IS_NOT_LESS_THAN;
			} else {
				mode = CobolFile.IS_EQUAL_TO;
			}
			writeLine(FileStatus.READY);
			byte[] record = readBytes();
			FileStatus status = file.start(mode, record);
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * start by key
	 * 
	 * @throws IOException IO Exception
	 */
	protected void startWith() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			writeLine(FileStatus.READY);
			int mode = 0;
			String smode = readLine();
			if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN)) {
				mode = CobolFile.IS_GREATER_THAN;
			} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN_OR_EQUAL_TO)) {
				mode = CobolFile.IS_GREATER_THAN_OR_EQUAL_TO;
			} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_NOT_LESS_THAN)) {
				mode = CobolFile.IS_NOT_LESS_THAN;
			} else {
				mode = CobolFile.IS_EQUAL_TO;
			}
			writeLine(FileStatus.READY);
			String skey = readTrim();
			writeLine(FileStatus.READY);
			byte[] record = readBytes();
			FileStatus status = file.start(skey, mode, record);
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * terminate
	 */
	protected void terminate() {
		try {
			Enumeration<String> keys = files.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				destroyFile(key);
				SQLNetServer.logger.log(Level.INFO, "closed..." + key);
			}
		} catch (Exception e3) {
			SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e3);
		}
		server.deleteSession(this);
		try {
			writeLine(FileStatus.OK);
		} catch (IOException e1) {
			SQLNetServer.logger.log(Level.SEVERE, "Can't Write.", e1);
		}
		try {
			sock.close();
		} catch (IOException e2) {
			SQLNetServer.logger.log(Level.SEVERE, "Can't Close.", e2);
		}
		setInitialized(false);
		super.terminate();
	}

	/**
	 * write / insert
	 * 
	 * @throws IOException IO Exception
	 */
	protected void write() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			writeLine(FileStatus.READY);
			byte[] record = readBytes();
			FileStatus status = file.write(record);
			writeLine(status);
		} else {
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}

	/**
	 * write status to socket
	 * 
	 * @param status status
	 * @throws IOException IO Exception
	 */
	protected void writeLine(FileStatus status) throws IOException {
		writeLine(status.toString());
	}

	/**
	 * wriet line to socket
	 * 
	 * @param line line
	 * @throws IOException IO Exception
	 */
	protected void writeLine(String line) throws IOException {
		SQLNetServer.logger.log(Level.FINEST, "SENDING:" + line + ":" + line.length());
		streamWriter.write(line, 0, line.length());
		streamWriter.flush();
	}
}
