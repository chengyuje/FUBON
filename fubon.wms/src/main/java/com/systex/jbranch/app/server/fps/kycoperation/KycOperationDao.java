package com.systex.jbranch.app.server.fps.kycoperation;

import com.systex.jbranch.app.common.fps.table.TBKYC_LOG;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component("KycOperationDao")
@Scope("request")
public class KycOperationDao extends FubonWmsBizLogic {

    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryLastMastDataNotWebStatus01(String custId) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append(" select SEQ , CREATE_DATE from TBKYC_INVESTOREXAM_M ")
			.append(" where CUST_ID = :custid and STATUS = '01' and INVEST_BRANCH_NBR != '999' ").toString())
			.setObject("custid", custId));
    }

    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryLastData(String custId) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append(" select * from TBKYC_INVESTOREXAM_M where CUST_ID = :custid ").toString())
			.setObject("custid", custId));
    }

    /**等待主管覆核資料：抓待啟用的KYC主檔 + KYC明細檔  + 客戶主檔**/
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> qeuryWaitReviewMastDetail(String custId) throws DAOException, JBranchException{
    	return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
    	.append(" select ")
    	.append(" 	a.SEQ, ")
    	.append(" 	a.CUST_NAME, ")
    	.append(" 	a.CUST_ID, ")
    	.append(" 	a.EXAM_VERSION, ")
    	.append(" 	a.CREATE_DATE as KYC_TEST_DATE, ")
    	.append(" 	c.GENDER, ")
    	.append(" 	b.CUST_EDUCTION_AFTER as EDUCATION, ")
    	.append(" 	b.CUST_CAREER_AFTER as CAREER, ")
    	.append(" 	b.CUST_MARRIAGE_AFTER as MARRAGE, ")
    	.append(" 	b.CUST_CHILDREN_AFTER as CHILD_NO, ")
    	.append(" 	b.CUST_HEALTH_AFTER as SICK_TYPE, ")
    	.append(" 	b.ANSWER_2, ")
    	.append(" 	a.CUST_RISK_AFR, ")
    	.append(" 	c.BRA_NBR, ")
    	.append(" 	c.RPRS_NAME, ")
    	.append(" 	c.RPRS_ID, ")
    	.append(" 	b.CUST_TEL, ")
    	.append(" 	b.CUST_EMAIL as EMAIL_ADDR, ")
    	.append(" 	b.CUST_ADDRESS as CUST_ADDR_1, ")
    	.append(" 	b.UPDATE_YN    ")
    	.append(" from TBKYC_INVESTOREXAM_M a, TBKYC_INVESTOREXAM_D b, TBCRM_CUST_MAST c ")
    	.append(" where a.SEQ = b.SEQ   ")
    	.append(" and a.CUST_ID = b.CUST_ID ")
    	.append(" and a.CUST_ID = c.CUST_ID ")
    	.append(" and a.CUST_ID = :cust_id ")
    	.append(" and a.STATUS = '01' ")
    		.toString())
    		.setObject("cust_id", custId)
    	);
    }

    /**取問卷資料、問卷題目：找出最新待啟用的問卷，並以客戶id對應是法人還是自然人問卷**/
    @SuppressWarnings({ "unchecked"})
    public List<Map<String, Object>> queryQuestion(String custId) throws DAOException, JBranchException{
	    return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
		    .append(" SELECT  ")
		    .append(" 	n.EXAM_VERSION, ")		//問卷版本
		    .append(" 	n.QUESTION_VERSION, ")	//題目序號
		    .append(" 	n.EXAM_NAME, ")			//問卷名稱
		    .append(" 	n.QUEST_TYPE,  ")		//問卷類型 ：02自然人、03法人
		    .append(" 	n.QST_NO, ")			//自訂題號
		    .append(" 	n.ESSENTIAL_FLAG, ")	//是否為必需輸入：Y、N
		    .append(" 	n.RL_VERSION, ")		//風險級距編號
		    .append(" 	n.RS_VERSION, ")		//降等機制矩陣版號
		    .append(" 	n.RLR_VERSION, ")		//可承受風險損失率版號
		    .append(" 	n.STATUS, ")			//狀態：01待審核、02待啟用
		    .append(" 	n.ACTIVE_DATE,  ")		//參數預計待啟用日期
		    .append(" 	n.SCORE_TYPE,  ")		//計分類別 C/W
		    .append(" 	n.INT_SCORE,  ")		//截距分
		    .append(" 	Q.QUESTION_DESC, ")		//題目名稱
		    .append(" 	Q.QUESTION_TYPE, ")		//題目類型：S：單選選單、M：複選答案、N：數字格式、T：文字格式
		    .append(" 	Q.ANS_OTHER_FLAG, ")	//答案是否有其他：Y、N
		    .append(" 	Q.ANS_MEMO_FLAG, ")		//是否有補充說明
		    .append(" 	Q.DEFINITION, ")		//外部使用定義
		    .append(" 	Q.PICTURE  ")			//圖檔之參考文件代碼
		    //問卷資料檔、題庫題目檔
		    .append(" from TBSYS_QUESTIONNAIRE n , TBSYS_QST_QUESTION Q  ")
		    //待啟用
		    .append(" where n.STATUS = '02'  ")
		    //題目序號對應
		    .append(" AND n.QUESTION_VERSION = Q.QUESTION_VERSION  ")
		    //問卷類型
		    .append(" and n.QUEST_TYPE = :quest_type  ")
		    //找出待啟用日其小於等於系統日且最接近的那筆
		    .append(" AND ACTIVE_DATE = ( ")
		    .append(" 	SELECT MAX(ACTIVE_DATE) FROM TBSYS_QUESTIONNAIRE WHERE STATUS = '02'  ")
		    .append(" 	AND QUEST_TYPE = :quest_type AND ACTIVE_DATE <= SYSDATE ")
		    .append(" ) order by QST_NO  ")
//		    .append(" AND n.EXAM_VERSION = 'KYC202003097811' order by QST_NO ")
		    .toString())
		    //問卷類型
			.setObject("quest_type", custId.length() >= 10 ? "02" : "03")
		);
    }

    /**取得指定問卷版本及指定題目的所有答案及分數**/
    @SuppressWarnings({ "unchecked"})
    public List<Map<String, Object>> queryAnswer(String examVersion , String questionVersion) throws DAOException, JBranchException{
	    return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
		    .append(" SELECT  ")
		    .append(" 	A.QUESTION_VERSION, ")	//題目序號
		    .append(" 	A.ANSWER_SEQ, ")		//答案序號
		    .append(" 	A.ANSWER_DESC, ")		//答案的中文內容
		    .append(" 	W.FRACTION  ")			//分數
		    //題庫題目答案列表檔、答案權重檔
		    .append(" FROM TBSYS_QST_ANSWER A , TBKYC_QUESTIONNAIRE_ANS_WEIGHT W  ")
		    //題目序號
		    .append(" WHERE A.QUESTION_VERSION = W.QUESTION_VERSION  ")
		    //答案序號
		    .append(" AND A.ANSWER_SEQ = W.ANSWER_SEQ  ")
		    //指定問卷版本
		    .append(" AND W.EXAM_VERSION = :exam_version  ")
		    //指定題目序號
		    .append(" AND A.QUESTION_VERSION = :question_version  ")
	    	.toString())
			.setObject("exam_version", examVersion)//指定問卷版本
			.setObject("question_version", questionVersion)//指定題目版次
		);
    }

    /**用風險級距編號去抓風險級距**/
    @SuppressWarnings({ "unchecked"})
    public List<Map<String, Object>> queryLevel(String rlVersion) throws DAOException, JBranchException{

	    return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
	    	.append(" SELECT ")
	    	.append(" 	CUST_RL_ID, ")	  //級距代碼
	    	.append(" 	RL_NAME, ")		  //級距名稱
	    	.append(" 	RL_UP_RATE, ")	  //風險比例上限
	    	.append(" 	PROD_RL_UP_RATE ")//商品風險上限
	    	//風險級距檔
	    	.append(" FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL N  ")
	    	//風險級距編號
	    	.append(" WHERE N.RL_VERSION = :rl_version  ")
	    	.toString())
			.setObject("rl_version", rlVersion)
		);
    }

    /**查當天是否有刪除的紀錄**/
	@SuppressWarnings({ "unchecked"})
	public List<Map<String, Object>> queryMastHisDel(String custId) throws DAOException, JBranchException{
	    return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
	    	.append(" SELECT COUNT(*) CNT FROM TBKYC_INVESTOREXAM_M_HIS ")
	    	.append(" WHERE TRUNC(CREATETIME) = TRUNC(SYSDATE) ")
	    	.append(" AND CUST_ID = :cust_id AND STATUS = '04' ")
	    	.toString())
			.setObject("cust_id", custId)
		);
    }

	@SuppressWarnings({ "unchecked"})
	public List<Map<String, Object>> queryLastRiskLevel(String custId) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append(" select SEQ, to_char(A.create_date,'YYYY-MM-DD HH24:MI:SS') as LastDate, A.CUST_RISK_AFR, B.RL_NAME ")
			.append(" from TBKYC_INVESTOREXAM_M A,TBKYC_QUESTIONNAIRE_RISK_LEVEL B ")
			.append(" WHERE CUST_RISK_AFR = B.CUST_RL_ID ")
			.append(" AND  A.STATUS = '03' ")
			.append(" AND A.CUST_ID = :custid ")
			.toString())
			.setObject("custid", custId)
		);
    }

	public void updateLastWaitForWeb(String seq , String custId) throws DAOException, JBranchException{
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append(" Update TBKYC_INVESTOREXAM_M a set STATUS = '03', SIGNOFF_ID = '999', MODIFIER = '已重做KYC', LASTUPDATE = sysdate , ")
			.append(" DIFF_DAYS = ( ")
			.append(" 	select ROUND(to_number(to_date(to_char(a.CREATE_DATE  , 'yyyyMMdd') , 'yyyyMMdd') - to_date(to_char(b.CREATE_DATE  , 'yyyyMMdd') , 'yyyyMMdd'))) from TBKYC_INVESTOREXAM_M_HIS b ")
			.append(" 	where cust_id = :custId and seq <> :seq and status = '03' order by CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY ")
			.append(" ) ")
			.append(" where SEQ = :seq ").toString())
			.setObject("seq", seq)
			.setObject("custId", custId));
	}

	@SuppressWarnings("unchecked")
	public Integer getCustNumber() throws JBranchException{
		Integer custNumber = 0;

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> custNumberMap = xmlInfo.doGetVariable("KYC.BASE_SCORE", FormatHelper.FORMAT_2);//自然人預設分數126
		Iterator<String> keyIter = custNumberMap.keySet().iterator();
		if(keyIter.hasNext())
			custNumber = Integer.parseInt(custNumberMap.get(keyIter.next()).toString());

		return custNumber;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryCustMastData(String custId) throws DAOException, JBranchException{
		String queryStr = " SELECT CUST_NAME , GENDER , CUST_ID , BRA_NBR FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ";
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("cust_id", custId));
	}

	/**將上一次問卷寫入主檔歷程**/
	public void insertHisForMast(String custId) throws DAOException, JBranchException{
		String queryStr = " Insert into TBKYC_INVESTOREXAM_M_HIS Select * from TBKYC_INVESTOREXAM_M Where cust_id = :cust_id ";
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("cust_id", custId));
	}

	/**將上一次問卷寫入明細歷程**/
	public void insertHisForDetail(String custId) throws DAOException, JBranchException{
		String queryStr = " Insert into TBKYC_INVESTOREXAM_D_HIS Select * from TBKYC_INVESTOREXAM_D Where cust_id = :cust_id";
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("cust_id", custId));
	}

	@SuppressWarnings("unchecked")
	public List<Map<String , Object>> queryLastMastHisForStatus03(String custId) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append(" select CREATE_DATE , CUST_RISK_AFR, TRUNC(ADD_MONTHS(CREATE_DATE, 6)) as TESTDATE_ADD6MON, SEQ from TBKYC_INVESTOREXAM_M_HIS ")
			.append(" where cust_id = :custId and status = '03' order by CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY ").toString())
			.setObject("custId" , custId));
	}

	/**刪除明細**/
	public void deleteDetail(String custId) throws DAOException, JBranchException{
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" DELETE TBKYC_INVESTOREXAM_D WHERE CUST_ID = :custid ")
			.setObject("custid", custId));
	}

	/**刪除主檔 **/
	public void deletelMast(String custId) throws DAOException, JBranchException{
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" DELETE TBKYC_INVESTOREXAM_M WHERE CUST_ID = :custid ")
			.setObject("custid", custId));
	}

	@SuppressWarnings("unchecked")
	public List<Map<String , Object>> queryAoBranch(String custId) throws DAOException, JBranchException{
		String queryStr = " SELECT AO_CODE,BRA_NBR FROM TBCRM_CUST_MAST WHERE CUST_ID =:custid ";
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("custid", custId));
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryDetail(String custId) throws DAOException, JBranchException{
		String queryStr = " SELECT * FROM TBKYC_INVESTOREXAM_D WHERE CUST_ID = :custid ";
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("custid", custId));
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryMast(String custId) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append(" SELECT * FROM TBKYC_INVESTOREXAM_M WHERE CUST_ID = :custid ").toString())
			.setObject("custid", custId));
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryRiskName(String examVersion , String cValueRisk) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append("SELECT DISTINCT L.RL_NAME FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL L LEFT JOIN TBSYS_QUESTIONNAIRE A ON A.RL_VERSION = L.RL_VERSION ")
			.append("WHERE A.EXAM_VERSION = :exam_version and CUST_RL_ID = :risk ").toString())
			.setObject("exam_version", examVersion)
			.setObject("risk", cValueRisk));
	}


	public void updateToCrmCustMast(String custId , Timestamp expiryDate , String custRiskAtr) throws DAOException, JBranchException{
		exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(
			"update TBCRM_CUST_MAST set CUST_RISK_ATR = :CUST_RISK_ATR , KYC_DUE_DATE = :KYC_DUE_DATE where CUST_ID = :CUST_ID ")
			.setObject("CUST_ID", custId)
			.setObject("KYC_DUE_DATE", expiryDate)
			.setObject("CUST_RISK_ATR", custRiskAtr)
		);
	}



	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryReSendDataFor390(List<String> branch_list) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
			.append(" select  ")
			.append("	MHIS.cust_id CUST_ID , ")
			//本次風險屬性
			.append("   CASE WHEN MHIS.CUST_RISK_AFR IS NULL THEN '  ' ELSE MHIS.CUST_RISK_AFR END CUST_RISK_AFR, ")
			//承作日期
			.append("   CASE WHEN MHIS.CREATETIME IS NOT NULL THEN  TO_CHAR(MHIS.CREATETIME , 'YYYYMMDD') ELSE TO_CHAR(SYSDATE , 'YYYYMMDD') END KYC_TEST_DATE,  ")
			//有效月份
			.append("   CASE WHEN MHIS.EXPIRY_DATE IS NOT NULL THEN LPAD(TO_CHAR(MHIS.EXPIRY_DATE , 'MM') , 8 , '0') ELSE '00000000' END EXPIRY_DATE,  ")
			//承作分行
			.append("   MHIS.INVEST_BRANCH_NBR , ")
			//承作員編
			.append("   CASE WHEN MHIS.CREATOR IS NULL THEN '      ' ELSE MHIS.CREATOR  END EMP_ID,  ")
			//學歷
			.append("   CASE WHEN DHIS.CUST_EDUCTION_AFTER IS NULL THEN ' ' ELSE (select param_name from TBSYSPARAMETER WHERE PARAM_TYPE = 'KYC.EDUCATION_MAPPING' and param_code = DHIS.CUST_EDUCTION_AFTER) END CUST_EDUCTION_AFTER ,  ")
			//婚姻
			.append("   CASE WHEN DHIS.CUST_MARRIAGE_AFTER IS NOT NULL THEN DHIS.CUST_MARRIAGE_AFTER  ELSE ' ' END CUST_MARRIAGE_AFTER, ")
			//子女數
			.append("   CASE WHEN DHIS.CUST_CHILDREN_AFTER = '0' THEN '5' WHEN DHIS.CUST_CHILDREN_AFTER IS NOT NULL THEN DHIS.CUST_CHILDREN_AFTER ELSE ' ' END CUST_CHILDREN_AFTER, ")
			//職業
			.append("   CASE WHEN DHIS.CUST_CAREER_AFTER IS NOT NULL THEN LPAD(DHIS.CUST_CAREER_AFTER ,2 ,' ') ELSE '  ' END CUST_CAREER_AFTER,  ")
			//重大疾病
			.append("   CASE WHEN DHIS.CUST_HEALTH_AFTER = '1' THEN 'Y' ELSE ' ' END SICK_TYPE_YN, ")
			//重大傷病等級
			.append("   CASE WHEN DHIS.CUST_HEALTH_AFTER IS NOT NULL THEN DHIS.CUST_HEALTH_AFTER ELSE ' ' END CUST_HEALTH_AFTER ")
			.append(" from TBKYC_INVESTOREXAM_M_HIS MHIS ")
			.append(" LEFT JOIN TBKYC_INVESTOREXAM_D_HIS DHIS ")
			.append(" ON MHIS.SEQ = DHIS.SEQ ")
			.append(" where MHIS.cust_id in(:custIds)  ")
			.append(" AND MHIS.SEQ = ( ")
			.append(" 	select SEQ from TBKYC_INVESTOREXAM_M_HIS B ")
			.append("   WHERE B.CUST_ID = MHIS.CUST_ID  ")
			.append("   AND status = '03'  ")
			.append("   order by CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY  ")
			.append(" ) ")
			.toString())
			.setObject("custIds", branch_list));
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> qeuryCustMastForCustId(String custId) throws DAOException, JBranchException{
		return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF()
			.setQueryString(" SELECT 'x' from TBCRM_CUST_MAST WHERE CUST_ID =:cust_id ")
			.setObject("cust_id", custId.toUpperCase()));
	}

	@SuppressWarnings("unchecked")
	public Map<String , String> findReSendOpenKey() throws JBranchException{
		Map<String, String> custNumberMap = new XmlInfo().doGetVariable("KYC.KYC_RE_SEND_390_FLAG", FormatHelper.FORMAT_3);
		return custNumberMap;
	}

	//獨立的交易不受其他影響(獨立交易)
	@Transactional(propagation = Propagation.REQUIRES_NEW , rollbackFor = {Exception.class})
	public TBKYC_LOG insertKycLog(TBKYC_LOG tbkycLog) throws DAOException, JBranchException{
		return (TBKYC_LOG)getDataAccessManager().create(tbkycLog);
	}

	//獨立的交易不受其他影響(獨立交易)
	@Transactional(propagation = Propagation.REQUIRES_NEW , rollbackFor = {Exception.class})
	public void updateKycLog(TBKYC_LOG tbkycLog) throws DAOException, JBranchException{
		 getDataAccessManager().update(tbkycLog);
	}

	public void updateCreatorMofifer(List<String> tableNames , String empId , String seq) throws DAOException, JBranchException{
		for(String tableName : tableNames){
			getDataAccessManager().exeUpdate(genDefaultQueryConditionIF()
				.setQueryString("update " + tableName + " set creator = :empId , modifier = :empId where seq = :seq " )
				.setObject("empId", empId)
				.setObject("seq", seq));
		}
	}

	/**
	 * 取得本日已做KYC次數
	 * 臨櫃/網銀/行銀/奈米投，合計一日限1次
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public BigDecimal getKycCounts(String custId) throws DAOException, JBranchException{
		List<Map<String, BigDecimal>> data = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
				.append(" SELECT COUNT(*) CNT FROM TBKYC_INVESTOREXAM_M ")
				.append(" WHERE TRUNC(CREATETIME) = TRUNC(SYSDATE) ")
				.append(" AND CUST_ID = :cust_id AND STATUS = '03' ")
				.toString())
				.setObject("cust_id", custId)
		);

		return data.get(0).get("CNT");
    }

    /** 取得承作日（包含）之最近 7 個日曆日內 kyc 次數 **/
    public BigDecimal getKycWeekCounts(String custId) throws JBranchException {
    	List<Map<String, BigDecimal>> data = Manager.manage(getDataAccessManager())
				.append("select count(1) CNT from TBKYC_INVESTOREXAM_M_HIS ")
				.append("where TRUNC(CREATETIME) >= TRUNC(SYSDATE-6) ")
				.append("and CUST_ID = :custId and STATUS = '03' and INVEST_BRANCH_NBR = '999' ")
				.put("custId", custId)
				.query();
    	return data.get(0).get("CNT");
	}

	/**
	 * 檢查客戶是否在冷靜期中
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> chkCoolingPeriod(String custId) throws DAOException, JBranchException{
	    return exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
	    	.append(" SELECT TO_CHAR(EFFECTIVE_DATE, 'YYYYMMDD') AS EFFECTIVE_DATE FROM TBKYC_COOLING_PERIOD ")
	    	.append(" WHERE CUST_ID = :cust_id AND STATUS = 'C' ")
	    	.toString())
			.setObject("cust_id", custId)
		);
    }

	/***
	 * 取得風險級距矩陣C值
	 * @param rsVersion
	 * @param scoreC
	 * @param scoreW
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public String getCRRMatrixLevel(String rsVersion, float scoreC, float scoreW) throws JBranchException {
		List<Map<String, Object>> crrList =  exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
	    	.append(" SELECT CUST_RL_ID FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ")
	    	.append(" WHERE (C_SCORE_BTN <= :scoreC AND :scoreC < C_SCORE_TOP) ")
	    	.append(" 	AND (W_SCORE_BTN <= :scoreW AND :scoreW < W_SCORE_TOP) ")
	    	.append(" 	AND RS_VERSION = :rs_version ")
	    	.toString())
			.setObject("rs_version", rsVersion)
			.setObject("scoreC", scoreC)
			.setObject("scoreW", scoreW)
		);

		if(CollectionUtils.isNotEmpty(crrList))
			return ObjectUtils.toString(crrList.get(0).get("CUST_RL_ID"));
		else
			return "";
	}

	/***
	 * 取得可承受風險損失率對應C值
	 * @param rlrVersion
	 * @param lossRate
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public String getRiskLossLevel(String rlrVersion, float lossRate) throws JBranchException {
		List<Map<String, Object>> rList = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
	    	.append(" SELECT CUST_RL_ID FROM TBKYC_QUESTIONNAIRE_RISK_LRATE ")
	    	.append(" WHERE :lossRate <= RL_UP_RATE AND RLR_VERSION = :rlr_version ORDER BY CUST_RL_ID ")
	    	.toString())
			.setObject("rlr_version", rlrVersion)
			.setObject("lossRate", lossRate)
		);

		if(CollectionUtils.isNotEmpty(rList))
			return ObjectUtils.toString(rList.get(0).get("CUST_RL_ID"));
		else
			return "";
	}

	/***
	 * CRR_RULE: true=> 總分與矩陣取低者為C值; false=> 以原始總分取得C值對應
	 * @return
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public Boolean getCrrRule() throws JBranchException {
		String crrRule = "";
		XmlInfo xmlInfo = new XmlInfo();

		Map<String, String> ruleMap = xmlInfo.doGetVariable("KYC.CRR_RULE", FormatHelper.FORMAT_3);
		Iterator<String> keyIter = ruleMap.keySet().iterator();
		if(keyIter.hasNext())
			crrRule = ruleMap.get(keyIter.next()).toString();

		return StringUtils.equals("Y", crrRule);
	}

	public Map<String, String> getKycExceptionMap(String key) throws JBranchException {
		List<Map<String, String>> result =
				Manager.manage(getDataAccessManager())
				.append("select PARAM_CODE, PARAM_NAME ")
				.append("from TBSYSPARAMETER ")
				.append("where PARAM_TYPE = 'KYC.WS_EXCEPTION' ")
				.append("and PARAM_CODE = :key ")
				.put("key", key)
				.query();
		return result.get(0);
	}

	/** 該客戶 recordRisk 服務（非試算時），目前的執行狀態是否為 P（Pending: 作業中） **/
	public String recordRiskIsPending(String custId) throws JBranchException {
		List<Map<String, String>> result =
                Manager.manage(getDataAccessManager())
                        .append("select STATUS ")
                        .append("from TBKYC_LOG ")
                        .append("where CUST_ID = :custId ")
                        .append("and CONFIRM = '1' ")
                        .append("and METHOD = 'recordRisk' ")
                        .append("and STATUS = 'P' ")
                        .append("order by SEQ desc ")
                        .append("fetch first 1 rows only ")
                        .put("custId", custId)
                        .query();
		if (result.isEmpty()) return "N";
		return "P".equals(result.get(0).get("STATUS"))? "Y": "N";
	}

	/*** 客戶臨櫃已做KYC且待覆核，若是:Y 否則:N **/
	public String inReviewStatus(String custId) throws JBranchException {
		List<Map<String, String>> result =
                Manager.manage(getDataAccessManager())
                        .append("Select 1 ")
                        .append("from TBKYC_INVESTOREXAM_M ")
                        .append("where CUST_ID = :custId ")
                        .append("and STATUS = '01' ")
                        .put("custId", custId)
                        .query();
		
		return CollectionUtils.isNotEmpty(result) ? "Y": "N";
	}
	
	/** 判斷該客戶是否重複呼叫 recordRisk 服務 **/
	public boolean recordRiskIsDuplicatedOperation(String custId) throws JBranchException {
		List<Map<String, String>> result =
				Manager.manage(getDataAccessManager())
						.append("select count(1) CNT ")
						.append("from TBKYC_LOG ")
						.append("where CUST_ID = :custId ")
						.append("  and CONFIRM = '1' ")
						.append("  and METHOD = 'recordRisk' ")
						.append("  and STATUS = 'P' ")
						.put("custId", custId)
						.query();
		BigDecimal count = new BigDecimal(ObjectUtils.toString(result.get(0).get("CNT")));
		// 該次呼叫也會新增到 TBKYC_LOG，所以當次數 > 1 時代表重複承作
		return count.compareTo(new BigDecimal(1)) > 0;
	}

	/**
	 * 判斷該客戶是否「超過可承作次數」。當天最多承作 1 次，一周最多承作 3 次
	 * 臨櫃/網銀/行銀/奈米投，合計一日限1次
	 * 網銀/行銀/奈米投，合計七日限3次
	 * @param custId
	 * @return
	 * @throws JBranchException
	 */
	public boolean isOverLimitationToRiskRecord(String custId) throws JBranchException {
		return getKycCounts(custId).compareTo(new BigDecimal(1)) >= 0 ||
				getKycWeekCounts(custId).compareTo(new BigDecimal(2)) > 0;
	}
}