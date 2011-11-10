#include    <stdio.h>
#include    <unistd.h>
#include    <stdlib.h>
#include    <string.h>
#include    <errno.h>
#include    <signal.h>
#include    <sys/param.h>
#include    <sys/types.h>
/***************************************/
#include    <jni.h>
#include    "jniclient.h"
#include    "getJNIOption.h"
#include    "config.h"
/***************************************/
#define     SQLJNISERVER "k_kim_mg/sa4cob2db/sql/ACMSQLJNISession"
/***************************************/
JNIEnv *env;
JavaVM *jvm;
jclass clazz;
jobject jniserv;
jmethodID midAssign;
jmethodID midClose;
jmethodID midCommitTransaction;
jmethodID midDelete;
jmethodID midInitialize;
jmethodID midMove;
jmethodID midNext;
jmethodID midOpen;
jmethodID midPrevious;
jmethodID midRead;
jmethodID midReadNext;
jmethodID midRewrite;
jmethodID midRollbackTransaction;
jmethodID midSetAutoCommit;
jmethodID midSetTransactionLevel;
jmethodID midStart;
jmethodID midStartWith;
jmethodID midTerminate;
jmethodID midWrite;
jmethodID midGetReadingRecord, midGetStatus;
struct servent *se;
int soc, portno, len;
char buf[255];
char recbuf[RECORD_LEN];
fd_set Mask, readOk;
int width;
struct timeval timeout;
int error;
char stat[3];
/** ステータスをセッションから取得する */
extern void getStatus(char *status) {
	int i;
	jobject sarray;
	sarray = (*env)->CallObjectMethod(env, jniserv, midGetStatus);
	jsize slen = 255;
	jboolean sbl;
	jbyte *spoint = (*env)->GetByteArrayElements(env, sarray, &sbl); 
	for (i = 0; i < slen; i++) {
		status[i] = spoint[i];
	}
	(*env)->ReleaseByteArrayElements(env, sarray, spoint, 0);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, sarray);
	return;
}
/** レコードをセッションから取得する */
extern void getRecord(char *record) {
	int i;
	jobject rarray;
	rarray = (*env)->CallObjectMethod(env, jniserv, midGetReadingRecord);
	jsize rlen = 8192;
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl); 
	for (i = 0; i < rlen; i++) {
		record[i] = rpoint[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, rarray);
	return;
}
/**
 * JNI環境の初期化
 */
extern int
initializeJNI () {
	// JVMを作成する
	JavaVMOption options[2];
	options[0].optionString = getClasspath();
	options[1].optionString = getConfigFile();
	JavaVMInitArgs vm_args;
	vm_args.version = JNI_VERSION_1_6;
	vm_args.options = options;
	vm_args.nOptions = 2;
	JNI_GetDefaultJavaVMInitArgs(&vm_args);
	JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
	// クラスの取得
	clazz = (*env)->FindClass(env, SQLJNISERVER);
	if (clazz == 0) {
		perror("SQLJNIServer Class Not Found.");
		return (-1);
	}
	// コンストラクタの取得
	jmethodID midInit
			= (*env)->GetMethodID(env, clazz, "<init>",	"()V");
	jniserv = (*env)->NewObject(env, clazz, midInit);
	midAssign	= (*env)->GetMethodID(env, clazz, "assign",	"([B)V");
	midClose	= (*env)->GetMethodID(env, clazz, "close",	"([B)V");
	midCommitTransaction 
			= (*env)->GetMethodID(env, clazz, "commitTransaction",
									"()V");
	midDelete	= (*env)->GetMethodID(env, clazz, "delete",	"([B[B)V");
	midInitialize	= (*env)->GetMethodID(env, clazz, "initialize",	"([B[B)V");
	midMove		= (*env)->GetMethodID(env, clazz, "move",	"([B[B)V");
	midNext		= (*env)->GetMethodID(env, clazz, "next",	"([B)V");
	midOpen		= (*env)->GetMethodID(env, clazz, "open",	"([B[B[B)V");
	midPrevious	= (*env)->GetMethodID(env, clazz, "previous",	"([B)V");
	midRead		= (*env)->GetMethodID(env, clazz, "read",	"([B)V");
	midReadNext	= (*env)->GetMethodID(env, clazz, "readNext",	"([B)V");
	midRewrite	= (*env)->GetMethodID(env, clazz, "rewrite",	"([B[B)V");
	midRollbackTransaction
			= (*env)->GetMethodID(env, clazz, "rollbackTransaction",
									"()V");
	midSetAutoCommit
			= (*env)->GetMethodID(env, clazz, "setAutoCommit",
									"([B)V");
	midSetTransactionLevel
			= (*env)->GetMethodID(env, clazz, "setTransactionLevel",
									"([B)V");
	midStart	= (*env)->GetMethodID(env, clazz, "start",	"([B[B[B)V");
	midStartWith	= (*env)->GetMethodID(env, clazz, "startWith",	"([B[B[B[B)V");
	midTerminate	= (*env)->GetMethodID(env, clazz, "terminate",	"()V");
	midWrite	= (*env)->GetMethodID(env, clazz, "write",	"([B[B)V");
	midGetReadingRecord
			= (*env)->GetMethodID(env, clazz, "getReadingRecord",
									"()[B");
	midGetStatus	= (*env)->GetMethodID(env, clazz, "getStatus",	"()[B");
	if (midAssign == 0 || midClose == 0 || midCommitTransaction == 0 
	 || midDelete == 0 || midInitialize == 0 || midMove == 0 || midNext == 0 
	 || midOpen == 0 || midPrevious == 0 || midRead == 0 || midReadNext == 0 
	 || midRewrite == 0 || midRollbackTransaction == 0 || midSetAutoCommit == 0
	 || midSetTransactionLevel == 0 || midStart == 0 || midStartWith == 0
	 || midTerminate == 0 || midWrite == 0 || midGetReadingRecord == 0
	 || midGetStatus == 0) {
		perror("method not found.");
		return -1;
	}
	return 0;
}

