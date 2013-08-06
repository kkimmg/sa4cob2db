#include    <stdio.h>
#include    <unistd.h>
#include    <stdlib.h>
#include    <string.h>
#include    <errno.h>
#include    <signal.h>
#include    <sys/param.h>
#include    <sys/types.h>
/**************************************/
#include    <jni.h>
#include    "jniclient.h"
#include    "getJNIOption.h"
#include    "config.h"
/**************************************/
#define     SQLJNISERVER "k_kim_mg/sa4cob2db/sql/ACMSQLJNISession"
/**************************************/
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
jmethodID midGetReadingRecord, midGetStatus, midGetOptionValue;
jmethodID midGetOption, midSetOption;
jmethodID midSetLength;
struct servent *se;
int soc, portno, len;
char buf[255];
int record_len, record_max;
char *recbuf;
fd_set Mask, readOk;
int width;
struct timeval timeout;
int error;
char stat[3];
/* get status */
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
	(*env)->DeleteLocalRef(env, sarray);
	return;
}
/* get record */
extern void getRecord(char *record) {
	int i;
	jobject rarray;
	rarray = (*env)->CallObjectMethod(env, jniserv, midGetReadingRecord);
	jsize rlen = record_len;
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		record[i] = rpoint[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}
/*
 * JNI
 */
extern int initializeJNI() {
	record_len = RECORD_LEN;
	record_max = RECORD_MAX;
	recbuf = malloc(record_len);
	if (recbuf == NULL) {
		fprintf(stderr, "can't malloc\n");
		return (-1);
	}
	// JVM
	JavaVMOption options[2];
	options[0].optionString = getClasspath();
	options[1].optionString = getConfigFile();
	JavaVMInitArgs vm_args;
	vm_args.version = JNI_VERSION_1_6;
	vm_args.options = options;
	vm_args.nOptions = 2;
	JNI_GetDefaultJavaVMInitArgs(&vm_args);
	JNI_CreateJavaVM(&jvm, (void**) &env, &vm_args);
	// get class
	clazz = (*env)->FindClass(env, SQLJNISERVER);
	if (clazz == 0) {
		perror("SQLJNIServer Class Not Found.");
		return (-1);
	}
	// get methods
	jmethodID midInit = (*env)->GetMethodID(env, clazz, "<init>", "()V");
	jniserv = (*env)->NewObject(env, clazz, midInit);
	midAssign = (*env)->GetMethodID(env, clazz, "assign", "([B)V");
	midClose = (*env)->GetMethodID(env, clazz, "close", "([B)V");
	midCommitTransaction = (*env)->GetMethodID(env, clazz, "commitTransaction",
			"()V");
	midDelete = (*env)->GetMethodID(env, clazz, "delete", "([B[B)V");
	midInitialize = (*env)->GetMethodID(env, clazz, "initialize", "([B[B)V");
	midMove = (*env)->GetMethodID(env, clazz, "move", "([B[B)V");
	midNext = (*env)->GetMethodID(env, clazz, "next", "([B)V");
	midOpen = (*env)->GetMethodID(env, clazz, "open", "([B[B[B)V");
	midPrevious = (*env)->GetMethodID(env, clazz, "previous", "([B)V");
	midRead = (*env)->GetMethodID(env, clazz, "read", "([B)V");
	midReadNext = (*env)->GetMethodID(env, clazz, "readNext", "([B)V");
	midRewrite = (*env)->GetMethodID(env, clazz, "rewrite", "([B[B)V");
	midRollbackTransaction = (*env)->GetMethodID(env, clazz,
			"rollbackTransaction", "()V");
	midSetAutoCommit = (*env)->GetMethodID(env, clazz, "setAutoCommit",
			"([B)V");
	midSetTransactionLevel = (*env)->GetMethodID(env, clazz,
			"setTransactionLevel", "([B)V");
	midStart = (*env)->GetMethodID(env, clazz, "start", "([B[B[B)V");
	midStartWith = (*env)->GetMethodID(env, clazz, "startWith", "([B[B[B[B)V");
	midTerminate = (*env)->GetMethodID(env, clazz, "terminate", "()V");
	midWrite = (*env)->GetMethodID(env, clazz, "write", "([B[B)V");
	midGetReadingRecord = (*env)->GetMethodID(env, clazz, "getReadingRecord",
			"()[B");
	midGetStatus = (*env)->GetMethodID(env, clazz, "getStatus", "()[B");
	midGetOption = (*env)->GetMethodID(env, clazz, "getJNIOption", "([B)V");
	midSetOption = (*env)->GetMethodID(env, clazz, "setJNIOption", "([B[B)V");
	midGetOptionValue = (*env)->GetMethodID(env, clazz, "getOptionValue",
			"()[B");
	midSetLength = (*env)->GetMethodID(env, clazz, "setMaxLength", "(I)V");

	if (midAssign == 0 || midClose == 0 || midCommitTransaction == 0
			|| midDelete == 0 || midInitialize == 0 || midMove == 0
			|| midNext == 0 || midOpen == 0 || midPrevious == 0 || midRead == 0
			|| midReadNext == 0 || midRewrite == 0
			|| midRollbackTransaction == 0 || midSetAutoCommit == 0
			|| midSetTransactionLevel == 0 || midStart == 0 || midStartWith == 0
			|| midTerminate == 0 || midWrite == 0 || midGetReadingRecord == 0
			|| midGetStatus == 0 || midGetOption == 0 || midSetOption == 0
			|| midGetOptionValue == 0 || midSetLength == 0) {
		perror("method not found.");
		return -1;
	}
	return 0;
}

/* dummy for open COBOL */
extern void libJNIClient() {
}

/* initialize */
extern void initializeJNISession(char *username, char *password, char *status) {
	int i;
	/* JVM */
	if (initializeJNI() < 0) {
		strcpy(status, STATUS_FAILURE);
		return;
	}
	/* user */
	jsize ulen = strlen(username);
	jbyteArray uarray = (*env)->NewByteArray(env, ulen);
	jboolean ubl;
	jbyte *upoint = (*env)->GetByteArrayElements(env, uarray, &ubl);
	for (i = 0; i < ulen; i++) {
		upoint[i] = username[i];
	}
	(*env)->ReleaseByteArrayElements(env, uarray, upoint, 0);
	/* password */
	jsize plen = strlen(password);
	jbyteArray parray = (*env)->NewByteArray(env, plen);
	jboolean pbl;
	jbyte *ppoint = (*env)->GetByteArrayElements(env, parray, &pbl);
	for (i = 0; i < plen; i++) {
		ppoint[i] = password[i];
	}
	(*env)->ReleaseByteArrayElements(env, uarray, upoint, 0);
	/* call */
	(*env)->CallVoidMethod(env, jniserv, midInitialize, uarray, parray);
	getStatus(status);
	/* term */
	(*env)->DeleteLocalRef(env, uarray);
	(*env)->DeleteLocalRef(env, parray);
	return;
}

/* initialize session using environment values */
extern void initializeJNISessionEnv(char *status) {
	char *username, *password;
	username = getenv(ACM_USERNAME_ENV);
	if (username == NULL) {
		username = "        ";
	}
	password = getenv(ACM_PASSWORD_ENV);
	if (password == NULL) {
		password = "        ";
	}
	initializeJNISession(username, password, status);
}

/* terminate */
void terminateJNISession(char *status) {
	/* terminate */
	(*env)->CallVoidMethod(env, jniserv, midTerminate);
	getStatus(status);
	/* JVM */
	(*jvm)->DestroyJavaVM(jvm);
	/* term */
	free(recbuf);
	return;
}

/*
 * assign
 */
extern void assignJNIFile(char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	int i;
	/* file name */
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midAssign, narray);
	getStatus(status);
	/* term */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/*
 * open file
 */
extern void openJNIFile(char *name, char *openmode, char *accessmode,
		char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* open mode*/
	openmode[7] = '\0';
	jsize olen = strlen(openmode);
	jbyteArray oarray = (*env)->NewByteArray(env, olen);
	jboolean obl;
	jbyte *opoint = (*env)->GetByteArrayElements(env, oarray, &obl);
	for (i = 0; i < olen; i++) {
		opoint[i] = openmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, oarray, opoint, 0);
	/* access mode */
	accessmode[7] = '\0';
	jsize alen = strlen(accessmode);
	jbyteArray aarray = (*env)->NewByteArray(env, alen);
	jboolean abl;
	jbyte *apoint = (*env)->GetByteArrayElements(env, aarray, &abl);
	for (i = 0; i < alen; i++) {
		apoint[i] = accessmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, aarray, apoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midOpen, narray, oarray, aarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, oarray);
	(*env)->DeleteLocalRef(env, aarray);
	return;
}

/*
 * close file
 */
extern void closeJNIFile(char *name, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midAssign, narray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/*
 * next
 */
extern void nextJNIRecord(char *name, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midNext, narray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/*
 * read
 */
extern void readJNIRecord(char *name, char *record, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midRead, narray);
	/* record */
	getRecord(record);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/*
 * read next
 */
extern void readNextJNIRecord(char *name, char *record, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midReadNext, narray);
	/* record */
	getRecord(record);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	return;
}

/*
 * move read
 */
extern void moveReadJNIRecord(char *name, char *record, char *status) {
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

/*
 * write
 */
int k = 0;
extern void writeJNIRecord(char *name, char *record, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* record */
	jsize rlen = record_len;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midWrite, narray, rarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/*
 * rewrite
 */
extern void rewriteJNIRecord(char *name, char *record, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* record */
	jsize rlen = record_len;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midRewrite, narray, rarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/*
 * delete
 */
extern void deleteJNIRecord(char *name, char *record, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* record */
	jsize rlen = record_len;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midDelete, narray, rarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/*
 * move
 */
extern void moveJNIRecord(char *name, char *record, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* record */
	jsize rlen = record_len;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midMove, narray, rarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/*
 * start
 */
extern void startJNIRecord(char *name, char *record, char *startmode,
		char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* record */
	jsize rlen = record_len;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/* start mode */
	startmode[FILE_STARTMODE_MAX] = '\0';
	jsize mlen = strlen(startmode);
	jbyteArray marray = (*env)->NewByteArray(env, mlen);
	jboolean mbl;
	jbyte *mpoint = (*env)->GetByteArrayElements(env, marray, &mbl);
	for (i = 0; i < mlen; i++) {
		mpoint[i] = startmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, marray, mpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midStart, narray, marray, rarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, marray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/*
 * start by index
 */
extern void startJNIRecordWith(char *name, char *record, char *indexname,
		char *startmode, char *status) {
	int i;
	/* file name */
	name[FILE_IDENT_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* record */
	jsize rlen = record_len;
	jbyteArray rarray = (*env)->NewByteArray(env, rlen);
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		rpoint[i] = record[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/* start mode */
	startmode[FILE_STARTMODE_MAX] = '\0';
	jsize mlen = strlen(startmode);
	jbyteArray marray = (*env)->NewByteArray(env, mlen);
	jboolean mbl;
	jbyte *mpoint = (*env)->GetByteArrayElements(env, marray, &mbl);
	for (i = 0; i < mlen; i++) {
		mpoint[i] = startmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, marray, mpoint, 0);
	/* index */
	indexname[FILE_INDEXNAME_MAX] = '\0';
	jsize ilen = strlen(indexname);
	jbyteArray iarray = (*env)->NewByteArray(env, ilen);
	jboolean ibl;
	jbyte *ipoint = (*env)->GetByteArrayElements(env, iarray, &ibl);
	for (i = 0; i < ilen; i++) {
		ipoint[i] = indexname[i];
	}
	(*env)->ReleaseByteArrayElements(env, iarray, ipoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midStartWith, narray, iarray, marray,
			rarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, iarray);
	(*env)->DeleteLocalRef(env, marray);
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/*
 * commit
 */
extern void commitJNISession(char *status) {
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midCommitTransaction);
	/* status */
	getStatus(status);
	return;
}

/*
 * rollback
 */
extern void rollbackJNISession(char *status) {
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midRollbackTransaction);
	/* status */
	getStatus(status);
	return;
}

/*
 * set commit mode
 */
extern void setJNICommitMode(char *commitmode, char *status) {
	int i;
	/* mode */
	commitmode[FILE_OPTION_MAX] = '\0';
	jsize clen = strlen(commitmode);
	jbyteArray carray = (*env)->NewByteArray(env, clen);
	jboolean cbl;
	jbyte *cpoint = (*env)->GetByteArrayElements(env, carray, &cbl);
	for (i = 0; i < clen; i++) {
		cpoint[i] = commitmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, carray, cpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midSetAutoCommit, carray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, carray);
	return;
}

/*
 * transaction level
 */
extern void setJNITransMode(char *transmode, char *status) {
	int i;
	/* levle */
	transmode[FILE_OPTION_MAX] = '\0';
	jsize tlen = strlen(transmode);
	jbyteArray tarray = (*env)->NewByteArray(env, tlen);
	jboolean tbl;
	jbyte *tpoint = (*env)->GetByteArrayElements(env, tarray, &tbl);
	for (i = 0; i < tlen; i++) {
		tpoint[i] = transmode[i];
	}
	(*env)->ReleaseByteArrayElements(env, tarray, tpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midSetTransactionLevel, tarray);
	/* status */
	getStatus(status);
	/* terminate */
	(*env)->DeleteLocalRef(env, tarray);
	return;
}

/* get option value */
extern void getOptionValue(char *value) {
	int i;
	jobject rarray;
	rarray = (*env)->CallObjectMethod(env, jniserv, midGetOptionValue);
	jsize rlen = OPTIONVALUE_LEN;
	jboolean rbl;
	jbyte *rpoint = (*env)->GetByteArrayElements(env, rarray, &rbl);
	for (i = 0; i < rlen; i++) {
		value[i] = rpoint[i];
	}
	(*env)->ReleaseByteArrayElements(env, rarray, rpoint, 0);
	/* terminate */
	(*env)->DeleteLocalRef(env, rarray);
	return;
}

/** set option */
extern void
setJNIOptionFromEnv (char *name, char *envkey, char *status) {
	char *wvalue;
	char value[OPTIONVALUE_LEN];
	envkey[OPTIONVALUE_MAX] = '\0';
	int i;
	for (i = OPTIONVALUE_MAX - 1; i > 0; i--) {
		if (envkey[i] == ' ') {
			envkey[i] = '\0';
		} else {
			break;
		}
	}
	wvalue = getenv(envkey);
	if (wvalue == NULL) {
		wvalue = '\0';
	} else if (strlen(wvalue) >= OPTIONVALUE_LEN) {
		wvalue[OPTIONVALUE_MAX] = '\0';
	}
	memset(value, '\0', OPTIONVALUE_MAX);
	strcpy(value, wvalue);
	setJNIOption(name, value, status);
	memcpy(envkey, value, OPTIONVALUE_LEN);
}

/* set option value */
extern void setJNIOption(char *name, char *value) {
	int i;
	/* name */
	name[OPTIONNAME_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* value */
	value[OPTIONVALUE_MAX] = '\0';
	jsize vlen = strlen(value);
	jbyteArray varray = (*env)->NewByteArray(env, vlen);
	jboolean vbl;
	jbyte *vpoint = (*env)->GetByteArrayElements(env, varray, &vbl);
	for (i = 0; i < vlen; i++) {
		vpoint[i] = value[i];
	}
	(*env)->ReleaseByteArrayElements(env, varray, vpoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midSetOption, narray, varray);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	(*env)->DeleteLocalRef(env, varray);
	return;
}

/* get optin */
extern void getJNIOption(char *name, char *value) {
	int i;
	/* name */
	name[OPTIONNAME_MAX] = '\0';
	jsize nlen = strlen(name);
	jbyteArray narray = (*env)->NewByteArray(env, nlen);
	jboolean nbl;
	jbyte *npoint = (*env)->GetByteArrayElements(env, narray, &nbl);
	for (i = 0; i < nlen; i++) {
		npoint[i] = name[i];
	}
	(*env)->ReleaseByteArrayElements(env, narray, npoint, 0);
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midGetOption, narray);
	/* terminate */
	(*env)->DeleteLocalRef(env, narray);
	/* value */
	getOptionValue(value);
	return;
}

/* set row length */
extern void setJNIMaxLength(char *length) {
	/* value */
	length[OPTIONVALUE_MAX] = '\0';
	int w_length;
	if ((w_length = atoi(length)) == 0) {
		fprintf(stderr, "bad length\n");
		return;
	}
	jint j_length;
	j_length = w_length;
	/* call method */
	(*env)->CallVoidMethod(env, jniserv, midSetLength, j_length);
	//
	free(recbuf);
	record_len = w_length;
	record_max = record_len - 1;
	recbuf = malloc(record_len);
	if (recbuf == NULL) {
		fprintf(stderr, "can't malloc\n");
		return;
	}
	return;
}
