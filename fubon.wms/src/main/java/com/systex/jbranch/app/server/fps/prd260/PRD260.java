package com.systex.jbranch.app.server.fps.prd260;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.systex.jbranch.app.common.fps.table.TBPRD_CUSTOMIZEDPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_CUSTOMIZEDVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_CUSTOMIZED_REVIEWVO;
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
 * prd260
 * 
 * @author moron
 * @date 2016/08/24
 * @spec null
 */
@Component("prd260")
@Scope("request")
public class PRD260 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD260.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD260InputVO inputVO = (PRD260InputVO) body;
		PRD260OutputVO return_VO = new PRD260OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD260' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		switch(inputVO.getPtype()) {
			case "BND":
				sql.append("SELECT a.SEQ,a.PRD_ID,a.BOND_CNAME_A,a.PTYPE,a.REGION_CENTER_ID,a.BRANCH_AREA_ID,a.BRANCH_NBR,a.CUST_ID,a.ACT_TYPE,a.REVIEW_STATUS,a.LASTUPDATE,a.CREATOR,b.REGION_CENTER_NAME,b.BRANCH_AREA_NAME,b.BRANCH_NAME FROM ( ");
				break;
			case "SI":
				sql.append("SELECT a.SEQ,a.PRD_ID,a.SI_CNAME,a.PTYPE,a.REGION_CENTER_ID,a.BRANCH_AREA_ID,a.BRANCH_NBR,a.CUST_ID,a.ACT_TYPE,a.REVIEW_STATUS,a.LASTUPDATE,a.CREATOR,b.REGION_CENTER_NAME,b.BRANCH_AREA_NAME,b.BRANCH_NAME FROM ( ");
				break;
			case "SN":
				sql.append("SELECT a.SEQ,a.PRD_ID,a.SN_CNAME_A,a.PTYPE,a.REGION_CENTER_ID,a.BRANCH_AREA_ID,a.BRANCH_NBR,a.CUST_ID,a.ACT_TYPE,a.REVIEW_STATUS,a.LASTUPDATE,a.CREATOR,b.REGION_CENTER_NAME,b.BRANCH_AREA_NAME,b.BRANCH_NAME FROM ( ");
				break;
		}
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			// ALL
			if (StringUtils.isBlank(inputVO.getStatus())) {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,cust.PRD_ID,bond.BOND_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_BOND bond on cust.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W') ");
						break;
					case "SI":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,cust.PRD_ID,si.SI_CNAME,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SI si on cust.PRD_ID = si.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W') ");
						break;
					case "SN":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,cust.PRD_ID,sn.SN_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SN sn on cust.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W') ");
						break;
				}
			}
			// 已生效
			else if (StringUtils.equals("Y", inputVO.getStatus())) {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT null as SEQ,cust.PRD_ID,bond.BOND_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_BOND bond on cust.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W') ");
						break;
					case "SI":
						sql.append("SELECT null as SEQ,cust.PRD_ID,si.SI_CNAME,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SI si on cust.PRD_ID = si.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W') ");
						break;
					case "SN":
						sql.append("SELECT null as SEQ,cust.PRD_ID,sn.SN_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SN sn on cust.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W') ");
						break;
				}
			}
			// 待核准
			else if (StringUtils.equals("W", inputVO.getStatus())) {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						break;
					case "SI":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						break;
					case "SN":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,rw.CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						break;
				}
			}
			// 已刪除
			else {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						break;
					case "SI":
						sql.append("SELECT null as SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						break;
					case "SN":
						sql.append("SELECT null as SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						break;
				}
			}
		}
		else {
			// ALL
			if (StringUtils.isBlank(inputVO.getStatus())) {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("AND rw.CREATOR = :creator ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("AND rw.CREATOR = :creator ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,cust.PRD_ID,bond.BOND_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_BOND bond on cust.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
						break;
					case "SI":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("AND rw.CREATOR = :creator ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("AND rw.CREATOR = :creator ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,cust.PRD_ID,si.SI_CNAME,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SI si on cust.PRD_ID = si.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
						break;
					case "SN":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("AND rw.CREATOR = :creator ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("AND rw.CREATOR = :creator ");
						sql.append("UNION ");
						sql.append("SELECT null as SEQ,cust.PRD_ID,sn.SN_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SN sn on cust.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
						break;
				}
			}
			// 已生效
			else if (StringUtils.equals("Y", inputVO.getStatus())) {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT null as SEQ,cust.PRD_ID,bond.BOND_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_BOND bond on cust.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
						break;
					case "SI":
						sql.append("SELECT null as SEQ,cust.PRD_ID,si.SI_CNAME,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SI si on cust.PRD_ID = si.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
						break;
					case "SN":
						sql.append("SELECT null as SEQ,cust.PRD_ID,sn.SN_CNAME_A,cust.PTYPE,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR,cust.CUST_ID,cust.ACT_TYPE,cust.REVIEW_STATUS,cust.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED cust left join TBPRD_SN sn on cust.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE NVL(cust.REVIEW_STATUS, 'Y') = 'Y' ");
						sql.append("AND (cust.PTYPE,cust.PRD_ID,cust.REGION_CENTER_ID,cust.BRANCH_AREA_ID,cust.BRANCH_NBR) NOT IN (SELECT PTYPE,PRD_ID,REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR FROM TBPRD_CUSTOMIZED_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
						break;
				}
			}
			// 待核准
			else if (StringUtils.equals("W", inputVO.getStatus())) {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("AND rw.CREATOR = :creator ");
						break;
					case "SI":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("AND rw.CREATOR = :creator ");
						break;
					case "SN":
						sql.append("SELECT rw.SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
						sql.append("AND rw.CREATOR = :creator ");
						break;
				}
			}
			// 已刪除
			else {
				switch(inputVO.getPtype()) {
					case "BND":
						sql.append("SELECT null as SEQ,rw.PRD_ID,bond.BOND_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_BOND bond on rw.PRD_ID = bond.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("AND rw.CREATOR = :creator ");
						break;
					case "SI":
						sql.append("SELECT null as SEQ,rw.PRD_ID,si.SI_CNAME,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SI si on rw.PRD_ID = si.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("AND rw.CREATOR = :creator ");
						break;
					case "SN":
						sql.append("SELECT null as SEQ,rw.PRD_ID,sn.SN_CNAME_A,rw.PTYPE,rw.REGION_CENTER_ID,rw.BRANCH_AREA_ID,rw.BRANCH_NBR,rw.CUST_ID,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.LASTUPDATE,null as CREATOR ");
						sql.append("FROM TBPRD_CUSTOMIZED_REVIEW rw left join TBPRD_SN sn on rw.PRD_ID = sn.PRD_ID ");
						sql.append("WHERE rw.REVIEW_STATUS = 'Y' and rw.ACT_TYPE = 'D' ");
						sql.append("AND rw.CREATOR = :creator ");
						break;
				}
			}
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") a left join VWORG_DEFN_INFO b on a.BRANCH_NBR = b.BRANCH_NBR where a.PTYPE = :ptype ");
		queryCondition.setObject("ptype", inputVO.getPtype());
		if (inputVO.getLast_sDate() != null) {
			sql.append("and a.LASTUPDATE >= :start ");
			queryCondition.setObject("start", inputVO.getLast_sDate());
		}
		if (inputVO.getLast_eDate() != null) {
			// to max hour
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(inputVO.getLast_eDate());
			calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			sql.append("and a.LASTUPDATE <= :end ");
			queryCondition.setObject("end", calendar.getTime());
		}
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and a.PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCname())) {
			switch(inputVO.getPtype()) {
				case "BND":
					sql.append("and a.BOND_CNAME_A like :cname ");
					break;
				case "SI":
					sql.append("and a.SI_CNAME like :cname ");
					break;
				case "SN":
					sql.append("and a.SN_CNAME_A like :cname ");
					break;
			}
			queryCondition.setObject("cname", "%" + inputVO.getCname() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("and a.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		if (!StringUtils.isBlank(inputVO.getRegion())) {
			sql.append("and a.REGION_CENTER_ID = :region ");
			queryCondition.setObject("region", inputVO.getRegion());
		}
		if (!StringUtils.isBlank(inputVO.getArea())) {
			sql.append("and a.BRANCH_AREA_ID = :area ");
			queryCondition.setObject("area", inputVO.getArea());
		}
		if (!StringUtils.isBlank(inputVO.getBranch())) {
			sql.append("and a.BRANCH_NBR = :nbr ");
			queryCondition.setObject("nbr", inputVO.getBranch());
		}
		sql.append("ORDER BY CASE WHEN a.REVIEW_STATUS = 'W' THEN 0 WHEN a.REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC, CASE WHEN a.ACT_TYPE = 'D' THEN 1 ELSE 0 END, a.PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD260InputVO inputVO = (PRD260InputVO) body;
		PRD260OutputVO return_VO = new PRD260OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch(inputVO.getPtype()) {
			case "BND":
				sql.append("SELECT PRD_ID,BOND_CNAME_A FROM TBPRD_BOND where PRD_ID = :id ");
				break;
			case "SI":
				sql.append("SELECT PRD_ID,SI_CNAME FROM TBPRD_SI where PRD_ID = :id ");
				break;
			case "SN":
				sql.append("SELECT PRD_ID,SN_CNAME_A FROM TBPRD_SN where PRD_ID = :id ");
				break;
		}
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			switch(inputVO.getPtype()) {
				case "BND":
					return_VO.setCname(ObjectUtils.toString(list.get(0).get("BOND_CNAME_A")));
					break;
				case "SI":
					return_VO.setCname(ObjectUtils.toString(list.get(0).get("SI_CNAME")));
					break;
				case "SN":
					return_VO.setCname(ObjectUtils.toString(list.get(0).get("SN_CNAME_A")));
					break;
			}
			return_VO.setCanEdit(true);
		}
		else {
			return_VO.setCanEdit(false);
			return_VO.setErrorMsg("ehl_01_common_026");
		}
		// true 再繼續檢查
		if(return_VO.getCanEdit()) {
			// TBPRD_CUSTOMIZED
			if(StringUtils.isBlank(inputVO.getCust_id())) {
				// 選擇分行
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT DISTINCT BRANCH_NBR from TBPRD_CUSTOMIZED where PTYPE = :type and PRD_ID = :prd_id ");
				sql.append("and REGION_CENTER_ID = :region ");
				queryCondition.setObject("type", inputVO.getPtype());
				queryCondition.setObject("prd_id", inputVO.getPrd_id());
				queryCondition.setObject("region", inputVO.getRegion());
				if(StringUtils.isNotBlank(inputVO.getArea())) {
					sql.append("and BRANCH_AREA_ID = :branch ");
					queryCondition.setObject("branch", inputVO.getArea());
				}
				if(StringUtils.isNotBlank(inputVO.getBranch())) {
					sql.append("and BRANCH_NBR = :bra_nbr ");
					queryCondition.setObject("bra_nbr", inputVO.getBranch());
				}
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0) {
					List<String> braList = new ArrayList<String>();
					for(Map<String, Object> map : list2) {
						braList.add(ObjectUtils.toString(map.get("BRANCH_NBR")));
					}
					return_VO.setResultList(braList);
					return_VO.setCanEdit(false);
					return_VO.setErrorMsg("ehl_01_prd260_003");
				}
			}
			// 查詢 by cust_id
			else {
				// get branch
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT INF.REGION_CENTER_ID, INF.BRANCH_AREA_ID, MAS.BRA_NBR, MAS.CUST_ID ");
				sql.append("FROM TBCRM_CUST_MAST MAS ");
				sql.append("LEFT JOIN VWORG_DEFN_INFO INF ON INF.BRANCH_NBR = MAS.BRA_NBR ");
				sql.append("WHERE MAS.CUST_ID = :cust_id ");
				queryCondition.setObject("cust_id", inputVO.getCust_id());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> cust_list = dam.exeQuery(queryCondition);
				// check exist
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT BRANCH_NBR from TBPRD_CUSTOMIZED where PTYPE = :type and PRD_ID = :prd_id ");
				sql.append("and REGION_CENTER_ID = :region and BRANCH_AREA_ID = :branch and BRANCH_NBR = :bra_nbr ");
				sql.append("and (CUST_ID = :cust_id or CUST_ID = 'ALL') ");
				queryCondition.setObject("type", inputVO.getPtype());
				queryCondition.setObject("prd_id", inputVO.getPrd_id());
				queryCondition.setObject("region", cust_list.get(0).get("REGION_CENTER_ID"));
				queryCondition.setObject("branch", cust_list.get(0).get("BRANCH_AREA_ID"));
				queryCondition.setObject("bra_nbr", cust_list.get(0).get("BRA_NBR"));
				queryCondition.setObject("cust_id", inputVO.getCust_id());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0) {
					List<String> braList = new ArrayList<String>();
					for(Map<String, Object> map : list2) {
						braList.add(ObjectUtils.toString(map.get("BRANCH_NBR")));
					}
					return_VO.setResultList(braList);
					return_VO.setCanEdit(false);
					return_VO.setErrorMsg("ehl_01_prd260_003");
				}
			}
			// true 再繼續檢查
			if(return_VO.getCanEdit()) {
				// TBPRD_CUSTOMIZED_REVIEW
				if(StringUtils.isBlank(inputVO.getCust_id())) {
					// 選擇分行
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("SELECT DISTINCT BRANCH_NBR from TBPRD_CUSTOMIZED_REVIEW where PTYPE = :type and PRD_ID = :prd_id and REVIEW_STATUS = 'W' ");
					sql.append("and REGION_CENTER_ID = :region ");
					queryCondition.setObject("type", inputVO.getPtype());
					queryCondition.setObject("prd_id", inputVO.getPrd_id());
					queryCondition.setObject("region", inputVO.getRegion());
					if(StringUtils.isNotBlank(inputVO.getArea())) {
						sql.append("and BRANCH_AREA_ID = :branch ");
						queryCondition.setObject("branch", inputVO.getArea());
					}
					if(StringUtils.isNotBlank(inputVO.getBranch())) {
						sql.append("and BRANCH_NBR = :bra_nbr ");
						queryCondition.setObject("bra_nbr", inputVO.getBranch());
					}
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
					if (list3.size() > 0) {
						List<String> braList = new ArrayList<String>();
						for(Map<String, Object> map : list3) {
							braList.add(ObjectUtils.toString(map.get("BRANCH_NBR")));
						}
						return_VO.setResultList(braList);
						return_VO.setCanEdit(false);
						return_VO.setErrorMsg("ehl_01_prd260_004");
					}
				}
				// 查詢 by cust_id
				else {
					// get branch
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("SELECT INF.REGION_CENTER_ID, INF.BRANCH_AREA_ID, MAS.BRA_NBR, MAS.CUST_ID ");
					sql.append("FROM TBCRM_CUST_MAST MAS ");
					sql.append("LEFT JOIN VWORG_DEFN_INFO INF ON INF.BRANCH_NBR = MAS.BRA_NBR ");
					sql.append("WHERE MAS.CUST_ID = :cust_id ");
					queryCondition.setObject("cust_id", inputVO.getCust_id());
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> cust_list = dam.exeQuery(queryCondition);
					// check exist
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sql = new StringBuffer();
					sql.append("SELECT BRANCH_NBR from TBPRD_CUSTOMIZED_REVIEW where PTYPE = :type and PRD_ID = :prd_id and REVIEW_STATUS = 'W' ");
					sql.append("and REGION_CENTER_ID = :region and BRANCH_AREA_ID = :branch and BRANCH_NBR = :bra_nbr ");
					sql.append("and (CUST_ID = :cust_id or CUST_ID = 'ALL') ");
					queryCondition.setObject("type", inputVO.getPtype());
					queryCondition.setObject("prd_id", inputVO.getPrd_id());
					queryCondition.setObject("region", cust_list.get(0).get("REGION_CENTER_ID"));
					queryCondition.setObject("branch", cust_list.get(0).get("BRANCH_AREA_ID"));
					queryCondition.setObject("bra_nbr", cust_list.get(0).get("BRA_NBR"));
					queryCondition.setObject("cust_id", inputVO.getCust_id());
					queryCondition.setQueryString(sql.toString());
					List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
					if (list2.size() > 0) {
						List<String> braList = new ArrayList<String>();
						for(Map<String, Object> map : list2) {
							braList.add(ObjectUtils.toString(map.get("BRANCH_NBR")));
						}
						return_VO.setResultList(braList);
						return_VO.setCanEdit(false);
						return_VO.setErrorMsg("ehl_01_prd260_003");
					}
				}
			}
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void checkCUST(Object body, IPrimitiveMap header) throws JBranchException {
		PRD260InputVO inputVO = (PRD260InputVO) body;
		PRD260OutputVO return_VO = new PRD260OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(*) as COUNT from TBCRM_CUST_MAST where CUST_ID = :id ");
		queryCondition.setObject("id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		BigDecimal count = new BigDecimal(ObjectUtils.toString(list.get(0).get("COUNT")));
		if (count.compareTo(new BigDecimal(0)) > 0)
			return_VO.setCanEdit(false);
		else
			return_VO.setCanEdit(true);
		this.sendRtnObject(return_VO);
	}
	
	public void addData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD260InputVO inputVO = (PRD260InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch(inputVO.getPtype()) {
			case "BND":
				sql.append("SELECT PRD_ID FROM TBPRD_BOND where PRD_ID = :id ");
				break;
			case "SI":
				sql.append("SELECT PRD_ID FROM TBPRD_SI where PRD_ID = :id ");
				break;
			case "SN":
				sql.append("SELECT PRD_ID FROM TBPRD_SN where PRD_ID = :id ");
				break;
		}
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() == 0)
			throw new APException("ehl_01_common_026");
		// CUST
		if(StringUtils.isNotBlank(inputVO.getCust_id())) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(*) as COUNT from TBCRM_CUST_MAST where CUST_ID = :id ");
			queryCondition.setObject("id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			BigDecimal count = new BigDecimal(ObjectUtils.toString(list2.get(0).get("COUNT")));
			if (count.compareTo(new BigDecimal(0)) <= 0)
				throw new APException("ehl_01_prd260_001");
		}
		// 選擇分行
		if(StringUtils.isBlank(inputVO.getCust_id())) {
			// TBPRD_CUSTOMIZED
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(*) as COUNT from TBPRD_CUSTOMIZED where PTYPE = :type and PRD_ID = :prd_id ");
			sql.append("and REGION_CENTER_ID = :region ");
			queryCondition.setObject("type", inputVO.getPtype());
			queryCondition.setObject("prd_id", inputVO.getPrd_id());
			queryCondition.setObject("region", inputVO.getRegion());
			if(StringUtils.isNotBlank(inputVO.getArea())) {
				sql.append("and BRANCH_AREA_ID = :branch ");
				queryCondition.setObject("branch", inputVO.getArea());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch())) {
				sql.append("and BRANCH_NBR = :bra_nbr ");
				queryCondition.setObject("bra_nbr", inputVO.getBranch());
			}
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			BigDecimal count2 = new BigDecimal(ObjectUtils.toString(list2.get(0).get("COUNT")));
			if (count2.compareTo(new BigDecimal(0)) > 0)
				throw new APException("ehl_01_prd260_003");
			
			// TBPRD_CUSTOMIZED_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(*) as COUNT from TBPRD_CUSTOMIZED_REVIEW where PTYPE = :type and PRD_ID = :prd_id and REVIEW_STATUS = 'W' ");
			sql.append("and REGION_CENTER_ID = :region ");
			queryCondition.setObject("type", inputVO.getPtype());
			queryCondition.setObject("prd_id", inputVO.getPrd_id());
			queryCondition.setObject("region", inputVO.getRegion());
			if(StringUtils.isNotBlank(inputVO.getArea())) {
				sql.append("and BRANCH_AREA_ID = :branch ");
				queryCondition.setObject("branch", inputVO.getArea());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch())) {
				sql.append("and BRANCH_NBR = :bra_nbr ");
				queryCondition.setObject("bra_nbr", inputVO.getBranch());
			}
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			BigDecimal count3 = new BigDecimal(ObjectUtils.toString(list3.get(0).get("COUNT")));
			if (count3.compareTo(new BigDecimal(0)) > 0)
				throw new APException("ehl_01_prd260_004");
		}
		// 查詢 by cust_id
		else {
			// get branch
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT INF.REGION_CENTER_ID, INF.BRANCH_AREA_ID, MAS.BRA_NBR, MAS.CUST_ID ");
			sql.append("FROM TBCRM_CUST_MAST MAS ");
			sql.append("LEFT JOIN VWORG_DEFN_INFO INF ON INF.BRANCH_NBR = MAS.BRA_NBR ");
			sql.append("WHERE MAS.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> cust_list = dam.exeQuery(queryCondition);
			// TBPRD_CUSTOMIZED
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(*) as COUNT from TBPRD_CUSTOMIZED where PTYPE = :type and PRD_ID = :prd_id ");
			sql.append("and REGION_CENTER_ID = :region and BRANCH_AREA_ID = :branch and BRANCH_NBR = :bra_nbr ");
			sql.append("and (CUST_ID = :cust_id or CUST_ID = 'ALL') ");
			queryCondition.setObject("type", inputVO.getPtype());
			queryCondition.setObject("prd_id", inputVO.getPrd_id());
			queryCondition.setObject("region", cust_list.get(0).get("REGION_CENTER_ID"));
			queryCondition.setObject("branch", cust_list.get(0).get("BRANCH_AREA_ID"));
			queryCondition.setObject("bra_nbr", cust_list.get(0).get("BRA_NBR"));
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			BigDecimal count2 = new BigDecimal(ObjectUtils.toString(list2.get(0).get("COUNT")));
			if (count2.compareTo(new BigDecimal(0)) > 0)
				throw new APException("ehl_01_prd260_003");
			
			// TBPRD_CUSTOMIZED_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(*) as COUNT from TBPRD_CUSTOMIZED_REVIEW where PTYPE = :type and PRD_ID = :prd_id and REVIEW_STATUS = 'W' ");
			sql.append("and REGION_CENTER_ID = :region and BRANCH_AREA_ID = :branch and BRANCH_NBR = :bra_nbr ");
			sql.append("and (CUST_ID = :cust_id or CUST_ID = 'ALL') ");
			queryCondition.setObject("type", inputVO.getPtype());
			queryCondition.setObject("prd_id", inputVO.getPrd_id());
			queryCondition.setObject("region", cust_list.get(0).get("REGION_CENTER_ID"));
			queryCondition.setObject("branch", cust_list.get(0).get("BRANCH_AREA_ID"));
			queryCondition.setObject("bra_nbr", cust_list.get(0).get("BRA_NBR"));
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			BigDecimal count3 = new BigDecimal(ObjectUtils.toString(list3.get(0).get("COUNT")));
			if (count3.compareTo(new BigDecimal(0)) > 0)
				throw new APException("ehl_01_prd260_004");
		}
		// 2017/8/23
		if(StringUtils.isBlank(inputVO.getCust_id())) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("select REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR from VWORG_DEFN_INFO WHERE REGION_CENTER_ID = :region ");
			queryCondition.setObject("region", inputVO.getRegion());
			if(StringUtils.isNotBlank(inputVO.getArea())) {
				sql.append("and BRANCH_AREA_ID = :branch ");
				queryCondition.setObject("branch", inputVO.getArea());
			}
			if(StringUtils.isNotBlank(inputVO.getBranch())) {
				sql.append("and BRANCH_NBR = :bra_nbr ");
				queryCondition.setObject("bra_nbr", inputVO.getBranch());
			}
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> bra_ans = dam.exeQuery(queryCondition);
			// add TBPRD_CUSTOMIZED_REVIEW
			for(Map<String, Object> ans : bra_ans) {
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBPRD_CUSTOMIZED_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				
				TBPRD_CUSTOMIZED_REVIEWVO vo = new TBPRD_CUSTOMIZED_REVIEWVO();
				vo.setSEQ(seqNo);
				vo.setPTYPE(inputVO.getPtype());
				vo.setPRD_ID(inputVO.getPrd_id());
				vo.setCUST_ID("ALL");
				vo.setREGION_CENTER_ID(ObjectUtils.toString(ans.get("REGION_CENTER_ID")));
				vo.setBRANCH_AREA_ID(ObjectUtils.toString(ans.get("BRANCH_AREA_ID")));
				vo.setBRANCH_NBR(ObjectUtils.toString(ans.get("BRANCH_NBR")));
				vo.setACT_TYPE("A");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
			}
		} else {
			// seq
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT SQ_TBPRD_CUSTOMIZED_REVIEW.nextval AS SEQ FROM DUAL ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
			BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
			// add TBPRD_CUSTOMIZED_REVIEW
			TBPRD_CUSTOMIZED_REVIEWVO vo = new TBPRD_CUSTOMIZED_REVIEWVO();
			vo.setSEQ(seqNo);
			vo.setPTYPE(inputVO.getPtype());
			vo.setPRD_ID(inputVO.getPrd_id());
			vo.setCUST_ID(inputVO.getCust_id());
			// 查詢 by cust_id
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT INF.REGION_CENTER_ID, INF.BRANCH_AREA_ID, MAS.BRA_NBR, MAS.CUST_ID ");
			sql.append("FROM TBCRM_CUST_MAST MAS ");
			sql.append("LEFT JOIN VWORG_DEFN_INFO INF ON INF.BRANCH_NBR = MAS.BRA_NBR ");
			sql.append("WHERE MAS.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> cust_list = dam.exeQuery(queryCondition);
			if(cust_list.size() > 0) {
				vo.setREGION_CENTER_ID(ObjectUtils.toString(cust_list.get(0).get("REGION_CENTER_ID")));
				vo.setBRANCH_AREA_ID(ObjectUtils.toString(cust_list.get(0).get("BRANCH_AREA_ID")));
				vo.setBRANCH_NBR(ObjectUtils.toString(cust_list.get(0).get("BRA_NBR")));
				if(cust_list.get(0).get("BRA_NBR") == null)
					throw new APException("ehl_01_prd260_002");
			}
			vo.setACT_TYPE("A");
			vo.setREVIEW_STATUS("W");
			dam.create(vo);
		}
		
		this.sendRtnObject(null);
	}
	
	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD260InputVO inputVO = (PRD260InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_CUSTOMIZED_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID from TBPRD_CUSTOMIZED_REVIEW where PTYPE = :type and PRD_ID = :prd_id and REVIEW_STATUS = 'W' ");
		sql.append("and REGION_CENTER_ID = :region and BRANCH_AREA_ID = :branch and BRANCH_NBR = :bra_nbr ");
		sql.append("and CUST_ID = :cust_id ");
		queryCondition.setObject("type", inputVO.getPtype());
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setObject("region", inputVO.getRegion());
		queryCondition.setObject("branch", inputVO.getArea());
		queryCondition.setObject("bra_nbr", inputVO.getBranch());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_CUSTOMIZED_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_CUSTOMIZEDVO vo = new TBPRD_CUSTOMIZEDVO();
		TBPRD_CUSTOMIZEDPK pk = new TBPRD_CUSTOMIZEDPK();
		pk.setPTYPE(inputVO.getPtype());
		pk.setPRD_ID(inputVO.getPrd_id());
		pk.setREGION_CENTER_ID(inputVO.getRegion());
		pk.setBRANCH_AREA_ID(inputVO.getArea());
		pk.setBRANCH_NBR(inputVO.getBranch());
		pk.setCUST_ID(inputVO.getCust_id());
		vo = (TBPRD_CUSTOMIZEDVO) dam.findByPKey(TBPRD_CUSTOMIZEDVO.TABLE_UID, pk);
		if (vo != null) {
			// add TBPRD_CUSTOMIZED_REVIEW
			TBPRD_CUSTOMIZED_REVIEWVO rvo = new TBPRD_CUSTOMIZED_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPTYPE(inputVO.getPtype());
			rvo.setPRD_ID(inputVO.getPrd_id());
			rvo.setCUST_ID(inputVO.getCust_id());
			rvo.setREGION_CENTER_ID(inputVO.getRegion());
			rvo.setBRANCH_AREA_ID(inputVO.getArea());
			rvo.setBRANCH_NBR(inputVO.getBranch());
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
		PRD260InputVO inputVO = (PRD260InputVO) body;
		dam = this.getDataAccessManager();
		
		// 2017/2/22
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_CUSTOMIZED_REVIEWVO rvo = new TBPRD_CUSTOMIZED_REVIEWVO();
			rvo = (TBPRD_CUSTOMIZED_REVIEWVO) dam.findByPKey(TBPRD_CUSTOMIZED_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// TBPRD_CUSTOMIZED
						TBPRD_CUSTOMIZEDVO vo = new TBPRD_CUSTOMIZEDVO();
						TBPRD_CUSTOMIZEDPK pk = new TBPRD_CUSTOMIZEDPK();
						pk.setPTYPE(rvo.getPTYPE());
						pk.setPRD_ID(rvo.getPRD_ID());
						pk.setREGION_CENTER_ID(rvo.getREGION_CENTER_ID());
						pk.setBRANCH_AREA_ID(rvo.getBRANCH_AREA_ID());
						pk.setBRANCH_NBR(rvo.getBRANCH_NBR());
						pk.setCUST_ID(rvo.getCUST_ID());
						vo.setcomp_id(pk);
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("Y");
						dam.create(vo);
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_CUSTOMIZEDVO vo = new TBPRD_CUSTOMIZEDVO();
						TBPRD_CUSTOMIZEDPK pk = new TBPRD_CUSTOMIZEDPK();
						pk.setPTYPE(rvo.getPTYPE());
						pk.setPRD_ID(rvo.getPRD_ID());
						pk.setREGION_CENTER_ID(rvo.getREGION_CENTER_ID());
						pk.setBRANCH_AREA_ID(rvo.getBRANCH_AREA_ID());
						pk.setBRANCH_NBR(rvo.getBRANCH_NBR());
						pk.setCUST_ID(rvo.getCUST_ID());
						vo = (TBPRD_CUSTOMIZEDVO) dam.findByPKey(TBPRD_CUSTOMIZEDVO.TABLE_UID, pk);
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