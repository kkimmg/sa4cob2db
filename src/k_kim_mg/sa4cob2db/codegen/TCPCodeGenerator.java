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
 * �����Ϥ��줿�����������ɤ����Ѥ����Ѵ�������̤򥪡��ʡ����֤���ǽ
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class TCPCodeGenerator implements CodeGenerator {
	/**
	 * SELECT��ξ�������򤹤륪�֥�������
	 */
	static class DefaultFileInfo implements FileInfo {
		/** ���������⡼�� */
		int acessMode;
		/**
		 * �ե�����̾�ʳ����ե�����̾���� <br/>
		 * �������󤹤�ʥե����륷���ƥ��Ρ˥ե�����̾
		 */
		String fileName;
		/**
		 * �쥳����̾�� <br/>
		 * FD���01��٥���ΰ�̾
		 */
		String recordName;
		/**
		 * �ե�����̾�������ե�����̾���� <br/>
		 * SELECT��μ��Υȡ�����
		 */
		String selectName;
		/**
		 * �����Ͼ��� <br/>
		 * �����Ͼ��֤򼨤��ΰ�̾(File Status [is] XXXXXX)
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
		 * ���������⡼�ɤ򥻥åȤ���
		 * @param acessmode ���åȤ��륢�������⡼��
		 */
		public void setAcessMode(int acessmode) {
			this.acessMode = acessmode;
		}
		/**
		 * �ե�����̾�򥻥åȤ���
		 * @param filename ���åȤ���ե�����̾
		 */
		public void setFileName(String filename) {
			this.fileName = filename;
		}
		/**
		 * �쥳����̾�򥻥åȤ���
		 * @param recordname ���åȤ���쥳����̾
		 */
		public void setRecordName(String recordname) {
			this.recordName = recordname;
		}
		/**
		 * �ե�����̾�򥻥åȤ���
		 * @param selectname ���åȤ���ե�����̾
		 */
		public void setSelectName(String selectname) {
			this.selectName = selectname;
		}
		/**
		 * ���ơ������ΰ�̾�򥻥åȤ���
		 * @param status ���åȤ��륹�ơ������ΰ�̾
		 */
		public void setStatus(String status) {
			this.status = status;
		}
	}
	/** �쥳����̾ */
	String acmRecName = null;
	/** ���ԡ���ΰ��� */
	ArrayList<String> copys = new ArrayList<String>();
	/** ���ߤΥѥ����� */
	String current;
	/** ���߽�����Υե�������� */
	DefaultFileInfo currentinfo = null;
	/** �Ѵ�����Ȥ������򤷤Ƥ���ʸ���� */
	ArrayList<String> currentlist = new ArrayList<String>();
	/** currentlists���Ѥ߾夲�� */
	Stack<ArrayList<String>> currentlists = new Stack<ArrayList<String>>();
	/** ���ߤ�DIVISION */
	String division = null;
	/** ���ߡ��Υե�������� */
	final FileInfo dummyInfo = new DefaultFileInfo();
	/** ���߽������FD�� */
	ArrayList<String> fdlist = new ArrayList<String>();
	/** �ե�����̾�ʳ����ե�����̾���ˤ���ե��������μ��Τ�������뤿��κ��� */
	Hashtable<String, DefaultFileInfo> filenametofile = new Hashtable<String, DefaultFileInfo>();
	/** ������Υץ�����(ACM�Ǥʤ�)�ºݤΥե�����򥢥����󤹤뤫�ɤ��� */
	boolean hasNonACM = false;
	/** ���ߡ�ACM�ե���������椫�� */
	boolean inACM = false;
	/** ���ԡ�������椫�� */
	boolean inCopy = false;
	/** ���ߡ�FD�������椫�� */
	boolean inFD = false;
	/** ���Υ������Ͻ����̿�᤬���Ϥ��줿���� */
	boolean initialized = false;
	/** PROCEDURE SECTION��μ����Ѥߥ�٥�� */
	int label = 0;
	/**
	 * ��٥�--�������������������ҥ�٥�
	 */
	int level = 0;
	/**
	 * �������Υ�����������
	 */
	ArrayList<String> list = new ArrayList<String>();
	private ArrayList<CodeGeneratorListener> listeners = new ArrayList<CodeGeneratorListener>();
	/**
	 * �ƥ��֥�������
	 */
	GeneratorOwner owner;
	/**
	 * Procefure Division������椫�ɤ���
	 */
	boolean proceduresection = false;
	/**
	 * �쥳����̾����ե�������Τ�������뤿��κ���
	 */
	Hashtable<String, DefaultFileInfo> recordnametofile = new Hashtable<String, DefaultFileInfo>();
	/**
	 * ���������
	 */
	String section = null;
	/**
	 * SELECT̾���ե�������Τ�������뤿��κ���
	 */
	Hashtable<String, DefaultFileInfo> selectnametofile = new Hashtable<String, DefaultFileInfo>();
	/**
	 * �ѥ�����Υ����å�
	 */
	Stack<String> stack = new Stack<String>();
	/**
	 * ���󥹥ȥ饯��
	 * @param owner �ƤӸ�
	 */
	public TCPCodeGenerator(GeneratorOwner owner) {
		this.owner = owner;
	}
	/**
	 * �ɲ�
	 * @param text �ɲä���ƥ�����
	 */
	void add(String text) {
		list.add(text);
		// SQLNetServer.DebugPrint(text);
	}
	/**
	 * FD����ɲ�
	 * @param text FD����Υƥ�����
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
	 * CLOSE����
	 * @param period "."ʸ����
	 */
	void addCallClose(FileInfo info, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     CALL \"closeACMFile\" USING ACM-FILE-IDENT ACM-STATUS-ALL" + period);
	}
	/**
	 * COMMIT���ɲ�
	 * @param period �ԥꥪ��(.)ʸ����
	 */
	void addCallCommit(String period) {
		add("    CALL \"commitACMSession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * DELETE����
	 * @param valid ͭ�����ν���
	 * @param invalid ̵�����ν���
	 * @param period "."ʸ����
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
	 * InitializeSession���ɲ�
	 * @param period �ԥꥪ��(.)ʸ����
	 */
	void addCallInitializeSession(String period) {
		add("     CALL \"initializeSessionEnv\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * OPEN INPUT ����
	 * @param info �ե�����ξ���
	 * @param period �ԥꥪ��(.)ʸ����
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
	 * OPEN I-O ����
	 * @param info �ե�����ξ���
	 * @param period �ԥꥪ��(.)ʸ����
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
	 * OPEN OUTPUT ����
	 * @param info �ե�����ξ���
	 * @param period �ԥꥪ��(.)ʸ����
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
	 * READ����
	 * @param info �ե��������
	 * @param invalid ͭ�����ν���
	 * @param notinvalid ̵���������
	 * @param indexkey ����̾
	 * @param period �ԥꥪ��(.)ʸ����
	 */
	void addCallRead(FileInfo info, ArrayList<String> invalid, ArrayList<String> notinvalid, String indexkey, String period) {
		add("     MOVE \"" + info.getFileName() + "\" TO ACM-FILE-IDENT" + period);
		add("     MOVE " + info.getRecordName() + " TO ACM-RECORD" + period);
		add("     CALL \"moveReadACMRecord\" USING ");
		add("                                ACM-FILE-IDENT");
		if (indexkey == null) {
			// �����꡼��
			add("                                ACM-RECORD");
		} else {
			// �����ˤ��꡼��
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
	 * READ����
	 * @param info �ե��������
	 * @param atend ͭ�����ν���
	 * @param notatend ̵��(�ե����뽪ü)�������
	 * @param indexkey ����̾
	 * @param period �ԥꥪ��(.)ʸ����
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
	 * Rewrite����
	 * @param info �ե��������
	 * @param invalid ͭ�����ν���
	 * @param notinvalid ̵���������
	 * @param indexkey ����̾
	 * @param period �ԥꥪ��(.)ʸ����
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
	 * ROLLBACK���ɲ�
	 * @param period �ԥꥪ��(.)ʸ����
	 */
	void addCallRollback(String period) {
		add("    CALL \"rollbackACMSession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * START����
	 * @param info �ե��������
	 * @param invalid ͭ�����ν���
	 * @param notinvalid ̵���������
	 * @param indexkey ����̾
	 * @param period �ԥꥪ��(.)ʸ����
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
	 * TermincateSession���ɲ�
	 * @param period �ԥꥪ��(.)ʸ����
	 */
	void addCallTerminateSession(String period) {
		add("     CALL  \"terminateSession\" USING ACM-STATUS-ALL" + period);
	}
	/**
	 * WRITE����
	 * @param info �ե��������
	 * @param invalid ͭ�����ν���
	 * @param notinvalid ̵���������
	 * @param indexkey ����̾
	 * @param period �ԥꥪ��(.)ʸ����
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
	 * ���å����ν����
	 * @param period "."ʸ����
	 */
	void addInitializeSession(String period) {
		FileInfo nullfile = new DefaultFileInfo();
		// ���٥�Ƚ���
		CodeGeneratorEvent event = new CodeGeneratorEvent(nullfile, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preInitialize(event);
		}
		// ///////////
		addCallInitializeSession(period);
		// ���٥�Ƚ���
		for (CodeGeneratorListener listener : listeners) {
			listener.postInitialize(event);
		}
		// ///////////
	}
	/**
	 * ���å����ν�λ
	 * @param period "."ʸ����
	 */
	void addTerminateSession(String period) {
		FileInfo nullfile = new DefaultFileInfo();
		// ���٥�Ƚ���
		CodeGeneratorEvent event = new CodeGeneratorEvent(nullfile, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preTerminate(event);
		}
		// ///////////
		addCallTerminateSession(period);
		// ���٥�Ƚ���
		for (CodeGeneratorListener listener : listeners) {
			listener.postTerminate(event);
		}
		// ///////////
	}
	/**
	 * FD�礬ACM��������뤫�ɤ���
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
	 * �Хåե������Ƥ򤹤٤Ʋ��Ϥ��ƥ��ꥢ����
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
	 * �Хåե���ȡ�����ν��ޤ��ʬ�򤹤�
	 * @return �ȡ�����ν���
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
	 * ���߽������DIVISION
	 * @param text ���߹�
	 * @return DIVISION̾
	 */
	String getDivision(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text);
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		}
		return null;
	}
	/**
	 * ���߽������SECTION
	 * @param text ���߹�
	 * @return SECTION̾
	 */
	String getSection(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text);
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		}
		return null;
	}
	/**
	 * ���ԡ����Ÿ�����뤫�ɤ���
	 * @return true Ÿ������<br/>
	 *         false Ÿ�����ʤ�
	 */
	public boolean isExpandCopy() {
		return owner.isExpandCopy();
	}
	/**
	 * ʸ����β���
	 * @param text ��ʸ����
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
				add(text); // �����Ȥξ����ɲäΤ�
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
	 * �����å������������
	 */
	void pop() {
		current = stack.pop();
		currentlist = currentlists.pop();
	}
	/**
	 * CLOSE����
	 * @param period "."ʸ����
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
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preClose(event);
			}
			// ///////////
			addCallClose(info, period);
			// ���٥�Ƚ���
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
	 * ���ԡ���ν���
	 */
	void process_copy() {
		inCopy = false;
		owner.callBackCopyStatement(copys);
	}
	/**
	 * DELETE����
	 * @param period "."ʸ����
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
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preDelete(event);
			}
			// ///////////
			addCallDelete(info, invalid, notinvalid, period);
			// ���٥�Ƚ���
			for (CodeGeneratorListener listener : listeners) {
				listener.postDelete(event);
			}
			// ///////////
		}
	}
	/**
	 * FD��ν���
	 */
	void process_fd() {
		//
	}
	/**
	 * OPEN����
	 * @param period "."ʸ����
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
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenInput(info, period);
			// ���٥�Ƚ���
			for (CodeGeneratorListener listener : listeners) {
				listener.postOpen(event);
			}
			// ///////////
		}
		for (int i = 0; i < output1.size(); i++) {
			FileInfo info = output1.get(i);
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenOutput(info, period);
			// ���٥�Ƚ���
			for (CodeGeneratorListener listener : listeners) {
				listener.postOpen(event);
			}
			// ///////////
		}
		for (int i = 0; i < io1.size(); i++) {
			FileInfo info = io1.get(i);
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preOpen(event);
			}
			// ///////////
			addCallOpenInputOutput(info, period);
			// ���٥�Ƚ���
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
	 * @param period "."ʸ����
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
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preReadNext(event);
			}
			// ///////////
			// Read Next ����
			addCallReadNext(info, atend, notatend, period);
			// ���٥�Ƚ���
			for (CodeGeneratorListener listener : listeners) {
				listener.postReadNext(event);
			}
			// ///////////
		} else {
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preMoveRead(event);
			}
			// ///////////
			// read ����
			addCallRead(info, invalid, notinvalid, indexkey, period);
			// ���٥�Ƚ���
			for (CodeGeneratorListener listener : listeners) {
				listener.postMoveRead(event);
			}
			// ///////////
		}
	}
	/**
	 * ��������
	 * @param period "."ʸ����
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
			// ���٥�Ƚ���
			CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preRewrite(event);
			}
			// ///////////
			addCallRewrite(info, invalid, notinvalid, period);
			// ���٥�Ƚ���
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
	 * @param period "."ʸ����
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
		// ���٥�Ƚ���
		CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preStart(event);
		}
		// ///////////
		addCallStart(info, startmodetext, invalid, notinvalid, indexkey, period);
		// ���٥�Ƚ���
		for (CodeGeneratorListener listener : listeners) {
			listener.postStart(event);
		}
		// ///////////
	}
	/**
	 * WRITE���������
	 * @param period �����Υԥꥪ��
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
		// ���٥�Ƚ���
		CodeGeneratorEvent event = new CodeGeneratorEvent(info, owner, this, period);
		for (CodeGeneratorListener listener : listeners) {
			listener.preWrite(event);
		}
		// ///////////
		addCallWrite(info, invalid, notinvalid, period);
		// ���٥�Ƚ���
		for (CodeGeneratorListener listener : listeners) {
			listener.postWrite(event);
		}
		// ///////////
	}
	/**
	 * ���ߤ��Խ����Ƥ����椹��
	 */
	void push() {
		if (current != null)
			stack.push(current);
		currentlists.push(currentlist);
	}
	/**
	 * ���ߥåȥ⡼�ɤ����ꤹ��<br/>
	 * ̵���ǹ����˥ԥꥪ�ɤ����ꤹ�뤳�Ȥ����
	 * @param text true/false��ޤ�ʸ����
	 */
	void whenACMAutoCommit(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		add("    MOVE \"" + option + "\" TO ACM-OPTION.");
		add("    CALL \"setACMCommitMode\" USING ACM-OPTION");
		add("                                    ACM-STATUS-ALL.");
	}
	/**
	 * �ȥ�󥶥������򥳥ߥåȤ���
	 * @param text ��ʸ����<br/>
	 *            ��ʸ����".(�ԥꥪ��)"�ǽ���äƤ���������˥ԥꥪ�ɤ�Ĥ���
	 */
	void whenACMCommit(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text) ? "." : "");
		// ���٥�Ƚ���
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preCommit(event);
			}
		}
		addCallCommit(period);
		// ���٥�Ƚ���
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postCommit(event);
			}
		}
	}
	/**
	 * �쥳����̾������
	 * @param text �쥳����̾��ޤ��
	 */
	void whenACMRecName(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		acmRecName = text.substring(indexOfEqual);
	}
	/**
	 * �ȥ�󥶥����������Хå�����
	 * @param text ��ʸ����<br/>
	 *            ��ʸ����".(�ԥꥪ��)"�ǽ���äƤ���������˥ԥꥪ�ɤ�Ĥ���
	 */
	void whenACMRollBack(String text) {
		String period = (Pattern.matches(CobolConsts.PERIOD, text) ? "." : "");
		// ���٥�Ƚ���
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.preRollback(event);
			}
		}
		addCallRollback(period);
		// ���٥�Ƚ���
		{
			CodeGeneratorEvent event = new CodeGeneratorEvent(dummyInfo, owner, this, period);
			for (CodeGeneratorListener listener : listeners) {
				listener.postRollback(event);
			}
		}
	}
	/**
	 * ACM�ե�����ؼ��γ���
	 * @param text ���Ϲ�
	 */
	void whenACMSTART(String text) {
		// ACM���ϥ�����
		add("*" + text);
		inACM = true;
		acmRecName = null;
	}
	/**
	 * �ȥ�󥶥�������٥������<br/>
	 * ̵���ǹ����˥ԥꥪ�ɤ����ꤹ�뤳�Ȥ����
	 * @param text ��٥�ʸ�����ޤ����
	 */
	void whenACMTransactionIsolation(String text) {
		int indexOfEqual = text.indexOf("=") + 1;
		String option = text.substring(indexOfEqual);
		add("    MOVE \"" + option + "\" TO ACM-OPTION.");
		add("    CALL \"setACMTransMode\" USING ACM-OPTION");
		add("                                   ACM-STATUS-ALL.");
	}
	/**
	 * CLOSE̿��
	 * @param text ������ɤ�ޤ��
	 */
	void whenClose(String text) {
		push();
		// �ե����륯����
		current = CobolConsts.CLOSE;
		add("*" + text);
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_close(".");
			pop();
		}
		// ------����������push!-------/
	}
	/**
	 * ���ԡ��礬�����Ȥ�
	 * @param text ���ԡ����ޤ��
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
			// ���ԡ���
			if (inFD) {
				add("*" + text);
				add_fd(text);
			} else {
				add(text);
			}
		}
	}
	/**
	 * DELETE��
	 * @param text ������ɤ�ޤ��
	 */
	void whenDelete(String text) {
		push();
		// �ե����륪���ץ�
		current = CobolConsts.DELETE;
		add("*" + text);
		currentlist.add(text);
		if (Pattern.matches(CobolConsts.PERIOD, text)) {
			process_delete(".");
			pop();
		}
	}
	/**
	 * DIVISION���ޤ��
	 * @param text ������ɤ�ޤ��
	 */
	void whenDivision(String text) {
		add(text);
		// �ե����륳��ȥ���ν�λ
		if (current == CobolConsts.FD) {
			process_fd();
			inFD = false;
		}
		// �ǥ��ӥ����
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
	 * END DELETE��
	 * @param text ������ɤ�ޤ��
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
	 * END WRITE�б�
	 * @param text ��ʸ����
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
		// �쥳�������
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
		// �ե����륳��ȥ���λϤޤ�
		current = CobolConsts.FILECONTROL;
	}
	/**
	 * @param text
	 */
	void whenLabel(String text) {
		// ��٥�ʵ��ˤ��ʤ�����
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
		// �ե����륪���ץ�
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
		// �ե����륳��ȥ���ν�λ
		if (current == CobolConsts.FD) {
			process_fd();
			inFD = false;
		}
		// ���������
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
		// ���쥯�ȡʥե����륢������λϤޤ��
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
		// ����ΰ����
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