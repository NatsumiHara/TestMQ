package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.bind.DatatypeConverter;

//import static org.hamcrest.CoreMatchers.containsString;
//import static org.hamcrest.CoreMatchers.is;
import static com.example.demo.KKK.*;

//import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.utils.FFFF;
import com.example.utils.Super;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

class RizaSampleApplicationTests2 implements Super, FFFF {

	@BeforeEach
	void before() throws Exception {

//		String qNames = QA_DH_DL.getQNames();

		for (KKK k : values())
			clean(k.getQNames());
//
//		List<String> qName = new ArrayList<>();
//		qName.add(QA_DH_DL.getQNames());
//		qName.add(QL_DH_ERR.getQNames());
//		qName.add(QL_DW_REP.getQNames());
//		qName.add(QL_DH_HTTP_LSR.getQNames());
//		
//		int size = qName.size();
//
//		for (int i = 0; i < size; i++) {
//			String a = qName.get(i);
//			System.out.println(a);
//			clean(a);
//		}
	}

	@Test
	@DisplayName("QCからDL")
	void contextLoadsQcqa() throws Exception {

		String data = fileToString(TEST2_XML);
		MQMessage putData = putMessages(data);
		put(QC_DH_REQ, putData);
		System.out.println(DatatypeConverter.printHexBinary(putData.messageId));

		MQMessage getData = get(QA_DH_DL, putData.messageId);
//		MQMessage getData = get(QA_DH_DL);
		String strMessage = getData.readLine();
		System.out.println(strMessage);
		extracted(putData, getData);

//		String getMessageId = new String(getData.correlationId, UTF8);
//		String putMessageId = new String(putData.messageId, UTF8);
//		assertThat(getMessageId).isEqualTo(putMessageId);

	}

//	public void extracted(MQMessage putData, MQMessage getData) {
//		assertThat(getData.replyToQueueManagerName.trim()).isEqualTo(QMFH01);
//		assertThat(getData.replyToQueueName.trim()).isEqualTo(QL_DH_REP.getQNames());
//		assertThat(getData.characterSet).isEqualTo(943);
//		assertThat(getData.priority).isEqualTo(putData.priority);
//		assertThat(getData.persistence).isEqualTo(putData.persistence);
//		assertThat(getData.messageType).isEqualTo(putData.messageType);
//		assertThat(getData.format.trim()).isEqualTo(putData.format.trim());
//		assertThat(getData.expiry).isEqualTo(putData.expiry);
//	}

	@Test
	@DisplayName("QLからDL")
	void contextLoadsQlqa() throws Exception {

		String data = fileToString(TEST2_XML);
		MQMessage putData = putMessages(data);
		put(QL_DH_REQ, putData);
		System.out.println(DatatypeConverter.printHexBinary(putData.messageId));

		MQMessage getData = get(QA_DH_DL, putData.messageId);
//		MQMessage getData = get(QA_DH_DL);

//		System.out.println(getData.replyToQueueManagerName);
//		System.out.println(getData.replyToQueueName);
//		System.out.println(getData.characterSet);
//		System.out.println(getData.messageId);
//		System.out.println(getData.correlationId);
//		System.out.println(getData.expiry);
//		System.out.println(getData.format);
//		System.out.println(getData.messageType);
//		System.out.println(getData.persistence);
//		System.out.println(getData.priority);
		String strMessage = getData.readLine();
		System.out.println(strMessage);
//		assertThat(getData.replyToQueueManagerName.trim()).isEqualTo(QMFH01);
//		assertThat(getData.replyToQueueName.trim()).isEqualTo(QL_DH_REP.getQNames());
//		assertThat(getData.characterSet).isEqualTo(943);
//		assertThat(getData.priority).isEqualTo(putData.priority);
//		assertThat(getData.persistence).isEqualTo(putData.persistence);
//		assertThat(getData.messageType).isEqualTo(putData.messageType);
//		assertThat(getData.format.trim()).isEqualTo(putData.format.trim());
//		assertThat(getData.expiry).isEqualTo(putData.expiry);

//		System.out.println(getData.correlationId);
//		System.out.println(putData.correlationId);
//		System.out.println(putData.messageId);
//		System.out.println(getData.messageId);
//		assertThat(getData.correlationId).isEqualTo(putData.messageId);

//		String getMessageId = new String(getData.correlationId, UTF8);
//		String putMessageId = new String(fff.messageId, UTF8);
//		assertThat(getMessageId).isEqualTo(putMessageId);
		extracted(putData, getData);
	}

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

}
