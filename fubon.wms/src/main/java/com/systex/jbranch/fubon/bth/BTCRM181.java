package com.systex.jbranch.fubon.bth;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.server.fps.cmfpg000.CMFPG000;
import com.systex.jbranch.app.server.fps.kyc320.KYC320;
import com.systex.jbranch.app.server.fps.kyc320.KYC320InputVO;
import com.systex.jbranch.app.server.fps.kyc320.KYC320OutputVO;
import com.systex.jbranch.app.server.fps.pms361.PMS361;
import com.systex.jbranch.app.server.fps.pms361.PMS361InputVO;
import com.systex.jbranch.app.server.fps.pms361.PMS361OutputVO;
import com.systex.jbranch.app.server.fps.pms401.PMS401;
import com.systex.jbranch.app.server.fps.pms401.PMS401InputVO;
import com.systex.jbranch.app.server.fps.pms401.PMS401OutputVO;
import com.systex.jbranch.app.server.fps.pms408.PMS408;
import com.systex.jbranch.app.server.fps.pms408.PMS408InputVO;
import com.systex.jbranch.app.server.fps.pms408.PMS408OutputVO;
import com.systex.jbranch.app.server.fps.pms409.PMS409;
import com.systex.jbranch.app.server.fps.pms409.PMS409InputVO;
import com.systex.jbranch.app.server.fps.pms409.PMS409OutputVO;
import com.systex.jbranch.app.server.fps.pms414.PMS414;
import com.systex.jbranch.app.server.fps.pms414.PMS414InputVO;
import com.systex.jbranch.app.server.fps.pms414.PMS414OutputVO;
import com.systex.jbranch.app.server.fps.pms418.PMS418;
import com.systex.jbranch.app.server.fps.pms418.PMS418InputVO;
import com.systex.jbranch.app.server.fps.pms418.PMS418OutputVO;
import com.systex.jbranch.app.server.fps.pms421.PMS421;
import com.systex.jbranch.app.server.fps.pms421.PMS421InputVO;
import com.systex.jbranch.app.server.fps.pms421.PMS421OutputVO;
import com.systex.jbranch.app.server.fps.pms422.PMS422;
import com.systex.jbranch.app.server.fps.pms422.PMS422InputVO;
import com.systex.jbranch.app.server.fps.pms422.PMS422OutputVO;
import com.systex.jbranch.app.server.fps.pms424.PMS424;
import com.systex.jbranch.app.server.fps.pms424.PMS424InputVO;
import com.systex.jbranch.app.server.fps.pms424.PMS424OutputVO;
import com.systex.jbranch.app.server.fps.pms426.PMS426;
import com.systex.jbranch.app.server.fps.pms426.PMS426InputVO;
import com.systex.jbranch.app.server.fps.pms426.PMS426OutputVO;
import com.systex.jbranch.app.server.fps.pms427.PMS427;
import com.systex.jbranch.app.server.fps.pms427.PMS427InputVO;
import com.systex.jbranch.app.server.fps.pms427.PMS427OutputVO;
import com.systex.jbranch.app.server.fps.pms428.PMS428;
import com.systex.jbranch.app.server.fps.pms428.PMS428InputVO;
import com.systex.jbranch.app.server.fps.pms428.PMS428OutputVO;
import com.systex.jbranch.app.server.fps.pms431.PMS431;
import com.systex.jbranch.app.server.fps.pms431.PMS431InputVO;
import com.systex.jbranch.app.server.fps.pms431.PMS431OutputVO;
import com.systex.jbranch.app.server.fps.pms433.PMS433;
import com.systex.jbranch.app.server.fps.pms433.PMS433InputVO;
import com.systex.jbranch.app.server.fps.pms433.PMS433OutputVO;
import com.systex.jbranch.app.server.fps.pms496.PMS496;
import com.systex.jbranch.app.server.fps.pms496.PMS496InputVO;
import com.systex.jbranch.app.server.fps.pms496.PMS496OutputVO;
import com.systex.jbranch.app.server.fps.pms997.PMS997;
import com.systex.jbranch.app.server.fps.pms997.PMS997InputVO;
import com.systex.jbranch.app.server.fps.pms997.PMS997OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("btcrm181")
@Scope("prototype")
public class BTCRM181 extends FubonWmsBizLogic {
	
	private DataAccessManager dam;
	
	SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void updShowYN(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT * ");
		sb.append("FROM TBCRM_WKPG_BY_EMPS ");
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		// Yesterday
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(new Date());
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		
		// 1 MONTH AGO
		Calendar calLast1Mon = Calendar.getInstance();
		calLast1Mon.setTime(new Date());
		calLast1Mon.add(Calendar.MONTH, -1);
		
		// 2 MONTH AGO
		Calendar calLast2Mon = Calendar.getInstance();
		calLast2Mon.setTime(new Date());
		calLast2Mon.add(Calendar.MONTH, -2);
		
		GenFileTools gft = new GenFileTools();
		Connection conn = gft.getConnection();
		conn.setAutoCommit(false);
		
		sb = new StringBuffer();
		
		sb.append("UPDATE TBCRM_WKPG_BY_EMPS ");
		sb.append("SET SHOW_YN = ? ");
		sb.append("WHERE E_SEQ = ? ");
		
		PreparedStatement pstmt = conn.prepareStatement(sb.toString());
		
		for (Map<String, Object> map : list) {
			pstmt.setBigDecimal(2, (BigDecimal) map.get("E_SEQ"));	
			
			CMFPG000 cmfpg000 = (CMFPG000) PlatformContext.getBean("cmfpg000");
			Map<String, Object> userInfo = cmfpg000.getUserInfo((String) map.get("E_EMP_ID"));
			Map<String, Object> sysRole = cmfpg000.getSysRole(dam, (String) map.get("E_EMP_ID"), (String) map.get("E_ROLE_ID"), null, userInfo);
			
			String loginBranch = "";
			String loginArea = "";
			String loginRegion = "";
			
			switch ((String) map.get("E_PRIVILEGEID")) {
				case "006":
				case "007":
				case "009":
				case "010":
				case "011":
					loginBranch = (String) map.get("E_BRANCH_NBR");
				case "UHRM006":
				case "UHRM012":
				case "012":
					loginArea = (String) map.get("E_BRANCH_AREA_ID");
				case "013":
					loginRegion = (String) map.get("E_REGION_CENTER_ID");
					break;
			}
			
			switch ((String) map.get("E_PRIVILEGEID")) {
				case "006":
				case "007":
				case "009":
				case "010":
				case "011":
				case "UHRM006":
				case "UHRM012":
				case "012":
				case "013":
				case "049":
					//若無明細資料，則於前端無須呈現(針對分行主管) #5908
					switch ((String) map.get("RPT_PROG_URL")) {
						// TODO:日報
						case "PMS401": // 分行人員當日存款異動明細表, 近30天是否有資料
						case "PMS401U":
							PMS401InputVO pms401InputVO = new PMS401InputVO();
							
							pms401InputVO.setsCreDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(calLast1Mon.getTime())).getTime()));
							pms401InputVO.seteCreDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new Date())).getTime()));
							pms401InputVO.setBranch_nbr(loginBranch);
							pms401InputVO.setBranch_area_id(loginArea);
							pms401InputVO.setRegion_center_id(loginRegion);
							pms401InputVO.setNeedConfirmYN("Y");
							pms401InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms401InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms401InputVO.setUhrmOP(loginArea);

							PMS401 pms401 = (PMS401) PlatformContext.getBean("pms401");
							PMS401OutputVO pms401OutputVO = pms401.queryData(pms401InputVO);
							if (CollectionUtils.isEmpty(pms401OutputVO.getTotalList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
						
							break;
						case "PMS422": // 分行人員與客戶資金往來異常報表
						case "PMS422U":
							PMS422InputVO pms422InputVO = new PMS422InputVO();
							
							pms422InputVO.setBranch_nbr(loginBranch);
							pms422InputVO.setBranch_area_id(loginArea);
							pms422InputVO.setRegion_center_id(loginRegion);
							pms422InputVO.setNeedConfirmYN("Y");
							pms422InputVO.setCurrentPageIndex(0);
							pms422InputVO.setPageCount(1); // 設定第一頁，取一筆即可
							pms422InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms422InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms422InputVO.setUhrmOP(loginArea);
							
							PMS422 pms422 = (PMS422) PlatformContext.getBean("pms422");
							PMS422OutputVO pms422OutputVO = pms422.queryData(pms422InputVO);
							PMS422OutputVO pms422OutputVO_V2 = pms422.queryDataV2(pms422InputVO);
							
							if (CollectionUtils.isEmpty(pms422OutputVO.getResultList()) && CollectionUtils.isEmpty(pms422OutputVO_V2.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS424": // 關聯戶交易月報
						case "PMS424U": // 關聯戶交易月報
							PMS424InputVO pms424InputVO = new PMS424InputVO();
							
							pms424InputVO.setsDate(calLast1Mon.getTime());
							pms424InputVO.seteDate(new Date());
							pms424InputVO.setBranch_nbr(loginBranch);
							pms424InputVO.setBranch_area_id(loginArea);
							pms424InputVO.setRegion_center_id(loginRegion);
							pms424InputVO.setFrom181("Y");
							pms424InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms424InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms424InputVO.setUhrmOP(loginArea);
							
							PMS424 pms424 = (PMS424) PlatformContext.getBean("pms424");
							PMS424OutputVO pms424OutputVO = pms424.query(pms424InputVO);
							if (CollectionUtils.isEmpty(pms424OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS428": // 理財戶大額轉出報表, 近30天是否有資料
						case "PMS428U":
							PMS428InputVO pms428InputVO = new PMS428InputVO();
							
							pms428InputVO.setsDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(calLast1Mon.getTime())).getTime()));
							pms428InputVO.seteDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new Date())).getTime()));
							pms428InputVO.setBranch_nbr(loginBranch);
							pms428InputVO.setBranch_area_id(loginArea);
							pms428InputVO.setRegion_center_id(loginRegion);
							pms428InputVO.setFrom181("Y");
							pms428InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms428InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms428InputVO.setUhrmOP(loginArea);

							PMS428 pms428 = (PMS428) PlatformContext.getBean("pms428");
							PMS428OutputVO pms428OutputVO = pms428.query(pms428InputVO);
							if (CollectionUtils.isEmpty(pms428OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS418": // 理財戶同一IP交易警示報表, 近30天是否有資料
						case "PMS418U":
							PMS418InputVO pms418InputVO = new PMS418InputVO();
							
							pms418InputVO.setsCreDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(calLast1Mon.getTime())).getTime()));
							pms418InputVO.seteCreDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new Date())).getTime()));
							pms418InputVO.setBranch_nbr(loginBranch);
							pms418InputVO.setBranch_area_id(loginArea);
							pms418InputVO.setRegion_center_id(loginRegion);
							pms418InputVO.setNeedConfirmYN("Y");
							pms418InputVO.setCurrentPageIndex(0);
							pms418InputVO.setPageCount(1); // 設定第一頁，取一筆即可
							pms418InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms418InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms418InputVO.setUhrmOP(loginArea);

							PMS418 pms418 = (PMS418) PlatformContext.getBean("pms418");
							PMS418OutputVO pms418OutputVO = pms418.queryData(pms418InputVO);
							
							if (CollectionUtils.isEmpty(pms418OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS361": // 分行人員使用行銀進行非本人帳戶交易日報, 近30天是否有資料
						case "PMS361U":
							PMS361InputVO pms361InputVO = new PMS361InputVO();
							
							pms361InputVO.setsDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(calLast1Mon.getTime())).getTime()));
							pms361InputVO.seteDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new Date())).getTime()));
							pms361InputVO.setBranch_nbr(loginBranch);
							pms361InputVO.setBranch_area_id(loginArea);
							pms361InputVO.setRegion_center_id(loginRegion);
							pms361InputVO.setNeedConfirmYN("Y");
							pms361InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms361InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms361InputVO.setUhrmOP(loginArea);

							PMS361 pms361 = (PMS361) PlatformContext.getBean("pms361");
							PMS361OutputVO pms361OutputVO = pms361.inquire(pms361InputVO);
							if (CollectionUtils.isEmpty(pms361OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS414": // 公用電腦查核日報, 近30天是否有資料
							PMS414InputVO pms414InputVO = new PMS414InputVO();
							
							pms414InputVO.setsCreDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(calLast1Mon.getTime())).getTime()));
							pms414InputVO.seteCreDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new Date())).getTime()));
							pms414InputVO.setBranch_nbr(loginBranch);
							pms414InputVO.setBranch_area_id(loginArea);
							pms414InputVO.setRegion_center_id(loginRegion);
							pms414InputVO.setNeedConfirmYN("Y");
							pms414InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));

							PMS414 pms414 = (PMS414) PlatformContext.getBean("pms414");
							PMS414OutputVO pms414OutputVO = pms414.queryData(pms414InputVO);
							if (CollectionUtils.isEmpty(pms414OutputVO.getTotalList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS408": // 客戶6個月內提高KYC報表, 近30天是否有資料
						case "PMS408U":
							PMS408InputVO pms408InputVO = new PMS408InputVO();
							
							pms408InputVO.setsCreDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(calLast1Mon.getTime())).getTime()));
							pms408InputVO.setEndDate(new java.sql.Date(sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new Date())).getTime()));
							pms408InputVO.setBranch_nbr(loginBranch);
							pms408InputVO.setBranch_area_id(loginArea);
							pms408InputVO.setRegion_center_id(loginRegion);
							pms408InputVO.setRoleID((String) map.get("E_ROLE_ID"));
							pms408InputVO.setNeedConfirmYN("Y");
							pms408InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms408InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms408InputVO.setUhrmOP(loginArea);

							PMS408 pms408 = (PMS408) PlatformContext.getBean("pms408");
							PMS408OutputVO pms408OutputVO = pms408.queryData(pms408InputVO);
							if (CollectionUtils.isEmpty(pms408OutputVO.getTotalList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS421": // 自取對帳單客戶名單(理專指定休假查核)
						case "PMS421U":
							PMS421InputVO pms421InputVO = new PMS421InputVO();
							
							pms421InputVO.setsCreDate(calLast2Mon.getTime());
							pms421InputVO.setEndDate(new Date());
							pms421InputVO.setBranch_nbr(loginBranch);
							pms421InputVO.setBranch_area_id(loginArea);
							pms421InputVO.setRegion_center_id(loginRegion);
							pms421InputVO.setLoginRole((String) map.get("E_ROLE_ID"));
							pms421InputVO.setFrom181("Y");
							pms421InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms421InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms421InputVO.setUhrmOP(loginArea);

							PMS421 pms421 = (PMS421) PlatformContext.getBean("pms421");
							PMS421OutputVO pms421OutputVO = pms421.queryData(pms421InputVO);
							if (CollectionUtils.isEmpty(pms421OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
							
						// TODO:月報
						case "PMS426": // 理財戶通訊關聯帳戶月報
						case "PMS426U":
							PMS426InputVO pms426InputVO = new PMS426InputVO();
							
							pms426InputVO.setsCreDate(sdfYYYYMM.format(calLast2Mon.getTime()));
							pms426InputVO.seteCreDate(sdfYYYYMM.format(calLast1Mon.getTime()));
							pms426InputVO.setBranch_nbr(loginBranch);
							pms426InputVO.setBranch_area_id(loginArea);
							pms426InputVO.setRegion_center_id(loginRegion);
							pms426InputVO.setFrom181("Y");
							pms426InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms426InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms426InputVO.setUhrmOP(loginArea);

							PMS426 pms426 = (PMS426) PlatformContext.getBean("pms426");
							PMS426OutputVO pms426OutputVO = pms426.query(pms426InputVO);
							if (CollectionUtils.isEmpty(pms426OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS427": // 理財戶對帳單退件及資產減損月報
						case "PMS427U":
							PMS427InputVO pms427InputVO = new PMS427InputVO();
							
							pms427InputVO.setsCreDate(sdfYYYYMM.format(calLast2Mon.getTime()));
							pms427InputVO.seteCreDate(sdfYYYYMM.format(calLast1Mon.getTime()));
							pms427InputVO.setBranch_nbr(loginBranch);
							pms427InputVO.setBranch_area_id(loginArea);
							pms427InputVO.setRegion_center_id(loginRegion);
							pms427InputVO.setFrom181("Y");
							pms427InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));	
							pms427InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms427InputVO.setUhrmOP(loginArea);

							PMS427 pms427 = (PMS427) PlatformContext.getBean("pms427");
							PMS427OutputVO pms427OutputVO = pms427.query(pms427InputVO);
							if (CollectionUtils.isEmpty(pms427OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS431": // 高風險投資報表
						case "PMS431U":
							PMS431InputVO pms431InputVO = new PMS431InputVO();
							
							pms431InputVO.setsCreDate(sdfYYYYMM.format(calLast2Mon.getTime()));
							pms431InputVO.seteCreDate(sdfYYYYMM.format(calLast1Mon.getTime()));
							pms431InputVO.setBranch_nbr(loginBranch);
							pms431InputVO.setBranch_area_id(loginArea);
							pms431InputVO.setRegion_center_id(loginRegion);
							pms431InputVO.setNeedConfirmYN("Y");
							pms431InputVO.setCurrentPageIndex(0);
							pms431InputVO.setPageCount(1); // 設定第一頁，取一筆即可
							pms431InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms431InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms431InputVO.setUhrmOP(loginArea);

							PMS431 pms431 = (PMS431) PlatformContext.getBean("pms431");
							PMS431OutputVO pms431OutputVO = pms431.queryData(pms431InputVO);
							
							if (CollectionUtils.isEmpty(pms431OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();	
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS496": // 基金/海外債短線交易進出月報
						case "PMS496U":
							PMS496InputVO pms496InputVO = new PMS496InputVO();
							
							pms496InputVO.setImportSDate(sdfYYYYMM.format(calLast2Mon.getTime()));
							pms496InputVO.setImportEDate(sdfYYYYMM.format(calLast1Mon.getTime()));
							pms496InputVO.setBranch_nbr(loginBranch);
							pms496InputVO.setBranch_area_id(loginArea);
							pms496InputVO.setRegion_center_id(loginRegion);
							pms496InputVO.setFrom181("Y");
							pms496InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms496InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms496InputVO.setUhrmOP(loginArea);

							PMS496 pms496 = (PMS496) PlatformContext.getBean("pms496");
							PMS496OutputVO pms496OutputVO = pms496.queryData(pms496InputVO);
							if (CollectionUtils.isEmpty(pms496OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS997": // 內控強化關懷名單
						case "PMS997U":
							PMS997InputVO pms997InputVO = new PMS997InputVO();
							
							pms997InputVO.setImportSDate(sdfYYYYMM.format(calLast1Mon.getTime()));
							pms997InputVO.setImportEDate(sdfYYYYMM.format(calLast1Mon.getTime()));
							pms997InputVO.setBranch_nbr(loginBranch);
							pms997InputVO.setBranch_area_id(loginArea);
							pms997InputVO.setRegion_center_id(loginRegion);
							pms997InputVO.setFrom181("Y");
							pms997InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							pms997InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							pms997InputVO.setUhrmOP(loginArea);

							PMS997 pms997 = (PMS997) PlatformContext.getBean("pms997");
							PMS997OutputVO pms997OutputVO = pms997.query(pms997InputVO);
							if (CollectionUtils.isEmpty(pms997OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
						
							break;
							
						// TODO:其它
						case "KYC320": // KYC問卷客戶記錄查詢, 昨天今天是否有資料
							KYC320InputVO kyc320InputVO = new KYC320InputVO();
							
							kyc320InputVO.setsTime(yesterday.getTime());
							kyc320InputVO.seteTime(new Date());
							kyc320InputVO.setMemLoginFlag((String) sysRole.get("memLoginFlag"));
							kyc320InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));
							
							KYC320 kyc320 = (KYC320) PlatformContext.getBean("kyc320");
							KYC320OutputVO kyc320OutputVO = kyc320.inquire(kyc320InputVO);
							if (CollectionUtils.isEmpty(kyc320OutputVO.getList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS409": // KYC例外管理月報
						case "PMS409U":
							PMS409InputVO pms409InputVO = new PMS409InputVO();
							
							pms409InputVO.setsCreDate(sdfYYYYMM.format(calLast1Mon.getTime()));
							pms409InputVO.setBranch_nbr(loginBranch);
							pms409InputVO.setBranch_area_id(loginArea);
							pms409InputVO.setRegion_center_id(loginRegion);
							pms409InputVO.setNeedConfirmYN("Y");
							pms409InputVO.setSelectRoleID((String) map.get("E_ROLE_ID"));

							PMS409 pms409 = (PMS409) PlatformContext.getBean("pms409");
							PMS409OutputVO pms409OutputVO = pms409.queryData(pms409InputVO);
							if (CollectionUtils.isEmpty(pms409OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}
							
							break;
						case "PMS433":
							PMS433InputVO pms433InputVO = new PMS433InputVO();
							pms433InputVO.setRegion_center_id(loginRegion);
							pms433InputVO.setBranch_area_id(loginArea);
							pms433InputVO.setBranch_nbr(loginBranch);
							
							pms433InputVO.setUhrm_branch_area_id(loginArea);
							String privilegeID = (String) map.get("E_PRIVILEGEID");
							pms433InputVO.setUhrmFlag(privilegeID.indexOf("UHRM") > 0 ? true : false);
							
							pms433InputVO.setSysRole((String) map.get("E_ROLE_ID"));
							
							
							PMS433 pms433 = (PMS433) PlatformContext.getBean("pms433");
							PMS433OutputVO pms433OutputVO = pms433.queryForCRM181(pms433InputVO);
							if(CollectionUtils.isEmpty(pms433OutputVO.getResultList())) {
								pstmt.setString(1, "N");
								pstmt.addBatch();
							} else {
								pstmt.setString(1, "Y");
								pstmt.addBatch();
							}

							break;
					}
					break;
			}

		}
		
		pstmt.executeBatch();
		conn.commit();
		
		if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
        if (conn != null) try { conn.close(); } catch (Exception e) {}
	}
}