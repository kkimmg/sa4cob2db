000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCC
000200*テストレコード
000300 01 SCR-RECORD.
000400   05 SCR-PROC                PIC  9(01).
000500   05 FILLER                  PIC  X(01)  VALUE  "|".
000600   05 SCR-DATAREC.
000700   10 SCR-ID                  PIC  9(05).
000800   10 FILLER                  PIC  X(01)  VALUE  "|".
000900   10 SCR-CD                  PIC  X(20).
001000   10 FILLER                  PIC  X(01)  VALUE  "|".
001100   10 SCR-NIHONGO             PIC  X(20).
001200   10 FILLER                  PIC  X(01)  VALUE  "|".
001300   10 SCR-SEISU               PIC  9(07).
001400   10 FILLER                  PIC  X(01)  VALUE  "|".
001500   10 SCR-SEISU-FLG           PIC  9(01).
001600   10 FILLER                  PIC  X(01)  VALUE  "|".
001700   10 SCR-HIZUKE.
001800     20 SCR-HIZUKE-YYYY       PIC  9(04).
001900     20 FILLER                PIC  X(01)  VALUE  "/".
002000     20 SCR-HIZUKE-MM         PIC  9(02).
002100     20 FILLER                PIC  X(01)  VALUE  "/".
002200     20 SCR-HIZUKE-DD         PIC  9(02).
002300   10 FILLER                  PIC  X(01)  VALUE  "|".
002400   10 SCR-JIKOKU.
002500     20 SCR-JIKOKU-HH         PIC  9(02).
002600     20 FILLER                PIC  X(01)  VALUE  ":".
002700     20 SCR-JIKOKU-MM         PIC  9(02).
002800     20 FILLER                PIC  X(01)  VALUE  ":".
002900     20 SCR-JIKOKU-SS         PIC  9(02).
003000   10 FILLER                  PIC  X(01)  VALUE  "|".
003100   10 SCR-FUDOU1              PIC  9(4).
003200   10 FILLER                  PIC  X(01)  VALUE  ".".
003300   10 SCR-FUDOU2              PIC  9(3).
003400   10 FILLER                  PIC  X(01)  VALUE  "|".
003500   10 SCR-FUDOU-FLG           PIC  9(1).
