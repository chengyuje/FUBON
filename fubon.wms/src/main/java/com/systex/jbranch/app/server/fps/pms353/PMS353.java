package com.systex.jbranch.app.server.fps.pms353;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.Undefined;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BRVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_BR_NAMEVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_PRDPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_IPO_PARAM_PRDVO;
import com.systex.jbranch.app.server.fps.pms205.PMS205detailInputVO;
import com.systex.jbranch.app.server.fps.pms305.PMS305OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :IPO戰報參數設定<br>
 * Comments Name : PMS353java<br>
 * Author : Kevin<br>
 * Date :2016/08/02 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms353")
@Scope("request")
public class PMS353 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS353.class);
	private static String finalstring;

	/** 新增CSV檔 表頭、固定欄位、變動欄位資料 **/
	private void insertCSVFile(PMS353InputVO inputVO, Timestamp currentTM)
			throws Exception {
		dam = this.getDataAccessManager();

		Path path = Paths.get(new File((String) SysInfo
				.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO
				.getFileName()).toString());
		List<String> lines = FileUtils.readLines(new File(path.toString()),
				"big5");

		int x = 3;
		int checkLength=0;
		// ==第一行為表頭欄位==
		for (int i = 0; i < lines.size(); i++) {
			liesCheck(lines.get(i).toString());	
			String str_line=finalstring;
			String[] str = str_line.split(",");
			try {
				
				if (i == 0) {
					
					for (int j = 0; j < str.length; j++) {
						// 設定表頭表格
						TBPMS_IPO_PARAM_BR_NAMEPK HDPK = new TBPMS_IPO_PARAM_BR_NAMEPK();
						TBPMS_IPO_PARAM_BR_NAMEVO HD = new TBPMS_IPO_PARAM_BR_NAMEVO();
						HDPK.setPRJ_SEQ(new BigDecimal(inputVO.getPRJ_SEQ()));
						// 第幾個欄位
						HDPK.setCOL_NO(BigDecimal.valueOf(j));
						// 設定目標
						HD.setTARGET(str[j]);
						// 設定PK
						HD.setcomp_id(HDPK);
						// ==其他必備資訊==
						HD.setCreatetime(currentTM);
						// 創建者
						HD.setCreator((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						// 修改者
						HD.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						// 最後修改者
						HD.setLastupdate(currentTM);
						dam.create(HD);
						checkLength=str.length;
					}
				} else {
					for (int j = 0; j < str.length; j++) {
						if (isNumeric(str[j]) == 1) {
							x += 0;
						} else {
							x = j + 1;
						}
						;
					}
					// ==其餘行為資料內容==
					// 固定欄位資料
					// 動態欄位
					for (int k = 1; k < checkLength; k++) {
						// 設定內容表格
						TBPMS_IPO_PARAM_BRPK DTLPK = new TBPMS_IPO_PARAM_BRPK();
						TBPMS_IPO_PARAM_BRVO DTL = new TBPMS_IPO_PARAM_BRVO();
						// 分行
						DTLPK.setBRANCH_NBR(str[0]);
						// 第幾欄位
						DTLPK.setCOL_NO(BigDecimal.valueOf(k));
						DTLPK.setPRJ_SEQ(new BigDecimal(inputVO.getPRJ_SEQ()));
						// PKEY
						DTL.setcomp_id(DTLPK);
						// 目標
						if(k<str.length){
							DTL.setTARGET(new BigDecimal(str[k].equals("") ? "0"
									: str[k]));
						}else{
							DTL.setTARGET(new BigDecimal("0"));
						}
						
						// 建立時間
						DTL.setCreatetime(currentTM);
						// 建立者
						DTL.setCreator((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						// 修改者
						DTL.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						// 最後修改者
						DTL.setLastupdate(currentTM);
						dam.create(DTL);
					}

				}
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s",
						StringUtil.getStackTraceAsString(e)));
				throw new APException("第" + (i + 1) + "筆檔案的第" + x
						+ "欄位資料有誤請確認一下喔!");
			}
		}
		this.sendRtnObject(null);

	}

	/** queryRPTCol資料 **/
	public void queryRPTCol(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS353InputVO inputVO = (PMS353InputVO) body;
		PMS353OutputVO return_VO = new PMS353OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		QueryConditionIF queryCondition1 = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		// ==表頭查詢==
		StringBuffer sql = new StringBuffer();
		// ==表頭內容查詢==
		StringBuffer sql1 = new StringBuffer();
		sql.append("SELECT TARGET from TBPMS_IPO_PARAM_BR_NAME WHERE 1=1 "); // 標頭
		sql1.append(" SELECT distinct BRANCH_NBR  FROM TBPMS_IPO_PARAM_BR  where 1=1  ");
		// ==查詢條件==
		if (!StringUtils.isBlank(inputVO.getPRJ_SEQ())) {
			sql.append(" and PRJ_SEQ=:PRJ_SEQQ ");
			sql1.append(" and PRJ_SEQ=:PRJ_SEQQ ");
		}
		// 表頭查詢字串
		queryCondition.setQueryString(sql.toString());
		// 表頭內容查詢字串
		queryCondition1.setQueryString(sql1.toString());

		// ==查詢條件設定==
		if (!StringUtils.isBlank(inputVO.getPRJ_SEQ())) {
			queryCondition.setObject("PRJ_SEQQ", inputVO.getPRJ_SEQ());
			queryCondition1.setObject("PRJ_SEQQ", inputVO.getPRJ_SEQ());
		}
		ResultIF bran = dam.executePaging(queryCondition1,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String, Object>> heade = dam.exeQuery(queryCondition);

		// ==result結果區==
		int totalPage_i = bran.getTotalPage();
		int totalRecord_i = bran.getTotalRecord();
		return_VO.setResultList(bran); // 表頭內容
		return_VO.setHeader(heade); // 表頭欄位資訊
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	// 檢視分行資訊
	// 顯示動態欄位例如:20億目標,30億目標,分行第一列選項
	public void getDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS353InputVO inputVO = (PMS353InputVO) body;
		PMS353OutputVO return_VO = new PMS353OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		QueryConditionIF queryCondition1 = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		// QueryConditionIF queryCondition2 = dam
		// .getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		// StringBuffer sql2 = new StringBuffer();

		sql.append("SELECT TARGET from TBPMS_IPO_PARAM_BR_NAME WHERE 1=1 "); // 標頭
		sql1.append(" SELECT distinct A.BRANCH_NBR, TBPMS_IPO_RPT.BRANCH_NAME FROM TBPMS_IPO_PARAM_BR A LEFT JOIN TBPMS_IPO_RPT  on  A.BRANCH_NBR = TBPMS_IPO_RPT.BRANCH_NBR where 1=1  ");
		// sql2.append(" SELECT distinct BRANCH_NAME  FROM TBPMS_IPO_RPT  where 1=1  ");

		if (!StringUtils.isBlank(inputVO.getPRJ_SEQ())) {
			sql.append(" and PRJ_SEQ=:PRJ_SEQQ ");
			sql1.append(" and A.PRJ_SEQ=:PRJ_SEQQ ");
			// sql2.append(" and PRJ_SEQ=:PRJ_SEQQ ");

		}
//		sql.append(" order ");
		sql1.append(" order by  BRANCH_NBR ");

		queryCondition.setQueryString(sql.toString());
		queryCondition1.setQueryString(sql1.toString());
		// queryCondition2.setQueryString(sql1.toString());

		if (!StringUtils.isBlank(inputVO.getPRJ_SEQ())) {
			queryCondition.setObject("PRJ_SEQQ", inputVO.getPRJ_SEQ());
			queryCondition1.setObject("PRJ_SEQQ", inputVO.getPRJ_SEQ());
			// queryCondition2.setObject("PRJ_SEQQ", inputVO.getPRJ_SEQ());
		}
		ResultIF bran = dam.executePaging(queryCondition1,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String, Object>> heade = dam.exeQuery(queryCondition);

		// 設定OutputVO
		int totalPage_i = bran.getTotalPage();
		int totalRecord_i = bran.getTotalRecord();
		return_VO.setResultList(bran);
		return_VO.setHeader(heade);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	public void queryRPTCol2(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS353InputVO inputVO = (PMS353InputVO) body;
		PMS353OutputVO return_VO = new PMS353OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// ==彈跳視窗查詢==
		sql.append("SELECT TARGET from TBPMS_IPO_PARAM_BR WHERE 1=1 "); // 標頭
		// ==查詢條件==
		if (!StringUtils.isBlank(inputVO.getPRJ_SEQ())) {
			sql.append(" and PRJ_SEQ=:PRJ_SEQQ ");
		}
		// 分行
		if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
			sql.append(" and BRANCH_NBR=:BRANCH_NBRR ");
		}

		queryCondition.setQueryString(sql.toString());
		// ==設定查詢條件==
		if (!StringUtils.isBlank(inputVO.getPRJ_SEQ())) {
			queryCondition.setObject("PRJ_SEQQ", inputVO.getPRJ_SEQ());
		}
		// 分行
		if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
			queryCondition.setObject("BRANCH_NBRR", inputVO.getBRANCH_NBR());
		}

		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);

		// result

		return_VO.setCollist(resultList);
		this.sendRtnObject(return_VO);
	}

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {

		PMS353InputVO inputVO = (PMS353InputVO) body;
		PMS353OutputVO outputVO = new PMS353OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// ==主頁面查詢==
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ROWNUM AS NUM,A.*  FROM  (SELECT b.*,(SELECT MAX(PRJ_SEQ) AS MAXJ FROM TBPMS_IPO_PARAM_MAST) AS MAXA FROM TBPMS_IPO_PARAM_MAST b ) A ");
		sb.append("  WHERE 1=1   ");
		queryCondition.setQueryString(sb.toString());
		// ==主頁面分頁==
		ResultIF list = dam.executePaging(queryCondition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String, Object>> list_tmp = (List<Map<String, Object>>) list;
		// 分頁處理tempList
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		// ==以下為處理查詢==
		for (Map<String, Object> map : list_tmp) {
			QueryConditionIF Condition2 = dam
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT * FROM TBPMS_IPO_PARAM_PRD WHERE PRJ_SEQ=:PRJ_SEQQ");

			Condition2.setQueryString(sb.toString());
			// 設定查詢條件
			Condition2.setObject("PRJ_SEQQ", map.get("PRJ_SEQ").toString());
			// 查詢結果
			List<Map<String, Object>> list2 = dam.exeQuery(Condition2);
			// 處理後重新放入List
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("NUM", map.get("NUM"));
			tempMap.put("PRJ_SEQ", map.get("PRJ_SEQ"));
			tempMap.put("PRJ_NAME", map.get("PRJ_NAME"));
			tempMap.put("START_DT", map.get("START_DT"));
			tempMap.put("END_DT", map.get("END_DT"));
			tempMap.put("BT_DT", map.get("BT_DT"));
			tempMap.put("VERSION", map.get("VERSION"));
			tempMap.put("CREATETIME", map.get("CREATETIME"));
			tempMap.put("CREATOR", map.get("CREATOR"));
			tempMap.put("MODIFIER", map.get("MODIFIER"));
			tempMap.put("LASTUPDATE", map.get("LASTUPDATE"));
			tempMap.put("MAXA", map.get("MAXA")); // 求出最大的SEQ
			StringBuffer STR = new StringBuffer();
			for (Map<String, Object> map2 : list2) {
				// 增加','
				STR.append((String) map2.get("PRD_ID")).append(",");
			}
			// 刪除最後一位','
			if (STR.length() != 0)
				STR.deleteCharAt(STR.length() - 1);
			String PRJID = STR.toString();
			// 放入tempMap map
			tempMap.put("PRJID", PRJID);
			// 最後新增一筆row
			tempList.add(tempMap);
		}

		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setResultList(tempList); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(outputVO);
	}

	
	
	//基金
	public void queryINS(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS205detailInputVO inputVO=(PMS205detailInputVO)body;
	    DataAccessManager dam=this.getDataAccessManager();
	    QueryConditionIF condition=dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    StringBuffer sql=new StringBuffer();
		String[] getINS_ID = inputVO.getINS_ID().split(",");

	    try {
	    	
//	    	if(inputVO.isCheckgi().equals("1")){
	    		sql.append("SELECT ROWNUM AS NUM,T.* FROM(");
				sql.append("SELECT distinct  PRD_ID,FUND_ENAME, FUND_CNAME FROM TBPRD_FUND WHERE 1=1");
				
				if(getINS_ID.length>1){
				if(StringUtils.isNotBlank(inputVO.getINS_ID()))
				{
					sql.append(" and (PRD_ID LIKE '%"+getINS_ID[0]+"%' ");
				}
				if(StringUtils.isNotBlank(inputVO.getINS_ID()))
				{
					for(int i = 1;i<getINS_ID.length;i++){
						sql.append(" or PRD_ID LIKE '%"+getINS_ID[i]+"%' ");
//						condition.setObject("INS_IDD","%"+getINS_ID[i] +"%");
					}
				}
					sql.append(" ) ");
				} else {
					if(StringUtils.isNotBlank(inputVO.getINS_ID()))
					{
						sql.append(" and PRD_ID LIKE :INS_IDD");
					}
					if(StringUtils.isNotBlank(inputVO.getINS_ID()))
					{
						condition.setObject("INS_IDD","%"+inputVO.getINS_ID() +"%");
					}

				}
//	    	}
				sql.append(" ORDER BY  PRD_ID)T");
				condition.setQueryString(sql.toString());

			
			
			List<Map<String,Object>> list=dam.exeQuery(condition);
			PMS305OutputVO outputVO = new PMS305OutputVO();
			outputVO.setResultList(list);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	    
	
	  }

	
	
	/** ======= 新增資料 ======== */
	public void addRPT(Object body, IPrimitiveMap header) throws Exception {
		PMS353InputVO inputVO = (PMS353InputVO) body;
		dam = this.getDataAccessManager();
		// ==以下無新增==
		// 現在時間
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		// 取得起日時間
		Timestamp sDATE = Timestamp.valueOf(new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss").format(inputVO.getsDate()));
		// 取得訖日時間
		Timestamp eDATE = Timestamp.valueOf(new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss").format(inputVO.geteDate()));
		// 取得pms353專用序號
		BigDecimal sequ = new BigDecimal(getSN());
		inputVO.setPRJ_SEQ(sequ.toString());
		TBPMS_IPO_PARAM_MASTVO vo = new TBPMS_IPO_PARAM_MASTVO();
		vo.setBT_DT(currentTM);
		vo.setEND_DT(eDATE);
		vo.setSTART_DT(sDATE);
		vo.setPRJ_NAME(inputVO.getPRJ_NAME().toString());
		vo.setPRJ_SEQ(sequ);
		// 創建時間
		vo.setCreatetime(currentTM);
		// 建立者
		vo.setCreator((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		// 修改者
		vo.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		// 修改時間
		vo.setLastupdate(currentTM);
		dam.create(vo);

		String[] str = inputVO.getPRD_ID().split(",");
		for (int i = 0; i < str.length; i++) {
			TBPMS_IPO_PARAM_PRDPK ppk = new TBPMS_IPO_PARAM_PRDPK();
			TBPMS_IPO_PARAM_PRDVO voo = new TBPMS_IPO_PARAM_PRDVO();
			ppk.setPRD_ID(str[i]);
			ppk.setPRJ_SEQ(new BigDecimal(inputVO.getPRJ_SEQ()));
			voo.setcomp_id(ppk);
			// 建立時間
			voo.setCreatetime(currentTM);
			// 創建者
			voo.setCreator((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			// 修改者
			voo.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			// 最後修改時間
			voo.setLastupdate(currentTM);
			dam.create(voo);
		}

		insertCSVFile(inputVO, currentTM);
		this.sendRtnObject(null);
	}

	/** ======= 修改資料 ======== */
	public void updateRPT(Object body, IPrimitiveMap header) throws Exception {
		PMS353InputVO inputVO = (PMS353InputVO) body;
		dam = this.getDataAccessManager();
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		// 取得起日 時間
		Timestamp sDATE = Timestamp.valueOf(new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss").format(inputVO.getsDate()));
		// 取得訖日 時間
		Timestamp eDATE = Timestamp.valueOf(new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss").format(inputVO.geteDate()));
		// 現在時間
		Timestamp bDATE = new Timestamp(System.currentTimeMillis());
		// ==找尋修改資料==
		TBPMS_IPO_PARAM_MASTVO VO = (TBPMS_IPO_PARAM_MASTVO) dam.findByPKey(
				TBPMS_IPO_PARAM_MASTVO.TABLE_UID,
				new BigDecimal(inputVO.getPRJ_SEQ()));
		// 如果找到資料
		if (VO != null) {
			VO.setBT_DT(bDATE);
			VO.setEND_DT(eDATE);
			VO.setPRJ_NAME(inputVO.getPRJ_NAME().toString());
			VO.setPRJ_SEQ(new BigDecimal(inputVO.getPRJ_SEQ().toString()));
			VO.setSTART_DT(sDATE);
			// 取得可修改 標頭 內容
			if (inputVO.getUploadFlag().equals("Y")) {
				delHeadData(inputVO, header);
				delDtlData(inputVO, header);
				insertCSVFile(inputVO, currentTM);
			}
			// 修改者
			VO.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			// 修改時間
			VO.setLastupdate(currentTM);
			delPrid(inputVO, header);
			String[] str = inputVO.getPRD_ID().split(",");
			for (int i = 0; i < str.length; i++) {
				TBPMS_IPO_PARAM_PRDPK ppk = new TBPMS_IPO_PARAM_PRDPK();
				ppk.setPRD_ID(str[i]);
				ppk.setPRJ_SEQ(new BigDecimal(inputVO.getPRJ_SEQ()));
				TBPMS_IPO_PARAM_PRDVO voou = new TBPMS_IPO_PARAM_PRDVO();
				// 設定PK
				voou.setcomp_id(ppk);
				// 創建時間
				voou.setCreatetime(currentTM);
				// 創建者
				voou.setCreator((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				// 修改者
				voou.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				// 最後跟新時間
				voou.setLastupdate(currentTM);
				dam.create(voou);

			}

			dam.update(VO);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}
		this.sendRtnObject(null);
	}

	/** ====== 刪除主檔資料 ======= */
	/**/
	public void delData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS353InputVO inputVO = (PMS353InputVO) body;
		dam = this.getDataAccessManager();
		// ==刪除主檔==
		TBPMS_IPO_PARAM_MASTVO vo = (TBPMS_IPO_PARAM_MASTVO) dam.findByPKey(
				TBPMS_IPO_PARAM_MASTVO.TABLE_UID,
				new BigDecimal(inputVO.getPRJ_SEQ()));
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_017");
		}
		delPrid(inputVO, header);
		delHeadData(inputVO, header);
		delDtlData(inputVO, header);

		this.sendRtnObject(null);
	}

	/** ====== 刪除表頭資料 ======= */
	public void delHeadData(PMS353InputVO inputVO, IPrimitiveMap header)
			throws JBranchException {
		dam = this.getDataAccessManager();
		// ==刪除表頭SQL==
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition
				.setQueryString("delete TBPMS_IPO_PARAM_BR_NAME where PRJ_SEQ = :seq");
		condition.setObject("seq", inputVO.getPRJ_SEQ());
		dam.exeUpdate(condition);

		this.sendRtnObject(null);
	}

	/** ====== 刪除PRID資料 ======= */
	public void delPrid(PMS353InputVO inputVO, IPrimitiveMap header)
			throws JBranchException {
		dam = this.getDataAccessManager();
		// ==刪除SQL==
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition
				.setQueryString("delete TBPMS_IPO_PARAM_PRD where PRJ_SEQ = :seq");
		condition.setObject("seq", inputVO.getPRJ_SEQ());
		dam.exeUpdate(condition);
		this.sendRtnObject(null);
	}

	/** ====== 刪除明細-固定欄位資料 ======= */
	public void delDtlData(PMS353InputVO inputVO, IPrimitiveMap header)
			throws JBranchException {
		dam = this.getDataAccessManager();
		// ==刪除SQL==
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition
				.setQueryString("delete TBPMS_IPO_PARAM_BR where PRJ_SEQ = :seq");
		condition.setObject("seq", inputVO.getPRJ_SEQ());
		dam.exeUpdate(condition);
		this.sendRtnObject(null);
	}

	public int isNumeric(String str) {
		try {
			if (str.equals(""))
				str = "0";
			Double.parseDouble(str);
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}
	
	/** 找字串"\""功能 **/
	public String liesCheck(String lines)
			throws JBranchException {
		
		if(lines.indexOf("\"")!=-1){
			//抓出第一個"
			int acc=lines.indexOf("\"");
			//原本所有字串
			StringBuffer lines1 = new StringBuffer(lines.toString());
			//切掉前面的字串
			String ori=lines.substring(0,acc);
			//
			String oriline =ori.toString();
			
			String fist=lines1.substring(acc);
			fist=fist.substring(1,fist.indexOf("\"",1));
			String[] firstsub=fist.split(",");
			for(int i=0;i<firstsub.length;i++){
				oriline+=firstsub[i].toString();
			}
			String okfirst=ori+"\""+fist+"\"";   //前面組好的
			String oriline2 = lines.substring(acc);
			
			oriline2=oriline2.replaceFirst("\""+fist+"\"", "");
			String fistTwo=lines1.substring(0,lines1.indexOf("\""));
			lines=oriline+oriline2;

		}
		if(lines.indexOf("\"")!=-1){
			liesCheck(lines);
		}else{
			finalstring=lines;
		}
		return lines;
			
	}

	/** 產生seq No PMS353專用 */
	private String getSN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("PMS353");
		}

		catch (Exception e) {
			sn.createNewSerial("PMS353", "0000000000", null, null, null, 6,
					new Long("99999999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("PMS353");
		}
		return seqNum;
	}

}
