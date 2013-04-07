package k_kim_mg.sa4cob2db.event;
import java.util.EventObject;
import k_kim_mg.sa4cob2db.sql.SQLNetServer;
/**
 * Server Event
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class ACMServerEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	/** event source */
	private SQLNetServer server;
	/**
	 * Constructor
	 * 
	 * @param server event source
	 */
	public ACMServerEvent(SQLNetServer server) {
		super(server);
		this.server = server;
	}
	/**
	 * server
	 * 
	 * @return event source
	 */
	public SQLNetServer getServer() {
		return server;
	}
}
