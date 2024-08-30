package com.systex.jbranch.app.server.fps.crm210;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm211.CRM211InputVO;
import com.systex.jbranch.app.server.fps.crm221.CRM221InputVO;
import com.systex.jbranch.app.server.fps.crm331.CRM331InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description : 客戶查詢
 * @Author      : 20161117 walalala
 * @Editor      : 20220512 Ocean    : WMS-CR-20220517-01_新增實動戶上傳註記
 */

@Component("crm210")
@Scope("request")
public class CRM210 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public CRM210OutputVO inquire_common(Object body, String type, String loginToken, Object loginAoCode) throws JBranchException {

		initUUID();

		CRM210OutputVO return_VO = new CRM210OutputVO();
		CRM210_ALLInputVO inputVO_all = (CRM210_ALLInputVO) body;
		CRM210InputVO inputVO = inputVO_all.getCrm210inputVO();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		boolean isHeadMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());	// 總行
		boolean isArMGR   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());	// 業務處
		boolean isMbrMGR  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());	// 營運區
		boolean isFC      = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());		// 理專
		boolean isFCH	  = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());		// 理專FCH
		boolean isPSOP    = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());		// 作業人員
		boolean isFAIA    = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());		// 輔銷人員
		boolean isPAO     = xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(inputVO_all.getLoginRole());		// 作業人員

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		
		/** 客戶資產相關 **/
		sql.append("       V.AUM_AMT, "); 											// 最新總資產(AUM)
		sql.append("       V.TXN_FEE_01, "); 										// 近3個月手收
		sql.append("       V.TXN_FEE_02, "); 										// 近6個月手收
		sql.append("       V.TXN_FEE_03, "); 										// 近一年手收
		sql.append("       V.AMT_01, "); 											// 上月平均AUM
		sql.append("       V.AMT_02, "); 											// 全行台外幣活存餘額
		sql.append("       V.AMT_03, "); 											// 台幣活存餘額
		sql.append("       V.AMT_04, "); 											// 外幣活存餘額
		sql.append("       V.AMT_06, "); 											// 台幣定存餘額
		sql.append("       V.AMT_08, "); 											// 台幣支存餘額
		sql.append("       V.AMT_07, "); 											// 外幣定存餘額
		sql.append("       V.AMT_09, "); 											// 全行基金
		sql.append("       V.AMT_10, "); 											// 全行海外ETF/海外股票
		sql.append("       V.AMT_24, "); 											// 全行海外商品餘額
		sql.append("       V.AMT_25, "); 											// 全行奈米投餘額
		sql.append("       V.AMT_26, "); 											// 全行金市海外債餘額
		sql.append("       V.AMT_12, "); 											// 全行SI
		sql.append("       V.AMT_13, "); 											// 全行海外債商品
		sql.append("       V.AMT_14, "); 											// 全行DCI
		sql.append("       V.AMT_15, "); 											// 全行金錢信託
		sql.append("       V.AMT_16, "); 											// 全行黃金存摺
		sql.append("       V.AMT_17, "); 											// 全行保險餘額
		sql.append("       V.AMT_18, "); 											// 全行貸款餘額
		// TODO 上2080 拆2017
//		sql.append("       V.AMT_18_1, "); 											// 房屋貸款
//		sql.append("       V.AMT_18_2, "); 											// 信用貸款
//		sql.append("       V.AMT_18_3, "); 											// 留學貸款
//		sql.append("       V.AMT_18_4, "); 											// 就學貸款
		sql.append("       V.AMT_19, "); 											// 前日大額100萬以上進出
		sql.append("       V.AMT_11, "); 											// SN
		
		sql.append("       V.ACT_PRD_NUMS, ");								        // 客戶活躍度
		
		/** 客戶資料相關 **/
		sql.append("       V.CUST_ID, "); 											// 客戶ID
		sql.append("       V.CUST_NAME, "); 										// 客戶姓名

		// 0004141: 客戶移轉篩選、分行客戶查詢、我的客戶查詢，今天已跨分行移轉的會無法同分行移轉 sen 2018/01/10
		sql.append("       V.BRA_NBR, ");											// 歸屬行
		sql.append("       V.BRANCH_NAME, ");

		sql.append("       V.CUST_03, "); 											// 性別
		sql.append("       V.CUST_04, "); 											// 理財會員等級
		sql.append("       V.CUST_05, "); 											// 出生年(西元)
		sql.append("       V.CUST_06, "); 											// 貢獻度等級
		sql.append("       V.CUST_07, "); 											// 法金板塊
		sql.append("       V.CUST_10, "); 											// 客訴戶
		sql.append("       V.CUST_12, "); 											// 可推介
		sql.append("       V.CUST_16, "); 											// 私人銀行家庭會員
		sql.append("       V.CUST_17, "); 											// 新總資產(AUM)
		sql.append("       V.CUST_18, "); 											// 潛力等級
		sql.append("       V.AMT_TOTAL, ");                                         // 歸戶總資產
		
		// 0001082: WMS-CR-20220325-01_業管系統新增證券查詢資訊及報表(高端) ADD BY OCEAN 
		sql.append("       NVL(V.AUM_SEC_TOTAL, 0) AS AUM_SEC_TOTAL, ");            // 歸戶總資產(含有價證券信託、證券往來)
		sql.append("       NVL(V.AMT_30, 0) AS AMT_30, "); 							// 有價證券信託餘額
		sql.append("       NVL(V.AMT_31, 0) AS AMT_31, "); 							// 證券往來-海外股票餘額
		sql.append("       NVL(V.AMT_32, 0) AS AMT_32, "); 							// 證券往來-海外債券餘額
		sql.append("       NVL(V.AMT_33, 0) AS AMT_33, "); 							// 證券往來-境外結構型商品餘額
		sql.append("       NVL(V.AMT_34, 0) AS AMT_34, "); 							// 證券往來-境內結構型商品餘額
		
		// 0004141: 客戶移轉篩選、分行客戶查詢、我的客戶查詢，今天已跨分行移轉的會無法同分行移轉 sen 2018/01/10
		sql.append("       SAO.AO_CODE, ");										    // 理財專員代碼
		sql.append("       MEM.EMP_NAME AS EMP_NAME, ");							// 理財專員的姓名
		sql.append("       MEM.EMP_ID, ");											// 理財專員的ID

		sql.append("       MEM.DEPT_ID AS AO_05, "); 								// 理財專員的分行別
		sql.append("       V.AO_06, "); 											// 理財職級
		
		/** 風險屬性相關 **/
		sql.append("       V.ATR_03, "); 											// 最後做風險屬性日期
		
		/** 其他 **/
		sql.append("       V.UNTAKE_CARE_MONTH, "); 								// 未經營月數
		sql.append("       V.TAKE_CARE_MATCH_YN, "); 								// 經營頻次符合度
		sql.append("       V.CO_ACCT_YN, "); 										// 法金戶
		
		/** 組織相關 **/
		sql.append("       V.ROLE_ID, "); 											// 角色代號
		sql.append("       V.PRIVILEGEID, ");

		/** UHRM **/
		sql.append("       V.UEMP_ID, "); 											// UHRM
		sql.append("       V.UEMP_NAME || V.UEMP_AO_TYPE AS UEMP_NAME, "); 			// UHRM姓名
		
		/** 其它 **/
		sql.append("       V.COMM_NS_YN, "); 										// 禁銷戶
		
		sql.append("	   SAO.TYPE AS AO_CODE_TYPE, "); 							// AO CODE類型：1主 2副 3維護
		sql.append("	   CASE WHEN SAO.TYPE = '1' THEN '(主)' "); 		    		
		sql.append("	        WHEN SAO.TYPE = '2' THEN '(副)' "); 										
		sql.append("	        WHEN SAO.TYPE = '3' THEN '(維護)'"); 										
		sql.append("	   ELSE '' END AS C_TYPE_NAME , ");
		
