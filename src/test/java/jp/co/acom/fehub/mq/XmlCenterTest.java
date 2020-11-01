package jp.co.acom.fehub.mq;

import static jp.co.acom.fehub.mq.ConstantQname.LOCALHOST;

import static jp.co.acom.fehub.mq.ConstantQname.QL_DH_ERR;
import static jp.co.acom.fehub.mq.ConstantQname.QL_DH_REP;
import static jp.co.acom.fehub.mq.ConstantQname.QL_DW_REP;
import static jp.co.acom.fehub.mq.ConstantQname.QMFH01;
import static jp.co.acom.fehub.mq.ConstantQname.SYSTEM_ADMIN_EVENT;
import static jp.co.acom.fehub.mq.ConstantQname.SYSTEM_BKR_CONFIG;
import static jp.co.acom.fehub.mq.ConstantQname._50014;
import static jp.co.acom.fehub.mq.ConstantQname.values;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

import jp.co.acom.fehub.tests.CommonTest;

class XmlCenterTest implements CommonTest {

	@BeforeEach
	void before() throws Exception {

		for (ConstantQname k : values())
			clean(k.getQNames());
	}

	ConstantQname inputQName(String q) {
		return ConstantQname.valueOf("Q" + q + "_DH_REQ");
	}

	ConstantQname outputQName(String q) {
		return ConstantQname.valueOf("QA_DH_D" + q);
	}

	MQMessage replaceF(String stringXmlData, String getQName) throws IOException {

		return replaceDF(stringXmlData, "D" + getQName);
	}

	MQMessage replaceDF(String stringXmlData, String getQName) throws IOException {

		int characterSet = 1208;
		if (!"DF".equals(getQName)) {
			stringXmlData = stringXmlData.replace("encoding=\"UTF-8\"", "encoding=\"IBM-930\"");
			characterSet = 943;

			String d1D2 = stringXmlData.substring(stringXmlData.indexOf("<D1>"),
					stringXmlData.indexOf("</D2>") + "</D2>".length());
			stringXmlData = stringXmlData.replace(d1D2, "");
		}

		String applicationIdData = null;
		applicationIdData = getQName;

		return putReplyMessages(stringXmlData, characterSet, applicationIdData);

	}

	private static final List<String> Q = Arrays.asList("C", "L");
	private static final List<String> D = Arrays.asList("L", "F");
	private static final List<String> A = Arrays.asList("", null, "A", "DA");
	private static final List<String> I = Arrays.asList("DF", "DL", "A", "");
	private static final List<String> R = Arrays.asList("00", "", null);
	private static final List<String> X = Arrays.asList("A", "");
	private static final List<String> ReplyError = Arrays.asList("", "<REPLY></REPLY>",
			"<REPLY><R_PVR>QMDW01</R_PVR><R_DST>" + QL_DW_REP.getQNames() + "</R_DST></REPLY>",
			"<REPLY><R_PVR>" + QMFH01 + "</R_PVR><R_DST>QA.DW.REP</R_DST></REPLY>",
			"<REPLY><R_PVR>QMDW01</R_PVR><R_DST>QA.DW.REP</R_DST></REPLY>");

	@ParameterizedTest(name = "Run{index}:putQName={0},getQName={1},xmlString={2}")
	@MethodSource("testMq")
	@DisplayName("要求テスト")
	void requestTest(String putQName, String getQName, String xmlString) throws Exception {

		String replaceServiceId = replaceSerId(fileToString(xmlString), getQName);
		MQMessage putData = putMessages(replaceServiceId);
		putData.replyToQueueName = QL_DW_REP.getQNames();
		put(inputQName(putQName), putData);
		requestQTest(putData, get(outputQName(getQName), putData.messageId));
	}

