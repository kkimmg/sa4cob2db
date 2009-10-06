/*
 * Created on 2004/06/01
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.codegen;
/**
 * コード変換に関するリテラルを提供する
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolConsts {
	/**AMFILE宣言の開始*/
	public static final String ACMSTART = "^\\*ACMFILE\\s*";
	/**ACMレコード名宣言*/
	public static final String ACMRECNAME = "^\\*ACMRECNAME=.*";
	/**AMトランザクション開始宣言*/
	public static final String ACMTRANS = "^\\*ACMTRANSACTION=.*";
	/**自動コミットモード宣言*/
	public static final String ACMAUTO = "^\\*ACMAUTOCOMMIT=.*";
	/**コミット宣言*/
	public static final String ACMCOMMIT = "^\\*ACMCOMMIT.*";
	/**ロールバック宣言*/
	public static final String ACMROLLBACK = "^\\*ACMROLLBACK.*";
	/**AT END句*/
	public static final String ATEND = "^\\s*[aA][tT]\\s*[eE][nN][dD].*";
	/**CLOSE命令*/
	public static final String CLOSE = "^\\s*[cC][lL][oO][sS][eE]\\s.*";
	/**コメント*/
	public static final String COMMENT = "^\\*.*";
	/**COPY句*/
	public static final String COPY = "^\\s*[cC][oO][pP][yY]\\s.*";
	/**DELETE命令*/
	public static final String DELETE = "^\\s*[dD][eE][lL][eE][tT][eE]\\s.*";
	/**DEVISION句*/
	public static final String DIVISION = ".*[dD][iI][vV][iI][sS][iI][oO][nN].*";
	/**END DELETE句*/
	public static final String ENDDELETE = "^\\s*[eE][nN][dD]-[dD][eE][lL][eE][tT][eE].*";
	/**その他のEND ???*/
	public static final String ENDOFOTHERS = "^\\s*[eE][nN][dD]-.*";
	/**END READ句*/
	public static final String ENDREAD = "^\\s*[eE][nN][dD]-[rR][eE][aA][dD].*";
	/**END REWRITE句*/
	public static final String ENDREWRITE = "^\\s*[eE][nN][dD]-[rR][eE][wW][rR][iI][tT][eE].*";
	/**END START句*/
	public static final String ENDSTART = "^\\s*[eE][nN][dD]-[sS][tT][aA][rR][tT].*";
	/**END WRITE句*/
	public static final String ENDWRITE = "^\\s*[eE][nN][dD]-[wW][rR][iI][tT][eE].*";
	/**EXIT PROGRAM句*/
	public static final String EXITPROGRAM = "^\\s*[eE][xX][iI][tT]\\s*[pP][rR][oO][gG][rR][aA][mM].*";
	/**FD句*/
	public static final String FD = "^\\s*[fF][dD]\\s.*";
	/**FILE CONTROL句*/
	public static final String FILECONTROL = "\\s*[fF][iI][lL][eE]-[cC][oO][nN][tT][rR][oO][lL]\\s*\\.\\s*";
	/**INVALID句*/
	public static final String INVALID = "^\\s*[iI][nN][vV][aA][lL][iI][dD].*";
	/**ラベル*/
	public static final String LABEL = "^\\s[^\\s].*";
	/**アクセスモード(拡張)*/
	public static final int MODE_EXTEND = 3;
	/**アクセスモード(入力)*/
	public static final int MODE_INPUT = 0;
	/**アクセスモード(入出力)*/
	public static final int MODE_IO = 2;
	/**アクセスモード(出力)*/
	public static final int MODE_OUTPUT = 1;
	/**NOT AT END句*/
	public static final String NOTATEND = "^\\s*[nN][oO][tT]\\s*[aA][tT]\\s*[eE][nN][dD].*";
	/**NOT INVALID句*/
	public static final String NOTINVALID = "^\\s*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD].*";
	/**OPEN命令*/
	public static final String OPEN = "^\\s*[oO][pP][eE][nN]\\s.*";
	/**ファイル形成(ダイナミック)*/
	public static final int ORG_DYNAMIC = 1;
	/**ファイル形成(ランダム)*/
	public static final int ORG_RANDOM = 2;
	/**ファイル形成(シーケンシャル)*/
	public static final int ORG_SEQUENTIAL = 0;
	/**ピリオドのみの行*/
	public static final String PERIOD_ROW = "\\.\\s*$";
	/**行末がピリオドの行*/
	public static final String PERIOD = ".*\\.\\s*";
	/**READ命令*/
	public static final String READ = "^\\s*[rR][eE][aA][dD]\\s.*";
	/**REWRITE命令*/
	public static final String REWRITE = "^\\s*[rR][eE][wW][rR][iI][tT][eE]\\s.*";
	/**SECTION句*/
	public static final String SECTION = ".*[sS][eE][cC][tT][iI][oO][nN]\\s*\\.\\s*";
	/**SELECT句*/
	public static final String SELECT = "^\\s*[sS][eE][lL][eE][cC][tT]\\s.*";
	/**START命令*/
	public static final String START = "^\\s*[sS][tT][aA][rR][tT].*";
	/**STOP ABORT命令*/
	public static final String STOPABORT = "^\\s*[sS][tT][oO][pP]\\s*[aA][bB][oO][rR][tT].*";
	/**STOP RUN命令*/
	public static final String STOPRUN = "^\\s*[sS][tT][oO][pP]\\s*[rR][uU][nN].*";
	/**PICTURE句の01レベル*/
	public static final String STORAGE = "^\\s*0*1\\s.*";
	/**WRITE命令*/
	public static final String WRITE = "^\\s*[wW][rR][iI][tT][eE]\\s.*";
	/**ACM関連の領域を確保するPICTURE句を格納するファイル(COPY句でソースに挿入する)*/
	static String ACMCONSTS_FILE = "ACMCONSTS.CBL";
}
