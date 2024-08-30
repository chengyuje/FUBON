package com.systex.jbranch.app.server.fps.sot660;

import java.math.BigDecimal;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT660InputVO extends PagingInputVO{
	
	private String custID;
	private WMSHAIADataVO wmshaiaData;
	private String kycLevel;
	private String custRemarks;
	private String prodRisk;
	
	private String CUR12;
	private String CUR13;
	private String CUR14;
	private String CUR15;
	private String CUR22;
	private String CUR23;
	private String CUR24;
	private String CUR25;
	private String CUR32;
	private String CUR33;
	private String CUR34;
	private String CUR35;
	private String CUR42;
	private String CUR43;
	private String CUR44;
	private String CUR45;
	private BigDecimal AMT12;
	private BigDecimal AMT13;
	private BigDecimal AMT14;
	private BigDecimal AMT15;
	private BigDecimal AMT22;
	private BigDecimal AMT23;
	private BigDecimal AMT24;
	private BigDecimal AMT25;
	private BigDecimal AMT32;
	private BigDecimal AMT33;
	private BigDecimal AMT34;
	private BigDecimal AMT35;
	private BigDecimal AMT42;
	private BigDecimal AMT43;
	private BigDecimal AMT44;
	private BigDecimal AMT45;
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public WMSHAIADataVO getWmshaiaData() {
		return wmshaiaData;
	}
	public void setWmshaiaData(WMSHAIADataVO wmshaiaData) {
		this.wmshaiaData = wmshaiaData;
	}
	public String getKycLevel() {
		return kycLevel;
	}
	public void setKycLevel(String kycLevel) {
		this.kycLevel = kycLevel;
	}
	public String getCustRemarks() {
		return custRemarks;
	}
	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}
	public String getProdRisk() {
		return prodRisk;
	}
	public void setProdRisk(String prodRisk) {
		this.prodRisk = prodRisk;
	}
	//	public FP032675DataVO getFp032675Data() {
//		return fp032675Data;
//	}
//	public void setFp032675Data(FP032675DataVO fp032675Data) {
//		this.fp032675Data = fp032675Data;
//	}
//	public CustKYCDataVO getCustKYCData() {
//		return custKYCData;
//	}
//	public void setCustKYCData(CustKYCDataVO custKYCData) {
//		this.custKYCData = custKYCData;
//	}
	public String getCUR12() {
		return CUR12;
	}
	public void setCUR12(String cUR12) {
		CUR12 = cUR12;
	}
	public String getCUR13() {
		return CUR13;
	}
	public void setCUR13(String cUR13) {
		CUR13 = cUR13;
	}
	public String getCUR14() {
		return CUR14;
	}
	public void setCUR14(String cUR14) {
		CUR14 = cUR14;
	}
	public String getCUR15() {
		return CUR15;
	}
	public void setCUR15(String cUR15) {
		CUR15 = cUR15;
	}
	public String getCUR22() {
		return CUR22;
	}
	public void setCUR22(String cUR22) {
		CUR22 = cUR22;
	}
	public String getCUR23() {
		return CUR23;
	}
	public void setCUR23(String cUR23) {
		CUR23 = cUR23;
	}
	public String getCUR24() {
		return CUR24;
	}
	public void setCUR24(String cUR24) {
		CUR24 = cUR24;
	}
	public String getCUR25() {
		return CUR25;
	}
	public void setCUR25(String cUR25) {
		CUR25 = cUR25;
	}
	public String getCUR32() {
		return CUR32;
	}
	public void setCUR32(String cUR32) {
		CUR32 = cUR32;
	}
	public String getCUR33() {
		return CUR33;
	}
	public void setCUR33(String cUR33) {
		CUR33 = cUR33;
	}
	public String getCUR34() {
		return CUR34;
	}
	public void setCUR34(String cUR34) {
		CUR34 = cUR34;
	}
	public String getCUR35() {
		return CUR35;
	}
	public void setCUR35(String cUR35) {
		CUR35 = cUR35;
	}
	public String getCUR42() {
		return CUR42;
	}
	public void setCUR42(String cUR42) {
		CUR42 = cUR42;
	}
	public String getCUR43() {
		return CUR43;
	}
	public void setCUR43(String cUR43) {
		CUR43 = cUR43;
	}
	public String getCUR44() {
		return CUR44;
	}
	public void setCUR44(String cUR44) {
		CUR44 = cUR44;
	}
	public String getCUR45() {
		return CUR45;
	}
	public void setCUR45(String cUR45) {
		CUR45 = cUR45;
	}
	public BigDecimal getAMT12() {
		return AMT12;
	}
	public void setAMT12(BigDecimal aMT12) {
		AMT12 = aMT12;
	}
	public BigDecimal getAMT13() {
		return AMT13;
	}
	public void setAMT13(BigDecimal aMT13) {
		AMT13 = aMT13;
	}
	public BigDecimal getAMT14() {
		return AMT14;
	}
	public void setAMT14(BigDecimal aMT14) {
		AMT14 = aMT14;
	}
	public BigDecimal getAMT15() {
		return AMT15;
	}
	public void setAMT15(BigDecimal aMT15) {
		AMT15 = aMT15;
	}
	public BigDecimal getAMT22() {
		return AMT22;
	}
	public void setAMT22(BigDecimal aMT22) {
		AMT22 = aMT22;
	}
	public BigDecimal getAMT23() {
		return AMT23;
	}
	public void setAMT23(BigDecimal aMT23) {
		AMT23 = aMT23;
	}
	public BigDecimal getAMT24() {
		return AMT24;
	}
	public void setAMT24(BigDecimal aMT24) {
		AMT24 = aMT24;
	}
	public BigDecimal getAMT25() {
		return AMT25;
	}
	public void setAMT25(BigDecimal aMT25) {
		AMT25 = aMT25;
	}
	public BigDecimal getAMT32() {
		return AMT32;
	}
	public void setAMT32(BigDecimal aMT32) {
		AMT32 = aMT32;
	}
	public BigDecimal getAMT33() {
		return AMT33;
	}
	public void setAMT33(BigDecimal aMT33) {
		AMT33 = aMT33;
	}
	public BigDecimal getAMT34() {
		return AMT34;
	}
	public void setAMT34(BigDecimal aMT34) {
		AMT34 = aMT34;
	}
	public BigDecimal getAMT35() {
		return AMT35;
	}
	public void setAMT35(BigDecimal aMT35) {
		AMT35 = aMT35;
	}
	public BigDecimal getAMT42() {
		return AMT42;
	}
	public void setAMT42(BigDecimal aMT42) {
		AMT42 = aMT42;
	}
	public BigDecimal getAMT43() {
		return AMT43;
	}
	public void setAMT43(BigDecimal aMT43) {
		AMT43 = aMT43;
	}
	public BigDecimal getAMT44() {
		return AMT44;
	}
	public void setAMT44(BigDecimal aMT44) {
		AMT44 = aMT44;
	}
	public BigDecimal getAMT45() {
		return AMT45;
	}
	public void setAMT45(BigDecimal aMT45) {
		AMT45 = aMT45;
	}
	
}
