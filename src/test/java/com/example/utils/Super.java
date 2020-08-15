package com.example.utils;

import static com.example.demo.KKK.QL_DH_REP;
import static com.example.demo.KKK.QMFH01;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.xpath.XPathEngine;

import com.example.demo.KKK;
import com.ibm.disthub2.impl.formats.Envelop.Constants.pubendsTable_type;
import com.ibm.mq.MQException;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.base.internal.MQEnvironment;
import com.ibm.msg.client.wmq.compat.base.internal.MQGetMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;
import com.ibm.msg.client.wmq.compat.base.internal.MQPutMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueue;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueueManager;

public interface Super extends FFFF  {

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

	public default void put(KKK z, MQMessage r) throws Exception {
		put(z.getQNames(), r);
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

	public default MQMessage get(KKK y) throws Exception {
//		MQMessage a = y.getQNames();
//		return ;
//		MQMessage a = get(y.getQNames());
		return get(y.getQNames());
	}

	public default MQMessage get(String qName) throws Exception {

		MQMessage getMessage = new MQMessage();
		MQGetMessageOptions mqgmo = new MQGetMessageOptions();
		return get(qName, getMessage, mqgmo);
	}

	public default MQMessage get(String qName, byte[] g) throws Exception {

		MQGetMessageOptions mqgmo = new MQGetMessageOptions();
		mqgmo.matchOptions = MQC.MQMO_MATCH_CORREL_ID;
		MQMessage getMessage = new MQMessage();
		getMessage.correlationId = g;
		return get(qName, getMessage, mqgmo);
	}

	default MQMessage get(String q, MQMessage a, MQGetMessageOptions b) throws Exception {
		setMQEnvironment();
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		try {
			qmgr = new MQQueueManager(getQmgr());
			int openOption = MQC.MQOO_INPUT_AS_Q_DEF;
			queue = qmgr.accessQueue(q, openOption);
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

	default void extracted(MQMessage putData, MQMessage getData) throws XpathException, ParserConfigurationException, SAXException, IOException {
		assertThat(getData.replyToQueueManagerName.trim()).isEqualTo(getQmgr());
		assertThat(getData.replyToQueueName.trim()).isEqualTo(QL_DH_REP.getQNames());
		assertThat(getData.characterSet).isEqualTo(943);
		assertThat(getData.priority).isEqualTo(putData.priority);
		assertThat(getData.persistence).isEqualTo(putData.persistence);
		assertThat(getData.messageType).isEqualTo(putData.messageType);
		assertThat(getData.format.trim()).isEqualTo(putData.format.trim());
		assertThat(getData.expiry).isEqualTo(putData.expiry);
		String a= mqMessageToString(getData);
		Document d =  stringToDocument(a);
		assertThat(getData.applicationIdData.trim()).isEqualTo(xPath(d, "/CENTER/GLB_HEAD/SERVICEID"));
		assertThat(xPath(d, "/CENTER/GLB_HEAD/RC")).isEqualTo("00");
	}

	default String xPath(Document data,String xmlPath) throws XpathException {
		
		XpathEngine xp = XMLUnit.newXpathEngine();
//		Document doc = XMLUnit.builControlDocument(data);
		return xp.evaluate(xmlPath, data);
	}
}
