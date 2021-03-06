000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000200*                      
000300*                    (SEQ)                        (SEQ)                  
000400 IDENTIFICATION              DIVISION.
000500 PROGRAM-ID.                 WRTTEST.
000600*AUTHOR.                     KENJI KIMURA.
000700 ENVIRONMENT                 DIVISION.
000800 CONFIGURATION               SECTION.
000900 INPUT-OUTPUT                SECTION.
001000 FILE-CONTROL.
001100*                        
001200*ACMFILE
001300*ACMRECNAME=O-RECORD
001400     SELECT OUT-FILE ASSIGN TO "dbtests2"
001500       ORGANIZATION LINE SEQUENTIAL.
001600 DATA                        DIVISION.
001700 FILE                        SECTION.
001800*                    
001900 FD  OUT-FILE.
002000     COPY "O_RECORD2.cbl".
002100 WORKING-STORAGE             SECTION.
002200 01  SOME-AREA.
002300     05  I-COUNTER           PIC 9(05).
002400     05  O-COUNTER           PIC 9(05).
002500     05  END-FLG             PIC 9(01)  VALUE  ZERO.
002600     05  W-COUNTER           PIC S9(05).
002700 01  WORK-AREA.
002800     05  W-A                 PIC S9(09).
002900     05  W-B                 PIC S9(09).
003000     05  W-C                 PIC S9(09).
003100     05  W-D                 PIC S9(09).
003200     05  W-EF.
003300       07  W-E               PIC 9(04).
003400       07  FILLER            PIC X(01)  VALUE  ".".
003500       07  W-F               PIC 9(03).
003600 PROCEDURE                   DIVISION.
003700*              
003800 MAIN                        SECTION.
003900     PERFORM   INIT.
004000     PERFORM   FL-OPEN.
004100     PERFORM   OUT-EDIT.
004200*                                                      
004300     PERFORM   UNTIL  END-FLG  NOT  =  ZERO
004400        PERFORM  OUT-WRITE
004500        PERFORM  OUT-EDIT
004600     END-PERFORM.
004700     PERFORM   FL-CLOSE.
004800     PERFORM   TERM.
004900     STOP RUN.
005000*              
005100 INIT                        SECTION.
005200     DISPLAY   "PROGRAM STARTING.".
005300     MOVE  -5000  TO  W-COUNTER.
005400     MOVE  0      TO  I-COUNTER.
005500     EXIT.
005600*                      
005700 FL-OPEN                     SECTION.
005800     OPEN   OUTPUT  OUT-FILE.
005900     EXIT.
006000*                  
006100 OUT-EDIT                    SECTION.
006200     ADD  1              TO  I-COUNTER.
006300     ADD  1              TO  W-COUNTER.
006400*                      
006500     MOVE I-COUNTER      TO  O-ID.
006600     MOVE I-COUNTER      TO  O-CD.
006700     MOVE I-COUNTER      TO  O-NIHONGO.
006800     MOVE W-COUNTER      TO  O-SEISU.
006900     COMPUTE  W-A  =  I-COUNTER  +  12000.
007000     DIVIDE  W-A  BY  2001  GIVING  W-C
007100                         REMAINDER  O-HIZUKE-YYYY.
007200     ADD     1000        TO  O-HIZUKE-YYYY.
007300     DIVIDE  W-A  BY  12    GIVING  W-C
007400                         REMAINDER  O-HIZUKE-MM.
007500     ADD     1           TO  O-HIZUKE-MM.
007600     DIVIDE  W-A  BY  28    GIVING  W-C
007700                         REMAINDER  O-HIZUKE-DD.
007800     ADD     1           TO  O-HIZUKE-DD.
007900     DIVIDE  W-A  BY  23    GIVING  W-C
008000                         REMAINDER  O-JIKOKU-HH.
008100     ADD     1           TO  O-JIKOKU-HH.
008200     DIVIDE  W-A  BY  59    GIVING  W-C
008300                         REMAINDER  O-JIKOKU-MM.
008400     ADD     1           TO  O-JIKOKU-MM.
008500     DIVIDE  W-A  BY  59    GIVING  W-C
008600                         REMAINDER  O-JIKOKU-SS.
008700     ADD     1           TO  O-JIKOKU-SS.
008800     COMPUTE  O-FUDOU  =  W-COUNTER  /  1000.
008900     IF  I-COUNTER  >  10000
009000*                                                                
009100         MOVE  1         TO  END-FLG
009200     END-IF.
009300     EXIT.
009400*                  
009500 OUT-WRITE                   SECTION.
009600     WRITE  O-RECORD.
009700     ADD  1                  TO  O-COUNTER.
009800     EXIT.
009900*                              
010000 FL-CLOSE                    SECTION.
010100     CLOSE  OUT-FILE.
010200     EXIT.
010300*              
010400 TERM                        SECTION.
010500     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
010600     DISPLAY   "OUTUT-COUNT:" I-COUNTER.
010700     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
010800     EXIT.
