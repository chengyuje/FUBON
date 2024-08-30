package com.systex.jbranch.app.server.fps.pms226;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 新戶轉介查詢InputVO<br>
 * Comments Name : PMS226InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月12日<br>
 */
public class PMS226InputVO extends PagingInputVO{
	private String sTime;
	private String branch;
	private String region;
	private String op;
	private String REF_ID;
	private String TP_ID;
	private String fileName;// 上傳檔案名
	private String custId;// 上傳檔案名
	public String getCustId()
	{
		return custId;
	}
	public void setCustId(String custId)
	{
		this.custId = custId;
	}
	public String getREF_ID()
	{
		return REF_ID;
	}
	public void setREF_ID(String rEF_ID)
	{
		REF_ID = rEF_ID;
	}
	public String getTP_ID()
	{
		return TP_ID;
	}
	public void setTP_ID(String tP_ID)
	{
		TP_ID = tP_ID;
	}
	public String getBranch() {
		return branch;
	}
	public String getRegion() {
		return region;
	}
	public String getOp() {
		return op;
	}
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
}
