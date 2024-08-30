package com.systex.jbranch.platform.server.eclient.conversation.report;

public class PrnFields
{
	private int topMargin=10;
	private int leftMargin=10;
	private boolean pageBeginFormfeed=false;
	private boolean pageEndFormfeed=false;
	//
	private TextField textField = new TextField();

	/**
	 * 設定單一報表上邊頁
	 * @param topMargin
	 */
	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}
	/**
	 * 設定單一報表左邊界
	 * @param leftMargin
	 */
	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}	
	/**
	 * 印表前是否退紙、退摺
	 * @param pageBeginFormfeed
	 */
	public void setPageBeginFormfeed(boolean pageBeginFormfeed) {
		this.pageBeginFormfeed = pageBeginFormfeed;
	}	
	/**
	 * 印表後是否退紙、退摺
	 * @param pageEndFormfeed
	 */
	public void setPageEndFormfeed(boolean pageEndFormfeed) {
		this.pageEndFormfeed = pageEndFormfeed;
	}	
	private String getAttr()
	{
		return String.format("topMargin='%d' leftMargin='%d'" +
				" formfeedBegin='%s' formfeedEnd='%s'",
				this.topMargin, this.leftMargin, 
				this.pageBeginFormfeed, this.pageEndFormfeed);
	}
	
	/**
	 * 加入單一列印區塊
	 * @return
	 */
	public TextField getTextField()
	{
		return this.textField; 
	}
	
	@Override
	public String toString()
	{
		return String.format("<prnPage %s >%s</prnPage>",
				this.getAttr(), this.textField.toString());
	}
}
