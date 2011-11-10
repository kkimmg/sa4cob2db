/**
 * 
 */
package k_kim_mg.sa4cob2db.codegen;
import java.util.ArrayList;

import k_kim_mg.sa4cob2db.FileStatus;
/**
 * Convert file access code to call statement
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class JNICodeGenerator extends TCPCodeGenerator {
	/**
	 * Constructor
	 * @param owner GeneratorOwner
	 */
	public JNICodeGenerator(GeneratorOwner owner) {
		super(owner);
	}
	/**
	 * CLOSE
	 * @param period Period Character
	 */
	void addCallClose(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"closeJNIFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
	}
	/**
	 * ADD CALL COMMIT
	 * @param period Period Character
	 */
	void addCallCommit(String period) {
		add("    CALL \"commitJNISession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * DELETE
	 * @param valid Lines that runs when file access is valid. 
	 * @param invalid Lines that runs when file access is invalid.
	 * @param period Period Character
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
	 * add InitializeSession
	 * @param period Period Character
	 */
	void addCallInitializeSession(String period) {
		add("     CALL \"libJNIClient\"" + period);
		add("     CALL \"initializeJNISessionEnv\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * OPEN INPUT
	 * @param info FileInfo Object
	 * @param period Period Character
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
	 * OPEN I-O
	 * @param info FileInfo Object
	 * @param period Period Character
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
	 * OPEN OUTPUT
	 * @param info FileInfo Object
	 * @param period Period Character
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
	 * READ
	 * @param info FileInfo Object
	 * @param invalid valid Lines that runs when file access is invalid.
	 * @param notinvalid valid Lines that runs when file access is valid.
	 * @param indexkey index name
	 * @param period Period Character
	 */
	void addCallRead(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String indexkey, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD" + period);
		add("     CALL \"moveReadJNIRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		if (indexkey == null) {
			add("                                ACM-RECORD");
		} else {
			add("                                ACM-RECORD");
			add("                                ACM-INDEX-NAME");
		}
		add("                                ACM-STATUS-ALL" + period);
		add("     IF  ACM-STATUS-CODE = \"" + FileStatus.STATUS_OK + "\"");
		add("         MOVE ACM-RECORD TO " + info.getRecordName());
		add("     END-IF" + period);
		if (invalid.size() > 0) {
			// Invalid
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_INVALID_KEY + "\"");
			for (String str : invalid) {
				if (str.trim().length() > 0) {
					add("         " + str.trim());
				}
			}
			if (notinvalid.size() > 0) {
				// Not Invalid
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
	 * READ Next
	 * @param info FileInfo Object
	 * @param atend row lines at EOF.
	 * @param notatend row lines not at EOF.
	 * @param indexkey index name
	 * @param period Period Character
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
			// At End
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_EOF + "\"");
			for (int i = 0; i < atend.size(); i++) {
				add("         " + atend.get(i));
			}
			if (notatend.size() > 0) {
				// Not At End
				add("     ELSE");
				for (int i = 0; i < notatend.size(); i++) {
					add("        " + notatend.get(i));
				}
			}
			add("     END-IF" + period);
		}
	}
	/**
	 * Rewrite
	 * @param info FileInfo Object
	 * @param invalid valid Lines that runs when file access is invalid.
	 * @param notinvalid valid Lines that runs when file access is valid.
	 * @param indexkey index name
	 * @param period Period Character
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
	 * add ROLLBACK
	 * @param period Period Character
	 */
	void addCallRollback(String period) {
		add("    CALL \"rollbackJNISession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * START
	 * @param info FileInfo Object
	 * @param invalid valid Lines that runs when file access is invalid.
	 * @param notinvalid valid Lines that runs when file access is valid.
	 * @param indexkey index name
	 * @param period Period Character
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
	 * add TermincateSession
	 * @param period Period Character
	 */
	void addCallTerminateSession(String period) {
		add("     CALL  \"terminateJNISession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * WRITE
	 * @param info FileInfo Object
	 * @param invalid valid Lines that runs when file access is invalid.
	 * @param notinvalid valid Lines that runs when file access is valid.
	 * @param indexkey index name
	 * @param period Period Character
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
	 * add commit mode<br/>
	 * Note that setting the period to end unconditional
	 * @param text string includes "=" true/false 
	 */
	void whenACMAutoCommit(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		add("    MOVE \"" + option + "\" TO ACM-OPTION.");
		add("    CALL \"setJNICommitMode\" USING ACM-OPTION");
		add("                                    ACM-STATUS-ALL.");
	}
	/**
	 * set transaction level<br/>
	 * Note that setting the period to end unconditional
	 * @param text string includes transaction level
	 */
	void whenACMTransactionIsolation(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		add("    MOVE \"" + option + "\" TO ACM-OPTION.");
		add("    CALL \"setJNITransMode\" USING ACM-OPTION");
		add("                                   ACM-STATUS-ALL.");
	}
}
