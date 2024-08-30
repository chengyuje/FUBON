package com.systex.jbranch.app.server.fps.ins111;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS111OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> COMList;				//保險公司
	private List<Map<String, Object>> IS_MAIN_YList;		//主約
	private List<Map<String, Object>> PAYMENTYEAR_SELList;	//繳費年期
	private List<Map<String, Object>> CURR_CDList;			//幣別
	private List<Map<String, Object>> COVERYEAR_SELList;	//保障年期
	private List<Map<String, Object>> INSURED_OBJECTList;	//保險對象
	private List<Map<String, Object>> KIND_SELList;			//種類
	private List<Map<String, Object>> QUANTITY_STYLEList;	//單位-保額挑選值(ITEM_P)	
	private List<Map<String, Object>> IS_MAIN_NList;		//附約
	private List<Map<String, Object>> PAYMENTYEAR_SEL_NList;//繳費年期(附約)
	private List<Map<String, Object>> CUUR_CD_NList;		//幣別(附約)
	private List<Map<String, Object>> COVERYEAR_SEL_NList;	//保障年期(附約)
	private List<Map<String, Object>> INSURED_OBJECT_NList;	//保險對象(附約)
	private List<Map<String, Object>> KIND_SEL_NList;		//種類(附約)
	private List<Map<String, Object>> QUANTITY_STYLE_NList;	//單位-保額挑選值(附約)
	
	// sen 2018/01/22 增加手動取得資訊
	private List<Map<String, Object>> INS_FORM_IDList; //保險單號
