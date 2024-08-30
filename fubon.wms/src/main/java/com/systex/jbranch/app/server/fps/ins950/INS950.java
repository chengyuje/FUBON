package com.systex.jbranch.app.server.fps.ins950;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBINS_PARA_HEADERVO;
import com.systex.jbranch.app.common.fps.table.TBINS_REPORTVO;
import com.systex.jbranch.app.server.fps.ins130.INS130;
import com.systex.jbranch.app.server.fps.ins200.INS200;
import com.systex.jbranch.app.server.fps.ins810.FubonIns810;
import com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ins950")
@Scope("request")
public class INS950 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS950.class);
	
	@Autowired @Qualifier("ins810")
	private FubonIns810 ins810;
	
	public FubonIns810 getIns810() {
		return ins810;
	}

	public void setIns810(FubonIns810 ins810) {
		this.ins810 = ins810;
	}
	
	@Autowired @Qualifier("ins200")
	private INS200 ins200;
	
	public INS200 getIns200() {
		return ins200;
	}

	public void setIns200(INS200 ins200) {
		this.ins200 = ins200;
	}
	
	@Autowired @Qualifier("ins130")
	private INS130 ins130;
	
	public INS130 getIns130() {
		return ins130;
	}
	
	public void setIns130(INS130 ins200) {
		this.ins130 = ins130;
	}
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException, InterruptedException {
		
		INS950InputVO inputVO = (INS950InputVO) body;
		INS950OutputVO return_VO = new INS950OutputVO();
		dam = this.getDataAccessManager();
		
		// TBINS_PARA_HEADER and TBINS_REPORT
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.PARA_TYPE, A.EFFECT_DATE, A.EXPIRY_DATE, A.STATUS,B.KEYNO, ");
		sql.append("B.FILE_NAME, B.REPORT_FILE, B.CREATOR ");
		sql.append("FROM TBINS_PARA_HEADER A, ");
		sql.append("TBINS_REPORT B ");
		sql.append("WHERE TO_CHAR(A.PARA_NO) = B.PLAN_ID ");
		sql.append("AND  (A.PARA_TYPE, A.EFFECT_DATE) IN ( ");
		sql.append("SELECT PARA_TYPE, MAX(EFFECT_DATE) ");
		sql.append("FROM TBINS_PARA_HEADER ");
		sql.append("WHERE EFFECT_DATE <= SYSDATE ");
		sql.append("AND NVL(EXPIRY_DATE, SYSDATE) >= SYSDATE ");
//		sql.append("WHERE EFFECT_DATE < TO_DATE(:nowDate, 'yyyyMMddhh24:mi:ss') ");
//		sql.append("AND NVL(EXPIRY_DATE, TO_DATE(:nowDate, 'yyyyMMddhh24:mi:ss')) >= TO_DATE(:nowDate, 'yyyyMMddhh24:mi:ss') ");
		sql.append("AND PARA_TYPE IN ('A', 'B', 'C') ");
		sql.append("AND STATUS = 'A' ");
		sql.append("GROUP BY PARA_TYPE) ");
		sql.append("Order by A.PARA_TYPE ");
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
//		String nowDate = sdf.format(new Date());
//		queryCondition.setObject("nowDate", nowDate);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		
		// 處理顯示 
		String[] paraTypes = new String[]{"A", "B", "C"};
		String[] types = new String[]{
				"1. 家庭財務安全 - 被保人生活費用 ：",
				"2. 家庭財務安全 - 子女生活費用：",
				"3. 家庭財務安全 - 子女教育基金："
		};
		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
		
		for(Map<String, Object> map : list) {
			newList.add(map);
		}
		
		for(int i =0; i<paraTypes.length; i++) {
			Map<String, Object> map = CollectionSearchUtils.findMapInColleciton(newList, "PARA_TYPE", paraTypes[i].toCharArray()[0]);
			if(map == null) {
				map = new HashMap<String, Object>();
				map.put("PARA_TYPE", paraTypes[i]);
				newList.add(map);
			} 
			map.put("TYPE", types[i]);
		}
		
		
		return_VO.setReportList(newList);
		
		this.sendRtnObject(return_VO);
	}
	
	public void goReview(Object body, IPrimitiveMap header) throws Exception {
		INS950InputVO inputVO = (INS950InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select sysdate from dual");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		Timestamp tp = (Timestamp) list.get(0).get("SYSDATE");
		// TBINS_PARA_HEADER	
		TBINS_PARA_HEADERVO new_head_vo = new TBINS_PARA_HEADERVO();
		new_head_vo.setPARA_NO(new BigDecimal(getSN("HEADER")));
		new_head_vo.setPARA_TYPE(inputVO.getPara_type());
		new_head_vo.setSUBMIT_DATE(tp);
		new_head_vo.setEFFECT_DATE(tp);
		new_head_vo.setCAL_DESC(inputVO.getCal_desc());
		new_head_vo.setSTATUS("A");
		dam.create(new_head_vo);
			
		// TBINS_REPORT
		// 非覆核修改舊的, 搬移舊上傳的檔案
//		if(inputVO.getFile_seq() != null) {
//			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			queryCondition.setQueryString("SELECT * FROM TBINS_REPORT WHERE KEYNO = :key_no");
//			queryCondition.setObject("key_no", inputVO.getFile_seq());
//			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
//			for(Map<String, Object> map : list) {
//				TBINS_REPORTVO rep_vo = new TBINS_REPORTVO();
//				rep_vo.setKEYNO(new BigDecimal(getSN("REPORT")));
//				rep_vo.setPLAN_ID(inputVO.getPara_no().toString());
//				if(map.get("FILE_NAME") != null)
//					rep_vo.setFILE_NAME(ObjectUtils.toString(map.get("FILE_NAME")));
//				if(map.get("REPORT_FILE") != null)
//					rep_vo.setREPORT_FILE((Blob) map.get("REPORT_FILE"));
//				dam.create(rep_vo);
//			}
//		} else {
			if(StringUtils.isNotBlank(inputVO.getFileName())) {				
				String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				byte[] data = Files.readAllBytes(new File(tempPath, inputVO.getFileName()).toPath());
				BigDecimal key_no = new BigDecimal(getSN("REPORT"));
				TBINS_REPORTVO rep_vo = new TBINS_REPORTVO();
				rep_vo.setKEYNO(key_no);
				rep_vo.setPLAN_ID(new_head_vo.getPARA_NO().toString());
				rep_vo.setFILE_NAME(inputVO.getFileRealName());
				rep_vo.setREPORT_FILE(ObjectUtil.byteArrToBlob(data));
				dam.create(rep_vo);
			}
//		}
		
		// 因為底下的 SQL 的關係 會有時間差的問題...所以小睡一下吧~
		Thread.sleep(2000);
		this.sendRtnObject(null);
	}
	
	private String getSN(String name) throws JBranchException {
		String ans = "";
		switch(name) {
			case "HEADER":
				QueryConditionIF qc_header = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_header.setQueryString("SELECT TBPRD_INS_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> head_list = dam.exeQuery(qc_header);
				ans = ObjectUtils.toString(head_list.get(0).get("SEQ"));
				break;
			case "REPORT":
				QueryConditionIF qc_report = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc_report.setQueryString("SELECT TBINS_REPORT_SEQ.nextval AS SEQ FROM DUAL");
				List<Map<String, Object>> report_list = dam.exeQuery(qc_report);
				ans = ObjectUtils.toString(report_list.get(0).get("SEQ"));
				break;
		}
		return ans;
	}
		
    /**
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.3.1 </b> 下載pdf檔案 (小幫手)</code>
     * @param inputVO : paraType
     * @return Object : file
     * @throws JBranchException : <br>
     *                <p>
     *                             <b>客製訊息如下 :</b>
     *                             <li>查無資料: </li>
     *                </p>
     */
	public void downloadFile (Object body, IPrimitiveMap<Object> header) throws JBranchException, SQLException{
		dam = this.getDataAccessManager();
		//取得傳進來的資料
		GenericMap inputGmap = new GenericMap((Map) body);	
		//取得paraType
		String paraType = inputGmap.getNotNullStr("paraType");
		//用來存放下載的PDF
		Map outputMap = new HashMap(); 
		//判斷是否為保單健診
		boolean isIns = false;
		//判斷是否為保險規劃
		boolean isInsPlan = false;
		//下載PDF的blob
		Blob pdfFile = null;
		
		switch(paraType){
			case "IC101" : {
				paraType = "A";
				isIns = true;
				break;
			}
			case "IC102" : {
				paraType = "B";
				isIns = true;
				break;
			}
			case "IC103" : {
				paraType = "C";
				isIns = true;
				break;
			}
			case "IP001" : {
				paraType = "1";
				isInsPlan = true;
				break;
			}
			case "IP002" : {
				paraType = "2";
				isInsPlan = true;
				break;
			}
			case "IP003" : {
				paraType = "3";
				isInsPlan = true;
				break;
			}
			case "IP004" : {
				paraType = "4";
				isInsPlan = true;
				break;
			}
			default: break;
		}
		
		if(isIns){
			List<Map<String, Object>> pdfSeqlist = getIns130().getPdfFileSql(paraType);
			BigDecimal keyNo = new BigDecimal(pdfSeqlist.get(0).get("KEYNO").toString());
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT REPORT_FILE FROM TBINS_REPORT WHERE KEYNO = :key_no");
			queryCondition.setObject("key_no", keyNo);
			List<Map<String, Object>> insList = dam.exeQuery(queryCondition);
			pdfFile = (Blob) insList.get(0).get("REPORT_FILE");
		}else if(isInsPlan){
			List<Map<String, Object>>  list = getIns810().getPlanNo(new PolicySuggestInputVO(paraType, null, null,null), dam);
			BigDecimal paraNo = new BigDecimal(getIns810().getSingleMapValue(list, "PARA_NO"));
			pdfFile = getIns200().getPolicySuggestFile(paraNo);
		}
					
		if(pdfFile == null){
			throw new APException("無PDF檔案");
		}
		
		byte[] pdfFileArr = ObjectUtil.blobToByteArr(pdfFile);
		
		//file : PDF檔案的byte[]，使用Base64加密
		outputMap.put("file", DatatypeConverter.printBase64Binary(pdfFileArr));
		
		sendRtnObject(outputMap);			
	}
	
}