CC = gcc
COBC = cobc
COBPP = cobpp -t -s EUCJP
COBP2 = cobpp    -s EUCJP
CFLAGS = -v -Wall -g -x
COBFLAGS = -c `cob-config --cflags`
#JDKLIB = -I/usr/local/jdk1.5.0/include/
#JDKOSLIB = -I/usr/local/jdk1.5.0/include/freebsd/
JDKLIB = -I/usr/lib/jvm/java-6-sun/include/
JDKOSLIB = -I/usr/lib/jvm/java-6-sun/include/linux/
CP = cp -f

all: cobc_std cobc_acm cobc_jni

cobpp_acm:
	$(COBPP) 			SEQTEST2.cob      ACMSEQTEST2.cob
	$(COBPP) 			WRTTEST2.cob      ACMWRTTEST2.cob
	$(COBPP) 			STTTEST.cob       ACMSTTTEST.cob
	$(COBPP) 			DYNTEST.cob       ACMDYNTEST.cob
	$(COBPP) 			DYNTEST1.cob      ACMDYNTEST1.cob
	$(COBPP) 			SEQTEST.cob       ACMSEQTEST.cob
	$(COBPP) 			WRTTEST.cob       ACMWRTTEST.cob
	$(COBPP) 			IDXTEST.cob       ACMIDXTEST.cob
	$(COBPP) 			WRTTEST4.cob      ACMWRTTEST4.cob

cobpp_jni:
	$(COBP2) 			SEQTEST2.cob      JNISEQTEST2.cob
	$(COBP2) 			WRTTEST2.cob      JNIWRTTEST2.cob
	$(COBP2) 			STTTEST.cob       JNISTTTEST.cob
	$(COBP2) 			DYNTEST.cob       JNIDYNTEST.cob
	$(COBP2) 			DYNTEST1.cob      JNIDYNTEST1.cob
	$(COBP2) 			SEQTEST.cob       JNISEQTEST.cob
	$(COBP2) 			WRTTEST.cob       JNIWRTTEST.cob
	$(COBP2) 			IDXTEST.cob       JNIIDXTEST.cob
	$(COBP2) 			WRTTEST4.cob       JNIWRTTEST4.cob
	
link_consts:
	$(CP) ../../cobs/ACMCONSTS.CBL ./

cobc_std:
	$(COBC) $(CFLAGS) 		SEQTEST.cob    -o SEQTEST
	$(COBC) $(CFLAGS) 		WRTTEST.cob    -o WRTTEST
	$(COBC) $(CFLAGS) 		SEQTEST2.cob   -o SEQTEST2
	$(COBC) $(CFLAGS) 		WRTTEST2.cob   -o WRTTEST2
	$(COBC) $(CFLAGS) 		STTTEST.cob    -o STTTEST
	$(COBC) $(CFLAGS) 		DYNTEST.cob    -o DYNTEST
	$(COBC) $(CFLAGS) 		IDXTEST.cob    -o IDXTEST

cobc_acm: link_consts cobpp_acm
	$(COBC) $(CFLAGS) 		ACMSEQTEST.cob -o ACMSEQTEST	
	$(COBC) $(CFLAGS) 		ACMWRTTEST.cob -o ACMWRTTEST
	$(COBC) $(CFLAGS) 		ACMSEQTEST2.cob -o ACMSEQTEST2
	$(COBC) $(CFLAGS) 		ACMWRTTEST2.cob -o ACMWRTTEST2
	$(COBC) $(CFLAGS) 		ACMSEQTEST2.cob -o ACMSEQTEST2
	$(COBC) $(CFLAGS) 		ACMSTTTEST.cob  -o ACMSTTTEST
	$(COBC) $(CFLAGS) 		ACMDYNTEST.cob  -o ACMDYNTEST
	$(COBC) $(CFLAGS) 		ACMDYNTEST1.cob -o ACMDYNTEST1
	$(COBC) $(CFLAGS) 		ACMIDXTEST.cob  -o ACMIDXTEST
	$(COBC) $(CFLAGS) 		ACMWRTTEST4.cob -o ACMWRTTEST4

cobc_jni: link_consts cobpp_jni
	$(COBC) $(CFLAGS) 		JNISEQTEST.cob -o JNISEQTEST	
	$(COBC) $(CFLAGS) 		JNIWRTTEST.cob -o JNIWRTTEST
	$(COBC) $(CFLAGS) 		JNISEQTEST2.cob -o JNISEQTEST2
	$(COBC) $(CFLAGS) 		JNIWRTTEST2.cob -o JNIWRTTEST2
	$(COBC) $(CFLAGS) 		JNISEQTEST2.cob -o JNISEQTEST2
	$(COBC) $(CFLAGS) 		JNISTTTEST.cob  -o JNISTTTEST
	$(COBC) $(CFLAGS) 		JNIDYNTEST.cob  -o JNIDYNTEST
	$(COBC) $(CFLAGS) 		JNIDYNTEST1.cob -o JNIDYNTEST1
	$(COBC) $(CFLAGS) 		JNIIDXTEST.cob  -o JNIIDXTEST
	$(COBC) $(CFLAGS) 		JNIWRTTEST4.cob -o JNIWRTTEST4

clean_cobs:
	rm ACMSEQTEST.cob
	rm ACMWRTTEST.cob
	rm ACMSEQTEST2.cob
	rm ACMWRTTEST2.cob
	rm ACMSTTTEST.cob
	rm ACMDYNTEST.cob
	rm ACMDYNTEST1.cob
	rm ACMIDXTEST.cob
	rm ACMWRTTEST4.cob
	rm JNISEQTEST.cob
	rm JNIWRTTEST.cob
	rm JNISEQTEST2.cob
	rm JNIWRTTEST2.cob
	rm JNISTTTEST.cob
	rm JNIDYNTEST.cob
	rm JNIDYNTEST1.cob
	rm JNIIDXTEST.cob
	rm JNIWRTTEST4.cob
	rm ACMCONSTS.CBL

clean_stdobj:
	rm SEQTEST
	rm WRTTEST
	rm SEQTEST2
	rm WRTTEST2
	rm STTTEST
	rm DYNTEST
	rm IDXTEST
	
clean_acmobj:
	rm ACMSEQTEST
	rm ACMWRTTEST
	rm ACMSEQTEST2
	rm ACMWRTTEST2
	rm ACMSTTTEST
	rm ACMDYNTEST
	rm ACMDYNTEST1
	rm ACMIDXTEST
	rm ACMWRTTEST4
	
clean_jniobj:
	rm JNISEQTEST
	rm JNIWRTTEST
	rm JNISEQTEST2
	rm JNIWRTTEST2
	rm JNISTTTEST
	rm JNIDYNTEST
	rm JNIDYNTEST1
	rm JNIIDXTEST
	rm JNIWRTTEST4
	
clean_obj: clean_stdobj clean_acmobj clean_jniobj

clean: clean_cobs clean_obj

