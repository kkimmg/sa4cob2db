#include    <stdio.h>
#include    <unistd.h>
#include    <stdlib.h>
#include    <string.h>
#include    <errno.h>
#include    <signal.h>
#include    <sys/param.h>
#include    <sys/types.h>
#include    <sys/socket.h>
#include    <netinet/in.h>
#include    <arpa/inet.h>
#include    <netdb.h>
/***************************************/
#include    "acmclient.h"
/***************************************/
struct in_addr addr;
struct in_addr *aptr;
struct hostent *host;
struct sockaddr_in server;
struct servent *se;
int soc, portno, len;
char buf[255];
char recbuf[RECORD_LEN];
fd_set Mask, readOk;
int width;
struct timeval timeout;
int error;
char stat[3];
/**
 * �����åȤν����
 * �����С�¦�ؤΥ��ͥ�������Ω�ޤ�
 */
extern int
initialize (char *hostname, char *hostport) {
	/* �ۥ���̾��IP���ɥ쥹�Ȳ��ꤷ�ƥۥ��Ⱦ������ */
	if ((addr.s_addr = inet_addr (hostname)) == -1) {
		/* �ۥ���̾��̾�ΤȤ��ƥۥ��Ⱦ������ */
		host = gethostbyname (hostname);
		if (host == NULL) {
			perror ("gethostbyname");
			return (-1);
		}
		aptr = (struct in_addr *) *host->h_addr_list;
		memcpy (&addr, aptr, sizeof (struct in_addr));
	}
	/* �����åȤ����� */
	if ((soc = socket (AF_INET, SOCK_STREAM, 0)) < 0) {
		perror ("socket");
		return (-1);
	}
	/* �ݡ����ֹ�η��� */
	memset ((char *) &server, 0, sizeof (server));
	server.sin_family = AF_INET;
	if ((se = getservbyname (hostport, "tcp")) == NULL) {
		/* �����ӥ��˸��Ĥ���ʤ�:�ݡ����ֹ���� */
		if ((portno = atoi (hostport)) == 0) {
			fprintf (stderr, "bad port no\n");
			return (-1);
		}
		server.sin_port = htons (portno);
	} else {
		/* �����ӥ��˸��Ĥ��ä�:�����ݡ����ֹ� */
		server.sin_port = se->s_port;
	}
	/* �ۥ��ȥ��ɥ쥹�λ��� */
	server.sin_addr = addr;
	/* ���ͥ��� */
	if (connect (soc, (struct sockaddr *) &server, sizeof (server)) == -1) {
		perror ("connect");
		return (-1);
	}
	return 0;
}

/**
 * ���ԥ����ɤ�����
 * @return int < 0 �����˼��Ԥ���
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
 * ��å�����������
 * @param message	��å�����
 */
extern int
sendMessage (char *message) {
	int ret = send (soc, message, strlen (message), 0);
	return ret;
}

/**
 * �쥳���ɤ�����
 * @param record	�쥳����
 */
extern int
sendRecord (char *record) {
	int ret = send (soc, record, RECORD_LEN, 0);
	return ret;
}

/**
 * ��å������μ���
 * @return ������å�����Ĺ�ޤ��ϥ��顼
 *         ���顼���ˤ�0���֤�
 */
extern int
recieveMessage () {
	if ((len = recv (soc, buf, sizeof (buf), 0)) < 0) {
		return 0;
	}
	return len;
}

/**
 * �ե����륹�ơ������μ���
 * @return ������å�����Ĺ�ޤ��ϥ��顼
 *         ���顼���ˤ�0���֤�
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
 * ��å������μ���
 * @return ������å�����Ĺ�ޤ��ϥ��顼
 *         ���顼���ˤ�0���֤�
 */
extern int
recieveRecord () {
	if ((len = recv (soc, recbuf, sizeof (recbuf), 0)) < 0) {
		return 0;
	}
	return len;
}

