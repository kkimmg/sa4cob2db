#include    <stdio.h>
#include    <stdlib.h>
#include    <string.h>
#include    <sys/param.h>
#include    <sys/types.h>
// JNI
#include    <jni.h>
#include    "config.h"
//
#define     CLASSPATHOPTION "-Djava.class.path="
#define     DEFNAME "/conf/metafile.xml"
#define     JARFILE "/sa4cob2db.jar"
#define     ACM_CONFFILE "ACM_CONFFILE"
#define     CLASSPATH "CLASSPATH"
/** class path */
char *getClasspath () {
	static char *classpathOption;
	char *envpath;
	int len;
	envpath = getenv(CLASSPATH);
	if (envpath == NULL) {
		len = strlen(CLASSPATHOPTION) + strlen(ACM_HOME) + strlen(JARFILE)/* + 1 */;/* printf("len=%i", len); */
		classpathOption = (char *)malloc(len);
		strcpy(classpathOption, CLASSPATHOPTION);
		strcat(classpathOption, ACM_HOME);
		strcat(classpathOption, JARFILE);
	} else {
		len = strlen(CLASSPATHOPTION) + strlen(envpath) + strlen(":") + strlen(ACM_HOME) + strlen(JARFILE);
		classpathOption = (char *)malloc(len);
		strcpy(classpathOption, CLASSPATHOPTION);
		strcat(classpathOption, envpath);
		strcat(classpathOption, ":");
		strcat(classpathOption, ACM_HOME);
		strcat(classpathOption, JARFILE);
	}
	return classpathOption;
}
/** meta data file name */
char *getConfigFile () {
	static char *configOption;
	char *envconfig;
	int len;
	envconfig = getenv(ACM_CONFFILE);
	if (envconfig == NULL) {
		len = strlen(ACM_HOME) + strlen(DEFNAME);
		configOption = (char *)malloc(len);
		strcpy(configOption, ACM_HOME);
		strcat(configOption, DEFNAME);
	} else {
		len = strlen(envconfig);
		configOption = (char *)malloc(len);
		strcpy(configOption, envconfig);
	}
	return configOption;
}
