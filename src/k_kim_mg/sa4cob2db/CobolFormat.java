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
		boolean db = false, cr = false, plus = false, minus = false, sla = false, bs = false, ast = false;
		private CobolColumn column;
		String logicalPattern;
		public PatternFlags(CobolColumn column, boolean db, boolean cr, boolean plus, boolean minus, boolean sla, boolean bs, boolean ast) {
			this.column = column;
			this.db = db;
			this.cr = cr;
			this.plus = plus;
			this.minus = minus;
			this.sla = sla;
			this.bs = bs;
			this.ast = ast;
			parse();
		}
		private void parse() {
			StringBuilder builder = new StringBuilder(column.getLength());
			for (int i = 0; i < column.getLength(); i++) {
				builder.append("#");
			}
			logicalPattern = builder.toString();
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
		public String getOriginalPattern() {
			return column.getFormat();
		}
		public String getLogicalPattern() {
			return logicalPattern;
		}
	}
	public static NumberFormat createFormatter(CobolColumn column) {
		String pattern = column.getFormat();
		boolean l_db = false, l_cr = false, l_plus = false, l_minus = false, l_sla = false, l_bs = false, l_ast = false;
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
		if (l_db | l_cr | l_plus | l_minus | l_sla | l_bs | l_ast) {
			PatternFlags flags = new PatternFlags(column, l_db, l_cr, l_plus, l_minus, l_sla, l_bs, l_ast);
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
		this.flags = flags;
	}
	@Override
	public Number parse(String arg0) throws ParseException {
		// TODO Auto-generated method stub
		return super.parse(arg0);
	}
	@Override
	public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
		// TODO Auto-generated method stub
		return super.format(number, result, fieldPosition);
	}
	@Override
	public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
		// TODO Auto-generated method stub
		return super.format(number, result, fieldPosition);
	}
}
