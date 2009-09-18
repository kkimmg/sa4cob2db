package k_kim_mg.sa4cob2db.codegen;
/**
 * ソースコードを生成するインターフェース
 * @author おれおれ
 */
public interface CodeGenerator {
	/**
	 * ソースコードを生成する
	 * @param row	行
	 */
	public void parse(String row);
	/**
	 * クリアする
	 * 蓄積した行をオーナーに全て渡してクリアする
	 */
	public void clear();
	/**
	 * コード生成持のイベント処理を追加する
	 * @param listener	コード生成持のイベント処理
	 */
	public void addCodeGeneratorListener(CodeGeneratorListener listener);
}