/**���������*/
extern void
initializeSession (char *hostname, char *hostport, char *username, char *password, char *status) {
	/* ������ν������ */
	if (initialize (hostname, hostport) < 0) {
		strcpy (status, STATUS_FAILURE);
		return;
	}
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_INITIALIZE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* �����ʥ��ơ������� */
	if (recieveMessage () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	buf[len] = '\0';
	/* ���ơ����������å� */
	if (strcmp (buf, MSG_USERNAME) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ桼����̾�� */
	if (sendMessage (username) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� �ʥ��ơ�������*/
	if (recieveMessage () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	buf[len] = '\0';
	/* �桼����̾�μ��ϥѥ�����׵᤬����Ϥ��� */
	if (strcmp (buf, MSG_PASSWORD) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥѥ���ɡ� */
	if (sendMessage (password) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* �����ʥ��ơ������� */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**���������*/
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

/**��λ����*/
void
terminateSession (char *status) {
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_TERMINATE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* �����ʥ��ơ������� */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	buf[len] = '\0';
	strcpy (status, buf);
	return;
}

/**�ե�����Υ�������
 * @param	name	�ե�����μ��̻�
 * @param	status	���ơ�����
 */
extern void
assignACMFile (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_ASSIGN) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* �����ʥ��ơ������� */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ�������ǧ */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* �����ʥ��ơ������� */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �ե�����Υ����ץ�
 * @param name	�ե�����̾
 * @param openmode	�����ץ�⡼��<br/>
 *			INPUT|OUTPUT|EXTEND|IO
 * @param accessmode	���������⡼��
 *			�������󥷥��|�����ʥߥå�|������
 * @param status	���ơ�����
 */
extern void
openACMFile (char *name, char *openmode, char *accessmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_OPEN) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ץ�⡼�� */
	openmode[7] = '\0';
	if (sendMessage (openmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ץ�⡼�� */
	accessmode[7] = '\0';
	if (sendMessage (accessmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �ե�������Ĥ���
 * @param name	�ե����뼱�̻�
 * @param status	���ơ�����
 */
extern void
closeACMFile (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_CLOSE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * ���Υ쥳���ɤ�
 * @param name	�ե����뼱�̻�
 * @param status	���ơ�����
 */
extern void
nextACMRecord (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_NEXT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �꡼�ɽ���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
readACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_READ) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (�ǡ���) */
	if (recieveRecord () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���������ǡ�����쥳�����ΰ��ž�� */
	memmove (record, recbuf, (len > RECORD_MAX ? RECORD_MAX : len));
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * ReadNext����
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
readNextACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_READNEXT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (�ǡ���) */
	if (recieveRecord () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���������ǡ�����쥳�����ΰ��ž�� */
	memmove (record, recbuf, (len > RECORD_MAX ? RECORD_MAX : len));
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * ���ַ���ڤ��ɤ߹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
moveReadACMRecord (char *name, char *record, char *status) {
	moveACMRecord(name, record, status);
	if (strcmp(stat, STATUS_OK) != 0) {
		return;
	}
	readACMRecord(name, record, status);
	return;
}

/**
 * ���ַ���ڤ��ɤ߹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param indexname ����ǥå���̾
 * @param status	���ơ�����
 */
extern void
moveReadACMRecordWith (char *name, char *record, char *indexname, char *status) {
	moveACMRecordWith(name, record, indexname, status);
	if (strcmp(status, STATUS_OK) != 0) {
		return;
	}
	readACMRecord(name, record, status);
	return;
}

/**
 * �񤭹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
writeACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_WRITE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* �����ʥ��ơ������� */
	if (recieveStatus () == 0)  {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ쥳�������Ρ� */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �񤭹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
rewriteACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_REWRITE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (READY) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ쥳�������Ρ� */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �񤭹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
deleteACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_DELETE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ쥳�������Ρ� */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �񤭹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
moveACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_MOVE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ����쥳���ɡ� */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �񤭹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
startACMRecord (char *name, char *record, char *startmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_START) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ����쥳���ɡ� */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �񤭹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
startACMRecordWith (char *name, char *record, char *indexname, char *startmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	indexname[FILE_INDEXNAME_MAX] = '\0';
	startmode[FILE_STARTMODE_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_STTTWITH) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ������ȥ⡼�ɡ� */
	if (sendMessage (startmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ���ǥå���̾�� */
	if (sendMessage (indexname) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ����쥳���ɡ� */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
 * �񤭹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void
moveACMRecordWith (char *name, char *record, char *indexname, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	indexname[FILE_OPTION_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_MOVE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ���ǥå���̾�� */
	if (sendMessage (indexname) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥ����쥳���ɡ� */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}


/**
* ���ߥå�
* @param status ���ơ�����
*/
extern void
commitACMSession (char *status) {
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_COMMIT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
* ����Хå�
* @param status ���ơ�����
*/
extern void
rollbackACMSession (char *status) {
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_ROLLBACK) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
* ���ߥåȥ⡼�ɤ����ꤹ��
* @param commitmode ���ߥåȥ⡼��
* @param status ���ơ�����
*/
extern void
setACMCommitMode (char *commitmode, char *status) {
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_AUTOCOMMIT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (commitmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

/**
* �ȥ�󥶥�������٥�����ꤹ��
* @param transmode �ȥ�󥶥�������٥�
* @param status ���ơ�����
*/
extern void
setACMTransMode (char *transmode, char *status) {
	/* �����ʥ��ޥ�ɡ� */
	if (sendMessage (MSG_TRNSMODE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* �����ʥե�����̾�� */
	if (sendMessage (transmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* ���� (���ơ�����) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ���ơ����������å� */
	strcpy (status, buf);
	return;
}

