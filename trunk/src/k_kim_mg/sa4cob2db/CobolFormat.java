package k_kim_mg.sa4cob2db;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
/**
 * Format for Cobol
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class CobolFormat extends DecimalFormat {
	protected static class PatternFlags {
		boolean db = false, cr = false, plus = false, minus = false, sla = false, bs = false, ast = false, z = false;
		private CobolColumn column;
		private String logicalPattern;
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
		 * 
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
				} else if (c == '*' || c == '\\') {
					output.append('#');
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
	}
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
		System.err.println("CobolFormat.new" + flags.getOriginalPattern() + ":" + flags.getLogicalPattern());
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
					if (i >= text.length() -2) {
						output.append(' ');
					}
				}
			} else {
				output.append(c);
			}
		}
		System.err.println("CobolFormat.parse(" + flags.getColumn().getName() + ")" + text + ":" + flags.getOriginalPattern() + ":" + flags.getLogicalPattern());
		return super.parse(output.toString());
	}
	@Override
	public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
		// TODO Auto-generated method stub
		StringBuffer ret = super.format(number, result, fieldPosition);
		int off = flags.getColumn().getLength() - ret.length();
		for (int i = 0; i < off; i++) {
			ret.insert(0, ' ');
		}
		return ret;
	}
	@Override
	public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
		// TODO Auto-generated method stub
		StringBuffer ret = super.format(number, result, fieldPosition);
		int off = flags.getColumn().getLength() - ret.length();
		for (int i = 0; i < off; i++) {
			ret.insert(0, ' ');
		}
		return ret;
	}
}
