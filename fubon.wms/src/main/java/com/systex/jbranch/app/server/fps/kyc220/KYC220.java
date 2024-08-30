package com.systex.jbranch.app.server.fps.kyc220;

import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBKYC_QUESTIONNAIRE_FLW_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBER_ROLEVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component("kyc220")
@Scope("request")
public class KYC220 extends FubonWmsBizLogic {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 查詢
     *
     * @param body
     * @param header
     * @throws APException 
     */
    public void inquire(Object body, IPrimitiveMap<Object> header) throws APException{
        KYC220InputVO inputVO = (KYC220InputVO) body;
        KYC220OutputVO outputVO = new KYC220OutputVO();

//        Calendar approveStartDate = inputVO.getApproveStartDate(); //主管建立日期(起)
//        Calendar approveEndDate   = inputVO.getApproveEndDate();   //主管建立日期(迄)
//        Calendar submitStartDate  = inputVO.getSubmitStartDate();  //經辦提交日期(起)
//        Calendar submitEndDate    = inputVO.getSubmitEndDate();    //經辦提交日期(迄)
        String   aoCode           = inputVO.getAoCode();           //經辦員編
        String   quesName         = inputVO.getQuesName();         //參數名稱
        String   approveStatus    = inputVO.getApproveStatus();    //簽核狀態

        try{
            
            QueryConditionIF condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

            Map params = new HashMap<>();
            StringBuffer sql = new StringBuffer()
                    .append(" SELECT DISTINCT A.EXAM_VERSION,A.EXAM_NAME,A.MODIFIER,C.EMP_NAME,trunc(A.CREATETIME) as LASTUPDATE,A.QUEST_TYPE,A.ACTIVE_DATE, ")
                    .append(" B.SIGNOFF_ID,B.SIGNOFF_NAME,B.CREATETIME,A.STATUS, A.INT_SCORE  ")
                    .append(" FROM TBSYS_QUESTIONNAIRE A,TBKYC_QUESTIONNAIRE_FLW_DETAIL B ,VWORG_BRANCH_EMP_DETAIL_INFO C ")
                    .append(" where A.EXAM_VERSION = B.EXAM_VERSION(+) AND A.MODIFIER = C.EMP_ID(+) AND A.QUEST_TYPE IN ('02','03') ");

            if(inputVO.getApproveStartDate() != null && inputVO.getApproveEndDate() == null){
                sql.append("and TRUNC(B.CREATETIME) >= :approveStartDate ");
                params.put("approveStartDate", inputVO.getApproveStartDate());
            }
            if(inputVO.getApproveStartDate() == null && inputVO.getApproveEndDate() != null){
                sql.append("and TRUNC(B.CREATETIME) <= :approveEndDAte ");
                params.put("approveEndDAte", inputVO.getApproveEndDate());
            }
            if(inputVO.getApproveStartDate() != null && inputVO.getApproveEndDate() != null){
                sql.append("and TRUNC(B.CREATETIME) >= :approveStartDate and TRUNC(B.CREATETIME) <= :approveEndDAte ");
                params.put("approveStartDate", inputVO.getApproveStartDate());
                params.put("approveEndDAte", inputVO.getApproveEndDate());
            }

            if(inputVO.getSubmitStartDate() != null && inputVO.getSubmitEndDate() == null){
                sql.append("and TRUNC(A.CREATETIME) >= :submitStartDate ");
                params.put("submitStartDate", inputVO.getSubmitStartDate());
            }
            if(inputVO.getSubmitStartDate() == null && inputVO.getSubmitEndDate() != null){
                sql.append("and TRUNC(A.CREATETIME) <= :submitEndDate ");
                params.put("submitEndDate", inputVO.getSubmitEndDate());
            }
            if(inputVO.getSubmitStartDate() != null && inputVO.getSubmitEndDate() != null){
                sql.append("and TRUNC(A.CREATETIME) >= :submitStartDate and TRUNC(A.CREATETIME) <= :submitEndDate ");
                params.put("submitStartDate", inputVO.getSubmitStartDate());
                params.put("submitEndDate", inputVO.getSubmitEndDate());
            }

            if(StringUtils.isNotBlank(aoCode)){
                sql.append("and A.MODIFIER = :aoCode ");
                params.put("aoCode", aoCode);
            }

            if(StringUtils.isNotBlank(quesName)){
                sql.append("and A.EXAM_NAME like :quesName ");
                params.put("quesName", "%"+quesName+"%");
            }

            if(StringUtils.isNotBlank(approveStatus)){
                sql.append("and A.STATUS = :approveStatus ");
                params.put("approveStatus", approveStatus);
            }

            //Group by
//            sql.append("group by A.EXAM_NAME, ")
//               .append("A.STATUS, A.CREATOR, A.EXAM_VERSION, ")
//               .append("B.CREATETIME, B.SIGNOFF_ID, ")
//               .append("A.QUEST_TYPE, A.RL_VERSION ");
            
            
            //order by
            sql.append(" order by A.ACTIVE_DATE DESC ");
            
            
            //set params
            Iterator itor = params.entrySet().iterator();
            while(itor.hasNext()){
                Map.Entry entry = (Map.Entry) itor.next();
                condition.setObject((String) entry.getKey(), entry.getValue());
            }

            condition.setQueryString(sql.toString());
//            outputVO.setResultList(getDataAccessManager().exeQuery(condition));

            ResultIF list = getDataAccessManager().executePaging(condition, inputVO
                    .getCurrentPageIndex() + 1, inputVO.getPageCount());

            if (list.size() > 0) {
                int totalPage = list.getTotalPage();
                outputVO.setTotalPage(totalPage);
                outputVO.setResultList(list);
                outputVO.setTotalRecord(list.getTotalRecord());

                outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
                sendRtnObject(outputVO);
            } else {
            	sendRtnObject(outputVO);
//                throw new APException("ehl_01_common_009");
            }
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (JBranchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查詢問卷內容
     *
     * @param body
     * @param header
     */
    public void checkInquire(Object body, IPrimitiveMap<Object> header){
        KYC220InputVO inputVO = (KYC220InputVO) body;
        KYC220OutputVO outputVO = new KYC220OutputVO();

        String examVersion      = inputVO.getExamVersion();     //問卷版本
        String questionVersion  = null;                         //問題序號
//        String rlVersion        = inputVO.getRlVersion();       //風險級距編號
        String empID            = inputVO.getEmpID();           //員編
        try{
            QueryConditionIF condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
            //問卷內容
            StringBuffer sql = new StringBuffer()
                    .append(" select ")
                    .append(" QUE.QUESTION_VERSION, ")       //題目版次
                    .append(" QUE.QST_NO, ")                 //自訂題號
                    .append(" case QST.QUESTION_TYPE")       //題目類型
                    .append(" when 'S' then '單選選單' ")
                    .append(" when 'M' then '複選選單' ")
                    .append(" when 'N' then '數字格式' ")
                    .append(" when 'T' then '文字格式' ")
                    .append(" end QUESTION_TYPE, ")
                    .append(" QST.QUESTION_DESC, ")          //題目名稱
                    .append(" QST.ANS_MEMO_FLAG, ")          //是否有補充說明
                    .append(" QUE.ESSENTIAL_FLAG, ")         //是否必須輸入
                    .append(" QUE.STATUS, ")                  //狀態 (03:已啟用)
                    .append(" QUE.RL_VERSION, ")
                    .append(" QUE.RLR_VERSION, ")
                    .append(" QUE.RS_VERSION, ")
                    .append(" QUE.QST_WEIGHT, ")
                    .append(" QUE.SCORE_TYPE ")
                    .append(" from TBSYS_QUESTIONNAIRE QUE, ")
                    .append(" TBSYS_QST_QUESTION QST ")
                    .append(" where QUE.QUESTION_VERSION = QST.QUESTION_VERSION ")
                    .append(" and QUE.EXAM_VERSION = ? ")
            		.append(" order by QUE.QST_NO ");

            condition.setString(1, examVersion);
            condition.setQueryString(sql.toString());

            List<Map> queryList = getDataAccessManager().exeQuery(condition);
            String rlVersion = queryList.get(0).get("RL_VERSION").toString();
//            ResultIF list = getDataAccessManager().executePaging(condition, inputVO
//                    .getCurrentPageIndex() + 1, inputVO.getPageCount());

//            Map result = queryList.get(0);
//            questionVersion = (result.size()>0) ? (String) result.get("QUESTION_VERSION") : "";
            if(!queryList.isEmpty()) {
            	List resultList = new ArrayList();
                for(Map<String, Object> map:queryList){
               	 //選項/權重
               	condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
               	sql = new StringBuffer()
                   .append("select ")
                   .append("ANS.QUESTION_VERSION, ")
                   .append("ANS.ANSWER_DESC, WEI.FRACTION ")
                   .append("from TBSYS_QST_ANSWER ANS, ")
                   .append("TBKYC_QUESTIONNAIRE_ANS_WEIGHT WEI ")
                   .append("where ANS.QUESTION_VERSION = WEI.QUESTION_VERSION ")
                   .append("and ANS.ANSWER_SEQ = WEI.ANSWER_SEQ ")
                   .append("and ANS.QUESTION_VERSION = :QUESTION_VERSION ")
                   .append(" and WEI.EXAM_VERSION = :exam_version ");
   		        condition.setObject("QUESTION_VERSION", map.get("QUESTION_VERSION").toString());
   		        condition.setObject("exam_version", examVersion);
   		        condition.setQueryString(sql.toString());
   		
   		        List<Map> fractionList = getDataAccessManager().exeQuery(condition);
   		        Map resMap = new HashMap();
   		        resMap.putAll(map);
   		        resMap.put("FRACTIONS", fractionList);
   		        resultList.add(resMap);
   		        
               }

                //風險級距
                condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
                sql = new StringBuffer()
                        .append("select ")
                        .append("CUST_RL_ID, ")     //級距代碼
                        .append("RL_NAME, ")        //級距名稱
                        .append("RL_UP_RATE, ")     //風險比例上限
                        .append("PROD_RL_UP_RATE ") //商品風險上限
                        .append("from TBKYC_QUESTIONNAIRE_RISK_LEVEL ")
                        .append("where 1 = 1 ")
                        .append("and RL_VERSION = :RL_VERSION ");

                condition.setObject("RL_VERSION", rlVersion);
                condition.setQueryString(sql.toString());

                List<Map> rlVersionList = getDataAccessManager().exeQuery(condition);

                //取得可承受風險損失率
                condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
                sql = new StringBuffer();
                sql.append(" select CUST_RL_ID as LRATE_CUST_RL_ID, RL_NAME as LRATE_RL_NAME, RL_UP_RATE as LRATE_RL_UP_RATE ");
                sql.append(" from TBKYC_QUESTIONNAIRE_RISK_LRATE ");
                sql.append(" where RLR_VERSION =  :rlr_version");
                sql.append(" order by CUST_RL_ID ");
                condition.setObject("rlr_version", queryList.get(0).get("RLR_VERSION").toString());
                condition.setQueryString(sql.toString());
                List<Map> rlrVersionList = getDataAccessManager().exeQuery(condition);				
				
				//取得風險承受能力
                condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
                sql = new StringBuffer();
                sql.append(" SELECT DISTINCT C_VAL ");
                sql.append(" FROM ( ");
                sql.append("       SELECT DISTINCT C_SCORE_BTN AS C_VAL ");
                sql.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
                sql.append("       WHERE RS_VERSION = :rs_version ");
                sql.append("       UNION ALL ");
                sql.append("       SELECT DISTINCT C_SCORE_TOP AS C_VAL "); 
                sql.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
                sql.append("       WHERE RS_VERSION = :rs_version ) ");
                sql.append(" ORDER BY C_VAL ASC ");
				condition.setObject("rs_version", queryList.get(0).get("RS_VERSION").toString());
				condition.setQueryString(sql.toString());
				List<Map> cValList = getDataAccessManager().exeQuery(condition);
				
				condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append(" SELECT DISTINCT W_VAL ");
				sql.append(" FROM ( ");
				sql.append("       SELECT DISTINCT W_SCORE_BTN AS W_VAL ");
				sql.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
				sql.append("       WHERE RS_VERSION = :rs_version ");
				sql.append("       UNION ALL ");
				sql.append("       SELECT DISTINCT W_SCORE_TOP AS W_VAL "); 
				sql.append("       FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
				sql.append("       WHERE RS_VERSION = :rs_version ) ");
				sql.append(" ORDER BY W_VAL ASC ");
				condition.setObject("rs_version", queryList.get(0).get("RS_VERSION").toString());
				condition.setQueryString(sql.toString());
				List<Map> wValList = getDataAccessManager().exeQuery(condition);
				
				condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append(" SELECT C_LEVEL,W_LEVEL,CUST_RL_ID ");
				sql.append(" FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
				sql.append(" WHERE RS_VERSION = :rs_version ");
				sql.append(" ORDER BY W_LEVEL,C_LEVEL ASC");
				condition.setObject("rs_version", queryList.get(0).get("RS_VERSION").toString());
				condition.setQueryString(sql.toString());
				List<Map> cwRLList = getDataAccessManager().exeQuery(condition);
				
				
                //說明備註
                condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
                sql = new StringBuffer()
                        .append("select ")
                        .append("SIGNOFF_NAME, ") //簽核員工
                        .append("CREATETIME, ")   //簽核日期
                        .append("REMARK, ")       //簽核備註
                        .append("'' EMP_ID ")     //員編
                        .append("from TBKYC_QUESTIONNAIRE_FLW_DETAIL ")
                        .append("where 1 = 1 ")
                        .append("and EXAM_VERSION = :EXAM_VERSION ")
                        .append("order by CREATETIME ASC");

                condition.setObject("EXAM_VERSION", examVersion);
                condition.setQueryString(sql.toString());

                List<Map> flwList = getDataAccessManager().exeQuery(condition);

                //多帶一筆給當前主管審核的資訊回去,顯示在說明備註中,供其輸入檢核備註
                condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
                sql = new StringBuffer()
                        .append("select EMP_NAME ")
                        .append("from TBORG_MEMBER ")
                        .append("where EMP_ID = :EMP_ID ");

                condition.setQueryString(sql.toString());
                condition.setObject("EMP_ID", empID);

                List<Map> managerInfoList = getDataAccessManager().exeQuery(condition);

                Map approveManager = new HashedMap();
                Map memberResult = managerInfoList.get(0);

                String empName = (memberResult.size() > 0) ? (String) memberResult.get("EMP_NAME") : "";
                approveManager.put("SIGNOFF_NAME", empName);
                approveManager.put("CREATETIME", new Date());
                approveManager.put("REMARK", "");
                approveManager.put("EMP_ID", empID);

                ArrayList resFlwist = new ArrayList();
                resFlwist.addAll(flwList);
                resFlwist.add(approveManager);

//                //回傳資料List
//                List resultList = new ArrayList();
//                for(Map queryMap : queryList){
//                    Map resMap = new HashMap();
//                    resMap.putAll(queryMap);
//
//                    resMap.put("FRACTIONS", fractionList);
//                    resultList.add(resMap);
//                }

                if (queryList.size() > 0) {
                    //帶入轉換過的資料
                    outputVO.setResultList(resultList);
                    outputVO.setRlVersionList(rlVersionList);
                    outputVO.setRlrVersionList(rlrVersionList);
                    outputVO.setcValList(cValList);
                    outputVO.setwValList(wValList);
                    outputVO.setCwRLList(cwRLList);
                    outputVO.setFlwList(resFlwist);
                    sendRtnObject(outputVO);
                }else{
                    throw new APException("ehl_01_common_009");
                }
            }
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (JBranchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 放行-核可資料
     *
     * @param body
     * @param header
     */
    public void approve(Object body, IPrimitiveMap<Object> header) throws Exception {
        KYC220InputVO inputVO = (KYC220InputVO) body;
        KYC220OutputVO outputVO = new KYC220OutputVO();
        String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
        String examVersion      = inputVO.getExamVersion();     //問卷版本
        Date   expiryDate       = inputVO.getExpiryDate();      //失效日
        String remark           = inputVO.getRemark();          //審核備註

        if(StringUtils.isBlank(remark)){
            throw new JBranchException("需輸入審核意見");
        }

        try{
            //01.更新前一版本生效問卷
        	//移至批次內處理
//            
//            QueryConditionIF condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//
//            StringBuffer sql = new StringBuffer()
//                    .append("update TBSYS_QUESTIONNAIRE ")
//                    .append("set EXPIRY_DATE = :EXPIRY_DATE ")
//                    .append("where STATUS = '03' ")
//                    .append("and EXPIRY_DATE is null ");
//
//            condition.setQueryString(sql.toString());
//            condition.setObject("EXPIRY_DATE", expiryDate);
//            getDataAccessManager().exeUpdate(condition);

            //02.啟用當前問卷

           	QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        	StringBuilder sb = new StringBuilder();
        	//sb.append(" UPDATE TBSYS_QUESTIONNAIRE SET STATUS = '02',MODIFIER = :loginID,LASTUPDATE = sysdate ");
        	sb.append(" UPDATE TBSYS_QUESTIONNAIRE SET STATUS = '02',LASTUPDATE = sysdate ");
        	sb.append(" WHERE EXAM_VERSION = :exam_version ");
        	//qc.setObject("loginID", loginID);
        	qc.setObject("exam_version", examVersion);
        	qc.setQueryString(sb.toString());
        	getDataAccessManager().exeUpdate(qc);
 
//            condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//
//            sql = new StringBuffer()
//                    .append("update TBSYS_QUESTIONNAIRE set STATUS = '03' where EXAM_VERSION = :EXAM_VERSION ");
//
//            condition.setQueryString(sql.toString());
//            condition.setObject("EXAM_VERSION", examVersion);
//            getDataAccessManager().exeUpdate(condition);
//
        	//移至批次內處理
//            //03.更新題目已使用狀態
//            condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//            sql = new StringBuffer()
//                    .append("update TBSYS_QST_QUESTION ")
//                    .append("set STATUS = :STATUS ")
//                    .append("where STATUS = 'N' ")
//                    .append("and QUESTION_VERSION in ( ")
//                    .append("select QUESTION_VERSION from TBSYS_QUESTIONNAIRE ")
//                    .append("where EXAM_VERSION = :EXAM_VERSION) ");
//
//            condition.setQueryString(sql.toString());
//            condition.setObject("STATUS", "3");
//            condition.setObject("EXAM_VERSION", examVersion);
//            getDataAccessManager().exeUpdate(condition);

            //新增資料至簽核流程檔
            insertTbkycQuestionnaireFlwDetail(inputVO);

        } catch (DAOException e) {
            e.printStackTrace();
        } catch (JBranchException e) {
            e.printStackTrace();
        }
        //success
        logger.info("ehl_01_common_004");
        this.sendRtnObject(null);
    }

    /**
     * 退回問卷
     *
     * @param body
     * @param header
     */
    public void reject(Object body, IPrimitiveMap<Object> header) throws JBranchException {
        KYC220InputVO inputVO = (KYC220InputVO) body;
        KYC220OutputVO outputVO = new KYC220OutputVO();

        String examVersion = inputVO.getExamVersion(); //問卷版本
        String remark = inputVO.getRemark();          //審核備註

        if(StringUtils.isBlank(remark)){
            throw new JBranchException("需輸入審核意見");
        }

        try{
            QueryConditionIF condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

            StringBuffer sql = new StringBuffer()
                    .append("update TBSYS_QUESTIONNAIRE ")
                    .append("set STATUS = :STATUS ")
                    .append("where EXAM_VERSION = :EXAM_VERSION ");

            condition.setQueryString(sql.toString());
            condition.setObject("STATUS", "04");
            condition.setObject("EXAM_VERSION", examVersion);
            getDataAccessManager().exeUpdate(condition);

            //新增資料至簽核流程檔
            insertTbkycQuestionnaireFlwDetail(inputVO);
        } catch (DAOException e) {
            e.printStackTrace();
        } catch (JBranchException e) {
            e.printStackTrace();
        }
        this.sendRtnObject(null);
    }

    /**
     * 新增簽核流程檔
     *
     * @param inputVO
     * @throws JBranchException
     */
    private void insertTbkycQuestionnaireFlwDetail(KYC220InputVO inputVO) throws JBranchException {
        String examVersion = inputVO.getExamVersion();
        String signoffNum = getSeq();

        String empID = inputVO.getEmpID();          //使用者員編
        String empName = inputVO.getEmpName();      //使用者名稱
        String branchID = inputVO.getBranchID();    //使用者分行代碼
        String roleID = null;                       //角色代碼
        String remark = inputVO.getRemark();        //檢核備註

        //取得員工角色代碼
        QueryConditionIF condition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_HQL);

        StringBuffer sql = new StringBuffer()
                .append("from TBORG_MEMBER_ROLEVO ")
                .append("where EMP_ID = :EMP_ID ");

        condition.setQueryString(sql.toString());
        condition.setObject("EMP_ID", empID);

        List<TBORG_MEMBER_ROLEVO> voList = getDataAccessManager().exeQuery(condition);
        TBORG_MEMBER_ROLEVO vo = voList.get(0);
        roleID = (vo != null) ? vo.getcomp_id().getROLE_ID() : null;

        //insert TBKYC_QUESTIONNAIRE_FLW_DETAIL
        TBKYC_QUESTIONNAIRE_FLW_DETAILPK flwPk = new TBKYC_QUESTIONNAIRE_FLW_DETAILPK(examVersion, signoffNum);
        TBKYC_QUESTIONNAIRE_FLW_DETAILVO flwVO = new TBKYC_QUESTIONNAIRE_FLW_DETAILVO(flwPk);

        flwVO.setSIGNOFF_ID(empID);
        flwVO.setSIGNOFF_NAME(empName);
        flwVO.setSIGNOFF_BANK(branchID);
        flwVO.setEMP_ROLE(roleID);
        flwVO.setREMARK(remark);

        getDataAccessManager().create(flwVO);
    }

    /**
     * 取KYC220交易序號
     *
     * @return
     * @throws JBranchException
     */
    private String getSeq() throws JBranchException{
        SerialNumberUtil sn = new SerialNumberUtil();
        java.util.Date date = new java.util.Date();
        SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
        String date_seqnum = simple.format(date);
        String seqNum = "";
        try {
            seqNum = date_seqnum+(sn.getNextSerialNumber("KYC220")).substring(1, 5);
        } catch (Exception e) {
            sn.createNewSerial("KYC220", "00000", null, null, null, 1,new Long("99999") , "y", new Long("0"), null);
            seqNum = date_seqnum+(sn.getNextSerialNumber("KYC220")).substring(1, 5);
        }
        return seqNum;
    }
}