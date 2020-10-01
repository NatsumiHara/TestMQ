package jp.co.acom.fehub.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

public interface Xml {

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

	default String mqMessageToString(MQMessage mqMessage) throws IOException {
		StringBuilder builder = new StringBuilder();
		mqMessage.setDataOffset(0);
		while (mqMessage.getDataLength() > 0)
			builder.append(mqMessage.readLine()).append(System.lineSeparator());

		String stringMessage = builder.toString();
		return stringMessage;
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
		return replaceErrorReply(stringXmlData, "<REPLY><R_PVR>" + getQmgr + "</R_PVR><R_DST>" + replyQ + "</R_DST></REPLY>");
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

}