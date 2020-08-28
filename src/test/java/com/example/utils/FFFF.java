package com.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

public interface FFFF {

	default String fileToData(String path)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(path));

		StringWriter stringWriter = new StringWriter();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
		return stringWriter.toString();

	}

	default Document fileToDocument(String path) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(path));
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

	default String fileToString(String path) throws IOException {
		String str = null;
		StringBuilder builder = new StringBuilder();
		try (BufferedReader sb = new BufferedReader(new FileReader(path));) {
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

		while (m.getDataLength() > 0)
			builder.append(m.readLine()).append(System.lineSeparator());

		String aaa = builder.toString();
		return aaa;
	}

}