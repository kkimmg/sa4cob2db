#define     _GNU_SOURCE
#include    <stdio.h>
#include    <unistd.h>
#include    <stdlib.h>
#include    <string.h>
#include    <errno.h>
#include    <signal.h>
#include    <getopt.h>
#include    <sys/param.h>
#include    <sys/types.h>
/***************************************/
#include    <jni.h>
#include    "getJNIOption.h"
#include    "config.h"
/***************************************/
#define    DEFAULT_LINEOUT "true"
#define    DEFAULT_DIAPLAY_USAGE "false"
/** オプション */
static struct option longopts[] = {
    {"metafile", required_argument, NULL, 'm'},
    {"lineout", required_argument, NULL, 'l'},
    {"help", required_argument, NULL, 'h'},
    {0, 0, 0, 0}
}
/** 主処理 */
int main (int argc, char *argv[]) {
	int opt;
	char* metafile = getConfigFile();
	char* lineout = DEFAULT_LINOUT;
	char* display_usage = DEFAULT_DISPLAY_USAGE;
	while ((opt = getopt_long(argc, argv, "m:l:d:", longopts, NULL)) != -1) {
		switch (opt) {
			case 'm':
                metafile = optarg;
				break;
			case 'l':
                lineout = optarg;
				break;
			case 'h':
                display_usage = optarg;
				break;			
		}
	}
}
