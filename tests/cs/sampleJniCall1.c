#include <libcob.h>
#include "JSampleJniCall1.h"
/**
 * ほとんどOpenCobolからそのままもらってきたのさ。
 * 繰り返しで使える?
 */
typedef char byte;
static int (*cobol_sub_program)(byte *head, byte *bodyIn, byte *bodyOut);
/**static void jcharTochar(jchar *src, char *dist, int length);*/
static void jbyteTobyte(jbyte *src, byte *dist, int length);
static void byteTojbyte(byte *src, jbyte *dist, int length);
/**
 * 指定したプログラムを起動する(ライブラリ指定あり)
 * env java環境
 * obj あり?使ってないぞ?
 * library ライブラリパス
 * prog プログラム名
 * head ヘッダ
 * bodyIn インパラメータ
 * bodyOut アウトパラメータ
 */
JNIEXPORT jint JNICALL Java_JSampleJniCall1_sampleJniCall2
  (JNIEnv *env, jobject obj, jstring library, jstring prog, jbyteArray head, jbyteArray bodyIn, jbyteArray bodyOut) {
	/* なんでCobolの戻り値がintなのか不明だよな */
	jint ret;
	/*ライブラリ名*/
	const char* slibr;
	jboolean isCopy1;
	slibr = (*env)->GetStringUTFChars(env, library, &isCopy1);
	/*プログラム名*/
	const char* sprog;
	jboolean isCopy2;
	sprog = (*env)->GetStringUTFChars(env, prog, &isCopy2);
	/*ヘッダ*/
	jsize hLength = (*env)->GetArrayLength (env, head);
	jbyte *phead  = (*env)->GetByteArrayElements(env, head, NULL );
	byte shead[hLength];
	jbyteTobyte(phead, shead, hLength);
	/*ボディ(入力)*/
	jsize bInLength = (*env)->GetArrayLength(env, bodyIn);
	jbyte *pbodyIn  = (*env)->GetByteArrayElements(env, bodyIn, NULL );
	byte sbodyIn[bInLength];
	jbyteTobyte(pbodyIn, sbodyIn, bInLength);
	/*ボディ(出力)*/
	jsize bOutLength = (*env)->GetArrayLength(env, bodyOut);
	jbyte *pbodyOut  = (*env)->GetByteArrayElements(env, bodyOut, NULL );
	byte sbodyOut[bOutLength];
	jbyteTobyte(pbodyOut, sbodyOut, bOutLength);

	/* おまじないじゃないけど常に必要 */
	cob_init(0, NULL);

	/* ライブラリパスを指定する */
	cob_set_library_path(slibr);

	/* 該当するプログラムを検索する */
	cobol_sub_program = cob_resolve(sprog);

	/* エラーチェック */
	if (cobol_sub_program == NULL) {
		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}

	/* 実行する */
	ret = cobol_sub_program(shead, sbodyIn, sbodyOut);

	byteTojbyte(shead, phead, hLength);
	byteTojbyte(sbodyIn, pbodyIn, bInLength);
	byteTojbyte(sbodyOut, pbodyOut, bOutLength);
	/* Javaの配列に値を戻す? */
	(*env)->ReleaseByteArrayElements(env, head, phead, 0);
	(*env)->ReleaseByteArrayElements(env, bodyIn, pbodyIn, 0);
	(*env)->ReleaseByteArrayElements(env, bodyOut, pbodyOut, 0);

	/* 文字列の開放 */
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
 * 指定したプログラムを起動する(ライブラリ指定なし)</br>
 * だからたぶんちゃんと動かない
 * env java環境
 * obj あり?使ってないぞ?
 * prog プログラム名
 * head ヘッダ
 * bodyIn インパラメータ
 * bodyOut アウトパラメータ
 */
JNIEXPORT jint JNICALL Java_JSampleJniCall1_sampleJniCall1
  (JNIEnv *env, jobject obj, jstring prog, jbyteArray head, jbyteArray bodyIn, jbyteArray bodyOut) {
	/* なんでCobolの戻り値がintなのか不明だよな */
	jint ret;
	/*プログラム名*/
	const char* sprog;
	jboolean isCopy2;
	sprog = (*env)->GetStringUTFChars(env, prog, &isCopy2);
	/*ヘッダ*/
	jsize hLength = (*env)->GetArrayLength (env, head);
	jbyte *phead  = (*env)->GetByteArrayElements(env, head, NULL );
	byte shead[hLength];
	jbyteTobyte(phead, shead, hLength);
	/*ボディ(入力)*/
	jsize bInLength = (*env)->GetArrayLength(env, bodyIn);
	jbyte *pbodyIn  = (*env)->GetByteArrayElements(env, bodyIn, NULL );
	byte sbodyIn[bInLength];
	jbyteTobyte(pbodyIn, sbodyIn, bInLength);
	/*ボディ(出力)*/
	jsize bOutLength = (*env)->GetArrayLength(env, bodyOut);
	jbyte *pbodyOut  = (*env)->GetByteArrayElements(env, bodyOut, NULL );
	byte sbodyOut[bOutLength];
	jbyteTobyte(pbodyOut, sbodyOut, bOutLength);

	/* おまじないじゃないけど常に必要 */
	cob_init(0, NULL);

	/* 該当するプログラムを検索する */
	cobol_sub_program = cob_resolve(sprog);

	/* エラーチェック */
	if (cobol_sub_program == NULL) {
 		fprintf(stderr, "%s\n", cob_resolve_error ());
		return -1;
	}

	/* 実行する */
	ret = cobol_sub_program(shead, sbodyIn, sbodyOut);

	byteTojbyte(shead, phead, hLength);
	byteTojbyte(sbodyIn, pbodyIn, bInLength);
	byteTojbyte(sbodyOut, pbodyOut, bOutLength);
	/* Javaの配列に値を戻す? */
	(*env)->ReleaseByteArrayElements(env, head, phead, 0);
	(*env)->ReleaseByteArrayElements(env, bodyIn, pbodyIn, 0);
	(*env)->ReleaseByteArrayElements(env, bodyOut, pbodyOut, 0);

	/* 文字列の開放 */
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



