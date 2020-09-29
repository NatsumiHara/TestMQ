package com.example.tests;

import java.io.IOException;
import java.util.Objects;

import com.example.mq.ConstantQname;
import com.ibm.mq.MQException;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.base.internal.MQEnvironment;
import com.ibm.msg.client.wmq.compat.base.internal.MQGetMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;
import com.ibm.msg.client.wmq.compat.base.internal.MQPutMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueue;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueueManager;

public interface Mq extends Xml {

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

	public default MQMessage putReplyMessages(String stringXmlData, String getQName) throws IOException {
		MQMessage putMessage = new MQMessage();
		if (!"F".equals(getQName)) {
			stringXmlData = stringXmlData.replace("encoding=\"UTF-8\"", "encoding=\"IBM-930\"");
			putMessage.characterSet = 943;

			String d1D2 = stringXmlData.substring(stringXmlData.indexOf("<D1>"),
					stringXmlData.indexOf("</D2>") + "</D2>".length());
			stringXmlData = stringXmlData.replace(d1D2, "");
		} else {
			putMessage.characterSet = 1208;
		}
		putMessage.applicationIdData = "D" + getQName;
		putMessage.priority = 5;
		putMessage.format = MQC.MQFMT_STRING;
		putMessage.writeString(stringXmlData);
		putMessage.persistence = 1;
		putMessage.messageId = MQC.MQMI_NONE;
		return putMessage;

	}

	public default MQMessage putReplyMessagesAppId(String stringXmlData, String appId) throws IOException {
		MQMessage putMessage = new MQMessage();

		if (!"DF".equals(appId)) {
			stringXmlData = stringXmlData.replace("encoding=\"UTF-8\"", "encoding=\"IBM-930\"");
			putMessage.characterSet = 943;

			String d1D2 = stringXmlData.substring(stringXmlData.indexOf("<D1>"),
					stringXmlData.indexOf("</D2>") + "</D2>".length());
			stringXmlData = stringXmlData.replace(d1D2, "");
		} else {
			putMessage.characterSet = 1208;
		}

		putMessage.applicationIdData = appId;
		putMessage.priority = 5;
		putMessage.format = MQC.MQFMT_STRING;
		putMessage.writeString(stringXmlData);
		putMessage.persistence = 1;
		putMessage.messageId = MQC.MQMI_NONE;

		return putMessage;
	}

	public default void put(ConstantQname constantQName, MQMessage putData) throws Exception {
		put(constantQName.getQNames(), putData);
	}

	public default void put(String qName, MQMessage putData) throws Exception {
		setMQEnvironment();
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		try {
			qmgr = new MQQueueManager(getQmgr());
			int openOption = MQC.MQOO_OUTPUT | MQC.MQOO_SET_IDENTITY_CONTEXT;
			queue = qmgr.accessQueue(qName, openOption);
			MQPutMessageOptions mqpmo = new MQPutMessageOptions();
			mqpmo.options = MQC.MQPMO_NO_SYNCPOINT | MQC.MQPMO_SET_IDENTITY_CONTEXT;
			queue.put(putData, mqpmo);
		} finally {

			if (queue != null)
				queue.close();

			if (qmgr != null)
				qmgr.disconnect();

		}
	}

	public default MQMessage get(ConstantQname constantQName) throws Exception {
		return get(constantQName.getQNames());
	}

	public default MQMessage get(String qName) throws Exception {

		MQMessage getMessage = new MQMessage();
		MQGetMessageOptions mqgmo = new MQGetMessageOptions();
		return get(qName, getMessage, mqgmo);
	}

	public default MQMessage get_wait(String qName) throws Exception {

		MQMessage getMessage = new MQMessage();
		MQGetMessageOptions mqgmo = new MQGetMessageOptions();
		mqgmo.options = MQC.MQGMO_WAIT;
		mqgmo.waitInterval = 10000;
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

	public default MQMessage get(ConstantQname constantQname, byte[] messageId) throws Exception {
		return get(constantQname.getQNames(), messageId);
	}

	default void putEnabled(String qName) throws Exception {
		this.alterQueue(qName, new int[] { MQC.MQIA_INHIBIT_PUT }, new int[] { MQC.MQQA_PUT_ALLOWED });
	}

	default void putDisabled(String qName) throws Exception {
		this.alterQueue(qName, new int[] { MQC.MQIA_INHIBIT_PUT }, new int[] { MQC.MQQA_PUT_INHIBITED });
	}

	default void alterQueue(String qName, int[] column, int[] value) throws Exception {
		this.setMQEnvironment();
		MQQueueManager qmgr = null;
		MQQueue q = null;
		try {
			qmgr = new MQQueueManager(getQmgr());
			qmgr.accessQueue(qName, MQC.MQOO_SET).set(column, value, null);
			;
		} finally {
			if (Objects.nonNull(q))
				q.close();
			if (Objects.nonNull(qmgr))
				qmgr.disconnect();
		}
	}

}
