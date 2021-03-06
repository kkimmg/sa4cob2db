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
extern int  main_too(int argc, char *argv[]);
extern int  initializeJNI();
extern void display_usage();
/***************************************/
/** options */
static struct option longopts[] = {
    {"informat", required_argument, NULL, 'i'},
	{"charset", required_argument, NULL, 's'},
	{"acmkeyregex", required_argument, NULL, 'r'},
    {"help", no_argument, NULL, 'h'},
    {0, 0, 0, 0}
};
/** main */
extern int main_too (int argc, char *argv[]) {
	int opt;
	char* informat = "";
	char* charset = "";
	char* keyregex = "";
	while ((opt = getopt_long(argc, argv, "i:s:r:h", longopts, NULL)) != -1) {
		switch (opt) {
			case 'i':
                informat = optarg;
				break;
			case 's':
                charset = optarg;
				break;
			case 'r':
                keyregex = optarg;
				break;
			case 'h':
                display_usage();
				return 0;
				break;	
			case '?':
                display_usage();
				return 0;
				break;						
		}
	}
    //
    initializeJNI();
    //
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
	jstring s_keyregex = (*env)->NewStringUTF(env, keyregex);

    //
    (*env)->CallStaticVoidMethod(env, clazz, midMainToo, 
    //infile,   outfile,   informat,   charset
      s_infile, s_outfile, s_informat, s_charset, s_keyregex);
	(*jvm)->DestroyJavaVM(jvm);
    return 0;
}

/**
 * initialize jni
 */
extern int initializeJNI () {
	// create jvm
	JavaVMOption options[1];
	options[0].optionString = getClasspath();
	JavaVMInitArgs vm_args;
	vm_args.version = JNI_VERSION_1_6;
	vm_args.options = options;
	vm_args.nOptions = 1;
	JNI_GetDefaultJavaVMInitArgs(&vm_args);
	JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
	clazz = (*env)->FindClass(env, "k_kim_mg/sa4cob2db/utils/Cbl2MetaData");
	if (clazz == 0) {
		perror("Class Not Found.");
		return (-1);
	}
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",
	//infile           ;  outfile        ;  informat       ;  charset        ; keyregex
	"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	if (midMainToo == 0) {
		perror("method not found.");
		return -1;
	}
	return 0;
}

/**
 * usage
 */
extern void display_usage () {
	printf("cob2meta infile outfile\n");
	printf("options\n");
	printf("\t-i/--informat\tfix or other input source code format. default is fix\n");
	printf("\t-s/--charset\tcharset of source code\n");
}

