package com.systex.jbranch.app.server.fps.crm641;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.CEW012R_CUST_DATA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.cew012r.CEW012RInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.cew012r.CEW012ROutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.cew012r.CEW012ROutputVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("crm641")
@Scope("request")
public class CRM641 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM641.class);
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
    
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM641InputVO inputVO = (CRM641InputVO) body;
		CRM641OutputVO return_VO = new CRM641OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		/* 保險
		 * 正常(已核保) status 為空值 */
		sql.append("SELECT NVL(PRD.INSPRD_NAME,M.POLICY_FULL_NAME) as POLICY_FULL_NAME, PARAM.PARAM_NAME as CONTRACT_TEXT ");
		sql.append("FROM TBCRM_AST_INS_MAST M ");
		sql.append("LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'CRM.CRM641_Accepted_STATUS' AND PARAM.PARAM_CODE = M.CONTRACT_STATUS ");
		sql.append("LEFT JOIN (select DISTINCT INSPRD_ID,INSPRD_NAME from TBPRD_INS_MAIN) PRD ON M.INS_TYPE = PRD.INSPRD_ID	WHERE 1 = 1 ");
		sql.append("AND (M.CONTRACT_STATUS IN ('K','CK','U','Y','N','X', 'M','T','Z','BK','AK','B','H','E','XN','NS','KD') OR M.CONTRACT_STATUS IS NULL) ");
		sql.append("AND CUST_ID = :id ");
		sql.append("UNION ");
		sql.append("SELECT INSPRD_NAME as POLICY_FULL_NAME, ");											//險種名稱
		sql.append("'申請中' as CONTRACT_TEXT FROM VWIOT_MAIN WHERE REG_TYPE IN ('1', '2') ");			//新契約 
		sql.append("AND STATUS IN ('10','20','30','38','40','42','50','52','60','62','70','80') ");		//進件中(申請中)
		sql.append("AND INSPRD_NAME is not null AND CUST_ID = :id ");
		
		/**此為不顯示保險進件舊資料已結單但狀態仍為申請中，故設一20170801日期後的過濾條件。**/
		sql.append("AND TO_CHAR(KEYIN_DATE,'YYYY-MM-DD') >= '2017-08-01' ");

		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("id", inputVO.getCust_id());
		
		List<Map<String,Object>> result = dam.exeQuery(queryCondition);
		return_VO.setResultList(result);
		sendRtnObject(return_VO);
	}
	
	public void loan(Object body, IPrimitiveMap header) throws Exception {
		CRM641InputVO inputVO = (CRM641InputVO) body;
		CRM641OutputVO return_VO = new CRM641OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		/* 信貸  */	
//		sql.append("SELECT NVL(SUBSTR(CAP.PRD_TYPE, 3), '') AS POLICY_FULL_NAME, NVL(APROV_STS, '申請中') AS CONTRACT_TEXT ");
//		sql.append("FROM (SELECT a.CRD_APRV_STATUS, A.CUST_ID ,A.RECEIVE_DATE ,D.DRAW_DATE, ");
//		sql.append("case when g.PROJ_CODE is not null then g.PROJ_CODE else f.PROJ_CODE end as APPROVE_PROJ_CODE, ");
//		sql.append("f.PROJ_CODE AS PROJ_CODE_I ,g.PROJ_CODE AS PROJ_CODE_A, J.CODE_NAME AS APROV_STS ,B.ACHIVE_ORG_NM ");
//		sql.append("FROM TBPMS_TB_CASE_MAIN_SG A ");
//		sql.append("LEFT JOIN TBPMS_TB_CASE_ACC_LOAN_SG D on a.CASE_NO=d.case_no ");
//		sql.append("left join (select * from TBPMS_TB_SYS_CODE_MAINTAIN_DSG where kind_id='377') j ");
//		sql.append("on a.CRD_APRV_STATUS = j.code_id ");
//		sql.append("LEFT JOIN (SELECT CASE_NO, ");
//		sql.append("CASE WHEN ACHIVE_ORG_NM IS NULL THEN AO_ORG_NM ELSE ACHIVE_ORG_NM END AS ACHIVE_ORG_NM ");
//		sql.append("FROM TBPMS_TB_CASE_APP_SALES_SG ) B ON A.CASE_NO = B.CASE_NO ");
//		sql.append("LEFT JOIN (SELECT AA.CASE_NO, AA.PROJ_CODE, FF.CODE_ID, FF.CODE_NAME ");
//		sql.append("FROM TBPMS_TB_CASE_APP_LOAN_SG AA ");
//		sql.append("LEFT JOIN (SELECT * FROM TBPMS_TB_SYS_CODE_MAINTAIN_DSG ");
//		sql.append("WHERE KIND_ID='564' ) FF ON AA.PROJ_CODE =FF.CODE_ID ) F ON A.CASE_NO = F.CASE_NO ");
//		sql.append("LEFT JOIN (SELECT A.CASE_NO,A.PROJ_CODE,B.CODE_NAME FROM TBPMS_TB_CASE_CRD_LOAN_SG A ");
//		sql.append("LEFT JOIN (SELECT * FROM TBPMS_TB_SYS_CODE_MAINTAIN_DSG WHERE KIND_ID='564' ) B ");
//		sql.append("ON A.PROJ_CODE=B.CODE_ID WHERE A.DATA_STATUS = 'B' AND A.DEL_FLAG='N' ) G ON A.CASE_NO=G.CASE_NO ");
//		sql.append("WHERE A.PROD_KIND='B' AND A.APPLY_AMT > '0' AND A.FLOW_TYPE IN ('LA','LE') ) RTN ");
//		sql.append("LEFT JOIN TBPMS_CREDIT_APPRO_PRDTYPE CAP ON RTN.APPROVE_PROJ_CODE = CAP.APPROVE_PROJ_CODE ");
//		sql.append("WHERE RTN.ACHIVE_ORG_NM NOT IN ('電銷一區','電銷二區','電銷三區','電銷四區','電銷服務科') ");
//		sql.append("AND RTN.APROV_STS IS NULL ");   //===>表申請中
////		資料先取201702才會有"目前狀態"資料，待測試無誤後，應依上行Mark程式邏輯
////		sql.append("AND (SUBSTR(RTN.DRAW_DATE,1,6)='201702' OR SUBSTR(RTN.RECEIVE_DATE,1,6)='201702') ");
//		sql.append("AND CUST_ID = :id ");
//		
//		sql.append("UNION ALL ");
//		
//		/* 房貸  */
//		sql.append("SELECT CASE WHEN RTN.ETN_TYPE LIKE '循環動%' THEN '循環型房貸' ");
//		sql.append("WHEN RTN.PURHOU_FLAG IN ('購屋-自用','購屋-其他') THEN '購屋房貸' ");
//		sql.append("WHEN RTN.PURHOU_FLAG='非購屋' THEN '非購屋房貸' END AS POLICY_FULL_NAME, ");
//		sql.append("NVL(RTN.CRD_APRV_STATUS, '申請中') AS CONTRACT_TEXT ");
//		sql.append("FROM (SELECT  CA.CASE_NO, CA.CUST_ID, CA.CRD_APRV_STATUS, DD.APP_TYPE, B5.CODE_NAME, ");
//		sql.append("B2.ETN_TYPE, CASE WHEN B2.USAGE_SUBKIND IN ('AA3') AND DD.APP_TYPE='1' THEN '購屋-自用' ");
//		sql.append("WHEN B2.USAGE_SUBKIND IN ('AA1','CA1','AA3') THEN '購屋-其他' ELSE '非購屋' END PURHOU_FLAG ");
//		sql.append("FROM TBPMS_TB_CASE_MAIN_SG CA ");
//		sql.append("LEFT JOIN ( ");
//		sql.append("SELECT CASE_NO, APP_TYPE FROM TBPMS_CASE_APP_MORTAGE_SG ");
//		sql.append("WHERE SUBSTR(CASE_NO,1,1)='M' ) DD ");
//		sql.append("ON CA.CASE_NO = DD.CASE_NO ");
//		sql.append("LEFT JOIN ( ");
//		sql.append("SELECT  CASE_NO, ");
//		sql.append("CASE WHEN DRAW_TYPE='2' AND PROJ_CODE IN ('BE40','BE46','BY08','BZ08','BY11','BZ11') THEN '循環動用型' ");
//		sql.append("WHEN PROJ_CODE IN ('BE40','BE27','BE28','BE60','BE61','BY08','BZ08','BE51','BE54','BE57','BE58','BE16','BE46','BY11','BZ11','BZ10','BY10','BE42') THEN '循環動用型' ");
//		sql.append("WHEN DRAW_TYPE='1' AND PROJ_CODE IN ('B100','B115','BY00','BZ00','BZ12','BZ13','BZ14') THEN '分期攤還型' ");
//		sql.append("WHEN DRAW_TYPE='1' THEN '循環動用型' WHEN DRAW_TYPE='2' THEN '分期攤還型' ELSE '待分類' END ETN_TYPE, USAGE_SUBKIND ");
//		sql.append("FROM TBPMS_TB_CASE_APP_LOAN_SG WHERE SUBSTR(CASE_NO,1,1)='M' ) B2 ");
//		sql.append("ON CA.CASE_NO=B2.CASE_NO ");
//		sql.append("LEFT JOIN ( ");
//		sql.append("SELECT KIND_ID, CODE_ID, CODE_NAME ");
//		sql.append("FROM TBPMS_TB_SYS_CODE_MAINTAIN_DSG ");
//		sql.append("WHERE KIND_ID='377' ) B5  ");
//		sql.append("ON CA.CRD_APRV_STATUS = B5.CODE_ID ");
//		sql.append("WHERE CA.PROD_KIND = 'A' AND CA.FLOW_TYPE IN ('MA','ME') ");
////		sql.append("AND SUBSTR(CA.RECEIVE_DATE,1,6) >= '201701' ");
//		sql.append(") RTN WHERE RTN.APP_TYPE <> 8 AND RTN.CRD_APRV_STATUS IS NULL ");
////		sql.append("AND RTN.ETN_TYPE ='待分類' ");
//		sql.append("AND RTN.CUST_ID = :id ");
		
		sql.append("SELECT PRD_NAME as POLICY_FULL_NAME, APPLY_STATUS as CONTRACT_TEXT FROM TBCRM_CUST_APPLY_CASE WHERE CUST_ID = :id ");
		
		
		
		queryCondition.setQueryString(sql.toString());		 
		queryCondition.setObject("id", inputVO.getCust_id());
		
		List<Map<String,Object>> result = dam.exeQuery(queryCondition);
		return_VO.setResultList(result);
		sendRtnObject(return_VO);		
	}
	
	public void creditCard(Object body, IPrimitiveMap header) throws Exception {
		CRM641InputVO input_VO = (CRM641InputVO) body;
		CRM641OutputVO return_VO = new CRM641OutputVO();
		dam = this.getDataAccessManager();
		
		String custID = input_VO.getCust_id();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TO_CHAR(BIRTH_DATE, 'ddmmyyyy') BIRTH_DATE FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
		queryCondition.setObject("cust_id", custID);
		queryCondition.setQueryString(sql.toString());
		List<Map<String,Object>> result = dam.exeQuery(queryCondition);
		
		// 2018/8/20 change電文inside
		if(result.size() > 0) {
			// old code
			String bir = result.get(0).get("BIRTH_DATE") == null ? null : result.get(0).get("BIRTH_DATE").toString();
			
			/**Mark :20161212 更改共用電文  BY Stella */		
			String htxtid = CEW012R_CUST_DATA;

	        //init util
	        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
	        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

	        //head
	        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
	        txHead.setDefaultTxHead();
	        esbUtilInputVO.setTxHeadVO(txHead);

	        //body
	        CEW012RInputVO cew012rInputVO = new CEW012RInputVO();
	        cew012rInputVO.setCUS_ID(custID);  	//客戶ID
	        cew012rInputVO.setCUS_BTH(bir);  	//客戶生日
	        esbUtilInputVO.setCew012rInputVO(cew012rInputVO);

	        //發送電文
	        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
	        /**Mark: 2017/01/17 上行電文OK，補回傳下行電文至前端 */
	        CEW012ROutputVO cew012rOutputVO = new CEW012ROutputVO();
	        List<CEW012ROutputDetailsVO> results = new ArrayList<>();
	        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
	        	cew012rOutputVO = esbUtilOutputVO.getCew012rOutputVO();
	        	results.addAll(cew012rOutputVO.getDetails());      	
	        }
	        return_VO.setResultList2(results);
		}
		
		this.sendRtnObject(return_VO);
	}
	
	
}