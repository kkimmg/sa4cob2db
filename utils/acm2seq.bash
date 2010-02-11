#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
ACM_METAFILE=${ACM_METAFILE:-"/usr/local/etc/acm/metafile.xml"}
JDBC_JARFILE=${JDBC_JARFILE:-"/usr/share/java/postgresql.jar"}

acmlibs=$ACMLIBS_DIR"/acmlibs.jar"

java -classpath $CLASSPATH":"$JDBC_JARFILE":"$acmlibs "k_kim_mg.sa4cob2db.sql.Acm2Seq" $1 $2
