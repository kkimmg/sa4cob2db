/*
 * Created on 2004/05/23 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.codegen;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import k_kim_mg.sa4cob2db.FileStatus;
/**
 * 引き渡されたソースコードを蓄積し、変換した結果をオーナーに返す機能
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class TCPCodeGenerator implements CodeGenerator {
	/**
	 * SELECT句の情報を退避するオブジェクト
	 */
	static class DefaultFileInfo implements FileInfo {
		/** アクセスモード */
		int acessMode;
		/**
		 * ファイル名（外部ファイル名？） <br/>
		 * アサインする（ファイルシステム上の）ファイル名
		 */
		String fileName;
		/**
		 * レコード名？ <br/>
		 * FD句で01レベルの領域名
		 */
		String recordName;
		/**
		 * ファイル名（内部ファイル名？） <br/>
		 * SELECT句の次のトークン
		 */
		String selectName;
		/**
		 * 入出力状態 <br/>
		 * 入出力状態を示す領域名(File Status [is] XXXXXX)
		 */
		String status;
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getAcessMode()
		 */
		public int getAcessMode() {
			return acessMode;
		}
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getFileName()
		 */
		public String getFileName() {
			return fileName;
		}
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getRecordName()
		 */
		public String getRecordName() {
			return recordName;
		}
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getSelectName()
		 */
		public String getSelectName() {
			return selectName;
		}
		/*
		 * (non-Javadoc)
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getStatus()
		 */
		public String getStatus() {
			return status;
		}
		/**
		 * アクセスモードをセットする
		 * @param acessmode セットするアクセスモード
		 */
		public void setAcessMode(int acessmode) {
			this.acessMode = acessmode;
		}
		/**
		 * ファイル名をセットする
		 * @param filename セットするファイル名
		 */
		public void setFileName(String filename) {
			this.fileName = filename;
		}
		/**
		 * レコード名をセットする
		 * @param recordname セットするレコード名
		 */
		public void setRecordName(String recordname) {
			this.recordName = recordname;
		}
		/**
		 * ファイル名をセットする
		 * @param selectname セットするファイル名
		 */
		public void setSelectName(String selectname) {
			this.selectName = selectname;
		}
		/**
		 * ステータス領域名をセットする
		 * @param status セットするステータス領域名
		 */
		public void setStatus(String status) {
			this.status = status;
		}
	}
	/** レコード名 */
	String acmRecName = null;
	/** コピー句の一覧 */
	ArrayList<String> copys = new ArrayList<String>();
	/** 現在のパターン */
	String current;
	/** 現在処理中のファイル情報 */
	DefaultFileInfo currentinfo = null;
	/** 変換候補として退避している文字列 */
	ArrayList<String> currentlist = new ArrayList<String>();
	/** currentlistsを積み上げる */
	Stack<ArrayList<String>> currentlists = new Stack<ArrayList<String>>();
	/** 現在のDIVISION */
	String division = null;
	/** ダミーのファイル情報 */
	final FileInfo dummyInfo = new DefaultFileInfo();
	/** 現在処理中のFD句 */
	ArrayList<String> fdlist = new ArrayList<String>();
	/** ファイル名（外部ファイル名？）からファイル情報の実体を取得するための索引 */
	Hashtable<String, DefaultFileInfo> filenametofile = new Hashtable<String, DefaultFileInfo>();
	/** 処理中のプログラムは(ACMでない)実際のファイルをアサインするかどうか */
	boolean hasNonACM = false;
	/** 現在、ACMファイル処理中か？ */
	boolean inACM = false;
	/** コピー句の途中か？ */
	boolean inCopy = false;
	/** 現在、FD句を処理中か？ */
	boolean inFD = false;
	/** このソースは初期化命令が入力されたか？ */
	boolean initialized = false;
	/** PROCEDURE SECTION中の取得済みラベル数 */
	int label = 0;
	/**
	 * レベル--ソースコード中の入れ子レベル
	 */
	int level = 0;
	/**
	 * 出力前のソースコード
	 */
	ArrayList<String> list = new ArrayList<String>();
	private ArrayList<CodeGeneratorListener> listeners = new ArrayList<CodeGeneratorListener>();
	/**
	 * 親オブジェクト
	 */
	GeneratorOwner owner;
	/**
	 * Procefure Divisionを処理中かどうか
	 */
	boolean proceduresection = false;
	/**
	 * レコード名からファイル実体を取得するための索引
	 */
	Hashtable<String, DefaultFileInfo> recordnametofile = new Hashtable<String, DefaultFileInfo>();
	/**
	 * セクション
	 */
	String section = null;
	/**
	 * SELECT名ｋらファイル実体を取得するための索引
	 */
	Hashtable<String, DefaultFileInfo> selectnametofile = new Hashtable<String, DefaultFileInfo>();
	/**
	 * パターンのスタック
	 */
	Stack<String> stack = new Stack<String>();
	/**
	 * コンストラクタ
	 * @param owner 呼び元
	 */
	public TCPCodeGenerator(GeneratorOwner owner) {
		this.owner = owner;
	}
	/**
	 * 追加
	 * @param text 追加するテキスト
	 */
	void add(String text) {
		list.add(text);
		// SQLNetServer.DebugPrint(text);
	}
	/**
	 * FD句の追加
	 * @param text FD句中のテキスト
	 */
	void add_fd(String text) {
		fdlist.add(text);
		if (Pattern.matches(CobolConsts.STORAGE, text)) {
			StringTokenizer tokenizer = new StringTokenizer(text);
			boolean check = false;
			while (tokenizer.hasMoreTokens()) {
				String recname = tokenizer.nextToken().trim();
				// SQLNetServer.DebugPrint(recname);
				// if (Pattern.matches(STORAGE, recname)) {
				if (!check) {
					check = true;
				} else {
					currentinfo.recordName = recname;
					recordnametofile.put(recname, currentinfo);
					// SQLNetServer.DebugPrint("***" + currentinfo);
					break;
				}
			}
		}
	}
	/**
	 * CLOSE処理
	 * @param period "."文字列
	 */
	void addCallClose(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"closeACMFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
	}
	/**
	 * COMMITの追加
	 * @param period ピリオド(.)文字列
	 */
	void addCallCommit(String period) {
		add("    CALL \"commitACMSession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * DELETE処理
	 * @param valid 有効時の処理
	 * @param invalid 無効時の処理
	 * @param period "."文字列
	 */
	void addCallDelete(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"deleteACMRecord\" USING ACM-FILE-IDENT");
		add("                                  ACM-RECORD");
		add("                                  ACM-STATUS-ALL" + period);
		// ///////////
		if (invalid.size() > 0) {
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_INVALID_KEY + "\"");
			for (String str : invalid) {
				if (str.trim().length() > 0) {
					add("         " + str.trim());
				}
			}
			if (notinvalid.size() > 0) {
				add("     ELSE");
				for (String str : notinvalid) {
					if (str.trim().length() > 0) {
						add("         " + str.trim());
					}
				}
			}
			add("     END-IF" + period);
		} else {
		}
	}
	/**
	 * InitializeSessionの追加
	 * @param period ピリオド(.)文字列
	 */
	void addCallInitializeSession(String period) {
		add("     CALL \"initializeSessionEnv\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * OPEN INPUT 処理
	 * @param info ファイルの情報
	 * @param period ピリオド(.)文字列
	 */
	void addCallOpenInput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"assignACMFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		add("     CALL \"openACMFile\"   USING ACM-FILE-IDENT");
		add("                                ACM-OPENMODE-INPUT");
		switch (info.getAcessMode()) {
		case CobolConsts.ORG_SEQUENTIAL:
			add("                                ACM-ACCESSMODE-SEQ");
			break;
		case CobolConsts.ORG_DYNAMIC:
			add("                                ACM-ACCESSMODE-DYN");
			break;
		case CobolConsts.ORG_RANDOM:
			add("                                ACM-ACCESSMODE-RND");
			break;
		}
		add("                                ACM-STATUS-ALL" + period);
	}
	/**
	 * OPEN I-O 処理
	 * @param info ファイルの情報
	 * @param period ピリオド(.)文字列
	 */
	void addCallOpenInputOutput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"assignACMFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		add("     CALL \"openACMFile\"   USING ACM-FILE-IDENT");
		add("                                ACM-OPENMODE-IO");
		switch (info.getAcessMode()) {
		case CobolConsts.ORG_SEQUENTIAL:
			add("                                ACM-ACCESSMODE-SEQ");
			break;
		case CobolConsts.ORG_DYNAMIC:
			add("                                ACM-ACCESSMODE-DYN");
			break;
		case CobolConsts.ORG_RANDOM:
			add("                                ACM-ACCESSMODE-RND");
			break;
		}
		add("                                ACM-STATUS-ALL" + period);
	}
	/**
	 * OPEN OUTPUT 処理
	 * @param info ファイルの情報
	 * @param period ピリオド(.)文字列
	 */
	void addCallOpenOutput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"assignACMFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		add("     CALL \"openACMFile\"   USING ACM-FILE-IDENT");
		add("                                ACM-OPENMODE-OUTPUT");
		switch (info.getAcessMode()) {
		case CobolConsts.ORG_SEQUENTIAL:
			add("                                ACM-ACCESSMODE-SEQ");
			break;
		case CobolConsts.ORG_DYNAMIC:
			add("                                ACM-ACCESSMODE-DYN");
			break;
		case CobolConsts.ORG_RANDOM:
			add("                                ACM-ACCESSMODE-RND");
			break;
		}
		add("                                ACM-STATUS-ALL" + period);
	}
	/**
	 * READ処理
	 * @param info ファイル情報
	 * @param invalid 有効時の処理
	 * @param notinvalid 無効時ん処理
	 * @param indexkey 索引名
	 * @param period ピリオド(.)文字列
	 */
	void addCallRead(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String indexkey, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD" + period);
		add("     CALL \"moveReadACMRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		if (indexkey == null) {
			// キーリード
			add("                                ACM-RECORD");
		} else {
			// 索引によるリード
			add("                                ACM-RECORD");
			add("                                ACM-INDEX-NAME");
		}
		add("                                ACM-STATUS-ALL" + period);
		add("     IF  ACM-STATUS-CODE = \"" + FileStatus.STATUS_OK + "\"");
		add("         MOVE ACM-RECORD TO " + info.getRecordName());
		add("     END-IF" + period);
		if (invalid.size() > 0) {
			// Invalid 処理
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_INVALID_KEY + "\"");
			for (String str : invalid) {
				if (str.trim().length() > 0) {
					add("         " + str.trim());
				}
			}
			if (notinvalid.size() > 0) {
				// Not Invalid 処理
				add(" ELSE");
				for (String str : notinvalid) {
					if (str.trim().length() > 0) {
						add("         " + str.trim());
					}
				}
			}
			add("     END-IF" + period);
		}
	}
	/**
	 * READ処理
	 * @param info ファイル情報
	 * @param atend 有効時の処理
	 * @param notatend 無効(ファイル終端)時ん処理
	 * @param indexkey 索引名
	 * @param period ピリオド(.)文字列
	 */
	void addCallReadNext(FileInfo info, ArrayList<String> atend, ArrayList<String> notatend, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"readNextACMRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		add("                                ACM-RECORD");
		add("                                ACM-STATUS-ALL" + period);
		add("     IF  ACM-STATUS-CODE = \"" + FileStatus.STATUS_OK + "\"");
		add("         MOVE ACM-RECORD TO " + info.getRecordName());
		add("     END-IF" + period);
		if (atend.size() > 0) {
			// At End 処理
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_EOF + "\"");
			for (int i = 0; i < atend.size(); i++) {
				add("         " + atend.get(i));
			}
			if (notatend.size() > 0) {
				// Not At End 処理
				add("     ELSE");
				for (int i = 0; i < notatend.size(); i++) {
					add("        " + notatend.get(i));
				}
			}
			add("     END-IF" + period);
		}
	}
	/**
	 * Rewrite処理
	 * @param info ファイル情報
	 * @param invalid 有効時の処理
	 * @param notinvalid 無効時ん処理
	 * @param indexkey 索引名
	 * @param period ピリオド(.)文字列
	 */
	void addCallRewrite(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"rewriteACMRecord\" USING ACM-FILE-IDENT");
		add("                                   ACM-RECORD");
		add("                                   ACM-STATUS-ALL" + period);
		// ///////////
		if (invalid.size() > 0) {
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_INVALID_KEY + "\"");
			for (String str : invalid) {
				if (str.trim().length() > 0) {
					add("         " + str.trim());
				}
			}
			if (notinvalid.size() > 0) {
				add("     ELSE");
				for (String str : notinvalid) {
					if (str.trim().length() > 0) {
						add("         " + str.trim());
					}
				}
			}
			add("     END-IF" + period);
		} else {
		}
	}
	/**
	 * ROLLBACKの追加
	 * @param period ピリオド(.)文字列
	 */
	void addCallRollback(String period) {
		add("    CALL \"rollbackACMSession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * START処理
	 * @param info ファイル情報
	 * @param invalid 有効時の処理
	 * @param notinvalid 無効時ん処理
	 * @param indexkey 索引名
	 * @param period ピリオド(.)文字列
	 */
	void addCallStart(FileInfo info, String startModeText, ArrayList<String> invalid, ArrayList<String> notinvalid, String indexkey, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE   " + startModeText + "   TO ACM-START-MODE" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD" + period);
		if (indexkey == null) {
			add("     CALL \"startACMRecord\" USING ACM-FILE-IDENT");
			add("                                ACM-RECORD");
		} else {
			add("     MOVE  \"" + indexkey + "\"  TO  ACM-INDEX-NAME" + period);
			add("     CALL \"startACMRecordWith\" USING ACM-FILE-IDENT");
			add("                                ACM-RECORD");
			add("                                ACM-INDEX-NAME");
		}
		add("                                ACM-START-MODE");
		add("                                ACM-STATUS-ALL" + period);
		if (invalid.size() > 0) {
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_INVALID_KEY + "\"");
			for (int i = 0; i < invalid.size(); i++) {
				add("         " + invalid.get(i));
			}
			if (notinvalid.size() > 0) {
				add("     ELSE");
				for (int i = 0; i < notinvalid.size(); i++) {
					add("         " + notinvalid.get(i));
				}
			}
			add("     END-IF" + period);
		} else {
		}
	}
	/**
	 * TermincateSessionの追加
	 * @param period ピリオド(.)文字列
	 */
	void addCallTerminateSession(String period) {
		add("     CALL  \"terminateSession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * WRITE処理
	 * @param info ファイル情報
	 * @param invalid 有効時の処理
	 * @param notinvalid 無効時ん処理
	 * @param indexkey 索引名
	 * @param period ピリオド(.)文字列
	 */
	void addCallWrite(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"writeACMRecord\" USING ACM-FILE-IDENT");
		add("                                 ACM-RECORD");
		add("                                 ACM-STATUS-ALL" + period);
		if (invalid.size() > 0) {
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_DUPLICATE_KEY + "\"");
			for (String str : invalid) {
				if (str.trim().length() > 0) {
					add("         " + str.trim());
				}
			}
			if (notinvalid.size() > 0) {
				add("     ELSE");
				for (String str : notinvalid) {
					if (str.trim().length() > 0) {
						add("         " + str.trim());
					}
				}
			}
			add("     END-IF" + period);
		} else {
		}
	}
	/*
	 * (non-Javadoc)
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.CodeGenerator#addCodeGeneratorListener
	 * (k_kim_mg.sa4cob2db.codegen.CodeGeneratorListener)
	 */
	public void addCodeGeneratorListener(CodeGeneratorListener listener) {
		listeners.add(listener);
	}
	/**
	 * セッションの初期化
	 * @param period "."文字列
	 */
	void addInitializeSession(String period) {
		FileInfo nullfile = new DefaultFileInfo();
		// イベント処理
		CodeGeneratorEvent event = new CodeGeneratorEvent(nullfile, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preInitialize(event);
		}
		// ///////////
		addCallInitializeSession(period);
		// イベント処理
		for (CodeGeneratorListener listener : listeners) {
			listener.postInitialize(event);
		}
		// ///////////
	}
	/**
	 * セッションの終了
	 * @param period "."文字列
	 */
	void addTerminateSession(String period) {
		FileInfo nullfile = new DefaultFileInfo();
		// イベント処理
		CodeGeneratorEvent event = new CodeGeneratorEvent(nullfile, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preTerminate(event);
		}
		// ///////////
		addCallTerminateSession(period);
		// イベント処理
		for (CodeGeneratorListener listener : listeners) {
			listener.postTerminate(event);
		}
		// ///////////
	}
	/**
	 * FD句がACM処理されるかどうか
	 * @param text
	 * @return
	 */
	boolean checkFD(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text);
		String prev = "", ident = "";
		while (tokenizer.hasMoreTokens()) {
			ident = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, " ").trim();
			// ident = tokenizer.nextToken().trim();
			if (prev.equalsIgnoreCase("fd")) {
				DefaultFileInfo info = selectnametofile.get(ident);
				if (info != null) {
					// info.recordname = ident;
					// recordnametofile.put(ident, info);
					currentinfo = info;
					return true;
				} else {
					return false;
				}
			} else {
				prev = ident;
			}
		}
		return false;
	}
	/**
	 * バッファの内容をすべて解析してクリアする
	 */
	public void clear() {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			String text = list.get(0);
			owner.generate(text);
			list.remove(0);
		}
	}
	/**
	 * バッファをトークンの集まりに分解する
	 * @return トークンの集合
	 */
	StringTokenizer current2tokenizer() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < currentlist.size(); i++) {
			sb.append(currentlist.get(i));
			sb.append(" ");
		}
		currentlist.clear();
		String str = sb.toString();
		return new StringTokenizer(str);
	}
	/**
	 * 現在処理中のDIVISION
	 * @param text 現在行
	 * @return DIVISION名
	 */
	String getDivision(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text);
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		}
		return null;
	}
	/**
	 * 現在処理中のSECTION
	 * @param text 現在行
	 * @return SECTION名
	 */
	String getSection(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text);
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		}
		return null;
	}
	/**
	 * コピー句を展開するかどうか
	 * @return true 展開する<br/>
	 *         false 展開しない
	 */
	public boolean isExpandCopy() {
		return owner.isExpandCopy();
	}
	/**
	 * 文字列の解析
	 * @param text 行文字列
	 */
	public void parse(String text) {
		if (inCopy && isExpandCopy()) {
			if (Pattern.matches(CobolConsts.PERIOD, text)) {
				whenOnlyPeriod(text);
			} else {
				whenNoMatchAny(text);
			}
		} else {
			if (Pattern.matches(CobolConsts.ACMSTART, text)) {
				whenACMSTART(text);
			} else if (Pattern.matches(CobolConsts.ACMRECNAME, text)) {
				whenACMRecName(text);
			} else if (Pattern.matches(CobolConsts.ACMTRANS, text)) {
				whenACMTransactionIsolation(text);
			} else if (Pattern.matches(CobolConsts.ACMAUTO, text)) {
				whenACMAutoCommit(text);
			} else if (Pattern.matches(CobolConsts.ACMCOMMIT, text)) {
				whenACMCommit(text);
			} else if (Pattern.matches(CobolConsts.ACMROLLBACK, text)) {
				whenACMRollBack(text);
			} else if (Pattern.matches(CobolConsts.COMMENT, text)) {
				add(text); // コメントの場合は追加のみ
			} else if (Pattern.matches(CobolConsts.DIVISION, text)) {
				whenDivision(text);
			} else if (Pattern.matches(CobolConsts.SECTION, text)) {
				whenSection(text);
			} else if (Pattern.matches(CobolConsts.FILECONTROL, text)) {
				whenFileControl(text);
			} else if (Pattern.matches(CobolConsts.SELECT, text)) {
				whenSelect(text);
			} else if (Pattern.matches(CobolConsts.FD, text)) {
				whenFd(text);
			} else if (Pattern.matches(CobolConsts.COPY, text)) {
				whenCopy(text);
			} else if (Pattern.matches(CobolConsts.STORAGE, text)) {
				whenStorage(text);
			} else if (Pattern.matches(CobolConsts.OPEN, text)) {
				whenOpen(text);
			} else if (Pattern.matches(CobolConsts.CLOSE, text)) {
				whenClose(text);
			} else if (Pattern.matches(CobolConsts.READ, text)) {
				whenRead(text);
			} else if (Pattern.matches(CobolConsts.WRITE, text)) {
				whenWrite(text);
			} else if (Pattern.matches(CobolConsts.REWRITE, text)) {
				whenRewrite(text);
			} else if (Pattern.matches(CobolConsts.START, text)) {
				whenStart(text);
			} else if (Pattern.matches(CobolConsts.DELETE, text)) {
				whenDelete(text);
			} else if (Pattern.matches(CobolConsts.ENDREAD, text)) {
				whenEndRead(text);
			} else if (Pattern.matches(CobolConsts.ENDWRITE, text)) {
				whenEndWrite(text);
			} else if (Pattern.matches(CobolConsts.ENDREWRITE, text)) {
				whenEndRewrite(text);
			} else if (Pattern.matches(CobolConsts.ENDSTART, text)) {
				whenEndStart(text);
			} else if (Pattern.matches(CobolConsts.ENDDELETE, text)) {
				whenEndDelete(text);
			} else if (Pattern.matches(CobolConsts.LABEL, text)) {
				whenLabel(text);
			} else if (Pattern.matches(CobolConsts.STOPRUN, text)) {
				whenStopRun(text);
			} else if (Pattern.matches(CobolConsts.STOPRUN, text)) {
				whenStopRun(text);
			} else {
				if (Pattern.matches(CobolConsts.PERIOD, text)) {
					whenOnlyPeriod(text);
				} else {
					whenNoMatchAny(text);
				}
			}
		}
		//
	}
	/**
	 * スタックから回復する
	 */
	void pop() {
		current = stack.pop();
		currentlist = currentlists.pop();
	}
	/**
	 * CLOSE処理
	 * @param period "."文字列
	 */
	void process_close(String period) {
		StringTokenizer tokenizer = current2tokenizer();
		ArrayList<FileInfo> files1 = new ArrayList<FileInfo>();
		ArrayList<String> files2 = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			// String token = tokenizer.nextToken().trim();
			if (token.equalsIgnoreCase("close")) {
				//
			} else {
				FileInfo info = selectnametofile.get(token);
				if (info != null) {
					files1.add(info);
				} else {
					files2.add(token);
				}
			}
		}
		for (int i = 0; i < files1.size(); i++) {
			FileInfo info = files1.get(i);
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preClose(event);
			}
			// ///////////
			addCallClose(info, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postClose(event);
			}
			// ///////////
		}
		for (int i = 0; i < files2.size(); i++) {
			add("     CLOSE " + files2.get(i) + period);
		}
	}
	/**
	 * コピー句の処理
	 */
	void process_copy() {
		inCopy = false;
		owner.callBackCopyStatement(copys);
	}
	/**
	 * DELETE処理
	 * @param period "."文字列
	 */
	void process_delete(String period) {
		ArrayList<String> backup = new ArrayList<String>(currentlist);
		StringTokenizer tokenizer = current2tokenizer();
		FileInfo info = null;
		String sWrite = "", sFname = ""/* , sFrom = "", sRname = "" */;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("delete")) {
				sWrite = token;
			} else {
				if (sWrite.equalsIgnoreCase("delete") && sFname.equalsIgnoreCase("")) {
					sFname = token;
				}
			}
		}
		info = selectnametofile.get(sFname);
		if (info == null) {
			for (int i = 0; i < backup.size(); i++) {
				add(backup.get(i));
			}
			return;
		} else {
			ArrayList<String> invalid = new ArrayList<String>();
			ArrayList<String> notinvalid = new ArrayList<String>();
			int status = 0;
			Pattern p = null;
			Matcher m = null;
			for (int i = 0; i < backup.size(); i++) {
				String row = backup.get(i);
				if (Pattern.matches(CobolConsts.INVALID, row)) {
					status = 3;
					p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
					m = p.matcher(row);
					if (m.find()) {
						invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					} else {
						p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]");
						m = p.matcher(row);
						invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					}
				} else if (Pattern.matches(CobolConsts.NOTINVALID, row)) {
					status = 4;
					p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
					m = p.matcher(row);
					if (m.find()) {
						invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					} else {
						p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]");
						m = p.matcher(row);
						notinvalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					}
				} else if (Pattern.matches(CobolConsts.ENDDELETE, row)) {
					status = 0;
				} else {
					switch (status) {
					case 3:
						invalid.add(row);
						break;
					case 4:
						notinvalid.add(row);
						break;
					}
				}
			}
			//
			add("* ACM Generated Write");
			for (int i = 0; i < backup.size(); i++) {
				add("*" + backup.get(i));
			}
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preDelete(event);
			}
			// ///////////
			addCallDelete(info, invalid, notinvalid, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postDelete(event);
			}
			// ///////////
		}
	}
	/**
	 * FD句の処理
	 */
	void process_fd() {
		//
	}
	/**
	 * OPEN処理
	 * @param period "."文字列
	 */
	void process_open(String period) {
		StringTokenizer tokenizer = current2tokenizer();
		ArrayList<FileInfo> input1 = new ArrayList<FileInfo>();
		ArrayList<FileInfo> output1 = new ArrayList<FileInfo>();
		ArrayList<FileInfo> io1 = new ArrayList<FileInfo>();
		ArrayList<String> input2 = new ArrayList<String>();
		ArrayList<String> output2 = new ArrayList<String>();
		ArrayList<String> io2 = new ArrayList<String>();
		int omode = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("open")) {
			} else if (token.equalsIgnoreCase("input")) {
				omode = CobolConsts.MODE_INPUT;
			} else if (token.equalsIgnoreCase("output") || token.equalsIgnoreCase("extend")) {
				omode = CobolConsts.MODE_OUTPUT;
			} else if (token.equalsIgnoreCase("i-o")) {
				omode = CobolConsts.MODE_IO;
			} else {
				DefaultFileInfo info = selectnametofile.get(token);
				switch (omode) {
				case CobolConsts.MODE_INPUT:
					if (info != null) {
						input1.add(info);
					} else {
						input2.add(token);
					}
					break;
				case CobolConsts.MODE_OUTPUT:
					if (info != null) {
						output1.add(info);
					} else {
						output2.add(token);
					}
					break;
				case CobolConsts.MODE_IO:
					if (info != null) {
						io1.add(info);
					} else {
						io2.add(token);
					}
					break;
				}
			}
		}
		for (int i = 0; i < input1.size(); i++) {
			FileInfo info = input1.get(i);
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenInput(info, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postOpen(event);
			}
			// ///////////
		}
		for (int i = 0; i < output1.size(); i++) {
			FileInfo info = output1.get(i);
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenOutput(info, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postOpen(event);
			}
			// ///////////
		}
		for (int i = 0; i < io1.size(); i++) {
			FileInfo info = io1.get(i);
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenInputOutput(info, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postOpen(event);
			}
			// ///////////
		}
		for (int i = 0; i < input2.size(); i++) {
			add("     OPEN INPUT " + input2.get(i) + period);
		}
		for (int i = 0; i < output2.size(); i++) {
			add("     OPEN OUTPUT " + output2.get(i) + period);
		}
		for (int i = 0; i < io2.size(); i++) {
			add("     OPEN I-O " + io2.get(i) + period);
		}
		currentlist.clear();
	}
	/**
	 * @param period "."文字列
	 */
	void process_read(String period) {
		ArrayList<String> backup = new ArrayList<String>(currentlist);
		StringTokenizer tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		ArrayList<String> atend = new ArrayList<String>();
		ArrayList<String> notatend = new ArrayList<String>();
		ArrayList<String> invalid = new ArrayList<String>();
		ArrayList<String> notinvalid = new ArrayList<String>();
		// String prev[] = { "", "", "", "" };
		String _read = "", _file = "", _next = ""/* , _from = "" */;
		String prev = "", prev2 = "", indexkey = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			// SQLNetServer.DebugPrint(token);
			if (token.equalsIgnoreCase("read")) {
				//
				_read = "read";
			} else if (token.equalsIgnoreCase("next")) {
				//
				_next = "next";
			} else {
				if (_read.equalsIgnoreCase("read") && _file.equalsIgnoreCase("")) {
					_file = token;
					info = selectnametofile.get(_file);
					if (info != null) {
						//
						for (int j = 0; j < backup.size(); j++) {
							add("*" + backup.get(j));
						}
						// break;
					} else {
						for (int j = 0; j < backup.size(); j++) {
							add(backup.get(j));
						}
						return;
					}
				} else {
					if (prev.equalsIgnoreCase("IS") && prev2.equalsIgnoreCase("KEY")) {
						indexkey = token;
					}
				}
			}
			prev2 = prev;
			prev = token;
		}
		int status = 0;
		Pattern p = null;
		Matcher m = null;
		for (int i = 0; i < backup.size(); i++) {
			String row = backup.get(i);
			if (Pattern.matches(CobolConsts.ATEND, row)) {
				status = 1;
				p = Pattern.compile(".*[aA][tT]\\s*[eE][nN][dD]");
				m = p.matcher(row);
				atend.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
			} else if (Pattern.matches(CobolConsts.NOTATEND, row)) {
				status = 2;
				p = Pattern.compile(".*[nN][oO][tT]\\s[aA][tT]\\s*[eE][nN][dD]");
				m = p.matcher(row);
				notatend.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
			} else if (Pattern.matches(CobolConsts.INVALID, row)) {
				status = 3;
				p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
				m = p.matcher(row);
				if (m.find()) {
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				} else {
					p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]");
					m = p.matcher(row);
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				}
			} else if (Pattern.matches(CobolConsts.NOTINVALID, row)) {
				status = 4;
				p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
				m = p.matcher(row);
				if (m.find()) {
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				} else {
					p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]");
					m = p.matcher(row);
					notinvalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				}
			} else if (Pattern.matches(CobolConsts.ENDREAD, row)) {
				status = 0;
			} else {
				switch (status) {
				case 1:
					atend.add(row);
					break;
				case 2:
					notatend.add(row);
					break;
				case 3:
					invalid.add(row);
					break;
				case 4:
					notinvalid.add(row);
					break;
				}
			}
		}
		if (info.acessMode == CobolConsts.ORG_SEQUENTIAL || _next.equalsIgnoreCase("next")) {
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preReadNext(event);
			}
			// ///////////
			// Read Next 処理
			addCallReadNext(info, atend, notatend, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postReadNext(event);
			}
			// ///////////
		} else {
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preMoveRead(event);
			}
			// ///////////
			// read 処理
			addCallRead(info, invalid, notinvalid, indexkey, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postMoveRead(event);
			}
			// ///////////
		}
	}
	/**
	 * 更新処理
	 * @param period "."文字列
	 */
	void process_rewrite(String period) {
		ArrayList<String> backup = new ArrayList<String>(currentlist);
		StringTokenizer tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		String sWrite = "", sFname = "", sFrom = "", sRname = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("rewrite")) {
				sWrite = token;
			} else if (token.equalsIgnoreCase("from")) {
				sFrom = token;
			} else {
				if (sWrite.equalsIgnoreCase("rewrite") && sFname.equalsIgnoreCase("")) {
					sFname = token;
				} else if (sFrom.equalsIgnoreCase("from") && sRname.equalsIgnoreCase("")) {
					sFrom = token;
				}
			}
		}
		info = recordnametofile.get(sFname);
		if (info == null) {
			for (int i = 0; i < backup.size(); i++) {
				add(backup.get(i));
			}
			return;
		} else {
			ArrayList<String> invalid = new ArrayList<String>();
			ArrayList<String> notinvalid = new ArrayList<String>();
			int status = 0;
			Pattern p = null;
			Matcher m = null;
			for (int i = 0; i < backup.size(); i++) {
				String row = backup.get(i);
				if (Pattern.matches(CobolConsts.INVALID, row)) {
					status = 3;
					p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
					m = p.matcher(row);
					if (m.find()) {
						invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					} else {
						p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]");
						m = p.matcher(row);
						invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					}
				} else if (Pattern.matches(CobolConsts.NOTINVALID, row)) {
					status = 4;
					p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
					m = p.matcher(row);
					if (m.find()) {
						invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					} else {
						p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]");
						m = p.matcher(row);
						notinvalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
					}
				} else if (Pattern.matches(CobolConsts.ENDREWRITE, row)) {
					status = 0;
				} else {
					switch (status) {
					case 3:
						invalid.add(row);
						break;
					case 4:
						notinvalid.add(row);
						break;
					}
				}
			}
			//
			add("* ACM Generated Write");
			for (int i = 0; i < backup.size(); i++) {
				add("*" + backup.get(i));
			}
			// イベント処理
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preRewrite(event);
			}
			// ///////////
			addCallRewrite(info, invalid, notinvalid, period);
			// イベント処理
			for (CodeGeneratorListener listener : listeners) {
				listener.postRewrite(event);
			}
			// ///////////
		}
	}
	/**
	 * 
	 */
	void process_select() {
		// ArrayList backup = new ArrayList();
		StringTokenizer tokenizer = current2tokenizer();
		int stat = 0;
		DefaultFileInfo info = new DefaultFileInfo();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("select")) {
				stat = 1;
			} else if (token.equalsIgnoreCase("access")) {
				stat = 2;
			} else if (token.equalsIgnoreCase("assign")) {
				stat = 4;
			} else if (token.equalsIgnoreCase("status")) {
				stat = 3;
			} else if (token.equalsIgnoreCase("sequential") || token.equalsIgnoreCase("sequential.")) {
				if (stat == 2) {
					info.acessMode = CobolConsts.ORG_SEQUENTIAL;
				}
			} else if (token.equalsIgnoreCase("dynamic") || token.equalsIgnoreCase("dynamic.")) {
				if (stat == 2) {
					info.acessMode = CobolConsts.ORG_DYNAMIC;
				}
			} else if (token.equalsIgnoreCase("random") || token.equalsIgnoreCase("random.")) {
				if (stat == 2) {
					info.acessMode = CobolConsts.ORG_RANDOM;
				}
			} else if (token.equalsIgnoreCase("is")) {
				// Do Nothing
			} else if (token.equalsIgnoreCase("to")) {
				// Do Nothing
			} else {
				if (stat == 1) {
					info.selectName = token;
					stat = 0;
				} else if (stat == 4) {
					String filename = token;
					info.fileName = filename.replaceAll("\"", "").trim();
					stat = 0;
				} else if (stat == 3) {
					info.status = token;
					stat = 0;
				}
			}
		}
		if (info.recordName == null) {
			if (acmRecName != null) {
				info.recordName = acmRecName;
				recordnametofile.put(info.recordName, info);
			}
		}
		selectnametofile.put(info.selectName, info);
		filenametofile.put(info.fileName, info);
		currentlist.clear();
		// SQLNetServer.DebugPrint("File-Converted:" + info);
	}
	/**
	 * @param period "."文字列
	 */
	void process_start(String period) {
		ArrayList<String> backup = new ArrayList<String>(currentlist);
		StringTokenizer tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		ArrayList<String> atend = new ArrayList<String>();
		ArrayList<String> notatend = new ArrayList<String>();
		ArrayList<String> invalid = new ArrayList<String>();
		ArrayList<String> notinvalid = new ArrayList<String>();
		String _start = "", _file = "";
		String prev = "", prev2 = "", prev3 = "", prev4 = "", prev5 = "", prev6 = "", prev7 = "";
		String indexkey = null;
		String startmodetext = "IS-EQUAL-TO";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("START")) {
				_start = "START";
			} else {
				if (_start.equalsIgnoreCase("START") && _file.equalsIgnoreCase("")) {
					_file = token;
					info = selectnametofile.get(_file);
					if (info != null) {
						//
						for (int j = 0; j < backup.size(); j++) {
							add("*" + backup.get(j));
						}
					} else {
						for (int j = 0; j < backup.size(); j++) {
							add(backup.get(j));
						}
						return;
					}
				} else {
					if ((prev.equalsIgnoreCase("TO") && prev2.equalsIgnoreCase("EQUAL") && prev3.equalsIgnoreCase("IS") && prev4.equalsIgnoreCase("KEY")) || (prev.equalsIgnoreCase("=") && prev2.equalsIgnoreCase("IS") && prev3.equalsIgnoreCase("KEY"))) {
						// startmode = CobolFile.IS_EQUAL_TO;
						startmodetext = "IS-EQUAL-TO";
						indexkey = token;
					} else if ((prev.equalsIgnoreCase("THAN") && prev2.equalsIgnoreCase("GRATER") && prev3.equalsIgnoreCase("IS") && prev4.equalsIgnoreCase("KEY"))
					        || (prev.equalsIgnoreCase(">") && prev2.equalsIgnoreCase("IS") && prev3.equalsIgnoreCase("KEY"))) {
						// startmode = CobolFile.IS_GREATER_THAN;
						startmodetext = "IS-GREATER-THAN";
						indexkey = token;
					} else if ((prev.equalsIgnoreCase("THAN") && prev2.equalsIgnoreCase("LESS") && prev3.equalsIgnoreCase("NOT") && prev4.equalsIgnoreCase("IS") && prev5.equalsIgnoreCase("KEY"))
					        || (prev.equalsIgnoreCase("<") && prev2.equalsIgnoreCase("NOT") && prev3.equalsIgnoreCase("IS") && prev4.equalsIgnoreCase("KEY"))
					        || (prev.equalsIgnoreCase("TO") && prev2.equalsIgnoreCase("EQUAL") && prev3.equalsIgnoreCase("OR") && prev4.equalsIgnoreCase("THAN") && prev5.equalsIgnoreCase("GREATER") && prev6.equalsIgnoreCase("IS") && prev7
					                .equalsIgnoreCase("KEY")) || (prev.equalsIgnoreCase(">=") && prev2.equalsIgnoreCase("IS") && prev3.equalsIgnoreCase("KEY"))) {
						// startmode = CobolFile.IS_GREATER_THAN_OR_EQUAL_TO;
						startmodetext = "IS-GREATER-THAN-OR-EQUAL-TO";
						indexkey = token;
					}
				}
			}
			prev7 = prev6;
			prev6 = prev5;
			prev5 = prev4;
			prev4 = prev3;
			prev3 = prev2;
			prev2 = prev;
			prev = token;
		}
		int status = 0;
		Pattern p = null;
		Matcher m = null;
		for (int i = 0; i < backup.size(); i++) {
			String row = backup.get(i);
			if (Pattern.matches(CobolConsts.INVALID, row)) {
				status = 3;
				p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]\\s[kK][eE][yY]");
				m = p.matcher(row);
				if (m.find()) {
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				} else {
					p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]");
					m = p.matcher(row);
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				}
			} else if (Pattern.matches(CobolConsts.NOTINVALID, row)) {
				status = 4;
				p = Pattern.compile(".*[nN][oO][tT]\\s[iI][nN][vV][aA][lL][iI][dD]\\s[kK][eE][yY]");
				m = p.matcher(row);
				if (m.find()) {
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				} else {
					p = Pattern.compile(".*[nN][oO][tT]\\s[iI][nN][vV][aA][lL][iI][dD]");
					m = p.matcher(row);
					notinvalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				}
			} else if (Pattern.matches(CobolConsts.ENDSTART, row)) {
				status = 0;
			} else {
				switch (status) {
				case 1:
					atend.add(row);
					break;
				case 2:
					notatend.add(row);
					break;
				case 3:
					invalid.add(row);
					break;
				case 4:
					notinvalid.add(row);
					break;
				}
			}
		}
		// イベント処理
		CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preStart(event);
		}
		// ///////////
		addCallStart(info, startmodetext, invalid, notinvalid, indexkey, period);
		// イベント処理
		for (CodeGeneratorListener listener : listeners) {
			listener.postStart(event);
		}
		// ///////////
	}
	/**
	 * WRITEを処理する
	 * @param period 行末のピリオド
	 */
	void process_write(String period) {
		ArrayList<String> backup = new ArrayList<String>(currentlist);
		StringTokenizer tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		String sWrite = "", sFname = "", sFrom = "", sRname = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("write")) {
				sWrite = token;
			} else if (token.equalsIgnoreCase("from")) {
				sFrom = token;
			} else {
				if (sWrite.equalsIgnoreCase("write") && sFname.equalsIgnoreCase("")) {
					sFname = token;
				} else if (sFrom.equalsIgnoreCase("from") && sRname.equalsIgnoreCase("")) {
					sFrom = token;
				}
			}
		}
		info = recordnametofile.get(sFname);
		if (info == null) {
			for (int i = 0; i < backup.size(); i++) {
				add(backup.get(i));
			}
			return;
		}
		ArrayList<String> invalid = new ArrayList<String>();
		ArrayList<String> notinvalid = new ArrayList<String>();
		int status = 0;
		Pattern p = null;
		Matcher m = null;
		for (int i = 0; i < backup.size(); i++) {
			String row = backup.get(i);
			if (Pattern.matches(CobolConsts.INVALID, row)) {
				status = 3;
				p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
				m = p.matcher(row);
				if (m.find()) {
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				} else {
					p = Pattern.compile(".*[iI][nN][vV][aA][lL][iI][dD]");
					m = p.matcher(row);
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				}
			} else if (Pattern.matches(CobolConsts.NOTINVALID, row)) {
				status = 4;
				p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]\\s*[kK][eE][yY]");
				m = p.matcher(row);
				if (m.find()) {
					invalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				} else {
					p = Pattern.compile(".*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD]");
					m = p.matcher(row);
					notinvalid.add(m.replaceFirst("").replaceAll(CobolConsts.PERIOD_ROW, "").trim());
				}
			} else if (Pattern.matches(CobolConsts.ENDWRITE, row)) {
				status = 0;
			} else {
				switch (status) {
				case 3:
					invalid.add(row);
					break;
				case 4:
					notinvalid.add(row);
					break;
				}
			}
		}
		//
		add("* ACM Generated Write");
		for (int i = 0; i < backup.size(); i++) {
			add("*" + backup.get(i));
		}
		// イベント処理
		CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preWrite(event);
		}
		// ///////////
		addCallWrite(info, invalid, notinvalid, period);
		// イベント処理
		for (CodeGeneratorListener listener : listeners) {
			listener.postWrite(event);
		}
		// ///////////
	}
	/**
	 * 現在の編集内容を対比する
	 */
	void push() {
		if (current != null)
			stack.push(current);
		currentlists.push(currentlist);
	}
	/**
	 * コミットモードを設定する<br/>
	 * 無条件で行末にピリオドを設定することに注意
	 * @param text true/falseを含む文字列
	 */
	void whenACMAutoCommit(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		add("    MOVE \"" + option + "\" TO ACM-OPTION.");
		add("    CALL \"setACMCommitMode\" USING ACM-OPTION");
		add("                                    ACM-STATUS-ALL.");
	}
	/**
	 * トランザクションをコミットする
	 * @param text 行文字列<br/>
	 *            行文字列が".(ピリオド)"で終わっていたら行末にピリオドをつける
	 */
	void whenACMCommit(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text) ? "." : "");
		// イベント処理
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preCommit(event);
			}
		}
		addCallCommit(period);
		// イベント処理
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postCommit(event);
			}
		}
	}
	/**
	 * レコード名の設定
	 * @param text レコード名を含む行
	 */
	void whenACMRecName(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		acmRecName = text.substring(indexOfEqual);
	}
	/**
	 * トランザクションをロールバックする
	 * @param text 行文字列<br/>
	 *            行文字列が".(ピリオド)"で終わっていたら行末にピリオドをつける
	 */
	void whenACMRollBack(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text) ? "." : "");
		// イベント処理
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preRollback(event);
			}
		}
		addCallRollback(period);
		// イベント処理
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postRollback(event);
			}
		}
	}
	/**
	 * ACMファイル指示の開始
	 * @param text 開始行
	 */
	void whenACMSTART(String text) {
		// ACM開始コメント
		add("*" + text);
		inACM = true;
		acmRecName = null;
	}
	/**
	 * トランザクションレベルの設定<br/>
	 * 無条件で行末にピリオドを設定することに注意
	 * @param text レベル文字列を含んだ行
	 */
	void whenACMTransactionIsolation(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		add("    MOVE \"" + option + "\" TO ACM-OPTION.");
		add("    CALL \"setACMTransMode\" USING ACM-OPTION");
		add("                                   ACM-STATUS-ALL.");
	}
	/**
	 * CLOSE命令
	 * @param text キーワードを含む行
	 */
	void whenClose(String text) {
		push();
		// ファイルクローズ
		current = CobolConsts.CLOSE;
		add("*" + text);
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_close(".");
			pop();
		}
		// ------↓ここからpush!-------/
	}
	/**
	 * コピー句がきたとき
	 * @param text コピー句を含む行
	 */
	void whenCopy(String text) {
		if (isExpandCopy()) {
			add("*" + text);
			inCopy = true;
			copys.clear();
			copys.add(text);
			if (Pattern.matches(CobolConsts.PERIOD, text)) {
				process_copy();
			}
		} else {
			// コピー句
			if (inFD) {
				add("*" + text);
				add_fd(text);
			} else {
				add(text);
			}
		}
	}
	/**
	 * DELETE句
	 * @param text キーワードを含む行
	 */
	void whenDelete(String text) {
		push();
		// ファイルオープン
		current = CobolConsts.DELETE;
		add("*" + text);
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_delete(".");
			pop();
		}
	}
	/**
	 * DIVISION句を含む行
	 * @param text キーワードを含む行
	 */
	void whenDivision(String text) {
		add(text);
		// ファイルコントロールの終了
		if (current == CobolConsts.FD) {
			process_fd();
			inFD = false;
		}
		// ディビジョン
		current = CobolConsts.DIVISION;
		division = getDivision(text);
		if (division.equalsIgnoreCase("data")) {
			if (hasNonACM) {
				add(" FILE SECTION.");
			}
		} else if (division.equalsIgnoreCase("procedure")) {
			proceduresection = true;
		}
	}
	/**
	 * END DELETE句
	 * @param text キーワードを含む行
	 */
	void whenEndDelete(String text) {
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_delete(".");
		} else {
			process_delete("");
		}
		pop();
		level--;
	}
	/**
	 * @param text
	 */
	void whenEndRead(String text) {
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_read(".");
		} else {
			process_read("");
		}
		pop();
		level--;
	}
	/**
	 * END WRITE対応
	 * @param text 行文字列
	 */
	void whenEndRewrite(String text) {
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_rewrite(".");
		} else {
			process_rewrite("");
		}
		pop();
		level--;
	}
	/**
	 * @param text
	 */
	void whenEndStart(String text) {
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_start(".");
		} else {
			process_start("");
		}
		pop();
		level--;
	}
	/**
	 * @param text
	 */
	void whenEndWrite(String text) {
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_write(".");
		} else {
			process_write("");
		}
		pop();
		level--;
	}
	/**
	 * @param text
	 */
	void whenFd(String text) {
		process_fd();
		inFD = false;
		// レコード宣言
		current = CobolConsts.FD;
		if (checkFD(text)) {
			add_fd("*ACM Genrated File Record");
			inFD = true;
			add("*" + text);
		} else {
			add(text);
			inFD = false;
		}
	}
	/**
	 * @param text
	 */
	void whenFileControl(String text) {
		add("*" + text);
		// ファイルコントロールの始まり
		current = CobolConsts.FILECONTROL;
	}
	/**
	 * @param text
	 */
	void whenLabel(String text) {
		// ラベル（気にしない？）
		add(text);
		if (proceduresection) {
			label++;
			if (label == 1 & !initialized) {
				addInitializeSession(".");
				initialized = true;
			}
		}
	}
	/**
	 * @param text
	 */
	void whenNoMatchAny(String text) {
		if (inCopy && isExpandCopy()) {
			copys.add(text);
		} else {
			//
			if (current == CobolConsts.SELECT) {
				if (inACM) {
					add("*" + text);
					currentlist.add(text);
				} else {
					add(text);
				}
			} else if (current == CobolConsts.FD) {
				if (inFD) {
					add("*" + text);
					add_fd(text);
				} else {
					add(text);
				}
			} else if (current == CobolConsts.OPEN) {
				add("*" + text);
				currentlist.add(text);
			} else if (current == CobolConsts.CLOSE) {
				add("*" + text);
				currentlist.add(text);
			} else if (current == CobolConsts.READ || current == CobolConsts.WRITE || current == CobolConsts.REWRITE || current == CobolConsts.DELETE || current == CobolConsts.START) {
				// add("*" + text);
				currentlist.add(text);
			} else {
				add(text);
			}
		}
	}
	/**
	 * @param text
	 */
	void whenOnlyPeriod(String text) {
		if (inCopy && isExpandCopy()) {
			copys.add(text);
			process_copy();
		} else {
			if (current == CobolConsts.SELECT) {
				if (inACM) {
					add("*" + text);
					currentlist.add(text);
					process_select();
					inACM = false;
				} else {
					add(text);
				}
			} else if (current == CobolConsts.FD) {
				if (inFD) {
					add("*" + text);
					add_fd(text);
				} else {
					add(text);
				}
			} else if (current == CobolConsts.OPEN) {
				add("*" + text);
				currentlist.add(text);
				process_open(".");
				pop();
			} else if (current == CobolConsts.CLOSE) {
				add("*" + text);
				currentlist.add(text);
				process_close(".");
				pop();
			} else if (current == CobolConsts.READ) {
				// add("*" + text);
				currentlist.add(text);
				level--;
				process_read(".");
				pop();
			} else if (current == CobolConsts.WRITE) {
				// add("*" + text);
				currentlist.add(text);
				level--;
				process_write(".");
				pop();
			} else if (current == CobolConsts.START) {
				// add("*" + text);
				currentlist.add(text);
				level--;
				process_start(".");
				pop();
			} else {
				add(text);
				clear();
			}
		}
	}
	/**
	 * @param text
	 */
	void whenOpen(String text) {
		push();
		// ファイルオープン
		current = CobolConsts.OPEN;
		add("*" + text);
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_open(".");
			pop();
		}
	}
	/**
	 * @param text
	 */
	void whenRead(String text) {
		push();
		//
		current = CobolConsts.READ;
		level++;
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			level--;
			process_read(".");
			pop();
		}
	}
	/**
	 * @param text
	 */
	void whenRewrite(String text) {
		//
		push();
		//
		current = CobolConsts.REWRITE;
		level++;
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			level--;
			process_rewrite(".");
			pop();
		}
	}
	/**
	 * @param text
	 */
	void whenSection(String text) {
		// ファイルコントロールの終了
		if (current == CobolConsts.FD) {
			process_fd();
			inFD = false;
		}
		// セクション
		current = CobolConsts.SECTION;
		section = getSection(text);
		if (section.equalsIgnoreCase("working-storage")) {
			add(text);
			add("*ACM Generated Contraints");
			if (isExpandCopy()) {
				parse(" COPY \"" + CobolConsts.ACMCONSTS_FILE + "\".");
			} else {
				add(" COPY \"" + CobolConsts.ACMCONSTS_FILE + "\".");
			}
			for (int i = 0; i < fdlist.size(); i++) {
				add(fdlist.get(i));
			}
			fdlist.clear();
		} else if (section.equalsIgnoreCase("input-output")) {
			add("*" + text);
		} else if (section.equalsIgnoreCase("file")) {
			add("*" + text);
		} else if (division.equalsIgnoreCase("procedure") & !initialized) {
			addInitializeSession(".");
			initialized = true;
		} else {
			add(text);
		}
	}
	/**
	 * @param text
	 */
	void whenSelect(String text) {
		// セレクト（ファイルアサインの始まり）
		current = CobolConsts.SELECT;
		if (inACM) {
			add("*" + text);
			currentlist.add(text);
		} else {
			if (!hasNonACM) {
				add(" INPUT-OUTPUT SECTION.");
				add(" FILE-CONTROL.");
			}
			hasNonACM = true;
			add(text);
		}
	}
	/**
	 * @param text
	 */
	void whenStart(String text) {
		//
		push();
		//
		current = CobolConsts.START;
		level++;
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			level--;
			process_start(".");
			pop();
		}
	}
	/**
	 * @param text
	 */
	void whenStopRun(String text) {
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			addTerminateSession(".");
		} else {
			this.addTerminateSession("");
		}
		add(text);
	}
	/**
	 * @param text
	 */
	void whenStorage(String text) {
		// 作業領域宣言
		if (inFD) {
			add("*" + text);
			add_fd(text);
		} else {
			add(text);
		}
	}
	/**
	 * @param text
	 */
	void whenWrite(String text) {
		//
		push();
		//
		current = CobolConsts.WRITE;
		level++;
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			level--;
			process_write(".");
			pop();
		}
	}
}