package com.systex.jbranch.app.server.fps.iot110;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT110OutputVO extends PagingOutputVO {
	private List KYCList;
	private List resultList;
	private List list;
	private BigDecimal account;
	private BigDecimal agentAccount;
	private String represetName;
	private int age;
	private String fatca;
	private boolean savepass;
	private boolean printAb;
	private String prematchSeq;
	private String invScoreChk;
	private String C_SENIOR_PVAL;
	private List<Map<String, Object>> INVESTList;
	private String invalidSeniorCustEvlRM;
	private String invalidSeniorCustEvlBossB;
	private String invalidSeniorCustEvlBossC;
	private String invalidSeniorCustEvlBossD;
	private String invalidSeniorCustEvlBossE;
	private CustHighNetWorthDataVO hnwcData;
	private String CALLOUT_YN;


	public List getKYCList() {
		return KYCList;
	}
	public void setKYCList(List kYCList) {
		KYCList = kYCList;
	}
	public boolean isSavepass() {
		return savepass;
	}
	public boolean isPrintAb() {
		return printAb;
	}
	public void setSavepass(boolean savepass) {
		this.savepass = savepass;
	}
	public void setPrintAb(boolean printAb) {
		this.printAb = printAb;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public BigDecimal getAccount() {
		return account;
	}
	public void setAccount(BigDecimal account) {
		this.account = account;
	}
	public BigDecimal getAgentAccount() {
		return agentAccount;
	}
	public void setAgentAccount(BigDecimal agentAccount) {
		this.agentAccount = agentAccount;
	}
	public String getRepresetName() {
		return represetName;
	}
	public void setRepresetName(String represetName) {
		this.represetName = represetName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getFatca() {
		return fatca;
	}
	public void setFatca(String fatca) {
		this.fatca = fatca;
	}
	public String getPrematchSeq() {
		return prematchSeq;
	}
	public void setPrematchSeq(String prematchSeq) {
		this.prematchSeq = prematchSeq;
	}
	public String getInvScoreChk() {
		return invScoreChk;
	}
	public void setInvScoreChk(String invScoreChk) {
		this.invScoreChk = invScoreChk;
	}
	public String getC_SENIOR_PVAL() {
		return C_SENIOR_PVAL;
	}
	public void setC_SENIOR_PVAL(String c_SENIOR_PVAL) {
		C_SENIOR_PVAL = c_SENIOR_PVAL;
	}
	public List<Map<String, Object>> getINVESTList() {
		return INVESTList;
	}
	public void setINVESTList(List<Map<String, Object>> iNVESTList) {
		INVESTList = iNVESTList;
	}
	public String getInvalidSeniorCustEvlRM() {
		return invalidSeniorCustEvlRM;
	}
	public void setInvalidSeniorCustEvlRM(String invalidSeniorCustEvlRM) {
		this.invalidSeniorCustEvlRM = invalidSeniorCustEvlRM;
	}
	public String getInvalidSeniorCustEvlBossB() {
		return invalidSeniorCustEvlBossB;
	}
	public void setInvalidSeniorCustEvlBossB(String invalidSeniorCustEvlBossB) {
		this.invalidSeniorCustEvlBossB = invalidSeniorCustEvlBossB;
	}
	public String getInvalidSeniorCustEvlBossC() {
		return invalidSeniorCustEvlBossC;
	}
	public void setInvalidSeniorCustEvlBossC(String invalidSeniorCustEvlBossC) {
		this.invalidSeniorCustEvlBossC = invalidSeniorCustEvlBossC;
	}
	public String getInvalidSeniorCustEvlBossD() {
		return invalidSeniorCustEvlBossD;
	}
	public void setInvalidSeniorCustEvlBossD(String invalidSeniorCustEvlBossD) {
		this.invalidSeniorCustEvlBossD = invalidSeniorCustEvlBossD;
	}
	public String getInvalidSeniorCustEvlBossE() {
		return invalidSeniorCustEvlBossE;
	}
	public void setInvalidSeniorCustEvlBossE(String invalidSeniorCustEvlBossE) {
		this.invalidSeniorCustEvlBossE = invalidSeniorCustEvlBossE;
	}
	public CustHighNetWorthDataVO getHnwcData() {
		return hnwcData;
	}
	public void setHnwcData(CustHighNetWorthDataVO hnwcData) {
		this.hnwcData = hnwcData;
	}
	public String getCALLOUT_YN() {
		return CALLOUT_YN;
	}
	public void setCALLOUT_YN(String cALLOUT_YN) {
		CALLOUT_YN = cALLOUT_YN;
	}
		
}
