package jp.co.acom.fehub.tests;

import static jp.co.acom.fehub.mq.ConstantQname.QL_DH_REP;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

public interface CommonTest extends Mq, Xml {

	public static final String TS_Tab = "/CENTER/GLB_HEAD/TIMESTAMP/TS";
	public static final String D2_Tab = "/CENTER/FRAME/APL_DATA/D2";
	public static final String D1_Tab = "/CENTER/FRAME/APL_DATA/D1";
	public static final String R_DST_Tab = "/CENTER/GLB_HEAD/REPLY/R_DST";
	public static final String R_PVR_Tab = "/CENTER/GLB_HEAD/REPLY/R_PVR";
	public static final String RC_Tab = "/CENTER/GLB_HEAD/RC";
	public static final String SERVICEID_Tab = "/CENTER/GLB_HEAD/SERVICEID";
	public static final String REPLY_Tab = "/CENTER/GLB_HEAD/REPLY";
	public static final String REQUESTID_Tab = "/CENTER/GLB_HEAD/REQUESTID";

	default void extracted(MQMessage putMQMessageData, Document putXmlData, MQMessage getMQMessageData)
			throws XpathException, ParserConfigurationException, SAXException, IOException, ParseException,
			TransformerException {

		assertNotNull(getMQMessageData);

		String getStringData = mqMessageToString(getMQMessageData);
		Document getDocumentData = stringToDocument(getStringData);

		assertEquals(getQmgr(), getMQMessageData.replyToQueueManagerName.trim());
		assertEquals(QL_DH_REP.getQNames(), getMQMessageData.replyToQueueName.trim());
		boolean serviceF = "F".equals(xPath(getDocumentData, SERVICEID_Tab).substring(1, 2));
		if (serviceF) {
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

		if (!serviceF) {
			assertEquals("¥¥", xPath(getDocumentData, D1_Tab));
			assertEquals("‾‾", xPath(getDocumentData, D2_Tab));
		}
		int putCount = xPathCount(putXmlData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);

		for (int i = 1; i <= putCount; i++) {
			assertEquals(xPath(putXmlData, TS_Tab + "[" + i + "]"), xPath(getDocumentData, TS_Tab + "[" + i + "]"));
			assertEquals(xPath(putXmlData, TS_Tab + "[" + i + "]/@SVR"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVR"));
			assertEquals(xPath(putXmlData, TS_Tab + "[" + i + "]/@KBN"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
			assertEquals(xPath(putXmlData, TS_Tab + "[" + i + "]/@LVL"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@LVL"));
			assertEquals(xPath(putXmlData, TS_Tab + "[" + i + "]/@SVC"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVC"));
		}

		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals("RSHUBFX", xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVR"));
			assertEquals(xPath(putXmlData, "1"), xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
			assertEquals(String.valueOf(i - putCount), xPath(getDocumentData, TS_Tab + "[" + i + "]/@LVL"));
			assertEquals(xPath(putXmlData, SERVICEID_Tab), xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVC"));
			String getDatePath = xPath(getDocumentData, TS_Tab + "[" + i + "]");
			assertTrue(LocalDateTime.parse(getDatePath.substring(0, 14), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));

			assertEquals("000", getDatePath.substring(14, 17));

			List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP"));
			if (!serviceF) {
				nodeList.add("D1");
				nodeList.add("D2");
			}
			boolean requestIdNull = 0 == xPathCount(putXmlData, REQUESTID_Tab);
			if (requestIdNull) {
				nodeList.add("REQUESTID");
				assertEquals(1, xPathCount(getDocumentData, REQUESTID_Tab));
			}

			boolean rep = xPathCount(putXmlData, REPLY_Tab) == 1;
			if (!rep) {
				nodeList.add("REPLY");
				assertEquals(putMQMessageData.replyToQueueName, xPath(getDocumentData, R_DST_Tab));
				assertEquals(putMQMessageData.replyToQueueManagerName, xPath(getDocumentData, R_PVR_Tab));

			}
			if (!serviceF)
				getStringData = getStringData.replace("IBM-930", "UTF-8");

			listPass(documentToString(putXmlData), getStringData, nodeList);

		}
	}

	default void listPass(String putData, String getReplaceData, List<String> nodeList) {

		final Diff diff = DiffBuilder.compare(putData).withTest(getReplaceData)
				.withNodeFilter(node -> !nodeList.contains(node.getNodeName())).build();

		Iterator<Difference> iter = diff.getDifferences().iterator();
		int size = 0;
		while (iter.hasNext()) {
			iter.next().toString();
			size++;
		}
		assertEquals(0, size);

	}

	default int xPathCount(Document data, String xmlPath) throws XpathException {
		return XMLUnit.newXpathEngine().getMatchingNodes(xmlPath, data).getLength();
	}

	default String xPath(Document getData, String xmlPath) throws XpathException {

		XpathEngine xp = XMLUnit.newXpathEngine();
		return xp.evaluate(xmlPath, getData);// TODO
	}

	default void returnQTest(MQMessage putMQMessage, MQMessage getMQMessage, String rc, String appOrService)
			throws IOException, ParserConfigurationException, SAXException, Exception {
		String putStringData = mqMessageToString(putMQMessage);
		Document putXmlData = stringToDocument(putStringData);

		String getStringData = mqMessageToString(getMQMessage);
		Document getDocumentData = stringToDocument(getStringData);

		assertEquals(rc, xPath(getDocumentData, RC_Tab));

		int putCount = xPathCount(putXmlData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);
		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals(xPath(putXmlData, "2"), xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
			assertEquals(String.valueOf(i - putCount), xPath(getDocumentData, TS_Tab + "[" + i + "]/@LVL"));
			assertEquals("RSHUBF ", xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVR"));
			assertEquals(appOrService, xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVC"));
			String getDatePath = xPath(getDocumentData, TS_Tab + "[" + i + "]");
			assertTrue(LocalDateTime.parse(getDatePath.substring(0, 14), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));
			assertEquals("000", getDatePath.substring(14, 17));

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

		putStringData = putStringData.replace("encoding=\"IBM-930\"", "encoding=\"UTF-8\"");

		listPass(putStringData, getStringData, nodeList);

	}

	default void errorQTest(MQMessage putMQMessage, MQMessage getMQMessage) throws IOException {

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
		String putStringData = mqMessageToString(putMQMessage);
		assertEquals(putStringData.replace(System.lineSeparator(), ""),
				getStringData.replace(System.lineSeparator(), ""));

	}

	default void deadQTest(MQMessage putMQMessage, MQMessage getMQMessage) throws IOException {

		// no changing
		assertEquals(putMQMessage.priority, getMQMessage.priority);
		assertEquals(putMQMessage.messageType, getMQMessage.messageType);
		assertEquals(MQC.MQFMT_DEAD_LETTER_HEADER, getMQMessage.format);
		assertEquals(getQmgr(), getMQMessage.replyToQueueManagerName.trim());
		assertEquals(putMQMessage.replyToQueueName, getMQMessage.replyToQueueName.trim());
		assertEquals(putMQMessage.applicationIdData.trim(), getMQMessage.applicationIdData.trim());
		assertEquals(546, getMQMessage.encoding);
		assertEquals(1208, getMQMessage.characterSet);

		// changing
		assertEquals(putMQMessage.expiry, getMQMessage.expiry);
		assertEquals(putMQMessage.persistence, getMQMessage.persistence);

	}

	default void returnMqTest(MQMessage putMQMessageData, MQMessage getMQMessageData)
			throws IOException, ParserConfigurationException, SAXException, XpathException, TransformerException {

		Document putDocumentXmlData = stringToDocument(mqMessageToString(putMQMessageData));
		assertNotNull(getMQMessageData);

		String getStringData = mqMessageToString(getMQMessageData);
		Document getDocumentData = stringToDocument(getStringData);

		boolean appF = "F".equals(getMQMessageData.applicationIdData.substring(1, 2));
		if (appF) {
			assertEquals(putMQMessageData.characterSet, getMQMessageData.characterSet);
		} else {
			assertEquals(1208, getMQMessageData.characterSet);
		}
		assertEquals(273, putMQMessageData.encoding);
		assertEquals(putMQMessageData.priority, getMQMessageData.priority);
		assertEquals(putMQMessageData.persistence, getMQMessageData.persistence);
		assertEquals(putMQMessageData.messageType, getMQMessageData.messageType);
		assertEquals(putMQMessageData.format.trim(), getMQMessageData.format.trim());
		assertEquals(putMQMessageData.expiry, getMQMessageData.expiry);
		assertEquals(putMQMessageData.applicationIdData.trim(), getMQMessageData.applicationIdData.trim());
		assertEquals("00", xPath(getDocumentData, RC_Tab));

		int putCount = xPathCount(putDocumentXmlData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);

		for (int i = 1; i <= putCount; i++) {
			assertEquals(xPath(putDocumentXmlData, TS_Tab + "[" + i + "]"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]"));
			assertEquals(xPath(putDocumentXmlData, TS_Tab + "[" + i + "]/@SVR"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVR"));
			assertEquals(xPath(putDocumentXmlData, TS_Tab + "[" + i + "]/@KBN"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
			assertEquals(xPath(putDocumentXmlData, TS_Tab + "[" + i + "]/@LVL"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@LVL"));
			assertEquals(xPath(putDocumentXmlData, TS_Tab + "[" + i + "]/@SVC"),
					xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVC"));

		}

		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals(getQmgr(), xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVR"));
			assertEquals(xPath(putDocumentXmlData, "2"), xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
			assertEquals(String.valueOf(getCount - (i - 1)), xPath(getDocumentData, TS_Tab + "[" + i + "]/@LVL"));
			assertEquals(putMQMessageData.applicationIdData, xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVC"));

			String getDatePath = xPath(getDocumentData, TS_Tab + "[" + i + "]");
			assertTrue(LocalDateTime.parse(getDatePath.substring(0, 14), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));

			assertEquals("000", getDatePath.substring(14, 17));

		}

		List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP"));

		assertEquals(xPath(putDocumentXmlData, R_DST_Tab), xPath(getDocumentData, R_DST_Tab));
		assertEquals(xPath(putDocumentXmlData, R_PVR_Tab), xPath(getDocumentData, R_PVR_Tab));
		assertEquals(getStringData.substring(getStringData.indexOf("encoding"),
				getStringData.indexOf("\"UTF-8\"") + "\"UTF-8\"".length()), "encoding=\"UTF-8\"");

		if (!appF) {
			getStringData = getStringData.replace("UTF-8", "IBM-930");

			listPass(documentToString(putDocumentXmlData), getStringData, nodeList);
		}
	}
}