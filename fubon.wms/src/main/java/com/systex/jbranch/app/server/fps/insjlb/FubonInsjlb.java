package com.systex.jbranch.app.server.fps.insjlb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.insjlb.service.CalFamilyGapServiceInf;
import com.systex.jbranch.app.server.fps.insjlb.service.GetInsPremServiceInf;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@Service("insjlb")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FubonInsjlb extends INSJLB implements FubonInsjlbBusinessInf{
	@Autowired @Qualifier("GetInsPremService")
	private	GetInsPremServiceInf getInsPremService;

	@Autowired @Qualifier("CalFamilyGapService")
	private	CalFamilyGapServiceInf calFamilyGapService;
	
	@Override
	/**計算家庭財務安全缺口*/
	public CalFamilyGapOutputVO calFamilyGap(CalFamilyGapInputVO inputVo) throws JBranchException {
		return calFamilyGapService.doCalFamilyGap(inputVo);
	}
	
	/**試算保費(單一險種)（行外保單輸入時使用 & 規劃試算保費）*/
	@Override
	public GetInsPremOutputVO getInsPrem(GetInsPremInputVO inputVo)throws JBranchException{
		return getGetInsPremService().getInsPrem(inputVo);
	}

	public GetInsPremServiceInf getGetInsPremService() {
		return getInsPremService;
	}

	public void setGetInsPremService(GetInsPremServiceInf getInsPremService) {
		this.getInsPremService = getInsPremService;
	}

	public CalFamilyGapServiceInf getCalFamilyGapService() {
		return calFamilyGapService;
	}

	public void setCalFamilyGapService(CalFamilyGapServiceInf calFamilyGapService) {
		this.calFamilyGapService = calFamilyGapService;
	}
}
