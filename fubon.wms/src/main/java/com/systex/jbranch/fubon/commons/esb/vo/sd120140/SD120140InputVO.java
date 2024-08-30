package com.systex.jbranch.fubon.commons.esb.vo.sd120140;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SD120140InputVO {
	@XmlElement
	private String FUNC_COD;	//功能
	@XmlElement
	private String IDNO;	//統編
	@XmlElement
	private String NAME_COD;	//戶名代碼
	@XmlElement
	private String USER_ID;	//使用者代碼
	@XmlElement
	private String FILLER;	//空白
	
	
	public String getFUNC_COD() {
		return FUNC_COD;
	}
	public void setFUNC_COD(String fUNC_COD) {
		FUNC_COD = fUNC_COD;
	}
	public String getIDNO() {
		return IDNO;
	}
	public void setIDNO(String iDNO) {
		IDNO = iDNO;
	}
	public String getNAME_COD() {
		return NAME_COD;
	}
	public void setNAME_COD(String nAME_COD) {
		NAME_COD = nAME_COD;
	}
	public String getUSER_ID() {
		return USER_ID;
	}
	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}
	public String getFILLER() {
		return FILLER;
	}
	public void setFILLER(String fILLER) {
		FILLER = fILLER;
	}
	
}