package com.systex.jbranch.app.server.fps.prd235;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_PRJ_ROTATION_MVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUND_OVS_PRIVATEVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_D_OVS_PRIPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_D_OVS_PRIVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703;
import com.systex.jbranch.app.server.fps.sot703.SOT703InputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfConfigVO;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd235")
@Scope("request")
public class PRD235  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD235InputVO inputVO = (PRD235InputVO) body;
		PRD235OutputVO return_VO = new PRD235OutputVO();
		
		List<Map<String, Object>> list = this.inquire(inputVO);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public List<Map<String, Object>> inquire(PRD235InputVO inputVO) throws JBranchException {
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.*, B.FUND_CNAME AS PRD_NAME, B.RISKCATE_ID, B.CURRENCY_STD_ID, C.STATUS, ");
		//是否可點選贖回總表(資料還未傳送主機成功、申請截止日之後)
		sql.append(" CASE WHEN C.STATUS IN ('0', '1', '2', '3', '5') AND TRUNC(SYSDATE) >= TRUNC(A.DEADLINE_DATE) THEN 'Y' ELSE 'N' END AS STATUS_1, "); 
		//是否可填寫新單位數(產出贖回總表後且資料還未傳送主機成功、系統日在申請截止日與交易日之間)
		sql.append(" CASE WHEN C.STATUS IN ('1', '2', '3', '5') AND TRUNC(SYSDATE) BETWEEN TRUNC(A.DEADLINE_DATE) AND TRUNC(A.TRADE_DATE) THEN 'Y' ELSE 'N' END AS STATUS_2, ");
		//是否可查詢新單位數修改明細(傳送主機成功)
		sql.append(" CASE WHEN C.STATUS = '4' THEN 'Y' ELSE 'N' END AS STATUS_3 ");
		sql.append(" FROM TBPRD_FUND_OVS_PRIVATE A ");
		sql.append(" INNER JOIN TBPRD_FUND B ON B.PRD_ID = A.PRD_ID ");
		sql.append(" LEFT JOIN (SELECT T.*, ROW_NUMBER()OVER (PARTITION BY PRD_SEQ_NO ORDER BY PRD_SEQ_NO) RN FROM TBSOT_NF_REDEEM_D_OVS_PRI T) C ON C.PRD_SEQ_NO = A.SEQ_NO AND C.RN = 1 ");
		sql.append(" WHERE 1 = 1 ");
		
		// where
		if (inputVO.getSEQ_NO() != null) {
			sql.append(" AND A.SEQ_NO = :seqNo ");
			queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		}
		if (!StringUtils.isBlank(inputVO.getPRD_ID())) {
			sql.append(" AND A.PRD_ID = :prdId ");
			queryCondition.setObject("prdId", inputVO.getPRD_ID().toUpperCase());
		}
		if (!StringUtils.isBlank(inputVO.getFUND_NAME())) {
			sql.append("and B.FUND_CNAME like :name ");
			queryCondition.setObject("name", "%" + inputVO.getFUND_NAME() + "%");
		}
		if(StringUtils.equals("Y", inputVO.getValidDateYN())) {
			sql.append("and TRUNC(A.END_DATE) >= TRUNC(SYSDATE) ");
		}
		if(!StringUtils.isBlank(inputVO.getTRADE_TYPE())) {
			sql.append(" AND A.TRADE_TYPE = :tradeType ");
			queryCondition.setObject("tradeType", inputVO.getTRADE_TYPE());
		}
		sql.append(" ORDER BY A.PRD_ID ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list;
	}
	
	//取得商品名稱
	public void getPrdName(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD235InputVO inputVO = (PRD235InputVO) body;
		PRD235OutputVO return_VO = new PRD235OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.FUND_CNAME, NVL(A.OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN, B.MIN_PURCHASE_AMT ");
		sql.append(" FROM TBPRD_FUND A ");		
		sql.append(" LEFT JOIN TBPRD_FUND_OVS_PRIVATE B ON B.PRD_ID = A.PRD_ID ");
		sql.append(" 	AND B.END_DATE = (SELECT MAX(END_DATE) FROM TBPRD_FUND_OVS_PRIVATE WHERE PRD_ID = A.PRD_ID) ");
		sql.append(" WHERE A.PRD_ID = :prdId ");
		queryCondition.setObject("prdId", inputVO.getPRD_ID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isEmpty(list)) {
			throw new APException("查無此商品資料");
		} else if(StringUtils.equals("N", list.get(0).get("OVS_PRIVATE_YN").toString())) {
//			throw new APException("此商品非境外私募基金");
		}
		return_VO.setPrdName(list.get(0).get("FUND_CNAME").toString());
		return_VO.setMIN_PURCHASE_AMT((BigDecimal) list.get(0).get("MIN_PURCHASE_AMT"));
		
		this.sendRtnObject(return_VO);
	}
	
	//取得費用率與報酬率資料
	public void getFeeRates(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD235InputVO inputVO = (PRD235InputVO) body;
		PRD235OutputVO return_VO = new PRD235OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.*, B.FUND_CNAME, C.PARAM_NAME AS FEE_YEAR ");
		sql.append(" FROM TBPRD_FUND_BONUSINFO_RATES A ");		
		sql.append(" LEFT JOIN TBPRD_FUND B ON B.PRD_ID = A.PRD_ID ");
		sql.append(" LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND C.PARAM_CODE = A.PRD_ID ");
		sql.append(" WHERE NVL(B.OVS_PRIVATE_YN, 'N') = 'Y' "); //境外私募基金
		queryCondition.setQueryString(sql.toString());		
		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(return_VO);
	}
	
	//商品資料儲存
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD235InputVO inputVO = (PRD235InputVO) body;
		PRD235OutputVO return_VO = new PRD235OutputVO();
		dam = this.getDataAccessManager();
				
		if(StringUtils.equals("1", inputVO.getSaveType())) { //新增/修改
			//同商品開放起訖日期間不得重疊
			chkStartEndDate(inputVO);
			
			TBPRD_FUND_OVS_PRIVATEVO fvo = new TBPRD_FUND_OVS_PRIVATEVO();
			if(inputVO.getSEQ_NO() == null) {
				fvo.setSEQ_NO(getKeyNo());
				fvo.setPRD_ID(inputVO.getPRD_ID());
				fvo.setTRADE_TYPE(inputVO.getTRADE_TYPE());
				fvo.setPRD_YEAR(inputVO.getPRD_YEAR());
				fvo.setPRD_CATEGORY(inputVO.getPRD_CATEGORY());
				fvo.setSTART_DATE(inputVO.getSTART_DATE());
				fvo.setEND_DATE(inputVO.getEND_DATE());
				fvo.setDEADLINE_DATE(inputVO.getDEADLINE_DATE());
				fvo.setTRADE_DATE(inputVO.getTRADE_DATE());
				fvo.setMIN_PURCHASE_AMT(inputVO.getMIN_PURCHASE_AMT());
				
				dam.create(fvo);
			} else {
				fvo = (TBPRD_FUND_OVS_PRIVATEVO) dam.findByPKey(TBPRD_FUND_OVS_PRIVATEVO.TABLE_UID, inputVO.getSEQ_NO());			
				if(fvo == null) {
					throw new APException("查無此筆序號資料：" + inputVO.getSEQ_NO().toString());
				}
				
				fvo.setPRD_ID(inputVO.getPRD_ID());
				fvo.setTRADE_TYPE(inputVO.getTRADE_TYPE());
				fvo.setPRD_YEAR(inputVO.getPRD_YEAR());
				fvo.setPRD_CATEGORY(inputVO.getPRD_CATEGORY());
				fvo.setSTART_DATE(inputVO.getSTART_DATE());
				fvo.setEND_DATE(inputVO.getEND_DATE());
				fvo.setDEADLINE_DATE(inputVO.getDEADLINE_DATE());
				fvo.setTRADE_DATE(inputVO.getTRADE_DATE());
				fvo.setMIN_PURCHASE_AMT(inputVO.getMIN_PURCHASE_AMT());
				
				dam.update(fvo);
			}
		} else if(StringUtils.equals("2", inputVO.getSaveType())) { //刪除
			TBPRD_FUND_OVS_PRIVATEVO fvo = (TBPRD_FUND_OVS_PRIVATEVO) dam.findByPKey(TBPRD_FUND_OVS_PRIVATEVO.TABLE_UID, inputVO.getSEQ_NO());			
			if(fvo == null) {
				throw new APException("查無此筆序號資料：" + inputVO.getSEQ_NO().toString());
			} else {
				dam.delete(fvo);
			}
		} else if(StringUtils.equals("3", inputVO.getSaveType())) { //調整贖回單位數，送主管覆核
			for (Map<String,Object> map : inputVO.getAdjList()) {
				TBSOT_NF_REDEEM_D_OVS_PRIPK oppk = new TBSOT_NF_REDEEM_D_OVS_PRIPK();
				oppk.setPRD_SEQ_NO(new BigDecimal((Double)map.get("PRD_SEQ_NO")));
				oppk.setBATCH_SEQ(ObjectUtils.toString(map.get("BATCH_SEQ")));
				TBSOT_NF_REDEEM_D_OVS_PRIVO opvo = (TBSOT_NF_REDEEM_D_OVS_PRIVO) dam.findByPKey(TBSOT_NF_REDEEM_D_OVS_PRIVO.TABLE_UID, oppk);
				
				if(opvo != null) {
					opvo.setADJ_UNIT_NUM(map.get("ADJ_UNIT_NUM") == null ? BigDecimal.ZERO : new BigDecimal((Double)map.get("ADJ_UNIT_NUM")));
					opvo.setNEW_RDM_TOTAL_UNITS(inputVO.getNEW_RDM_TOTAL_UNITS() == null ? BigDecimal.ZERO : inputVO.getNEW_RDM_TOTAL_UNITS());
					opvo.setSTATUS("2");
					dam.update(opvo);
				}
			}
		} else if(StringUtils.equals("4", inputVO.getSaveType())) { //調整贖回單位數，主管核可
			//傳送電文
			SOT703InputVO inputVO703 = new SOT703InputVO();
			SOT703OutputVO outputVO703 = new SOT703OutputVO();
			inputVO703.setSeqNo(inputVO.getPRD_SEQ_NO());
			SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
			outputVO703 = sot703.adjESBRedeemNFOvsPri(inputVO703);
						
			String status = "4"; //傳送電文成功
			if(StringUtils.isNotBlank(outputVO703.getErrorCode())) {
				status = "5"; //傳送電文失敗，有錯誤訊息
				return_VO.setErrorCode(outputVO703.getErrorCode());
				return_VO.setErrorMsg(outputVO703.getErrorMsg());
			}
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBSOT_NF_REDEEM_D_OVS_PRI ");
			sql.append(" SET STATUS = :status, MODIFIER = :loginId, LASTUPDATE = SYSDATE ");
			sql.append("WHERE PRD_SEQ_NO = :prdSeqNo ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("loginId", getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("prdSeqNo", inputVO.getPRD_SEQ_NO());
			queryCondition.setObject("status", status);
			dam.exeUpdate(queryCondition);
		} else if(StringUtils.equals("5", inputVO.getSaveType())) { //調整贖回單位數，主管退回
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE TBSOT_NF_REDEEM_D_OVS_PRI ");
			sql.append(" SET STATUS = '3', MODIFIER = :loginId, LASTUPDATE = SYSDATE ");
			sql.append("WHERE PRD_SEQ_NO = :prdSeqNo ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("loginId", getUserVariable(FubonSystemVariableConsts.LOGINID));
			queryCondition.setObject("prdSeqNo", inputVO.getPRD_SEQ_NO());
			dam.exeUpdate(queryCondition);
		}
		
		this.sendRtnObject(return_VO);
	}
	
	// 取得境外私募基金資料檔主鍵
	private BigDecimal getKeyNo() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT TBPRD_FUND_OVS_PRIVATE_SEQ.NEXTVAL FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		BigDecimal keyNo = (BigDecimal) list.get(0).get("NEXTVAL");
		
		return keyNo;
	}
	
	//檢核同商品開放起訖日期間不得重疊
	private void chkStartEndDate(PRD235InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT 1 FROM TBPRD_FUND_OVS_PRIVATE ");
		sql.append(" WHERE PRD_ID = :prdId AND TRADE_TYPE = :tradeType ");
		sql.append(" AND (TRUNC(:startDate) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE) OR TRUNC(:endDate) BETWEEN TRUNC(START_DATE) AND TRUNC(END_DATE)) ");
		if(inputVO.getSEQ_NO() != null) {
			sql.append(" AND SEQ_NO <> :seqNo ");
			queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		}
		queryCondition.setObject("prdId", inputVO.getPRD_ID());
		queryCondition.setObject("tradeType", inputVO.getTRADE_TYPE());
		queryCondition.setObject("startDate", inputVO.getSTART_DATE());
		queryCondition.setObject("endDate", inputVO.getEND_DATE());
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(CollectionUtils.isNotEmpty(list)) {
			throw new APException("同商品開放起訖日期間不得重疊");
		}
		
		return;
	}
	
	/**
     * 下載費用率與報酬率範例檔
     * @param body
     * @param header
     * @throws Exception
     */
    public void downloadSimpleFee(Object body, IPrimitiveMap header) {
		notifyClientToDownloadFile("doc//PRD//PRD235_EXAMPLE.csv", "上傳費用率與報酬率範例.csv");
	    this.sendRtnObject(null);
	}
    
    /***
     * 上傳費用率與報酬率資料
     * @param body
     * @param header
     * @throws JBranchException
     */
  	public void uploadFee(Object body, IPrimitiveMap header) throws JBranchException {
  		PRD235InputVO inputVO = (PRD235InputVO) body;
		PRD235OutputVO return_VO = new PRD235OutputVO();
  		dam = this.getDataAccessManager();
  		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
  		String loginID = getUserVariable(FubonSystemVariableConsts.LOGINID).toString();
  		
  		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
  		List<String[]> dataCsv = new ArrayList<String[]>();
  		if(StringUtils.isNotBlank(inputVO.getFileName())) {
  			dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
  		}
  		
  		if(!dataCsv.isEmpty()) {
  			//費用率與報酬率資料匯入
  			for(int i = 0; i < dataCsv.size(); i++) {
  				if(i == 0) continue; //Header
  				
  				String[] str = dataCsv.get(i);
  				if(StringUtils.isBlank(str[0]) || StringUtils.isBlank(str[1]))
  					continue; //商品代碼為空白
  				if(str.length != 12)
  					throw new APException("檔案格式錯誤，請依據下載範例");
  				
  				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
  	  			queryCondition.setQueryString("SELECT 1 FROM TBPRD_FUND_BONUSINFO_RATES WHERE PRD_ID = :prdId ");
  	  			queryCondition.setObject("prdId", str[0]);
  	  			
  	  			StringBuilder sb = new StringBuilder();
				if (CollectionUtils.isEmpty(dam.exeQuery(queryCondition))) {
					sb.append("INSERT INTO TBPRD_FUND_BONUSINFO_RATES ( ");
					sb.append(" 		PRD_ID, FEE_RATE_1, FEE_RATE_2, FEE_RATE_3, FEE_RATE_4, FEE_RATE_5, ROI_1, ROI_2, ROI_3, ROI_4, ROI_5, ");
					sb.append(" 		VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
					sb.append(" VALUES( :PRD_ID, :FEE_RATE_1, :FEE_RATE_2, :FEE_RATE_3, :FEE_RATE_4, :FEE_RATE_5, :ROI_1, :ROI_2, :ROI_3, :ROI_4, :ROI_5, ");
					sb.append(" 		1, :loginID, SYSDATE, :loginID, SYSDATE) ");
				} else {
					sb.append("UPDATE TBPRD_FUND_BONUSINFO_RATES ");
					sb.append(" SET FEE_RATE_1 = :FEE_RATE_1, FEE_RATE_2 = :FEE_RATE_2, FEE_RATE_3 = :FEE_RATE_3, FEE_RATE_4 = :FEE_RATE_4, FEE_RATE_5 = :FEE_RATE_5, ");
					sb.append(" 	ROI_1 = :ROI_1, ROI_2 = :ROI_2, ROI_3 = :ROI_3, ROI_4 = :ROI_4, ROI_5 = :ROI_5, ");
					sb.append(" 	VERSION = VERSION + 1, CREATOR = :loginID, CREATETIME = SYSDATE , MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
					sb.append(" WHERE PRD_ID = :PRD_ID ");
				}
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("PRD_ID", str[0]);
				queryCondition.setObject("FEE_RATE_1", str[2]);
				queryCondition.setObject("FEE_RATE_2", str[3]);
				queryCondition.setObject("FEE_RATE_3", str[4]);
				queryCondition.setObject("FEE_RATE_4", str[5]);
				queryCondition.setObject("FEE_RATE_5", str[6]);
				queryCondition.setObject("ROI_1", str[7]);
				queryCondition.setObject("ROI_2", str[8]);
				queryCondition.setObject("ROI_3", str[9]);
				queryCondition.setObject("ROI_4", str[10]);
				queryCondition.setObject("ROI_5", str[11]);
				queryCondition.setObject("loginID", loginID);
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);
				
				//更新基金主檔"基金報酬揭露檢核"欄位
				updateFundMain(str[0]);
				
				//更新年度
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);					
				queryCondition.setQueryString("SELECT 1 FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND PARAM_CODE = :prdId ");
  	  			queryCondition.setObject("prdId", str[0]);
  	  			sb = new StringBuilder();
  	  			if (CollectionUtils.isEmpty(dam.exeQuery(queryCondition))) {
	  	  			sb.append("INSERT INTO TBSYSPARAMETER ( ");
					sb.append(" 		PARAM_TYPE, PARAM_CODE, PARAM_NAME, PARAM_NAME_EDIT, PARAM_ORDER, PARAM_STATUS, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ");
					sb.append(" VALUES( 'PRD.FUND_FEE_YEAR', :prdId, :prdYear, :prdYear, 1, '1', 1, :loginID, SYSDATE, :loginID, SYSDATE) ");
				} else {
					sb.append("UPDATE TBSYSPARAMETER ");
					sb.append(" SET PARAM_NAME = :prdYear, PARAM_NAME_EDIT = :prdYear, ");
					sb.append(" 	VERSION = VERSION + 1, CREATOR = :loginID, CREATETIME = SYSDATE , MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
					sb.append(" WHERE PARAM_TYPE = 'PRD.FUND_FEE_YEAR' AND PARAM_CODE = :prdId ");
				}
	  	  		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setObject("prdId", str[0]);
				queryCondition.setObject("prdYear", str[1]);
				queryCondition.setObject("loginID", loginID);
				queryCondition.setQueryString(sb.toString());
				dam.exeUpdate(queryCondition);
  			}
  		}
  		
  		this.sendRtnObject(null);
  	}	
  	
  	/** 更新TBPRD_FUND **/
	private void updateFundMain(String pKey) throws DAOException {
		// 2017/3/13
		dam = this.getDataAccessManager();
		TBPRD_FUNDVO mvo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, pKey);
		if(mvo != null) {
			mvo.setFLAG("1");
			dam.update(mvo);
		}
	}
	
	/***
	 * 取得贖回資料明細
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void getRedeemDetails(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		PRD235InputVO inputVO = (PRD235InputVO) body;
		PRD235OutputVO outputVO = new PRD235OutputVO();
		
		outputVO.setResultList(getRedeemDetails(inputVO));
		
		List<Map<String, Object>> totalList = getRdmTotalUnitsList(inputVO);
		outputVO.setRdmTotalUnits((CollectionUtils.isEmpty(totalList) || totalList.get(0).get("TOTAL_UNIT") == null) ? BigDecimal.ZERO : (BigDecimal) totalList.get(0).get("TOTAL_UNIT"));
		
		this.sendRtnObject(outputVO);
	}
	
	/***
	 * 取得贖回資料明細
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	private List<Map<String, Object>> getRedeemDetails(PRD235InputVO inputVO) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//取得該商品贖回資料明細
		sql.append("SELECT A.*, B.PRD_YEAR, B.PRD_CATEGORY, TO_CHAR(B.DEADLINE_DATE, 'YYYY/MM/DD') AS DEADLINE_DATE, ");
		sql.append(" TO_CHAR(A.UNIT_NUM, '999G999G990D9999') AS UNIT_NUM_STR, TO_CHAR(A.RDM_TRADE_DATE, 'YYYY/MM/DD') AS RDM_TRADE_DATE_STR, ");
		sql.append(" C.PRD_YEAR AS BUY_PRD_YEAR, C.PRD_CATEGORY AS BUY_PRD_CATEGORY, D.FUND_ENAME ");
		sql.append(" FROM TBSOT_NF_REDEEM_D_OVS_PRI A ");
		sql.append(" LEFT JOIN TBPRD_FUND_OVS_PRIVATE B ON B.SEQ_NO = A.PRD_SEQ_NO ");
		sql.append(" LEFT JOIN TBPRD_FUND_OVS_PRIVATE C ON C.PRD_ID = A.PRD_ID AND C.TRADE_TYPE = '1' AND TRUNC(C.TRADE_DATE) = TRUNC(A.RDM_TRADE_DATE) "); //以申購交易日資料串回申購年份及次別
		sql.append(" LEFT JOIN TBPRD_FUND D ON D.PRD_ID = A.PRD_ID ");
		sql.append(" WHERE PRD_SEQ_NO = :seqNo ");
		queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		queryCondition.setQueryString(sql.toString());		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list;
	}
	
	/***
	 * 總贖回單位數
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getRdmTotalUnitsList(PRD235InputVO inputVO) throws DAOException, JBranchException {
		//總贖回單位數
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();		
		sql.append("SELECT PRD_SEQ_NO, TO_CHAR(SUM(UNIT_NUM), '999G999G999G999G990D9999') AS TOTAL_UNIT_STR, SUM(UNIT_NUM) AS TOTAL_UNIT ");
		sql.append(" FROM TBSOT_NF_REDEEM_D_OVS_PRI WHERE PRD_SEQ_NO = :seqNo GROUP BY PRD_SEQ_NO ");
		queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		queryCondition.setQueryString(sql.toString());		
		List<Map<String, Object>> totalList = dam.exeQuery(queryCondition);
		
		return totalList;
	}
	
	/***
	 * 以認購日期加總贖回單位數
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getRdmTotalByTradeDateList(PRD235InputVO inputVO) throws DAOException, JBranchException {
		//以認購日期加總贖回單位數
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();	
		sql.append("SELECT * FROM ( ");
		sql.append(" SELECT B.PRD_YEAR, B.PRD_CATEGORY, TO_CHAR(SUM(A.UNIT_NUM), '999G999G999G999G990D9999') AS TOTAL_UNIT_STR_1, SUM(A.UNIT_NUM) AS TOTAL_UNIT_1 ");
		sql.append(" FROM TBSOT_NF_REDEEM_D_OVS_PRI A ");
		sql.append(" LEFT JOIN TBPRD_FUND_OVS_PRIVATE B ON B.PRD_ID = A.PRD_ID AND B.TRADE_TYPE = '1' AND TRUNC(B.TRADE_DATE) = TRUNC(A.RDM_TRADE_DATE) "); //以申購交易日資料串回申購年份及次別
		sql.append(" WHERE A.PRD_SEQ_NO = :seqNo ");
		sql.append(" GROUP BY B.PRD_YEAR, B.PRD_CATEGORY ");
		sql.append(" ) ORDER BY PRD_YEAR, PRD_CATEGORY ");
		queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		queryCondition.setQueryString(sql.toString());		
		List<Map<String, Object>> totalList = dam.exeQuery(queryCondition);
		
		return totalList;
	}
	
	/***
	 * 產出贖回總表
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void printReport(Object body, IPrimitiveMap header) throws JBranchException {
		PRD235InputVO inputVO = (PRD235InputVO) body;
		PRD235OutputVO return_VO = new PRD235OutputVO();
		
		//取得贖回資料明細
		List<Map<String, Object>> list = getRedeemDetails(inputVO);		
		if(CollectionUtils.isEmpty(list)) {
			throw new APException("查無贖回資料");
		}
		
		//總贖回單位數
		List<Map<String, Object>> totalList = getRdmTotalUnitsList(inputVO);
		String totalUnit = CollectionUtils.isEmpty(totalList) ? "" : ObjectUtils.toString(totalList.get(0).get("TOTAL_UNIT_STR"));
		//以認購日期加總贖回單位數
		List<Map<String, Object>> tradeDateList = getRdmTotalByTradeDateList(inputVO);
//		List<Map<String, Object>> totalTradeDateList = new ArrayList<Map<String, Object>>();
//		if(CollectionUtils.isNotEmpty(tradeDateList)) {
//			totalTradeDateList.addAll(tradeDateList);
//			Map<String, Object> maptotal = new HashMap<String, Object>();
//			maptotal.put("RDM_TRADE_DATE_1", "Total");
//			maptotal.put("TOTAL_UNIT_STR_1", totalUnit);
//			maptotal.put("TOTAL_UNIT_1", CollectionUtils.isEmpty(totalList) ? null : (BigDecimal)totalList.get(0).get("TOTAL_UNIT"));
//			totalTradeDateList.add(maptotal);
//		}
		
		String txnCode = "PRD235";
		String reportID = "R1";
		ReportIF report = null;
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator();
		
		data.addRecordList("DATALIST", list);
		data.addParameter("PRD_NAME", list.get(0).get("PRD_NAME"));
		data.addParameter("FUND_ENAME", list.get(0).get("FUND_ENAME"));
		data.addParameter("PRD_YEAR", list.get(0).get("PRD_YEAR"));
		data.addParameter("PRD_CATEGORY", list.get(0).get("PRD_CATEGORY"));
		data.addParameter("DEADLINE_DATE", list.get(0).get("DEADLINE_DATE"));
		data.addParameter("TOTAL_UNIT_NUM", totalUnit.trim());
		//以認購日期加總贖回單位數
		data.addRecordList("TOTAL_TRADEDATE", tradeDateList);
		
		report = gen.generateReport(txnCode, reportID, data);
		String url = report.getLocation();
		String encryptUrl = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), url));
		notifyClientToDownloadFile(encryptUrl, "贖回總表.pdf");
		
		//產出贖回總表後，將狀態壓為可填寫(只更新狀態為尚未列印贖回總表的資料)
		dam = this.getDataAccessManager();
  		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
  		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE TBSOT_NF_REDEEM_D_OVS_PRI ");
		sb.append(" SET STATUS = '1', MODIFIER = :loginID, LASTUPDATE = SYSDATE ");
		sb.append(" WHERE PRD_SEQ_NO = :seqNo AND STATUS = '0' ");
		queryCondition.setObject("seqNo", inputVO.getSEQ_NO());
		queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(return_VO);
	}
}
