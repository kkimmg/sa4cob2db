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
    {"metafile", required_argument, NULL, 'm'},
    {"linein", required_argument, NULL, 'l'},
    {"extend", required_argument, NULL, 'e'},
    {"help", no_argument, NULL, 'h'},
    {0, 0, 0, 0}
};
/** main */
int main (int argc, char *argv[]) {
	int opt;
	char* metafile = getConfigFile();
	char* linein = "true";
	char* extend = "false";
	while ((opt = getopt_long(argc, argv, "m:l:h", longopts, NULL)) != -1) {
		switch (opt) {
			case 'm':
                metafile = optarg;
				break;
			case 'l':
                linein = optarg;
				break;
			case 'e':
				extend = optarg;
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
    // JVM
    initializeJNI();
    if (argc > optind + 1) {
		char* acmfile = argv[optind];
		char* infile = argv[optind + 1];
		jstring s_acmfile = (*env)->NewStringUTF(env, acmfile);
		jstring s_infile = (*env)->NewStringUTF(env, infile);
		jstring s_metafile = (*env)->NewStringUTF(env, metafile);
		jstring s_linein = (*env)->NewStringUTF(env, linein);
		jstring s_extend = (*env)->NewStringUTF(env, extend);
		jstring s_display_usage = (*env)->NewStringUTF(env, "false");
    	// execute
    	(*env)->CallStaticVoidMethod(env, clazz, midMainToo, s_acmfile, s_infile, s_metafile, s_linein, s_extend, s_display_usage);
	} else {
		display_usage();
	}
	(*jvm)->DestroyJavaVM(jvm);
    exit(0);
}

/**
 * JNI
 */
int
initializeJNI () {
	// JVM
	JavaVMOption options[1];
	options[0].optionString = getClasspath();
	JavaVMInitArgs vm_args;
	vm_args.version = JNI_VERSION_1_6;
	vm_args.options = options;
	vm_args.nOptions = 1;
	JNI_GetDefaultJavaVMInitArgs(&vm_args);
	JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
	// get class
	clazz = (*env)->FindClass(env, "k_kim_mg/sa4cob2db/sql/Seq2Acm");
	if (clazz == 0) {
		perror("Seq2Acm Class Not Found.");
		return (-1);
	}
	// get method
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",	"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
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
	printf("acm2seq acmfile infile\n");
	printf("options\n");
	printf("\t-m/--metafile\tconfiguration file of record layout. default is /opt/sa4cob2db/conf/metafile.xml\n");
	printf("\t-l/--linein\ttrue/false infile is line file. default is true\n");
	printf("\t-e/--extend\ttrue/false open acmfile mode is extend. default is false.\n");
	printf("\t-h/--help\tshow this message.\n");
}

