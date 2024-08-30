package com.systex.jbranch.app.server.fps.pms206;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : AMC活動量<br>
 * Comments Name : pms206InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS206InputVO extends PagingInputVO{
	
	private String YEARMON;          //年月
	private String AO_CODE;			//舊理專
	private String THIS_WEEK_START_DATE;  //本週起始日
	private String THIS_WEEK_END_DATE;  //本週結束日
	private String LAST_WEEK_START_DATE;  //上週起始日
	private String LAST_WEEK_END_DATE;  //上週結束日
	private String THIS_MON_START_DATE;  //本月起始日
	private String THIS_MON_END_DATE;  //本月結束日
	private String LAST_MON_START_DATE;  //上月起始日
	private String LAST_MON_END_DATE;  //上月結束日
	private String EMP_ID;           //員編
	private String VIP_DEGREE;       
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	
	private String MONTH;//資料日期的月
	private String WEEK;//資料日期的週
	
	

	
	
	public String getMONTH() {
		return MONTH;
	}
	public void setMONTH(String mONTH) {
		MONTH = mONTH;
	}
	public String getWEEK() {
		return WEEK;
	}
	public void setWEEK(String wEEK) {
		WEEK = wEEK;
	}
	public String getYEARMON() {
		return YEARMON;
	}
	public String getAO_CODE() {
		return AO_CODE;
	}
	
	public String getEMP_ID() {
		return EMP_ID;
	}
	public String getVIP_DEGREE() {
		return VIP_DEGREE;
	}
	public void setYEARMON(String yEARMON) {
		YEARMON = yEARMON;
	}
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	public void setVIP_DEGREE(String vIP_DEGREE) {
		VIP_DEGREE = vIP_DEGREE;
	}
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getTHIS_WEEK_START_DATE() {
		return THIS_WEEK_START_DATE;
	}
	public void setTHIS_WEEK_START_DATE(String tHIS_WEEK_START_DATE) {
		THIS_WEEK_START_DATE = tHIS_WEEK_START_DATE;
	}
	public String getTHIS_WEEK_END_DATE() {
		return THIS_WEEK_END_DATE;
	}
	public void setTHIS_WEEK_END_DATE(String tHIS_WEEK_END_DATE) {
		THIS_WEEK_END_DATE = tHIS_WEEK_END_DATE;
	}
	public String getLAST_WEEK_START_DATE() {
		return LAST_WEEK_START_DATE;
	}
	public void setLAST_WEEK_START_DATE(String lAST_WEEK_START_DATE) {
		LAST_WEEK_START_DATE = lAST_WEEK_START_DATE;
	}
	public String getLAST_WEEK_END_DATE() {
		return LAST_WEEK_END_DATE;
	}
	public void setLAST_WEEK_END_DATE(String lAST_WEEK_END_DATE) {
		LAST_WEEK_END_DATE = lAST_WEEK_END_DATE;
	}
	public String getTHIS_MON_START_DATE() {
		return THIS_MON_START_DATE;
	}
	public void setTHIS_MON_START_DATE(String tHIS_MON_START_DATE) {
		THIS_MON_START_DATE = tHIS_MON_START_DATE;
	}
	public String getTHIS_MON_END_DATE() {
		return THIS_MON_END_DATE;
	}
	public void setTHIS_MON_END_DATE(String tHIS_MON_END_DATE) {
		THIS_MON_END_DATE = tHIS_MON_END_DATE;
	}
	public String getLAST_MON_START_DATE() {
		return LAST_MON_START_DATE;
	}
	public void setLAST_MON_START_DATE(String lAST_MON_START_DATE) {
		LAST_MON_START_DATE = lAST_MON_START_DATE;
	}
	public String getLAST_MON_END_DATE() {
		return LAST_MON_END_DATE;
	}
	public void setLAST_MON_END_DATE(String lAST_MON_END_DATE) {
		LAST_MON_END_DATE = lAST_MON_END_DATE;
	}
	
	
}
