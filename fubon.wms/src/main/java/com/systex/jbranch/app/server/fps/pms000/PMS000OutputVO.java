package com.systex.jbranch.app.server.fps.pms000;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 共用<br>
 * Comments Name : PMS000OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年12月29日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS000OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List totalList;
	private List<Map<String, Object>> orgList;   //可視範圍回傳資料LIST
	private List<Map<String, Object>> regionList;   //可視轄下業務處資料LIST
	private List<Map<String, Object>> areaList;     //可視轄下營運區資料LIST
	private List<Map<String, Object>> region_areaList;     //可視轄下業務處-營運區資料LIST
	private List<Map<String, Object>> branchList;   //可視轄下分行資料LIST
	private List<Map<String, Object>> area_branchList;   //可視轄下分行資料LIST
	private List<Map<String, Object>> aoList;       //可視轄下理專資料AO_LIST
	private List<Map<String, Object>> empList;       //可視轄下理專資料EMP_LIST
	
	private List<Map<String,Object>> fcList;         //FC1~FCX 組織查詢專用LIST

	private List<String> v_regionList; //java端查詢強制帶入之可視查詢條件-區域
	private List<String> v_areaList; //java端查詢強制帶入之可視查詢條件-營運區
	private List<String> v_branchList; //java端查詢強制帶入之可視查詢條件-分行
	private List<String> v_empList; //java端查詢強制帶入之可視查詢條件-EMP
	private List<String> v_aoList; //java端查詢強制帶入之可視查詢條件-AO
	
	private List<Map<String, Object>> ymList;       //報表年月資料選單
	private String currentYM;  //系統時間年月
	
	/**
	 *  欄位說明
	 *  TYPE : 表示產品類別 01:房貸 02:信貸 03:好運貸 04:信用卡
	 *  RANK : 表示該項排名
	 *  TOTAL : 表示該項總人數
	 */
	private List<Map<String, Object>> MTDList;
	private List<Map<String, Object>> YTDList;
	
	private List<Map<String, Object>> IPOList; //IPO產品銷量清單
	
	public String getCurrentYM() {
		return currentYM;
	}
	public void setCurrentYM(String currentYM) {
		this.currentYM = currentYM;
	}
	public List<Map<String, Object>> getYmList() {
		return ymList;
	}
	public void setYmList(List<Map<String, Object>> ymList) {
		this.ymList = ymList;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getTotalList() {
		return totalList;
	}
	public List<Map<String, Object>> getOrgList() {
		return orgList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
	public void setOrgList(List<Map<String, Object>> orgList) {
		this.orgList = orgList;
	}
	public List<Map<String, Object>> getRegionList() {
		return regionList;
	}
	public void setRegionList(List<Map<String, Object>> regionList) {
		this.regionList = regionList;
	}
	public List<Map<String, Object>> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<Map<String, Object>> areaList) {
		this.areaList = areaList;
	}
	public List<Map<String, Object>> getBranchList() {
		return branchList;
	}
	public void setBranchList(List<Map<String, Object>> branchList) {
		this.branchList = branchList;
	}
	public List<Map<String, Object>> getAoList() {
		return aoList;
	}
	public void setAoList(List<Map<String, Object>> aoList) {
		this.aoList = aoList;
	}
	public List<Map<String, Object>> getRegion_areaList() {
		return region_areaList;
	}
	public void setRegion_areaList(List<Map<String, Object>> region_areaList) {
		this.region_areaList = region_areaList;
	}
	public List<Map<String, Object>> getArea_branchList() {
		return area_branchList;
	}
	public void setArea_branchList(List<Map<String, Object>> area_branchList) {
		this.area_branchList = area_branchList;
	}
	public List<String> getV_regionList() {
		return v_regionList;
	}
	public void setV_regionList(List<String> v_regionList) {
		this.v_regionList = v_regionList;
	}
	public List<String> getV_areaList() {
		return v_areaList;
	}
	public void setV_areaList(List<String> v_areaList) {
		this.v_areaList = v_areaList;
	}
	public List<String> getV_branchList() {
		return v_branchList;
	}
	public void setV_branchList(List<String> v_branchList) {
		this.v_branchList = v_branchList;
	}
	public List<String> getV_empList() {
		return v_empList;
	}
	public void setV_empList(List<String> v_empList) {
		this.v_empList = v_empList;
	}
	public List<String> getV_aoList() {
		return v_aoList;
	}
	public void setV_aoList(List<String> v_aoList) {
		this.v_aoList = v_aoList;
	}
	public List<Map<String, Object>> getFcList() {
		return fcList;
	}
	public void setFcList(List<Map<String, Object>> fcList) {
		this.fcList = fcList;
	}
	public List<Map<String, Object>> getEmpList() {
		return empList;
	}
	public void setEmpList(List<Map<String, Object>> empList) {
		this.empList = empList;
	}
	public List<Map<String, Object>> getMTDList() {
		return MTDList;
	}
	public void setMTDList(List<Map<String, Object>> mTDList) {
		MTDList = mTDList;
	}
	public List<Map<String, Object>> getYTDList() {
		return YTDList;
	}
	public void setYTDList(List<Map<String, Object>> yTDList) {
		YTDList = yTDList;
	}
	public List<Map<String, Object>> getIPOList() {
		return IPOList;
	}
	public void setIPOList(List<Map<String, Object>> iPOList) {
		IPOList = iPOList;
	}
	
}
