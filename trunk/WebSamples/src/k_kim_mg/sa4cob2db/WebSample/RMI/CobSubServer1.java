package k_kim_mg.sa4cob2db.WebSample.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;
/**
 * Serv Call COBOL SubProgram
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobSubServer1 extends Remote {
	/**
	 * Calling Subprogram
	 * @param progname	program name
	 * @param header	header
	 * @param req		request(in parameter)
	 * @param res		response(out parameter)
	 * @return 0=OK/-1=NG
	 * @throws RemoteException	exception
	 */
	public int callCobSub(String progname, byte[] header, byte[] req, byte[] res) throws RemoteException;
}
