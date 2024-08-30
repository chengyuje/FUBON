package com.systex.jbranch.fubon.commons.esb.vo.fc032675;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/09/09.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032675InputVO {
	@XmlElement
	private String CUSID;	//功能類別
	@XmlElement
	private String FKey3;	//FKey
	@XmlElement
	private String BUSADDR1;	//客戶統編
	@XmlElement
	private String FKey4;	//FKey
	@XmlElement
	private String FKey1;	//功能說明
	@XmlElement
	private String FKey2;	//FKey
	@XmlElement
	private String IDU_COD;	//FKey
	
	/**Mark : 20161212 新增FUNCTION BY Stella*/
	@XmlElement
	private String FUNCTION;	//FUNCTION
	
	
	public String getCUSID() {
		return CUSID;
	}

	public String setCUSID( String CUSID) {
		 return this.CUSID = CUSID;
	}

	public String getFKey3() {
		return FKey3;
	}

	public String setFKey3( String FKey3) {
		 return this.FKey3 = FKey3;
	}

	public String getBUSADDR1() {
		return BUSADDR1;
	}

	public String setBUSADDR1( String BUSADDR1) {
		 return this.BUSADDR1 = BUSADDR1;
	}

	public String getFKey4() {
		return FKey4;
	}

	public String setFKey4( String FKey4) {
		 return this.FKey4 = FKey4;
	}

	public String getFKey1() {
		return FKey1;
	}

	public String setFKey1( String FKey1) {
		 return this.FKey1 = FKey1;
	}

	public String getFKey2() {
		return FKey2;
	}

	public String setFKey2( String FKey2) {
		 return this.FKey2 = FKey2;
	}

	public String getIDU_COD() {
		return IDU_COD;
	}

	public String setIDU_COD( String IDU_COD) {
		 return this.IDU_COD = IDU_COD;
	}

	public String getFUNCTION() {
		return FUNCTION;
	}

	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	
	
}