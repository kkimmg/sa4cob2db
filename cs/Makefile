CC = gcc
LN = ln -s
COBLIB_PATH = ./
JDKLIB = -I$(JAVA_HOME)/include/
JDKOSLIB = -I$(JAVA_HOME)/include/linux/
JVMLINK = -L$(JAVA_HOME)/jre/lib/i386/client

all:
	$(CC) -Wall -shared -v acmclient.c -o acmclient.so
	$(CC) $(JDKLIB) $(JDKOSLIB) $(JVMLINK) -ljvm -Wall -shared -v jniclient.c -o jniclient.so

install_opencobol:
	$(LN) $(OBLIB_PATH)acmclient.so assignACMFile.so
	$(LN) $(OBLIB_PATH)acmclient.so initializeSessionEnv.so

clean:
	rm acmclient.so
	rm jniclient.so
	

