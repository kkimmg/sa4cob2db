package k_kim_mg.sa4cob2db.codegen;

import java.util.EventObject;
/**
 * Event that
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CodeGeneratorEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	/** file information */
	private transient FileInfo file;
	/** owner */
	private transient GeneratorOwner owner;
	/** generator  */
	private transient CodeGenerator generator;
	/** period string */
	private String period;
	/**
	 * Constructor
	 * @param source	file information
	 * @param owner		owner
	 * @param generator	generator
	 * @param period	period string
	 */
	public CodeGeneratorEvent (FileInfo source, GeneratorOwner owner, CodeGenerator generator, String period) {
		super(source);
		this.file = source;
		this.owner = owner;
		this.generator = generator;
		this.period = period;
	}
	/**
	 * file information
	 * @return file information
	 */
	public FileInfo getFile() {
		return file;
	}
	/**
	 * generator
	 * @return generator
	 */
	public CodeGenerator getGenerator() {
		return generator;
	}
	/**
	 * owner
	 * @return owner
	 */
	public GeneratorOwner getOwner() {
		return owner;
	}
	/**
	 * period
	 * @return period string
	 */
	public String getPeriod() {
		return period;
	}
}
