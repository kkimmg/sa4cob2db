package k_kim_mg.sa4cob2db.WebSample.RMI;
import java.io.File;
import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;

import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.CobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.DefaultCobolRecord;
import k_kim_mg.sa4cob2db.cobsub.JSampleJniCall1;
import k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaDataSet;
import k_kim_mg.sa4cob2db.sql.xml.NodeReadLoader;

public class CobSubServerTest {
	CobolRecordMetaDataSet metaset;
	CobolRecordMetaData metaHead, metaReqInp, metaReqOut;
	CobolRecord head;
	JSampleJniCall1 jni;
	public static void main (String[] args) {
		CobSubServerTest ins = new CobSubServerTest();
		/*
		try {
			ins.setUp();
			ins.testRead();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		try {
			ins.setUp();
			ins.testTCPRead();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * set up
	 * 
	 * @throws Exception exception
	 */
	//@Before
	public void setUp() throws Exception {
		metaset = new SQLCobolRecordMetaDataSet();
		NodeReadLoader nodeLoader = new NodeReadLoader();
		String filename = "/home/kenji/workspace/sa4cob2db/conf/metafile.xml";
		File metaFile = new File(filename);
		Properties properties = new Properties();
		try {
			nodeLoader.createMetaDataSet(metaFile, metaset, properties);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		metaHead = metaset.getMetaData("MSGHEAD");
		head = new DefaultCobolRecord(metaHead);
		metaReqInp = metaset.getMetaData("SCR_RECORD");
		metaReqOut = metaset.getMetaData("SCR_RECORD");
		//
		jni = new JSampleJniCall1();
		// head
		head.updateString(metaHead.getColumn("ACM-AUTHTYPE"), "value");
		head.updateString(metaHead.getColumn("ACM-CHARACTERENCODING"), "value");
		head.updateInt(metaHead.getColumn("ACM-CONTENTLENGTH"), 0);
		head.updateString(metaHead.getColumn("ACM-CONTENTTYPE"), "value");
		head.updateString(metaHead.getColumn("ACM-METHOD"), "value");
		head.updateString(metaHead.getColumn("ACM-PROTOCOL"), "value");
		head.updateString(metaHead.getColumn("ACM-QUERYSTRING"), "value");
		head.updateString(metaHead.getColumn("ACM-REMOTEADDR"), "value");
		head.updateString(metaHead.getColumn("ACM-REMOTEHOST"), "value");
		head.updateString(metaHead.getColumn("ACM-SCHEME"), "value");
		head.updateString(metaHead.getColumn("ACM-SERVERNAME"), "value");
		head.updateInt(metaHead.getColumn("ACM-SERVERPORT"), 0);
		head.updateInt(metaHead.getColumn("ACM-SECURE"), 0);
		head.updateString(metaHead.getColumn("ACM-REQUESTEDSESSIONID"), "value");
		head.updateInt(metaHead.getColumn("ACM-REQUESTEDSESSIONIDFROMCOOKIE"), 0);
		head.updateInt(metaHead.getColumn("ACM-REQUESTEDSESSIONIDFROMURL"), 0);
		head.updateInt(metaHead.getColumn("ACM-REQUESTEDSESSIONIDVALID"), 0);
		head.updateString(metaHead.getColumn("ACM-CONTEXTPATH"), "value");
		head.updateString(metaHead.getColumn("ACM-PATHINFO"), "value");
		head.updateString(metaHead.getColumn("ACM-PATHTRANSLATED"), "value");
		head.updateString(metaHead.getColumn("ACM-REQUESTURI"), "value");
		head.updateString(metaHead.getColumn("ACM-REQUESTURL"), "value");
		head.updateString(metaHead.getColumn("ACM-SERVLETPATH"), "value");
		head.updateString(metaHead.getColumn("ACM-REMOTEUSER"), "value");
		head.updateInt(metaHead.getColumn("ACM-USERINROLE"), 0);
		head.updateString(metaHead.getColumn("ACM-CURRENTCONTENT"), "value");
		head.updateString(metaHead.getColumn("ACM-NEXTCONTENT"), "value");
		head.updateString(metaHead.getColumn("ACM-SESSIONGLOBALAREA"), "value");
	}
	//@Test
	public void testRead() {
		CobolRecord reqInp, reqOut;
		reqInp = new DefaultCobolRecord(metaReqInp);
		reqOut = new DefaultCobolRecord(metaReqOut);
		// input
		try {
			reqInp.updateInt(metaReqInp.getColumn("SCR_PROC"), 2);
			reqInp.updateInt(metaReqInp.getColumn("SCR_ID"), 500);
			reqInp.updateString(metaReqInp.getColumn("SCR_CD"), "value");
			reqInp.updateString(metaReqInp.getColumn("SCR_NIHONGO"), "value");
			reqInp.updateInt(metaReqInp.getColumn("SCR_SEISU"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_SEISU_FLG"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_HIZUKE_YYYY"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_HIZUKE_MM"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_HIZUKE_DD"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_JIKOKU_HH"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_JIKOKU_MM"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_JIKOKU_SS"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_FUDOU1"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_FUDOU2"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_FUDOU_FLG"), 0);
		} catch (CobolRecordException e) {
			e.printStackTrace();
		}
		byte[] header = new byte[metaHead.getRowSize()];
		byte[] req = new byte[metaReqInp.getRowSize()];
		byte[] res = new byte[metaReqOut.getRowSize()];
		jni.jniCallCobol_fork("DYNTEST2", header, req, res);
	}
	//@Test
	public void testTCPRead() {
		CobolRecord reqInp, reqOut;
		reqInp = new DefaultCobolRecord(metaReqInp);
		reqOut = new DefaultCobolRecord(metaReqOut);
		// input
		try {
			reqInp.updateInt(metaReqInp.getColumn("SCR_PROC"), 2);
			reqInp.updateInt(metaReqInp.getColumn("SCR_ID"), 500);
			reqInp.updateString(metaReqInp.getColumn("SCR_CD"), "value");
			reqInp.updateString(metaReqInp.getColumn("SCR_NIHONGO"), "value");
			reqInp.updateInt(metaReqInp.getColumn("SCR_SEISU"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_SEISU_FLG"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_HIZUKE_YYYY"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_HIZUKE_MM"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_HIZUKE_DD"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_JIKOKU_HH"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_JIKOKU_MM"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_JIKOKU_SS"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_FUDOU1"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_FUDOU2"), 0);
			reqInp.updateInt(metaReqInp.getColumn("SCR_FUDOU_FLG"), 0);
		} catch (CobolRecordException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
		try {
			byte[] header = new byte[metaHead.getRowSize()];
			head.getRecord(header);
			byte[] req = new byte[metaReqInp.getRowSize()];
			reqInp.getRecord(req);
			byte[] res = new byte[metaReqOut.getRowSize()];
			jni.jniCallCobol_fork("TCPDYNTEST2", header, req, res);
		} catch (CobolRecordException e) {
			e.printStackTrace();
			//fail(e.getMessage());
		}
		//
		
	}
}
