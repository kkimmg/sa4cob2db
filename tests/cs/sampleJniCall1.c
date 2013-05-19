#include <libcob.h>
#include "JSampleJniCall1.h"
typedef char byte;
static int (*cobol_sub_program)(byte *head, byte *bodyIn, byte *bodyOut);
/**static void jcharTochar(jchar *src, char *dist, int length);*/
static void jbyteTobyte(jbyte *src, byte *dist, int length);
static void byteTojbyte(byte *src, jbyte *dist, int length);
/**
 * run program
 */
JNIEXPORT jint JNICALL Java_JSampleJniCall1_sampleJniCall2
  (JNIEnv *env, jobject obj, jstring library, jstring prog, jbyteArray head, jbyteArray bodyIn, jbyteArray bodyOut) {
	jint ret;
	/*library name*/
	const char* slibr;
	jboolean isCopy1;
	slibr = (*env)->GetStringUTFChars(env, library, &isCopy1);
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

	/* set library */
	cob_set_library_path(slibr);

	/* get program */
	cobol_sub_program = cob_resolve(sprog);

	/* error check */
	if (cobol_sub_program == NULL) {
		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}

	/* execute */
	ret = cobol_sub_program(shead, sbodyIn, sbodyOut);

	byteTojbyte(shead, phead, hLength);
	byteTojbyte(sbodyIn, pbodyIn, bInLength);
	byteTojbyte(sbodyOut, pbodyOut, bOutLength);
	/* back */
	(*env)->ReleaseByteArrayElements(env, head, phead, 0);
	(*env)->ReleaseByteArrayElements(env, bodyIn, pbodyIn, 0);
	(*env)->ReleaseByteArrayElements(env, bodyOut, pbodyOut, 0);

	/* release string */
	if (isCopy1 == JNI_TRUE) {
		(*env)->ReleaseStringUTFChars(env, library, slibr);
	}
	if (isCopy2 == JNI_TRUE) {
		(*env)->ReleaseStringUTFChars(env, prog, sprog);
	}

	/* ok? */
	return ret;
}
/**
 * run program
 */
JNIEXPORT jint JNICALL Java_JSampleJniCall1_sampleJniCall1
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

	cob_init(0, NULL);

	/* error check */
	cobol_sub_program = cob_resolve(sprog);

	/* error check */
	if (cobol_sub_program == NULL) {
 		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}

	/* execute*/
	ret = cobol_sub_program(shead, sbodyIn, sbodyOut);

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



