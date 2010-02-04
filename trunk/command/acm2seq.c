#define     _GNU_SOURCE
#include    <stdio.h>
#include    <unistd.h>
#include    <stdlib.h>
#include    <string.h>
#include    <errno.h>
#include    <signal.h>
#include    <getopt.h>
#include    <sys/param.h>
#include    <sys/types.h>
/***************************************/
#include    <jni.h>
#include    "getJNIOption.h"
#include    "config.h"
/***************************************/
JNIEnv *env;
JavaVM *jvm;
jclass clazz;
jobject jniserv;
jmethodID midMainToo;
/***************************************/
/** オプション */
static struct option longopts[] = {
    {"metafile", required_argument, NULL, 'm'},
    {"lineout", required_argument, NULL, 'l'},
    {"help", required_argument, NULL, 'h'},
    {0, 0, 0, 0}
}
/** 主処理 */
int main (int argc, char *argv[]) {
	int opt;
	char* metafile = getConfigFile();
	char* lineout = "true";
	char* display_usage = "false";
	while ((opt = getopt_long(argc, argv, "m:l:h", longopts, NULL)) != -1) {
		switch (opt) {
			case 'm':
                metafile = optarg;
				break;
			case 'l':
                lineout = optarg;
				break;
			case 'h':
                display_usage = "true";
				break;			
		}
	}
    // JVMの生成
    initailizeJNI();
    // 入力ファイルと出力ファイルの取得
    char* acmfile;
    char* outfile;
    // 実効
    (*env)->CallStaticVoidMethod(clazz, midMainToo, acmfile, outfile, metafile, lineout, display_usage);
}

/**
 * JNI環境の初期化
 */
int
initializeJNI () {
	// JVMを作成する
	JavaVMOption options[2];
	options[0].optionString = getClasspath();
	//options[1].optionString = getConfigFile();
	JavaVMInitArgs vm_args;
	vm_args.version = JNI_VERSION_1_6;
	vm_args.options = options;
	vm_args.nOptions = 2;
	JNI_GetDefaultJavaVMInitArgs(&vm_args);
	JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
	// クラスの取得
	clazz = (*env)->FindClass(env, "k_kim_mg.sa4cob2db.Acm2Seq");
	if (clazz == 0) {
		perror("Acm2Seq Class Not Found.");
		return (-1);
	}
	// コンストラクタの取得
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",	"([B[B[B[B[B)V");
	if (midMainToo == 0) {
		perror("method not found.");
		return -1;
	}
	return 0;
}