	static Stream<Arguments> testMq() {

		List<Arguments> list = new ArrayList<>();
		Q.forEach(QX -> {
			D.forEach(DX -> {
				list.add(Arguments.of(QX, DX, NOMAL_XML));
				list.add(Arguments.of(QX, DX, TS0_XML));
				list.add(Arguments.of(QX, DX, TS2_XML));
				list.add(Arguments.of(QX, DX, TS3_XML));
				list.add(Arguments.of(QX, DX, TS4_XML));
				list.add(Arguments.of(QX, DX, TS5_XML));
				list.add(Arguments.of(QX, DX, TS6_XML));
				list.add(Arguments.of(QX, DX, TS7_XML));
				list.add(Arguments.of(QX, DX, TS8_XML));
				list.add(Arguments.of(QX, DX, TIMESTAMP_NOT_XML));
				list.add(Arguments.of(QX, DX, RC00_XML));
				list.add(Arguments.of(QX, DX, RC01_XML));
				list.add(Arguments.of(QX, DX, RC_Tab_XML));
				list.add(Arguments.of(QX, DX, USERID_NOT_XML));
				list.add(Arguments.of(QX, DX, VERSION_NOT_XML));
				list.add(Arguments.of(QX, DX, KUBUN_NOT_XML));
				list.add(Arguments.of(QX, DX, REQUESTID_NOT_XML));
			});
		});

		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:putQName={0},xmlServiceId={1},xmlPath={2}")
	@MethodSource("returnTest")
	@DisplayName("要求戻りreplyToQテスト")
	void requestReturnTest(String putQName, String xmlServiceId, String xmlPath) throws Exception {

		String xmlData = replacePluralSerId(fileToString(xmlPath), xmlServiceId);
		String rc;
		if ("DA".equals(xmlServiceId)) {
			rc = "02";
		} else {
			rc = "01";
		}
		MQMessage putData = putMessages(xmlData);
		putData.replyToQueueName = QL_DW_REP.getQNames();
		put(inputQName(putQName), putData);
		responseReturnQTest(putData, get(QL_DW_REP.getQNames(), putData.messageId), rc,
				xPath(stringToDocument(xmlData), SERVICEID_Tab));
	}

	@ParameterizedTest(name = "Run{index}:putQName={0},xmlServiceId={1},xmlPath={2}")
	@MethodSource("returnTest")
	@DisplayName("要求戻りREPLYテスト")
	void requestReturnReplyTest(String putQName, String xmlServiceId, String xmlPath) throws Exception {
		String returnQName = QL_DW_REP.getQNames();
		String xmlData = replacePluralSerId(fileToString(xmlPath), xmlServiceId);
		xmlData = replaceReply(xmlData, returnQName, getQmgr());
		String rc;
		if ("DA".equals(xmlServiceId)) {
			rc = "02";
		} else {
			rc = "01";
		}
		MQMessage putData = putMessages(xmlData);
		put(inputQName(putQName), putData);
		responseReturnQTest(putData, get(returnQName, putData.messageId), rc,
				xPath(stringToDocument(xmlData), SERVICEID_Tab));
	}

	static Stream<Arguments> returnTest() {
		List<Arguments> list = new ArrayList<>();
		Q.forEach(QX -> {
			A.forEach(DX -> {
				list.add(Arguments.of(QX, DX, NOMAL_XML));
				list.add(Arguments.of(QX, DX, TS0_XML));
				list.add(Arguments.of(QX, DX, TS2_XML));
				list.add(Arguments.of(QX, DX, TS3_XML));
				list.add(Arguments.of(QX, DX, TS4_XML));
				list.add(Arguments.of(QX, DX, TS5_XML));
				list.add(Arguments.of(QX, DX, TS6_XML));
				list.add(Arguments.of(QX, DX, TS7_XML));
				list.add(Arguments.of(QX, DX, TS8_XML));
				list.add(Arguments.of(QX, DX, TIMESTAMP_NOT_XML));
				list.add(Arguments.of(QX, DX, RC00_XML));
				list.add(Arguments.of(QX, DX, RC01_XML));
				list.add(Arguments.of(QX, DX, RC_Tab_XML));
				list.add(Arguments.of(QX, DX, USERID_NOT_XML));
				list.add(Arguments.of(QX, DX, VERSION_NOT_XML));
				list.add(Arguments.of(QX, DX, KUBUN_NOT_XML));
				list.add(Arguments.of(QX, DX, REQUESTID_NOT_XML));
			});
		});
		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:putQName={0},getQName={1},xmlPath={2}")
	@MethodSource("parseErrorTest")
	@DisplayName("要求パースエラーテスト")
	void requestParseError(String putQName, String getQName, String xmlPath) throws Exception {

		MQMessage putData = putMessages(replaceSerId(fileToString(xmlPath), getQName));
		putData.replyToQueueName = QL_DW_REP.getQNames();
		put(inputQName(putQName), putData);
		errorQTest(putData, get_wait(QL_DH_ERR.getQNames()));

	}

	@ParameterizedTest(name = "Run{index}:putQName={0},getQName={1},xmlPath={2}")
	@MethodSource("parseErrorTest")
	@DisplayName("要求デットレターテスト")
	void requestDeadQError(String putQName, String getQName, String xmlPath) throws Exception {

		try {
			putDisabled(QL_DH_ERR.getQNames());
			MQMessage putData = putMessages(replaceSerId(fileToString(xmlPath), getQName));
			put(inputQName(putQName), putData);
			deadQTest(putData, get_wait(SYSTEM_ADMIN_EVENT.getQNames()));
		} finally {
			putEnabled(QL_DH_ERR.getQNames());
		}
	}

	static Stream<Arguments> parseErrorTest() {

		List<Arguments> list = new ArrayList<>();
		Q.forEach(QX -> {
			D.forEach(DX -> {
				list.add(Arguments.of(QX, DX, KUBUN_BREAK_XML));
				list.add(Arguments.of(QX, DX, RC_BREAK_XML));
				list.add(Arguments.of(QX, DX, REPLY_BREAK_XML));
				list.add(Arguments.of(QX, DX, REQUESTID_BREAK_XML));
				list.add(Arguments.of(QX, DX, SERVICEID_BREAK_XML));
				list.add(Arguments.of(QX, DX, TIMESTAMP_BREAK_XML));
				list.add(Arguments.of(QX, DX, TS_BREAK_XML));
				list.add(Arguments.of(QX, DX, USERID_BREAK_XML));
				list.add(Arguments.of(QX, DX, VERSION_BREAK_XML));
			});
		});

		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:putQName={0},xmlServiceId={1},reply={2},xmlPath={3}")
	@MethodSource("failureError")
	@DisplayName("要求Failureエラーテスト")
	void requestFailureError(String putQName, String xmlServiceId, String reply, String xmlPath) throws Exception {

		String xmlData = replacePluralSerId(fileToString(xmlPath), xmlServiceId);
		xmlData = replaceErrorReply(fileToString(xmlPath), reply);
		MQMessage putData = putMessages(xmlData);
		put(inputQName(putQName), putData);
		errorQTest(putData, get_wait(QL_DH_ERR.getQNames()));

	}

	static Stream<Arguments> failureError() {

		List<Arguments> list = new ArrayList<>();
		Q.forEach(QX -> {
			A.forEach(DX -> {
				ReplyError.forEach(Reply -> {
					list.add(Arguments.of(QX, DX, Reply, NOMAL_XML));
				});
			});
		});

		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:getQName={0},xmlString={1}")
	@MethodSource("returnTestMq")
	@DisplayName("応答テスト")
	void responseTest(String getQName, String xmlString) throws Exception {

		String replyQ = QL_DW_REP.getQNames();
		MQMessage putData = replaceF(replaceReply(fileToString(xmlString), replyQ, getQmgr()), getQName);
		put(QL_DH_REP.getQNames(), putData);
		MQMessage getData = get(replyQ, putData.messageId);
		responseQTest(putData, getData);
	}

	static Stream<Arguments> returnTestMq() {

		List<Arguments> list = new ArrayList<>();
		D.forEach(DX -> {

			list.add(Arguments.of(DX, NOMAL_XML));
			list.add(Arguments.of(DX, TS0_XML));
			list.add(Arguments.of(DX, TS2_XML));
			list.add(Arguments.of(DX, TS3_XML));
			list.add(Arguments.of(DX, TS4_XML));
			list.add(Arguments.of(DX, TS5_XML));
			list.add(Arguments.of(DX, TS6_XML));
			list.add(Arguments.of(DX, TS7_XML));
			list.add(Arguments.of(DX, TS8_XML));
			list.add(Arguments.of(DX, TIMESTAMP_NOT_XML));
			list.add(Arguments.of(DX, RC00_XML));
			list.add(Arguments.of(DX, RC_Tab_XML));
			list.add(Arguments.of(DX, USERID_NOT_XML));
			list.add(Arguments.of(DX, VERSION_NOT_XML));
			list.add(Arguments.of(DX, KUBUN_NOT_XML));
			list.add(Arguments.of(DX, REQUESTID_NOT_XML));
		});

		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:appId={0},xmlPath={1}")
	@MethodSource("responseAppIdTest")
	@DisplayName("応答戻りRcテスト")
	void responseReturnRcTest(String appId, String xmlPath) throws Exception {

		String replyQ = QL_DW_REP.getQNames();
		String xmlData = replaceReply(fileToString(xmlPath), replyQ, getQmgr());
		xmlData = replaceRc(xmlData);
		MQMessage putData = replaceDF(xmlData, appId);
		put(QL_DH_REP.getQNames(), putData);
		responseReturnQTest(putData, get(replyQ, putData.messageId), "03", appId);

	}

	static Stream<Arguments> responseAppIdTest() {

		List<Arguments> list = new ArrayList<>();
		I.forEach(ID -> {
			list.add(Arguments.of(ID, NOMAL_XML));
			list.add(Arguments.of(ID, TS0_XML));
			list.add(Arguments.of(ID, TS2_XML));
			list.add(Arguments.of(ID, TS3_XML));
			list.add(Arguments.of(ID, TS4_XML));
			list.add(Arguments.of(ID, TS5_XML));
			list.add(Arguments.of(ID, TS6_XML));
			list.add(Arguments.of(ID, TS7_XML));
			list.add(Arguments.of(ID, TS8_XML));
			list.add(Arguments.of(ID, TIMESTAMP_NOT_XML));
			list.add(Arguments.of(ID, USERID_NOT_XML));
			list.add(Arguments.of(ID, VERSION_NOT_XML));
			list.add(Arguments.of(ID, KUBUN_NOT_XML));
			list.add(Arguments.of(ID, REQUESTID_NOT_XML));
		});

		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:appId={0},rc={1},xmlPath={2}")
	@MethodSource("responseRcTest")
	@DisplayName("応答戻りAppテスト")
	void responseReturnAppIdTest(String appId, String rc, String xmlPath) throws Exception {

		String replyQ = QL_DW_REP.getQNames();
		String xmlData = replaceReply(fileToString(xmlPath), replyQ, getQmgr());
		xmlData = replacePluralRc(xmlData, rc);
		MQMessage putData = replaceDF(xmlData, appId);
		put(QL_DH_REP.getQNames(), putData);
		responseReturnQTest(putData, get(replyQ, putData.messageId), "01", appId);

	}

	static Stream<Arguments> responseRcTest() {

		List<Arguments> list = new ArrayList<>();
		X.forEach(ID -> {
			R.forEach(RC -> {
				list.add(Arguments.of(ID, RC, NOMAL_XML));
				list.add(Arguments.of(ID, RC, TS0_XML));
				list.add(Arguments.of(ID, RC, TS2_XML));
				list.add(Arguments.of(ID, RC, TS3_XML));
				list.add(Arguments.of(ID, RC, TS4_XML));
				list.add(Arguments.of(ID, RC, TS5_XML));
				list.add(Arguments.of(ID, RC, TS6_XML));
				list.add(Arguments.of(ID, RC, TS7_XML));
				list.add(Arguments.of(ID, RC, TS8_XML));
				list.add(Arguments.of(ID, RC, TIMESTAMP_NOT_XML));
				list.add(Arguments.of(ID, RC, USERID_NOT_XML));
				list.add(Arguments.of(ID, RC, VERSION_NOT_XML));
				list.add(Arguments.of(ID, RC, KUBUN_NOT_XML));
				list.add(Arguments.of(ID, RC, REQUESTID_NOT_XML));
			});
		});

		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:getQName={0},ReplyError={1},xmlString={2}")
	@MethodSource("responseFailureErrorTest")
	@DisplayName("応答戻りfailureエラーテスト")
	void responseFailureErrorTest(String getQName, String ReplyError, String xmlString) throws Exception {

		MQMessage putData = replaceF(replaceErrorReply(fileToString(xmlString), ReplyError), getQName);
		put(QL_DH_REP.getQNames(), putData);
		errorQTest(putData, get_wait(QL_DH_ERR.getQNames()));

	}

	static Stream<Arguments> responseFailureErrorTest() {

		List<Arguments> list = new ArrayList<>();
		D.forEach(DX -> {
			ReplyError.forEach(Reply -> {
				list.add(Arguments.of(DX, Reply, NOMAL_XML));
				list.add(Arguments.of(DX, Reply, TS0_XML));
				list.add(Arguments.of(DX, Reply, TS2_XML));
				list.add(Arguments.of(DX, Reply, TS3_XML));
				list.add(Arguments.of(DX, Reply, TS4_XML));
				list.add(Arguments.of(DX, Reply, TS5_XML));
				list.add(Arguments.of(DX, Reply, TS6_XML));
				list.add(Arguments.of(DX, Reply, TS7_XML));
				list.add(Arguments.of(DX, Reply, TS8_XML));
				list.add(Arguments.of(DX, Reply, TIMESTAMP_NOT_XML));
				list.add(Arguments.of(DX, Reply, RC00_XML));
				list.add(Arguments.of(DX, Reply, RC_Tab_XML));
				list.add(Arguments.of(DX, Reply, USERID_NOT_XML));
				list.add(Arguments.of(DX, Reply, VERSION_NOT_XML));
				list.add(Arguments.of(DX, Reply, KUBUN_NOT_XML));
				list.add(Arguments.of(DX, Reply, REQUESTID_NOT_XML));

			});
		});
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:appId={0},reply={1},xmlPath={2}")
	@MethodSource("responseFailureRcErrorTest")
	@DisplayName("応答戻りfailureRcエラーテスト")
	void responseFailureRcErrorTest(String appId, String reply, String xmlPath) throws Exception {

		String xmlData = replaceErrorReply(fileToString(xmlPath), reply);
		xmlData = replaceRc(xmlData);
		MQMessage putData = replaceDF(xmlData, appId);
		put(QL_DH_REP.getQNames(), putData);
		errorQTest(putData, get_wait(QL_DH_ERR.getQNames()));

	}

	static Stream<Arguments> responseFailureRcErrorTest() {

		List<Arguments> list = new ArrayList<>();
		I.forEach(ID -> {
			ReplyError.forEach(Reply -> {
				list.add(Arguments.of(ID, Reply, NOMAL_XML));
				list.add(Arguments.of(ID, Reply, TS0_XML));
				list.add(Arguments.of(ID, Reply, TS2_XML));
				list.add(Arguments.of(ID, Reply, TS3_XML));
				list.add(Arguments.of(ID, Reply, TS4_XML));
				list.add(Arguments.of(ID, Reply, TS5_XML));
				list.add(Arguments.of(ID, Reply, TS6_XML));
				list.add(Arguments.of(ID, Reply, TS7_XML));
				list.add(Arguments.of(ID, Reply, TS8_XML));
				list.add(Arguments.of(ID, Reply, TIMESTAMP_NOT_XML));
				list.add(Arguments.of(ID, Reply, USERID_NOT_XML));
				list.add(Arguments.of(ID, Reply, VERSION_NOT_XML));
				list.add(Arguments.of(ID, Reply, KUBUN_NOT_XML));
				list.add(Arguments.of(ID, Reply, REQUESTID_NOT_XML));
			});
		});
		return list.stream();
	}

	@ParameterizedTest(name = "Run{index}:appId={0},rc={1},reply={2},xmlPath={3}")
	@MethodSource("responseAppTest")
	@DisplayName("応答戻りfailureAppエラーテスト")
	void responseReturnAppTest(String appId, String rc, String reply, String xmlPath) throws Exception {

		String xmlData = replaceErrorReply(fileToString(xmlPath), reply);
		xmlData = replacePluralRc(xmlData, rc);
		MQMessage putData = replaceF(xmlData, appId);
		put(QL_DH_REP.getQNames(), putData);
		errorQTest(putData, get_wait(QL_DH_ERR.getQNames()));

	}

	static Stream<Arguments> responseAppTest() {

		List<Arguments> list = new ArrayList<>();
		X.forEach(ID -> {
			R.forEach(RC -> {
				ReplyError.forEach(Reply -> {
					list.add(Arguments.of(ID, RC, Reply, NOMAL_XML));
					list.add(Arguments.of(ID, RC, Reply, TS0_XML));
					list.add(Arguments.of(ID, RC, Reply, TS2_XML));
					list.add(Arguments.of(ID, RC, Reply, TS3_XML));
					list.add(Arguments.of(ID, RC, Reply, TS4_XML));
					list.add(Arguments.of(ID, RC, Reply, TS5_XML));
					list.add(Arguments.of(ID, RC, Reply, TS6_XML));
					list.add(Arguments.of(ID, RC, Reply, TS7_XML));
					list.add(Arguments.of(ID, RC, Reply, TS8_XML));
					list.add(Arguments.of(ID, RC, Reply, TIMESTAMP_NOT_XML));
					list.add(Arguments.of(ID, RC, Reply, USERID_NOT_XML));
					list.add(Arguments.of(ID, RC, Reply, VERSION_NOT_XML));
					list.add(Arguments.of(ID, RC, Reply, KUBUN_NOT_XML));
					list.add(Arguments.of(ID, RC, Reply, REQUESTID_NOT_XML));
				});
			});
		});

		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:appId={0},xmlPath={1}")
	@MethodSource("responseParseErrorTest")
	@DisplayName("応答戻りパースエラーテスト")
	void responseParseErrorTest(String appId, String xmlPath) throws Exception {

		MQMessage putData = replaceF(fileToString(xmlPath), appId);
		put(QL_DH_REP.getQNames(), putData);
		errorQTest(putData, get_wait(QL_DH_ERR.getQNames()));

	}

	static Stream<Arguments> responseParseErrorTest() {

		List<Arguments> list = new ArrayList<>();
		D.forEach(ID -> {
			list.add(Arguments.of(ID, KUBUN_BREAK_XML));
			list.add(Arguments.of(ID, RC_BREAK_XML));
			list.add(Arguments.of(ID, REPLY_BREAK_XML));
			list.add(Arguments.of(ID, REQUESTID_BREAK_XML));
			list.add(Arguments.of(ID, SERVICEID_BREAK_XML));
			list.add(Arguments.of(ID, TIMESTAMP_BREAK_XML));
			list.add(Arguments.of(ID, TS_BREAK_XML));
			list.add(Arguments.of(ID, USERID_BREAK_XML));
			list.add(Arguments.of(ID, VERSION_BREAK_XML));
		});

		return list.stream();

	}

	@ParameterizedTest(name = "Run{index}:appId={0},reply={1},xmlPath={2}")
	@MethodSource("responseDeadErrorTest")
	@DisplayName("応答戻りデットレターテスト")
	void responseDeadErrorTest(String appId, String reply, String xmlPath) throws Exception {

		try {
			putDisabled(QL_DH_ERR.getQNames());
			String xmlStringData = fileToString(xmlPath);
			xmlStringData = replaceErrorReply(xmlStringData, reply);
			MQMessage putData = replaceF(xmlStringData, appId);
			put(QL_DH_REP.getQNames(), putData);
			deadQTest(putData, get_wait(SYSTEM_ADMIN_EVENT.getQNames()));
		} finally {
			putEnabled(QL_DH_ERR.getQNames());
		}

	}

	static Stream<Arguments> responseDeadErrorTest() {

		List<Arguments> list = new ArrayList<>();
		D.forEach(DX -> {
			ReplyError.forEach(Reply -> {
				list.add(Arguments.of(DX, Reply, KUBUN_BREAK_XML));
				list.add(Arguments.of(DX, Reply, RC_BREAK_XML));
				list.add(Arguments.of(DX, Reply, REPLY_BREAK_XML));
				list.add(Arguments.of(DX, Reply, REQUESTID_BREAK_XML));
				list.add(Arguments.of(DX, Reply, SERVICEID_BREAK_XML));
				list.add(Arguments.of(DX, Reply, TIMESTAMP_BREAK_XML));
				list.add(Arguments.of(DX, Reply, TS_BREAK_XML));
				list.add(Arguments.of(DX, Reply, USERID_BREAK_XML));
				list.add(Arguments.of(DX, Reply, VERSION_BREAK_XML));
			});
		});

		return list.stream();

	}

	@Override
	public String getQmgr() {
		return QMFH01;
	}

	@Override
	public String getLocalhost() {
		return LOCALHOST;
	}

	@Override
	public String getCannal() {
		return SYSTEM_BKR_CONFIG;
	}

	@Override
	public int getPort() {
		return _50014;
	}

}