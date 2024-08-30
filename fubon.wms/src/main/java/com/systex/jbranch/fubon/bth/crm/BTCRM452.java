package com.systex.jbranch.fubon.bth.crm;

import com.ibm.icu.text.NumberFormat;
import com.systex.jbranch.app.common.fps.table.TBAPI_MPLUS_TRACEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPLY_PERIODVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPLY_SINGLEVO;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.mplus.MPlusInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusUtil;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.systex.jbranch.fubon.jlb.DataFormat.getNameForHighRisk;

@Component("btcrm452")
@Scope("prototype")
public class BTCRM452 extends BizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	public void pushAuthMessageBT(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT PABTH_UTIL.FC_IsHoliday(sysdate, 'TWD')AS ISHOLIDAY FROM DUAL");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> holidayList = dam.exeQuery(queryCondition);

		if("N".equals(holidayList.get(0).get("ISHOLIDAY"))){	//非假日(營業日)才執行

			sql = new StringBuffer();

			sql.append("SELECT APPLY_SEQ, CUST_ID, AUTH_STATUS + 1 AS AUTH_STATUS, CREATOR, ");
			sql.append("CASE APPLY_TYPE WHEN '1' THEN '1' WHEN '2' THEN '1' ELSE '2' END AS PROD_TYPE, ");
			sql.append("'single' as TYPE from TBCRM_BRG_APPLY_SINGLE ");
			sql.append("WHERE AUTH_STATUS < HIGHEST_AUTH_LV AND APPLY_STATUS = '1' ");
			sql.append("UNION ");
			sql.append("select APPLY_SEQ, CUST_ID, AUTH_STATUS + 1 AS AUTH_STATUS, CREATOR, ");
			sql.append("APPLY_TYPE AS PROD_TYPE, 'period' as TYPE from TBCRM_BRG_APPLY_PERIOD ");
			sql.append("WHERE AUTH_STATUS < HIGHEST_AUTH_LV AND APPLY_STATUS = '1' ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> updateList = dam.exeQuery(queryCondition);
			for(Map<String, Object> updateMap : updateList) {

				String custID = updateMap.get("CUST_ID").toString();
				String prodType = updateMap.get("PROD_TYPE").toString();
				String creator = updateMap.get("CREATOR").toString();
				String empIDList = getReviewLevelEmpList(custID, String.valueOf(updateMap.get("AUTH_STATUS")), prodType, creator);

				if("single".equals(updateMap.get("TYPE"))){
					TBCRM_BRG_APPLY_SINGLEVO svo = new TBCRM_BRG_APPLY_SINGLEVO();
					svo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, updateMap.get("APPLY_SEQ").toString());

					switch(updateMap.get("AUTH_STATUS").toString()) {

						case "1":
							String mgr_emp_id_1 = (svo.getMGR_EMP_ID_1() == null ? "" : svo.getMGR_EMP_ID_1());
							if(!mgr_emp_id_1.equals(empIDList)){
								svo.setMGR_EMP_ID_1(empIDList);
								dam.update(svo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;

						case "2":
							String mgr_emp_id_2 = (svo.getMGR_EMP_ID_2() == null ? "" : svo.getMGR_EMP_ID_2());
							if(!mgr_emp_id_2.equals(empIDList)){
								svo.setMGR_EMP_ID_2(empIDList);
								dam.update(svo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;

						case "3":
							String mgr_emp_id_3 = (svo.getMGR_EMP_ID_3() == null ? "" : svo.getMGR_EMP_ID_3());
							if(!mgr_emp_id_3.equals(empIDList)){
								svo.setMGR_EMP_ID_3(empIDList);
								dam.update(svo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;

						case "4":
							String mgr_emp_id_4 = (svo.getMGR_EMP_ID_4() == null ? "" : svo.getMGR_EMP_ID_4());
							if(!mgr_emp_id_4.equals(empIDList)){
								svo.setMGR_EMP_ID_4(empIDList);
								dam.update(svo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_SINGLE", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;
					}

				}else{
					TBCRM_BRG_APPLY_PERIODVO pvo = new TBCRM_BRG_APPLY_PERIODVO();
					pvo = (TBCRM_BRG_APPLY_PERIODVO) dam.findByPKey(TBCRM_BRG_APPLY_PERIODVO.TABLE_UID, updateMap.get("APPLY_SEQ").toString());

					switch(updateMap.get("AUTH_STATUS").toString()) {

						case "1":
							String mgr_emp_id_1 = (pvo.getMGR_EMP_ID_1() == null ? "" : pvo.getMGR_EMP_ID_1());
							if(!mgr_emp_id_1.equals(empIDList)){
								pvo.setMGR_EMP_ID_1(empIDList);
								dam.update(pvo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;

						case "2":
							String mgr_emp_id_2 = (pvo.getMGR_EMP_ID_2() == null ? "" : pvo.getMGR_EMP_ID_2());
							if(!mgr_emp_id_2.equals(empIDList)){
								pvo.setMGR_EMP_ID_2(empIDList);
								dam.update(pvo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;

						case "3":
							String mgr_emp_id_3 = (pvo.getMGR_EMP_ID_3() == null ? "" : pvo.getMGR_EMP_ID_3());
							if(!mgr_emp_id_3.equals(empIDList)){
								pvo.setMGR_EMP_ID_3(empIDList);
								dam.update(pvo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;

						case "4":
							String mgr_emp_id_4 = (pvo.getMGR_EMP_ID_4() == null ? "" : pvo.getMGR_EMP_ID_4());
							if(!mgr_emp_id_4.equals(empIDList)){
								pvo.setMGR_EMP_ID_4(empIDList);
								dam.update(pvo);

								//==== for CBS 模擬日測試用========
								setfakeSysdate("TBCRM_BRG_APPLY_PERIOD", updateMap.get("APPLY_SEQ").toString());
								//================================================================
							}
							break;
					}
				}
			}

			sql = new StringBuffer();
			sql.append("SELECT * FROM ");
			sql.append("(SELECT CUST_ID, DECODE(AUTH_STATUS, '0', MGR_EMP_ID_1, '1', MGR_EMP_ID_2, '2', MGR_EMP_ID_3, '3', MGR_EMP_ID_4) MGR_EMP_ID, ");
			sql.append("CREATOR, 'single' as TYPE from TBCRM_BRG_APPLY_SINGLE ");
			sql.append("WHERE AUTH_STATUS < HIGHEST_AUTH_LV ");
			sql.append("AND APPLY_STATUS = '1' ");
			sql.append("UNION ");
			sql.append("select CUST_ID, DECODE(AUTH_STATUS, '0', MGR_EMP_ID_1, '1', MGR_EMP_ID_2, '2', MGR_EMP_ID_3, '3', MGR_EMP_ID_4) MGR_EMP_ID,  ");
			sql.append("CREATOR, 'period' as TYPE from TBCRM_BRG_APPLY_PERIOD ");
			sql.append("WHERE AUTH_STATUS < HIGHEST_AUTH_LV ");
			sql.append("AND APPLY_STATUS = '1' ");
			sql.append(") WHERE MGR_EMP_ID IS NOT NULL ");
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			for(Map<String, Object> map : list) {

				String custID = map.get("CUST_ID").toString();
				String type = map.get("TYPE").toString();
				String creator = map.get("CREATOR").toString();

				if(map.get("MGR_EMP_ID").toString().contains(",")){		//一個層級有多位覆核主管
					String[] empIds = map.get("MGR_EMP_ID").toString().split(",");
					for(String empId: empIds){
						pushAuthMessage(empId, type, custID, creator);
					}
				}else{
					String empId = map.get("MGR_EMP_ID").toString();
					pushAuthMessage(empId, type, custID, creator);
				}
			}
		}
	}

	public void pushAuthMessage(String empId, String type, String custID, String creator) throws JBranchException, IOException {
		dam = this.getDataAccessManager();

		// 取得理專資訊
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT BRANCH_NBR, BRANCH_NAME, AO_CODE, EMP_NAME ");
		sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :emp_id ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("emp_id", creator);

		List<Map<String, Object>> creatorList = dam.exeQuery(queryCondition);

		// 取得客戶資訊
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("SELECT CUST_ID, NVL(SUM(ACT_PRFT), 0) AS Y_PROFEE ");
		sb.append("FROM TBCRM_CUST_PROFEE ");
		sb.append("WHERE DATA_YEAR||DATA_MONTH BETWEEN TO_CHAR(CURRENT_DATE-365, 'YYYYMM') ");
		sb.append("AND TO_CHAR(CURRENT_DATE, 'YYYYMM') ");
		sb.append("GROUP BY CUST_ID ");
		sb.append(") ");
		sb.append("SELECT CM.CUST_ID, CM.CUST_NAME, CM.CON_DEGREE, CM.VIP_DEGREE, CON_D.PARAM_NAME AS CON_DEGREE_NAME, CON_V.PARAM_NAME AS VIP_DEGREE_NAME, NVL(CM.AUM_AMT, 0) AS AUM_AMT, CM.BRA_NBR, NVL(CP.Y_PROFEE, 0) AS Y_PROFEE ");
		sb.append("FROM TBCRM_CUST_MAST CM ");
		sb.append("LEFT JOIN BASE CP ON CM.CUST_ID = CP.CUST_ID ");
		sb.append("LEFT JOIN TBSYSPARAMETER CON_D ON CON_D.PARAM_TYPE = 'CRM.CON_DEGREE' AND CON_D.PARAM_CODE = CM.CON_DEGREE ");
		sb.append("LEFT JOIN TBSYSPARAMETER CON_V ON CON_V.PARAM_TYPE = 'CRM.VIP_DEGREE' AND CON_V.PARAM_CODE = CM.VIP_DEGREE ");
		sb.append("WHERE CM.CUST_ID = :cust_id ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("cust_id", custID);

		List<Map<String, Object>> custDTL = dam.exeQuery(queryCondition);

		// 文訊息內容,當msgType=I時為必填,支援多組訊息,順序按Array中之排序
		StringBuffer cust_sb = new StringBuffer();
		cust_sb.append((StringUtils.equals("period", type) ? "期間議價授權通知" : "單次議價授權通知")).append("\n");
		cust_sb.append("\n");
		if (custDTL.size() > 0) {
			String custName = getNameForHighRisk(StringUtils.defaultString((String) custDTL.get(0).get("CUST_NAME")));
			cust_sb.append("客戶姓名　　：").append(custName).append("\n");
			cust_sb.append("貢獻度等級　：").append(custDTL.get(0).get("CON_DEGREE_NAME")).append("\n");
			cust_sb.append("理財會員等級：").append(custDTL.get(0).get("VIP_DEGREE_NAME")).append("\n");
			if(StringUtils.isNotBlank(ObjectUtils.toString(custDTL.get(0).get("Y_PROFEE")))){
				double y_profee = Double.parseDouble(custDTL.get(0).get("Y_PROFEE").toString());
				cust_sb.append("近一年手收　：").append(NumberFormat.getNumberInstance(Locale.US).format(y_profee)).append("\n");
			}else{
				cust_sb.append("近一年手收　：0").append("\n");
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(custDTL.get(0).get("AUM_AMT")))){
				double aum_amt = Double.parseDouble(custDTL.get(0).get("AUM_AMT").toString());
				cust_sb.append("前日ＡＵＭ　：").append(NumberFormat.getNumberInstance(Locale.US).format(aum_amt));
			}else{
				cust_sb.append("前日ＡＵＭ　： 0");
			}
		}
		cust_sb.append("\n");
		cust_sb.append("申請分行　　：").append(creatorList.get(0).get("BRANCH_NBR")).append(" ").append(creatorList.get(0).get("BRANCH_NAME"));
		cust_sb.append("\n");
		String empName = getNameForHighRisk(StringUtils.defaultString((String) creatorList.get(0).get("EMP_NAME")));
		cust_sb.append("申請理專　　：").append(creatorList.get(0).get("AO_CODE")).append(" ").append(empName);
		cust_sb.append("\n");
		cust_sb.append("欲進行授權請點選下方檢視詳情。");

		String [] empArray = empId.split(",");
		List<String> tempArray = new ArrayList<String>();//暫存覆核主管EMPID及代理人EMPID

		//只要是代理狀態存在，m+就不通知被代理人 20170921
		for(String empstr : empArray){

			//取代理人
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT AGT.AGENT_ID FROM TBORG_AGENT AGT WHERE 1=1  ");
			sb.append("AND AGT.EMP_ID = :empID AND AGT.AGENT_STATUS = 'S' ");

			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("empID", empstr);

			List<Map<String, Object>> agtList = dam.exeQuery(queryCondition);

			if(agtList.size() > 0){
				for(int a = 0; a < agtList.size(); a++){
					tempArray.add(agtList.get(a).get("AGENT_ID").toString()); //覆核主管的代理人EMPID
				}
			}else{
				tempArray.add(empstr);//覆核主管EMPID
			}
		}

		// M+留軌
		TBAPI_MPLUS_TRACEVO tracevo = new TBAPI_MPLUS_TRACEVO();
		tracevo.setCUST_ID(custID);
		tracevo.setEMP_ID(StringUtils.join(tempArray, ","));
		tracevo.setCONTENT(cust_sb.toString());
		dam.create(tracevo);

		for(String empstr : tempArray) {
			MPlusInputVO inputVO = new MPlusInputVO();


			// FIXME: 以empId重DB中查出empPhones(目前table來源尚未確定)
	        // 若targetType=1,則必須上傳名單檔案。名單檔案格式為一行一個門號的txt檔
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT MPLUS_UID FROM TBAPI_MPLUS WHERE EMP_NUMBER = :empid ");
			queryCondition.setObject("empid", empstr);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(list.size() == 0)
				continue;

			inputVO.setTargetType("5"); // 發送目標型態 0: 全員發送 1: 指定名單 2: 群組 3: 部門
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(ObjectUtils.toString(list.get(0).get("MPLUS_UID")).getBytes());
			byte[] bytes_1 = baos.toByteArray();
	        MPlusInputVO.BinaryFile file_1 = inputVO.new BinaryFile();
	        file_1.setFileName("target.txt");
	        file_1.setFileCxt(bytes_1);
	        inputVO.setTarget(file_1); // 若 targetType=1,則必須上傳名單檔案。名單檔案格式為一行一個門號的 txt 檔

	        inputVO.setMsgType("I"); // 訊息型態 T: 文字 P: 圖片 I: InfoPush
			inputVO.setInfopushCount("1"); //InfoPush 內容個數；若 msgType=I 必填
			inputVO.setTemplateType("1"); //手機上訊息呈現的版型 1:版型一(內嵌中繼頁, 連結另開網 頁) 2: 版型二(另開網頁),若 msgType=I,則必填

			// 文訊息內容,當msgType=I時為必填,支援多組訊息,順序按Array中之排序
			JSONObject title = new JSONObject();
	        List<String> list_title = new ArrayList<String>();
	        list_title.add(cust_sb.toString());
	        title.put("title", list_title);
	        inputVO.setHeadline(title);

	        //第一組 info-push圖檔,若msgType=I時必填,須為jpg檔案
	  		File jpg = new File(DataManager.getRealPath() + "/assets/images/logo_01.jpg");
	  		BufferedImage bufferedImage = ImageIO.read(jpg);
	  		ByteArrayOutputStream img = new ByteArrayOutputStream();
	  		ImageIO.write(bufferedImage, "jpg", img);
	  		img.flush();
	  		byte[] bytes_2 = img.toByteArray();
	  		MPlusInputVO.BinaryFile file_2 = inputVO.new BinaryFile();
	  		file_2.setFileName("logo_01.jpg");
	  		file_2.setFileCxt(bytes_2);
	  		inputVO.setIconFile1(file_2); // 第一組 info-push 圖檔 ,若 msgType=I 時必填, 須為 jpg 檔案

	  		//從TBSYSPARAMETER中抓取 M_PLUS.URL (http://203.74.183.85:9801/)
	  		XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> m_plus_url = xmlInfo.doGetVariable("M_PLUS.URL", FormatHelper.FORMAT_3);

			String mPlusURL = m_plus_url.get("1");

	  		// 內容鏈結,若msgType=I,至少填入一組,須與Infopush內容數量對應,格式為正確的URL連結
	  		JSONObject url = new JSONObject();
	        List<String> list_url = new ArrayList<String>();
	        list_url.add( mPlusURL + "CRM452.html?empID=" + empstr + "&type=" + type + "&custID=" + custID + "&time=" + new Date().getTime());
	        url.put("url", list_url);
	        inputVO.setInfoUrl(url);

	        MPlusUtil mplusUtil = (MPlusUtil) PlatformContext.getBean("mplusutil");
			mplusUtil.send2MPlus(inputVO);
		}
	}

	public String getReviewLevelEmpList (String custID, String levelNO, String prodType, String creator) throws JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT BRANCH_NBR FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = :creator ");
		queryCondition.setObject("creator", creator);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
		String creatorBranchNbr = tempList.get(0).get("BRANCH_NBR").toString();

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH BASE AS (");
		sb.append("SELECT CASE WHEN REPLACE(R.SYS_ROLE, '_ROLE', '') <> 'HEADMGR' THEN DTL2.REGION_CENTER_ID ELSE NULL END AS REGION_CENTER_ID,");
		sb.append("       CASE WHEN REPLACE(R.SYS_ROLE, '_ROLE', '') NOT IN ('HEADMGR', 'ARMGR') THEN DTL2.BRANCH_AREA_ID ELSE NULL END AS BRANCH_AREA_ID,");
		sb.append("       CASE WHEN REPLACE(R.SYS_ROLE, '_ROLE', '') NOT IN ('HEADMGR', 'ARMGR', 'MBRMGR') THEN DTL2.BRANCH_NBR ELSE NULL END AS BRANCH_NBR,");
		sb.append("       R.ROLE_ID ");
		sb.append("FROM TBORG_ROLE R,");
		sb.append("     (SELECT DTL.REGION_CENTER_ID, DTL.BRANCH_AREA_ID, DTL.BRANCH_NBR, PRI.ROLEID ");
		sb.append("      FROM TBSYSSECUROLPRIASS PRI,");
		sb.append("           (SELECT CUST.CUST_ID, CUST.CON_DEGREE,");
		sb.append("                   RC.DEPT_ID AS REGION_CENTER_ID, OP.DEPT_ID AS BRANCH_AREA_ID, BR.DEPT_ID AS BRANCH_NBR,");
		sb.append("                   BRG.PROD_TYPE, BRG.LEVEL_NO, BRG.DISCOUNT, BRG.ROLE_LIST ");
		sb.append("            FROM TBCRM_CUST_MAST CUST ");
		sb.append("            LEFT JOIN TBORG_DEFN BR ON BR.ORG_TYPE = '50' AND BR.DEPT_ID = :creatorBranchNbr ");
		sb.append("            LEFT JOIN TBORG_DEFN OP ON OP.ORG_TYPE = '40' AND (BR.PARENT_DEPT_ID = OP.DEPT_ID OR OP.DEPT_ID = :creatorBranchNbr) ");
		sb.append("            LEFT JOIN TBORG_DEFN RC ON RC.ORG_TYPE = '30' AND (OP.PARENT_DEPT_ID = RC.DEPT_ID OR RC.DEPT_ID = :creatorBranchNbr) ");
		sb.append("            LEFT JOIN TBCRM_BRG_SETUP BRG ON BRG.CON_DEGREE = NVL(CUST.CON_DEGREE, 'OTH') ");
		sb.append("            WHERE CUST.CUST_ID = :custID ");
		sb.append("            AND BRG.LEVEL_NO = :levelNO ");
		sb.append("            AND BRG.PROD_TYPE = :prodType ) DTL  ");
		sb.append("      WHERE INSTR((DTL.ROLE_LIST), PRI.PRIVILEGEID) > 0) DTL2 ");
		sb.append("WHERE INSTR((DTL2.ROLEID), R.ROLE_ID) > 0 ");
		sb.append(") ");
		sb.append("SELECT MEM.EMP_ID ");
		sb.append("FROM BASE ");
		sb.append("LEFT JOIN (SELECT EMP_ID, ROLE_ID, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR FROM VWORG_BRANCH_EMP_DETAIL_INFO UNION SELECT EMP_ID, ROLE_ID, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR FROM VWORG_EMP_PLURALISM_INFO) MEM ON MEM.ROLE_ID = BASE.ROLE_ID ");
		sb.append("AND (MEM.REGION_CENTER_ID = BASE.REGION_CENTER_ID OR (MEM.REGION_CENTER_ID IS NULL AND BASE.REGION_CENTER_ID IS NULL)) ");
		sb.append("AND (MEM.BRANCH_AREA_ID = BASE.BRANCH_AREA_ID OR (MEM.BRANCH_AREA_ID IS NULL AND BASE.BRANCH_AREA_ID IS NULL)) ");
		sb.append("AND (MEM.BRANCH_NBR = BASE.BRANCH_NBR OR (MEM.BRANCH_NBR IS NULL AND BASE.BRANCH_NBR IS NULL)) ");
		sb.append("AND BASE.ROLE_ID = MEM.ROLE_ID ");
		sb.append("WHERE MEM.EMP_ID IS NOT NULL ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("custID", custID);
		queryCondition.setObject("levelNO", levelNO);
		queryCondition.setObject("prodType", prodType);

		queryCondition.setObject("creatorBranchNbr", creatorBranchNbr);

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String empList = "";
		for (Integer i = 0; i < list.size(); i ++) {
			empList = empList + (String) list.get(i).get("EMP_ID");
			if ((i + 1) == list.size()) {
			} else {
				empList = empList + ",";
			}
		}

		return empList;
	}

	/** 議價 For CBS 環境先用模擬日代替 **/
	private void setfakeSysdate(String table, String seq) throws JBranchException {
		Manager.manage(getDataAccessManager())
				.append("update " + table + " ")
				.append("set LASTUPDATE = to_date(:fake, 'yyyymmddhh24miss') ")
				.append("where APPLY_SEQ = :seq ")
				.put("fake", cbsservice.getCBSTestDate())
				.put("seq", seq)
				.update();
	}
}
