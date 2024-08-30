package com.systex.jbranch.app.server.fps.prd251;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICEPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICEVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_LIMITED_PRICE_REVIEWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd251
 * 
 * @author moron
 * @date 2016/08/24
 * @spec null
 */
@Component("prd251")
@Scope("request")
public class PRD251 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD251.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD251InputVO inputVO = (PRD251InputVO) body;
		PRD251OutputVO return_VO = new PRD251OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD251' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT SEQ,PRD_ID,BOND_CNAME_A,CUST_ID,LIMITED_PRICE,CHANNEL_FEE,ACT_TYPE,REVIEW_STATUS,LASTUPDATE,CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			// ALL
			if (StringUtils.isBlank(inputVO.getStatus())) {
				sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
				sql.append("UNION ");
				sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
				sql.append("UNION ");
				sql.append("SELECT null as SEQ,pri.PRD_ID,bond.BOND_CNAME_A,pri.CUST_ID,pri.LIMITED_PRICE,pri.CHANNEL_FEE,pri.ACT_TYPE,pri.REVIEW_STATUS,pri.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE pri left join TBPRD_BOND bond on pri.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE NVL(pri.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND (pri.PRD_ID,pri.CUST_ID) NOT IN (SELECT PRD_ID,CUST_ID FROM TBPRD_LIMITED_PRICE_REVIEW WHERE REVIEW_STATUS = 'W') ");
			}
			// 已生效
			else if (StringUtils.equals("Y", inputVO.getStatus())) {
				sql.append("SELECT null as SEQ,pri.PRD_ID,bond.BOND_CNAME_A,pri.CUST_ID,pri.LIMITED_PRICE,pri.CHANNEL_FEE,pri.ACT_TYPE,pri.REVIEW_STATUS,pri.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE pri left join TBPRD_BOND bond on pri.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE NVL(pri.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND (pri.PRD_ID,pri.CUST_ID) NOT IN (SELECT PRD_ID,CUST_ID FROM TBPRD_LIMITED_PRICE_REVIEW WHERE REVIEW_STATUS = 'W') ");
			}
			// 待核准
			else if (StringUtils.equals("W", inputVO.getStatus())) {
				sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			}
			// 已刪除
			else {
				sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
			}
		}
		else {
			// ALL
			if (StringUtils.isBlank(inputVO.getStatus())) {
				sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
				sql.append("AND rw.CREATOR = :creator ");
				sql.append("UNION ");
				sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
				sql.append("AND rw.CREATOR = :creator ");
				sql.append("UNION ");
				sql.append("SELECT null as SEQ,pri.PRD_ID,bond.BOND_CNAME_A,pri.CUST_ID,pri.LIMITED_PRICE,pri.CHANNEL_FEE,pri.ACT_TYPE,pri.REVIEW_STATUS,pri.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE pri left join TBPRD_BOND bond on pri.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE NVL(pri.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND (pri.PRD_ID,pri.CUST_ID) NOT IN (SELECT PRD_ID,CUST_ID FROM TBPRD_LIMITED_PRICE_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			}
			// 已生效
			else if (StringUtils.equals("Y", inputVO.getStatus())) {
				sql.append("SELECT null as SEQ,pri.PRD_ID,bond.BOND_CNAME_A,pri.CUST_ID,pri.LIMITED_PRICE,pri.CHANNEL_FEE,pri.ACT_TYPE,pri.REVIEW_STATUS,pri.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE pri left join TBPRD_BOND bond on pri.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE NVL(pri.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND (pri.PRD_ID,pri.CUST_ID) NOT IN (SELECT PRD_ID,CUST_ID FROM TBPRD_LIMITED_PRICE_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			}
			// 待核准
			else if (StringUtils.equals("W", inputVO.getStatus())) {
				sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
				sql.append("AND rw.CREATOR = :creator ");
			}
			// 已刪除
			else {
				sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.CUST_ID,rw.LIMITED_PRICE,rw.CHANNEL_FEE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
				sql.append("FROM TBPRD_LIMITED_PRICE_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
				sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
				sql.append("AND rw.CREATOR = :creator ");
			}
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") where 1=1 ");
		if (inputVO.getLast_sDate() != null) {
			sql.append("and LASTUPDATE >= :start ");
			queryCondition.setObject("start", inputVO.getLast_sDate());
		}
		if (inputVO.getLast_eDate() != null) {
			// to max hour
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(inputVO.getLast_eDate());
			calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			sql.append("and LASTUPDATE <= :end ");
			queryCondition.setObject("end", calendar.getTime());
		}
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCname())) {
			sql.append("and BOND_CNAME_A like :cname ");
			queryCondition.setObject("cname", "%" + inputVO.getCname() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("and CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC, CASE WHEN ACT_TYPE = 'D' THEN 1 ELSE 0 END, PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD251InputVO inputVO = (PRD251InputVO) body;
		PRD251OutputVO return_VO = new PRD251OutputVO();
		dam = this.getDataAccessManager();
		
		// TBPRD_BOND
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID,BOND_CNAME_A FROM TBPRD_BOND where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			return_VO.setBond_name(ObjectUtils.toString(list.get(0).get("BOND_CNAME_A")));
			return_VO.setCanEdit(true);
		}
		else {
			return_VO.setCanEdit(false);
			return_VO.setErrorMsg("ehl_01_common_026");
		}
		// TBPRD_LIMITED_PRICE
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_LIMITED_PRICE where PRD_ID = :prd_id and CUST_ID = :cust_id ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0) {
			return_VO.setCanEdit(false);
			return_VO.setErrorMsg("ehl_01_common_027");
		}
		// TBPRD_LIMITED_PRICE_REVIEW
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_LIMITED_PRICE_REVIEW where PRD_ID = :prd_id and CUST_ID = :cust_id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() > 0) {
			return_VO.setCanEdit(false);
			return_VO.setErrorMsg("ehl_01_common_028");
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void addData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD251InputVO inputVO = (PRD251InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_BOND
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");
		// TBPRD_LIMITED_PRICE
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_LIMITED_PRICE where PRD_ID = :prd_id and CUST_ID = :cust_id ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if (list2.size() > 0)
			throw new APException("ehl_01_common_026");
		// TBPRD_LIMITED_PRICE_REVIEW
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_LIMITED_PRICE_REVIEW where PRD_ID = :prd_id and CUST_ID = :cust_id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if (list3.size() > 0)
			throw new APException("ehl_01_common_026");
		// CUST
		if(StringUtils.isNotBlank(inputVO.getCust_id())) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(*) as COUNT from TBCRM_CUST_MAST where CUST_ID = :id ");
			queryCondition.setObject("id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
			BigDecimal count = new BigDecimal(ObjectUtils.toString(list4.get(0).get("COUNT")));
			if (count.compareTo(new BigDecimal(0)) <= 0)
				throw new APException("ehl_01_prd260_001");
		}
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_LIMITED_PRICE_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_LIMITED_PRICE_REVIEW
		TBPRD_LIMITED_PRICE_REVIEWVO vo = new TBPRD_LIMITED_PRICE_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		vo.setCUST_ID(inputVO.getCust_id());
		vo.setLIMITED_PRICE(inputVO.getLimit_price());
		vo.setCHANNEL_FEE(inputVO.getChannel_fee());
		vo.setACT_TYPE("A");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		this.sendRtnObject(null);
	}
	
	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
//		PRD251InputVO inputVO = (PRD251InputVO) body;
//		dam = this.getDataAccessManager();
//		
//		// check again
//		// TBPRD_LIMITED_PRICE_REVIEW
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT PRD_ID FROM TBPRD_LIMITED_PRICE_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
//		queryCondition.setObject("id", inputVO.getPrd_id());
//		queryCondition.setQueryString(sql.toString());
//		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
//		if (list3.size() > 0)
//			throw new APException("ehl_01_common_026");
//		
//		// seq
//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sql = new StringBuffer();
//		sql.append("SELECT SQ_TBPRD_LIMITED_PRICE_REVIEW.nextval AS SEQ FROM DUAL ");
//		queryCondition.setQueryString(sql.toString());
//		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
//		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
//		// add TBPRD_LIMITED_PRICE_REVIEW
//		TBPRD_LIMITED_PRICE_REVIEWVO vo = new TBPRD_LIMITED_PRICE_REVIEWVO();
//		vo.setSEQ(seqNo);
//		vo.setPRD_ID(inputVO.getPrd_id());
//		vo.setCUST_ID(inputVO.getCust_id());
//		if(StringUtils.isNotBlank(inputVO.getLimit_price()))
//			vo.setLIMITED_PRICE(new BigDecimal(inputVO.getLimit_price()));
//		if(StringUtils.isNotBlank(inputVO.getChannel_fee()))
//			vo.setCHANNEL_FEE(new BigDecimal(inputVO.getChannel_fee()));
//		vo.setACT_TYPE("M");
//		vo.setREVIEW_STATUS("W");
//		dam.create(vo);
//		this.sendRtnObject(null);
	}
	
	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD251InputVO inputVO = (PRD251InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_LIMITED_PRICE_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_LIMITED_PRICE_REVIEW where PRD_ID = :prd_id and CUST_ID = :cust_id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_026");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_LIMITED_PRICE_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_LIMITED_PRICEVO vo = new TBPRD_LIMITED_PRICEVO();
		TBPRD_LIMITED_PRICEPK pk = new TBPRD_LIMITED_PRICEPK();
		pk.setPRD_ID(inputVO.getPrd_id());
		pk.setCUST_ID(inputVO.getCust_id());
		vo = (TBPRD_LIMITED_PRICEVO) dam.findByPKey(TBPRD_LIMITED_PRICEVO.TABLE_UID, pk);
		if (vo != null) {
			// add TBPRD_LIMITED_PRICE_REVIEW
			TBPRD_LIMITED_PRICE_REVIEWVO rvo = new TBPRD_LIMITED_PRICE_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(inputVO.getPrd_id());
			rvo.setCUST_ID(inputVO.getCust_id());
			rvo.setLIMITED_PRICE(vo.getLIMITED_PRICE());
			rvo.setCHANNEL_FEE(vo.getCHANNEL_FEE());
			rvo.setACT_TYPE("D");
			rvo.setREVIEW_STATUS("W");
			dam.create(rvo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}
	
	public void review(Object body, IPrimitiveMap header) throws JBranchException {
		PRD251InputVO inputVO = (PRD251InputVO) body;
		dam = this.getDataAccessManager();
		
		// 2017/2/22
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_LIMITED_PRICE_REVIEWVO rvo = new TBPRD_LIMITED_PRICE_REVIEWVO();
			rvo = (TBPRD_LIMITED_PRICE_REVIEWVO) dam.findByPKey(TBPRD_LIMITED_PRICE_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_LIMITED_PRICE
						TBPRD_LIMITED_PRICEVO vo = new TBPRD_LIMITED_PRICEVO();
						TBPRD_LIMITED_PRICEPK pk = new TBPRD_LIMITED_PRICEPK();
						pk.setPRD_ID(rvo.getPRD_ID());
						pk.setCUST_ID(rvo.getCUST_ID());
						vo.setcomp_id(pk);
						vo.setLIMITED_PRICE(rvo.getLIMITED_PRICE());
						vo.setCHANNEL_FEE(rvo.getCHANNEL_FEE());
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						// no edit
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_LIMITED_PRICEVO vo = new TBPRD_LIMITED_PRICEVO();
						TBPRD_LIMITED_PRICEPK pk = new TBPRD_LIMITED_PRICEPK();
						pk.setPRD_ID(rvo.getPRD_ID());
						pk.setCUST_ID(rvo.getCUST_ID());
						vo = (TBPRD_LIMITED_PRICEVO) dam.findByPKey(TBPRD_LIMITED_PRICEVO.TABLE_UID, pk);
						if (vo != null) {
							dam.delete(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
				}
				rvo.setREVIEW_STATUS(inputVO.getStatus());
				dam.update(rvo);
			} else
				throw new APException("ehl_01_common_001");
		}
		
		this.sendRtnObject(null);
	}
	
	
	
}