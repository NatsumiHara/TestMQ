package jp.co.acom.fehub.tests;

import java.io.IOException;
import java.util.Objects;

import com.ibm.mq.MQException;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.base.internal.MQEnvironment;
import com.ibm.msg.client.wmq.compat.base.internal.MQGetMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;
import com.ibm.msg.client.wmq.compat.base.internal.MQPutMessageOptions;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueue;
import com.ibm.msg.client.wmq.compat.base.internal.MQQueueManager;

import jp.co.acom.fehub.mq.ConstantQname;

public interface Mq {

	public String getQmgr();

	public String getLocalhost();

	public String getCannal();

	public int getPort();

	public default void clean(String qName) throws Exception {
		MQMessage putQName;
		do {
			putQName = get(qName);
		} while (putQName != null);
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

	public default MQMessage putReplyMessages(String stringXmlData, int characterSet, String applicationIdData)
			throws IOException {
		MQMessage putMessage = new MQMessage();
		putMessage.characterSet = characterSet;
		putMessage.applicationIdData = applicationIdData;
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
			queue = qmgr.accessQueue(qName, MQC.MQOO_OUTPUT | MQC.MQOO_SET_IDENTITY_CONTEXT);
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
			queue = qmgr.accessQueue(qName, MQC.MQOO_INPUT_AS_Q_DEF);
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

	default String mqMessageToString(MQMessage mqMessage) throws IOException {
		StringBuilder builder = new StringBuilder();
		mqMessage.setDataOffset(0);
		while (mqMessage.getDataLength() > 0)
			builder.append(mqMessage.readLine()).append(System.lineSeparator());

		String stringMessage = builder.toString();
		return stringMessage;
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
		MQQueue mq = null;
		try {
			qmgr = new MQQueueManager(getQmgr());
			qmgr.accessQueue(qName, MQC.MQOO_SET).set(column, value, null);
			;
		} finally {
			if (Objects.nonNull(mq))
				mq.close();
			if (Objects.nonNull(qmgr))
				qmgr.disconnect();
		}
	}

}
