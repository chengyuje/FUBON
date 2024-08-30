package com.systex.jbranch.fubon.commons.esb.vo.nmp8yb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by CathyTang on 2018/12/20.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMP8YBOutputVO {	
    @XmlElement(name="TxRepeat")
    private List<NMP8YBOutputVODetails> details;
    
	public List<NMP8YBOutputVODetails> getDetails() {
		return details;
	}
	public void setDetails(List<NMP8YBOutputVODetails> details) {
		this.details = details;
	}
}
