CC = gcc
COBC = cobc
COBPP = cobpp -t -s EUCJP
COBP2 = cobpp    -s EUCJP
COBP3 = cobpp -t -s EUCJP -b
COBP4 = cobpp    -s EUCJP -b
CFLAGS = -v -Wall -g -x
MFLAGS = -v -Wall -g -m
COBFLAGS = -c `cob-config --cflags`
#JDKLIB = -I/usr/local/jdk1.5.0/include/
#JDKOSLIB = -I/usr/local/jdk1.5.0/include/freebsd/
JDKLIB = -I/usr/lib/jvm/java-6-sun/include/
JDKOSLIB = -I/usr/lib/jvm/java-6-sun/include/linux/
CP = cp -f

all: cobc

cobpp:
	$(COBP2) 			../SEQTEST2.cob      JNISEQTEST2.cob
	$(COBP2) 			../WRTTEST2.cob      JNIWRTTEST2.cob
	$(COBP2) 			../STTTEST.cob       JNISTTTEST.cob
	$(COBP2) 			../DYNTEST.cob       JNIDYNTEST.cob
	$(COBP2) 			../DYNTEST1.cob      JNIDYNTEST1.cob
	$(COBP2) 			../SEQTEST.cob       JNISEQTEST.cob
	$(COBP2) 			../WRTTEST.cob       JNIWRTTEST.cob
	$(COBP2) 			../IDXTEST.cob       JNIIDXTEST.cob
	$(COBP2) 			../WRTTEST4.cob      JNIWRTTEST4.cob
	$(COBP2) 			../SEQTEST4.cob      JNISEQTEST4.cob
	$(COBP2) 			../OPTTEST.cob       JNIOPTTEST.cob
	$(COBP2) 			../MAINTEST.cob      JNIMAINTEST.cob
	$(COBP2) 			../TYPESTEST.cob     JNITYPESTEST.cob
	$(COBP4) 			../SUBTEST.cob       JNISUBTEST.cob
	$(COBP2) 			../LENTEST.cob       JNILENTEST.cob
	
link_consts:
	$(CP) ../../../cobs/ACMCONSTS.CBL ./
	$(CP) ../*.cbl ./

cobc: link_consts cobpp
	$(COBC) $(CFLAGS) 		JNISEQTEST.cob   -o JNISEQTEST	
	$(COBC) $(CFLAGS) 		JNIWRTTEST.cob   -o JNIWRTTEST
	$(COBC) $(CFLAGS) 		JNISEQTEST2.cob  -o JNISEQTEST2
	$(COBC) $(CFLAGS) 		JNIWRTTEST2.cob  -o JNIWRTTEST2
	$(COBC) $(CFLAGS) 		JNISEQTEST2.cob  -o JNISEQTEST2
	$(COBC) $(CFLAGS) 		JNISTTTEST.cob   -o JNISTTTEST
	$(COBC) $(CFLAGS) 		JNIDYNTEST.cob   -o JNIDYNTEST
	$(COBC) $(CFLAGS) 		JNIDYNTEST1.cob  -o JNIDYNTEST1
	$(COBC) $(CFLAGS) 		JNIIDXTEST.cob   -o JNIIDXTEST
	$(COBC) $(CFLAGS) 		JNIWRTTEST4.cob  -o JNIWRTTEST4
	$(COBC) $(CFLAGS) 		JNISEQTEST4.cob  -o JNISEQTEST4
	$(COBC) $(CFLAGS) 		JNIOPTTEST.cob   -o JNIOPTTEST
	$(COBC) $(CFLAGS) 		JNIMAINTEST.cob  -o JNIMAINTEST
	$(COBC) $(CFLAGS) 		JNITYPESTEST.cob -o JNITYPESTEST
	$(COBC) $(MFLAGS) 		JNISUBTEST.cob   -o JNISUBTEST.so
	$(COBC) $(CFLAGS) 		JNILENTEST.cob   -o JNILENTEST	

clean:
	rm JNI*
	rm ACMCONSTS.CBL
	rm *.cbl
