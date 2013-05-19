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
    {"daemon", no_argument, NULL, 'd'},
    {"help", no_argument, NULL, 'h'},
    {0, 0, 0, 0}
};
/** main */
int main (int argc, char *argv[]) {
	int opt;
	char* metafile = getConfigFile();
	while ((opt = getopt_long(argc, argv, "m:dh", longopts, NULL)) != -1) {
		switch (opt) {
			case 'm':
                metafile = optarg;
				break;
			case 'd':
				if (daemon(0, 0) != 0) {
					perror("can't daemon()");
				}
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
    // get file
	jstring s_metafile = (*env)->NewStringUTF(env, metafile);
    // execute
    (*env)->CallStaticVoidMethod(env, clazz, midMainToo, s_metafile);
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
	clazz = (*env)->FindClass(env, "k_kim_mg/sa4cob2db/sql/SQLNetServer");
	if (clazz == 0) {
		perror("COBPP1 Class Not Found.");
		return (-1);
	}
	// get method
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",	"(Ljava/lang/String;)V");
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
	printf("\t-m/--metafile\tfile of metadata\n");
	printf("\t-d/--daemon\trun as daemon\n");
	printf("\t-h/--help\tshow this message.\n");
}

