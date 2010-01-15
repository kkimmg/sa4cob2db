#include    <stdio.h>
#include    <stdlib.h>
#include    <string.h>
#include    <sys/param.h>
#include    <sys/types.h>

#include    <jni.h>
#include    "config.h"

#define     CLASSPATHOPTION "-Djava.class.path="
#define     CONFOPTION "-DACM_CONFFILE="
#define     DEFNAME "/conf/metafile.xml"
#define     JARFILE "/sa4cob2db.jar"
#define     ACM_CONFFILE "ACM_CONFFILE"
#define     CLASSPATH "CLASSPATH"
/** 設定ファイルの名称 */
char* getClasspath () {
	static char *classpathOption;
	char *envpath;
	envpath = getenv(CLASSPATH);
	if (envpath == NULL) {
		classpathOption = (char *)malloc(strlen(CLASSPATHOPTION) + strlen(ACM_HOME) + strlen(JARFILE) );
		strcpy(classpathOption, CLASSPATHOPTION);
		strcat(classpathOption, ACM_HOME);
		strcat(classpathOption, JARFILE);
	} else {
		classpathOption = (char *)malloc(strlen(CLASSPATHOPTION) + strlen(envpath) + strlen(":") + strlen(ACM_HOME) + strlen(JARFILE));
		strcpy(classpathOption, CLASSPATHOPTION);
		strcat(classpathOption, envpath);
		strcat(classpathOption, ":");
		strcat(classpathOption, ACM_HOME);
		strcat(classpathOption, JARFILE);
	}
	return classpathOption;
}
/** 設定ファイルの名称 */
char *getConfigFile () {
	static char *configOption;
	char *envconfig;
	envconfig = getenv(ACM_CONFFILE);
	if (envconfig == NULL) {
		configOption = (char *)malloc(strlen(CONFOPTION) + strlen(ACM_HOME) + strlen(DEFNAME));
		strcpy(configOption, CONFOPTION);
		strcat(configOption, ACM_HOME);
		strcat(configOption, DEFNAME);
	} else {
		configOption = (char *)malloc(strlen(CONFOPTION) + strlen(envconfig));
		strcpy(configOption, CONFOPTION);
		strcat(configOption, envconfig);
	}
	return configOption;
}
