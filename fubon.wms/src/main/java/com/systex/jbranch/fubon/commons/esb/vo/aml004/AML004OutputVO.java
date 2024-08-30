package com.systex.jbranch.fubon.commons.esb.vo.aml004;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * 洗錢防制電文
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AML004OutputVO {
    private String CustomerNo;
    private String CustomerName;
    private String CustomerNativeName;
    private String RiskScore;
    private String RiskRanking;
    private String NextReviewDate;
    private String ErrorMessage;
    private String IsPEP;
    private String IsNegativeNews;
    private String IsBlackList;
    private String IsHitTerroristsList;

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerNativeName() {
        return CustomerNativeName;
    }

    public void setCustomerNativeName(String customerNativeName) {
        CustomerNativeName = customerNativeName;
    }

    public String getRiskScore() {
        return RiskScore;
    }

    public void setRiskScore(String riskScore) {
        RiskScore = riskScore;
    }

    public String getRiskRanking() {
        return RiskRanking;
    }

    public void setRiskRanking(String riskRanking) {
        RiskRanking = riskRanking;
    }

    public String getNextReviewDate() {
        return NextReviewDate;
    }

    public void setNextReviewDate(String nextReviewDate) {
        NextReviewDate = nextReviewDate;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getIsPEP() {
        return IsPEP;
    }

    public void setIsPEP(String isPEP) {
        IsPEP = isPEP;
    }

    public String getIsNegativeNews() {
        return IsNegativeNews;
    }

    public void setIsNegativeNews(String isNegativeNews) {
        IsNegativeNews = isNegativeNews;
    }

    public String getIsBlackList() {
        return IsBlackList;
    }

    public void setIsBlackList(String isBlackList) {
        IsBlackList = isBlackList;
    }

    public String getIsHitTerroristsList() {
        return IsHitTerroristsList;
    }

    public void setIsHitTerroristsList(String isHitTerroristsList) {
        IsHitTerroristsList = isHitTerroristsList;
    }
}
