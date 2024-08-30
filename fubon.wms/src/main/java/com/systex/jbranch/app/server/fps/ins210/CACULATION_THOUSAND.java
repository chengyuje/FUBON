package com.systex.jbranch.app.server.fps.ins210;

import java.math.BigDecimal;




/**
 * Calculate the amount to multiply the amount and return the bigdecimal
 * @author  Jimmy Kao
 * */
public class CACULATION_THOUSAND {
	
	private BigDecimal Ten_thousand;
	private BigDecimal LIFE_EXPENSE;//家庭生活準備金(每月)
	private BigDecimal LIFE_EXPENSE_AMT;//每月家庭生活準備金-總生活費
	private BigDecimal LOAN_AMT;//負債總額
	private BigDecimal EDU_FEE;//子女教育費
	private BigDecimal PREPARE_AMT;//已準備資金
	private BigDecimal CAREFEES;//看護費
	
	
	/**
	 * @param body(INS210InputVO)
	 * */
	public void CaculateMultiplyThousand(Object body){
		INS210InputVO inputVO = (INS210InputVO) body;
		
		Ten_thousand = new BigDecimal(10000);
		LIFE_EXPENSE = new BigDecimal(0);//家庭生活準備金(每月)
		LIFE_EXPENSE_AMT = new BigDecimal(0);//每月家庭生活準備金-總生活費
		LOAN_AMT = new BigDecimal(0);//負債總額
		EDU_FEE = new BigDecimal(0);//子女教育費
		PREPARE_AMT = new BigDecimal(0);//已準備資金
		CAREFEES = new BigDecimal(0);//看護費
		
		if(inputVO.getLIFE_EXPENSE() != null){
			LIFE_EXPENSE = new BigDecimal(inputVO.getLIFE_EXPENSE().replaceAll(",", "")).multiply(Ten_thousand);//家庭生活準備金(每月)
		}
		if(inputVO.getLIFE_EXPENSE_AMT() != null){
			LIFE_EXPENSE_AMT = inputVO.getLIFE_EXPENSE_AMT().multiply(Ten_thousand);//每月家庭生活準備金-總生活費
		}
		if(inputVO.getLOAN_AMT() != null){
			LOAN_AMT = new BigDecimal(inputVO.getLOAN_AMT().replaceAll(",", "")).multiply(Ten_thousand);//負債總額
		}
		if(inputVO.getEDU_FEE() != null){
			EDU_FEE = new BigDecimal(inputVO.getEDU_FEE().replaceAll(",", "")).multiply(Ten_thousand);//子女教育費
		}
		if(inputVO.getPREPARE_AMT() != null){
			PREPARE_AMT = new BigDecimal(inputVO.getPREPARE_AMT().replaceAll(",", "")).multiply(Ten_thousand);//已準備資金
		}
		if(inputVO.getCAREFEES() != null){
			CAREFEES = new BigDecimal(inputVO.getCAREFEES().replaceAll(",", "")).multiply(Ten_thousand);//看護費
		}
	}

	public BigDecimal getTen_thousand() {
		return Ten_thousand;
	}

	public BigDecimal getLIFE_EXPENSE() {
		return LIFE_EXPENSE;
	}

	public BigDecimal getLIFE_EXPENSE_AMT() {
		return LIFE_EXPENSE_AMT;
	}

	public BigDecimal getLOAN_AMT() {
		return LOAN_AMT;
	}

	public BigDecimal getEDU_FEE() {
		return EDU_FEE;
	}

	public BigDecimal getPREPARE_AMT() {
		return PREPARE_AMT;
	}

	public void setTen_thousand(BigDecimal ten_thousand) {
		Ten_thousand = ten_thousand;
	}

	public void setLIFE_EXPENSE(BigDecimal lIFE_EXPENSE) {
		LIFE_EXPENSE = lIFE_EXPENSE;
	}

	public void setLIFE_EXPENSE_AMT(BigDecimal lIFE_EXPENSE_AMT) {
		LIFE_EXPENSE_AMT = lIFE_EXPENSE_AMT;
	}

	public void setLOAN_AMT(BigDecimal lOAN_AMT) {
		LOAN_AMT = lOAN_AMT;
	}

	public void setEDU_FEE(BigDecimal eDU_FEE) {
		EDU_FEE = eDU_FEE;
	}

	public void setPREPARE_AMT(BigDecimal pREPARE_AMT) {
		PREPARE_AMT = pREPARE_AMT;
	}

	public BigDecimal getCAREFEES() {
		return CAREFEES;
	}

	public void setCAREFEES(BigDecimal cAREFEES) {
		CAREFEES = cAREFEES;
	}
	
	
}
