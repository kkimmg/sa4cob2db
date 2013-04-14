package k_kim_mg.sa4cob2db.test;
import java.sql.ResultSet;
import java.sql.SQLException;

import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.SQLCobolColumn;
import k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData;

/**
 * 日本語文字 columnの半角←→全角変換用ユーティリティクラス
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * @version 1.0
 */
public class HankakuZenkaku extends SQLCobolColumn {
	public HankakuZenkaku(CobolRecordMetaData meta) {
	    super((SQLCobolRecordMetaData)meta);
    }
	/**	半角と全角のセット */
	private static class HanZenSet {
		/**半角文字*/
		private char hankaku;
		/**全角文字*/
		private char zenkaku;
		/**
		 * @param hankaku
		 * @param zenkaku
		 */
		public HanZenSet(char hankaku, char zenkaku) {
			this.hankaku = hankaku;
			this.zenkaku = zenkaku;
		}
		/**
		 * @return
		 */
		public char getHankaku() {
			return hankaku;
		}
		/**
		 * @return
		 */
		public char getZenkaku() {
			return zenkaku;
		}
	}
	/**	半角と全角のセット */
	private static class HanZenSet2 {
		/**半角文字*/
		private String hankaku;
		/**全角文字*/
		private String zenkaku;
		/**
		 * @param hankaku
		 * @param zenkaku
		 */
		public HanZenSet2(String hankaku, String zenkaku) {
			this.hankaku = hankaku;
			this.zenkaku = zenkaku;
		}
		/**
		 * @return
		 */
		/**
		 * @return
		 */
		public String getHankaku() {
			return hankaku;
		}
		/**
		 * @return
		 */
		public String getZenkaku() {
			return zenkaku;
		}
	}
	/**	変換用文字 columnセット */
	private static final HanZenSet[] MOJISET =
		new HanZenSet[] {
			new HanZenSet('!', '！'),
			new HanZenSet('"', '”'),
			new HanZenSet('#', '＃'),
			new HanZenSet('$', '＄'),
			new HanZenSet('%', '％'),
			new HanZenSet('&', '＆'),
			new HanZenSet('\'', '’'),
			new HanZenSet('(', '（'),
			new HanZenSet(')', '）'),
			new HanZenSet('*', '＊'),
			new HanZenSet('+', '＋'),
			new HanZenSet(',', '，'),
			new HanZenSet('-', '−'),
			new HanZenSet('.', '．'),
			new HanZenSet('/', '／'),
			new HanZenSet('0', '０'),
			new HanZenSet('1', '１'),
			new HanZenSet('2', '２'),
			new HanZenSet('3', '３'),
			new HanZenSet('4', '４'),
			new HanZenSet('5', '５'),
			new HanZenSet('6', '６'),
			new HanZenSet('7', '７'),
			new HanZenSet('8', '８'),
			new HanZenSet('9', '９'),
			new HanZenSet(':', '：'),
			new HanZenSet(';', '；'),
			new HanZenSet('<', '＜'),
			new HanZenSet('=', '＝'),
			new HanZenSet('>', '＞'),
			new HanZenSet('?', '？'),
			new HanZenSet('@', '＠'),
			new HanZenSet('A', 'Ａ'),
			new HanZenSet('B', 'Ｂ'),
			new HanZenSet('C', 'Ｃ'),
			new HanZenSet('D', 'Ｄ'),
			new HanZenSet('E', 'Ｅ'),
			new HanZenSet('F', 'Ｆ'),
			new HanZenSet('G', 'Ｇ'),
			new HanZenSet('H', 'Ｈ'),
			new HanZenSet('I', 'Ｉ'),
			new HanZenSet('J', 'Ｊ'),
			new HanZenSet('K', 'Ｋ'),
			new HanZenSet('L', 'Ｌ'),
			new HanZenSet('M', 'Ｍ'),
			new HanZenSet('N', 'Ｎ'),
			new HanZenSet('O', 'Ｏ'),
			new HanZenSet('P', 'Ｐ'),
			new HanZenSet('Q', 'Ｑ'),
			new HanZenSet('R', 'Ｒ'),
			new HanZenSet('S', 'Ｓ'),
			new HanZenSet('T', 'Ｔ'),
			new HanZenSet('U', 'Ｕ'),
			new HanZenSet('V', 'Ｖ'),
			new HanZenSet('W', 'Ｗ'),
			new HanZenSet('X', 'Ｘ'),
			new HanZenSet('Y', 'Ｙ'),
			new HanZenSet('Z', 'Ｚ'),
			new HanZenSet('[', '［'),
			new HanZenSet('\\', '¥'),
			new HanZenSet(']', '］'),
			new HanZenSet('^', '＾'),
			new HanZenSet('_', '＿'),
			new HanZenSet('`', '‘'),
			new HanZenSet('a', 'ａ'),
			new HanZenSet('b', 'ｂ'),
			new HanZenSet('c', 'ｃ'),
			new HanZenSet('d', 'ｄ'),
			new HanZenSet('e', 'ｅ'),
			new HanZenSet('f', 'ｆ'),
			new HanZenSet('g', 'ｇ'),
			new HanZenSet('h', 'ｈ'),
			new HanZenSet('i', 'ｉ'),
			new HanZenSet('j', 'ｊ'),
			new HanZenSet('k', 'ｋ'),
			new HanZenSet('l', 'ｌ'),
			new HanZenSet('m', 'ｍ'),
			new HanZenSet('n', 'ｎ'),
			new HanZenSet('o', 'ｏ'),
			new HanZenSet('p', 'ｐ'),
			new HanZenSet('q', 'ｑ'),
			new HanZenSet('r', 'ｒ'),
			new HanZenSet('s', 'ｓ'),
			new HanZenSet('t', 'ｔ'),
			new HanZenSet('u', 'ｕ'),
			new HanZenSet('v', 'ｖ'),
			new HanZenSet('w', 'ｗ'),
			new HanZenSet('x', 'ｘ'),
			new HanZenSet('y', 'ｙ'),
			new HanZenSet('z', 'ｚ'),
			new HanZenSet('{', '｛'),
			new HanZenSet('|', '｜'),
			new HanZenSet('}', '｝'),
			new HanZenSet('~', '〜'),
			new HanZenSet('?', '?'),
			new HanZenSet('。', '。'),
			new HanZenSet('「', '「'),
			new HanZenSet('」', '」'),
			new HanZenSet('、', '、'),
			new HanZenSet('・', '・'),
			new HanZenSet('ヲ', 'ヲ'),
			new HanZenSet('ァ', 'ァ'),
			new HanZenSet('ィ', 'ィ'),
			new HanZenSet('ゥ', 'ゥ'),
			new HanZenSet('ェ', 'ェ'),
			new HanZenSet('ォ', 'ォ'),
			new HanZenSet('ャ', 'ャ'),
			new HanZenSet('ュ', 'ュ'),
			new HanZenSet('ョ', 'ョ'),
			new HanZenSet('ッ', 'ッ'),
			new HanZenSet('ー', 'ー'),
			new HanZenSet('ア', 'ア'),
			new HanZenSet('イ', 'イ'),
			new HanZenSet('ウ', 'ウ'),
			new HanZenSet('エ', 'エ'),
			new HanZenSet('オ', 'オ'),
			new HanZenSet('カ', 'カ'),
			new HanZenSet('キ', 'キ'),
			new HanZenSet('ク', 'ク'),
			new HanZenSet('ケ', 'ケ'),
			new HanZenSet('コ', 'コ'),
			new HanZenSet('サ', 'サ'),
			new HanZenSet('シ', 'シ'),
			new HanZenSet('ス', 'ス'),
			new HanZenSet('セ', 'セ'),
			new HanZenSet('ソ', 'ソ'),
			new HanZenSet('タ', 'タ'),
			new HanZenSet('チ', 'チ'),
			new HanZenSet('ツ', 'ツ'),
			new HanZenSet('テ', 'テ'),
			new HanZenSet('ト', 'ト'),
			new HanZenSet('ナ', 'ナ'),
			new HanZenSet('ニ', 'ニ'),
			new HanZenSet('ヌ', 'ヌ'),
			new HanZenSet('ネ', 'ネ'),
			new HanZenSet('ノ', 'ノ'),
			new HanZenSet('ハ', 'ハ'),
			new HanZenSet('ヒ', 'ヒ'),
			new HanZenSet('フ', 'フ'),
			new HanZenSet('ヘ', 'ヘ'),
			new HanZenSet('ホ', 'ホ'),
			new HanZenSet('マ', 'マ'),
			new HanZenSet('ミ', 'ミ'),
			new HanZenSet('ム', 'ム'),
			new HanZenSet('メ', 'メ'),
			new HanZenSet('モ', 'モ'),
			new HanZenSet('ヤ', 'ヤ'),
			new HanZenSet('ユ', 'ユ'),
			new HanZenSet('ヨ', 'ヨ'),
			new HanZenSet('ラ', 'ラ'),
			new HanZenSet('リ', 'リ'),
			new HanZenSet('ル', 'ル'),
			new HanZenSet('レ', 'レ'),
			new HanZenSet('ロ', 'ロ'),
			new HanZenSet('ワ', 'ワ'),
			new HanZenSet('ン', 'ン'),
			new HanZenSet('゛', '゛'),
			new HanZenSet('゜', '゜')};
	/**	変換用文字 columnセット */
	private static final HanZenSet2[] MOJISET2 =
		new HanZenSet2[] {
			new HanZenSet2(" ", "　"),
			new HanZenSet2("!", "！"),
			new HanZenSet2("\"", "”"),
			new HanZenSet2("#", "＃"),
			new HanZenSet2("\\$", "＄"),
			new HanZenSet2("%", "％"),
			new HanZenSet2("&", "＆"),
			new HanZenSet2("'", "’"),
			new HanZenSet2("\\(", "（"),
			new HanZenSet2("\\)", "）"),
			new HanZenSet2("\\*", "＊"),
			new HanZenSet2("\\+", "＋"),
			new HanZenSet2(",", "，"),
			new HanZenSet2("-", "−"),
			new HanZenSet2("\\.", "．"),
			new HanZenSet2("/", "／"),
			new HanZenSet2("0", "０"),
			new HanZenSet2("1", "１"),
			new HanZenSet2("2", "２"),
			new HanZenSet2("3", "３"),
			new HanZenSet2("4", "４"),
			new HanZenSet2("5", "５"),
			new HanZenSet2("6", "６"),
			new HanZenSet2("7", "７"),
			new HanZenSet2("8", "８"),
			new HanZenSet2("9", "９"),
			new HanZenSet2(":", "："),
			new HanZenSet2(";", "；"),
			new HanZenSet2("<", "＜"),
			new HanZenSet2("=", "＝"),
			new HanZenSet2(">", "＞"),
			new HanZenSet2("\\?", "？"),
			new HanZenSet2("\\@", "＠"),
			new HanZenSet2("A", "Ａ"),
			new HanZenSet2("B", "Ｂ"),
			new HanZenSet2("C", "Ｃ"),
			new HanZenSet2("D", "Ｄ"),
			new HanZenSet2("E", "Ｅ"),
			new HanZenSet2("F", "Ｆ"),
			new HanZenSet2("G", "Ｇ"),
			new HanZenSet2("H", "Ｈ"),
			new HanZenSet2("I", "Ｉ"),
			new HanZenSet2("J", "Ｊ"),
			new HanZenSet2("K", "Ｋ"),
			new HanZenSet2("L", "Ｌ"),
			new HanZenSet2("M", "Ｍ"),
			new HanZenSet2("N", "Ｎ"),
			new HanZenSet2("O", "Ｏ"),
			new HanZenSet2("P", "Ｐ"),
			new HanZenSet2("Q", "Ｑ"),
			new HanZenSet2("R", "Ｒ"),
			new HanZenSet2("S", "Ｓ"),
			new HanZenSet2("T", "Ｔ"),
			new HanZenSet2("U", "Ｕ"),
			new HanZenSet2("V", "Ｖ"),
			new HanZenSet2("W", "Ｗ"),
			new HanZenSet2("X", "Ｘ"),
			new HanZenSet2("Y", "Ｙ"),
			new HanZenSet2("Z", "Ｚ"),
			new HanZenSet2("\\[", "［"),
			new HanZenSet2("\\?", "¥"),
			new HanZenSet2("\\]", "］"),
			new HanZenSet2("\\^", "＾"),
			new HanZenSet2("_", "＿"),
			new HanZenSet2("`", "‘"),
			new HanZenSet2("a", "ａ"),
			new HanZenSet2("b", "ｂ"),
			new HanZenSet2("c", "ｃ"),
			new HanZenSet2("d", "ｄ"),
			new HanZenSet2("e", "ｅ"),
			new HanZenSet2("f", "ｆ"),
			new HanZenSet2("g", "ｇ"),
			new HanZenSet2("h", "ｈ"),
			new HanZenSet2("i", "ｉ"),
			new HanZenSet2("j", "ｊ"),
			new HanZenSet2("k", "ｋ"),
			new HanZenSet2("l", "ｌ"),
			new HanZenSet2("m", "ｍ"),
			new HanZenSet2("n", "ｎ"),
			new HanZenSet2("o", "ｏ"),
			new HanZenSet2("p", "ｐ"),
			new HanZenSet2("q", "ｑ"),
			new HanZenSet2("r", "ｒ"),
			new HanZenSet2("s", "ｓ"),
			new HanZenSet2("t", "ｔ"),
			new HanZenSet2("u", "ｕ"),
			new HanZenSet2("v", "ｖ"),
			new HanZenSet2("w", "ｗ"),
			new HanZenSet2("x", "ｘ"),
			new HanZenSet2("y", "ｙ"),
			new HanZenSet2("z", "ｚ"),
			new HanZenSet2("\\{", "｛"),
			new HanZenSet2("\\|", "｜"),
			new HanZenSet2("\\}", "｝"),
			new HanZenSet2("\\~", "〜"),
			new HanZenSet2("。", "。"),
			new HanZenSet2("「", "「"),
			new HanZenSet2("」", "」"),
			new HanZenSet2("、", "、"),
			new HanZenSet2("・", "・"),
			new HanZenSet2("ヲ", "ヲ"),
			new HanZenSet2("ァ", "ァ"),
			new HanZenSet2("ィ", "ィ"),
			new HanZenSet2("ゥ", "ゥ"),
			new HanZenSet2("ェ", "ェ"),
			new HanZenSet2("ォ", "ォ"),
			new HanZenSet2("ャ", "ャ"),
			new HanZenSet2("ュ", "ュ"),
			new HanZenSet2("ョ", "ョ"),
			new HanZenSet2("ッ", "ッ"),
			new HanZenSet2("ー", "ー"),
			new HanZenSet2("ア", "ア"),
			new HanZenSet2("イ", "イ"),
			new HanZenSet2("ウ", "ウ"),
			new HanZenSet2("エ", "エ"),
			new HanZenSet2("オ", "オ"),
			new HanZenSet2("カ", "カ"),
			new HanZenSet2("キ", "キ"),
			new HanZenSet2("ク", "ク"),
			new HanZenSet2("ケ", "ケ"),
			new HanZenSet2("コ", "コ"),
			new HanZenSet2("サ", "サ"),
			new HanZenSet2("シ", "シ"),
			new HanZenSet2("ス", "ス"),
			new HanZenSet2("セ", "セ"),
			new HanZenSet2("ソ", "ソ"),
			new HanZenSet2("タ", "タ"),
			new HanZenSet2("チ", "チ"),
			new HanZenSet2("ツ", "ツ"),
			new HanZenSet2("テ", "テ"),
			new HanZenSet2("ト", "ト"),
			new HanZenSet2("ナ", "ナ"),
			new HanZenSet2("ニ", "ニ"),
			new HanZenSet2("ヌ", "ヌ"),
			new HanZenSet2("ネ", "ネ"),
			new HanZenSet2("ノ", "ノ"),
			new HanZenSet2("ハ", "ハ"),
			new HanZenSet2("ヒ", "ヒ"),
			new HanZenSet2("フ", "フ"),
			new HanZenSet2("ヘ", "ヘ"),
			new HanZenSet2("ホ", "ホ"),
			new HanZenSet2("マ", "マ"),
			new HanZenSet2("ミ", "ミ"),
			new HanZenSet2("ム", "ム"),
			new HanZenSet2("メ", "メ"),
			new HanZenSet2("モ", "モ"),
			new HanZenSet2("ヤ", "ヤ"),
			new HanZenSet2("ユ", "ユ"),
			new HanZenSet2("ヨ", "ヨ"),
			new HanZenSet2("ラ", "ラ"),
			new HanZenSet2("リ", "リ"),
			new HanZenSet2("ル", "ル"),
			new HanZenSet2("レ", "レ"),
			new HanZenSet2("ロ", "ロ"),
			new HanZenSet2("ワ", "ワ"),
			new HanZenSet2("ン", "ン"),
			new HanZenSet2("゛", "゛"),
			new HanZenSet2("゜", "゜"),
			new HanZenSet2("ガ", "ガ"),
			new HanZenSet2("ギ", "キ"),
			new HanZenSet2("ギ", "ギ"),
			new HanZenSet2("グ", "グ"),
			new HanZenSet2("ゲ", "ゲ"),
			new HanZenSet2("ゴ", "ゴ"),
			new HanZenSet2("ザ", "ザ"),
			new HanZenSet2("ジ", "ジ"),
			new HanZenSet2("ズ", "ズ"),
			new HanZenSet2("ゼ", "ゼ"),
			new HanZenSet2("ゾ", "ゾ"),
			new HanZenSet2("ダ", "ダ"),
			new HanZenSet2("ヂ", "ヂ"),
			new HanZenSet2("ッ", "ッ"),
			new HanZenSet2("ヅ", "ヅ"),
			new HanZenSet2("デ", "デ"),
			new HanZenSet2("ド", "ド"),
			new HanZenSet2("バ", "バ"),
			new HanZenSet2("パ", "パ"),
			new HanZenSet2("ビ", "ビ"),
			new HanZenSet2("ピ", "ピ"),
			new HanZenSet2("ブ", "ブ"),
			new HanZenSet2("プ", "プ"),
			new HanZenSet2("ベ", "ベ"),
			new HanZenSet2("ペ", "ペ"),
			new HanZenSet2("ボ", "ボ"),
			new HanZenSet2("ポ", "ポ"),
			new HanZenSet2("ャ", "ャ"),
			new HanZenSet2("ュ", "ュ"),
			new HanZenSet2("ョ", "ョ"),
			new HanZenSet2("ヮ", "ヮ"),
			new HanZenSet2("ヴ", "ヴ"),
			new HanZenSet2("ヵ", "ヵ"),
			new HanZenSet2("ヶ", "ヶ")};
	/**	変換用文字 columnセット */
	private static final HanZenSet2[] MOJISET3 =
		new HanZenSet2[] {
			new HanZenSet2(" ", "　"),
			new HanZenSet2("!", "！"),
			new HanZenSet2("\"", "”"),
			new HanZenSet2("#", "＃"),
			new HanZenSet2("$", "＄"),
			new HanZenSet2("%", "％"),
			new HanZenSet2("&", "＆"),
			new HanZenSet2("'", "’"),
			new HanZenSet2("(", "（"),
			new HanZenSet2(")", "）"),
			new HanZenSet2("*", "＊"),
			new HanZenSet2("+", "＋"),
			new HanZenSet2(",", "，"),
			new HanZenSet2("-", "−"),
			new HanZenSet2(".", "．"),
			new HanZenSet2("/", "／"),
			new HanZenSet2("0", "０"),
			new HanZenSet2("1", "１"),
			new HanZenSet2("2", "２"),
			new HanZenSet2("3", "３"),
			new HanZenSet2("4", "４"),
			new HanZenSet2("5", "５"),
			new HanZenSet2("6", "６"),
			new HanZenSet2("7", "７"),
			new HanZenSet2("8", "８"),
			new HanZenSet2("9", "９"),
			new HanZenSet2(":", "："),
			new HanZenSet2(";", "；"),
			new HanZenSet2("<", "＜"),
			new HanZenSet2("=", "＝"),
			new HanZenSet2(">", "＞"),
			new HanZenSet2("?", "？"),
			new HanZenSet2("@", "＠"),
			new HanZenSet2("A", "Ａ"),
			new HanZenSet2("B", "Ｂ"),
			new HanZenSet2("C", "Ｃ"),
			new HanZenSet2("D", "Ｄ"),
			new HanZenSet2("E", "Ｅ"),
			new HanZenSet2("F", "Ｆ"),
			new HanZenSet2("G", "Ｇ"),
			new HanZenSet2("H", "Ｈ"),
			new HanZenSet2("I", "Ｉ"),
			new HanZenSet2("J", "Ｊ"),
			new HanZenSet2("K", "Ｋ"),
			new HanZenSet2("L", "Ｌ"),
			new HanZenSet2("M", "Ｍ"),
			new HanZenSet2("N", "Ｎ"),
			new HanZenSet2("O", "Ｏ"),
			new HanZenSet2("P", "Ｐ"),
			new HanZenSet2("Q", "Ｑ"),
			new HanZenSet2("R", "Ｒ"),
			new HanZenSet2("S", "Ｓ"),
			new HanZenSet2("T", "Ｔ"),
			new HanZenSet2("U", "Ｕ"),
			new HanZenSet2("V", "Ｖ"),
			new HanZenSet2("W", "Ｗ"),
			new HanZenSet2("X", "Ｘ"),
			new HanZenSet2("Y", "Ｙ"),
			new HanZenSet2("Z", "Ｚ"),
			new HanZenSet2("[", "［"),
			new HanZenSet2("\\\\'", "¥"),
			new HanZenSet2("]", "］"),
			new HanZenSet2("^", "＾"),
			new HanZenSet2("_", "＿"),
			new HanZenSet2("`", "‘"),
			new HanZenSet2("a", "ａ"),
			new HanZenSet2("b", "ｂ"),
			new HanZenSet2("c", "ｃ"),
			new HanZenSet2("d", "ｄ"),
			new HanZenSet2("e", "ｅ"),
			new HanZenSet2("f", "ｆ"),
			new HanZenSet2("g", "ｇ"),
			new HanZenSet2("h", "ｈ"),
			new HanZenSet2("i", "ｉ"),
			new HanZenSet2("j", "ｊ"),
			new HanZenSet2("k", "ｋ"),
			new HanZenSet2("l", "ｌ"),
			new HanZenSet2("m", "ｍ"),
			new HanZenSet2("n", "ｎ"),
			new HanZenSet2("o", "ｏ"),
			new HanZenSet2("p", "ｐ"),
			new HanZenSet2("q", "ｑ"),
			new HanZenSet2("r", "ｒ"),
			new HanZenSet2("s", "ｓ"),
			new HanZenSet2("t", "ｔ"),
			new HanZenSet2("u", "ｕ"),
			new HanZenSet2("v", "ｖ"),
			new HanZenSet2("w", "ｗ"),
			new HanZenSet2("x", "ｘ"),
			new HanZenSet2("y", "ｙ"),
			new HanZenSet2("z", "ｚ"),
			new HanZenSet2("\\{", "｛"),
			new HanZenSet2("\\|", "｜"),
			new HanZenSet2("\\}", "｝"),
			new HanZenSet2("\\~", "〜"),
			new HanZenSet2("。", "。"),
			new HanZenSet2("「", "「"),
			new HanZenSet2("」", "」"),
			new HanZenSet2("、", "、"),
			new HanZenSet2("・", "・"),
			new HanZenSet2("ヲ", "ヲ"),
			new HanZenSet2("ァ", "ァ"),
			new HanZenSet2("ィ", "ィ"),
			new HanZenSet2("ゥ", "ゥ"),
			new HanZenSet2("ェ", "ェ"),
			new HanZenSet2("ォ", "ォ"),
			new HanZenSet2("ャ", "ャ"),
			new HanZenSet2("ュ", "ュ"),
			new HanZenSet2("ョ", "ョ"),
			new HanZenSet2("ッ", "ッ"),
			new HanZenSet2("ー", "ー"),
			new HanZenSet2("ア", "ア"),
			new HanZenSet2("イ", "イ"),
			new HanZenSet2("ウ", "ウ"),
			new HanZenSet2("エ", "エ"),
			new HanZenSet2("オ", "オ"),
			new HanZenSet2("カ", "カ"),
			new HanZenSet2("キ", "キ"),
			new HanZenSet2("ク", "ク"),
			new HanZenSet2("ケ", "ケ"),
			new HanZenSet2("コ", "コ"),
			new HanZenSet2("サ", "サ"),
			new HanZenSet2("シ", "シ"),
			new HanZenSet2("ス", "ス"),
			new HanZenSet2("セ", "セ"),
			new HanZenSet2("ソ", "ソ"),
			new HanZenSet2("タ", "タ"),
			new HanZenSet2("チ", "チ"),
			new HanZenSet2("ツ", "ツ"),
			new HanZenSet2("テ", "テ"),
			new HanZenSet2("ト", "ト"),
			new HanZenSet2("ナ", "ナ"),
			new HanZenSet2("ニ", "ニ"),
			new HanZenSet2("ヌ", "ヌ"),
			new HanZenSet2("ネ", "ネ"),
			new HanZenSet2("ノ", "ノ"),
			new HanZenSet2("ハ", "ハ"),
			new HanZenSet2("ヒ", "ヒ"),
			new HanZenSet2("フ", "フ"),
			new HanZenSet2("ヘ", "ヘ"),
			new HanZenSet2("ホ", "ホ"),
			new HanZenSet2("マ", "マ"),
			new HanZenSet2("ミ", "ミ"),
			new HanZenSet2("ム", "ム"),
			new HanZenSet2("メ", "メ"),
			new HanZenSet2("モ", "モ"),
			new HanZenSet2("ヤ", "ヤ"),
			new HanZenSet2("ユ", "ユ"),
			new HanZenSet2("ヨ", "ヨ"),
			new HanZenSet2("ラ", "ラ"),
			new HanZenSet2("リ", "リ"),
			new HanZenSet2("ル", "ル"),
			new HanZenSet2("レ", "レ"),
			new HanZenSet2("ロ", "ロ"),
			new HanZenSet2("ワ", "ワ"),
			new HanZenSet2("ン", "ン"),
			new HanZenSet2("゛", "゛"),
			new HanZenSet2("゜", "゜"),
			new HanZenSet2("ガ", "ガ"),
			new HanZenSet2("ギ", "キ"),
			new HanZenSet2("ギ", "ギ"),
			new HanZenSet2("グ", "グ"),
			new HanZenSet2("ゲ", "ゲ"),
			new HanZenSet2("ゴ", "ゴ"),
			new HanZenSet2("ザ", "ザ"),
			new HanZenSet2("ジ", "ジ"),
			new HanZenSet2("ズ", "ズ"),
			new HanZenSet2("ゼ", "ゼ"),
			new HanZenSet2("ゾ", "ゾ"),
			new HanZenSet2("ダ", "ダ"),
			new HanZenSet2("ヂ", "ヂ"),
			new HanZenSet2("ッ", "ッ"),
			new HanZenSet2("ヅ", "ヅ"),
			new HanZenSet2("デ", "デ"),
			new HanZenSet2("ド", "ド"),
			new HanZenSet2("バ", "バ"),
			new HanZenSet2("パ", "パ"),
			new HanZenSet2("ビ", "ビ"),
			new HanZenSet2("ピ", "ピ"),
			new HanZenSet2("ブ", "ブ"),
			new HanZenSet2("プ", "プ"),
			new HanZenSet2("ベ", "ベ"),
			new HanZenSet2("ペ", "ペ"),
			new HanZenSet2("ボ", "ボ"),
			new HanZenSet2("ポ", "ポ"),
			new HanZenSet2("ャ", "ャ"),
			new HanZenSet2("ュ", "ュ"),
			new HanZenSet2("ョ", "ョ"),
			new HanZenSet2("ヮ", "ヮ"),
			new HanZenSet2("ヴ", "ヴ"),
			new HanZenSet2("ヵ", "ヵ"),
			new HanZenSet2("ヶ", "ヶ")};
	/**
	 * @param hankaku
	 * @return
	 */
	public static String han2zen(String hankaku) {
		String ret = hankaku;
		for (int i = 0; i < MOJISET2.length; i++) {
			try {
				//SQLNetServer.DebugPrint(MOJISET2[i].getHankaku()+"->"+MOJISET2[i].getZenkaku());
				ret = ret.replaceAll(MOJISET2[i].getHankaku(), MOJISET2[i].getZenkaku());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	/**
	 * @param hankaku
	 * @return
	 */
	public static String hanTozen(String hankaku) {
		String ret = hankaku;
		for (int i = 0; i < MOJISET.length; i++) {
			try {
				ret = ret.replace(MOJISET[i].getHankaku(), MOJISET[i].getZenkaku());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	/**
	 * @param zenkaku
	 * @return
	 */
	public static String zen2han(String zenkaku) {
		String ret = zenkaku;
		for (int i = 0; i < MOJISET2.length; i++) {
			try {
				ret = ret.replaceAll(MOJISET3[i].getZenkaku(), MOJISET3[i].getHankaku());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	/**
	 * @param zenkaku
	 * @return
	 */
	public static String zenTohan(String zenkaku) {
		String ret = zenkaku;
		for (int i = 0; i < MOJISET.length; i++) {
			try {
				ret = ret.replace(MOJISET[i].getZenkaku(), MOJISET[i].getHankaku());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.sql.SQLCobolColumn#SetRecord2ResultSet(k_kim_mg.sa4cob2db.CobolRecord, java.sql.ResultSet)
     */
    @Override
    public void setRecord2ResultSet(CobolRecord src, ResultSet dst) throws SQLException, CobolRecordException {
    	String text0 = src.getString(this).trim();
    	String text1 = HankakuZenkaku.han2zen(text0);
    	dst.updateString(getOriginalColumnName(), text1);
    }
	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.sql.SQLCobolColumn#SetResultSet2Record(java.sql.ResultSet, k_kim_mg.sa4cob2db.CobolRecord)
     */
    @Override
    public void setResultSet2Record(ResultSet src, CobolRecord dst) throws SQLException, CobolRecordException {
    	String text0 = src.getString(getOriginalColumnName()).trim();
    	String text1 = HankakuZenkaku.han2zen(text0);
    	dst.updateString(this, text1);
    }
	/* (non-Javadoc)
     * @see k_kim_mg.sa4cob2db.DefaultCobolColumn#getPhysicalLength()
     */
    @Override
    public int getPhysicalLength() {
	    return getLength() * 2;
    }
}
