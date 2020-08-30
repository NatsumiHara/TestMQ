package com.example.utils;

import java.io.IOException;

import com.example.demo.KKK;
import com.ibm.mq.MQException;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.base.internal.MQEnvironment;
import com.ibm.msg.client.wmq.compat.base.internal.MQGetMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;
import com.ibm.msg.client.wmq.compat.base.internal.MQPutMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueue;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueueManager;

public interface Super extends FFFF {

	public static final String TS_Tab = "/CENTER/GLB_HEAD/TIMESTAMP/TS";
	public static final String D2_Tab = "/CENTER/FRAME/APL_DATA/D2";
	public static final String D1_Tab = "/CENTER/FRAME/APL_DATA/D1";
	public static final String R_DST_Tab = "/CENTER/GLB_HEAD/REPLY/R_DST";
	public static final String R_PVR_Tab = "/CENTER/GLB_HEAD/REPLY/R_PVR";
	public static final String RC_Tab = "/CENTER/GLB_HEAD/RC";
	public static final String SERVICEID_Tab = "/CENTER/GLB_HEAD/SERVICEID";

	public String getQmgr();

	public String getLocalhost();

	public String getCannal();

	public int getPort();

	public default void clean(String qName) throws Exception {
		MQMessage qu;
		do {
			qu = get(qName);
		} while (qu != null);
	}

	public default void setMQEnvironment() throws Exception {

		MQEnvironment.hostname = getLocalhost();
		MQEnvironment.channel = getCannal();
		MQEnvironment.port = getPort();
		MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);
	}

	public default MQMessage putMessages(String data) throws IOException {
		MQMessage putMessage = new MQMessage();
		putMessage.priority = 5;
		putMessage.characterSet = 1208;
		putMessage.replyToQueueManagerName = getQmgr();
		putMessage.format = MQC.MQFMT_STRING;
		putMessage.writeString(data);
		putMessage.persistence = 1;
		putMessage.messageId = MQC.MQMI_NONE;
		return putMessage;
	}

	public default void put(KKK constantQName, MQMessage putData) throws Exception {
		put(constantQName.getQNames(), putData);
	}

	public default void put(String qName, MQMessage putData) throws Exception {
		setMQEnvironment();
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		try {
			qmgr = new MQQueueManager(getQmgr());
			int openOption = MQC.MQOO_OUTPUT;
			queue = qmgr.accessQueue(qName, openOption);
			MQPutMessageOptions mqpmo = new MQPutMessageOptions();
			mqpmo.options = MQC.MQPMO_NO_SYNCPOINT;
			queue.put(putData, mqpmo);
		} finally {

			if (queue != null)
				queue.close();

			if (qmgr != null)
				qmgr.disconnect();

		}
	}

	public default MQMessage get(KKK constantQName) throws Exception {
		return get(constantQName.getQNames());
	}

	public default MQMessage get(String qName) throws Exception {

		MQMessage getMessage = new MQMessage();
		MQGetMessageOptions mqgmo = new MQGetMessageOptions();
		return get(qName, getMessage, mqgmo);
	}

	public default MQMessage get(String qName, byte[] putMessageId) throws Exception {

		MQGetMessageOptions mqgmo = new MQGetMessageOptions();
		mqgmo.matchOptions = MQC.MQMO_MATCH_CORREL_ID;
		MQMessage getMessage = new MQMessage();
		getMessage.correlationId = putMessageId;
		return get(qName, getMessage, mqgmo);
	}

	default MQMessage get(String qName, MQMessage getMessageInCorrelId, MQGetMessageOptions matchCorrelIdOption)
			throws Exception {
		setMQEnvironment();
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		try {
			qmgr = new MQQueueManager(getQmgr());
			int openOption = MQC.MQOO_INPUT_AS_Q_DEF;
			queue = qmgr.accessQueue(qName, openOption);
//			matchCorrelIdOption.options = MQC.MQGMO_NO_WAIT;
			matchCorrelIdOption.options = MQC.MQGMO_WAIT;
			matchCorrelIdOption.waitInterval = 1000;
			queue.get(getMessageInCorrelId, matchCorrelIdOption);
			return getMessageInCorrelId;
		} catch (MQException e) {
			if (e.reasonCode == 2033) {
				return null;
			} else {
				throw e;
			}

		} finally {
			if (queue != null)
				queue.close();

			if (qmgr != null)
				qmgr.disconnect();
		}
	}

	public default MQMessage get(KKK z, byte[] d) throws Exception {
		return get(z.getQNames(), d);
	}

