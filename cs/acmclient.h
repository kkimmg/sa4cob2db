#define TRUE 0
#define FALSE -1
#define STATUS_LEN 3
#define STATUS_MAX (STATUS_LEN - 1)
#define FILE_IDENT_LEN 256
#define FILE_IDENT_MAX (FILE_IDENT_LEN - 1)
#define FILE_OPTION_LEN 256
#define FILE_OPTION_MAX (FILE_OPTION_LEN - 1)
#define FILE_STARTMODE_LEN 30
#define FILE_STARTMODE_MAX (FILE_STARTMODE_LEN - 1)
#define FILE_INDEXNAME_LEN 226
#define FILE_INDEXNAME_MAX (FILE_INDEXNAME_LEN - 1)
#define OPTIONNAME_LEN 30
#define OPTIONNAME_MAX (FILE_STARTMODE_LEN - 1)
#define OPTIONVALUE_LEN 226
#define OPTIONVALUE_MAX (FILE_INDEXNAME_LEN - 1)
#define RECORD_LEN 8192
#define RECORD_MAX (RECORD_LEN - 1)
/*********************************/
#define STATUS_OK			"00"
#define STATUS_DUPLICATE_SUBKEY		"02"
#define STATUS_NOTEXIST			"05"
#define STATUS_EOF			"10"
#define STATUS_DUPLICATE_KEY		"22"
#define STATUS_INVALID_KEY		"23"
#define STATUS_FILEOVER			"24"
#define STATUS_CANT_OPEN		"37"
#define STATUS_ALREADY_OPENED		"41"
#define STATUS_ALREADY_CLOSED		"42"
#define STATUS_NOT_MOVED		"43"
#define STATUS_CANT_READ		"47"
#define STATUS_CANT_WRITE		"48"
#define STATUS_CANT_REWRITE		"49"
#define STATUS_CANT_DELETE		"49"
#define STATUS_READY			"90"
#define STATUS_BOF			"91"
#define STATUS_NOT_OPENED		"92"
#define STATUS_NOT_ASSIGND		"93"
#define STATUS_SEND_ERROR		"96"
#define STATUS_RECV_ERROR		"97"
#define STATUS_UNSUPPORTED_METHOD	"98"
#define STATUS_FAILURE			"99"
/*********************************/
#define MSG_INITIALIZE    "INITIALIZE"
#define MSG_USERNAME      "USERNAME"
#define MSG_PASSWORD      "PASSWORD"
#define MSG_TERMINATE     "TERMINATE"
#define MSG_ASSIGN        "ASSIGN"
#define MSG_OPEN          "OPEN"
#define MSG_CLOSE         "CLOSE"
#define MSG_NEXT          "NEXT"
#define MSG_READ          "READ"
#define MSG_READNEXT      "READNXT"
#define MSG_WRITE         "WRITE"
#define MSG_REWRITE       "REWRITE"
#define MSG_DELETE        "DELETE"
#define MSG_MOVE          "MOVE"
#define MSG_START         "START"
#define MSG_STTTWITH      "STRTWITH"
#define MSG_TRNSMODE      "SETTRNS"
#define MSG_AUTOCOMMIT    "SETAUTO"
#define MSG_COMMIT        "COMMIT"
#define MSG_ROLLBACK      "ROLLBACK"
#define MSG_SETOPTION     "SETOPTION"
#define MSG_GETOPTION     "GETOPTION"
/*********************************/
#define ACM_HOSTNAME_ENV  "ACM_HOSTNAME"
#define ACM_HOSTPORT_ENV  "ACM_HOSTPPRT"
#define ACM_USERNAME_ENV  "ACM_USERNAME"
#define ACM_PASSWORD_ENV  "ACM_PASSWORD"
/*********************************/
/**
 * �����åȤν����
 * �����С�¦�ؤΥ��ͥ�������Ω�ޤ�
 */
extern int initialize (char *hostname, char *hostport);

/**
 * ���ԥ����ɤ�����
 * @return int < 0 �����˼��Ԥ���
 */
extern int sendReturn (void);

/**
 * ��å�����������
 * @param message	��å�����
 */
extern int sendMessage (char *message);

/**
 * ��å������μ���
 * @return ������å�����Ĺ�ޤ��ϥ��顼
 *         ���顼���ˤ�0���֤�
 */
extern int recieveMessage ();

/**
 * �ե����륹�ơ������μ���
 * @return ������å�����Ĺ�ޤ��ϥ��顼
 *         ���顼���ˤ�0���֤�
 */
extern int recieveStatus ();

/**
 * ��å������μ���
 * @return ������å�����Ĺ�ޤ��ϥ��顼
 *         ���顼���ˤ�0���֤�
 */
extern int recieveRecord ();

/**���������*/
extern void initializeSession (char *hostname, char *hostport, char *username,
		   char *password, char *status);

/**���������*/
extern void initializeSessionEnv (char *status);

/**���������*/
extern void libACMClient ();

/**��λ����*/
extern void terminateSession (char *status);

/**�ե�����Υ�������
* @param	name	�ե�����μ��̻�
* @param	status	���ơ�����
*/
extern void assignACMFile (char *name, char *status);

/**
* �ե�����Υ����ץ�
* @param name	�ե�����̾
* @param openmode	�����ץ�⡼��<br/>
*			INPUT|OUTPUT|EXTEND|IO
* @param accessmode	���������⡼��
*			�������󥷥��|�����ʥߥå�|������
* @param status	���ơ�����
*/
extern void openACMFile (char *name, char *openmode, char *accessmode, char *status);

/**
 * �ե�������Ĥ���
 * @param name	�ե����뼱�̻�
 * @param status	���ơ�����
 */
extern void closeACMFile (char *name, char *status);

/**
 * ���Υ쥳���ɤ�
 * @param name	�ե����뼱�̻�
 * @param status	���ơ�����
 */
extern void nextACMRecord (char *name, char *status);

/**
 * �꡼�ɽ���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void readACMRecord (char *name, char *record, char *status);

/**
* Read Next ����
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void readNextACMRecord (char *name, char *record, char *status);

/**
 * �ɤ߹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void moveReadACMRecord (char *name, char *record, char *status);

/**
* ����ǥå�������ꤷ�����ַ������
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void moveReadACMRecordWith (char *name, char *record, char *indexname, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void writeACMRecord (char *name, char *record, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void rewriteACMRecord (char *name, char *record, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void deleteACMRecord (char *name, char *record, char *status);

/**
 * ���ַ������
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void moveACMRecord (char *name, char *record, char *status);

/**
* ����ǥå�������ꤷ�����ַ������
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void moveACMRecordWith (char *name, char *record, char *indexname, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void startACMRecord (char *name, char *record, char *startmode, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void startACMRecordWith (char *name, char *record, char *indexname, char *startmode, char *status);

/**
* ���ߥå�
* @param status ���ơ�����
*/
extern void commitACMSession (char *status);

/**
* ����Хå�
* @param status ���ơ�����
*/
extern void rollbackACMSession (char *status);

/**
* ���ߥåȥ⡼�ɤ����ꤹ��
* @param commitmode ���ߥåȥ⡼��
* @param status ���ơ�����
*/
extern void setACMCommitMode (char *commitmode, char *status);

/**
* �ȥ�󥶥�������٥�����ꤹ��
* @param transmode �ȥ�󥶥�������٥�
* @param status ���ơ�����
*/
extern void setACMTransMode (char *transmode, char *status);

extern void setACMOption (char *name, char *value);

extern void getACMOption (char *name, char *value);
