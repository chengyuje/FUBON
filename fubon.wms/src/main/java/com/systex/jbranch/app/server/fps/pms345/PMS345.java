package com.systex.jbranch.app.server.fps.pms345;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.security.impl.JBranchCryptology;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :新契約照會日報<br>
 * Comments Name : PMS345.java<br>
 * Author : Kevin<br>
 * Date :2016/06/29 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms345")
@Scope("request")
public class PMS345 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS345.class);

	/**
	 * 匯出確認NULL
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if (key == "POLICY_NO") {
				return String.valueOf(map.get(key)) + "-"
						+ String.valueOf(map.get("SEQ")) + "-"
						+ String.valueOf(map.get("ID_DUP"));
			}
			return (String.valueOf(map.get(key)) == "null") ? "0" : String
					.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 匯出
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		PMS345OutputVO return_VO = (PMS345OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "新契約照會日報" + sdf.format(new Date()) + "-"
						+ getUserVariable(FubonSystemVariableConsts.LOGINID)
						+ ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[20];
					int i = 0;
					records[i] = checkIsNull(map, "NOTE_DATE"); // 照會日期
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNull(map, "AO_CODE"); // AO Code
					records[++i] = checkIsNull(map, "EMP_ID"); // 理專員編
					records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
					records[++i] = checkIsNull(map, "ATTRACT_ID"); // 招攬人ID
					records[++i] = checkIsNull(map, "POLICY_NO"); // 保單號碼
					records[++i] = checkIsNull(map, "PRD_NAME"); // 商品名稱
					records[++i] = checkIsNull(map, "PROPOSER_ID"); // 要保人ID
					records[++i] = checkIsNull(map, "PROPOSER_NAME"); // 要保人姓名
					records[++i] = checkIsNull(map, "NOTE_TYPE"); // 照會類別
					records[++i] = checkIsNull(map, "REPLY_DATE"); // 照會回覆截止日
					records[++i] = checkIsNull(map, "NOTE_URL"); // 照會內容
					records[++i] = checkIsNull(map, "SUMMARY"); // 訪談摘要
					records[++i] = checkIsNull(map, "STATUS"); // 處理狀態
					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[20];
				int j = 0;
				csvHeader[j] = "照會日期";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO Code";
				csvHeader[++j] = "理專員編";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "招攬人ID";
				csvHeader[++j] = "保單號碼";
				csvHeader[++j] = "商品名稱";
				csvHeader[++j] = "要保人ID";
				csvHeader[++j] = "要保人姓名";
				csvHeader[++j] = "照會類別";
				csvHeader[++j] = "照會回覆截止日";
				csvHeader[++j] = "照會內容";
				csvHeader[++j] = "訪談摘要";
				csvHeader[++j] = "處理狀態";
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else
				return_VO.setResultList(list);
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {

		PMS345InputVO inputVO = (PMS345InputVO) body;
		PMS345OutputVO outputVO = new PMS345OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			Date d = new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSS");
			String str = sdf.format(d);
			String Timer = str.substring(0,16);
			//String Timer = "2016073120250501";
			String skey = "";
			
			// ==主查詢==
			sql.append("SELECT ROWNUM AS NUM,T.* FROM "
					+ "(select A.*,(A.POLICY_NO ||'-'|| TRIM(TO_CHAR(A.SEQ ,'0')) || CASE WHEN A.ID_DUP <> ' ' THEN '-' || A.ID_DUP END ) AS POLICY_NO_2,A.CREATETIME AS CDATE "
					+ "FROM TBPMS_DAILY_CONTRACT A WHERE C_TYPE='1'    ");

			// ==主查詢條件==
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRegion())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getOp())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
			}
			// 分行
			if (!StringUtils.isBlank(inputVO.getBranch())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
			}
			// 契約序號
			if (!StringUtils.isBlank(inputVO.getNum())) {
				sql.append(" and POLICY_NO LIKE :POLICY_NOO");
			}
			if (!StringUtils.isBlank(inputVO.getClas())) {
				sql.append(" and NOTE_TYPE LIKE :NOTE_TYPEE");
			}
			// 員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and AO_CODE LIKE :EMP_IDEE");
			}
			// 狀態
			if (!StringUtils.isBlank(inputVO.getType())) {
				sql.append(" and STATUS LIKE :STATUSS");
			}
			if (!StringUtils.isBlank(inputVO.getId())) {
				sql.append(" and PROPOSER_ID LIKE :PROPOSER_IDD");
			}
			if (inputVO.getsCreDate() != null) {
				sql.append("   and NOTE_DATE >= :crestart ");
			}
			if (inputVO.geteCreDate() != null) {
				sql.append("   and NOTE_DATE <= :creend ");
			}
			if (inputVO.getsCreDate2() != null) {
				sql.append("   and REPLY_DATE >= :crestart2 ");
			}
			if (inputVO.geteCreDate2() != null) {
				sql.append("    and REPLY_DATE <= :creend2 ");
			}

			/*** 以下是序號-保單序號-ID ***/
			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
				sql.append(" and POLICY_NO LIKE :POLICY_NOO");
			}
			if (!StringUtils.isBlank(inputVO.getNum())) {
				sql.append(" and SEQ LIKE :SEQQ");
			}
			if (!StringUtils.isBlank(inputVO.getID_DUP())) {
				sql.append(" and ID_DUP LIKE :ID_DUPP");
			}
			/***** END *******/
			// 排序
			sql.append("   ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR,POLICY_NO,NOTE_DATE,POLICY_NO,REPLY_DATE ) T");
			condition.setQueryString(sql.toString());

			// ==主查詢條件設定==
			if (!StringUtils.isBlank(inputVO.getNum())) {
				condition.setObject("SEQQ", "%" + inputVO.getNum() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getID_DUP())) {
				condition.setObject("ID_DUPP", "%" + inputVO.getID_DUP() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
				condition.setObject("POLICY_NOO", "%" + inputVO.getPOLICY_NO()
						+ "%");
			}
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRegion())) {
				condition.setObject("REGION_CENTER_IDDD",
						"%" + inputVO.getRegion() + "%");
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getOp())) {
				condition
						.setObject("OP_AREA_IDDD", "%" + inputVO.getOp() + "%");
			}
			// 分行
			if (!StringUtils.isBlank(inputVO.getBranch())) {
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch()
						+ "%");
			}
			// 類別
			if (!StringUtils.isBlank(inputVO.getClas())) {
				condition.setObject("NOTE_TYPEE", "%" + inputVO.getClas() + "%");
			}
			// 狀態
			if (!StringUtils.isBlank(inputVO.getType())) {
				condition.setObject("STATUSS", "%" + inputVO.getType() + "%");
			}
			if (!StringUtils.isBlank(inputVO.getId())) {
				condition.setObject("PROPOSER_IDD", "%" + inputVO.getId() + "%");
			}
			if (inputVO.getsCreDate() != null) {
				condition.setObject("crestart", new Timestamp(inputVO
						.getsCreDate().getTime()));
			}

			if (inputVO.geteCreDate() != null) {
				condition.setObject("creend", new Timestamp(inputVO
						.geteCreDate().getTime()));
			}

			if (inputVO.getsCreDate2() != null) {
				condition.setObject("crestart2", new Timestamp(inputVO
						.getsCreDate2().getTime()));
			}

			if (inputVO.geteCreDate2() != null) {
				condition.setObject("creend2", new Timestamp(inputVO
						.geteCreDate2().getTime()));
			}

			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				condition.setObject("EMP_IDEE", "%" + inputVO.getAo_code() + "%");
			}
			
			// 分頁查詢結果
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			// CSV 查詢結果
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			
			//取得加解密key值
			QueryConditionIF condition2 = dam
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql2 = new StringBuffer();
			sql2.append(" select PARAM_NAME_EDIT from TBSYSPARAMETER ");
			sql2.append(" WHERE PARAM_CODE = 'DES_ENCRY' ");
			condition2.setQueryString(sql2.toString());
			
			List<Map<String, Object>> pwList = dam.exeQuery(condition2);
			if(CollectionUtils.isNotEmpty(pwList)){
				for(Map<String, Object> resultMap : pwList){
					skey = (String)resultMap.get("PARAM_NAME_EDIT");
				}
			}
			
			//加解密url
			if(CollectionUtils.isNotEmpty(csvList)){
				for(Map<String, Object> reaultMap : csvList){
					String caseNo = (String)reaultMap.get("CASE_NO");
					String url = (String)reaultMap.get("NOTE_URL");
					//String caseNo = "1069044781";
					//String code = "6BF5CB787FD66EE641AEA6A60914C3711AE8D4FE4963142D49ACC70F850B3D58DD69BEE750928EB3100F1AAB9888043D9FAA338BA78563589DA3FE52E79430CE";
					
					if(StringUtils.isNotBlank(url) && StringUtils.isNotBlank(caseNo)){
						String parameter = "timer="+Timer+"&partner_code=209&authorkey="+caseNo;
						System.err.println(url+"&PA="+parameter+" DES key="+skey);
						reaultMap.put("NOTE_URL" ,url+"&PA="+ JBranchCryptology.encode(parameter , skey));
						//reaultMap.put("NOTE_URL" , url+"&"+JBranchCryptology.decode(code , skey));
					}
				}
			}
			
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data 分頁結果
			outputVO.setCsvList(csvList); // csv
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

}