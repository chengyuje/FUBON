package com.systex.jbranch.host;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ibm.websphere.runtime.ServerName;

public class APServerInfo {

	/**
	 * @return server name
	 * @throws UnknownHostException 
	 */
	public String getServerName() throws UnknownHostException{
	
		String hostName = InetAddress.getLocalHost().getHostName();
		String osName = System.getProperty("os.name");
		
		if ("AIX".equals(osName)) {
			return ServerName.getDisplayName();
		} else {
			return hostName;
		}
	}
}
