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
 * ソケットの初期化
 * サーバー側へのコネクション確立まで
 */
extern int
initialize (char *hostname, char *hostport) {
	/* ホスト名がIPアドレスと仮定してホスト情報取得 */
	if ((addr.s_addr = inet_addr (hostname)) == -1) {
		/* ホスト名が名称としてホスト情報取得 */
		host = gethostbyname (hostname);
		if (host == NULL) {
			perror ("gethostbyname");
			return (-1);
		}
		aptr = (struct in_addr *) *host->h_addr_list;
		memcpy (&addr, aptr, sizeof (struct in_addr));
	}
	/* ソケットの生成 */
	if ((soc = socket (AF_INET, SOCK_STREAM, 0)) < 0) {
		perror ("socket");
		return (-1);
	}
	/* ポート番号の決定 */
	memset ((char *) &server, 0, sizeof (server));
	server.sin_family = AF_INET;
	if ((se = getservbyname (hostport, "tcp")) == NULL) {
		/* サービスに見つからない:ポート番号数値 */
		if ((portno = atoi (hostport)) == 0) {
			fprintf (stderr, "bad port no\n");
			return (-1);
		}
		server.sin_port = htons (portno);
	} else {
		/* サービスに見つかった:該当ポート番号 */
		server.sin_port = se->s_port;
	}
	/* ホストアドレスの指定 */
	server.sin_addr = addr;
	/* コネクト */
	if (connect (soc, (struct sockaddr *) &server, sizeof (server)) == -1) {
		perror ("connect");
		return (-1);
	}
	return 0;
}

/**
 * 改行コードの送信
 * @return int < 0 送信に失敗した
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
 * メッセージの送信
 * @param message	メッセージ
 */
extern int
sendMessage (char *message) {
	int ret = send (soc, message, strlen (message), 0);
	return ret;
}

/**
 * レコードの送信
 * @param record	レコード
 */
extern int
sendRecord (char *record) {
	int ret = send (soc, record, RECORD_LEN, 0);
	return ret;
}

/**
 * メッセージの受信
 * @return 受信メッセージ長またはエラー
 *         エラー時にも0を返す
 */
extern int
recieveMessage () {
	if ((len = recv (soc, buf, sizeof (buf), 0)) < 0) {
		return 0;
	}
	return len;
}

/**
 * ファイルステータスの受信
 * @return 受信メッセージ長またはエラー
 *         エラー時にも0を返す
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
 * メッセージの受信
 * @return 受信メッセージ長またはエラー
 *         エラー時にも0を返す
 */
extern int
recieveRecord () {
	if ((len = recv (soc, recbuf, sizeof (recbuf), 0)) < 0) {
		return 0;
	}
	return len;
}

