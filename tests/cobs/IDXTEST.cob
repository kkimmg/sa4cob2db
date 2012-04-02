000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000200*サンプルプログラム
000300*入力ファイル(SEQ)→出力ファイル(SEQ)の転記処理
000400 IDENTIFICATION              DIVISION.
000500 PROGRAM-ID.                 IDXTEST.
000600*AUTHOR.                     KENJI KIMURA.
000700 ENVIRONMENT                 DIVISION.
000800 CONFIGURATION               SECTION.
000900 INPUT-OUTPUT                SECTION.
001000 FILE-CONTROL.
001100*    入力ファイル
001200*ACMFILE
001300*ACMRECNAME=I-RECORD
001400     SELECT INP-FILE ASSIGN TO "dbtests2"
001500       ORGANIZATION IS INDEXED
001600       ACCESS MODE     DYNAMIC
001700       RECORD KEY   IS I-ID
001800       ALTERNATE RECORD KEY IS I-SEISU.  
001900 DATA                        DIVISION.
002000 FILE                        SECTION.
002100*入力ファイル
002200 FD  INP-FILE.
002300 COPY "I_RECORD.cbl".
002400 WORKING-STORAGE             SECTION.
002500 01  SOME-AREA.
002600     05  I-COUNTER           PIC 9(05).
002700     05  O-COUNTER           PIC 9(05).
002800     05  END-FLG             PIC 9(01)  VALUE  ZERO.
002900 PROCEDURE                   DIVISION.
003000*主処理節
003100 MAIN                        SECTION.
003200     PERFORM   INIT.
003300     PERFORM   FL-OPEN.
003400     PERFORM  INP-READ.
003500*    入力ファイルが終了するまで繰り返し
003600     PERFORM   UNTIL  END-FLG  NOT  =  ZERO
003700                  OR  I-SEISU       > 202
003800        PERFORM  OUT-WRITE
003900        PERFORM  INP-READ
004000     END-PERFORM.
004100     PERFORM   FL-CLOSE.
004200     PERFORM   TERM.
004300     STOP RUN.
004400*開始処理
004500 INIT                        SECTION.
004600     DISPLAY   "PROGRAM STARTING.".
004700     EXIT.
004800*ファイルを開く節
004900 FL-OPEN                     SECTION.
005000     OPEN   INPUT  INP-FILE.
005100     MOVE  200    TO  I-SEISU.
005200     START INP-FILE
005300       KEY IS = I-SEISU
005400     INVALID KEY MOVE 10001   TO  I-COUNTER
                       MOVE    1    TO  END-FLG
005500     END-START.
005600     EXIT.
005700*入力処理節
005800 INP-READ                    SECTION.
005900     READ INP-FILE NEXT
006000       AT END  MOVE 1 TO END-FLG
006100     END-READ.
006200     IF  END-FLG  =  ZERO
006300*        終端に達していなければカウンターを増分
006400         ADD  1              TO  I-COUNTER
006500     END-IF.
006600     EXIT.
006700*出力処理節
006800 OUT-WRITE                   SECTION.
006900     DISPLAY  I-RECORD.
007000     ADD  1                  TO  O-COUNTER.
007100     EXIT.
007200*ファイルを閉じる節
007300 FL-CLOSE                    SECTION.
007400     CLOSE  INP-FILE.
007500     EXIT.
007600*終了処理
007700 TERM                        SECTION.
007800     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
007900     DISPLAY   "INPUT-COUNT:" I-COUNTER.
008000     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
008100     EXIT.
