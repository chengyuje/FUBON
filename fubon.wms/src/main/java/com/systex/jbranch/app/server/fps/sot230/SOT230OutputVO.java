package com.systex.jbranch.app.server.fps.sot230;

import java.util.List;

import com.systex.jbranch.app.server.fps.sot705.CustFillETFVO;
import com.systex.jbranch.app.server.fps.sot705.CustOrderETFVO;

public class SOT230OutputVO {
private List<CustOrderETFVO> custOrderETFVOs;
private List<CustFillETFVO> custFillETFVOs;
public List<CustOrderETFVO> getCustOrderETFVOs() {
	return custOrderETFVOs;
}

public void setCustOrderETFVOs(List<CustOrderETFVO> custOrderETFVOs) {
	this.custOrderETFVOs = custOrderETFVOs;
}

public List<CustFillETFVO> getCustFillETFVOs() {
	return custFillETFVOs;
}

public void setCustFillETFVOs(List<CustFillETFVO> custFillETFVOs) {
	this.custFillETFVOs = custFillETFVOs;
}


}
