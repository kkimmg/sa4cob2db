package k_kim_mg.sa4cob2db.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import k_kim_mg.sa4cob2db.CobolColumn;
import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.FileStatus;

/**
 * file too
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLFile2 extends SQLFile {
	private static final long serialVersionUID = 1L;

	private PreparedStatement keyReadStatement = null;
	private PreparedStatement startGTStatement = null;
	private PreparedStatement startGEStatement = null;

	public SQLFile2(Connection connection, SQLCobolRecordMetaData2 meta) {
		super(connection, meta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLFile#close()
	 */
	@Override
	public FileStatus close() {
		try {
			keyReadStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return super.close();
	}

	/**
	 * @return the keyReadStatement
	 */
	public PreparedStatement getKeyReadStatement() {
		return keyReadStatement;
	}

	/**
	 * @return the startGEStatement
	 */
	public PreparedStatement getStartGEStatement() {
		return startGEStatement;
	}

	/**
	 * @return the startGTStatement
	 */
	public PreparedStatement getStartGTStatement() {
		return startGTStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLFile#move(byte[])
	 */
	@Override
	public FileStatus move(byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		if (getKeyReadStatement() == null) {
			ret = super.move(record);
		} else {
			try {
				ResultSet wrk = getResultSet();
				if (wrk != null) {
					if (!wrk.isClosed()) {
						wrk.close();
					}
				}
				setParamsRecord2PreaparedStatement(record, getKeyReadStatement());
				wrk = getKeyReadStatement().executeQuery();
				if (wrk.first()) {
					setResultSet(wrk);
					ret = FileStatus.OK;
				}
			} catch (SQLException e) {
				ret = getException2FileStatus(e);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLFile#open(int, int)
	 */
	@Override
	public FileStatus open(int mode, int accessmode) {
		FileStatus ret = super.open(mode, accessmode);
		CobolRecordMetaData wrk = getMetaData();
		if (wrk instanceof SQLCobolRecordMetaData2) {
			SQLCobolRecordMetaData2 meta = (SQLCobolRecordMetaData2) wrk;
			try {
				keyReadStatement = getConnection().prepareStatement(meta.getKeyReadStatement());
			} catch (SQLException ex) {
				keyReadStatement = null;
				SQLNetServer.logger.severe(ex.toString());
				ret = getException2FileStatus(ex);
			}
			try {
				startGTStatement = getConnection().prepareStatement(meta.getStartGTStatement());
			} catch (SQLException ex) {
				startGTStatement = null;
				SQLNetServer.logger.severe(ex.toString());
				ret = getException2FileStatus(ex);
			}
			try {
				startGEStatement = getConnection().prepareStatement(meta.getStartGEStatement());
			} catch (SQLException ex) {
				startGEStatement = null;
				SQLNetServer.logger.severe(ex.toString());
				ret = getException2FileStatus(ex);
			}
		}
		return ret;
	}

	/**
	 * @param keyReadStatement the keyReadStatement to set
	 */
	public void setKeyReadStatement(PreparedStatement keyReadStatement) {
		this.keyReadStatement = keyReadStatement;
	}

	/**
	 * Set parateters to PreaparedStatement Object
	 * 
	 * @param record record
	 * @param stmt PreparedStatement Object
	 * @throws SQLException Exception
	 */
	protected void setParamsRecord2PreaparedStatement(byte[] record, PreparedStatement stmt) throws SQLException {
		CobolRecord cobrec = new DefaultCobolRecord(getMetaData());
		cobrec.setRecord(record);

		int l = getMetaData().getKeyCount();
		int j = 1;
		for (int i = 0; i < l; i++) {
			CobolColumn column = getMetaData().getKey(i);
			if (column instanceof SQLCobolColumn) {
				SQLCobolColumn key = (SQLCobolColumn) column;
				switch (key.getType()) {
				case CobolColumn.TYPE_XCHAR:
				case CobolColumn.TYPE_NCHAR:
					stmt.setString(j, cobrec.getString(key));
					break;
				case CobolColumn.TYPE_INTEGER:
					stmt.setInt(j, cobrec.getInt(key));
					break;
				case CobolColumn.TYPE_LONG:
					stmt.setLong(j, cobrec.getLong(key));
					break;
				case CobolColumn.TYPE_DATE:
					stmt.setDate(j, new java.sql.Date(cobrec.getDate(key).getTime()));
					break;
				case CobolColumn.TYPE_TIME:
					stmt.setTime(j, new java.sql.Time(cobrec.getDate(key).getTime()));
					break;
				case CobolColumn.TYPE_TIMESTAMP:
					stmt.setTimestamp(j, new java.sql.Timestamp(cobrec.getDate(key).getTime()));
					break;
				case CobolColumn.TYPE_DOUBLE:
					stmt.setDouble(j, cobrec.getDouble(key));
					break;
				case CobolColumn.TYPE_FLOAT:
					stmt.setFloat(j, cobrec.getFloat(key));
					break;
				case CobolColumn.TYPE_DECIMAL:
					stmt.setBigDecimal(j, cobrec.getBigDecimal(key));
					break;
				default:
					stmt.setString(j, cobrec.getString(key));
					break;
				}
				j++;
			}
		}
	}

	/**
	 * @param startGEStatement the startGEStatement to set
	 */
	public void setStartGEStatement(PreparedStatement startGEStatement) {
		this.startGEStatement = startGEStatement;
	}

	/**
	 * @param startGTStatement the startGTStatement to set
	 */
	public void setStartGTStatement(PreparedStatement startGTStatement) {
		this.startGTStatement = startGTStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLFile#startEqual(byte[])
	 */
	@Override
	public FileStatus startDuplicatesEqual(byte[] record) {
		return startEqual(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLFile#startEqual(byte[])
	 */
	@Override
	protected FileStatus startEqual(byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		if (getStartGEStatement() == null) {
			ret = super.startGreaterEqual(record);
		} else {
			try {
				ResultSet wrk = getResultSet();
				if (wrk != null) {
					if (!wrk.isClosed()) {
						wrk.close();
					}
				}
				setParamsRecord2PreaparedStatement(record, getStartGEStatement());
				wrk = getStartGEStatement().executeQuery();
				if (wrk.first()) {
					byte[] CurrentRecord = new byte[getMetaData().getRowSize()];
					read(CurrentRecord);
					int comp = compare(CurrentRecord, record);
					if (comp == COMPARE_EQUAL) {
						// not found(after last)
						ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
					} else {
						ret = FileStatus.OK;
					}
				} else {
					// not found(after last)
					ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
				}
			} catch (SQLException e) {
				ret = getException2FileStatus(e);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLFile#startGreater(byte[])
	 */
	@Override
	protected FileStatus startGreater(byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		if (getStartGTStatement() == null) {
			ret = super.startGreater(record);
		} else {
			try {
				ResultSet wrk = getResultSet();
				if (wrk != null) {
					if (!wrk.isClosed()) {
						wrk.close();
					}
				}
				setParamsRecord2PreaparedStatement(record, getStartGTStatement());
				wrk = getStartGTStatement().executeQuery();
				if (wrk.first()) {
					setResultSet(wrk);
					ret = FileStatus.OK;
				} else {
					// not found(after last)
					ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
				}
			} catch (SQLException e) {
				ret = getException2FileStatus(e);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.sql.SQLFile#startGreaterEqual(byte[])
	 */
	@Override
	protected FileStatus startGreaterEqual(byte[] record) {
		FileStatus ret = FileStatus.FAILURE;
		if (getStartGEStatement() == null) {
			ret = super.startGreaterEqual(record);
		} else {
			try {
				ResultSet wrk = getResultSet();
				if (wrk != null) {
					if (!wrk.isClosed()) {
						wrk.close();
					}
				}
				setParamsRecord2PreaparedStatement(record, getStartGEStatement());
				wrk = getStartGEStatement().executeQuery();
				if (wrk.first()) {
					setResultSet(wrk);
					ret = FileStatus.OK;
				} else {
					// not found(after last)
					ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
				}
			} catch (SQLException e) {
				ret = getException2FileStatus(e);
			}
		}
		return ret;
	}

}
