/**
 * 
 */
package k_kim_mg.sa4cob2db.codegen;
import java.util.ArrayList;

import k_kim_mg.sa4cob2db.FileStatus;
/**
 * 引き渡されたソースコードを蓄積し、変換した結果をオーナーに返す機能(JNIバージョン)
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class JNICodeGenerator extends TCPCodeGenerator {
	/**
	 * コンストラクタ
	 * @param owner 呼び元
	 */
	public JNICodeGenerator(GeneratorOwner owner) {
		super(owner);
	}
	/**
	 * CLOSE処理
	 * @param period "."文字列
	 */
	void addCallClose(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"closeJNIFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
	}
	/**
	 * COMMITの追加
	 * @param period ピリオド(.)文字列
	 */
	void addCallCommit(String period) {
		add("    CALL \"commitJNISession\" USING ACM-STATUS-ALL" + period);
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
		add("     CALL \"deleteJNIRecord\" USING ACM-FILE-IDENT");
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
		add("     CALL \"initializeJNISessionEnv\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * OPEN INPUT 処理
	 * @param info ファイルの情報
	 * @param period ピリオド(.)文字列
	 */
	void addCallOpenInput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"assignJNIFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		add("     CALL \"openJNIFile\"   USING ACM-FILE-IDENT");
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
		add("     CALL \"assignJNIFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		add("     CALL \"openJNIFile\"   USING ACM-FILE-IDENT");
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
		add("     CALL \"assignJNIFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		add("     CALL \"openJNIFile\"   USING ACM-FILE-IDENT");
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
		add("     CALL \"moveReadJNIRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		if (indexkey == null) {
			// �����꡼��
			add("                                ACM-RECORD");
		} else {
			// ����ˤ��꡼��
			add("                                ACM-RECORD");
			add("                                ACM-INDEX-NAME");
		}
		add("                                ACM-STATUS-ALL" + period);
		add("     IF  ACM-STATUS-CODE = \"" + FileStatus.STATUS_OK + "\"");
		add("         MOVE ACM-RECORD TO " + info.getRecordName());
		add("     END-IF" + period);
		if (invalid.size() > 0) {
			// Invalid ����
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_INVALID_KEY + "\"");
			for (String str : invalid) {
				if (str.trim().length() > 0) {
					add("         " + str.trim());
				}
			}
			if (notinvalid.size() > 0) {
				// Not Invalid ����
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
		add("     CALL \"readNextJNIRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		add("                                ACM-RECORD");
		add("                                ACM-STATUS-ALL" + period);
		add("     IF  ACM-STATUS-CODE = \"" + FileStatus.STATUS_OK + "\"");
		add("         MOVE ACM-RECORD TO " + info.getRecordName());
		add("     END-IF" + period);
		if (atend.size() > 0) {
			// At End ����
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_EOF + "\"");
			for (int i = 0; i < atend.size(); i++) {
				add("         " + atend.get(i));
			}
			if (notatend.size() > 0) {
				// Not At End ����
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
		add("     CALL \"rewriteJNIRecord\" USING ACM-FILE-IDENT");
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
		add("    CALL \"rollbackJNISession\" USING ACM-STATUS-ALL" + period);
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
			add("     CALL \"startJNIRecord\" USING ACM-FILE-IDENT");
			add("                                ACM-RECORD");
		} else {
			add("     MOVE  \"" + indexkey + "\"  TO  ACM-INDEX-NAME" + period);
			add("     CALL \"startJNIRecordWith\" USING ACM-FILE-IDENT");
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
		add("     CALL  \"terminateJNISession\" USING ACM-STATUS-ALL" + period);
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
		add("     CALL \"writeJNIRecord\" USING ACM-FILE-IDENT");
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
	/**
	 * コミットモードを設定する<br/>
	 * 無条件で行末にピリオドを設定することに注意
	 * @param text true/falseを含む文字列
	 */
	void whenACMAutoCommit(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		add("    MOVE \"" + option + "\" TO ACM-OPTION.");
		add("    CALL \"setJNICommitMode\" USING ACM-OPTION");
		add("                                    ACM-STATUS-ALL.");
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
		add("    CALL \"setJNITransMode\" USING ACM-OPTION");
		add("                                   ACM-STATUS-ALL.");
	}
}
