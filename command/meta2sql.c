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
/** options */
static struct option longopts[] = {
    {"help", no_argument, NULL, 'h'},
    {0}
};
/** main */
int main (int argc, char *argv[]) {
	int opt;
	char* informat = "";
	char* charset = "";
	while ((opt = getopt_long(argc, argv, "h", longopts, NULL)) != -1) {
		switch (opt) {
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
    //
    (*env)->CallStaticVoidMethod(env, clazz, midMainToo);
	(*jvm)->DestroyJavaVM(jvm);
    exit(0);
}

/**
 * initialize jni
 */
int
initializeJNI () {
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
	"()V");
	if (midMainToo == 0) {
		perror("method not found.");
		return -1;
	}
	return 0;
}

/**
 * usage
 */
void
display_usage () {
	printf("cobpp infile outfile\n");
	printf("options\n");
	printf("\t-h/--help\tshow this message.\n");
}
