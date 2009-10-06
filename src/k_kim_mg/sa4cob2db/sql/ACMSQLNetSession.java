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
 * �����åȥ١����Υ��å���������ǽ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSQLNetSession extends ACMSQLSession implements Runnable {
    private static final long serialVersionUID = 1L;
    /** �̿��Τ����Υ��ޥ����ʬ��ʸ����Ǽ����Ȥ뤿��Υ꡼���� */
	protected transient BufferedReader bufferedReader;
	/** �����åȤΥ����ȥץåȥ��ȥ꡼��ΥХåե� */
	protected transient BufferedOutputStream bufput;
	/** �����åȤΥ���ץåȥ��ȥ꡼�� */
	protected transient InputStream input;
	/** �����åȤΥ����ȥץåȥ��ȥ꡼�� */
	protected transient OutputStream output;
	/** �ե�������ΥХ������� */
	protected Map<CobolFile, byte[]> recordBytes = new Hashtable<CobolFile, byte[]>();
	/** �����С� */
	private final transient  SQLNetServer server;
	/** �����å� */
	protected transient Socket sock;
	/** �꡼���� */
	protected transient InputStreamReader streamReader;
	/** �饤���� */
	protected transient OutputStreamWriter streamWriter;
	/**
	 * �����С�
	 * @param server �����С�
	 * @param sock �����å�
	 * @throws Exception �㳰
	 */
	public ACMSQLNetSession(SQLNetServer server, Socket sock) throws Exception {
		super(server.getFileServer());
		this.server = server;
		this.sock = sock;
	}
	/**
	 * �ե�����򥢥����󤹤�
	 * @throws IOException �����㳰
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
	 * �ե�����򥯥�������
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
	 * �ȥ�󥶥������򥳥ߥåȤ���
	 * @throws IOException �������㳰
	 */
	protected void commitTransaction() throws IOException {
		try {
			getConnection().commit();
			callCommitEvent();
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}
	/**
	 * ���߹Ԥκ��
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
	 * �����åȤ���ե�����̾��������ƥե�������֤�
	 * @return �ե�����ޤ���null
	 */
	protected CobolFile getFileFromLine() {
		CobolFile file = null;
		try {
			// �ե��������
			String name = readTrim();
			// �ե�����μ���
			file = getFile(name);
		} catch (IOException e) {
			// 
			SQLNetServer.logger.log(Level.SEVERE, "Can't Write.", e);
		}
		return file;
	}
	/**
	 * ��������ƥ����С�����Ͽ����
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
				writeLine(new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, message));
				terminate();
			}
		} catch (IOException e) {
			SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e);
		}
	}
	/**
	 * �����ȥ쥳���ɤΰ�ư
	 * @throws IOException
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
	 * ���Υ쥳���ɤ�
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
	 * �ե�����򳫤�
	 * @throws IOException
	 */
	protected void open() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			// ////////////////////////////////////////////////////
			// �����ץ�⡼��
			writeLine(FileStatus.READY);
			int mode = -1;
			String modeString = readTrim();
			if (modeString.equalsIgnoreCase("INPUT")) {
				// �ɤ߹��ߥ⡼��
				mode = CobolFile.MODE_INPUT;
			} else if (modeString.equalsIgnoreCase("OUTPUT")) {
				// �񤭹��ߥ⡼��
				mode = CobolFile.MODE_OUTPUT;
			} else if (modeString.equalsIgnoreCase("EXTEND")) {
				// �ɵ��⡼��
				mode = CobolFile.MODE_EXTEND;
			} else if (modeString.equalsIgnoreCase("IO")) {
				// �����ϥ⡼��
				mode = CobolFile.MODE_INPUT_OUTPUT;
			}
			if (mode == CobolFile.MODE_INPUT || mode == CobolFile.MODE_OUTPUT || mode == CobolFile.MODE_EXTEND || mode == CobolFile.MODE_INPUT_OUTPUT) {
				// �⡼�ɤ�������
				writeLine(FileStatus.READY);
			} else {
				// �⡼������
				writeLine(new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file"));
				return;
			}
			// //////////////////////////////////////////////////////
			// ���������⡼��
			int accessmode = -1;
			String accessmodeString = readTrim();
			if (accessmodeString.equalsIgnoreCase("SEQUENC")) {
				// �祢������
				accessmode = CobolFile.ACCESS_SEQUENCIAL;
			} else if (accessmodeString.equalsIgnoreCase("RANDOM")) {
				// ưŪ��������
				accessmode = CobolFile.ACCESS_RANDOM;
			} else if (accessmodeString.equalsIgnoreCase("DYNAMIC")) {
				// �𥢥�����
				accessmode = CobolFile.ACCESS_DYNAMIC;
			}
			if (accessmode == CobolFile.ACCESS_SEQUENCIAL || accessmode == CobolFile.ACCESS_DYNAMIC || accessmode == CobolFile.ACCESS_RANDOM) {
				// �⡼�ɤ�������
				FileStatus status = file.open(mode, accessmode);
				writeLine(status);
			} else {
				// �⡼������
				writeLine(new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file"));
				return;
			}
		} else {
			readLine();// ���ߡ��꡼��
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * ���Υ쥳���ɤ�
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
	 * �᥽�åɤؤΥ����׽���
	 * @param method �᥽�å�
	 * @throws IOException
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
		} else if (method.equals("COMMIT")) {
			commitTransaction();
		} else if (method.equals("ROLLBACK")) {
			rollbackTransaction();
		} else {
			writeLine(new FileStatus(FileStatus.STATUS_UNSUPPORTED_METHOD, FileStatus.NULL_CODE, 0, method + " is not supported."));
		}
	}
	/**
	 * �꡼��
	 * @throws IOException
	 */
	protected void read() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			byte[] readingRecord;
			if (recordBytes.containsKey(file)) {
				readingRecord = recordBytes.get(file);
			} else {
				int recordLength = (file.getMetaData() == null ? ACMNetSession.RECORD_LEN : file.getMetaData().getRowSize());
				readingRecord = new byte[recordLength];
			}
			// �꡼�ɽ���
			FileStatus status = file.read(readingRecord);
			// 
			bufput.write(readingRecord);
			bufput.flush();
			//
			writeLine(status);
		} else {
			writeLine("");// ���ߡ�
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * �����åȤ���Х���������ɤ߼��
	 * @return �Х�������
	 * @throws IOException �������㳰
	 */
	protected byte[] readBytes() throws IOException {
		/** �����åȤ����ɤ߼��Х������� */
		byte[] bytes = new byte[ACMNetSession.RECORD_LEN];
		int size = input.read(bytes, 0, ACMNetSession.RECORD_LEN);
		if (bytes.length != size) {
			SQLNetServer.logger.warning("unmatch length." + bytes.length + ":" + size);
		}			
		return bytes;
	}
	/**
	 * �����åȤ���ʸ������ɤ߼��
	 * @return ʸ����
	 * @throws IOException �������㳰
	 */
	protected String readLine() throws IOException {
		/** �����åȤ����ɤ߼��ʸ�����������뤿���Char���� */
		char[] chars = new char[ACMNetSession.RECORD_LEN];
		int size = streamReader.read(chars, 0, ACMNetSession.RECORD_LEN);
		String line = new String(chars);
		SQLNetServer.logger.log(Level.FINEST, "RECIEVE:" + line.trim() + ":" + line.length());
		if (chars.length != size) {
			SQLNetServer.logger.warning("unmatch length." + chars.length + ":" + size);
		}		
		return line;
	}
	/**
	 * �����åȤ���ʸ������ɤ߼���null��""���Ѵ���
	 * @return ʸ����
	 * @throws IOException �������㳰
	 */
	protected String readTrim() throws IOException {
		String ret = readLine();
		return (ret == null ? "" : ret.trim());
	}
	
	protected void readNext() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			byte[] readingRecord;
			if (recordBytes.containsKey(file)) {
				readingRecord = recordBytes.get(file);
			} else {
				int recordLength = (file.getMetaData() == null ? ACMNetSession.RECORD_LEN : file.getMetaData().getRowSize());
				readingRecord = new byte[recordLength];
			}
			// �꡼�ɽ���
			FileStatus status = file.read(readingRecord);
			// 
			bufput.write(readingRecord);
			bufput.flush();
			//
			writeLine(status);
			// ������
			file.next();
		} else {
			writeLine("");// ���ߡ�
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * ����
	 * @throws IOException
	 */
	protected void rewrite() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			writeLine(FileStatus.READY);
			byte[] record = readBytes();
			FileStatus status = file.rewrite(record);
			writeLine(status);
		} else {
			// readLine();// ���ߡ��꡼��
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * �ȥ�󥶥����������Хå�����
	 * @throws IOException �������㳰
	 */
	protected void rollbackTransaction() throws IOException {
		try {
			getConnection().rollback();
			callRollbackEvent();
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			input = sock.getInputStream();
			streamReader = new InputStreamReader(input);
			bufferedReader = new BufferedReader(streamReader);
			output = sock.getOutputStream();
			bufput = new BufferedOutputStream(output, ACMNetSession.RECORD_LEN);
			// boutput = new BufferedOutputStream(output);
			streamWriter = new OutputStreamWriter(output);
			// bufferedWriter = new BufferedWriter(streamWriter);
			while (sock.isConnected() && !sock.isClosed()) {
				try {
					String method = readTrim();
					if (method.equals(ACMNetSession.MSG_INITIALIZE)) {
						// �����
						initialize();
					} else {
						// �����ǽ�����Ѥߤ������å�����
						if (true) {
							if (method.equals(ACMNetSession.MSG_TERMINATE)) {
								// ��λ
								terminate();
								// //streamWriter.flush();
							} else {
								// �ե��������
								processMethod(method);
							}
						} else {
							// ���������Ƥ��ʤ�
							writeLine(new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "not initialized."));
							terminate();
							// //streamWriter.flush();
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
	 * �����ȥ��ߥåȤ����ꤹ��
	 * @throws IOException �������㳰
	 */
	protected void setAutoCommit() throws IOException {
		String commitString = readTrim();
		boolean autoCommit = Boolean.parseBoolean(commitString);
		setAutoCommit(autoCommit);
	}
	/**
	 * �����ȥ��ߥåȤ����ꤹ��
	 * @param autoCommit	���ߥåȥ⡼��
	 * @throws IOException �������㳰
	 */
	protected void setAutoCommit (boolean autoCommit) throws IOException {
		try {
			getConnection().setAutoCommit(autoCommit);
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}
	/**
	 * �ȥ�󥶥������򳫻Ϥ���
	 * @throws IOException �������㳰
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
	 * ���ꤷ���ȥ�󥶥��������ǥ�٥�ǥȥ�󥶥������򳫻Ϥ���
	 * @param level �ȥ�󥶥��������ǥ�٥�
	 * @throws IOException �������㳰
	 */
	protected void setTransactionLevel(int level) throws IOException {
		try {
			getConnection().setTransactionIsolation(level);
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}
	/**
	 * �����դ�
	 * @throws IOException
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
			// readLine();// ���ߡ��꡼��
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * �����դ�
	 * @throws IOException
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
	 * ���å����ν�λ
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
		// ���å�����������ˤ���
		server.deleteSession(this);
		// ��λ��å�����
		try {
			writeLine(FileStatus.OK);
		} catch (IOException e1) {
			SQLNetServer.logger.log(Level.SEVERE, "Can't Write.", e1);
		}
		// �����åȤ���
		try {
			sock.close();
		} catch (IOException e2) {
			SQLNetServer.logger.log(Level.SEVERE, "Can't Close.", e2);
		}
		// ��̥��饹�ν�λ����
		super.terminate();
	}
	/**
	 * �񤭹���
	 * @throws IOException
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
	 * �����åȤؤΥե����륹�ơ������ν񤭹���
	 * @param status �ե����륹�ơ�����
	 * @throws IOException
	 */
	protected void writeLine(FileStatus status) throws IOException {
		writeLine(status.toString());
	}
	/**
	 * �����åȤؤιԽ񤭹���
	 * @param line ��
	 * @throws IOException
	 */
	protected void writeLine(String line) throws IOException {
		SQLNetServer.logger.log(Level.FINEST, "SENDING:" + line);
		streamWriter.write(line, 0, line.length());
		streamWriter.flush();
	}
}
