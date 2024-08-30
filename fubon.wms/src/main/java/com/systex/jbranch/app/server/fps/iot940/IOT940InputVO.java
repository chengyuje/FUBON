package com.systex.jbranch.app.server.fps.iot940;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT940InputVO extends PagingInputVO {
	private String PREMATCH_SEQ;
	private String CHKLIST_TYPE;
	private Map<String, Object> CHK_YN_LIST;
	
	public String getPREMATCH_SEQ() {
		return PREMATCH_SEQ;
	}
	public void setPREMATCH_SEQ(String pREMATCH_SEQ) {
		PREMATCH_SEQ = pREMATCH_SEQ;
	}
	public String getCHKLIST_TYPE() {
		return CHKLIST_TYPE;
	}
	public void setCHKLIST_TYPE(String cHKLIST_TYPE) {
		CHKLIST_TYPE = cHKLIST_TYPE;
	}
	public Map<String, Object> getCHK_YN_LIST() {
		return CHK_YN_LIST;
	}
	public void setCHK_YN_LIST(Map<String, Object> cHK_YN_LIST) {
		CHK_YN_LIST = cHK_YN_LIST;
	}

}
