000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCC
000200*                    
000300 01 DSP-RECORD1.
000400   10 VALUE "PROC"                        LINE  2  COL  2.
000500   10 VALUE "["                           LINE  2  COL 12.
000600   10 PIC  9(01)     TO  SCR-PROC         LINE  2  COL 13.
000700   10 VALUE "]"                           LINE  2  COL 14.
000800   10 VALUE "ID"                          LINE  4  COL  2.
000900   10 VALUE "["                           LINE  4  COL 12.
001000   10 PIC  9(05)     TO  SCR-ID           LINE  4  COL 13.
001100   10 VALUE "]"                           LINE  4  COL 33.
001200   10 VALUE "CD"                          LINE  6  COL  2.
001300   10 VALUE "["                           LINE  6  COL 12.
001400   10 PIC  X(20)     TO  SCR-CD           LINE  6  COL 13.
001500   10 VALUE "]"                           LINE  6  COL 33.
001600   10 VALUE "JAPANESE"                    LINE  8  COL  2.
001700   10 VALUE "["                           LINE  8  COL 12.
001800   10 PIC  X(20)     TO  SCR-NIHONGO      LINE  8  COL 13.
001900   10 VALUE "]"                           LINE  8  COL 33.
002000   10 VALUE "INTEGER"                     LINE 10  COL  2.
002100   10 VALUE "["                           LINE 10  COL 12.
002200   10 PIC  S9(07)    TO  SCR-SEISU        LINE 10  COL 13.
002300   10 VALUE "]"                           LINE 10  COL 20.
002400   10 VALUE "["                           LINE 10  COL 21.
002500   10 PIC  9(01)     TO  SCR-SEISU-FLG    LINE 10  COL 22.
002600   10 VALUE "]"                           LINE 10  COL 23.
002700   10 VALUE "DATE"                        LINE 12  COL  2.
002800   10 VALUE "["                           LINE 12  COL 12.
002900   10 PIC  9(04)     TO  SCR-HIZUKE-YYYY  LINE 12  COL 13.
003000   10 VALUE "/"                           LINE 12  COL 17.
003100   10 PIC  9(02)     TO  SCR-HIZUKE-MM    LINE 12  COL 18.
003200   10 VALUE "/"                           LINE 12  COL 20.
003300   10 PIC  9(02)     TO  SCR-HIZUKE-DD    LINE 12  COL 21.
003400   10 VALUE "]"                           LINE 12  COL 23.
003500   10 VALUE "TIME"                        LINE 14  COL  2.
003600   10 VALUE "["                           LINE 14  COL 12.
003700   10 PIC  9(02)     TO  SCR-JIKOKU-HH    LINE 14  COL 13.
003800   10 VALUE ":"                           LINE 14  COL 15.
003900   10 PIC  9(02)     TO  SCR-JIKOKU-MM    LINE 14  COL 16.
004000   10 VALUE ":"                           LINE 14  COL 18.
004100   10 PIC  9(02)     TO  SCR-JIKOKU-SS    LINE 14  COL 19.
004200   10 VALUE "]"                           LINE 14  COL 21.
004300   10 VALUE "FLOAT"                       LINE 16  COL  2.
004400   10 VALUE "["                           LINE 16  COL 12.
004500   10 PIC  9(4)      TO  SCR-FUDOU1       LINE 16  COL 13.
004600   10 VALUE "."                           LINE 16  COL 17.
004700   10 PIC  9(3)      TO  SCR-FUDOU2       LINE 16  COL 18.
004800   10 VALUE "]"                           LINE 16  COL 21.
004900   10 VALUE "["                           LINE 16  COL 22.
005000   10 PIC  9(1)      TO  SCR-FUDOU-FLG    LINE 16  COL 23.
005100   10 VALUE "]"                           LINE 16  COL 24.
005200 01 DSP-RECORD2.
005300   10 VALUE "PROC"                        LINE  2  COL  2.
005400   10 VALUE "["                           LINE  2  COL 12.
005500   10 PIC  9(01)    FROM SCR-PROC         LINE  2  COL 13.
005600   10 VALUE "]"                           LINE  2  COL 14.
005700   10 VALUE "ID"                          LINE  4  COL  2.
005800   10 VALUE "["                           LINE  4  COL 12.
005900   10 PIC  9(05)    FROM SCR-ID           LINE  4  COL 13.
006000   10 VALUE "]"                           LINE  4  COL 33.
006100   10 VALUE "CD"                          LINE  6  COL  2.
006200   10 VALUE "["                           LINE  6  COL 12.
006300   10 PIC  X(20)    FROM SCR-CD           LINE  6  COL 13.
006400   10 VALUE "]"                           LINE  6  COL 33.
006500   10 VALUE "JAPANESE"                    LINE  8  COL  2.
006600   10 VALUE "["                           LINE  8  COL 12.
006700   10 PIC  X(20)    FROM SCR-NIHONGO      LINE  8  COL 13.
006800   10 VALUE "]"                           LINE  8  COL 33.
006900   10 VALUE "INTEGER"                     LINE 10  COL  2.
007000   10 VALUE "["                           LINE 10  COL 12.
007100   10 PIC  S9(07)   FROM SCR-SEISU        LINE 10  COL 13.
007200   10 VALUE "]"                           LINE 10  COL 20.
007300   10 VALUE "["                           LINE 10  COL 21.
007400   10 PIC  9(01)    FROM SCR-SEISU-FLG    LINE 10  COL 22.
007500   10 VALUE "]"                           LINE 10  COL 23.
007600   10 VALUE "DATE"                        LINE 12  COL  2.
007700   10 VALUE "["                           LINE 12  COL 12.
007800   10 PIC  9(04)    FROM SCR-HIZUKE-YYYY  LINE 12  COL 13.
007900   10 VALUE "/"                           LINE 12  COL 17.
008000   10 PIC  9(02)    FROM SCR-HIZUKE-MM    LINE 12  COL 18.
008100   10 VALUE "/"                           LINE 12  COL 20.
008200   10 PIC  9(02)    FROM SCR-HIZUKE-DD    LINE 12  COL 21.
008300   10 VALUE "]"                           LINE 12  COL 23.
008400   10 VALUE "TIME"                        LINE 14  COL  2.
008500   10 VALUE "["                           LINE 14  COL 12.
008600   10 PIC  9(02)    FROM SCR-JIKOKU-HH    LINE 14  COL 13.
008700   10 VALUE ":"                           LINE 14  COL 15.
008800   10 PIC  9(02)    FROM SCR-JIKOKU-MM    LINE 14  COL 16.
008900   10 VALUE ":"                           LINE 14  COL 18.
009000   10 PIC  9(02)    FROM SCR-JIKOKU-SS    LINE 14  COL 19.
009100   10 VALUE "]"                           LINE 14  COL 21.
009200   10 VALUE "FLOAT"                       LINE 16  COL  2.
009300   10 VALUE "["                           LINE 16  COL 12.
009400   10 PIC  9(4)     FROM SCR-FUDOU1       LINE 16  COL 13.
009500   10 VALUE "."                           LINE 16  COL 17.
009600   10 PIC  9(3)     FROM SCR-FUDOU2       LINE 16  COL 18.
009700   10 VALUE "]"                           LINE 16  COL 21.
009800   10 VALUE "["                           LINE 16  COL 22.
009900   10 PIC  9(1)     FROM SCR-FUDOU-FLG    LINE 16  COL 23.
010000   10 VALUE "]"                           LINE 16  COL 24.
