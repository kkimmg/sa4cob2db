000010*AAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBCCCCCCCC
000020*サンプルプログラム
000030*入力ファイル(SEQ)→出力ファイル(SEQ)の転記処理
000040 IDENTIFICATION              DIVISION.
000050 PROGRAM-ID.                 SEQTEST.
000060*AUTHOR.                     KENJI KIMURA.
000070 ENVIRONMENT                 DIVISION.
000080 CONFIGURATION               SECTION.
000090* INPUT-OUTPUT                SECTION.
000100* FILE-CONTROL.
000110*    入力ファイル
000120**ACMFILE
000130*     SELECT INP-FILE ASSIGN TO "dbtests2"
000140*       ORGANIZATION LINE SEQUENTIAL.
000150 DATA                        DIVISION.
000160* FILE                        SECTION.
000170*入力ファイル
000180* FD  INP-FILE.
000190* COPY "I_RECORD2.cbl".
000200 WORKING-STORAGE             SECTION.
000210*ACM Generated Contraints
000220 COPY "ACMCONSTS.CBL".
000230*ACM Genrated File Record
000240 COPY "I_RECORD2.cbl".
000250 01  SOME-AREA.
000260     05  I-COUNTER           PIC 9(05).
000270     05  O-COUNTER           PIC 9(05).
000280     05  END-FLG             PIC 9(01)  VALUE  ZERO.
000290 PROCEDURE                   DIVISION.
000300*主処理節
000310     CALL "initializeJNISessionEnv" USING ACM-STATUS-ALL.
000320     PERFORM   INIT.
000330     PERFORM   FL-OPEN.
000340     PERFORM  INP-READ.
000350*    入力ファイルが終了するまで繰り返し
000360     PERFORM   UNTIL  END-FLG  NOT  =  ZERO
000370*                 OR  I-COUNTER     > 10000
000380        PERFORM  OUT-WRITE
000390        PERFORM  INP-READ
000400     END-PERFORM.
000410     PERFORM   FL-CLOSE.
000420     PERFORM   TERM.
000430     CALL  "terminateJNISession" USING ACM-STATUS-ALL.
000440     STOP RUN.
000450*開始処理
000460 INIT                        SECTION.
000470     DISPLAY   "PROGRAM STARTING.".
000480     EXIT.
000490*ファイルを開く節
000500 FL-OPEN                     SECTION.
000510*     OPEN   INPUT  INP-FILE.
000520     MOVE "dbtests2" TO ACM-FILE-IDENT.
000530     CALL "assignJNIFile" USING ACM-FILE-IDENT ACM-STATUS-ALL.
000540     CALL "openJNIFile"   USING ACM-FILE-IDENT
000550                                ACM-OPENMODE-INPUT
000560                                ACM-ACCESSMODE-SEQ
000570                                ACM-STATUS-ALL.
000580     EXIT.
000590*入力処理節
000600 INP-READ                    SECTION.
000610*     READ INP-FILE NEXT
000620*       AT END  MOVE 1 TO END-FLG
000630*     END-READ.
000640     MOVE "dbtests2" TO ACM-FILE-IDENT.
000650     CALL "readNextJNIRecord" USING 
000660                                ACM-FILE-IDENT
000670                                ACM-RECORD
000680                                ACM-STATUS-ALL.
000690     IF  ACM-STATUS-CODE = "00"
000700         MOVE ACM-RECORD TO I-RECORD
000710     END-IF.
000720     IF ACM-STATUS-CODE = "10"
000730         MOVE 1 TO END-FLG
000740     END-IF.
000750     IF  END-FLG  =  ZERO
000760*        終端に達していなければカウンターを増分
000770         ADD  1              TO  I-COUNTER
000780     END-IF.
000790     EXIT.
000800*出力処理節
000810 OUT-WRITE                   SECTION.
000820     DISPLAY  I-RECORD.
000830     ADD  1                  TO  O-COUNTER.
000840     EXIT.
000850*ファイルを閉じる節
000860 FL-CLOSE                    SECTION.
000870*     CLOSE  INP-FILE.
000880     MOVE "dbtests2" TO ACM-FILE-IDENT.
000890     CALL "closeJNIFile" USING ACM-FILE-IDENT ACM-STATUS-ALL.
000900     EXIT.
000910*終了処理
000920 TERM                        SECTION.
000930     DISPLAY   "PROGRAM NORMALLY TERMINATED.".
000940     DISPLAY   "INPUT-COUNT:" I-COUNTER.
000950     DISPLAY   "OUTPUT-COUNT:" O-COUNTER.
000960     EXIT.
