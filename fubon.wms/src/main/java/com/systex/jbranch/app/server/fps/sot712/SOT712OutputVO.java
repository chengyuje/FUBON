package com.systex.jbranch.app.server.fps.sot712;

import java.util.Date;

import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;


public class SOT712OutputVO extends PagingOutputVO{
	
	private String EMP_NAME;
	private boolean Recseq;
	private Date TradeDate;
	private String tradeSEQ;
	private String KYCResult;
	private WMSHACRDataVO hmshacrDataVO;
	
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	
	public boolean isRecseq() {
		return Recseq;
	}
	
	public void setRecseq(boolean recseq) {
		Recseq = recseq;
	}
	
	public java.util.Date getTradeDate() {
		return TradeDate;
	}
	
	public void setTradeDate(java.util.Date tradeDate) {
		TradeDate = tradeDate;
	}

	public String getTradeSEQ() {
		return tradeSEQ;
	}

	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
	}
	public String getKYCResult() {
		return KYCResult;
	}

	public void setKYCResult(String KYCResult) {
		this.KYCResult = KYCResult;
	}

	public WMSHACRDataVO getHmshacrDataVO() {
		return hmshacrDataVO;
	}

	public void setHmshacrDataVO(WMSHACRDataVO hmshacrDataVO) {
		this.hmshacrDataVO = hmshacrDataVO;
	}
	
}
