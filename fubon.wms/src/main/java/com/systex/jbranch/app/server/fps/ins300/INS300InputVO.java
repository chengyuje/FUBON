package com.systex.jbranch.app.server.fps.ins300;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS300InputVO extends PagingInputVO {
	public INS300InputVO(){}
	private String COM_NAME;
	private String QID;
	private String PRD_NAME;
	private String IS_SALE;
	private String IS_MAIN;
	private List isSaleList;

	private String[] prdIDArr;

	public void setCOM_NAME(String COM_NAME){
		this.COM_NAME = COM_NAME;
	}
	public String getCOM_NAME(){
		return COM_NAME;
	}

	public void setQID(String QID){
		this.QID = QID;
	}

	public String getQID(){
		return QID;
	}

	public void setPRD_NAME(String PRD_NAME){
		this.PRD_NAME = PRD_NAME;
	}

	public String getPRD_NAME(){
		return PRD_NAME;
	}

	public void setIS_SALE(String IS_SALE){
		this.IS_SALE = IS_SALE;
	}
	public String getIS_SALE(){
		return IS_SALE;
	}

	public void setIS_MAIN(String IS_MAIN){
		this.IS_MAIN = IS_MAIN;
	}

	public String getIS_MAIN(){
		return IS_MAIN;
	}

	public String[] getPrdIDArr() {
		return prdIDArr;
	}

	public void setPrdIDArr(String[] prdIDArr) {
		this.prdIDArr = prdIDArr;
	}
	public List getIsSaleList() {
		return isSaleList;
	}
	public void setIsSaleList(List isSaleList) {
		this.isSaleList = isSaleList;
	}
	
}
