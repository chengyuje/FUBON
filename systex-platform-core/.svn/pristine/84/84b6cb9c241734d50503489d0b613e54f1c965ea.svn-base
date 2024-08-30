package com.systex.jbranch.platform.server.eclient.conversation.report;

import java.util.ArrayList;
import java.util.List;

public class TextField
{
	private final boolean dbWidth=false;
	private final boolean dbHeight=false;
	private final int cpi=12;
	private final int lpi=5;
	private final EnumTextFieldType prnType=EnumTextFieldType.textType;
	private List<txtField> aryTxtField = new ArrayList<txtField>();					
		
	public enum EnumTextFieldType {
		textType,
		msrType,
		barcodeType
	}
	
	/**
	 * InnerClass
	 * 記錄單一區塊的文字訊息
	 * @author Administrator
	 *
	 */
	private class txtField
	{
		private final String constCtrlChar = "|";
		private boolean dbWidth;
		private boolean dbHeight;
		private int cpi;
		private int lpi;
		private int row;
		private int col;		
		private String data="";
		private String type="";		//區分條碼、磁條、一般文字
		
		public void setDbWidth(boolean dbWidth) {
			this.dbWidth = dbWidth;
		}
		public void setDbHeight(boolean dbHeight) {
			this.dbHeight = dbHeight;
		}
		public void setCpi(int cpi) {
			this.cpi = cpi;
		}
		public void setLpi(int lpi) {
			this.lpi = lpi;
		}	
		public void setRow(int row) {
			this.row = row;
		}
		public void setCol(int col) {
			this.col = col;
		}
		public void setData(String data) {
			this.data = data;
		}
		
		private String getCtrl() {
			return String.format("%d%s%d%s%d%s%d",
					(this.dbWidth ? 1 : 0),
					constCtrlChar,
					(this.dbHeight ? 1 : 0),
					constCtrlChar,
					this.cpi,
					constCtrlChar,
					this.lpi);
		}			
		@Override
		public String toString() {
			return "<Field ctrl='" + this.getCtrl() +
					"' row='" + this.row + 
					"' col='" + this.col +
					"' data='" + this.data +
					"' type='" + this.type + "' />";
		}											
	}

	/**
	 * 加入單一文字區塊
	 * @param row 位置
	 * @param col 位置
	 * @param data  資料
	 */
	public void addTxtField(int row, int col, String data)
	{
		addTxtField(this.dbWidth, this.dbHeight, 
					this.cpi, this.lpi,
					row, col, data, prnType);
	}	
	/**
	 * 加入單一文字區塊
	 * @param dbWidth 是否倍寬
	 * @param dbHeight 是否倍高
	 * @param row 位置
	 * @param col 位置
	 * @param data 資料
	 */
	public void addTxtField(boolean dbWidth, boolean dbHeight, int row, int col, String data)
	{
		addTxtField(dbWidth, dbHeight, 
					this.cpi, this.lpi,
					row, col, data, prnType);
	}	
	/**
	 * 加入單一文字區塊
	 * @param cpi 字距
	 * @param lpi 行距
	 * @param row 位置
	 * @param col 位置
	 * @param data 資料
	 */
	public void addTxtField(int cpi, int lpi, int row, int col, String data)
	{
		addTxtField(this.dbWidth, this.dbHeight, 
					cpi, lpi,
					row, col, 
					data, prnType);
	}	
	/**
	 * 加入單一文字區塊
	 * @param dbWidth 是否倍寬
	 * @param dbHeight 是否倍高
	 * @param cpi 字距
	 * @param lpi 行距
	 * @param row 位置
	 * @param col 位置
	 * @param data 資料
	 * @param type
	 */
	public void addTxtField(
			boolean dbWidth, boolean dbHeight,
			int cpi, int lpi,
			int row, int col, 
			String data, EnumTextFieldType prnType)
	{
		txtField obj = new txtField();
		obj.setDbWidth(dbWidth);
		obj.setDbHeight(dbHeight);
		obj.setLpi(lpi);
		obj.setCpi(cpi);
		obj.setRow(row);
		obj.setCol(col);
		obj.setData(data);
		aryTxtField.add(obj);		
	}
											
	@Override
	public String toString()
	{
		StringBuilder strData = new StringBuilder();		
		for (int nPos=0; nPos< this.aryTxtField.size(); nPos++)
		{
			strData.append(this.aryTxtField.get(nPos).toString());
		}		
		return strData.toString();
	}
}
