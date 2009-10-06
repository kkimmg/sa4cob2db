import java.sql.ResultSet;
import java.sql.SQLException;

import k_kim_mg.sa4cob2db.CobolRecord;
import k_kim_mg.sa4cob2db.CobolRecordException;
import k_kim_mg.sa4cob2db.CobolRecordMetaData;
import k_kim_mg.sa4cob2db.sql.SQLCobolColumn;
import k_kim_mg.sa4cob2db.sql.SQLCobolRecordMetaData;

/**
 * ���ܸ�ʸ�����Ⱦ�Ѣ��������Ѵ��ѥ桼�ƥ���ƥ����饹
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 * @version 1.0
 */
public class HankakuZenkaku extends SQLCobolColumn {
	public HankakuZenkaku(CobolRecordMetaData meta) {
	    super((SQLCobolRecordMetaData)meta);
    }
	/**	Ⱦ�Ѥ����ѤΥ��å� */
	private static class HanZenSet {
		/**Ⱦ��ʸ��*/
		private char hankaku;
		/**����ʸ��*/
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
	/**	Ⱦ�Ѥ����ѤΥ��å� */
	private static class HanZenSet2 {
		/**Ⱦ��ʸ��*/
		private String hankaku;
		/**����ʸ��*/
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
	/**	�Ѵ���ʸ���󥻥å� */
	private static final HanZenSet[] MOJISET =
		new HanZenSet[] {
			new HanZenSet('!', '��'),
			new HanZenSet('"', '��'),
			new HanZenSet('#', '��'),
			new HanZenSet('$', '��'),
			new HanZenSet('%', '��'),
			new HanZenSet('&', '��'),
			new HanZenSet('\'', '��'),
			new HanZenSet('(', '��'),
			new HanZenSet(')', '��'),
			new HanZenSet('*', '��'),
			new HanZenSet('+', '��'),
			new HanZenSet(',', '��'),
			new HanZenSet('-', '��'),
			new HanZenSet('.', '��'),
			new HanZenSet('/', '��'),
			new HanZenSet('0', '��'),
			new HanZenSet('1', '��'),
			new HanZenSet('2', '��'),
			new HanZenSet('3', '��'),
			new HanZenSet('4', '��'),
			new HanZenSet('5', '��'),
			new HanZenSet('6', '��'),
			new HanZenSet('7', '��'),
			new HanZenSet('8', '��'),
			new HanZenSet('9', '��'),
			new HanZenSet(':', '��'),
			new HanZenSet(';', '��'),
			new HanZenSet('<', '��'),
			new HanZenSet('=', '��'),
			new HanZenSet('>', '��'),
			new HanZenSet('?', '��'),
			new HanZenSet('@', '��'),
			new HanZenSet('A', '��'),
			new HanZenSet('B', '��'),
			new HanZenSet('C', '��'),
			new HanZenSet('D', '��'),
			new HanZenSet('E', '��'),
			new HanZenSet('F', '��'),
			new HanZenSet('G', '��'),
			new HanZenSet('H', '��'),
			new HanZenSet('I', '��'),
			new HanZenSet('J', '��'),
			new HanZenSet('K', '��'),
			new HanZenSet('L', '��'),
			new HanZenSet('M', '��'),
			new HanZenSet('N', '��'),
			new HanZenSet('O', '��'),
			new HanZenSet('P', '��'),
			new HanZenSet('Q', '��'),
			new HanZenSet('R', '��'),
			new HanZenSet('S', '��'),
			new HanZenSet('T', '��'),
			new HanZenSet('U', '��'),
			new HanZenSet('V', '��'),
			new HanZenSet('W', '��'),
			new HanZenSet('X', '��'),
			new HanZenSet('Y', '��'),
			new HanZenSet('Z', '��'),
			new HanZenSet('[', '��'),
			new HanZenSet('\\', '��'),
			new HanZenSet(']', '��'),
			new HanZenSet('^', '��'),
			new HanZenSet('_', '��'),
			new HanZenSet('`', '��'),
			new HanZenSet('a', '��'),
			new HanZenSet('b', '��'),
			new HanZenSet('c', '��'),
			new HanZenSet('d', '��'),
			new HanZenSet('e', '��'),
			new HanZenSet('f', '��'),
			new HanZenSet('g', '��'),
			new HanZenSet('h', '��'),
			new HanZenSet('i', '��'),
			new HanZenSet('j', '��'),
			new HanZenSet('k', '��'),
			new HanZenSet('l', '��'),
			new HanZenSet('m', '��'),
			new HanZenSet('n', '��'),
			new HanZenSet('o', '��'),
			new HanZenSet('p', '��'),
			new HanZenSet('q', '��'),
			new HanZenSet('r', '��'),
			new HanZenSet('s', '��'),
			new HanZenSet('t', '��'),
			new HanZenSet('u', '��'),
			new HanZenSet('v', '��'),
			new HanZenSet('w', '��'),
			new HanZenSet('x', '��'),
			new HanZenSet('y', '��'),
			new HanZenSet('z', '��'),
			new HanZenSet('{', '��'),
			new HanZenSet('|', '��'),
			new HanZenSet('}', '��'),
			new HanZenSet('~', '��'),
			new HanZenSet('?', '?'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��'),
			new HanZenSet('��', '��')};
	/**	�Ѵ���ʸ���󥻥å� */
	private static final HanZenSet2[] MOJISET2 =
		new HanZenSet2[] {
			new HanZenSet2(" ", "��"),
			new HanZenSet2("!", "��"),
			new HanZenSet2("\"", "��"),
			new HanZenSet2("#", "��"),
			new HanZenSet2("\\$", "��"),
			new HanZenSet2("%", "��"),
			new HanZenSet2("&", "��"),
			new HanZenSet2("'", "��"),
			new HanZenSet2("\\(", "��"),
			new HanZenSet2("\\)", "��"),
			new HanZenSet2("\\*", "��"),
			new HanZenSet2("\\+", "��"),
			new HanZenSet2(",", "��"),
			new HanZenSet2("-", "��"),
			new HanZenSet2("\\.", "��"),
			new HanZenSet2("/", "��"),
			new HanZenSet2("0", "��"),
			new HanZenSet2("1", "��"),
			new HanZenSet2("2", "��"),
			new HanZenSet2("3", "��"),
			new HanZenSet2("4", "��"),
			new HanZenSet2("5", "��"),
			new HanZenSet2("6", "��"),
			new HanZenSet2("7", "��"),
			new HanZenSet2("8", "��"),
			new HanZenSet2("9", "��"),
			new HanZenSet2(":", "��"),
			new HanZenSet2(";", "��"),
			new HanZenSet2("<", "��"),
			new HanZenSet2("=", "��"),
			new HanZenSet2(">", "��"),
			new HanZenSet2("\\?", "��"),
			new HanZenSet2("\\@", "��"),
			new HanZenSet2("A", "��"),
			new HanZenSet2("B", "��"),
			new HanZenSet2("C", "��"),
			new HanZenSet2("D", "��"),
			new HanZenSet2("E", "��"),
			new HanZenSet2("F", "��"),
			new HanZenSet2("G", "��"),
			new HanZenSet2("H", "��"),
			new HanZenSet2("I", "��"),
			new HanZenSet2("J", "��"),
			new HanZenSet2("K", "��"),
			new HanZenSet2("L", "��"),
			new HanZenSet2("M", "��"),
			new HanZenSet2("N", "��"),
			new HanZenSet2("O", "��"),
			new HanZenSet2("P", "��"),
			new HanZenSet2("Q", "��"),
			new HanZenSet2("R", "��"),
			new HanZenSet2("S", "��"),
			new HanZenSet2("T", "��"),
			new HanZenSet2("U", "��"),
			new HanZenSet2("V", "��"),
			new HanZenSet2("W", "��"),
			new HanZenSet2("X", "��"),
			new HanZenSet2("Y", "��"),
			new HanZenSet2("Z", "��"),
			new HanZenSet2("\\[", "��"),
			new HanZenSet2("\\?", "��"),
			new HanZenSet2("\\]", "��"),
			new HanZenSet2("\\^", "��"),
			new HanZenSet2("_", "��"),
			new HanZenSet2("`", "��"),
			new HanZenSet2("a", "��"),
			new HanZenSet2("b", "��"),
			new HanZenSet2("c", "��"),
			new HanZenSet2("d", "��"),
			new HanZenSet2("e", "��"),
			new HanZenSet2("f", "��"),
			new HanZenSet2("g", "��"),
			new HanZenSet2("h", "��"),
			new HanZenSet2("i", "��"),
			new HanZenSet2("j", "��"),
			new HanZenSet2("k", "��"),
			new HanZenSet2("l", "��"),
			new HanZenSet2("m", "��"),
			new HanZenSet2("n", "��"),
			new HanZenSet2("o", "��"),
			new HanZenSet2("p", "��"),
			new HanZenSet2("q", "��"),
			new HanZenSet2("r", "��"),
			new HanZenSet2("s", "��"),
			new HanZenSet2("t", "��"),
			new HanZenSet2("u", "��"),
			new HanZenSet2("v", "��"),
			new HanZenSet2("w", "��"),
			new HanZenSet2("x", "��"),
			new HanZenSet2("y", "��"),
			new HanZenSet2("z", "��"),
			new HanZenSet2("\\{", "��"),
			new HanZenSet2("\\|", "��"),
			new HanZenSet2("\\}", "��"),
			new HanZenSet2("\\~", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("�Î�", "��"),
			new HanZenSet2("�Ď�", "��"),
			new HanZenSet2("�ʎ�", "��"),
			new HanZenSet2("�ʎ�", "��"),
			new HanZenSet2("�ˎ�", "��"),
			new HanZenSet2("�ˎ�", "��"),
			new HanZenSet2("�̎�", "��"),
			new HanZenSet2("�̎�", "��"),
			new HanZenSet2("�͎�", "��"),
			new HanZenSet2("�͎�", "��"),
			new HanZenSet2("�Ύ�", "��"),
			new HanZenSet2("�Ύ�", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��")};
	/**	�Ѵ���ʸ���󥻥å� */
	private static final HanZenSet2[] MOJISET3 =
		new HanZenSet2[] {
			new HanZenSet2(" ", "��"),
			new HanZenSet2("!", "��"),
			new HanZenSet2("\"", "��"),
			new HanZenSet2("#", "��"),
			new HanZenSet2("$", "��"),
			new HanZenSet2("%", "��"),
			new HanZenSet2("&", "��"),
			new HanZenSet2("'", "��"),
			new HanZenSet2("(", "��"),
			new HanZenSet2(")", "��"),
			new HanZenSet2("*", "��"),
			new HanZenSet2("+", "��"),
			new HanZenSet2(",", "��"),
			new HanZenSet2("-", "��"),
			new HanZenSet2(".", "��"),
			new HanZenSet2("/", "��"),
			new HanZenSet2("0", "��"),
			new HanZenSet2("1", "��"),
			new HanZenSet2("2", "��"),
			new HanZenSet2("3", "��"),
			new HanZenSet2("4", "��"),
			new HanZenSet2("5", "��"),
			new HanZenSet2("6", "��"),
			new HanZenSet2("7", "��"),
			new HanZenSet2("8", "��"),
			new HanZenSet2("9", "��"),
			new HanZenSet2(":", "��"),
			new HanZenSet2(";", "��"),
			new HanZenSet2("<", "��"),
			new HanZenSet2("=", "��"),
			new HanZenSet2(">", "��"),
			new HanZenSet2("?", "��"),
			new HanZenSet2("@", "��"),
			new HanZenSet2("A", "��"),
			new HanZenSet2("B", "��"),
			new HanZenSet2("C", "��"),
			new HanZenSet2("D", "��"),
			new HanZenSet2("E", "��"),
			new HanZenSet2("F", "��"),
			new HanZenSet2("G", "��"),
			new HanZenSet2("H", "��"),
			new HanZenSet2("I", "��"),
			new HanZenSet2("J", "��"),
			new HanZenSet2("K", "��"),
			new HanZenSet2("L", "��"),
			new HanZenSet2("M", "��"),
			new HanZenSet2("N", "��"),
			new HanZenSet2("O", "��"),
			new HanZenSet2("P", "��"),
			new HanZenSet2("Q", "��"),
			new HanZenSet2("R", "��"),
			new HanZenSet2("S", "��"),
			new HanZenSet2("T", "��"),
			new HanZenSet2("U", "��"),
			new HanZenSet2("V", "��"),
			new HanZenSet2("W", "��"),
			new HanZenSet2("X", "��"),
			new HanZenSet2("Y", "��"),
			new HanZenSet2("Z", "��"),
			new HanZenSet2("[", "��"),
			new HanZenSet2("\\\\'", "��"),
			new HanZenSet2("]", "��"),
			new HanZenSet2("^", "��"),
			new HanZenSet2("_", "��"),
			new HanZenSet2("`", "��"),
			new HanZenSet2("a", "��"),
			new HanZenSet2("b", "��"),
			new HanZenSet2("c", "��"),
			new HanZenSet2("d", "��"),
			new HanZenSet2("e", "��"),
			new HanZenSet2("f", "��"),
			new HanZenSet2("g", "��"),
			new HanZenSet2("h", "��"),
			new HanZenSet2("i", "��"),
			new HanZenSet2("j", "��"),
			new HanZenSet2("k", "��"),
			new HanZenSet2("l", "��"),
			new HanZenSet2("m", "��"),
			new HanZenSet2("n", "��"),
			new HanZenSet2("o", "��"),
			new HanZenSet2("p", "��"),
			new HanZenSet2("q", "��"),
			new HanZenSet2("r", "��"),
			new HanZenSet2("s", "��"),
			new HanZenSet2("t", "��"),
			new HanZenSet2("u", "��"),
			new HanZenSet2("v", "��"),
			new HanZenSet2("w", "��"),
			new HanZenSet2("x", "��"),
			new HanZenSet2("y", "��"),
			new HanZenSet2("z", "��"),
			new HanZenSet2("\\{", "��"),
			new HanZenSet2("\\|", "��"),
			new HanZenSet2("\\}", "��"),
			new HanZenSet2("\\~", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("����", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("�Î�", "��"),
			new HanZenSet2("�Ď�", "��"),
			new HanZenSet2("�ʎ�", "��"),
			new HanZenSet2("�ʎ�", "��"),
			new HanZenSet2("�ˎ�", "��"),
			new HanZenSet2("�ˎ�", "��"),
			new HanZenSet2("�̎�", "��"),
			new HanZenSet2("�̎�", "��"),
			new HanZenSet2("�͎�", "��"),
			new HanZenSet2("�͎�", "��"),
			new HanZenSet2("�Ύ�", "��"),
			new HanZenSet2("�Ύ�", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��"),
			new HanZenSet2("��", "��")};
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
