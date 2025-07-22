#ifndef JNICLIENT_H
#define JNICLIENT_H

#include    "acmclient.h"

extern void getStatus(char *status);

extern void getRecord(char *record);

extern void libJNIClient ();

extern int initializeJNI ();

extern void initializeJNISession (char *username, char *password, char *status);

extern void initializeJNISessionEnv (char *status);

extern void terminateJNISession (char *status);

extern void assignJNIFile (char *name, char *status);

extern void openJNIFile (char *name, char *openmode, char *accessmode, char *status);

extern void closeJNIFile (char *name, char *status);

extern void nextJNIRecord (char *name, char *status);

extern void readJNIRecord (char *name, char *record, char *status);

extern void readNextJNIRecord (char *name, char *record, char *status);

extern void moveReadJNIRecord (char *name, char *record, char *status);

//extern void moveReadJNIRecordWith (char *name, char *record, char *indexname, char *status);

extern void writeJNIRecord (char *name, char *record, char *status);

extern void rewriteJNIRecord (char *name, char *record, char *status);

extern void deleteJNIRecord (char *name, char *record, char *status);

extern void moveJNIRecord (char *name, char *record, char *status);

//extern void moveJNIRecordWith (char *name, char *record, char *indexname, char *status);

extern void startJNIRecord (char *name, char *record, char *startmode, char *status);

extern void startJNIRecordWith (char *name, char *record, char *indexname, char *startmode, char *status);

extern void commitJNISession (char *status);

extern void rollbackJNISession (char *status);

extern void setJNICommitMode (char *commitmode, char *status);

extern void setJNITransMode (char *transmode, char *status);

extern void getOptionValue(char *record);

extern void setJNIOption (char *name, char *value);

extern void setJNIOptionFromEnv (char *name, char *value);

extern void getJNIOption (char *name, char *value);

extern void setJNIMaxLength (char *length);

extern void pushJNIFileList (char *status);

extern void popJNIFileList (char *status);

#endif
