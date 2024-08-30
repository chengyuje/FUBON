package com.systex.jbranch.app.server.fps.cmmgr015;

/**
 * 訊息維護
 * @author SamTu
 * @date 20180719
 *
 */
public class CMMGR015InputVO {

	private String code;
	private String text;
	private String sqlStr;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	  public String getSqlStr()
	  {
	    return this.sqlStr;
	  }

	  public void setSqlStr(String sqlStr)
	  {
	    this.sqlStr = sqlStr;
	  }

}
