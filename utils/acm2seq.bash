#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
ACM_CONFFILE=${ACM_CONFFILE:-"/opt/sa4cob2db/conf/metafile.xml"}
JDBC_JARFILE=${JDBC_JARFILE:-"/usr/share/java/postgresql.jar"}

acmlibs=$ACMLIBS_DIR"/sa4cob2db.jar"

echo $ACM_CONFFILE
echo $ACMLIB_DIR
echo $acmlibs

java -classpath $CLASSPATH":"$JDBC_JARFILE":"$acmlibs "k_kim_mg.sa4cob2db.sql.Acm2Seq" $1 $2 $ACM_CONFFILE