/**初期化処理*/
extern void
libJNIClient () {
}

/**初期化処理*/
extern void
initializeJNISession (char *username, char *password, char *status) {
	int i;
	/* 初期化の初期化！ */
	if (initializeJNI () < 0) {
		strcpy (status, STATUS_FAILURE);
		return;
	}
	/** ユーザー名 */
	jsize ulen = strlen(username);
	jbyteArray uarray = (*env)->NewByteArray(env, ulen);
	jboolean ubl;
	jbyte *upoint = (*env)->GetByteArrayElements(env, uarray, &ubl); 
	for (i = 0; i < ulen; i++) {
		upoint[i] = username[i];
	}
	(*env)->ReleaseByteArrayElements(env, uarray, upoint, 0);
	/** パスワード */
	jsize plen = strlen(password);
	jbyteArray parray = (*env)->NewByteArray(env, plen);
	jboolean pbl;
	jbyte *ppoint = (*env)->GetByteArrayElements(env, parray, &pbl); 
	for (i = 0; i < plen; i++) {
		ppoint[i] = password[i];
	}
	(*env)->ReleaseByteArrayElements(env, uarray, upoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midInitialize, uarray, parray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, uarray);
	(*env)->DeleteLocalRef(env, parray);
	return;
}

/**初期化処理*/
extern void
initializeJNISessionEnv (char *status) {
	char *username, *password;
	username = getenv(ACM_USERNAME_ENV);
	if (username == NULL) {
		username = "        ";
	}
	password = getenv(ACM_PASSWORD_ENV);
	if (password == NULL) {
		password = "        ";
	}
	initializeJNISession (username, password, status);
}

/**終了処理*/
void
terminateJNISession (char *status) {
	/* 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midTerminate);
	/* ステータスチェック */
	getStatus(status);
	/* JVMを破棄する */
	(*jvm)->DestroyJavaVM(jvm);
	return;
}

/**ファイルのアサイン
 * @param	name	ファイルの識別子
 * @param	status	ステータス
 */
