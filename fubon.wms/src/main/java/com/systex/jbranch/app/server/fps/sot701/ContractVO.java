package com.systex.jbranch.app.server.fps.sot701;

import java.math.BigDecimal;

public class ContractVO {

	String CONTRACT_ID;
	String ACC;
	String CUR1;
	BigDecimal VALUE1;
	String CUR2;
	BigDecimal VALUE2;
	String CUR3;
	BigDecimal VALUE3;
	String CUR4;
	BigDecimal VALUE4;
	String CUR5;
	BigDecimal VALUE5;
	String CONTRACT_P_TYPE;
	String CONTRACT_SPE_FLAG;
	String CREDIT_FLAG; //受益人滿55歲(Y/N)
	BigDecimal TRUST_PEOP_NUM;
	String GUARDIANSHIP_FLAG; // 空白：無監護輔助 1.監護宣告 2輔助宣告
	
	
	
	public String getGUARDIANSHIP_FLAG() {
		return GUARDIANSHIP_FLAG;
	}

	public void setGUARDIANSHIP_FLAG(String gUARDIANSHIP_FLAG) {
		GUARDIANSHIP_FLAG = gUARDIANSHIP_FLAG;
	}

	public BigDecimal getTRUST_PEOP_NUM() {
		return TRUST_PEOP_NUM;
	}

	public void setTRUST_PEOP_NUM(BigDecimal tRUST_PEOP_NUM) {
		TRUST_PEOP_NUM = tRUST_PEOP_NUM;
	}

	public String getCONTRACT_ID() {
		return CONTRACT_ID;
	}

	public void setCONTRACT_ID(String cONTRACT_ID) {
		CONTRACT_ID = cONTRACT_ID;
	}

	public String getACC() {
		return ACC;
	}

	public void setACC(String aCC) {
		ACC = aCC;
	}

	public String getCUR1() {
		return CUR1;
	}

	public void setCUR1(String cUR1) {
		CUR1 = cUR1;
	}

	public BigDecimal getVALUE1() {
		return VALUE1;
	}

	public void setVALUE1(BigDecimal vALUE1) {
		VALUE1 = vALUE1;
	}

	public String getCUR2() {
		return CUR2;
	}

	public void setCUR2(String cUR2) {
		CUR2 = cUR2;
	}

	public BigDecimal getVALUE2() {
		return VALUE2;
	}

	public void setVALUE2(BigDecimal vALUE2) {
		VALUE2 = vALUE2;
	}

	public String getCUR3() {
		return CUR3;
	}

	public void setCUR3(String cUR3) {
		CUR3 = cUR3;
	}

	public BigDecimal getVALUE3() {
		return VALUE3;
	}

	public void setVALUE3(BigDecimal vALUE3) {
		VALUE3 = vALUE3;
	}

	public String getCUR4() {
		return CUR4;
	}

	public void setCUR4(String cUR4) {
		CUR4 = cUR4;
	}

	public BigDecimal getVALUE4() {
		return VALUE4;
	}

	public void setVALUE4(BigDecimal vALUE4) {
		VALUE4 = vALUE4;
	}

	public String getCUR5() {
		return CUR5;
	}

	public void setCUR5(String cUR5) {
		CUR5 = cUR5;
	}

	public BigDecimal getVALUE5() {
		return VALUE5;
	}

	public void setVALUE5(BigDecimal vALUE5) {
		VALUE5 = vALUE5;
	}

	public String getCONTRACT_P_TYPE() {
		return CONTRACT_P_TYPE;
	}

	public void setCONTRACT_P_TYPE(String cONTRACT_P_TYPE) {
		CONTRACT_P_TYPE = cONTRACT_P_TYPE;
	}

	public String getCONTRACT_SPE_FLAG() {
		return CONTRACT_SPE_FLAG;
	}

	public void setCONTRACT_SPE_FLAG(String cONTRACT_SPE_FLAG) {
		CONTRACT_SPE_FLAG = cONTRACT_SPE_FLAG;
	}

	public String getCREDIT_FLAG() {
		return CREDIT_FLAG;
	}

	public void setCREDIT_FLAG(String cREDIT_FLAG) {
		CREDIT_FLAG = cREDIT_FLAG;
	}



}
