package com.systex.jbranch.platform.server.eclient.conversation.report;

public class TOAPage
{
	private String pageId;
	private PageMsg oMessage = new PageMsg();
	private PageAttr oAttr = new PageAttr();
	//private List<PrnFields> aryPrnField = new ArrayList<PrnFields>();
	private PrnFields oPrnField = new PrnFields();
	/**
	 * 設定某個週邊區塊的代號
	 * @param pageId
	 */
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public PageAttr getAttr()
	{
		return this.oAttr;
	}
	public PageMsg getMessage()
	{
		return this.oMessage;
	}
	public PrnFields getPrnFields()
	{
		return this.oPrnField;
	}

	@Override
	public String toString()
	{
		StringBuilder strPageDoc = new StringBuilder();
		strPageDoc.append(String.format("<page id='%s'>%s%s",
				this.pageId, this.oAttr.toString(), this.oMessage.toString()));
		/*
		for(int nPos=0; nPos<this.aryPrnField.size(); nPos++)
		{
			strPageDoc.append(this.aryPrnField.get(nPos).toString());
		}
		*/
		strPageDoc.append(this.oPrnField.toString());
		strPageDoc.append("</page>");
		return strPageDoc.toString();
	}
}