//		sql.append("       V.CONTACT_LIGHT "); 									    // 聯繫燈號
		
		sql.append("       V.W8BEN_YN AS CUST_W8BEN, "); 						    // W-8BEN(W-8BEN-E)
		
		sql.append("       V.VOC_FLAG "); 						      				// 是否有承租保管箱
		
		sql.append("FROM MVCRM_AST_AMT V ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST CM ON V.CUST_ID = CM.CUST_ID ");
		sql.append("LEFT JOIN TBORG_SALES_AOCODE SAO ON SAO.AO_CODE = CM.AO_CODE ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM ON SAO.EMP_ID = MEM.EMP_ID ");
		
		sql.append("WHERE 1 = 1 ");
				
		switch (type) {
			case "CRM211": // 我的客戶查詢
				CRM211InputVO inputVO_CRM211 = inputVO_all.getCrm211inputVO();
				String aoCode = inputVO_CRM211.getAo_code();

				if (StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
					String[] uEmpDtl = inputVO_CRM211.getuEmpID().split(",");
					if (uEmpDtl.length >= 2) { //有輸入
						sql.append("AND V.UEMP_ID = :uEmpID ");
						sql.append("AND V.UEMP_AO_TYPE = :uEmpAoType ");

						queryCondition.setObject("uEmpID", uEmpDtl[0]);
						queryCondition.setObject("uEmpAoType", uEmpDtl[1]);
					} else {
						sql.append("AND V.UEMP_ID = :uEmpID ");
						queryCondition.setObject("uEmpID", getUserVariable(FubonSystemVariableConsts.LOGINID));
					}
				} else {
					//AO Code
					if ((StringUtils.isNotBlank(aoCode) && !StringUtils.equals("0", aoCode))) { //有輸入
						// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管 change svn:log
						sql.append("AND EXISTS ( ");
						
						if (StringUtils.equals(aoCode, "Diamond Team")) {
							sql.append("  SELECT 'X' ");
							sql.append("  FROM TBCRM_CUST_MAST A ");
							sql.append("  LEFT JOIN VWORG_DEFN_INFO B on B.BRANCH_NBR = A.BRA_NBR ");
							sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
							sql.append("  AND EXISTS ( ");
							sql.append("    SELECT AO.AO_CODE ");
							sql.append("    FROM TBORG_DIAMOND_TEAM DT_A ");
							sql.append("    INNER JOIN TBORG_SALES_AOCODE AO ON DT_A.EMP_ID = AO.EMP_ID ");
							sql.append("    WHERE EXISTS (SELECT 1 FROM TBORG_DIAMOND_TEAM DT_B WHERE DT_A.BRANCH_NBR = DT_B.BRANCH_NBR AND DT_A.TEAM_TYPE = DT_B.TEAM_TYPE AND EMP_ID = :empID) ");
							sql.append("    AND A.AO_CODE = AO.AO_CODE ");
							sql.append("  ) ");
							queryCondition.setObject("empID", getUserVariable(FubonSystemVariableConsts.LOGINID));
						} else {
							sql.append("  SELECT 'X' ");
							sql.append("  FROM TBCRM_CUST_MAST A ");
							sql.append("  LEFT JOIN VWORG_DEFN_INFO B on B.BRANCH_NBR = A.BRA_NBR ");
							sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
							sql.append("  AND A.AO_CODE = :ao_code ");
							queryCondition.setObject("ao_code", aoCode);
						}
						
						if (StringUtils.isNotBlank(inputVO.getCust_02())) {
							sql.append("  AND A.BRA_NBR = :cust_02 ");
							queryCondition.setObject("cust_02", inputVO.getCust_02());
						} else {
							if (isHeadMGR) {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList) OR B.REGION_CENTER_ID IS NULL) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
							} else if (isArMGR) {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
							} else if (isMbrMGR) {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
							} else {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList)) ");
							}
							
							queryCondition.setObject("rcIdList", inputVO_all.getAvailRegionList());
							queryCondition.setObject("opIdList", inputVO_all.getAvailAreaList());
							queryCondition.setObject("brNbrList", inputVO_all.getAvailBranchList());
						}
						
						sql.append(") ");
					} else if (StringUtils.equals("0", aoCode)) { //查詢空Code
						sql.append("AND EXISTS ( ");
						
						sql.append("  SELECT 'X' ");
						sql.append("  FROM TBCRM_CUST_MAST A ");
						sql.append("  LEFT JOIN VWORG_DEFN_INFO B on B.BRANCH_NBR = A.BRA_NBR ");
						sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
						sql.append("  AND A.AO_CODE IS NULL "); //先暫時這樣寫，待提升效能後再修正
						if (StringUtils.isNotBlank(inputVO.getCust_02())) {
							sql.append("  AND A.BRA_NBR = :cust_02 ");
							queryCondition.setObject("cust_02", inputVO.getCust_02());
						} else {
							if (isHeadMGR) {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList) OR B.REGION_CENTER_ID IS NULL) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
							} else if (isArMGR) {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
							} else if (isMbrMGR) {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
							} else {
								sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
								sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
								sql.append("  AND (B.BRANCH_NBR IN (:brNbrList)) ");
							}
							
							queryCondition.setObject("rcIdList", inputVO_all.getAvailRegionList());
							queryCondition.setObject("opIdList", inputVO_all.getAvailAreaList());
							queryCondition.setObject("brNbrList", inputVO_all.getAvailBranchList());
						}
						
						sql.append(") ");
					} else if (StringUtils.isBlank(aoCode)) { //請選擇
						if (isFC || isPSOP || isFCH || isPAO) { //fc or psop or fch or pao
							sql.append("AND EXISTS ( ");
							
							sql.append("  SELECT 'X' ");
							sql.append("  FROM TBCRM_CUST_MAST A ");
							sql.append("  LEFT JOIN VWORG_DEFN_INFO B on B.BRANCH_NBR = A.BRA_NBR ");
							sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
							sql.append("  AND A.AO_CODE in (SELECT AO_CODE FROM VWORG_AO_INFO WHERE EMP_ID = :loginID) ");
							queryCondition.setObject("loginID", inputVO_all.getLoginEmpID());
							
							if (StringUtils.isNotBlank(inputVO.getCust_02())) {
								sql.append("  and A.BRA_NBR = :cust_02 ");
								queryCondition.setObject("cust_02", inputVO.getCust_02());
							} else {
								if (isHeadMGR) {
									sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList) OR B.REGION_CENTER_ID IS NULL) ");
									sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
									sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
								} else if (isArMGR) {
									sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
									sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
									sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
								} else if (isMbrMGR) {
									sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
									sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
									sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
								} else {
									sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
									sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
									sql.append("  AND (B.BRANCH_NBR IN (:brNbrList)) ");
								}
								
								queryCondition.setObject("rcIdList", inputVO_all.getAvailRegionList());
								queryCondition.setObject("opIdList", inputVO_all.getAvailAreaList());
								queryCondition.setObject("brNbrList", inputVO_all.getAvailBranchList());
							}
							
							sql.append(") ");
						}
					}
				}
				
				//客戶ID
				if (!StringUtils.isBlank(inputVO_CRM211.getCust_id())) {
					sql.append("AND V.CUST_ID = :cust_id ");
					queryCondition.setObject("cust_id", inputVO_CRM211.getCust_id());
				}
	
				//客戶姓名
				if (!StringUtils.isBlank(inputVO_CRM211.getCust_name())) {
					sql.append("AND V.CUST_NAME LIKE '%" + inputVO_CRM211.getCust_name() + "%' ");
				}
	
				//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
				if (!"".equals(loginToken)) {
					if ("mobile".equals(loginToken)) {
						sql.append("AND V.CUST_ID in (SELECT CUST_ID FROM VWCRM_MAO_QRY_CUST WHERE AO_CODE in (:loginAoCode)) ");
						queryCondition.setObject("loginAoCode", loginAoCode);
					}
				}
				
				break;
			case "CRM221": //分行客戶查詢
				//歸屬行主管可查詢轄下分行的所有客戶，包含有掛Code的客戶和空code客戶，帳戶行主管及理專僅能查詢於本分行開戶的空code單一客戶。
				//查詢與理專所屬分行相同之"歸屬行"或"帳務行"空code客戶
				
				CRM221InputVO inputVO_CRM221 = inputVO_all.getCrm221inputVO();
	
				if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
					!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
					String[] uEmpDtl = inputVO_CRM221.getuEmpID().split(",");
					if (uEmpDtl.length >= 2) { //有輸入
						sql.append("AND V.UEMP_ID = :uEmpID ");
						sql.append("AND V.UEMP_AO_TYPE = :uEmpAoType ");
						
						queryCondition.setObject("uEmpID", uEmpDtl[0]);
						queryCondition.setObject("uEmpAoType", uEmpDtl[1]);
					} else {
						sql.append("AND V.UEMP_ID IS NOT NULL ");
						sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = V.UEMP_ID AND UHRM.DEPT_ID = :loginArea) ");
						queryCondition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
					}
				} else {
					String[] uEmpDtl = inputVO_CRM221.getuEmpID().split(",");
					if (uEmpDtl.length >= 2) { //有輸入
						sql.append("AND V.UEMP_ID = :uEmpID ");
						sql.append("AND V.UEMP_AO_TYPE = :uEmpAoType ");

						queryCondition.setObject("uEmpID", uEmpDtl[0]);
						queryCondition.setObject("uEmpAoType", uEmpDtl[1]);
					}
					
					if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0 &&
						!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "bs")) {
						// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
						if (StringUtils.isNotBlank(inputVO_CRM221.getAo_code())) {
							sql.append("AND V.AO_CODE = :aoCode ");
							queryCondition.setObject("aoCode", inputVO_CRM221.getAo_code());
						} else {
							sql.append("AND EXISTS (SELECT 'X' FROM VWORG_EMP_BS_INFO BS WHERE BS.BS_CODE = V.AO_CODE) ");
						}
					}
					
					//mantis 0004529: 理專調分行,原分行客戶可被新分行主管移轉 sen 2018/04/10
					if (StringUtils.isNotBlank(inputVO_CRM221.getAo_code()) && !"0".equals(inputVO_CRM221.getAo_code())) {
						sql.append("AND EXISTS (SELECT 'X' FROM TBCRM_CUST_MAST A WHERE V.CUST_ID = A.CUST_ID AND A.AO_CODE = :ao_code AND A.BRA_NBR IN (:brNbrList)) ");
						queryCondition.setObject("ao_code", inputVO_CRM221.getAo_code());
						queryCondition.setObject("brNbrList", inputVO_all.getAvailBranchList());
					}
					
					// old code查詢空CODE
					else {
						//有選取分行
						if (!StringUtils.isBlank(inputVO_CRM221.getAo_05()) || !StringUtils.isBlank(inputVO.getCust_02())) {
							//有歸屬行
							if (!StringUtils.isBlank(inputVO.getCust_02())) {
								sql.append("AND EXISTS ( ");
								sql.append("  SELECT 'X' ");
								sql.append("  FROM TBCRM_CUST_MAST A ");
								sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
								sql.append("  AND A.BRA_NBR = :bra_nbr ");
								
								if ("0".equals(inputVO_CRM221.getAo_code())) {
									sql.append("  AND A.AO_CODE IS NULL "); //本行客戶空code
								}
								sql.append(")  ");
								
								queryCondition.setObject("bra_nbr", inputVO.getCust_02());
							} else { // 查歸屬行+帳務行
								if (new BigDecimal(inputVO_CRM221.getAo_05()).compareTo(new BigDecimal("200")) >= 0 && 
									new BigDecimal(inputVO_CRM221.getAo_05()).compareTo(new BigDecimal("900")) <= -1) {
									sql.append("AND ( ");
									sql.append("  EXISTS ( ");
									sql.append("    SELECT 'X' ");
									sql.append("    FROM TBCRM_CUST_MAST A ");
									sql.append("    WHERE V.CUST_ID = A.CUST_ID ");
									sql.append("    AND A.BRA_NBR = :bra_nbr ");
									
									if ("0".equals(inputVO_CRM221.getAo_code())) {
										sql.append("    AND A.AO_CODE IS NULL "); //本行客戶空code
									}
									
									sql.append("  ) ");
									sql.append("  OR ");
									sql.append("  EXISTS ( ");
									sql.append("    SELECT 'X' ");
									sql.append("    FROM TBCRM_ACCT_MAST B, TBCRM_CUST_MAST A ");
									sql.append("    WHERE A.CUST_ID = B.CUST_ID ");
									sql.append("    AND B.CUST_ID = V.CUST_ID ");
									sql.append("    AND NVL(ACCT_STATUS,'X') <> '2' ");
									sql.append("    AND SUBSTR(ACCT_TYPE, 1, 2) <> 'LN' ");
									sql.append("    AND A.BRA_NBR = :bra_nbr ");
									sql.append("    AND A.AO_CODE IS NULL");
									sql.append("  )");
									sql.append(") "); //帳務行	    	    
									
									queryCondition.setObject("bra_nbr", inputVO_CRM221.getAo_05());
								} else {
									sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = V.UEMP_ID AND UHRM.DEPT_ID = :branchAreaID) ");
									queryCondition.setObject("branchAreaID", inputVO_CRM221.getAo_04());
								}
							}
						} else {
							//未選取分行，但有選取 營運區 || 區域
							if (!StringUtils.isBlank(inputVO_CRM221.getAo_04()) || !StringUtils.isBlank(inputVO_CRM221.getAo_03())) {
								sql.append("AND ( ");
								sql.append("  EXISTS ( ");
								sql.append("    SELECT 'X' ");
								sql.append("    FROM TBCRM_CUST_MAST A ");
								sql.append("    WHERE V.CUST_ID = A.CUST_ID ");
								sql.append("    AND A.BRA_NBR IN (:branch_list) ");
								
								if ("0".equals(inputVO_CRM221.getAo_code())) {
									sql.append("    AND A.AO_CODE IS NULL "); //本行客戶空code
								}
								
								sql.append("  ) ");
								sql.append("  OR ");
								sql.append("  EXISTS ( ");
								sql.append("    SELECT 'X' ");
								sql.append("    FROM TBCRM_ACCT_MAST B, TBCRM_CUST_MAST A ");
								sql.append("    WHERE A.CUST_ID = B.CUST_ID ");
								sql.append("    AND B.CUST_ID = V.CUST_ID ");
								sql.append("    AND NVL(ACCT_STATUS,'X') <> '2' ");
								sql.append("    AND SUBSTR(ACCT_TYPE, 1, 2) <> 'LN' ");
								sql.append("    AND A.BRA_NBR IN (:branch_list) ");
								sql.append("    AND A.AO_CODE IS NULL ");
								sql.append("  )");
								
								sql.append("  OR ");
								sql.append("  EXISTS ( ");
								sql.append("    SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = V.UEMP_ID AND UHRM.DEPT_ID = :branchAreaID ");
								sql.append("  ) ");

								sql.append(") "); //帳務行
	
								List<String> branch_list = new ArrayList<String>();
								for (int i = 0; i < inputVO_CRM221.getBranch_list().size(); i++) {
									branch_list.add(inputVO_CRM221.getBranch_list().get(i).get("DATA"));
								}

								queryCondition.setObject("branch_list", branch_list);
								queryCondition.setObject("branchAreaID", inputVO_CRM221.getAo_04());
							} else {
								// 區域、營運區、分行 皆未選取(總行人員才可)       			
								if ("0".equals(inputVO_CRM221.getAo_code())) {
									sql.append("AND EXISTS (SELECT 'X' FROM TBCRM_CUST_MAST A WHERE V.CUST_ID = A.CUST_ID AND A.AO_CODE IS NULL) ");
								}
								
								// 2018/7/2 faiaMap
								else if (isFAIA) {
									sql.append("AND EXISTS( ");
									sql.append("  SELECT 'X' ");
									sql.append("  FROM TBCRM_CUST_MAST A ");
									sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
									sql.append("  AND A.AO_CODE in ( ");
									sql.append("    SELECT AO_CODE ");
									sql.append("    FROM VWORG_AO_INFO ");
									sql.append("    WHERE BRA_NBR IN (select BRANCH_NBR from TBORG_FAIA where EMP_ID = :loginID)");
									sql.append("  )");
									sql.append(")");
									queryCondition.setObject("loginID", inputVO_all.getLoginEmpID());
								}
							}
						}
					}
				}
	
				//客戶ID
				if (!StringUtils.isBlank(inputVO_CRM221.getCust_id())) {
					sql.append("AND V.CUST_ID = :cust_id ");
					queryCondition.setObject("cust_id", inputVO_CRM221.getCust_id());
				}
	
				//客戶姓名
				if (!StringUtils.isBlank(inputVO_CRM221.getCust_name())) {
					sql.append("AND V.CUST_NAME LIKE '%" + inputVO_CRM221.getCust_name() + "%' ");
				}

				break;
			case "CRM331":
				CRM331InputVO inputVO_CRM331 = inputVO_all.getCrm331inputVO();
	
				if ((StringUtils.isNotBlank(inputVO_CRM331.getAo_code()) || StringUtils.isNotBlank(inputVO_CRM331.getRe_ao_code())) && !"0".equals(inputVO_CRM331.getAo_code())) {
					if (StringUtils.isNotBlank(inputVO_CRM331.getAo_code())) {
						// mantis 0004529: 理專調分行,原分行客戶可被新分行主管移轉 sen 2018/04/10
						sql.append("AND EXISTS(SELECT 'X' FROM TBCRM_CUST_MAST A WHERE V.CUST_ID = A.CUST_ID AND A.AO_CODE = :ao_code AND A.BRA_NBR IN (:brNbrList)) ");
						queryCondition.setObject("ao_code", inputVO_CRM331.getAo_code());
						queryCondition.setObject("brNbrList", inputVO_all.getAvailBranchList());
					} else {
						// 2017/9/22 test
						sql.append("AND EXISTS ( ");
						sql.append("  SELECT 'X' ");
						sql.append("  FROM TBCRM_CUST_MAST A ");
						sql.append("  LEFT JOIN VWORG_DEFN_INFO B on B.BRANCH_NBR = A.BRA_NBR ");
						sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
						sql.append("  AND A.AO_CODE = :ao_code ");
						
						if (isHeadMGR) {
							sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList) OR B.REGION_CENTER_ID IS NULL) ");
							sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
							sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
						} else if (isArMGR) {
							sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
							sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList) OR B.BRANCH_AREA_ID IS NULL) ");
							sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
						} else if (isMbrMGR) {
							sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
							sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
							sql.append("  AND (B.BRANCH_NBR IN (:brNbrList) OR B.BRANCH_NBR IS NULL) ");
						} else {
							sql.append("  AND (B.REGION_CENTER_ID IN (:rcIdList)) ");
							sql.append("  AND (B.BRANCH_AREA_ID IN (:opIdList)) ");
							sql.append("  AND (B.BRANCH_NBR IN (:brNbrList)) ");
						}
						
						sql.append(") ");
						
						queryCondition.setObject("ao_code", inputVO_CRM331.getRe_ao_code());
						queryCondition.setObject("rcIdList", inputVO_all.getAvailRegionList());
						queryCondition.setObject("opIdList", inputVO_all.getAvailAreaList());
						queryCondition.setObject("brNbrList", inputVO_all.getAvailBranchList());
					}
				}
				
				// old code查詢空CODE
				else {
					// 有選取分行
					if (StringUtils.isNotBlank(inputVO_CRM331.getAo_05()) || StringUtils.isNotBlank(inputVO.getCust_02())) {
						// 有歸屬行
						if (!StringUtils.isBlank(inputVO.getCust_02())) {
							sql.append("AND EXISTS ( ");
							sql.append("  SELECT 'X' ");
							sql.append("  FROM TBCRM_CUST_MAST A ");
							sql.append("  WHERE V.CUST_ID = A.CUST_ID ");
							sql.append("  AND A.BRA_NBR = :bra_nbr ");
							
							if ("0".equals(inputVO_CRM331.getAo_code())) {
								sql.append("  AND A.AO_CODE IS NULL "); //本行客戶空code
							}
							
							sql.append(")  ");
							
							queryCondition.setObject("bra_nbr", inputVO.getCust_02());
						} else { // 查歸屬行+帳務行
							sql.append("AND ( ");
							sql.append("  EXISTS ( ");
							sql.append("    SELECT 'X' ");
							sql.append("    FROM TBCRM_CUST_MAST A ");
							sql.append("    WHERE V.CUST_ID = A.CUST_ID ");
							sql.append("    AND A.BRA_NBR = :bra_nbr ");
							
							if ("0".equals(inputVO_CRM331.getAo_code())) {
								sql.append("    AND A.AO_CODE IS NULL "); //本行客戶空code
							}
							
							sql.append("  ) ");
							sql.append("  OR ");
							sql.append("  EXISTS ( ");
							sql.append("    SELECT 'X' ");
							sql.append("    FROM TBCRM_ACCT_MAST B, TBCRM_CUST_MAST A ");
							sql.append("    WHERE A.CUST_ID = B.CUST_ID ");
							sql.append("    AND B.CUST_ID = V.CUST_ID ");
							sql.append("    AND NVL(ACCT_STATUS,'X') <> '2' ");
							sql.append("    AND SUBSTR(ACCT_TYPE, 1, 2) <> 'LN' ");
							sql.append("    AND A.BRA_NBR = :bra_nbr ");
							sql.append("    AND A.AO_CODE IS NULL ");
							sql.append("  )");
							sql.append(") "); //帳務行	    
							
							queryCondition.setObject("bra_nbr", inputVO_CRM331.getAo_05());
						}
					} else {
						// 未選取分行，但有選取 營運區 || 區域
						if (!StringUtils.isBlank(inputVO_CRM331.getAo_04()) || !StringUtils.isBlank(inputVO_CRM331.getAo_03())) {
							sql.append("AND ( ");
							sql.append("  EXISTS ( ");
							sql.append("    SELECT 'X' ");
							sql.append("    FROM TBCRM_CUST_MAST A ");
							sql.append("    WHERE V.CUST_ID = A.CUST_ID ");
							sql.append("    AND A.BRA_NBR IN (:branch_list) ");
							
							if ("0".equals(inputVO_CRM331.getAo_code())) {
								sql.append("    AND A.AO_CODE IS NULL "); //本行客戶空code
							}
							
							sql.append("  ) ");
							sql.append("  OR ");
							sql.append("  EXISTS ( ");
							sql.append("    SELECT 'X' ");
							sql.append("    FROM TBCRM_ACCT_MAST B, TBCRM_CUST_MAST A ");
							sql.append("    WHERE A.CUST_ID = B.CUST_ID ");
							sql.append("    AND B.CUST_ID = V.CUST_ID ");
							sql.append("    AND NVL(ACCT_STATUS,'X') <> '2' ");
							sql.append("    AND SUBSTR(ACCT_TYPE, 1, 2) <> 'LN' ");
							sql.append("    AND A.BRA_NBR IN (:branch_list) ");
							sql.append("    AND A.AO_CODE IS NULL ");
							sql.append("  )");
							sql.append(") "); //帳務行
	
							List<String> branch_list = new ArrayList<String>();
							for (int i = 0; i < inputVO_CRM331.getBranch_list().size(); i++) {
								branch_list.add(inputVO_CRM331.getBranch_list().get(i).get("DATA"));
							}
							
							queryCondition.setObject("branch_list", branch_list);
						} else {
							// 區域、營運區、分行 皆未選取(總行人員才可)
							if ("0".equals(inputVO_CRM331.getAo_code())) {
								sql.append("AND EXISTS(SELECT 'X' FROM TBCRM_CUST_MAST A WHERE V.CUST_ID = A.CUST_ID AND A.AO_CODE IS NULL) ");
							}
						}
					}
				}
	
				// BS待生效的應也要排除
				sql.append("AND V.CUST_ID NOT in (SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE PROCESS_STATUS in ('L0', 'L1', 'L2', 'L3', 'L4', 'L5', 'BS') AND TRS_TYPE NOT IN ('7', '8', '9')) ");
				
				//客戶ID
				if (!StringUtils.isBlank(inputVO_CRM331.getCust_id())) {
					sql.append("AND V.CUST_ID = :cust_id ");
					queryCondition.setObject("cust_id", inputVO_CRM331.getCust_id());
				}
				
				//客戶姓名
				if (!StringUtils.isBlank(inputVO_CRM331.getCust_name())) {
					sql.append("AND V.CUST_NAME like :cust_name ");
					queryCondition.setObject("cust_name", "%" + inputVO_CRM331.getCust_name() + "%");
				}
				
				//最近異動頻率
				if (!StringUtils.isBlank(inputVO_CRM331.getChg_frq())) {
					sql.append("AND V.CUST_ID NOT IN ( ");
					sql.append("  SELECT CUST_ID ");
					sql.append("  FROM TBCRM_CUST_AOCODE_CHGLOG ");
					sql.append("  WHERE TRUNC(LETGO_DATETIME) >= TRUNC(SYSDATE - :chg_frq) ");
					sql.append(") ");
					queryCondition.setObject("chg_frq", inputVO_CRM331.getChg_frq());
				}
				
				// TODO 上2080 拆2073
