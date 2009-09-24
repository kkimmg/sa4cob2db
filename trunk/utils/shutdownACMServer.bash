#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
ACMOFF_FILE=${ACMOFF_FILE:-"./servoff.conf"}
acmlibs=$ACMLIBS_DIR"/acmlibs.jar"
acmofff=$ACMOFF_FILE

java -classpath $acmlibs":"$CLASSPATH k_kim_mg.sa4cob2db.admin.RemoteShutdown $acmofff
