package com.systex.jbranch.fubon.commons.esb.vo.nfbrn1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by SebastianWu on 2016/10/6.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN1OutputVO {
    @XmlElement
    private String ERR_COD;             //錯誤碼
    @XmlElement
    private String ERR_TXT;             //錯誤訊息
    @XmlElement
    private String TX_FLG;              //是否可承作交易
    @XmlElement
    private String ERR_COD_1;           //1錯誤碼
    @XmlElement
    private String ERR_TXT_1;           //1錯誤訊息
    @XmlElement
    private String TRUST_CURR_1;        //1投資幣別
    @XmlElement
    private String FEE_1;          //1手續費
    @XmlElement
    private String FEE_RATE_1;     //1手續費率
    @XmlElement
    private String GROUP_OFA_1;         //1優惠團體代碼(O)
    @XmlElement
    private String ERR_COD_2;           //2錯誤碼
    @XmlElement
    private String ERR_TXT_2;           //2錯誤訊息
    @XmlElement
    private String TRUST_CURR_2;        //2投資幣別
    @XmlElement
    private String FEE_2;           //2手續費
    @XmlElement
    private String FEE_RATE_2;      //2手續費率
    @XmlElement
    private String GROUP_OFA_2;         //2優惠團體代碼(O)
    @XmlElement
    private String ERR_COD_3;           //3錯誤碼
    @XmlElement
    private String ERR_TXT_3;           //3錯誤訊息
    @XmlElement
    private String TRUST_CURR_3;        //3投資幣別
    @XmlElement
    private String FEE_3;           //3手續費
    @XmlElement
    private String FEE_RATE_3;      //3手續費率
    @XmlElement
    private String GROUP_OFA_3;         //3優惠團體代碼(O)
    @XmlElement
    private String FEE_M_1;         //手續費1(中)
    @XmlElement
    private String FEE_RATE_M_1;    //手續費率1(中)
    @XmlElement
    private String FEE_H_1;         //手續費1(高)
    @XmlElement
    private String FEE_RATE_H_1;    //手續費率1(高)
    @XmlElement
    private String FEE_M_2;         //手續費2(中)
    @XmlElement
    private String FEE_RATE_M_2;    //手續費率2(中)
    @XmlElement
    private String FEE_H_2;         //手續費2(高)
    @XmlElement
    private String FEE_RATE_H_2;    //手續費率2(高)
    @XmlElement
    private String FEE_M_3;         //手續費3(中)
    @XmlElement
    private String FEE_RATE_M_3;    //手續費率3(中)
    @XmlElement
    private String FEE_H_3;         //手續費3(高)
    @XmlElement
    private String FEE_RATE_H_3;    //手續費率3(高)
    
    
	public String getERR_COD() {
		return ERR_COD;
	}
	public void setERR_COD(String eRR_COD) {
		ERR_COD = eRR_COD;
	}
	public String getERR_TXT() {
		return ERR_TXT;
	}
	public void setERR_TXT(String eRR_TXT) {
		ERR_TXT = eRR_TXT;
	}
	public String getTX_FLG() {
		return TX_FLG;
	}
	public void setTX_FLG(String tX_FLG) {
		TX_FLG = tX_FLG;
	}
	public String getERR_COD_1() {
		return ERR_COD_1;
	}
	public void setERR_COD_1(String eRR_COD_1) {
		ERR_COD_1 = eRR_COD_1;
	}
	public String getERR_TXT_1() {
		return ERR_TXT_1;
	}
	public void setERR_TXT_1(String eRR_TXT_1) {
		ERR_TXT_1 = eRR_TXT_1;
	}
	public String getTRUST_CURR_1() {
		return TRUST_CURR_1;
	}
	public void setTRUST_CURR_1(String tRUST_CURR_1) {
		TRUST_CURR_1 = tRUST_CURR_1;
	}
	public String getFEE_1() {
		return FEE_1;
	}
	public void setFEE_1(String fEE_1) {
		FEE_1 = fEE_1;
	}
	public String getFEE_RATE_1() {
		return FEE_RATE_1;
	}
	public void setFEE_RATE_1(String fEE_RATE_1) {
		FEE_RATE_1 = fEE_RATE_1;
	}
	public String getGROUP_OFA_1() {
		return GROUP_OFA_1;
	}
	public void setGROUP_OFA_1(String gROUP_OFA_1) {
		GROUP_OFA_1 = gROUP_OFA_1;
	}
	public String getERR_COD_2() {
		return ERR_COD_2;
	}
	public void setERR_COD_2(String eRR_COD_2) {
		ERR_COD_2 = eRR_COD_2;
	}
	public String getERR_TXT_2() {
		return ERR_TXT_2;
	}
	public void setERR_TXT_2(String eRR_TXT_2) {
		ERR_TXT_2 = eRR_TXT_2;
	}
	public String getTRUST_CURR_2() {
		return TRUST_CURR_2;
	}
	public void setTRUST_CURR_2(String tRUST_CURR_2) {
		TRUST_CURR_2 = tRUST_CURR_2;
	}
	public String getFEE_2() {
		return FEE_2;
	}
	public void setFEE_2(String fEE_2) {
		FEE_2 = fEE_2;
	}
	public String getFEE_RATE_2() {
		return FEE_RATE_2;
	}
	public void setFEE_RATE_2(String fEE_RATE_2) {
		FEE_RATE_2 = fEE_RATE_2;
	}
	public String getGROUP_OFA_2() {
		return GROUP_OFA_2;
	}
	public void setGROUP_OFA_2(String gROUP_OFA_2) {
		GROUP_OFA_2 = gROUP_OFA_2;
	}
	public String getERR_COD_3() {
		return ERR_COD_3;
	}
	public void setERR_COD_3(String eRR_COD_3) {
		ERR_COD_3 = eRR_COD_3;
	}
	public String getERR_TXT_3() {
		return ERR_TXT_3;
	}
	public void setERR_TXT_3(String eRR_TXT_3) {
		ERR_TXT_3 = eRR_TXT_3;
	}
	public String getTRUST_CURR_3() {
		return TRUST_CURR_3;
	}
	public void setTRUST_CURR_3(String tRUST_CURR_3) {
		TRUST_CURR_3 = tRUST_CURR_3;
	}
	public String getFEE_3() {
		return FEE_3;
	}
	public void setFEE_3(String fEE_3) {
		FEE_3 = fEE_3;
	}
	public String getFEE_RATE_3() {
		return FEE_RATE_3;
	}
	public void setFEE_RATE_3(String fEE_RATE_3) {
		FEE_RATE_3 = fEE_RATE_3;
	}
	public String getGROUP_OFA_3() {
		return GROUP_OFA_3;
	}
	public void setGROUP_OFA_3(String gROUP_OFA_3) {
		GROUP_OFA_3 = gROUP_OFA_3;
	}
	public String getFEE_M_1() {
		return FEE_M_1;
	}
	public void setFEE_M_1(String fEE_M_1) {
		FEE_M_1 = fEE_M_1;
	}
	public String getFEE_RATE_M_1() {
		return FEE_RATE_M_1;
	}
	public void setFEE_RATE_M_1(String fEE_RATE_M_1) {
		FEE_RATE_M_1 = fEE_RATE_M_1;
	}
	public String getFEE_H_1() {
		return FEE_H_1;
	}
	public void setFEE_H_1(String fEE_H_1) {
		FEE_H_1 = fEE_H_1;
	}
	public String getFEE_RATE_H_1() {
		return FEE_RATE_H_1;
	}
	public void setFEE_RATE_H_1(String fEE_RATE_H_1) {
		FEE_RATE_H_1 = fEE_RATE_H_1;
	}
	public String getFEE_M_2() {
		return FEE_M_2;
	}
	public void setFEE_M_2(String fEE_M_2) {
		FEE_M_2 = fEE_M_2;
	}
	public String getFEE_RATE_M_2() {
		return FEE_RATE_M_2;
	}
	public void setFEE_RATE_M_2(String fEE_RATE_M_2) {
		FEE_RATE_M_2 = fEE_RATE_M_2;
	}
	public String getFEE_H_2() {
		return FEE_H_2;
	}
	public void setFEE_H_2(String fEE_H_2) {
		FEE_H_2 = fEE_H_2;
	}
	public String getFEE_RATE_H_2() {
		return FEE_RATE_H_2;
	}
	public void setFEE_RATE_H_2(String fEE_RATE_H_2) {
		FEE_RATE_H_2 = fEE_RATE_H_2;
	}
	public String getFEE_M_3() {
		return FEE_M_3;
	}
	public void setFEE_M_3(String fEE_M_3) {
		FEE_M_3 = fEE_M_3;
	}
	public String getFEE_RATE_M_3() {
		return FEE_RATE_M_3;
	}
	public void setFEE_RATE_M_3(String fEE_RATE_M_3) {
		FEE_RATE_M_3 = fEE_RATE_M_3;
	}
	public String getFEE_H_3() {
		return FEE_H_3;
	}
	public void setFEE_H_3(String fEE_H_3) {
		FEE_H_3 = fEE_H_3;
	}
	public String getFEE_RATE_H_3() {
		return FEE_RATE_H_3;
	}
	public void setFEE_RATE_H_3(String fEE_RATE_H_3) {
		FEE_RATE_H_3 = fEE_RATE_H_3;
	}
}
