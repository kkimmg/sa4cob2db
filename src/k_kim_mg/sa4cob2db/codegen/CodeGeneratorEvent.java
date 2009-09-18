package k_kim_mg.sa4cob2db.codegen;

import java.util.EventObject;
/**
 * ジェネレータで発生したイベント
 * @author おれおれ
 */
public class CodeGeneratorEvent extends EventObject {
	/**
	 * むむ？
	 */
	private static final long serialVersionUID = 1L;
	/** ファイルの情報 */
	private transient FileInfo file;
	/** オーナー */
	private transient GeneratorOwner owner;
	/** 生成クラス */
	private transient CodeGenerator generator;
	/** ピリオドを追加するかどうか */
	private String period;
	/**
	 * コンストラクタ
	 * @param source	ファイル情報
	 * @param owner		オーナー
	 * @param generator	生成クラス
	 * @param period	ピリオドを追加するかどうか
	 */
	public CodeGeneratorEvent (FileInfo source, GeneratorOwner owner, CodeGenerator generator, String period) {
		super(source);
		this.file = source;
		this.owner = owner;
		this.generator = generator;
		this.period = period;
	}
	/**
	 * ファイルの情報
	 * @return ファイルの情報
	 */
	public FileInfo getFile() {
		return file;
	}
	/**
	 * ジェネレータ
	 * @return ジェネレータ
	 */
	public CodeGenerator getGenerator() {
		return generator;
	}
	/**
	 * ジェネレータオーナー
	 * @return ジェネレータオーナー
	 */
	public GeneratorOwner getOwner() {
		return owner;
	}
	/**
	 * ピリオドを追加するかどうか
	 * @return ピリオド文字列
	 */
	public String getPeriod() {
		return period;
	}
}
