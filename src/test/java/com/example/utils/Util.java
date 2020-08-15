//package com.example.utils;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.StringWriter;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.w3c.dom.Document;
//import org.xml.sax.SAXException;
//
//import com.ibm.mq.MQException;
//import com.ibm.msg.client.wmq.compat.base.internal.MQC;
//import com.ibm.msg.client.wmq.compat.base.internal.MQEnvironment;
//import com.ibm.msg.client.wmq.compat.base.internal.MQGetMessageOptions;
//import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;
//import com.ibm.msg.client.wmq.compat.base.internal.MQPutMessageOptions;
//import com.ibm.msg.client.wmq.compat.base.internal.MQQueue;
//import com.ibm.msg.client.wmq.compat.base.internal.MQQueueManager;
//
//public class Util {
//
//	public String fileToData(String path)
//			throws ParserConfigurationException, SAXException, IOException, TransformerException {
//
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document doc = builder.parse(new File(path));
//
//		StringWriter stringWriter = new StringWriter();
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
//		return stringWriter.toString();
//
//	}
//}
