000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000200*                      
000300*                    (SEQ)                        (SEQ)                  
000400 IDENTIFICATION DIVISION.
000500*ACMCOMMENTSTART
000600 PROGRAM-ID.                 DYNTEST2.
000700*ACMCOMMENTEND
000800*ACMINSERTSTART
000900*PROGRAM-ID.                 TCPDYNTEST2.
001000*ACMINSERTEND
001100*AUTHOR.                     KENJI KIMURA.
001200 ENVIRONMENT DIVISION.
001300 INPUT-OUTPUT SECTION.
001400 FILE-CONTROL.
001500*                        
001600*ACMFILE
001700*ACMRECNAME=I-RECORD
001800 SELECT INP-FILE ASSIGN TO "dbtests2"
001900        ORGANIZATION IS INDEXED
002000        ACCESS MODE  IS DYNAMIC
002100        RECORD KEY   IS I-ID.
002200 DATA DIVISION.
002300 FILE SECTION.
002400*                    
002500 FD  INP-FILE.
002600 COPY "I_RECORD2.cbl".
002700 WORKING-STORAGE SECTION.
002800 01  SOME-AREA.
002900     05  I-COUNTER           PIC 9(05).
003000     05  O-COUNTER           PIC 9(05).
003100     05  END-FLG             PIC 9(01)  VALUE  ZERO.
003200 01  FUDOU-AREA.
003300     05  W-FUDOU             PIC S9(4)V9(3).
003400     05  INTDATA             PIC 9(07).
003500     05  INTWORK             PIC 9(07).
003600     05  INT1000             PIC 9(07).
003700     05  INTDEC              PIC 9(07).
003800*
003900 COPY "SCR_RECORD.cbl".
004000 LINKAGE SECTION.
004100 COPY "ACMCONSTS2.CBL".
004200 COPY "SCR_RECORD_RED.cbl".
004300 PROCEDURE DIVISION USING ACM-WEB-IF-PARAMS
004400                          SCR1-RECORD
004500                          SCR2-RECORD.
004600*              
004700 MAIN SECTION.
004800     DISPLAY "HEAD:" ACM-WEB-IF-PARAMS(1:100).
004900     DISPLAY "INPT:" SCR1-RECORD.
005000     DISPLAY "OTPT:" SCR2-RECORD.
005100     MOVE SCR1-RECORD TO SCR-RECORD.
005200     PERFORM   INIT.
005300     PERFORM   FL-OPEN.
005400*                                                      
005500*    PERFORM   UNTIL  END-FLG  NOT  =  ZERO
005600        EVALUATE  SCR-PROC
005700        WHEN  0
005800*                 
005900            MOVE  1  TO  END-FLG
006000        WHEN  1
006100*                 
006200            PERFORM  OUT-WRITE
006300        WHEN  2
006400*                       
006500            PERFORM  INP-READ
006600        WHEN  3
006700*                   
006800            PERFORM  OUT-REWRITE
006900        WHEN  9
007000*                 
007100            PERFORM  OUT-DELETE
007200        END-EVALUATE
007300*    END-PERFORM.
007400     MOVE SCR-RECORD TO SCR2-RECORD.
           DISPLAY "OTPT:" SCR2-RECORD.
