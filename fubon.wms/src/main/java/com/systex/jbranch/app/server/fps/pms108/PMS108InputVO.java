package com.systex.jbranch.app.server.fps.pms108;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import java.util.List;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 潛力金流客戶參數設定<br>
 * Comments Name : pms108InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月13日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

public class PMS108InputVO extends PagingInputVO {

	private String ROI1; // 基金
	private String ROI2; // ETF
	private String ROI3; // SI/SN
	private String ROI4; // 海外債
	private String AMT_TWD1; // 基金
	private String AMT_TWD2; // ETF
	private String AMT_TWD3; // SI/SN
	private String AMT_TWD4; // 海外債

	private String TYPE;
	private String ROI;
	private String AMT_TWD;
	private String TER_FEE_YEAR; // 險種年期
	private String INS_NBR; // 險種代號
	private List List; // list修增修改
	private List LDEL; // list刪除用

	public String getROI1() {
		return ROI1;
	}

	public String getROI2() {
		return ROI2;
	}

	public String getROI3() {
		return ROI3;
	}

	public String getROI4() {
		return ROI4;
	}

	public String getAMT_TWD1() {
		return AMT_TWD1;
	}

	public String getAMT_TWD2() {
		return AMT_TWD2;
	}

	public String getAMT_TWD3() {
		return AMT_TWD3;
	}

	public String getAMT_TWD4() {
		return AMT_TWD4;
	}

	public String getROI() {
		return ROI;
	}

	public String getAMT_TWD() {
		return AMT_TWD;
	}

	public String getTER_FEE_YEAR() {
		return TER_FEE_YEAR;
	}

	public String getINS_NBR() {
		return INS_NBR;
	}

	public void setROI1(String rOI1) {
		ROI1 = rOI1;
	}

	public void setROI2(String rOI2) {
		ROI2 = rOI2;
	}

	public void setROI3(String rOI3) {
		ROI3 = rOI3;
	}

	public void setROI4(String rOI4) {
		ROI4 = rOI4;
	}

	public void setAMT_TWD1(String aMT_TWD1) {
		AMT_TWD1 = aMT_TWD1;
	}

	public void setAMT_TWD2(String aMT_TWD2) {
		AMT_TWD2 = aMT_TWD2;
	}

	public void setAMT_TWD3(String aMT_TWD3) {
		AMT_TWD3 = aMT_TWD3;
	}

	public void setAMT_TWD4(String aMT_TWD4) {
		AMT_TWD4 = aMT_TWD4;
	}

	public void setROI(String rOI) {
		ROI = rOI;
	}

	public void setAMT_TWD(String aMT_TWD) {
		AMT_TWD = aMT_TWD;
	}

	public void setTER_FEE_YEAR(String tER_FEE_YEAR) {
		TER_FEE_YEAR = tER_FEE_YEAR;
	}

	public void setINS_NBR(String iNS_NBR) {
		INS_NBR = iNS_NBR;
	}

	public List getLDEL() {
		return LDEL;
	}

	public void setLDEL(List lDEL) {
		LDEL = lDEL;
	}

	public List getList() {
		return List;
	}

	public void setList(List list) {
		List = list;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

}
