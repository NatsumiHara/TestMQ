package com.example.demo;

public enum KKK {

	QA_DH_DL("QA.DH.DL"),

	QL_DW_REP("QL.DW.REP"),

	QL_DH_HTTP_LSR("QL.DH.HTTP_LSR"),

	QL_DH_ERR("QL.DH.ERR"),
	
	QL_DH_REQ("QL.DH.REQ"),
	
	QC_DH_REQ("QC.DH.REQ"),
	
	QL_DH_REP("QL.DH.REP");
	
	public static final String QMFH01 = "QMFH01";
	public static final String LOCALHOST = "localhost";
	public static final String SYSTEM_BKR_CONFIG = "SYSTEM.BKR.CONFIG";
	public static final int _50014 = 50014;
	public static final String TEST2_XML = "/Users/webma/Documents/workspace/RizaSample/src/main/java/com/example/demo/test2.xml";
	public static final String TEST_XML = "/Users/webma/Documents/workspace/RizaSample/src/main/java/com/example/demo/test.xml";
	public static final String UTF8 = "UTF-8";
	private String qNames;

	public String getQNames() {
		return qNames;
	}

	private KKK(String qNames) {
		this.qNames = qNames;
	}

}
