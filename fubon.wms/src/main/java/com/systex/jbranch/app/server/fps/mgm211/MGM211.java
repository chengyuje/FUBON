package com.systex.jbranch.app.server.fps.mgm211;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_FILEVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFTPK;
import com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_GIFTVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_SIGN_FORMVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import de.schlichtherle.io.FileInputStream;

/**
 * MGM211
 * 
 * @author Carley
 * @date 2018/03/13
 */
@Component("mgm211")
@Scope("request")
public class MGM211 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<String, String>();
	
	public MGM211() {
		headColumnMap.put("客戶ID"		, "CUST_ID");
		headColumnMap.put("回饋金額"		, "TOTAL");
		headColumnMap.put("贈品代碼1"	, "GIFT01");
		headColumnMap.put("贈品代碼2"	, "GIFT02");
		headColumnMap.put("贈品代碼3"	, "GIFT03");
		headColumnMap.put("贈品代碼4"	, "GIFT04");
		headColumnMap.put("贈品代碼5"	, "GIFT05");
		headColumnMap.put("贈品代碼6"	, "GIFT06");
		headColumnMap.put("贈品代碼7"	, "GIFT07");
		headColumnMap.put("贈品代碼8"	, "GIFT08");
		headColumnMap.put("贈品代碼9"	, "GIFT09");
		headColumnMap.put("贈品代碼10"	, "GIFT10");
	}
	
	//範例.csv下載
	public void getExample(Object body, IPrimitiveMap header) throws Exception {
		CSVUtil csv = new CSVUtil();
		// 設定表頭
		csv.setHeader(headColumnMap.keySet().toArray(new String[headColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "VIP名單.csv");
		sendRtnObject(null);
	}
	
	//查詢所有禮贈品
	public void getGiftList (Object body, IPrimitiveMap header) throws JBranchException {
		MGM211OutputVO outputVO = new MGM211OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT GIFT_SEQ, GIFT_NAME FROM TBMGM_GIFT_INFO ORDER BY GIFT_SEQ ");
		
		queryCondition.setQueryString(sb.toString());
		outputVO.setGiftList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	//檢核上傳名單
	public void checkVipList (Object body, IPrimitiveMap header) throws Exception {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		MGM211OutputVO outputVO = new MGM211OutputVO();
		dam = this.getDataAccessManager();
		
		if (!StringUtils.isBlank(inputVO.getFile_name())) {
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFile_name());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			
			String line = null;
			List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
			Set<String> giftSet = new HashSet<>();
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, String> dataMap = new HashMap<String, String>();
				for (int i = 0; i < data.length; i++) {
//					System.out.println("**************" + i);
//					System.out.println(headColumnMap.get(head[i]));
//					System.out.println(head[i]);
//					System.out.println(data[i]);
					if("CUST_ID".equals(headColumnMap.get(head[i]))){
						String cust_id = data[i];
						
						QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						StringBuffer sb = new StringBuffer();
						sb.append("SELECT * FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
						
						queryCondition.setObject("cust_id", cust_id);
						queryCondition.setQueryString(sb.toString());
						
						List<Map<String,Object>> list = dam.exeQuery(queryCondition);
						if(list.size() == 0){
							throw new APException("上傳名單中有客戶ID非本行客戶，請重新上傳。");
						}
					} else if (!"TOTAL".equals(headColumnMap.get(head[i]))){
						giftSet.add(data[i]);
					}
				}
			}
			
			List<Map<String,Object>> list = new ArrayList<>();
			for(String giftNbr : giftSet){
				Map<String,Object> map = new HashMap<>();
				map.put("GIFT_SEQ", giftNbr);
				list.add(map);
			}
			
			outputVO.setResultList(list);
			sendRtnObject(outputVO);
			
		} else {
			sendRtnObject(null);
		}
	}
	
	//VIP：比對上傳名單之贈品代碼是否存在於適用贈品清單
	public void checkVipGift (Object body, IPrimitiveMap header) throws Exception {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		
		if(inputVO.getGiftList().size() > 0 && inputVO.getListGiftNbr().size() > 0){
			Set<String> giftNbr = new HashSet<>();
			for(Map<String,Object> map : inputVO.getGiftList()){
				giftNbr.add(map.get("GIFT_SEQ").toString());
			}
			for(Map<String,Object> map : inputVO.getListGiftNbr()){
//				System.out.println(map.get("GIFT_SEQ").toString());
				if(!giftNbr.contains(map.get("GIFT_SEQ").toString())){
					throw new APException("上傳名單中有贈品代碼不屬於適用贈品，請重新上傳。");
				}
			}
		}
		sendRtnObject(null);
	}
	
	//儲存活動
	public void save (Object body, IPrimitiveMap header) throws Exception {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		MGM211OutputVO outputVO = new MGM211OutputVO();
		dam = this.getDataAccessManager();
		
		String act_seq = inputVO.getAct_seq().trim();
//		String act_type = inputVO.getAct_type();
		
		if (StringUtils.isNotBlank(act_seq)) {
			TBMGM_ACTIVITY_MAINVO actVO = (TBMGM_ACTIVITY_MAINVO) dam.findByPKey(TBMGM_ACTIVITY_MAINVO.TABLE_UID, act_seq);
			
			//新增活動
			if(actVO == null){
				//上傳VIP名單(TBMGM_VIP)
//				if("V".equals(act_type)){
//					if (!StringUtils.isBlank(inputVO.getFile_name())) {
//						File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFile_name());
//						FileInputStream fi = new FileInputStream(csvFile);
//						BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
//						// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
//						String[] head = br.readLine().split(",");
//						String line = null;
//						List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
//						dam = this.getDataAccessManager();
//						
//						while((line = br.readLine()) != null) {
//							String[] data = line.split(",");
//							HashMap<String, String> dataMap = new HashMap<String, String>();
//							for (int i = 0; i < data.length; i++) {
//								dataMap.put(headColumnMap.get(head[i]), data[i]);
//							}
//							inputLst.add(dataMap);
//						}
//						
//						for(Map<String, String> map : inputLst){
//							TBMGM_VIPPK pk = new TBMGM_VIPPK();
//							TBMGM_VIPVO vo = new TBMGM_VIPVO();
//							
//							pk.setACT_SEQ(act_seq);
//							pk.setCUST_ID(map.get("CUST_ID"));
//							
//							vo.setcomp_id(pk);
//							vo.setSEQ(this.getEsbSeq());
//							vo.setTOTAL(new BigDecimal(map.get("TOTAL")));
//							
//							if(map.get("GIFT01") != null){
//								vo.setGIFT01(map.get("GIFT01"));
//							}
//							if(map.get("GIFT02") != null){
//								vo.setGIFT02(map.get("GIFT02"));
//							}
//							if(map.get("GIFT03") != null){
//								vo.setGIFT03(map.get("GIFT03"));
//							}
//							if(map.get("GIFT04") != null){
//								vo.setGIFT04(map.get("GIFT04"));
//							}
//							if(map.get("GIFT05") != null){
//								vo.setGIFT05(map.get("GIFT05"));
//							}
//							if(map.get("GIFT06") != null){
//								vo.setGIFT06(map.get("GIFT06"));
//							}
//							if(map.get("GIFT07") != null){
//								vo.setGIFT07(map.get("GIFT07"));
//							}
//							if(map.get("GIFT08") != null){
//								vo.setGIFT08(map.get("GIFT08"));
//							}
//							if(map.get("GIFT09") != null){
//								vo.setGIFT09(map.get("GIFT09"));
//							}
//							if(map.get("GIFT10") != null){
//								vo.setGIFT10(map.get("GIFT10"));
//							}
//							dam.create(vo);
//						}
//					}
//				}
				
				//上傳活動附件(TBMGM_ACTIVITY_FILE)
				if(inputVO.getFileList().size() > 0){
					for(Map<String,Object> map : inputVO.getFileList()){
						String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
						String joinedPath = new File(tempPath, map.get("name").toString()).toString();
						Path path = Paths.get(joinedPath);
						byte[] data = Files.readAllBytes(path);
						
						TBMGM_ACTIVITY_FILEVO fileVO = new TBMGM_ACTIVITY_FILEVO();
						fileVO.setSEQ(this.getFILE_SEQ());
						fileVO.setACT_SEQ(act_seq);
						fileVO.setACT_FILE_NAME(map.get("rname").toString());
						fileVO.setACT_FILE(ObjectUtil.byteArrToBlob(data));
						
						dam.create(fileVO);
					}
				}
				
				//上傳客戶簽收單(TBMGM_ACTIVITY_RECEIPT)
//				if (!StringUtils.isBlank(inputVO.getReceipt_name())) {
//					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
//					String joinedPath = new File(tempPath, inputVO.getReceipt_name()).toString();
//					Path path = Paths.get(joinedPath);
//					byte[] data = Files.readAllBytes(path);
//					
//					TBMGM_ACTIVITY_RECEIPTVO receiptVO = new TBMGM_ACTIVITY_RECEIPTVO();
//					receiptVO.setACT_SEQ(act_seq);
//					receiptVO.setRECEIPT_NAME(inputVO.getActual_receipt_name());
//					receiptVO.setRECEIPT(ObjectUtil.byteArrToBlob(data));
//						
//					dam.create(receiptVO);
//				}
				
				//上傳空白(推薦人/被推薦人)簽署表單
				if (StringUtils.isNotBlank(inputVO.getMgm_form_name()) || StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())) {
					TBMGM_ACTIVITY_SIGN_FORMVO formVO = new TBMGM_ACTIVITY_SIGN_FORMVO();
					formVO.setACT_SEQ(act_seq);
					
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					//推薦人
					if (StringUtils.isNotBlank(inputVO.getMgm_form_name())){
						String joinedPath = new File(tempPath, inputVO.getMgm_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] mgm_form = Files.readAllBytes(path);
						
						formVO.setMGM_FORM_NAME(inputVO.getReal_mgm_form_name());
						formVO.setMGM_FORM(ObjectUtil.byteArrToBlob(mgm_form));
					}
					//被推薦人
					if (StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())){
						String joinedPath = new File(tempPath, inputVO.getBe_mgm_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] be_mgm_form = Files.readAllBytes(path);
						
						formVO.setBE_MGM_FORM_NAME(inputVO.getReal_be_mgm_form_name());;
						formVO.setBE_MGM_FORM(ObjectUtil.byteArrToBlob(be_mgm_form));
					}
					dam.create(formVO);
				}
				
				//上傳適用贈品(TBMGM_ACTIVITY_GIFT)
				if(inputVO.getGiftList().size() > 0){
					for(Map<String,Object> map : inputVO.getGiftList()){
						TBMGM_ACTIVITY_GIFTPK pk = new TBMGM_ACTIVITY_GIFTPK();
						pk.setACT_SEQ(act_seq);
						pk.setGIFT_SEQ(map.get("GIFT_SEQ").toString());
						
						TBMGM_ACTIVITY_GIFTVO vo = new TBMGM_ACTIVITY_GIFTVO();
						vo.setcomp_id(pk);
						
						BigDecimal exch_uni = new BigDecimal(map.get("EXCH_UNI").toString());
						
//						if("M".equals(act_type)){
//							vo.setGIFT_POINTS(exch_uni);					
//						} else if ("V".equals(act_type)){
//							vo.setGIFT_REWARD(exch_uni);
//						}
						
						vo.setGIFT_POINTS(exch_uni);
						
						dam.create(vo);
					}
				}
				
				//上傳禮贈品活動資訊(TBMGM_ACTIVITY_MAIN)
				actVO = new TBMGM_ACTIVITY_MAINVO();
				actVO.setACT_SEQ(act_seq);							//活動代碼
				actVO.setACT_TYPE("M");								//活動類型 (2018.05.03會議決議移除VIP活動，僅保留MGM活動)
				actVO.setACT_NAME(inputVO.getAct_name());			//活動名稱
				actVO.setEFF_DATE(new Timestamp(inputVO.getEff_date().getTime()));				//生效日期
				actVO.setDEADLINE(new Timestamp(inputVO.getDeadline().getTime()));				//截止日期
				actVO.setEXC_DEADLINE(new Timestamp(inputVO.getExc_deadline().getTime()));		//兌換截止日期
				actVO.setACT_CONTENT(inputVO.getAct_content());		//活動內容
				actVO.setACT_APPROACH(inputVO.getAct_approach());	//活動辦法
				actVO.setPRECAUTIONS(inputVO.getPrecautions());		//注意事項
				
				dam.create(actVO);
				
			} else {	//活動維護 (VIP名單上傳後不可變更)
				
				//更新禮贈品活動資訊(TBMGM_ACTIVITY_MAIN)
				actVO.setACT_NAME(inputVO.getAct_name());			//活動名稱
				actVO.setEFF_DATE(new Timestamp(inputVO.getEff_date().getTime()));				//生效日期
				actVO.setDEADLINE(new Timestamp(inputVO.getDeadline().getTime()));				//截止日期
				actVO.setEXC_DEADLINE(new Timestamp(inputVO.getExc_deadline().getTime()));		//兌換截止日期
				actVO.setACT_CONTENT(inputVO.getAct_content());		//活動內容
				actVO.setACT_APPROACH(inputVO.getAct_approach());	//活動辦法
				actVO.setPRECAUTIONS(inputVO.getPrecautions());		//注意事項
				
				if("Y".equals(actVO.getTEMP_YN())){
					actVO.setTEMP_YN(null);
				}
				
				dam.update(actVO);
				
				//更新活動附件
				if(inputVO.getDelFileSeq() != null && inputVO.getDelFileSeq().size() > 0){
					for(Map<String, String> map :inputVO.getDelFileSeq()){
						if (StringUtils.isNotBlank(map.get("seq"))) {
							TBMGM_ACTIVITY_FILEVO fileVO =  (TBMGM_ACTIVITY_FILEVO) dam.findByPKey(TBMGM_ACTIVITY_FILEVO.TABLE_UID, map.get("seq"));
							if(null != fileVO)
								dam.delete(fileVO);							
						}
					}
				}
				
				if(inputVO.getFileList().size() > 0){
					for(Map<String,Object> map : inputVO.getFileList()){
						if(map.get("SEQ") == null){
							String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
							String joinedPath = new File(tempPath, map.get("name").toString()).toString();
							Path path = Paths.get(joinedPath);
							byte[] data = Files.readAllBytes(path);
							
							TBMGM_ACTIVITY_FILEVO fileVO = new TBMGM_ACTIVITY_FILEVO();
							fileVO.setSEQ(this.getFILE_SEQ());
							fileVO.setACT_SEQ(act_seq);
							fileVO.setACT_FILE_NAME(map.get("rname").toString());
							fileVO.setACT_FILE(ObjectUtil.byteArrToBlob(data));
							
							dam.create(fileVO);
						}
					}
				}
				
//				//更新客戶簽收單
//				if (!StringUtils.isBlank(inputVO.getReceipt_name())) {
//					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
//					String joinedPath = new File(tempPath, inputVO.getReceipt_name()).toString();
//					Path path = Paths.get(joinedPath);
//					byte[] data = Files.readAllBytes(path);
//					
//					TBMGM_ACTIVITY_RECEIPTVO receiptVO = (TBMGM_ACTIVITY_RECEIPTVO) dam.findByPKey(TBMGM_ACTIVITY_RECEIPTVO.TABLE_UID, act_seq);
//					if(null != receiptVO){		//更新
//						receiptVO.setRECEIPT_NAME(inputVO.getActual_receipt_name());
//						receiptVO.setRECEIPT(ObjectUtil.byteArrToBlob(data));					
//						dam.update(receiptVO);
//					} else {					//新增
//						TBMGM_ACTIVITY_RECEIPTVO newReceipVO = new TBMGM_ACTIVITY_RECEIPTVO();
//						newReceipVO.setACT_SEQ(act_seq);
//						newReceipVO.setRECEIPT_NAME(inputVO.getActual_receipt_name());
//						newReceipVO.setRECEIPT(ObjectUtil.byteArrToBlob(data));
//							
//						dam.create(newReceipVO);
//					}
//				}
				
				//更新空白(推薦人/被推薦人)簽署表單
				if (StringUtils.isNotBlank(inputVO.getMgm_form_name()) || StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())) {
					TBMGM_ACTIVITY_SIGN_FORMVO formVO = (TBMGM_ACTIVITY_SIGN_FORMVO) dam.findByPKey(TBMGM_ACTIVITY_SIGN_FORMVO.TABLE_UID, act_seq);
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					
					if(null != formVO){		//更新
						if (StringUtils.isNotBlank(inputVO.getMgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getMgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] mgm_form = Files.readAllBytes(path);
							
							formVO.setMGM_FORM_NAME(inputVO.getReal_mgm_form_name());
							formVO.setMGM_FORM(ObjectUtil.byteArrToBlob(mgm_form));
						}
						if (StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getBe_mgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] be_mgm_form = Files.readAllBytes(path);
							
							formVO.setBE_MGM_FORM_NAME(inputVO.getReal_be_mgm_form_name());;
							formVO.setBE_MGM_FORM(ObjectUtil.byteArrToBlob(be_mgm_form));
						}
						dam.update(formVO);
					} else {	//新增
						formVO = new TBMGM_ACTIVITY_SIGN_FORMVO();
						formVO.setACT_SEQ(act_seq);
						//推薦人
						if (StringUtils.isNotBlank(inputVO.getMgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getMgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] mgm_form = Files.readAllBytes(path);
							
							formVO.setMGM_FORM_NAME(inputVO.getReal_mgm_form_name());
							formVO.setMGM_FORM(ObjectUtil.byteArrToBlob(mgm_form));
						}
						//被推薦人
						if (StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getBe_mgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] be_mgm_form = Files.readAllBytes(path);
							
							formVO.setBE_MGM_FORM_NAME(inputVO.getReal_be_mgm_form_name());;
							formVO.setBE_MGM_FORM(ObjectUtil.byteArrToBlob(be_mgm_form));
						}
						dam.create(formVO);
					}
				}
				
				//更新適用贈品
				if(inputVO.getGiftList().size() > 0){
					//先刪除
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sb = new StringBuffer();
					sb.append("DELETE FROM TBMGM_ACTIVITY_GIFT WHERE ACT_SEQ = :act_seq ");
					queryCondition.setObject("act_seq", act_seq);
					queryCondition.setQueryString(sb.toString());
					dam.exeUpdate(queryCondition);
					
					dam = this.getDataAccessManager();		//避免"擷取的順序不對"
					//再新增
					for(Map<String,Object> map : inputVO.getGiftList()){
						//PK
						TBMGM_ACTIVITY_GIFTPK pk = new TBMGM_ACTIVITY_GIFTPK();
						pk.setACT_SEQ(act_seq);
						pk.setGIFT_SEQ(map.get("GIFT_SEQ").toString());
						//VO
						TBMGM_ACTIVITY_GIFTVO vo = new TBMGM_ACTIVITY_GIFTVO();
						vo.setcomp_id(pk);
						BigDecimal exch_uni = new BigDecimal(map.get("EXCH_UNI").toString());
//						if("M".equals(act_type)){
//							vo.setGIFT_POINTS(exch_uni);					
//						} else if ("V".equals(act_type)){
//							vo.setGIFT_REWARD(exch_uni);
//						}
						vo.setGIFT_POINTS(exch_uni);
						dam.create(vo);
					}
				}
			}
		} else {
			throw new APException("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
		}
		sendRtnObject(null);
	}
	
	//暫存
	public void tempSave (Object body, IPrimitiveMap header) throws Exception {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		String act_seq = inputVO.getAct_seq();
		MGM211OutputVO outputVO = new MGM211OutputVO();
		
		if (StringUtils.isNotBlank(act_seq)) {
			dam = this.getDataAccessManager();
			TBMGM_ACTIVITY_MAINVO actVO = (TBMGM_ACTIVITY_MAINVO) dam.findByPKey(TBMGM_ACTIVITY_MAINVO.TABLE_UID, act_seq);
			
			//新增暫存
			if(null == actVO){
				
				//上傳禮贈品活動資訊(TBMGM_ACTIVITY_MAIN)
				actVO = new TBMGM_ACTIVITY_MAINVO();
				actVO.setACT_SEQ(act_seq);								//活動代碼
				actVO.setACT_TYPE("M");									//活動類型 (2018.05.03會議決議移除VIP活動，僅保留MGM活動)
				
				if (StringUtils.isNotBlank(inputVO.getAct_name()))
					actVO.setACT_NAME(inputVO.getAct_name());			//活動名稱					
				
				if(inputVO.getEff_date() != null)
					actVO.setEFF_DATE(new Timestamp(inputVO.getEff_date().getTime()));			//生效日期
				
				if(inputVO.getDeadline() != null)
					actVO.setDEADLINE(new Timestamp(inputVO.getDeadline().getTime()));			//截止日期
				
				if(inputVO.getExc_deadline() != null)
					actVO.setEXC_DEADLINE(new Timestamp(inputVO.getExc_deadline().getTime()));	//兌換截止日期
				
				if (StringUtils.isNotBlank(inputVO.getAct_content()))
					actVO.setACT_CONTENT(inputVO.getAct_content());		//活動內容
				
				if (StringUtils.isNotBlank(inputVO.getAct_approach()))
					actVO.setACT_APPROACH(inputVO.getAct_approach());	//活動辦法
				
				if (StringUtils.isNotBlank(inputVO.getPrecautions()))
					actVO.setPRECAUTIONS(inputVO.getPrecautions());		//注意事項
				
				actVO.setTEMP_YN("Y");									//暫存註記
				
				dam.create(actVO);
				
				//上傳活動附件(TBMGM_ACTIVITY_FILE)
				if(inputVO.getFileList().size() > 0){
					for(Map<String,Object> map : inputVO.getFileList()){
						String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
						String joinedPath = new File(tempPath, map.get("name").toString()).toString();
						Path path = Paths.get(joinedPath);
						byte[] data = Files.readAllBytes(path);
						
						TBMGM_ACTIVITY_FILEVO fileVO = new TBMGM_ACTIVITY_FILEVO();
						fileVO.setSEQ(this.getFILE_SEQ());
						fileVO.setACT_SEQ(act_seq);
						fileVO.setACT_FILE_NAME(map.get("rname").toString());
						fileVO.setACT_FILE(ObjectUtil.byteArrToBlob(data));
						
						dam.create(fileVO);
					}
				}
				
				//上傳空白(推薦人/被推薦人)簽署表單
				if (StringUtils.isNotBlank(inputVO.getMgm_form_name()) || StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())) {
					TBMGM_ACTIVITY_SIGN_FORMVO formVO = new TBMGM_ACTIVITY_SIGN_FORMVO();
					formVO.setACT_SEQ(act_seq);
					
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					//推薦人
					if (StringUtils.isNotBlank(inputVO.getMgm_form_name())){
						String joinedPath = new File(tempPath, inputVO.getMgm_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] mgm_form = Files.readAllBytes(path);
						
						formVO.setMGM_FORM_NAME(inputVO.getReal_mgm_form_name());
						formVO.setMGM_FORM(ObjectUtil.byteArrToBlob(mgm_form));
					}
					//被推薦人
					if (StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())){
						String joinedPath = new File(tempPath, inputVO.getBe_mgm_form_name()).toString();
						Path path = Paths.get(joinedPath);
						byte[] be_mgm_form = Files.readAllBytes(path);
						
						formVO.setBE_MGM_FORM_NAME(inputVO.getReal_be_mgm_form_name());;
						formVO.setBE_MGM_FORM(ObjectUtil.byteArrToBlob(be_mgm_form));
					}
					dam.create(formVO);
				}
				
				//上傳適用贈品(TBMGM_ACTIVITY_GIFT)
				if(inputVO.getGiftList().size() > 0){
					for(Map<String,Object> map : inputVO.getGiftList()){
						TBMGM_ACTIVITY_GIFTPK pk = new TBMGM_ACTIVITY_GIFTPK();
						pk.setACT_SEQ(act_seq);
						pk.setGIFT_SEQ(map.get("GIFT_SEQ").toString());
						
						TBMGM_ACTIVITY_GIFTVO vo = new TBMGM_ACTIVITY_GIFTVO();
						vo.setcomp_id(pk);
						
						BigDecimal exch_uni = new BigDecimal(map.get("EXCH_UNI").toString());
						vo.setGIFT_POINTS(exch_uni);
						
						dam.create(vo);
					}
				}
				
			} else {
				//修改暫存
				//更新禮贈品活動資訊(TBMGM_ACTIVITY_MAIN)
				if (StringUtils.isNotBlank(inputVO.getAct_name().trim())){						//活動名稱
					actVO.setACT_NAME(inputVO.getAct_name());	
				} else {
					actVO.setACT_NAME(null);	
				}								
				if(inputVO.getEff_date() != null){												//生效日期
					actVO.setEFF_DATE(new Timestamp(inputVO.getEff_date().getTime()));
				} else {
					actVO.setEFF_DATE(null);
				}
				if(inputVO.getDeadline() != null){												//截止日期
					actVO.setDEADLINE(new Timestamp(inputVO.getDeadline().getTime()));
				} else {
					actVO.setDEADLINE(null);
				}
				if(inputVO.getExc_deadline() != null){											//兌換截止日期
					actVO.setEXC_DEADLINE(new Timestamp(inputVO.getExc_deadline().getTime()));
				} else {
					actVO.setEXC_DEADLINE(null);
				}
				if (StringUtils.isNotBlank(inputVO.getAct_content())){							//活動內容
					actVO.setACT_CONTENT(inputVO.getAct_content());
				} else {
					actVO.setACT_CONTENT(null);
				}
				if (StringUtils.isNotBlank(inputVO.getAct_approach())){							//活動辦法
					actVO.setACT_APPROACH(inputVO.getAct_approach());
				} else {
					actVO.setACT_APPROACH(null);
				}
				if (StringUtils.isNotBlank(inputVO.getPrecautions())){							//注意事項
					actVO.setPRECAUTIONS(inputVO.getPrecautions());
				} else {
					actVO.setPRECAUTIONS(null);
				}
				
				//暫存註記
				actVO.setTEMP_YN("Y");
				
				dam.update(actVO);
				
				//更新活動附件
				if(inputVO.getDelFileSeq() != null && inputVO.getDelFileSeq().size() > 0){
					for(Map<String, String> map :inputVO.getDelFileSeq()){
						if (StringUtils.isNotBlank(map.get("seq"))){
							TBMGM_ACTIVITY_FILEVO fileVO =  (TBMGM_ACTIVITY_FILEVO) dam.findByPKey(TBMGM_ACTIVITY_FILEVO.TABLE_UID, map.get("seq"));
							if(null != fileVO)
								dam.delete(fileVO);							
						}
					}
				}
				if(inputVO.getFileList().size() > 0){
					for(Map<String,Object> map : inputVO.getFileList()){
						if(map.get("SEQ") == null){
							String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
							String joinedPath = new File(tempPath, map.get("name").toString()).toString();
							Path path = Paths.get(joinedPath);
							byte[] data = Files.readAllBytes(path);
							
							TBMGM_ACTIVITY_FILEVO fileVO = new TBMGM_ACTIVITY_FILEVO();
							fileVO.setSEQ(this.getFILE_SEQ());
							fileVO.setACT_SEQ(act_seq);
							fileVO.setACT_FILE_NAME(map.get("rname").toString());
							fileVO.setACT_FILE(ObjectUtil.byteArrToBlob(data));
							
							dam.create(fileVO);
						}
					}
				}
				
				//更新空白(推薦人/被推薦人)簽署表單
				if (StringUtils.isNotBlank(inputVO.getMgm_form_name()) || StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())) {
					TBMGM_ACTIVITY_SIGN_FORMVO formVO = (TBMGM_ACTIVITY_SIGN_FORMVO) dam.findByPKey(TBMGM_ACTIVITY_SIGN_FORMVO.TABLE_UID, act_seq);
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					
					if(null != formVO){		//更新
						if (StringUtils.isNotBlank(inputVO.getMgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getMgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] mgm_form = Files.readAllBytes(path);
							
							formVO.setMGM_FORM_NAME(inputVO.getReal_mgm_form_name());
							formVO.setMGM_FORM(ObjectUtil.byteArrToBlob(mgm_form));
						}
						if (StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getBe_mgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] be_mgm_form = Files.readAllBytes(path);
							
							formVO.setBE_MGM_FORM_NAME(inputVO.getReal_be_mgm_form_name());;
							formVO.setBE_MGM_FORM(ObjectUtil.byteArrToBlob(be_mgm_form));
						}
						dam.update(formVO);
					} else {	//新增
						formVO = new TBMGM_ACTIVITY_SIGN_FORMVO();
						formVO.setACT_SEQ(act_seq);
						//推薦人
						if (StringUtils.isNotBlank(inputVO.getMgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getMgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] mgm_form = Files.readAllBytes(path);
							
							formVO.setMGM_FORM_NAME(inputVO.getReal_mgm_form_name());
							formVO.setMGM_FORM(ObjectUtil.byteArrToBlob(mgm_form));
						}
						//被推薦人
						if (StringUtils.isNotBlank(inputVO.getBe_mgm_form_name())){
							String joinedPath = new File(tempPath, inputVO.getBe_mgm_form_name()).toString();
							Path path = Paths.get(joinedPath);
							byte[] be_mgm_form = Files.readAllBytes(path);
							
							formVO.setBE_MGM_FORM_NAME(inputVO.getReal_be_mgm_form_name());;
							formVO.setBE_MGM_FORM(ObjectUtil.byteArrToBlob(be_mgm_form));
						}
						dam.create(formVO);
					}
				}
				
				//更新適用贈品
				if(inputVO.getGiftList().size() > 0){
					//先刪除
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sb = new StringBuffer();
					sb.append("DELETE FROM TBMGM_ACTIVITY_GIFT WHERE ACT_SEQ = :act_seq ");
					queryCondition.setObject("act_seq", act_seq);
					queryCondition.setQueryString(sb.toString());
					dam.exeUpdate(queryCondition);
					
					dam = this.getDataAccessManager();		//避免"擷取的順序不對"
					//再新增
					for(Map<String,Object> map : inputVO.getGiftList()){
						//PK
						TBMGM_ACTIVITY_GIFTPK pk = new TBMGM_ACTIVITY_GIFTPK();
						pk.setACT_SEQ(act_seq);
						pk.setGIFT_SEQ(map.get("GIFT_SEQ").toString());
						//VO
						TBMGM_ACTIVITY_GIFTVO vo = new TBMGM_ACTIVITY_GIFTVO();
						vo.setcomp_id(pk);
						BigDecimal exch_uni = new BigDecimal(map.get("EXCH_UNI").toString());
						vo.setGIFT_POINTS(exch_uni);
						dam.create(vo);
					}
				}
			}
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");
			
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
			queryCondition.setQueryString(sb.toString());
			
			outputVO.setResultList(dam.exeQuery(queryCondition));
			
		} else {
			throw new APException("「暫存」需至少輸入活動代碼。");
		}
		
		sendRtnObject(outputVO);
	}
	
	//查詢活動適用贈品
	public void getActGift (Object body, IPrimitiveMap header) throws JBranchException {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		MGM211OutputVO outputVO = new MGM211OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT A.GIFT_SEQ, INFO.GIFT_NAME, (CASE WHEN A.ACT_TYPE = 'M' THEN GIFT_POINTS ");
		sb.append("WHEN A.ACT_TYPE = 'V' THEN GIFT_REWARD END) AS EXCH_UNI FROM ( ");
		sb.append("SELECT ACT.ACT_TYPE, GIFT.* FROM ( ");
		sb.append("SELECT ACT_SEQ, GIFT_SEQ, GIFT_POINTS, GIFT_REWARD FROM TBMGM_ACTIVITY_GIFT ");
		sb.append("WHERE ACT_SEQ = :act_seq ) GIFT ");
		sb.append("LEFT JOIN TBMGM_ACTIVITY_MAIN ACT ON GIFT.ACT_SEQ = ACT.ACT_SEQ ) A ");
		sb.append("LEFT JOIN TBMGM_GIFT_INFO INFO ON A.GIFT_SEQ = INFO.GIFT_SEQ ");
		sb.append("ORDER BY A.GIFT_SEQ ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		sendRtnObject(outputVO);
	}
	
	//查詢活動附件
	public void getActFile (Object body, IPrimitiveMap header) throws JBranchException {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		MGM211OutputVO outputVO = new MGM211OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQ, ACT_FILE_NAME FROM TBMGM_ACTIVITY_FILE WHERE ACT_SEQ = :act_seq ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		sendRtnObject(outputVO);
	}
	
	//取得VIP上傳名單的案件序號(SEQ)
	private String getVIP_SEQ () throws JBranchException {		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());
        
		try{
			seqNum = date + "VIP" + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_VIP")));
		}catch(Exception e){
			sn.createNewSerial("TBMGM_VIP", "00000", 1, "d", null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = date + "VIP" + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_VIP")));
		}
		
		return seqNum;
	}
	
	//取得禮贈品活動附件檔的序號(SEQ)
	private String getFILE_SEQ () throws JBranchException {		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());
        
		try{
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_ACTIVITY_FILE")));
		}catch(Exception e){
			sn.createNewSerial("TBMGM_ACTIVITY_FILE", "00000", 1, "d", null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMGM_ACTIVITY_FILE")));
		}
		
		return seqNum;
	}
	
	//查詢活動(推薦人/被推薦人)簽署表單檔名
	public void getSignFormName (Object body, IPrimitiveMap header) throws JBranchException {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		MGM211OutputVO outputVO = new MGM211OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT MGM_FORM_NAME, BE_MGM_FORM_NAME FROM TBMGM_ACTIVITY_SIGN_FORM WHERE ACT_SEQ = :act_seq ");
		
		queryCondition.setObject("act_seq", inputVO.getAct_seq());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		sendRtnObject(outputVO);
	}
	//檢視已上傳的簽署表
	public void formView (Object body, IPrimitiveMap header) throws Exception {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		MGM211OutputVO outputVO = new MGM211OutputVO();
		
		String act_seq = inputVO.getAct_seq();
		String formType = inputVO.getFormType();
		if(StringUtils.isNotBlank(act_seq) && StringUtils.isNotBlank(formType)){
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBMGM_ACTIVITY_SIGN_FORM WHERE ACT_SEQ = :act_seq ");
			queryCondition.setObject("act_seq", act_seq);
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String uuid = UUID.randomUUID().toString();

			if("R".equals(formType)){
				Blob blob = (Blob) list.get(0).get("MGM_FORM");
				String mgm_form_name = list.get(0).get("MGM_FORM_NAME").toString();
				int index = mgm_form_name.lastIndexOf(".");
				String data_name = mgm_form_name.substring(index);
				
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				
				File targetFile = new File(filePath, uuid + data_name);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
//		    	this.notifyClientToDownloadFile("temp//"+uuid, fileName);
				outputVO.setPdfUrl("temp/" + uuid + data_name);
				
			} else if("B".equals(formType)){
				Blob blob = (Blob) list.get(0).get("BE_MGM_FORM");
				String be_mgm_form_name = list.get(0).get("BE_MGM_FORM_NAME").toString();
				int index = be_mgm_form_name.lastIndexOf(".");
				String data_name = be_mgm_form_name.substring(index);
				
				int blobLength = (int) blob.length();  
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				
				File targetFile = new File(filePath, uuid + data_name);
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
//		    	this.notifyClientToDownloadFile("temp//"+uuid, fileName);
				outputVO.setPdfUrl("temp/" + uuid + data_name);
			}
		}
	    this.sendRtnObject(outputVO);
	}
	
	//檢核活動代碼是否重複輸入
	public void checkActSeq (Object body, IPrimitiveMap header) throws JBranchException {
		MGM211InputVO inputVO = (MGM211InputVO) body;
		MGM211OutputVO outputVO = new MGM211OutputVO();
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		
		String act_seq = inputVO.getAct_seq();
		if (StringUtils.isNotBlank(act_seq)) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBMGM_ACTIVITY_MAIN WHERE ACT_SEQ = :act_seq ");
			queryCondition.setObject("act_seq", act_seq);
			queryCondition.setQueryString(sb.toString());
			resultList = dam.exeQuery(queryCondition);
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
}