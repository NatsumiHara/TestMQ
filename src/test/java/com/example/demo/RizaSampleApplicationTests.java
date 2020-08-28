package com.example.demo;

import static com.example.demo.KKK.*;
import static com.example.demo.KKK.values;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;

import com.example.utils.FFFF;
import com.example.utils.Super;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

class RizaSampleApplicationTests implements Super, FFFF, IDGenerator {

	private static final String TEST_XML = "/Users/webma/Documents/workspace/RizaSample/src/main/java/com/example/demo/test.xml";

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
