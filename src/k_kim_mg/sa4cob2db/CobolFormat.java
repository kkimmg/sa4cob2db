package k_kim_mg.sa4cob2db;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
/**
 * Format for Cobol
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CobolFormat extends DecimalFormat {
	/**
	 * index and character for replace
	 * 
	 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
	 */
	static class InsertChar {
		private int index;
		private char replace;
		/**
		 * Constructor
		 * 
		 * @param index index
		 * @param replace character
		 */
		public InsertChar(int index, char replace) {
			this.index = index;
			this.replace = replace;
		}
		/**
		 * get index
		 * 
		 * @return index
		 */
		public int getIndex() {
			return index;
		}
		/**
		 * set index
		 * 
		 * @param index index
		 */
		public void setIndex(int index) {
			this.index = index;
		}
		/**
		 * get character
		 * 
		 * @return character
		 */
		public char getReplace() {
			return replace;
		}
		/**
		 * set character
		 * 
		 * @param replace character
		 */
		public void setReplace(char replace) {
			this.replace = replace;
		}
	}
	/**
	 * pattern
	 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
	 */
	static class PatternFlags {
		private boolean db = false, cr = false, plus = false, minus = false, sla = false, bs = false, ast = false, z = false;
		private CobolColumn column;
		private String logicalPattern;
		private List<InsertChar> inserts = new ArrayList<InsertChar>();
		/**
		 * constructor
		 * 
		 * @param column column to format
		 * @param db DB
		 * @param cr CR
		 * @param plus +
		 * @param minus -
		 * @param sla /
		 * @param bs \
		 * @param ast *
		 * @param srp #
		 */
		public PatternFlags(CobolColumn column, boolean db, boolean cr, boolean plus, boolean minus, boolean sla, boolean bs, boolean ast, boolean srp) {
			this.column = column;
			this.db = db;
			this.cr = cr;
			this.plus = plus;
			this.minus = minus;
			this.sla = sla;
			this.bs = bs;
			this.ast = ast;
			this.z = srp;
			parsePicture();
		}
		/**
		 * parse picture statement
		 */
		public void parsePicture() {
			String text = column.getFormat();
			StringBuilder output = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				char c = text.toUpperCase().charAt(i);
				if (c == '9' || c == '0') {
					output.append('0');
				} else if (c == '.' || c == ',') {
					output.append(c);
				} else if (c == '/') {
					output.append(',');
					inserts.add(new InsertChar(i, c));
				} else if (c == '*' || c == '\\') {
					output.append('#');
					inserts.add(new InsertChar(i, c));
				} else if (c == 'Z' || c == '#') {
					output.append('#');
				} else if (c == '-' || c == '+') {
					output.append('#');
					// } else {
					// output.append(c);
				}
			}
			logicalPattern = output.toString();
		}
		/**
		 * DB
		 * 
		 * @return has DB true/false
		 */
		public boolean isDb() {
			return db;
		}
		public void setDb(boolean db) {
			this.db = db;
		}
		public boolean isCr() {
			return cr;
		}
		public void setCr(boolean cr) {
			this.cr = cr;
		}
		public boolean isPlus() {
			return plus;
		}
		public void setPlus(boolean plus) {
			this.plus = plus;
		}
		public boolean isMinus() {
			return minus;
		}
		public void setMinus(boolean minus) {
			this.minus = minus;
		}
		public boolean isSla() {
			return sla;
		}
		public void setSla(boolean sla) {
			this.sla = sla;
		}
		public boolean isBs() {
			return bs;
		}
		public void setBs(boolean bs) {
			this.bs = bs;
		}
		public boolean isAst() {
			return ast;
		}
		public void setAst(boolean ast) {
			this.ast = ast;
		}
		public CobolColumn getColumn() {
			return column;
		}
		public String getOriginalPattern() {
			return column.getFormat();
		}
		public String getLogicalPattern() {
			return logicalPattern;
		}
		public boolean isSrp() {
			return z;
		}
		public void setSrp(boolean srp) {
			this.z = srp;
		}
		public List<InsertChar> getInserts() {
			return inserts;
		}
	}
	/**
	 * create format
	 * @param column column
	 * @return format
	 */
	public static NumberFormat createFormatter(CobolColumn column) {
		String pattern = column.getFormat();
		boolean l_db = false, l_cr = false, l_plus = false, l_minus = false, l_sla = false, l_bs = false, l_ast = false, l_z = false;
		;
		NumberFormat df = null;
		if (pattern.toUpperCase().contains("DB")) {
			l_db = true;
		}
		if (pattern.toUpperCase().contains("CR")) {
			l_cr = true;
		}
		if (pattern.toUpperCase().contains("+")) {
			l_plus = true;
		}
		if (pattern.toUpperCase().contains("-")) {
			l_minus = true;
		}
		if (pattern.toUpperCase().contains("/")) {
			l_sla = true;
		}
		if (pattern.toUpperCase().contains("\\")) {
			l_bs = true;
		}
		if (pattern.toUpperCase().contains("*")) {
			l_ast = true;
		}
		if (pattern.toUpperCase().contains("Z")) {
			l_z = true;
		}
		if (pattern.toUpperCase().contains("#")) {
			l_z = true;
		}
		if (l_db | l_cr | l_plus | l_minus | l_sla | l_bs | l_ast | l_z) {
			PatternFlags flags = new PatternFlags(column, l_db, l_cr, l_plus, l_minus, l_sla, l_bs, l_ast, l_z);
			df = new CobolFormat(flags);
		} else {
			df = new DecimalFormat(pattern);
		}
		return df;
	}
	private static final long serialVersionUID = 1L;
	private PatternFlags flags;
	/**
	 * CobolFormat
	 * 
	 * @param format format
	 */
	public CobolFormat(PatternFlags flags) {
		super(flags.getLogicalPattern());
		// System.err.println("CobolFormat.new" + flags.getOriginalPattern() +
		// ":" + flags.getLogicalPattern());
		this.flags = flags;
		if (flags.isDb()) {
			setNegativeSuffix("DB");
			setPositiveSuffix("  ");
			setNegativePrefix("");
		}
		if (flags.isCr()) {
			setNegativeSuffix("CR");
			setPositiveSuffix("  ");
			setNegativePrefix("");
		}
		if (flags.isPlus()) {
			setPositivePrefix("+");
			setNegativePrefix("-");
		}
		if (flags.isMinus()) {
			setPositivePrefix("");
			setNegativePrefix("-");
		}
	}
	@Override
	public Number parse(String text) throws ParseException {
		StringBuffer output = new StringBuffer(text.length());
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '/' || c == '*' || c == '\\') {
				// do nothing
			} else if (c == ' ') {
				if (flags.isDb() || flags.isCr()) {
					if (i >= text.length() - 2) {
						output.append(' ');
					}
				}
			} else {
				output.append(c);
			}
		}
		return super.parse(output.toString());
	}
	@Override
	public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
		StringBuffer ret = super.format(number, result, fieldPosition);
		replaceChars(ret, flags);
		return ret;
	}
	/**
	 * replace ' ' to '/*\\'
	 * @param ret StringBuffer
	 * @param flags pattern
	 * @return StringBuffer
	 */
	StringBuffer replaceChars(StringBuffer ret, PatternFlags flags) {
		int off = flags.getColumn().getLength() - ret.length();
		for (int i = 0; i < off; i++) {
			ret.insert(0, ' ');
		}
		for (InsertChar ins : flags.getInserts()) {
			int i = ins.getIndex();
			char c = ret.charAt(ins.getIndex());
			char r = ins.getReplace();
			switch (r) {
			case '/':
				ret.setCharAt(i, '/');
				break;
			case '*':
				if (c == ' ') {
					ret.setCharAt(i, '*');
				}
				break;
			case '\\':
				if (c == ' ') {
					ret.setCharAt(i, '\\');
				}
				break;
			}
		}
		return ret;
	}
	@Override
	public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
		StringBuffer ret = super.format(number, result, fieldPosition);
		replaceChars(ret, flags);
		return ret;
	}
}
