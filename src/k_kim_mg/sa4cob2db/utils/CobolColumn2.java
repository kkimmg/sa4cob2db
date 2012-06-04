package k_kim_mg.sa4cob2db.utils;
import k_kim_mg.sa4cob2db.CobolColumn;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
public interface CobolColumn2 extends CobolColumn {
	/**
	 * 分析する
	 * @param logical 論理行
	 * @return レベル(パース失敗したら00)
	 */
	public int parce(String logical);
	/**
	 * レベル
	 * 
	 * @return レベル
	 */
	public int getLevel();
	public void setLevel(int level);
	/**
	 * 繰り返し回数
	 * 
	 * @return 繰り返し回数
	 */
	public int getOccurs();
	public void setOccurs(int ocuurs);
	/**
	 * XMLノードに出力する
	 * 
	 * @param document XML Document
	 * @param parent 親ノード
	 * @param start 開始位置
	 * @param fix 尻になんかつける
	 * @return 終了位置
	 */
	public int exportToNode(Document document, Node parent, int start, String fix);
}
