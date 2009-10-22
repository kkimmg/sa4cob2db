CC = gcc
COBC = cobc
CFLAGS = -v -Wall -g -x
SFLAGS = -v -Wall -g
#JDKLIB = -I/usr/local/jdk1.5.0/include/
#JDKOSLIB = -I/usr/local/jdk1.5.0/include/freebsd/
JDKLIB = -I/usr/lib/jvm/java-6-sun/include/
JDKOSLIB = -I/usr/lib/jvm/java-6-sun/include/linux/

all:
	$(CC)   $(COBFLAGS) $(JDKLIB) $(JDKOSLIB) $(SFLAGS) sampleJniCall1.c -o libsampleJniCall.o
	$(COBC) $(SFLAGS) -m -o libsampleJniCall.so libsampleJniCall.o 
	$(COBC) $(SFLAGS) -m    TESTSUB.COB -o libTESTSUB.so

clean:
	rm libsampleJniCall.so
	rm libsampleJniCall.o
	rm libTESTSUB.so

