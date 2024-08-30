package com.systex.jbranch.fubon.commons.esb.vo.eb032168;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/***
 * 洗錢防制電文
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB032168InputVO {
	private String FUNC; 		// 功能
	private String FUNC_01;		// 功能
	private String CUST_NO;		// 客戶統一編號
	private String FUNC_02; 	// 是否為全新客戶(含信用卡戶)
	private String APROV_DATE; 	// AML洗錢及資恐風險評估結果
	private String FUNC_04; 	// 基本資料有無異動
	private String CUST_NAME; 	// 客戶戶名
	private String ENG_NAME; 	// 英文姓名
	private String BDAY; 		// 出生日期
	private String CTRY_COD; 	// 國籍國家代號
	private String TYP_01; 		// 國籍、出生地、戶籍/通訊/註冊/營業地址是否在Sanction或高風險High國家
	private String TYP_02; 		// 國籍、出生地、戶籍/通訊/註冊/營業地址是否在中風險Medium國家
	private String FUNC_05; 	// 職業是否為最高風險職業(個人)
	private String FUNC_06; 	// 職業是否為高風險職業(個人)
	private String FUNC_07; 	// 主要行業是否為最高風險行業(法人)
	private String FUNC_08; 	// 股權是否具有複雜結構(法人)
	private String FUNC_09; 	// 是否為得發行無記名股票(法人)
	private String FUNC_10; 	// 主要行業是否為高風險行業(法人)
	private String FUNC_03; 	// 是否涉及稅務洗錢風險
	private String COD_01; 		// 是否為黑名單
	private String COD_02; 		// 是否為重要政治職務人士(PEP)或其家庭成員或其密切關係之人等
	private String COD_03; 		// 是否為警示戶或警檢調來函偵辦通報對象或曾被通報疑似可疑交易申報
	private String COD_04; 		// 是否有涉及洗錢、貪汙、違反稅務法令、規避納稅義務等相關之負面新聞
	private String ID_TYPE; 	// ID_TYPE （new add）

	public String getFUNC() {
		return FUNC;
	}

	public void setFUNC(String FUNC) {
		this.FUNC = FUNC;
	}

	public String getFUNC_01() {
		return FUNC_01;
	}

	public void setFUNC_01(String FUNC_01) {
		this.FUNC_01 = FUNC_01;
	}

	public String getCUST_NO() {
		return CUST_NO;
	}

	public void setCUST_NO(String CUST_NO) {
		this.CUST_NO = CUST_NO;
	}

	public String getFUNC_02() {
		return FUNC_02;
	}

	public void setFUNC_02(String FUNC_02) {
		this.FUNC_02 = FUNC_02;
	}

	public String getAPROV_DATE() {
		return APROV_DATE;
	}

	public void setAPROV_DATE(String APROV_DATE) {
		this.APROV_DATE = APROV_DATE;
	}

	public String getFUNC_04() {
		return FUNC_04;
	}

	public void setFUNC_04(String FUNC_04) {
		this.FUNC_04 = FUNC_04;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(String CUST_NAME) {
		this.CUST_NAME = CUST_NAME;
	}

	public String getENG_NAME() {
		return ENG_NAME;
	}

	public void setENG_NAME(String ENG_NAME) {
		this.ENG_NAME = ENG_NAME;
	}

	public String getBDAY() {
		return BDAY;
	}

	public void setBDAY(String BDAY) {
		this.BDAY = BDAY;
	}

	public String getCTRY_COD() {
		return CTRY_COD;
	}

	public void setCTRY_COD(String CTRY_COD) {
		this.CTRY_COD = CTRY_COD;
	}

	public String getTYP_01() {
		return TYP_01;
	}

	public void setTYP_01(String TYP_01) {
		this.TYP_01 = TYP_01;
	}

	public String getTYP_02() {
		return TYP_02;
	}

	public void setTYP_02(String TYP_02) {
		this.TYP_02 = TYP_02;
	}

	public String getFUNC_05() {
		return FUNC_05;
	}

	public void setFUNC_05(String FUNC_05) {
		this.FUNC_05 = FUNC_05;
	}

	public String getFUNC_06() {
		return FUNC_06;
	}

	public void setFUNC_06(String FUNC_06) {
		this.FUNC_06 = FUNC_06;
	}

	public String getFUNC_07() {
		return FUNC_07;
	}

	public void setFUNC_07(String FUNC_07) {
		this.FUNC_07 = FUNC_07;
	}

	public String getFUNC_08() {
		return FUNC_08;
	}

	public void setFUNC_08(String FUNC_08) {
		this.FUNC_08 = FUNC_08;
	}

	public String getFUNC_09() {
		return FUNC_09;
	}

	public void setFUNC_09(String FUNC_09) {
		this.FUNC_09 = FUNC_09;
	}

	public String getFUNC_10() {
		return FUNC_10;
	}

	public void setFUNC_10(String FUNC_10) {
		this.FUNC_10 = FUNC_10;
	}

	public String getFUNC_03() {
		return FUNC_03;
	}

	public void setFUNC_03(String FUNC_03) {
		this.FUNC_03 = FUNC_03;
	}

	public String getCOD_01() {
		return COD_01;
	}

	public void setCOD_01(String COD_01) {
		this.COD_01 = COD_01;
	}

	public String getCOD_02() {
		return COD_02;
	}

	public void setCOD_02(String COD_02) {
		this.COD_02 = COD_02;
	}

	public String getCOD_03() {
		return COD_03;
	}

	public void setCOD_03(String COD_03) {
		this.COD_03 = COD_03;
	}

	public String getCOD_04() {
		return COD_04;
	}

	public void setCOD_04(String COD_04) {
		this.COD_04 = COD_04;
	}

	public String getID_TYPE() {
		return ID_TYPE;
	}

	public void setID_TYPE(String ID_TYPE) {
		this.ID_TYPE = ID_TYPE;
	}
}