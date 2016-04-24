package k_kim_mg.sa4cob2db.codegen;

/**
 * An object that holds information about the file during code generation
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface FileInfo {
	/**
	 * Aceess Mode
	 * 
	 * @return Aceess Mode
	 */
	public int getAcessMode();

	/**
	 * Externalfilename
	 * 
	 * @return filename(Externalfilename) <br/>
	 *         name of file to assign
	 */
	public String getFileName();

	/**
	 * RecordName
	 * 
	 * @return RecordName <br/>
	 *         area name of first level
	 */
	public String getRecordName();

	/**
	 * Internalfilename
	 * 
	 * @return filename(Internalfilename) <br/>
	 *         select XXX
	 */
	public String getSelectName();

	/**
	 * File Status
	 * 
	 * @return File Status Area Name <br/>
	 *         File Status [is] XXX
	 */
	public String getStatus();
}
