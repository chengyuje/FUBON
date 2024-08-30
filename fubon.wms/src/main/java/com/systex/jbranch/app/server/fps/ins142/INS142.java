package com.systex.jbranch.app.server.fps.ins142;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmsub302.PrintInsExaminationReportInputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbBusinessInf;
import com.systex.jbranch.app.server.fps.insjlb.dao.InsjlbDaoInf;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.common.io.util.PdfInputOutputUtils;
import com.systex.jbranch.comutil.io.JoinDifferentSysBizLogic;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;

@Component("ins142")
@Scope("request")
public class INS142 extends JoinDifferentSysBizLogic{
	@Autowired @Qualifier("insjlb")
	private InsjlbBusinessInf insjlb;
	
	@Autowired @Qualifier("insjlbDao")
	private InsjlbDaoInf insjlbDao;
	
	@Autowired @Qualifier("ins810")
	private INS810 ins810;
	
	@Autowired 
	private PdfInputOutputUtils pdfUtils;	
	private final String RPT_CMRPT231 = "CMRPT231";// 逐年壽險保障一覽表
	private final String RPT_CMRPT232 = "CMRPT232";// 逐年意外保障一覽表
	private final String RPT_CMRPT233 = "CMRPT233";// 逐年重大疾病保障一覽表
	private final String RPT_CMRPT234 = "CMRPT234";// 逐年癌症保障一覽表
	private final String RPT_CMRPT229 = "CMRPT229";// 逐年還本金額一覽表
	private final String RPT_CMRPT230 = "CMRPT230";// 逐年保費一覽表
	private final String RPT_CMRPT205 = "CMRPT205";// 個人化彙總表
	private final String RPT_CMRPT212 = "CMRPT212";// 給付內容明細表
	private final String RPT_CMRPT216 = "CMRPT216";// 家庭資產負債表
	private final String RPT_R1 = "R1";
	private final String STR_TOTAL = "合計";
	private final String DATE_YEAR = "yyyy";
	private final String INDEX_0301 = "10301,10301A,10301B,10301C,20301,20301A,20301B,20301C,30301,30301A,30301B,30301C,99901,99902";

	/**
	 * 列印
	 */
	public String[] insReportCreate(JoinDifferentSysBizLogic target , PrintInsExaminationReportInputVO inputVO , DoGetCoverage03OutputVO doGetCoverage03OutputVO) throws Exception{
		//回傳報表url清單
		List<String> reportList = doGenPdfFile(inputVO , doGetCoverage03OutputVO);

		if (reportList.isEmpty()) {
			return new String[]{};
//			throw new APException("ehl_01_common_031");
		}

		if (reportList.size() > 0) {
			ReportDataIF rptData = new ReportData();
			rptData.addParameter("custName" , inputVO.getCustName());
			rptData.addParameter("loginName" , target.getCommonVariable(SystemVariableConsts.LOGINNAME));
			
			rptData.addParameter("loginBrhName" , 
				target.getCommonVariable(SystemVariableConsts.LOGINBRHNAME) + "(" + 
				target.getCommonVariable(SystemVariableConsts.LOGINBRH) + ")"
			);

			String date_str = getDateStr(new Date(), "yyyy/MM/dd", true);
			String[] date_lst = date_str.split("/");
			rptData.addParameter("year", date_lst[0]);
			rptData.addParameter("month", date_lst[1]);
			rptData.addParameter("day", date_lst[2]);

			rptData.addParameter("aphorism", "本公司已徵得委託人暸解並同意，" + "針對委託人所提供之保單資料(含保單關係人個人資料)" + "為保單檢視與保險規劃目的之必要範圍內蒐集、處理或利用。");
			rptData.setMerge(true);
		}

		// 2012/08/16,Carey,報表輸出成實體檔案，然後最後才去merge,BEGIN
		String[] merRpt = new String[reportList.size()];

		for (int index_int = 0; index_int < reportList.size(); index_int++) {
			merRpt[index_int] = reportList.get(index_int);
		}

		return merRpt;
	}
	
