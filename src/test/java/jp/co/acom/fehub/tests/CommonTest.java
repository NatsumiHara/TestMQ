package jp.co.acom.fehub.tests;

import static jp.co.acom.fehub.mq.ConstantQname.QL_DH_REP;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;

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

	default void mqNoChangeTest(List<String> mqmd, MQMessage putMQMessage, MQMessage getMQMessage) {
		assertAll(

				() -> {
					if (!mqmd.contains("priority"))
						assertEquals(putMQMessage.priority, getMQMessage.priority);
				},

				() -> {
					if (!mqmd.contains("persistence"))
						assertEquals(putMQMessage.persistence, getMQMessage.persistence);
				},

				() -> {
					if (!mqmd.contains("messageType"))
						assertEquals(putMQMessage.messageType, getMQMessage.messageType);
				},

				() -> {
					if (!mqmd.contains("format"))
						assertEquals(putMQMessage.format.trim(), getMQMessage.format.trim());
				},

				() -> {
					if (!mqmd.contains("expiry"))
						assertEquals(putMQMessage.expiry, getMQMessage.expiry);
				},

				() -> {
					if (!mqmd.contains("replyToQueueManagerName"))
						assertEquals(getQmgr(), getMQMessage.replyToQueueManagerName.trim());
				},

				() -> {
					if (!mqmd.contains("replyToQueueName"))
						assertEquals(putMQMessage.replyToQueueName, getMQMessage.replyToQueueName.trim());
				},

				() -> {
					if (!mqmd.contains("applicationIdData"))
						assertEquals(putMQMessage.applicationIdData.trim(), getMQMessage.applicationIdData.trim());
				},

				() -> {
					if (!mqmd.contains("characterSet"))
						assertEquals(putMQMessage.characterSet, getMQMessage.characterSet);
				},

				() -> {
					if (!mqmd.contains("encoding"))
						assertEquals(putMQMessage.encoding, getMQMessage.encoding);
				},

				() -> {
					if (!mqmd.contains("expiry"))
						assertEquals(putMQMessage.expiry, getMQMessage.expiry);
				}

		);
	}

	default void requestQTest(MQMessage putMQMessageData, MQMessage getMQMessageData) throws Exception {
		
		assertNotNull(getMQMessageData,"GETの結果メッセージが空でした");

		String getStringData = mqMessageToString(getMQMessageData);
		Document getDocumentData = stringToDocument(getStringData);
		String putStringData = mqMessageToString(putMQMessageData);
		Document putDocumentData = stringToDocument(putStringData);

		boolean serviceF = "F".equals(xPath(putDocumentData, SERVICEID_Tab).substring(1, 2));
		if (serviceF) {
			assertEquals(putMQMessageData.characterSet, getMQMessageData.characterSet);
		} else {
			assertEquals(943, getMQMessageData.characterSet);
		}
		assertEquals(xPath(putDocumentData, SERVICEID_Tab), getMQMessageData.applicationIdData.trim());
		assertEquals(QL_DH_REP.getQNames(), getMQMessageData.replyToQueueName.trim());

		//no change
		List<String> mqmd = new ArrayList<>(Arrays.asList("replyToQueueName", "applicationIdData", "characterSet"));
		mqNoChangeTest(mqmd, putMQMessageData, getMQMessageData);

		assertEquals("00", xPath(getDocumentData, RC_Tab));
		if (!serviceF) {
			assertEquals("¥¥", xPath(getDocumentData, D1_Tab));
			assertEquals("‾‾", xPath(getDocumentData, D2_Tab));
		}
		int putCount = xPathCount(putDocumentData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);

		// existiongTS
		existingTimestamp(putDocumentData, getDocumentData, putCount);

		// additionTS
		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals("RSHUBFX", xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVR"));
			assertEquals("1", xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
			assertEquals(String.valueOf(i - putCount), xPath(getDocumentData, TS_Tab + "[" + i + "]/@LVL"));
			assertEquals(xPath(putDocumentData, SERVICEID_Tab), xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVC"));
			String getDatePath = xPath(getDocumentData, TS_Tab + "[" + i + "]");
			assertTrue(LocalDateTime.parse(getDatePath.substring(0, 14), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));
			assertEquals("000", getDatePath.substring(14, 17));

		}

		List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP"));
		if (!serviceF) {
			nodeList.add("D1");
			nodeList.add("D2");
		}
		boolean requestIdNull = 0 == xPathCount(putDocumentData, REQUESTID_Tab);
		if (requestIdNull) {
			nodeList.add("REQUESTID");
			assertEquals(1, xPathCount(getDocumentData, REQUESTID_Tab));
		}

		boolean rep = xPathCount(putDocumentData, REPLY_Tab) == 1;
		if (!rep) {
			nodeList.add("REPLY");
			assertEquals(putMQMessageData.replyToQueueName, xPath(getDocumentData, R_DST_Tab));
			assertEquals(putMQMessageData.replyToQueueManagerName, xPath(getDocumentData, R_PVR_Tab));

		}
		if (!serviceF)
			getStringData = getStringData.replace("IBM-930", "UTF-8");

		List<String> differenceList = listPass(documentToString(putDocumentData), getStringData, nodeList);
		assertTrue(differenceList.isEmpty(), differenceList.toString());
			
	}

	default void responseReturnQTest(MQMessage putMQMessage, MQMessage getMQMessage, String rc, String appOrService)
			throws Exception {
		String putStringData = mqMessageToString(putMQMessage);
		Document putXmlData = stringToDocument(putStringData);

		String getStringData = mqMessageToString(getMQMessage);
		Document getDocumentData = stringToDocument(getStringData);

		assertEquals(rc, xPath(getDocumentData, RC_Tab));

		int putCount = xPathCount(putXmlData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);

		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals("2", xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
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
		List<String> mqmd = new ArrayList<>(Arrays.asList("encoding", "characterSet"));
		mqNoChangeTest(mqmd, putMQMessage, getMQMessage);
		
		List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP"));
		putStringData = putStringData.replace("encoding=\"IBM-930\"", "encoding=\"UTF-8\"");

		List<String> differenceList = listPass(putStringData, getStringData, nodeList);
		assertTrue(differenceList.isEmpty(), differenceList.toString());

	}

	default void errorQTest(MQMessage putMQMessage, MQMessage getMQMessage) throws IOException {
		
		assertNotNull(getMQMessage,"GETの結果メッセージが空でした");

		// no changing
		List<String> mqmd = new ArrayList<>(Arrays.asList("expiry", "persistence"));
		mqNoChangeTest(mqmd, putMQMessage, getMQMessage);

		// changing
		assertEquals(MQC.MQEI_UNLIMITED, getMQMessage.expiry);
		assertEquals(MQC.MQPER_PERSISTENT, getMQMessage.persistence);

		String getStringData = mqMessageToString(getMQMessage);
		String putStringData = mqMessageToString(putMQMessage);
		assertEquals(putStringData,getStringData);

	}

	default void deadQTest(MQMessage putMQMessage, MQMessage getMQMessage) throws IOException {

		// no changing
		List<String> mqmd = new ArrayList<>(Arrays.asList("format", "encoding", "characterSet"));
		mqNoChangeTest(mqmd, putMQMessage, getMQMessage);

		// changing
		assertEquals(MQC.MQFMT_DEAD_LETTER_HEADER, getMQMessage.format);
		assertEquals(546, getMQMessage.encoding);
		assertEquals(1208, getMQMessage.characterSet);
		

	}

	default void responseQTest(MQMessage putMQMessageData, MQMessage getMQMessageData)
			throws Exception {

		Document putDocumentXmlData = stringToDocument(mqMessageToString(putMQMessageData));
		assertNotNull(getMQMessageData, "GETの結果メッセージが空でした");

		String getStringData = mqMessageToString(getMQMessageData);
		Document getDocumentData = stringToDocument(getStringData);

		//change
		boolean appF = "F".equals(putMQMessageData.applicationIdData.substring(1, 2));
		if (appF) {
			assertEquals(putMQMessageData.characterSet, getMQMessageData.characterSet);
		} else {
			assertEquals(1208, getMQMessageData.characterSet);
		}
		assertEquals(273, putMQMessageData.encoding);
		
		//no change
		List<String> mqmd = new ArrayList<>(Arrays.asList("encoding", "characterSet"));
		mqNoChangeTest(mqmd, putMQMessageData, getMQMessageData);
		
		assertEquals("00", xPath(getDocumentData, RC_Tab));
		int putCount = xPathCount(putDocumentXmlData, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);

		// existiongTS
		existingTimestamp(putDocumentXmlData, getDocumentData, putCount);
		// additionTS

		for (int i = putCount + 1; i <= getCount; i++) {
			assertEquals(getQmgr(), xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVR"));
			assertEquals("2", xPath(getDocumentData, TS_Tab + "[" + i + "]/@KBN"));
			assertEquals(String.valueOf(getCount - (i - 1)), xPath(getDocumentData, TS_Tab + "[" + i + "]/@LVL"));
			assertEquals(putMQMessageData.applicationIdData, xPath(getDocumentData, TS_Tab + "[" + i + "]/@SVC"));

			String getDatePath = xPath(getDocumentData, TS_Tab + "[" + i + "]");
			assertTrue(LocalDateTime.parse(getDatePath.substring(0, 14), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));

			assertEquals("000", getDatePath.substring(14, 17));

		}

		List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP"));

		assertEquals(getStringData.substring(getStringData.indexOf("encoding"),
				getStringData.indexOf("\"UTF-8\"") + "\"UTF-8\"".length()), "encoding=\"UTF-8\"");

		if (!appF) {
			getStringData = getStringData.replace("UTF-8", "IBM-930");

			List<String> differenceList = listPass(documentToString(putDocumentXmlData), getStringData, nodeList);
			assertTrue(differenceList.isEmpty(), differenceList.toString());
		}
	}

	default void existingTimestamp(Document putXmlData, Document getDocumentData, int putCount) throws XpathException {
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
	}
}