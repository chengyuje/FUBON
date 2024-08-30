package com.systex.jbranch.app.server.fps.pms703;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 保險新承作明細報表InputVO<br>
 * Comments Name : PMS703InputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月15日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月15日<br>
 */
public class PMS703InputVO extends PagingInputVO{
	private String sTime;
	private String AO_CODE;
	private String branch;
	private String region;
	private String op;
	
	//套用心可視範圍但vo 沒對應  2017/0707補上
	private String branch_nbr ;
	private String region_center_id ;
	private String ao_code  ;
	private String branch_area_id  ;
	
	
	private String POLICY_NO;   //保單號碼
	private String APPL_ID;   //要保人ID
	
	public String getsTime()
	{
		return sTime;
	}
	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}
	public String getAO_CODE()
	{
		return AO_CODE;
	}
	public void setAO_CODE(String aO_CODE)
	{
		AO_CODE = aO_CODE;
	}
	public String getBranch()
	{
		return branch;
	}
	public void setBranch(String branch)
	{
		this.branch = branch;
	}
	public String getRegion()
	{
		return region;
	}
	public void setRegion(String region)
	{
		this.region = region;
	}
	public String getOp()
	{
		return op;
	}
	public void setOp(String op)
	{
		this.op = op;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getPOLICY_NO() {
		return POLICY_NO;
	}
	public void setPOLICY_NO(String pOLICY_NO) {
		POLICY_NO = pOLICY_NO;
	}
	public String getAPPL_ID() {
		return APPL_ID;
	}
	public void setAPPL_ID(String aPPL_ID) {
		APPL_ID = aPPL_ID;
	}
}
