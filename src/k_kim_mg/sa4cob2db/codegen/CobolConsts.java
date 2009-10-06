/*
 * Created on 2004/06/01
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.codegen;
/**
 * �������Ѵ��˴ؤ����ƥ����󶡤���
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public abstract class CobolConsts {
	/**AMFILE����γ���*/
	public static final String ACMSTART = "^\\*ACMFILE\\s*";
	/**ACM�쥳����̾���*/
	public static final String ACMRECNAME = "^\\*ACMRECNAME=.*";
	/**AM�ȥ�󥶥�����󳫻����*/
	public static final String ACMTRANS = "^\\*ACMTRANSACTION=.*";
	/**��ư���ߥåȥ⡼�����*/
	public static final String ACMAUTO = "^\\*ACMAUTOCOMMIT=.*";
	/**���ߥå����*/
	public static final String ACMCOMMIT = "^\\*ACMCOMMIT.*";
	/**����Хå����*/
	public static final String ACMROLLBACK = "^\\*ACMROLLBACK.*";
	/**AT END��*/
	public static final String ATEND = "^\\s*[aA][tT]\\s*[eE][nN][dD].*";
	/**CLOSE̿��*/
	public static final String CLOSE = "^\\s*[cC][lL][oO][sS][eE]\\s.*";
	/**������*/
	public static final String COMMENT = "^\\*.*";
	/**COPY��*/
	public static final String COPY = "^\\s*[cC][oO][pP][yY]\\s.*";
	/**DELETE̿��*/
	public static final String DELETE = "^\\s*[dD][eE][lL][eE][tT][eE]\\s.*";
	/**DEVISION��*/
	public static final String DIVISION = ".*[dD][iI][vV][iI][sS][iI][oO][nN].*";
	/**END DELETE��*/
	public static final String ENDDELETE = "^\\s*[eE][nN][dD]-[dD][eE][lL][eE][tT][eE].*";
	/**����¾��END ???*/
	public static final String ENDOFOTHERS = "^\\s*[eE][nN][dD]-.*";
	/**END READ��*/
	public static final String ENDREAD = "^\\s*[eE][nN][dD]-[rR][eE][aA][dD].*";
	/**END REWRITE��*/
	public static final String ENDREWRITE = "^\\s*[eE][nN][dD]-[rR][eE][wW][rR][iI][tT][eE].*";
	/**END START��*/
	public static final String ENDSTART = "^\\s*[eE][nN][dD]-[sS][tT][aA][rR][tT].*";
	/**END WRITE��*/
	public static final String ENDWRITE = "^\\s*[eE][nN][dD]-[wW][rR][iI][tT][eE].*";
	/**EXIT PROGRAM��*/
	public static final String EXITPROGRAM = "^\\s*[eE][xX][iI][tT]\\s*[pP][rR][oO][gG][rR][aA][mM].*";
	/**FD��*/
	public static final String FD = "^\\s*[fF][dD]\\s.*";
	/**FILE CONTROL��*/
	public static final String FILECONTROL = "\\s*[fF][iI][lL][eE]-[cC][oO][nN][tT][rR][oO][lL]\\s*\\.\\s*";
	/**INVALID��*/
	public static final String INVALID = "^\\s*[iI][nN][vV][aA][lL][iI][dD].*";
	/**��٥�*/
	public static final String LABEL = "^\\s[^\\s].*";
	/**���������⡼��(��ĥ)*/
	public static final int MODE_EXTEND = 3;
	/**���������⡼��(����)*/
	public static final int MODE_INPUT = 0;
	/**���������⡼��(������)*/
	public static final int MODE_IO = 2;
	/**���������⡼��(����)*/
	public static final int MODE_OUTPUT = 1;
	/**NOT AT END��*/
	public static final String NOTATEND = "^\\s*[nN][oO][tT]\\s*[aA][tT]\\s*[eE][nN][dD].*";
	/**NOT INVALID��*/
	public static final String NOTINVALID = "^\\s*[nN][oO][tT]\\s*[iI][nN][vV][aA][lL][iI][dD].*";
	/**OPEN̿��*/
	public static final String OPEN = "^\\s*[oO][pP][eE][nN]\\s.*";
	/**�ե��������(�����ʥߥå�)*/
	public static final int ORG_DYNAMIC = 1;
	/**�ե��������(������)*/
	public static final int ORG_RANDOM = 2;
	/**�ե��������(�������󥷥��)*/
	public static final int ORG_SEQUENTIAL = 0;
	/**�ԥꥪ�ɤΤߤι�*/
	public static final String PERIOD_ROW = "\\.\\s*$";
	/**�������ԥꥪ�ɤι�*/
	public static final String PERIOD = ".*\\.\\s*";
	/**READ̿��*/
	public static final String READ = "^\\s*[rR][eE][aA][dD]\\s.*";
	/**REWRITE̿��*/
	public static final String REWRITE = "^\\s*[rR][eE][wW][rR][iI][tT][eE]\\s.*";
	/**SECTION��*/
	public static final String SECTION = ".*[sS][eE][cC][tT][iI][oO][nN]\\s*\\.\\s*";
	/**SELECT��*/
	public static final String SELECT = "^\\s*[sS][eE][lL][eE][cC][tT]\\s.*";
	/**START̿��*/
	public static final String START = "^\\s*[sS][tT][aA][rR][tT].*";
	/**STOP ABORT̿��*/
	public static final String STOPABORT = "^\\s*[sS][tT][oO][pP]\\s*[aA][bB][oO][rR][tT].*";
	/**STOP RUN̿��*/
	public static final String STOPRUN = "^\\s*[sS][tT][oO][pP]\\s*[rR][uU][nN].*";
	/**PICTURE���01��٥�*/
	public static final String STORAGE = "^\\s*0*1\\s.*";
	/**WRITE̿��*/
	public static final String WRITE = "^\\s*[wW][rR][iI][tT][eE]\\s.*";
	/**ACM��Ϣ���ΰ����ݤ���PICTURE����Ǽ����ե�����(COPY��ǥ���������������)*/
	static String ACMCONSTS_FILE = "ACMCONSTS.CBL";
}
