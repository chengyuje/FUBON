package com.systex.jbranch.app.server.fps.sot312;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot707.BondGTCDataDetailVO;
import com.systex.jbranch.app.server.fps.sot707.BondGTCDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT312OutputVO extends PagingOutputVO {
	
	private List<BondGTCDataVO> gtcList;
	private List<BondGTCDataDetailVO> gtcDetailList;

	public List<BondGTCDataVO> getGtcList() {
		return gtcList;
	}

	public void setGtcList(List<BondGTCDataVO> gtcList) {
		this.gtcList = gtcList;
	}

	public List<BondGTCDataDetailVO> getGtcDetailList() {
		return gtcDetailList;
	}

	public void setGtcDetailList(List<BondGTCDataDetailVO> gtcDetailList) {
		this.gtcDetailList = gtcDetailList;
	}
	
}
