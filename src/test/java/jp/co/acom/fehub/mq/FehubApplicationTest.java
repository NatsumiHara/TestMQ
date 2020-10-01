package jp.co.acom.fehub.mq;

import static jp.co.acom.fehub.mq.ConstantQname.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;

import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

import jp.co.acom.fehub.tests.CommonTest;

class FehubApplicationTest implements IDGenerator, CommonTest {

	@BeforeEach
	void before() throws Exception {

		for (ConstantQname k : values())
			clean(k.getQNames());
	}

	@ParameterizedTest(name = "Run{index}:putQName={0},getQName={1},xmlString={2}")
	@MethodSource("testMq")
	@DisplayName("要求テスト")
	void requestTest(String putQName, String getQName, String xmlString) throws Exception {

		String replaceServiceId = replaceSerId(fileToString(xmlString), getQName);
		MQMessage putData = putMessages(replaceServiceId);
		putData.replyToQueueName = QL_DW_REP.getQNames();
		put("Q" + putQName + ".DH.REQ", putData);
		MQMessage getData = get("QA.DH.D" + getQName, putData.messageId);
		Document xmlData = stringToDocument(replaceServiceId);
		extracted(putData, xmlData, getData);
	}

	static Stream<Arguments> testMq() {

		List<String> Q = Arrays.asList("C", "L");
		List<String> D = Arrays.asList("L", "F");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = Q.iterator(); itr1.hasNext();) {
			String QX = itr1.next();
			for (Iterator<String> itr2 = D.iterator(); itr2.hasNext();) {
				String DX = itr2.next();
				list.add(Arguments.of(QX, DX, TEST_XML));
				list.add(Arguments.of(QX, DX, TEST2_XML));
				list.add(Arguments.of(QX, DX, TEST3_XML));
				list.add(Arguments.of(QX, DX, TEST4_XML));
				list.add(Arguments.of(QX, DX, TEST5_XML));
				list.add(Arguments.of(QX, DX, TEST6_XML));
				list.add(Arguments.of(QX, DX, TEST7_XML));
				list.add(Arguments.of(QX, DX, TEST8_XML));
				list.add(Arguments.of(QX, DX, TEST9_XML));
				list.add(Arguments.of(QX, DX, TEST10_XML));
				list.add(Arguments.of(QX, DX, TEST11_XML));
				list.add(Arguments.of(QX, DX, TEST12_XML));
				list.add(Arguments.of(QX, DX, TEST13_XML));
				list.add(Arguments.of(QX, DX, TEST14_XML));
				list.add(Arguments.of(QX, DX, TEST15_XML));
				list.add(Arguments.of(QX, DX, TEST16_XML));
				list.add(Arguments.of(QX, DX, TEST17_XML));
				list.add(Arguments.of(QX, DX, TEST18_XML));
			}
		}

		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:putQName={0},xmlServiceId={1},xmlPath={2}")
	@MethodSource("returnTest")
	@DisplayName("要求戻りテスト")
	void requestReturnTest(String putQName, String xmlServiceId, String xmlPath) throws Exception {

		String putQNames = "Q" + putQName + ".DH.REQ";
		String stringXmlData = fileToString(xmlPath);
		String xmlData = replacePluralSerId(stringXmlData, xmlServiceId);
		Document documentXmlData = stringToDocument(xmlData);
		String serviceId = xPath(documentXmlData, SERVICEID_Tab);
		String rc;
		if ("DA".equals(xmlServiceId)) {
			rc = "02";
		} else {
			rc = "01";
		}
		MQMessage putData = putMessages(xmlData);
		putData.replyToQueueName = QL_DW_REP.getQNames();
		put(putQNames, putData);
		MQMessage getData = get(QL_DW_REP.getQNames(), putData.messageId);
		returnQTest(putData, getData, rc, serviceId);
	}

