package com.systex.jbranch.platform.server.eclient.conversation.datagrid;

import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;

/**
 * 產生一組對應端末類似 Table 元件的資料，以利輸出
 * @author Administrator
 *
 */
public class TOADatagrid extends Toa {
	private List<TOATable> aryToaTable = new ArrayList<TOATable>();
	
	public TOADatagrid() {
		this.Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.DataGrid.ordinal());
	}	
	/**
	 * 
	 * @param strTable 畫面的 Table 名稱
	 * @return 實際設定資料的 Table 物件
	 */
	public TOATable addTable(String strTable)
	{
		TOATable obj = new TOATable();
		obj.setTableId(strTable);
		this.aryToaTable.add(obj);
		return obj;
	}	
	public void delTable(){
		aryToaTable.clear();
	}
	public TOATable addTable()
	{
		return addTable("");
	}	
	/**
	 * 根據每一列資料組成 XML格式
	 * @return 
	 */
	public String toXML()
	{
		String outputData = "<document>";
		for(int nPos=0; nPos<this.aryToaTable.size(); nPos++)		
		{
			outputData += this.aryToaTable.get(nPos).toString();
		}		
		
		return outputData + "</document>";		
	}
	
	@Override
	public String toString()
	{
		try {
			XMLSerializer oXmlSer = new XMLSerializer();
			//JSON json = oXmlSer.read("<root>" + toXML() + "</root>");
			oXmlSer.setForceTopLevelObject(true);
			JSON json = oXmlSer.read(toXML());
			return json.toString();
		} catch(Exception ex) { }
		return "";
	}
}


