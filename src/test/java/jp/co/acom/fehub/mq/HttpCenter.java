package jp.co.acom.fehub.mq;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jp.co.acom.fehub.mq.ConstantQname.*;

import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

import jp.co.acom.fehub.tests.CommonTest;
import jp.co.acom.fehub.tests.XmlHttp;

public class HttpCenter extends XmlHttp implements CommonTest{
	
	@Test
	@DisplayName("要求テスト")
	void requestTest() throws Exception {
		
		String replaceServiceId = replaceSerId(fileToString(TS0_XML), "F000000000");
		MQMessage putData = putMessages(replaceServiceId);
		putData.replyToQueueName = QL_DH_REP.getQNames();
		putData.applicationIdData="DF000000000";
		putData.characterSet=1208;
		putData.messageId=MQC.MQMI_NONE;
		putData.correlationId=MQC.MQMI_NONE;
		putData.expiry=10000;
		put(QL_DH_HTTP_LSR.getQNames(), putData);
		MQMessage getData = get(QL_DH_REP.getQNames());
		System.out.println(mqMessageToString(getData));
	}


}
