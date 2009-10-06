package k_kim_mg.sa4cob2db.sql;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.CobolRecordMetaData;
/**
 * 登録済みのメタデータからファイルオブジェクトを作ったりする機能
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLFileServer {
	/** SQLコネクション用ユーティリティ */
	protected DBConnector connector;
	/** メタデータのセット */
	protected SQLCobolRecordMetaDataSet metaDataSet;
	/** セッションIDのためのシーケンス */
	protected/* synchronized */
	/*  順序 */
	int sequence = 0;
	/** コンストラクタ */
	public SQLFileServer() {
		connector = new DBConnector();
		metaDataSet = new SQLCobolRecordMetaDataSet();
	}
	/**
	 * 接続の作成
	 * @return 接続
	 */
	public Connection createConnection() {
		return createConnection(true);
	}
	/**
	 * 接続の作成
	 * @param what true 常に新たに接続を作成する<br/>
	 *            false 既に接続が存在したら新たに接続を作成しない。
	 * @return 接続
	 */
	public Connection createConnection(boolean what) {
		Connection ret = null;
		try {
			SQLNetServer.logger.log(Level.FINEST, getDriverURL() + ":" + getDatabaseURL() + ":" + getUsername() + ":" + getPassword());
			if (what) {
				ret = connector.createConnection(getDriverURL(), getDatabaseURL(), getUsername(), getPassword());
			} else {
				ret = connector.createConnection();
			}
		} catch (ClassNotFoundException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
		}
		return ret;
	}
	/**
	 * 接続の開放
	 * @param connection 開放する接続
	 */
	public void removeConnection (Connection connection) {
		try {
			connector.removeConnection(connection);
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage());
		}
	}
	/**
	 * データベースURL
	 * @return データベースURL
	 */
	private String getDatabaseURL() {
		return metaDataSet.getDatabaseURL();
	}
	/**
	 * ドライバURL
	 * @return ドライバURL
	 */
	private String getDriverURL() {
		return metaDataSet.getDriverURL();
	}
	/**
	 * メタデータの取得
	 * @param i i番目のめたデータを取得する
	 * @return メタデータ
	 */
	public CobolRecordMetaData getMetaData(int i) {
		return metaDataSet.getMetaData(i);
	}
	/**
	 * メタデータの取得
	 * @param name メタデータ名
	 * @return メタデータ
	 */
	public CobolRecordMetaData getMetaData(String name) {
		return metaDataSet.getMetaData(name);
	}
	/** 登録されたメタデータの数 */
	public int getMetaDataCount() {
		return metaDataSet.getMetaDataCount();
	}
	/**
	 * メタデータを取得する
	 * @return メタデータ
	 */
	public SQLCobolRecordMetaDataSet getMetaDataSet() {
		return metaDataSet;
	}
	/**
	 * データベースパスワード
	 * @return データベースパスワード
	 */
	private String getPassword() {
		return metaDataSet.getPassword();
	}
	/**
	 * データベースユーザー名
	 * @return データベースユーザー名
	 */
	private String getUsername() {
		return metaDataSet.getUsername();
	}
	/**
	 * メタデータを登録する
	 * @param meta メタデータ
	 */
	public void installMetaData(CobolRecordMetaData meta) {
		metaDataSet.installMetaData(meta);
	}
	/**
	 * メタデータを削除する
	 * @param meta メタデータ
	 */
	public void removeMetaData(CobolRecordMetaData meta) {
		metaDataSet.removeMetaData(meta);
	}
	/**
	 * メタデータをセットする
	 * @param set メタデータ
	 */
	public void setMetaDataSet(SQLCobolRecordMetaDataSet set) {
		metaDataSet = set;
	}
}
