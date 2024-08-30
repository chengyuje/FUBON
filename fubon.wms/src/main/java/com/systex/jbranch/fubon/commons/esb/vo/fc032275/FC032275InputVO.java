package com.systex.jbranch.fubon.commons.esb.vo.fc032275;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/09/09.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032275InputVO {
	@XmlElement
	private String ENQ_IND;	//功能
	@XmlElement
	private String APLY_DATE_12;	//FKey
	@XmlElement
	private String APLY_DATE_11;	//統一編號
	@XmlElement
	private String SEL_IND;	//FKey
	@XmlElement
	private String COD_01;	//持美國護照開戶
	@XmlElement
	private String SEP01;	//FKey
	@XmlElement
	private String ENDKey;	//簽署日
	@XmlElement
	private String ORG_TYPE;	//FKey
	@XmlElement
	private String RIST_RATE;	//美國公民或居留權
	@XmlElement
	private String APLY_DATE_13;	//FKey
	@XmlElement
	private String BUS_TYP;	//美國出生地
	@XmlElement(name="CUST_NO")
	private String CUST_NO;	//FKey
	@XmlElement
	private String SEP26;	//美國居住及郵寄地址
	@XmlElement
	private String SEP27;	//FKey
	@XmlElement
	private String SEP24;	//美國電話（僅有美國電話）
	@XmlElement
	private String SEP25;	//FKey
	@XmlElement
	private String TYP_03;	//美國電話（有非美國電話）
	@XmlElement
	private String TYP_02;	//FKey
	@XmlElement
	private String TYP_01;	//匯款至美國境內帳戶之常行指示
	@XmlElement
	private String TYP_07;	//FKey
	@XmlElement
	private String TYP_06;	//受託人具有美國地址
	@XmlElement
	private String PRT_IND;	//FKey
	@XmlElement
	private String TYP_05;	//只留有轉信或代收郵寄地址
	@XmlElement
	private String TYP_04;	//FKey
	@XmlElement
	private String TYP_08;	//目前FATCA身分
	@XmlElement
	private String RISK_RANK;	//FKey
	@XmlElement
	private String SEP22;	//美國稅籍編號
	@XmlElement
	private String SEP23;	//FKey
	@XmlElement
	private String SEP20;	//文件編號1
	@XmlElement
	private String SEP21;	//FKey
	@XmlElement
	private String SEP15;	//簽署(取得)日1
	@XmlElement
	private String SEP16;	//FKey
	@XmlElement
	private String SEP13;	//有效日期1
	@XmlElement
	private String SEP14;	//FKey
	@XmlElement
	private String SEP19;	//文件編號2
	@XmlElement
	private String SEP17;	//FKey
	@XmlElement
	private String SEP18;	//簽署(取得)日2
	@XmlElement
	private String APLY_DATE_01;	//FKey
	@XmlElement
	private String FUNC;	//有效日期2
	@XmlElement
	private String SEP11;	//FKey
	@XmlElement
	private String SEP12;	//文件編號3
	@XmlElement
	private String SEP10;	//FKey
	@XmlElement
	private String CNO4;	//簽署(取得)日3
	@XmlElement
	private String SEP04;	//FKey
	@XmlElement
	private String SEP05;	//有效日期3
	@XmlElement
	private String SEP02;	//FKey
	@XmlElement
	private String SEP03;	//FATCA身分辨識完成且證明文件已收齊
	@XmlElement
	private String APROV_DATE;	//FKey
	@XmlElement
	private String SEP08;	//是否同步修改去年身分
	@XmlElement
	private String SEP09;	//FKey
	@XmlElement
	private String LOST_FLG;	//負責辨識版塊
	@XmlElement
	private String SEP06;	//FKey
	@XmlElement
	private String SEP07;	//負責辨識分行
	@XmlElement
	private String APLY_DATE_03;	//FKey
	@XmlElement
	private String APLY_DATE_02;	//FKey

	public String getENQ_IND() {
		return ENQ_IND;
	}

	public String setENQ_IND( String ENQ_IND) {
		 return this.ENQ_IND = ENQ_IND;
	}

	public String getAPLY_DATE_12() {
		return APLY_DATE_12;
	}

	public String setAPLY_DATE_12( String APLY_DATE_12) {
		 return this.APLY_DATE_12 = APLY_DATE_12;
	}

	public String getAPLY_DATE_11() {
		return APLY_DATE_11;
	}

	public String setAPLY_DATE_11( String APLY_DATE_11) {
		 return this.APLY_DATE_11 = APLY_DATE_11;
	}

	public String getSEL_IND() {
		return SEL_IND;
	}

	public String setSEL_IND( String SEL_IND) {
		 return this.SEL_IND = SEL_IND;
	}

	public String getCOD_01() {
		return COD_01;
	}

	public String setCOD_01( String COD_01) {
		 return this.COD_01 = COD_01;
	}

	public String getSEP01() {
		return SEP01;
	}

	public String setSEP01( String SEP01) {
		 return this.SEP01 = SEP01;
	}

	public String getENDKey() {
		return ENDKey;
	}

	public String setENDKey( String ENDKey) {
		 return this.ENDKey = ENDKey;
	}

	public String getORG_TYPE() {
		return ORG_TYPE;
	}

	public String setORG_TYPE( String ORG_TYPE) {
		 return this.ORG_TYPE = ORG_TYPE;
	}

	public String getRIST_RATE() {
		return RIST_RATE;
	}

	public String setRIST_RATE( String RIST_RATE) {
		 return this.RIST_RATE = RIST_RATE;
	}

	public String getAPLY_DATE_13() {
		return APLY_DATE_13;
	}

	public String setAPLY_DATE_13( String APLY_DATE_13) {
		 return this.APLY_DATE_13 = APLY_DATE_13;
	}

	public String getBUS_TYP() {
		return BUS_TYP;
	}

	public String setBUS_TYP( String BUS_TYP) {
		 return this.BUS_TYP = BUS_TYP;
	}

	public String getCUST_NO() {
		return CUST_NO;
	}

	public String setCUST_NO( String CUST_NO) {
		 return this.CUST_NO = CUST_NO;
	}

	public String getSEP26() {
		return SEP26;
	}

	public String setSEP26( String SEP26) {
		 return this.SEP26 = SEP26;
	}

	public String getSEP27() {
		return SEP27;
	}

	public String setSEP27( String SEP27) {
		 return this.SEP27 = SEP27;
	}

	public String getSEP24() {
		return SEP24;
	}

	public String setSEP24( String SEP24) {
		 return this.SEP24 = SEP24;
	}

	public String getSEP25() {
		return SEP25;
	}

	public String setSEP25( String SEP25) {
		 return this.SEP25 = SEP25;
	}

	public String getTYP_03() {
		return TYP_03;
	}

	public String setTYP_03( String TYP_03) {
		 return this.TYP_03 = TYP_03;
	}

	public String getTYP_02() {
		return TYP_02;
	}

	public String setTYP_02( String TYP_02) {
		 return this.TYP_02 = TYP_02;
	}

	public String getTYP_01() {
		return TYP_01;
	}

	public String setTYP_01( String TYP_01) {
		 return this.TYP_01 = TYP_01;
	}

	public String getTYP_07() {
		return TYP_07;
	}

	public String setTYP_07( String TYP_07) {
		 return this.TYP_07 = TYP_07;
	}

	public String getTYP_06() {
		return TYP_06;
	}

	public String setTYP_06( String TYP_06) {
		 return this.TYP_06 = TYP_06;
	}

	public String getPRT_IND() {
		return PRT_IND;
	}

	public String setPRT_IND( String PRT_IND) {
		 return this.PRT_IND = PRT_IND;
	}

	public String getTYP_05() {
		return TYP_05;
	}

	public String setTYP_05( String TYP_05) {
		 return this.TYP_05 = TYP_05;
	}

	public String getTYP_04() {
		return TYP_04;
	}

	public String setTYP_04( String TYP_04) {
		 return this.TYP_04 = TYP_04;
	}

	public String getTYP_08() {
		return TYP_08;
	}

	public String setTYP_08( String TYP_08) {
		 return this.TYP_08 = TYP_08;
	}

	public String getRISK_RANK() {
		return RISK_RANK;
	}

	public String setRISK_RANK( String RISK_RANK) {
		 return this.RISK_RANK = RISK_RANK;
	}

	public String getSEP22() {
		return SEP22;
	}

	public String setSEP22( String SEP22) {
		 return this.SEP22 = SEP22;
	}

	public String getSEP23() {
		return SEP23;
	}

	public String setSEP23( String SEP23) {
		 return this.SEP23 = SEP23;
	}

	public String getSEP20() {
		return SEP20;
	}

	public String setSEP20( String SEP20) {
		 return this.SEP20 = SEP20;
	}

	public String getSEP21() {
		return SEP21;
	}

	public String setSEP21( String SEP21) {
		 return this.SEP21 = SEP21;
	}

	public String getSEP15() {
		return SEP15;
	}

	public String setSEP15( String SEP15) {
		 return this.SEP15 = SEP15;
	}

	public String getSEP16() {
		return SEP16;
	}

	public String setSEP16( String SEP16) {
		 return this.SEP16 = SEP16;
	}

	public String getSEP13() {
		return SEP13;
	}

	public String setSEP13( String SEP13) {
		 return this.SEP13 = SEP13;
	}

	public String getSEP14() {
		return SEP14;
	}

	public String setSEP14( String SEP14) {
		 return this.SEP14 = SEP14;
	}

	public String getSEP19() {
		return SEP19;
	}

	public String setSEP19( String SEP19) {
		 return this.SEP19 = SEP19;
	}

	public String getSEP17() {
		return SEP17;
	}

	public String setSEP17( String SEP17) {
		 return this.SEP17 = SEP17;
	}

	public String getSEP18() {
		return SEP18;
	}

	public String setSEP18( String SEP18) {
		 return this.SEP18 = SEP18;
	}

	public String getAPLY_DATE_01() {
		return APLY_DATE_01;
	}

	public String setAPLY_DATE_01( String APLY_DATE_01) {
		 return this.APLY_DATE_01 = APLY_DATE_01;
	}

	public String getFUNC() {
		return FUNC;
	}

	public String setFUNC( String FUNC) {
		 return this.FUNC = FUNC;
	}

	public String getSEP11() {
		return SEP11;
	}

	public String setSEP11( String SEP11) {
		 return this.SEP11 = SEP11;
	}

	public String getSEP12() {
		return SEP12;
	}

	public String setSEP12( String SEP12) {
		 return this.SEP12 = SEP12;
	}

	public String getSEP10() {
		return SEP10;
	}

	public String setSEP10( String SEP10) {
		 return this.SEP10 = SEP10;
	}

	public String getCNO4() {
		return CNO4;
	}

	public String setCNO4( String CNO4) {
		 return this.CNO4 = CNO4;
	}

	public String getSEP04() {
		return SEP04;
	}

	public String setSEP04( String SEP04) {
		 return this.SEP04 = SEP04;
	}

	public String getSEP05() {
		return SEP05;
	}

	public String setSEP05( String SEP05) {
		 return this.SEP05 = SEP05;
	}

	public String getSEP02() {
		return SEP02;
	}

	public String setSEP02( String SEP02) {
		 return this.SEP02 = SEP02;
	}

	public String getSEP03() {
		return SEP03;
	}

	public String setSEP03( String SEP03) {
		 return this.SEP03 = SEP03;
	}

	public String getAPROV_DATE() {
		return APROV_DATE;
	}

	public String setAPROV_DATE( String APROV_DATE) {
		 return this.APROV_DATE = APROV_DATE;
	}

	public String getSEP08() {
		return SEP08;
	}

	public String setSEP08( String SEP08) {
		 return this.SEP08 = SEP08;
	}

	public String getSEP09() {
		return SEP09;
	}

	public String setSEP09( String SEP09) {
		 return this.SEP09 = SEP09;
	}

	public String getLOST_FLG() {
		return LOST_FLG;
	}

	public String setLOST_FLG( String LOST_FLG) {
		 return this.LOST_FLG = LOST_FLG;
	}

	public String getSEP06() {
		return SEP06;
	}

	public String setSEP06( String SEP06) {
		 return this.SEP06 = SEP06;
	}

	public String getSEP07() {
		return SEP07;
	}

	public String setSEP07( String SEP07) {
		 return this.SEP07 = SEP07;
	}

	public String getAPLY_DATE_03() {
		return APLY_DATE_03;
	}

	public String setAPLY_DATE_03( String APLY_DATE_03) {
		 return this.APLY_DATE_03 = APLY_DATE_03;
	}

	public String getAPLY_DATE_02() {
		return APLY_DATE_02;
	}

	public String setAPLY_DATE_02( String APLY_DATE_02) {
		 return this.APLY_DATE_02 = APLY_DATE_02;
	}
}