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
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public interface GeneratorOwner {
  /**
   * called by generator when generating copy statement
   * 
   * @param statement
   *          copy statement
   */
  public void callBackCopyStatement(ArrayList<String> statement);

  /**
   * Generate new source from original
   * 
   * @param text
   *          original source (physical line)
   */
  public void generate(String text);

  /**
   * Ignore Comment Out
   * 
   * @return true yes false not
   */
  public boolean isDontComment();

  /**
   * expand copy statement?
   * 
   * @return true expand false don't
   */
  public boolean isExpandCopy();

  /**
   * In file format is free or fix.
   * 
   * @return true is free. false is fix.
   */
  public boolean isInfreeformat();

  /**
   * Out file format is free or fix.
   * 
   * @return true is free. false is fix.
   */
  public boolean isOutfreeformat();

  /**
   * don't initialize?
   * 
   * @return true yes false not
   */
  public boolean isSubprogram();
}
