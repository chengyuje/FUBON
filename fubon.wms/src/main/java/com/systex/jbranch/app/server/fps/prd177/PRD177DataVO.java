package com.systex.jbranch.app.server.fps.prd177;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class PRD177DataVO {
    private BigDecimal productSerialNum;    // 保險產品序號
    private BigDecimal insuranceCoSerialNum;// 保險公司代號
    private String productId;       // 英文代碼
    private String mof;             // 核准文號
    private String productName;     // 保險產品
    private String mainstayProduct; // 主推商品
    private String productShortName;// 保險產品簡稱
    private String contractId;      // 主附約別
    private String addSubFlag;      // 可附加附約
    private Date productOnDt;       // 產品上架日
    private Date productValidFrom;  // 開始銷售日
    private Date productValidThru;  // 停止銷售日
    private String comm1Flag;       // 保費必輸檢核-目標保費
    private String comm2Flag;       // 保費必輸檢核-超額保費
    private String productType1;    // 產品大分類
    private String productCategory; // 產品中分類
    private String sumAssumedType;  // 保額型態
    private String premiumTable;    // 平台
    private String contractBonus;   // 年化佣金註記
    private String currency1;       // 商品幣別
    private String channel;         // 案件來源
    private String productRisk;     // 產品風險
    private String riskRate;        // 風險等級
    private BigDecimal commRate;    // 目標佣金率
    private BigDecimal commRateA;   // 超額佣金率
    private BigDecimal commRateC;   // 浮動佣金率
    private String boundRate;       // 續期佣金率
    private String productPudType;  // 繳費年期狀態
    private String productPud;      // 繳費年期
    private String productedType;   // 保障期狀態
    private String producted;       // 保障年期
    private String productExpDate;  // 產品年期
    private String memo;            // 備註
    private String chFlag;          // 轉換保單
    private String aFlag;           // 證照檢核-壽險證照
    private String bFlag;           // 證照檢核-投資證照
    private String cFlag;           // 證照檢核-外幣商品證照
    private BigDecimal commRate2;   // 目標保費佣金率(網路)
    private BigDecimal commRateA2;  // 超額保費佣金率(網路)
    private BigDecimal commRateC2;  // 浮動佣金率 (網路)
    private String policyType;      // 契約類型

    private List<ProdBoundDataVO> prodBoundGroup; // 產品 Bound 資料

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getMof() {
        return mof;
    }

    public void setMof(String mof) {
        this.mof = mof;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMainstayProduct() {
        return mainstayProduct;
    }

    public void setMainstayProduct(String mainstayProduct) {
        this.mainstayProduct = mainstayProduct;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getAddSubFlag() {
        return addSubFlag;
    }

    public void setAddSubFlag(String addSubFlag) {
        this.addSubFlag = addSubFlag;
    }

    public Date getProductOnDt() {
        return productOnDt;
    }

    public void setProductOnDt(Date productOnDt) {
        this.productOnDt = productOnDt;
    }

    public Date getProductValidFrom() {
        return productValidFrom;
    }

    public void setProductValidFrom(Date productValidFrom) {
        this.productValidFrom = productValidFrom;
    }

    public Date getProductValidThru() {
        return productValidThru;
    }

    public void setProductValidThru(Date productValidThru) {
        this.productValidThru = productValidThru;
    }

    public String getComm1Flag() {
        return comm1Flag;
    }

    public void setComm1Flag(String comm1Flag) {
        this.comm1Flag = comm1Flag;
    }

    public String getComm2Flag() {
        return comm2Flag;
    }

    public void setComm2Flag(String comm2Flag) {
        this.comm2Flag = comm2Flag;
    }

    public String getProductType1() {
        return productType1;
    }

    public void setProductType1(String productType1) {
        this.productType1 = productType1;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getSumAssumedType() {
        return sumAssumedType;
    }

    public void setSumAssumedType(String sumAssumedType) {
        this.sumAssumedType = sumAssumedType;
    }

    public String getPremiumTable() {
        return premiumTable;
    }

    public void setPremiumTable(String premiumTable) {
        this.premiumTable = premiumTable;
    }

    public String getContractBonus() {
        return contractBonus;
    }

    public void setContractBonus(String contractBonus) {
        this.contractBonus = contractBonus;
    }

    public String getCurrency1() {
        return currency1;
    }

    public void setCurrency1(String currency1) {
        this.currency1 = currency1;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProductRisk() {
        return productRisk;
    }

    public void setProductRisk(String productRisk) {
        this.productRisk = productRisk;
    }

    public String getRiskRate() {
        return riskRate;
    }

    public void setRiskRate(String riskRate) {
        this.riskRate = riskRate;
    }

    public BigDecimal getCommRate() {
        return commRate;
    }

    public void setCommRate(BigDecimal commRate) {
        this.commRate = commRate;
    }

    public BigDecimal getCommRateA() {
        return commRateA;
    }

    public void setCommRateA(BigDecimal commRateA) {
        this.commRateA = commRateA;
    }

    public BigDecimal getCommRateC() {
        return commRateC;
    }

    public void setCommRateC(BigDecimal commRateC) {
        this.commRateC = commRateC;
    }

    public String getBoundRate() {
        return boundRate;
    }

    public void setBoundRate(String boundRate) {
        this.boundRate = boundRate;
    }

    public String getProductPudType() {
        return productPudType;
    }

    public void setProductPudType(String productPudType) {
        this.productPudType = productPudType;
    }

    public String getProductPud() {
        return productPud;
    }

    public void setProductPud(String productPud) {
        this.productPud = productPud;
    }

    public String getProductedType() {
        return productedType;
    }

    public void setProductedType(String productedType) {
        this.productedType = productedType;
    }

    public String getProducted() {
        return producted;
    }

    public void setProducted(String producted) {
        this.producted = producted;
    }

    public String getProductExpDate() {
        return productExpDate;
    }

    public void setProductExpDate(String productExpDate) {
        this.productExpDate = productExpDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getChFlag() {
        return chFlag;
    }

    public void setChFlag(String chFlag) {
        this.chFlag = chFlag;
    }

    public String getaFlag() {
        return aFlag;
    }

    public void setaFlag(String aFlag) {
        this.aFlag = aFlag;
    }

    public String getbFlag() {
        return bFlag;
    }

    public void setbFlag(String bFlag) {
        this.bFlag = bFlag;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    public BigDecimal getInsuranceCoSerialNum() {
        return insuranceCoSerialNum;
    }

    public void setInsuranceCoSerialNum(BigDecimal insuranceCoSerialNum) {
        this.insuranceCoSerialNum = insuranceCoSerialNum;
    }

    public BigDecimal getCommRate2() {
        return commRate2;
    }

    public void setCommRate2(BigDecimal commRate2) {
        this.commRate2 = commRate2;
    }

    public BigDecimal getCommRateA2() {
        return commRateA2;
    }

    public void setCommRateA2(BigDecimal commRateA2) {
        this.commRateA2 = commRateA2;
    }

    public BigDecimal getCommRateC2() {
        return commRateC2;
    }

    public void setCommRateC2(BigDecimal commRateC2) {
        this.commRateC2 = commRateC2;
    }

    public List<ProdBoundDataVO> getProdBoundGroup() {
        return prodBoundGroup;
    }

    public void setProdBoundGroup(List<ProdBoundDataVO> prodBoundGroup) {
        this.prodBoundGroup = prodBoundGroup;
    }

    public BigDecimal getProductSerialNum() {
        return productSerialNum;
    }

    public void setProductSerialNum(BigDecimal productSerialNum) {
        this.productSerialNum = productSerialNum;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
}
