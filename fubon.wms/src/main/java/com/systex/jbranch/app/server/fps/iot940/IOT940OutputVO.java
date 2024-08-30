package com.systex.jbranch.app.server.fps.iot940;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class IOT940OutputVO {
	
	private List<Map<String, Object>> CHK_YN_LIST;
	private BigDecimal CHKLIST_KEYNO;

	public List<Map<String, Object>> getCHK_YN_LIST() {
		return CHK_YN_LIST;
	}

	public void setCHK_YN_LIST(List<Map<String, Object>> cHK_YN_LIST) {
		CHK_YN_LIST = cHK_YN_LIST;
	}

	public BigDecimal getCHKLIST_KEYNO() {
		return CHKLIST_KEYNO;
	}

	public void setCHKLIST_KEYNO(BigDecimal cHKLIST_KEYNO) {
		CHKLIST_KEYNO = cHKLIST_KEYNO;
	};

	
}