007500     PERFORM   FL-CLOSE.
007600     PERFORM   TERM.
007700     STOP RUN.
007800*              
007900 INIT SECTION.
008000     DISPLAY   "PROGRAM STARTING.".
008100     EXIT.
008200*                      
008300 FL-OPEN SECTION.
008400     OPEN   I-O  INP-FILE.
008500     EXIT.
008600*                  
008700 INP-READ SECTION.
008800     MOVE SCR-ID  TO  I-ID.
008900     READ INP-FILE 
009000     INVALID KEY
009100         MOVE  1  TO  END-FLG
009200     END-READ.
009300     IF  END-FLG  =  ZERO
009400*                                                                
009500         ADD  1              TO  I-COUNTER
009600         PERFORM REC2SCR
009700     END-IF.
009800     EXIT.
009900*                  
010000 OUT-WRITE SECTION.
010100     PERFORM SCR2REC.
010200     WRITE I-RECORD
010300     INVALID KEY
010400         MOVE  1  TO  END-FLG
010500     NOT INVALID KEY
010600         INITIALIZE   SCR-RECORD
010700     END-WRITE.
010800     ADD  1                  TO  O-COUNTER.
010900     EXIT.
011000*                  
011100 OUT-REWRITE SECTION.
011200     PERFORM SCR2REC.
011300     REWRITE I-RECORD
011400     INVALID  KEY
011500         MOVE  1  TO  END-FLG
011600     NOT INVALID KEY
011700         INITIALIZE   SCR-RECORD
011800     END-REWRITE.
011900     ADD  1                  TO  O-COUNTER.
012000     EXIT.
012100*                  
012200 OUT-DELETE SECTION.
012300     PERFORM SCR2REC.
012400     DELETE INP-FILE
012500     INVALID  KEY
012600         MOVE  1  TO  END-FLG
012700     NOT INVALID KEY
012800         INITIALIZE   SCR-RECORD
012900     END-DELETE.
013000     ADD  1                  TO  O-COUNTER.
013100     EXIT.
013200*                      
013300 REC2SCR SECTION.
013400     MOVE I-ID            TO SCR-ID.
013500     MOVE I-CD            TO SCR-CD.
013600     MOVE I-NIHONGO       TO SCR-NIHONGO.
013700*    MOVE I-SEISU         TO SCR-SEISU.
013800     IF  I-SEISU  >  ZERO
013900         MOVE I-SEISU     TO SCR-SEISU
014000         MOVE ZERO        TO SCR-SEISU-FLG
014100     ELSE
014200         COMPUTE SCR-SEISU = I-SEISU * (-1)
014300         MOVE    1        TO SCR-SEISU-FLG
014400     END-IF
014500     MOVE I-HIZUKE-YYYY   TO SCR-HIZUKE-YYYY.
014600     MOVE I-HIZUKE-MM     TO SCR-HIZUKE-MM.
014700     MOVE I-HIZUKE-DD     TO SCR-HIZUKE-DD.
014800     MOVE I-JIKOKU-HH     TO SCR-JIKOKU-HH.
014900     MOVE I-JIKOKU-MM     TO SCR-JIKOKU-MM.
015000     MOVE I-JIKOKU-SS     TO SCR-JIKOKU-SS.
015100*    MOVE I-FUDOU         TO SCR-FUDOU.
015200     IF  I-FUDOU  >  ZERO
015300         MOVE  I-FUDOU    TO W-FUDOU
015400         MOVE  ZERO       TO  SCR-FUDOU-FLG
015500     ELSE
015600         COMPUTE  W-FUDOU  =  I-FUDOU * (-1)
015700         MOVE  1          TO  SCR-FUDOU-FLG
015800     END-IF.
015900     COMPUTE  INT1000      =  W-FUDOU  *  1000.
016000     MOVE     W-FUDOU     TO  INTWORK.
016100     COMPUTE  INTDATA      =  INTWORK  *  1000.
016200     COMPUTE  INTDEC       =  INT1000  -  INTDATA.
016300     COMPUTE  SCR-FUDOU1   =  INT1000  /  1000.
016400     COMPUTE  SCR-FUDOU2   =  INTDEC.
016500     EXIT.
016600*                      
016700 SCR2REC SECTION.
016800     MOVE SCR-ID          TO I-ID.
016900     MOVE SCR-CD          TO I-CD.
017000     MOVE SCR-NIHONGO     TO I-NIHONGO.
017100*    MOVE SCR-SEISU       TO I-SEISU.
017200     IF  SCR-SEISU-FLG  = ZERO
017300         MOVE SCR-SEISU       TO I-SEISU
017400     ELSE
017500         COMPUTE  I-SEISU  = SCR-SEISU * (-1)
017600     END-IF
017700     MOVE SCR-HIZUKE-YYYY TO I-HIZUKE-YYYY.
017800     MOVE SCR-HIZUKE-MM   TO I-HIZUKE-MM.
017900     MOVE SCR-HIZUKE-DD   TO I-HIZUKE-DD.
018000     MOVE SCR-JIKOKU-HH   TO I-JIKOKU-HH.
018100     MOVE SCR-JIKOKU-MM   TO I-JIKOKU-MM.
018200     MOVE SCR-JIKOKU-SS   TO I-JIKOKU-SS.
018300*    MOVE SCR-FUDOU       TO I-FUDOU.
018400     COMPUTE  W-FUDOU     =  (SCR-FUDOU1  * 1000
018500                          +   SCR-FUDOU2) / 1000.
018600     IF  SCR-FUDOU-FLG  = ZERO
018700         MOVE  W-FUDOU    TO  I-FUDOU
018800     ELSE
018900         COMPUTE  I-FUDOU  =  W-FUDOU * (-1)
019000     END-IF.
019100     EXIT.
019200*                              
019300 FL-CLOSE SECTION.
019400     CLOSE  INP-FILE.
019500     EXIT.
019600*              
019700 TERM SECTION.
019800     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
019900     DISPLAY   "INPUT-COUNT:" I-COUNTER.
020000     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
020100     EXIT.
020200
