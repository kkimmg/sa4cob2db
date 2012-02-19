/***************************************/
#include    "acmclient.h"
/***************************************/
/** ステータスをセッションから取得する */
extern void getStatus(char *status);
/** レコードをセッションから取得する */
extern void getRecord(char *record);
/**初期化処理*/
extern void libJNIClient ();/**
 * ソケットの初期化
 * サーバー側へのコネクション確立まで
 */
extern int initializeJNI ();

/**初期化処理*/
extern void initializeJNISession (char *username, char *password, char *status);

/**初期化処理*/
extern void initializeJNISessionEnv (char *status);

/**終了処理*/
extern void terminateJNISession (char *status);

/**ファイルのアサイン
* @param	name	ファイルの識別子
* @param	status	ステータス
*/
extern void assignJNIFile (char *name, char *status);

/**
* ファイルのオープン
* @param name	ファイル名
* @param openmode	オープンモード<br/>
*			INPUT|OUTPUT|EXTEND|IO
* @param accessmode	アクセスモード
*			シーケンシャル|ダイナミック|ランダム
* @param status	ステータス
*/
extern void openJNIFile (char *name, char *openmode, char *accessmode, char *status);

/**
 * ファイルを閉じる
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void closeJNIFile (char *name, char *status);

/**
 * 次のレコードへ
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void nextJNIRecord (char *name, char *status);

/**
 * リード処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void readJNIRecord (char *name, char *record, char *status);

/**
* Read Next 処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void readNextJNIRecord (char *name, char *record, char *status);

/**
 * 読み込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void moveReadJNIRecord (char *name, char *record, char *status);

/**
* インデックスを指定した位置決定処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void moveReadJNIRecordWith (char *name, char *record, char *indexname, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void writeJNIRecord (char *name, char *record, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void rewriteJNIRecord (char *name, char *record, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void deleteJNIRecord (char *name, char *record, char *status);

/**
 * 位置決定処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void moveJNIRecord (char *name, char *record, char *status);

/**
* インデックスを指定した位置決定処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void moveJNIRecordWith (char *name, char *record, char *indexname, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void startJNIRecord (char *name, char *record, char *startmode, char *status);

/**
* 書き込み処理
* @param name	ファイル識別子
* @param record	レコード
* @param status	ステータス
*/
extern void startJNIRecordWith (char *name, char *record, char *indexname, char *startmode, char *status);

/**
* コミット
* @param status ステータス
*/
extern void commitJNISession (char *status);

/**
* ロールバック
* @param status ステータス
*/
extern void rollbackJNISession (char *status);

/**
* コミットモードを設定する
* @param commitmode コミットモード
* @param status ステータス
*/
extern void setJNICommitMode (char *commitmode, char *status);

/**
* トランザクションレベルを設定する
* @param transmode トランザクションレベル
* @param status ステータス
*/
extern void setJNITransMode (char *transmode, char *status);

extern void getOptionValue(char *record);

extern void setJNISetOption (char *name, char *value);

extern void setJNIGetOption (char *name, char *value);
