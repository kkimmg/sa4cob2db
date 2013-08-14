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
    {"help", no_argument, NULL, 'h'},
    {0}
};
/** main */
extern int main_too (int argc, char *argv[]) {
	int opt;
	/*char* informat = "";*/
	char* charset = "";
	while ((opt = getopt_long(argc, argv, "s:h", longopts, NULL)) != -1) {
		switch (opt) {
			case 's':
		        charset = optarg;
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
    jstring s_charset = (*env)->NewStringUTF(env, charset);
    //
    (*env)->CallStaticVoidMethod(env, clazz, midMainToo,
    //infile,   outfile,   charset
      s_infile, s_outfile, s_charset);
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
	clazz = (*env)->FindClass(env, "k_kim_mg/sa4cob2db/utils/MetaData2SQL");
	if (clazz == 0) {
		perror("COBPP1 Class Not Found.");
		return (-1);
	}
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",
	//infile           ;  outfile        ;  charset
	"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
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
	printf("cobpp infile outfile\n");
	printf("options\n");
	printf("\t-s/--charset\tcharset of source code\n");
	printf("\t-h/--help\tshow this message.\n");
}

