package com.systex.jbranch.platform.server.eclient.conversation.report;

public class PageMsg {

	private boolean forceInsertClick=false;
	private String insertPrompt="";
	private String printPrompt="";
	
	/**
	 * 
	 * @param forceInsertClick
	 */
	public void setForceInsertClick(boolean forceInsertClick) {
		this.forceInsertClick = forceInsertClick;
	}
	/**
	 * 列印提示訊息
	 * @param insertPrompt
	 */
	public void setInsertPrompt(String insertPrompt) {
		this.insertPrompt = insertPrompt;
	}
	/**
	 * 印表中提示訊息
	 * @param printPrompt
	 */
	public void setPrintPrompt(String printPrompt) {
		this.printPrompt = printPrompt;
	}
	
	@Override
	public String toString()
	{
		return String.format("<message forceInsertClick='%s' insertPrompt='%s' printPrompt='%s' />",
				this.forceInsertClick, this.insertPrompt, this.printPrompt);			
	}	
}
