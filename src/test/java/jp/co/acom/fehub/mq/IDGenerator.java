package jp.co.acom.fehub.mq;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;

public interface IDGenerator {

	default String getUnique24() throws UnknownHostException {

		return new StringBuffer()
				.append(getHex8(InetAddress.getByName(InetAddress.getLocalHost().getHostName()).hashCode()))
				.append(getHex8(new UID().hashCode())).append("00000000").toString().toUpperCase();

	}

	default String getHex8(int value) {

		StringBuffer sb = new StringBuffer(8);

		for (int i = 0; i < 8; i++) {
			sb.append("0123456789abcdef".charAt(value & 15));
			value >>= 4;
		}

		return sb.reverse().toString();

	}

}
