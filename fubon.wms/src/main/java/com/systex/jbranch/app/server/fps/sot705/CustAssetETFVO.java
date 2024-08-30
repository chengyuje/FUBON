package com.systex.jbranch.app.server.fps.sot705;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/9/21.
 */
public class CustAssetETFVO {
	private String  assetType;			//庫存類型
    private String  TrxMarket;          //交易市場
    private String  InsuranceNo;        //商品代號
    private String  CurCode;            //商品幣別
    private BigDecimal  Number;         //庫存股數
    private String  State;				//狀態
    private String  EntrustCur;         //交易幣別
    private BigDecimal  AvgBuyingPrice; //平均買進價格
    private BigDecimal  CurAmt;         //參考收盤價
    private BigDecimal  ForCurBal;      //外幣現值
    private BigDecimal  AcctBal;        //台幣現值
    private Date    Date08;             //收盤價日期
    private BigDecimal  ReferenceRate;  //參考匯率
    private String  TrustBusinessType;  //信託業務別 Y：外幣 N：台幣
    private String  TrustAcct;          //信託帳號
    private String  ReturnRateSign;     //報酬率正負
    private BigDecimal  ReturnRate;     //報酬率
    private String  ProductType2;       //新舊商品 Y：舊商品(債轉股) ' '：新商品
    private String  ProductType;        //產品類別
    private String  ProductName;        //商品名稱
    private BigDecimal  ForCurCost;     //外幣成本
    private BigDecimal  AcctBalCost;    //台幣成本
    private String Result;				//委託結果 0002: 1= 交易已成交未扣款    2= 交易已成交已扣款未入庫  3= 交易已取消(部分成交)未扣款  4= 交易已取消(部分成交)已扣款未入庫
    									//		0003: 1= 交易已成交未撥款    2= 交易已取消(部分成交)未撥款
    private Date EntrustDate;			//委託日期
    private Date TradeDate;				//市場交易日
    private BigDecimal EntrustAmt;		//委託股數
    private BigDecimal EntrustPrice;	//委託價格
    private String ChannelType;			//通路別 “ “  => 臨櫃    “W”  => 網銀  “P” =>CTI	“M” =>行銀
	private String TrustType;			//信託業務別 Y：外幣 N：台幣
	private BigDecimal TradeAmt;		//成交股數
	private BigDecimal TradePrice;		//成交報價
	private BigDecimal TradeCost;		//成交金額
	private String OrderType;			//委託方式  "0"：約定限價  "1"：市價單  "2 "：限價單 (LiMiT order) 簡稱 LMT
	private Date  TradeDateEnd; 		//截止市場交易日期
	
	/** 2017.11.21 add by Carley 新增賣出委託交易資訊下行 **/
	private BigDecimal Occur;				//資料筆數
	
	private BigDecimal StockUForCurBal;		//賣出在途使用金額
	private BigDecimal StockUBal;			//賣出在途使用金額-台幣
	private BigDecimal StockCForCurBal;		//股票/ETF圈存金額
	private BigDecimal StockCBal;			//股票/ETF圈存金額-台幣
	private BigDecimal TrmCurBal;			//股票/ETF委託原幣金額
	private BigDecimal TrmBal;				//股票/ETF委託台幣金額
	private BigDecimal StockTForCurBal;		//股票/ETF在途款原幣金額
	private BigDecimal StockTBal;			//股票/ETF在途款台幣金額
	
	private String IsPledged;			//質借圈存註記(cmkType=MK03 ==> Y else N) 	MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他

	/*
	 * 20230731_#1536_VOC議題 - 海外ETF股票顯示含息投資報酬率
	 */
	@XmlElement
	private BigDecimal Dividend; // 累積配息
	@XmlElement
	private String ReturnRateSign2; // 含息報酬率正負
	@XmlElement
	private BigDecimal ReturnRate2; // 含息報酬率 股票代號=LEH-T AND參考收盤價=0顯示--	總長度6位含2位小數
	@XmlElement
	private String onTradeMemo; // 賣出在途狀態

    public BigDecimal getOccur() {
		return Occur;
	}

	public void setOccur(BigDecimal occur) {
		Occur = occur;
	}

	public BigDecimal getStockUForCurBal() {
		return StockUForCurBal;
	}

	public void setStockUForCurBal(BigDecimal stockUForCurBal) {
		StockUForCurBal = stockUForCurBal;
	}

	public BigDecimal getStockUBal() {
		return StockUBal;
	}

	public void setStockUBal(BigDecimal stockUBal) {
		StockUBal = stockUBal;
	}

	public BigDecimal getStockCForCurBal() {
		return StockCForCurBal;
	}

	public void setStockCForCurBal(BigDecimal stockCForCurBal) {
		StockCForCurBal = stockCForCurBal;
	}

	public BigDecimal getStockCBal() {
		return StockCBal;
	}

	public void setStockCBal(BigDecimal stockCBal) {
		StockCBal = stockCBal;
	}

