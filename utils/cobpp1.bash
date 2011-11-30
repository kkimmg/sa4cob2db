#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
cobpplibs=$ACMLIBS_DIR"/sa4cob2db.jar"

java -classpath $cobpplibs k_kim_mg.sa4cob2db.codegen.COBPP1 $1 $2 
