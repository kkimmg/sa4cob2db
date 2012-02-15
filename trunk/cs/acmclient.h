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
 * ソケットの初期化
 * サーバー側へのコネクション確立まで
 */
extern int initialize (char *hostname, char *hostport);

/**
 * 改行コードの送信
 * @return int < 0 送信に失敗した
 */
extern int sendReturn (void);

/**
 * メッセージの送信
 * @param message	メッセージ
 */
extern int sendMessage (char *message);

/**
 * メッセージの受信
 * @return 受信メッセージ長またはエラー
 *         エラー時にも0を返す
 */
extern int recieveMessage ();

/**
 * ファイルステータスの受信
 * @return 受信メッセージ長またはエラー
 *         エラー時にも0を返す
 */
extern int recieveStatus ();

/**
 * メッセージの受信
 * @return 受信メッセージ長またはエラー
 *         エラー時にも0を返す
 */
extern int recieveRecord ();

/**初期化処理*/
extern void initializeSession (char *hostname, char *hostport, char *username,
		   char *password, char *status);

/**初期化処理*/
extern void initializeSessionEnv (char *status);

/**初期化処理*/
extern void libACMClient ();

/**終了処理*/
extern void terminateSession (char *status);

/**ファイルのアサイン
* @param	name	ファイルの識別子
* @param	status	ステータス
*/
extern void assignACMFile (char *name, char *status);

/**
* ファイルのオープン
* @param name	ファイル名
* @param openmode	オープンモード<br/>
*			INPUT|OUTPUT|EXTEND|IO
* @param accessmode	アクセスモード
*			シーケンシャル|ダイナミック|ランダム
* @param status	ステータス
*/
extern void openACMFile (char *name, char *openmode, char *accessmode, char *status);

/**
 * ファイルを閉じる
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void closeACMFile (char *name, char *status);

/**
 * 次のレコードへ
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void nextACMRecord (char *name, char *status);

/**
 * リード処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void readACMRecord (char *name, char *record, char *status);

/**
* Read Next 処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void readNextACMRecord (char *name, char *record, char *status);

/**
 * 読み込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void moveReadACMRecord (char *name, char *record, char *status);

/**
* インデックスを指定した位置決定処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void moveReadACMRecordWith (char *name, char *record, char *indexname, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void writeACMRecord (char *name, char *record, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void rewriteACMRecord (char *name, char *record, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void deleteACMRecord (char *name, char *record, char *status);

/**
 * 位置決定処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void moveACMRecord (char *name, char *record, char *status);

/**
* インデックスを指定した位置決定処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void moveACMRecordWith (char *name, char *record, char *indexname, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void startACMRecord (char *name, char *record, char *startmode, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void startACMRecordWith (char *name, char *record, char *indexname, char *startmode, char *status);

/**
* コミット
* @param status ステータス
*/
extern void commitACMSession (char *status);

/**
* ロールバック
* @param status ステータス
*/
extern void rollbackACMSession (char *status);

/**
* コミットモードを設定する
* @param commitmode コミットモード
* @param status ステータス
*/
extern void setACMCommitMode (char *commitmode, char *status);

/**
* トランザクションレベルを設定する
* @param transmode トランザクションレベル
* @param status ステータス
*/
extern void setACMTransMode (char *transmode, char *status);

extern void setACMOption (char *name, char *value);

extern void getACMOption (char *name, char *value);
