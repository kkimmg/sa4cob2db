package k_kim_mg.sa4cob2db;
import java.io.Serializable;
import k_kim_mg.sa4cob2db.event.ACMSessionEventListener;
/** session */
public interface ACMSession extends Serializable {
	/**
	 * add event listener
	 * 
	 * @param listener event listener
	 */
	public void addACMSessionEventListener(ACMSessionEventListener listener);
	/**
	 * create file
	 * 
	 * @param name filename
	 * @return file object 
	 */
	public CobolFile createFile(String name);
	/**
	 * release file
	 * 
	 * @param name filename
	 */
	public void destroyFile(String name);
	/**
	 * get option value
	 * 
	 * @param key key
	 * @return option value
	 */
	public String getACMOption(String key);
	/**
	 * get file from name
	 * 
	 * @param name filename
	 * @return file object 
	 */
	public CobolFile getFile(String name);
	/**
	 * get max record length
	 * 
	 * @return length
	 */
	public int getMaxLength();
	/**
	 * sessionID
	 * 
	 * @return sessionID
	 */
	public String getSessionId();
	/**
	 * delete event listener
	 * 
	 * @param listener event listener
	 */
	public void removeACMSessionEventListener(ACMSessionEventListener listener);
	/**
	 * set option value
	 * 
	 * @param key key
	 * @param value value
	 */
	public void setACMOption(String key, String value);
	/**
	 * set max record length
	 * 
	 * @param length length
	 */
	public void setMaxLength(int length);
}