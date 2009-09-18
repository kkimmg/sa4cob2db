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
 * コボルファイルの基本型<br>
 * このクラスではイベントを発生させないので実装クラス側でイベントを発生させてちょ。
 * @author おれおれ
 */
public abstract class AbstractCobolFile implements CobolFile {
	/**
	 * シーケンシャルファイルのバッファ
	 * @author おれおれ
	 */
	protected class DefaultSequencialReadBuffer implements Runnable, SequencialReadBuffer {
		/** バッファの使用中フラグ */
		private volatile boolean[][] buffStatus;
		/** 読み込みを継続するかどうか */
		private volatile boolean cont = true;
		/** 現在のサイズ (CurrentSize) */
		private volatile int cs;
		/** 配列サイズ */
		private int dimentionSize;
		/** 初期サイズに(一旦)到達した */
		private volatile boolean initCleared = false;
		/**
		 * 初期サイズ <br>
		 * 1.未初期化の状態でこの数値を下回った場合は待機する<br>
		 * 2.一旦minSizeを下回ったあと、この数値を下回った場合は待機する
		 */
		private int initSize;
		/** EOFか？ */
		private volatile boolean lastRead = false;
		/** 最大サイズ */
		private int maxSize;
		/**
		 * 最小サイズ このサイズを下回ったらinitSizeまで読み込みを待機する
		 */
		private int minSize;
		/** Next処理のファイルステータス */
		private volatile FileStatus[][] nextStatus;
		/** 現在読み込み中のバッファ指定(ReadingBuffer) */
		private volatile int rb;
		/** Read処理のファイルステータス */
		private volatile FileStatus[][] readStatus;
		/** レコードのバッファ */
		private volatile byte[][][] records;
		/** 読み込み中の位置(ReadingIndex) */
		private volatile int ri;
		/** 内部スレッド */
		private Thread thread;
		/** 現在書き込み中のバッファ指定(WritingBuffer) */
		private volatile int wb;
		/** 過去込み中の位置(WritingIndex) */
		private volatile int wi;
		/**
		 * コンストラクタ<br>
		 * 最小サイズ:2500<br>
		 * 初期サイズ:5000<br>
		 * 最大サイズ:10000<br>
		 * で作成する
		 */
		public DefaultSequencialReadBuffer() {
			this(5000, 2500, 10000);
		}
		/**
		 * コンストラクタ
		 * @param initSize 初期サイズ
		 * @param minSize 最小サイズ
		 * @param maxSize 最大サイズ
		 */
		public DefaultSequencialReadBuffer(int initSize, int minSize, int maxSize) {
			this.initSize = initSize;
			this.minSize = minSize;
			this.maxSize = maxSize;
			cs = 0;
			// バッファサイズの決定
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
			// バッファの初期化
			initBuffers();
		}
		/**
		 * バッファの初期化
		 * @param size バッファサイズ(最大サイズの半分)
		 */
		void initBuffers() {
			records = new byte[2][dimentionSize][];
			nextStatus = new FileStatus[2][dimentionSize];
			readStatus = new FileStatus[2][dimentionSize];
			buffStatus = new boolean[2][dimentionSize];
			// レコードサイズ
			int recSize = getMetaData().getRowSize();
			// バッファの初期化処理
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
			// まだ最終レコードまでいってない
			lastRead = false;
		}
		/**
		 * バッファ読み込みを待機するべきかどうか？ <br>
		 * 1.初期サイズに到達していない <br>
		 * 2.読み込み対象バッファがまだ有効ではない<br>
		 * 3.バッファのサイズが0より小さい
		 * @return true 待機せよ<br>
		 *         false 待機しない
		 */
		boolean isWaitToRead() {
			boolean ret = false;
			if (!lastRead) {
				// EOFでないこと
				if (!initCleared && cs < initSize) {
					// 初期サイズに到達していない
					ret = true;
				}
				if (!buffStatus[rb][ri]) {
					// 読み込み対象バッファがまだ有効ではない
					ret = true;
				}
				if (cs < 0) {
					// バッファサイズが0より小さい
					ret = true;
				}
			}
			return ret;
		}
		/**
		 * バッファ作成を待機するべきかどうか？<br>
		 * 1.バッファが最大サイズに達している <br>
		 * 2.書き込み先のバッファが未使用である
		 * @return true 待機せよ<br>
		 *         false 待機しない
		 */
		boolean isWaitToWrite() {
			// バッファが最大サイズに達しているか
			// 書き込み先のバッファが未使用であれば待機する
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
			// 読み込み位置の変更
			ri++;
			if (ri >= dimentionSize) {
				rb = (rb == 0 ? 1 : 0);
				ri = 0;
			}
			// 読み込み待機
			while (isWaitToRead()) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					SQLNetServer.logger.log(Level.SEVERE, "Interrupted To Read Wait", e);
				}
			}
			// ファイルステータスを返す
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
		 * 実行
		 */
		public void run() {
			cont = true;
			FileStatus ns, rs; // NextStatus, ReadStatus
			while (cont) {
				// 読み込み待ち
				while (isWaitToWrite() && cont) {
					try {
						SQLNetServer.logger.log(Level.FINEST, "Buffering is Waiting");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						SQLNetServer.logger.log(Level.SEVERE, "Interrupted To Write Wait", e);
					}
				}
				if (cont) {
					// 読み込み実行
					ns = nextOnFile();
					nextStatus[wb][wi] = ns;
					if (ns.getStatusCode().equals(FileStatus.STATUS_OK)) {
						rs = readFromFile(records[wb][wi]);
						readStatus[wb][wi] = rs;
						buffStatus[wb][wi] = true;
						// 次へ
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
						// エンド オブ ファイル
						// SQLNetServer.logger.log(Level.FINEST, "buffering
						// end." + thread.getName());
						lastRead = true;
						cont = false;
						readStatus[wb][wi] = ns;
						buffStatus[wb][wi] = true;
					} else {
						// 何らかのエラー
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
	 * イベントを実行するためのクラス
	 * @author おれおれ
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
	/** ２つのレコード（キー）は等しい */
	protected static final int COMPARE_EQUAL = 0;
	/** レコード１が小さい */
	protected static final int COMPARE_REC1 = 1;
	/** レコード２が小さい */
	protected static final int COMPARE_REC2 = 2;
	private static final long serialVersionUID = 1L;
	/** エンドオブファイル */
	protected static final FileStatus STATUS_EOF = new FileStatus(FileStatus.STATUS_EOF, FileStatus.NULL_CODE, 0, "end of file.");
	/** 原因不明のデフォルトの失敗ステータス */
	protected static final FileStatus STATUS_UNKNOWN_ERROR = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "Unknown Error.");
	/** アクセスモード */
	protected int accessMode;
	/** 現在使用しているインデックス */
	protected CobolIndex currentIndex = null;
	/** 現在使用しているインデックスファイル */
	// protected CobolFile currentIndexFile = null;
	/** イベントを実行する部分 */
	private CobolFileEventListener eventer = new InnerCobolFileEventAdapter();
	/** インデックスからインデックスファイルを取得する */
	protected Map<CobolIndex, CobolFile> index2File;
	/** インデックス名からインデックスを取得する */
	protected Map<String, CobolIndex> indexName2Index;
	/**
	 * バッファの初期サイズ、最小サイズ、最大サイズ
	 */
	private int initialSequencialReadBufferSize = 0, maximumSequencialReadBufferSize = 0, minimumSequencialReadBufferSize = 0;
	/** イベントリスナ */
	private ArrayList<CobolFileEventListener> listeners = new ArrayList<CobolFileEventListener>();
	/** オープンモード */
	protected int openmode;
	/** 内部バッファ */
	protected SequencialReadBuffer sequencialReadBuffer = null;
	/** セッション */
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
	 * インデックスを閉じる
	 * @return ステータス
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
	 * レコードバイト配列の比較 キー列を比較する
	 * @param record1 レコードバイト配列
	 * @param record2 レコードバイト配列
	 * @return STATUS_EQUAL キーが等しい <br/>
	 *         STATUS_REC1 record1のキーが小さい <br/>
	 *         STATUS_REC2 record2のキーが小さい
	 * @throws CobolRecordException
	 */
	protected int compare(byte[] record1, byte[] record2) throws CobolRecordException {
		int ret = COMPARE_EQUAL;
		CobolRecordMetaData meta = getMetaData();
		// レコード１
		DefaultCobolRecord crecord1 = new DefaultCobolRecord(meta);
		crecord1.setRecord(record1);
		// レコード２
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
	 * 内部バッファの作成
	 * @return デフォルトの内部バッファ
	 */
	protected SequencialReadBuffer createSequencialReadBuffer() {
		return new DefaultSequencialReadBuffer(getInitialSequencialReadBufferSize(), getMinimumSequencialReadBufferSize(), getMaximumSequencialReadBufferSize());
	}
	/**
	 * アクセスモード
	 * @return アクセスモード
	 */
	public int getAccessMode() {
		return accessMode;
	}
	/**
	 * 現在使用中のインデックスを取得する
	 * @return インデックス<\br>使用していない場合はnull
	 */
	public CobolIndex getCurrentIndex() {
		return currentIndex;
	}
	/**
	 * イベントを実行するためのオブジェクト
	 * @return イベントを実行するためのオブジェクト
	 */
	protected CobolFileEventListener getEventProcessor() {
		return eventer;
	}
	/**
	 * インデックス名からインデックスの取得
	 * @param name インデックス名
	 * @return インデックス、ただし無かったらnull
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
	 * コボルインデックスからインデックスファイルの取得
	 * @param index インデックス
	 * @return インデックスファイル、ただし無かったらnull
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
	 * 初期バッファさイズ
	 * @return 初期バッファさイズ
	 */
	protected int getInitialSequencialReadBufferSize() {
		return initialSequencialReadBufferSize;
	}
	/**
	 * 最大バッファサイズ
	 * @return 最大バッファサイズ
	 */
	protected int getMaximumSequencialReadBufferSize() {
		return maximumSequencialReadBufferSize;
	}
	/**
	 * 最小バッファサイズ
	 * @return 最小バッファサイズ
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
	 * リードバッファの取得
	 * @return リードバッファ
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
	 * ファイルが終端まで読み込まれたか？
	 * @return ファイルが終端まで読み込まれたかどうか
	 */
	public abstract boolean isLastMoved();
	/**
	 * 位置づけ処理
	 * @param row 何行目？
	 * @return ステータス
	 */
	public abstract FileStatus move(int row);
	/**
	 * 行セットの一番最初に移動する
	 * @return ステータス
	 */
	public abstract FileStatus moveFirst();
	/**
	 * 行セットの一番最後に移動する
	 * @return ステータス
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
	 * row行分次のレコードへ移動する
	 * @param row 行数
	 * @return ステータス
	 */
	public abstract FileStatus next(int row);
	/**
	 * バッファ上の位置を移動する
	 * @return ファイルステータス
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
	 * 内部ファイルの行位置を移動する
	 * @return ステータス
	 */
	protected abstract FileStatus nextOnFile();
	/**
	 * インデックスに応じた順番のレコード次位置に移動する
	 * @return ステータス
	 */
	protected FileStatus nextOnIndex() {
		if (currentIndex == null) {
			return new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, "not started");
		}
		// インデックスレコード
		CobolFile currentIndexFile = getIndexFile(currentIndex);
		CobolRecordMetaData indexmeta = currentIndexFile.getMetaData();
		DefaultCobolRecord indexrecord = new DefaultCobolRecord(indexmeta);
		byte[] indexbytes = new byte[indexmeta.getRowSize()];
		// 主ファイルレコード
		CobolRecordMetaData meta = getMetaData();
		DefaultCobolRecord mainrecord = new DefaultCobolRecord(meta);
		FileStatus ret;
		ret = currentIndexFile.next();
		if (ret.getStatusCode() == FileStatus.STATUS_OK) {
			// インデックスをよみこむ
			ret = currentIndexFile.read(indexbytes);
			if (ret.getStatusCode() == FileStatus.STATUS_OK) {
				try {
					indexrecord.setRecord(indexbytes);
					// インデックス→主ファイル
					Map<CobolColumn, CobolColumn> map1 = currentIndex.getFileKey2IndexColumn();
					Set<Map.Entry<CobolColumn, CobolColumn>> set = map1.entrySet();
					Iterator<Map.Entry<CobolColumn, CobolColumn>> ite = set.iterator();
					while (ite.hasNext()) {
						Map.Entry<CobolColumn, CobolColumn> ent = ite.next();
						CobolColumn indexColumn = ent.getKey();
						CobolColumn fileColumn = ent.getValue();
						mainrecord.updateBytes(fileColumn, indexrecord.getBytes(indexColumn));
					}
					// 主ファイルを検索
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
	 * インデックスを開く
	 * @return ステータス
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
	 * row行分前のレコードへ移動する
	 * @param row 行数
	 * @return ステータス
	 */
	public abstract FileStatus previous(int row);
	/**
	 * thisオブジェクトを返す
	 * @return this<br>
	 *         まぁ、入れ子になっているクラスのためのメソッド・・・よね？
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
	 * 内部バッファからレコードを読み取る
	 * @param record レコード
	 * @return ファイルステータス
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
	 * 内部ファイルから取得する
	 * @param record 連想するレコード
	 * @return ステータス
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
	 * 現在使用中のインデックスをクリアする<br>
	 * つまりインデックスを使用していない状態にする<br>
	 * setCurrentIndex(null)と同じ<br>
	 * このクラスのサブクラスをさくする場合はmoveの実装でこのメソッドを呼ぶこと
	 */
	public void resetCurrentIndex() {
		setCurrentIndex(null);
	}
	/**
	 * 現在使用中のインデックスを設定する
	 * @param currentIndex インデックス<br>
	 *            使用しない場合はnull
	 */
	public void setCurrentIndex(CobolIndex currentIndex) {
		this.currentIndex = currentIndex;
	}
	/**
	 * 初期バッファさイズ
	 * @param initialSequencialReadBufferSize 初期バッファさイズ
	 */
	protected void setInitialSequencialReadBufferSize(int initialSequencialReadBufferSize) {
		this.initialSequencialReadBufferSize = initialSequencialReadBufferSize;
	}
	/**
	 * 　最大バッファサイズ
	 * @param maximumSequencialReadBufferSize 最大バッファサイズ
	 */
	protected void setMaximumSequencialReadBufferSize(int maximumSequencialReadBufferSize) {
		this.maximumSequencialReadBufferSize = maximumSequencialReadBufferSize;
	}
	/**
	 * 最小バッファサイズ
	 * @param minimumSequencialReadBufferSize 最小バッファサイズ
	 */
	protected void setMinimumSequencialReadBufferSize(int minimumSequencialReadBufferSize) {
		this.minimumSequencialReadBufferSize = minimumSequencialReadBufferSize;
	}
	/**
	 * リードバッファのセット
	 * @param sequencialReadBuffer リードバッファ
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
			// 主ファイルレコード
			DefaultCobolRecord mainrecord = new DefaultCobolRecord(meta);
			mainrecord.setRecord(record);
			try {
				CobolRecordMetaData indexmeta = indexFile.getMetaData();
				DefaultCobolRecord indexrecord = new DefaultCobolRecord(indexmeta);
				byte[] indexbytes = new byte[indexmeta.getRowSize()];
				// 主ファイル→インデックス
				Map<CobolColumn, CobolColumn> map0 = index.getIndexKey2FileColumn();
				Set<Map.Entry<CobolColumn, CobolColumn>> set0 = map0.entrySet();
				Iterator<Map.Entry<CobolColumn, CobolColumn>> ite0 = set0.iterator();
				while (ite0.hasNext()) {
					Map.Entry<CobolColumn, CobolColumn> ent0 = ite0.next();
					CobolColumn indexColumn = ent0.getKey();
					CobolColumn fileColumn = ent0.getValue();
					indexrecord.updateBytes(indexColumn, mainrecord.getBytes(fileColumn));
				}
				// インデックスを検索
				/* int len = */indexrecord.getRecord(indexbytes);
				ret = indexFile.start(mode, indexbytes, index.isDuplicates());
				if (ret.getStatusCode() == FileStatus.STATUS_OK) {
					ret = indexFile.read(indexbytes);
					indexrecord.setRecord(indexbytes);
					// インデックス→主ファイル
					Map<CobolColumn, CobolColumn> map1 = index.getFileKey2IndexColumn();
					Set<Map.Entry<CobolColumn, CobolColumn>> set1 = map1.entrySet();
					Iterator<Map.Entry<CobolColumn, CobolColumn>> ite1 = set1.iterator();
					while (ite1.hasNext()) {
						Map.Entry<CobolColumn, CobolColumn> ent1 = ite1.next();
						CobolColumn indexColumn = ent1.getKey();
						CobolColumn fileColumn = ent1.getValue();
						mainrecord.updateBytes(fileColumn, indexrecord.getBytes(indexColumn));
					}
					// 主ファイルを検索
					/* len = */mainrecord.getRecord(record);
					ret = move(record);
					if (ret.getStatusCode() == FileStatus.STATUS_OK) {
						currentIndex = index;
					}
				}
			} catch (CobolRecordException e) {
				SQLNetServer.logger.log(Level.SEVERE, "Exception", e);
				// この中の例外はそのまま外に出す
				ret = new FileStatus(FileStatus.STATUS_FAILURE, e.getSQLState(), e.getErrorCode(), e.getMessage());
			} catch (Exception ex) {
				SQLNetServer.logger.log(Level.SEVERE, "Exception", ex);
				// そのほかの例外
				ret = new FileStatus(FileStatus.STATUS_FAILURE, FileStatus.NULL_CODE, 0, ex.getMessage());
			}
		} else {
			// ２次キーなしで検索
			resetCurrentIndex();
			ret = start(mode, record);
		}
		return ret;
	}
	/**
	 * バッファリングの開始 リードオンリーで順ファイルの時、バッファリングを開始する
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
	 * 位置付けする（重複あり）
	 * @param mode モード(EQ GT など)
	 * @param record キーを含むレコード
	 * @return ステータス
	 */
	public abstract FileStatus startDuplicates(int mode, byte[] record);
}