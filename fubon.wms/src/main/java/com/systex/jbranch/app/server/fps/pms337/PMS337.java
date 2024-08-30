package com.systex.jbranch.app.server.fps.pms337;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_HO_MGMTPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_HO_MGMTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :保險月底提存及月中核實報表Controller <br>
 * Comments Name : PMS337.java<br>
 * Author :Kevin<br>
 * Date :2016年10月15日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
@Component("pms337")
@Scope("request")
public class PMS337 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS337.class);

	/**
	 * 查詢資料 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS337InputVO inputVO = (PMS337InputVO) body;
		PMS337OutputVO outputVO = new PMS337OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//==主查詢==
		StringBuffer sql = new StringBuffer();
//		sql.append(" SELECT PRT.ACC_NO,PRT.POLICY_NO,PRT.SEQ,PRT.CODE,PRT.PRD_NAME,    ");
//		sql.append(" PRT.YEARMON,PRT.REGION_CENTER_ID,PRT.REGION_CENTER_NAME,PRT.BRANCH_AREA_ID,PRT.BRANCH_AREA_NAME,PRT.BRANCH_NBR,PRT.BRANCH_NAME,    ");
//		sql.append(" PRT.PRD_TYPE,PRT.PAY_YEAR,PRT.PAY_TYPE,PRT.INS_AGE,PRT.STAFF_CODE,   ");
//		sql.append(" PRT.MASTER_RIDER,PRT.POLICY_YEAR,PRT.POLICY_NOP,AO.AO_CODE,    ");
//		sql.append(" PRT.SRC_NO,PRT.CONTRACT_BEGIN,PRT.INS_AMT,PRT.ACT_INS_FEE,    ");
//		sql.append(" PRT.ACT_CMSN,PRT.PRJ_BONUS,PRT.IND_BONUS,PRT.SUMMIT_BONUS,    ");
//		sql.append(" PRT.BONUS_YN,PRT.HP_BONUS,PRJ_CODE,PRT.AGRG_CODE,         ");
//		sql.append(" PRT.DLVR_UNIT,PRT.STAFF_YN,O.EMP_NAME    ");
//		sql.append(" FROM TBPMS_ESCROW_VRFCTN_RPT PRT    ");
//		sql.append(" LEFT JOIN TBPMS_EMPLOYEE_REC_N O    ");
//		sql.append(" ON O.CUST_ID=PRT.STAFF_CODE    ");
//		sql.append(" LEFT JOIN TBPMS_SALES_AOCODE_REC AO    ");
//		sql.append(" ON O.EMP_ID=AO.EMP_ID AND AO.TYPE='1'  ");
//		sql.append(" WHERE 1=1    ");
		sql.append(" SELECT PRT.ACC_NO,PRT.POLICY_NO,PRT.SEQ,PRT.CODE,PRT.PRD_NAME, ");
		sql.append(" PRT.YEARMON,PRT.REGION_CENTER_ID,PRT.REGION_CENTER_NAME,PRT.BRANCH_AREA_ID,PRT.BRANCH_AREA_NAME,PRT.BRANCH_NBR,PRT.BRANCH_NAME, ");
		sql.append(" PRT.PAY_YEAR,PRT.PAY_TYPE,PRT.INS_AGE,PRT.STAFF_CODE, ");
		sql.append(" PRT.MASTER_RIDER,PRT.POLICY_YEAR,PRT.POLICY_NOP, ");
		sql.append(" PRT.SRC_NO,PRT.CONTRACT_BEGIN,PRT.INS_AMT,PRT.ACT_INS_FEE, ");
		sql.append(" PRT.ACT_CMSN,PRT.PRJ_BONUS,PRT.IND_BONUS,PRT.SUMMIT_BONUS, ");
		sql.append(" PRT.BONUS_YN,PRT.HP_BONUS,PRJ_CODE,PRT.AGRG_CODE, ");
		sql.append(" PRT.DLVR_UNIT,PRT.STAFF_YN ,PRT.BONUS,AO.EMP_NAME , AO.AO_CODE, ");
		sql.append(" INS.INS_TYPE ");
		sql.append(" FROM TBPMS_ESCROW_VRFCTN_RPT PRT ");
		sql.append(" LEFT JOIN TBPRD_INSINFO INS ");
    	sql.append("   ON PRT.CODE = INS.PRD_ID ");
		sql.append(" LEFT JOIN (SELECT EMP_NAME , AO_CODE , CUST_ID FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE AO_CODE IS NOT NULL) AO ");
		sql.append("   ON PRT.STAFF_CODE = AO.CUST_ID ");
		sql.append(" WHERE 1 = 1 ");
		//==主查詢條件
		
		if(StringUtils.isNotBlank(inputVO.getPreviewType()))
		{
			if("2".equals(inputVO.getPreviewType())) {
				sql.append(" and PRT.BONUS_YN  = 'A'    ");  // 未結案
			} else{
				sql.append(" and PRT.BONUS_YN  <> 'A'   ");  // 已結案
			}
		}
		
		// 報表月份
		if (!StringUtils.isBlank(inputVO.getsCreDate())) {
			sql.append(" and PRT.YEARMON LIKE :yrmn    ");
		}
		// 區域中心
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append(" and PRT.REGION_CENTER_ID LIKE :rcid    ");
		}
		// 營運區
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append(" and PRT.BRANCH_AREA_ID LIKE :opid    ");
		}
		// 分行
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql.append(" and PRT.BRANCH_NBR LIKE :brid    ");
		}
		
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sql.append(" and AO.AO_CODE LIKE :aocode    ");
		}
		//排序
		sql.append(" order by PRT.ACC_NO,PRT.POLICY_NO,PRT.SEQ,PRT.CODE     ");
		condition.setQueryString(sql.toString());

//		if(StringUtils.isNotBlank(inputVO.getPreviewType())){
//			
//			if("2".equals(inputVO.getPreviewType())){
//				condition.setObject("yn","%A%" );
//			}else{
//				condition.setObject("yn","%A%");
////				condition.setObject("yn1","%N%");
//
//			}
//		}
		if (!StringUtils.isBlank(inputVO.getsCreDate())) {
			condition.setObject("yrmn", "%"+inputVO.getsCreDate()+"%");
		}
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			condition.setObject("rcid", "%"+inputVO.getRegion_center_id()+"%");
		}
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			condition.setObject("opid", "%"+inputVO.getBranch_area_id()+"%");
		}
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			condition.setObject("brid", "%"+inputVO.getBranch_nbr()+"%");
		}
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			condition.setObject("aocode", "%"+inputVO.getAo_code()+"%");
		}
		//分頁查詢
		ResultIF list = dam.executePaging(condition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		List<Map<String, Object>> csvList = dam.exeQuery(condition);
		int totalPage = list.getTotalPage();
		outputVO.setList(csvList);
		outputVO.setTotalPage(totalPage);
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		sendRtnObject(outputVO);
		
	}
	
	/**
	 * 匯出 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		PMS337OutputVO return_VO = (PMS337OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		
		String tempName = "";
		if ("1".equals(return_VO.getPreviewType())) {
			tempName = "_每月底提存-已結案佣金檔_";
		} else {
			tempName = "_每月底提存-未結案佣金檔_";
		}
		
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "保險月底提存報表"+ tempName + sdf.format(new Date())
						+ "_"+(String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// column
					String[] records = new String[34];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");// 資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");// 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");// 區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");// 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");// 營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR");// 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");// 分行名稱
					records[++i] = checkIsNull(map, "ACC_NO");// 受理編號
					records[++i] = checkIsNull(map, "POLICY_NO");// 保單號碼
					records[++i] = checkIsNull(map, "SEQ");// 序號
					records[++i] = checkIsNull(map, "CODE");// 代號
					records[++i] = checkIsNull(map, "PRD_NAME");// 商品名稱
					records[++i] = checkIsNull(map, "INS_TYPE");// 商品類型
					records[++i] = checkIsNull(map, "PAY_YEAR");// 繳費年期
					records[++i] = checkIsNull(map, "PAY_TYPE");// 繳別
					records[++i] = checkIsNull(map, "INS_AGE");// 投保年齡
					records[++i] = checkIsNull(map, "MASTER_RIDER");// 主/附約別
					records[++i] = checkIsNull(map, "POLICY_YEAR");// 保單年度
					records[++i] = checkIsNull(map, "POLICY_NOP");// 保單期數
					records[++i] = checkIsNull(map, "SRC_NO");// 來源編號
					records[++i] = checkIsNull(map, "CONTRACT_BEGIN");// 契約始期
					records[++i] = checkIsNull(map, "INS_AMT");// 保額
					records[++i] = checkIsNull(map, "ACT_INS_FEE");// 實繳保費
					records[++i] = checkIsNull(map, "ACT_CMSN");// 實發佣金
					records[++i] = checkIsNull(map, "PRJ_BONUS");// 商品專案獎勵金
					records[++i] = checkIsNull(map, "IND_BONUS");// 理專個獎
					records[++i] = checkIsNull(map, "SUMMIT_BONUS");// 海外高峰會
					records[++i] = checkIsNull(map, "BONUS");// 年終獎金
					records[++i] = checkIsNull(map, "HP_BONUS");// 高產能獎勵金-1P&2P
					records[++i] = checkIsNull(map, "PRJ_CODE");// 專案代碼
					records[++i] = checkIsNull(map, "AGRG_CODE");// 集彙代號
					records[++i] = checkIsNull(map, "DLVR_UNIT");// 送件單位
					records[++i] = checkIsNull(map, "STAFF_CODE");// 員工代號
					records[++i] = checkIsNull(map, "STAFF_YN");// 員工件

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[34];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "受理編號";
				csvHeader[++j] = "保單號碼";
				csvHeader[++j] = "序號";
				csvHeader[++j] = "代號";
				csvHeader[++j] = "商品名稱";
				csvHeader[++j] = "商品類型";
				csvHeader[++j] = "繳費年期";
				csvHeader[++j] = "繳別";
				csvHeader[++j] = "投保年齡";
				csvHeader[++j] = "主/附約別";
				csvHeader[++j] = "保單年度";
				csvHeader[++j] = "保單期數";
				csvHeader[++j] = "來源編號";
				csvHeader[++j] = "契約始期";
				csvHeader[++j] = "保額";
				csvHeader[++j] = "實繳保費";
				csvHeader[++j] = "實發佣金";
				csvHeader[++j] = "商品專案獎勵金";
				csvHeader[++j] = "理專個獎";
				csvHeader[++j] = "海外高峰會";
				csvHeader[++j] = "年終獎金";
				csvHeader[++j] = "高產能獎勵金-1P&2P";
				csvHeader[++j] = "專案代碼";
				csvHeader[++j] = "集彙代號";
				csvHeader[++j] = "送件單位";
				csvHeader[++j] = "員工代號";
				csvHeader[++j] = "員工件";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else

				this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/* ==== 【儲存】更新資料 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS337InputVO inputVO = (PMS337InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		String aAoCode, aAoCode2 = "";

		for (Map<String, Object> map : inputVO.getList()) {
			for (Map<String, Object> map2 : inputVO.getList2()) {
				aAoCode = map.get("A_AO_CODE") == null ? "" : map.get(
						"A_AO_CODE").toString();
				aAoCode2 = map2.get("A_AO_CODE") == null ? "" : map2.get(
						"A_AO_CODE").toString();
				if (map.get("YEARMON").equals(map2.get("YEARMON"))
						&& map.get("CUST_ID").equals(map2.get("CUST_ID"))
						&& (!aAoCode.equals(aAoCode2))) {
					TBPMS_HO_MGMTPK pk = new TBPMS_HO_MGMTPK();
					pk.setYEARMON(map.get("YEARMON").toString());
					pk.setCUST_ID(map.get("CUST_ID").toString());

					TBPMS_HO_MGMTVO paramVO = (TBPMS_HO_MGMTVO) dam.findByPKey(
							TBPMS_HO_MGMTVO.TABLE_UID, pk);
					paramVO.setA_AO_CODE(map.get("A_AO_CODE").toString());
					paramVO.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					paramVO.setLastupdate(stamp);
					dam.update(paramVO);
				}
			}
		}
		sendRtnObject(null);
	}

	/* ==== 【確認】移轉資料 ======== */
	public void confirm(Object body, IPrimitiveMap header)
			throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS337InputVO inputVO = (PMS337InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getList()) {
			TBPMS_HO_MGMTPK pk = new TBPMS_HO_MGMTPK();
			pk.setYEARMON(map.get("YEARMON").toString());
			pk.setCUST_ID(map.get("CUST_ID").toString());

			TBPMS_HO_MGMTVO paramVO = (TBPMS_HO_MGMTVO) dam.findByPKey(
					TBPMS_HO_MGMTVO.TABLE_UID, pk);
			paramVO.setT_FLAG("Y");
			paramVO.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			paramVO.setLastupdate(stamp);

			dam.update(paramVO);
		}
		sendRtnObject(null);
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))  && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}