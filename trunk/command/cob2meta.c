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
	{"charset", required_argument, NULL, 's'},
    {"help", no_argument, NULL, 'h'},
    {0, 0, 0, 0}
};
/** 主処理 */
int main (int argc, char *argv[]) {
	int opt;
	char* informat = "";
	char* charset = "";
	while ((opt = getopt_long(argc, argv, "i:o:s:h", longopts, NULL)) != -1) {
		switch (opt) {
			case 'i':
                informat = optarg;
				break;
			case 's':
                charset = optarg;
				break;
			case 'h':
                display_usage();
				exit(0);
				break;	
			case '?':
                display_usage();
				exit(0);
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
	jstring s_charset = (*env)->NewStringUTF(env, charset);

    // 実効
    (*env)->CallStaticVoidMethod(env, clazz, midMainToo, 
    //infile,   outfile,   informat,   charset
      s_infile, s_outfile, s_informat, s_charset);
	(*jvm)->DestroyJavaVM(jvm);
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
	clazz = (*env)->FindClass(env, "k_kim_mg/sa4cob2db/utils/Cbl2MetaData");
	if (clazz == 0) {
		perror("COBPP1 Class Not Found.");
		return (-1);
	}
	// コンストラクタの取得
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",
	//infile           ;  outfile        ;  informat       ;  charset
	"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
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
	printf("\t-s/--charset\tcharset of source code\n");
	printf("\t-h/--help\tshow this message.\n");
}

