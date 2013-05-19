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
001400     SELECT OUT-FILE ASSIGN TO "dbtests"
001500       ORGANIZATION LINE SEQUENTIAL.
001600 DATA                        DIVISION.
001700 FILE                        SECTION.
001800*                    
001900 FD  OUT-FILE.
002000     COPY "O_RECORD.cbl".
002100 WORKING-STORAGE             SECTION.
002200 01  SOME-AREA.
002300     05  I-COUNTER           PIC 9(05).
002400     05  O-COUNTER           PIC 9(05).
002500     05  END-FLG             PIC 9(01)  VALUE  ZERO.
002600 01  WORK-AREA.
002700     05  W-A                 PIC 9(09).
002800     05  W-B                 PIC 9(09).
002900     05  W-C                 PIC 9(09).
003000     05  W-D                 PIC 9(09).
003100     05  W-EF.
003200       07  W-E               PIC 9(04).
003300       07  FILLER            PIC X(01)  VALUE  ".".
003400       07  W-F               PIC 9(03).
003500 PROCEDURE                   DIVISION.
003600*              
003700 MAIN                        SECTION.
003800     PERFORM   INIT.
003900     PERFORM   FL-OPEN.
004000     PERFORM   OUT-EDIT.
004100*                                                      
004200     PERFORM   UNTIL  END-FLG  NOT  =  ZERO
004300        PERFORM  OUT-WRITE
004400        PERFORM  OUT-EDIT
004500     END-PERFORM.
004600     PERFORM   FL-CLOSE.
004700     PERFORM   TERM.
004800     STOP RUN.
004900*              
005000 INIT                        SECTION.
005100     DISPLAY   "PROGRAM STARTING.".
005200     EXIT.
005300*                      
005400 FL-OPEN                     SECTION.
005500     OPEN   OUTPUT  OUT-FILE.
005600     EXIT.
005700*                  
005800 OUT-EDIT                    SECTION.
005900     ADD  1              TO  I-COUNTER.
006000*                      
006100     MOVE I-COUNTER      TO  O-ID.
006200     MOVE I-COUNTER      TO  O-CD.
006300     MOVE I-COUNTER      TO  O-NIHONGO.
006400     MOVE I-COUNTER      TO  O-SEISU.
006500     COMPUTE  W-A  =  I-COUNTER  +  12000.
006600     DIVIDE  W-A  BY  2001  GIVING  W-C
006700                         REMAINDER  O-HIZUKE-YYYY.
006800     ADD     1000        TO  O-HIZUKE-YYYY.
006900     DIVIDE  W-A  BY  12    GIVING  W-C
007000                         REMAINDER  O-HIZUKE-MM.
007100     ADD     1           TO  O-HIZUKE-MM.
007200     DIVIDE  W-A  BY  28    GIVING  W-C
007300                         REMAINDER  O-HIZUKE-DD.
007400     ADD     1           TO  O-HIZUKE-DD.
007500     DIVIDE  W-A  BY  23    GIVING  W-C
007600                         REMAINDER  O-JIKOKU-HH.
007700     ADD     1           TO  O-JIKOKU-HH.
007800     DIVIDE  W-A  BY  59    GIVING  W-C
007900                         REMAINDER  O-JIKOKU-MM.
008000     ADD     1           TO  O-JIKOKU-MM.
008100     DIVIDE  W-A  BY  59    GIVING  W-C
008200                         REMAINDER  O-JIKOKU-SS.
008300     ADD     1           TO  O-JIKOKU-SS.
008400     DIVIDE  W-A  BY  9999  GIVING  W-C
008500                         REMAINDER  W-E.
008600     DIVIDE  W-A  BY  99    GIVING  W-C
008700                         REMAINDER  W-F.
008800     MOVE    W-EF        TO  O-FUDOU.
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
