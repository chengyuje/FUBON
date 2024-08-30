package com.systex.jbranch.platform.server.eclient.conversation.datasource;

import com.systex.jbranch.platform.server.conversation.message.EnumOutputType;
import com.systex.jbranch.platform.server.conversation.message.EnumToaHeader;
import com.systex.jbranch.platform.server.conversation.message.Toa;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import java.util.ArrayList;
import java.util.List;
/**
 * 針對類似 ComboBox 元件的複合資料，所提供的資料來源
 * 以利端未更換
 * @author Ryan
 *
 */
public class TOADatasource extends Toa {
	private List<DsField> aryToaDatasource = new ArrayList<DsField>();

	public TOADatasource() {
		this.Headers().setInt(EnumToaHeader.OutputType, EnumOutputType.DataSource.ordinal());
	}
	/**
	 * 加入一組替換資料
	 * @return
	 */
	public DsField addDataSource()
	{
		return addDataSource("");
	}
	public DsField addDataSource(String sFieldId)
	{
		DsField obj = new DsField();
		obj.setFieldId(sFieldId);
		this.aryToaDatasource.add(obj);
		return obj;
	}

	public String toXML()
	{
		String outputData = "<document>";
		for(int nPos=0; nPos<this.aryToaDatasource.size(); nPos++)
		{
			outputData += this.aryToaDatasource.get(nPos).toString();
		}
		outputData += "</document>";
		return outputData;
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
