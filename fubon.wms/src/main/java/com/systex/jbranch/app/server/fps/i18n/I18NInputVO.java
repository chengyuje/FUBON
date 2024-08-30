package com.systex.jbranch.app.server.fps.i18n;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class I18NInputVO extends PagingInputVO {
	private String locale;
	private String oriCode;
	private String code;
	private String text;
	private String type;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getOriCode() {
		return oriCode;
	}

	public void setOriCode(String oriCode) {
		this.oriCode = oriCode;
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}