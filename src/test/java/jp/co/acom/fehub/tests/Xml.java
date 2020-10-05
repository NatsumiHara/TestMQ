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
	public static final String TS1_XML = "ts1.xml";
	public static final String NOMAL_XML = "NORMAL.xml";
	public static final String TS0_XML = "TS0.xml";
	public static final String TS2_XML = "TS2.xml";
	public static final String TS3_XML = "TS3.xml";
	public static final String TS4_XML = "TS4.xml";
	public static final String TS5_XML = "TS5.xml";
	public static final String TS6_XML = "TS6.xml";
	public static final String TS7_XML = "TS7.xml";
	public static final String TS8_XML = "TS8.xml";
	public static final String TIMESTAMP_NOT_XML = "TIMESTAMP_NOT.xml";
	public static final String RC00_XML = "RC00.xml";
	public static final String RC01_XML = "RC01.xml";
	public static final String RC_Tab_XML = "RC_Tab.xml";
	public static final String REPLY_XML = "REPLY.xml";
	public static final String USERID_NOT_XML = "userId_not.xml";
	public static final String VERSION_NOT_XML = "version_not.xml";
	public static final String KUBUN_NOT_XML = "kubun_not.xml";
	public static final String REQUESTID_NOT_XML = "requestId_not.xml";
	public static final String KUBUN_BREAK_XML = "KUBUN_break.xml";
	public static final String RC_BREAK_XML = "RC_break.xml";
	public static final String REPLY_BREAK_XML = "REPLY_break.xml";
	public static final String REQUESTID_BREAK_XML = "REQUESTID_break.xml";
	public static final String SERVICEID_BREAK_XML = "SERVICEID_break.xml";
	public static final String TIMESTAMP_BREAK_XML = "TIMESTAMP_break.xml";
	public static final String TS_BREAK_XML = "TS_break.xml";
	public static final String USERID_BREAK_XML = "USERID_break.xml";
	public static final String VERSION_BREAK_XML = "VERSION_break.xml";
	public static final String REPLY_QMGR_XML = "REPLY_qmgr.xml";
	public static final String REPLY_QNAME_XML = "REPLY_qName.xml";
	public static final String REPLY_QMGR_QNAME_XML = "REPLY_qmgr+qName.xml";
	
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