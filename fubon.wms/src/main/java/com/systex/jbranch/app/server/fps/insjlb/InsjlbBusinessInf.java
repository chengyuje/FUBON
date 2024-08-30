package com.systex.jbranch.app.server.fps.insjlb;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.ins142.Ins145ReportInf.Type;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalculateInsAgeInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalculateInsAgeOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCoInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCoOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCompareInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCompareOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOldItemListOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdOutputVO;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

public interface InsjlbBusinessInf extends InsjlbParamInf{	
	/**取回保險公司的清單*/
	public GetInsCoOutputVO getInsCO(GetInsCoInputVO inputVO) throws JBranchException;
	/**取回資訊源產品資訊*/
	public ThirdInsProdOutputVO getThirdInsProd(ThirdInsProdInputVO inputVO) throws JBranchException;
	/**準備webservice GetCoverage03的資料並呼叫**/
	public DoGetCoverage03OutputVO doGetCoverage03(DoGetCoverage03InputVO inputVO) throws JBranchException;
	/**準備webservice GetCoverage01的資料並呼叫**/
	public DoGetCoverage01OutputVO doGetCoverage01(DoGetCoverage01InputVO doGetCoverage01InputVO) throws JBranchException;
	/**計算保險年齡**/
	public CalculateInsAgeOutputVO calculateInsAge(CalculateInsAgeInputVO calculateInsAgeInputVO) throws JBranchException ;
	/**取得既有保障明細*/
	public GetOldItemListOutputVO getOldItemList(GetOdItemListInputVO getOdItemListInputVO) throws JBranchException ;
	/**取得保險百科條款與說明檔(url)**/
	public GetInsPdfOutputVO getInsPdf(GetInsPdfInputVO inputVo)throws JBranchException;
	/**取得保險百科條款與說明檔(url)，並下載PDF**/
	public GetInsPdfOutputVO printPdf(BizLogic bizLogic , GetInsPdfInputVO inputVo) throws Exception;
	/**保險險種比較*/
	public GetInsCompareOutputVO getInsCompare(GetInsCompareInputVO inputVo) throws JBranchException;
	/**保險險種比較，並將組合好的給付中間檔製作成INS145報表並回傳URL*/
	public String getInsCompareToIns145Report(GetInsCompareInputVO inputVo) throws JBranchException;
	/**製作142 pdf並回傳URLl*/
	public List<String> printReportINS142(DoGetCoverage03OutputVO doOutputVO , Map<String, Object> sortNoNameMap) throws JBranchException;
	/**製作143 pdf並回傳URL*/
	public List<String> printReportINS143(DoGetCoverage03OutputVO doOutputVO , List<Map<String, Object>> lstFamily) throws DAOException, JBranchException;
	/**製作144 pdf並回傳URL*/
	public String printReportINS144(DoGetCoverage03OutputVO doOutputVO , List<Map<String, Object>> familyDataList) throws DAOException, JBranchException;
	/**製作145 pdf並回傳URL*/
	public String printReportINS145(List lstEx , Type type)throws DAOException, JBranchException;
	/**製作145 pdf並回傳URL*/
	public String printReportINS145(List lstEx , Map<String , Object> insCompanyMap , Map<String , Object> sortNoNameMap , Type type)throws JBranchException;
}
