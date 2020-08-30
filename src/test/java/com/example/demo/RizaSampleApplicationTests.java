package com.example.demo;

import static com.example.demo.KKK.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import com.example.utils.FFFF;
import com.example.utils.Super;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

class RizaSampleApplicationTests implements Super, FFFF, IDGenerator {

//	private static final String TEST_XML = "/Users/webma/Documents/workspace/RizaSample/src/main/java/com/example/demo/test.xml";

	@BeforeEach
	void before() throws Exception {

		for (KKK k : values())
			clean(k.getQNames());
	}

	@ParameterizedTest
	@MethodSource("testMq")
	@DisplayName("テスト")
	void multiArguments(KKK putQName, KKK getQName, String xmlPath) throws Exception {

		Document putXmlData = fileToDocument(xmlPath);
		MQMessage putData = putMessages(documentToString(putXmlData));
		put(putQName, putData);
		MQMessage getData = get(getQName, putData.messageId);
		extracted(putData, putXmlData, getData);

	}

	static Stream<Arguments> testMq() {
		return Stream.of(

				Arguments.of(QC_DH_REQ, QA_DH_DL, TEST2_XML), Arguments.of(QL_DH_REQ, QA_DH_DL, TEST2_XML),
				Arguments.of(QC_DH_REQ, QA_DH_DF, TEST_XML), Arguments.of(QL_DH_REQ, QA_DH_DF, TEST_XML)

		);
	}

	@ParameterizedTest
	@MethodSource("returnTest")
	@DisplayName("戻りテスト")
	void returnTest(KKK putQName, KKK getQName, String xmlPath) throws Exception {

		Document putXmlData = fileToDocument(xmlPath);
		MQMessage putData = putMessages(documentToString(putXmlData));
		putData.replyToQueueName = QL_DW_REP.getQNames();	// TODO
		put(putQName, putData);
		MQMessage getData = get(getQName, putData.messageId);
		returnQTest(putData, putXmlData ,getData);
	}

	static Stream<Arguments> returnTest() {
		return Stream.of(Arguments.of(QC_DH_REQ, QL_DW_REP, TEST3_XML), Arguments.of(QL_DH_REQ, QL_DW_REP, TEST4_XML));
	}
	
	@ParameterizedTest
	@MethodSource("errorTest")
	@DisplayName("エラーテスト")
	void errorTest(KKK putQName, KKK getQName, String xmlPath) throws Exception {

		String putXmlData = fileToString(xmlPath);
		MQMessage putData = putMessages(putXmlData);
		put(putQName, putData);
		MQMessage getData = get(getQName);
//		String getMqData = mqMessageToString(getData);
		errorQTest(putData, putXmlData ,getData);
		
	}

	static Stream<Arguments> errorTest() {
		return Stream.of(Arguments.of(QC_DH_REQ, QL_DH_ERR, TEST5_XML));
	}

//	@Test
//	void TestRC() throws Exception {
//		
//		String putXmlData = fileToString(TEST5_XML);
//		MQMessage putData = putMessages(putXmlData);
//		putData.replyToQueueName = QL_DW_REP.getQNames();
//		put(QC_DH_REQ, putData);
//		MQMessage getData = get(QL_DH_ERR);
//		String getMqData = mqMessageToString(getData);
//		System.out.println(getMqData);
//	}

	@Override
	public String getQmgr() {
		// TODO Auto-generated method stub
		return QMFH01;
	}

	@Override
	public String getLocalhost() {
		// TODO Auto-generated method stub
		return LOCALHOST;
	}

	@Override
	public String getCannal() {
		// TODO Auto-generated method stub
		return SYSTEM_BKR_CONFIG;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return _50014;
	}
	
	
	
