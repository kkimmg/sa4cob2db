CC = gcc
COBC = cobc
COBPP = cobpp    -s EUCJP
COBP2 = cobpp -t -s EUCJP
COBP3 = cobpp    -s EUCJP -b
COBP4 = cobpp -t -s EUCJP -b
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
	$(COBP2) 			../SEQTEST2.cob      TCPSEQTEST2.cob
	$(COBP2) 			../WRTTEST2.cob      TCPWRTTEST2.cob
	$(COBP2) 			../STTTEST.cob       TCPSTTTEST.cob
	$(COBP2) 			../DYNTEST.cob       TCPDYNTEST.cob
	$(COBP2) 			../DYNTEST1.cob      TCPDYNTEST1.cob
	$(COBP2) 			../DYNTEST2.cob       TCPDYNTEST2.cob
	$(COBP2) 			../SEQTEST.cob       TCPSEQTEST.cob
	$(COBP2) 			../WRTTEST.cob       TCPWRTTEST.cob
	$(COBP2) 			../IDXTEST.cob       TCPIDXTEST.cob
	$(COBP2) 			../WRTTEST4.cob      TCPWRTTEST4.cob
	$(COBP2) 			../SEQTEST4.cob      TCPSEQTEST4.cob
	$(COBP2) 			../OPTTEST.cob       TCPOPTTEST.cob
	$(COBP2) 			../MAINTEST.cob      TCPMAINTEST.cob
	$(COBP2) 			../TYPESTEST.cob     TCPTYPESTEST.cob
	$(COBP4) 			../SUBTEST.cob       TCPSUBTEST.cob
	$(COBP2) 			../LENTEST.cob       TCPLENTEST.cob
	
link_consts:
	$(CP) ../../../cobs/ACMCONSTS.CBL ./
	$(CP) ../ACMCONSTS2.CBL ./
	$(CP) ../*.cbl ./

cobc: link_consts cobpp
	$(COBC) $(CFLAGS) 		TCPSEQTEST.cob   -o TCPSEQTEST	
	$(COBC) $(CFLAGS) 		TCPWRTTEST.cob   -o TCPWRTTEST
	$(COBC) $(CFLAGS) 		TCPSEQTEST2.cob  -o TCPSEQTEST2
	$(COBC) $(CFLAGS) 		TCPWRTTEST2.cob  -o TCPWRTTEST2
	$(COBC) $(CFLAGS) 		TCPSEQTEST2.cob  -o TCPSEQTEST2
	$(COBC) $(CFLAGS) 		TCPSTTTEST.cob   -o TCPSTTTEST
	$(COBC) $(CFLAGS) 		TCPDYNTEST.cob   -o TCPDYNTEST
	$(COBC) $(CFLAGS) 		TCPDYNTEST1.cob  -o TCPDYNTEST1
	$(COBC) $(MFLAGS) 		TCPDYNTEST2.cob  -o TCPDYNTEST2.so
	$(COBC) $(CFLAGS) 		TCPIDXTEST.cob   -o TCPIDXTEST
	$(COBC) $(CFLAGS) 		TCPWRTTEST4.cob  -o TCPWRTTEST4
	$(COBC) $(CFLAGS) 		TCPSEQTEST4.cob  -o TCPSEQTEST4
	$(COBC) $(CFLAGS) 		TCPOPTTEST.cob   -o TCPOPTTEST
	$(COBC) $(CFLAGS) 		TCPMAINTEST.cob  -o TCPMAINTEST
	$(COBC) $(CFLAGS) 		TCPTYPESTEST.cob -o TCPTYPESTEST
	$(COBC) $(MFLAGS) 		TCPSUBTEST.cob   -o TCPSUBTEST.so
	$(COBC) $(CFLAGS) 		TCPLENTEST.cob   -o TCPLENTEST	

clean:
	rm TCP*
	rm ACMCONSTS.CBL
	rm ACMCONSTS2.CBL
	rm *.cbl

