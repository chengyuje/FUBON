package com.systex.jbranch.app.server.fps.pms205;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 追蹤產品設定<br>
 * Comments Name : pms205InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS205InputVO extends PagingInputVO{
	private String id;
	private String MAIN_COM_NBR;
	private String REL_COM_NBR;
	private Date eTime;
	
	private String sTimes;
	private String eTimes;
	
	private String es;
	
	
	
	
	
	

	
	public String getEs() {
		return es;
	}
	public void setEs(String es) {
		this.es = es;
	}
	public Date geteTime() {
		return eTime;
	}
	public void seteTime(Date eTime) {
		this.eTime = eTime;
	}
	public String getsTimes() {
		return sTimes;
	}
	public void setsTimes(String sTimes) {
		this.sTimes = sTimes;
	}
	public String geteTimes() {
		return eTimes;
	}
	public void seteTimes(String eTimes) {
		this.eTimes = eTimes;
	}
	//	public Date getsTimes() {
//		return sTimes;
//	}
//	public void setsTimes(Date sTimes) {
//		this.sTimes = sTimes;
//	}
//	public Date geteTimes() {
//		return eTimes;
//	}
//	public void seteTimes(Date eTimes) {
//		this.eTimes = eTimes;
//	}
//	public Date geteTime() {
//		return eTime;
//	}
//	public void seteTime(Date eTime) {
//		this.eTime = eTime;
//	}
	private String MAIN_COM_NBR_DEL;
	private String eTime_DEL;
	
	private String checkgi;
	
	
	
	public String getCheckgi() {
		return checkgi;
	}
	public void setCheckgi(String checkgi) {
		this.checkgi = checkgi;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getMAIN_COM_NBR() {
		return MAIN_COM_NBR;
	}
	public String getREL_COM_NBR() {
		return REL_COM_NBR;
	}
	public void setMAIN_COM_NBR(String mAIN_COM_NBR) {
		MAIN_COM_NBR = mAIN_COM_NBR;
	}
	public void setREL_COM_NBR(String rEL_COM_NBR) {
		REL_COM_NBR = rEL_COM_NBR;
	}
	public String getMAIN_COM_NBR_DEL() {
		return MAIN_COM_NBR_DEL;
	}
	public void setMAIN_COM_NBR_DEL(String mAIN_COM_NBR_DEL) {
		MAIN_COM_NBR_DEL = mAIN_COM_NBR_DEL;
	}
	public String geteTime_DEL() {
		return eTime_DEL;
	}
	public void seteTime_DEL(String eTime_DEL) {
		this.eTime_DEL = eTime_DEL;
	}
}