//	default void extracted(MQMessage putMQMessageData, Document putXmlData, MQMessage getMQMessageData)
//			throws XpathException, ParserConfigurationException, SAXException, IOException, ParseException,
//			TransformerException {
//
//		assertNotNull(getMQMessageData);
//
//		String getStringData = mqMessageToString(getMQMessageData);
//		Document getDocumentData = stringToDocument(getStringData);
//
//		assertEquals(getQmgr(), getMQMessageData.replyToQueueManagerName.trim());
//		assertEquals(QL_DH_REP.getQNames(), getMQMessageData.replyToQueueName.trim());
//		String xServiceId = xPath(getDocumentData, SERVICEID_Tab);
//		boolean f = "F".equals(xServiceId.substring(1, 2));
//		if (f) {
//			assertEquals(putMQMessageData.characterSet, getMQMessageData.characterSet);
//		} else {
//			assertEquals(943, getMQMessageData.characterSet);
//		}
//		assertEquals(putMQMessageData.priority, getMQMessageData.priority);
//		assertEquals(putMQMessageData.persistence, getMQMessageData.persistence);
//		assertEquals(putMQMessageData.messageType, getMQMessageData.messageType);
//		assertEquals(putMQMessageData.format.trim(), getMQMessageData.format.trim());
//		assertEquals(putMQMessageData.expiry, getMQMessageData.expiry);
//		assertEquals(xPath(getDocumentData, SERVICEID_Tab), getMQMessageData.applicationIdData.trim());
//		assertEquals("00", xPath(getDocumentData, RC_Tab));
//		assertEquals(putMQMessageData.replyToQueueManagerName, xPath(getDocumentData, R_PVR_Tab));
//		assertEquals(putMQMessageData.replyToQueueName, xPath(getDocumentData, R_DST_Tab));
//		if (!f) {
//			assertEquals("¥¥", xPath(getDocumentData, D1_Tab));
//			assertEquals("‾‾", xPath(getDocumentData, D2_Tab));
//		}
//		int putCount = xPathCount(putXmlData, TS_Tab);
//		int getCount = xPathCount(getDocumentData, TS_Tab);
//
//		for (int i = 1; i <= putCount; i++) {
//			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]"), 
//					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]"));
//			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"), 
//					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"));
//			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"), 
//					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"));
//			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"), 
//					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"));
//			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"), 
//					xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"));
//		}
//
//		for (int i = putCount + 1; i <= getCount; i++) {
//			assertEquals("RSHUBFX", xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"));
//			assertEquals(xPath(putXmlData, "/CENTER/GLB_HEAD/KUBUN"), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"));
//			int ff = i - putCount;
//			String as = String.valueOf(ff);
//			assertEquals(as, xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"));
//			assertEquals(xPath(putXmlData, SERVICEID_Tab), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"));
//
//			String getDatePath = xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]");
//			String getDate = getDatePath.substring(0, 14);
//
//			System.out.println(getDate);
//			String getDateCorrelation = getDatePath.substring(14, 17);
//			System.out.println(getDateCorrelation);
//			assertTrue(LocalDateTime.parse(getDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
//					.isBefore(LocalDateTime.now()));
//
//			assertEquals("000", getDateCorrelation);
//
//			List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP", "REPLY"));
//			if (!f) {
//				nodeList.add("D1");
//				nodeList.add("D2");
//			}
//
//			if (!f)
//				getStringData = getStringData.replace("IBM-930", "UTF-8");
//
//			listPass(documentToString(putXmlData), getStringData, nodeList);
//		}
//		System.out.println(putCount);
//		System.out.println(getCount);
//
//	}
//
//	default void listPass(String putData, String getReplaceData, List<String> nodeList) {
//
//		final Diff diff = DiffBuilder.compare(putData).withTest(getReplaceData)
//				.withNodeFilter(node -> !nodeList.contains(node.getNodeName())).build();
//
//		Iterator<Difference> iter = diff.getDifferences().iterator();
//		int size = 0;
//		while (iter.hasNext()) {
//			System.out.println(iter.next().toString());
//			size++;
//		}
//		assertEquals(0, size);
//
//	}
//
//	default int xPathCount(Document data, String xmlPath) throws XpathException {
//		return XMLUnit.newXpathEngine().getMatchingNodes(xmlPath, data).getLength();
//	}
//
//	default String xPath(Document getData, String xmlPath) throws XpathException {
//
//		XpathEngine xp = XMLUnit.newXpathEngine();
//		return xp.evaluate(xmlPath, getData);
//	}
//
//	default void returnQTest(MQMessage putMQMessage, Document putXmlData, MQMessage getMQMessage)
//			throws IOException, ParserConfigurationException, SAXException, Exception {
//
//		String getStringData = mqMessageToString(getMQMessage);
//		Document getDocumentData = stringToDocument(getStringData);
//
//		String xServiceId = xPath(getDocumentData, SERVICEID_Tab);
//		boolean a = "D".equals(xServiceId.substring(0, 1));
//		if (!a) {
//			assertEquals("01", xPath(getDocumentData, RC_Tab));
//		}
//		boolean b = "F".equals(xServiceId.substring(1, 2)) || "L".equals(xServiceId.substring(1, 2));
//		if (!b) {
//			assertEquals("02", xPath(getDocumentData, RC_Tab));
//		}
//		int putCount = xPathCount(putXmlData, TS_Tab);
//		int getCount = xPathCount(getDocumentData, TS_Tab);
//		for (int i = putCount + 1; i <= getCount; i++) {
//			assertEquals(xPath(putXmlData, "2"), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"));
//			int ff = i - putCount;
//			String as = String.valueOf(ff);
//			assertEquals(as, xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"));
//			assertEquals("RSHUBF ", xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"));
//			assertEquals(xPath(putXmlData, SERVICEID_Tab), xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"));
//
//			String getDatePath = xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]");
//			String getDate = getDatePath.substring(0, 14);
//			String getDateCorrelation = getDatePath.substring(14, 17);
//			assertTrue(LocalDateTime.parse(getDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
//					.isBefore(LocalDateTime.now()));
//
//			assertEquals("000", getDateCorrelation);
//
//		}
//
//		// changing
//		assertEquals(273, getMQMessage.encoding);
//		assertEquals(1208, getMQMessage.characterSet);
//
//		// no changing
//		assertEquals(putMQMessage.priority, getMQMessage.priority);
//		assertEquals(putMQMessage.persistence, getMQMessage.persistence);
//		assertEquals(putMQMessage.messageType, getMQMessage.messageType);
//		assertEquals(putMQMessage.format.trim(), getMQMessage.format.trim());
//		assertEquals(putMQMessage.expiry, getMQMessage.expiry);
//		assertEquals(getQmgr(), getMQMessage.replyToQueueManagerName.trim());
//		assertEquals(putMQMessage.replyToQueueName, getMQMessage.replyToQueueName.trim());
//		assertEquals(putMQMessage.applicationIdData.trim(), getMQMessage.applicationIdData.trim());
//
//		List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP"));
//
//		listPass(documentToString(putXmlData), getStringData, nodeList);
//	}
//
//	default void errorQTest(MQMessage putMQMessage, String putXmlData, MQMessage getMQMessage) throws IOException {
//
//		// no changing
//		assertEquals(putMQMessage.priority, getMQMessage.priority);
//		assertEquals(putMQMessage.messageType, getMQMessage.messageType);
//		assertEquals(putMQMessage.format.trim(), getMQMessage.format.trim());
//		assertEquals(getQmgr(), getMQMessage.replyToQueueManagerName.trim());
//		assertEquals(putMQMessage.replyToQueueName, getMQMessage.replyToQueueName.trim());
//		assertEquals(putMQMessage.applicationIdData.trim(), getMQMessage.applicationIdData.trim());
//		assertEquals(getMQMessage.encoding, putMQMessage.encoding);
//		assertEquals(getMQMessage.characterSet, putMQMessage.characterSet);
//
//		// changing
//		assertEquals(MQC.MQEI_UNLIMITED, getMQMessage.expiry);
//		assertEquals(MQC.MQPER_PERSISTENT, getMQMessage.persistence);
//
//		String getStringData = mqMessageToString(getMQMessage);
//		assertEquals(putXmlData.replace(System.lineSeparator(), ""), getStringData.replace(System.lineSeparator(), ""));
//
//	}
}
