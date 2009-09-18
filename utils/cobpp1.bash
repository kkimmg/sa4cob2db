#!/bin/bash
ACMLIBS_DIR=${ACMLIBS_DIR:-"."}
cobpplibs=$ACMLIBS_DIR"/cobpp.jar"

java -jar $cobpplibs $1 $2 
