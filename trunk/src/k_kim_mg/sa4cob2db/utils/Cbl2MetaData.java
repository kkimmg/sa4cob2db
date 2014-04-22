package k_kim_mg.sa4cob2db.utils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import k_kim_mg.sa4cob2db.codegen.COBPP1;
/**
 * generate meta data file from COBOL source.
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class Cbl2MetaData {
	/**
	 * main
	 * 
	 * @param argv filename and other's
	 */
	public static void main(String[] argv) {
		// /////////////////
		Cbl2MetaData cobpp = new Cbl2MetaData(argv);
		cobpp.run();
		System.exit(0);
	}
	/**
	 * main to...
	 * 
	 * @param infile input filename
	 * @param outfile output filename
	 * @param informat COBOL source is free format?
	 * @param acm_charset
	 */
	public static void main_too(String infile, String outfile, String informat, String acm_charset) {
		String[] argv = new String[] { infile, outfile };
		if (informat.trim().length() > 0) {
			System.setProperty("informat", informat.trim());
		}
		if (acm_charset.trim().length() > 0) {
			System.setProperty(COBPP1.ACM_CHARSET, acm_charset.trim());
		}
		System.setProperty("display_usage", "false");
		main(argv);
	}
	private RecordGenerator generator = new RecordGenerator();
	private boolean infreeformat = false;
	private InputStream input;
	private String outfile;
	/**
	 * Constructor
	 * 
	 * @param argv argument values
	 */
	public Cbl2MetaData(String[] argv) {
		String csn = "";
		// input file
		String infile = (argv.length > 0 ? argv[0].trim() : "");
		if (infile.trim().length() <= 0) {
			input = System.in;
		} else {
			try {
				input = new FileInputStream(infile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				input = System.in;
			}
		}
		// output file
		outfile = (argv.length > 1 ? argv[1].trim() : "");
		// format
		String infmttext = getEnvValue("informat", "fix");
		if (infmttext.compareToIgnoreCase("free") == 0) {
			infreeformat = true;
		}
		// usage
		String usageStr = getEnvValue("display_usage", "true");
		if (Boolean.parseBoolean(usageStr)) {
			System.err.println("usage:java -cp path_to_jar " + getClass().getName() + " $1 $2");
			System.err.println("\t$1=" + infile);
			System.err.println("\t$2=" + outfile);
			System.err.println("\tinformat=" + infmttext + "\t:fix or free");
			System.err.println("\tdisplay_usage=" + usageStr + "\t:true or false or space");
			System.err.println("\tacm_pp_charset=" + csn + "\t:chaset encodeing");
		}
	}
	/**
	 * get environment values
	 * 
	 * @param key key
	 * @param defaultValue default value
	 * @return value
	 */
	private String getEnvValue(String key, String defaultValue) {
		String ret = System.getProperty(key, System.getenv(key));
		if (ret == null)
			ret = defaultValue;
		if (ret.length() == 0)
			ret = defaultValue;
		return ret;
	}
	/**
	 * Run
	 */
	public void run() {
		try {
			InputStreamReader isr = null;
			String csn = getEnvValue(COBPP1.ACM_CHARSET, "").trim();
			if (csn.length() == 0) {
				isr = new InputStreamReader(input);
			} else {
				try {
					isr = new InputStreamReader(input, csn);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					isr = new InputStreamReader(input);
				}
			}
			BufferedReader br = new BufferedReader(isr);
			String row = br.readLine();
			while (row != null) {
				String text = row;
				if (!infreeformat) {
					text = row.substring(6, (row.length() > 73 ? 73 : row.length()));
				}
				generator.parse(text);
				row = br.readLine();
			}
			generator.flush();
			generator.exportToNode(outfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
