package jp.co.acom.fehub.tests;

import static jp.co.acom.fehub.mq.ConstantQname.QL_DW_REP;

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
	
	public String getQmgr();

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

	default String mqMessageToString(MQMessage m) throws IOException {
		StringBuilder builder = new StringBuilder();
		m.setDataOffset(0);
		while (m.getDataLength() > 0)
			builder.append(m.readLine()).append(System.lineSeparator());

		String aaa = builder.toString();
		return aaa;
	}
	
	default String replaceSerId(String xmlData, String getQName) {
		return xmlData.replace("<SERVICEID>", "<SERVICEID>D" + getQName);
	}
	
	default String replacePluralSerId(String xmlData, String xmlServiceId) {
		if (xmlServiceId == null) {
			xmlData = xmlData.replace("<SERVICEID></SERVICEID>", "");
		} else {
			xmlData = xmlData.replace("<SERVICEID></SERVICEID>",
					"<SERVICEID>" + xmlServiceId + "</SERVICEID>");	
		}return xmlData;
	}
	
	default String replaceReply(String stringXmlData,String replyQ) {
		
		stringXmlData = stringXmlData.replace("</GLB_HEAD>",
				"<REPLY><R_PVR>" + getQmgr() + "</R_PVR><R_DST>" + replyQ + "</R_DST></REPLY></GLB_HEAD>");
		return stringXmlData;
	}
	
	default String replaceRc(String xmlData) {
		return xmlData.replace("</GLB_HEAD>", "<RC>99</RC></GLB_HEAD>");
	}
	
	default String replacePluralRc(String xmlData, String rc) {
		if (rc == null) {
			xmlData = xmlData.replace("</GLB_HEAD>", "</GLB_HEAD>");
		} else {
			xmlData = xmlData.replace("</GLB_HEAD>", "<RC>" + rc + "</RC></GLB_HEAD>");
		}
		return xmlData;
	}
	
	default String replaceErrorReply(String stringXmlData, String ReplyError) {
		return stringXmlData.replace("</GLB_HEAD>", ReplyError + "</GLB_HEAD>");
	}

}