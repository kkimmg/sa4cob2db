#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
cobpplibs=$ACMLIBS_DIR"/acmlibs.jar"

java -classpath $cobpplibs k_kim_mg.sa4cob2db.codegen.COBPP1 $1 $2 
