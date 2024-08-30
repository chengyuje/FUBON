package com.systex.jbranch.app.server.fps.cmmgr010;

import java.math.BigDecimal;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CMMGR010InputVO extends PagingInputVO{

	public CMMGR010InputVO() {
	}

	private String hostid;
	private String ip;
	private BigDecimal port;
	private String username;
	private String password;

	private String type;

	public String getHostid() {
		return hostid;
	}

	public void setHostid(String hostid) {
		this.hostid = hostid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public BigDecimal getPort() {
		return port;
	}

	public void setPort(BigDecimal port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
