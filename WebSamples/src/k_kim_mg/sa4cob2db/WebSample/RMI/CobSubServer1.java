package k_kim_mg.sa4cob2db.WebSample.RMI;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Serv Call COBOL SubProgram
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CobSubServer1 extends Remote {
	/**
	 * Return Value.
	 * 
	 * @author kenji
	 * 
	 */
	public static class CobSubRet implements Serializable {
		private static final long serialVersionUID = 1L;
		private boolean success;
		private byte[] head;
		private byte[] request;
		private byte[] response;

		/**
		 * get head.
		 * @return the head
		 */
		public byte[] getHead() {
			return head;
		}

		/**
		 * get request.
		 * @return the request
		 */
		public byte[] getRequest() {
			return request;
		}

		/**
		 * get response.
		 * 
		 * @return the response
		 */
		public byte[] getResponse() {
			return response;
		}

		/**
		 * Success?.
		 * 
		 * @return the success
		 */
		public boolean isSuccess() {
			return success;
		}

		/**
		 * @param head the head to set
		 */
		public void setHead(byte[] head) {
			this.head = head;
		}

		/**
		 * @param request the request to set.
		 */
		public void setRequest(byte[] request) {
			this.request = request;
		}

		/**
		 * set response.
		 * 
		 * @param response the response to set
		 */
		public void setResponse(byte[] response) {
			this.response = response;
		}

		/**
		 * Success?.
		 * 
		 * @param success the success to set
		 */
		public void setSuccess(boolean success) {
			this.success = success;
		}
	}

	/**
	 * Calling Subprogram.
	 * 
	 * @param progname program name
	 * @param header header
	 * @param req request(in parameter)
	 * @param res response(out parameter)
	 * @return CobSubRet
	 * @throws RemoteException exception
	 */
	public CobSubRet callCobSub(String progname, byte[] header, byte[] req, byte[] res) throws RemoteException;
}
