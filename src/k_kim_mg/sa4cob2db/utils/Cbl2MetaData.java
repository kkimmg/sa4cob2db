/*
 * Created on 2004/05/23 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.utils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import k_kim_mg.sa4cob2db.codegen.CobolConsts;
/**
 * PreProcessing COBOL file access to call statement.
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class Cbl2MetaData{
	private InputStream input;
	private boolean infreeformat = false;
	private boolean expandCopy = false;
	/** Environment value name of Charset. */
	public static final String ACM_CHARSET = "acm_pp_charset";
	/** Generator */
	private RecordGenerator generator = new RecordGenerator();
	/**
	 * main
	 * 
	 * @param argv filename and other's
	 */
	public static void main(String[] argv) {
		Cbl2MetaData cobpp = new Cbl2MetaData(argv);
		cobpp.run();
		System.exit(0);
	}
	/**
	 * main to...
	 * 
	 * @param infile input filename
	 * @param outfile output filename
	 * @param informat format "fix" or "free"
	 * @param outformat format "fix" or "free"
	 * @param initrow first row number default 10
	 * @param increase increase row number default 10
	 * @param acmconsts_file filename of "ACMCONSTS.CBL"
	 * @param expand_copy expand copy statement default false
	 * @param codegeneratorlisteners Event listener class name of generator
	 * @param customcodegeneratorclass custom generator class name if you use
	 * @param s_subprogram true if processing program is subprogram
	 * @param acm_charset
	 */
	public static void main_too(String infile, String outfile, String informat, String outformat, String initrow, String increase, String acmconsts_file, String expand_copy, String codegeneratorlisteners, String customcodegeneratorclass,
			String acm_charset, String s_subprogram) {
		String[] argv = new String[] { infile, outfile };
		if (informat.trim().length() > 0) {
			System.setProperty("informat", informat.trim());
		}
		if (outformat.trim().length() > 0) {
			System.setProperty("outformat", outformat.trim());
		}
		if (initrow.trim().length() > 0) {
			System.setProperty("initrow", initrow.trim());
		}
		if (increase.trim().length() > 0) {
			System.setProperty("increase", increase.trim());
		}
		if (acmconsts_file.trim().length() > 0) {
			System.setProperty("acmconsts_file", acmconsts_file.trim());
		}
		if (expand_copy.trim().length() > 0) {
			System.setProperty("expand_copy", expand_copy.trim());
		}
		if (codegeneratorlisteners.trim().length() > 0) {
			System.setProperty("codegeneratorlisteners", codegeneratorlisteners.trim());
		}
		if (customcodegeneratorclass.trim().length() > 0) {
			System.setProperty("customcodegeneratorclass", customcodegeneratorclass.trim());
		}
		if (acm_charset.trim().length() > 0) {
			System.setProperty(ACM_CHARSET, acm_charset.trim());
		}
		if (s_subprogram.trim().length() > 0) {
			System.setProperty("subprogram", s_subprogram.trim());
		}
		System.setProperty("display_usage", "false");
		main(argv);
	}
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
		if (infile == "") {
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
		String outfmttext = getEnvValue("outformat", infmttext);
		// const file
		String acmconsts_file = getEnvValue("acmconsts_file", CobolConsts.ACMCONSTS_FILE);
		if (acmconsts_file.trim().length() > 0) {
			CobolConsts.ACMCONSTS_FILE = acmconsts_file.trim();
		}
		// expand copy
		String expandStr = getEnvValue("expand_copy", "false");
		expandCopy = Boolean.parseBoolean(expandStr);
		// usage
		String usageStr = getEnvValue("display_usage", "true");
		if (Boolean.parseBoolean(usageStr)) {
			System.err.println("usage:java -cp path_to_jar \"k_kim_mg.sa4cob2db.codegen.COBPP2\" $1 $2");
			System.err.println("\t$1=" + infile);
			System.err.println("\t$2=" + outfile);
			System.err.println("\tinformat=" + infmttext + "\t:fix or free");
			System.err.println("\toutformat=" + outfmttext + "\t:fix or free");
			System.err.println("\tacmconsts_file=" + acmconsts_file);
			System.err.println("\texpand_copy=" + expandStr + "\t:true or false or space");
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.GeneratorOwner#generate(java.lang
	 * .String)
	 */
	public void generate(String text) {
		// TODO
	}
	/**
	 * Run
	 */
	public void run() {
		try {
			InputStreamReader isr = null;
			String csn = getEnvValue(ACM_CHARSET, "").trim();
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
	/**
	 * expand copy?
	 * 
	 * @return true expand</br> false don't
	 */
	public boolean isExpandCopy() {
		return expandCopy;
	}
	/**
	 * set expand copy?
	 * 
	 * @param expandCopy true expand</br> false don't
	 */
	public void setExpandCopy(boolean expandCopy) {
		this.expandCopy = expandCopy;
	}
}
