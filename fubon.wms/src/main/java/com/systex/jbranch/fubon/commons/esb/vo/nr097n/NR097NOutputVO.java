package com.systex.jbranch.fubon.commons.esb.vo.nr097n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by SebastianWu on 2016/11/18.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR097NOutputVO {
    @XmlElement
    private String SPRefId;
    @XmlElement
    private String AcctId16;
    @XmlElement
    private String Name;
    @XmlElement
    private String SEX;
    @XmlElement
    private String Occur;
    @XmlElement(name = "TxRepeat")
    private List<NR097NOutputVODetails> details;

    public String getSPRefId() {
        return SPRefId;
    }

    public void setSPRefId(String SPRefId) {
        this.SPRefId = SPRefId;
    }

    public String getAcctId16() {
        return AcctId16;
    }

    public void setAcctId16(String acctId16) {
        AcctId16 = acctId16;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSEX() {
        return SEX;
    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }

    public String getOccur() {
        return Occur;
    }

    public void setOccur(String occur) {
        Occur = occur;
    }

    public List<NR097NOutputVODetails> getDetails() {
        return details;
    }

    public void setDetails(List<NR097NOutputVODetails> details) {
        this.details = details;
    }
}
