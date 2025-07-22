#include    <stdio.h>
//#include    <unistd.h>
#include    <stdlib.h>
#include    <string.h>
#include    <errno.h>
#include    <signal.h>
//#include    <sys/param.h>
#include    <sys/types.h>
/***************************************/
#include    <winsock2.h>
#include    <ws2tcpip.h>
#pragma comment(lib, "Ws2_32.lib")
/***************************************/
#include    "acmclient.h"
/***************************************/
struct sockaddr_in addr;
struct sockaddr_in *aptr;
struct hostent *host;
struct sockaddr_in server;
struct servent *se;
int soc, portno, len;
char buf[256];
int record_len, record_max;
char *recbuf;
fd_set Mask, readOk;
int width;
struct timeval timeout;
int error;
char stat[3];
/**
 * initalize socket and connet to server
 */
extern int
initialize (char *hostname, char *hostport) {
	record_len = RECORD_LEN;
	record_max = RECORD_MAX;
	recbuf = malloc(record_len);
	if (recbuf == NULL) {
		fprintf(stderr, "can't malloc\n");
		return (-1);
	}
	memset(recbuf, ' ', record_max);
	recbuf[record_max] = '\0';
	/* get host */
	if ((addr.sin_addr.s_addr = inet_addr (hostname)) == -1) {
		host = gethostbyname (hostname);
		if (host == NULL) {
			perror ("gethostbyname");
			return (-1);
		}
		aptr = (struct sockaddr_in *) *host->h_addr_list;
		memcpy (&addr, aptr, sizeof (struct in_addr));
	}
	/* socket */
	if ((soc = socket (AF_INET, SOCK_STREAM, 0)) < 0) {
		perror ("socket");
		return (-1);
	}
	/* port */
	memset ((char *) &server, 0, sizeof (server));
	server.sin_family = AF_INET;
	if ((se = getservbyname (hostport, "tcp")) == NULL) {
		if ((portno = atoi (hostport)) == 0) {
			fprintf (stderr, "bad port no\n");
			return (-1);
		}
		server.sin_port = htons (portno);
	} else {
		server.sin_port = se->s_port;
	}
	server.sin_addr = addr.sin_addr;
	/* connect */
	if (connect (soc, (struct sockaddr *) &server, sizeof (server)) == -1) {
		perror ("connect");
		return (-1);
	}
	return 0;
}

/**
 * send "\n"
 */
extern int
sendReturn (void) {
	if ((len = send (soc, "\n", strlen ("\n"), 0)) < 0) {
		perror ("sendReturn");
		return (-1);
	}
	return 0;
}

/**
 * send message
 */
extern int
sendMessage (char *message) {
/*fprintf(stderr, "1sendMessage:%d:", strlen(message));
fprintf(stderr, message);
fprintf(stderr, "\n");*/
	int ret = send (soc, message, strlen (message), 0);
/*fprintf(stderr, "2sendMessage:%d:", strlen(message));
fprintf(stderr, message);
fprintf(stderr, ":%d\n", ret);*/

	return ret;
}

/**
 * send record
 */
extern int
sendRecord (char *record) {
	int ret = send (soc, record, record_len, 0);
	return ret;
}

/**
 * recieve message
 */
extern int
recieveMessage () {
	memset(buf, ' ', 255);
	buf[255] = '\0';
	if ((len = recv (soc, buf, 256, 0)) < 0) {
		return 0;
	}
/*
fprintf(stderr, "recvMessage:%d:", len);
fprintf(stderr, buf);
fprintf(stderr, ":%d\n", strlen(buf));
*/
	return len;
}


/**
 * recieve status
 */
extern int
recieveStatus () {
	if ((len = recieveMessage()) < 3) {
		return 0;
	}
	strncpy (stat, buf, len);
	stat[2] = '\0';
	return len;
}

/**
 * recieve message
 */
extern int
recieveRecord () {
	if ((len = recv (soc, recbuf, record_len, 0)) < 0) {
		return 0;
	}
	return len;
}

/**
 * initialize
 */
