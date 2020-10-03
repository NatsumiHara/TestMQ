package jp.co.acom.fehub.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

public interface Xml {
	public static final String TS1 = "ts1.xml";
	public static final String TEST_XML = "NORMAL.xml";
	public static final String TEST2_XML = "TS0.xml";
	public static final String TEST3_XML = "TS2.xml";
	public static final String TEST4_XML = "TS3.xml";
	public static final String TEST5_XML = "TS4.xml";
	public static final String TEST6_XML = "TS5.xml";
	public static final String TEST7_XML = "TS6.xml";
	public static final String TEST8_XML = "TS7.xml";
	public static final String TEST9_XML = "TS8.xml";
	public static final String TEST10_XML = "TIMESTAMP_NOT.xml";
	public static final String TEST11_XML = "RC00.xml";
	public static final String TEST12_XML = "RC01.xml";
	public static final String TEST13_XML = "RC_Tab.xml";
	public static final String TEST14_XML = "REPLY.xml";
	public static final String TEST15_XML = "userId_not.xml";
	public static final String TEST16_XML = "version_not.xml";
	public static final String TEST17_XML = "kubun_not.xml";
	public static final String TEST18_XML = "requestId_not.xml";
	public static final String TEST19_XML = "KUBUN_break.xml";
	public static final String TEST20_XML = "RC_break.xml";
	public static final String TEST21_XML = "REPLY_break.xml";
	public static final String TEST22_XML = "REQUESTID_break.xml";
	public static final String TEST23_XML = "SERVICEID_break.xml";
	public static final String TEST24_XML = "TIMESTAMP_break.xml";
	public static final String TEST25_XML = "TS_break.xml";
	public static final String TEST26_XML = "USERID_break.xml";
	public static final String TEST27_XML = "VERSION_break.xml";
	public static final String TEST28_XML = "REPLY_qmgr.xml";
	public static final String TEST29_XML = "REPLY_qName.xml";
	public static final String TEST30_XML = "REPLY_qmgr+qName.xml";
	
	default String xPath(Document getData, String xmlPath) throws XpathException {

		XpathEngine xp = XMLUnit.newXpathEngine();
		return xp.evaluate(xmlPath, getData);
	}
	
	default int xPathCount(Document data, String xmlPath) throws XpathException {
		return XMLUnit.newXpathEngine().getMatchingNodes(xmlPath, data).getLength();
	}

	default Document fileToDocument(String file) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(getClass().getResourceAsStream("/" + file)));
		return doc;
	}

	default String documentToString(Document doc) throws TransformerException {

		StringWriter stringWriter = new StringWriter();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
		return stringWriter.toString();
	}

	default Document stringToDocument(String stringWriter)
			throws ParserConfigurationException, SAXException, IOException {
		InputSource inputSource = new InputSource(new StringReader(stringWriter));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(inputSource);
		return doc;
	}

	default String fileToString(String file) throws IOException {
		String str = null;
		StringBuilder builder = new StringBuilder();
		try (BufferedReader sb = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/" + file)));) {
			str = sb.readLine();
			while (str != null) {
				builder.append(str + System.lineSeparator());
				str = sb.readLine();
			}
			return builder.toString();
		}
	}

	default String replaceSerId(String xmlData, String getQName) {
		return replacePluralSerId(xmlData, "D" + getQName);
	}

	default String replacePluralSerId(String xmlData, String xmlServiceId) {
		if (xmlServiceId == null) {
			xmlData = xmlData.replace("<SERVICEID></SERVICEID>", "");
		} else {
			xmlData = xmlData.replace("<SERVICEID></SERVICEID>", "<SERVICEID>" + xmlServiceId + "</SERVICEID>");
		}
		return xmlData;
	}

	default String replaceReply(String stringXmlData, String replyQ, String getQmgr) {
		return replaceErrorReply(stringXmlData,
				"<REPLY><R_PVR>" + getQmgr + "</R_PVR><R_DST>" + replyQ + "</R_DST></REPLY>");
	}

	default String replaceRc(String xmlData) {
		return replacePluralRc(xmlData, "99");
	}

	default String replacePluralRc(String xmlData, String rc) {
//		if (rc != null) {
//			xmlData = replaceErrorReply(xmlData,"<RC>" + rc + "</RC>");
//		}
//		
//		return xmlData;
//
		return rc != null ? replaceErrorReply(xmlData, "<RC>" + rc + "</RC>") : xmlData;
	}

	default String replaceErrorReply(String stringXmlData, String replyError) {
		return stringXmlData.replace("</GLB_HEAD>", replyError + "</GLB_HEAD>");
	}

	default List<String> listPass(String putData, String getReplaceData, List<String> nodeList) {

		final Diff diff = DiffBuilder.compare(putData).withTest(getReplaceData)
				.withNodeFilter(node -> !nodeList.contains(node.getNodeName())).build();

		Iterator<Difference> iter = diff.getDifferences().iterator();
		
		List<String> list = new ArrayList<>();
		while (iter.hasNext()) {
			list.add(iter.next().toString());
		}
		return list;
	}

}