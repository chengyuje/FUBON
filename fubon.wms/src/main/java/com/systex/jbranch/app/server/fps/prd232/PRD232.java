package com.systex.jbranch.app.server.fps.prd232;

import com.systex.jbranch.app.common.fps.table.TBPRD_FUNDVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUND_BONUSINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_FUND_BONUSINFO_REVIEWVO;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
//import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * prd232
 * 
 * @author moron
 * @date 2016/09/29
 * @spec null
 */
@Component("prd232")
@Scope("request")
public class PRD232 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD232.class);

	@Autowired
	private PRD232OutputVO returnVO;

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD232InputVO inputVO = (PRD232InputVO) body;
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD232' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT SEQ, PRD_ID, FUND_CNAME, CASE WHEN IS_SALE = '1' THEN 'Y' WHEN IS_SALE = '2' THEN 'N' ELSE '' END AS IS_SALE, ");
		sql.append("CURRENCY_STD_ID, TRUST_COM, INV_TARGET, RISKCATE_ID, LIPPER_ID, TIER_LEVEL, RRNOTES, FEE, FEE1, FEE2, FEE3, ");
		sql.append("M_FEE, TRAIN, OTHER_REWARD, OTHER_REWARD_INV, OTHER_REWARD_AGN, OTHER_REWARD_FRN, ACT_TYPE, REVIEW_STATUS, CREATOR, ");
		// DISTRIBUTION_RATE 分銷費（資料源頭有可能為「無」）
		sql.append("DECODE(DISTRIBUTION_RATE, '無', '0', DISTRIBUTION_RATE) DISTRIBUTION_RATE, ");
		sql.append("TB1_FEE, OTHER_DISTRIBUTION, IPO_START, IPO_END, IPO_RAISE_START, IPO_RAISE_END, DISTRIBUTION_FEE, M_FEE_RATE FROM (  ");

		// 是否為覆核人員
		boolean isReviewRole = ((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0;
		// 查詢首頁近來覆核人員  為Home才會查詢
		boolean isReviewFromHome = StringUtils.equals("FUNDBONUSINFO", inputVO.getPassParams());

		sql.append("select REVIEW.SEQ, REVIEW.PRD_ID, REVIEW.FUND_CNAME, REVIEW.IS_SALE, REVIEW.CURRENCY_STD_ID, REVIEW.TRUST_COM, ");
		sql.append("       REVIEW.INV_TARGET, REVIEW.RISKCATE_ID, REVIEW.LIPPER_ID, REVIEW.TIER_LEVEL, REVIEW.RRNOTES, REVIEW.FEE, REVIEW.FEE1, ");
		sql.append("	   REVIEW.FEE2, REVIEW.FEE3, REVIEW.M_FEE, REVIEW.TRAIN, REVIEW.OTHER_REWARD, REVIEW.OTHER_REWARD_INV, ");
		sql.append("       REVIEW.OTHER_REWARD_AGN, REVIEW.OTHER_REWARD_FRN, REVIEW.ACT_TYPE, REVIEW.REVIEW_STATUS, REVIEW.CREATOR, ");
		sql.append("       REVIEW.DISTRIBUTION_RATE, REVIEW.TB1_FEE, REVIEW.OTHER_DISTRIBUTION, REVIEW.IPO_START, REVIEW.IPO_END, REVIEW.IPO_RAISE_START, REVIEW.IPO_RAISE_END, REVIEW.DISTRIBUTION_FEE, ");

		// 待覆核的資料將計算 M_FEE_RATE（經理費(本行)）欄位（以前是透過上傳資料來存入 REVIEW）
		sql.append("	   CASE WHEN NVL(REVIEW.DISTRIBUTION_FEE, 0) <= 1 THEN TO_CHAR(REVIEW.DISTRIBUTION_FEE)  ");
		sql.append("	   ELSE ( ");
		sql.append("           CASE WHEN PARAM.PARAM_NAME LIKE '%percent%' THEN ( ");
		sql.append("	            CASE WHEN TO_CHAR(CEIL(REVIEW.M_FEE*TO_NUMBER(REPLACE(PARAM.PARAM_NAME, 'percent', ''))/100)) > NVL(REVIEW.DISTRIBUTION_FEE, 0) ");
		sql.append("	                 THEN TO_CHAR(REVIEW.DISTRIBUTION_FEE) ");
		sql.append("	                 ELSE TO_CHAR(CEIL(REVIEW.M_FEE*TO_NUMBER(REPLACE(PARAM.PARAM_NAME, 'percent', ''))/100)) ");
		sql.append("	            END ) ");
		sql.append("	            ELSE ( ");
		sql.append("	                CASE WHEN PARAM.PARAM_NAME IS NULL THEN ( ");
		sql.append("	                         CASE WHEN CEIL(REVIEW.M_FEE*0.6) > NVL(REVIEW.DISTRIBUTION_FEE, 0) ");
		sql.append("	                              THEN TO_CHAR(REVIEW.DISTRIBUTION_FEE) ");
		sql.append("	                              ELSE TO_CHAR(CEIL(REVIEW.M_FEE*0.6)) ");
		sql.append("	                         END) ");
		sql.append("	                     ELSE ( ");
		sql.append("	                         CASE WHEN NVL(PARAM.PARAM_NAME, 0) > NVL(REVIEW.DISTRIBUTION_FEE, 0) ");
		sql.append("	                              THEN TO_CHAR(REVIEW.DISTRIBUTION_FEE) ");
		sql.append("	                              ELSE PARAM.PARAM_NAME ");
		sql.append("	                         END ) ");
		sql.append("	                     END ) ");
		sql.append("	            END ) ");
		sql.append("	   END M_FEE_RATE ");

		sql.append("from ( ");
		sql.append("	SELECT rw.SEQ, rw.PRD_ID, fund.FUND_CNAME, fund.IS_SALE, fund.CURRENCY_STD_ID, fund.TRUST_COM, ");
		sql.append("		   fund.INV_TARGET, fund.RISKCATE_ID, rw.LIPPER_ID, rw.TIER_LEVEL, rw.RRNOTES, rw.FEE, rw.FEE1, ");
		sql.append("	  	   rw.FEE2, rw.FEE3, rw.M_FEE, rw.TRAIN, rw.OTHER_REWARD, rw.OTHER_REWARD_INV, ");
		sql.append("		   rw.OTHER_REWARD_AGN, rw.OTHER_REWARD_FRN, rw.ACT_TYPE, rw.REVIEW_STATUS, rw.CREATOR, ");
		sql.append("		   rate.DISTRIBUTION_RATE, rw.TB1_FEE, rw.OTHER_DISTRIBUTION, rw.IPO_START, rw.IPO_END, rw.IPO_RAISE_START, rw.IPO_RAISE_END, ");
		// DISTRIBUTION_FEE（經銷費合計）= 嘉實經理費 + 分銷費 + 12B-1 FEE + 其他經銷費
		// 待覆核的資料將相加計算 DISTRIBUTION_FEE（經銷費合計）欄位
		sql.append("		   CAST(NVL(rw.M_FEE, 0) + NVL(rw.TB1_FEE, 0) + NVL(rw.OTHER_DISTRIBUTION, 0) + ");
		// 分銷費（資料源頭有可能為「無」）
		sql.append("	  	   NVL(DECODE(rate.DISTRIBUTION_RATE, '無', 0, rate.DISTRIBUTION_RATE), 0) as VARCHAR2(10)) as DISTRIBUTION_FEE, ");
		// 計算 M_FEE_RATE 用
		sql.append("		   SUBSTR(fund.PRD_ID, 1, 2) COMPANY ");
		sql.append("	FROM TBPRD_FUND_BONUSINFO_REVIEW rw ");
		sql.append("	left join TBPRD_FUND fund on rw.PRD_ID = fund.PRD_ID ");
		sql.append("	left join TBPRD_FUNDINFO fund_info on rw.PRD_ID = fund_info.PRD_ID ");
		sql.append("	left join TBPRD_FUND_DISTRIBUTION_RATES rate on (rate.PRD_ID = rw.PRD_ID) ");
		sql.append("	WHERE rw.REVIEW_STATUS = 'W' ");
		sql.append("	  AND NVL(fund.OVS_PRIVATE_YN, 'N') = 'N' "); //非境外私募基金

		if (!isReviewRole)
			sql.append("	  AND rw.CREATOR = :creator ");

		sql.append(") REVIEW ");
		sql.append("LEFT JOIN TBSYSPARAMETER PARAM ");
		sql.append("ON REVIEW.COMPANY = PARAM.PARAM_CODE AND PARAM.PARAM_TYPE = 'PRD.FUND_DISTRIBUTION' ");

		//境外私募基金
		sql.append(" UNION ");
		sql.append("	SELECT rw.SEQ, rw.PRD_ID, fund.FUND_CNAME, fund.IS_SALE, fund.CURRENCY_STD_ID, fund.TRUST_COM, ");
		sql.append("		   fund.INV_TARGET, fund.RISKCATE_ID, rw.LIPPER_ID, rw.TIER_LEVEL, rw.RRNOTES, rw.FEE, rw.FEE1, ");
		sql.append("	  	   rw.FEE2, rw.FEE3, rw.M_FEE, rw.TRAIN, rw.OTHER_REWARD, rw.OTHER_REWARD_INV, ");
		sql.append("		   rw.OTHER_REWARD_AGN, rw.OTHER_REWARD_FRN, rw.ACT_TYPE, rw.REVIEW_STATUS, rw.CREATOR, ");
		sql.append("		   rw.DISTRIBUTION_RATE, rw.TB1_FEE, rw.OTHER_DISTRIBUTION, rw.IPO_START, rw.IPO_END, rw.IPO_RAISE_START, rw.IPO_RAISE_END, rw.DISTRIBUTION_FEE, rw.M_FEE_RATE ");
		sql.append("	FROM TBPRD_FUND_BONUSINFO_REVIEW rw ");
		sql.append("	left join TBPRD_FUND fund on rw.PRD_ID = fund.PRD_ID ");
		sql.append("	WHERE rw.REVIEW_STATUS = 'W' ");
		sql.append("	  AND NVL(fund.OVS_PRIVATE_YN, 'N') = 'Y' "); //境外私募基金
		if (!isReviewRole) sql.append("	  AND rw.CREATOR = :creator ");
		
		if (!isReviewRole || !isReviewFromHome) {
			sql.append("UNION ");
			sql.append("SELECT null as SEQ, info.PRD_ID, fund.FUND_CNAME, fund.IS_SALE, fund.CURRENCY_STD_ID, fund.TRUST_COM, ");
			sql.append("	   fund.INV_TARGET, fund.RISKCATE_ID, info.LIPPER_ID, info.TIER_LEVEL, info.RRNOTES, info.FEE, ");
			sql.append("	   info.FEE1, info.FEE2, info.FEE3, info.M_FEE, info.TRAIN, info.OTHER_REWARD, ");
			sql.append("	   info.OTHER_REWARD_INV, info.OTHER_REWARD_AGN, info.OTHER_REWARD_FRN, info.ACT_TYPE, ");
			sql.append("	   info.REVIEW_STATUS, null as CREATOR, rate.DISTRIBUTION_RATE, info.TB1_FEE, info.OTHER_DISTRIBUTION, ");
			// 不是待覆核的資料直接撈取 DISTRIBUTION_FEE（經銷費合計）、M_FEE_RATE（經理費(本行)）欄位以取得加總的數值
			sql.append("	   info.IPO_START, info.IPO_END, info.IPO_RAISE_START, info.IPO_RAISE_END, info.DISTRIBUTION_FEE, info.M_FEE_RATE ");
			sql.append("FROM TBPRD_FUND_BONUSINFO info ");
			sql.append("left join TBPRD_FUND fund on info.PRD_ID = fund.PRD_ID ");
			sql.append("left join TBPRD_FUNDINFO fund_info on info.PRD_ID = fund_info.PRD_ID ");
			sql.append("left join TBPRD_FUND_DISTRIBUTION_RATES rate on (rate.PRD_ID = info.PRD_ID) ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append(" AND NVL(fund.OVS_PRIVATE_YN, 'N') = 'N' "); //非境外私募基金
			sql.append(" AND info.PRD_ID NOT IN ( ");
			sql.append("	SELECT PRD_ID FROM TBPRD_FUND_BONUSINFO_REVIEW ");
			sql.append(" 	WHERE REVIEW_STATUS = 'W' ");
			sql.append(!isReviewRole? "AND CREATOR = :creator ": "");
			sql.append(") ");
			
			//境外私募基金
			sql.append("UNION ");
			sql.append("SELECT null as SEQ, info.PRD_ID, fund.FUND_CNAME, fund.IS_SALE, fund.CURRENCY_STD_ID, fund.TRUST_COM, ");
			sql.append("	   fund.INV_TARGET, fund.RISKCATE_ID, info.LIPPER_ID, info.TIER_LEVEL, info.RRNOTES, info.FEE, ");
			sql.append("	   info.FEE1, info.FEE2, info.FEE3, info.M_FEE, info.TRAIN, info.OTHER_REWARD, ");
			sql.append("	   info.OTHER_REWARD_INV, info.OTHER_REWARD_AGN, info.OTHER_REWARD_FRN, info.ACT_TYPE, ");
			sql.append("	   info.REVIEW_STATUS, null as CREATOR, info.DISTRIBUTION_RATE, info.TB1_FEE, info.OTHER_DISTRIBUTION, ");
			sql.append("	   info.IPO_START, info.IPO_END, info.IPO_RAISE_START, info.IPO_RAISE_END, info.DISTRIBUTION_FEE, info.M_FEE_RATE ");
			sql.append("FROM TBPRD_FUND_BONUSINFO info ");
			sql.append("left join TBPRD_FUND fund on info.PRD_ID = fund.PRD_ID ");
			sql.append("WHERE NVL(info.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append(" AND NVL(fund.OVS_PRIVATE_YN, 'N') = 'Y' "); //境外私募基金
			sql.append(" AND info.PRD_ID NOT IN ( ");
			sql.append("	SELECT PRD_ID FROM TBPRD_FUND_BONUSINFO_REVIEW ");
			sql.append(" 	WHERE REVIEW_STATUS = 'W' ");
			sql.append(!isReviewRole? "AND CREATOR = :creator ": "");
			sql.append(") ");
		}

		if (!isReviewRole)
			queryCondition.setObject("creator", ws.getUser().getUserID());

		sql.append(") WHERE 1=1 ");
		// 2017/6/20 user:根據查詢出來的資料來做覆核
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("AND PRD_ID like :id ");
			queryCondition.setObject("id", inputVO.getPrd_id() + "%");
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		returnVO.setResultList(list);
		returnVO.setRowCount(list.size()+"");
		this.sendRtnObject(returnVO);
	}

	/** 整批更新 Review 特定欄位功能 **/
	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD232InputVO inputVO = (PRD232InputVO) body;
		dam = this.getDataAccessManager();
		
		for(String id : inputVO.getId_map()) {
			// 2017/4/21 can edit
			// TBPRD_FUND_BONUSINFO_REVIEW
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SEQ FROM TBPRD_FUND_BONUSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
			queryCondition.setObject("id", id);
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			// edit review
			Boolean exist = false;
			BigDecimal seqNo;
			if (list.size() > 0) {
				exist = true;
				seqNo = (BigDecimal) list.get(0).get("SEQ");
			}
			else {
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBPRD_FUND_BONUSINFO_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
			}
			// edit
			if(exist) {
				TBPRD_FUND_BONUSINFO_REVIEWVO vo = new TBPRD_FUND_BONUSINFO_REVIEWVO();
				vo = (TBPRD_FUND_BONUSINFO_REVIEWVO) dam.findByPKey(TBPRD_FUND_BONUSINFO_REVIEWVO.TABLE_UID, seqNo);

				editReviewCol(inputVO, vo);
				vo.setCreator(ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID)));
				dam.update(vo);
			}
			// old add
			else {
				// get ori
				TBPRD_FUND_BONUSINFOVO orivo = new TBPRD_FUND_BONUSINFOVO();
				orivo = (TBPRD_FUND_BONUSINFOVO) dam.findByPKey(TBPRD_FUND_BONUSINFOVO.TABLE_UID, id);
				// add TBPRD_FUND_BONUSINFO_REVIEW
				TBPRD_FUND_BONUSINFO_REVIEWVO vo = new TBPRD_FUND_BONUSINFO_REVIEWVO();
				vo.setSEQ(seqNo);
				vo.setPRD_ID(id);
				vo.setLIPPER_ID(orivo.getLIPPER_ID());
				//前端已經拿掉 
//				vo.setSELLING(orivo.getSELLING());
				vo.setTIER_LEVEL(orivo.getTIER_LEVEL());
				vo.setRRNOTES(orivo.getRRNOTES());
				vo.setFEE(orivo.getFEE());
				vo.setFEE1(orivo.getFEE1());
				vo.setFEE2(orivo.getFEE2());
				vo.setFEE3(orivo.getFEE3());
				vo.setM_FEE(orivo.getM_FEE());
				vo.setM_FEE_RATE(orivo.getM_FEE_RATE());
//				vo.setREWARD(orivo.getREWARD());
//				vo.setREWARD_DF(orivo.getREWARD_DF());
				vo.setTRAIN(orivo.getTRAIN());
//				vo.setOTHER_REWARD(orivo.getOTHER_REWARD());
				vo.setOTHER_REWARD_INV(orivo.getOTHER_REWARD_INV());
				vo.setOTHER_REWARD_AGN(orivo.getOTHER_REWARD_AGN());
				vo.setOTHER_REWARD_FRN(orivo.getOTHER_REWARD_FRN());
				vo.setTB1_FEE(orivo.getTB1_FEE());
				vo.setOTHER_DISTRIBUTION(orivo.getOTHER_DISTRIBUTION());
				vo.setIPO_START(orivo.getIPO_START());
				vo.setIPO_END(orivo.getIPO_END());
				vo.setIPO_RAISE_START(orivo.getIPO_RAISE_START());
				vo.setIPO_RAISE_END(orivo.getIPO_RAISE_END());

				editReviewCol(inputVO, vo);
				vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
				// 2017/3/13
				TBPRD_FUNDVO mvo = new TBPRD_FUNDVO();
				mvo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, id);
				if(mvo != null) {
					mvo.setFLAG("1");
					dam.update(mvo);
				}
			}
		}
		this.sendRtnObject(null);
	}

	/** 依據前端選擇的「欲整批更新欄位」，更新 Review 欄位於特定值 **/
	private void editReviewCol(PRD232InputVO inputVO, TBPRD_FUND_BONUSINFO_REVIEWVO vo) {
		switch (inputVO.getEdit_col()) {
			//前端已拿掉
//					case "1":
//						vo.setSELLING(inputVO.getCol());
//						break;
			case "2":
				vo.setTIER_LEVEL(inputVO.getCol());
				break;
			case "3":
				vo.setFEE(inputVO.getCol());
				break;
			case "4":
				vo.setFEE1(inputVO.getCol());
				break;
//			case "5":
//				vo.setREWARD(inputVO.getCol());
//				break;
//			case "6":
//				vo.setREWARD_DF(inputVO.getCol());
//				break;
			case "7":
				vo.setTRAIN(inputVO.getCol());
				break;
//					case "8":
//						vo.setOTHER_REWARD(inputVO.getCol());
//						break;
			case "9":
				vo.setOTHER_REWARD_INV(inputVO.getCol());
				break;
			case "10":
				vo.setOTHER_REWARD_AGN(inputVO.getCol());
				break;
			case "11":
				vo.setOTHER_REWARD_FRN(inputVO.getCol());
				break;
			case "12":
				vo.setTB1_FEE(inputVO.getCol());
				break;
			case "13":
				vo.setOTHER_DISTRIBUTION(inputVO.getCol());
		}
	}

	/**
	 * 覆核功能
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		PRD232InputVO inputVO = (PRD232InputVO) body;
		dam = this.getDataAccessManager();
		
		// 2017/2/21
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_FUND_BONUSINFO_REVIEWVO rvo = (TBPRD_FUND_BONUSINFO_REVIEWVO) dam.findByPKey(TBPRD_FUND_BONUSINFO_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_FUND_BONUSINFO
						TBPRD_FUND_BONUSINFOVO vo = new TBPRD_FUND_BONUSINFOVO();
						vo.setPRD_ID(rvo.getPRD_ID());
						vo.setLIPPER_ID(rvo.getLIPPER_ID());
						//前端已拿掉
//						vo.setSELLING(rvo.getSELLING());
						vo.setTIER_LEVEL(rvo.getTIER_LEVEL());
						vo.setRRNOTES(rvo.getRRNOTES());
						vo.setFEE(rvo.getFEE());
						vo.setFEE1(rvo.getFEE1());
						vo.setFEE2(rvo.getFEE2());
						vo.setFEE3(rvo.getFEE3());
						vo.setM_FEE(rvo.getM_FEE());

						// 直接儲存前端傳回的已相加數值
						vo.setM_FEE_RATE((String) rmap.get("M_FEE_RATE"));

//						vo.setREWARD(rvo.getREWARD());
//						vo.setREWARD_DF(rvo.getREWARD_DF());
						vo.setTRAIN(rvo.getTRAIN());
//						vo.setOTHER_REWARD(rvo.getOTHER_REWARD());
						vo.setOTHER_REWARD_INV(rvo.getOTHER_REWARD_INV());
						vo.setOTHER_REWARD_AGN(rvo.getOTHER_REWARD_AGN());
						vo.setOTHER_REWARD_FRN(rvo.getOTHER_REWARD_FRN());

						vo.setTB1_FEE(rvo.getTB1_FEE());
						vo.setOTHER_DISTRIBUTION(rvo.getOTHER_DISTRIBUTION());

						// 直接儲存前端傳回的已相加數值
						vo.setDISTRIBUTION_FEE((String) rmap.get("DISTRIBUTION_FEE"));

						vo.setIPO_START(rvo.getIPO_START());
						vo.setIPO_END(rvo.getIPO_END());
						vo.setIPO_RAISE_START(rvo.getIPO_RAISE_START());
						vo.setIPO_RAISE_END(rvo.getIPO_RAISE_END());

						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);
						// 2017/3/13
						TBPRD_FUNDVO mvo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, rvo.getPRD_ID());
						if(mvo != null) {
							mvo.setFLAG("2");
							/** 覆核完畢後，該檔基金立即可以銷售 **/
							mvo.setIS_SALE("1");
							dam.update(mvo);
						}
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						TBPRD_FUND_BONUSINFOVO vo = (TBPRD_FUND_BONUSINFOVO) dam.findByPKey(TBPRD_FUND_BONUSINFOVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							//前端已經拿掉
//							vo.setSELLING(rvo.getSELLING());
							vo.setTIER_LEVEL(rvo.getTIER_LEVEL());
							vo.setRRNOTES(rvo.getRRNOTES());
							vo.setFEE(rvo.getFEE());
							vo.setFEE1(rvo.getFEE1());
							vo.setFEE2(rvo.getFEE2());
							vo.setFEE3(rvo.getFEE3());
							vo.setM_FEE(rvo.getM_FEE());

							// 直接儲存前端傳回的已相加數值
							vo.setM_FEE_RATE((String) rmap.get("M_FEE_RATE"));

//							vo.setREWARD(rvo.getREWARD());
//							vo.setREWARD_DF(rvo.getREWARD_DF());
							vo.setTRAIN(rvo.getTRAIN());
//							vo.setOTHER_REWARD(rvo.getOTHER_REWARD());
							vo.setOTHER_REWARD_INV(rvo.getOTHER_REWARD_INV());
							vo.setOTHER_REWARD_AGN(rvo.getOTHER_REWARD_AGN());
							vo.setOTHER_REWARD_FRN(rvo.getOTHER_REWARD_FRN());

							vo.setTB1_FEE(rvo.getTB1_FEE());
							vo.setOTHER_DISTRIBUTION(rvo.getOTHER_DISTRIBUTION());
							// 直接儲存前端傳回的已相加數值
							vo.setDISTRIBUTION_FEE((String) rmap.get("DISTRIBUTION_FEE"));

							vo.setIPO_START(rvo.getIPO_START());
							vo.setIPO_END(rvo.getIPO_END());
							vo.setIPO_RAISE_START(rvo.getIPO_RAISE_START());
							vo.setIPO_RAISE_END(rvo.getIPO_RAISE_END());
							
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							dam.update(vo);
							// 2017/3/13
							TBPRD_FUNDVO mvo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, rvo.getPRD_ID());
							if(mvo != null) {
								mvo.setFLAG("2");
								/** 覆核完畢後，該檔基金立即可以銷售 **/
								mvo.setIS_SALE("1");
								dam.update(mvo);
							}
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_009");
						}
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						// no del
					}
				} else {
					//退回
					TBPRD_FUND_BONUSINFOVO bvo = (TBPRD_FUND_BONUSINFOVO) dam.findByPKey(TBPRD_FUND_BONUSINFOVO.TABLE_UID, rvo.getPRD_ID());
					TBPRD_FUNDVO mvo = (TBPRD_FUNDVO) dam.findByPKey(TBPRD_FUNDVO.TABLE_UID, rvo.getPRD_ID());
					if(bvo != null && mvo != null) {
						mvo.setFLAG("2");
						/** 覆核完畢後，該檔基金立即可以銷售 **/
						mvo.setIS_SALE("1");
						dam.update(mvo);
					}
				}
				
				rvo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(rvo);
			} else
				throw new APException("ehl_01_common_009");
		}
		
		this.sendRtnObject(null);
	}

	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD232InputVO inputVO = (PRD232InputVO) body;
		dam = this.getDataAccessManager();
		
		List<String> error = new ArrayList<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				if(i == 0) {
					continue;
				}
				String[] str = dataCsv.get(i);
				// check prd_id
				// TBPRD_FUND
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_FUND where PRD_ID = :id ");
				queryCondition.setObject("id", str[0]);
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				}
				// TBPRD_FUND_BONUSINFO_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_FUND_BONUSINFO_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
				queryCondition.setObject("id", str[0]);
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0) {
					error.add(str[0]);
					continue;
				}
			}
		}
		returnVO.setErrorList(error);
		this.sendRtnObject(returnVO);
	}

    /**
     * PM專區，基金報酬通路整批上傳
     * @param body
     * @param header
     * @throws JBranchException
     */
	public void upload(Object body, IPrimitiveMap header) throws JBranchException {
		PRD232InputVO inputVO = (PRD232InputVO) body;
		List<String> prodIsReviewError = new ArrayList();
		List<String> dataInvalidError = new ArrayList();
		List<String> duplicatedProdError = new ArrayList();
		List<String> prodIdMissingError = new ArrayList();
		Set<String> idList = new HashSet();

		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(AccessContext.tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			checkDataOrder(dataCsv.get(0));

		    for(int i = 1; i < dataCsv.size(); i++) {
				String[] row = dataCsv.get(i);
				if (checkProdIdIsMissing(prodIdMissingError, i, row[0])) continue;
				if (isDuplicatedProd(duplicatedProdError, idList, row[0])) continue;

				String opfYN = getOPFYN(row[0]); //是否為境外私募基金
				List<Map> checkResult = exeQueryForMap(getUploadCheckSql(), getOnlyPrdIdMap(row[0]));
				// 準備要寫入TBPRD_FUND_BONUSINFO_REVIEW的Data Map
				Map<String, Object> data = checkResult.get(0);
				if (isReview(prodIsReviewError, row[0], data)) continue;

				try {
					if(!StringUtils.equals("Y", opfYN)) { //非境外私募基金
						checkAndConfigureReviewMap(row, data);
					} else { //境外私募基金
						checkAndConfigureReviewMap_OPF(row, data);
					}
                } catch (APException ex) {
				    dataInvalidError.add(ex.getMessage());
				    continue;
                }
				
				if(!StringUtils.equals("Y", opfYN)) { //非境外私募基金
					exeUpdateForMap(getUploadInsertSql() ,data);
				} else { //境外私募基金
					exeUpdateForMap(getUploadInsertSql_OPF() ,data);
				}
				
				updateFundMain(row[0]);
			}
		}
		returnVO.setErrorList2(prodIsReviewError);
		returnVO.setErrorList3(dataInvalidError);
		returnVO.setErrorList4(duplicatedProdError);
		returnVO.setErrorList5(prodIdMissingError);
		this.sendRtnObject(returnVO);
	}

	/** 非境外私募基金：取得上傳通路報酬表，要Insert的SQL **/
	private String getUploadInsertSql() {
		return new StringBuilder()
				.append("insert into TBPRD_FUND_BONUSINFO_REVIEW (SEQ, REVIEW_STATUS, LIPPER_ID, ACT_TYPE, ")
				.append("PRD_ID, TIER_LEVEL, RRNOTES, FEE, FEE1, FEE2, FEE3, M_FEE, TB1_FEE, OTHER_DISTRIBUTION, ")
				.append("TRAIN, OTHER_REWARD_INV, OTHER_REWARD_AGN, OTHER_REWARD_FRN, IPO_START, IPO_END, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE, IPO_RAISE_START, IPO_RAISE_END) ")
				.append("values (SQ_TBPRD_FUND_BONUSINFO_REVIEW.nextval, 'W', :LIPPER_ID, :ACT_TYPE, ")
				.append(":PRD_ID, :TIER_LEVEL, :RRNOTES, :FEE, :FEE1, :FEE2, :FEE3, :M_FEE, :TB1_FEE, :OTHER_DISTRIBUTION, ")
				.append(":TRAIN, :OTHER_REWARD_INV, :OTHER_REWARD_AGN, :OTHER_REWARD_FRN, :IPO_START, :IPO_END, 1, :CREATOR, :CREATETIME, :MODIFIER, :LASTUPDATE, :IPO_RAISE_START, :IPO_RAISE_END) ")
				.toString();
	}
	
	/** 境外私募基金：取得上傳通路報酬表，要Insert的SQL **/
	private String getUploadInsertSql_OPF() {
		return new StringBuilder()
				.append("insert into TBPRD_FUND_BONUSINFO_REVIEW (SEQ, REVIEW_STATUS, LIPPER_ID, ACT_TYPE, ")
				.append("PRD_ID, TIER_LEVEL, RRNOTES, FEE, FEE1, FEE2, FEE3, M_FEE, DISTRIBUTION_RATE, TB1_FEE, OTHER_DISTRIBUTION, DISTRIBUTION_FEE, M_FEE_RATE, ")
				.append("TRAIN, OTHER_REWARD_INV, OTHER_REWARD_AGN, OTHER_REWARD_FRN, IPO_START, IPO_END, VERSION, CREATOR, CREATETIME, MODIFIER, LASTUPDATE, IPO_RAISE_START, IPO_RAISE_END) ")
				.append("values (SQ_TBPRD_FUND_BONUSINFO_REVIEW.nextval, 'W', :LIPPER_ID, :ACT_TYPE, ")
				.append(":PRD_ID, :TIER_LEVEL, :RRNOTES, :FEE, :FEE1, :FEE2, :FEE3, :M_FEE, :DISTRIBUTION_RATE, :TB1_FEE, :OTHER_DISTRIBUTION, :DISTRIBUTION_FEE, :M_FEE_RATE,  ")
				.append(":TRAIN, :OTHER_REWARD_INV, :OTHER_REWARD_AGN, :OTHER_REWARD_FRN, :IPO_START, :IPO_END, 1, :CREATOR, :CREATETIME, :MODIFIER, :LASTUPDATE, :IPO_RAISE_START, :IPO_RAISE_END) ")
				.toString();
	}

	/** 商品是否還在覆核中 **/
	private boolean isReview(List<String> prodIsReviewError, String prodId, Map<String, Object> checkData) {
		if (Boolean.valueOf(checkData.get("IS_REVIEW").toString())) {
			prodIsReviewError.add(prodId);
			return true;
		}
		checkData.remove("IS_REVIEW"); // 判斷完移除此欄位
		return false;
	}

	/** 判斷商品ID是否重複，使用者很愛重覆上傳 **/
	private boolean isDuplicatedProd(List<String> duplicatedProdError, Set<String> idList, String prodId) {
		if(idList.contains(prodId.trim())) {
			duplicatedProdError.add(prodId);
			return true;
		}
		idList.add(prodId.trim());
		return false;
	}

	/** 判斷商品ID欄位是否為空 **/
	private boolean checkProdIdIsMissing(List<String> prodIdMissingError, int i, String prodId) {
		if(StringUtils.isBlank(prodId)) {
			prodIdMissingError.add(String.valueOf(i+1));
			return true;
		}
		return false;
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

	/** 取得只有PRD_ID的Map作為查詢SQL的參數 **/
	private Map getOnlyPrdIdMap(String prdId) {
		Map checkParamMap = new HashMap();
		checkParamMap.put("prdId", prdId.trim());
		return checkParamMap;
	}

	/** 取得上傳通路報酬表檢核步驟的SQL，撈出檢核用欄位以及其必要資訊 **/
	private String getUploadCheckSql() {
		return new StringBuilder()
				.append("select trim(D.PRD_ID) PRD_ID, FUND.LIPPER_ID, DECODE(INFO.PRD, null, 'A', 'M') ACT_TYPE, ")
				.append("       DECODE(REVIEW.PRD, null, 'false', 'true') IS_REVIEW ")
				.append("from (select REGEXP_REPLACE(:prdId, '[[:space:]]') PRD_ID from dual) D ")
				.append("left join ( ")
				.append("  select PRD_ID, LIPPER_ID from TBPRD_FUND ")
				.append(") FUND on D.PRD_ID = FUND.PRD_ID ")
				.append("left join ( ")
				.append("  select PRD_ID PRD from TBPRD_FUND_BONUSINFO ")
				.append(") INFO on D.PRD_ID = INFO.PRD ")
				.append("left join ( ")
				.append("  select PRD_ID PRD, REVIEW_STATUS from TBPRD_FUND_BONUSINFO_REVIEW ")
				.append(") REVIEW on D.PRD_ID = REVIEW.PRD and REVIEW.REVIEW_STATUS = 'W' ")
				.toString();
	}

	/** 檢查欄位順序是否正確 **/
    private void checkDataOrder(String[] header) throws APException {
    	String[] spec = {"商品代碼", "TIER LEVEL", "RR風險等級", "轉換手續費%", "轉換手續費(本行)%", "申購手續費%", "申購手續費(本行)%",
				"嘉實經理費%", "分銷費%", "12B-1 FEE%", "其他經銷費%", "經銷費合計(表單數字)%", "經理費(本行)%", "教育訓練獎勵金(萬)",
				"其他報酬_投信", "其他報酬_總代理人", "其他報酬_境外基金結構", "IPO預購起日", "IPO預購迄日", "IPO募集起日", "IPO募集迄日"};

        for (int i = 0; i < spec.length; i++) {
            if (!spec[i].equals(header[i]))
                throw new APException(header[i] + ":上傳格式錯誤，請下載範例檔案");
        }
    }

    /** 非境外私募基金：檢查資料是否正確並配置data map **/
    private void checkAndConfigureReviewMap(String[] row, Map data) throws JBranchException {
		String[] keys = {"PRD_ID", "TIER_LEVEL", "RRNOTES", "FEE", "FEE1", "FEE2", "FEE3", "M_FEE",
				"分銷費(該欄位PM不上傳資料，單純顯示)", "TB1_FEE", "OTHER_DISTRIBUTION", "經銷費合計(表單數字)(該欄位PM不上傳資料，單純顯示)",
				"經理費(本行)(該欄位PM不上傳資料，單純顯示)", "TRAIN", "OTHER_REWARD_INV", "OTHER_REWARD_AGN", "OTHER_REWARD_FRN",
				"IPO_START", "IPO_END", "IPO_RAISE_START", "IPO_RAISE_END"};
		for (int i = 0; i < keys.length; i++) {
			//分銷費、經銷費合計(表單數字)、經理費(本行) 欄位單純顯示在 Excel 上，PM 並不上傳資料，所以略過
			if (i == 8 || i == 11 || i == 12) continue;
			checkPrecisionAndUtf8Length(row, i, data, keys[i]);
		}
		configureBasicInfo(data);
    }

    /** 境外私募基金：檢查資料是否正確並配置data map **/
    private void checkAndConfigureReviewMap_OPF(String[] row, Map data) throws JBranchException {
		String[] keys = {"PRD_ID", "TIER_LEVEL", "RRNOTES", "FEE", "FEE1", "FEE2", "FEE3", "M_FEE",
				"DISTRIBUTION_RATE", "TB1_FEE", "OTHER_DISTRIBUTION", "DISTRIBUTION_FEE",
				"M_FEE_RATE", "TRAIN", "OTHER_REWARD_INV", "OTHER_REWARD_AGN", "OTHER_REWARD_FRN",
				"IPO_START", "IPO_END", "IPO_RAISE_START", "IPO_RAISE_END"};
		for (int i = 0; i < keys.length; i++) {
			checkPrecisionAndUtf8Length(row, i, data, keys[i]);
		}
		configureBasicInfo(data);
    }
    
    /** 準備基本參數 **/
	private void configureBasicInfo(Map data) throws JBranchException {
		String loginId = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
		Date date = new Date(System.currentTimeMillis());
		data.put("CREATOR", loginId);
		data.put("CREATETIME", date);
		data.put("MODIFIER", loginId);
		data.put("LASTUPDATE", date);
	}

	/** 確認資料的精確度，以及其資料長度是否在UTF-8特定長度的規範內 **/
    private void checkPrecisionAndUtf8Length(String[] row, int index, Map data, String key) throws APException {
		if (index >= 3 && index <= 13) { // 確認精確度
			if (StringUtils.isNotBlank(row[index])) {
				BigDecimal num = new BigDecimal(row[index]);
				// VARCHAR2(10 BYTE)
				if (num.precision() > 10) throw new APException(row[0] + ":" + row[index]);
				data.put(key, num.toString());
			} else data.put(key, null);
		} else { // 確認UTF-8的長度
			if(utf_8_length(row[index]) > getSpecLenth(index)) throw new APException(index == 0? row[index]: (row[0] + ":" + row[index]));
			//#2075:去除全形空白
			else data.put(key, index == 0? row[index].replace("\u3000", "").trim(): row[index]);
		}

    }

    /** 取得規定長度 **/
	private int getSpecLenth(int index) {
		return index == 0? 16: index == 2? 3: (index == 15 || index == 16)?8 :10;
	}

	/**
     * 下載通路報酬範例檔
     * @param body
     * @param header
     * @throws Exception
     */
    public void downloadSimple(Object body, IPrimitiveMap header) {
		notifyClientToDownloadFile("doc//PRD//PRD232_EXAMPLE.csv", "上傳指定商品代碼範例.csv");
	    this.sendRtnObject(null);
	}
    
	/** 下載參數檔(投信、代理人) 
	 * @throws JBranchException 
	 * @throws DAOException **/
    public void download(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
    	dam = this.getDataAccessManager();
    	QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.FUND_COMPANY_TYPE' ");
    	queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> paramList = dam.exeQuery(queryCondition);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "投信/總代理人/境外基金結構參數表_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		if (paramList.size() == 0) {
			String[] records = new String[2];
			records[0] = "查無資料";
			listCSV.add(records);
		} else {
			for (Map<String, Object> map : paramList) {
				String[] records = new String[2];
				int i = 0;

				records[i] = checkIsNull(map, "PARAM_CODE");
				records[++i] = checkIsNull(map, "PARAM_NAME"); // 最新異動日期

				listCSV.add(records);
			}
		}
		// header
		String[] csvHeader = new String[2];
		int j = 0;
		csvHeader[j] = "基金公司代碼";
		csvHeader[++j] = "投信/總代理人/境外基金結構";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);

		this.sendRtnObject(null);
	}
    
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	//取得境外私募基金註記
	private String getOPFYN(String prdId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
    	QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT NVL(OVS_PRIVATE_YN, 'N') AS OVS_PRIVATE_YN FROM TBPRD_FUND WHERE PRD_ID = :prdId ");
    	queryCondition.setObject("prdId", prdId);
    	queryCondition.setQueryString(sql.toString());    	
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return CollectionUtils.isEmpty(list) ? "N" : list.get(0).get("OVS_PRIVATE_YN").toString();
	}
}