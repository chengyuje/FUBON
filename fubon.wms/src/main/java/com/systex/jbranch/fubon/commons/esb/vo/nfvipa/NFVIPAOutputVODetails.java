package com.systex.jbranch.fubon.commons.esb.vo.nfvipa;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/23.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFVIPAOutputVODetails {

	@XmlElement
	private String ProductType; // 商品類型
	@XmlElement
	private String TrxMarket; // 交易市場
	@XmlElement
	private String InsuranceNo; // 商品代號
	@XmlElement
	private String CurCode; // 商品幣別
	@XmlElement
	private String Number; // 庫存股數
	@XmlElement
	private String State; // 狀態
	@XmlElement
	private String EntrustCur; // 交易幣別
	@XmlElement
	private String AvgBuyingPrice; // 平均買進價格
	@XmlElement
	private String CurAmt; // 參考收盤價
	@XmlElement
	private String ForCurBal; // 外幣現值
	@XmlElement
	private String AcctBal; // 台幣現值
	@XmlElement
	private String Date08; // 收盤價日期
	@XmlElement
	private String ReferenceRate; // 參考匯率
	@XmlElement
	private String TrustBusinessType; // 信託業務別
	@XmlElement
	private String TrustAcct; // 信託帳號
	@XmlElement
	private String ReturnRateSign; // 報酬率正負
	@XmlElement
	private String ReturnRate; // 報酬率
	@XmlElement
	private String ProductType2; // 新舊商品
	@XmlElement
	private String ProductName; // 商品名稱
	@XmlElement
	private String ForCurCost; // 外幣成本
	@XmlElement
	private String AcctBalCost; // 台幣成本
	@XmlElement
	private String Result; // 委託結果 0002: 1= 交易已成交未扣款 2= 交易已成交已扣款未入庫 3=
							// 交易已取消(部分成交)未扣款 4= 交易已取消(部分成交)已扣款未入庫
							// 0003: 1= 交易已成交未撥款 2= 交易已取消(部分成交)未撥款
	@XmlElement
	private String EntrustDate; // 委託日期
	@XmlElement
	private String TradeDate; // 市場交易日
	@XmlElement
	private String EntrustAmt; // 委託股數
	@XmlElement
	private String EntrustPrice; // 委託價格
	@XmlElement
	private String ChannelType; // 通路別 “ “ => 臨櫃 “W” => 網銀 “P” =>CTI “M” =>行銀
	@XmlElement
	private String TrustType; // 信託業務別 Y：外幣 N：台幣
	@XmlElement
	private String TradeAmt; // 成交股數
	@XmlElement
	private String TradePrice; // 成交報價
	@XmlElement
	private String TradeCost; // 成交金額
	@XmlElement
	private String OrderType; // 委託方式 "0"：約定限價 "1"：市價單 "2 "：限價單 (LiMiT order) 簡稱
								// LMT
	@XmlElement
	private String UID; // 身分證號
	@XmlElement
	private String TradeDateEnd; // 截止市場交易日期

	// 2016-12-22 by Jacky 新增資產總覽電文
	// 業務別代碼 40 有價証券信託 51 指單-計劃性投資組合 52 全權委託 58 金錢信託 59 指單-環球動態投資
	// 60 指單-環球固定收益投資 CK 台幣支存 D1 國內代理基金 D2 國內債券型代理基金 DF 代理基金
	// F1 外幣組合式商品 FD 外幣定存 FS 外幣活存 GD 黃金存摺 LN 貸款 PB 台幣活存 PF 私募基金
	// RP RP SD DCD T1 台幣組合式商品 TD 台幣定存 TL 資產總計(依昨日日終買入匯率換算)
	// TM 集合管理帳戶 U1 海外基金 U2 國內基金 U3 國內債券型基金 UF 國內外基金 UJ 海外商品
	// UK 海外股票/ETF V1 保險 VL 投保資產總計(依昨日日終買入匯率換算)
	@XmlElement
	private String BUSINESS_CODE;
	@XmlElement
	private String CURRENCY;
	@XmlElement
	private String TOTAL_SUM;

	/** 2017.11.21 add by Carley 新增賣出委託交易資訊下行 **/
	// 0006
	@XmlElement
	private String StockUForCurBal; // 賣出在途使用金額9(11)V2

	@XmlElement
	private String StockUBal; // 賣出在途使用金額-台幣9(11)V0

	// 0007
	@XmlElement
	private String StockCForCurBal; // 股票/ETF圈存金額9(11)V2

	@XmlElement
	private String StockCBal; // 股票/ETF圈存金額-台幣9(11)V0

	// 0008
	@XmlElement
	private String TrmCurBal; // 股票/ETF委託原幣金額9(11)V2

	@XmlElement
	private String TrmBal; // 股票/ETF委託台幣金額9(11)V0

	// 0009
	@XmlElement
	private String StockTForCurBal; // 股票/ETF在途款原幣金額

	@XmlElement
	private String StockTBal; // 股票/ETF在途款台幣金額

	/** 國內代理基金 FUNCTION:D1及D2 回傳下行 **/
	@XmlElement
	private String ARR00; // 業務別

	@XmlElement
	private String ARR01; // 類別

	@XmlElement
	private String ARR02; // 身份證

	@XmlElement
	private String ARR03; // 分行碼

	@XmlElement
	private String ARR04; // 基金編號

	@XmlElement
	private String ARR05; // 戶號

	@XmlElement
	private String ARR06; // 最近一次申購日

	@XmlElement
	private String ARR07; // 投資單位數

	@XmlElement
	private String ARR08; // 參考贖回淨值

	@XmlElement
	private String ARR09; // 淨值日

	@XmlElement
	private String ARR10; // 原始投資金額

	@XmlElement
	private String ARR11; // 參考現值

	@XmlElement
	private String ARR12; // 報酬率正負

	@XmlElement
	private String ARR13; // 報酬率

	@XmlElement
	private String ARR14; // 年報酬率正負

	@XmlElement
	private String ARR15; // 年報酬率

	@XmlElement
	private String ARR16; // 參考損益正負

	@XmlElement
	private String ARR17; // 參考損益

	@XmlElement
	private String ARR18; // 基金名稱
	
    @XmlElement
    private String CmkType;			//質借圈存註記 	MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他

	/** 海外債orSN商品查詢 FUNCTION:UJ、SN 回傳下行 **/
	@XmlElement
	private String V0100; // 業務別
	@XmlElement
	private String V0101; // 客戶身份證
	@XmlElement
	private String V0102; // 分行代碼
	@XmlElement
	private String V0103; // 債券代碼
	@XmlElement
	private String V0104; // 債券名稱
	@XmlElement
	private String V0105; // 憑證編號
	@XmlElement
	private String V0106; // 投資幣別
	@XmlElement
	private String V0107; // 計價幣別
	@XmlElement
	private String V0108; // 交割日
	@XmlElement
	private String V0109; // 原幣投資成本
	@XmlElement
	private String V0110; // 參考報價
	@XmlElement
	private String V0111; // 參考報價日期
	@XmlElement
	private String V0112; // 參考買回匯率
	@XmlElement
	private String V0113; // 台幣投資成本
	@XmlElement
	private String V0114; // 台幣參考現值
	@XmlElement
	private String V0115; // 原幣參考現值
	@XmlElement
	private String V0116; // 原幣損益正負
	@XmlElement
	private String V0117; // 原幣參考損益
	@XmlElement
	private String V0118; // 原幣報酬正負
	@XmlElement
	private String V0119; // 原幣報酬
	@XmlElement
	private String V0120; // 下次付息日
	@XmlElement
	private String V0121; // 累計配息
	@XmlElement
	private String V0122; // 信託帳號
	@XmlElement
	private String V0123; // 收益帳號
	@XmlElement
	private String V0124; // 幣別１
	@XmlElement
	private String V0125; // 幣別２
	@XmlElement
	private String V0126; // 累計配息２
	@XmlElement
	private String V0127; // 最近一期配息日期
	@XmlElement
	private String V0128; // 最近一期配息金額
	@XmlElement
	private String V0129; // 已付前手息
	@XmlElement
	private String V0130; // 應收前手息
	@XmlElement
	private String V0131; // 含累積現金配息報酬率正負號
	@XmlElement
	private String V0132; // 含累積現金配息報酬率
	@XmlElement
	private String V0133; // 庫存面額
	@XmlElement
	private String V0134; // 申購日期
	@XmlElement
	private String V0135; // 質借註記
	/*
	 * 20230731_#1536_VOC議題 - 海外ETF股票顯示含息投資報酬率
	 */
	@XmlElement
	private String Dividend; // 累積配息
	@XmlElement
	private String ReturnRateSign2; // 含息報酬率正負
	@XmlElement
	private String ReturnRate2; // 含息報酬率 股票代號=LEH-T AND參考收盤價=0顯示--	總長度6位含2位小數

    
    public String getProductType() {

		return ProductType;
	}

	public void setProductType(String productType) {
		ProductType = productType;
	}

	public String getTrxMarket() {
		return TrxMarket;
	}

	public void setTrxMarket(String trxMarket) {
		TrxMarket = trxMarket;
	}

	public String getInsuranceNo() {
		return InsuranceNo;
	}

	public void setInsuranceNo(String insuranceNo) {
		InsuranceNo = insuranceNo;
	}

	public String getCurCode() {
		return CurCode;
	}

	public void setCurCode(String curCode) {
		CurCode = curCode;
	}

	public String getNumber() {
		return Number;
	}

	public void setNumber(String number) {
		Number = number;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getEntrustCur() {
		return EntrustCur;
	}

	public void setEntrustCur(String entrustCur) {
		EntrustCur = entrustCur;
	}

	public String getAvgBuyingPrice() {
		return AvgBuyingPrice;
	}

	public void setAvgBuyingPrice(String avgBuyingPrice) {
		AvgBuyingPrice = avgBuyingPrice;
	}

	public String getCurAmt() {
		return CurAmt;
	}

	public void setCurAmt(String curAmt) {
		CurAmt = curAmt;
	}

	public String getForCurBal() {
		return ForCurBal;
	}

	public void setForCurBal(String forCurBal) {
		ForCurBal = forCurBal;
	}

	public String getAcctBal() {
		return AcctBal;
	}

	public void setAcctBal(String acctBal) {
		AcctBal = acctBal;
	}

	public String getDate08() {
		return Date08;
	}

	public void setDate08(String date08) {
		Date08 = date08;
	}

	public String getReferenceRate() {
		return ReferenceRate;
	}

	public void setReferenceRate(String referenceRate) {
		ReferenceRate = referenceRate;
	}

	public String getTrustBusinessType() {
		return TrustBusinessType;
	}

	public void setTrustBusinessType(String trustBusinessType) {
		TrustBusinessType = trustBusinessType;
	}

	public String getTrustAcct() {
		return TrustAcct;
	}

	public void setTrustAcct(String trustAcct) {
		TrustAcct = trustAcct;
	}

	public String getReturnRateSign() {
		return ReturnRateSign;
	}

	public void setReturnRateSign(String returnRateSign) {
		ReturnRateSign = returnRateSign;
	}

	public String getReturnRate() {
		return ReturnRate;
	}

	public void setReturnRate(String returnRate) {
		ReturnRate = returnRate;
	}

	public String getProductType2() {
		return ProductType2;
	}

	public void setProductType2(String productType2) {
		ProductType2 = productType2;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public String getForCurCost() {
		return ForCurCost;
	}

	public void setForCurCost(String forCurCost) {
		ForCurCost = forCurCost;
	}

	public String getAcctBalCost() {
		return AcctBalCost;
	}

	public void setAcctBalCost(String acctBalCost) {
		AcctBalCost = acctBalCost;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String UID) {
		this.UID = UID;
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public String getEntrustDate() {
		return EntrustDate;
	}

	public void setEntrustDate(String entrustDate) {
		EntrustDate = entrustDate;
	}

	public String getTradeDate() {
		return TradeDate;
	}

	public void setTradeDate(String tradeDate) {
		TradeDate = tradeDate;
	}

	public String getEntrustAmt() {
		return EntrustAmt;
	}

	public void setEntrustAmt(String entrustAmt) {
		EntrustAmt = entrustAmt;
	}

	public String getEntrustPrice() {
		return EntrustPrice;
	}

	public void setEntrustPrice(String entrustPrice) {
		EntrustPrice = entrustPrice;
	}

	public String getChannelType() {
		return ChannelType;
	}

	public void setChannelType(String channelType) {
		ChannelType = channelType;
	}

	public String getTrustType() {
		return TrustType;
	}

	public void setTrustType(String trustType) {
		TrustType = trustType;
	}

	public String getTradeAmt() {
		return TradeAmt;
	}

	public void setTradeAmt(String tradeAmt) {
		TradeAmt = tradeAmt;
	}

	public String getTradePrice() {
		return TradePrice;
	}

	public void setTradePrice(String tradePrice) {
		TradePrice = tradePrice;
	}

	public String getTradeCost() {
		return TradeCost;
	}

	public void setTradeCost(String tradeCost) {
		TradeCost = tradeCost;
	}

	public String getOrderType() {
		return OrderType;
	}

	public void setOrderType(String orderType) {
		OrderType = orderType;
	}

	public String getBUSINESS_CODE() {
		return BUSINESS_CODE;
	}

	public void setBUSINESS_CODE(String bUSINESS_CODE) {
		BUSINESS_CODE = bUSINESS_CODE;
	}

	public String getCURRENCY() {
		return CURRENCY;
	}

	public void setCURRENCY(String cURRENCY) {
		CURRENCY = cURRENCY;
	}

	public String getTOTAL_SUM() {
		return TOTAL_SUM;
	}

	public void setTOTAL_SUM(String tOTAL_SUM) {
		TOTAL_SUM = tOTAL_SUM;
	}

	public String getTradeDateEnd() {
		return TradeDateEnd;
	}

	public void setTradeDateEnd(String tradeDateEnd) {
		TradeDateEnd = tradeDateEnd;
	}

	public String getARR00() {
		return ARR00;
	}

	public void setARR00(String aRR00) {
		ARR00 = aRR00;
	}

	public String getARR01() {
		return ARR01;
	}

	public void setARR01(String aRR01) {
		ARR01 = aRR01;
	}

	public String getARR02() {
		return ARR02;
	}

	public void setARR02(String aRR02) {
		ARR02 = aRR02;
	}

	public String getARR03() {
		return ARR03;
	}

	public void setARR03(String aRR03) {
		ARR03 = aRR03;
	}

	public String getARR04() {
		return ARR04;
	}

	public void setARR04(String aRR04) {
		ARR04 = aRR04;
	}

	public String getARR05() {
		return ARR05;
	}

	public void setARR05(String aRR05) {
		ARR05 = aRR05;
	}

	public String getARR06() {
		return ARR06;
	}

	public void setARR06(String aRR06) {
		ARR06 = aRR06;
	}

	public String getARR07() {
		return ARR07;
	}

	public void setARR07(String aRR07) {
		ARR07 = aRR07;
	}

	public String getARR08() {
		return ARR08;
	}

	public void setARR08(String aRR08) {
		ARR08 = aRR08;
	}

	public String getARR09() {
		return ARR09;
	}

	public void setARR09(String aRR09) {
		ARR09 = aRR09;
	}

	public String getARR10() {
		return ARR10;
	}

	public void setARR10(String aRR10) {
		ARR10 = aRR10;
	}

	public String getARR11() {
		return ARR11;
	}

	public void setARR11(String aRR11) {
		ARR11 = aRR11;
	}

	public String getARR12() {
		return ARR12;
	}

	public void setARR12(String aRR12) {
		ARR12 = aRR12;
	}

	public String getARR13() {
		return ARR13;
	}

	public void setARR13(String aRR13) {
		ARR13 = aRR13;
	}

	public String getARR14() {
		return ARR14;
	}

	public void setARR14(String aRR14) {
		ARR14 = aRR14;
	}

	public String getARR15() {
		return ARR15;
	}

	public void setARR15(String aRR15) {
		ARR15 = aRR15;
	}

	public String getARR16() {
		return ARR16;
	}

	public void setARR16(String aRR16) {
		ARR16 = aRR16;
	}

	public String getARR17() {
		return ARR17;
	}

	public void setARR17(String aRR17) {
		ARR17 = aRR17;
	}

	public String getARR18() {
		return ARR18;
	}

	public void setARR18(String aRR18) {
		ARR18 = aRR18;
	}

	public String getStockUForCurBal() {
		return StockUForCurBal;
	}

	public void setStockUForCurBal(String stockUForCurBal) {
		StockUForCurBal = stockUForCurBal;
	}

	public String getStockUBal() {
		return StockUBal;
	}

	public void setStockUBal(String stockUBal) {
		StockUBal = stockUBal;
	}

	public String getStockCForCurBal() {
		return StockCForCurBal;
	}

	public void setStockCForCurBal(String stockCForCurBal) {
		StockCForCurBal = stockCForCurBal;
	}

	public String getStockCBal() {
		return StockCBal;
	}

	public void setStockCBal(String stockCBal) {
		StockCBal = stockCBal;
	}

	public String getTrmCurBal() {
		return TrmCurBal;
	}

	public void setTrmCurBal(String trmCurBal) {
		TrmCurBal = trmCurBal;
	}

	public String getTrmBal() {
		return TrmBal;
	}

	public void setTrmBal(String trmBal) {
		TrmBal = trmBal;
	}

	public String getStockTForCurBal() {
		return StockTForCurBal;
	}

	public void setStockTForCurBal(String stockTForCurBal) {
		StockTForCurBal = stockTForCurBal;
	}

	public String getStockTBal() {
		return StockTBal;
	}

	public void setStockTBal(String stockTBal) {
		StockTBal = stockTBal;
	}

	public String getV0100() {
		return V0100;
	}

	public void setV0100(String v0100) {
		V0100 = v0100;
	}

	public String getV0101() {
		return V0101;
	}

	public void setV0101(String v0101) {
		V0101 = v0101;
	}

	public String getV0102() {
		return V0102;
	}

	public void setV0102(String v0102) {
		V0102 = v0102;
	}

	public String getV0103() {
		return V0103;
	}

	public void setV0103(String v0103) {
		V0103 = v0103;
	}

	public String getV0104() {
		return V0104;
	}

	public void setV0104(String v0104) {
		V0104 = v0104;
	}

	public String getV0105() {
		return V0105;
	}

	public void setV0105(String v0105) {
		V0105 = v0105;
	}

	public String getV0106() {
		return V0106;
	}

	public void setV0106(String v0106) {
		V0106 = v0106;
	}

	public String getV0107() {
		return V0107;
	}

	public void setV0107(String v0107) {
		V0107 = v0107;
	}

	public String getV0108() {
		return V0108;
	}

	public void setV0108(String v0108) {
		V0108 = v0108;
	}

	public String getV0109() {
		return V0109;
	}

	public void setV0109(String v0109) {
		V0109 = v0109;
	}

	public String getV0110() {
		return V0110;
	}

	public void setV0110(String v0110) {
		V0110 = v0110;
	}

	public String getV0111() {
		return V0111;
	}

	public void setV0111(String v0111) {
		V0111 = v0111;
	}

	public String getV0112() {
		return V0112;
	}

	public void setV0112(String v0112) {
		V0112 = v0112;
	}

	public String getV0113() {
		return V0113;
	}

	public void setV0113(String v0113) {
		V0113 = v0113;
	}

	public String getV0114() {
		return V0114;
	}

	public void setV0114(String v0114) {
		V0114 = v0114;
	}

	public String getV0115() {
		return V0115;
	}

	public void setV0115(String v0115) {
		V0115 = v0115;
	}

	public String getV0116() {
		return V0116;
	}

	public void setV0116(String v0116) {
		V0116 = v0116;
	}

	public String getV0117() {
		return V0117;
	}

	public void setV0117(String v0117) {
		V0117 = v0117;
	}

	public String getV0118() {
		return V0118;
	}

	public void setV0118(String v0118) {
		V0118 = v0118;
	}

	public String getV0119() {
		return V0119;
	}

	public void setV0119(String v0119) {
		V0119 = v0119;
	}

	public String getV0120() {
		return V0120;
	}

	public void setV0120(String v0120) {
		V0120 = v0120;
	}

	public String getV0121() {
		return V0121;
	}

	public void setV0121(String v0121) {
		V0121 = v0121;
	}

	public String getV0122() {
		return V0122;
	}

	public void setV0122(String v0122) {
		V0122 = v0122;
	}

	public String getV0123() {
		return V0123;
	}

	public void setV0123(String v0123) {
		V0123 = v0123;
	}

	public String getV0124() {
		return V0124;
	}

	public void setV0124(String v0124) {
		V0124 = v0124;
	}

	public String getV0125() {
		return V0125;
	}

	public void setV0125(String v0125) {
		V0125 = v0125;
	}

	public String getV0126() {
		return V0126;
	}

	public void setV0126(String v0126) {
		V0126 = v0126;
	}
	

	public String getV0127() {
		return V0127;
	}

	public void setV0127(String v0127) {
		V0127 = v0127;
	}

	public String getV0128() {
		return V0128;
	}

	public void setV0128(String v0128) {
		V0128 = v0128;
	}

	public String getV0129() {
		return V0129;
	}

	public void setV0129(String v0129) {
		V0129 = v0129;
	}

	public String getV0130() {
		return V0130;
	}

	public void setV0130(String v0130) {
		V0130 = v0130;
	}

	public String getV0131() {
		return V0131;
	}

	public void setV0131(String v0131) {
		V0131 = v0131;
	}

	public String getV0132() {
		return V0132;
	}

	public void setV0132(String v0132) {
		V0132 = v0132;
	}

	
	public String getV0133() {
		return V0133;
	}

	public void setV0133(String v0133) {
		V0133 = v0133;
	}

	public String getV0134() {
		return V0134;
	}

	public void setV0134(String v0134) {
		V0134 = v0134;
	}
	
	

	public String getV0135() {
		return V0135;
	}

	public void setV0135(String v0135) {
		V0135 = v0135;
	}

	public String getCmkType() {
		return CmkType;
	}

	public void setCmkType(String cmkType) {
		CmkType = cmkType;
	}
	

    public String getDividend() {
		return Dividend;
	}

	public void setDividend(String dividend) {
		Dividend = dividend;
	}

	public String getReturnRateSign2() {
		return ReturnRateSign2;
	}

	public void setReturnRateSign2(String returnRateSign2) {
		ReturnRateSign2 = returnRateSign2;
	}

	public String getReturnRate2() {
		return ReturnRate2;
	}

	public void setReturnRate2(String returnRate2) {
		ReturnRate2 = returnRate2;
	}

	public String toString() {
        return "NFVIPAOutputVODetails{" +
                "ProductType=" + ProductType +
                ", TrxMarket='" + TrxMarket + '\'' +
                ", InsuranceNo='" + InsuranceNo + '\'' +
                ", CurCode='" + CurCode + '\'' +
                ", Number='" + Number + '\'' +
                ", State='" + State + '\'' +
                ", EntrustCur='" + EntrustCur + '\'' +
                ", AvgBuyingPrice='" + AvgBuyingPrice + '\'' +
                ", CurAmt='" + CurAmt + '\'' +
                ", ForCurBal='" + ForCurBal + '\'' +
                ", AcctBal='" + AcctBal + '\'' +
                ", Date08='" + Date08 + '\'' +
                ", ReferenceRate='" + ReferenceRate + '\'' +
                ", TrustBusinessType='" + TrustBusinessType + '\'' +
                ", TrustAcct='" + TrustAcct + '\'' +
                ", ReturnRateSign='" + ReturnRateSign + '\'' +
                ", ReturnRate='" + ReturnRate + '\'' +
                ", ProductType2='" + ProductType2 + '\'' +
                ", ProductName='" + ProductName + '\'' +
                ", ForCurCost='" + ForCurCost + '\'' +
                ", AcctBalCost='" + AcctBalCost + '\'' +
                ", Result='" + Result + '\'' +
                ", EntrustDate='" + EntrustDate + '\'' +
                ", TradeDate='" + TradeDate + '\'' +
                ", EntrustAmt='" + EntrustAmt + '\'' +
                ", EntrustPrice='" + EntrustPrice + '\'' +
                ", ChannelType='" + ChannelType + '\'' +
                ", TrustType='" + TrustType + '\'' +
                ", TradeAmt='" + TradeAmt + '\'' +
                ", TradePrice='" + TradePrice + '\'' +
                ", TradeCost='" + TradeCost + '\'' +
                ", OrderType='" + OrderType + '\'' +
                ", CmkType='" + CmkType + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
