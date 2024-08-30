package com.systex.jbranch.app.server.fps.insjlb.service;

import static com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf.INSCOTYPE_1;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCoInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCoOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdOutputVO;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**取回資訊源產品資訊*/
@Service("GetInsLocalPolicyService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GetInsLocalPolicyService extends GetCoverageService implements GetInsLocalPolicyServiceInf{
	/**取回保險公司的清單*/
	public GetInsCoOutputVO getInsCO(GetInsCoInputVO inputVO) throws JBranchException
	{
		// 檢核欄位
		GetInsCoOutputVO insCOOutputVO = new GetInsCoOutputVO();
		
		//沒type就全查
		if(StringUtils.isEmpty(inputVO.getInsCoType())){
			insCOOutputVO.setGetInsCoList(getInsjlbDao().queryInsCompanyAll());
		}
		
		//不是1的
		if(!INSCOTYPE_1.equals(inputVO.getInsCoType())){
			throw new APException("ehl_01_common_006", InsjlbUtils.createErrorMsgList("insCotype"));
		}
		
		insCOOutputVO.setGetInsCoList(getInsjlbDao().queryInsCompanyAll(inputVO.getInsCoType()));
		
		return insCOOutputVO;
	}
	
	/**取回資訊源產品資訊*/
	public ThirdInsProdOutputVO getThirdInsProd(ThirdInsProdInputVO inputVO) throws JBranchException {
		ThirdInsProdOutputVO outputVO = new ThirdInsProdOutputVO();
		List<Map<String, Object>> result = null;
		
		GetInsValiateUtils.validate(inputVO);
	
		result = getInsjlbDao().queryThirdInsProdMsg(inputVO);
		
		if(CollectionUtils.isEmpty(result)){
			outputVO.setThirdProdList(result);
		}
	
		return outputVO;
	}
}
