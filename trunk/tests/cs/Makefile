CC = gcc
COBC = cobc
CFLAGS = -v -Wall -g -x
SFLAGS = -v -Wall -g
COBFLAGS = -c `cob-config --cflags`
JDKLIB = -I/usr/lib/jvm/default-java/include/
JDKOSLIB = -I/usr/lib/jvm/default-java/include/linux/

all:
	$(CC)   $(COBFLAGS) $(JDKLIB) $(JDKOSLIB) $(SFLAGS) sampleJniCall1.c -o libsampleJniCall.o
	$(COBC) $(SFLAGS) -m -o libsampleJniCall.so libsampleJniCall.o 
	$(COBC) $(SFLAGS) -m    TESTSUB.COB -o libTESTSUB.so

clean:
	rm libsampleJniCall.so
	rm libsampleJniCall.o
	rm libTESTSUB.so

