#include <stdlib.h>
#include <libcob.h>
#include <sys/wait.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <stdio.h>
#include "k_kim_mg_sa4cob2db_cobsub_JSampleJniCall1.h"
typedef char byte;
static int (*cobol_sub_program)(byte *head, byte *bodyIn, byte *bodyOut);
/**static void jcharTochar(jchar *src, char *dist, int length);*/
static void jbyteTobyte(jbyte *src, byte *dist, int length);
static void byteTojbyte(byte *src, jbyte *dist, int length);
/**
 * run program
 */
JNIEXPORT jint JNICALL Java_k_1kim_1mg_sa4cob2db_cobsub_JSampleJniCall1_sampleJniCall_1nonfork
  (JNIEnv *env, jobject obj, jstring prog, jbyteArray head, jbyteArray bodyIn, jbyteArray bodyOut) {
	jint ret;
	/*program name*/
	const char* sprog;
	jboolean isCopy2;
	sprog = (*env)->GetStringUTFChars(env, prog, &isCopy2);
	/*header*/
	jsize hLength = (*env)->GetArrayLength (env, head);
	jbyte *phead  = (*env)->GetByteArrayElements(env, head, NULL );
	byte shead[hLength];
	jbyteTobyte(phead, shead, hLength);
	/*body(input)*/
	jsize bInLength = (*env)->GetArrayLength(env, bodyIn);
	jbyte *pbodyIn  = (*env)->GetByteArrayElements(env, bodyIn, NULL );
	byte sbodyIn[bInLength];
	jbyteTobyte(pbodyIn, sbodyIn, bInLength);
	/*body(output)*/
	jsize bOutLength = (*env)->GetArrayLength(env, bodyOut);
	jbyte *pbodyOut  = (*env)->GetByteArrayElements(env, bodyOut, NULL );
	byte sbodyOut[bOutLength];
	jbyteTobyte(pbodyOut, sbodyOut, bOutLength);

	/*  */
	cob_init(0, NULL);

	/* get program */
	cobol_sub_program = cob_resolve(sprog);

	/* error check */
	if (cobol_sub_program == NULL) {
		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}

	/* execute */
	ret = cobol_sub_program(shead, sbodyIn, sbodyOut);
	
	/*fprintf("shead:%s\n", shead);*/
	/*fprintf("sbodyIn:%s\n", sbodyIn);*/
	printf("sbodyOut:%s\n", sbodyOut);
	
	byteTojbyte(shead, phead, hLength);
	byteTojbyte(sbodyIn, pbodyIn, bInLength);
	byteTojbyte(sbodyOut, pbodyOut, bOutLength);
	/* back */
	(*env)->ReleaseByteArrayElements(env, head, phead, 0);
	(*env)->ReleaseByteArrayElements(env, bodyIn, pbodyIn, 0);
	(*env)->ReleaseByteArrayElements(env, bodyOut, pbodyOut, 0);

	if (isCopy2 == JNI_TRUE) {
		(*env)->ReleaseStringUTFChars(env, prog, sprog);
	}

	/* ok? */
	return ret;
}
/**
 * run program
 */
JNIEXPORT jint Java_k_1kim_1mg_sa4cob2db_cobsub_JSampleJniCall1_sampleJniCall_1fork
  (JNIEnv *env, jobject obj, jstring prog, jbyteArray head, jbyteArray bodyIn, jbyteArray bodyOut) {
	int st;
	pid_t pid;
	jint ret;
	/*program name*/
	const char* sprog;
	jboolean isCopy2;
	sprog = (*env)->GetStringUTFChars(env, prog, &isCopy2);
	/*header*/
	jsize hLength = (*env)->GetArrayLength (env, head);
	jbyte *phead  = (*env)->GetByteArrayElements(env, head, NULL );
	byte *shead;
	int headid = shmget(IPC_PRIVATE, sizeof(byte) * hLength, 0600);
	shead = shmat(headid, 0, 0);
	jbyteTobyte(phead, shead, hLength);
	/*body(input)*/
	jsize bInLength = (*env)->GetArrayLength(env, bodyIn);
	jbyte *pbodyIn  = (*env)->GetByteArrayElements(env, bodyIn, NULL );
	byte *sbodyIn;
	int bodyInid = shmget(IPC_PRIVATE, sizeof(byte) * bInLength, 0600);
	sbodyIn = shmat(bodyInid, 0, 0);
	jbyteTobyte(pbodyIn, sbodyIn, bInLength);
	/*body(output)*/
	jsize bOutLength = (*env)->GetArrayLength(env, bodyOut);
	jbyte *pbodyOut  = (*env)->GetByteArrayElements(env, bodyOut, NULL );
	byte *sbodyOut;
	int bodyOutid = shmget(IPC_PRIVATE, sizeof(byte) * bOutLength, 0600);
	sbodyOut = shmat(bodyOutid, 0, 0);
	jbyteTobyte(pbodyOut, sbodyOut, bOutLength);

	cob_init(0, NULL);

	/* error check */
	cobol_sub_program = cob_resolve(sprog);

	/* error check */
	if (cobol_sub_program == NULL) {
 		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}
	/* fork */
	pid = fork();
	if  (pid == -1) {
		//couldn't create process 
		// execute 
		ret = cobol_sub_program(shead, sbodyIn, sbodyOut);
	} else if  (pid == 0) {
		// child process 
		// execute 
		byte *fhead; fhead = shmat(headid, 0, 0);
		byte *fbodyIn; fbodyIn = shmat(bodyInid, 0, 0);
		byte *fbodyOut; fbodyOut = shmat(bodyOutid, 0, 0);
		ret = cobol_sub_program(fhead, fbodyIn, fbodyOut);
		exit(EXIT_SUCCESS);
	} else {
		// parent 
		pid = wait(&st);
		if  (pid  == -1) {
			ret = -1;
		}
	}
	/* bytes to jbytes */
	byteTojbyte(shead, phead, hLength);
	byteTojbyte(sbodyIn, pbodyIn, bInLength);
	byteTojbyte(sbodyOut, pbodyOut, bOutLength);
	/* back */
	(*env)->ReleaseByteArrayElements(env, head, phead, 0);
	(*env)->ReleaseByteArrayElements(env, bodyIn, pbodyIn, 0);
	(*env)->ReleaseByteArrayElements(env, bodyOut, pbodyOut, 0);

	/* release string */
	if (isCopy2 == JNI_TRUE) {
		(*env)->ReleaseStringUTFChars(env, prog, sprog);
	}

	/* ok? */
	return ret;
}
/*
static void jcharTochar(jchar *src, char *dist, int length) {
	int i;
	for (i = 0; i < length; i++) {
		dist[i] = src[i];
	}
}
*/

static void jbyteTobyte(jbyte *src, byte *dist, int length) {
	int i;
	for (i = 0; i < length; i++) {
		dist[i] = src[i];
	}
}

static void byteTojbyte(byte *src, jbyte *dist, int length) {
	int i;
	for (i = 0; i < length; i++) {
		dist[i] = src[i];
	}
}



