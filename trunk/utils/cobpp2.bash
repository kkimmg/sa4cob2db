#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
cobpplibs=$ACMLIBS_DIR"/cobpp.jar"
#customcodegeneratorclass="k_kim_mg.sa4cob2db.codegen.JNICodeGenerator"

java -Dcustomcodegeneratorclass="k_kim_mg.sa4cob2db.codegen.JNICodeGenerator" -jar $cobpplibs $1 $2 
