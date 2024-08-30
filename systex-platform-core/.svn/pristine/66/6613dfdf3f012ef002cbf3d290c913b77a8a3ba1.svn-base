package com.systex.jbranch.platform.server.eclient.conversation.report;
/**
 * 提供週邊輸出用，如印表、磁條機、BarCode、晶片卡更新輸出
 */
import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import java.util.ArrayList;
import java.util.List;



public class TOAReport extends Toa {
	//Document 區
	private String docId="001_Report";

	private List<TOAPage> aryToaPage = new ArrayList<TOAPage>();

	public TOAReport()
	{
		this.Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.Device.ordinal());
	}
	/**
	 * 加入某一筆週邊輸出資料，Ex 印表、磁條
	 * @return
	 */
	public TOAPage addPage()
	{
		TOAPage obj = new TOAPage();
		this.aryToaPage.add(obj);
		obj.setPageId(Integer.toString(pageSize()));
		return obj;
	}

	/**
	 * 清除報表資料
	 */
	public void clearPage()
	{
		this.aryToaPage.clear();
	}

	/**
	 * 設定此週邊資料的代號
	 * @param documentId
	 */
	public void setDocumentId(String documentId) {
		this.docId = documentId;
	}
	public int pageSize()
	{
		return this.aryToaPage.size();
	}

	public String toXML()
	{
		StringBuilder outputData = new StringBuilder();
		//String strHeader = "<?xml version='1.0' encoding='UTF-8'?>";
		outputData.append(String.format("<document id='%s'>", this.docId));
		for(int nPos=0; nPos<this.aryToaPage.size(); nPos++)
		{
			outputData.append(this.aryToaPage.get(nPos).toString());
		}
		outputData.append("</document>");
		return outputData.toString();
	}

	@Override
	public String toString()
	{
		try {
			XMLSerializer oXmlSer = new XMLSerializer();
			JSON json = oXmlSer.read("<root>" + toXML() + "</root>");
			return json.toString();
		} catch(Exception ex) { }
		return "";
	}
}