//	private List<Map<String, Object>> IS_MAIN_NList2;		//附約
//	private List<Map<String, Object>> INS_DATA_PRDList; //產品資訊
	
	private String QUANTITY_STYLE;							//單位-保額挑選值(ITEM_U)
	private String COVERCACULUNITDESC;						//單位
	private String IS_WL;									//是否終身
	private String WL_TERM;									//終身年齡
	
	public List<Map<String, Object>> getCOMList() {
		return COMList;
	}

	public void setCOMList(List<Map<String, Object>> cOMList) {
		COMList = cOMList;
	}

	public List<Map<String, Object>> getIS_MAIN_YList() {
		return IS_MAIN_YList;
	}

	public List<Map<String, Object>> getPAYMENTYEAR_SELList() {
		return PAYMENTYEAR_SELList;
	}

	public List<Map<String, Object>> getCURR_CDList() {
		return CURR_CDList;
	}

	public List<Map<String, Object>> getCOVERYEAR_SELList() {
		return COVERYEAR_SELList;
	}

	public List<Map<String, Object>> getINSURED_OBJECTList() {
		return INSURED_OBJECTList;
	}

	public List<Map<String, Object>> getKIND_SELList() {
		return KIND_SELList;
	}

	public List<Map<String, Object>> getQUANTITY_STYLEList() {
		return QUANTITY_STYLEList;
	}

	public List<Map<String, Object>> getIS_MAIN_NList() {
		return IS_MAIN_NList;
	}

	public List<Map<String, Object>> getPAYMENTYEAR_SEL_NList() {
		return PAYMENTYEAR_SEL_NList;
	}

	public List<Map<String, Object>> getCUUR_CD_NList() {
		return CUUR_CD_NList;
	}

	public List<Map<String, Object>> getCOVERYEAR_SEL_NList() {
		return COVERYEAR_SEL_NList;
	}

	public List<Map<String, Object>> getINSURED_OBJECT_NList() {
		return INSURED_OBJECT_NList;
	}

	public List<Map<String, Object>> getKIND_SEL_NList() {
		return KIND_SEL_NList;
	}

	public List<Map<String, Object>> getQUANTITY_STYLE_NList() {
		return QUANTITY_STYLE_NList;
	}

	public void setIS_MAIN_YList(List<Map<String, Object>> iS_MAIN_YList) {
		IS_MAIN_YList = iS_MAIN_YList;
	}

	public void setPAYMENTYEAR_SELList(List<Map<String, Object>> pAYMENTYEAR_SELList) {
		PAYMENTYEAR_SELList = pAYMENTYEAR_SELList;
	}

	public void setCURR_CDList(List<Map<String, Object>> cURR_CDList) {
		CURR_CDList = cURR_CDList;
	}

	public void setCOVERYEAR_SELList(List<Map<String, Object>> cOVERYEAR_SELList) {
		COVERYEAR_SELList = cOVERYEAR_SELList;
	}

	public void setINSURED_OBJECTList(List<Map<String, Object>> iNSURED_OBJECTList) {
		INSURED_OBJECTList = iNSURED_OBJECTList;
	}

	public void setKIND_SELList(List<Map<String, Object>> kIND_SELList) {
		KIND_SELList = kIND_SELList;
	}

	public void setQUANTITY_STYLEList(List<Map<String, Object>> qUANTITY_STYLEList) {
		QUANTITY_STYLEList = qUANTITY_STYLEList;
	}

	public void setIS_MAIN_NList(List<Map<String, Object>> iS_MAIN_NList) {
		IS_MAIN_NList = iS_MAIN_NList;
	}

	public void setPAYMENTYEAR_SEL_NList(
			List<Map<String, Object>> pAYMENTYEAR_SEL_NList) {
		PAYMENTYEAR_SEL_NList = pAYMENTYEAR_SEL_NList;
	}

	public void setCUUR_CD_NList(List<Map<String, Object>> cUUR_CD_NList) {
		CUUR_CD_NList = cUUR_CD_NList;
	}

	public void setCOVERYEAR_SEL_NList(List<Map<String, Object>> cOVERYEAR_SEL_NList) {
		COVERYEAR_SEL_NList = cOVERYEAR_SEL_NList;
	}

	public void setINSURED_OBJECT_NList(
			List<Map<String, Object>> iNSURED_OBJECT_NList) {
		INSURED_OBJECT_NList = iNSURED_OBJECT_NList;
	}

	public void setKIND_SEL_NList(List<Map<String, Object>> kIND_SEL_NList) {
		KIND_SEL_NList = kIND_SEL_NList;
	}

	public void setQUANTITY_STYLE_NList(
			List<Map<String, Object>> qUANTITY_STYLE_NList) {
		QUANTITY_STYLE_NList = qUANTITY_STYLE_NList;
	}

	public String getQUANTITY_STYLE() {
		return QUANTITY_STYLE;
	}
	public void setQUANTITY_STYLE(String qUANTITY_STYLE) {
		QUANTITY_STYLE = qUANTITY_STYLE;
	}

	public String getCOVERCACULUNITDESC() {
		return COVERCACULUNITDESC;
	}
	public void setCOVERCACULUNITDESC(String cOVERCACULUNITDESC) {
		COVERCACULUNITDESC = cOVERCACULUNITDESC;
	}

	public String getIS_WL() {
		return IS_WL;
	}
	public void setIS_WL(String iS_WL) {
		IS_WL = iS_WL;
	}

	public String getWL_TERM() {
		return WL_TERM;
	}
	public void setWL_TERM(String wL_TERM) {
		WL_TERM = wL_TERM;
	}

	public List<Map<String, Object>> getINS_FORM_IDList() {
		return INS_FORM_IDList;
	}

	public void setINS_FORM_IDList(List<Map<String, Object>> iNS_FORM_IDList) {
		INS_FORM_IDList = iNS_FORM_IDList;
	}

//	public List<Map<String, Object>> getIS_MAIN_NList2() {
//		return IS_MAIN_NList2;
//	}
//
//	public void setIS_MAIN_NList2(List<Map<String, Object>> iS_MAIN_NList2) {
//		IS_MAIN_NList2 = iS_MAIN_NList2;
//	}

//	public List<Map<String, Object>> getINS_DATA_PRDList() {
//		return INS_DATA_PRDList;
//	}
//
//	public void setINS_DATA_PRDList(List<Map<String, Object>> iNS_DATA_PRDList) {
//		INS_DATA_PRDList = iNS_DATA_PRDList;
//	}	
	
	
}