//				//RM理專
//				if (!StringUtils.isBlank(inputVO_CRM331.getRm_id())) {
//					sql.append("AND MEM.EMP_ID = :emp_id ");
//					queryCondition.setObject("emp_id", inputVO_CRM331.getRm_id());
//				}
//				
//				//AO CODE最新異動日期(起)
//				if (null != inputVO_CRM331.getsDate()) {
//					sql.append("AND TRUNC(CM.AO_LASTUPDATE) >= :sDate ");
//					queryCondition.setObject("sDate", new Timestamp(inputVO_CRM331.getsDate().getTime()));
//				}
//
//				//AO CODE最新異動日期(迄)
//				if (null != inputVO_CRM331.geteDate()) {
//					sql.append("AND TRUNC(CM.AO_LASTUPDATE) <= :eDate ");
//					queryCondition.setObject("eDate", new Timestamp(inputVO_CRM331.geteDate().getTime()));
//				}
				
				//客戶在ON CODE排除名單不須顯示
				sql.append("AND V.CUST_ID NOT IN (SELECT CUST_ID FROM TBCRM_TRS_CUST_EXCLUDE WHERE NVL(DEL_YN, 'N') = 'N') ");
				
				break;
		}

		// common where

		// 歸屬行
		if (!StringUtils.isBlank(inputVO.getCust_02())) {
			sql.append("AND V.CUST_02 = :cust_02 ");
			queryCondition.setObject("cust_02", inputVO.getCust_02());
		}

		// 理財會員等級----A/B/M/V
		if (!StringUtils.isBlank(inputVO.getCust_04())) {
			switch (inputVO.getCust_04()) {
				case "M":
					sql.append("AND (V.CUST_04 = 'M' OR V.CUST_04 IS NULL) ");
					break;
				default:
					sql.append("AND V.CUST_04 = :cust_04 ");
					queryCondition.setObject("cust_04", inputVO.getCust_04());
					break;
			}
		}

		// 貢獻度等級----E/I/O/OTH/P/S
		if (!StringUtils.isBlank(inputVO.getCust_06())) {
			sql.append("AND V.CUST_06 = :cust_06 ");
			queryCondition.setObject("cust_06", inputVO.getCust_06());
		}

		// 性別----1/2
		if (!StringUtils.isBlank(inputVO.getCust_03())) {
			sql.append("AND V.CUST_03 = :cust_03 ");
			queryCondition.setObject("cust_03", inputVO.getCust_03());
		}

		// 出生年(起)
		if (null != inputVO.getCust_05_sDate()) {
			sql.append("AND TO_CHAR(TRUNC(V.Cust_05), 'YYYY') >= TO_CHAR(:cust_05_sDate, 'YYYY') ");
			queryCondition.setObject("cust_05_sDate", new Timestamp(inputVO.getCust_05_sDate().getTime()));
		}

		// 出生年(迄)
		if (null != inputVO.getCust_05_eDate()) {
			sql.append("AND TO_CHAR(TRUNC(V.Cust_05), 'YYYY') <= TO_CHAR(:cust_05_eDate, 'YYYY') ");
			queryCondition.setObject("cust_05_eDate", new Timestamp(inputVO.getCust_05_eDate().getTime()));
		}

		// 薪轉戶----01/02
		if (!StringUtils.isBlank(inputVO.getCust_09())) {
			switch (inputVO.getCust_09()) {
				case "N":
					sql.append("AND (V.CUST_09 IS NULL OR CUST_09 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_09 = 'Y' ");
					break;
			}
		}

		// 法金戶----01/02
		if (!StringUtils.isBlank(inputVO.getCust_07())) {
			switch (inputVO.getCust_07()) {
				case "N":
					sql.append("AND (V.CUST_07 IS NULL OR CUST_07 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_07 = 'Y' ");
					break;
			}
		}

		// 有無投保註記----Y/N
		if (!StringUtils.isBlank(inputVO.getCust_08())) {
			switch (inputVO.getCust_08()) {
				case "N":
					sql.append("AND (V.CUST_08 IS NULL OR CUST_08 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_08 = 'Y' ");
					break;
			}
		}

		// 潛力註記----Y/N
		if (!StringUtils.isBlank(inputVO.getCust_18())) {
			switch (inputVO.getCust_18()) {
				case "N":
					sql.append("AND V.CUST_18 IS NULL ");
					break;
				case "Y":
					sql.append("AND V.CUST_18 IS NOT NULL ");
					break;
			}
		}

		// 客訴戶
		if (!StringUtils.isBlank(inputVO.getCust_10())) {
			switch (inputVO.getCust_10()) {
				case "N":
					sql.append("AND (V.CUST_10 IS NULL OR CUST_10 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_10 = 'Y' ");
					break;
			}
		}

		// 專業投資人
		if (!StringUtils.isBlank(inputVO.getCust_11())) {
			switch (inputVO.getCust_11()) {
				case "N":
					sql.append("AND (V.CUST_11 IS NULL OR CUST_11 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_11 = 'Y' ");
					break;
			}
		}

		// 可推介
		if (!StringUtils.isBlank(inputVO.getCust_12())) {
			switch (inputVO.getCust_12()) {
				case "N":
					sql.append("AND (V.CUST_12 IS NULL OR CUST_12 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_12 = 'Y' ");
					break;
			}
		}

		// 拒銷戶(RS)
		if (!StringUtils.isBlank(inputVO.getCust_13())) {
			switch (inputVO.getCust_13()) {
				case "N":
					sql.append("AND (V.CUST_13 IS NULL OR CUST_13 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_13 = 'Y' ");
					break;
			}
		}

		// 禁銷戶(NS)
		if (!StringUtils.isBlank(inputVO.getCust_14())) {
			switch (inputVO.getCust_14()) {
				case "N":
					sql.append("AND (V.CUST_14 IS NULL OR CUST_14 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_14 = 'Y' ");
					break;
			}
		}

		// C0戶
		if (!StringUtils.isBlank(inputVO.getCust_15())) {
			switch (inputVO.getCust_15()) {
				case "N":
					sql.append("AND (V.CUST_15 IS NULL OR CUST_15 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.CUST_15 = 'Y' ");
					break;
			}
		}

		// 資產總餘額(起)
		if (!StringUtils.isBlank(inputVO.getAmt_total_s())) {
			sql.append("AND V.AMT_TOTAL >= :amt_total_s ");
			queryCondition.setObject("amt_total_s", getBigDecimal(inputVO.getAmt_total_s()));
		}

		// 資產總餘額(迄)
		if (!StringUtils.isBlank(inputVO.getAmt_total_e())) {
			sql.append("AND V.AMT_TOTAL <= :amt_total_e ");
			queryCondition.setObject("amt_total_e", getBigDecimal(inputVO.getAmt_total_e()));
		}

		// 前日大額100萬以上進出
		if (!StringUtils.isBlank(inputVO.getAmt_19())) {
			switch (inputVO.getAmt_19()) {
				case "N":
					sql.append("AND (V.AMT_19 = '0' OR AMT_19 = 'N') ");
					break;
				case "Y":
					sql.append("AND V.AMT_19 = 'Y' ");
					break;
			}
		}

		// 上月平均全行AUM
		if (!StringUtils.isBlank(inputVO.getAmt_01_s())) {
			sql.append("AND V.AUM_AMT >= :amt_01_s ");
			queryCondition.setObject("amt_01_s", getBigDecimal(inputVO.getAmt_01_s()));
		}

		// 上月平均全行AUM
		if (!StringUtils.isBlank(inputVO.getAmt_01_e())) {
			sql.append("AND V.AUM_AMT <= :amt_01_e ");
			queryCondition.setObject("amt_01_e", getBigDecimal(inputVO.getAmt_01_e()));
		}

		// 全行台外幣活存餘額
		if (!StringUtils.isBlank(inputVO.getAmt_02_s())) {
			sql.append("AND V.AMT_02 >= :amt_02_s ");
			queryCondition.setObject("amt_02_s", getBigDecimal(inputVO.getAmt_02_s()));
		}

		// 全行台外幣活存餘額
		if (!StringUtils.isBlank(inputVO.getAmt_02_e())) {
			sql.append("AND V.AMT_02 <= :amt_02_e ");
			queryCondition.setObject("amt_02_e", getBigDecimal(inputVO.getAmt_02_e()));
		}

		// 全行境外結構型商品
		if (!StringUtils.isBlank(inputVO.getAmt_13_s())) {
			sql.append("AND V.AMT_11 >= :amt_13_s ");
			queryCondition.setObject("amt_13_s", getBigDecimal(inputVO.getAmt_13_s()));
		}

		// 全行境外結構型商品
		if (!StringUtils.isBlank(inputVO.getAmt_13_e())) {
			sql.append("AND V.AMT_11 <= :amt_13_e ");
			queryCondition.setObject("amt_13_e", getBigDecimal(inputVO.getAmt_13_e()));
		}

		// 全行台外幣定存餘額
		if (!StringUtils.isBlank(inputVO.getAmt_05_s())) {
			sql.append("AND V.AMT_05 >= :amt_05_s ");
			queryCondition.setObject("amt_05_s", getBigDecimal(inputVO.getAmt_05_s()));
		}

		// 全行台外幣定存餘額
		if (!StringUtils.isBlank(inputVO.getAmt_05_e())) {
			sql.append("AND V.AMT_05 <= :amt_05_e ");
			queryCondition.setObject("amt_05_e", getBigDecimal(inputVO.getAmt_05_e()));
		}

		// 全行外匯雙享利
		if (!StringUtils.isBlank(inputVO.getAmt_14_s())) {
			sql.append("AND V.AMT_14 >= :amt_14_s ");
			queryCondition.setObject("amt_14_s", getBigDecimal(inputVO.getAmt_14_s()));
		}

		// 全行外匯雙享利
		if (!StringUtils.isBlank(inputVO.getAmt_14_e())) {
			sql.append("AND V.AMT_14 <= :amt_14_e ");
			queryCondition.setObject("amt_14_e", getBigDecimal(inputVO.getAmt_14_e()));
		}

		// 全行基金
		if (!StringUtils.isBlank(inputVO.getAmt_09_s())) {
			sql.append("AND V.AMT_09 >= :amt_09_s ");
			queryCondition.setObject("amt_09_s", getBigDecimal(inputVO.getAmt_09_s()));
		}

		// 全行基金
		if (!StringUtils.isBlank(inputVO.getAmt_09_e())) {
			sql.append("AND V.AMT_09 <= :amt_09_e ");
			queryCondition.setObject("amt_09_e", getBigDecimal(inputVO.getAmt_09_e()));
		}

		// 全行金錢信託
		if (!StringUtils.isBlank(inputVO.getAmt_15_s())) {
			sql.append("AND V.AMT_15 >= :amt_15_s ");
			queryCondition.setObject("amt_15_s", getBigDecimal(inputVO.getAmt_15_s()));
		}

		// 全行金錢信託
		if (!StringUtils.isBlank(inputVO.getAmt_15_e())) {
			sql.append("AND V.AMT_15 <= :amt_15_e ");
			queryCondition.setObject("amt_15_e", getBigDecimal(inputVO.getAmt_15_e()));
		}

		// 全行海外ETF/海外股票
		if (!StringUtils.isBlank(inputVO.getAmt_10_s())) {
			sql.append("AND V.AMT_10 >= :amt_10_s ");
			queryCondition.setObject("amt_10_s", getBigDecimal(inputVO.getAmt_10_s()));
		}

		// 全行海外ETF/海外股票
		if (!StringUtils.isBlank(inputVO.getAmt_10_e())) {
			sql.append("AND V.AMT_10 <= :amt_10_e ");
			queryCondition.setObject("amt_10_e", getBigDecimal(inputVO.getAmt_10_e()));
		}

		// 全行黃金存摺
		if (!StringUtils.isBlank(inputVO.getAmt_16_s())) {
			sql.append("AND V.AMT_16 >= :amt_16_s ");
			queryCondition.setObject("amt_16_s", getBigDecimal(inputVO.getAmt_16_s()));
		}

		// 全行黃金存摺
		if (!StringUtils.isBlank(inputVO.getAmt_16_e())) {
			sql.append("AND V.AMT_16 <= :amt_16_e ");
			queryCondition.setObject("amt_16_e", getBigDecimal(inputVO.getAmt_16_e()));
		}

		// 全行海外債
		if (!StringUtils.isBlank(inputVO.getAmt_11_s())) {
			sql.append("AND V.AMT_13 >= :amt_11_s ");
			queryCondition.setObject("amt_11_s", getBigDecimal(inputVO.getAmt_11_s()));
		}

		// 全行海外債
		if (!StringUtils.isBlank(inputVO.getAmt_11_e())) {
			sql.append("AND V.AMT_13 <= :amt_11_e ");
			queryCondition.setObject("amt_11_e", getBigDecimal(inputVO.getAmt_11_e()));
		}

		// 保險餘額
		if (!StringUtils.isBlank(inputVO.getAmt_17_s())) {
			sql.append("AND V.AMT_17 >= :amt_17_s ");
			queryCondition.setObject("amt_17_s", getBigDecimal(inputVO.getAmt_17_s()));
		}

		// 保險餘額
		if (!StringUtils.isBlank(inputVO.getAmt_17_e())) {
			sql.append("AND V.AMT_17 <= :amt_17_e ");
			queryCondition.setObject("amt_17_e", getBigDecimal(inputVO.getAmt_17_e()));
		}

		// 全行組合式商品
		if (!StringUtils.isBlank(inputVO.getAmt_12_s())) {
			sql.append("AND V.AMT_12 >= :amt_12_s ");
			queryCondition.setObject("amt_12_s", getBigDecimal(inputVO.getAmt_12_s()));
		}

		// 全行組合式商品
		if (!StringUtils.isBlank(inputVO.getAmt_12_e())) {
			sql.append("AND V.AMT_12 <= :amt_12_e ");
			queryCondition.setObject("amt_12_e", getBigDecimal(inputVO.getAmt_12_e()));
		}

		// 貸款餘額
		if (!StringUtils.isBlank(inputVO.getAmt_18_s())) {
			sql.append("AND V.AMT_18 >= :amt_18_s ");
			queryCondition.setObject("amt_18_s", getBigDecimal(inputVO.getAmt_18_s()));
		}

		// 貸款餘額
		if (!StringUtils.isBlank(inputVO.getAmt_18_e())) {
			sql.append("AND V.AMT_18 <= :amt_18_e ");
			queryCondition.setObject("amt_18_e", getBigDecimal(inputVO.getAmt_18_e()));
		}

		// 奈米投餘額
		if (!StringUtils.isBlank(inputVO.getAmt_25_s())) {
			sql.append("AND V.AMT_25 >= :amt_25_s ");
			queryCondition.setObject("amt_25_s", getBigDecimal(inputVO.getAmt_25_s()));
		}

		// 奈米投餘額
		if (!StringUtils.isBlank(inputVO.getAmt_25_e())) {
			sql.append("AND V.AMT_25 <= :amt_25_e ");
			queryCondition.setObject("amt_25_e", getBigDecimal(inputVO.getAmt_25_e()));
		}

		// 金市海外債餘額
		if (!StringUtils.isBlank(inputVO.getAmt_26_s())) {
			sql.append("AND V.AMT_26 >= :amt_26_s ");
			queryCondition.setObject("amt_26_s", getBigDecimal(inputVO.getAmt_26_s()));
		}

		// 金市海外債餘額
		if (!StringUtils.isBlank(inputVO.getAmt_26_e())) {
			sql.append("AND V.AMT_26 <= :amt_26_e ");
			queryCondition.setObject("amt_26_e", getBigDecimal(inputVO.getAmt_26_e()));
		}
		
		// 風險屬性--狀態--01/02/03
		if (!StringUtils.isBlank(inputVO.getAtr_01())) {
			sql.append("AND V.ATR_01 = :atr_01 ");
			queryCondition.setObject("atr_01", inputVO.getAtr_01());
		}

		// 風險屬性--等級--C1~C4
		if (!StringUtils.isBlank(inputVO.getAtr_02())) {
			sql.append("AND V.ATR_02 = :atr_02 ");
			queryCondition.setObject("atr_02", inputVO.getAtr_02());
		}

		// 最後做風險屬性日期-S
		if (null != inputVO.getAtr_03_sDate()) {
			sql.append("AND TRUNC(V.ATR_03) >= :atr_03_sDate ");
			queryCondition.setObject("atr_03_sDate", new Timestamp(inputVO.getAtr_03_sDate().getTime()));
		}

		// 最後做風險屬性日期-E
		if (null != inputVO.getAtr_03_eDate()) {
			sql.append("AND TRUNC(V.ATR_03) <= :atr_03_eDate ");
			queryCondition.setObject("atr_03_eDate", new Timestamp(inputVO.getAtr_03_eDate().getTime()));
		}

		// 新增大於小於--未經營月數--B/S/E
		if (!StringUtils.isBlank(inputVO.getManage_01())) {
			switch (inputVO.getManage_04()) {
				case "B":
					sql.append("AND V.UNTAKE_CARE_MONTH > :manage_01 ");
					break;
				case "S":
					sql.append("AND V.UNTAKE_CARE_MONTH < :manage_01 ");
					break;
				case "E":
					sql.append("AND V.UNTAKE_CARE_MONTH = :manage_01 ");
					break;
				default :
					sql.append("AND V.UNTAKE_CARE_MONTH = :manage_01 ");
					break;
			}
			
			queryCondition.setObject("manage_01", inputVO.getManage_01());
		}

		// 經營頻次符合度---N/Y(TAKE_CARE_MATCH_YN)
		if (!StringUtils.isBlank(inputVO.getManage_02())) {
			switch (inputVO.getManage_02()) {
				case "N":
					sql.append("AND (V.TAKE_CARE_MATCH_YN IS NULL OR V.TAKE_CARE_MATCH_YN = 'N') ");
					break;
				case "Y":
					sql.append("AND V.TAKE_CARE_MATCH_YN = 'Y' ");
					break;
			}
		}

		// 新登錄日期-S(REG_DATE)
		if (null != inputVO.getManage_03_sDate()) {
			sql.append("AND TRUNC(V.REG_DATE) >= :manage_03_sDate ");
			queryCondition.setObject("manage_03_sDate", new Timestamp(inputVO.getManage_03_sDate().getTime()));
		}

		// 新登錄日期-E
		if (null != inputVO.getManage_03_eDate()) {
			sql.append("AND TRUNC(V.REG_DATE) <= :manage_03_eDate ");
			queryCondition.setObject("manage_03_eDate", new Timestamp(inputVO.getManage_03_eDate().getTime()));
		}

		// 手收貢獻度 -- 查詢區間(近三個月、近六個月、近一年) / 手續費收入
		if (!StringUtils.isBlank(inputVO.getManage_05_Date()) && !StringUtils.isBlank(inputVO.getManage_06_fee_s()) && !StringUtils.isBlank(inputVO.getManage_06_fee_e())) {
			switch (inputVO.getManage_05_Date()) {
				case "Three":	// 近三個月
					sql.append("AND V.TXN_FEE_01 BETWEEN :txn_fee_s AND :txn_fee_e ");
					break;
				case "Six": 	// 近六個月
					sql.append("AND V.TXN_FEE_02 BETWEEN :txn_fee_s AND :txn_fee_e ");
					break;
				case "OneYear": // 近一年
					sql.append("AND V.TXN_FEE_03 BETWEEN :txn_fee_s AND :txn_fee_e ");
					break;
			}
			
			queryCondition.setObject("txn_fee_s", getBigDecimal(inputVO.getManage_06_fee_s()));
			queryCondition.setObject("txn_fee_e", getBigDecimal(inputVO.getManage_06_fee_e()));
		}
		
		// 活躍度 : WMS-CR-20211013-01_擬客戶活躍度週報供RM參考 add by ocean
		if (StringUtils.isNotEmpty(inputVO.getActPrdNums())) {
			sql.append("AND V.ACT_PRD_NUMS = :actPrdNums ");
			queryCondition.setObject("actPrdNums", getBigDecimal(inputVO.getActPrdNums()));
		}
		
		// 是否實動戶 Y/N : WMS-CR-20220517-01_新增實動戶上傳註記 add by ocean
		if (!StringUtils.isBlank(inputVO.getIS_ACTUAL())) {
			switch (inputVO.getIS_ACTUAL()) {
				case "N":
					sql.append("AND (V.ACT_FLAG IS NULL OR V.ACT_FLAG = 'N') ");
					break;
				case "Y":
					sql.append("AND V.ACT_FLAG = 'Y' ");
					break;
			}
		}
		
		// 是否有承租保管箱 Y/N : WMS-CR-20231222-01_新增保管箱資訊及分行追蹤管理報表調整 add by ocean
		if (!StringUtils.isBlank(inputVO.getIS_VOC())) {
			switch (inputVO.getIS_VOC()) {
				case "N":
					sql.append("AND (V.VOC_FLAG IS NULL OR V.VOC_FLAG = 'N') ");
					break;
				case "Y":
					sql.append("AND V.VOC_FLAG = 'Y' ");
					break;
			}
		}
		
		// W-8BEN(W-8BEN-E)
		if (!StringUtils.isBlank(inputVO.getCust_w8ben())) {
			switch (inputVO.getCust_w8ben()) {
				case "N":
					sql.append("AND (V.W8BEN_YN IS NULL OR V.W8BEN_YN = 'N') ");
					break;
				case "Y":
					sql.append("AND V.W8BEN_YN = 'Y' ");
					break;
			}
		}

		// 燈號----1:黃燈 2:紅燈
