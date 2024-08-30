package com.systex.jbranch.fubon.commons.esb.vo.sc120100;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;

/**
 * Created by Ocean on 2016/09/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SC120100OutputVO {

	// details
	@XmlElement(name = "TxRepeat")
	private List<SC120100DetailOutputVO> details;

	public List<SC120100DetailOutputVO> getDetails() {
		return details;
	}

	public void setDetails(List<SC120100DetailOutputVO> details) {
		this.details = details;
	}

}