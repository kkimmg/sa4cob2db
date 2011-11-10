/*
 * Created on 2004/05/23
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.codegen;

import java.util.ArrayList;

/**
 * calls CodeGenerators Methods
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface GeneratorOwner {
	/**
	 * Generate new source from original
	 * @param text	original source (phisical line)
	 */
	public void generate(String text);
	/**
	 * called by generator when generating copy statement
	 * @param statement	copy statement
	 */
	public void callBackCopyStatement (ArrayList<String> statement);
	/**
	 * expand copy statement?
	 * @return true	expand</br>
	 *          false	don't
	 */
	public boolean isExpandCopy();
}