	public void extracted(MQMessage putMQMessageData, Document putXmlData, MQMessage getMQMessageData)
			throws XpathException, ParserConfigurationException, SAXException, IOException, ParseException,
			TransformerException {

		assertNotNull(getMQMessageData);

		String getStringData = mqMessageToString(getMQMessageData);
		Document getDocumentData = stringToDocument(getStringData);

		assertEquals(getQmgr(), getMQMessageData.replyToQueueManagerName.trim());
		assertEquals(QL_DH_REP.getQNames(), getMQMessageData.replyToQueueName.trim());
		String xServiceId = xPath(getDocumentData, SERVICEID_Tab);
		boolean f = "F".equals(xServiceId.substring(1, 2));
		if (f) {
			assertEquals(putMQMessageData.characterSet, getMQMessageData.characterSet);
		} else {
			assertEquals(943, getMQMessageData.characterSet);
		}
		assertEquals(putMQMessageData.priority, getMQMessageData.priority);
		assertEquals(putMQMessageData.persistence, getMQMessageData.persistence);
		assertEquals(putMQMessageData.messageType, getMQMessageData.messageType);
		assertEquals(putMQMessageData.format.trim(), getMQMessageData.format.trim());
		assertEquals(putMQMessageData.expiry, getMQMessageData.expiry);
		assertEquals(xPath(getDocumentData, SERVICEID_Tab), getMQMessageData.applicationIdData.trim());
		assertEquals("00", xPath(getDocumentData, RC_Tab));
		assertEquals(putMQMessageData.replyToQueueManagerName, xPath(getDocumentData, R_PVR_Tab));
		assertEquals(putMQMessageData.replyToQueueName, xPath(getDocumentData, R_DST_Tab));
		if (!f) {
			assertEquals("¥¥", xPath(getDocumentData, D1_Tab));
			assertEquals("‾‾", xPath(getDocumentData, D2_Tab));
		}
		int putCount = xPathCount(putXmlData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);

		for (int i = 1; i <= putCount; i++) {
			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]"), 
					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]"));
			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"), 
					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"));
			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"), 
					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"));
			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"), 
					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"));
			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"), 
					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"));
		}

		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals("RSHUBFX", xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"));
			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/KUBUN"), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"));
			int ff = i - putCount;
			String as = String.valueOf(ff);
			assertEquals(as, xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"));
			assertEquals(xPath(putXmlData, SERVICEID_Tab), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"));

			String getDatePath = xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]");
			String getDate = getDatePath.substring(0, 14);

			System.out.println(getDate);
			String getDateCorrelation = getDatePath.substring(14, 17);
			System.out.println(getDateCorrelation);
			assertTrue(LocalDateTime.parse(getDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));

			assertEquals("000", getDateCorrelation);

			List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP", "REPLY"));
			if (!f) {
				nodeList.add("D1");
				nodeList.add("D2");
			}

			if (!f)
				getStringData = getStringData.replace("IBM-930", "UTF-8");

			listPass(documentToString(putXmlData), getStringData, nodeList);
		}
		System.out.println(putCount);
		System.out.println(getCount);

	}

	public void listPass(String putData, String getReplaceData, List<String> nodeList) {

		final Diff diff = DiffBuilder.compare(putData).withTest(getReplaceData)
				.withNodeFilter(node -> !nodeList.contains(node.getNodeName())).build();

		Iterator<Difference> iter = diff.getDifferences().iterator();
		int size = 0;
		while (iter.hasNext()) {
			System.out.println(iter.next().toString());
			size++;
		}
		assertEquals(0, size);

	}

	public int xPathCount(Document data, String xmlPath) throws XpathException {
		return XMLUnit.newXpathEngine().getMatchingNodes(xmlPath, data).getLength();
	}

	public String xPath(Document getData, String xmlPath) throws XpathException {

		XpathEngine xp = XMLUnit.newXpathEngine();
		return xp.evaluate(xmlPath, getData);
	}

	public void returnQTest(MQMessage putMQMessage, Document putXmlData, MQMessage getMQMessage)
			throws IOException, ParserConfigurationException, SAXException, Exception {

		String getStringData = mqMessageToString(getMQMessage);
		Document getDocumentData = stringToDocument(getStringData);

		String xServiceId = xPath(getDocumentData, SERVICEID_Tab);
		boolean a = "D".equals(xServiceId.substring(0, 1));
		if (!a) {
			assertEquals("01", xPath(getDocumentData, RC_Tab));
		}
		boolean b = "F".equals(xServiceId.substring(1, 2)) || "L".equals(xServiceId.substring(1, 2));
		if (!b) {
			assertEquals("02", xPath(getDocumentData, RC_Tab));
		}
		int putCount = xPathCount(putXmlData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);
		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals(xPath(putXmlData, "2"), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"));
			int ff = i - putCount;
			String as = String.valueOf(ff);
			assertEquals(as, xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"));
			assertEquals("RSHUBF ", xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"));
			assertEquals(xPath(putXmlData, SERVICEID_Tab), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"));

			String getDatePath = xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]");
			String getDate = getDatePath.substring(0, 14);
			String getDateCorrelation = getDatePath.substring(14, 17);
			assertTrue(LocalDateTime.parse(getDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));

			assertEquals("000", getDateCorrelation);

		}

		// changing
		assertEquals(273, getMQMessage.encoding);
		assertEquals(1208, getMQMessage.characterSet);

		// no changing
		assertEquals(putMQMessage.priority, getMQMessage.priority);
		assertEquals(putMQMessage.persistence, getMQMessage.persistence);
		assertEquals(putMQMessage.messageType, getMQMessage.messageType);
		assertEquals(putMQMessage.format.trim(), getMQMessage.format.trim());
		assertEquals(putMQMessage.expiry, getMQMessage.expiry);
		assertEquals(getQmgr(), getMQMessage.replyToQueueManagerName.trim());
		assertEquals(putMQMessage.replyToQueueName, getMQMessage.replyToQueueName.trim());
		assertEquals(putMQMessage.applicationIdData.trim(), getMQMessage.applicationIdData.trim());

		List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP"));

		listPass(documentToString(putXmlData), getStringData, nodeList);
	}

	public void errorQTest(MQMessage putMQMessage, String putXmlData, MQMessage getMQMessage) throws IOException {

		// no changing
		assertEquals(putMQMessage.priority, getMQMessage.priority);
		assertEquals(putMQMessage.messageType, getMQMessage.messageType);
		assertEquals(putMQMessage.format.trim(), getMQMessage.format.trim());
		assertEquals(getQmgr(), getMQMessage.replyToQueueManagerName.trim());
		assertEquals(putMQMessage.replyToQueueName, getMQMessage.replyToQueueName.trim());
		assertEquals(putMQMessage.applicationIdData.trim(), getMQMessage.applicationIdData.trim());
		assertEquals(getMQMessage.encoding, putMQMessage.encoding);
		assertEquals(getMQMessage.characterSet, putMQMessage.characterSet);

		// changing
		assertEquals(MQC.MQEI_UNLIMITED, getMQMessage.expiry);
		assertEquals(MQC.MQPER_PERSISTENT, getMQMessage.persistence);

		String getStringData = mqMessageToString(getMQMessage);
		assertEquals(putXmlData.replace(System.lineSeparator(), ""), getStringData.replace(System.lineSeparator(), ""));

	}

}
