package k_kim_mg.sa4cob2db.codegen;

import java.util.ArrayList;

/**
 * Literals for Code Generation
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolConsts {
	// ////////////////////////
	// Don't Sort Members!! //
	// ////////////////////////
	/**
	 * Start of Comment out <br>
	 * *ACMCOMMENTSTART
	 */
	public static final String ACMCOMMENTSTART = "^\\*ACMCOMMENTSTART.*";
	/** End of Comment out */
	public static final String ACMCOMMENTEND = "^\\*ACMCOMMENTEND.*";
	/** Start of Comment in */
	public static final String ACMINSERTSTART = "^\\*ACMINSERTSTART.*";
	/** End of Comment in */
	public static final String ACMINSERTEND = "^\\*ACMINSERTEND.*";
	/** Declare Start of AMFILE */
	public static final String ACMSTART = "^\\*ACMFILE\\s*";
	/** Declaration of ACM Record Name */
	public static final String ACMRECNAME = "^\\*ACMRECNAME=.*";
	/** Redefine ACM Assign File Name */
	public static final String ACMASSIGNNAME = "^\\*ACMASSIGNNAME=.*";
	/** Declare ACM Transaction */
	public static final String ACMTRANS = "^\\*ACMTRANSACTION=.*";
	/** Declaration Auto Commit */
	public static final String ACMAUTO = "^\\*ACMAUTOCOMMIT=.*";
	/** Commit */
	public static final String ACMCOMMIT = "^\\*ACMCOMMIT.*";
	/** Rollback */
	public static final String ACMROLLBACK = "^\\*ACMROLLBACK.*";
	/** Set optional value to ... */
	public static final String ACMSETOPTION = "^\\*ACMSETOPTION.*";
	/** Get optional value from ... */
	public static final String ACMGETOPTION = "^\\*ACMGETOPTION.*";
	/** Set Max Record length ... */
	public static final String ACMSETLENGTH = "^\\*ACMSETLENGTH.*";
	/** NAME= */
	public static final String NAME_EQUAL = "\\s*NAME=.*";
	/** ENV= */
	public static final String ENV_EQUAL = "\\s*ENV=.*";
	/** TO= */
	public static final String TO_EQUAL = "\\s*TO=.*";
	/** FROM= */
	public static final String FROM_EQUAL = "\\s*FROM=.*";
	/** VALUE= */
	public static final String VALUE_EQUAL = "\\s*VALUE=.*";
	/** AT END Statement */
	public static final String ATEND = "^\\s*[aA][tT]\\s*[eE][nN][dD].*";
	/** CALL Statement */
	public static final String CALL = "^\\s*[cC][aA][lL][lL]\\s.*";
	/** CLOSE Command */
	public static final String CLOSE = "^\\s*[cC][lL][oO][sS][eE]\\s.*";
	/** Comment */
	public static final String COMMENT = "^\\*.*";
	/** Hyphen */
	public static final String HYPHEN = "^-.*";
	/** COPY Statement */
	public static final String COPY = "^\\s*[cC][oO][pP][yY]\\s.*";
	/** DELETE Command */
	public static final String DELETE = "^\\s*[dD][eE][lL][eE][tT][eE]\\s.*";
	/** DEVISION Statement */
	public static final String DIVISION = ".*[dD][iI][vV][iI][sS][iI][oO][nN].*";
	/** END CALL Statement */
	public static final String ENDCALL = "^\\s*[eE][nN][dD]-[cC][aA][lL][lL].*";
	/** END DELETE Statement */
	public static final String ENDDELETE = "^\\s*[eE][nN][dD]-[dD][eE][lL][eE][tT][eE].*";
	/** Other END ??? */
	public static final String ENDOFOTHERS = "^\\s*[eE][nN][dD]-.*";
	/** END READ Statement */
	public static final String ENDREAD = "^\\s*[eE][nN][dD]-[rR][eE][aA][dD].*";
	/** END REWRITE Statement */
	public static final String ENDREWRITE = "^\\s*[eE][nN][dD]-[rR][eE][wW][rR][iI][tT][eE].*";
	/** END START Statement */
	public static final String ENDSTART = "^\\s*[eE][nN][dD]-[sS][tT][aA][rR][tT].*";
	/** END WRITE Statement */
	public static final String ENDWRITE = "^\\s*[eE][nN][dD]-[wW][rR][iI][tT][eE].*";
	/** EXIT PROGRAM Statement */
	public static final String EXITPROGRAM = "^\\s*[eE][xX][iI][tT]\\s*[pP][rR][oO][gG][rR][aA][mM].*";
	/** FD Statement */
	public static final String FD = "^\\s*[fF][dD]\\s.*";
	/** FILE CONTROL Statement */
	public static final String FILECONTROL = "\\s*[fF][iI][lL][eE]-[cC][oO][nN][tT][rR][oO][lL]\\s*\\.\\s*";
	/** INVALID Statement */
	public static final String INVALID = "^\\s*[iI][nN][vV][aA][lL][iI][dD].*";
	/** Label */
	public static final String LABEL = "^\\s[^\\s].*";
	/** Access Mode(Extend) */
	public static final int MODE_EXTEND = 3;
	/** Access Mode(Input) */
	
	public static final int MODE_INPUT = 0;
	/** Access Mode(Input-Output) */
	public static final int MODE_IO = 2;
	/** Access Mode(Output) */
	public static final int MODE_OUTPUT = 1;
	/** NOT AT END Statement */
	public static final String NOTATEND = "^\\s*[nN][oO][tT]\\s*[aA][tT]\\s*[eE][nN][dD].*";
	/** NOT INVALID Statement */
	public static final String NOTINVALID = "^\\s*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD].*";
	/** OPEN Command */
	public static final String OPEN = "^\\s*[oO][pP][eE][nN]\\s.*";
	/** File Organization(Dynamic) */
	public static final int ORG_DYNAMIC = 1;
	/** File Organization(Random) */
	public static final int ORG_RANDOM = 2;
	/** File Organization(Sequential) */
	public static final int ORG_SEQUENTIAL = 0;
	/** Period Only */
	
	public static final String PERIOD_ROW = "\\.\\s*$";
	/** A Logical Line */
	public static final String PERIOD = ".*\\.\\s*";
	/** READ Command */
	public static final String READ = "^\\s*[rR][eE][aA][dD]\\s.*";
	/** REWRITE Command */
	public static final String REWRITE = "^\\s*[rR][eE][wW][rR][iI][tT][eE]\\s.*";
	/** SECTION Statement */
	public static final String SECTION = ".*[sS][eE][cC][tT][iI][oO][nN]\\s*\\.\\s*";
	/** SELECT Statement */
	public static final String SELECT = "^\\s*[sS][eE][lL][eE][cC][tT]\\s.*";
	/** START Command */
	public static final String START = "^\\s*[sS][tT][aA][rR][tT].*";
	/** STOP ABORT Command */
	public static final String STOPABORT = "^\\s*[sS][tT][oO][pP]\\s*[aA][bB][oO][rR][tT].*";
	/** STOP RUN Command */
	public static final String STOPRUN = "^\\s*[sS][tT][oO][pP]\\s*[rR][uU][nN].*";
	/** 01 Level */
	public static final String STORAGE = "^\\s*0*1\\s.*";
	/** WRITE Command */
	public static final String WRITE = "^\\s*[wW][rR][iI][tT][eE]\\s.*";
	/** Name of file that includes Literals */
	private static String ACMCONSTS_FILE = "ACMCONSTS.CBL";

	/** Words to be ignored */
	private static ArrayList<String> IGNORE = new ArrayList<String>();

	static {
		// add ignored word
		IGNORE.add("OPTIONAL");
		IGNORE.add("IS");
		IGNORE.add("TO");
	}

	/**
	 * Name of file that includes Literals
	 * 
	 * @return filename
	 */
	public static String getACMCONSTS_FILE() {
		return ACMCONSTS_FILE;
	}

	/**
	 * Words to be ignored
	 * 
	 * @return ArrayList of words
	 */
	public static ArrayList<String> getIGNORE() {
		return IGNORE;
	}

	/**
	 * Name of file that includes Literals
	 * 
	 * @param file filename
	 */
	public static void setACMCONSTS_FILE(String file) {
		if (file != null) {
			ACMCONSTS_FILE = file;
		}
	}
}
