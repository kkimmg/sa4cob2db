000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000200*                      
000300*                    (SEQ)                        (SEQ)                  
000400 IDENTIFICATION              DIVISION.
000500 PROGRAM-ID.                 LENTEST.
000600*AUTHOR.                     KENJI KIMURA.
000700 ENVIRONMENT                 DIVISION.
000800 CONFIGURATION               SECTION.
000900 INPUT-OUTPUT                SECTION.
001000 FILE-CONTROL.
001100*                        
001200*ACMFILE
001300*ACMRECNAME=I-RECORD
001400     SELECT INP-FILE ASSIGN TO "dbtests"
001500       ORGANIZATION LINE SEQUENTIAL.
001600 DATA                        DIVISION.
001700 FILE                        SECTION.
001800*                    
001900 FD  INP-FILE.
002000 COPY "I_RECORD.cbl".
002100 WORKING-STORAGE             SECTION.
002200 01  SOME-AREA.
002300     05  I-COUNTER           PIC 9(05).
002400     05  O-COUNTER           PIC 9(05).
002500     05  END-FLG             PIC 9(01)  VALUE  ZERO.
002600 01  OPTION-AREA.
002700     05  OPTIONVALUE         PIC X(10).
002800 PROCEDURE                   DIVISION.
002900*              
003000 MAIN                        SECTION.
003050*ACMSETLENGTH 30.
003100     PERFORM   INIT.
003200     PERFORM   FL-OPEN.
003300     PERFORM  INP-READ.
003400*                                                      
003500     PERFORM   UNTIL  END-FLG  NOT  =  ZERO
003600*                 OR  I-COUNTER     > 10000
003700        PERFORM  OUT-WRITE
003800        PERFORM  INP-READ
003900     END-PERFORM.
004000     PERFORM   FL-CLOSE.
004100     PERFORM   TERM.
004200     MOVE "TEST"   TO   OPTIONVALUE.
004300     DISPLAY OPTIONVALUE.
004400*ACMSETOPTION NAME="TEST" FROM=OPTIONVALUE.
004500     MOVE SPACE    TO   OPTIONVALUE.
004600*ACMGETOPTION NAME="TEST" TO=OPTIONVALUE.
004700     DISPLAY OPTIONVALUE.
004800*ACMSETOPTION NAME="TEST" VALUE="TSET".
004900     DISPLAY OPTIONVALUE.
005000*ACMGETOPTION NAME="TEST" TO=OPTIONVALUE.
005100     DISPLAY OPTIONVALUE.
005200     STOP RUN.
005300*              
005400 INIT                        SECTION.
005500     DISPLAY   "PROGRAM STARTING.".
005600     EXIT.
005700*                      
005800 FL-OPEN                     SECTION.
005900     OPEN   INPUT  INP-FILE.
006000     EXIT.
006100*                  
006200 INP-READ                    SECTION.
006300     READ INP-FILE NEXT
006400       AT END  MOVE 1 TO END-FLG
006500     END-READ.
006600     IF  END-FLG  =  ZERO
006700*                                                                
006800         ADD  1              TO  I-COUNTER
006900     END-IF.
007000     EXIT.
007100*                  
007200 OUT-WRITE                   SECTION.
007300     DISPLAY  I-RECORD.
007400     ADD  1                  TO  O-COUNTER.
007500     EXIT.
007600*                              
007700 FL-CLOSE                    SECTION.
007800     CLOSE  INP-FILE.
007900     EXIT.
008000*              
008100 TERM                        SECTION.
008200     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
008300     DISPLAY   "INPUT-COUNT:" I-COUNTER.
008400     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
008500     EXIT.