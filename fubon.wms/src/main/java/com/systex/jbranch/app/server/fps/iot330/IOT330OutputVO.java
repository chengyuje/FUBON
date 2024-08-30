package com.systex.jbranch.app.server.fps.iot330;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT330OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> Bounced;
	private List<Map<String, Object>> IOT_MAIN;
	private List<Map<String, Object>> INO;
	private String INS_KEYNO;
	
	
	public String getINS_KEYNO() {
		return INS_KEYNO;
	}

	public void setINS_KEYNO(String iNS_KEYNO) {
		INS_KEYNO = iNS_KEYNO;
	}

	public List<Map<String, Object>> getIOT_MAIN() {
		return IOT_MAIN;
	}

	public void setIOT_MAIN(List<Map<String, Object>> iOT_MAIN) {
		IOT_MAIN = iOT_MAIN;
	}

	public List<Map<String, Object>> getBounced() {
		return Bounced;
	}

	public void setBounced(List<Map<String, Object>> bounced) {
		Bounced = bounced;
	}

	public List<Map<String, Object>> getINO() {
		return INO;
	}

	public void setINO(List<Map<String, Object>> iNO) {
		INO = iNO;
	}
	
	
	
	

}
