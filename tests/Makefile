CC = gcc
COBC = cobc
COBPP = ../utils/cobpp1.bash
COBP2 = ../utils/cobpp2.bash
CFLAGS = -v -Wall -g -x
SFLAGS = -v -Wall -g
COBFLAGS = -c `cob-config --cflags`
#JDKLIB = -I/usr/local/jdk1.5.0/include/
#JDKOSLIB = -I/usr/local/jdk1.5.0/include/freebsd/
JDKLIB = -I/usr/lib/jvm/java-6-sun/include/
JDKOSLIB = -I/usr/lib/jvm/java-6-sun/include/linux/


jnisample:
	$(CC) $(COBFLAGS) $(JDKLIB) $(JDKOSLIB) $(SFLAGS) sampleJniCall1.c -o libsampleJniCall.o
	$(COBC) $(SFLAGS) -m -o libsampleJniCall.so libsampleJniCall.o 
	$(COBC) $(SFLAGS) -m    TESTSUB.COB -o libTESTSUB.so

sample:
	$(COBC) $(CFLAGS) 		SEQTEST.cob    -o SEQTEST
	$(COBC) $(CFLAGS) 		WRTTEST.cob    -o WRTTEST
	$(COBC) $(CFLAGS) 		SEQTEST2.cob   -o SEQTEST2
	$(COBC) $(CFLAGS) 		WRTTEST2.cob   -o WRTTEST2
	$(COBC) $(CFLAGS) 		STTTEST.cob    -o STTTEST
	$(COBC) $(CFLAGS) 		DYNTEST.cob    -o DYNTEST
	$(COBC) $(CFLAGS) 		IDXTEST.cob    -o IDXTEST
	$(COBPP) 			SEQTEST2.cob      ACMSEQTEST2.cob
	$(COBPP) 			WRTTEST2.cob      ACMWRTTEST2.cob
	$(COBPP) 			STTTEST.cob       ACMSTTTEST.cob
	$(COBPP) 			DYNTEST.cob       ACMDYNTEST.cob
	$(COBPP) 			DYNTEST1.cob      ACMDYNTEST1.cob
	$(COBPP) 			SEQTEST.cob       ACMSEQTEST.cob
	$(COBPP) 			WRTTEST.cob       ACMWRTTEST.cob
	$(COBPP) 			IDXTEST.cob       ACMIDXTEST.cob
	$(COBPP) 			WRTTEST4.cob       ACMWRTTEST4.cob
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
	$(COBP2) 			SEQTEST2.cob      JNISEQTEST2.cob
	$(COBP2) 			WRTTEST2.cob      JNIWRTTEST2.cob
	$(COBP2) 			STTTEST.cob       JNISTTTEST.cob
	$(COBP2) 			DYNTEST.cob       JNIDYNTEST.cob
	$(COBP2) 			DYNTEST1.cob      JNIDYNTEST1.cob
	$(COBP2) 			SEQTEST.cob       JNISEQTEST.cob
	$(COBP2) 			WRTTEST.cob       JNIWRTTEST.cob
	$(COBP2) 			IDXTEST.cob       JNIIDXTEST.cob
	$(COBP2) 			WRTTEST4.cob       JNIWRTTEST4.cob
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

clean:
	rm libsampleJniCall.so
	rm libsampleJniCall.o
	rm libTESTSUB.so
	rm SEQTEST
	rm ACMSEQTEST
	rm WRTTEST
	rm ACMWRTTEST
	rm SEQTEST2
	rm ACMSEQTEST2
	rm WRTTEST2
	rm ACMWRTTEST2
	rm STTTEST
	rm ACMSTTTEST
	rm DYNTEST
	rm ACMDYNTEST
	rm IDXTEST
	rm ACMIDXTEST
	rm WRTTEST4
	rm ACMWRTTEST4