	static Stream<Arguments> returnTest() {

		List<String> Q = Arrays.asList("C", "L");
		List<String> D = Arrays.asList("", null, "A", "DA");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = Q.iterator(); itr1.hasNext();) {
			String QX = itr1.next();
			for (Iterator<String> itr2 = D.iterator(); itr2.hasNext();) {
				String DX = itr2.next();

				list.add(Arguments.of(QX, DX, TEST_XML));
				list.add(Arguments.of(QX, DX, TEST2_XML));
				list.add(Arguments.of(QX, DX, TEST3_XML));
				list.add(Arguments.of(QX, DX, TEST4_XML));
				list.add(Arguments.of(QX, DX, TEST5_XML));
				list.add(Arguments.of(QX, DX, TEST6_XML));
				list.add(Arguments.of(QX, DX, TEST7_XML));
				list.add(Arguments.of(QX, DX, TEST8_XML));
				list.add(Arguments.of(QX, DX, TEST9_XML));
				list.add(Arguments.of(QX, DX, TEST10_XML));
				list.add(Arguments.of(QX, DX, TEST11_XML));
				list.add(Arguments.of(QX, DX, TEST12_XML));
				list.add(Arguments.of(QX, DX, TEST13_XML));
				list.add(Arguments.of(QX, DX, TEST14_XML));
				list.add(Arguments.of(QX, DX, TEST15_XML));
				list.add(Arguments.of(QX, DX, TEST16_XML));
				list.add(Arguments.of(QX, DX, TEST17_XML));
				list.add(Arguments.of(QX, DX, TEST18_XML));
			}
		}
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:putQName={0},getQName={1},xmlPath={2}")
	@MethodSource("parseErrorTest")
	@DisplayName("要求パースエラーテスト")
	void requestParseError(String putQName, String getQName, String xmlPath) throws Exception {

		String putQNames = "Q" + putQName + ".DH.REQ";
		String xmlStringData = fileToString(xmlPath);
		String replaceServiceId = replaceSerId(xmlStringData, getQName);
		MQMessage putData = putMessages(replaceServiceId);
		putData.replyToQueueName = QL_DW_REP.getQNames();
		put(putQNames, putData);
		MQMessage getData = get_wait(QL_DH_ERR.getQNames());
		errorQTest(putData, getData);

	}

	@ParameterizedTest(name = "Run{index}:putQName={0},getQName={1},xmlPath={2}")
	@MethodSource("parseErrorTest")
	@DisplayName("要求デットレターテスト")
	void requestDeadQError(String putQName, String getQName, String xmlPath) throws Exception {

		try {
			String putQNames = "Q" + putQName + ".DH.REQ";
			String xmlStringData = fileToString(xmlPath);
			String replaceServiceId = replaceSerId(xmlStringData, getQName);
			MQMessage putData = putMessages(replaceServiceId);
			putDisabled(QL_DH_ERR.getQNames());
			put(putQNames, putData);
			MQMessage getData = get_wait(SYSTEM_ADMIN_EVENT.getQNames());
			deadQTest(putData, getData);
		} finally {
			putEnabled(QL_DH_ERR.getQNames());
		}

	}

	static Stream<Arguments> parseErrorTest() {

		List<String> Q = Arrays.asList("C", "L");
		List<String> D = Arrays.asList("L", "F");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = Q.iterator(); itr1.hasNext();) {
			String QX = itr1.next();
			for (Iterator<String> itr2 = D.iterator(); itr2.hasNext();) {
				String DX = itr2.next();

//				list.add(Arguments.of(QX, DX, TEST19_XML));
//				list.add(Arguments.of(QX, DX, TEST20_XML));
//				list.add(Arguments.of(QX, DX, TEST21_XML));
//				list.add(Arguments.of(QX, DX, TEST22_XML));
//				list.add(Arguments.of(QX, DX, TEST23_XML));
//				list.add(Arguments.of(QX, DX, TEST24_XML));
//				list.add(Arguments.of(QX, DX, TEST25_XML));
//				list.add(Arguments.of(QX, DX, TEST26_XML));
				list.add(Arguments.of(QX, DX, TEST27_XML));

			}
		}
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:putQName={0},xmlServiceId={1},xmlPath={2}")
	@MethodSource("failureError")
	@DisplayName("要求Failureエラーテスト")
	void requestFailureError(String putQName, String xmlServiceId, String xmlPath) throws Exception {

		String putQNames = "Q" + putQName + ".DH.REQ";
		String stringXmlData = fileToString(xmlPath);
		String xmlData = replacePluralSerId(stringXmlData, xmlServiceId);
		MQMessage putData = putMessages(xmlData);
		put(putQNames, putData);
		MQMessage getData = get_wait(QL_DH_ERR.getQNames());
		errorQTest(putData, getData);

	}

