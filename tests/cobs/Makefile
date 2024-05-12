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
JDKLIB = -I/usr/lib/jvm/default-java/include/
JDKOSLIB = -I/usr/lib/jvm/default-java/include/linux/
CP = cp -f

all: cobc_std cobc_acm cobc_jni

cobpp_acm:
	cd tcp; make cobpp

cobpp_jni:
	cd jni; make cobpp
	
cobc_std:
	$(COBC) $(CFLAGS) 		SEQTEST.cob    -o SEQTEST
	$(COBC) $(CFLAGS) 		WRTTEST.cob    -o WRTTEST
	$(COBC) $(CFLAGS) 		SEQTEST2.cob   -o SEQTEST2
	$(COBC) $(CFLAGS) 		WRTTEST2.cob   -o WRTTEST2
	$(COBC) $(CFLAGS) 		STTTEST.cob    -o STTTEST
	$(COBC) $(CFLAGS) 		DYNTEST.cob    -o DYNTEST
	$(COBC) $(CFLAGS) 		IDXTEST.cob    -o IDXTEST
	$(COBC) $(CFLAGS) 		WRTTEST4.cob   -o WRTTEST4
	$(COBC) $(CFLAGS) 		SEQTEST4.cob   -o SEQTEST4
	$(COBC) $(CFLAGS) 		OPTTEST.cob    -o OPTTEST
	$(COBC) $(CFLAGS) 		MAINTEST.cob   -o MAINTEST
	$(COBC) $(CFLAGS) 		TYPESTEST.cob  -o TYPESTEST
	$(COBC) $(MFLAGS) 		SUBTEST.cob    -o SUBTEST.so
	$(COBC) $(CFLAGS) 		LENTEST.cob    -o LENTEST
	$(COBC) $(MFLAGS) 		DYNTEST2.cob   -o DYNTEST2.so

cobc_acm:
	cd tcp; make

cobc_jni:
	cd jni; make

clean:
	rm SEQTEST
	rm WRTTEST
	rm SEQTEST2
	rm WRTTEST2
	rm STTTEST
	rm DYNTEST
	rm DYNTEST2.so
	rm IDXTEST
	rm WRTTEST4
	rm SEQTEST4
	rm OPTTEST
	rm MAINTEST
	rm TYPESTEST
	rm SUBTEST.so
	rm LENTEST
	cd tcp; make clean
	cd jni; make clean
	rm *.c
	rm *.h

