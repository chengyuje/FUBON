package com.systex.jbranch.platform.server.eclient.conversation.report;

import java.util.ArrayList;
import java.util.List;
/**
 * 對應磁條機資料輸出用
 * @author Administrator
 *
 */
public class MsrFields
{
	private List<msrField> aryMsrField = new ArrayList<msrField>();
		
	/**
	 * Inner Class
	 * 單純記錄單軌內容
	 * @author Administrator
	 *
	 */
	private class msrField
	{
		private int track;
		private String data;
		public void setTrack(int track) {
			this.track = track;
		}
		public void setData(String data) {
			this.data = data;
		}
		@Override
		public String toString()
		{
			return "<msrField track='" + this.track +
					"' data='" + this.data + "' />";
		}
	}
	
	/**
	 * 單軌內容
	 * @param track 第幾軌
	 * @param data	磁軌資料
	 */
	public void addMsrField(int track, String data)
	{
		msrField obj = new msrField();
		obj.track = track;
		obj.data = data;
		this.aryMsrField.add(obj);
	}
	
	@Override
	public String toString()
	{
		String strData = "<msrFields>";
		for(int nPos=0; nPos<this.aryMsrField.size(); nPos++)
		{
			strData += this.aryMsrField.get(nPos).toString();
		}
		strData += "</msrFields>";
		return strData;
	}
}