	static Stream<Arguments> failureError() {

		List<String> Q = Arrays.asList("C", "L");
		List<String> D = Arrays.asList("", null, "A", "DA");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = Q.iterator(); itr1.hasNext();) {
			String QX = itr1.next();
			for (Iterator<String> itr2 = D.iterator(); itr2.hasNext();) {
				String DX = itr2.next();

//				list.add(Arguments.of(QX, DX, TEST28_XML));
//				list.add(Arguments.of(QX, DX, TEST29_XML));
//				list.add(Arguments.of(QX, DX, TEST30_XML));
				list.add(Arguments.of(QX, DX, TEST_XML));

			}
		}
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:getQName={0},xmlString={1}")
	@MethodSource("returnTestMq")
	@DisplayName("応答テスト")
	void responseTest(String getQName, String xmlString) throws Exception {

		String stringXmlData = fileToString(xmlString);
		String replyQ = QL_DW_REP.getQNames();
		String xmlData = replaceReply(stringXmlData, replyQ, getQmgr());
		MQMessage putData = putReplyMessages(xmlData, getQName);
		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get(replyQ, putData.messageId);
		returnMqTest(putData, getData);

	}

	static Stream<Arguments> returnTestMq() {

		List<String> D = Arrays.asList("F", "L");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr = D.iterator(); itr.hasNext();) {
			String DX = itr.next();
			list.add(Arguments.of(DX, TEST_XML));
			list.add(Arguments.of(DX, TEST2_XML));
			list.add(Arguments.of(DX, TEST3_XML));
			list.add(Arguments.of(DX, TEST4_XML));
			list.add(Arguments.of(DX, TEST5_XML));
			list.add(Arguments.of(DX, TEST6_XML));
			list.add(Arguments.of(DX, TEST7_XML));
			list.add(Arguments.of(DX, TEST8_XML));
			list.add(Arguments.of(DX, TEST9_XML));
			list.add(Arguments.of(DX, TEST10_XML));
			list.add(Arguments.of(DX, TEST11_XML));
			list.add(Arguments.of(DX, TEST13_XML));
			list.add(Arguments.of(DX, TEST14_XML));
			list.add(Arguments.of(DX, TEST15_XML));
			list.add(Arguments.of(DX, TEST16_XML));
			list.add(Arguments.of(DX, TEST17_XML));
			list.add(Arguments.of(DX, TEST18_XML));
		}

		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:appId={0},xmlPath={1}")
	@MethodSource("responseAppIdTest")
	@DisplayName("応答戻りRcテスト")
	void responseReturnRcTest(String appId, String xmlPath) throws Exception {

		String stringXmlData = fileToString(xmlPath);

		String replyQ = QL_DW_REP.getQNames();
		String xmlData = replaceReply(stringXmlData, replyQ, getQmgr());
		xmlData = replaceRc(xmlData);

		MQMessage putData = putReplyMessagesAppId(xmlData, appId);

		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get(replyQ, putData.messageId);

		returnQTest(putData, getData, "03", appId);

	}

