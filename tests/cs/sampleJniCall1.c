#include <libcob.h>
#include "JSampleJniCall1.h"
/**
 * �ۤȤ��OpenCobol���餽�Τޤޤ��äƤ����Τ���
 * �����֤��ǻȤ���?
 */
typedef char byte;
static int (*cobol_sub_program)(byte *head, byte *bodyIn, byte *bodyOut);
/**static void jcharTochar(jchar *src, char *dist, int length);*/
static void jbyteTobyte(jbyte *src, byte *dist, int length);
static void byteTojbyte(byte *src, jbyte *dist, int length);
/**
 * ���ꤷ���ץ�����ư����(�饤�֥����ꤢ��)
 * env java�Ķ�
 * obj ����?�ȤäƤʤ���?
 * library �饤�֥��ѥ�
 * prog �ץ����̾
 * head �إå�
 * bodyIn ����ѥ�᡼��
 * bodyOut �����ȥѥ�᡼��
 */
JNIEXPORT jint JNICALL Java_JSampleJniCall1_sampleJniCall2
  (JNIEnv *env, jobject obj, jstring library, jstring prog, jbyteArray head, jbyteArray bodyIn, jbyteArray bodyOut) {
	/* �ʤ��Cobol������ͤ�int�ʤΤ���������� */
	jint ret;
	/*�饤�֥��̾*/
	const char* slibr;
	jboolean isCopy1;
	slibr = (*env)->GetStringUTFChars(env, library, &isCopy1);
	/*�ץ����̾*/
	const char* sprog;
	jboolean isCopy2;
	sprog = (*env)->GetStringUTFChars(env, prog, &isCopy2);
	/*�إå�*/
	jsize hLength = (*env)->GetArrayLength (env, head);
	jbyte *phead  = (*env)->GetByteArrayElements(env, head, NULL );
	byte shead[hLength];
	jbyteTobyte(phead, shead, hLength);
	/*�ܥǥ�(����)*/
	jsize bInLength = (*env)->GetArrayLength(env, bodyIn);
	jbyte *pbodyIn  = (*env)->GetByteArrayElements(env, bodyIn, NULL );
	byte sbodyIn[bInLength];
	jbyteTobyte(pbodyIn, sbodyIn, bInLength);
	/*�ܥǥ�(����)*/
	jsize bOutLength = (*env)->GetArrayLength(env, bodyOut);
	jbyte *pbodyOut  = (*env)->GetByteArrayElements(env, bodyOut, NULL );
	byte sbodyOut[bOutLength];
	jbyteTobyte(pbodyOut, sbodyOut, bOutLength);

	/* ���ޤ��ʤ�����ʤ����ɾ��ɬ�� */
	cob_init(0, NULL);

	/* �饤�֥��ѥ�����ꤹ�� */
	cob_set_library_path(slibr);

	/* ��������ץ����򸡺����� */
	cobol_sub_program = cob_resolve(sprog);

	/* ���顼�����å� */
	if (cobol_sub_program == NULL) {
		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}

	/* �¹Ԥ��� */
	ret = cobol_sub_program(shead, sbodyIn, sbodyOut);

	byteTojbyte(shead, phead, hLength);
	byteTojbyte(sbodyIn, pbodyIn, bInLength);
	byteTojbyte(sbodyOut, pbodyOut, bOutLength);
	/* Java��������ͤ��᤹? */
	(*env)->ReleaseByteArrayElements(env, head, phead, 0);
	(*env)->ReleaseByteArrayElements(env, bodyIn, pbodyIn, 0);
	(*env)->ReleaseByteArrayElements(env, bodyOut, pbodyOut, 0);

	/* ʸ����γ��� */
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
 * ���ꤷ���ץ�����ư����(�饤�֥�����ʤ�)</br>
 * �����餿�֤������ư���ʤ�
 * env java�Ķ�
 * obj ����?�ȤäƤʤ���?
 * prog �ץ����̾
 * head �إå�
 * bodyIn ����ѥ�᡼��
 * bodyOut �����ȥѥ�᡼��
 */
JNIEXPORT jint JNICALL Java_JSampleJniCall1_sampleJniCall1
  (JNIEnv *env, jobject obj, jstring prog, jbyteArray head, jbyteArray bodyIn, jbyteArray bodyOut) {
	/* �ʤ��Cobol������ͤ�int�ʤΤ���������� */
	jint ret;
	/*�ץ����̾*/
	const char* sprog;
	jboolean isCopy2;
	sprog = (*env)->GetStringUTFChars(env, prog, &isCopy2);
	/*�إå�*/
	jsize hLength = (*env)->GetArrayLength (env, head);
	jbyte *phead  = (*env)->GetByteArrayElements(env, head, NULL );
	byte shead[hLength];
	jbyteTobyte(phead, shead, hLength);
	/*�ܥǥ�(����)*/
	jsize bInLength = (*env)->GetArrayLength(env, bodyIn);
	jbyte *pbodyIn  = (*env)->GetByteArrayElements(env, bodyIn, NULL );
	byte sbodyIn[bInLength];
	jbyteTobyte(pbodyIn, sbodyIn, bInLength);
	/*�ܥǥ�(����)*/
	jsize bOutLength = (*env)->GetArrayLength(env, bodyOut);
	jbyte *pbodyOut  = (*env)->GetByteArrayElements(env, bodyOut, NULL );
	byte sbodyOut[bOutLength];
	jbyteTobyte(pbodyOut, sbodyOut, bOutLength);

	/* ���ޤ��ʤ�����ʤ����ɾ��ɬ�� */
	cob_init(0, NULL);

	/* ��������ץ����򸡺����� */
	cobol_sub_program = cob_resolve(sprog);

	/* ���顼�����å� */
	if (cobol_sub_program == NULL) {
 		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}

	/* �¹Ԥ��� */
	ret = cobol_sub_program(shead, sbodyIn, sbodyOut);

	byteTojbyte(shead, phead, hLength);
	byteTojbyte(sbodyIn, pbodyIn, bInLength);
	byteTojbyte(sbodyOut, pbodyOut, bOutLength);
	/* Java��������ͤ��᤹? */
	(*env)->ReleaseByteArrayElements(env, head, phead, 0);
	(*env)->ReleaseByteArrayElements(env, bodyIn, pbodyIn, 0);
	(*env)->ReleaseByteArrayElements(env, bodyOut, pbodyOut, 0);

	/* ʸ����γ��� */
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



