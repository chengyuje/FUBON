package com.systex.jbranch.platform.server.conversation.message.payload;

public class EndBracket {

	public static final EndBracket SUCCESS=new EndBracket("success");
	public static final EndBracket DEFAULT_ERROR=new EndBracket("error");
	
	private String result;

	public EndBracket(String result) {

		this.result=result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	

}
