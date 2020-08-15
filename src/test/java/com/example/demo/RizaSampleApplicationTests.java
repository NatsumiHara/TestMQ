package com.example.demo;

//import static com.example.utils.Util.get;
//import static com.example.utils.Util.put;
//import static com.example.utils.Util.fileToData;

//import static com.example.utils.Util.setMQEnvironment;
//import org.junit.jupiter.api.BeforeEach;
import static com.example.demo.KKK.*;


import java.io.BufferedReader;
import java.io.FileReader;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import com.example.utils.FFFF;
import com.example.utils.Super;
import com.ibm.msg.client.wmq.compat.base.internal.MQMessage;

class RizaSampleApplicationTests implements Super, FFFF, IDGenerator {

	@BeforeEach
	void before() throws Exception {

		for (KKK k : values())
			clean(k.getQNames());
	}

	@Test
	@DisplayName("QCからDF")
	void contextLoads() throws Exception {

		String data = fileToString(TEST_XML);
		MQMessage putData1 = putMessages(data);
		put(QC_DH_REQ, putData1);
		System.out.println(DatatypeConverter.printHexBinary(putData1.messageId));
		MQMessage putData2 = putMessages(data);
		put(QC_DH_REQ, putData2);
		System.out.println(DatatypeConverter.printHexBinary(putData2.messageId));
		MQMessage putData3 = putMessages(data);
		put(QC_DH_REQ, putData3);
		System.out.println(DatatypeConverter.printHexBinary(putData3.messageId));

		MQMessage getData = get(QL_DH_HTTP_LSR, putData1.messageId);
//		MQMessage getData = get(QL_DH_HTTP_LSR.getQNames(),getUnique24().getBytes());
//		MQMessage getData = get(QL_DH_HTTP_LSR);
		System.out.println(DatatypeConverter.printHexBinary(getData.correlationId));
//		System.out.println(getData.correlationId);
		String strMessage = getData.readLine();
		System.out.println(strMessage);
		extracted(putData1, getData);
		extracted(putData2, getData);
		extracted(putData3, getData);
	}

	@Test
	@DisplayName("QLからDF")
	void contextLoadsDlql() throws Exception {

		Document data = fileToDocument(TEST_XML);
		String xData = xPath(data, "/CENTER/GLB_HEAD/SERVICEID");
		String xData2 = xPath(data, "/CENTER/GLB_HEAD/TIMESTAMP/TS[1]/@SVR");
		String xData3 = xPath(data, "/CENTER/GLB_HEAD/TIMESTAMP/TS[2]");
		
		
		System.out.println(xData);
		System.out.println(xData2);
		System.out.println(xData3);
		MQMessage putData = putMessages(documentToString(data));
		put(QL_DH_REQ, putData);
//		System.out.println(DatatypeConverter.printHexBinary(putData.messageId));
		MQMessage getData = get(QL_DH_HTTP_LSR, putData.messageId);
//		String ms = mqMessageToString(getData);
//		MQMessage getData = get(QL_DH_HTTP_LSR);
//		String strMessage = getData.readLine();
//		String getData;
	
//		StringBuilder builder = new StringBuilder();
//
//		while (getData.getDataLength() > 0)
//			builder.append(getData.readLine());
//
//		String aaa = builder.toString();
		
//		try (BufferedReader sb = new BufferedReader(new FileReader(getDatas));) {
//			str = sb.readLine();
//			while (str != null) {
//				builder.append(str + System.lineSeparator());
//				str = sb.readLine();
//			}
//			builder.toString();
//		}
//
//		while(strMessage != null) {
//			getData.readLine();
//		}
//		Document docMessage = stringToDocument(ms);
//		String xDataGet = xPath(docMessage, "/CENTER/GLB_HEAD/SERVICEID");
//		System.out.println(xDataGet);
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