extern void
assignJNIFile (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';	
	int i;
	/** ファイル名 */
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midAssign, narray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/**
 * ファイルのオープン
 * @param name	ファイル名
 * @param openmode	オープンモード<br/>
 *			INPUT|OUTPUT|EXTEND|IO
 * @param accessmode	アクセスモード
 *			シーケンシャル|ダイナミック|ランダム
 * @param status	ステータス
 */
extern void
openJNIFile (char *name, char *openmode, char *accessmode, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* オープンモード */
	openmode[7] = '\0';
	jsize olen = strlen(openmode);
	jbyteArray oarray = (*env)->NewByteArray(env, olen);
	jboolean obl;
	jbyte *opoint = (*env)->GetByteArrayElements(env, oarray, &obl); 
	for (i = 0; i < olen; i++) {
		opoint[i] = openmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, oarray, opoint, 0);
	/* アクセスモード */
	accessmode[7] = '\0';
	jsize alen = strlen(accessmode);
	jbyteArray aarray = (*env)->NewByteArray(env, alen);
	jboolean abl;
	jbyte *apoint = (*env)->GetByteArrayElements(env, aarray, &abl); 
	for (i = 0; i < alen; i++) {
		apoint[i] = accessmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, aarray, apoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midOpen, narray, oarray, aarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, oarray);
	(*env)->DeleteLocalRef(env, aarray);
	return;
}

/**
 * ファイルを閉じる
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void
closeJNIFile (char *name, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midAssign, narray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/**
 * 次のレコードへ
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void
nextJNIRecord (char *name, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midNext, narray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/**
 * リード処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
readJNIRecord (char *name, char *record, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midRead, narray);
	/* 受信したデータをレコード領域に転送 */
	getRecord(record);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/**
 * ReadNext処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
readNextJNIRecord (char *name, char *record, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midReadNext, narray);
	/* 受信したデータをレコード領域に転送 */
	getRecord(record);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/**
 * 位置決定及び読み込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
moveReadJNIRecord (char *name, char *record, char *status) {
	moveJNIRecord(name, record, status);
	stat[0] = status[0];
	stat[1] = status[1];
	stat[2] = '\0';
	if (strcmp(stat, STATUS_OK) != 0) {
		return;
	}
	readJNIRecord(name, record, status);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
int k = 0;
extern void
writeJNIRecord (char *name, char *record, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** レコード */
	jsize rlen = RECORD_LEN;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midWrite, narray, rarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
rewriteJNIRecord (char *name, char *record, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** レコード */
	jsize rlen = RECORD_LEN;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl); 
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midRewrite, narray, rarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
deleteJNIRecord (char *name, char *record, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** レコード */
	jsize rlen = RECORD_LEN;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl); 
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midDelete, narray, rarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
moveJNIRecord (char *name, char *record, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** レコード */
	jsize rlen = RECORD_LEN;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl); 
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midMove, narray, rarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
startJNIRecord (char *name, char *record, char *startmode, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** レコード */
	jsize rlen = RECORD_LEN;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl); 
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/** スタートモード */
	startmode[FILE_STARTMODE_MAX] = '\0';
	jsize mlen = strlen(startmode);
	jbyteArray marray = (*env)->NewByteArray(env, mlen);
	jboolean mbl;
	jbyte *mpoint = (*env)->GetByteArrayElements(env, marray, &mbl); 
	for (i = 0; i < mlen; i++) {
		mpoint[i] = startmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, marray, mpoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midStart, narray, marray, rarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, marray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
startJNIRecordWith (char *name, char *record, char *indexname, char *startmode, char *status) {
	int i;
	/** ファイル名 */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl); 
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/** レコード */
	jsize rlen = RECORD_LEN;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl); 
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/** スタートモード */
	startmode[FILE_STARTMODE_MAX] = '\0';
	jsize mlen = strlen(startmode);
	jbyteArray marray = (*env)->NewByteArray(env, mlen);
	jboolean mbl;
	jbyte *mpoint = (*env)->GetByteArrayElements(env, marray, &mbl); 
	for (i = 0; i < mlen; i++) {
		mpoint[i] = startmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, marray, mpoint, 0);
	/** 索引名 */
	indexname[FILE_INDEXNAME_MAX] = '\0';
	jsize ilen = strlen(indexname);
	jbyteArray iarray = (*env)->NewByteArray(env, ilen);
	jboolean ibl;
	jbyte *ipoint = (*env)->GetByteArrayElements(env, iarray, &ibl); 
	for (i = 0; i < ilen; i++) {
		ipoint[i] = indexname[i];
	}
	(*env)->ReleaseByteArrayElements(env, iarray, ipoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midStartWith, narray, iarray, marray, rarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, iarray);
	(*env)->DeleteLocalRef(env, marray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/**
* コミット
* @param status ステータス
*/
extern void
commitJNISession (char *status) {
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midCommitTransaction);
	/* ステータスチェック */
	getStatus(status);
	return;
}

/**
* ロールバック
* @param status ステータス
*/
extern void
rollbackJNISession (char *status) {
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midRollbackTransaction);
	/* ステータスチェック */
	getStatus(status);
	return;
}

/**
* コミットモードを設定する
* @param commitmode コミットモード
* @param status ステータス
*/
extern void
setJNICommitMode (char *commitmode, char *status) {
	int i;
	/** コミットモード */
	commitmode[255] = '\0';
	jsize clen = strlen(commitmode);
	jbyteArray carray = (*env)->NewByteArray(env, clen);
	jboolean cbl;
	jbyte *cpoint = (*env)->GetByteArrayElements(env, carray, &cbl); 
	for (i = 0; i < clen; i++) {
		cpoint[i] = commitmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, carray, cpoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midSetAutoCommit, carray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, carray);
	return;
}

/**
* トランザクションレベルを設定する
* @param transmode トランザクションレベル
* @param status ステータス
*/
extern void
setJNITransMode (char *transmode, char *status) {
	int i;
	/** トランザクションモード */
	transmode[255] = '\0';
	jsize tlen = strlen(transmode);
	jbyteArray tarray = (*env)->NewByteArray(env, tlen);
	jboolean tbl;
	jbyte *tpoint = (*env)->GetByteArrayElements(env, tarray, &tbl); 
	for (i = 0; i < tlen; i++) {
		tpoint[i] = transmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, tarray, tpoint, 0);
	/** 処理を呼び出してみる */
	(*env)->CallVoidMethod(env, jniserv, midSetTransactionLevel, tarray);
	/* ステータスチェック */
	getStatus(status);
	/** ローカル参照を削除する */
	(*env)->DeleteLocalRef(env, tarray);
	return;
}

