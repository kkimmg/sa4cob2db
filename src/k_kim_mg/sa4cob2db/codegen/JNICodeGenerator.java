/**
 * 
 */
package k_kim_mg.sa4cob2db.codegen;
import java.util.ArrayList;
import k_kim_mg.sa4cob2db.FileStatus;
/**
 * Convert file access code to call statement
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
/**
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class JNICodeGenerator extends TCPCodeGenerator {
	/**
	 * Constructor
	 * 
	 * @param owner GeneratorOwner
	 */
	public JNICodeGenerator(GeneratorOwner owner) {
		super(owner);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addACMAutoCommit(java.lang
	 * .String, java.lang.String)
	 */
	void addACMAutoCommit(String option, String period) {
		add("     MOVE \"" + option + "\" TO ACM-OPTION" + period);
		add("     CALL \"setJNICommitMode\" USING ACM-OPTION");
		add("                                     ACM-STATUS-ALL" + period);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addACMTransactionIsolation
	 * (java.lang.String, java.lang.String)
	 */
	void addACMTransactionIsolation(String option, String period) {
		add("     MOVE \"" + option + "\" TO ACM-OPTION" + period);
		add("     CALL \"setJNITransMode\" USING ACM-OPTION");
		add("                                    ACM-STATUS-ALL" + period);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addAssignFiles(java.lang.
	 * String)
	 */
	void addAssignFiles(String period) {
		for (FileInfo info : getSelectnametofile().values()) {
			add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
			add("     CALL \"assignJNIFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallClose(k_kim_mg.sa4cob2db
	 * .codegen.FileInfo, java.lang.String)
	 */
	void addCallClose(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"closeJNIFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallCommit(java.lang.String
	 * )
	 */
	void addCallCommit(String period) {
		add("    CALL \"commitJNISession\" USING ACM-STATUS-ALL" + period);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallDelete(k_kim_mg.sa4cob2db
	 * .codegen.FileInfo, java.util.ArrayList, java.util.ArrayList,
	 * java.lang.String)
	 */
	void addCallDelete(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"deleteJNIRecord\" USING ACM-FILE-IDENT");
		add("                                  ACM-RECORD");
		add("                                  ACM-STATUS-ALL" + period);
		// ///////////
		if (invalid.size() > 0) {
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_KEY_NOT_EXISTS + "\"");
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
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallInitializeSession(
	 * java.lang.String)
	 */
	void addCallInitializeSession(String period) {
		add("     CALL \"libJNIClient\"" + period);
		add("     CALL \"initializeJNISessionEnv\" USING ACM-STATUS-ALL" + period);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallOpenInput(k_kim_mg
	 * .sa4cob2db.codegen.FileInfo, java.lang.String)
	 */
	void addCallOpenInput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallOpenInputOutput(k_kim_mg
	 * .sa4cob2db.codegen.FileInfo, java.lang.String)
	 */
	void addCallOpenInputOutput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallOpenOutput(k_kim_mg
	 * .sa4cob2db.codegen.FileInfo, java.lang.String)
	 */
	void addCallOpenOutput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallRead(k_kim_mg.sa4cob2db
	 * .codegen.FileInfo, java.util.ArrayList, java.util.ArrayList,
	 * java.lang.String, java.lang.String)
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
		add("     IF  ACM-STATUS-CODE = \"" + FileStatus.STATUS_SUCCESS + "\"");
		add("         MOVE ACM-RECORD TO " + info.getRecordName());
		add("     END-IF" + period);
		if (invalid.size() > 0) {
			// Invalid
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_KEY_NOT_EXISTS + "\"");
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallReadNext(k_kim_mg.
	 * sa4cob2db.codegen.FileInfo, java.util.ArrayList, java.util.ArrayList,
	 * java.lang.String)
	 */
	void addCallReadNext(FileInfo info, ArrayList<String> atend, ArrayList<String> notatend, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"readNextJNIRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		add("                                ACM-RECORD");
		add("                                ACM-STATUS-ALL" + period);
		add("     IF  ACM-STATUS-CODE = \"" + FileStatus.STATUS_SUCCESS + "\"");
		add("         MOVE ACM-RECORD TO " + info.getRecordName());
		add("     END-IF" + period);
		if (atend.size() > 0) {
			// At End
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_END_OF_FILE + "\"");
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallRewrite(k_kim_mg.sa4cob2db
	 * .codegen.FileInfo, java.util.ArrayList, java.util.ArrayList,
	 * java.lang.String)
	 */
	void addCallRewrite(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"rewriteJNIRecord\" USING ACM-FILE-IDENT");
		add("                                   ACM-RECORD");
		add("                                   ACM-STATUS-ALL" + period);
		// ///////////
		if (invalid.size() > 0) {
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_KEY_NOT_EXISTS + "\"");
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
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallRollback(java.lang
	 * .String)
	 */
	void addCallRollback(String period) {
		add("    CALL \"rollbackJNISession\" USING ACM-STATUS-ALL" + period);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallStart(k_kim_mg.sa4cob2db
	 * .codegen.FileInfo, java.lang.String, java.util.ArrayList,
	 * java.util.ArrayList, java.lang.String, java.lang.String)
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
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_KEY_NOT_EXISTS + "\"");
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallTerminateSession(java
	 * .lang.String)
	 */
	void addCallTerminateSession(String period) {
		add("     CALL  \"terminateJNISession\" USING ACM-STATUS-ALL" + period);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addCallWrite(k_kim_mg.sa4cob2db
	 * .codegen.FileInfo, java.util.ArrayList, java.util.ArrayList,
	 * java.lang.String)
	 */
	void addCallWrite(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"writeJNIRecord\" USING ACM-FILE-IDENT");
		add("                                 ACM-RECORD");
		add("                                 ACM-STATUS-ALL" + period);
		if (invalid.size() > 0) {
			add("     IF ACM-STATUS-CODE = \"" + FileStatus.STATUS_KEY_EXISTS + "\"");
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
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addGetACMOption(java.lang
	 * .String, java.lang.String)
	 */
	void addGetACMOption(String name, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     CALL \"getJNIOption\" USING ACM-OPTION-NAME");
			add("                                 ACM-STATUS-ALL" + period);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addGetACMOption(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	void addGetACMOption(String name, String value, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     CALL \"getJNIOption\" USING ACM-OPTION-NAME");
			add("                                 ACM-STATUS-ALL" + period);
			add("     MOVE ACM-OPTION-VALUE TO " + value + period);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addSetACMOption(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	void addSetACMOption(String name, String value, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     MOVE " + value + " TO ACM-OPTION-VALUE" + period);
			add("     CALL \"setJNIOption\" USING ACM-OPTION-NAME");
			add("                                 ACM-OPTION-VALUE");
			add("                                 ACM-STATUS-ALL" + period);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addSetACMOptionFromEnv(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	void addSetACMOptionFromEnv(String name, String value, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     MOVE " + value + " TO ACM-OPTION-VALUE" + period);
			add("     CALL \"setJNIOptionFromEnv\" USING ACM-OPTION-NAME");
			add("                                 ACM-OPTION-VALUE");
			add("                                 ACM-STATUS-ALL" + period);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.TCPCodeGenerator#addSetMaxLength(int,
	 * java.lang.String)
	 */
	void addSetMaxLength(int value, String period) {
		if (value > 0) {
			add("     MOVE \"" + value + "\" TO ACM-OPTION-VALUE" + period);
			add("     CALL \"setJNIMaxLength\" USING ACM-OPTION-VALUE");
			add("                                    ACM-STATUS-ALL" + period);
		}
	}
}
