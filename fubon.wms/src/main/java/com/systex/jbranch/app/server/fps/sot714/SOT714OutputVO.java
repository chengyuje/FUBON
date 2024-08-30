package com.systex.jbranch.app.server.fps.sot714;

import java.util.List;

import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT714OutputVO extends PagingOutputVO {
	
	private CustHighNetWorthDataVO hnwcDataVO;
	private WMSHACRDataVO wmshacrDataVO;
	private WMSHAIADataVO wmshaiaDataVO;
	private List<CentInvDataVO> CentInvDataList;
	
	
	public CustHighNetWorthDataVO getHnwcDataVO() {
		return hnwcDataVO;
	}

	public void setHnwcDataVO(CustHighNetWorthDataVO hnwcDataVO) {
		this.hnwcDataVO = hnwcDataVO;
	}

	public WMSHACRDataVO getWmshacrDataVO() {
		return wmshacrDataVO;
	}

	public void setWmshacrDataVO(WMSHACRDataVO wmshacrDataVO) {
		this.wmshacrDataVO = wmshacrDataVO;
	}

	public WMSHAIADataVO getWmshaiaDataVO() {
		return wmshaiaDataVO;
	}

	public void setWmshaiaDataVO(WMSHAIADataVO wmshaiaDataVO) {
		this.wmshaiaDataVO = wmshaiaDataVO;
	}

	public List<CentInvDataVO> getCentInvDataList() {
		return CentInvDataList;
	}

	public void setCentInvDataList(List<CentInvDataVO> centInvDataList) {
		CentInvDataList = centInvDataList;
	}
	
	
}
