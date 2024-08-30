package com.systex.jbranch.app.server.fps.insjlb.service;

import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public interface GetCoverage03ServiceInf{
	/**@param 
	 * DoGetCoverage03InputVO inputVO<br>
	 * ___ String insCustID ： 客戶ID<br>
	 * ___ String callType ： 呼叫類型(1: 要保人'得到全家',2: 被保險人)<br>
	 * ___ String includeAll ： 是否包含非家庭戶(個人)資料(Y: 是,N: 否 ,空白/NULL：亦否)	<br>
	 * ___ List<Map> lstInsData ： 客戶保單資訊<br>
	 * ___ List lstException ： 例外清單<br>
	 * ___ List<Map> lstFamily ： 家庭戶客戶id清單<br>
	 * ___ String doSave ： 是否存檔(Y: 存檔,N: 不存檔,空白/null：亦不存檔)<br>
	 * 
	 * @return 
	 * DoGetCoverage03OutputVO<br>
	 * ___ WholeLife ： 保障領回中間檔 多筆：加總的值；(個人生涯彙總表、壽險保障彙總表用)<br>
	 * ___ Expression ：給付說明中間檔 多筆；(檢視總表用)( RelationCode asc , CustName asc, SignDate asc,PolicyID asc, FirstKind asc, SortNo asc)<br>
	 * ___ PremPerMonth ：逐月保費中間檔 多筆；(逐月保費一覽表用)<br>
	 * ___ PremDetail ：保費明細中間檔 多筆；(保費明細表用)<br>
	 * ___ NewReportExpression ：給付內容明細表<br>
	 * ___ AllPolicyOBJ多筆：(TableName、CountNUM、LastNUM、CustPolicy、StartAge)<br>
	 * ___ tmpExpression ：檢視總表合計用：(比較dsPolicyDtl內所有的PolicyID(TableName)下所有的資料列，若是姓名一樣會產生tmpExpression) <br>                                                   
	 * ___ wholeLifeDtl：客戶姓名+流水號 多筆(按客戶姓名第幾張保單去產生WholeLife格式)       <br>                                                 
	 * ___ PayYear：回傳顯示各主附約的繳費年期<br>
	*/
	public DoGetCoverage03OutputVO doGetCoverage03(DoGetCoverage03InputVO inputVO) throws JBranchException;
}
