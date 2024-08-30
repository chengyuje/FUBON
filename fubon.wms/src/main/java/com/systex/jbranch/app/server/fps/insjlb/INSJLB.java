package com.systex.jbranch.app.server.fps.insjlb;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.app.server.fps.ins142.Ins145ReportInf;
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
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

abstract public class INSJLB extends AbstractInsjlb{	
	/**取得保險百科條款與說明檔資訊)*/
	public GetInsPdfOutputVO getInsPdf(GetInsPdfInputVO inputVo) throws JBranchException{
		return getGetInsPdfService().getInsPdf(inputVo);
	}
	
	/**下載保險百科條款與說明檔*/
	public GetInsPdfOutputVO printPdf(BizLogic bizLogic , GetInsPdfInputVO inputVo) throws Exception {
		GetInsPdfOutputVO outputVo = null;
		String pType = ObjectUtils.toString(inputVo.getPdfType());
		
		//1.條款 2.說明 3.列印用(同時含條款&說明) 4.條款+說明
		if(!pType.matches("[1-4]")){
			throw new Exception("pdfType " + ObjectUtils.toString(inputVo.getPdfType()) + " can't print pdf");
		}
		
		//取得保險百科pdf以及資料
		outputVo = getInsPdf(inputVo);
				
		if(CollectionUtils.isEmpty(outputVo.getLstPdf())){
			throw new Exception("lstPdf can't not empty");
		}
		
		for(Map pdfMsgMap : outputVo.getLstPdf()){
			GenericMap pdfMsgGmap = new GenericMap(pdfMsgMap);
			//1.條款 2.說明 3.列印用(同時含條款&說明) 4.條款+說明
			if(pType.matches("[1-3]") && pdfMsgGmap.isStringNotBank("URL1")){
				String url = pdfMsgGmap.getNotNullStr("URL1");
				String fileName = pdfMsgGmap.getNotNullStr("pdfName1").replaceAll("\\(|\\)", "") + ".pdf";
				bizLogic.notifyClientToDownloadFile(url , fileName);
			}
			//4.條款+說明
			else if(pType.equals("4") && pdfMsgGmap.isStringNotBank("URL2")){
				String url = pdfMsgGmap.getNotNullStr("URL2");
				String fileName = pdfMsgGmap.getNotNullStr("pdfName2").replaceAll("\\(|\\)", "") + ".pdf";
				bizLogic.notifyClientToDownloadFile(url , fileName);	
			}			
		}
		
		return outputVo;
	}
	
	/**取回保險公司的清單 */
	public GetInsCoOutputVO getInsCO(GetInsCoInputVO inputVO) throws JBranchException{
		return getGetInsLocalPolicyService().getInsCO(inputVO);
	}
	
	/**取回資訊源產品資訊*/
	public ThirdInsProdOutputVO getThirdInsProd(ThirdInsProdInputVO inputVO) throws JBranchException {
		return getGetInsLocalPolicyService().getThirdInsProd(inputVO);
	}
	
	/**健檢攤列的各項資料(多筆)(報表)*/
	public DoGetCoverage03OutputVO doGetCoverage03(DoGetCoverage03InputVO inputVO) throws JBranchException {
		return getGetCoverage03Service().doGetCoverage03(inputVO);
	}
	
	/**健檢攤列的各項資料(單筆)*/
	public DoGetCoverage01OutputVO doGetCoverage01(DoGetCoverage01InputVO inputVO) throws JBranchException{
		return getGetCoverage01Service().doGetCoverage01(inputVO);
	}
	
	/**計算保險年齡*/
	public CalculateInsAgeOutputVO calculateInsAge(CalculateInsAgeInputVO calculateInsAgeInputVO) throws JBranchException {
		CalculateInsAgeOutputVO outputVo = new CalculateInsAgeOutputVO();
		outputVo.setInsCustAge(ObjectUtils.toString(InsjlbUtils.calculateInsAge(
			calculateInsAgeInputVO.getInsSysDate(),
			calculateInsAgeInputVO.getInsCustBirthday()
		)));
		return outputVo;
	}
	
	/**取得既有保障明細*/
	public GetOldItemListOutputVO getOldItemList(GetOdItemListInputVO getOdItemListInputVO) throws JBranchException {
		return getGetOldItemListService().getOldItemList(getOdItemListInputVO);
	}
	
	/**保險險種比較*/
	public GetInsCompareOutputVO getInsCompare(GetInsCompareInputVO inputVo) throws JBranchException{
		return getGetInsCompareService().getInsCompare(inputVo);
	}
	
	/**保險險種比較後列印表單*/
	public String getInsCompareToIns145Report(GetInsCompareInputVO inputVo) throws JBranchException{
		GetInsCompareOutputVO outputVo = getGetInsCompareService().getInsCompare(inputVo);
		
		if(CollectionUtils.isNotEmpty(outputVo.getLstExpressionCombind())){
			return printReportINS145(outputVo.getLstExpressionCombind() , Ins145ReportInf.Type.COV01);
		}
		
		return null;
	}
	
	public List<String> printReportINS142(DoGetCoverage03OutputVO doOutputVO , Map<String, Object> sortNoNameMap) throws JBranchException{
		return getIns142Report().printReportINS142(doOutputVO , sortNoNameMap);
	}
	
	public List<String> printReportINS143(DoGetCoverage03OutputVO doOutputVO , List<Map<String, Object>> lstFamily) throws DAOException, JBranchException{
		return getIns143Report().printReportINS143(doOutputVO, lstFamily);
	}
	
	public String printReportINS144(DoGetCoverage03OutputVO doOutputVO , List<Map<String, Object>> familyDataList) throws DAOException, JBranchException{
		return getIns144Report().printReportINS144(doOutputVO, familyDataList);
	}
	
	public String printReportINS145(List lstEx , Type type)throws DAOException, JBranchException{
		return getIns145Report().printReportINS145(lstEx, type);
	}
	
	public String printReportINS145(List lstEx , Map<String , Object> insCompanyMap , Map<String , Object> sortNoNameMap , Type type)throws JBranchException{
		return getIns145Report().printReportINS145(lstEx, insCompanyMap, sortNoNameMap, type);
	}
}
