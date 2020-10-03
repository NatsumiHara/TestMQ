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
	public static final String UTF8 = "UTF-8";
	
	private String qNames;

	public String getQNames() {
		return qNames;
	}

	private ConstantQname(String qNames) {
		this.qNames = qNames;
	}

}