	public List<String> doGenPdfFile(PrintInsExaminationReportInputVO inputVO , DoGetCoverage03OutputVO doGetCoverage03OutputVO) throws Exception{
		// 列印報表標誌
		String prtReportFlag = "";
		// 每頁被保險人數
		int cntNumber = 0;
		//主附約繳費年期有資料
		boolean isLstPayYearNotEmpty = CollectionUtils.isNotEmpty(doGetCoverage03OutputVO.getLstPayYear());
		//給付中間檔有資料
		boolean isLstExpressionNotEmpty = CollectionUtils.isNotEmpty(doGetCoverage03OutputVO.getLstExpression());
		//給付內容明細表中間檔
		boolean isLstNewReportExpressionNotEmpty = CollectionUtils.isNotEmpty(doGetCoverage03OutputVO.getLstNewReportExpression());
		//保障領回中間檔有資料
		boolean isLstWholeLifeNotEmpty = CollectionUtils.isNotEmpty(doGetCoverage03OutputVO.getLstWholeLife());
		//客戶姓名+流水號 多筆(按客戶姓名第幾張保單去產生WholeLife格式)
		boolean isLstWholeLifeDtlNotEmpty = CollectionUtils.isNotEmpty(doGetCoverage03OutputVO.getLstWholeLifeDtl());
		//給付編號與中文
		Map sortNoNameMap = null;
		//保險公司名稱與代碼
		Map<String , Object> insCompany = null;
		//回傳報表url清單
		List<String> reportList = new ArrayList<String>();
		//回傳報表url清單-暫存多張
		List<String> tempReportList = null;
		//回傳報表url清單-暫存單張
		String tempReport = null;
		
		// 保費及保險險種名稱一覽表
//		if (inputVO.getPolicyDetail() && isLstPayYearNotEmpty) {
//			String reportLocation = null;
//			
//			if(StringUtils.isNotBlank(reportLocation = printReportCMRPT216(inputVO , doGetCoverage03OutputVO.getLstPayYear()))){
//				reportList.add(reportLocation);
//			}
//		}

		// 保障項目彙總表- INS142
		if (inputVO.getViewStructure() && isLstExpressionNotEmpty) {
			sortNoNameMap = sortNoNameMap == null ? doGetSortNoName() : sortNoNameMap;
			tempReportList = insjlb.printReportINS142(doGetCoverage03OutputVO , sortNoNameMap);
			
			if(CollectionUtils.isNotEmpty(tempReportList)){
				reportList.addAll(tempReportList);
			}
		}

		// 個人保險保障彙總表 - INS143
		if (inputVO.getIndYearSum() && isLstWholeLifeNotEmpty) {
			tempReportList = insjlb.printReportINS143(doGetCoverage03OutputVO , inputVO.getLstFamily());

			if(CollectionUtils.isNotEmpty(tempReportList)){
				reportList.addAll(tempReportList);
			}
		}

		// 生涯透視表 - INS144
		if (inputVO.getFamilyYear() && isLstWholeLifeNotEmpty) {
			tempReport = insjlb.printReportINS144(doGetCoverage03OutputVO , doGetCoverage03OutputVO.getLstExpression());
			
			if(StringUtils.isNotBlank(tempReport)){
				reportList.add(tempReport);
			}
		}
		
		// 保障項目明細表(詳實版) - INS145
		if (inputVO.getViewSumAll() && isLstExpressionNotEmpty) {
			
			tempReport = insjlb.printReportINS145(
				doGetCoverage03OutputVO.getLstExpression() , insCompany , sortNoNameMap , Ins145ReportInf.Type.COV03
			);
					
			if(StringUtils.isNotBlank(tempReport)){
				reportList.add(tempReport);
			}
		}
		
		// 取得產生PDF檔的instance
		//ReportGeneratorIF generator = ReportFactory.getGenerator();

//		// 逐年還本金額一覽表基本資料
//		if (inputVO.getReturnYear() && isLstWholeLifeDtlNotEmpty) {
//			prtReportFlag = "returnYear";
//			cntNumber = 5;// 設置每頁列印報表人數,CR,Ranchen,2012/09/27,修改為5
//			insCompany = insCompany == null ? doGetInsCompany() : insCompany;
//			this.CommonDataByTrue(reportList, doGetCoverage03OutputVO, insCompany, generator, prtReportFlag, cntNumber); // 设置rp
//		}
//
//		// 逐年保費一覽表基本資料
//		if (inputVO.getInsFeeYear() && isLstWholeLifeDtlNotEmpty) {
//			prtReportFlag = "insFeeYear";
//			cntNumber = 5;// 設置每頁列印報表人數,CR,Ranchen,2012/09/27,修改為5
//			insCompany = insCompany == null ? doGetInsCompany() : insCompany;
//			this.CommonDataByTrue(reportList, doGetCoverage03OutputVO, insCompany, generator, prtReportFlag, cntNumber); // 设置rp
//		}
//
//		// 給付內容明細表
//		if (inputVO.getInsPayDetail() && isLstExpressionNotEmpty && isLstNewReportExpressionNotEmpty) {
//			sortNoNameMap = sortNoNameMap == null ? doGetSortNoName() : sortNoNameMap;
//			insCompany = insCompany == null ? doGetInsCompany() : insCompany;
//			insPayDetailByTrue(doGetCoverage03OutputVO, reportList, insCompany, sortNoNameMap);
//		}
//		
//		// 檢視總表(個人化彙總表)
//		if (inputVO.getViewSum() && isLstExpressionNotEmpty) {
//			//受益人清單
//			Map<String, Object> benefitMap = doGetBeneficiary(inputVO.getLstFamily());
//			
//			if(MapUtils.isNotEmpty(benefitMap)){
//				insCompany = insCompany == null ? doGetInsCompany() : insCompany;
//				createIndividualSummaryRpt(doGetCoverage03OutputVO, reportList, insCompany, benefitMap);
//			}
//		}
		
		return reportList;
	}
	
	/**給付編號與中文，key為編號，value為給附項目中文*/
	public Map<String, Object> doGetSortNoName() throws DAOException, JBranchException{
		List<Map<String, Object>> sortNoNameList = null;
		Map sortNoNameMap = null;
		
		if (CollectionUtils.isNotEmpty(sortNoNameList = insjlbDao.querySortMap())) {
			sortNoNameMap = new HashMap<String, Object>();
			
			for (Map<String, Object> tempsortNoNameMap : sortNoNameList) {
				String sortNo = ObjectUtils.toString(tempsortNoNameMap.get("SORTNO"));
				String dsc = ObjectUtils.toString(tempsortNoNameMap.get("DSC"));
				sortNoNameMap.put(sortNo , dsc);
			}
		}
		
		return sortNoNameMap;
	}

	/**
	 * 將 Date 類型取得 int 類型(由 pattern 指定 年、月、日)
	 * 
	 * @param obj
	 * @return
	 */
	private String getDateStr(Object obj, String pattern, Boolean yyyBol) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (obj != null) {
			String result_str = sdf.format(obj);
			if (yyyBol) {
				int yyy_int = Integer.valueOf(result_str.substring(0, 4)) - 1911;
				return result_str.replaceFirst(result_str.substring(0, 4), "" + yyy_int);
			} else {
				return result_str;
			}
		} else {
			return "";
		}
	}
}