//		if (!StringUtils.isBlank(inputVO.getManage_07())) {
//			sql.append("and V.CONTACT_LIGHT = :manage_07 ");
//			queryCondition.setObject("manage_07", inputVO.getManage_07());
//		}	
//				
//		sql.append("ORDER BY V.CUST_ID ");
		
		queryCondition.setMaxResults((Integer) SysInfo.getInfoValue(FubonSystemVariableConsts.QRY_MAX_RESULTS));
		queryCondition.setQueryString(sql.toString().replaceAll("\\s+", " "));
		
		return_VO.setResultList(distinct(dam.exeQuery(queryCondition)));

		return return_VO;
	}
	
	//#1854_改善CRM210.inquire_common,查詢效能
	private List<Map<String, Object>> distinct(List<Map<String, Object>> list) {
		
		Set<Map<String, Object>> set = new LinkedHashSet<>(list);
		
		return new ArrayList<Map<String, Object>>(set);
	}

	/** 轉Decimal **/
	public BigDecimal getBigDecimal(Object value) {

		BigDecimal ret = null;
		
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
			}
		}

		return ret;
	}

	public void getAllBranch(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM210OutputVO return_VO = new CRM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT BRANCH_NBR, BRANCH_NAME FROM VWORG_DEFN_INFO ");
		
		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQueryWithoutSort(queryCondition));
		
		this.sendRtnObject(return_VO);
	}

}