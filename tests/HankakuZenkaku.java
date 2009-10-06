import java.sql.ResultSet;
import java.sql.SQLException;

import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.SQLCobolColumn;
import k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData;

/**
 * ÆüËÜ¸ìÊ¸»úÎó¤ÎÈ¾³Ñ¢«¢ªÁ´³ÑÊÑ´¹ÍÑ¥æ¡¼¥Æ¥£¥ê¥Æ¥£¥¯¥é¥¹
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * @version 1.0
 */
public class HankakuZenkaku extends SQLCobolColumn {
	public HankakuZenkaku(CobolRecordMetaData meta) {
	    super((SQLCobolRecordMetaData)meta);
    }
	/**	È¾³Ñ¤ÈÁ´³Ñ¤Î¥»¥Ã¥È */
	private static class HanZenSet {
		/**È¾³ÑÊ¸»ú*/
		private char hankaku;
		/**Á´³ÑÊ¸»ú*/
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
	/**	È¾³Ñ¤ÈÁ´³Ñ¤Î¥»¥Ã¥È */
	private static class HanZenSet2 {
		/**È¾³ÑÊ¸»ú*/
		private String hankaku;
		/**Á´³ÑÊ¸»ú*/
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
	/**	ÊÑ´¹ÍÑÊ¸»úÎó¥»¥Ã¥È */
	private static final HanZenSet[] MOJISET =
		new HanZenSet[] {
			new HanZenSet('!', '¡ª'),
			new HanZenSet('"', '¡É'),
			new HanZenSet('#', '¡ô'),
			new HanZenSet('$', '¡ð'),
			new HanZenSet('%', '¡ó'),
			new HanZenSet('&', '¡õ'),
			new HanZenSet('\'', '¡Ç'),
			new HanZenSet('(', '¡Ê'),
			new HanZenSet(')', '¡Ë'),
			new HanZenSet('*', '¡ö'),
			new HanZenSet('+', '¡Ü'),
			new HanZenSet(',', '¡¤'),
			new HanZenSet('-', '¡Ý'),
			new HanZenSet('.', '¡¥'),
			new HanZenSet('/', '¡¿'),
			new HanZenSet('0', '£°'),
			new HanZenSet('1', '£±'),
			new HanZenSet('2', '£²'),
			new HanZenSet('3', '£³'),
			new HanZenSet('4', '£´'),
			new HanZenSet('5', '£µ'),
			new HanZenSet('6', '£¶'),
			new HanZenSet('7', '£·'),
			new HanZenSet('8', '£¸'),
			new HanZenSet('9', '£¹'),
			new HanZenSet(':', '¡§'),
			new HanZenSet(';', '¡¨'),
			new HanZenSet('<', '¡ã'),
			new HanZenSet('=', '¡á'),
			new HanZenSet('>', '¡ä'),
			new HanZenSet('?', '¡©'),
			new HanZenSet('@', '¡÷'),
			new HanZenSet('A', '£Á'),
			new HanZenSet('B', '£Â'),
			new HanZenSet('C', '£Ã'),
			new HanZenSet('D', '£Ä'),
			new HanZenSet('E', '£Å'),
			new HanZenSet('F', '£Æ'),
			new HanZenSet('G', '£Ç'),
			new HanZenSet('H', '£È'),
			new HanZenSet('I', '£É'),
			new HanZenSet('J', '£Ê'),
			new HanZenSet('K', '£Ë'),
			new HanZenSet('L', '£Ì'),
			new HanZenSet('M', '£Í'),
			new HanZenSet('N', '£Î'),
			new HanZenSet('O', '£Ï'),
			new HanZenSet('P', '£Ð'),
			new HanZenSet('Q', '£Ñ'),
			new HanZenSet('R', '£Ò'),
			new HanZenSet('S', '£Ó'),
			new HanZenSet('T', '£Ô'),
			new HanZenSet('U', '£Õ'),
			new HanZenSet('V', '£Ö'),
			new HanZenSet('W', '£×'),
			new HanZenSet('X', '£Ø'),
			new HanZenSet('Y', '£Ù'),
			new HanZenSet('Z', '£Ú'),
			new HanZenSet('[', '¡Î'),
			new HanZenSet('\\', '¡ï'),
			new HanZenSet(']', '¡Ï'),
			new HanZenSet('^', '¡°'),
			new HanZenSet('_', '¡²'),
			new HanZenSet('`', '¡Æ'),
			new HanZenSet('a', '£á'),
			new HanZenSet('b', '£â'),
			new HanZenSet('c', '£ã'),
			new HanZenSet('d', '£ä'),
			new HanZenSet('e', '£å'),
			new HanZenSet('f', '£æ'),
			new HanZenSet('g', '£ç'),
			new HanZenSet('h', '£è'),
			new HanZenSet('i', '£é'),
			new HanZenSet('j', '£ê'),
			new HanZenSet('k', '£ë'),
			new HanZenSet('l', '£ì'),
			new HanZenSet('m', '£í'),
			new HanZenSet('n', '£î'),
			new HanZenSet('o', '£ï'),
			new HanZenSet('p', '£ð'),
			new HanZenSet('q', '£ñ'),
			new HanZenSet('r', '£ò'),
			new HanZenSet('s', '£ó'),
			new HanZenSet('t', '£ô'),
			new HanZenSet('u', '£õ'),
			new HanZenSet('v', '£ö'),
			new HanZenSet('w', '£÷'),
			new HanZenSet('x', '£ø'),
			new HanZenSet('y', '£ù'),
			new HanZenSet('z', '£ú'),
			new HanZenSet('{', '¡Ð'),
			new HanZenSet('|', '¡Ã'),
			new HanZenSet('}', '¡Ñ'),
			new HanZenSet('~', '¡Á'),
			new HanZenSet('?', '?'),
			new HanZenSet('Ž¡', '¡£'),
			new HanZenSet('Ž¢', '¡Ö'),
			new HanZenSet('Ž£', '¡×'),
			new HanZenSet('Ž¤', '¡¢'),
			new HanZenSet('Ž¥', '¡¦'),
			new HanZenSet('Ž¦', '¥ò'),
			new HanZenSet('Ž§', '¥¡'),
			new HanZenSet('Ž¨', '¥£'),
			new HanZenSet('Ž©', '¥¥'),
			new HanZenSet('Žª', '¥§'),
			new HanZenSet('Ž«', '¥©'),
			new HanZenSet('Ž¬', '¥ã'),
			new HanZenSet('Ž­', '¥å'),
			new HanZenSet('Ž®', '¥ç'),
			new HanZenSet('Ž¯', '¥Ã'),
			new HanZenSet('Ž°', '¡¼'),
			new HanZenSet('Ž±', '¥¢'),
			new HanZenSet('Ž²', '¥¤'),
			new HanZenSet('Ž³', '¥¦'),
			new HanZenSet('Ž´', '¥¨'),
			new HanZenSet('Žµ', '¥ª'),
			new HanZenSet('Ž¶', '¥«'),
			new HanZenSet('Ž·', '¥­'),
			new HanZenSet('Ž¸', '¥¯'),
			new HanZenSet('Ž¹', '¥±'),
			new HanZenSet('Žº', '¥³'),
			new HanZenSet('Ž»', '¥µ'),
			new HanZenSet('Ž¼', '¥·'),
			new HanZenSet('Ž½', '¥¹'),
			new HanZenSet('Ž¾', '¥»'),
			new HanZenSet('Ž¿', '¥½'),
			new HanZenSet('ŽÀ', '¥¿'),
			new HanZenSet('ŽÁ', '¥Á'),
			new HanZenSet('ŽÂ', '¥Ä'),
			new HanZenSet('ŽÃ', '¥Æ'),
			new HanZenSet('ŽÄ', '¥È'),
			new HanZenSet('ŽÅ', '¥Ê'),
			new HanZenSet('ŽÆ', '¥Ë'),
			new HanZenSet('ŽÇ', '¥Ì'),
			new HanZenSet('ŽÈ', '¥Í'),
			new HanZenSet('ŽÉ', '¥Î'),
			new HanZenSet('ŽÊ', '¥Ï'),
			new HanZenSet('ŽË', '¥Ò'),
			new HanZenSet('ŽÌ', '¥Õ'),
			new HanZenSet('ŽÍ', '¥Ø'),
			new HanZenSet('ŽÎ', '¥Û'),
			new HanZenSet('ŽÏ', '¥Þ'),
			new HanZenSet('ŽÐ', '¥ß'),
			new HanZenSet('ŽÑ', '¥à'),
			new HanZenSet('ŽÒ', '¥á'),
			new HanZenSet('ŽÓ', '¥â'),
			new HanZenSet('ŽÔ', '¥ä'),
			new HanZenSet('ŽÕ', '¥æ'),
			new HanZenSet('ŽÖ', '¥è'),
			new HanZenSet('Ž×', '¥é'),
			new HanZenSet('ŽØ', '¥ê'),
			new HanZenSet('ŽÙ', '¥ë'),
			new HanZenSet('ŽÚ', '¥ì'),
			new HanZenSet('ŽÛ', '¥í'),
			new HanZenSet('ŽÜ', '¥ï'),
			new HanZenSet('ŽÝ', '¥ó'),
			new HanZenSet('ŽÞ', '¡«'),
			new HanZenSet('Žß', '¡¬')};
	/**	ÊÑ´¹ÍÑÊ¸»úÎó¥»¥Ã¥È */
	private static final HanZenSet2[] MOJISET2 =
		new HanZenSet2[] {
			new HanZenSet2(" ", "¡¡"),
			new HanZenSet2("!", "¡ª"),
			new HanZenSet2("\"", "¡É"),
			new HanZenSet2("#", "¡ô"),
			new HanZenSet2("\\$", "¡ð"),
			new HanZenSet2("%", "¡ó"),
			new HanZenSet2("&", "¡õ"),
			new HanZenSet2("'", "¡Ç"),
			new HanZenSet2("\\(", "¡Ê"),
			new HanZenSet2("\\)", "¡Ë"),
			new HanZenSet2("\\*", "¡ö"),
			new HanZenSet2("\\+", "¡Ü"),
			new HanZenSet2(",", "¡¤"),
			new HanZenSet2("-", "¡Ý"),
			new HanZenSet2("\\.", "¡¥"),
			new HanZenSet2("/", "¡¿"),
			new HanZenSet2("0", "£°"),
			new HanZenSet2("1", "£±"),
			new HanZenSet2("2", "£²"),
			new HanZenSet2("3", "£³"),
			new HanZenSet2("4", "£´"),
			new HanZenSet2("5", "£µ"),
			new HanZenSet2("6", "£¶"),
			new HanZenSet2("7", "£·"),
			new HanZenSet2("8", "£¸"),
			new HanZenSet2("9", "£¹"),
			new HanZenSet2(":", "¡§"),
			new HanZenSet2(";", "¡¨"),
			new HanZenSet2("<", "¡ã"),
			new HanZenSet2("=", "¡á"),
			new HanZenSet2(">", "¡ä"),
			new HanZenSet2("\\?", "¡©"),
			new HanZenSet2("\\@", "¡÷"),
			new HanZenSet2("A", "£Á"),
			new HanZenSet2("B", "£Â"),
			new HanZenSet2("C", "£Ã"),
			new HanZenSet2("D", "£Ä"),
			new HanZenSet2("E", "£Å"),
			new HanZenSet2("F", "£Æ"),
			new HanZenSet2("G", "£Ç"),
			new HanZenSet2("H", "£È"),
			new HanZenSet2("I", "£É"),
			new HanZenSet2("J", "£Ê"),
			new HanZenSet2("K", "£Ë"),
			new HanZenSet2("L", "£Ì"),
			new HanZenSet2("M", "£Í"),
			new HanZenSet2("N", "£Î"),
			new HanZenSet2("O", "£Ï"),
			new HanZenSet2("P", "£Ð"),
			new HanZenSet2("Q", "£Ñ"),
			new HanZenSet2("R", "£Ò"),
			new HanZenSet2("S", "£Ó"),
			new HanZenSet2("T", "£Ô"),
			new HanZenSet2("U", "£Õ"),
			new HanZenSet2("V", "£Ö"),
			new HanZenSet2("W", "£×"),
			new HanZenSet2("X", "£Ø"),
			new HanZenSet2("Y", "£Ù"),
			new HanZenSet2("Z", "£Ú"),
			new HanZenSet2("\\[", "¡Î"),
			new HanZenSet2("\\?", "¡ï"),
			new HanZenSet2("\\]", "¡Ï"),
			new HanZenSet2("\\^", "¡°"),
			new HanZenSet2("_", "¡²"),
			new HanZenSet2("`", "¡Æ"),
			new HanZenSet2("a", "£á"),
			new HanZenSet2("b", "£â"),
			new HanZenSet2("c", "£ã"),
			new HanZenSet2("d", "£ä"),
			new HanZenSet2("e", "£å"),
			new HanZenSet2("f", "£æ"),
			new HanZenSet2("g", "£ç"),
			new HanZenSet2("h", "£è"),
			new HanZenSet2("i", "£é"),
			new HanZenSet2("j", "£ê"),
			new HanZenSet2("k", "£ë"),
			new HanZenSet2("l", "£ì"),
			new HanZenSet2("m", "£í"),
			new HanZenSet2("n", "£î"),
			new HanZenSet2("o", "£ï"),
			new HanZenSet2("p", "£ð"),
			new HanZenSet2("q", "£ñ"),
			new HanZenSet2("r", "£ò"),
			new HanZenSet2("s", "£ó"),
			new HanZenSet2("t", "£ô"),
			new HanZenSet2("u", "£õ"),
			new HanZenSet2("v", "£ö"),
			new HanZenSet2("w", "£÷"),
			new HanZenSet2("x", "£ø"),
			new HanZenSet2("y", "£ù"),
			new HanZenSet2("z", "£ú"),
			new HanZenSet2("\\{", "¡Ð"),
			new HanZenSet2("\\|", "¡Ã"),
			new HanZenSet2("\\}", "¡Ñ"),
			new HanZenSet2("\\~", "¡Á"),
			new HanZenSet2("Ž¡", "¡£"),
			new HanZenSet2("Ž¢", "¡Ö"),
			new HanZenSet2("Ž£", "¡×"),
			new HanZenSet2("Ž¤", "¡¢"),
			new HanZenSet2("Ž¥", "¡¦"),
			new HanZenSet2("Ž¦", "¥ò"),
			new HanZenSet2("Ž§", "¥¡"),
			new HanZenSet2("Ž¨", "¥£"),
			new HanZenSet2("Ž©", "¥¥"),
			new HanZenSet2("Žª", "¥§"),
			new HanZenSet2("Ž«", "¥©"),
			new HanZenSet2("Ž¬", "¥ã"),
			new HanZenSet2("Ž­", "¥å"),
			new HanZenSet2("Ž®", "¥ç"),
			new HanZenSet2("Ž¯", "¥Ã"),
			new HanZenSet2("Ž°", "¡¼"),
			new HanZenSet2("Ž±", "¥¢"),
			new HanZenSet2("Ž²", "¥¤"),
			new HanZenSet2("Ž³", "¥¦"),
			new HanZenSet2("Ž´", "¥¨"),
			new HanZenSet2("Žµ", "¥ª"),
			new HanZenSet2("Ž¶", "¥«"),
			new HanZenSet2("Ž·", "¥­"),
			new HanZenSet2("Ž¸", "¥¯"),
			new HanZenSet2("Ž¹", "¥±"),
			new HanZenSet2("Žº", "¥³"),
			new HanZenSet2("Ž»", "¥µ"),
			new HanZenSet2("Ž¼", "¥·"),
			new HanZenSet2("Ž½", "¥¹"),
			new HanZenSet2("Ž¾", "¥»"),
			new HanZenSet2("Ž¿", "¥½"),
			new HanZenSet2("ŽÀ", "¥¿"),
			new HanZenSet2("ŽÁ", "¥Á"),
			new HanZenSet2("ŽÂ", "¥Ä"),
			new HanZenSet2("ŽÃ", "¥Æ"),
			new HanZenSet2("ŽÄ", "¥È"),
			new HanZenSet2("ŽÅ", "¥Ê"),
			new HanZenSet2("ŽÆ", "¥Ë"),
			new HanZenSet2("ŽÇ", "¥Ì"),
			new HanZenSet2("ŽÈ", "¥Í"),
			new HanZenSet2("ŽÉ", "¥Î"),
			new HanZenSet2("ŽÊ", "¥Ï"),
			new HanZenSet2("ŽË", "¥Ò"),
			new HanZenSet2("ŽÌ", "¥Õ"),
			new HanZenSet2("ŽÍ", "¥Ø"),
			new HanZenSet2("ŽÎ", "¥Û"),
			new HanZenSet2("ŽÏ", "¥Þ"),
			new HanZenSet2("ŽÐ", "¥ß"),
			new HanZenSet2("ŽÑ", "¥à"),
			new HanZenSet2("ŽÒ", "¥á"),
			new HanZenSet2("ŽÓ", "¥â"),
			new HanZenSet2("ŽÔ", "¥ä"),
			new HanZenSet2("ŽÕ", "¥æ"),
			new HanZenSet2("ŽÖ", "¥è"),
			new HanZenSet2("Ž×", "¥é"),
			new HanZenSet2("ŽØ", "¥ê"),
			new HanZenSet2("ŽÙ", "¥ë"),
			new HanZenSet2("ŽÚ", "¥ì"),
			new HanZenSet2("ŽÛ", "¥í"),
			new HanZenSet2("ŽÜ", "¥ï"),
			new HanZenSet2("ŽÝ", "¥ó"),
			new HanZenSet2("ŽÞ", "¡«"),
			new HanZenSet2("Žß", "¡¬"),
			new HanZenSet2("Ž¶ŽÞ", "¥¬"),
			new HanZenSet2("Ž·ŽÞ", "¥­"),
			new HanZenSet2("Ž·ŽÞ", "¥®"),
			new HanZenSet2("Ž¸ŽÞ", "¥°"),
			new HanZenSet2("Ž¹ŽÞ", "¥²"),
			new HanZenSet2("ŽºŽÞ", "¥´"),
			new HanZenSet2("Ž»ŽÞ", "¥¶"),
			new HanZenSet2("Ž¼ŽÞ", "¥¸"),
			new HanZenSet2("Ž½ŽÞ", "¥º"),
			new HanZenSet2("Ž¾ŽÞ", "¥¼"),
			new HanZenSet2("Ž¿ŽÞ", "¥¾"),
			new HanZenSet2("ŽÀŽÞ", "¥À"),
			new HanZenSet2("ŽÁŽÞ", "¥Â"),
			new HanZenSet2("Ž¯", "¥Ã"),
			new HanZenSet2("ŽÂŽÞ", "¥Å"),
			new HanZenSet2("ŽÃŽÞ", "¥Ç"),
			new HanZenSet2("ŽÄŽÞ", "¥É"),
			new HanZenSet2("ŽÊŽÞ", "¥Ð"),
			new HanZenSet2("ŽÊŽß", "¥Ñ"),
			new HanZenSet2("ŽËŽÞ", "¥Ó"),
			new HanZenSet2("ŽËŽß", "¥Ô"),
			new HanZenSet2("ŽÌŽÞ", "¥Ö"),
			new HanZenSet2("ŽÌŽß", "¥×"),
			new HanZenSet2("ŽÍŽÞ", "¥Ù"),
			new HanZenSet2("ŽÍŽß", "¥Ú"),
			new HanZenSet2("ŽÎŽÞ", "¥Ü"),
			new HanZenSet2("ŽÎŽß", "¥Ý"),
			new HanZenSet2("Ž¬", "¥ã"),
			new HanZenSet2("Ž­", "¥å"),
			new HanZenSet2("Ž®", "¥ç"),
			new HanZenSet2("¥î", "¥î"),
			new HanZenSet2("¥ô", "¥ô"),
			new HanZenSet2("¥õ", "¥õ"),
			new HanZenSet2("¥ö", "¥ö")};
	/**	ÊÑ´¹ÍÑÊ¸»úÎó¥»¥Ã¥È */
	private static final HanZenSet2[] MOJISET3 =
		new HanZenSet2[] {
			new HanZenSet2(" ", "¡¡"),
			new HanZenSet2("!", "¡ª"),
			new HanZenSet2("\"", "¡É"),
			new HanZenSet2("#", "¡ô"),
			new HanZenSet2("$", "¡ð"),
			new HanZenSet2("%", "¡ó"),
			new HanZenSet2("&", "¡õ"),
			new HanZenSet2("'", "¡Ç"),
			new HanZenSet2("(", "¡Ê"),
			new HanZenSet2(")", "¡Ë"),
			new HanZenSet2("*", "¡ö"),
			new HanZenSet2("+", "¡Ü"),
			new HanZenSet2(",", "¡¤"),
			new HanZenSet2("-", "¡Ý"),
			new HanZenSet2(".", "¡¥"),
			new HanZenSet2("/", "¡¿"),
			new HanZenSet2("0", "£°"),
			new HanZenSet2("1", "£±"),
			new HanZenSet2("2", "£²"),
			new HanZenSet2("3", "£³"),
			new HanZenSet2("4", "£´"),
			new HanZenSet2("5", "£µ"),
			new HanZenSet2("6", "£¶"),
			new HanZenSet2("7", "£·"),
			new HanZenSet2("8", "£¸"),
			new HanZenSet2("9", "£¹"),
			new HanZenSet2(":", "¡§"),
			new HanZenSet2(";", "¡¨"),
			new HanZenSet2("<", "¡ã"),
			new HanZenSet2("=", "¡á"),
			new HanZenSet2(">", "¡ä"),
			new HanZenSet2("?", "¡©"),
			new HanZenSet2("@", "¡÷"),
			new HanZenSet2("A", "£Á"),
			new HanZenSet2("B", "£Â"),
			new HanZenSet2("C", "£Ã"),
			new HanZenSet2("D", "£Ä"),
			new HanZenSet2("E", "£Å"),
			new HanZenSet2("F", "£Æ"),
			new HanZenSet2("G", "£Ç"),
			new HanZenSet2("H", "£È"),
			new HanZenSet2("I", "£É"),
			new HanZenSet2("J", "£Ê"),
			new HanZenSet2("K", "£Ë"),
			new HanZenSet2("L", "£Ì"),
			new HanZenSet2("M", "£Í"),
			new HanZenSet2("N", "£Î"),
			new HanZenSet2("O", "£Ï"),
			new HanZenSet2("P", "£Ð"),
			new HanZenSet2("Q", "£Ñ"),
			new HanZenSet2("R", "£Ò"),
			new HanZenSet2("S", "£Ó"),
			new HanZenSet2("T", "£Ô"),
			new HanZenSet2("U", "£Õ"),
			new HanZenSet2("V", "£Ö"),
			new HanZenSet2("W", "£×"),
			new HanZenSet2("X", "£Ø"),
			new HanZenSet2("Y", "£Ù"),
			new HanZenSet2("Z", "£Ú"),
			new HanZenSet2("[", "¡Î"),
			new HanZenSet2("\\\\'", "¡ï"),
			new HanZenSet2("]", "¡Ï"),
			new HanZenSet2("^", "¡°"),
			new HanZenSet2("_", "¡²"),
			new HanZenSet2("`", "¡Æ"),
			new HanZenSet2("a", "£á"),
			new HanZenSet2("b", "£â"),
			new HanZenSet2("c", "£ã"),
			new HanZenSet2("d", "£ä"),
			new HanZenSet2("e", "£å"),
			new HanZenSet2("f", "£æ"),
			new HanZenSet2("g", "£ç"),
			new HanZenSet2("h", "£è"),
			new HanZenSet2("i", "£é"),
			new HanZenSet2("j", "£ê"),
			new HanZenSet2("k", "£ë"),
			new HanZenSet2("l", "£ì"),
			new HanZenSet2("m", "£í"),
			new HanZenSet2("n", "£î"),
			new HanZenSet2("o", "£ï"),
			new HanZenSet2("p", "£ð"),
			new HanZenSet2("q", "£ñ"),
			new HanZenSet2("r", "£ò"),
			new HanZenSet2("s", "£ó"),
			new HanZenSet2("t", "£ô"),
			new HanZenSet2("u", "£õ"),
			new HanZenSet2("v", "£ö"),
			new HanZenSet2("w", "£÷"),
			new HanZenSet2("x", "£ø"),
			new HanZenSet2("y", "£ù"),
			new HanZenSet2("z", "£ú"),
			new HanZenSet2("\\{", "¡Ð"),
			new HanZenSet2("\\|", "¡Ã"),
			new HanZenSet2("\\}", "¡Ñ"),
			new HanZenSet2("\\~", "¡Á"),
			new HanZenSet2("Ž¡", "¡£"),
			new HanZenSet2("Ž¢", "¡Ö"),
			new HanZenSet2("Ž£", "¡×"),
			new HanZenSet2("Ž¤", "¡¢"),
			new HanZenSet2("Ž¥", "¡¦"),
			new HanZenSet2("Ž¦", "¥ò"),
			new HanZenSet2("Ž§", "¥¡"),
			new HanZenSet2("Ž¨", "¥£"),
			new HanZenSet2("Ž©", "¥¥"),
			new HanZenSet2("Žª", "¥§"),
			new HanZenSet2("Ž«", "¥©"),
			new HanZenSet2("Ž¬", "¥ã"),
			new HanZenSet2("Ž­", "¥å"),
			new HanZenSet2("Ž®", "¥ç"),
			new HanZenSet2("Ž¯", "¥Ã"),
			new HanZenSet2("Ž°", "¡¼"),
			new HanZenSet2("Ž±", "¥¢"),
			new HanZenSet2("Ž²", "¥¤"),
			new HanZenSet2("Ž³", "¥¦"),
			new HanZenSet2("Ž´", "¥¨"),
			new HanZenSet2("Žµ", "¥ª"),
			new HanZenSet2("Ž¶", "¥«"),
			new HanZenSet2("Ž·", "¥­"),
			new HanZenSet2("Ž¸", "¥¯"),
			new HanZenSet2("Ž¹", "¥±"),
			new HanZenSet2("Žº", "¥³"),
			new HanZenSet2("Ž»", "¥µ"),
			new HanZenSet2("Ž¼", "¥·"),
			new HanZenSet2("Ž½", "¥¹"),
			new HanZenSet2("Ž¾", "¥»"),
			new HanZenSet2("Ž¿", "¥½"),
			new HanZenSet2("ŽÀ", "¥¿"),
			new HanZenSet2("ŽÁ", "¥Á"),
			new HanZenSet2("ŽÂ", "¥Ä"),
			new HanZenSet2("ŽÃ", "¥Æ"),
			new HanZenSet2("ŽÄ", "¥È"),
			new HanZenSet2("ŽÅ", "¥Ê"),
			new HanZenSet2("ŽÆ", "¥Ë"),
			new HanZenSet2("ŽÇ", "¥Ì"),
			new HanZenSet2("ŽÈ", "¥Í"),
			new HanZenSet2("ŽÉ", "¥Î"),
			new HanZenSet2("ŽÊ", "¥Ï"),
			new HanZenSet2("ŽË", "¥Ò"),
			new HanZenSet2("ŽÌ", "¥Õ"),
			new HanZenSet2("ŽÍ", "¥Ø"),
			new HanZenSet2("ŽÎ", "¥Û"),
			new HanZenSet2("ŽÏ", "¥Þ"),
			new HanZenSet2("ŽÐ", "¥ß"),
			new HanZenSet2("ŽÑ", "¥à"),
			new HanZenSet2("ŽÒ", "¥á"),
			new HanZenSet2("ŽÓ", "¥â"),
			new HanZenSet2("ŽÔ", "¥ä"),
			new HanZenSet2("ŽÕ", "¥æ"),
			new HanZenSet2("ŽÖ", "¥è"),
			new HanZenSet2("Ž×", "¥é"),
			new HanZenSet2("ŽØ", "¥ê"),
			new HanZenSet2("ŽÙ", "¥ë"),
			new HanZenSet2("ŽÚ", "¥ì"),
			new HanZenSet2("ŽÛ", "¥í"),
			new HanZenSet2("ŽÜ", "¥ï"),
			new HanZenSet2("ŽÝ", "¥ó"),
			new HanZenSet2("ŽÞ", "¡«"),
			new HanZenSet2("Žß", "¡¬"),
			new HanZenSet2("Ž¶ŽÞ", "¥¬"),
			new HanZenSet2("Ž·ŽÞ", "¥­"),
			new HanZenSet2("Ž·ŽÞ", "¥®"),
			new HanZenSet2("Ž¸ŽÞ", "¥°"),
			new HanZenSet2("Ž¹ŽÞ", "¥²"),
			new HanZenSet2("ŽºŽÞ", "¥´"),
			new HanZenSet2("Ž»ŽÞ", "¥¶"),
			new HanZenSet2("Ž¼ŽÞ", "¥¸"),
			new HanZenSet2("Ž½ŽÞ", "¥º"),
			new HanZenSet2("Ž¾ŽÞ", "¥¼"),
			new HanZenSet2("Ž¿ŽÞ", "¥¾"),
			new HanZenSet2("ŽÀŽÞ", "¥À"),
			new HanZenSet2("ŽÁŽÞ", "¥Â"),
			new HanZenSet2("Ž¯", "¥Ã"),
			new HanZenSet2("ŽÂŽÞ", "¥Å"),
			new HanZenSet2("ŽÃŽÞ", "¥Ç"),
			new HanZenSet2("ŽÄŽÞ", "¥É"),
			new HanZenSet2("ŽÊŽÞ", "¥Ð"),
			new HanZenSet2("ŽÊŽß", "¥Ñ"),
			new HanZenSet2("ŽËŽÞ", "¥Ó"),
			new HanZenSet2("ŽËŽß", "¥Ô"),
			new HanZenSet2("ŽÌŽÞ", "¥Ö"),
			new HanZenSet2("ŽÌŽß", "¥×"),
			new HanZenSet2("ŽÍŽÞ", "¥Ù"),
			new HanZenSet2("ŽÍŽß", "¥Ú"),
			new HanZenSet2("ŽÎŽÞ", "¥Ü"),
			new HanZenSet2("ŽÎŽß", "¥Ý"),
			new HanZenSet2("Ž¬", "¥ã"),
			new HanZenSet2("Ž­", "¥å"),
			new HanZenSet2("Ž®", "¥ç"),
			new HanZenSet2("¥î", "¥î"),
			new HanZenSet2("¥ô", "¥ô"),
			new HanZenSet2("¥õ", "¥õ"),
			new HanZenSet2("¥ö", "¥ö")};
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