	static Stream<Arguments> responseAppIdTest() {

		List<String> I = Arrays.asList("DF", "DL", "A", "");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr = I.iterator(); itr.hasNext();) {
			String ID = itr.next();

//			list.add(Arguments.of(ID, TEST_XML));
//			list.add(Arguments.of(ID, TEST2_XML));
//			list.add(Arguments.of(ID, TEST3_XML));
//			list.add(Arguments.of(ID, TEST4_XML));
//			list.add(Arguments.of(ID, TEST5_XML));
//			list.add(Arguments.of(ID, TEST6_XML));
//			list.add(Arguments.of(ID, TEST7_XML));
//			list.add(Arguments.of(ID, TEST8_XML));
//			list.add(Arguments.of(ID, TEST9_XML));
//			list.add(Arguments.of(ID, TEST10_XML));
//			list.add(Arguments.of(ID, TEST14_XML));
//			list.add(Arguments.of(ID, TEST15_XML));
//			list.add(Arguments.of(ID, TEST16_XML));
//			list.add(Arguments.of(ID, TEST17_XML));
			list.add(Arguments.of(ID, TEST18_XML));
		}
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:appId={0},rc={1},xmlPath={2}")
	@MethodSource("responseRcTest")
	@DisplayName("応答戻りAppテスト")
	void responseReturnAppIdTest(String appId, String rc, String xmlPath) throws Exception {

		String stringXmlData = fileToString(xmlPath);
		String replyQ = QL_DW_REP.getQNames();
		String xmlData = replaceReply(stringXmlData, replyQ, getQmgr());
		xmlData = replacePluralRc(xmlData,rc);
		MQMessage putData = putReplyMessagesAppId(xmlData, appId);
		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get(replyQ, putData.messageId);
		returnQTest(putData, getData, "01", appId);

	}

	static Stream<Arguments> responseRcTest() {

		List<String> I = Arrays.asList("A", "");
		List<String> R = Arrays.asList("00", "", null);

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = I.iterator(); itr1.hasNext();) {
			String ID = itr1.next();
			for (Iterator<String> itr2 = R.iterator(); itr2.hasNext();) {
				String RC = itr2.next();

//				list.add(Arguments.of(ID, RC, TEST_XML));
//				list.add(Arguments.of(ID, RC, TEST2_XML));
//				list.add(Arguments.of(ID, RC, TEST3_XML));
//				list.add(Arguments.of(ID, RC, TEST4_XML));
//				list.add(Arguments.of(ID, RC, TEST5_XML));
//				list.add(Arguments.of(ID, RC, TEST6_XML));
//				list.add(Arguments.of(ID, RC, TEST7_XML));
//				list.add(Arguments.of(ID, RC, TEST8_XML));
//				list.add(Arguments.of(ID, RC, TEST9_XML));
//				list.add(Arguments.of(ID, RC, TEST10_XML));
//				list.add(Arguments.of(ID, RC, TEST14_XML));
//				list.add(Arguments.of(ID, RC, TEST15_XML));
//				list.add(Arguments.of(ID, RC, TEST16_XML));
//				list.add(Arguments.of(ID, RC, TEST17_XML));
				list.add(Arguments.of(ID, RC, TEST18_XML));
			}
		}
		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:getQName={0},ReplyError={1},xmlString={2}")
	@MethodSource("responseFailureErrorTest")
	@DisplayName("応答戻りfailureエラーテスト")
	void responseFailureErrorTest(String getQName, String ReplyError, String xmlString) throws Exception {

		String stringXmlData = fileToString(xmlString);		
		String replyQ = QL_DH_ERR.getQNames();
		String xmlData = replaceErrorReply(stringXmlData, ReplyError);
		MQMessage putData = putReplyMessages(xmlData, getQName);
		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get_wait(replyQ);
		errorQTest(putData, getData);

	}

