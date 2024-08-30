package com.systex.jbranch.platform.server.eclient.conversation.report;

public class PageAttr {
	private EnumPrnOperation operation = EnumPrnOperation.DEFAULT;	
	private int copies = 1;
	private boolean rePrint = false;	
	private String deviceType = "P";
	
	public enum EnumPrnOperation
	{
		WOSA1,	
		WOSA2,
		WOSA3,
		WOSA4,
		WOSA5,
		BRANCH,		//Branch預設
		DEFAULT,	//系統預設
		SYSTEM		//系統選擇
	}
	public enum EnumDeviceType
	{
		Screen,
		PBPrinter,
		SystemPrinter,
		MsrDevice,
		IFDDevice		
	}
	
	/**
	 * 設定此區塊的輸出設備
	 * @param deviceType
	 * 			P:存摺印表機, S:系統印表機, M:磁條機, I:晶片卡機, D: 畫面
	 */
	public void setDeviceType(EnumDeviceType deviceType) {
		this.deviceType = getDeviceType(deviceType);
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	private String getDeviceType(EnumDeviceType deviceType)
	{
		String strType = "P";
		switch(deviceType)
		{
			case Screen:
				strType="D";
				break;
			case IFDDevice:
				strType="I";
				break;		
			case SystemPrinter:
				strType="S";
				break;				
			case MsrDevice:
				strType="M";
				break;				
		}
		return strType;
	}
	
	/**
	 * 設定列印份數
	 * @param copies
	 */
	public void setCopies(int copies) {
		this.copies = copies;
	}
	/**
	 * 設定是否允許重印
	 * @param rePrint
	 */
	public void setRePrint(boolean rePrint) {
		this.rePrint = rePrint;
	}	
	/**
	 * 設定週邊驅動行為模式
	 * @param operation
	 * 			1~5:WOSA1~5, 6:Branch預設, 7:系統預設, 8:系統選擇
	 */
	public void setOperation(EnumPrnOperation operation) {
		this.operation = operation;
	}	
	private int getOperation()
	{
		return this.operation.ordinal()+1;
	}
	
	@Override
	public String toString()
	{
		return String.format("<attr device='%s' copies='%d' rePrint='%s' operation='%d' />",
				this.deviceType, this.copies, this.rePrint, this.getOperation());	
	}
}
