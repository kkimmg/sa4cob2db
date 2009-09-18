package k_kim_mg.sa4cob2db;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import k_kim_mg.sa4cob2db.event.CobolFileEvent;
import k_kim_mg.sa4cob2db.event.CobolFileEventAdapter;
import k_kim_mg.sa4cob2db.event.CobolFileEventListener;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * ���ܥ�ե�����δ��ܷ�<br>
 * ���Υ��饹�Ǥϥ��٥�Ȥ�ȯ�������ʤ��ΤǼ������饹¦�ǥ��٥�Ȥ�ȯ�������Ƥ��硣
 * @author ���줪��
 */
public abstract class AbstractCobolFile implements CobolFile {
	/**
	 * �������󥷥��ե�����ΥХåե�
	 * @author ���줪��
	 */
	protected class DefaultSequencialReadBuffer implements Runnable, SequencialReadBuffer {
		/** �Хåե��λ�����ե饰 */
		private volatile boolean[][] buffStatus;
		/** �ɤ߹��ߤ��³���뤫�ɤ��� */
		private volatile boolean cont = true;
		/** ���ߤΥ����� (CurrentSize) */
		private volatile int cs;
		/** ���󥵥��� */
		private int dimentionSize;
		/** �����������(��ö)��ã���� */
		private volatile boolean initCleared = false;
		/**
		 * ��������� <br>
		 * 1.̤������ξ��֤Ǥ��ο��ͤ򲼲�ä������Ե�����<br>
		 * 2.��öminSize�򲼲�ä����ȡ����ο��ͤ򲼲�ä������Ե�����
		 */
		private int initSize;
		/** EOF���� */
		private volatile boolean lastRead = false;
		/** ���祵���� */
		private int maxSize;
		/**
		 * �Ǿ������� ���Υ������򲼲�ä���initSize�ޤ��ɤ߹��ߤ��Ե�����
		 */
		private int minSize;
		/** Next�����Υե����륹�ơ����� */
		private volatile FileStatus[][] nextStatus;
		/** �����ɤ߹�����ΥХåե�����(ReadingBuffer) */
		private volatile int rb;
		/** Read�����Υե����륹�ơ����� */
		private volatile FileStatus[][] readStatus;
		/** �쥳���ɤΥХåե� */
		private volatile byte[][][] records;
		/** �ɤ߹�����ΰ���(ReadingIndex) */
		private volatile int ri;
		/** ��������å� */
		private Thread thread;
		/** ���߽񤭹�����ΥХåե�����(WritingBuffer) */
		private volatile int wb;
		/** ��������ΰ���(WritingIndex) */
		private volatile int wi;
		/**
		 * ���󥹥ȥ饯��<br>
		 * �Ǿ�������:2500<br>
		 * ���������:5000<br>
		 * ���祵����:10000<br>
		 * �Ǻ�������
		 */
		public DefaultSequencialReadBuffer() {
			this(5000, 2500, 10000);
		}
		/**
		 * ���󥹥ȥ饯��
		 * @param initSize ���������
		 * @param minSize �Ǿ�������
		 * @param maxSize ���祵����
		 */
		public DefaultSequencialReadBuffer(int initSize, int minSize, int maxSize) {
			this.initSize = initSize;
			this.minSize = minSize;
			this.maxSize = maxSize;
			cs = 0;
			// �Хåե��������η���
			if (maxSize % 2 != 0) {
				dimentionSize = (maxSize + 1) / 2;
			} else {
				dimentionSize = maxSize / 2;
			}
			addCobolFileEventListener(new CobolFileEventAdapter() {
				/*
				 * (non-Javadoc)
				 * @see k_kim_mg.sa4cob2db.event.CobolFileEventAdapter
				 * #postClose (k_kim_mg.sa4cob2db.event.CobolFileEvent)
				 */
				@Override
				public void postClose(CobolFileEvent e) {
					SQLNetServer.logger.warning("stopping!!");
					cont = false;
				}
			});
			// �Хåե��ν����
			initBuffers();
		}
		/**
		 * �Хåե��ν����
		 * @param size �Хåե�������(���祵������Ⱦʬ)
		 */
		void initBuffers() {
			records = new byte[2][dimentionSize][];
			nextStatus = new FileStatus[2][dimentionSize];
			readStatus = new FileStatus[2][dimentionSize];
			buffStatus = new boolean[2][dimentionSize];
			// �쥳���ɥ�����
			int recSize = getMetaData().getRowSize();
			// �Хåե��ν��������
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < dimentionSize; j++) {
					buffStatus[i][j] = false;
					records[i][j] = new byte[recSize];
					nextStatus[i][j] = FileStatus.FAILURE;
					readStatus[i][j] = FileStatus.FAILURE;
				}
			}
			wb = 0;
			wi = 0;
			rb = 0;
			ri = -1;
			// �ޤ��ǽ��쥳���ɤޤǤ��äƤʤ�
			lastRead = false;
		}
		/**
		 * �Хåե��ɤ߹��ߤ��Ե�����٤����ɤ����� <br>
		 * 1.�������������ã���Ƥ��ʤ� <br>
		 * 2.�ɤ߹����оݥХåե����ޤ�ͭ���ǤϤʤ�<br>
		 * 3.�Хåե��Υ�������0��꾮����
		 * @return true �Ե�����<br>
		 *         false �Ե����ʤ�
		 */
		boolean isWaitToRead() {
			boolean ret = false;
			if (!lastRead) {
				// EOF�Ǥʤ�����
				if (!initCleared && cs < initSize) {
					// �������������ã���Ƥ��ʤ�
					ret = true;
				}
				if (!buffStatus[rb][ri]) {
					// �ɤ߹����оݥХåե����ޤ�ͭ���ǤϤʤ�
					ret = true;
				}
				if (cs < 0) {
					// �Хåե���������0��꾮����
					ret = true;
				}
			}
			return ret;
		}
		/**
		 * �Хåե��������Ե�����٤����ɤ�����<br>
		 * 1.�Хåե������祵������ã���Ƥ��� <br>
		 * 2.�񤭹�����ΥХåե���̤���ѤǤ���
		 * @return true �Ե�����<br>
		 *         false �Ե����ʤ�
		 */
		boolean isWaitToWrite() {
			// �Хåե������祵������ã���Ƥ��뤫
			// �񤭹�����ΥХåե���̤���ѤǤ�����Ե�����
			boolean ret = false;
			ret = (cs >= maxSize);
			ret = (ret || buffStatus[wb][wi]);
			return ret;
		}
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.SequencialReadBuffer#nextBuffer()
		 */
		public synchronized FileStatus nextBuffer() {
			// �ɤ߹��߰��֤��ѹ�
			ri++;
			if (ri >= dimentionSize) {
				rb = (rb == 0 ? 1 : 0);
				ri = 0;
			}
			// �ɤ߹����Ե�
			while (isWaitToRead()) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					SQLNetServer.logger.log(Level.SEVERE, "Interrupted To Read Wait", e);
				}
			}
			// �ե����륹�ơ��������֤�
			FileStatus ret = nextStatus[rb][ri];
			if (ret != null) {
				if (!ret.getStatusCode().equals(FileStatus.STATUS_OK)) {
					if (lastRead) {
						nextStatus[rb][ri] = STATUS_EOF;
					} else {
						buffStatus[rb][ri] = false;
					}
				}
			}
			return ret;
		}
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.SequencialReadBuffer#readBuffer(byte [])
		 */
		public synchronized FileStatus readBuffer(byte[] record) {
			cs--;
			if (cs <= minSize && initCleared && !lastRead) {
				initCleared = false;
			}
			System.arraycopy(records[rb][ri], 0, record, 0, (records[rb][ri].length > record.length ? record.length : records[rb][ri].length));
			FileStatus ret = readStatus[rb][ri];
			if (lastRead) {
				readStatus[rb][ri] = STATUS_EOF;
			}
			buffStatus[rb][ri] = false;
			return ret;
		}
		/**
		 * �¹�
		 */
		public void run() {
			cont = true;
			FileStatus ns, rs; // NextStatus, ReadStatus
			while (cont) {
				// �ɤ߹����Ԥ�
				while (isWaitToWrite() && cont) {
					try {
						SQLNetServer.logger.log(Level.FINEST, "Buffering is Waiting");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						SQLNetServer.logger.log(Level.SEVERE, "Interrupted To Write Wait", e);
					}
				}
				if (cont) {
					// �ɤ߹��߼¹�
					ns = nextOnFile();
					nextStatus[wb][wi] = ns;
					if (ns.getStatusCode().equals(FileStatus.STATUS_OK)) {
						rs = readFromFile(records[wb][wi]);
						readStatus[wb][wi] = rs;
						buffStatus[wb][wi] = true;
						// ����
						cs++;
						wi++;
						if (wi >= dimentionSize) {
							// SQLNetServer.logger.log(Level.FINEST, "changing
							// writing buffer:" + wi + " >= " + dimentionSize +
							// " : " + wb);
							wb = (wb == 0 ? 1 : 0);
							wi = 0;
						}
						if (!initCleared && cs >= initSize) {
							initCleared = true;
						}
					} else if (ns.getStatusCode().equals(FileStatus.STATUS_EOF)) {
						// ����� ���� �ե�����
						// SQLNetServer.logger.log(Level.FINEST, "buffering
						// end." + thread.getName());
						lastRead = true;
						cont = false;
						readStatus[wb][wi] = ns;
						buffStatus[wb][wi] = true;
					} else {
						// ���餫�Υ��顼
						SQLNetServer.logger.log(Level.SEVERE, ns.getStatusMessage());
						cont = false;
						readStatus[wb][wi] = ns;
						buffStatus[wb][wi] = true;
					}
				}
			}
		}
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.SequencialReadBuffer#startBuffering()
		 */
		public void startBuffering() {
			if (thread != null)
				return;
			thread = new Thread(this);
			thread.start();
		}
	}
	/**
	 * ���٥�Ȥ�¹Ԥ��뤿��Υ��饹
	 * @author ���줪��
	 */
	protected class InnerCobolFileEventAdapter implements CobolFileEventListener {
		/*
		 * (non-Javadoc)
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
		 * @see k_kim_mg.sa4cob2db.event.CobolFileEventListener#preWrite
		 * (k_kim_mg.sa4cob2db.event.CobolFileEvent, byte[])
		 */
		public void preWrite(CobolFileEvent e, byte[] record) {
			for (CobolFileEventListener listener : listeners) {
				listener.preWrite(e, record);
			}
		}
	}
	/** ���ĤΥ쥳���ɡʥ����ˤ������� */
	protected static final int COMPARE_EQUAL = 0;
	/** �쥳���ɣ��������� */
	protected static final int COMPARE_REC1 = 1;
	/** �쥳���ɣ��������� */
	protected static final int COMPARE_REC2 = 2;
	private static final long serialVersionUID = 1L;
	/** ����ɥ��֥ե����� */
	protected static final FileStatus STATUS_EOF = new FileStatus(FileStatus.STATUS_EOF, FileStatus.NULL_CODE, 0, "end of file.");
	/** ���������Υǥե���Ȥμ��ԥ��ơ����� */
	protected static final FileStatus STATUS_UNKNOWN_ERROR = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "Unknown Error.");
	/** ���������⡼�� */
	protected int accessMode;
	/** ���߻��Ѥ��Ƥ��륤��ǥå��� */
	protected CobolIndex currentIndex = null;
	/** ���߻��Ѥ��Ƥ��륤��ǥå����ե����� */
	// protected CobolFile currentIndexFile = null;
	/** ���٥�Ȥ�¹Ԥ�����ʬ */
	private CobolFileEventListener eventer = new InnerCobolFileEventAdapter();
	/** ����ǥå������饤��ǥå����ե������������� */
	protected Map<CobolIndex, CobolFile> index2File;
	/** ����ǥå���̾���饤��ǥå������������ */
	protected Map<String, CobolIndex> indexName2Index;
	/**
	 * �Хåե��ν�����������Ǿ������������祵����
	 */
	private int initialSequencialReadBufferSize = 0, maximumSequencialReadBufferSize = 0, minimumSequencialReadBufferSize = 0;
	/** ���٥�ȥꥹ�� */
	private ArrayList<CobolFileEventListener> listeners = new ArrayList<CobolFileEventListener>();
	/** �����ץ�⡼�� */
	protected int openmode;
	/** �����Хåե� */
	protected SequencialReadBuffer sequencialReadBuffer = null;
	/** ���å���� */
	private ACMSession session;
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#addCobolFileEventListener(jp
	 * .ne.biglobe.mvh.k_kim_mg.acm.event.CobolFileEventListener)
	 */
	public void addCobolFileEventListener(CobolFileEventListener listener) {
		listeners.add(listener);
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#addIndex(jp.ne.biglobe.mvh.k_kim_mg
	 * .acm.CobolIndex, k_kim_mg.sa4cob2db.CobolFile)
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
	 * @see k_kim_mg.sa4cob2db.CobolFile#bindSession(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.ACMSession)
	 */
	public void bindSession(ACMSession session) {
		this.session = session;
	}
	/**
	 * ����ǥå������Ĥ���
	 * @return ���ơ�����
	 */
	protected FileStatus closeIndexes() {
		FileStatus ret = FileStatus.OK;
		if (index2File != null) {
			for (CobolFile idfile : index2File.values()) {
				FileStatus stat = idfile.close();
				if (!stat.getStatusCode().equals(FileStatus.STATUS_OK)) {
					ret = stat;
				}
			}
		}
		return ret;
	}
	/**
	 * �쥳���ɥХ����������� ���������Ӥ���
	 * @param record1 �쥳���ɥХ�������
	 * @param record2 �쥳���ɥХ�������
	 * @return STATUS_EQUAL ������������ <br/>
	 *         STATUS_REC1 record1�Υ����������� <br/>
	 *         STATUS_REC2 record2�Υ�����������
	 * @throws CobolRecordException
	 */
	protected int compare(byte[] record1, byte[] record2) throws CobolRecordException {
		int ret = COMPARE_EQUAL;
		CobolRecordMetaData meta = getMetaData();
		// �쥳���ɣ�
		DefaultCobolRecord crecord1 = new DefaultCobolRecord(meta);
		crecord1.setRecord(record1);
		// �쥳���ɣ�
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
	 * �����Хåե��κ���
	 * @return �ǥե���Ȥ������Хåե�
	 */
	protected SequencialReadBuffer createSequencialReadBuffer() {
		return new DefaultSequencialReadBuffer(getInitialSequencialReadBufferSize(), getMinimumSequencialReadBufferSize(), getMaximumSequencialReadBufferSize());
	}
	/**
	 * ���������⡼��
	 * @return ���������⡼��
	 */
	public int getAccessMode() {
		return accessMode;
	}
	/**
	 * ���߻�����Υ���ǥå������������
	 * @return ����ǥå���<\br>���Ѥ��Ƥ��ʤ�����null
	 */
	public CobolIndex getCurrentIndex() {
		return currentIndex;
	}
	/**
	 * ���٥�Ȥ�¹Ԥ��뤿��Υ��֥�������
	 * @return ���٥�Ȥ�¹Ԥ��뤿��Υ��֥�������
	 */
	protected CobolFileEventListener getEventProcessor() {
		return eventer;
	}
	/**
	 * ����ǥå���̾���饤��ǥå����μ���
	 * @param name ����ǥå���̾
	 * @return ����ǥå�����������̵���ä���null
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
	 * ���ܥ륤��ǥå������饤��ǥå����ե�����μ���
	 * @param index ����ǥå���
	 * @return ����ǥå����ե����롢������̵���ä���null
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
	 * ����Хåե�������
	 * @return ����Хåե�������
	 */
	protected int getInitialSequencialReadBufferSize() {
		return initialSequencialReadBufferSize;
	}
	/**
	 * ����Хåե�������
	 * @return ����Хåե�������
	 */
	protected int getMaximumSequencialReadBufferSize() {
		return maximumSequencialReadBufferSize;
	}
	/**
	 * �Ǿ��Хåե�������
	 * @return �Ǿ��Хåե�������
	 */
	protected int getMinimumSequencialReadBufferSize() {
		return minimumSequencialReadBufferSize;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#getOpenMode()
	 */
	public int getOpenMode() {
		return openmode;
	}
	/**
	 * �꡼�ɥХåե��μ���
	 * @return �꡼�ɥХåե�
	 */
	protected SequencialReadBuffer getSequencialReadBuffer() {
		return sequencialReadBuffer;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#getSession(jp.ne.biglobe.mvh
	 * .k_kim_mg.acm.ACMSession)
	 */
	public ACMSession getSession() {
		return session;
	}
	/**
	 * �ե����뤬��ü�ޤ��ɤ߹��ޤ줿����
	 * @return �ե����뤬��ü�ޤ��ɤ߹��ޤ줿���ɤ���
	 */
	public abstract boolean isLastMoved();
	/**
	 * ���֤Ť�����
	 * @param row �����ܡ�
	 * @return ���ơ�����
	 */
	public abstract FileStatus move(int row);
	/**
	 * �ԥ��åȤΰ��ֺǽ�˰�ư����
	 * @return ���ơ�����
	 */
	public abstract FileStatus moveFirst();
	/**
	 * �ԥ��åȤΰ��ֺǸ�˰�ư����
	 * @return ���ơ�����
	 */
	public abstract FileStatus moveLast();
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#next()
	 */
	public FileStatus next() {
		if (getAccessMode() == CobolFile.ACCESS_DYNAMIC && currentIndex != null) {
			return nextOnIndex();
		}
		if (getMaximumSequencialReadBufferSize() > 0 && sequencialReadBuffer != null && getAccessMode() == CobolFile.ACCESS_SEQUENCIAL && getOpenMode() == CobolFile.MODE_INPUT) {
			return nextOnBuffer();
		}
		return nextOnFile();
	}
	/**
	 * row��ʬ���Υ쥳���ɤذ�ư����
	 * @param row �Կ�
	 * @return ���ơ�����
	 */
	public abstract FileStatus next(int row);
	/**
	 * �Хåե���ΰ��֤��ư����
	 * @return �ե����륹�ơ�����
	 */
	public FileStatus nextOnBuffer() {
		FileStatus ret = null;
		if (sequencialReadBuffer == null) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "buffer is null.");
		} else {
			ret = sequencialReadBuffer.nextBuffer();
		}
		return ret;
	}
	/**
	 * �����ե�����ι԰��֤��ư����
	 * @return ���ơ�����
	 */
	protected abstract FileStatus nextOnFile();
	/**
	 * ����ǥå����˱��������֤Υ쥳���ɼ����֤˰�ư����
	 * @return ���ơ�����
	 */
	protected FileStatus nextOnIndex() {
		if (currentIndex == null) {
			return new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "not started");
		}
		// ����ǥå����쥳����
		CobolFile currentIndexFile = getIndexFile(currentIndex);
		CobolRecordMetaData indexmeta = currentIndexFile.getMetaData();
		DefaultCobolRecord indexrecord = new DefaultCobolRecord(indexmeta);
		byte[] indexbytes = new byte[indexmeta.getRowSize()];
		// ��ե�����쥳����
		CobolRecordMetaData meta = getMetaData();
		DefaultCobolRecord mainrecord = new DefaultCobolRecord(meta);
		FileStatus ret;
		ret = currentIndexFile.next();
		if (ret.getStatusCode() == FileStatus.STATUS_OK) {
			// ����ǥå������ߤ���
			ret = currentIndexFile.read(indexbytes);
			if (ret.getStatusCode() == FileStatus.STATUS_OK) {
				try {
					indexrecord.setRecord(indexbytes);
					// ����ǥå�������ե�����
					Map<CobolColumn, CobolColumn> map1 = currentIndex.getFileKey2IndexColumn();
					Set<Map.Entry<CobolColumn, CobolColumn>> set = map1.entrySet();
					Iterator<Map.Entry<CobolColumn, CobolColumn>> ite = set.iterator();
					while (ite.hasNext()) {
						Map.Entry<CobolColumn, CobolColumn> ent = ite.next();
						CobolColumn indexColumn = ent.getKey();
						CobolColumn fileColumn = ent.getValue();
						mainrecord.updateBytes(fileColumn, indexrecord.getBytes(indexColumn));
					}
					// ��ե�����򸡺�
					byte[] record = new byte[meta.getRowSize()];
					/* len = */mainrecord.getRecord(record);
					ret = move(record);
				} catch (CobolRecordException e) {
					SQLNetServer.logger.log(Level.WARNING, e.getMessage(), e);
					ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, e.getMessage());
				}
			}
		}
		return ret;
	}
	/**
	 * ����ǥå����򳫤�
	 * @return ���ơ�����
	 */
	protected FileStatus openIndexes() {
		FileStatus ret = FileStatus.OK;
		if (index2File != null) {
			for (CobolFile idfile : index2File.values()) {
				FileStatus stat = idfile.open(CobolFile.MODE_INPUT, CobolFile.ACCESS_DYNAMIC);
				if (!stat.getStatusCode().equals(FileStatus.STATUS_OK)) {
					ret = stat;
				}
			}
		}
		return ret;
	}
	/**
	 * row��ʬ���Υ쥳���ɤذ�ư����
	 * @param row �Կ�
	 * @return ���ơ�����
	 */
	public abstract FileStatus previous(int row);
	/**
	 * this���֥������Ȥ��֤�
	 * @return this<br>
	 *         �ޤ�������ҤˤʤäƤ��륯�饹�Τ���Υ᥽�åɡ�������͡�
	 */
	protected CobolFile proxy() {
		return this;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#read(byte[])
	 */
	public FileStatus read(byte[] record) {
		if (getMaximumSequencialReadBufferSize() > 0 && sequencialReadBuffer != null && getAccessMode() == CobolFile.ACCESS_SEQUENCIAL && getOpenMode() == CobolFile.MODE_INPUT) {
			return readFromBuffer(record);
		}
		return readFromFile(record);
	}
	/**
	 * �����Хåե�����쥳���ɤ��ɤ߼��
	 * @param record �쥳����
	 * @return �ե����륹�ơ�����
	 */
	public FileStatus readFromBuffer(byte[] record) {
		FileStatus ret = null;
		if (sequencialReadBuffer == null) {
			ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "buffer is null.");
		} else {
			ret = sequencialReadBuffer.readBuffer(record);
		}
		return ret;
	}
	/**
	 * �����ե����뤫���������
	 * @param record Ϣ�ۤ���쥳����
	 * @return ���ơ�����
	 */
	protected abstract FileStatus readFromFile(byte[] record);
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#removeCobolFileEventListener
	 * (k_kim_mg.sa4cob2db.event.CobolFileEventListener)
	 */
	public void removeCobolFileEventListener(CobolFileEventListener listener) {
		listeners.remove(listener);
	}
	/**
	 * ���߻�����Υ���ǥå����򥯥ꥢ����<br>
	 * �Ĥޤꥤ��ǥå�������Ѥ��Ƥ��ʤ����֤ˤ���<br>
	 * setCurrentIndex(null)��Ʊ��<br>
	 * ���Υ��饹�Υ��֥��饹�򤵤��������move�μ����Ǥ��Υ᥽�åɤ�Ƥ֤���
	 */
	public void resetCurrentIndex() {
		setCurrentIndex(null);
	}
	/**
	 * ���߻�����Υ���ǥå��������ꤹ��
	 * @param currentIndex ����ǥå���<br>
	 *            ���Ѥ��ʤ�����null
	 */
	public void setCurrentIndex(CobolIndex currentIndex) {
		this.currentIndex = currentIndex;
	}
	/**
	 * ����Хåե�������
	 * @param initialSequencialReadBufferSize ����Хåե�������
	 */
	protected void setInitialSequencialReadBufferSize(int initialSequencialReadBufferSize) {
		this.initialSequencialReadBufferSize = initialSequencialReadBufferSize;
	}
	/**
	 * ������Хåե�������
	 * @param maximumSequencialReadBufferSize ����Хåե�������
	 */
	protected void setMaximumSequencialReadBufferSize(int maximumSequencialReadBufferSize) {
		this.maximumSequencialReadBufferSize = maximumSequencialReadBufferSize;
	}
	/**
	 * �Ǿ��Хåե�������
	 * @param minimumSequencialReadBufferSize �Ǿ��Хåե�������
	 */
	protected void setMinimumSequencialReadBufferSize(int minimumSequencialReadBufferSize) {
		this.minimumSequencialReadBufferSize = minimumSequencialReadBufferSize;
	}
	/**
	 * �꡼�ɥХåե��Υ��å�
	 * @param sequencialReadBuffer �꡼�ɥХåե�
	 */
	protected void setSequencialReadBuffer(SequencialReadBuffer sequencialReadBuffer) {
		this.sequencialReadBuffer = sequencialReadBuffer;
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(int, byte[], boolean)
	 */
	public FileStatus start(int mode, byte[] record, boolean duplicates) {
		return (duplicates ? startDuplicates(mode, record) : start(mode, record));
	}
	/*
	 * (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.CobolFile#start(java.lang.String, int, byte[],
	 * boolean)
	 */
	public FileStatus start(String IndexName, int mode, byte[] record) {
		FileStatus ret = STATUS_UNKNOWN_ERROR;
		CobolIndex index = getIndex(IndexName);
		CobolFile indexFile = getIndexFile(index);
		if (index != null && indexFile != null) {
			CobolRecordMetaData meta = getMetaData();
			// ��ե�����쥳����
			DefaultCobolRecord mainrecord = new DefaultCobolRecord(meta);
			mainrecord.setRecord(record);
			try {
				CobolRecordMetaData indexmeta = indexFile.getMetaData();
				DefaultCobolRecord indexrecord = new DefaultCobolRecord(indexmeta);
				byte[] indexbytes = new byte[indexmeta.getRowSize()];
				// ��ե����뢪����ǥå���
				Map<CobolColumn, CobolColumn> map0 = index.getIndexKey2FileColumn();
				Set<Map.Entry<CobolColumn, CobolColumn>> set0 = map0.entrySet();
				Iterator<Map.Entry<CobolColumn, CobolColumn>> ite0 = set0.iterator();
				while (ite0.hasNext()) {
					Map.Entry<CobolColumn, CobolColumn> ent0 = ite0.next();
					CobolColumn indexColumn = ent0.getKey();
					CobolColumn fileColumn = ent0.getValue();
					indexrecord.updateBytes(indexColumn, mainrecord.getBytes(fileColumn));
				}
				// ����ǥå����򸡺�
				/* int len = */indexrecord.getRecord(indexbytes);
				ret = indexFile.start(mode, indexbytes, index.isDuplicates());
				if (ret.getStatusCode() == FileStatus.STATUS_OK) {
					ret = indexFile.read(indexbytes);
					indexrecord.setRecord(indexbytes);
					// ����ǥå�������ե�����
					Map<CobolColumn, CobolColumn> map1 = index.getFileKey2IndexColumn();
					Set<Map.Entry<CobolColumn, CobolColumn>> set1 = map1.entrySet();
					Iterator<Map.Entry<CobolColumn, CobolColumn>> ite1 = set1.iterator();
					while (ite1.hasNext()) {
						Map.Entry<CobolColumn, CobolColumn> ent1 = ite1.next();
						CobolColumn indexColumn = ent1.getKey();
						CobolColumn fileColumn = ent1.getValue();
						mainrecord.updateBytes(fileColumn, indexrecord.getBytes(indexColumn));
					}
					// ��ե�����򸡺�
					/* len = */mainrecord.getRecord(record);
					ret = move(record);
					if (ret.getStatusCode() == FileStatus.STATUS_OK) {
						currentIndex = index;
					}
				}
			} catch (CobolRecordException e) {
				SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
				// ��������㳰�Ϥ��Τޤ޳��˽Ф�
				ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
			} catch (Exception ex) {
				SQLNetServer.logger.log(Level.SEVERE, "Exception", ex);
				// ���Τۤ����㳰
				ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, ex.getMessage());
			}
		} else {
			// ���������ʤ��Ǹ���
			resetCurrentIndex();
			ret = start(mode, record);
		}
		return ret;
	}
	/**
	 * �Хåե���󥰤γ��� �꡼�ɥ���꡼�ǽ�ե�����λ����Хåե���󥰤򳫻Ϥ���
	 */
	public void startBuffer() {
		if ((getMaximumSequencialReadBufferSize() > 0 && getAccessMode() == CobolFile.ACCESS_SEQUENCIAL && getOpenMode() == CobolFile.MODE_INPUT)) {
			if (sequencialReadBuffer == null) {
				sequencialReadBuffer = createSequencialReadBuffer();
			}
			getSequencialReadBuffer().startBuffering();
		}
	}
	/**
	 * �����դ�����ʽ�ʣ�����
	 * @param mode �⡼��(EQ GT �ʤ�)
	 * @param record ������ޤ�쥳����
	 * @return ���ơ�����
	 */
	public abstract FileStatus startDuplicates(int mode, byte[] record);
}