	static Stream<Arguments> responseFailureErrorTest() {

		List<String> D = Arrays.asList("F", "L");
		List<String> ReplyError = Arrays.asList("", "<REPLY></REPLY>");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = D.iterator(); itr1.hasNext();) {
			String DX = itr1.next();
			for (Iterator<String> itr2 = ReplyError.iterator(); itr2.hasNext();) {
				String Reply = itr2.next();
				list.add(Arguments.of(DX, Reply, TEST_XML));
//				list.add(Arguments.of(DX, Reply, TEST2_XML));
//				list.add(Arguments.of(DX, Reply, TEST3_XML));
//				list.add(Arguments.of(DX, Reply, TEST4_XML));
//				list.add(Arguments.of(DX, Reply, TEST5_XML));
//				list.add(Arguments.of(DX, Reply, TEST6_XML));
//				list.add(Arguments.of(DX, Reply, TEST7_XML));
//				list.add(Arguments.of(DX, Reply, TEST8_XML));
//				list.add(Arguments.of(DX, Reply, TEST9_XML));
//				list.add(Arguments.of(DX, Reply, TEST10_XML));
//				list.add(Arguments.of(DX, Reply, TEST11_XML));
//				list.add(Arguments.of(DX, Reply, TEST13_XML));
//				list.add(Arguments.of(DX, Reply, TEST15_XML));
//				list.add(Arguments.of(DX, Reply, TEST16_XML));
//				list.add(Arguments.of(DX, Reply, TEST17_XML));
//				list.add(Arguments.of(DX, Reply, TEST18_XML));
			}
		}
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:appId={0},reply={1},xmlPath={2}")
	@MethodSource("responseFailureRcErrorTest")
	@DisplayName("応答戻りfailureRcエラーテスト")
	void responseFailureRcErrorTest(String appId, String reply, String xmlPath) throws Exception {

		String stringXmlData = fileToString(xmlPath);
		String replyQ = QL_DH_ERR.getQNames();
		String xmlData = replaceErrorReply(stringXmlData, reply);
		xmlData = replaceRc(xmlData);
		MQMessage putData = putReplyMessagesAppId(xmlData, appId);
		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get_wait(replyQ);

		errorQTest(putData, getData);

	}

	static Stream<Arguments> responseFailureRcErrorTest() {

		List<String> I = Arrays.asList("DF", "DL", "A", "");
		List<String> ReplyError = Arrays.asList("", "<REPLY></REPLY>");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = I.iterator(); itr1.hasNext();) {
			String ID = itr1.next();
			for (Iterator<String> itr2 = ReplyError.iterator(); itr2.hasNext();) {
				String Reply = itr2.next();

				list.add(Arguments.of(ID, Reply, TEST_XML));
//				list.add(Arguments.of(ID, Reply, TEST2_XML));
//				list.add(Arguments.of(ID, Reply, TEST3_XML));
//				list.add(Arguments.of(ID, Reply, TEST4_XML));
//				list.add(Arguments.of(ID, Reply, TEST5_XML));
//				list.add(Arguments.of(ID, Reply, TEST6_XML));
//				list.add(Arguments.of(ID, Reply, TEST7_XML));
//				list.add(Arguments.of(ID, Reply, TEST8_XML));
//				list.add(Arguments.of(ID, Reply, TEST9_XML));
//				list.add(Arguments.of(ID, Reply, TEST10_XML));
//				list.add(Arguments.of(ID, Reply, TEST15_XML));
//				list.add(Arguments.of(ID, Reply, TEST16_XML));
//				list.add(Arguments.of(ID, Reply, TEST17_XML));
//				list.add(Arguments.of(ID, Reply, TEST18_XML));
			}
		}
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:appId={0},rc={1},reply={2},xmlPath={3}")
	@MethodSource("responseAppTest")
	@DisplayName("応答戻りfailureAppエラーテスト")
	void responseReturnAppTest(String appId, String rc, String reply, String xmlPath) throws Exception {

		String stringXmlData = fileToString(xmlPath);

		String replyQ = QL_DH_ERR.getQNames();
		String xmlData = replaceErrorReply(stringXmlData, reply);
		xmlData = replacePluralRc(xmlData, rc);
		MQMessage putData = putReplyMessagesAppId(xmlData, appId);
		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get_wait(replyQ);
		errorQTest(putData, getData);

	}

