package jp.co.acom.fehub.mq;

public enum ConstantQname {

	QA_DH_DL("QA.DH.DL"),

	QL_DW_REP("QL.DW.REP"),

	QL_DH_HTTP_LSR("QL.DH.HTTP_LSR"),

	QL_DH_ERR("QL.DH.ERR"),
	
	QL_DH_REQ("QL.DH.REQ"),
	
	QC_DH_REQ("QC.DH.REQ"),
	
	QL_DH_REP("QL.DH.REP"),
	
	QA_DH_DF("QA.DH.DF"),
	
	SYSTEM_ADMIN_EVENT("SYSTEM.ADMIN.EVENT");
	
	public static final String QMFH01 = "QMFH01";
	public static final String LOCALHOST = "localhost";
	public static final String SYSTEM_BKR_CONFIG = "SYSTEM.BKR.CONFIG";
	public static final int _50014 = 50014;
	public static final String TS1 = "ts1.xml";
	public static final String TEST_XML = "NORMAL.xml";
	public static final String TEST2_XML = "TS0.xml";
	public static final String TEST3_XML = "TS2.xml";
	public static final String TEST4_XML = "TS3.xml";
	public static final String TEST5_XML = "TS4.xml";
	public static final String TEST6_XML = "TS5.xml";
	public static final String TEST7_XML = "TS6.xml";
	public static final String TEST8_XML = "TS7.xml";
	public static final String TEST9_XML = "TS8.xml";
	public static final String TEST10_XML = "TIMESTAMP_NOT.xml";
	public static final String TEST11_XML = "RC00.xml";
	public static final String TEST12_XML = "RC01.xml";
	public static final String TEST13_XML = "RC_Tab.xml";
	public static final String TEST14_XML = "REPLY.xml";
	public static final String TEST15_XML = "userId_not.xml";
	public static final String TEST16_XML = "version_not.xml";
	public static final String TEST17_XML = "kubun_not.xml";
	public static final String TEST18_XML = "requestId_not.xml";
	public static final String TEST19_XML = "KUBUN_break.xml";
	public static final String TEST20_XML = "RC_break.xml";
	public static final String TEST21_XML = "REPLY_break.xml";
	public static final String TEST22_XML = "REQUESTID_break.xml";
	public static final String TEST23_XML = "SERVICEID_break.xml";
	public static final String TEST24_XML = "TIMESTAMP_break.xml";
	public static final String TEST25_XML = "TS_break.xml";
	public static final String TEST26_XML = "USERID_break.xml";
	public static final String TEST27_XML = "VERSION_break.xml";
	public static final String TEST28_XML = "REPLY_qmgr.xml";
	public static final String TEST29_XML = "REPLY_qName.xml";
	public static final String TEST30_XML = "REPLY_qmgr+qName.xml";
	
	public static final String UTF8 = "UTF-8";
	private String qNames;

	public String getQNames() {
		return qNames;
	}

	private ConstantQname(String qNames) {
		this.qNames = qNames;
	}

}
