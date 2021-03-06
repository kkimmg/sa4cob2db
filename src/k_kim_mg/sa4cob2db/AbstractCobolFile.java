package k_kim_mg.sa4cob2db;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.event.CobolFileEvent;
import k_kim_mg.sa4cob2db.event.CobolFileEventAdapter;
import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;

/**
 * This class does not generate an event.<br/>
 * Please raise an event on the implementation class
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class AbstractCobolFile implements CobolFile {
	/**
	 * buffer of Sequential file.
	 * 
	 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
	 */
	protected class DefaultSequentialReadBuffer implements Runnable, SequentialReadBuffer {
		/**
		 * Buffer Record
		 */
		private class BufferRecord {
			private FileStatus nextStatus;
			private FileStatus readStatus;
			private byte[] record;

			/**
			 * get Next Status.
			 * 
			 * @return FileStatus
			 */
			public FileStatus getNextStatus() {
				return nextStatus;
			}

			/**
			 * get Read Status.
			 * 
			 * @return FileStatus
			 */
			public FileStatus getReadStatus() {
				return readStatus;
			}

			/**
			 * get Record.
			 * 
			 * @return record
			 */
			public byte[] getRecord() {
				return record;
			}

			/**
			 * set Next Status.
			 * 
			 * @param nextStatus FileStatus
			 */
			public void setNextStatus(FileStatus nextStatus) {
				this.nextStatus = nextStatus;
			}

			/**
			 * set Read Status.
			 * 
			 * @param readStatus FileStatus
			 */
			public void setReadStatus(FileStatus readStatus) {
				this.readStatus = readStatus;
			}

			/**
			 * set Record.
			 * 
			 * @param record record
			 */
			public void setRecord(byte[] record) {
				this.record = record;
			}
		}

		private boolean initCleared = false;
		private boolean cont = true;
		private int initSize;
		private boolean lastRead = false;
		private int maxSize;
		private int minSize;
		private Thread thread;
		private BufferRecord currentBuffer;
		private Queue<BufferRecord> recordQueue;
		private boolean first = true;

		/**
		 * Constructor.<br>
		 * minimum size:2500<br>
		 * initial size:5000<br>
		 * maximum size:10000<br>
		 */
		public DefaultSequentialReadBuffer() {
			this(5000, 2500, 10000);
		}

		/**
		 * Constructor.
		 * 
		 * @param initSize initial size
		 * @param minSize minimum size
		 * @param maxSize maximum size
		 */
		public DefaultSequentialReadBuffer(int initSize, int minSize, int maxSize) {
			this.initSize = initSize;
			this.minSize = minSize;
			this.maxSize = maxSize;
			recordQueue = new LinkedBlockingQueue<BufferRecord>(maxSize);
			addCobolFileEventListener(new CobolFileEventAdapter() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see k_kim_mg.sa4cob2db.event.CobolFileEventAdapter
				 * #postClose (k_kim_mg.sa4cob2db.event.CobolFileEvent)
				 */
				@Override
				public void preClose(CobolFileEvent e) {
					SQLNetServer.logger.warning("stopping!!");
					cont = false;
				}
			});
		}

		/**
		 * wait?.
		 * 
		 * @return true wait<br>
		 *         false no wait
		 */
		boolean isWaitToRead() {
			boolean ret = false;
			if (!lastRead) {
				if (!initCleared && recordQueue.size() < initSize) {
					ret = true;
				}
				if (initCleared && recordQueue.size() < minSize) {
					ret = true;
				}
				if (recordQueue.size() <= 0) {
					ret = true;
				}
			}
			return ret;
		}

		/**
		 * wait?.
		 * 
		 * @return true wait<br>
		 *         false no wait
		 */
		boolean isWaitToWrite() {
			boolean ret = false;
			ret = (recordQueue.size() >= maxSize);
			return ret;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.SequentialReadBuffer#nextBuffer()
		 */
		@Override
		public synchronized FileStatus nextBuffer() {
			//
			while (isWaitToRead()) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					SQLNetServer.logger.log(Level.SEVERE, "Interrupted To Read Wait", e);
				}
			}
			currentBuffer = recordQueue.poll();
			return currentBuffer.getNextStatus();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.SequentialReadBuffer#readBuffer(byte[])
		 */
		@Override
		public synchronized FileStatus readBuffer(byte[] record) {
			byte[] currentRecord = currentBuffer.getRecord();
			System.arraycopy(currentRecord, 0, record, 0, (currentRecord.length > record.length ? record.length : currentRecord.length));
			return currentBuffer.getReadStatus();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			cont = true;
			FileStatus ns;
			FileStatus rs; // NextStatus, ReadStatus
			while (cont) {
				//
				while (isWaitToWrite() && cont) {
					try {
						SQLNetServer.logger.log(Level.FINEST, "Buffering is Waiting");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						SQLNetServer.logger.log(Level.SEVERE, "Interrupted To Write Wait", e);
					}
				}
				if (cont) {
					BufferRecord buffer = new BufferRecord();
					//
					ns = nextOnFile();
					buffer.setNextStatus(ns);
					byte[] record = new byte[getMetaData().getRowSize()];
					if (ns.getStatusCode().equals(FileStatus.STATUS_SUCCESS)) {
						rs = readFromFile(record);
						buffer.setRecord(record);
						buffer.setReadStatus(rs);
						if (!initCleared && recordQueue.size() >= initSize) {
							initCleared = true;
						}
					} else if (ns.getStatusCode().equals(FileStatus.STATUS_END_OF_FILE)) {
						lastRead = true;
						cont = false;
						buffer.setRecord(record);
						buffer.setReadStatus(ns);
						SQLNetServer.logger.log(Level.FINEST, "Buffering ends");
					} else {
						//
						SQLNetServer.logger.log(Level.SEVERE, ns.getStatusMessage());
						cont = false;
						buffer.setRecord(record);
						buffer.setReadStatus(ns);
					}
					boolean offerStatus = recordQueue.offer(buffer);
					while (!offerStatus && cont) {
						try {
							SQLNetServer.logger.log(Level.FINEST, "Buffering is Waiting");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							SQLNetServer.logger.log(Level.SEVERE, "Interrupted To Write Wait", e);
						}
						offerStatus = recordQueue.offer(buffer);
					}
					if (first) {
						currentBuffer = buffer;
						first = false;
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.SequentialReadBuffer#startBuffering()
		 */
		@Override
		public void startBuffering() {
			if (thread != null) {
				return;
			}
			thread = new Thread(this);
			thread.start();
		}

	}

	/**
	 * listen COBOL file event.
	 * 
	 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
	 */
	protected class InnerCobolFileEventAdapter implements CobolFileEventListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postClose
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postClose(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postClose(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postDelete
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postDelete(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postDelete(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postMove
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postMove(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postMove(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postNext
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postNext(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postNext(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postOpen
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postOpen(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postOpen(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postPrevous
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postPrevious(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postPrevious(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postRead
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, byte[])
		 */
		public void postRead(CobolFileEvent e, byte[] record) {
			for (CobolFileEventListener listener : listeners) {
				listener.postRead(e, record);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postRewrite
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postRewrite(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postRewrite(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postStart
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postStart(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postStart(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postStart
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, java.lang.String)
		 */
		public void postStart(CobolFileEvent e, String indexname) {
			for (CobolFileEventListener listener : listeners) {
				listener.postStart(e, indexname);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#postWrite
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void postWrite(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.postWrite(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preClose
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void preClose(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.preClose(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preDelete
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void preDelete(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.preDelete(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preMove
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, byte[])
		 */
		public void preMove(CobolFileEvent e, byte[] record) {
			for (CobolFileEventListener listener : listeners) {
				listener.preMove(e, record);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preNext
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void preNext(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.preNext(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preOpen
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void preOpen(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.preOpen(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#prePrevious
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void prePrevious(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.prePrevious(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preRead
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent)
		 */
		public void preRead(CobolFileEvent e) {
			for (CobolFileEventListener listener : listeners) {
				listener.preRead(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preRewrite
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, byte[])
		 */
		public void preRewrite(CobolFileEvent e, byte[] record) {
			for (CobolFileEventListener listener : listeners) {
				listener.preRewrite(e, record);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preStart
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, byte[])
		 */
		public void preStart(CobolFileEvent e, byte[] record) {
			for (CobolFileEventListener listener : listeners) {
				listener.preStart(e, record);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preStart
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, java.lang.String, byte[])
		 */
		public void preStart(CobolFileEvent e, String indexname, byte[] record) {
			for (CobolFileEventListener listener : listeners) {
				listener.preStart(e, indexname, record);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preWrite
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, byte[])
		 */
		public void preWrite(CobolFileEvent e, byte[] record) {
			for (CobolFileEventListener listener : listeners) {
				listener.preWrite(e, record);
			}
		}
	}

	/** Two record keys are equal. */
	protected static final int COMPARE_EQUAL = 0;

	/** Record 1 is small. */
	protected static final int COMPARE_REC1 = 1;

	/** Record 2 is small. */
	protected static final int COMPARE_REC2 = 2;

	private static final long serialVersionUID = 1L;

	/** EOF. */
	protected static final FileStatus STATUS_EOF = new FileStatus(FileStatus.STATUS_END_OF_FILE, FileStatus.NULL_CODE, 0, "end of file.");

	/** Something failure. */
	protected static final FileStatus STATUS_UNKNOWN_ERROR = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "Unknown Error.");

	/** Accessmode. */
	protected int accessMode;

	/** index current in use. */
	protected CobolIndex currentIndex = null;

	/** event listener. */
	private CobolFileEventListener eventer = new InnerCobolFileEventAdapter();

	/** map of index and file. */
	protected Map<CobolIndex, CobolFile> index2File;

	/** map of file and index. */
	protected Map<String, CobolIndex> indexName2Index;

	private int initialSequentialReadBufferSize = 0;

	private int maximumSequentialReadBufferSize = 0;

	private int minimumSequentialReadBufferSize = 0;

	private ArrayList<CobolFileEventListener> listeners = new ArrayList<CobolFileEventListener>();

	/** open mode. */
	protected int openmode;

	/** Internal buffer. */
	protected SequentialReadBuffer sequentialReadBuffer = null;

	private ACMSession session;

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#addCobolFileEventListener(jp
	 * .ne.biglobe.mvh.k_kim_mg.acm.event.CobolFileEventListener)
	 */
	public void addCobolFileEventListener(CobolFileEventListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#addIndex(k_kim_mg.CobolIndex,
	 * k_kim_mg.sa4cob2db.CobolFile)
	 */
	@Override
	public void addIndex(CobolIndex index, CobolFile file) {
		if (index2File == null) {
			index2File = new Hashtable<CobolIndex, CobolFile>();
		}
		index2File.put(index, file);
		if (indexName2Index == null) {
			indexName2Index = new Hashtable<String, CobolIndex>();
		}
		indexName2Index.put(index.getIndexKeyName(), index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.CobolFile#bindSession(k_kim_mg.sa4cob2db.ACMSession)
	 */
	public void bindSession(ACMSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#close()
	 */
	@Override
	public final FileStatus close() {
		getEventProcessor().preClose(getReadyEvent());
		FileStatus ret = closeMain();
		getEventProcessor().postClose(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * Main of close process.
	 * 
	 * @return FileStatus.
	 */
	protected abstract FileStatus closeMain();

	/**
	 * close index.
	 * 
	 * @return status status
	 */
	protected FileStatus closeIndexes() {
		FileStatus ret = FileStatus.OK;
		if (index2File != null) {
			for (CobolFile idfile : index2File.values()) {
				FileStatus stat = idfile.close();
				if (!stat.getStatusCode().equals(FileStatus.STATUS_SUCCESS)) {
					ret = stat;
				}
			}
		}
		return ret;
	}

	/**
	 * compare records.
	 * 
	 * @param record1 record
	 * @param record2 record
	 * @return STATUS_EQUAL record1 == record2<br/>
	 *         STATUS_REC1 record1 < record2<br/>
	 *         STATUS_REC2 record1 > record2
	 * @throws CobolRecordException CobolRecordException
	 */
	protected int compare(byte[] record1, byte[] record2) throws CobolRecordException {
		CobolRecordMetaData meta = getMetaData();
		return compare(record1, record2, meta.isKeyByValue());
	}

	/**
	 * compare records.
	 * 
	 * @param record1 record
	 * @param record2 record
	 * @param byValue copare by value
	 * @return STATUS_EQUAL record1 == record2<br/>
	 *         STATUS_REC1 record1 < record2<br/>
	 *         STATUS_REC2 record1 > record2
	 * @throws CobolRecordException CobolRecordException
	 */
	protected int compare(byte[] record1, byte[] record2, boolean byValue) throws CobolRecordException {
		if (byValue) {
			return compare_byValue(record1, record2);
		}
		return compare_byBytes(record1, record2);
	}

	/**
	 * compare records by bytes.
	 * 
	 * @param record1 record
	 * @param record2 record
	 * @return STATUS_EQUAL record1 == record2<br/>
	 *         STATUS_REC1 record1 < record2<br/>
	 *         STATUS_REC2 record1 > record2
	 * @throws CobolRecordException CobolRecordException
	 */
	protected int compare_byBytes(byte[] record1, byte[] record2) throws CobolRecordException {
		int ret = COMPARE_EQUAL;
		CobolRecordMetaData meta = getMetaData();
		// record1
		DefaultCobolRecord crecord1 = new DefaultCobolRecord(meta);
		crecord1.setRecord(record1);
		// record2
		DefaultCobolRecord crecord2 = new DefaultCobolRecord(meta);
		crecord2.setRecord(record2);
		int count = meta.getKeyCount();
		int i = 0;
		while (i < count && ret == COMPARE_EQUAL) {
			CobolColumn key = meta.getKey(i);
			byte[] key1 = crecord1.getBytes(key);
			byte[] key2 = crecord2.getBytes(key);
			for (int j = 0; j < key1.length; j++) {
				if (key1[j] < key2[j]) {
					return COMPARE_REC1;
				} else if (key1[j] > key2[j]) {
					return COMPARE_REC2;
				}
			}
			i++;
		}
		return ret;
	}

	/**
	 * compare records by value.
	 * 
	 * @param record1 record
	 * @param record2 record
	 * @return STATUS_EQUAL record1 == record2<br/>
	 *         STATUS_REC1 record1 < record2<br/>
	 *         STATUS_REC2 record1 > record2
	 * @throws CobolRecordException CobolRecordException
	 */
	protected int compare_byValue(byte[] record1, byte[] record2) throws CobolRecordException {
		int ret = COMPARE_EQUAL;
		CobolRecordMetaData meta = getMetaData();
		// record1
		DefaultCobolRecord crecord1 = new DefaultCobolRecord(meta);
		crecord1.setRecord(record1);
		// record2
		DefaultCobolRecord crecord2 = new DefaultCobolRecord(meta);
		crecord2.setRecord(record2);
		int count = meta.getKeyCount();
		int i = 0;
		while (i < count && ret == COMPARE_EQUAL) {
			CobolColumn key = meta.getKey(i);
			switch (key.getType()) {
			case CobolColumn.TYPE_DATE:
			case CobolColumn.TYPE_TIME:
			case CobolColumn.TYPE_TIMESTAMP:
				Date date1 = crecord1.getDate(key);
				Date date2 = crecord2.getDate(key);
				if (date1.after(date2)) {
					return COMPARE_REC2;
				} else if (date1.before(date2)) {
					return COMPARE_REC1;
				}
				break;
			case CobolColumn.TYPE_DOUBLE:
			case CobolColumn.TYPE_FLOAT:
				double double1 = crecord1.getDouble(key);
				double double2 = crecord2.getDouble(key);
				if (double1 > double2) {
					return COMPARE_REC2;
				} else if (double1 < double2) {
					return COMPARE_REC1;
				}
				break;
			case CobolColumn.TYPE_INTEGER:
			case CobolColumn.TYPE_LONG:
				long long1 = crecord1.getLong(key);
				long long2 = crecord2.getLong(key);
				if (long1 > long2) {
					return COMPARE_REC2;
				} else if (long1 < long2) {
					return COMPARE_REC1;
				}
				break;
			case CobolColumn.TYPE_XCHAR:
			case CobolColumn.TYPE_NCHAR:
				String string1 = crecord1.getString(key);
				String string2 = crecord2.getString(key);
				int work = string1.compareTo(string2);
				if (work > 0) {
					return COMPARE_REC2;
				} else if (work < 0) {
					return COMPARE_REC1;
				}
				break;
			case CobolColumn.TYPE_STRUCT:
			default:
				return this.compare(record1, record2, false);
			}
			i++;
		}
		return ret;
	}

	/**
	 * create internal buffer.
	 * 
	 * @return default internal buffer
	 */
	protected SequentialReadBuffer createSequentialReadBuffer() {
		return new DefaultSequentialReadBuffer(getInitialSequentialReadBufferSize(), getMinimumSequentialReadBufferSize(), getMaximumSequentialReadBufferSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#delete(byte[])
	 */
	@Override
	public final FileStatus delete(byte[] record) {
		getEventProcessor().preDelete(getReadyEvent());
		FileStatus ret = deleteMain(record);
		getEventProcessor().postDelete(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * Main of delete process
	 * 
	 * @param record byte array
	 * @return FileStatus.
	 */
	protected abstract FileStatus deleteMain(byte[] record);

	/**
	 * get ACCESS MODE.
	 * 
	 * @return ACCESS MODE
	 */
	public int getAccessMode() {
		return accessMode;
	}

	/**
	 * get using index.
	 * 
	 * @return using index if not in return null
	 */
	public CobolIndex getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * get event listener.
	 * 
	 * @return listener object
	 */
	protected CobolFileEventListener getEventProcessor() {
		return eventer;
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

	/**
	 * get index by name.
	 * 
	 * @param name index name
	 * @return index if not found return null
	 */
	protected CobolIndex getIndex(String name) {
		if (indexName2Index == null) {
			return null;
		}
		if (!indexName2Index.containsKey(name)) {
			return null;
		}
		return indexName2Index.get(name);
	}

	/**
	 * get index file from index object.
	 * 
	 * @param index index
	 * @return index file if not found null
	 */
	protected CobolFile getIndexFile(CobolIndex index) {
		if (index == null) {
			return null;
		}
		if (index2File == null) {
			return null;
		}
		if (!index2File.containsKey(index)) {
			return null;
		}
		return index2File.get(index);
	}

	/**
	 * get initial buffer size.
	 * 
	 * @return initial buffer size
	 */
	protected int getInitialSequentialReadBufferSize() {
		return initialSequentialReadBufferSize;
	}

	/**
	 * maximumbuffersize.
	 * 
	 * @return maximumbuffersize
	 */
	protected int getMaximumSequentialReadBufferSize() {
		return maximumSequentialReadBufferSize;
	}

	/**
	 * minimumbuffersize.
	 * 
	 * @return minimumbuffersize
	 */
	protected int getMinimumSequentialReadBufferSize() {
		return minimumSequentialReadBufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#getOpenMode()
	 */
	public int getOpenMode() {
		return openmode;
	}

	/**
	 * create event object
	 * 
	 * @return fileevent(READY)
	 */
	protected CobolFileEvent getReadyEvent() {
		return new CobolFileEvent(this, FileStatus.READY);
	}

	/**
	 * get sequential read buffer.
	 * 
	 * @return buffer
	 */
	protected SequentialReadBuffer getSequentialReadBuffer() {
		return sequentialReadBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.CobolFile#getSession(k_kim_mg.sa4cob2db.ACMSession)
	 */
	public ACMSession getSession() {
		return session;
	}

	/**
	 * last record has read ?.
	 * 
	 * @return true/false
	 */
	public abstract boolean isLastMoved();

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#move(byte[])
	 */
	@Override
	public final FileStatus move(byte[] record) {
		resetCurrentIndex();
		getEventProcessor().preMove(getReadyEvent(), record);
		FileStatus ret = moveMain(record);
		getEventProcessor().postMove(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * move to row'th record.
	 * 
	 * @param row row'th
	 * @return status
	 */
	public abstract FileStatus move(int row);

	/**
	 * Main of move process.
	 * 
	 * @param record byte array
	 * @return FileStatus.
	 */
	protected abstract FileStatus moveMain(byte[] record);

	/**
	 * move to first record.
	 * 
	 * @return status
	 */
	public abstract FileStatus moveFirst();

	/**
	 * move to last record.
	 * 
	 * @return status
	 */
	public abstract FileStatus moveLast();

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#next()
	 */
	@Override
	public final FileStatus next() {
		getEventProcessor().preNext(getReadyEvent());
		if (getAccessMode() == CobolFile.ACCESS_DYNAMIC && currentIndex != null) {
			return nextOnIndex();
		}
		if (getMaximumSequentialReadBufferSize() > 0 && sequentialReadBuffer != null && getAccessMode() == CobolFile.ACCESS_SEQUENTIAL && getOpenMode() == CobolFile.MODE_INPUT) {
			return nextOnBuffer();
		}
		FileStatus ret = nextOnFile();
		getEventProcessor().postNext(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * move to record.
	 * 
	 * @param row distance
	 * @return status
	 */
	public abstract FileStatus next(int row);

	/**
	 * next record on buffer.
	 * 
	 * @return status
	 */
	public FileStatus nextOnBuffer() {
		FileStatus ret = null;
		if (sequentialReadBuffer == null) {
			ret = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "buffer is null.");
		} else {
			ret = sequentialReadBuffer.nextBuffer();
		}
		return ret;
	}

	/**
	 * next record on file.
	 * 
	 * @return status
	 */
	protected abstract FileStatus nextOnFile();

	/**
	 * next record on index.
	 * 
	 * @return status
	 */
	protected FileStatus nextOnIndex() {
		if (currentIndex == null) {
			return new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "not started");
		}
		// index record
		CobolFile currentIndexFile = getIndexFile(currentIndex);
		CobolRecordMetaData indexmeta = currentIndexFile.getMetaData();
		DefaultCobolRecord indexrecord = new DefaultCobolRecord(indexmeta);
		byte[] indexbytes = new byte[indexmeta.getRowSize()];
		// filerecord
		CobolRecordMetaData meta = getMetaData();
		DefaultCobolRecord mainrecord = new DefaultCobolRecord(meta);
		FileStatus ret;
		ret = currentIndexFile.next();
		if (ret.getStatusCode() == FileStatus.STATUS_SUCCESS) {
			// read index
			ret = currentIndexFile.read(indexbytes);
			if (ret.getStatusCode() == FileStatus.STATUS_SUCCESS) {
				try {
					indexrecord.setRecord(indexbytes);
					// index -> file
					Map<CobolColumn, CobolColumn> map1 = currentIndex.getFileKey2IndexColumn();
					Set<Map.Entry<CobolColumn, CobolColumn>> set = map1.entrySet();
					Iterator<Map.Entry<CobolColumn, CobolColumn>> ite = set.iterator();
					while (ite.hasNext()) {
						Map.Entry<CobolColumn, CobolColumn> ent = ite.next();
						CobolColumn indexColumn = ent.getValue();
						CobolColumn fileColumn = ent.getKey();
						mainrecord.updateBytes(fileColumn, indexrecord.getBytes(indexColumn));
					}
					// file
					byte[] record = new byte[meta.getRowSize()];
					/* len = */mainrecord.getRecord(record);
					ret = move(record);
				} catch (CobolRecordException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
					ret = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
				}
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#open(int, int)
	 */
	@Override
	public final FileStatus open(int mode, int accessmode) {
		this.openmode = mode;
		this.accessMode = accessmode;
		getEventProcessor().preOpen(getReadyEvent());
		FileStatus ret = openMain(mode, accessmode);
		getEventProcessor().postOpen(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * Main of open process.
	 * 
	 * @param mode Open mode.
	 * @param accessmode Acess Mode.
	 * @return FileStatus
	 */
	protected abstract FileStatus openMain(int mode, int accessmode);

	/**
	 * open indexes.
	 * 
	 * @return status
	 */
	protected FileStatus openIndexes() {
		FileStatus ret = FileStatus.OK;
		if (index2File != null) {
			for (CobolFile idfile : index2File.values()) {
				FileStatus stat = idfile.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
				if (!stat.getStatusCode().equals(FileStatus.STATUS_SUCCESS)) {
					ret = stat;
				}
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#previous()
	 */
	@Override
	public final FileStatus previous() {
		getEventProcessor().prePrevious(getReadyEvent());
		FileStatus ret = previousMain();
		getEventProcessor().postPrevious(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * move to previous record.
	 * 
	 * @param row move count
	 * @return status
	 */
	public abstract FileStatus previous(int row);

	/**
	 * Main of previous process
	 * 
	 * @return FileStatus.
	 */
	protected abstract FileStatus previousMain();

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#read(byte[])
	 */
	@Override
	public FileStatus read(byte[] record) {

		FileStatus ret = FileStatus.FAILURE;
		getEventProcessor().preRead(getReadyEvent());
		if (getMaximumSequentialReadBufferSize() > 0 && sequentialReadBuffer != null && getAccessMode() == CobolFile.ACCESS_SEQUENTIAL && getOpenMode() == CobolFile.MODE_INPUT) {
			ret = readFromBuffer(record);
		} else {
			ret = readFromFile(record);
		}
		getEventProcessor().postRead(getFileStatus2Event(ret), record);
		return ret;
	}

	/**
	 * read record from buffer.
	 * 
	 * @param record record
	 * @return status
	 */
	public FileStatus readFromBuffer(byte[] record) {
		FileStatus ret = null;
		if (sequentialReadBuffer == null) {
			ret = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, "buffer is null.");
		} else {
			ret = sequentialReadBuffer.readBuffer(record);
		}
		return ret;
	}

	/**
	 * read from file.
	 * 
	 * @param record record
	 * @return status
	 */
	protected abstract FileStatus readFromFile(byte[] record);

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#removeCobolFileEventListener
	 * (k_kim_mg.sa4cob2db.event.CobolFileEventListener)
	 */
	public void removeCobolFileEventListener(CobolFileEventListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc) Actually do nothing.
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#reopen()
	 */
	@Override
	public FileStatus reopen() {
		FileStatus ret = FileStatus.FAILURE;
		if (isOpened()) {
			ret = new FileStatus(FileStatus.STATUS_NOT_OPEN, "File is not opened.", 0, "File is not opened.");
		} else {
			ret = close();
			if (ret.getStatusCode().equals(FileStatus.STATUS_SUCCESS)) {
				open(openmode, accessMode);
			}
		}
		return ret;
	}

	/**
	 * clear using index.<br>
	 * same as setCurrentIndex(null)<br>
	 */
	public void resetCurrentIndex() {
		setCurrentIndex(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#rewrite(byte[])
	 */
	@Override
	public final FileStatus rewrite(byte[] record) {
		getEventProcessor().preRewrite(getReadyEvent(), record);
		FileStatus ret = rewriteMain(record);
		getEventProcessor().postRewrite(getFileStatus2Event(ret));
		return ret;
	}

	/**
	 * Main of rewrite process.
	 * 
	 * @param record byte array.
	 * @return FileStutus
	 */
	protected abstract FileStatus rewriteMain(byte[] record);

	/**
	 * set using index.
	 * 
	 * @param currentIndex index
	 */
	public void setCurrentIndex(CobolIndex currentIndex) {
		this.currentIndex = currentIndex;
	}

	/**
	 * set initial buffer size.
	 * 
	 * @param initialSequentialReadBufferSize initial buffer size
	 */
	protected void setInitialSequentialReadBufferSize(int initialSequentialReadBufferSize) {
		this.initialSequentialReadBufferSize = initialSequentialReadBufferSize;
	}

	/**
	 * set maximum buffer size.
	 * 
	 * @param maximumSequentialReadBufferSize maximum buffer size
	 */
	protected void setMaximumSequentialReadBufferSize(int maximumSequentialReadBufferSize) {
		this.maximumSequentialReadBufferSize = maximumSequentialReadBufferSize;
	}

	/**
	 * set minimum buffer size.
	 * 
	 * @param minimumSequentialReadBufferSize minimum buffer size
	 */
	protected void setMinimumSequentialReadBufferSize(int minimumSequentialReadBufferSize) {
		this.minimumSequentialReadBufferSize = minimumSequentialReadBufferSize;
	}

	/**
	 * set buffer.
	 * 
	 * @param SequentialReadBuffer buffer
	 */
	protected void setSequentialReadBuffer(SequentialReadBuffer SequentialReadBuffer) {
		this.sequentialReadBuffer = SequentialReadBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(int, byte[])
	 */
	@Override
	public final FileStatus start(int mode, byte[] record) {
		resetCurrentIndex();
		getEventProcessor().preStart(getReadyEvent(), record);
		FileStatus ret = startMain(mode, record);
		getEventProcessor().postStart(getReadyEvent());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(int, byte[], boolean)
	 */
	public FileStatus start(int mode, byte[] record, boolean duplicates) {
		return (duplicates ? startDuplicates(mode, record) : start(mode, record));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(java.lang.String, int, byte[],
	 * boolean)
	 */
	@Override
	public FileStatus start(String IndexName, int mode, byte[] record) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		CobolIndex index = getIndex(IndexName);
		CobolFile indexFile = getIndexFile(index);
		if (index != null && indexFile != null) {
			CobolRecordMetaData meta = getMetaData();
			DefaultCobolRecord mainrecord = new DefaultCobolRecord(meta);
			mainrecord.setRecord(record);
			try {
				CobolRecordMetaData indexmeta = indexFile.getMetaData();
				DefaultCobolRecord indexrecord = new DefaultCobolRecord(indexmeta);
				byte[] indexbytes = new byte[indexmeta.getRowSize()];
				Map<CobolColumn, CobolColumn> map0 = index.getIndexKey2FileColumn();
				Set<Map.Entry<CobolColumn, CobolColumn>> set0 = map0.entrySet();
				Iterator<Map.Entry<CobolColumn, CobolColumn>> ite0 = set0.iterator();
				while (ite0.hasNext()) {
					Map.Entry<CobolColumn, CobolColumn> ent0 = ite0.next();
					CobolColumn indexColumn = ent0.getKey();
					CobolColumn fileColumn = ent0.getValue();
					indexrecord.updateBytes(indexColumn, mainrecord.getBytes(fileColumn));
				}
				indexrecord.getRecord(indexbytes);
				ret = indexFile.start(mode, indexbytes, index.isDuplicates());
				if (ret.getStatusCode() == FileStatus.STATUS_SUCCESS) {
					ret = indexFile.read(indexbytes);
					indexrecord.setRecord(indexbytes);
					Map<CobolColumn, CobolColumn> map1 = index.getFileKey2IndexColumn();
					Set<Map.Entry<CobolColumn, CobolColumn>> set1 = map1.entrySet();
					Iterator<Map.Entry<CobolColumn, CobolColumn>> ite1 = set1.iterator();
					while (ite1.hasNext()) {
						Map.Entry<CobolColumn, CobolColumn> ent1 = ite1.next();
						CobolColumn indexColumn = ent1.getValue();
						CobolColumn fileColumn = ent1.getKey();
						mainrecord.updateBytes(fileColumn, indexrecord.getBytes(indexColumn));
					}
					mainrecord.getRecord(record);
					ret = move(record);
					if (ret.getStatusCode() == FileStatus.STATUS_SUCCESS) {
						currentIndex = index;
					}
				}
			} catch (CobolRecordException e) {
				SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
				ret = new FileStatus(FileStatus.STATUS_99_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
			} catch (Exception ex) {
				SQLNetServer.logger.log(Level.SEVERE, "Exception", ex);
				ret = new FileStatus(FileStatus.STATUS_99_FAILURE, FileStatus.NULL_CODE, 0, ex.getMessage());
			}
		} else {
			resetCurrentIndex();
			ret = start(mode, record);
		}
		return ret;
	}

	/**
	 * Main of start process.
	 * 
	 * @param mode Start Mode
	 * @param record byte array
	 * @return FileStatus.
	 */
	protected abstract FileStatus startMain(int mode, byte[] record);

	/**
	 * starts buffering.
	 */
	public void startBuffer() {
		if ((getMaximumSequentialReadBufferSize() > 0 && getAccessMode() == CobolFile.ACCESS_SEQUENTIAL && getOpenMode() == CobolFile.MODE_INPUT)) {
			if (sequentialReadBuffer == null) {
				sequentialReadBuffer = createSequentialReadBuffer();
			}
			getSequentialReadBuffer().startBuffering();
		}
	}

	/**
	 * locate or START.
	 * 
	 * @param mode MODE (EQ or GT etc)
	 * @param record record includes key value
	 * @return status
	 */
	public abstract FileStatus startDuplicates(int mode, byte[] record);

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.CobolFile#write(byte[])
	 */
	@Override
	public final FileStatus write(byte[] record) {
		getEventProcessor().preWrite(getReadyEvent(), record);
		FileStatus ret = writeMain(record);
		getEventProcessor().postWrite(getFileStatus2Event(ret));
		return null;
	}

	/**
	 * Main of write process.
	 * 
	 * @param record byte array.
	 * @return FileStatus.
	 */
	protected abstract FileStatus writeMain(byte[] record);
}