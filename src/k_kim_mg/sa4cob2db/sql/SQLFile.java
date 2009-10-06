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
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class SQLFile extends AbstractCobolFile implements CobolFile {
	private static final long serialVersionUID = 1L;
	private transient Connection connection;
	private boolean lastShowed = false;
	private transient SQLCobolRecordMetaData meta;
	private transient ResultSet resultSet;
	private int rowCount = 0;
	private transient SQLCobolRecord rowData;
	/**
	 * ���󥹥ȥ饯��
	 * @param connection ���ͥ������
	 * @param meta �᥿�ǡ���
	 */
	public SQLFile(Connection connection, SQLCobolRecordMetaData meta) {
		this.connection = connection;
		this.meta = meta;
		// �Хåե����������
		setInitialSequencialReadBufferSize(meta.getInitialSequencialReadBufferSize());
		setMinimumSequencialReadBufferSize(meta.getMinimumSequencialReadBufferSize());
		setMaximumSequencialReadBufferSize(meta.getMaximumSequencialReadBufferSize());
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#close()
	 */
	public FileStatus close() {
		getEventProcessor().preClose(getReadyEvent());
		FileStatus ret = FileStatus.OK;
		try {
			if (resultSet == null) {
				ret = new FileStatus(FileStatus.STATUS_ALREADY_CLOSED, FileStatus.NULL_CODE, 0, "ResultSet is Null.");
			} else if (rowData == null) {
				ret = new FileStatus(FileStatus.STATUS_ALREADY_CLOSED, FileStatus.NULL_CODE, 0, "RowData is Null.");
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
	 * �쥳���ɤκ���
	 * @return �쥳����
	 */
	public SQLCobolRecord createCobolRecord() {
		return createCobolRecord(meta, resultSet);
	}
	/**
	 * �쥳���ɤκ���
	 * @param meta �᥿�ǡ���
	 * @param resultSet ��̥��å�
	 * @return �쥳����
	 */
	protected SQLCobolRecord createCobolRecord(SQLCobolRecordMetaData meta, ResultSet resultSet) {
		return new SQLCobolRecord(meta, connection, resultSet);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#delete()
	 */
	public FileStatus delete(byte[] record) {
		getEventProcessor().preDelete(getReadyEvent());
		FileStatus ret = move(record);
		if (ret.getStatusCode() == FileStatus.STATUS_OK) {
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
	 * Java�㳰����ե����륹�ơ��������Ѵ�����
	 * @param e java�㳰
	 * @return �ե����륹�ơ�����
	 */
	protected FileStatus getException2FileStatus(Exception e) {
		return new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
	}
	/**
	 * �ե����륹�ơ���������ե����륤�٥�Ȥ�ȯ��������
	 * @param stat �ե����륹�ơ�����
	 * @return �ե����륤�٥��
	 */
	protected CobolFileEvent getFileStatus2Event(FileStatus stat) {
		return new CobolFileEvent(this, stat);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#getMetaData()
	 */
	public CobolRecordMetaData getMetaData() {
		return meta;
	}
	/**
	 * READY���٥�Ȥ�ȯ��������
	 * @return �ե����륤�٥��(READY)
	 */
	protected CobolFileEvent getReadyEvent() {
		return new CobolFileEvent(this, FileStatus.READY);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#getRowCount()
	 */
	public int getRowCount() {
		setRowCount();
		return rowCount;
	}
	/**
	 * SQL�㳰����ե����륹�ơ��������Ѵ�����
	 * @param e SQL�㳰
	 * @return �ե����륹�ơ�����
	 */
	protected FileStatus getSQLException2FileStatus(SQLException e) {
		return new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#isLastMoved()
	 */
	public boolean isLastMoved() {
		return lastShowed;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#isOpened()
	 */
	public boolean isOpened() {
		return !(resultSet == null && rowData == null);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#move(byte[])
	 */
	public FileStatus move(byte[] record) {
		resetCurrentIndex();
		getEventProcessor().preMove(getReadyEvent(), record);
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		try {
			int CurrentRow = 0;
			byte[] CurrentRecord = new byte[meta.getRowSize()];
			// int status;
			if (getRowCount() > 0) {
				// �����ȥ쥳���ɤ�����
				CurrentRow = resultSet.getRow();
			} else if (!(resultSet.isAfterLast() || resultSet.isBeforeFirst())) {
				// �����ܤΥ쥳���ɤ����
				CurrentRow = resultSet.getRow();
				try {
					/* status = */
					read(CurrentRecord);
				} catch (Exception e1) {
					SQLNetServer.logger.log(Level.SEVERE, e1.getMessage(), e1);
				}
			} else {
				// �����ޤޤ�Ƥ��ʤ�
				ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, FileStatus.NULL_CODE, 0, "move(byte[]) can't find record.");
				getEventProcessor().postMove(getFileStatus2Event(ret));
				return ret;
			}
			// ���ߤΥ쥳���ɤ���Ӥ��Ƥ��鸡������
			int comp = compare(CurrentRecord, record);
			if (comp == COMPARE_REC1) {
				// ���ߤθ��Ⱦʬ�򸡺�����
				ret = move(record, 1, getRowCount());
			} else if (comp == COMPARE_REC2) {
				// ���ߤ���Ⱦʬ�򸡺�����
				ret = move(record, 1, CurrentRow - 1);
			} else {
				// �ʤ�ȣ����ܤǥҥå�
				ret = FileStatus.OK;
				resultSet.absolute(CurrentRow);
			}
			if (ret.getStatusCode() != FileStatus.STATUS_OK) {
				// ���������Ԥ����鸵�ΰ��֤����
				resultSet.absolute(CurrentRow);
				rowData.setRecord(CurrentRecord);
			}
		} catch (SQLException e) {
			// ���餫��SQL�㳰
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			ret = getSQLException2FileStatus(e);
		}
		getEventProcessor().postMove(getFileStatus2Event(ret));
		return ret;
	}
	/**
	 * �쥳���ɤΰ����դ�
	 * @param record �����쥳����
	 * @param start ���ϰ���
	 * @param end ��ü
	 * @return �ե����륹�ơ�����
	 */
	protected FileStatus move(byte[] record, int start, int end) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		int naka = (start + end) / 2; // ��ְ���
		if (naka <= 0) {
			return new FileStatus(FileStatus.STATUS_INVALID_KEY, FileStatus.NULL_CODE, 0, "movd(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
		}
		byte[] CurrentRecord = new byte[meta.getRowSize()]; // ���ߤΥ쥳����
		try {
			resultSet.absolute(naka);
			read(CurrentRecord);
			int comp = compare(CurrentRecord, record);
			if (comp == COMPARE_REC1) {
				if (naka < getRowCount()) {
					if (naka < end) {
						// ����˸��Ⱦʬ�򸡺�����
						ret = move(record, naka + 1, end);
					} else {
						// ������
						ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, FileStatus.NULL_CODE, 0, "move(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
					}
				} else {
					if (isLastMoved()) {
						// ���Ǥ����쥳���ɤη����������Ƥ��ꤳ��ʾ�θ�����̵�̤Ǥ���
						ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, FileStatus.NULL_CODE, 0, "movd(byte[]," + start + ", " + end + ") is can't find record.(isLastMoved) " + "location is " + naka);
					} else {
						if (next(getRowCount()).getStatusCode() == FileStatus.STATUS_OK) {
							// �쥳���ɤκǽ����֤򸽺ߤ��ܤΰ��֤Ȥ���
							ret = move(record, naka + 1, getRowCount());
						} else if (moveLast().getStatusCode() == FileStatus.STATUS_OK) {
							// ��̥��åȤκǽ����֤��������
							ret = move(record, naka + 1, getRowCount());
						} else {
							// ������
							ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, FileStatus.NULL_CODE, 0, "move(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
						}
					}
				}
			} else if (comp == COMPARE_REC2) {
				if (naka <= 1) {
					// �����ܤ���ǡ�����������
					ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, FileStatus.NULL_CODE, 0, "movd(byte[]," + start + ", " + end + ") is can't find record. location is " + naka);
				} else {
					// �������Ⱦʬ�򸡺�����
					ret = move(record, start, naka - 1);
				}
			} else {
				// �ҥå�
				ret = FileStatus.OK;
				// �ǥե��å�
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
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#next()
	 */
	@Override
	public FileStatus next() {
		getEventProcessor().preNext(getReadyEvent()); // ���٥�Ƚ���
		FileStatus ret = super.next();
		getEventProcessor().postNext(getFileStatus2Event(ret)); // ���٥�Ƚ���
		return ret;
	}
	/*
	 * (non-Javadoc)
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
	 * �����ե�����ι԰��֤��ư����
	 * @return ���ơ�����
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
			return new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		return (ok ? FileStatus.OK : AbstractCobolFile.STATUS_EOF);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#open(int)
	 */
	public FileStatus open(int mode, int accessmode) {
		this.openmode = mode;
		this.accessMode = accessmode;
		int resultSetType = (accessmode == CobolFile.ACCESS_SEQUENCIAL ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_INSENSITIVE);
		int resultSetConcurrency = (mode == CobolFile.MODE_INPUT ? ResultSet.CONCUR_READ_ONLY : ResultSet.CONCUR_UPDATABLE);
		getEventProcessor().preOpen(getReadyEvent());
		FileStatus ret = FileStatus.OK;
		try {
			if (resultSet != null) {
				// ���˻��Ѥ���Ƥ��ʤ����ɤ����γ�ǧ
				SQLNetServer.logger.log(Level.INFO, "���Ǥ˥����ץ󤵤�Ƥ���");
				ret = new FileStatus(FileStatus.STATUS_ALREADY_OPENED, FileStatus.NULL_CODE, 0, "resultset isn't null.");
			} else if (rowData != null) {
				// ���˻��Ѥ���Ƥ��ʤ����ɤ����γ�ǧ
				SQLNetServer.logger.log(Level.INFO, "���Ǥ˥����ץ󤵤�Ƥ���");
				ret = new FileStatus(FileStatus.STATUS_ALREADY_OPENED, FileStatus.NULL_CODE, 0, "rowData isn't null.");
			} else {
				// �ǡ����١�����Ϣ����
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
		if ((getMaximumSequencialReadBufferSize() > 0 && getAccessMode() == CobolFile.ACCESS_SEQUENCIAL && getOpenMode() == CobolFile.MODE_INPUT)) {
			// �Хåե���󥰤γ���
			startBuffer();
		}
		// �ե�������֤ν������
		if (mode == CobolFile.MODE_EXTEND) {
			// �Ǹ�Υ쥳���ɤ�
			moveLast();
		} else if (mode == CobolFile.MODE_OUTPUT) {
			// �⤷��"���ƺ��"̿�᤬����м¹Ԥ���
			truncate();
		} else {
			// �ǽ�Υ쥳���ɤ�
			next();
		}
		getEventProcessor().postOpen(getFileStatus2Event(ret));
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#previous()
	 */
	public FileStatus previous() {
		getEventProcessor().prePrevious(getReadyEvent());
		FileStatus ret = FileStatus.OK;
		try {
			if (resultSet.isFirst()) {
				ret = new FileStatus(FileStatus.STATUS_BOF, FileStatus.NULL_CODE, 0, "Begin of File.");
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
	 * @see k_kim_mg.sa4cob2db.CobolFile#previous(int)
	 */
	public FileStatus previous(int row) {
		if (row < 0)
			return next(row * -1);
		boolean ok = false;
		try {
			if (resultSet.isFirst()) {
				return new FileStatus(FileStatus.STATUS_BOF, FileStatus.NULL_CODE, 0, "Begin of File." + row);
			}
			ok = resultSet.relative(row * -1);
		} catch (SQLException e) {
			SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
			return new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
		}
		return (ok ? FileStatus.OK : STATUS_UNKNOWN_ERROR);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.AbstractCobolFile#read(byte[])
	 */
	@Override
	public FileStatus read(byte[] record) {
		getEventProcessor().preRead(getReadyEvent()); // ���٥�Ƚ���
		FileStatus ret = super.read(record);
		getEventProcessor().postRead(getFileStatus2Event(ret), record); // ���٥�Ƚ���
		return ret;
	}
	/**
	 * �����ե����뤫���������
	 * @param record Ϣ�ۤ���쥳����
	 * @return ���ơ�����
	 */
	protected FileStatus readFromFile(byte[] record) {
		int length = 0;
		try {
			if (resultSet.isAfterLast()) {
				// ����ɥ��֥ե�����
				return AbstractCobolFile.STATUS_EOF;
			} else {
				rowData.result2record();
				length = rowData.getRecord(record);
			}
		} catch (CobolRecordException ce) {
			SQLNetServer.logger.log(Level.SEVERE, ce.getMessage(), ce);
			return new FileStatus(FileStatus.STATUS_FAILURE, ce.getSQLState(), ce.getErrorCode(), ce.getMessage());
		} catch (SQLException se) {
			SQLNetServer.logger.log(Level.SEVERE, se.getMessage(), se);
			return getSQLException2FileStatus(se);
		}
		return (length == meta.getRowSize() ? FileStatus.OK : new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "RecordSize MissMatch."));
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#rewrite(byte[])
	 */
	public FileStatus rewrite(byte[] record) {
		getEventProcessor().preRewrite(getReadyEvent(), record);
		FileStatus ret = move(record);
		if (ret.getStatusCode() == FileStatus.STATUS_OK) {
			int length = 0;
			try {
				length = rowData.setRecord(record);
				rowData.record2result(SQLCobolRecord.REWRITE);
				resultSet.updateRow();
				if (length != meta.getRowSize()) {
					ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "RecordSize MissMatch.");
				}
			} catch (CobolRecordException e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
			} catch (SQLException e) {
				SQLNetServer.logger.log(Level.SEVERE, e.getMessage(), e);
				ret = getSQLException2FileStatus(e);
			}
		}
		getEventProcessor().postRewrite(getFileStatus2Event(ret));
		return ret;
	}
	/**
	 * ���ߤι��ֹ�����ꤹ��
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
			if (ret.getStatusCode() != FileStatus.STATUS_OK) {
				ret = startGreater(record);
			}
		} else if (mode == IS_NOT_LESS_THAN) {
			ret = move(record);
			if (ret.getStatusCode() != FileStatus.STATUS_OK) {
				ret = startGreater(record);
			}
		} else {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "invalid mode.");
		}
		getEventProcessor().postStart(getReadyEvent());
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.AbstractCobolFile#start(java.lang.String,
	 * int, byte[])
	 */
	@Override
	public FileStatus start(String IndexName, int mode, byte[] record) {
		getEventProcessor().preStart(getReadyEvent(), IndexName, record);
		FileStatus ret = super.start(IndexName, mode, record);
		getEventProcessor().postStart(getReadyEvent(), IndexName);
		return ret;
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.AbstractCobolFile#startDuplicates(int,
	 * byte[])
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
			if (ret.getStatusCode() != FileStatus.STATUS_OK) {
				ret = startGreater(record);
			}
		} else if (mode == IS_NOT_LESS_THAN) {
			ret = startDuplicatesEqual(record);
			if (ret.getStatusCode() != FileStatus.STATUS_OK) {
				ret = startGreater(record);
			}
		} else {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "invalid mode.");
		}
		getEventProcessor().postStart(getReadyEvent());
		return ret;
	}
	/**
	 * �����ν�ʣ��������θ����Ͻ�����ǹԤ�
	 * @param record �����쥳����
	 * @return �ե����륹�ơ�����
	 */
	public FileStatus startDuplicatesEqual(byte[] record) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		try {
			byte[] CurrentRecord = new byte[meta.getRowSize()];
			boolean first = resultSet.first();
			if (first) {
				read(CurrentRecord);
				int comp = compare(CurrentRecord, record);
				if (comp == COMPARE_EQUAL) {
					// �ǽ�Υ쥳���ɤ������쥳���ɤ��������Τǰ��֤Ť���λ
					ret = FileStatus.OK;
				} else if (comp == COMPARE_REC1) {
					// �ǽ�Υ쥳���ɤ������쥳���ɤ���礭���Τǥ��顼
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(CurrentRecord);
						comp = compare(CurrentRecord, record);
						if (comp == COMPARE_REC1) {
							// �쥳���ɤ������쥳���ɤ���礭���Τǰ��֤Ť���λ
							found = true;
							ret = FileStatus.OK;
						} else if (comp == COMPARE_REC1) {
							// �쥳���ɤ������쥳���ɤ���礭���Τǥ��顼
							break;
						}
					}
					if (!found) {
						// ���֤Ť����Ǥ��ʤ��ޤ�EOF��ã����
						ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "can't find.");
					}
				}
			} else {
				// �Ԥ�¸�ߤ��ʤ�
				ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// ���餫��SQL�㳰
			return getSQLException2FileStatus(e);
		}
		return ret;
	}
	/**
	 * �����դ���=��
	 * @param record ������ޤ�쥳����
	 * @return ���ơ�����
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
					// �ǽ�Υ쥳���ɤ������쥳���ɤ���礭���Τǰ��֤Ť���λ
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(CurrentRecord);
						comp = compare(CurrentRecord, record);
						if (comp == COMPARE_EQUAL) {
							// �쥳���ɤ������쥳���ɤ���礭���Τǰ��֤Ť���λ
							found = true;
							ret = FileStatus.OK;
						} else if (comp == COMPARE_REC1) {
							break;
						}
					}
					if (!found) {
						// ���֤Ť����Ǥ��ʤ��ޤ�EOF��ã����
						ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "can't find.");
					}
				}
			} else {
				// �Ԥ�¸�ߤ��ʤ�
				ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// ���餫��SQL�㳰
			return getSQLException2FileStatus(e);
		}
		return ret;
	}
	/**
	 * �����դ���Greater��
	 * @param record ������ޤ�쥳����
	 * @return ���ơ�����
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
					// �ǽ�Υ쥳���ɤ������쥳���ɤ���礭���Τǰ��֤Ť���λ
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(CurrentRecord);
						comp = compare(CurrentRecord, record);
						if (comp == COMPARE_REC1) {
							// �쥳���ɤ������쥳���ɤ���礭���Τǰ��֤Ť���λ
							found = true;
							ret = FileStatus.OK;
						}
					}
					if (!found) {
						// ���֤Ť����Ǥ��ʤ��ޤ�EOF��ã����
						ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "can't find.");
					}
				}
			} else {
				// �Ԥ�¸�ߤ��ʤ�
				ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// ���餫��SQL�㳰
			return getSQLException2FileStatus(e);
		}
		return ret;
	}
	/**
	 * �����դ���Greater Equal��
	 * @param record ������ޤ�쥳����
	 * @return ���ơ�����
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
					// �ǽ�Υ쥳���ɤ������쥳���ɤ���礭���Τǰ��֤Ť���λ
					ret = FileStatus.OK;
				} else {
					boolean found = false;
					while (resultSet.next() && !found) {
						read(CurrentRecord);
						comp = compare(CurrentRecord, record);
						if (comp != COMPARE_REC2) {
							// �쥳���ɤ������쥳���ɤ���礭���Τǰ��֤Ť���λ
							found = true;
							ret = FileStatus.OK;
						}
					}
					if (!found) {
						// ���֤Ť����Ǥ��ʤ��ޤ�EOF��ã����
						ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "can't find.");
					}
				}
			} else {
				// �Ԥ�¸�ߤ��ʤ�
				ret = new FileStatus(FileStatus.STATUS_INVALID_KEY, "", 0, "zero records.");
			}
		} catch (SQLException e) {
			// ���餫��SQL�㳰
			return getSQLException2FileStatus(e);
		}
		return ret;
	}
	/**
	 * ���٤ƺ������
	 */
	void truncate() {
		String truncate = meta.getTruncateStatement();
		if (truncate != null) {
			if (truncate.trim().length() > 0) {
				try {
					// �ǡ����١�����Ϣ����
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
