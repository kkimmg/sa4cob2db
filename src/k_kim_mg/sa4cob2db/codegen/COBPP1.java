/*
 * Created on 2004/05/23 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package k_kim_mg.sa4cob2db.codegen;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
/**
 * PreProcessing COBOL file access to call statement.
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class COBPP1 implements GeneratorOwner {
	/**
	 * Copy Statement
	 * 
	 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
	 */
	static class CopyInfo {
		private ArrayList<String> statement;
		private String filename;
		private Properties replacing = new Properties();
		/**
		 * Constructor
		 * 
		 * @param Statement Copy Statements
		 */
		public CopyInfo(ArrayList<String> Statement) {
			statement = Statement;
			parse();
		}
		/**
		 * filename
		 * 
		 * @return filename
		 */
		public String getFilename() {
			return filename;
		}
		/**
		 * Replaced Statement
		 * 
		 * @param target original statement
		 * @return replaced statement
		 */
		public String getReplacedStatement(String target) {
			String ret = target;
			Set<String> keys = replacing.stringPropertyNames();
			Iterator<String> ite = keys.iterator();
			while (ite.hasNext()) {
				String key = ite.next();
				String val = replacing.getProperty(key);
				ret = ret.replaceAll(key, val);
			}
			return ret;
		}
		private void parse() {
			for (int i = 0; i < statement.size(); i++) {
				String row = statement.get(i);
				StringTokenizer tokenizer = new StringTokenizer(row, " '=\t\n\r\f\"");
				String prev = "";
				String key = "";
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken().trim();
					System.err.println(token);
					if (prev.equalsIgnoreCase("COPY")) {
						filename = token;
						System.err.println("filename=" + filename);
					} else if (prev.equalsIgnoreCase("REPLACING")) {
						key = token;
					} else if (prev.equalsIgnoreCase("BY")) {
						replacing.setProperty(key, token);
					}
					prev = token;
				}
			}
		}
	}
	/**
	 * Expand Copy Statement
	 * 
	 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
	 */
	class CopyProcesser {
		private CopyInfo info;
		private FileInputStream input2;
		/**
		 * Constructor
		 * 
		 * @param Info of copy statement
		 * @throws IOException when file not found.
		 */
		public CopyProcesser(CopyInfo Info) throws IOException {
			info = Info;
			input2 = new FileInputStream(info.getFilename());
		}
		/**
		 * @throws IOException when something wrong
		 */
		public void run() throws IOException {
			InputStreamReader isr = new InputStreamReader(input2);
			BufferedReader br = new BufferedReader(isr);
			String row = br.readLine();
			while (row != null) {
				String text = row;
				if (!infreeformat) {
					text = row.substring(6);
				}
				generator.parse(info.getReplacedStatement(text));
				row = br.readLine();
			}
			generator.flush();
		}
	}
	/**
	 * main
	 * 
	 * @param argv filename and other's
	 */
	public static void main(String[] argv) {
		COBPP1 cobpp = new COBPP1(argv);
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
	 * @param s_dontcomment true then dont output original as comment.
	 * @param acm_charset
	 */
	public static void main_too(String infile, String outfile, String informat, String outformat, String initrow, String increase, String acmconsts_file, String expand_copy, String codegeneratorlisteners, String customcodegeneratorclass,
			String acm_charset, String s_subprogram, String s_dontcomment) {
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
		if (s_dontcomment.trim().length() > 0) {
			System.setProperty("dontcomment", s_dontcomment.trim());
		}
		System.setProperty("display_usage", "false");
		main(argv);
	}
	private DecimalFormat decimal;
	private InputStream input;
	private PrintStream output;
	private boolean outfreeformat = false;
	private boolean infreeformat = false;
	private int initrownum = 10;
	private int incrrownum = 10;
	private boolean expandCopy = false;
	private boolean subprogram = false;
	private boolean dontcomment = false;
	/** Environment value name of Charset. */
	public static final String ACM_CHARSET = "acm_pp_charset";
	/** Generator */
	private CodeGenerator generator = new TCPCodeGenerator(this);
	/**
	 * Constructor
	 * 
	 * @param argv argument values
	 */
	public COBPP1(String[] argv) {
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
		String outfile = (argv.length > 1 ? argv[1].trim() : "");
		if (outfile.length() == 0) {
			output = System.out;
		} else {
			try {
				csn = getEnvValue(ACM_CHARSET, "").trim();
				if (csn.length() == 0) {
					output = new PrintStream(outfile);
				} else {
					try {
						output = new PrintStream(outfile, csn);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						output = new PrintStream(outfile);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				output = System.out;
			}
		}
		// format
		String infmttext = getEnvValue("informat", "fix");
		if (infmttext.compareToIgnoreCase("free") == 0) {
			infreeformat = true;
		}
		String outfmttext = getEnvValue("outformat", infmttext);
		if (outfmttext.compareToIgnoreCase("free") == 0) {
			outfreeformat = true;
		} else {
			outfreeformat = false;
			decimal = new DecimalFormat("000000");
			String initrowtxt = getEnvValue("initrow", "10");
			try {
				initrownum = Integer.parseInt(initrowtxt);
			} catch (NumberFormatException e) {
				initrownum = 10;
				// e.printStackTrace();
			}
			String incrrowtxt = getEnvValue("increase", "10");
			try {
				incrrownum = Integer.parseInt(incrrowtxt);
			} catch (NumberFormatException e) {
				incrrownum = 10;
				// e.printStackTrace();
			}
		}
		// const file
		String acmconsts_file = getEnvValue("acmconsts_file", CobolConsts.getACMCONSTS_FILE());
		if (acmconsts_file.trim().length() > 0) {
			CobolConsts.setACMCONSTS_FILE(acmconsts_file.trim());
		}
		// expand copy
		String expandStr = getEnvValue("expand_copy", "false");
		expandCopy = Boolean.parseBoolean(expandStr);
		// listeners
		String namelist = getEnvValue("codegeneratorlisteners", "");
		if (namelist.length() > 0) {
			this.addCodeGeneratorListeners(namelist);
		}
		// custorm generator
		String generatorClass = getEnvValue("customcodegeneratorclass", "");
		if (generatorClass.length() > 0) {
			try {
				Class<? extends CodeGenerator> clazz = Class.forName(generatorClass).asSubclass(CodeGenerator.class);
				Class<?>[] params = new Class<?>[] { GeneratorOwner.class };
				Constructor<? extends CodeGenerator> constructor = clazz.getConstructor(params);
				generator = constructor.newInstance(new Object[] { this });
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			generatorClass = generator.getClass().getName();
		}
		// subprogram
		String subprogramStr = getEnvValue("subprogram", "false");
		subprogram = Boolean.parseBoolean(subprogramStr);
		// ignoreOut
		String dontcommentStr = getEnvValue("dontcomment", "false");
		dontcomment = Boolean.parseBoolean(dontcommentStr);
		// usage
		String usageStr = getEnvValue("display_usage", "true");
		if (Boolean.parseBoolean(usageStr)) {
			System.err.println("usage:java -cp path_to_jar \"k_kim_mg.sa4cob2db.codegen.COBPP1\" $1 $2");
			System.err.println("\t$1=" + infile);
			System.err.println("\t$2=" + outfile);
			System.err.println("\tinformat=" + infmttext + "\t:fix or free");
			System.err.println("\toutformat=" + outfmttext + "\t:fix or free");
			System.err.println("\tinitrow=" + initrownum);
			System.err.println("\tincrease=" + incrrownum);
			System.err.println("\tacmconsts_file=" + acmconsts_file);
			System.err.println("\texpand_copy=" + expandStr + "\t:true or false or space");
			System.err.println("\tdisplay_usage=" + usageStr + "\t:true or false or space");
			System.err.println("\tacm_pp_charset=" + csn + "\t:chaset encodeing");
			System.err.println("\tcodegeneratorlisteners=" + namelist + "\t::separated class names");
			System.err.println("\tsubprogram=" + subprogramStr + "\t:true or false or space");
			System.err.println("\tcustomcodegeneratorclass=" + generatorClass + "\t::separated class names");
			System.err.println("\tdontcomment=" + dontcommentStr + "\t:true or false or space");
		}
	}
	/**
	 * add listeners
	 * 
	 * @param names class names(separated by ":")
	 */
	void addCodeGeneratorListeners(String namelist) {
		if (namelist.length() > 0) {
			try {
				String[] names = namelist.split(":");
				for (String name : names) {
					try {
						Class<? extends CodeGeneratorListener> clazz = Class.forName(name).asSubclass(CodeGeneratorListener.class);
						CodeGeneratorListener listener = clazz.newInstance();
						generator.addCodeGeneratorListener(listener);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void callBackCopyStatement(ArrayList<String> statement) {
		if (expandCopy) {
			CopyInfo info = new CopyInfo(statement);
			try {
				CopyProcesser proc = new CopyProcesser(info);
				proc.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see k_kim_mg.sa4cob2db.codegen.GeneratorOwner#generate(java.lang
	 * .String)
	 */
	public void generate(String text) {
		if (outfreeformat) {
			output.println(text);
		} else {
			output.println(decimal.format(initrownum) + text);
			initrownum += incrrownum;
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
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.codegen.GeneratorOwner#isExpandCopy()
	 */
	@Override
	public boolean isExpandCopy() {
		return expandCopy;
	}
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.codegen.GeneratorOwner#isDontComment()
	 */
	@Override
	public boolean isDontComment() {
		return dontcomment;
	}
	/* (non-Javadoc)
	 * @see k_kim_mg.sa4cob2db.codegen.GeneratorOwner#isSubprogram()
	 */
	@Override
	public boolean isSubprogram() {
		return subprogram;
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * set expand copy?
	 * 
	 * @param expandCopy true expand</br> false don't
	 */
	public void setExpandCopy(boolean expandCopy) {
		this.expandCopy = expandCopy;
	}
	/**
	 * set Ignore
	 * @param dontComment true:ignore false don't
	 */
	public void setDontComment (boolean dontComment) {
		this.dontcomment = dontComment;
	}
	/**
	 * set Subprogram
	 * @param subprogram true then program is subprogram.
	 */
	public void setSubprogram(boolean subprogram) {
		this.subprogram = subprogram;
	}
}
