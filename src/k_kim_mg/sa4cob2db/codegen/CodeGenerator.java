package k_kim_mg.sa4cob2db.codegen;

/**
 * Code Generator
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface CodeGenerator {
	/**
	 * parse row text.
	 * 
	 * @param row logical row
	 */
	public void parse(String row);

	/**
	 * pass buffered row to owner and clear buffer.
	 */
	public void flush();

	/**
	 * add event listener.
	 * 
	 * @param listener listener
	 */
	public void addCodeGeneratorListener(CodeGeneratorListener listener);

	/**
	 * Get GeneratorOwner.
	 * 
	 * @return GeneratorOwner
	 */
	public GeneratorOwner getOwner();

	/**
	 * Set GeneratorOwner.
	 * 
	 * @param owner GeneratorOwner
	 */
	public void setOwner(GeneratorOwner owner);
}
