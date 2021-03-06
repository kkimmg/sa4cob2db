000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000200*
000300*                    (SEQ)                        (SEQ)
000400 IDENTIFICATION              DIVISION.
000500 PROGRAM-ID.                 SEQTEST3.
000600*AUTHOR.                     KENJI KIMURA.
000700 ENVIRONMENT                 DIVISION.
000800 CONFIGURATION               SECTION.
000900 INPUT-OUTPUT                SECTION.
001000 FILE-CONTROL.
001100*
001200*ACMFILE
001300*ACMRECNAME=I-RECORD
001400 SELECT INP-FILE ASSIGN TO "dbtests"
001500        ORGANIZATION IS INDEXED
001600        ACCESS MODE  IS DYNAMIC
001700        RECORD KEY   IS I-ID.
001800 DATA                        DIVISION.
001900 FILE                        SECTION.
002000*
002100 FD  INP-FILE.
002200 COPY "I_RECORD2.cbl".
002300 WORKING-STORAGE             SECTION.
002400 01  SOME-AREA.
002500     05  I-COUNTER           PIC 9(05).
002600     05  O-COUNTER           PIC 9(05).
002700     05  END-FLG             PIC 9(01)  VALUE  ZERO.
002800 PROCEDURE                   DIVISION.
002900*
003000 MAIN                        SECTION.
003100     PERFORM   INIT.
003200     PERFORM   FL-OPEN.
003300     PERFORM  INP-READ.
003400*
003500     PERFORM   UNTIL                                             E
003600-ND-FLG  NOT  =  ZERO
003700*                 OR  I-COUNTER     > 10000
003800        PERFORM  OUT-WRITE
003900        PERFORM  INP-READ
004000     END-PERFORM.
004100     PERFORM   FL-CLOSE.
004200     PERFORM   TERM.
004300     STOP RUN.
004400*
004500 INIT                        SECTION.
004600     DISPLAY   "PROGRAM STARTING.".
004700     EXIT.
004800*
004900 FL-OPEN                     SECTION.
005000     OPEN   INPUT  INP-FILE.
005100     EXIT.
005200*
005300 INP-READ                    SECTION.
005400     READ INP-FILE NEXT
005500       AT END  MOVE 1 TO END-FLG
005600     END-READ.
005700     IF  END-FLG  =  ZERO
005800*
005900         ADD  1              TO  I-COUNTER
006000     END-IF.
006100     EXIT.
006200*
006300 OUT-WRITE                   SECTION.
006400     DISPLAY  I-RECORD.
006500     ADD  1                  TO  O-COUNTER.
006600     EXIT.
006700*
006800 FL-CLOSE                    SECTION.
006900     CLOSE  INP-FILE.
007000     EXIT.
007100*
007200 TERM                        SECTION.
007300     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
007400     DISPLAY   "INPUT-COUNT:" I-COUNTER.
007500     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
007600     EXIT.
