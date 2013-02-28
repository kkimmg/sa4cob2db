000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000200*����ץ�ץ����
000300*���ϥե�����(SEQ)�����ϥե�����(SEQ)��ž������
000400 IDENTIFICATION              DIVISION.
000500 PROGRAM-ID.                 TYPETEST.
000600*AUTHOR.                     KENJI KIMURA.
000700 ENVIRONMENT                 DIVISION.
000800 CONFIGURATION               SECTION.
000900 INPUT-OUTPUT                SECTION.
001000 FILE-CONTROL.
001100*    ���ϥե�����
001200*ACMFILE
001300*ACMRECNAME=DATA-TYPES
001400     SELECT OUT-FILE ASSIGN TO "TYPETEST"
001500       ORGANIZATION LINE SEQUENTIAL.
001600 DATA                        DIVISION.
001700 FILE                        SECTION.
001800*���ϥե�����
001900 FD  OUT-FILE.
002000     COPY "DATATYPES.cbl".
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
003600*�������
003700 MAIN                        SECTION.
003800     PERFORM   INIT.
003900     PERFORM   FL-OPEN.
004000     PERFORM   OUT-EDIT.
004300     PERFORM   OUT-DISPLAY.
004300     PERFORM   OUT-WRITE.
004600     PERFORM   FL-CLOSE.
123456*    *****************************************
003900*    PERFORM   FL-OPEN2.
004000*    PERFORM   OUT-READ.
004300*    PERFORM   OUT-DISPLAY.
004600*    PERFORM   FL-CLOSE.
123456*    *****************************************
004700     PERFORM   TERM.
004800     STOP RUN.
004900*���Ͻ���
005000 INIT                        SECTION.
005100     DISPLAY   "PROGRAM STARTING.".
005200     EXIT.
005300*�ե�����򳫤���
005400 FL-OPEN                     SECTION.
005500     OPEN   OUTPUT  OUT-FILE.
005600     EXIT.
005300*�ե�����򳫤���
005400 FL-OPEN2                    SECTION.
005500     OPEN   INPUT   OUT-FILE.
005600     EXIT.
005700*���Ͻ�����
005800 OUT-EDIT                    SECTION.
005900     ADD  1              TO  I-COUNTER.
006000*    �Խ�������
123456     MOVE  "ABCD"            TO  TYPE-A.
123456     MOVE  "ABCDEFGHI"       TO  TYPE-X.
123456     MOVE  12345678901235678 TO  TYPE-9.
123456*    MOVE  "������"          TO  TYPE-N.
123456     MOVE  5678              TO  TYPE-SP.
123456     MOVE  -5678             TO  TYPE-SM.
123456     MOVE  9.99              TO  TYPE-V.
123456     MOVE  99                TO  TYPE-P.
123456     MOVE  "AB"              TO  TYPE-0.
123456     MOVE  "ABC"             TO  TYPE-XB.
123456*    MOVE  "������"          TO  TYPE-NB.
123456     MOVE  5678              TO  TYPE-CONMA.
123456     MOVE  5678              TO  TYPE-SLASH.
123456     MOVE  9.99              TO  TYPE-PERIOD.
123456     MOVE  56                TO  TYPE-PLUS.
123456     MOVE  -5678             TO  TYPE-MINUS.
123456     MOVE  56                TO  TYPE-CR1.
123456     MOVE  56                TO  TYPE-DB1.
123456     MOVE  -56               TO  TYPE-CR2.
123456     MOVE  -56               TO  TYPE-DB2.
123456     MOVE  56                TO  TYPE-Z.
123456     MOVE  56                TO  TYPE-AST.
123456*    MOVE  56                TO  TYPE-BS.
123456     MOVE  5678              TO  TYPE-FOM1.
123456     MOVE  5678              TO  TYPE-FOM2.
123456     MOVE  56                TO  TYPE-FOM3.
123456     MOVE  -56               TO  TYPE-FOM4.
123456*    MOVE  567               TO  TYPE-FOM5.
123456*    MOVE  5678              TO  TYPE-FOM6.
123456*    MOVE  5678              TO  TYPE-FOM7.
123456     MOVE  56                TO  TYPE-FOM8.
123456     MOVE  5678              TO  TYPE-FOM9.
123456     MOVE  5678              TO  TYPE-DISP.
123456     MOVE  5678              TO  TYPE-PACKED-DECIMAL.
123456     MOVE  5678              TO  TYPE-COMP-3.
123456*    MOVE  5678              TO  TYPE-BIN1.
123456*    MOVE  5678              TO  TYPE-BIN4.
123456*    MOVE  5678              TO  TYPE-BIN5.
123456*    MOVE  5678              TO  TYPE-BIN9.
123456*    MOVE  5678              TO  TYPE-BIN10.
123456*    MOVE  5678              TO  TYPE-BIN18.
009300     EXIT.
005700*ɽ����
005800 OUT-DISPLAY                    SECTION.
654321*    ���ܤ��Ȥ�display
123456     DISPLAY  "TYPE-A              "  TYPE-A.
123456     DISPLAY  "TYPE-X              "  TYPE-X.
123456     DISPLAY  "TYPE-9              "  TYPE-9.
123456*    DISPLAY  "TYPE-N              "  TYPE-N.
123456     DISPLAY  "TYPE-SP             "  TYPE-SP.
123456     DISPLAY  "TYPE-SM             "  TYPE-SM.
123456     DISPLAY  "TYPE-V              "  TYPE-V.
123456     DISPLAY  "TYPE-P              "  TYPE-P.
123456     DISPLAY  "TYPE-0              "  TYPE-0.
123456     DISPLAY  "TYPE-XB             "  TYPE-XB.
123456*    DISPLAY  "TYPE-NB             "  TYPE-NB.
123456     DISPLAY  "TYPE-CONMA          "  TYPE-CONMA.
123456     DISPLAY  "TYPE-SLASH          "  TYPE-SLASH.
123456     DISPLAY  "TYPE-PERIOD         "  TYPE-PERIOD.
123456     DISPLAY  "TYPE-PLUS           "  TYPE-PLUS.
123456     DISPLAY  "TYPE-MINUS          "  TYPE-MINUS.
123456     DISPLAY  "TYPE-CR             "  TYPE-CR1.
123456     DISPLAY  "TYPE-CR             "  TYPE-CR2.
123456     DISPLAY  "TYPE-DB             "  TYPE-DB1.
123456     DISPLAY  "TYPE-DB             "  TYPE-DB2.
123456     DISPLAY  "TYPE-Z              "  TYPE-Z.
123456     DISPLAY  "TYPE-AST            "  TYPE-AST.
123456*    DISPLAY  "TYPE-BS             "  TYPE-BS.
123456     DISPLAY  "TYPE-FOM1           "  TYPE-FOM1.
123456     DISPLAY  "TYPE-FOM2           "  TYPE-FOM2.
123456     DISPLAY  "TYPE-FOM3           "  TYPE-FOM3.
123456     DISPLAY  "TYPE-FOM4           "  TYPE-FOM4.
123456*    DISPLAY  "TYPE-FOM5           "  TYPE-FOM5.
123456*    DISPLAY  "TYPE-FOM6           "  TYPE-FOM6.
123456*    DISPLAY  "TYPE-FOM7           "  TYPE-FOM7.
123456     DISPLAY  "TYPE-FOM8           "  TYPE-FOM8.
123456     DISPLAY  "TYPE-FOM9           "  TYPE-FOM9.
123456     DISPLAY  "TYPE-DISP           "  TYPE-DISP.
123456     DISPLAY  "TYPE-PACKED-DECIMAL "  TYPE-PACKED-DECIMAL.
123456     DISPLAY  "TYPE-COMP-3         "  TYPE-COMP-3.
123456*    DISPLAY  "TYPE-BIN1           "  TYPE-BIN1.
123456*    DISPLAY  "TYPE-BIN4           "  TYPE-BIN4.
123456*    DISPLAY  "TYPE-BIN5           "  TYPE-BIN5.
123456*    DISPLAY  "TYPE-BIN9           "  TYPE-BIN9.
123456*    DISPLAY  "TYPE-BIN10          "  TYPE-BIN10.
123456*    DISPLAY  "TYPE-BIN18          "  TYPE-BIN18.
654321*    �ޤȤ��display
123456     DISPLAY  "ALL                 "  DATA-TYPES.
009300     EXIT.
009400*���Ͻ�����
009500 OUT-WRITE                   SECTION.
009600     WRITE  DATA-TYPES.
009700     ADD  1                  TO  O-COUNTER.
009800     EXIT.
009900*�ե�������Ĥ�����
010000 FL-CLOSE                    SECTION.
010100     CLOSE  OUT-FILE.
010200     EXIT.
010300*��λ����
010400 TERM                        SECTION.
010500     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
010600     DISPLAY   "OUTUT-COUNT:" I-COUNTER.
010700     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
010800     EXIT.
