package jp.co.acom.fehub.tests;

import static jp.co.acom.fehub.mq.ConstantQname.LOCALHOST;
import static jp.co.acom.fehub.mq.ConstantQname.QMFH01;
import static jp.co.acom.fehub.mq.ConstantQname.SYSTEM_BKR_CONFIG;
import static jp.co.acom.fehub.mq.ConstantQname._50014;

public class XmlHttp implements CommonTest{

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
