package com.systex.jbranch.fubon.jlb;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LE_IMP_TEMPVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("cam996")
@Scope("prototype")
public class CAM996 extends BizLogic {
	
	SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public BigDecimal getCampaignSEQ (DataAccessManager dam) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SQ_TBCAM_SFA_LEADS_IMP.nextval AS SEQNO FROM DUAL");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> leadsImpSEQ = dam.exeQueryWithoutSort(queryCondition);
		
		return (BigDecimal) leadsImpSEQ.get(0).get("SEQNO");
	}
	
	public void saveCampaign (DataAccessManager dam, 
			  BigDecimal seqNO, 
			  String campaignID, 
			  String campaignName, 
			  String campaignDesc, 
			  String stepID, 
			  String leadSourceId, 
			  Date startDate, 
			  Date endDate, 
			  String leadType, 
			  String leadPara1, 
			  String leadPara2,
			  String examID, 
			  String salesPitch, 
			  String firstChannel, 
			  String secondChannel, 
			  String impStatus, 
			  String checkStatus, 
			  String giftCampaignID, 
			  BigDecimal leTotalCnt, 
			  String responseCode) throws JBranchException {

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer dayUseSQL = new StringBuffer();
		dayUseSQL.append("SELECT PABTH_UTIL.FC_getTwoDateDiff(TO_DATE(:sDate, 'yyyyMMdd'), TO_DATE(:eDate, 'yyyyMMdd')) AS WORK_DAY ");
		dayUseSQL.append("FROM DUAL ");
		queryCondition.setObject("sDate", SDFYYYYMMDD.format(startDate));
		queryCondition.setObject("eDate", SDFYYYYMMDD.format(endDate));
		queryCondition.setQueryString(dayUseSQL.toString());
		List<Map<String, Object>> dayUse = dam.exeQueryWithoutSort(queryCondition);
		
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo.setSEQNO(seqNO);
		vo.setCAMPAIGN_ID(campaignID); // 行銷活動代碼
		vo.setCAMPAIGN_NAME(campaignName); // 行銷活動名稱
		vo.setCAMPAIGN_DESC(campaignDesc); // 簡要說明
		vo.setSTEP_ID(stepID); // 行銷活動步驟代碼
		vo.setLEAD_SOURCE_ID(leadSourceId); //名單來源
		vo.setSTART_DATE(new Timestamp(startDate.getTime())); // 行銷活動起日
		vo.setEND_DATE(new Timestamp(endDate.getTime())); // 行銷活動迄日
		vo.setLEAD_TYPE(leadType); // 名單類型
		vo.setLEAD_PARA1(leadPara1); //附帶資料蒐集
		vo.setLEAD_DAYUSE(new BigDecimal(dayUse.get(0).get("WORK_DAY").toString()));
		vo.setEXAM_ID(examID);
		vo.setSALES_PITCH(salesPitch); // 話術
		vo.setFIRST_CHANNEL(firstChannel); // 使用部隊
		vo.setSECOND_CHANNEL(secondChannel); // 第二使用通路
		vo.setIMP_STATUS(impStatus); // 匯入狀態:IN-未處理
		vo.setCHECK_STATUS(checkStatus); // 放行狀態:01-待放行
		vo.setGIFT_CAMPAIGN_ID(giftCampaignID); //票券/贈品/講座活動代碼
		vo.setLEAD_PARA2(leadPara2); //行銷活動參考文件
		vo.setLE_TOTAL_CNT(leTotalCnt);
		vo.setLEAD_RESPONSE_CODE(StringUtils.isNotBlank(responseCode) ? responseCode : "0000000000");
		
		dam.create(vo);
	}
	
	public void saveCampaign (DataAccessManager dam, 
							  BigDecimal seqNO, 
							  String campaignID, 
							  String campaignName, 
							  String campaignDesc, 
							  String stepID, 
							  String leadSourceId, 
							  Date startDate, 
							  Date endDate, 
							  String leadType, 
							  String leadPara1, 
							  String leadPara2,
							  String examID, 
							  String salesPitch, 
							  String firstChannel, 
							  String secondChannel, 
							  String impStatus, 
							  String checkStatus, 
							  String giftCampaignID, 
							  BigDecimal leTotalCnt, 
							  String responseCode,
							  String campPurpose) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer dayUseSQL = new StringBuffer();
		dayUseSQL.append("SELECT PABTH_UTIL.FC_getTwoDateDiff(TO_DATE(:sDate, 'yyyyMMdd'), TO_DATE(:eDate, 'yyyyMMdd')) AS WORK_DAY ");
		dayUseSQL.append("FROM DUAL ");
		queryCondition.setObject("sDate", SDFYYYYMMDD.format(startDate));
		queryCondition.setObject("eDate", SDFYYYYMMDD.format(endDate));
		queryCondition.setQueryString(dayUseSQL.toString());
		List<Map<String, Object>> dayUse = dam.exeQueryWithoutSort(queryCondition);
		
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo.setSEQNO(seqNO);
		vo.setCAMPAIGN_ID(campaignID); // 行銷活動代碼
		vo.setCAMPAIGN_NAME(campaignName); // 行銷活動名稱
		vo.setCAMPAIGN_DESC(campaignDesc); // 簡要說明
		vo.setSTEP_ID(stepID); // 行銷活動步驟代碼
		vo.setLEAD_SOURCE_ID(leadSourceId); //名單來源
		vo.setSTART_DATE(new Timestamp(startDate.getTime())); // 行銷活動起日
		vo.setEND_DATE(new Timestamp(endDate.getTime())); // 行銷活動迄日
		vo.setLEAD_TYPE(leadType); // 名單類型
		vo.setLEAD_PARA1(leadPara1); //附帶資料蒐集
		vo.setLEAD_DAYUSE(new BigDecimal(dayUse.get(0).get("WORK_DAY").toString()));
		vo.setEXAM_ID(examID);
		vo.setSALES_PITCH(salesPitch); // 話術
		vo.setFIRST_CHANNEL(firstChannel); // 使用部隊
		vo.setSECOND_CHANNEL(secondChannel); // 第二使用通路
		vo.setIMP_STATUS(impStatus); // 匯入狀態:IN-未處理
		vo.setCHECK_STATUS(checkStatus); // 放行狀態:01-待放行
		vo.setGIFT_CAMPAIGN_ID(giftCampaignID); //票券/贈品/講座活動代碼
		vo.setLEAD_PARA2(leadPara2); //行銷活動參考文件
		vo.setLE_TOTAL_CNT(leTotalCnt);
		vo.setLEAD_RESPONSE_CODE(StringUtils.isNotBlank(responseCode) ? responseCode : "0000000000");
		vo.setCAMP_PURPOSE(campPurpose);
		
		dam.create(vo);
	}
	
	public void saveLeads (DataAccessManager dam, 
						   BigDecimal seqNo, 
						   String custID, 
						   String custName, 
						   String branchID, 
						   String aoCode, 
						   Date startDate, 
						   Date endDate, 
						   String leadType) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO FROM DUAL");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQueryWithoutSort(queryCondition);
		
		TBCAM_SFA_LE_IMP_TEMPVO vo = new TBCAM_SFA_LE_IMP_TEMPVO();
		vo.setSEQNO((BigDecimal) list.get(0).get("SEQNO"));
		vo.setIMP_SEQNO(seqNo); // 匯入主檔TBCAM_SFA_LEADS_IMP 的資料序號
		vo.setCUST_ID(custID); // 客戶ID
		vo.setCUST_NAME(custName); // 客戶姓名
		vo.setBRANCH_ID(branchID); // 指派分行
		vo.setAO_CODE(aoCode); // 指派AO_CODE
		vo.setSTART_DATE(new Timestamp(startDate.getTime())); // 名單起始日
		vo.setEND_DATE(new Timestamp(endDate.getTime())); // 名單到期日
		vo.setLEAD_TYPE(leadType); // 名單類型
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP_LEAD.nextval AS SEQNO FROM DUAL");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listSEQ = dam.exeQueryWithoutSort(queryCondition);
		vo.setLEAD_ID("USR" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(((BigDecimal) list.get(0).get("SEQNO")).toString(), 14)); // 來源系統的名單代碼
		
		dam.create(vo);
	}
	
	private String addZeroForNum(String str, int strLength) {
		
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }

	    return str;
	}
	
}
