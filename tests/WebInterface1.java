/**
 * 
 */
//package k_kim_mg.sa4cob2db.sample;

public class WebInterface1 {
	/**インターフェース名*/
	String name;
	/**コンテンツ*/
	String content;
	/**コールするサブルーチン*/
	String subroutine;
	/**入力書式*/
	String inFormat;
	/**出力書式*/
	String outFormat;
	public String getInFormat() {
		return inFormat;
	}
	public void setInFormat(String inFormat) {
		this.inFormat = inFormat;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOutFormat() {
		return outFormat;
	}
	public void setOutFormat(String outFormat) {
		this.outFormat = outFormat;
	}
	public String getSubroutine() {
		return subroutine;
	}
	public void setSubroutine(String subroutine) {
		this.subroutine = subroutine;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}