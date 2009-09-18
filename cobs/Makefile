CC = gcc
COBC = cobc
COBFLAGS = -c `cob-config --cflags`
JDKLIB = -I/usr/lib/jvm/java-1.5.0-sun-1.5.0.11/include/
JDKOSLIB = -I/usr/lib/jvm/java-1.5.0-sun-1.5.0.11/include/linux/

all:
	$(COBC) -Wall       sample01.cob
	$(COBC) -Wall       sample02.cob
	$(COBC) -Wall       sample03.cob
	$(COBC) -Wall       sample04.cob
	$(COBC) -Wall       sample05.cob
	$(COBC) -Wall       sample06.cob

testsub:
	$(CC) $(COBFLAGS) $(JDKLIB) $(JDKOSLIB) -Wall -shared -v sampleJniCall1.c -o libsampleJniCall.so
	$(COBC) -Wall       TESTSUB.COB

clean:
	rm sample01
	rm sample02
	rm sample03
	rm sample04
	rm sample05
	rm sample06
	rm sample07
	rm TESTSUB
	rm libsampleJniCall.so



