package com.systex.jbranch.fubon.commons.esb.vo.nfvipa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by SebastianWu on 2016/9/23.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFVIPAOutputVO {
    @XmlElement
    private String ERR_COD; //錯誤碼
    @XmlElement
    private String ERR_TXT; //錯誤訊息
    @XmlElement
    private String FUNCTION;	//功能碼
    @XmlElement
    private String RANGE;		//範圍  0001:庫存
    @XmlElement
    private String OCCUR;		//資料筆數
	@XmlElement(name = "TxRepeat")
    private List<NFVIPAOutputVODetails> details;

    public String getERR_COD() {
        return ERR_COD;
    }

    public void setERR_COD(String ERR_COD) {
        this.ERR_COD = ERR_COD;
    }

    public String getERR_TXT() {
        return ERR_TXT;
    }

    public void setERR_TXT(String ERR_TXT) {
        this.ERR_TXT = ERR_TXT;
    }

    public String getFUNCTION() {
		return FUNCTION;
	}

	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}

	public String getRANGE() {
		return RANGE;
	}

	public void setRANGE(String rANGE) {
		RANGE = rANGE;
	}

	public List<NFVIPAOutputVODetails> getDetails() {
        return details;
    }

    public void setDetails(List<NFVIPAOutputVODetails> details) {
        this.details = details;
    }

	public String getOCCUR() {
		return OCCUR;
	}

	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}

	@Override
    public String toString() {
        return "NFVIPAOutputVO{" +
                "ERR_COD='" + ERR_COD + '\'' +
                ", ERR_TXT='" + ERR_TXT + '\'' +
                ", FUNCTION='" + FUNCTION + '\'' +
                ", RANGE='" + RANGE + '\'' +
                ", details=" + details +
                '}';
    }
}