	static Stream<Arguments> responseAppTest() {

		List<String> I = Arrays.asList("A", "");
		List<String> R = Arrays.asList("00", "", null);
		List<String> ReplyError = Arrays.asList("", "<REPLY></REPLY>");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = I.iterator(); itr1.hasNext();) {
			String ID = itr1.next();
			for (Iterator<String> itr2 = R.iterator(); itr2.hasNext();) {
				String RC = itr2.next();
				for (Iterator<String> itr3 = ReplyError.iterator(); itr3.hasNext();) {
					String Reply = itr3.next();

//					list.add(Arguments.of(ID, RC, Reply, TEST_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST2_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST3_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST4_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST5_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST6_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST7_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST8_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST9_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST10_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST15_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST16_XML));
//					list.add(Arguments.of(ID, RC, Reply, TEST17_XML));
					list.add(Arguments.of(ID, RC, Reply, TEST18_XML));
				}
			}
		}
		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:appId={0},xmlPath={1}")
	@MethodSource("responseParseErrorTest")
	@DisplayName("応答戻りパースエラーテスト")
	void responseParseErrorTest(String appId, String xmlPath) throws Exception {

		String xmlStringData = fileToString(xmlPath);
		MQMessage putData = putReplyMessages(xmlStringData, appId);
		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get_wait(QL_DH_ERR.getQNames());
		errorQTest(putData, getData);

	}

	static Stream<Arguments> responseParseErrorTest() {

		List<String> I = Arrays.asList("F", "L");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr2 = I.iterator(); itr2.hasNext();) {
			String ID = itr2.next();

			list.add(Arguments.of(ID, TEST19_XML));
//			list.add(Arguments.of(ID, TEST20_XML));
//			list.add(Arguments.of(ID, TEST21_XML));
//			list.add(Arguments.of(ID, TEST22_XML));
//			list.add(Arguments.of(ID, TEST23_XML));
//			list.add(Arguments.of(ID, TEST24_XML));
//			list.add(Arguments.of(ID, TEST25_XML));
//			list.add(Arguments.of(ID, TEST26_XML));
//			list.add(Arguments.of(ID, TEST27_XML));
		}
		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:appId={0},reply={1},xmlPath={2}")
	@MethodSource("responseDeadErrorTest")
	@DisplayName("応答戻りデットレターテスト")
	void responseDeadErrorTest(String appId, String reply, String xmlPath) throws Exception {

		try {
			String xmlStringData = fileToString(xmlPath);
			xmlStringData = replaceErrorReply(xmlStringData, reply);
			MQMessage putData = putReplyMessages(xmlStringData, appId);
			putDisabled(QL_DH_ERR.getQNames());
			put(QL_DH_REP.getQNames(), putData);
			MQMessage getData = get_wait(SYSTEM_ADMIN_EVENT.getQNames());
			deadQTest(putData, getData);
		} finally {
			putEnabled(QL_DH_ERR.getQNames());
		}

	}

	static Stream<Arguments> responseDeadErrorTest() {

		List<String> D = Arrays.asList("F", "L");
		List<String> ReplyError = Arrays.asList("", "<REPLY></REPLY>");

		List<Arguments> list = new ArrayList<>();
		for (Iterator<String> itr1 = D.iterator(); itr1.hasNext();) {
			String DX = itr1.next();
			for (Iterator<String> itr2 = ReplyError.iterator(); itr2.hasNext();) {
				String Reply = itr2.next();

//				list.add(Arguments.of(DX, Reply, TEST19_XML));
//				list.add(Arguments.of(DX, Reply, TEST20_XML));
//				list.add(Arguments.of(DX, Reply, TEST21_XML));
//				list.add(Arguments.of(DX, Reply, TEST22_XML));
//				list.add(Arguments.of(DX, Reply, TEST23_XML));
//				list.add(Arguments.of(DX, Reply, TEST24_XML));
//				list.add(Arguments.of(DX, Reply, TEST25_XML));
//				list.add(Arguments.of(DX, Reply, TEST26_XML));
				list.add(Arguments.of(DX, Reply, TEST27_XML));
			}
		}
		return list.stream();

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