#include    <stdio.h>
#include    <stdlib.h>
#include    <string.h>
//#include    <sys/param.h>
#include    <sys/types.h>
/** JNI */
#include    <jni.h>
//#include    "config.h"
/** const values */
#define     CLASSPATHOPTION "-Djava.class.path="
#define     CONFOPTION "-DACM_CONFFILE="
#define     DEFNAME "\\conf\\metafile.xml"
#define     ACM_HOME "C:\\sa4cob2db"
#define     JARFILE "\\sa4cob2db.jar"
#define     ACM_CONFFILE "ACM_CONFFILE"
#define     CLASSPATH "CLASSPATH"
/** get class pass */
char *getClasspath () {
	static char *classpathOption;
	char *envpath;
	int len;
	envpath = getenv(CLASSPATH);
	if (envpath == NULL) {
		len = strlen(CLASSPATHOPTION) + strlen(ACM_HOME) + strlen(JARFILE)/* + 1 */;/* printf("len=%i", len) */;
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
/** get config file */
char *getConfigFile () {
	static char *configOption;
	char *envconfig;
	int len;
	envconfig = getenv(ACM_CONFFILE);
	if (envconfig == NULL) {
		len = strlen(CONFOPTION) + strlen(ACM_HOME) + strlen(DEFNAME);
		configOption = (char *)malloc(len);
		strcpy(configOption, CONFOPTION);
		strcat(configOption, ACM_HOME);
		strcat(configOption, DEFNAME);
	} else {
		len = strlen(CONFOPTION) + strlen(envconfig);
		configOption = (char *)malloc(len);
		strcpy(configOption, CONFOPTION);
		strcat(configOption, envconfig);
	}
	return configOption;
}
