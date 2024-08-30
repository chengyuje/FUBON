package com.systex.jbranch.app.server.fps.insjlb.service;

import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCoInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCoOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdOutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface GetInsLocalPolicyServiceInf {
	/** getInsCO：取回保險公司的清單<br>
	 * @param GetInsCoInputVO<br>
	 * ---String insCoType ：固定1<br>
	 */
	public GetInsCoOutputVO getInsCO(GetInsCoInputVO inputVO) throws JBranchException;
	
	/** getThirdInsProd：取回資訊源產品資訊
	 * ThirdInsProdInputVO：
	 * --- String thirdProdCode :產品ID<br>
	 * --- String prodName :產品關鍵字<br>
	 * --- String insCO :保險公司代碼<br>
	 * --- String qid :商品屬性編號<br>
	 * --- String isMain :主約/附約<br>
	 * --- String isOld :現售/停售<br>
	 * --- String srfind<br>
	 **/
	public ThirdInsProdOutputVO getThirdInsProd(ThirdInsProdInputVO inputVO) throws JBranchException;
}
