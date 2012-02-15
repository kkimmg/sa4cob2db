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
 * ソケットベースのセッション管理機能
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSQLNetSession extends ACMSQLSession implements Runnable {
	private static final long serialVersionUID = 1L;
	/** 通信のうちのコマンド部分を文字列で受けとるためのリーダー */
	protected transient BufferedReader bufferedReader;
	/** ソケットのアウトプットストリームのバッファ */
	protected transient BufferedOutputStream bufput;
	/** ソケットのインプットストリーム */
	protected transient InputStream input;
	/** ソケットのアウトプットストリーム */
	protected transient OutputStream output;
	/** ファイル毎のバイト配列 */
	protected Map<CobolFile, byte[]> recordBytes = new Hashtable<CobolFile, byte[]>();
	/** サーバー */
	private final transient SQLNetServer server;
	/** ソケット */
	protected transient Socket sock;
	/** リーダー */
	protected transient InputStreamReader streamReader;
	/** ライター */
	protected transient OutputStreamWriter streamWriter;
	/** このセッションは初期化済みかどうか */
	private boolean initialized = false;
	/**
	 * サーバー
	 * 
	 * @param server サーバー
	 * @param sock ソケット
	 * @throws Exception 例外
	 */
	public ACMSQLNetSession(SQLNetServer server, Socket sock) throws Exception {
		super(server.getFileServer());
		this.server = server;
		this.sock = sock;
	}
	/**
	 * ファイルをアサインする
	 * 
	 * @throws IOException 入出例外
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
	 * ファイルをクローズする
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
	 * トランザクションをコミットする
	 * 
	 * @throws IOException 入出力例外
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
	 * 現在行の削除
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
	 * ソケットからfilenameを取得してファイルを返す
	 * 
	 * @return ファイルまたはnull
	 */
	protected CobolFile getFileFromLine() {
		CobolFile file = null;
		try {
			// ファイル処理
			String name = readTrim();
			// ファイルの取得
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
	 * @throws IOException
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
	 * 初期化してサーバーに登録する
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
				writeLine(new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, message));
				terminate();
			}
		} catch (IOException e) {
			SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e);
		}
	}
	/** このセッションは初期化済みかどうか */
	protected boolean isInitialized() {
		return initialized;
	}
	/**
	 * カレントレコードの移動
	 * 
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
	 * 次のレコードへ
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
	 * ファイルを開く
	 * 
	 * @throws IOException
	 */
	protected void open() throws IOException {
		CobolFile file = getFileFromLine();
		if (file != null) {
			// ////////////////////////////////////////////////////
			// オープンモード
			writeLine(FileStatus.READY);
			int mode = -1;
			String modeString = readTrim();
			if (modeString.equalsIgnoreCase("INPUT")) {
				// 読み込みモード
				mode = CobolFile.MODE_INPUT;
			} else if (modeString.equalsIgnoreCase("OUTPUT")) {
				// 書き込みモード
				mode = CobolFile.MODE_OUTPUT;
			} else if (modeString.equalsIgnoreCase("EXTEND")) {
				// 追記モード
				mode = CobolFile.MODE_EXTEND;
			} else if (modeString.equalsIgnoreCase("IO")) {
				// 入出力モード
				mode = CobolFile.MODE_INPUT_OUTPUT;
			}
			if (mode == CobolFile.MODE_INPUT || mode == CobolFile.MODE_OUTPUT || mode == CobolFile.MODE_EXTEND || mode == CobolFile.MODE_INPUT_OUTPUT) {
				// モードが正しい
				writeLine(FileStatus.READY);
			} else {
				// モード不正
				writeLine(new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file"));
				return;
			}
			// //////////////////////////////////////////////////////
			// アクセスモード
			int accessmode = -1;
			String accessmodeString = readTrim();
			if (accessmodeString.equalsIgnoreCase("SEQUENC")) {
				// 順アクセス
				accessmode = CobolFile.ACCESS_SEQUENCIAL;
			} else if (accessmodeString.equalsIgnoreCase("RANDOM")) {
				// 動的アクセス
				accessmode = CobolFile.ACCESS_RANDOM;
			} else if (accessmodeString.equalsIgnoreCase("DYNAMIC")) {
				// 乱アクセス
				accessmode = CobolFile.ACCESS_DYNAMIC;
			}
			if (accessmode == CobolFile.ACCESS_SEQUENCIAL || accessmode == CobolFile.ACCESS_DYNAMIC || accessmode == CobolFile.ACCESS_RANDOM) {
				// モードが正しい
				FileStatus status = file.open(mode, accessmode);
				writeLine(status);
			} else {
				// モード不正
				writeLine(new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file"));
				return;
			}
		} else {
			readLine();// ダミーリード
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * 前のレコードへ
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
	 * メソッドへのジャンプ処理
	 * 
	 * @param method メソッド
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
		} else if (method.equals("SETOPTION")) {
			writeLine(FileStatus.READY);
			setTCPOption();
		} else if (method.equals("GETOPTION")) {
			writeLine(FileStatus.READY);
			getTCPOption();
		} else if (method.equals("COMMIT")) {
			commitTransaction();
		} else if (method.equals("ROLLBACK")) {
			rollbackTransaction();
		} else {
			writeLine(new FileStatus(FileStatus.STATUS_UNSUPPORTED_METHOD, FileStatus.NULL_CODE, 0, method + " is not supported."));
		}
	}
	/**
	 * リード
	 * 
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
			// リード処理
			FileStatus status = file.read(readingRecord);
			// 
			bufput.write(readingRecord);
			bufput.flush();
			//
			writeLine(status);
		} else {
			writeLine("");// ダミー
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * ソケットからバイト配列を読み取る
	 * 
	 * @return バイト配列
	 * @throws IOException 入出力例外
	 */
	protected byte[] readBytes() throws IOException {
		/** ソケットから読み取るバイト配列 */
		byte[] bytes = new byte[ACMNetSession.RECORD_LEN];
		int size = input.read(bytes, 0, ACMNetSession.RECORD_LEN);
		if (bytes.length != size) {
			SQLNetServer.logger.warning("unmatch length." + bytes.length + ":" + size);
		}
		return bytes;
	}
	/**
	 * ソケットから文字列を読み取る
	 * 
	 * @return 文字列
	 * @throws IOException 入出力例外
	 */
	protected String readLine() throws IOException {
		/** ソケットから読み取る文字列を作成するためのChar配列 */
		char[] chars = new char[ACMNetSession.RECORD_LEN];
		int size = streamReader.read(chars, 0, ACMNetSession.RECORD_LEN);
		String line = new String(chars);
		SQLNetServer.logger.log(Level.FINEST, "RECIEVE:" + line.trim() + ":" + line.length());
		if (chars.length != size) {
			SQLNetServer.logger.warning("unmatch length." + chars.length + ":" + size);
		}
		return line;
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
			// リード処理
			FileStatus status = file.read(readingRecord);
			// 
			bufput.write(readingRecord);
			bufput.flush();
			//
			writeLine(status);
			// 次処理
			file.next();
		} else {
			writeLine("");// ダミー
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * ソケットから文字列を読み取る（null→""に変換）
	 * 
	 * @return 文字列
	 * @throws IOException 入出力例外
	 */
	protected String readTrim() throws IOException {
		String ret = readLine();
		return (ret == null ? "" : ret.trim());
	}
	/**
	 * 更新
	 * 
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
			// readLine();// ダミーリード
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * トランザクションをロールバックする
	 * 
	 * @throws IOException 入出力例外
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
	 * 
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
						// 初期化
						initialize();
					} else {
						// ここで初期化済みかチェックする
						if (isInitialized()) {
							if (method.equals(ACMNetSession.MSG_TERMINATE)) {
								// 終了
								terminate();
								// //streamWriter.flush();
							} else {
								// ファイル処理
								processMethod(method);
							}
						} else {
							// 初期化されていない
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
	 * オートコミットを設定する
	 * 
	 * @throws IOException 入出力例外
	 */
	protected void setAutoCommit() throws IOException {
		String commitString = readTrim();
		boolean autoCommit = Boolean.parseBoolean(commitString);
		setAutoCommit(autoCommit);
	}
	/**
	 * オートコミットを設定する
	 * 
	 * @param autoCommit コミットモード
	 * @throws IOException 入出力例外
	 */
	protected void setAutoCommit(boolean autoCommit) throws IOException {
		try {
			getConnection().setAutoCommit(autoCommit);
			writeLine(FileStatus.OK);
		} catch (SQLException e) {
			writeLine(new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage()));
		}
	}
	/**
	 * このセッションは初期化済みかどうかをセットする
	 * 
	 * @param initialized フラグ
	 */
	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	/**
	 * set option
	 * 
	 * @throws IOException
	 */
	protected void setTCPOption() throws IOException {
		String name = readTrim();
		writeLine(FileStatus.READY);
		String value = readLine();
		setACMOption(name, value);
		writeLine(FileStatus.OK);
	}
	/**
	 * トランザクションを開始する
	 * 
	 * @throws IOException 入出力例外
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
	 * 指定したトランザクション遮断レベルでトランザクションを開始する
	 * 
	 * @param level トランザクション遮断レベル
	 * @throws IOException 入出力例外
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
	 * 位置付け
	 * 
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
			// readLine();// ダミーリード
			writeLine(FileStatus.NOT_ASSIGNED);
		}
	}
	/**
	 * 位置付け
	 * 
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
	 * セッションの終了
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
		// セッションを管理外にする
		server.deleteSession(this);
		// 終了メッセージ
		try {
			writeLine(FileStatus.OK);
		} catch (IOException e1) {
			SQLNetServer.logger.log(Level.SEVERE, "Can't Write.", e1);
		}
		// ソケットを開放
		try {
			sock.close();
		} catch (IOException e2) {
			SQLNetServer.logger.log(Level.SEVERE, "Can't Close.", e2);
		}
		setInitialized(false);
		// 上位クラスの終了処理
		super.terminate();
	}
	/**
	 * 書き込み
	 * 
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
	 * ソケットへのファイルステータスの書き込み
	 * 
	 * @param status ファイルステータス
	 * @throws IOException
	 */
	protected void writeLine(FileStatus status) throws IOException {
		writeLine(status.toString());
	}
	/**
	 * ソケットへの行書き込み
	 * 
	 * @param line 行
	 * @throws IOException
	 */
	protected void writeLine(String line) throws IOException {
		SQLNetServer.logger.log(Level.FINEST, "SENDING:" + line);
		streamWriter.write(line, 0, line.length());
		streamWriter.flush();
	}
}