	public BigDecimal getTrmCurBal() {
		return TrmCurBal;
	}

	public void setTrmCurBal(BigDecimal trmCurBal) {
		TrmCurBal = trmCurBal;
	}

	public BigDecimal getTrmBal() {
		return TrmBal;
	}

	public void setTrmBal(BigDecimal trmBal) {
		TrmBal = trmBal;
	}

	public BigDecimal getStockTForCurBal() {
		return StockTForCurBal;
	}

	public void setStockTForCurBal(BigDecimal stockTForCurBal) {
		StockTForCurBal = stockTForCurBal;
	}

	public BigDecimal getStockTBal() {
		return StockTBal;
	}

	public void setStockTBal(BigDecimal stockTBal) {
		StockTBal = stockTBal;
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

    public BigDecimal getNumber() {
        return Number;
    }

    public void setNumber(BigDecimal number) {
        Number = number;
    }

    public String getEntrustCur() {
        return EntrustCur;
    }

    public void setEntrustCur(String entrustCur) {
        EntrustCur = entrustCur;
    }

    public BigDecimal getAvgBuyingPrice() {
        return AvgBuyingPrice;
    }

    public void setAvgBuyingPrice(BigDecimal avgBuyingPrice) {
        AvgBuyingPrice = avgBuyingPrice;
    }

    public BigDecimal getCurAmt() {
        return CurAmt;
    }

    public void setCurAmt(BigDecimal curAmt) {
        CurAmt = curAmt;
    }

    public BigDecimal getForCurBal() {
        return ForCurBal;
    }

    public void setForCurBal(BigDecimal forCurBal) {
        ForCurBal = forCurBal;
    }

    public BigDecimal getAcctBal() {
        return AcctBal;
    }

    public void setAcctBal(BigDecimal acctBal) {
        AcctBal = acctBal;
    }

    public Date getDate08() {
        return Date08;
    }

    public void setDate08(Date date08) {
        Date08 = date08;
    }

    public BigDecimal getReferenceRate() {
        return ReferenceRate;
    }

    public void setReferenceRate(BigDecimal referenceRate) {
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

    public BigDecimal getReturnRate() {
        return ReturnRate;
    }

    public void setReturnRate(BigDecimal returnRate) {
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

    public BigDecimal getForCurCost() {
        return ForCurCost;
    }

    public void setForCurCost(BigDecimal forCurCost) {
        ForCurCost = forCurCost;
    }

    public BigDecimal getAcctBalCost() {
        return AcctBalCost;
    }

    public void setAcctBalCost(BigDecimal acctBalCost) {
        AcctBalCost = acctBalCost;
    }

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public Date getEntrustDate() {
		return EntrustDate;
	}

	public void setEntrustDate(Date entrustDate) {
		EntrustDate = entrustDate;
	}

	public Date getTradeDate() {
		return TradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		TradeDate = tradeDate;
	}

	public BigDecimal getEntrustAmt() {
		return EntrustAmt;
	}

	public void setEntrustAmt(BigDecimal entrustAmt) {
		EntrustAmt = entrustAmt;
	}

	public BigDecimal getEntrustPrice() {
		return EntrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
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

	public BigDecimal getTradeAmt() {
		return TradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		TradeAmt = tradeAmt;
	}

	public BigDecimal getTradePrice() {
		return TradePrice;
	}

	public void setTradePrice(BigDecimal tradePrice) {
		TradePrice = tradePrice;
	}

	public BigDecimal getTradeCost() {
		return TradeCost;
	}

	public void setTradeCost(BigDecimal tradeCost) {
		TradeCost = tradeCost;
	}

	public String getOrderType() {
		return OrderType;
	}

	public void setOrderType(String orderType) {
		OrderType = orderType;
	}

	public String getProductType() {
		return ProductType;
	}

	public void setProductType(String productType) {
		ProductType = productType;
	}

	public Date getTradeDateEnd() {
		return TradeDateEnd;
	}

	public void setTradeDateEnd(Date tradeDateEnd) {
		TradeDateEnd = tradeDateEnd;
	}

	public String getIsPledged() {
		return IsPledged;
	}

	public void setIsPledged(String isPledged) {
		IsPledged = isPledged;
	}



	public BigDecimal getDividend() {
		return Dividend;
	}

	public void setDividend(BigDecimal dividend) {
		Dividend = dividend;
	}

	public String getReturnRateSign2() {
		return ReturnRateSign2;
	}

	public void setReturnRateSign2(String returnRateSign2) {
		ReturnRateSign2 = returnRateSign2;
	}

	public BigDecimal getReturnRate2() {
		return ReturnRate2;
	}

	public void setReturnRate2(BigDecimal returnRate2) {
		ReturnRate2 = returnRate2;
	}

	public String getOnTradeMemo() {
		return onTradeMemo;
	}

	public void setOnTradeMemo(String onTradeMemo) {
		this.onTradeMemo = onTradeMemo;
	}


	    
}
