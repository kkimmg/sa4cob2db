package k_kim_mg.sa4cob2db.WebSample;

import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.cobsub.JSampleJniCall1;

//import k_kim_mg.sa4cob2db.sample.ServletSampleTool1;
//import k_kim_mg.sa4cob2db.sample.jSampleJniCall1;

/**
 * 
 */

/**
 * @author kkimmg@gmail.com
 * 
 */
public class TestCobolCall {

	/** Emvironment Value */
	private static final String ENV_NAME = "ACM_CONF";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServletSampleTool1 servletSampleTool1 = new ServletSampleTool1(System.getenv(ENV_NAME));
		JSampleJniCall1 jniCaller = new JSampleJniCall1();
		String progname = "TESTSUB";
		CobolRecord head;
		CobolRecord bodyIn;
		CobolRecord bodyOut;
		head = servletSampleTool1.createRecord("subtest1");
		bodyIn = servletSampleTool1.createRecord("subtest1");
		bodyOut = servletSampleTool1.createRecord("subtest1");
		try {
			head.updateString(head.getMetaData().getColumn("xtype"), "head");
			bodyIn.updateString(bodyIn.getMetaData().getColumn("xtype"), "bodyin");
			bodyOut.updateString(bodyOut.getMetaData().getColumn("xtype"), "bodyout");
		} catch (CobolRecordException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			jniCaller.jniCallCobol_fork(progname, head, bodyIn, bodyOut);
			System.err.println(head.getString(head.getMetaData().getColumn("xtype")));
			System.err.println(bodyIn.getString(bodyIn.getMetaData().getColumn("xtype")));
			System.err.println(bodyOut.getString(bodyOut.getMetaData().getColumn("xtype")));
		} catch (CobolRecordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
