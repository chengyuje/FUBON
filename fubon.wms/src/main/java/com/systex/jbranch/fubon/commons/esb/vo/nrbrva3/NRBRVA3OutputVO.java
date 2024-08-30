package com.systex.jbranch.fubon.commons.esb.vo.nrbrva3;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NRBRVA3OutputVO {
	
	@XmlElement
	private String ERR_COD;	//錯誤碼
	
	@XmlElement
	private String ERR_TXT;	//錯誤訊息
	
	@XmlElement
	private String CUST_ID;	//客戶ID
	
	@XmlElement(name="TxRepeat")
	private List<NRBRVA3OutputDetailsVO> details;

	public String getERR_COD() {
		return ERR_COD;
	}

	public void setERR_COD(String eRR_COD) {
		ERR_COD = eRR_COD;
	}

	public String getERR_TXT() {
		return ERR_TXT;
	}

	public void setERR_TXT(String eRR_TXT) {
		ERR_TXT = eRR_TXT;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public List<NRBRVA3OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NRBRVA3OutputDetailsVO> details) {
		this.details = details;
	}

}