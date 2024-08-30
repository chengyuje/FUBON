package com.systex.jbranch.fubon.commons.esb.vo.fc032275;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/09/09.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032275OutputVO {
	@XmlElement
	private String IDF_Y;	//功能
	@XmlElement
	private String IDX_1;	//FKey
	@XmlElement
	private String CLASS;	//統一編號
	@XmlElement
	private String IDX_6;	//FKey
	@XmlElement
	private String IDX_7;	//持美國護照開戶
	@XmlElement
	private String SEH_DATE_C;	//FKey
	@XmlElement
	private String IDX_8;	//簽署日
	@XmlElement
	private String IDX_2;	//FKey
	@XmlElement
	private String IDX_3;	//美國公民或居留權
	@XmlElement
	private String IDX_4;	//FKey
	@XmlElement
	private String IDX_5;	//美國出生地
	@XmlElement
	private String SEH_DATE_E;	//FKey
	@XmlElement
	private String IDF_P;	//美國居住及郵寄地址
	@XmlElement
	private String TIN;	//FKey
	@XmlElement
	private String SHE_DATE_P;	//美國電話（僅有美國電話）
	@XmlElement
	private String BRH_COD;	//FKey
	@XmlElement
	private String IDF_S;	//美國電話（有非美國電話）
	@XmlElement
	private String IDF_N;	//FKey
	@XmlElement(name="TxRepeat")
	private List<FC032275OutputDetailsVO> details;

	public String getIDF_Y() {
		return IDF_Y;
	}

	public String setIDF_Y( String IDF_Y) {
		 return this.IDF_Y = IDF_Y;
	}

	public String getIDX_1() {
		return IDX_1;
	}

	public String setIDX_1( String IDX_1) {
		 return this.IDX_1 = IDX_1;
	}

	public String getCLASS() {
		return CLASS;
	}

	public String setCLASS( String CLASS) {
		 return this.CLASS = CLASS;
	}

	public String getIDX_6() {
		return IDX_6;
	}

	public String setIDX_6( String IDX_6) {
		 return this.IDX_6 = IDX_6;
	}

	public String getIDX_7() {
		return IDX_7;
	}

	public String setIDX_7( String IDX_7) {
		 return this.IDX_7 = IDX_7;
	}

	public String getSEH_DATE_C() {
		return SEH_DATE_C;
	}

	public String setSEH_DATE_C( String SEH_DATE_C) {
		 return this.SEH_DATE_C = SEH_DATE_C;
	}

	public String getIDX_8() {
		return IDX_8;
	}

	public String setIDX_8( String IDX_8) {
		 return this.IDX_8 = IDX_8;
	}

	public String getIDX_2() {
		return IDX_2;
	}

	public String setIDX_2( String IDX_2) {
		 return this.IDX_2 = IDX_2;
	}

	public String getIDX_3() {
		return IDX_3;
	}

	public String setIDX_3( String IDX_3) {
		 return this.IDX_3 = IDX_3;
	}

	public String getIDX_4() {
		return IDX_4;
	}

	public String setIDX_4( String IDX_4) {
		 return this.IDX_4 = IDX_4;
	}

	public String getIDX_5() {
		return IDX_5;
	}

	public String setIDX_5( String IDX_5) {
		 return this.IDX_5 = IDX_5;
	}

	public String getSEH_DATE_E() {
		return SEH_DATE_E;
	}

	public String setSEH_DATE_E( String SEH_DATE_E) {
		 return this.SEH_DATE_E = SEH_DATE_E;
	}

	public String getIDF_P() {
		return IDF_P;
	}

	public String setIDF_P( String IDF_P) {
		 return this.IDF_P = IDF_P;
	}

	public String getTIN() {
		return TIN;
	}

	public String setTIN( String TIN) {
		 return this.TIN = TIN;
	}

	public String getSHE_DATE_P() {
		return SHE_DATE_P;
	}

	public String setSHE_DATE_P( String SHE_DATE_P) {
		 return this.SHE_DATE_P = SHE_DATE_P;
	}

	public String getBRH_COD() {
		return BRH_COD;
	}

	public String setBRH_COD( String BRH_COD) {
		 return this.BRH_COD = BRH_COD;
	}

	public String getIDF_S() {
		return IDF_S;
	}

	public String setIDF_S( String IDF_S) {
		 return this.IDF_S = IDF_S;
	}

	public String getIDF_N() {
		return IDF_N;
	}

	public String setIDF_N( String IDF_N) {
		 return this.IDF_N = IDF_N;
	}

	public List<FC032275OutputDetailsVO> getDetails() {
		return details;
	}

	public List<FC032275OutputDetailsVO> setDetails( List<FC032275OutputDetailsVO> details) {
		 return this.details = details;
	}
}