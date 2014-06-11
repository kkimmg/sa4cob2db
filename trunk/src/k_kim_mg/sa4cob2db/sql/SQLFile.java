package k_kim_mg.sa4cob2db.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import k_kim_mg.sa4cob2db.AbstractCobolFile;
import k_kim_mg.sa4cob2db.CobolFile;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.FileStatus;
import k_kim_mg.sa4cob2db.event.CobolFileEvent;

/**
 * file
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLFile extends AbstractCobolFile implements CobolFile {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private boolean lastShowed = false;
	private SQLCobolRecordMetaData meta;
	private ResultSet resultSet;
	private int rowCount = 0;
	private SQLCobolRecord rowData;
	private volatile boolean isInReopen = false;

	/**
	 * Constructor
	 * 
	 * @param connection Connection
	 * @param meta meta data
	 */
	public SQLFile(Connection connection, SQLCobolRecordMetaData meta) {
		this.connection = connection;
		this.meta = meta;
		setInitialSequentialReadBufferSize(meta.getInitialSequencialReadBufferSize());
		setMinimumSequentialReadBufferSize(meta.getMinimumSequencialReadBufferSize());
		setMaximumSequentialReadBufferSize(meta.getMaximumSequencialReadBufferSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#close()
	 */
	public FileStatus close() {
		getEventProcessor().preClose(getReadyEvent());
		FileStatus ret = FileStatus.OK;
		try {
			if (resultSet == null) {
				ret = new FileStatus(FileStatus.STATUS_NOT_OPEN, FileStatus.NULL_CODE, 0, "ResultSet is Null.");
			} else if (rowData == null) {
				ret = new FileStatus(FileStatus.STATUS_NOT_OPEN, FileStatus.NULL_CODE, 0, "RowData is Null.");
			} else {
				resultSet.close();
			}
			closeIndexes();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = getSQLException2FileStatus(e);
		} finally {
			resultSet = null;
			rowData = null;
		}
		getEventProcessor().postClose(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * create record
	 * 
	 * @return record
	 */
	public SQLCobolRecord createCobolRecord() {
		return createCobolRecord(meta, resultSet);
	}

	/**
	 * create record
	 * 
	 * @param meta meta data
	 * @param resultSet result set
	 * @return record
	 */
	protected SQLCobolRecord createCobolRecord(SQLCobolRecordMetaData meta, ResultSet resultSet) {
		return new SQLCobolRecord(meta, connection, resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#delete()
	 */
	public FileStatus delete(byte[] record) {
		getEventProcessor().preDelete(getReadyEvent());
		FileStatus ret = move(record);
		if (ret.getStatusCode() == FileStatus.STATUS_SUCCESS) {
			try {
				resultSet.deleteRow();
				try {
					int row = getCurrentRow();
					move(row);
					while (resultSet.rowDeleted()) {
						next();
					}
				} catch (Exception e1) {
					SQLNetServer.logger.log(Level.WARNING, e1.getLocalizedMessage());
					ret = getException2FileStatus(e1);
				}
			} catch (SQLException e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				ret = getSQLException2FileStatus(e);
			} catch (Exception e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				ret = getException2FileStatus(e);
			}
		}
		getEventProcessor().postDelete(getFileStatus2Event(ret));
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#getCurrentRow()
	 */
	public int getCurrentRow() {
		int row = 0;
		try {
			row = resultSet.getRow();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			row = 0;
		}
		return row;
	}

	/**
	 * exception to file status
	 * 
	 * @param e exception
	 * @return file status
	 */
	protected FileStatus getException2FileStatus(Exception e) {
		return new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
	}

	/**
	 * create event object
	 * 
	 * @param stat file status
	 * @return file event
	 */
	protected CobolFileEvent getFileStatus2Event(FileStatus stat) {
		return new CobolFileEvent(this, stat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#getMetaData()
	 */
	public CobolRecordMetaData getMetaData() {
		return meta;
	}

	/**
	 * create event object
	 * 
	 * @return fileevent(READY)
	 */
	protected CobolFileEvent getReadyEvent() {
		return new CobolFileEvent(this, FileStatus.READY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#getRowCount()
	 */
	public int getRowCount() {
		setRowCount();
		return rowCount;
	}

	/**
	 * create file status from exception
	 * 
	 * @param e SQLexception
	 * @return file status
	 */
	protected FileStatus getSQLException2FileStatus(SQLException e) {
		return new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
	}

	/**
	 * @return the isInReopen
	 */
	protected boolean isInReopen() {
		return isInReopen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#isLastMoved()
	 */
	public boolean isLastMoved() {
		return lastShowed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#isOpened()
	 */
	public boolean isOpened() {
		return !(resultSet == null && rowData == null);
	}

	@Override
	public boolean isReOpenWhenNoDataFound() {
		return meta.isReOpenWhenNoDataFound();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#move(byte[])
	 */
	public FileStatus move(byte[] record) {
		resetCurrentIndex();
		getEventProcessor().preMove(getReadyEvent(), record);
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		try {
			int currentRow = 0;
			byte[] currentRecord = new byte[meta.getRowSize()];
			// int status;
			if (getRowCount() > 0) {
				// store current record
				currentRow = resultSet.getRow();
			} else if (!(resultSet.isAfterLast() || resultSet.isBeforeFirst())) {
				// first record
				currentRow = resultSet.getRow();
				try {
					/* status = */
					read(currentRecord);
				} catch (Exception e1) {
					SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
				}
			} else {
				// no record
				ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, FileStatus.NULL_CODE, 0, "move(byte[]) can't find record.");
				getEventProcessor().postMove(getFileStatus2Event(ret));
				if (isReOpenWhenNoDataFound() && !isInReopen()) {
					setInReopen(true);
					FileStatus work = reopen();
					if (work.getStatusCode().equals(FileStatus.STATUS_SUCCESS)) {
						ret = move(record);
					} else {
						ret = new FileStatus(FileStatus.STATUS_99_FAILURE, ret.getSqlStatus(), ret.getErrStatus(), ret.getStatusMessage());
					}
					setInReopen(false);
				}
				return ret;
			}
			// check first record and ....
			int comp = compare(currentRecord, record);
			if (comp == COMPARE_REC1) {
				// search second half
				ret = move(record, 1, getRowCount());
			} else if (comp == COMPARE_REC2) {
				// search first half
				ret = move(record, 1, currentRow - 1);
			} else {
				// hit!
				ret = FileStatus.OK;
				resultSet.absolute(currentRow);
			}
			if (ret.getStatusCode().equals(FileStatus.STATUS_KEY_NOT_EXISTS) && isReOpenWhenNoDataFound() && !isInReopen()) {
				setInReopen(true);
				FileStatus work = reopen();
				if (work.getStatusCode().equals(FileStatus.STATUS_SUCCESS)) {
					ret = move(record);
				} else {
					ret = new FileStatus(FileStatus.STATUS_99_FAILURE, ret.getSqlStatus(), ret.getErrStatus(), ret.getStatusMessage());
				}
				setInReopen(false);
			} else if (ret.getStatusCode() != FileStatus.STATUS_SUCCESS) {
				// not found
				resultSet.absolute(currentRow);
				rowData.setRecord(currentRecord);
			}
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = getSQLException2FileStatus(e);
		}
		getEventProcessor().postMove(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * move to record that located by key value
	 * 
	 * @param record record includes key value
	 * @param start start of range
	 * @param end end of range
	 * @return file status
	 */
	protected FileStatus move(byte[] record, int start, int end) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		int naka = (start + end) / 2; // half
		if (naka <= 0) {
			return new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, FileStatus.NULL_CODE, 0, "movd(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
		}
		byte[] CurrentRecord = new byte[meta.getRowSize()]; // current record
		try {
			resultSet.absolute(naka);
			read(CurrentRecord);
			int comp = compare(CurrentRecord, record);
			if (comp == COMPARE_REC1) {
				if (naka < getRowCount()) {
					if (naka < end) {
						// second half
						ret = move(record, naka + 1, end);
					} else {
						// not found
						ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, FileStatus.NULL_CODE, 0, "move(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
					}
				} else {
					if (isLastMoved()) {
						// after eof ... not found
						ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, FileStatus.NULL_CODE, 0, "movd(byte[]," + start + ", " + end + ") is can't find record.(isLastMoved) " + "location is " + naka);
					} else {
						if (next(getRowCount()).getStatusCode() == FileStatus.STATUS_SUCCESS) {
							// more records
							ret = move(record, naka + 1, getRowCount());
						} else if (moveLast().getStatusCode() == FileStatus.STATUS_SUCCESS) {
							// more records to eof
							ret = move(record, naka + 1, getRowCount());
						} else {
							// not found
							ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, FileStatus.NULL_CODE, 0, "move(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
						}
					}
				}
			} else if (comp == COMPARE_REC2) {
				if (naka <= 1) {
					// not found
					ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, FileStatus.NULL_CODE, 0, "movd(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
				} else {
					// first half
					ret = move(record, start, naka - 1);
				}
			} else {
				// found!
				ret = FileStatus.OK;
				resultSet.absolute(naka);
			}
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = getSQLException2FileStatus(e);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#move(int)
	 */
	public FileStatus move(int row) {
		boolean ok = false;
		try {
			ok = resultSet.absolute(row);
			setRowCount();
			if (resultSet.isLast()) {
				lastShowed = true;
			}
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			return getSQLException2FileStatus(e);
		}
		return (ok ? FileStatus.OK : STATUS_UNKNOWN_ERROR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#moveFirst()
	 */
	public FileStatus moveFirst() {
		boolean ok = false;
		try {
			ok = resultSet.first();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			return getSQLException2FileStatus(e);
		}
		return (ok ? FileStatus.OK : STATUS_UNKNOWN_ERROR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#moveLast()
	 */
	public FileStatus moveLast() {
		boolean ok = false;
		try {
			ok = resultSet.last();
			lastShowed = true;
			setRowCount();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			return getSQLException2FileStatus(e);
		}
		return (ok ? FileStatus.OK : STATUS_UNKNOWN_ERROR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#next()
	 */
	@Override
	public FileStatus next() {
		getEventProcessor().preNext(getReadyEvent()); // event
		FileStatus ret = super.next();
		getEventProcessor().postNext(getFileStatus2Event(ret)); // event
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#next(int)
	 */
	public FileStatus next(int row) {
		if (row < 0)
			return previous(row * -1);
		boolean ok = false;
		try {
			if (resultSet.isLast()) {
				lastShowed = true;
				return AbstractCobolFile.STATUS_EOF;
			}
			ok = resultSet.relative(row);
			if (resultSet.isLast()) {
				lastShowed = true;
			}
			setRowCount();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			return getSQLException2FileStatus(e);
		}
		return (ok ? FileStatus.OK : STATUS_UNKNOWN_ERROR);
	}

	/**
	 * next record
	 * 
	 * @return status
	 */
	protected FileStatus nextOnFile() {
		boolean ok = false;
		try {
			try {
				if (resultSet.isAfterLast()) {
					lastShowed = true;
					return AbstractCobolFile.STATUS_EOF;
				} else if (resultSet.isLast()) {
					lastShowed = true;
				}
			} catch (SQLException e1) {
				SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
			}
			ok = resultSet.next();
			try {
				if (resultSet.isLast()) {
					lastShowed = true;
				}
			} catch (SQLException e2) {
				SQLNetServer.logger.log(Level.SEVERE, e2.getMessage(), e2);
			}
			setRowCount();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			return new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		return (ok ? FileStatus.OK : AbstractCobolFile.STATUS_EOF);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#open(int)
	 */
	public FileStatus open(int mode, int accessmode) {
		this.openmode = mode;
		this.accessMode = accessmode;
		int resultSetType = (accessmode == CobolFile.ACCESS_SEQUENTIAL ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_SENSITIVE);
		int resultSetConcurrency = (mode == CobolFile.MODE_INPUT ? ResultSet.CONCUR_READ_ONLY : ResultSet.CONCUR_UPDATABLE);
		getEventProcessor().preOpen(getReadyEvent());
		FileStatus ret = FileStatus.OK;
		try {
			if (resultSet != null) {
				SQLNetServer.logger.log(Level.INFO, "already opened.");
				ret = new FileStatus(FileStatus.STATUS_ALREADY_OPEN, FileStatus.NULL_CODE, 0, "resultset isn't null.");
			} else if (rowData != null) {
				SQLNetServer.logger.log(Level.INFO, "already opened.");
				ret = new FileStatus(FileStatus.STATUS_ALREADY_OPEN, FileStatus.NULL_CODE, 0, "rowData isn't null.");
			} else {
				Statement statement = null;
				statement = connection.createStatement(resultSetType, resultSetConcurrency);
				resultSet = statement.executeQuery(meta.getSelectStatement());
				rowData = createCobolRecord(meta, resultSet);
			}
			openIndexes();
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, "can't open.", e);
			ret = getSQLException2FileStatus(e);
		}
		rowCount = 0;
		if ((getMaximumSequentialReadBufferSize() > 0 && getAccessMode() == CobolFile.ACCESS_SEQUENTIAL && getOpenMode() == CobolFile.MODE_INPUT)) {
			startBuffer();
		}
		if (mode == CobolFile.MODE_EXTEND) {
			moveLast();
		} else if (mode == CobolFile.MODE_OUTPUT) {
			// delete all records for over write
			truncate();
		} else {
			// first record
			next();
		}
		getEventProcessor().postOpen(getFileStatus2Event(ret));
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#previous()
	 */
	public FileStatus previous() {
		getEventProcessor().prePrevious(getReadyEvent());
		FileStatus ret = FileStatus.OK;
		try {
			if (resultSet.isFirst()) {
				ret = new FileStatus(FileStatus.STATUS_NOT_AVAILABLE, FileStatus.NULL_CODE, 0, "Begin of File.");
			} else {
				if (!resultSet.previous()) {
					ret = STATUS_UNKNOWN_ERROR;
				}
			}
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = getSQLException2FileStatus(e);
		}
		getEventProcessor().postPrevious(getFileStatus2Event(ret));
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#previous(int)
	 */
	public FileStatus previous(int row) {
		if (row < 0)
			return next(row * -1);
		boolean ok = false;
		try {
			if (resultSet.isFirst()) {
				return new FileStatus(FileStatus.STATUS_NOT_AVAILABLE, FileStatus.NULL_CODE, 0, "Begin of File." + row);
			}
			ok = resultSet.relative(row * -1);
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			return new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		return (ok ? FileStatus.OK : STATUS_UNKNOWN_ERROR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#read(byte[])
	 */
	@Override
	public FileStatus read(byte[] record) {
		getEventProcessor().preRead(getReadyEvent()); // event
		FileStatus ret = super.read(record);
		getEventProcessor().postRead(getFileStatus2Event(ret), record); // event
		return ret;
	}

	/**
	 * read from file
	 * 
	 * @param record record
	 * @return status
	 */
	protected FileStatus readFromFile(byte[] record) {
		int length = 0;
		try {
			if (resultSet.isAfterLast()) {
				// eof
				return AbstractCobolFile.STATUS_EOF;
			} else {
				rowData.result2record();
				length = rowData.getRecord(record);
			}
		} catch (CobolRecordException ce) {
			SQLNetServer.logger.log(Level.SEVERE, ce.getMessage(), ce);
			return new FileStatus(FileStatus.STATUS_99_FAILURE, ce.getSQLState(), ce.getErrorCode(), ce.getMessage());
		} catch (SQLException se) {
			SQLNetServer.logger.log(Level.SEVERE, se.getMessage(), se);
			return getSQLException2FileStatus(se);
		}
		return (length == meta.getRowSize() ? FileStatus.OK : new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "RecordSize MissMatch."));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#reopen()
	 */
	@Override
	public FileStatus reopen() {
		this.rowCount = 0;
		this.lastShowed = false;
		return super.reopen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#rewrite(byte[])
	 */
	public FileStatus rewrite(byte[] record) {
		getEventProcessor().preRewrite(getReadyEvent(), record);
		FileStatus ret = move(record);
		if (ret.getStatusCode() == FileStatus.STATUS_SUCCESS) {
			int length = 0;
			try {
				length = rowData.setRecord(record);
				rowData.record2result(SQLCobolRecord.REWRITE);
				resultSet.updateRow();
				if (length != meta.getRowSize()) {
					ret = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "RecordSize MissMatch.");
				}
			} catch (CobolRecordException e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				ret = new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
			} catch (SQLException e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				ret = getSQLException2FileStatus(e);
			}
		}
		getEventProcessor().postRewrite(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * @param isInReopen the isInReopen to set
	 */
	protected void setInReopen(boolean isInReopen) {
		this.isInReopen = isInReopen;
	}

	/**
	 * set row count
	 */
	private void setRowCount() {
		try {
			int currentRow = resultSet.getRow();
			if (rowCount < currentRow) {
				rowCount = currentRow;
			}
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(int, byte[])
	 */
	public FileStatus start(int mode, byte[] record) {
		resetCurrentIndex();
		getEventProcessor().preStart(getReadyEvent(), record);
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		if (mode == IS_EQUAL_TO) {
			ret = move(record);
		} else if (mode == IS_GREATER_THAN) {
			ret = startGreater(record);
		} else if (mode == IS_GREATER_THAN_OR_EQUAL_TO) {
			ret = move(record);
			if (ret.getStatusCode() != FileStatus.STATUS_SUCCESS) {
				ret = startGreater(record);
			}
		} else if (mode == IS_NOT_LESS_THAN) {
			ret = move(record);
			if (ret.getStatusCode() != FileStatus.STATUS_SUCCESS) {
				ret = startGreater(record);
			}
		} else {
			ret = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "invalid mode.");
		}
		if (ret.getStatusCode().equals(FileStatus.STATUS_KEY_NOT_EXISTS) && isReOpenWhenNoDataFound() && !isInReopen()) {
			setInReopen(true);
			FileStatus work = reopen();
			if (work.getStatusCode().equals(FileStatus.STATUS_SUCCESS)) {
				ret = start(mode, record);
			} else {
				ret = new FileStatus(FileStatus.STATUS_99_FAILURE, ret.getSqlStatus(), ret.getErrStatus(), ret.getStatusMessage());
			}
			setInReopen(false);
		}
		getEventProcessor().postStart(getReadyEvent());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#start(java.lang.String, int, byte[])
	 */
	@Override
	public FileStatus start(String indexName, int mode, byte[] record) {
		getEventProcessor().preStart(getReadyEvent(), indexName, record);
		FileStatus ret = super.start(indexName, mode, record);
		getEventProcessor().postStart(getReadyEvent(), indexName);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#startDuplicates(int, byte[])
	 */
	@Override
	public FileStatus startDuplicates(int mode, byte[] record) {
		resetCurrentIndex();
		getEventProcessor().preStart(getReadyEvent(), record);
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		if (mode == IS_EQUAL_TO) {
			ret = startDuplicatesEqual(record);
		} else if (mode == IS_GREATER_THAN) {
			ret = startGreater(record);
		} else if (mode == IS_GREATER_THAN_OR_EQUAL_TO) {
			ret = startDuplicatesEqual(record);
			if (ret.getStatusCode() != FileStatus.STATUS_SUCCESS) {
				ret = startGreater(record);
			}
		} else if (mode == IS_NOT_LESS_THAN) {
			ret = startDuplicatesEqual(record);
			if (ret.getStatusCode() != FileStatus.STATUS_SUCCESS) {
				ret = startGreater(record);
			}
		} else {
			ret = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "invalid mode.");
		}
		getEventProcessor().postStart(getReadyEvent());
		return ret;
	}

	/**
	 * start with duplicted key (greater equal)
	 * 
	 * @param record record includes key value
	 * @return file status
	 */
	public FileStatus startDuplicatesEqual(byte[] record) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		try {
			byte[] currentRecord = new byte[meta.getRowSize()];
			boolean first = resultSet.first();
			if (first) {
				read(currentRecord);
				int comp = compare(currentRecord, record);
				if (comp == COMPARE_EQUAL) {
					// currentRecord = record(hit)
					ret = FileStatus.OK;
				} else if (comp == COMPARE_REC1) {
					// currentRecord < record(hit)
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(currentRecord);
						comp = compare(currentRecord, record);
						if (comp == COMPARE_REC1) {
							// currentRecord < record
							found = true;
							ret = FileStatus.OK;
							// } else if (comp == COMPARE_REC2) {
							// currentRecord < record
							// break;
						}
					}
					if (!found) {
						// not found
						ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
					}
				}
			} else {
				// not found
				ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// SQLexception
			return getSQLException2FileStatus(e);
		}
		return ret;
	}

	/**
	 * start(=)
	 * 
	 * @param record record includes key value
	 * @return status
	 */
	FileStatus startEqual(byte[] record) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		try {
			byte[] CurrentRecord = new byte[meta.getRowSize()];
			boolean first = resultSet.first();
			if (first) {
				read(CurrentRecord);
				int comp = compare(CurrentRecord, record);
				if (comp == COMPARE_EQUAL) {
					// currentRecord = record(hit)
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(CurrentRecord);
						comp = compare(CurrentRecord, record);
						if (comp == COMPARE_EQUAL) {
							// currentRecord = record(hit)
							found = true;
							ret = FileStatus.OK;
						} else if (comp == COMPARE_REC1) {
							break;
						}
					}
					if (!found) {
						// not found(after last)
						ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
					}
				}
			} else {
				// not found
				ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// SQLexception
			return getSQLException2FileStatus(e);
		}
		return ret;
	}

	/**
	 * start(Greater)
	 * 
	 * @param record record includes key value
	 * @return status
	 */
	FileStatus startGreater(byte[] record) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		try {
			byte[] CurrentRecord = new byte[meta.getRowSize()];
			boolean first = resultSet.first();
			if (first) {
				read(CurrentRecord);
				int comp = compare(CurrentRecord, record);
				if (comp == COMPARE_REC1) {
					// currentRecord < record(hit)
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(CurrentRecord);
						comp = compare(CurrentRecord, record);
						if (comp == COMPARE_REC1) {
							// currentRecord < record(hit)
							found = true;
							ret = FileStatus.OK;
						}
					}
					if (!found) {
						// not found(after last)
						ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
					}
				}
			} else {
				// not found
				ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// SQLexception
			return getSQLException2FileStatus(e);
		}
		return ret;
	}

	/**
	 * start(Greater Equal)
	 * 
	 * @param record record includes key value
	 * @return status
	 */
	FileStatus startGreaterEqual(byte[] record) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		try {
			byte[] CurrentRecord = new byte[meta.getRowSize()];
			boolean first = resultSet.first();
			if (first) {
				read(CurrentRecord);
				int comp = compare(CurrentRecord, record);
				if (comp != COMPARE_REC2) {
					// currentRecord = record(hit)
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(CurrentRecord);
						comp = compare(CurrentRecord, record);
						if (comp != COMPARE_REC2) {
							// currentRecord <= record(hit)
							found = true;
							ret = FileStatus.OK;
						}
					}
					if (!found) {
						// not found(after last)
						ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "can't find.");
					}
				}
			} else {
				// not found
				ret = new FileStatus(FileStatus.STATUS_KEY_NOT_EXISTS, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// SQLexception
			return getSQLException2FileStatus(e);
		}
		return ret;
	}

	/*
	 * delete all
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#truncate()
	 */
	public void truncate() {
		String truncate = meta.getTruncateStatement();
		if (truncate != null) {
			if (truncate.trim().length() > 0) {
				try {
					//
					Statement statement = null;
					try {
						statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						statement.execute(truncate);
					} finally {
						statement.close();
					}
				} catch (SQLException e) {
					SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#write(byte[])
	 */
	public FileStatus write(byte[] record) {
		getEventProcessor().preWrite(getReadyEvent(), record);
		FileStatus ret = FileStatus.OK;
		try {
			resultSet.moveToInsertRow();
			/* length = */rowData.setRecord(record);
			rowData.record2result(SQLCobolRecord.WRITE);
			resultSet.insertRow();
		} catch (CobolRecordException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = getSQLException2FileStatus(e);
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = getSQLException2FileStatus(e);
		}
		getEventProcessor().postWrite(getFileStatus2Event(ret));
		return ret;
	}
}
