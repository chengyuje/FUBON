package com.systex.jbranch.fubon.commons.esb.vo.cm061435cr;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created 2023/04/26
 * 高資產客戶註記
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CM061435CROutputVO {
	@XmlElement
	private String HNWC_FLG; //高資產客戶註記
	@XmlElement
	private String HNWC_DUE_DATE; //高資產客戶資格到期日
	@XmlElement
	private String HNWC_INVALID_DATE; //高資產客戶資格註銷日期
	@XmlElement
	private String HNWC_ID_TYP; //高資產客戶身份註記
	@XmlElement
	private String HNWC_SERV; //可提供高資產商品或服務
	@XmlElement
	private String INVEST_FLG; //客戶申購戶況
	@XmlElement
	private String INVEST_EXP; //是否具備結構複雜商品之投資經驗
	@XmlElement
	private String INVEST_DUE; //申請日距今是否滿兩週
	@XmlElement
	private String F2287_FLG; //授權辦理交易人員(032287)是否具備專業資格(Y/N)
	@XmlElement
	private String PROFESSIONAL_FLG; //是否具備專業知識註記(空白/ 1~5) (限專投申購之結構型商品註記)
	@XmlElement
	private String LEGAL_PROD1; //法人授權辦理交易人員1 商品
	@XmlElement
	private String LEGAL_ID1; //法人授權辦理交易人員1 ID
	@XmlElement
	private String LEGAL_PROD2; //法人授權辦理交易人員2 商品
	@XmlElement
	private String LEGAL_ID2; //法人授權辦理交易人員2 ID
	@XmlElement
	private String LEGAL_PROD3; //法人授權辦理交易人員3 商品
	@XmlElement
	private String LEGAL_ID3; //法人授權辦理交易人員3 ID
	@XmlElement
	private String LEGAL_PROD4; //法人授權辦理交易人員4 商品
	@XmlElement
	private String LEGAL_ID4; //法人授權辦理交易人員4 ID
	@XmlElement
	private String LEGAL_PROD5; //法人授權辦理交易人員5 商品
	@XmlElement
	private String LEGAL_ID5; //法人授權辦理交易人員5 ID
	@XmlElement
	private String LEGAL_PROD6; //法人授權辦理交易人員6 商品
	@XmlElement
	private String LEGAL_ID6; //法人授權辦理交易人員6 ID
	@XmlElement
	private String LEGAL_PROD7; //法人授權辦理交易人員7 商品
	@XmlElement
	private String LEGAL_ID7; //法人授權辦理交易人員7 ID
	@XmlElement
	private String LEGAL_PROD8; //法人授權辦理交易人員8 商品
	@XmlElement
	private String LEGAL_ID8; //法人授權辦理交易人員8 ID
	@XmlElement
	private String LEGAL_PROD9; //法人授權辦理交易人員9 商品
	@XmlElement
	private String LEGAL_ID9; //法人授權辦理交易人員9 ID
	@XmlElement
	private String LEGAL_PROD10; //法人授權辦理交易人員10 商品
	@XmlElement
	private String LEGAL_ID10; //法人授權辦理交易人員10 ID
	@XmlElement
	private String LEGAL_PROD11; //法人授權辦理交易人員11 商品
	@XmlElement
	private String LEGAL_ID11; //法人授權辦理交易人員11 ID
	@XmlElement
	private String LEGAL_PROD12; //法人授權辦理交易人員12 商品
	@XmlElement
	private String LEGAL_ID12; //法人授權辦理交易人員12 ID
	@XmlElement
	private String LEGAL_PROD13; //法人授權辦理交易人員13 商品
	@XmlElement
	private String LEGAL_ID13; //法人授權辦理交易人員13 ID
	@XmlElement
	private String LEGAL_PROD14; //法人授權辦理交易人員14 商品
	@XmlElement
	private String LEGAL_ID14; //法人授權辦理交易人員14 ID
	@XmlElement
	private String LEGAL_PROD15; //法人授權辦理交易人員15 商品
	@XmlElement
	private String LEGAL_ID15; //法人授權辦理交易人員15 ID
	@XmlElement
	private String LEGAL_PROD16; //法人授權辦理交易人員16 商品
	@XmlElement
	private String LEGAL_ID16; //法人授權辦理交易人員16 ID
	@XmlElement
	private String LEGAL_PROD17; //法人授權辦理交易人員17 商品
	@XmlElement
	private String LEGAL_ID17; //法人授權辦理交易人員17 ID
	@XmlElement
	private String LEGAL_PROD18; //法人授權辦理交易人員18 商品
	@XmlElement
	private String LEGAL_ID18; //法人授權辦理交易人員18 ID
	@XmlElement
	private String LEGAL_PROD19; //法人授權辦理交易人員19 商品
	@XmlElement
	private String LEGAL_ID19; //法人授權辦理交易人員19 ID
	@XmlElement
	private String LEGAL_PROD20; //法人授權辦理交易人員20 商品
	@XmlElement
	private String LEGAL_ID20; //法人授權辦理交易人員20 ID
	@XmlElement
	private String SP_FLG; //是否為高資產特定客戶註記 (1)年齡70歲(含)以上。(2)教育程度為國中(含)以下。(3)有全民健康保險重大傷病證明。
	
	public String getHNWC_FLG() {
		return HNWC_FLG;
	}
	public void setHNWC_FLG(String hNWC_FLG) {
		HNWC_FLG = hNWC_FLG;
	}
	public String getHNWC_DUE_DATE() {
		return HNWC_DUE_DATE;
	}
	public void setHNWC_DUE_DATE(String hNWC_DUE_DATE) {
		HNWC_DUE_DATE = hNWC_DUE_DATE;
	}
	public String getHNWC_INVALID_DATE() {
		return HNWC_INVALID_DATE;
	}
	public void setHNWC_INVALID_DATE(String hNWC_INVALID_DATE) {
		HNWC_INVALID_DATE = hNWC_INVALID_DATE;
	}
	public String getHNWC_ID_TYP() {
		return HNWC_ID_TYP;
	}
	public void setHNWC_ID_TYP(String hNWC_ID_TYP) {
		HNWC_ID_TYP = hNWC_ID_TYP;
	}
	public String getHNWC_SERV() {
		return HNWC_SERV;
	}
	public void setHNWC_SERV(String hNWC_SERV) {
		HNWC_SERV = hNWC_SERV;
	}
	public String getINVEST_FLG() {
		return INVEST_FLG;
	}
	public void setINVEST_FLG(String iNVEST_FLG) {
		INVEST_FLG = iNVEST_FLG;
	}
	public String getINVEST_EXP() {
		return INVEST_EXP;
	}
	public void setINVEST_EXP(String iNVEST_EXP) {
		INVEST_EXP = iNVEST_EXP;
	}
	public String getINVEST_DUE() {
		return INVEST_DUE;
	}
	public void setINVEST_DUE(String iNVEST_DUE) {
		INVEST_DUE = iNVEST_DUE;
	}
	public String getF2287_FLG() {
		return F2287_FLG;
	}
	public void setF2287_FLG(String f2287_FLG) {
		F2287_FLG = f2287_FLG;
	}
	public String getPROFESSIONAL_FLG() {
		return PROFESSIONAL_FLG;
	}
	public void setPROFESSIONAL_FLG(String pROFESSIONAL_FLG) {
		PROFESSIONAL_FLG = pROFESSIONAL_FLG;
	}
	public String getLEGAL_PROD1() {
		return LEGAL_PROD1;
	}
	public void setLEGAL_PROD1(String lEGAL_PROD1) {
		LEGAL_PROD1 = lEGAL_PROD1;
	}
	public String getLEGAL_ID1() {
		return LEGAL_ID1;
	}
	public void setLEGAL_ID1(String lEGAL_ID1) {
		LEGAL_ID1 = lEGAL_ID1;
	}
	public String getLEGAL_PROD2() {
		return LEGAL_PROD2;
	}
	public void setLEGAL_PROD2(String lEGAL_PROD2) {
		LEGAL_PROD2 = lEGAL_PROD2;
	}
	public String getLEGAL_ID2() {
		return LEGAL_ID2;
	}
	public void setLEGAL_ID2(String lEGAL_ID2) {
		LEGAL_ID2 = lEGAL_ID2;
	}
	public String getLEGAL_PROD3() {
		return LEGAL_PROD3;
	}
	public void setLEGAL_PROD3(String lEGAL_PROD3) {
		LEGAL_PROD3 = lEGAL_PROD3;
	}
	public String getLEGAL_ID3() {
		return LEGAL_ID3;
	}
	public void setLEGAL_ID3(String lEGAL_ID3) {
		LEGAL_ID3 = lEGAL_ID3;
	}
	public String getLEGAL_PROD4() {
		return LEGAL_PROD4;
	}
	public void setLEGAL_PROD4(String lEGAL_PROD4) {
		LEGAL_PROD4 = lEGAL_PROD4;
	}
	public String getLEGAL_ID4() {
		return LEGAL_ID4;
	}
	public void setLEGAL_ID4(String lEGAL_ID4) {
		LEGAL_ID4 = lEGAL_ID4;
	}
	public String getLEGAL_PROD5() {
		return LEGAL_PROD5;
	}
	public void setLEGAL_PROD5(String lEGAL_PROD5) {
		LEGAL_PROD5 = lEGAL_PROD5;
	}
	public String getLEGAL_ID5() {
		return LEGAL_ID5;
	}
	public void setLEGAL_ID5(String lEGAL_ID5) {
		LEGAL_ID5 = lEGAL_ID5;
	}
	public String getLEGAL_PROD6() {
		return LEGAL_PROD6;
	}
	public void setLEGAL_PROD6(String lEGAL_PROD6) {
		LEGAL_PROD6 = lEGAL_PROD6;
	}
	public String getLEGAL_ID6() {
		return LEGAL_ID6;
	}
	public void setLEGAL_ID6(String lEGAL_ID6) {
		LEGAL_ID6 = lEGAL_ID6;
	}
	public String getLEGAL_PROD7() {
		return LEGAL_PROD7;
	}
	public void setLEGAL_PROD7(String lEGAL_PROD7) {
		LEGAL_PROD7 = lEGAL_PROD7;
	}
	public String getLEGAL_ID7() {
		return LEGAL_ID7;
	}
	public void setLEGAL_ID7(String lEGAL_ID7) {
		LEGAL_ID7 = lEGAL_ID7;
	}
	public String getLEGAL_PROD8() {
		return LEGAL_PROD8;
	}
	public void setLEGAL_PROD8(String lEGAL_PROD8) {
		LEGAL_PROD8 = lEGAL_PROD8;
	}
	public String getLEGAL_ID8() {
		return LEGAL_ID8;
	}
	public void setLEGAL_ID8(String lEGAL_ID8) {
		LEGAL_ID8 = lEGAL_ID8;
	}
	public String getLEGAL_PROD9() {
		return LEGAL_PROD9;
	}
	public void setLEGAL_PROD9(String lEGAL_PROD9) {
		LEGAL_PROD9 = lEGAL_PROD9;
	}
	public String getLEGAL_ID9() {
		return LEGAL_ID9;
	}
	public void setLEGAL_ID9(String lEGAL_ID9) {
		LEGAL_ID9 = lEGAL_ID9;
	}
	public String getLEGAL_PROD10() {
		return LEGAL_PROD10;
	}
	public void setLEGAL_PROD10(String lEGAL_PROD10) {
		LEGAL_PROD10 = lEGAL_PROD10;
	}
	public String getLEGAL_ID10() {
		return LEGAL_ID10;
	}
	public void setLEGAL_ID10(String lEGAL_ID10) {
		LEGAL_ID10 = lEGAL_ID10;
	}
	public String getLEGAL_PROD11() {
		return LEGAL_PROD11;
	}
	public void setLEGAL_PROD11(String lEGAL_PROD11) {
		LEGAL_PROD11 = lEGAL_PROD11;
	}
	public String getLEGAL_ID11() {
		return LEGAL_ID11;
	}
	public void setLEGAL_ID11(String lEGAL_ID11) {
		LEGAL_ID11 = lEGAL_ID11;
	}
	public String getLEGAL_PROD12() {
		return LEGAL_PROD12;
	}
	public void setLEGAL_PROD12(String lEGAL_PROD12) {
		LEGAL_PROD12 = lEGAL_PROD12;
	}
	public String getLEGAL_ID12() {
		return LEGAL_ID12;
	}
	public void setLEGAL_ID12(String lEGAL_ID12) {
		LEGAL_ID12 = lEGAL_ID12;
	}
	public String getLEGAL_PROD13() {
		return LEGAL_PROD13;
	}
	public void setLEGAL_PROD13(String lEGAL_PROD13) {
		LEGAL_PROD13 = lEGAL_PROD13;
	}
	public String getLEGAL_ID13() {
		return LEGAL_ID13;
	}
	public void setLEGAL_ID13(String lEGAL_ID13) {
		LEGAL_ID13 = lEGAL_ID13;
	}
	public String getLEGAL_PROD14() {
		return LEGAL_PROD14;
	}
	public void setLEGAL_PROD14(String lEGAL_PROD14) {
		LEGAL_PROD14 = lEGAL_PROD14;
	}
	public String getLEGAL_ID14() {
		return LEGAL_ID14;
	}
	public void setLEGAL_ID14(String lEGAL_ID14) {
		LEGAL_ID14 = lEGAL_ID14;
	}
	public String getLEGAL_PROD15() {
		return LEGAL_PROD15;
	}
	public void setLEGAL_PROD15(String lEGAL_PROD15) {
		LEGAL_PROD15 = lEGAL_PROD15;
	}
	public String getLEGAL_ID15() {
		return LEGAL_ID15;
	}
	public void setLEGAL_ID15(String lEGAL_ID15) {
		LEGAL_ID15 = lEGAL_ID15;
	}
	public String getLEGAL_PROD16() {
		return LEGAL_PROD16;
	}
	public void setLEGAL_PROD16(String lEGAL_PROD16) {
		LEGAL_PROD16 = lEGAL_PROD16;
	}
	public String getLEGAL_ID16() {
		return LEGAL_ID16;
	}
	public void setLEGAL_ID16(String lEGAL_ID16) {
		LEGAL_ID16 = lEGAL_ID16;
	}
	public String getLEGAL_PROD17() {
		return LEGAL_PROD17;
	}
	public void setLEGAL_PROD17(String lEGAL_PROD17) {
		LEGAL_PROD17 = lEGAL_PROD17;
	}
	public String getLEGAL_ID17() {
		return LEGAL_ID17;
	}
	public void setLEGAL_ID17(String lEGAL_ID17) {
		LEGAL_ID17 = lEGAL_ID17;
	}
	public String getLEGAL_PROD18() {
		return LEGAL_PROD18;
	}
	public void setLEGAL_PROD18(String lEGAL_PROD18) {
		LEGAL_PROD18 = lEGAL_PROD18;
	}
	public String getLEGAL_ID18() {
		return LEGAL_ID18;
	}
	public void setLEGAL_ID18(String lEGAL_ID18) {
		LEGAL_ID18 = lEGAL_ID18;
	}
	public String getLEGAL_PROD19() {
		return LEGAL_PROD19;
	}
	public void setLEGAL_PROD19(String lEGAL_PROD19) {
		LEGAL_PROD19 = lEGAL_PROD19;
	}
	public String getLEGAL_ID19() {
		return LEGAL_ID19;
	}
	public void setLEGAL_ID19(String lEGAL_ID19) {
		LEGAL_ID19 = lEGAL_ID19;
	}
	public String getLEGAL_PROD20() {
		return LEGAL_PROD20;
	}
	public void setLEGAL_PROD20(String lEGAL_PROD20) {
		LEGAL_PROD20 = lEGAL_PROD20;
	}
	public String getLEGAL_ID20() {
		return LEGAL_ID20;
	}
	public void setLEGAL_ID20(String lEGAL_ID20) {
		LEGAL_ID20 = lEGAL_ID20;
	}
	public String getSP_FLG() {
		return SP_FLG;
	}
	public void setSP_FLG(String sP_FLG) {
		SP_FLG = sP_FLG;
	}
	
}