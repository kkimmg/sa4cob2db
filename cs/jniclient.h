/***************************************/
#include    "acmclient.h"
/***************************************/
/** ���ơ������򥻥å���󤫤�������� */
extern void getStatus(char *status);
/** �쥳���ɤ򥻥å���󤫤�������� */
extern void getRecord(char *record);
/**���������*/
extern void libJNIClient ();/**
 * �����åȤν����
 * �����С�¦�ؤΥ��ͥ�������Ω�ޤ�
 */
extern int initializeJNI ();

/**���������*/
extern void initializeJNISession (char *username, char *password, char *status);

/**���������*/
extern void initializeJNISessionEnv (char *status);

/**��λ����*/
extern void terminateJNISession (char *status);

/**�ե�����Υ�������
* @param	name	�ե�����μ��̻�
* @param	status	���ơ�����
*/
extern void assignJNIFile (char *name, char *status);

/**
* �ե�����Υ����ץ�
* @param name	�ե�����̾
* @param openmode	�����ץ�⡼��<br/>
*			INPUT|OUTPUT|EXTEND|IO
* @param accessmode	���������⡼��
*			�������󥷥��|�����ʥߥå�|������
* @param status	���ơ�����
*/
extern void openJNIFile (char *name, char *openmode, char *accessmode, char *status);

/**
 * �ե�������Ĥ���
 * @param name	�ե����뼱�̻�
 * @param status	���ơ�����
 */
extern void closeJNIFile (char *name, char *status);

/**
 * ���Υ쥳���ɤ�
 * @param name	�ե����뼱�̻�
 * @param status	���ơ�����
 */
extern void nextJNIRecord (char *name, char *status);

/**
 * �꡼�ɽ���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void readJNIRecord (char *name, char *record, char *status);

/**
* Read Next ����
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void readNextJNIRecord (char *name, char *record, char *status);

/**
 * �ɤ߹��߽���
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void moveReadJNIRecord (char *name, char *record, char *status);

/**
* ����ǥå�������ꤷ�����ַ������
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void moveReadJNIRecordWith (char *name, char *record, char *indexname, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void writeJNIRecord (char *name, char *record, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void rewriteJNIRecord (char *name, char *record, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void deleteJNIRecord (char *name, char *record, char *status);

/**
 * ���ַ������
 * @param name	�ե����뼱�̻�
 * @param record	�쥳����
 * @param status	���ơ�����
 */
extern void moveJNIRecord (char *name, char *record, char *status);

/**
* ����ǥå�������ꤷ�����ַ������
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void moveJNIRecordWith (char *name, char *record, char *indexname, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void startJNIRecord (char *name, char *record, char *startmode, char *status);

/**
* �񤭹��߽���
* @param name	�ե����뼱�̻�
* @param record	�쥳����
* @param status	���ơ�����
*/
extern void startJNIRecordWith (char *name, char *record, char *indexname, char *startmode, char *status);

/**
* ���ߥå�
* @param status ���ơ�����
*/
extern void commitJNISession (char *status);

/**
* ����Хå�
* @param status ���ơ�����
*/
extern void rollbackJNISession (char *status);

/**
* ���ߥåȥ⡼�ɤ����ꤹ��
* @param commitmode ���ߥåȥ⡼��
* @param status ���ơ�����
*/
extern void setJNICommitMode (char *commitmode, char *status);

/**
* �ȥ�󥶥�������٥�����ꤹ��
* @param transmode �ȥ�󥶥�������٥�
* @param status ���ơ�����
*/
extern void setJNITransMode (char *transmode, char *status);

extern void getOptionValue(char *record);

extern void setJNISetOption (char *name, char *value);

extern void setJNIGetOption (char *name, char *value);
