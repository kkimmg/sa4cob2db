package k_kim_mg.sa4cob2db.sql;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import k_kim_mg.sa4cob2db.ACMSession;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.FileStatus;
import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;
import org.xml.sax.SAXException;
/**
 * JNIベースのサーバー機能
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMSQLJNISession implements ACMSession {
	static final String NOT_ASSIGNED = FileStatus.NOT_ASSIGNED.toString();
	private static final long serialVersionUID = 1L;
	final byte[] initalOption;
	/** option value */
	byte[] optionValue = new byte[ACMNetSession.OPTIONVALUE_LEN];
	/** record */
	byte[] readingRecord = new byte[ACMNetSession.INITIAL_RECORD_LEN];
	/** FILE STATUS */
	byte[] status = new byte[255];
	/** Session */
	ACMSQLSession superobj;
	/** Constructor */
	public ACMSQLJNISession() throws Exception {
		super();
		byte[] work = new byte[ACMNetSession.OPTIONVALUE_LEN];
		for (int i = 0; i < work.length; i++) {
			work[i] = ' ';
		}
		initalOption = work;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#addACMSessionEventListener(
	 * k_kim_mg.sa4cob2db.event.ACMSessionEventListener)
	 */
	@Override
	public void addACMSessionEventListener(ACMSessionEventListener listener) {
		superobj.addACMSessionEventListener(listener);
	}
	/**
	 * ファイルをアサインする
	 * 
	 * @throws IOException 入出例外
	 */
	public void assign(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = createFile(FileName);
		if (file != null) {
			ret = FileStatus.OK;
		} else {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, fileName + "is unknown file.");
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * ファイルをクローズする
	 */
	public void close(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.close();
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * トランザクションをコミットする
	 * 
	 * @throws IOException 入出力例外
	 */
	public void commitTransaction() {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().commit();
			superobj.callCommitEvent();
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#createFile(java.lang.String)
	 */
	@Override
	public CobolFile createFile(String name) {
		return superobj.createFile(name);
	}
	/**
	 * 現在行の削除
	 */
	public void delete(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.delete(record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#destroyFile(java.lang.String)
	 */
	@Override
	public void destroyFile(String name) {
		superobj.destroyFile(name);
	}
	@Override
	public String getACMOption(String key) {
		String ret = superobj.getACMOption(key);
		return (ret != null ? ret : "");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#getFile(java.lang.String)
	 */
	@Override
	public CobolFile getFile(String name) {
		return superobj.getFile(name);
	}
	public void getJNIOption(byte[] key) {
		String s_key = new String(key);
		String s_val = getACMOption(s_key);
		byte[] optValue = s_val.getBytes();
		int l = optionValue.length;
		int j = optValue.length;
		System.arraycopy(initalOption, 0, optionValue, 0, l);
		System.arraycopy(optValue, 0, optionValue, 0, (l > j ? j : l));
		setFileStatus2Bytes(FileStatus.OK, status);
	}
	@Override
	public int getMaxLength() {
		return superobj.getMaxLength();
	}
	/**
	 * モード文字列からint値を得る
	 * 
	 * @param bytes モードを表す文字列のバイト配列
	 * @return
	 */
	int getModeBytes2ModeInt(byte[] bmode) {
		String smode = new String(bmode);
		int mode = CobolFile.IS_EQUAL_TO;
		if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN)) {
			mode = CobolFile.IS_GREATER_THAN;
		} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_GREATER_THAN_OR_EQUAL_TO)) {
			mode = CobolFile.IS_GREATER_THAN_OR_EQUAL_TO;
		} else if (smode.trim().toUpperCase().equals(ACMNetSession.STT_IS_NOT_LESS_THAN)) {
			mode = CobolFile.IS_NOT_LESS_THAN;
		} else {
			mode = CobolFile.IS_EQUAL_TO;
		}
		return mode;
	}
	/**
	 * option value
	 * 
	 * @return value
	 */
	public byte[] getOptionValue() {
		return optionValue;
	}
	/**
	 * record
	 * 
	 * @return the readingRecord
	 */
	public byte[] getReadingRecord() {
		return readingRecord;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#getSessionId()
	 */
	@Override
	public String getSessionId() {
		return superobj.getSessionId();
	}
	/**
	 * FILE STATUS
	 * 
	 * @return FILE STATUS
	 */
	public byte[] getStatus() {
		return status;
	}
	/**
	 * 初期化してサーバーに登録する
	 */
	public void initialize(byte[] acmUsername, byte[] acmPassword) {
		FileStatus ret = FileStatus.OK;
		SQLFileServer sqlfileserver = new SQLFileServer();
		try {
			String filename = SQLNetServer.getEnvValue("ACM_CONFFILE", SQLNetServer.DEFAULT_CONFIG);
			NodeReadLoader nodeLoader = new NodeReadLoader();
			CobolRecordMetaDataSet metaset = sqlfileserver.getMetaDataSet();
			File metaFile = new File(filename);
			Properties properties = new Properties();
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
			if (metaset instanceof SQLCobolRecordMetaDataSet) {
				SQLCobolRecordMetaDataSet sqlset = (SQLCobolRecordMetaDataSet) metaset;
				SQLNetServer.updateProperty(properties, "jdbcdriverurl", "ACM_JDBCDRIVERURL");
				SQLNetServer.updateProperty(properties, "jdbcdatabaseurl", "ACM_JDBCDATABASEURL");
				SQLNetServer.updateProperty(properties, "jdbcusername", "ACM_JDBCUSERNAME");
				SQLNetServer.updateProperty(properties, "jdbcpassword", "ACM_JDBCPASSWORD");
				sqlset.setDriverURL(properties.getProperty("jdbcdriverurl"));
				sqlset.setDatabaseURL(properties.getProperty("jdbcdatabaseurl"));
				sqlset.setUsername(properties.getProperty("jdbcusername"));
				sqlset.setPassword(properties.getProperty("jdbcpassword"));
			}
			// /////////////////////////////////////////////////////////
			// ログの設定
			String logSetting = properties.getProperty("log", "");
			if (logSetting.trim().length() > 0) {
				try {
					SQLNetServer.logger.log(Level.CONFIG, "Reading Log Setting From " + logSetting);
					InputStream stream = new FileInputStream(logSetting);
					LogManager manager = LogManager.getLogManager();
					manager.readConfiguration(stream);
				} catch (FileNotFoundException fnfe) {
					SQLNetServer.logger.log(Level.WARNING, "File Not Found " + logSetting, fnfe);
				}
			}
			// パスワードの設定
			Properties users = new Properties();
			String userFile = properties.getProperty("authfile", "");
			if (userFile == "") {
				users.put("", "");// デフォルトパスワード
				SQLNetServer.logger.log(Level.CONFIG, "authfile is null. using default password.");
			} else {
				SQLNetServer.logger.log(Level.CONFIG, "Loading authfile " + userFile + ".");
				FileInputStream fio = new FileInputStream(userFile);
				users.load(fio);
			}
			superobj = new ACMSQLSession(sqlfileserver);
		} catch (ParserConfigurationException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (FactoryConfigurationError e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (SAXException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (IOException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		} catch (Exception e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * カレントレコードの移動
	 */
	public void move(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.move(record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * 次のレコードへ
	 */
	public void next(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.next();
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * ファイルを開く
	 */
	public void open(byte[] fileName, byte[] bmode, byte[] baccessmode) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			// ////////////////////////////////////////////////////
			// オープンモード
			int mode = -1;
			String modeString = new String(bmode).trim();
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
			// //////////////////////////////////////////////////////
			// アクセスモード
			int accessmode = -1;
			String accessmodeString = new String(baccessmode).trim();
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
			// ////////////////////////////////////////////////////
			// オープンモード
			if (mode == CobolFile.MODE_INPUT || mode == CobolFile.MODE_OUTPUT || mode == CobolFile.MODE_EXTEND || mode == CobolFile.MODE_INPUT_OUTPUT) {
				// モードが正しい
			} else {
				// モード不正
				ret = new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file");
				setFileStatus2Bytes(ret, status);
			}
			// //////////////////////////////////////////////////////
			// アクセスモード
			if (accessmode == CobolFile.ACCESS_SEQUENCIAL || accessmode == CobolFile.ACCESS_DYNAMIC || accessmode == CobolFile.ACCESS_RANDOM) {
				// モードが正しい
				ret = file.open(mode, accessmode);
			} else {
				// モード不正
				ret = new FileStatus(FileStatus.STATUS_CANT_OPEN, FileStatus.NULL_CODE, 0, "can't open file");
				setFileStatus2Bytes(ret, status);
			}
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * 前のレコードへ
	 */
	public void previous(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.previous();
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * リード
	 */
	public void read(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			// リード処理
			ret = file.read(readingRecord);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * 次のレコードへ
	 * 
	 * @param fileName
	 */
	public void readNext(byte[] fileName) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			;
			// リード処理
			ret = file.read(readingRecord);
			if (ret.getStatusCode().equals(FileStatus.STATUS_OK)) {
				// 次処理
				file.next();
			}
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.ACMSession#removeACMSessionEventListener
	 * (k_kim_mg.sa4cob2db.event.ACMSessionEventListener)
	 */
	@Override
	public void removeACMSessionEventListener(ACMSessionEventListener listener) {
		superobj.removeACMSessionEventListener(listener);
	}
	/**
	 * 更新
	 */
	public void rewrite(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.rewrite(record);
		} else {
			// readLine();// ダミーリード
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * トランザクションをロールバックする 入出力例外
	 */
	public void rollbackTransaction() {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().rollback();
			superobj.callRollbackEvent();
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	@Override
	public void setACMOption(String key, String value) {
		superobj.setACMOption(key, value);
	}
	/**
	 * オートコミットを設定する
	 * 
	 * @param autoCommit コミットモード 入出力例外
	 */
	public void setAutoCommit(boolean autoCommit) {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().setAutoCommit(autoCommit);
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * オートコミットを設定する
	 * 
	 * @throws IOException 入出力例外
	 */
	public void setAutoCommit(byte[] commitBytes) {
		String commitString = new String(commitBytes).trim();
		boolean autoCommit = Boolean.parseBoolean(commitString);
		setAutoCommit(autoCommit);
	}
	/**
	 * ファイルステータスをバイト配列に転記する
	 * 
	 * @param source ファイルステータス
	 * @param dist ステータス
	 */
	void setFileStatus2Bytes(FileStatus source, byte[] dest) {
		String string = source.toString();
		byte[] bytes = string.getBytes();
		System.arraycopy(bytes, 0, dest, 0, (bytes.length < dest.length ? bytes.length : dest.length));
	}
	public void setJNIOption(byte[] key, byte[] value) {
		String s_key = new String(key);
		String s_val = new String(value);
		setACMOption(s_key, s_val);
		setFileStatus2Bytes(FileStatus.OK, status);
	}
	@Override
	public void setMaxLength(int length) {
		if (length <= 0) {
			length = ACMNetSession.INITIAL_RECORD_LEN;
		}
		readingRecord = new byte[getMaxLength()];
		superobj.setMaxLength(length);
	}
	/**
	 * トランザクションを開始する
	 * 
	 * @throws IOException 入出力例外
	 */
	public void setTransactionLevel(byte[] levelBytes) {
		String levelString = new String(levelBytes).trim();
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
	 * @param level トランザクション遮断レベル 入出力例外
	 */
	public void setTransactionLevel(int level) {
		FileStatus ret = FileStatus.FAILURE;
		try {
			superobj.getConnection().setTransactionIsolation(level);
			ret = FileStatus.OK;
		} catch (SQLException e) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * 位置付け
	 */
	public void start(byte[] fileName, byte[] modeBytes, byte[] record) {
		int mode = getModeBytes2ModeInt(modeBytes);
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.start(mode, record);
		} else {
			// readLine();// ダミーリード
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * 位置付け
	 */
	public void startWith(byte[] fileName, byte[] skey, byte[] modeBytes, byte[] record) {
		int mode = getModeBytes2ModeInt(modeBytes);
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			String skeyName = new String(skey).trim();
			ret = file.start(skeyName, mode, record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
	/**
	 * terminate
	 */
	public void terminate() {
		try {
			Hashtable<String, CobolFile> files = superobj.files;
			Enumeration<String> keys = files.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				destroyFile(key);
				SQLNetServer.logger.log(Level.INFO, "closed..." + key);
			}
		} catch (Exception e3) {
			SQLNetServer.logger.log(Level.SEVERE, "Something Wrong.", e3);
			setFileStatus2Bytes(FileStatus.FAILURE, status);
			return;
		}
		// server.deleteSession(this);
		superobj.terminate();
		setFileStatus2Bytes(FileStatus.OK, status);
	}
	/**
	 * write file
	 */
	public void write(byte[] fileName, byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		String FileName = new String(fileName).trim();
		CobolFile file = getFile(FileName);
		if (file != null) {
			ret = file.write(record);
		} else {
			ret = FileStatus.NOT_ASSIGNED;
		}
		setFileStatus2Bytes(ret, status);
	}
}
