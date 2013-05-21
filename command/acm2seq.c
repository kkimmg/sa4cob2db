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
    {"lineout", required_argument, NULL, 'l'},
    {"sql", required_argument, NULL, 'q'},
    {"sqlin", no_argument, NULL, 'a'},
    {"help", no_argument, NULL, 'h'},
    {0, 0, 0, 0, 0, 0}
};
/** main */
int main (int argc, char *argv[]) {
	int opt;
	char* metafile = getConfigFile();
	char* lineout = "true";
	char* sql = "";
	char* sqlin = "false";
	while ((opt = getopt_long(argc, argv, "m:l:q:ah", longopts, NULL)) != -1) {
		switch (opt) {
			case 'm':
                metafile = optarg;
				break;
			case 'l':
                lineout = optarg;
				break;
			case 'q':
                sql = optarg;
				break;
			case 'a':
                sqlin = "true";
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
    // meta data
    char* acmfile = "";
    if (argc > optind) {
        acmfile = argv[optind];
    }
    char* outfile = "";
    if (argc > optind + 1) {
        outfile = argv[optind + 1];
    }
	jstring s_acmfile = (*env)->NewStringUTF(env, acmfile);
	jstring s_outfile = (*env)->NewStringUTF(env, outfile);
	jstring s_metafile = (*env)->NewStringUTF(env, metafile);
	jstring s_lineout = (*env)->NewStringUTF(env, lineout);
	jstring s_sql = (*env)->NewStringUTF(env, sql);
	jstring s_sqlin = (*env)->NewStringUTF(env, sqlin);
	jstring s_display_usage = (*env)->NewStringUTF(env, "false");
    // call main
    (*env)->CallStaticVoidMethod(env, clazz, midMainToo, s_acmfile, s_outfile, s_metafile, s_lineout, s_sql, s_sqlin, s_display_usage);
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
	clazz = (*env)->FindClass(env, "k_kim_mg/sa4cob2db/sql/Acm2Seq");
	if (clazz == 0) {
		perror("Acm2Seq Class Not Found.");
		return (-1);
	}
	// get main method
	midMainToo	= (*env)->GetStaticMethodID(env, clazz, "main_too",	"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
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
	printf("acm2seq acmfile outfile\n");
	printf("options\n");
	printf("\t-m/--metafile\tconfiguration file of record layout. default is /opt/sa4cob2db/conf/metafile.xml\n");
	printf("\t-l/--lineout\ttrue/false outfile is line file. default is true\n");
	printf("\t-h/--help\tshow this message.\n");
}

