package com.systex.jbranch.app.server.fps.pms345;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS345InputVO extends PagingInputVO {

	private String ao_code; // 理專
	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	private String branch; // 分行
	private String region; // 區域中心
	private String op; // 營運區
	private String EMP_ID; // 員編
	private String YEARMON; // 年月

	private Date sCreDate;
	private Date eCreDate;
	private Date sCreDate2;
	private Date eCreDate2;

	private String id;
	private String type;
	private String clas;
	private String POLICY_NO; // 保單號碼
	private String num;
	private String ID_DUP; // 保單ID

	public Date getsCreDate() {
		return sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public Date getsCreDate2() {
		return sCreDate2;
	}

	public Date geteCreDate2() {
		return eCreDate2;
	}

	public String getNum() {
		return num;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getClas() {
		return clas;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public void setsCreDate2(Date sCreDate2) {
		this.sCreDate2 = sCreDate2;
	}

	public void seteCreDate2(Date eCreDate2) {
		this.eCreDate2 = eCreDate2;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setClas(String clas) {
		this.clas = clas;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public String getYEARMON() {
		return YEARMON;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public void setYEARMON(String yEARMON) {
		YEARMON = yEARMON;
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

	

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getPOLICY_NO() {
		return POLICY_NO;
	}

	public String getID_DUP() {
		return ID_DUP;
	}

	public void setPOLICY_NO(String pOLICY_NO) {
		POLICY_NO = pOLICY_NO;
	}

	public void setID_DUP(String iD_DUP) {
		ID_DUP = iD_DUP;
	}
}
