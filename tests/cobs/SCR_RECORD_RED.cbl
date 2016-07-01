000100*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCC
000200*
000300 01 SCR1-RECORD.
000400   05 SCR1-PROC                PIC  9(01).
000500   05 FILLER                  PIC  X(01)  VALUE  "|".
000600   05 SCR1-DATAREC.
000700   10 SCR1-ID                  PIC  9(05).
000800   10 FILLER                  PIC  X(01)  VALUE  "|".
000900   10 SCR1-CD                  PIC  X(20).
001000   10 FILLER                  PIC  X(01)  VALUE  "|".
001100   10 SCR1-NIHONGO             PIC  X(20).
001200   10 FILLER                  PIC  X(01)  VALUE  "|".
001300   10 SCR1-SEISU               PIC  9(07).
001400   10 FILLER                  PIC  X(01)  VALUE  "|".
001500   10 SCR1-SEISU-FLG           PIC  9(01).
001600   10 FILLER                  PIC  X(01)  VALUE  "|".
001700   10 SCR1-HIZUKE.
001800     20 SCR1-HIZUKE-YYYY       PIC  9(04).
001900     20 FILLER                PIC  X(01)  VALUE  "/".
002000     20 SCR1-HIZUKE-MM         PIC  9(02).
002100     20 FILLER                PIC  X(01)  VALUE  "/".
002200     20 SCR1-HIZUKE-DD         PIC  9(02).
002300   10 FILLER                  PIC  X(01)  VALUE  "|".
002400   10 SCR1-JIKOKU.
002500     20 SCR1-JIKOKU-HH         PIC  9(02).
002600     20 FILLER                PIC  X(01)  VALUE  ":".
002700     20 SCR1-JIKOKU-MM         PIC  9(02).
002800     20 FILLER                PIC  X(01)  VALUE  ":".
002900     20 SCR1-JIKOKU-SS         PIC  9(02).
003000   10 FILLER                  PIC  X(01)  VALUE  "|".
003100   10 SCR1-FUDOU1              PIC  9(4).
003200   10 FILLER                  PIC  X(01)  VALUE  ".".
003300   10 SCR1-FUDOU2              PIC  9(3).
003400   10 FILLER                  PIC  X(01)  VALUE  "|".
003500   10 SCR1-FUDOU-FLG           PIC  9(1).
000300 01 SCR2-RECORD.
000400   05 SCR2-PROC                PIC  9(01).
000500   05 FILLER                  PIC  X(01)  VALUE  "|".
000600   05 SCR2-DATAREC.
000700   10 SCR2-ID                  PIC  9(05).
000800   10 FILLER                  PIC  X(01)  VALUE  "|".
000900   10 SCR2-CD                  PIC  X(20).
001000   10 FILLER                  PIC  X(01)  VALUE  "|".
001100   10 SCR2-NIHONGO             PIC  X(20).
001200   10 FILLER                  PIC  X(01)  VALUE  "|".
001300   10 SCR2-SEISU               PIC  9(07).
001400   10 FILLER                  PIC  X(01)  VALUE  "|".
001500   10 SCR2-SEISU-FLG           PIC  9(01).
001600   10 FILLER                  PIC  X(01)  VALUE  "|".
001700   10 SCR2-HIZUKE.
001800     20 SCR2-HIZUKE-YYYY       PIC  9(04).
001900     20 FILLER                PIC  X(01)  VALUE  "/".
002000     20 SCR2-HIZUKE-MM         PIC  9(02).
002100     20 FILLER                PIC  X(01)  VALUE  "/".
002200     20 SCR2-HIZUKE-DD         PIC  9(02).
002300   10 FILLER                  PIC  X(01)  VALUE  "|".
002400   10 SCR2-JIKOKU.
002500     20 SCR2-JIKOKU-HH         PIC  9(02).
002600     20 FILLER                PIC  X(01)  VALUE  ":".
002700     20 SCR2-JIKOKU-MM         PIC  9(02).
002800     20 FILLER                PIC  X(01)  VALUE  ":".
002900     20 SCR2-JIKOKU-SS         PIC  9(02).
003000   10 FILLER                  PIC  X(01)  VALUE  "|".
003100   10 SCR2-FUDOU1              PIC  9(4).
003200   10 FILLER                  PIC  X(01)  VALUE  ".".
003300   10 SCR2-FUDOU2              PIC  9(3).
003400   10 FILLER                  PIC  X(01)  VALUE  "|".
003500   10 SCR2-FUDOU-FLG           PIC  9(1).
