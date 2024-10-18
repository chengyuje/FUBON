package com.systex.jbranch.fubon.bth.crm;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("btcrm3502")
@Scope("prototype")
/*
 * #2188_理專異動通知調整 2024.10.07 SamTu
 * 新查詢邏輯
 * 移除限制條件，使其能查出NEW_AOCODE為空的案例
 * WHERE NEW_AO_CODE IS NOT NULL
 * AND NEW_AO_NAME IS NOT NULL
 * 新篩選邏輯注意事項
 * 1. NEW_AOCODE為空的情況 不能寄出
 * 2. 前舊理專姓名相同，但從維護CODE變成主、兼CODE要寄出
 * 3. 多判斷NEW_AO_NAME要有值才寄出 (不然寄出去的信沒新理專名稱)
 */
public class BTCRM3502 extends BizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public void calculateAOChangeNotification(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 抓取上週AO有變更紀錄的客戶
		// mantis 0004091: WMS-TDS-BTCRM-3502-客戶交接後續寄送控管郵件計算批次 sen 2018/03/05 (改)
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.addAll(getLastWeekMailDatas(true));
		list.addAll(getLastWeekMailDatas(false));
		
		// check
		ArrayList<Map<String, Object>> aListSameBRH = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> aListDiffBRH = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> aListMHSameBRH = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> aListMHDiffBRH = new ArrayList<Map<String, Object>>();

		// 做資料排除動作 
		// CASE 1 排除 客戶郵寄地址或分行聯絡資訊沒資料
		// CASE 2 排除 客戶移來移去的作法
		// 同一個客戶就取最新根最舊的兩筆
		// old ao code = new ao code 不寄
		// old ao code != new ao code 
		// 如果相同 EMP_ID 代表理專人同一個主 code 兼 code 互換 不寄
		// 其他 要寄
		List<Map<String, Object>> afterPrepareList = new ArrayList<Map<String, Object>>();
		Map<String, List<Map<String, Object>>> groupCustList = new HashMap<String, List<Map<String, Object>>>();

		doGroupData(list, groupCustList);
		doDataPrepare(afterPrepareList, groupCustList);
		transferUHRMData(afterPrepareList);

		for (int i = 0; i < afterPrepareList.size(); i++) {
			// E、I等級客戶以實體信通知 / P、O、S 或無等級客戶以Email通知
			switch (ObjectUtils.toString(afterPrepareList.get(i).get("CON_DEGREE"))) {
				case "E":
				case "I":
					// 是否同分行
					if (ObjectUtils.toString(afterPrepareList.get(i).get("ORG_AO_BRH")).equals(ObjectUtils.toString(afterPrepareList.get(i).get("NEW_AO_BRH")))) {
						// 同分行：有通訊地址寄通訊地址，沒有通訊地址寄 Email，若都無就不處理
						// 是否有通訊地址
						if (checkMap(afterPrepareList.get(i), "COM_ADDRESS")) {
							aListSameBRH.add(afterPrepareList.get(i));
						} else {
							// 是否有 email
							if (afterPrepareList.get(i).get("EMAIL") != null) {
								//sendMail1(list.get(i), type_2_html);
								aListMHSameBRH.add(afterPrepareList.get(i));
							}
						}
					} else {
						// 不同分行：有通訊地址寄通訊地址，沒有通訊地址寄 Email，若都無就不處理
						if (checkMap(afterPrepareList.get(i), "COM_ADDRESS")) {
							aListDiffBRH.add(afterPrepareList.get(i));
						} else {
							if (afterPrepareList.get(i).get("EMAIL") != null) {
								//sendMail1(list.get(i), type_2_html);
								aListMHDiffBRH.add(afterPrepareList.get(i));
							}
						}
					}
					break;
				default:
					if (ObjectUtils.toString(afterPrepareList.get(i).get("ORG_AO_BRH")).equals(ObjectUtils.toString(afterPrepareList.get(i).get("NEW_AO_BRH")))) {
						// 如無Email則寄實體信
						if (afterPrepareList.get(i).get("EMAIL") != null) {
							//sendMail1(list.get(i), type_2_html);
							aListMHSameBRH.add(afterPrepareList.get(i));
						} else {
							// 寄送實體信要確認是否有通訊地址
							if (checkMap(afterPrepareList.get(i), "COM_ADDRESS")) {
								aListSameBRH.add(afterPrepareList.get(i));
							}
						}
					} else {
						// 如無Email則寄實體信
						if (afterPrepareList.get(i).get("EMAIL") != null) {
							//sendMail2(list.get(i), type_3_html);
							aListMHDiffBRH.add(afterPrepareList.get(i));
						} else {
							// 寄送實體信要確認是否有通訊地址
							if (checkMap(afterPrepareList.get(i), "COM_ADDRESS")) {
								aListDiffBRH.add(afterPrepareList.get(i));
							}
						}
					}
					
					break;
			}
		}
		
		// 將aListSameBRH轉成txt輸出到 WM_INFO_CUST_CHGAO_SAME_BRH.YYYYMMDD
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = tempPath + "reports\\WM_INFO_CUST_CHGAO_SAME_BRH." + sdf.format(new Date());
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		for (Map<String, Object> map : aListSameBRH) {
			String temp = "";
			temp += ObjectUtils.toString(map.get("CUST_NAME")).replaceAll(",", "");
			temp += "," + ObjectUtils.toString(map.get("SIMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("EMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665");
			temp += "," + ObjectUtils.toString(map.get("NEW_AO_NAME"));
			temp += "," + ObjectUtils.toString(map.get("COM_ZIP_CODE"));
			temp += "," + ObjectUtils.toString(map.get("COM_ADDRESS"));

			bw.write(temp + "\r\n");
		}
		
		bw.flush();
		bw.close();

		// 20200122 Betty 產生檢核檔(SameBRH) ZWMS_SCHGAO.yyyymmdd 搭配 WM_CHGAO_SBRH.yyyymmdd 配合新版檔案加解密，調整檢核檔格式
		String fileNamez1 = tempPath + "reports\\ZWMS_SCHGAO." + sdf.format(new Date());
		BufferedWriter bwz1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNamez1), "UTF-8"));
		String contenStr = "";
		contenStr += LenString("WMS_SCHGAO", 10);
		contenStr += LenString("WM_CHGAO_SBRH." + sdf.format(new Date()), 40);
		contenStr += LenString("PUT", 10);
		contenStr += LenString("WM_CHGAO_SBRH." + sdf.format(new Date()), 40);
		bwz1.write(contenStr + "\r\n");
		bwz1.flush();
		bwz1.close();

		contenStr = "";

		// 將aListDiffBRH轉成txt輸出到  WM_INFO_CUST_CHGAO_DIFF_BRH.YYYYMMDD
		String fileName2 = tempPath + "reports\\WM_INFO_CUST_CHGAO_DIFF_BRH" + sdf.format(new Date()) + ".NTF";
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName2), "UTF-8"));
		for (Map<String, Object> map : aListDiffBRH) {
			String temp = "";
			temp += ObjectUtils.toString(map.get("CUST_NAME")).replaceAll(",", "");
			temp += "," + ObjectUtils.toString(map.get("SIMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("EMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665");
			temp += "," + ObjectUtils.toString(map.get("NEW_AO_NAME"));
			temp += "," + ObjectUtils.toString(map.get("COM_ZIP_CODE"));
			temp += "," + ObjectUtils.toString(map.get("COM_ADDRESS"));

			bw2.write(temp + "\r\n");
		}
		
		bw2.flush();
		bw2.close();

		// 20200122 Betty 產生檢核檔(DiffBRH) ZWMS_DCHGAO.yyyymmdd 搭配 WM_CHGAO_DBRH.yyyymmdd 配合新版檔案加解密，調整檢核檔格式
		String fileNamez2 = tempPath + "reports\\ZWMS_DCHGAO." + sdf.format(new Date());
		BufferedWriter bwz2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNamez2), "UTF-8"));
		contenStr += LenString("WMS_DCHGAO", 10);
		contenStr += LenString("WM_CHGAO_DBRH." + sdf.format(new Date()), 40);
		contenStr += LenString("PUT", 10);
		contenStr += LenString("WM_CHGAO_DBRH." + sdf.format(new Date()), 40);
		bwz2.write(contenStr + "\r\n");
		bwz2.flush();
		bwz2.close();

		contenStr = "";

		// 產生檢核檔 MH_CUST_CHGAO_SAME_BRH_YYYYMMDD
		String fileName4 = tempPath + "reports\\MH_CUST_CHGAO_SAME_BRH_" + sdf.format(new Date());
		BufferedWriter bw4 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName4), "UTF-8"));

		for (Map<String, Object> map : aListMHSameBRH) {
			String temp = "";
			temp += "";
			temp += "," + ObjectUtils.toString(map.get("EMAIL"));
			temp += "," + ObjectUtils.toString(map.get("CUST_NAME")).replaceAll(",", "");
			temp += "," + ObjectUtils.toString(map.get("SIMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("EMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665");
			temp += "," + ObjectUtils.toString(map.get("NEW_AO_NAME"));
			temp += "," + ObjectUtils.toString(map.get("CUST_ID"));
			bw4.write(temp + "\r\n");
		}
		
		bw4.flush();
		bw4.close();

		String fileName5 = tempPath + "reports\\MH_CUST_CHGAO_DIFF_BRH_" + sdf.format(new Date());
		BufferedWriter bw5 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName5), "UTF-8"));

		for (Map<String, Object> map : aListMHDiffBRH) {
			String temp = "";
			temp += "";
			temp += "," + ObjectUtils.toString(map.get("EMAIL"));
			temp += "," + ObjectUtils.toString(map.get("CUST_NAME")).replaceAll(",", "");
			temp += "," + ObjectUtils.toString(map.get("SIMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("EMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665");
			temp += "," + ObjectUtils.toString(map.get("NEW_AO_NAME"));
			temp += "," + ObjectUtils.toString(map.get("CUST_ID"));
			bw5.write(temp + "\r\n");
		}
		
		bw5.flush();
		bw5.close();
	}
	

	/*
	 * 0728
	 * 客戶資料寫死條件：CUST_BRA_NBR
	 * (1)  客戶分行於715 (TBCRM_CUST_MAST.BRA_NBR = '715')
	 * (2)  分行理專為高端理專 (TBCRM_CUST_MAST.AO_CODE IN ('下方AOCODE'))
	 * 員工高端判斷條件： UHRM_AO_CODE
	 * (1)  員工在高端客群 (TBORG_MEMBER.DEPT_ID = '031')
	 * (2)  員工兼職理專 (TBORG_MEMBER_ROLE IN ('A145', 'A148', 'A221', 'A222', 'A223', 'A224', 'A237', 'A8A7', 'ABRE', 'ABRH'))
	 * 20220104_#0829_理專異動函高端資訊修正
	 * 不寫死NEW_AO_NAME
	 * 
	 * 2024.10.15 Sam Tu
	 * 根據主管 修改分行名稱為私銀XX區
	 */
	private void transferUHRMData(List<Map<String, Object>> afterPrepareList) throws DAOException, JBranchException {
		
		List<Map<String, Object>> tempAfterPrepareList = new ArrayList();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		
		queryCondition.setQueryString("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'BTCRM3502_UHRM_TRANSFER_DATA' ORDER BY PARAM_ORDER");
		
		List<Map> list = dam.exeQuery(queryCondition);
		
		for (Map<String, Object> data : afterPrepareList) {
//			if (StringUtils.isNotBlank(ObjectUtils.toString(data.get("UHRM_AO_CODE"))) && 
//				StringUtils.equals("715", ObjectUtils.toString(data.get("CUST_BRA_NBR")))) {
//				HashMap<String, Object> newMap = new HashMap<>();
//				newMap.putAll(data);
//				newMap.put("SIMP_NAME", list.get(0).get("PARAM_NAME").toString());
//				newMap.put("EMP_NAME", list.get(1).get("PARAM_NAME").toString());
//				//newMap.put("NEW_AO_NAME", list.get(2).get("PARAM_NAME").toString());
//				newMap.put("BRH_TEL", list.get(3).get("PARAM_NAME").toString());
//				tempAfterPrepareList.add(newMap);
//			} else {
				tempAfterPrepareList.add(data);
//			}
		}
		
		afterPrepareList.clear();
		afterPrepareList.addAll(tempAfterPrepareList);
	}

	public void sampleAOChangeVerification(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 抓取上週AO有變更紀錄的客戶
		// mantis 0004091: WMS-TDS-BTCRM-3502-客戶交接後續寄送控管郵件計算批次 sen 2018/03/05 (改)
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.addAll(getLastWeekMailDatas(true));
		list.addAll(getLastWeekMailDatas(false));

		// check
		ArrayList<Map<String, Object>> aListSameBRH = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> aListDiffBRH = new ArrayList<Map<String, Object>>();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();

		// 做資料排除動作 
		// CASE 1 排除 客戶郵寄地址或分行聯絡資訊沒資料
		// CASE 2 排除 客戶移來移去的作法
		// 同一個客戶就取最新根最舊的兩筆
		// old ao code = new ao code 不寄
		// old ao code != new ao code 
		// 如果相同 EMP_ID 代表理專人同一個主 code 兼 code 互換 不寄
		// 其他 要寄
		List<Map<String, Object>> afterPrepareList = new ArrayList<Map<String, Object>>();
		Map<String, List<Map<String, Object>>> groupCustList = new HashMap<String, List<Map<String, Object>>>();

		doGroupData(list, groupCustList);
		doDataPrepare(afterPrepareList, groupCustList);

		for (int i = 0; i < afterPrepareList.size(); i++) {
			// P、O、S或是無等級客戶以Email通知
			if ("P".equals(ObjectUtils.toString(afterPrepareList.get(i).get("CON_DEGREE"))) || 
				"O".equals(ObjectUtils.toString(afterPrepareList.get(i).get("CON_DEGREE"))) || 
				"S".equals(ObjectUtils.toString(afterPrepareList.get(i).get("CON_DEGREE"))) || 
				StringUtils.isEmpty(ObjectUtils.toString(afterPrepareList.get(i).get("CON_DEGREE")))) {
				// 同分行
				if (ObjectUtils.toString(afterPrepareList.get(i).get("ORG_AO_BRH")).equals(ObjectUtils.toString(afterPrepareList.get(i).get("NEW_AO_BRH")))) {
					if (afterPrepareList.get(i).get("EMAIL") != null) {
						if (aListSameBRH.size() > 0 && aListDiffBRH.size() > 0)
							break;
						aListSameBRH.add(afterPrepareList.get(i));
					}
				}
				// 不同分行
				else {
					if (afterPrepareList.get(i).get("EMAIL") != null) {
						if (aListSameBRH.size() > 0 && aListDiffBRH.size() > 0)
							break;
						aListDiffBRH.add(afterPrepareList.get(i));
					}
				}
			}
		}

		// PARAM_NAME 之後改code 卡方便
		queryCondition = dam.getQueryCondition();
		queryCondition.setQueryString("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'CRM.BTCRM3502_SAMPLE_MAIL_RECEIVER'");
		List<Map<String, Object>> xml_list = dam.exeQuery(queryCondition);
		
		if (xml_list.size() == 0)
			throw new Exception("XML參數沒設定");
		
		String xml_mail = ObjectUtils.toString(xml_list.get(0).get("PARAM_NAME"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		// 同分行
		String fileName = tempPath + "reports\\MH_CUST_CHGAO_SAME_BRH_" + sdf.format(new Date());
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		for (Map<String, Object> map : aListSameBRH) {
			String temp = "";
			temp += "";
			temp += "," + xml_mail;
			temp += "," + ObjectUtils.toString(map.get("CUST_NAME")).replaceAll(",", "");
			temp += "," + ObjectUtils.toString(map.get("SIMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("EMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665");
			temp += "," + ObjectUtils.toString(map.get("NEW_AO_NAME"));
			temp += "," + ObjectUtils.toString(map.get("CUST_ID"));
			bw.write(temp + "\r\n");
		}
		
		bw.flush();
		bw.close();
		
		// 不同分行
		String fileName2 = tempPath + "reports\\MH_CUST_CHGAO_DIFF_BRH_" + sdf.format(new Date());
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName2), "UTF-8"));
		for (Map<String, Object> map : aListDiffBRH) {
			String temp = "";
			temp += "";
			temp += "," + xml_mail;
			temp += "," + ObjectUtils.toString(map.get("CUST_NAME")).replaceAll(",", "");
			temp += "," + ObjectUtils.toString(map.get("SIMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("EMP_NAME"));
			temp += "," + ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665");
			temp += "," + ObjectUtils.toString(map.get("NEW_AO_NAME"));
			temp += "," + ObjectUtils.toString(map.get("CUST_ID"));
			bw2.write(temp + "\r\n");
		}
		
		bw2.flush();
		bw2.close();
	}

	private void sendMail1(Map<String, Object> map, String htmlStr) throws Exception {
		
		FubonSendJavaMail sendMail = new FubonSendJavaMail();
		
		Map<String, String> mailMap = new HashMap<String, String>();
		mailMap.put(FubonSendJavaMail.MAIL, ObjectUtils.toString(map.get("EMAIL")));
		
		List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
		mailList.add(mailMap);
		
		FubonMail mail = new FubonMail();
		mail.setLstMailTo(mailList);
		
		//設定信件主旨
		mail.setSubject("同行客戶異動通知信");
		
		//設定信件內容
		htmlStr = htmlStr.replace("[CUST_NAME]", ObjectUtils.toString(map.get("CUST_NAME")));
		htmlStr = htmlStr.replace("[NEW_AO_NAME]", ObjectUtils.toString(map.get("NEW_AO_NAME")));
		htmlStr = htmlStr.replace("[SIMP_NAME]", ObjectUtils.toString(map.get("SIMP_NAME")));
		htmlStr = htmlStr.replace("[EMP_NAME]", ObjectUtils.toString(map.get("EMP_NAME")));
		htmlStr = htmlStr.replace("[BRH_TEL]", ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665"));
		
		mail.setContent(htmlStr);
		
		//寄出信件-無附件
		Map<String, Object> annexData = new HashMap<String, Object>();
		sendMail.sendMail(mail, annexData);
	}

	private void sendMail2(Map<String, Object> map, String htmlStr) throws Exception {
		
		FubonSendJavaMail sendMail = new FubonSendJavaMail();
		
		Map<String, String> mailMap = new HashMap<String, String>();
		mailMap.put(FubonSendJavaMail.MAIL, ObjectUtils.toString(map.get("EMAIL")));
		
		List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
		mailList.add(mailMap);
		
		FubonMail mail = new FubonMail();
		mail.setLstMailTo(mailList);
		
		//設定信件主旨
		mail.setSubject("分流客戶異動通知信");
		
		//設定信件內容
		htmlStr = htmlStr.replace("[CUST_NAME]", ObjectUtils.toString(map.get("CUST_NAME")));
		htmlStr = htmlStr.replace("[NEW_AO_NAME]", ObjectUtils.toString(map.get("NEW_AO_NAME")));
		htmlStr = htmlStr.replace("[SIMP_NAME]", ObjectUtils.toString(map.get("SIMP_NAME")));
		htmlStr = htmlStr.replace("[EMP_NAME]", ObjectUtils.toString(map.get("EMP_NAME")));
		htmlStr = htmlStr.replace("[BRH_TEL]", ObjectUtils.toString(map.get("BRH_TEL"), "02-87516665"));
		mail.setContent(htmlStr);
		
		//寄出信件-無附件
		Map<String, Object> annexData = new HashMap<String, Object>();
		sendMail.sendMail(mail, annexData);
	}

	// mantis 0004091: WMS-TDS-BTCRM-3502-客戶交接後續寄送控管郵件計算批次 sen 2018/03/05 ===== ↓ ↓ ↓ =====
	private List<Map<String, Object>> getLastWeekMailDatas(boolean hasName) throws DAOException, JBranchException {
		
		// 抓取上週AO有變更紀錄的客戶
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		
		queryCondition.setQueryString(hasName ? hasNameSql() : hasNotNameSql());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list != null && list.size() > 0 ? list : new ArrayList<Map<String, Object>>();
	}

	// 可以撈出有分行主管的
	private String hasNameSql() {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * ");
		sql.append("FROM ( ");
		sql.append("  SELECT  L.LETGO_DATETIME, ");
		sql.append("          IVW.EMP_ID NEW_AO_EMP_ID, ");
		sql.append("          OLDIVW.EMP_ID OLD_AO_EMP_ID, ");
		sql.append("          L.CUST_ID, ");
		sql.append("          L.ORG_AO_CODE, ");
		sql.append("          L.NEW_AO_CODE, ");
		sql.append("          L.ORG_AO_BRH, ");
		sql.append("          L.NEW_AO_BRH, ");
		sql.append("          IVW.EMP_NAME AS NEW_AO_NAME, ");
		sql.append("          L.APL_REASON, ");
		sql.append("          REPLACE(C.CUST_NAME, '''') AS CUST_NAME, ");
		sql.append("          C.VIP_DEGREE, ");
		sql.append("          C.CON_DEGREE, ");
		sql.append("          REPLACE(CT.EMAIL,',','.' ) AS EMAIL, ");
		sql.append("          CT.COM_ZIP_CODE, ");
		sql.append("          CT.COM_ADDRESS, ");
		sql.append("          TD.DEPT_ID AS BRA_NBR, ");
		sql.append("          CASE WHEN UHRM_BOSS.BRANCH_AREA_NAME IS NOT NULL THEN REPLACE(UHRM_BOSS.BRANCH_AREA_NAME, '台北富邦') ELSE REPLACE(TD.DEPT_NAME, '台北富邦') END AS SIMP_NAME, ");
		sql.append("          BR.ZIP_COD, ");
		sql.append("          BR.CHIN_ADDR, ");
		sql.append("          BR.TEL_NO_MAIN AS BRH_TEL, ");
		sql.append("          CASE WHEN UHRM_BOSS.EMP_NAME IS NOT NULL THEN UHRM_BOSS.EMP_NAME ELSE BASE.EMP_NAME END AS EMP_NAME, ");
		sql.append("          UHRM.AO_CODE AS UHRM_AO_CODE, ");
		sql.append("          C.BRA_NBR AS CUST_BRA_NBR, ");
		sql.append("          IVW.TYPE AS NEW_AO_TYPE, ");
		sql.append("          OLDIVW.TYPE AS OLD_AO_TYPE ");
		sql.append("  FROM TBCRM_CUST_AOCODE_CHGLOG L ");
		sql.append("  LEFT JOIN TBCRM_CUST_CONTACT_FOR3502 CT ON L.CUST_ID = CT.CUST_ID ");
		sql.append("  LEFT JOIN TBCRM_CUST_MAST C ON L.CUST_ID = C.CUST_ID ");
		sql.append("  LEFT JOIN TBCRM_CUST_NOTE NOTE ON L.CUST_ID = NOTE.CUST_ID ");
		sql.append("  LEFT JOIN TBORG_BRH_CONTACT BR ON L.NEW_AO_BRH = BR.BRH_COD ");
		sql.append("  LEFT JOIN TBORG_DEFN TD ON TD.DEPT_ID = L.NEW_AO_BRH ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT DISTINCT EMP_ID, EMP_NAME, JOB_TITLE_NAME, ROLE_ID, ROLE_NAME, BRANCH_NBR, BRANCH_NAME ");
		sql.append("    FROM ( ");
		sql.append("      SELECT EMP_ID, EMP_NAME, JOB_TITLE_NAME, ROLE_ID, ROLE_NAME, BRANCH_NBR, BRANCH_NAME ");
		sql.append("      FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sql.append("      WHERE ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '011') ");
		sql.append("      UNION ");
		sql.append("      SELECT EMP_ID, EMP_NAME, JOB_TITLE_NAME, ROLE_ID, ROLE_NAME, BRANCH_NBR, BRANCH_NAME ");
		sql.append("      FROM VWORG_EMP_PLURALISM_INFO ");
		sql.append("      WHERE ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '011') ");
		sql.append("    ) ");
		sql.append("  ) BASE ON L.NEW_AO_BRH = BASE.BRANCH_NBR ");
		sql.append("  LEFT JOIN VWORG_AO_INFO IVW ON L.NEW_AO_CODE = IVW.AO_CODE "); // -- 新理專
		sql.append("  LEFT JOIN VWORG_AO_INFO OLDIVW ON L.ORG_AO_CODE = OLDIVW.AO_CODE "); // -- 原理專
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT MEM.EMP_ID, AO.AO_CODE, DEPT_ID, ROLE.ROLE_ID ");
		sql.append("    FROM TBORG_MEMBER MEM ");
		sql.append("    INNER JOIN TBORG_UHRM_BRH UH ON MEM.EMP_ID = UH.EMP_ID ");
		sql.append("    LEFT JOIN TBORG_SALES_AOCODE AO ON MEM.EMP_ID = AO.EMP_ID ");
		sql.append("    LEFT JOIN TBORG_MEMBER_ROLE ROLE ON AO.EMP_ID = ROLE.EMP_ID ");
		sql.append("    WHERE 1 = 1 ");
		sql.append("  ) UHRM ON UHRM.AO_CODE = L.NEW_AO_CODE "); // --判定高端
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT DISTINCT EMP_ID, EMP_NAME, DEPT_ID, BRANCH_AREA_NAME ");
		sql.append("    FROM VWORG_EMP_UHRM_INFO ");
		sql.append("    WHERE PRIVILEGEID = 'UHRM012' ");
		sql.append("  ) UHRM_BOSS ON UHRM.DEPT_ID = UHRM_BOSS.DEPT_ID "); // --高端主管
		sql.append("  WHERE 1 = 1 ");
		sql.append("  AND (BASE.EMP_NAME IS NOT NULL OR UHRM_BOSS.EMP_NAME IS NULL) ");
		sql.append("  AND ((CHG_DATE IS NULL AND TRUNC(LETGO_DATETIME) BETWEEN TRUNC(CURRENT_DATE, 'D')-7 AND TRUNC (CURRENT_DATE, 'D')-1) OR (TRUNC(CHG_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D')-7 AND TRUNC (CURRENT_DATE, 'D')-1)) ");
		sql.append("  AND NOTE.DEATH_YN <> 'Y' "); // --排除死亡戶
		sql.append("  ORDER BY L.LETGO_DATETIME ASC ");
		sql.append(") ");
		
		return sql.toString();
	}

	// 無法撈出有分行主管的 (加工撈取)
	private String hasNotNameSql() {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * ");
		sql.append("FROM ( ");
		sql.append("  SELECT M.*, VW.EMP_NAME ");
		sql.append("  FROM ( ");
		sql.append("    SELECT  L.LETGO_DATETIME, ");
		sql.append("            IVW.EMP_ID AS NEW_AO_EMP_ID, ");
		sql.append("            OLDIVW.EMP_ID AS OLD_AO_EMP_ID, ");
		sql.append("            L.CUST_ID, ");
		sql.append("            L.ORG_AO_CODE, ");
		sql.append("            L.NEW_AO_CODE, ");
		sql.append("            L.ORG_AO_BRH, ");
		sql.append("            L.NEW_AO_BRH, ");
		sql.append("            IVW.EMP_NAME AS NEW_AO_NAME, ");
		sql.append("            L.APL_REASON, ");
		sql.append("            REPLACE(C.CUST_NAME, '''') AS CUST_NAME, ");
		sql.append("            TD.PARENT_DEPT_ID, ");
		sql.append("            C.VIP_DEGREE, ");
		sql.append("            C.CON_DEGREE, ");
		sql.append("            REPLACE(CT.EMAIL,',','.' ) AS EMAIL, ");
		sql.append("            CT.COM_ZIP_CODE, ");
		sql.append("            CT.COM_ADDRESS, ");
		sql.append("            TD.DEPT_ID AS BRA_NBR, ");
		sql.append("            REPLACE(TD.DEPT_NAME, '台北富邦') AS SIMP_NAME, ");
		sql.append("            BR.ZIP_COD, BR.CHIN_ADDR, ");
		sql.append("            BR.TEL_NO_MAIN AS BRH_TEL, ");
		sql.append("            UHRM.AO_CODE AS UHRM_AO_CODE, ");
		sql.append("            C.BRA_NBR AS CUST_BRA_NBR, ");
		sql.append("            IVW.TYPE AS NEW_AO_TYPE ");
		sql.append("    FROM TBCRM_CUST_AOCODE_CHGLOG L ");
		sql.append("    LEFT JOIN TBCRM_CUST_CONTACT_FOR3502 CT ON L.CUST_ID = CT.CUST_ID ");
		sql.append("    LEFT JOIN TBCRM_CUST_MAST C ON L.CUST_ID=C.CUST_ID ");
		sql.append("    LEFT JOIN TBCRM_CUST_NOTE NOTE ON L.CUST_ID=NOTE.CUST_ID ");
		sql.append("    LEFT JOIN TBORG_BRH_CONTACT BR ON L.NEW_AO_BRH = BR.BRH_COD ");
		sql.append("    LEFT JOIN TBORG_DEFN TD ON TD.DEPT_ID = L.NEW_AO_BRH ");
		sql.append("    LEFT JOIN ( ");
		sql.append("      SELECT DISTINCT EMP_ID, EMP_NAME, JOB_TITLE_NAME, ROLE_ID, ROLE_NAME, BRANCH_NBR, BRANCH_NAME ");
		sql.append("      FROM ( ");
		sql.append("        SELECT EMP_ID, EMP_NAME, JOB_TITLE_NAME ,ROLE_ID, ROLE_NAME, BRANCH_NBR, BRANCH_NAME ");
		sql.append("        FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sql.append("        WHERE ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '011') ");
		sql.append("        UNION ");
		sql.append("        SELECT EMP_ID, EMP_NAME, JOB_TITLE_NAME, ROLE_ID, ROLE_NAME, BRANCH_NBR, BRANCH_NAME ");
		sql.append("        FROM VWORG_EMP_PLURALISM_INFO ");
		sql.append("        WHERE ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '011') ");
		sql.append("      ) ");
		sql.append("    ) BASE ON L.NEW_AO_BRH = BASE.BRANCH_NBR ");
		sql.append("    LEFT JOIN VWORG_AO_INFO IVW ON L.NEW_AO_CODE = IVW.AO_CODE "); // --新理專
		sql.append("    LEFT JOIN VWORG_AO_INFO OLDIVW ON L.ORG_AO_CODE = OLDIVW.AO_CODE "); // --原理專
		sql.append("    LEFT JOIN ( ");
		sql.append("      SELECT MEM.EMP_ID, AO.AO_CODE, DEPT_ID, ROLE.ROLE_ID ");
		sql.append("      FROM TBORG_MEMBER MEM ");
		sql.append("      INNER JOIN TBORG_UHRM_BRH UH ON MEM.EMP_ID = UH.EMP_ID ");
		sql.append("      LEFT JOIN TBORG_SALES_AOCODE AO ON MEM.EMP_ID = AO.EMP_ID ");
		sql.append("      LEFT JOIN TBORG_MEMBER_ROLE ROLE ON AO.EMP_ID = ROLE.EMP_ID ");
		sql.append("      WHERE 1 = 1 ");
		sql.append("    ) UHRM ON UHRM.AO_CODE = L.NEW_AO_CODE "); // --判定高端
		sql.append("    LEFT JOIN ( ");
		sql.append("      SELECT DISTINCT EMP_ID, EMP_NAME, DEPT_ID ");
		sql.append("      FROM VWORG_EMP_UHRM_INFO ");
		sql.append("      WHERE PRIVILEGEID = 'UHRM012' ");
		sql.append("    ) UHRM_BOSS ON UHRM.DEPT_ID = UHRM_BOSS.DEPT_ID "); // --高端主管
		sql.append("    WHERE 1 = 1 ");
		sql.append("    AND BASE.EMP_NAME IS NULL ");
		sql.append("    AND UHRM_BOSS.EMP_NAME IS NULL ");
		sql.append("    AND ((CHG_DATE IS NULL AND TRUNC(LETGO_DATETIME) BETWEEN TRUNC(CURRENT_DATE, 'D')-7 AND TRUNC (CURRENT_DATE, 'D')-1) OR ( TRUNC(CHG_DATE) BETWEEN TRUNC(CURRENT_DATE, 'D')-7 AND TRUNC (CURRENT_DATE, 'D')-1)) ");
		sql.append("    AND NOTE.DEATH_YN <> 'Y' "); // --排除死亡戶
		sql.append("    ORDER BY L.LETGO_DATETIME ASC ");
		sql.append("  ) M ");
		sql.append("  LEFT JOIN WMSUSER.VWORG_BRANCH_EMP_DETAIL_INFO VW ON M.PARENT_DEPT_ID = VW.BRANCH_AREA_ID AND VW.BRANCH_NBR IS NULL ");
		sql.append(") ");
				
		return sql.toString();
	}

	/** 將資料戶回來的第一手資料做分組，透過客戶 ID 來分，再透過一個客戶最多只能有兩筆資料操過兩筆 只取最新與最舊的，最後再去看誰不是移來移去的 **/
	private void doGroupData(List<Map<String, Object>> sourceList, Map<String, List<Map<String, Object>>> groupMap) {
		
		for (Map<String, Object> sourceMap : sourceList) {
			String custId = ObjectUtils.toString(sourceMap.get("CUST_ID"));
			if (groupMap.containsKey(custId)) {
				List<Map<String, Object>> currentList = groupMap.get(custId);
				if (currentList.size() == 2) {
					List<Map<String, Object>> tempCurrentList = new ArrayList<Map<String, Object>>();
					Date[] currentDate = new Date[3];
					currentList.add(sourceMap);
					for (int i = 0; i < 3; i++) {
						tempCurrentList.add(currentList.get(i));
						currentDate[i] = (Date) currentList.get(i).get("LETGO_DATETIME");
					}

					int getMinIndex = 0, getMaxIndex = 0;
					getMinIndex = currentDate[0].before(currentDate[1]) ? 0 : 1;
					getMinIndex = currentDate[getMinIndex].before(currentDate[2]) ? getMinIndex : 2;

					getMaxIndex = currentDate[0].after(currentDate[1]) ? 0 : 1;
					getMaxIndex = currentDate[getMinIndex].after(currentDate[2]) ? getMinIndex : 2;

					currentList.clear();
					currentList.add(tempCurrentList.get(getMinIndex));
					currentList.add(tempCurrentList.get(getMaxIndex));

				} else {
					currentList.add(sourceMap);
				}
				// 判斷 大小 currentList 最多兩筆
			} else {
				List<Map<String, Object>> currentList = new ArrayList<Map<String, Object>>();
				currentList.add(sourceMap);
				groupMap.put(custId, currentList);
			}
		}

	}

	/** 產生最後要能寄出的 清單 **/
	private void doDataPrepare(List<Map<String, Object>> afterPrepareList, Map<String, List<Map<String, Object>>> groupMap) throws DAOException, JBranchException {
		
		int onlyCount = 0;
		
		for (Entry<String, List<Map<String, Object>>> entry : groupMap.entrySet()) {
			if (entry.getValue().size() == 1) {
				if (checkMap(entry.getValue().get(0), "BRA_NBR") && needPrintMap(entry.getValue().get(0))) {
					afterPrepareList.add(entry.getValue().get(0));
					onlyCount += 1;
				}
			} else {
				Map<String, Object> needPrintMap = needPrintMap(entry.getValue());
				if (MapUtils.isNotEmpty(needPrintMap)) {
					if (checkMap(needPrintMap, "BRA_NBR")) {
						//						System.out.println(needPrintMap.get("CUST_ID"));
						afterPrepareList.add(needPrintMap);
					}
				}
			}
		}
	}

	/** 找出同一筆中是否重複 EMP_ID **/
	private boolean needPrintMap(Map<String, Object> compareMap) throws DAOException, JBranchException {
		
		String oldAoCode = ObjectUtils.toString(compareMap.get("ORG_AO_CODE"));
		String newAoCode = ObjectUtils.toString(compareMap.get("NEW_AO_CODE"));
		String oldAoCodeEmp = ObjectUtils.toString(compareMap.get("OLD_AO_EMP_ID"));
		String newAoCodeEmp = ObjectUtils.toString(compareMap.get("NEW_AO_EMP_ID"));
		String oldAoType = ObjectUtils.toString(compareMap.get("OLD_AO_TYPE"));
		String newAoType = ObjectUtils.toString(compareMap.get("NEW_AO_TYPE"));
		String newAoName = ObjectUtils.toString(compareMap.get("NEW_AO_NAME"));
		
		if(StringUtils.isBlank(newAoCode) || StringUtils.isBlank(newAoName) ) {
			return false;
		}

		if (StringUtils.isEmpty(oldAoCode)) {
			if(checkAOType(oldAoType,newAoType,"notSameName")) {
				return true;
			} else {
				return false;
			}		
		} else {
			if (newAoCodeEmp.equals(oldAoCodeEmp)) {
				// 同的理專(人) 只是主 code 兼 code 互換 不用寄
				// 同理專但從維護code變主、兼code要寄
				if(checkAOType(oldAoType,newAoType,"sameName")) {
					return true;
				} else {
					return false;
				}
			} else {
				if(checkAOType(oldAoType,newAoType,"notSameName")) {
					return true;
				} else {
					return false;
				}
			}
		}
	}




	/** 找出兩筆中最後要能寄的 Map **/
	private Map<String, Object> needPrintMap(List<Map<String, Object>> compareList) throws DAOException, JBranchException {
		
		Map<String, Object> oldMap = compareList.get(0);
		Map<String, Object> newMap = compareList.get(1);
		String oldAoCode = ObjectUtils.toString(oldMap.get("ORG_AO_CODE"));
		String newAoCode = ObjectUtils.toString(newMap.get("NEW_AO_CODE"));
		String oldAoCodeEmp = ObjectUtils.toString(oldMap.get("OLD_AO_EMP_ID"));
		String newAoCodeEmp = ObjectUtils.toString(newMap.get("NEW_AO_EMP_ID"));
		String oldAoType = ObjectUtils.toString(oldMap.get("OLD_AO_TYPE"));
		String newAoType = ObjectUtils.toString(newMap.get("NEW_AO_TYPE"));
		String newAoName = ObjectUtils.toString(newMap.get("NEW_AO_NAME"));
		
		if(StringUtils.isBlank(newAoCode) || StringUtils.isBlank(newAoName) ) {
			return null;
		}
		

		if (StringUtils.isEmpty(oldAoCode)) {
			if(checkAOType(oldAoType,newAoType,"notSameName")) {
				return newMap;
			} else {
				return null;
			}		
		} else {
			if (newAoCode.equals(oldAoCode)) {
				// A => B, B => A 不寄
				return null;
			} else {
				if (newAoCodeEmp.equals(oldAoCodeEmp)) {
					// 同的理專(人) 只是主 code 兼 code 互換 不用寄
					// 同理專但從維護code變主、兼code要寄
					if(checkAOType(oldAoType,newAoType,"sameName")) {
						return newMap;
					} else {
						return null;
					}				
				} else {
					if(checkAOType(oldAoType,newAoType,"notSameName")) {
						return newMap;
					} else {
						return null;
					}	
				}
			}
		}
	}

	/** 排除客戶郵寄地址或分行聯絡資訊沒資料 **/
	private boolean checkMap(Map<String, Object> currentMap, String key) {
		
		if (currentMap.get(key) != null) {
			return true;
		}
		
		return false;
	}

	// 2017/4/17
	private String LenString(String inputStr, int StrLen) {
		
		String returnString = "";
		int LenCtrl = 0;
		int inputStrLen;
		int i = 0;

		if (StringUtils.isBlank(inputStr)) {
			inputStrLen = 0;
			inputStr = "";
		} else
			inputStrLen = inputStr.length();
		
		while (inputStrLen > i) {
			//判斷該字元是否為雙位元資料
			if (Integer.parseInt(Integer.toString(inputStr.charAt(i), 16), 16) >= 128) {
				if ((StrLen - LenCtrl - 1) >= 2) {
					returnString = returnString + inputStr.substring(i, i + 1);
					LenCtrl = LenCtrl + 2;
				}
			} else {
				if ((StrLen - LenCtrl) >= 1) {
					returnString = returnString + inputStr.substring(i, i + 1);
					LenCtrl++;
				}
			}
			
			i++;
		}

		// 現有字串以後補空白
		while (LenCtrl < StrLen) {
			returnString = returnString + " ";
			LenCtrl++;
		}
		
		// 表示該字串中含有雙位元資料
		if (returnString.length() < StrLen)
			StrLen = returnString.length();
		
		return returnString.substring(0, StrLen);
	}
	
	
	
	private boolean checkAOType(String oldAoType, String newAoType, String method) {
		
		switch(method) {
		  case "sameName" :
			  if(StringUtils.equals("3", oldAoType) && !StringUtils.equals("3", newAoType)) {
					return true;
				} else {
					return false;
				}	
		  case "notSameName" :
			  if(StringUtils.equals("3", newAoType)) {
					return false;
				} else {
					return true;
				}	
		  default:
				return false;		
		}
		
	}
}