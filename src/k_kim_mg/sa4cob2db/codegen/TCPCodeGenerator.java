package k_kim_mg.sa4cob2db.codegen;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import k_kim_mg.sa4cob2db.FileStatus;

/**
 * Convert file access code to call statement
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class TCPCodeGenerator implements CodeGenerator {
	/**
	 * object that store infomation of SELECT Statement
	 */
	static class DefaultFileInfo implements FileInfo {
		/** access mode */
		int acessMode;
		/**
		 * filename(Externalfilename) <br/>
		 * To assign (on the file system) filename
		 */
		String fileName;
		/**
		 * RecordName<br/>
		 * level1 area name
		 */
		String recordName;
		/**
		 * filename(Internalfilename) <br/>
		 * first token from select statement
		 */
		String selectName;
		/**
		 * file status <br/>
		 * area that store file status(File Status [is] XXXXXX)
		 */
		String status;

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getAcessMode()
		 */
		public int getAcessMode() {
			return acessMode;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getFileName()
		 */
		public String getFileName() {
			return fileName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getRecordName()
		 */
		public String getRecordName() {
			return recordName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getSelectName()
		 */
		public String getSelectName() {
			return selectName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see k_kim_mg.sa4cob2db.codegen.FileInfo#getStatus()
		 */
		public String getStatus() {
			return status;
		}

		/**
		 * set access mode
		 * 
		 * @param acessmode access mode
		 * @see k_kim_mg.sa4cob2db.codegen.CobolConsts#MODE_INPUT
		 * @see k_kim_mg.sa4cob2db.codegen.CobolConsts#MODE_OUTPUT
		 * @see k_kim_mg.sa4cob2db.codegen.CobolConsts#MODE_IO
		 * @see k_kim_mg.sa4cob2db.codegen.CobolConsts#MODE_EXTEND
		 */
		public void setAcessMode(int acessmode) {
			this.acessMode = acessmode;
		}

		/**
		 * set filename
		 * 
		 * @param filename external filename to set
		 */
		public void setFileName(String filename) {
			this.fileName = filename;
		}

		/**
		 * set recordname
		 * 
		 * @param recordname RecordName to set
		 */
		public void setRecordName(String recordname) {
			this.recordName = recordname;
		}

		/**
		 * set filename
		 * 
		 * @param selectname internal filename to set
		 */
		public void setSelectName(String selectname) {
			this.selectName = selectname;
		}

		/**
		 * set file status area name
		 * 
		 * @param status area name to set
		 */
		public void setStatus(String status) {
			this.status = status;
		}
	}

	private String acmRecName = null;
	private String acmAssignName = null;
	private ArrayList<String> copies = new ArrayList<String>();
	private String current;
	private DefaultFileInfo currentinfo = null;
	private ArrayList<String> currentlist = new ArrayList<String>();
	private Deque<ArrayList<String>> currentlists = new LinkedList<ArrayList<String>>();
	private String division = null;
	private final FileInfo dummyInfo = new DefaultFileInfo();
	private ArrayList<String> fdlist = new ArrayList<String>();
	private Hashtable<String, DefaultFileInfo> filenametofile = new Hashtable<String, DefaultFileInfo>();
	private boolean hasNonACM = false;
	private boolean inACM = false;
	private boolean inCopy = false;
	private boolean inFD = false;
	private boolean initialized = false;
	private int label = 0;
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<CodeGeneratorListener> listeners = new ArrayList<CodeGeneratorListener>();
	private GeneratorOwner owner;
	private boolean proceduresection = false;
	private Hashtable<String, DefaultFileInfo> recordnametofile = new Hashtable<String, DefaultFileInfo>();
	private String section = null;
	private Hashtable<String, DefaultFileInfo> selectnametofile = new Hashtable<String, DefaultFileInfo>();
	private Deque<String> stack = new LinkedList<String>();
	private boolean inCommentOut = false;
	private boolean inInsert = false;

	/**
	 * Constructor
	 * 
	 * @param owner owner
	 */
	public TCPCodeGenerator(GeneratorOwner owner) {
		this.owner = owner;
	}

	/**
	 * add text
	 * 
	 * @param text text to add
	 */
	void add(String text) {
		if (!getOwner().isOutfreeformat() && text.length() > 66) {
			String trimed = text.trim();
			addTrimed(trimed);
		} else {
			list.add(text);
		}
	}

	/**
	 * add fd statement
	 * 
	 * @param text text of fd statement
	 */
	void add_fd(String text) {
		fdlist.add(text);
		if (Pattern.matches(CobolConsts.STORAGE, text)) {
			CobolTokens tokenizer = new CobolTokens(text);
			boolean check = false;
			while (tokenizer.hasNext()) {
				String recname = tokenizer.next().trim();
				if (!check) {
					check = true;
				} else {
					currentinfo.recordName = recname;
					recordnametofile.put(recname, currentinfo);
					break;
				}
			}
		}
	}

	/**
	 * Add row text to inner list.
	 * 
	 * @param text row text.
	 */
	void add2CurrentList(String text) {
		// currentlist_.add(text);
		if (!getOwner().isOutfreeformat() && text.length() > 66) {
			currentlist.add(text.substring(0, 66));
		} else {
			currentlist.add(text);
		}
	}

	/**
	 * Add row text to inner list.
	 * 
	 * @param text row text.
	 */
	void add2LastList(String text) {
		// currentlist_.add(text);
		int sz = currentlist.size();
		if (sz > 0) {
			String last = currentlist.remove(sz - 1);
			if (!getOwner().isOutfreeformat() && text.length() > 66) {
				last = last + text.substring(1, 66);
			} else {
				last = last + text.substring(1);
			}
			currentlist.add(last);
		} else {
			add2CurrentList(text);
		}
	}
	
	/**
	 * add commit mode<br/>
	 * Note that setting the period to end unconditional
	 * 
	 * @param text string includes "=" true/false
	 */
	void addACMAutoCommit(String option, String period) {
		add("     MOVE \"" + option + "\" TO ACM-OPTION" + period);
		add("     CALL \"setTCPCommitMode\" USING ACM-OPTION");
		add("                                     ACM-STATUS-ALL" + period);
	}

	/**
	 * set transaction level<br/>
	 * Note that setting the period to end unconditional
	 * 
	 * @param text string includes transaction level
	 */
	void addACMTransactionIsolation(String option, String period) {
		add("     MOVE \"" + option + "\" TO ACM-OPTION" + period);
		add("     CALL \"setTCPTransMode\" USING ACM-OPTION");
		add("                                    ACM-STATUS-ALL" + period);
	}

	/**
	 * add File Assigns
	 * 
	 * @param period "." or ""
	 */
	void addAssignFiles(String period) {
		for (FileInfo info : getSelectnametofile().values()) {
			add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
			add("     CALL \"assignACMFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
		}
	}

	/**
	 * CLOSE
	 * 
	 * @param period "." or ""
	 */
	void addCallClose(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"closeACMFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
	}

	/**
	 * COMMIT
	 * 
	 * @param period "." or ""
	 */
	void addCallCommit(String period) {
		add("     CALL \"commitACMSession\" USING ACM-STATUS-ALL" + period);
	}

	/**
	 * DELETE
	 * 
	 * @param valid Lines that runs when file access is valid.
	 * @param invalid Lines that runs when file access is invalid.
	 * @param period "." or ""
	 */
	void addCallDelete(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"deleteACMRecord\" USING ACM-FILE-IDENT");
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
			// } else {
		}
	}

	/**
	 * add InitializeSession
	 * 
	 * @param period "." or ""
	 */
	void addCallInitializeSession(String period) {
		add("     CALL \"libACMClient\"" + period);
		add("     CALL \"initializeSessionEnv\" USING ACM-STATUS-ALL" + period);
	}

	/**
	 * OPEN INPUT
	 * 
	 * @param info file information
	 * @param period "." or ""
	 */
	void addCallOpenInput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
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
	 * OPEN I-O
	 * 
	 * @param info file information
	 * @param period "." or ""
	 */
	void addCallOpenInputOutput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
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
	 * OPEN OUTPUT
	 * 
	 * @param info file information
	 * @param period "." or ""
	 */
	void addCallOpenOutput(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
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
	 * POP
	 * 
	 * @param period "." or ""
	 */
	void addCallPop(String period) {
		add("     CALL \"popACMFileList\" USING ACM-STATUS-ALL" + period);
	}

	/**
	 * PUSH
	 * 
	 * @param period "." or ""
	 */
	void addCallPush(String period) {
		add("     CALL \"pushACMFileList\" USING ACM-STATUS-ALL" + period);
	}

	/**
	 * READ
	 * 
	 * @param info file information
	 * @param invalid Lines that runs when file access is valid.
	 * @param notinvalid Lines that runs when file access is invalid.
	 * @param indexkey index name
	 * @param period "." or ""
	 */
	void addCallRead(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String indexkey, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD" + period);
		add("     CALL \"moveReadACMRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		if (indexkey == null) {
			// by key
			add("                                ACM-RECORD");
		} else {
			// by index
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

	/**
	 * READ
	 * 
	 * @param info file information
	 * @param atend Lines that runs when file at end.
	 * @param notatend Lines that runs when file not at end.
	 * @param indexkey index name
	 * @param period "." or ""
	 */
	void addCallReadNext(FileInfo info, ArrayList<String> atend, ArrayList<String> notatend, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"readNextACMRecord\" USING ");
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

	/**
	 * Rewrite
	 * 
	 * @param info file information
	 * @param invalid Lines that runs when file access is valid.
	 * @param notinvalid Lines that runs when file access is invalid.
	 * @param indexkey index name
	 * @param period "." or ""
	 */
	void addCallRewrite(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"rewriteACMRecord\" USING ACM-FILE-IDENT");
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
			// } else {
		}
	}

	/**
	 * add ROLLBACK
	 * 
	 * @param period "." or ""
	 */
	void addCallRollback(String period) {
		add("     CALL \"rollbackACMSession\" USING ACM-STATUS-ALL" + period);
	}

	/**
	 * START
	 * 
	 * @param info file information
	 * @param invalid Lines that runs when file access is valid.
	 * @param notinvalid Lines that runs when file access is invalid.
	 * @param indexkey index name
	 * @param period "." or ""
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
			// } else {
		}
	}

	/**
	 * add TermincateSession
	 * 
	 * @param period "." or ""
	 */
	void addCallTerminateSession(String period) {
		add("     CALL  \"terminateSession\" USING ACM-STATUS-ALL" + period);
	}

	/**
	 * WRITE
	 * 
	 * @param info file information
	 * @param invalid Lines that runs when file access is valid.
	 * @param notinvalid Lines that runs when file access is invalid.
	 * @param indexkey index name
	 * @param period "." or ""
	 */
	void addCallWrite(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD");
		add("     CALL \"writeACMRecord\" USING ACM-FILE-IDENT");
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
			// } else {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.CodeGenerator#addCodeGeneratorListener
	 * (k_kim_mg.sa4cob2db.codegen.CodeGeneratorListener)
	 */
	public void addCodeGeneratorListener(CodeGeneratorListener listener) {
		listeners.add(listener);
	}

	/**
	 * Add text to list directly.
	 * 
	 * @param text text to add
	 */
	void addDirectly(String text) {
		list.add(text);
	}

	/**
	 * add "setACMOption" function
	 * 
	 * @param name option name
	 * @param value option value
	 * @param period "." or ""
	 */
	void addGetACMOption(String name, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     CALL \"getACMOption\" USING ACM-OPTION-NAME");
			add("                                 ACM-OPTION-VALUE");
			add("                                 ACM-STATUS-ALL" + period);
		}
	}

	/**
	 * add "setACMOption" function
	 * 
	 * @param name option name
	 * @param value option value
	 * @param period "." or ""
	 */
	void addGetACMOption(String name, String value, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     CALL \"getACMOption\" USING ACM-OPTION-NAME");
			add("                                 ACM-OPTION-VALUE");
			add("                                 ACM-STATUS-ALL" + period);
			add("     MOVE ACM-OPTION-VALUE TO " + value + period);
		}
	}

	/**
	 * add initialize session
	 * 
	 * @param period "." or ""
	 */
	void addInitializeSession(String period) {
		if (!getOwner().isSubprogram()) {
			FileInfo nullfile = new DefaultFileInfo();
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(nullfile, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preInitialize(event);
			}
			// ///////////
			addCallInitializeSession(period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postInitialize(event);
			}
			// ///////////
		}
		addAssignFiles(period);
	}

	/**
	 * add "setACMOption" function
	 * 
	 * @param name option name
	 * @param value option value
	 * @param period "." or ""
	 */
	void addSetACMOption(String name, String value, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     MOVE " + value + " TO ACM-OPTION-VALUE" + period);
			add("     CALL \"setACMOption\" USING ACM-OPTION-NAME");
			add("                                 ACM-OPTION-VALUE");
			add("                                 ACM-STATUS-ALL" + period);
		}
	}

	/**
	 * add "setACMOptionFromEnv" function
	 * 
	 * @param name option name
	 * @param value environment value name
	 * @param period "." or ""
	 */
	void addSetACMOptionFromEnv(String name, String value, String period) {
		if (name != null) {
			add("     MOVE " + name + " TO ACM-OPTION-NAME" + period);
			add("     MOVE " + value + " TO ACM-OPTION-VALUE" + period);
			add("     CALL \"setACMOptionFromEnv\" USING ACM-OPTION-NAME");
			add("                                 ACM-OPTION-VALUE");
			add("                                 ACM-STATUS-ALL" + period);
		}
	}

	/**
	 * add "setACMLength" function
	 * 
	 * @param value new length
	 * @param period "." or ""
	 */
	void addSetMaxLength(int value, String period) {
		if (value > 0) {
			add("     MOVE \"" + value + "\" TO ACM-OPTION-VALUE" + period);
			add("     CALL \"setACMMaxLength\" USING ACM-OPTION-VALUE");
			add("                                    ACM-STATUS-ALL" + period);
		}
	}

	/**
	 * add terminate session
	 * 
	 * @param period "." or ""
	 */
	void addTerminateSession(String period) {
		if (!getOwner().isSubprogram()) {
			FileInfo nullfile = new DefaultFileInfo();
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(nullfile, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preTerminate(event);
			}
			// ///////////
			addCallTerminateSession(period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postTerminate(event);
			}
		}
		// ///////////
	}

	/**
	 * add text.
	 * 
	 * @param trimed trimed line
	 */
	void addTrimed(String trimed) {
		CobolTokens tokens = new CobolTokens(trimed);
		while (tokens.hasNext()) {
			String token = tokens.next();
			if (token.length() < 66) {
				add(" " + token);
			} else {
				boolean dq = (token.startsWith("\""));
				int idx = (dq ? 65 : 66);
				String first = token.substring(0, idx - 1) + (dq ? "\"" : "");
				add(first);
				String rest = token.substring(idx);
				boolean cont = true;
				while (cont) {
					boolean endDq = rest.endsWith("\"");
					idx = (dq ? (endDq ? 64 : 65) : 66);
					if (rest.length() < idx) {
						idx = rest.length();
						cont = false;
					}
					first = rest.substring(0, idx - 1);
					add(" " + (dq ? "\"" : "") + first + (endDq ? "\"" : ""));
					if (cont) {
						rest = rest.substring(idx);
					}
				}
			}
		}
	}

	/**
	 * Call 'addSetACMOption'
	 * 
	 * @param name option name
	 * @param value option value
	 * @param period "."
	 */
	private void callAddSetACMOption(String name, String value, String period) {
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preSetOption(event);
			}
		}
		addSetACMOption(name, value, period);
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postSetOption(event);
			}
		}
	}

	/**
	 * Call 'addSetACMOption'
	 * 
	 * @param name option name
	 * @param value option value
	 * @param period "."
	 */
	private void callAddSetACMOptionFromEnv(String name, String value, String period) {
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preSetOption(event);
			}
		}
		addSetACMOptionFromEnv(name, value, period);
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postSetOption(event);
			}
		}
	}

	/**
	 * is this FD statement be processed?
	 * 
	 * @param text FD statement
	 * @return true yes<br/>
	 *         false no
	 */
	boolean checkFD(String text) {
		CobolTokens tokenizer = new CobolTokens(text);
		String prev = "";
		String ident = "";
		while (tokenizer.hasNext()) {
			ident = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, " ").trim();
			if (prev.equalsIgnoreCase("fd")) {
				DefaultFileInfo info = selectnametofile.get(ident);
				if (info != null) {
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
	 * Clear Current List.
	 */
	void clearCurrentList() {
		currentlist.clear();
	}

	/**
	 * Comment Out Row
	 * 
	 * @param text logical row
	 */
	void commentOut(String text) {
		if (!getOwner().isDontComment()) {
			String right = (text.length() > 1 ? text.substring(1) : "");
			addDirectly("*" + right);
		}
	}

	/**
	 * Copy current list.
	 * 
	 * @return copy of current list.
	 */
	ArrayList<String> copyCurrentList() {
		return new ArrayList<String>(currentlist);
	}

	/**
	 * create CobolTokens from buffer
	 * 
	 * @return CobolTokens
	 */
	CobolTokens current2tokenizer() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < currentlist.size(); i++) {
			sb.append(currentlist.get(i));
			sb.append(" ");
		}
		currentlist.clear();
		String str = sb.toString();
		return new CobolTokens(str);
	}

	/**
	 * write back and clear buffer
	 */
	public void flush() {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			String text = list.get(0);
			owner.generate(text);
			list.remove(0);
		}
	}

	/**
	 * Current DIVISION
	 * 
	 * @param text Current Row
	 * @return DIVISION Name
	 */
	String getDivision(String text) {
		CobolTokens tokenizer = new CobolTokens(text);
		if (tokenizer.hasNext()) {
			return tokenizer.next();
		}
		return null;
	}

	/**
	 * file list
	 * 
	 * @return file list(filename to file)
	 */
	public Hashtable<String, DefaultFileInfo> getFilenametofile() {
		return filenametofile;
	}

	/**
	 * Get GeneratorOwner
	 * 
	 * @return GeneratorOwner
	 */
	public GeneratorOwner getOwner() {
		return owner;
	}

	/**
	 * file list
	 * 
	 * @return file list(recordname to file)
	 */
	public Hashtable<String, DefaultFileInfo> getRecordnametofile() {
		return recordnametofile;
	}

	/**
	 * Current SECTION
	 * 
	 * @param text Current Row
	 * @return SECTION Name
	 */
	String getSection(String text) {
		String[] tokens = text.trim().split("\\s");
		if (tokens.length > 0) {
			return tokens[0];
		}
		return null;
	}

	/**
	 * file name
	 * 
	 * @return file name (selectname to file)
	 */
	public Hashtable<String, DefaultFileInfo> getSelectnametofile() {
		return selectnametofile;
	}

	/**
	 * Insert Row
	 * 
	 * @param text logical row
	 */
	void insertComment(String text) {
		String right = (text.length() > 1 ? text.substring(1) : "");
		add(" " + right);
	}

	/**
	 * Expand Copy Statement?
	 * 
	 * @return true yes<br/>
	 *         false no
	 */
	public boolean isExpandCopy() {
		return owner.isExpandCopy();
	}

	/**
	 * Should the word be ignored?
	 * 
	 * @param token word/token
	 * @return true: yes <br>
	 *         false: no
	 */
	boolean isIgnoredWord(String token) {
		boolean ret = false;
		ArrayList<String> list = CobolConsts.getIGNORE();
		for (String work : list) {
			if (token.trim().equalsIgnoreCase(work.trim())) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * Comment outing...
	 * 
	 * @return in/out
	 */
	public boolean isInCommentOut() {
		return inCommentOut;
	}

	/**
	 * Inserting...
	 * 
	 * @return in/out
	 */
	public boolean isInInsert() {
		return inInsert;
	}

	/**
	 * Parse Text
	 * 
	 * @param text logical row
	 */
	public void parse(String text) {
		if (isInCommentOut()) {
			if (Pattern.matches(CobolConsts.ACMCOMMENTEND, text)) {
				whenCommentEnd(text);
			} else {
				commentOut(text);
			}
		} else if (inCopy && isExpandCopy()) {
			if (isPeriodLine(text)) {
				whenOnlyPeriod(text);
			} else {
				whenNoMatchAny(text);
			}
		} else if (isInInsert()) {
			if (Pattern.matches(CobolConsts.ACMINSERTEND, text)) {
				whenInsertEnd(text);
			} else {
				String right = (text.length() > 1 ? text.substring(1) : "");
				whenNoMatchAny(" " + right);
			}
		} else {
			if (Pattern.matches(CobolConsts.ACMSTART, text)) {
				whenACMSTART(text);
			} else if (Pattern.matches(CobolConsts.ACMRECNAME, text)) {
				whenACMRecName(text);
			} else if (Pattern.matches(CobolConsts.ACMASSIGNNAME, text)) {
				whenACMAssignName(text);
			} else if (Pattern.matches(CobolConsts.ACMTRANS, text)) {
				whenACMTransactionIsolation(text);
			} else if (Pattern.matches(CobolConsts.ACMAUTO, text)) {
				whenACMAutoCommit(text);
			} else if (Pattern.matches(CobolConsts.ACMCOMMIT, text)) {
				whenACMCommit(text);
			} else if (Pattern.matches(CobolConsts.ACMROLLBACK, text)) {
				whenACMRollBack(text);
			} else if (Pattern.matches(CobolConsts.ACMGETOPTION, text)) {
				whenACMGetOption(text);
			} else if (Pattern.matches(CobolConsts.ACMSETOPTION, text)) {
				whenACMSetOption(text);
			} else if (Pattern.matches(CobolConsts.ACMSETLENGTH, text)) {
				whenACMSetLength(text);
			} else if (Pattern.matches(CobolConsts.ACMCOMMENTSTART, text)) {
				whenCommentStart(text);
			} else if (Pattern.matches(CobolConsts.ACMCOMMENTEND, text)) {
				whenCommentEnd(text);
			} else if (Pattern.matches(CobolConsts.ACMINSERTSTART, text)) {
				whenInsertStart(text);
			} else if (Pattern.matches(CobolConsts.ACMINSERTEND, text)) {
				whenInsertEnd(text);
			} else if (Pattern.matches(CobolConsts.COMMENT, text)) {
				addDirectly(text);
			} else if (Pattern.matches(CobolConsts.HYPHEN, text)) {
				whenHypen(text);
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
			} else if (Pattern.matches(CobolConsts.CALL, text)) {
				whenCall(text);
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
			} else if (Pattern.matches(CobolConsts.ENDCALL, text)) {
				whenEndCall(text);
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
			} else if (Pattern.matches(CobolConsts.EXITPROGRAM, text)) {
				whenExitProgram(text);
			} else {
				if (isPeriodLine(text)) {
					whenOnlyPeriod(text);
				} else {
					whenNoMatchAny(text);
				}
			}
		}
		//
	}

	/**
	 * pop from stack
	 */
	void pop() {
		current = stack.pop();
		currentlist = currentlists.pop();
	}

	/**
	 * CALL
	 * 
	 * @param period "." or ""
	 */
	void process_call(String period) {
		addCallPop(period);
	}

	/**
	 * CLOSE
	 * 
	 * @param period "." or ""
	 */
	void process_close(String period) {
		CobolTokens tokenizer = current2tokenizer();
		ArrayList<FileInfo> files1 = new ArrayList<FileInfo>();
		ArrayList<String> files2 = new ArrayList<String>();
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("close") || token.equals(".") || token.length() == 0) {
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
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preClose(event);
			}
			// ///////////
			addCallClose(info, period);
			// event
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
	 * process copy statement
	 */
	void process_copy() {
		inCopy = false;
		owner.callBackCopyStatement(copies);
	}

	/**
	 * DELETE
	 * 
	 * @param period "." or ""
	 */
	void process_delete(String period) {
		ArrayList<String> backup = copyCurrentList();
		CobolTokens tokenizer = current2tokenizer();
		FileInfo info = null;
		String sWrite = "";
		String sFname = "";
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
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
			commentOut(" ACM Generated Delete");
			for (int i = 0; i < backup.size(); i++) {
				commentOut(backup.get(i));
			}
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preDelete(event);
			}
			// ///////////
			addCallDelete(info, invalid, notinvalid, period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postDelete(event);
			}
			// ///////////
		}
	}

	/**
	 * 
	 */
	void process_fd() {
		//
	}

	/**
	 * OPEN
	 * 
	 * @param period "." or ""
	 */
	void process_open(String period) {
		CobolTokens tokenizer = current2tokenizer();
		ArrayList<FileInfo> input1 = new ArrayList<FileInfo>();
		ArrayList<FileInfo> output1 = new ArrayList<FileInfo>();
		ArrayList<FileInfo> io1 = new ArrayList<FileInfo>();
		ArrayList<String> input2 = new ArrayList<String>();
		ArrayList<String> output2 = new ArrayList<String>();
		ArrayList<String> io2 = new ArrayList<String>();
		int omode = 0;
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("open") || token.equals(".") || token.length() == 0) {
				// Do Nothing
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
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenInput(info, period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postOpen(event);
			}
			// ///////////
		}
		for (int i = 0; i < output1.size(); i++) {
			FileInfo info = output1.get(i);
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenOutput(info, period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postOpen(event);
			}
			// ///////////
		}
		for (int i = 0; i < io1.size(); i++) {
			FileInfo info = io1.get(i);
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenInputOutput(info, period);
			// event
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
		clearCurrentList();
	}

	/**
	 * READ
	 * 
	 * @param period "." or ""
	 */
	void process_read(String period) {
		ArrayList<String> backup = copyCurrentList();
		CobolTokens tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		ArrayList<String> atend = new ArrayList<String>();
		ArrayList<String> notatend = new ArrayList<String>();
		ArrayList<String> invalid = new ArrayList<String>();
		ArrayList<String> notinvalid = new ArrayList<String>();
		// String prev[] = { "", "", "", "" };
		String _read = "";
		String _file = "";
		String _next = "";
		String prev = "";
		String prev2 = "";
		String indexkey = null;
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
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
							commentOut(backup.get(j));
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
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preReadNext(event);
			}
			// ///////////
			// Read Next
			addCallReadNext(info, atend, notatend, period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postReadNext(event);
			}
			// ///////////
		} else {
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preMoveRead(event);
			}
			// ///////////
			// read
			addCallRead(info, invalid, notinvalid, indexkey, period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postMoveRead(event);
			}
			// ///////////
		}
	}

	/**
	 * REWRITE
	 * 
	 * @param period "." or ""
	 */
	void process_rewrite(String period) {
		ArrayList<String> backup = copyCurrentList();
		CobolTokens tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		String sWrite = "";
		String sFname = "";
		String sFrom = "";
		String sRname = "";
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
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
			commentOut(" ACM Generated Write");
			for (int i = 0; i < backup.size(); i++) {
				commentOut(backup.get(i));
			}
			// event
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preRewrite(event);
			}
			// ///////////
			addCallRewrite(info, invalid, notinvalid, period);
			// event
			for (CodeGeneratorListener listener : listeners) {
				listener.postRewrite(event);
			}
			// ///////////
		}
	}

	/**
	 * SELECT STATEMENT
	 */
	void process_select() {
		// ArrayList backup = new ArrayList();
		CobolTokens tokenizer = current2tokenizer();
		int stat = 0;
		DefaultFileInfo info = new DefaultFileInfo();
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
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
			} else if (isIgnoredWord(token)) {
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
		if (acmAssignName != null) {
			info.fileName = acmAssignName;
		}
		if (acmRecName != null) {
			info.recordName = acmRecName;
		}
		recordnametofile.put(info.recordName, info);
		selectnametofile.put(info.selectName, info);
		filenametofile.put(info.fileName, info);
		clearCurrentList();
		// SQLNetServer.DebugPrint("File-Converted:" + info);
	}

	/**
	 * START
	 * 
	 * @param period "." or ""
	 */
	void process_start(String period) {
		ArrayList<String> backup = copyCurrentList();
		CobolTokens tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		ArrayList<String> atend = new ArrayList<String>();
		ArrayList<String> notatend = new ArrayList<String>();
		ArrayList<String> invalid = new ArrayList<String>();
		ArrayList<String> notinvalid = new ArrayList<String>();
		String _start = "";
		String _file = "";
		String prev = "";
		String prev2 = "";
		String prev3 = "";
		String prev4 = "";
		String prev5 = "";
		String prev6 = "";
		String prev7 = "";
		String indexkey = null;
		String startmodetext = "IS-EQUAL-TO";
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
			if (token.equalsIgnoreCase("START")) {
				_start = "START";
			} else {
				if (_start.equalsIgnoreCase("START") && _file.equalsIgnoreCase("")) {
					_file = token;
					info = selectnametofile.get(_file);
					if (info != null) {
						//
						for (int j = 0; j < backup.size(); j++) {
							commentOut(backup.get(j));
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
					} else if ((prev.equalsIgnoreCase("THAN") && prev2.equalsIgnoreCase("GRATER") && prev3.equalsIgnoreCase("IS") && prev4.equalsIgnoreCase("KEY")) || (prev.equalsIgnoreCase(">") && prev2.equalsIgnoreCase("IS") && prev3.equalsIgnoreCase("KEY"))) {
						// startmode = CobolFile.IS_GREATER_THAN;
						startmodetext = "IS-GREATER-THAN";
						indexkey = token;
					} else if ((prev.equalsIgnoreCase("THAN") && prev2.equalsIgnoreCase("LESS") && prev3.equalsIgnoreCase("NOT") && prev4.equalsIgnoreCase("IS") && prev5.equalsIgnoreCase("KEY")) || (prev.equalsIgnoreCase("<") && prev2.equalsIgnoreCase("NOT") && prev3.equalsIgnoreCase("IS") && prev4.equalsIgnoreCase("KEY")) || (prev.equalsIgnoreCase("TO") && prev2.equalsIgnoreCase("EQUAL") && prev3.equalsIgnoreCase("OR") && prev4.equalsIgnoreCase("THAN") && prev5.equalsIgnoreCase("GREATER") && prev6.equalsIgnoreCase("IS") && prev7.equalsIgnoreCase("KEY")) || (prev.equalsIgnoreCase(">=") && prev2.equalsIgnoreCase("IS") && prev3.equalsIgnoreCase("KEY"))) {
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
		// event
		CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preStart(event);
		}
		// ///////////
		addCallStart(info, startmodetext, invalid, notinvalid, indexkey, period);
		// event
		for (CodeGeneratorListener listener : listeners) {
			listener.postStart(event);
		}
		// ///////////
	}

	/**
	 * WRITE
	 * 
	 * @param period "." or ""
	 */
	void process_write(String period) {
		ArrayList<String> backup = copyCurrentList();
		CobolTokens tokenizer = current2tokenizer();
		DefaultFileInfo info = null;
		String sWrite = "";
		String sFname = "";
		String sFrom = "";
		String sRname = "";
		while (tokenizer.hasNext()) {
			String token = tokenizer.next().replaceAll(CobolConsts.PERIOD_ROW, "");
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
		commentOut(" ACM Generated Write");
		for (int i = 0; i < backup.size(); i++) {
			commentOut(backup.get(i));
		}
		// event
		CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preWrite(event);
		}
		// ///////////
		addCallWrite(info, invalid, notinvalid, period);
		// event
		for (CodeGeneratorListener listener : listeners) {
			listener.postWrite(event);
		}
		// ///////////
	}

	/**
	 * push to stack
	 */
	void push() {
		if (current != null) {
			stack.push(current);
		}
		currentlists.push(currentlist);
	}

	/**
	 * Comment outing...
	 * 
	 * @param inCommentOut in/out
	 */
	public void setInCommentOut(boolean inCommentOut) {
		this.inCommentOut = inCommentOut;
	}

	/**
	 * Inserting
	 * 
	 * @param inInsert in/out
	 */
	public void setInInsert(boolean inInsert) {
		this.inInsert = inInsert;
	}

	/**
	 * Set GeneratorOwner
	 * 
	 * @param owner GeneratorOwner
	 */
	public void setOwner(GeneratorOwner owner) {
		this.owner = owner;
	}

	/**
	 * set AssignName
	 * 
	 * @param text line
	 */
	void whenACMAssignName(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		acmAssignName = text.substring(indexOfEqual);
	}

	/**
	 * set auto commit mode<br/>
	 * add "." to end of line
	 * 
	 * @param text comment row
	 */
	void whenACMAutoCommit(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text.trim()) ? "." : "");
		text = (period.length() == 0 ? text.trim() : text.trim().substring(0, text.length() - 1));
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		addACMAutoCommit(option, period);
	}

	/**
	 * commit transaction
	 * 
	 * @param text line
	 * 
	 */
	void whenACMCommit(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text.trim()) ? "." : "");
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preCommit(event);
			}
		}
		addCallCommit(period);
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postCommit(event);
			}
		}
	}

	/**
	 * set option value<br/>
	 * add "." to end of line
	 * 
	 * @param text comment row
	 */
	void whenACMGetOption(String text) {
		String name = null;
		String value = "";
		String period = (Pattern.matches(CobolConsts.PERIOD, text.trim()) ? "." : "");
		text = (period.length() == 0 ? text.trim() : text.trim().substring(0, text.length() - 1));
		CobolTokens tokenizer = new CobolTokens(text);
		while (tokenizer.hasNext()) {
			String token = tokenizer.next();
			int indexOfEqual = token.indexOf("=") + 1;
			if (Pattern.matches(CobolConsts.NAME_EQUAL, token)) {
				name = token.substring(indexOfEqual);
			} else if (Pattern.matches(CobolConsts.TO_EQUAL, token)) {
				value = token.substring(indexOfEqual);
			}
		}
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preGetOption(event);
			}
		}
		if (value == null || value.trim().length() == 0) {
			addGetACMOption(name, period);
		} else {
			addGetACMOption(name, value, period);
		}
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postGetOption(event);
			}
		}
	}

	/**
	 * set RecordName
	 * 
	 * @param text line
	 */
	void whenACMRecName(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		acmRecName = text.substring(indexOfEqual);
	}

	/**
	 * rollback transaction
	 * 
	 * @param text line
	 * 
	 */
	void whenACMRollBack(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text.trim()) ? "." : "");
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preRollback(event);
			}
		}
		addCallRollback(period);
		// event
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postRollback(event);
			}
		}
	}

	/**
	 * set option value<br/>
	 * add "." to end of line
	 * 
	 * @param text comment row
	 */
	void whenACMSetLength(String text) {
		int value = 0;
		String period = (Pattern.matches(CobolConsts.PERIOD, text.trim()) ? "." : "");
		text = (period.length() == 0 ? text.trim() : text.trim().substring(0, text.length() - 1));
		CobolTokens tokenizer = new CobolTokens(text);
		while (tokenizer.hasNext() && value <= 0) {
			String token = tokenizer.next();
			try {
				value = Integer.parseInt(token);
			} catch (NumberFormatException e) {
				// do nothing
			}
		}
		if (value > 0) {
			// event
			{
				CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
				for (CodeGeneratorListener listener : listeners) {
					listener.preSetLength(event);
				}
			}
			addSetMaxLength(value, period);
			// event
			{
				CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
				for (CodeGeneratorListener listener : listeners) {
					listener.postSetLength(event);
				}
			}
		}
	}

	/**
	 * set option value<br/>
	 * add "." to end of line
	 * 
	 * @param text comment row
	 */
	void whenACMSetOption(String text) {
		boolean isEnv = false;
		String name = null;
		String value = "";
		String period = (Pattern.matches(CobolConsts.PERIOD, text.trim()) ? "." : "");
		text = (period.length() == 0 ? text.trim() : text.trim().substring(0, text.length() - 1));
		CobolTokens tokenizer = new CobolTokens(text);
		while (tokenizer.hasNext()) {
			String token = tokenizer.next();
			int indexOfEqual = token.indexOf("=") + 1;
			if (Pattern.matches(CobolConsts.NAME_EQUAL, token)) {
				name = token.substring(indexOfEqual);
			} else if (Pattern.matches(CobolConsts.FROM_EQUAL, token)) {
				value = token.substring(indexOfEqual);
			} else if (Pattern.matches(CobolConsts.VALUE_EQUAL, token)) {
				value = token.substring(indexOfEqual);
			} else if (Pattern.matches(CobolConsts.ENV_EQUAL, token)) {
				value = token.substring(indexOfEqual);
				isEnv = true;
			}
		}
		if (isEnv) {
			callAddSetACMOptionFromEnv(name, value, period);
		} else {
			callAddSetACMOption(name, value, period);
		}
	}

	/**
	 * start of file define
	 * 
	 * @param text line
	 */
	void whenACMSTART(String text) {
		commentOut(text);
		inACM = true;
		acmRecName = null;
		acmAssignName = null;
	}

	/**
	 * set transaction level
	 * 
	 * @param text line
	 */
	void whenACMTransactionIsolation(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text.trim()) ? "." : "");
		text = (period.length() == 0 ? text.trim() : text.trim().substring(0, text.length() - 1));
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		addACMTransactionIsolation(option, period);
	}

	/**
	 * CALL
	 * 
	 * @param text line
	 */
	void whenCall(String text) {
		push();
		//
		addCallPush((isPeriodLine(text) ? "." : ""));
		add(text);
		//
		current = CobolConsts.CALL;
		if (isPeriodLine(text)) {
			process_call(".");
			pop();
		}
	}

	/**
	 * CLOSE
	 * 
	 * @param text line
	 */
	void whenClose(String text) {
		push();
		current = CobolConsts.CLOSE;
		commentOut(text);
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_close(".");
			pop();
		}
	}

	/**
	 * End of Comment
	 * 
	 * @param text logical row
	 */
	void whenCommentEnd(String text) {
		setInCommentOut(false);
	}

	/**
	 * Add to last of currentlist
	 * @param text logical row
	 */
	void whenHypen(String text) {
		//add2LastList(text);
		if (inCopy && isExpandCopy()) {
			copies.add(text);
		} else {
			//
			if (current == CobolConsts.SELECT) {
				if (inACM) {
					commentOut(text);
					add2LastList(text);
					if (isPeriodLine(text)) {
						whenOnlyPeriod("");
					}
				} else {
					addDirectly(text);
				}
			} else if (current == CobolConsts.FD) {
				if (inFD) {
					commentOut(text);
					add_fd(text);
					if (isPeriodLine(text)) {
						whenOnlyPeriod("");
					}
				} else {
					addDirectly(text);
				}
			} else if (current == CobolConsts.OPEN) {
				commentOut(text);
				add2LastList(text);
				if (isPeriodLine(text)) {
					whenOnlyPeriod("");
				}
			} else if (current == CobolConsts.CLOSE) {
				commentOut(text);
				add2LastList(text);
				if (isPeriodLine(text)) {
					whenOnlyPeriod("");
				}
			} else if (current == CobolConsts.READ || current == CobolConsts.WRITE || current == CobolConsts.REWRITE || current == CobolConsts.DELETE || current == CobolConsts.START) {
				// commentOut(text);
				add2LastList(text);
				if (isPeriodLine(text)) {
					whenOnlyPeriod("");
				}
			} else {
				addDirectly(text);
			}
		}
	}
	
	/**
	 * Start of Comment
	 * 
	 * @param text logical row
	 */
	void whenCommentStart(String text) {
		setInCommentOut(true);
	}

	/**
	 * copy statement
	 * 
	 * @param text line
	 */
	void whenCopy(String text) {
		if (isExpandCopy()) {
			commentOut(text);
			inCopy = true;
			copies.clear();
			copies.add(text);
			if (isPeriodLine(text)) {
				process_copy();
			}
		} else {
			if (inFD) {
				commentOut(text);
				add_fd(text);
			} else {
				add(text);
			}
		}
	}

	/**
	 * DELETE
	 * 
	 * @param text line
	 */
	void whenDelete(String text) {
		push();
		current = CobolConsts.DELETE;
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_delete(".");
			pop();
		}
	}

	/**
	 * DIVISION
	 * 
	 * @param text line
	 */
	void whenDivision(String text) {
		add(text);
		if (current == CobolConsts.FD) {
			process_fd();
			inFD = false;
		}
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
	 * END CALL
	 * 
	 * @param text line
	 */
	void whenEndCall(String text) {
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_call(".");
		} else {
			process_call("");
		}
		pop();
	}

	/**
	 * END DELETE
	 * 
	 * @param text line
	 */
	void whenEndDelete(String text) {
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_delete(".");
		} else {
			process_delete("");
		}
		pop();
	}

	/**
	 * END READ
	 * 
	 * @param text line
	 */
	void whenEndRead(String text) {
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_read(".");
		} else {
			process_read("");
		}
		pop();
	}

	/**
	 * END WRITE
	 * 
	 * @param text line
	 */
	void whenEndRewrite(String text) {
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_rewrite(".");
		} else {
			process_rewrite("");
		}
		pop();
	}

	/**
	 * END START
	 * 
	 * @param text line
	 */
	void whenEndStart(String text) {
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_start(".");
		} else {
			process_start("");
		}
		pop();
	}

	/**
	 * END WRITE
	 * 
	 * @param text line
	 */
	void whenEndWrite(String text) {
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_write(".");
		} else {
			process_write("");
		}
		pop();
	}

	/**
	 * STOP RUN
	 * 
	 * @param text line
	 */
	void whenExitProgram(String text) {
		if (isPeriodLine(text)) {
			addTerminateSession(".");
		} else {
			this.addTerminateSession("");
		}
		add(text);
	}

	/**
	 * FD Statement
	 * 
	 * @param text line
	 */
	void whenFd(String text) {
		process_fd();
		inFD = false;
		current = CobolConsts.FD;
		if (checkFD(text)) {
			inFD = true;
			commentOut(text);
		} else {
			add(text);
			inFD = false;
		}
	}

	/**
	 * FILE CONTROLL
	 * 
	 * @param text line
	 */
	void whenFileControl(String text) {
		commentOut(text);
		current = CobolConsts.FILECONTROL;
	}

	/**
	 * End of Insert
	 * 
	 * @param text logical row
	 */
	void whenInsertEnd(String text) {
		setInInsert(false);
	}

	/**
	 * Start of Insert
	 * 
	 * @param text logical row
	 */
	void whenInsertStart(String text) {
		setInInsert(true);
	}

	/**
	 * LABEL
	 * 
	 * @param text line
	 */
	void whenLabel(String text) {
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
	 * OTHER
	 * 
	 * @param text line
	 */
	void whenNoMatchAny(String text) {
		if (inCopy && isExpandCopy()) {
			copies.add(text);
		} else {
			//
			if (current == CobolConsts.SELECT) {
				if (inACM) {
					commentOut(text);
					add2CurrentList(text);
				} else {
					addDirectly(text);
				}
			} else if (current == CobolConsts.FD) {
				if (inFD) {
					commentOut(text);
					add_fd(text);
				} else {
					addDirectly(text);
				}
			} else if (current == CobolConsts.OPEN) {
				commentOut(text);
				add2CurrentList(text);
			} else if (current == CobolConsts.CLOSE) {
				commentOut(text);
				add2CurrentList(text);
			} else if (current == CobolConsts.READ || current == CobolConsts.WRITE || current == CobolConsts.REWRITE || current == CobolConsts.DELETE || current == CobolConsts.START) {
				// commentOut(text);
				add2CurrentList(text);
			} else {
				addDirectly(text);
			}
		}
	}

	/**
	 * only period line
	 * 
	 * @param text line
	 */
	void whenOnlyPeriod(String text) {
		if (inCopy && isExpandCopy()) {
			copies.add(text);
			process_copy();
		} else {
			if (current == CobolConsts.SELECT) {
				if (inACM) {
					commentOut(text);
					add2CurrentList(text);
					process_select();
					inACM = false;
				} else {
					add(text);
				}
			} else if (current == CobolConsts.FD) {
				if (inFD) {
					commentOut(text);
					add_fd(text);
				} else {
					add(text);
				}
			} else if (current == CobolConsts.OPEN) {
				commentOut(text);
				add2CurrentList(text);
				process_open(".");
				pop();
			} else if (current == CobolConsts.CLOSE) {
				commentOut(text);
				add2CurrentList(text);
				process_close(".");
				pop();
			} else if (current == CobolConsts.READ) {
				// commentOut(text);
				add2CurrentList(text);
				process_read(".");
				pop();
			} else if (current == CobolConsts.WRITE) {
				// commentOut(text);
				add2CurrentList(text);
				process_write(".");
				pop();
			} else if (current == CobolConsts.START) {
				// commentOut(text);
				add2CurrentList(text);
				process_start(".");
				pop();
			} else {
				add(text);
				flush();
			}
		}
	}

	/**
	 * Is Current line ends with period(.)?
	 * @param text text
	 * @return true:ends with period/false: not.
	 */
	boolean isPeriodLine(String text) {
		boolean ret = false;
		if (!getOwner().isOutfreeformat() && text.length() > 66) {
			ret = isPeriodLine(text.substring(0, 65));
		} else {
			if (Pattern.matches(CobolConsts.PERIOD, text.trim())) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * OPEN
	 * 
	 * @param text line
	 */
	void whenOpen(String text) {
		push();
		current = CobolConsts.OPEN;
		commentOut(text);
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_open(".");
			pop();
		}
	}

	/**
	 * READ
	 * 
	 * @param text line
	 */
	void whenRead(String text) {
		push();
		//
		current = CobolConsts.READ;
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_read(".");
			pop();
		}
	}

	/**
	 * REWRITE
	 * 
	 * @param text line
	 */
	void whenRewrite(String text) {
		//
		push();
		//
		current = CobolConsts.REWRITE;
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_rewrite(".");
			pop();
		}
	}

	/**
	 * SECTION
	 * 
	 * @param text line
	 */
	void whenSection(String text) {
		if (current == CobolConsts.FD) {
			process_fd();
			inFD = false;
		}
		current = CobolConsts.SECTION;
		section = getSection(text);
		if (section.equalsIgnoreCase("working-storage")) {
			add(text);
			commentOut("*ACM Generated Contraints");
			if (isExpandCopy()) {
				parse(" COPY \"" + CobolConsts.getACMCONSTS_FILE() + "\".");
			} else {
				add(" COPY \"" + CobolConsts.getACMCONSTS_FILE() + "\".");
			}
			for (int i = 0; i < fdlist.size(); i++) {
				add(fdlist.get(i));
			}
			fdlist.clear();
		} else if (section.equalsIgnoreCase("input-output")) {
			commentOut(text);
		} else if (section.equalsIgnoreCase("file")) {
			commentOut(text);
		} else if (division.equalsIgnoreCase("procedure") & !initialized) {
			add(text);
			addInitializeSession(".");
			initialized = true;
		} else {
			add(text);
		}
	}

	/**
	 * SELECT
	 * 
	 * @param text line
	 */
	void whenSelect(String text) {
		current = CobolConsts.SELECT;
		if (inACM) {
			commentOut(text);
			add2CurrentList(text);
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
	 * START
	 * 
	 * @param text line
	 */
	void whenStart(String text) {
		//
		push();
		//
		current = CobolConsts.START;
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_start(".");
			pop();
		}
	}

	/**
	 * STOP RUN
	 * 
	 * @param text line
	 */
	void whenStopRun(String text) {
		if (isPeriodLine(text)) {
			addTerminateSession(".");
		} else {
			this.addTerminateSession("");
		}
		add(text);
	}

	/**
	 * STORAGE
	 * 
	 * @param text line
	 */
	void whenStorage(String text) {
		if (inFD) {
			commentOut(text);
			add_fd(text);
		} else {
			add(text);
		}
	}

	/**
	 * WRITE
	 * 
	 * @param text line
	 */
	void whenWrite(String text) {
		//
		push();
		//
		current = CobolConsts.WRITE;
		add2CurrentList(text);
		if (isPeriodLine(text)) {
			process_write(".");
			pop();
		}
	}
}