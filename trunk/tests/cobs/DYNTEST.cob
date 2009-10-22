000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000200*����ץ�ץ������
000300*���ϥե�����(SEQ)�����ϥե�����(SEQ)��ž������
000400 IDENTIFICATION DIVISION.
000500 PROGRAM-ID.                 DYNTEST.
000600*AUTHOR.                     KENJI KIMURA.
000700 ENVIRONMENT DIVISION.
000800 INPUT-OUTPUT SECTION.
000900 FILE-CONTROL.
001000*    ���ϥե�����
001100*ACMFILE
001200*ACMRECNAME=I-RECORD
001300 SELECT INP-FILE ASSIGN TO "dbtests2"
001400        ORGANIZATION IS INDEXED
001500        ACCESS MODE  IS DYNAMIC
001600        RECORD KEY   IS I-ID.
001700 DATA DIVISION.
001800 FILE SECTION.
001900*���ϥե�����
002000 FD  INP-FILE.
002100 COPY "I_RECORD2.cbl".
002200 WORKING-STORAGE SECTION.
002300 01  SOME-AREA.
002400     05  I-COUNTER           PIC 9(05).
002500     05  O-COUNTER           PIC 9(05).
002600     05  END-FLG             PIC 9(01)  VALUE  ZERO.
002700*
002800 COPY "SCR_RECORD.cbl".
002900*����
003000 SCREEN SECTION.
003100*
003200 COPY "DSP_RECORD.cbl".
003300*
003400 PROCEDURE DIVISION.
003500*�������
003600 MAIN SECTION.
003700     PERFORM   INIT.
003800     PERFORM   FL-OPEN.
003900*    ���ϥե����뤬��λ����ޤǷ����֤�
004000     PERFORM   UNTIL  END-FLG  NOT  =  ZERO
004100        DISPLAY   DSP-RECORD2
004200        ACCEPT    DSP-RECORD1
004300        EVALUATE  SCR-PROC
004400        WHEN  0
004500*           ��λ
004600            MOVE  1  TO  END-FLG
004700        WHEN  1
004800*           �ɲ�
004900            PERFORM  OUT-WRITE
005000        WHEN  2
005100*           �ɤ߹���
005200            PERFORM  INP-READ
005300        WHEN  3
005400*           ����
005500            PERFORM  OUT-REWRITE
005600        WHEN  9
005700*           ���
005800            PERFORM  OUT-DELETE
005900        END-EVALUATE
006000     END-PERFORM.
006100     PERFORM   FL-CLOSE.
006200     PERFORM   TERM.
006300     STOP RUN.
006400*���Ͻ���
006500 INIT SECTION.
006600     DISPLAY   "PROGRAM STARTING.".
006700     EXIT.
006800*�ե�����򳫤���
006900 FL-OPEN SECTION.
007000     OPEN   I-O  INP-FILE.
007100     EXIT.
007200*���Ͻ�����
007300 INP-READ SECTION.
007400     MOVE SCR-ID  TO  I-ID.
007500     READ INP-FILE 
007600     INVALID KEY
007700         DISPLAY  "CANT FIND"
007800         MOVE  1  TO  END-FLG
007900     END-READ.
008000     IF  END-FLG  =  ZERO
008100*        ��ü��ã���Ƥ��ʤ���Х����󥿡�����ʬ
008200         ADD  1              TO  I-COUNTER
008300         PERFORM REC2SCR
008400     END-IF.
008500     EXIT.
008600*���Ͻ�����
008700 OUT-WRITE SECTION.
008800     PERFORM SCR2REC.
008900     WRITE I-RECORD
009000     INVALID KEY
009100         MOVE  1  TO  END-FLG
009200     NOT INVALID KEY
009300         INITIALIZE   SCR-RECORD
009400     END-WRITE.
009500     ADD  1                  TO  O-COUNTER.
009600     EXIT.
009700*���Ͻ�����
009800 OUT-REWRITE SECTION.
009900     PERFORM SCR2REC.
010000     REWRITE I-RECORD
010100     INVALID  KEY
010200         MOVE  1  TO  END-FLG
010300     NOT INVALID KEY
010400         INITIALIZE   SCR-RECORD
010500     END-REWRITE.
010600     ADD  1                  TO  O-COUNTER.
010700     EXIT.
010800*���Ͻ�����
010900 OUT-DELETE SECTION.
011000     PERFORM SCR2REC.
011100     DELETE INP-FILE
011200     INVALID  KEY
011300         MOVE  1  TO  END-FLG
011400     NOT INVALID KEY
011500         INITIALIZE   SCR-RECORD
011600     END-DELETE.
011700     ADD  1                  TO  O-COUNTER.
011800     EXIT.
011900*�쥳����ž����
012000 REC2SCR SECTION.
012100     MOVE I-ID            TO SCR-ID.
012200     MOVE I-CD            TO SCR-CD.
012300     MOVE I-NIHONGO       TO SCR-NIHONGO.
012400*    MOVE I-SEISU         TO SCR-SEISU.
012500     IF  I-SEISU  >  ZERO
012600         MOVE I-SEISU     TO SCR-SEISU
012700         MOVE ZERO        TO SCR-SEISU-FLG
012800     ELSE
012900         COMPUTE SCR-SEISU = I-SEISU * (-1)
013000         MOVE    1        TO SCR-SEISU-FLG
013100     END-IF
013200     MOVE I-HIZUKE-YYYY   TO SCR-HIZUKE-YYYY.
013300     MOVE I-HIZUKE-MM     TO SCR-HIZUKE-MM.
013400     MOVE I-HIZUKE-DD     TO SCR-HIZUKE-DD.
013500     MOVE I-JIKOKU-HH     TO SCR-JIKOKU-HH.
013600     MOVE I-JIKOKU-MM     TO SCR-JIKOKU-MM.
013700     MOVE I-JIKOKU-SS     TO SCR-JIKOKU-SS.
013800*    MOVE I-FUDOU         TO SCR-FUDOU.
013900     EXIT.
014000*�쥳����ž����
014100 SCR2REC SECTION.
014200     MOVE SCR-ID          TO I-ID.
014300     MOVE SCR-CD          TO I-CD.
014400     MOVE SCR-NIHONGO     TO I-NIHONGO.
014500*    MOVE SCR-SEISU       TO I-SEISU.
014600     IF  SCR-SEISU-FLG  = ZERO
014700         MOVE SCR-SEISU       TO I-SEISU
014800     ELSE
014900         COMPUTE  I-SEISU  = SCR-SEISU * (-1)
015000     END-IF
015100     MOVE SCR-HIZUKE-YYYY TO I-HIZUKE-YYYY.
015200     MOVE SCR-HIZUKE-MM   TO I-HIZUKE-MM.
015300     MOVE SCR-HIZUKE-DD   TO I-HIZUKE-DD.
015400     MOVE SCR-JIKOKU-HH   TO I-JIKOKU-HH.
015500     MOVE SCR-JIKOKU-MM   TO I-JIKOKU-MM.
015600     MOVE SCR-JIKOKU-SS   TO I-JIKOKU-SS.
015700*    MOVE SCR-FUDOU       TO I-FUDOU.
015800     EXIT.
015900*�ե�������Ĥ�����
016000 FL-CLOSE SECTION.
016100     CLOSE  INP-FILE.
016200     EXIT.
016300*��λ����
016400 TERM SECTION.
016500     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
016600     DISPLAY   "INPUT-COUNT:" I-COUNTER.
016700     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
016800     EXIT.
016900