/*
 * Created on 2004/05/23
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.codegen;

import java.util.ArrayList;

/**
 * CodeGeneratorを呼び出す機能
 * @author おれおれ
 */
public interface GeneratorOwner {
	/**
	 * ソースコードを生成する
	 * @param text	生成するソースコード
	 */
	public void generate(String text);
	/**
	 * コピー句が来たのでなんとかしてください
	 * @param statement	コピー句
	 */
	public void callBackCopyStatement (ArrayList<String> statement);
	/**
	 * コピー句を展開するかどうか
	 * @return true	展開する</br>
	 *          false	展開しない
	 */
	public boolean isExpandCopy();
}