extern void
initializeSession (char *hostname, char *hostport, char *username, char *password, char *status){ 
	WORD wVersionRequested;
	WSADATA wsaData;
    wVersionRequested = WSAStartup(MAKEWORD(2, 0), &wsaData);
	if (0 != wVersionRequested) {
		strcpy(status, STATUS_FAILURE);
		return;
	}

	/* socket */
	if (initialize (hostname, hostport) < 0) {
		strcpy (status, STATUS_FAILURE);
		return;
	}
	/* send(command) */
	if (sendMessage (MSG_INITIALIZE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveMessage () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	buf[len] = '\0';
	/* check status */
	if (strcmp (buf, MSG_USERNAME) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(user name) */
	if (sendMessage (username) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status)*/
	if (recieveMessage () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	buf[len] = '\0';
	/* password */
	if (strcmp (buf, MSG_PASSWORD) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(password) */
	if (sendMessage (password) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * dummy for OpenCobol
 */
extern void
libACMClient () {
}

/**initialize using environment value*/
extern void
initializeSessionEnv (char *status) {
	char *hostname, *hostport, *username, *password;
	hostname = getenv(ACM_HOSTNAME_ENV);
	if (hostname == NULL) {
		hostname = "localhost";
	}
	hostport = getenv(ACM_HOSTPORT_ENV);
	if (hostport == NULL) {
		hostport = "12345";
	}
	username = getenv(ACM_USERNAME_ENV);
	if (username == NULL) {
		username = "        ";
	}
	password = getenv(ACM_PASSWORD_ENV);
	if (password == NULL) {
		password = "        ";
	}
	initializeSession (hostname, hostport, username, password, status);
}

/**terminate*/
void
terminateSession (char *status) {
	/* send(command) */
	if (sendMessage (MSG_TERMINATE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	buf[len] = '\0';
	strcpy (status, buf);
	//
	free(recbuf);
	//
	WSACleanup();
	return;
}

/**
 * asign file
 */
extern void
assignACMFile (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_ASSIGN) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * open file
 */
extern void
openACMFile (char *name, char *openmode, char *accessmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_OPEN) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* open mode */
	openmode[7] = '\0';
	if (sendMessage (openmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* access mode */
	accessmode[7] = '\0';
	if (sendMessage (accessmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * close file
 */
extern void
closeACMFile (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_CLOSE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * move to next record
 */
extern void
nextACMRecord (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_NEXT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * read from file
 */
extern void
readACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_READ) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(data) */
	if (recieveRecord () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* move data that was recieved from server to record area. */
	memmove (record, recbuf, (len > record_max ? record_max : len));
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * read next
 */
extern void
readNextACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_READNEXT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(data) */
	if (recieveRecord () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* move data that was recieved from server to record area. */
	memmove (record, recbuf, (len > record_max ? record_max : len));
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * move and read
 */
extern void
moveReadACMRecord (char *name, char *record, char *status) {
	char back[FILE_IDENT_LEN];
	name[FILE_IDENT_MAX] = '\0';
	strcpy(back, name);

	moveACMRecord(name, record, status);
	if (strcmp(stat, STATUS_OK) != 0) {
		return;
	}
	strcpy(name, back);
	
	//strcpy(name, "dbtests2");
	readACMRecord(name, record, status);
	//fprintf(stderr, ":third=");
	//fprintf(stderr, name);
	//fprintf(stderr, "\n");
	return;
}

/**
 * move and read
 */
extern void
moveReadACMRecordWith (char *name, char *record, char *indexname, char *status) {
	char back[FILE_IDENT_LEN];
	name[FILE_IDENT_MAX] = '\0';
	strcpy(back, name);

	moveACMRecordWith(name, record, indexname, status);
	if (strcmp(status, STATUS_OK) != 0) {
		return;
	}
	strcpy(name, back);
	
	readACMRecord(name, record, status);
	return;
}

/**
 * write/insert
 */
extern void
writeACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[record_max] = '\0';
	/* send(command) */
	if (sendMessage (MSG_WRITE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0)  {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(record) */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * rewrite/update
 */
extern void
rewriteACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[record_max] = '\0';
	/* send(command) */
	if (sendMessage (MSG_REWRITE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* send(READY) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(record) */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * delete/delete
 */
extern void
deleteACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_DELETE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(record) */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * move 
 */
extern void
moveACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[record_max] = '\0';
	/* send(command) */
	if (sendMessage (MSG_MOVE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(key) */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * start
 */
extern void
startACMRecord (char *name, char *record, char *startmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[record_max] = '\0';
	/* send(command) */
	if (sendMessage (MSG_START) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(key) */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * start by index
 */
extern void
startACMRecordWith (char *name, char *record, char *indexname, char *startmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	indexname[FILE_INDEXNAME_MAX] = '\0';
	startmode[FILE_STARTMODE_MAX] = '\0';
	record[record_max] = '\0';
	/* send(command) */
	if (sendMessage (MSG_STTTWITH) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(mode) */
	if (sendMessage (startmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(index name) */
	if (sendMessage (indexname) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(key) */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * move by index
 */
extern void
moveACMRecordWith (char *name, char *record, char *indexname, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	indexname[FILE_OPTION_MAX] = '\0';
	record[record_max] = '\0';
	/* send(command) */
	if (sendMessage (MSG_MOVE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(index name) */
	if (sendMessage (indexname) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(key) */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
* push File List
*/
extern void
pushACMFileList (char *status) {
	/* send(command) */
	if (sendMessage (MSG_PUSHFILELIST) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
* push File List
*/
extern void
popACMFileList (char *status) {
	/* send(command) */
	if (sendMessage (MSG_POPFILELIST) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
* commit
*/
extern void
commitACMSession (char *status) {
	/* send(command) */
	if (sendMessage (MSG_COMMIT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * rollback
 */
extern void
rollbackACMSession (char *status) {
	/* send(command) */
	if (sendMessage (MSG_ROLLBACK) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * set auto commit
 */
extern void
setACMCommitMode (char *commitmode, char *status) {
	/* send(command) */
	if (sendMessage (MSG_AUTOCOMMIT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(file name) */
	if (sendMessage (commitmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/**
 * set transaction level
 */
extern void
setACMTransMode (char *transmode, char *status) {
	/* send(command) */
	if (sendMessage (MSG_TRNSMODE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(mode) */
	if (sendMessage (transmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/** set option */
extern void
setACMOptionFromEnv (char *name, char *envkey, char *status) {
	char *wvalue;
	char value[OPTIONVALUE_LEN];
	envkey[OPTIONVALUE_MAX] = '\0';
	int i;
	for (i = OPTIONVALUE_MAX - 1; i > 0; i--) {
		if (envkey[i] == ' ') {
			envkey[i] = '\0';
		} else {
			break;
		}
	}
	wvalue = getenv(envkey);
	if (wvalue == NULL) {
		wvalue = "";
	} else if (strlen(wvalue) >= OPTIONVALUE_LEN) {
		wvalue[OPTIONVALUE_MAX] = '\0';
	}
	memset(value, '\0', OPTIONVALUE_MAX);
	strcpy(value, wvalue);
	setACMOption(name, value, status);
	memcpy(envkey, value, OPTIONVALUE_LEN);
}

/** set option */
extern void
setACMOption (char *name, char *value, char *status) {
	name[OPTIONNAME_MAX] = '\0';
	value[OPTIONVALUE_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_SETOPTION) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(option name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(value) */
	if (sendMessage (value) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

extern void
getACMOption (char *name, char *value, char *status) {
	name[OPTIONNAME_MAX] = '\0';
	/* send(command) */
	if (sendMessage (MSG_GETOPTION) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(option name) */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(data) */
	if (recieveMessage () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* move data that was recieved from server to record area. */
	memmove (value, buf, (len > OPTIONVALUE_MAX ? OPTIONVALUE_MAX : len));
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	strcpy (status, buf);
	return;
}

/** set record length */
extern void
setACMMaxLength (char *length, char *status) {
	length[OPTIONVALUE_MAX] = '\0';
	int w_length;
	if ((w_length = atoi (length)) == 0) {
		fprintf (stderr, "bad length\n");
		return;
	}
	/* send(command) */
	if (sendMessage (MSG_SETLENGTH) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* check status */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* send(value) */
	if (sendMessage (length) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* recieve(status) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	//
	free(recbuf);
	record_len = w_length;
	record_max = record_len - 1;
	recbuf = malloc(record_len);
	if (recbuf == NULL) {
		fprintf(stderr, "can't malloc\n");
		return;
	}
	memset(recbuf, ' ', record_max);
	recbuf[record_max] = '\0';
	/* check status */
	strcpy (status, buf);
	return;
}
