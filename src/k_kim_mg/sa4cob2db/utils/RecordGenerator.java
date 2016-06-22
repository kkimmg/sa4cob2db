package k_kim_mg.sa4cob2db.utils;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import k_kim_mg.sa4cob2db.codegen.CobolTokens;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Convert file access code to call statement
 * 
 * @author <a mailto="kkimmg@gmail.com">Kenji Kimura</a>
 */
public class RecordGenerator {
	private ArrayList<MetaCobolRecordMetaData> records = new ArrayList<MetaCobolRecordMetaData>();
	private MetaCobolRecordMetaData curr;
	private String buffer = "";

	/**
	 * parse text
	 * 
	 * @param txt text
	 */
	public void parse(String txt) {
		String row = txt.trim();
		String last = txt.substring(txt.length(), txt.length());
		CobolTokens tokenizer = new CobolTokens(row);
		if (buffer.length() <= 0) {
			if (tokenizer.hasNext()) {
				String first = tokenizer.next();
				int level = -1;
				try {
					level = Integer.parseInt(first);
				} catch (NumberFormatException e) {
					level = -1;
				}
				if (level <= 0) {
					//
				} else if (level == 1) {
					curr = new MetaCobolRecordMetaData();
					records.add(curr);
					curr.parse(row);
				} else if (level > 1 && curr != null) {
					curr.parse(row);
				}
			}
		}
		if (last.equals(".")) {
			flush();
		}
	}

	/**
	 * flush
	 */
	public void flush() {
		if (curr != null) {
			curr.parse(buffer);
		}
		buffer = "";
	}

	/**
	 * export to node
	 * 
	 * @param outname output file name
	 */
	public int exportToNode(String outname) {
		int ret = 0;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
			Document document = docbuilder.newDocument();
			ret = exportToNode(document, outname);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * export to node
	 * 
	 * @param document XML Document
	 * @param outname output file name
	 */
	public int exportToNode(Document document, String outname) {
		int ret = 0;
		Node root = document.createElement("metadataset");
		document.appendChild(root);
		for (MetaCobolRecordMetaData x : records) {
			x.exportToNode(document, root);
		}
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			if (outname != null && outname.trim().length() > 0) {
				File outfile = new File(outname);
				transformer.transform(new DOMSource(document), new StreamResult(outfile));
			} else {
				transformer.transform(new DOMSource(document), new StreamResult(System.out));
			}
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return ret;
	}
}