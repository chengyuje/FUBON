package com.systex.jbranch.fubon.commons.esb.vo.bkdcd003;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/06.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class BKDCD003OutputVO {
	@XmlElement(name = "TxRepeat")
	private List<BKDCD003OutputDetailVO> details;

    public List<BKDCD003OutputDetailVO> getDetails() {
        return details;
    }

    public void setDetails(List<BKDCD003OutputDetailVO> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "BKDCD003OutputVO{" +
                ", details=" + details +
                '}';
    }
}