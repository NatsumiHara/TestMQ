package com.example.utils;

import static com.example.demo.KKK.QL_DH_REP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
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
//		MQMessage qu = get("QA.DH.DL");
//
//		while (qu != null) {
//			qu = get("QA.DH.DL");
//		}
		do {
			qu = get(qName);
		} while (qu != null);
	}

	public default void setMQEnvironment() throws Exception {

		MQEnvironment.hostname = getLocalhost();
		MQEnvironment.channel = getCannal();
		MQEnvironment.port = getPort();
//		MQEnvironment.CCSID = 1208;
		MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);
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
//			MQMessage putMessage = new MQMessage();
//			putMessage.priority = 5;
//			putMessage.characterSet = 1208;
//			putMessage.replyToQueueManagerName = "QMDW01";
//			putMessage.format=MQC.MQFMT_STRING;
//			putMessage.replyToQueueName = "QL.DW.REP";
//			putMessage.writeString(data);
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
//		MQMessage a = y.getQNames();
//		return ;
//		MQMessage a = get(y.getQNames());
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

	default MQMessage get(String qName, MQMessage a, MQGetMessageOptions b) throws Exception {
		setMQEnvironment();
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		try {
			qmgr = new MQQueueManager(getQmgr());
			int openOption = MQC.MQOO_INPUT_AS_Q_DEF;
			queue = qmgr.accessQueue(qName, openOption);
			b.options = MQC.MQGMO_NO_WAIT;
//			MQGetMessageOptions mqgmo = new MQGetMessageOptions();
//			mqgmo.matchOptions = MQC.MQMO_MATCH_CORREL_ID;
//			MQMessage getMessage = new MQMessage();
//			getMessage.correlationId = g;

			queue.get(a, b);
			return a;
		} catch (MQException e) {
//			System.out.println(e.reasonCode);
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

	public default MQMessage putMessages(String data) throws IOException {
		MQMessage putMessage = new MQMessage();
		putMessage.priority = 5;
		putMessage.characterSet = 1208;
		putMessage.replyToQueueManagerName = "QMDW01";
		putMessage.format = MQC.MQFMT_STRING;
		putMessage.replyToQueueName = "QL.DW.REP";
		putMessage.writeString(data);
		putMessage.persistence = 1;
//		putMessage.messageId = "99999".getBytes();
		putMessage.messageId = MQC.MQMI_NONE;
		return putMessage;
	}

	default void extracted(MQMessage putData, Document data, MQMessage getMQMessageData) throws XpathException,
			ParserConfigurationException, SAXException, IOException, ParseException, TransformerException {

		String getStringData = mqMessageToString(getMQMessageData);
		Document getDocumentData = stringToDocument(getStringData);

		assertThat(getMQMessageData.replyToQueueManagerName.trim()).isEqualTo(getQmgr());
		assertThat(getMQMessageData.replyToQueueName.trim()).isEqualTo(QL_DH_REP.getQNames());
		String xServiceId = xPath(getDocumentData, SERVICEID_Tab);
//		String xServiceId =null;
		boolean f = "F".equals(xServiceId.substring(1, 2));
//		System.out.println("kkkkkkkkkkkkkk" + serviceId);
		if (f) {
			assertThat(getMQMessageData.characterSet).isEqualTo(putData.characterSet);
		} else {
			assertThat(getMQMessageData.characterSet).isEqualTo(943);
		}
		assertThat(getMQMessageData.priority).isEqualTo(putData.priority);
		assertThat(getMQMessageData.persistence).isEqualTo(putData.persistence);
		assertThat(getMQMessageData.messageType).isEqualTo(putData.messageType);
		assertThat(getMQMessageData.format.trim()).isEqualTo(putData.format.trim());
		assertThat(getMQMessageData.expiry).isEqualTo(putData.expiry);
//		String a = mqMessageToString(getData);
//
//		System.out.println(a);
//		Document d = stringToDocument(a);
//		String b = mqMessageToString(putData);
//		Document c = stringToDocument(b);
		assertThat(getMQMessageData.applicationIdData.trim()).isEqualTo(xPath(getDocumentData, SERVICEID_Tab));
		assertThat(xPath(getDocumentData, RC_Tab)).isEqualTo("00");
		assertThat(xPath(getDocumentData, R_PVR_Tab)).isEqualTo(putData.replyToQueueManagerName);
		assertThat(xPath(getDocumentData, R_DST_Tab)).isEqualTo(putData.replyToQueueName);
		if (!f) {
			assertThat(xPath(getDocumentData, D1_Tab)).isEqualTo("¥¥");
			assertThat(xPath(getDocumentData, D2_Tab)).isEqualTo("‾‾");
		}
		int putCount = xPathCount(data, TS_Tab);
		int getCount = xPathCount(getDocumentData, TS_Tab);
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[2]/@LVL")).isEqualTo("1");
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[3]/@LVL")).isEqualTo("2");
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[2]/@KBN")).isEqualTo(xPath(data, "/CENTER/GLB_HEAD/KUBUN"));
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[3]/@KBN")).isEqualTo(xPath(data, "/CENTER/GLB_HEAD/KUBUN"));
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[2]/@SVR")).isEqualTo("RSHUBFX");
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[3]/@SVR")).isEqualTo("RSHUBFX");
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[2]/@SVC"))
//				.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/SERVICEID"));
//		assertThat(xPath(d, "/CENTER/GLB_HEAD/TIMESTAMP/TS[3]/@SVC"))
//				.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/SERVICEID"));

		for (int i = 1; i <= putCount; i++) {
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]"))
					.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]"));
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"))
					.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR"));
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"))
					.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"));
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"))
					.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL"));
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"))
					.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"));
		}

		for (int i = putCount + 1; i <= getCount; i++) {
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVR")).isEqualTo("RSHUBFX");
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@KBN"))
					.isEqualTo(xPath(data, "/CENTER/GLB_HEAD/KUBUN"));
			int ff = i - putCount;
			String as = String.valueOf(ff);
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@LVL")).isEqualTo(as);
			assertThat(xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]/@SVC"))
					.isEqualTo(xPath(data, SERVICEID_Tab));

//			LocalDateTime nowDateTime = LocalDateTime.now(); 
//			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
			String getDatePath = xPath(getDocumentData, "/CENTER/GLB_HEAD/TIMESTAMP/TS[" + i + "]");
			String getDate = getDatePath.substring(0, 14);

//	        Date tsData = sd.parse(ts);
			System.out.println(getDate);
			String getDateCorrelation = getDatePath.substring(14, 17);
			System.out.println(getDateCorrelation);
			assertTrue(LocalDateTime.parse(getDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
					.isBefore(LocalDateTime.now()));

			assertThat(getDateCorrelation).isEqualTo("000");

			//
//			List<String> nodeList = new ArrayList<>();
			List<String> nodeList = new ArrayList<>(Arrays.asList("RC", "TIMESTAMP", "REPLY"));
			if (!f) {
				nodeList.add("D1");
				nodeList.add("D2");
			}
//			nodeList.add("GGG");
//			nodeList.add("TIMESTAMP");
//			nodeList.add("REPLY");

			if (!f)
				getStringData = getStringData.replace("IBM-930", "UTF-8");

			listPass(documentToString(data), getStringData, nodeList);

			//			List<String> list = ArrayList<String>();
//			list.add("RC");
//			list.add("TIMESTAMP");
//			list.add("REPLY");
//
//			final Diff diff = DiffBuilder.compare(data).withTest(d)
//					.withNodeFilter(node -> !node.getNodeName().equals("RC")&&!node.getNodeName().equals("TIMESTAMP")&&!node.getNodeName().equals("REPLY")).build();
//			Iterator<Difference> iter = diff.getDifferences().iterator();
//			int size = 0;
//			while (iter.hasNext()) {
//				System.out.println(iter.next().toString());
//				size++;
//			}
//			assertThat(size).isEqualTo(0);

		}
//		2020 08 18 13 18 03 000
		System.out.println(putCount);
		System.out.println(getCount);

	}

	default void listPass(String putData, String getReplaceData, List<String> nodeList) {

		final Diff diff = DiffBuilder.compare(putData).withTest(getReplaceData)
//				.withNodeFilter(node -> !node.getNodeName().equals("RC")&&!node.getNodeName().equals("TIMESTAMP")&&!node.getNodeName().equals("REPLY")).build();
				.withNodeFilter(node -> !nodeList.contains(node.getNodeName())).build();

		Iterator<Difference> iter = diff.getDifferences().iterator();
		int size = 0;
		while (iter.hasNext()) {
			System.out.println(iter.next().toString());
			size++;
		}
		assertThat(size).isEqualTo(0);

	}

	default int xPathCount(Document data, String xmlPath) throws XpathException {
		return XMLUnit.newXpathEngine().getMatchingNodes(xmlPath, data).getLength();
	}

	default String xPath(Document getData, String xmlPath) throws XpathException {

		XpathEngine xp = XMLUnit.newXpathEngine();
//		Document doc = XMLUnit.builControlDocument(data);
		return xp.evaluate(xmlPath, getData);
	}
}
