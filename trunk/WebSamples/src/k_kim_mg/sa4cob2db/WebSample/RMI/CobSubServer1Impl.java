package k_kim_mg.sa4cob2db.WebSample.RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import k_kim_mg.sa4cob2db.cobsub.JSampleJniCall1;

/**
 * Remote object to call cobol subprogram by jni.
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
@SuppressWarnings("serial")
public class CobSubServer1Impl extends UnicastRemoteObject implements CobSubServer1 {
	protected CobSubServer1Impl() throws RemoteException {
		super();
	}

	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.WebSample.RMI.CobSubServer1#callCobSub(java.lang.String, byte[], byte[], byte[])
	 */
	@Override
	public CobSubRet callCobSub(String progname, byte[] header, byte[] req, byte[] res) throws RemoteException {
		CobSubRet ret = new CobSubRet();
		
		int wrk = JSampleJniCall1.getSingletonInstance().jniCallCobol_fork(progname, header, req, res);
		if (wrk != 0) {
			ret.setSuccess(true);
			ret.setResponse(res);
		} else {
			ret.setSuccess(false);
			ret.setResponse(res);
		}
		return ret;
	}

}
