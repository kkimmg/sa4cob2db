#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
cobpplibs=$ACMLIBS_DIR"/sa4cob2db.jar"
#customcodegeneratorclass="k_kim_mg.sa4cob2db.codegen.JNICodeGenerator"

java -Dcustomcodegeneratorclass="k_kim_mg.sa4cob2db.codegen.JNICodeGenerator" -classpath $cobpplibs k_kim_mg.sa4cob2db.codegen.COBPP1 $1 $2 