/**初期化処理*/
extern void
initializeSession (char *hostname, char *hostport, char *username, char *password, char *status) {
	/* 初期化の初期化！ */
	if (initialize (hostname, hostport) < 0) {
		strcpy (status, STATUS_FAILURE);
		return;
	}
	/* 送信（コマンド） */
	if (sendMessage (MSG_INITIALIZE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信（ステータス） */
	if (recieveMessage () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	buf[len] = '\0';
	/* ステータスチェック */
	if (strcmp (buf, MSG_USERNAME) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ユーザー名） */
	if (sendMessage (username) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 （ステータス）*/
	if (recieveMessage () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	buf[len] = '\0';
	/* ユーザー名の次はパスワード要求がくるはずだ */
	if (strcmp (buf, MSG_PASSWORD) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（パスワード） */
	if (sendMessage (password) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信（ステータス） */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**初期化処理*/
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

/**終了処理*/
void
terminateSession (char *status) {
	/* 送信（コマンド） */
	if (sendMessage (MSG_TERMINATE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信（ステータス） */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	buf[len] = '\0';
	strcpy (status, buf);
	return;
}

/**ファイルのアサイン
 * @param	name	ファイルの識別子
 * @param	status	ステータス
 */
extern void
assignACMFile (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_ASSIGN) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信（ステータス） */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータス確認 */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信（ステータス） */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * ファイルのオープン
 * @param name	ファイル名
 * @param openmode	オープンモード<br/>
 *			INPUT|OUTPUT|EXTEND|IO
 * @param accessmode	アクセスモード
 *			シーケンシャル|ダイナミック|ランダム
 * @param status	ステータス
 */
extern void
openACMFile (char *name, char *openmode, char *accessmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_OPEN) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* オープンモード */
	openmode[7] = '\0';
	if (sendMessage (openmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* オープンモード */
	accessmode[7] = '\0';
	if (sendMessage (accessmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * ファイルを閉じる
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void
closeACMFile (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_CLOSE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 次のレコードへ
 * @param name	ファイル識別子
 * @param status	ステータス
 */
extern void
nextACMRecord (char *name, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_NEXT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * リード処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
readACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_READ) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (データ) */
	if (recieveRecord () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* 受信したデータをレコード領域に転送 */
	memmove (record, recbuf, (len > RECORD_MAX ? RECORD_MAX : len));
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * ReadNext処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
readNextACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_READNEXT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (データ) */
	if (recieveRecord () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* 受信したデータをレコード領域に転送 */
	memmove (record, recbuf, (len > RECORD_MAX ? RECORD_MAX : len));
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 位置決定及び読み込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
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
 * 位置決定及び読み込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param indexname インデックス名
 * @param status	ステータス
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
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
writeACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_WRITE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信（ステータス） */
	if (recieveStatus () == 0)  {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（レコード本体） */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
rewriteACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_REWRITE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (READY) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（レコード本体） */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
deleteACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_DELETE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（レコード本体） */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
moveACMRecord (char *name, char *record, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_MOVE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（キーレコード） */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
startACMRecord (char *name, char *record, char *startmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_START) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（キーレコード） */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
startACMRecordWith (char *name, char *record, char *indexname, char *startmode, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	indexname[FILE_INDEXNAME_MAX] = '\0';
	startmode[FILE_STARTMODE_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_STTTWITH) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（スタートモード） */
	if (sendMessage (startmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（インデックス名） */
	if (sendMessage (indexname) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（キーレコード） */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
 * 書き込み処理
 * @param name	ファイル識別子
 * @param record	レコード
 * @param status	ステータス
 */
extern void
moveACMRecordWith (char *name, char *record, char *indexname, char *status) {
	name[FILE_IDENT_MAX] = '\0';
	indexname[FILE_OPTION_MAX] = '\0';
	record[RECORD_MAX] = '\0';
	/* 送信（コマンド） */
	if (sendMessage (MSG_MOVE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (name) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（インデックス名） */
	if (sendMessage (indexname) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（キーレコード） */
	if (sendRecord (record) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}


/**
* コミット
* @param status ステータス
*/
extern void
commitACMSession (char *status) {
	/* 送信（コマンド） */
	if (sendMessage (MSG_COMMIT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
* ロールバック
* @param status ステータス
*/
extern void
rollbackACMSession (char *status) {
	/* 送信（コマンド） */
	if (sendMessage (MSG_ROLLBACK) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
* コミットモードを設定する
* @param commitmode コミットモード
* @param status ステータス
*/
extern void
setACMCommitMode (char *commitmode, char *status) {
	/* 送信（コマンド） */
	if (sendMessage (MSG_AUTOCOMMIT) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (commitmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

/**
* トランザクションレベルを設定する
* @param transmode トランザクションレベル
* @param status ステータス
*/
extern void
setACMTransMode (char *transmode, char *status) {
	/* 送信（コマンド） */
	if (sendMessage (MSG_TRNSMODE) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	if (strcmp (stat, STATUS_READY) != 0) {
		strcpy (status, buf);
		return;
	}
	/* 送信（ファイル名） */
	if (sendMessage (transmode) < 0) {
		strcpy (status, STATUS_SEND_ERROR);
		return;
	}
	/* 受信 (ステータス) */
	if (recieveStatus () == 0) {
		strcpy (status, STATUS_RECV_ERROR);
		return;
	}
	/* ステータスチェック */
	strcpy (status, buf);
	return;
}

