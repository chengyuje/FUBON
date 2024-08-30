package com.systex.jbranch.fubon.commons.esb.vo.eb12020002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/09.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB12020002InputVO {
	@XmlElement
	private String FUNC_COD;
	@XmlElement
	private String IDNO;
	@XmlElement
	private String NAME_COD;
	@XmlElement
	private String FUNC;
	@XmlElement
	private String IDTYPE;
	@XmlElement
	private String ACNO;
	@XmlElement
	private String FUNC_SW;
	
	
	
	
	
	
	public String getFUNC_SW() {
		return FUNC_SW;
	}
	public void setFUNC_SW(String fUNC_SW) {
		FUNC_SW = fUNC_SW;
	}
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getIDTYPE() {
		return IDTYPE;
	}
	public void setIDTYPE(String iDTYPE) {
		IDTYPE = iDTYPE;
	}
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
	public String getFUNC() {
		return FUNC;
	}
	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}
	

}