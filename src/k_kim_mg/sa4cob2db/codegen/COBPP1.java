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
 * COBOL�����ɤ�����Ѵ����ƥե����륢��������ACM�ؤΥ����������ѹ�����
 * @author ���줪��
 */
public class COBPP1 implements GeneratorOwner {
	private DecimalFormat decimal;
	private InputStream input;
	private PrintStream output;
	private boolean outfreeformat = false;
	private boolean infreeformat = false;
	private int initrownum = 10;
	private int incrrownum = 10;
	private boolean expandCopy = false;
	/** �����������ɤΥ���饯�������åȤ���ꤹ��Ķ��ѿ���̾�� */
	public static final String ACM_CHARSET = "acm_pp_charset";
	/**
	 * ��������
	 */
	private CodeGenerator generator = new TCPCodeGenerator(this);
	/**
	 * �ᥤ��
	 * @param argv �ե�����̾�ʤ�
	 */
	public static void main(String[] argv) {
		COBPP1 cobpp = new COBPP1(argv);
		cobpp.run();
		System.exit(0);
	}
	/**
	 * ���󥹥ȥ饯��
	 * @param argv ��ư�ѥ�᡼��
	 */
	public COBPP1(String[] argv) {
		String csn = "";
		// ���ϥե�����
		String infile = (argv.length > 0 ? argv[0] : "");
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
		// ���ϥե�����
		String outfile = (argv.length > 1 ? argv[1] : "");
		if (outfile.length() == 0) {
			output = System.out;
		} else {
			try {
				csn = getEnvValue(ACM_CHARSET, "");
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
		// �ե����ޥå�
		String infmttext = getEnvValue("informat", "fix");
		if (infmttext.compareToIgnoreCase("free") == 0) {
			infreeformat = true;
		}
		String outfmttext = getEnvValue("outformat", "fix");
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
		// ���ԡ���λ���
		String acmconsts_file = getEnvValue("acmconsts_file", CobolConsts.ACMCONSTS_FILE);
		if (acmconsts_file.trim().length() > 0) {
			CobolConsts.ACMCONSTS_FILE = acmconsts_file.trim();
		}
		// ���ԡ����Ÿ������
		String expandStr = getEnvValue("expand_copy", "false");
		expandCopy = Boolean.parseBoolean(expandStr);
		// ���٥�Ƚ�����ǽ
		String namelist = getEnvValue("codegeneratorlisteners", "");
		if (namelist.length() > 0) {
			this.addCodeGeneratorListeners(namelist);
		}
		// ���٥�Ƚ�����ǽ
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
		// �Ȥ���������������
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
			System.err.println("\tcustomcodegeneratorclass=" + generatorClass + "\t::separated class names");
		}
	}
	/**
	 * �ꥹ�ʤ��ɲä���
	 * @param names ���饹̾(:�Ƕ��ڤ롩)
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
	/**
	 * �Ķ��ѿ����������
	 * @param key ����
	 * @param defaultValue �ǥե������
	 * @return �ѿ�����
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
	 * @see
	 * k_kim_mg.sa4cob2db.codegen.GeneratorOwner#generate(java.lang
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
	/**
	 * �¹�
	 */
	public void run() {
		try {
			InputStreamReader isr = null;
			String csn = getEnvValue(ACM_CHARSET, "");
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
			// String text1;
			while (row != null) {
				// text1 = new String(text.getBytes());
				// cg1.parse(text1);
				String text = row;
				if (!infreeformat) {
					text = row.substring(6);
					// SQLNetServer.DebugPrint(text);
				}
				generator.parse(text);
				row = br.readLine();
			}
			generator.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ���ԡ����Ÿ����������
	 * @author ���줪��
	 */
	class CopyProcesser {
		private CopyInfo info;
		private FileInputStream input2;
		/**
		 * ���󥹥ȥ饯��
		 * @param Info ���ԡ���ξ���
		 * @throws IOException �������㳰
		 */
		public CopyProcesser(CopyInfo Info) throws IOException {
			info = Info;
			input2 = new FileInputStream(info.getFilename());
		}
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
			generator.clear();
		}
	}
	/**
	 * ���ԡ����ξ���
	 * @author ���줪��
	 */
	static class CopyInfo {
		private ArrayList<String> statement;
		private String filename;
		private Properties replacing = new Properties();
		/**
		 * ���󥹥ȥ饯��
		 * @param Statement Copy�����̳ʸ����
		 */
		public CopyInfo(ArrayList<String> Statement) {
			statement = Statement;
			parse();
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
		/**
		 * �ե�����̾
		 * @return �ե�����̾
		 */
		public String getFilename() {
			return filename;
		}
		/**
		 * �ִ����줿ʸ����μ���
		 * @param target �ִ�����ʸ����
		 * @return �ִ����ʸ����
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
	}
	/**
	 * ���ԡ����Ÿ�����뤫�ɤ���
	 * @return true Ÿ������</br> false Ÿ�����ʤ�
	 */
	public boolean isExpandCopy() {
		return expandCopy;
	}
	/**
	 * ���ԡ����Ÿ�����뤫�ɤ���
	 * @param expandCopy true Ÿ������</br> false Ÿ�����ʤ�
	 */
	public void setExpandCopy(boolean expandCopy) {
		this.expandCopy = expandCopy;
	}
}
