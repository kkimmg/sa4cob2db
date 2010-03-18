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
int initializeJNI();
void display_usage();
/***************************************/
/** オプション */
static struct option longopts[] = {
    {"informat", required_argument, NULL, 'i'},
	{"outformat", required_argument, NULL, 'o'},
	{"initrow", required_argument, NULL, 'r'},
	{"increase", required_argument, NULL, 'c'},
	{"acmconsts_file", required_argument, NULL, 'a'},
	{"expand_copy", required_argument, NULL, 'e'},
	{"codegenaratorlisteners", required_argument, NULL, 'l'},
	{"customcodegeneratorclass", required_argument, NULL, 'x'},
    {"tcp", no_argument, NULL, 't'},
    {"jni", no_argument, NULL, 'j'},
    {"help", no_argument, NULL, 'h'},
    {0, 0, 0, 0}
};
/** 主処理 */
int main (int argc, char *argv[]) {
	int opt;
	char* informat = "";
	char* outformat = "";
	char* initrow = "";
	char* increase = "";
	char* acmconsts_file = "";
	char* expand_copy = "";
	char* codegenaratorlisteners = "";
	char* customcodegeneratorclass = "k_kim_mg.sa4cob2db.codegen.JNICodeGenerator";
	while ((opt = getopt_long(argc, argv, "i:o:r:c:a:e:l:x:tjh", longopts, NULL)) != -1) {
		switch (opt) {
			case 'i':
                informat = optarg;
				break;
			case 'o':
                outformat = optarg;
				break;
			case 'r':
                initrow = optarg;
				break;
			case 'c':
                increase = optarg;
				break;
			case 'a':
                acmconsts_file = optarg;
				break;
			case 'e':
                expand_copy = optarg;
				break;
			case 'l':
                codegenaratorlisteners = optarg;
				break;
			case 'x':
                customcodegeneratorclass = optarg;
				break;
			case 't':
                customcodegeneratorclass = "";
				break;
			case 'j':
                customcodegeneratorclass = "k_kim_mg.sa4cob2db.codegen.JNICodeGenerator";
				break;
			case 'h':
                display_usage();
				break;			
		}
	}
    // JVMの生成
    initializeJNI();
    // 入力ファイルと出力ファイルの取得
    char* infile = "";
    if (argc > optind) {
        infile = argv[optind];
    }
    char* outfile = "";
    if (argc > optind + 1) {
        outfile = argv[optind + 1];
    }
	jstring s_infile = (*env)->NewStringUTF(env, infile);
	jstring s_outfile = (*env)->NewStringUTF(env, outfile);
	jstring s_informat = (*env)->NewStringUTF(env, informat);
	jstring s_outformat = (*env)->NewStringUTF(env, outformat);
	jstring s_initrow = (*env)->NewStringUTF(env, initrow);
	jstring s_increase = (*env)->NewStringUTF(env, increase);
	jstring s_consts = (*env)->NewStringUTF(env, acmconsts_file);
	jstring s_expand = (*env)->NewStringUTF(env, expand_copy);
	jstring s_listeners = (*env)->NewStringUTF(env, codegenaratorlisteners);
	jstring s_custom = (*env)->NewStringUTF(env, customcodegeneratorclass);
	jstring s_display_usage = (*env)->NewStringUTF(env, "false");
    // 実効                                              infile, outfile, informat, outformat, initrow, increase, acmconsts_file, expand_copy, codegeneratorlisteners, customcodegeneratorclass
    (*env)->CallStaticVoidMethod(env, clazz, midMainToo, s_infile, s_outfile, s_informat, s_outformat, s_initrow, s_increase, s_consts, s_expand, s_listeners, s_custom, s_display_usage);
    exit(0);
}

/**
 * JNI環境の初期化
 */
int
initializeJNI () {
	// JVMを作成する
	JavaVMOption options[1];
	options[0].optionString = getClasspath();
	JavaVMInitArgs vm_args;
	vm_args.version = JNI_VERSION_1_6;
	vm_args.options = options;
	vm_args.nOptions = 1;
	JNI_GetDefaultJavaVMInitArgs(&vm_args);
	JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
	// クラスの取得
	clazz = (*env)->FindClass(env, "k_kim_mg/sa4cob2db/codegen/COBPP1");
	if (clazz == 0) {
		perror("COBPP1 Class Not Found.");
		return (-1);
	}
	// コンストラクタの取得                                             infile           ;  outfile        ; informat       ;   outformat     ;  initrow         ;  acmconsts_file; expand_copy;listener; custom
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",	"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	if (midMainToo == 0) {
		perror("method not found.");
		return -1;
	}
	return 0;
}

/**
 * 使い方の説明
 */
void
display_usage () {
	printf("cobpp infile outfile\n");
	printf("options\n");
	printf("\t-i/--informat\tfix or other input source code format. default is fix\n");
	printf("\t-o/--outformat\tfix or other output source code format default is fix\n");
	printf("\t-r/--initrow\tinitial row number default is 000010\n");
	printf("\t-c/--increase\tincrease row number default is 10\n");
	printf("\t-a/--acmconsts_file\tlocation of ACMCONSTS.CBL\n");
	printf("\t-e/--expand_copy\texpand file on source directed by copy statement\n");
	printf("\t-l/--codegenaratorlisteners\teventlistener class names separated by :\n");
	printf("\t-x/--customcodegeneratorclass\tcustom class for generating souce code\n");
	printf("\t-t/--tcp\tgenerate for client of sqlnetserver\n");
	printf("\t-j/--jni\tgenerate for client with JNI\n");
	printf("\t-h/--help\tshow this message.\n");
}

