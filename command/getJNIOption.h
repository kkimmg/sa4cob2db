#ifndef GETCLASSPATH_H
#define GETCLASSPATH_H

JNIEnv *env;
JavaVM *jvm;
jclass clazz;
jmethodID midMain;
/** get classpath */
char* getClasspath ();
/** get meta data file name */
char *getConfigFile ();

#endif
