package com.systex.jbranch.app.server.fps.prd241;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_STOCKVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_STOCK_REVIEWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd241
 * 
 * @author moron
 * @date 2016/08/24
 * @spec null
 */
@Component("prd241")
@Scope("request")
public class PRD241 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD241.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD241InputVO inputVO = (PRD241InputVO) body;
		PRD241OutputVO return_VO = new PRD241OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD241' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT SEQ,STOCK_CODE,PRD_ID,STOCK_CNAME,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,ACT_TYPE,REVIEW_STATUS,CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.SEQ,rw.STOCK_CODE,rw.PRD_ID,sto.STOCK_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR ");
			sql.append("FROM TBPRD_STOCK_REVIEW rw left join TBPRD_STOCK sto on rw.PRD_ID = sto.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			if(!StringUtils.equals("STOCKINFO", inputVO.getPassParams())) {
				sql.append("UNION ");
				sql.append("SELECT null as SEQ,stock.STOCK_CODE,stock.PRD_ID,stock.STOCK_CNAME,stock.CNR_YIELD,stock.CNR_MULTIPLE,stock.MULTIPLE_SDATE,stock.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
				sql.append("FROM TBPRD_STOCK stock ");
				sql.append("left join TBPRD_STOCK_REVIEW rw on rw.PRD_ID = stock.PRD_ID and rw.REVIEW_STATUS = 'W' ");
				sql.append("WHERE NVL(stock.REVIEW_STATUS, 'Y') = 'Y' ");
				sql.append("AND stock.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_STOCK_REVIEW WHERE REVIEW_STATUS = 'W') ");
			}
		}
		else {
			sql.append("SELECT rw.SEQ,rw.STOCK_CODE,rw.PRD_ID,sto.STOCK_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_STOCK_REVIEW rw left join TBPRD_STOCK sto on rw.PRD_ID = sto.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT null as SEQ,stock.STOCK_CODE,stock.PRD_ID,stock.STOCK_CNAME,stock.CNR_YIELD,stock.CNR_MULTIPLE,stock.MULTIPLE_SDATE,stock.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_STOCK stock ");
			sql.append("left join TBPRD_STOCK_REVIEW rw on rw.PRD_ID = stock.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(stock.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND stock.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_STOCK_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("AND PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void checkID(Object body, IPrimitiveMap header) throws JBranchException {
		PRD241InputVO inputVO = (PRD241InputVO) body;
		PRD241OutputVO return_VO = new PRD241OutputVO();
		dam = this.getDataAccessManager();
		
		// update
		if(StringUtils.equals("Y", inputVO.getStatus())) {
			// TBPRD_STOCK
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID,STOCK_CNAME FROM TBPRD_STOCK where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setStock_name(ObjectUtils.toString(list.get(0).get("STOCK_CNAME")));
				return_VO.setCanEdit(true);
			}
			else {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_026");
			}
			
			// TBPRD_STOCK_REVIEW
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRD_ID FROM TBPRD_STOCK_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if (list2.size() > 0) {
				return_VO.setCanEdit(false);
				return_VO.setErrorMsg("ehl_01_common_028");
			}
		}
		// sort
		else if(StringUtils.equals("S", inputVO.getStatus())) {
			// TBPRD_ETF
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_ID, STOCK_CNAME, RISKCATE_ID FROM TBPRD_STOCK where PRD_ID = :id ");
			queryCondition.setObject("id", inputVO.getPrd_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				return_VO.setStock_name(ObjectUtils.toString(list.get(0).get("STOCK_CNAME")));
				return_VO.setRick_id(ObjectUtils.toString(list.get(0).get("RISKCATE_ID")));
				return_VO.setCanEdit(true);
			}
			else
				return_VO.setCanEdit(false);
		}
		// add
		else {
			// no add
		}
		
		this.sendRtnObject(return_VO);
	}
	
	public void editData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD241InputVO inputVO = (PRD241InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_STOCK_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_STOCK_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_STOCK_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// add TBPRD_STOCK_REVIEW
		TBPRD_STOCK_REVIEWVO vo = new TBPRD_STOCK_REVIEWVO();
		vo.setSEQ(seqNo);
		vo.setPRD_ID(inputVO.getPrd_id());
		if(StringUtils.isNotBlank(inputVO.getYield()))
			vo.setCNR_YIELD(new BigDecimal(inputVO.getYield()));
		else
			vo.setCNR_YIELD(null);
		if(StringUtils.isNotBlank(inputVO.getCnr_mult()))
			vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getCnr_mult()));
		else
			vo.setCNR_MULTIPLE(null);
		if(inputVO.getMulti_sDate() != null)
			vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMulti_sDate().getTime()));
		else
			vo.setMULTIPLE_SDATE(null);
		if(inputVO.getMulti_eDate() != null)
			vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMulti_eDate().getTime()));
		else
			vo.setMULTIPLE_EDATE(null);
		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		dam.create(vo);
		this.sendRtnObject(null);
	}
	
	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException {
		PRD241InputVO inputVO = (PRD241InputVO) body;
		dam = this.getDataAccessManager();
		
		// check again
		// TBPRD_STOCK_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRD_ID FROM TBPRD_STOCK_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
		queryCondition.setObject("id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_common_028");
		
		// seq
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SQ_TBPRD_STOCK_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		// get ori
		TBPRD_STOCKVO vo = new TBPRD_STOCKVO();
		vo = (TBPRD_STOCKVO) dam.findByPKey(TBPRD_STOCKVO.TABLE_UID, inputVO.getPrd_id());
		if (vo != null) {
			// add TBPRD_STOCK_REVIEW
			TBPRD_STOCK_REVIEWVO rvo = new TBPRD_STOCK_REVIEWVO();
			rvo.setSEQ(seqNo);
			rvo.setPRD_ID(vo.getPRD_ID());
			rvo.setCNR_YIELD(vo.getCNR_YIELD());
			rvo.setCNR_MULTIPLE(vo.getCNR_MULTIPLE());
			rvo.setMULTIPLE_SDATE(vo.getMULTIPLE_SDATE());
			rvo.setMULTIPLE_EDATE(vo.getMULTIPLE_EDATE());
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
		PRD241InputVO inputVO = (PRD241InputVO) body;
		dam = this.getDataAccessManager();
		
		// 2017/2/21
		for(Map<String, Object> rmap : inputVO.getReview_list()) {
			TBPRD_STOCK_REVIEWVO rvo = new TBPRD_STOCK_REVIEWVO();
			rvo = (TBPRD_STOCK_REVIEWVO) dam.findByPKey(TBPRD_STOCK_REVIEWVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(rmap.get("SEQ"))));
			if (rvo != null) {
				// confirm
				if("Y".equals(inputVO.getStatus())) {
					// 新增
					if("A".equals(rvo.getACT_TYPE())) {
						// no add
					}
					// 修改
					else if("M".equals(rvo.getACT_TYPE())) {
						TBPRD_STOCKVO vo = new TBPRD_STOCKVO();
						vo = (TBPRD_STOCKVO) dam.findByPKey(TBPRD_STOCKVO.TABLE_UID, rvo.getPRD_ID());
						if (vo != null) {
							vo.setCNR_YIELD(rvo.getCNR_YIELD());
							vo.setCNR_MULTIPLE(rvo.getCNR_MULTIPLE());
							vo.setMULTIPLE_SDATE(rvo.getMULTIPLE_SDATE());
							vo.setMULTIPLE_EDATE(rvo.getMULTIPLE_EDATE());
							vo.setACT_TYPE("M");
							vo.setREVIEW_STATUS("Y");
							dam.update(vo);
						} else {
							// 顯示資料不存在
							throw new APException("ehl_01_common_001");
						}
					}
					// 刪除
					else if("D".equals(rvo.getACT_TYPE())) {
						TBPRD_STOCKVO vo = new TBPRD_STOCKVO();
						vo = (TBPRD_STOCKVO) dam.findByPKey(TBPRD_STOCKVO.TABLE_UID, rvo.getPRD_ID());
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
	
	public void download(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD241InputVO inputVO = (PRD241InputVO) body;
		PRD241OutputVO return_VO = new PRD241OutputVO();
		dam = this.getDataAccessManager();
		
		// getRoleList
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(ROLEID) AS COUNTS FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD241' AND FUNCTIONID = 'confirm') AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> privilegeCount = dam.exeQuery(queryCondition);
		//
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT STOCK_CODE,PRD_ID,STOCK_CNAME,CNR_YIELD,CNR_MULTIPLE,MULTIPLE_SDATE,MULTIPLE_EDATE,ACT_TYPE,REVIEW_STATUS,CREATOR FROM ( ");
		// 覆核人員
		if (((BigDecimal) privilegeCount.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) > 0) {
			sql.append("SELECT rw.STOCK_CODE,rw.PRD_ID,sto.STOCK_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,rw.CREATOR ");
			sql.append("FROM TBPRD_STOCK_REVIEW rw left join TBPRD_STOCK sto on rw.PRD_ID = sto.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("UNION ");
			sql.append("SELECT stock.STOCK_CODE,stock.PRD_ID,stock.STOCK_CNAME,stock.CNR_YIELD,stock.CNR_MULTIPLE,stock.MULTIPLE_SDATE,stock.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_STOCK stock ");
			sql.append("left join TBPRD_STOCK_REVIEW rw on rw.PRD_ID = stock.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(stock.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND stock.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_STOCK_REVIEW WHERE REVIEW_STATUS = 'W') ");
		}
		else {
			sql.append("SELECT rw.STOCK_CODE,rw.PRD_ID,sto.STOCK_CNAME,rw.CNR_YIELD,rw.CNR_MULTIPLE,rw.MULTIPLE_SDATE,rw.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_STOCK_REVIEW rw left join TBPRD_STOCK sto on rw.PRD_ID = sto.PRD_ID ");
			sql.append("WHERE rw.REVIEW_STATUS = 'W' ");
			sql.append("AND rw.CREATOR = :creator ");
			sql.append("UNION ");
			sql.append("SELECT stock.STOCK_CODE,stock.PRD_ID,stock.STOCK_CNAME,stock.CNR_YIELD,stock.CNR_MULTIPLE,stock.MULTIPLE_SDATE,stock.MULTIPLE_EDATE,rw.ACT_TYPE,rw.REVIEW_STATUS,null as CREATOR ");
			sql.append("FROM TBPRD_STOCK stock ");
			sql.append("left join TBPRD_STOCK_REVIEW rw on rw.PRD_ID = stock.PRD_ID and rw.REVIEW_STATUS = 'W' ");
			sql.append("WHERE NVL(stock.REVIEW_STATUS, 'Y') = 'Y' ");
			sql.append("AND stock.PRD_ID NOT IN (SELECT PRD_ID FROM TBPRD_STOCK_REVIEW WHERE REVIEW_STATUS = 'W' AND CREATOR = :creator) ");
			queryCondition.setObject("creator", ws.getUser().getUserID());
		}
		sql.append(") WHERE 1=1 ");
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("AND PRD_ID like :id ");
			queryCondition.setObject("id", "%" + inputVO.getPrd_id() + "%");
		}
		sql.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 WHEN REVIEW_STATUS = 'Y' THEN 1 ELSE 2 END ASC,PRD_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0) {
			// gen csv
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "股票清單_"+ sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				// 8 column
				String[] records = new String[8];
				int i = 0;
				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))))
					records[i] = "覆核中";
				else
					records[i] = "已覆核";
				records[++i] = checkIsNull(map, "PRD_ID");
				records[++i] = checkIsNull(map, "STOCK_CNAME");
				records[++i] = checkIsNull(map, "CNR_YIELD");
				records[++i] = checkIsNull(map, "CNR_MULTIPLE");
				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_SDATE") + "\"";
				records[++i] = "=\"" + checkIsNull(map, "MULTIPLE_EDATE") + "\"";
//				if ("W".equals(ObjectUtils.toString(map.get("REVIEW_STATUS"))) && Integer.valueOf(ObjectUtils.toString(map.get("SEQ"))) != 0 )
//					records[++i] = "A".equals(checkIsNull(map, "ACT_TYPE")) ? "新增" : "M".equals(checkIsNull(map, "ACT_TYPE")) ? "編輯" : "刪除";
//				else
//					records[++i] = "";
				listCSV.add(records);
			}
			// header
			String[] csvHeader = new String[8];
			int j = 0;
			csvHeader[j] = "覆核狀態";
			csvHeader[++j] = "股票代碼";
			csvHeader[++j] = "股票名稱";
			csvHeader[++j] = "CNR分配率";
			csvHeader[++j] = "CNR加減碼";
			csvHeader[++j] = "加碼區間起日";
			csvHeader[++j] = "加碼區間迄日";
//			csvHeader[++j] = "狀態";
						
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);  
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			// download
			notifyClientToDownloadFile(url, fileName);
		} else
			return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	public void upload(Object body, IPrimitiveMap header) throws JBranchException {
		PRD241InputVO inputVO = (PRD241InputVO) body;
		PRD241OutputVO return_VO = new PRD241OutputVO();
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		List<String> error = new ArrayList<String>();
		List<String> error2 = new ArrayList<String>();
		List<String> error3 = new ArrayList<String>();
		List<String> error4 = new ArrayList<String>();
		List<String> error5 = new ArrayList<String>();
		Set<String> idList = new HashSet<String>();
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if(!dataCsv.isEmpty()) {
			for(int i = 0;i < dataCsv.size();i++) {
				String[] str = dataCsv.get(i);
				if(i == 0) {
					try {
						if(!"商品代碼".equals(str[0].trim()))
							throw new Exception(str[0]);
						else if(!"CNR分配率".equals(str[1].trim()))
							throw new Exception(str[1]);
						else if(!"CNR加減碼".equals(str[2].trim()))
							throw new Exception(str[2]);
						else if(!"加碼區間起日".equals(str[3].trim()))
							throw new Exception(str[3]);
						else if(!"加碼區間迄日".equals(str[4].trim()))
							throw new Exception(str[4]);
					} catch(Exception ex) {
						throw new APException(ex.getMessage() + ":上傳格式錯誤，請下載範例檔案");
					}
					continue;
				}
				// check prd_id 請PG判斷，第一欄若為空，就跳過去不判斷。
				if(StringUtils.isBlank(str[0]))
					continue;
				// 使用者很愛重覆上傳
				if(idList.contains(str[0].trim())) {
					error4.add(str[0]);
					continue;
				}
				idList.add(str[0].trim());
				// TBPRD_STOCK
				QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_STOCK where PRD_ID = :id ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list.size() == 0) {
					error.add(str[0]);
					continue;
				}
				// TBPRD_STOCK_REVIEW
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT PRD_ID FROM TBPRD_STOCK_REVIEW where PRD_ID = :id and REVIEW_STATUS = 'W' ");
				queryCondition.setObject("id", str[0].trim());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
				if (list2.size() > 0) {
					error2.add(str[0]);
					continue;
				}
				
				// seq
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT SQ_TBPRD_STOCK_REVIEW.nextval AS SEQ FROM DUAL ");
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
				BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
				// add TBPRD_STOCK_REVIEW
				TBPRD_STOCK_REVIEWVO vo = new TBPRD_STOCK_REVIEWVO();
				vo.setSEQ(seqNo);
				if(utf_8_length(str[0]) > 10) {
					error3.add(str[0]);
					continue;
				}	
				else
					vo.setPRD_ID(str[0].trim());
				if(StringUtils.isNotBlank(str[1])) {
					try {
						BigDecimal str1 = new BigDecimal(str[1]);
						// NUMBER(6,2)
						if(getNumOfBigDecimal(str1) > 4)
							throw new Exception("");
						vo.setCNR_YIELD(str1);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[1]);
						continue;
					}
				}
				else
					vo.setCNR_YIELD(null);
				if(StringUtils.isNotBlank(str[2])) {
					try {
						BigDecimal str2 = new BigDecimal(str[2]);
						// NUMBER(6,2)
						if(getNumOfBigDecimal(str2) > 4)
							throw new Exception("");
						vo.setCNR_MULTIPLE(str2);
					} catch (Exception e) {
						error3.add(str[0]+":"+str[2]);
						continue;
					}
				}
				else
					vo.setCNR_MULTIPLE(null);
				if(StringUtils.isNotBlank(str[3])) {
					try {
						vo.setMULTIPLE_SDATE(new Timestamp(sdf.parse(str[3]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMULTIPLE_SDATE(new Timestamp(sdf2.parse(str[3]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[3]);
							continue;
						}
					}
				}
				else
					vo.setMULTIPLE_SDATE(null);
				if(StringUtils.isNotBlank(str[4])) {
					try {
						vo.setMULTIPLE_EDATE(new Timestamp(sdf.parse(str[4]).getTime()));
					} catch (Exception e) {
						try {
							vo.setMULTIPLE_EDATE(new Timestamp(sdf2.parse(str[4]).getTime()));
						} catch (Exception e2) {
							error3.add(str[0]+":"+str[4]);
							continue;
						}
					}
				}
				else
					vo.setMULTIPLE_EDATE(null);
				vo.setACT_TYPE("M");
				vo.setREVIEW_STATUS("W");
				dam.create(vo);
			}
		}
		return_VO.setErrorList(error);
		return_VO.setErrorList2(error2);
		return_VO.setErrorList3(error3);
		return_VO.setErrorList4(error4);
		return_VO.setErrorList5(error5);
		this.sendRtnObject(return_VO);
	}
	
	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PRD//PRD241_EXAMPLE.csv", "上傳指定商品代碼範例.csv");
	    this.sendRtnObject(null);
	}
	
	public void upload_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD241InputVO inputVO = (PRD241InputVO) body;
		PRD241OutputVO return_VO = new PRD241OutputVO();
		dam = this.getDataAccessManager();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// TBSYS_PRD_LINK
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.PRD_ID,b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_START_DATE,c.DOC_DUE_DATE,c.FILE_NAME,c.CREATETIME,'N' as SHARED from ");
		sql.append("(select DOC_ID,PRD_ID from TBSYS_PRD_LINK where PTYPE = :ptype ");
		queryCondition.setObject("ptype", inputVO.getPtype());
		if (!StringUtils.isBlank(inputVO.getPrd_id())) {
			sql.append("and PRD_ID = :prd_id ");
			queryCondition.setObject("prd_id", inputVO.getPrd_id());
		}
		sql.append(") a ");
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and DOC_TYPE = '02' ");
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and sysdate BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		
		// TBSYS_PRD_SHARED_LINK
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("select b.DOC_ID,b.DOC_NAME,b.DOC_TYPE,c.DOC_FILE_TYPE,c.DOC_URL,c.DOC_START_DATE,c.DOC_DUE_DATE,c.FILE_NAME,c.CREATETIME,'Y' as SHARED from ");
		sql.append("(select DOC_ID from TBSYS_PRD_SHARED_LINK where PTYPE = :ptype) a ");
		queryCondition.setObject("ptype", inputVO.getPtype());
		sql.append("inner join (select DOC_ID,DOC_NAME,DOC_TYPE from TBSYS_FILE_MAIN where SUBSYSTEM_TYPE = 'PRD' and DOC_TYPE = '02' ");
		sql.append(") b on a.DOC_ID = b.DOC_ID ");
		sql.append("inner join (select DOC_ID,DOC_FILE_TYPE,DOC_URL,DOC_START_DATE,DOC_DUE_DATE,FILE_NAME,CREATETIME from TBSYS_FILE_DETAIL where DOC_VERSION_STATUS = '2' and sysdate BETWEEN DOC_START_DATE and DOC_DUE_DATE) c on b.DOC_ID = c.DOC_ID ");
		queryCondition.setQueryString(sql.toString());
		list.addAll(dam.exeQuery(queryCondition));
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getRank(Object body, IPrimitiveMap header) throws JBranchException {
		PRD241OutputVO return_VO = new PRD241OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PRD_ID, STOCK_CNAME, RISKCATE_ID, PRD_RANK FROM TBPRD_STOCK WHERE PRD_RANK IS NOT NULL ORDER BY PRD_RANK");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void saveSort(Object body, IPrimitiveMap header) throws JBranchException {
		PRD241InputVO inputVO = (PRD241InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("UPDATE TBPRD_STOCK SET PRD_RANK = null WHERE PRD_RANK IS NOT NULL");
		dam.exeUpdate(queryCondition);
		
		for(Map<String, Object> map : inputVO.getReview_list()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("UPDATE TBPRD_STOCK SET PRD_RANK = :rank WHERE PRD_ID = :prd_id");
			queryCondition.setObject("rank", map.get("rank"));
			queryCondition.setObject("prd_id", map.get("prd_id"));
			dam.exeUpdate(queryCondition);
		}
		
		this.sendRtnObject(null);
	}
	
	
}