#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
JDBC_JARFILE=${JDBC_JARFILE:-"/usr/share/java/postgresql.jar"}
acmlibs=$ACMLIBS_DIR"/acmlibs.jar"

java -classpath $acmlibs":"$JDBC_JARFILE":"$CLASSPATH -server k_kim_mg.sa4cob2db.sql.SQLNetServer
