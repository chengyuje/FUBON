package com.systex.jbranch.app.server.fps.ins810;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBINS_CUST_MASTVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.io.JoinDifferentSysBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * INS810
 * 
 * @author Jimmy
 * @date 2017/08/15
 * @spec null
 */
abstract public class INS810 extends JoinDifferentSysBizLogic{
	public enum QueryType{
		IN_BUY , OUT_BUY;
	}
	
	private Logger logger = LoggerFactory.getLogger(INS810.class);
	
	/**取得行外保單*/
	abstract public List<Map<String, Object>> doGetOutBuy(Object body) throws JBranchException;
	
	/**行內取得可視範圍中保單號碼清單*/
	abstract public List queryInBuyPolicyList(String custId , List<String> aoCode , String loginBraNbr) throws JBranchException;
	
	/**行內取得可視範圍中保單號碼清單*/
	abstract public List queryInBuyPolicyList(List<Map<String , Object>> inOutBuyFamilyList) throws JBranchException;
	
	/**取行內保單*/
	abstract public List<Map<String, Object>> doGetInBuy(Object body) throws JBranchException;
	
	/**取家庭戶資料*/
	abstract public INS810OutputVO getFamailyLst(Object body) throws JBranchException;
	
	/**取得關係戶*/
	abstract public List<Map<String , Object>> getGenealogyList(List<Map<String , Object>> qeryList);
	
	/**關係戶代碼轉換*/
	abstract public Map<String , String> getGenealogyMap(List<Map<String , Object>> qeryList);
	
	/**行外保單查詢結果別名轉換*/
	abstract public List<Map<String, Object>> reNameOutBuyForPolicyMap(
		List<Map<String, Object>> outBuyList , InOutBuyDataProcess [] inOutBuyDataProcess) throws JBranchException;
	
	/**行內保單查詢結果別名轉換*/
	abstract public List<Map<String, Object>> reNameInBuyForPolicyMap(
		List<Map<String, Object>> inBuyList , InOutBuyDataProcess [] inOutBuyDataProcess) throws JBranchException;
	
	/**取匯率檔*/
	abstract public Map<String , BigDecimal> queryRefExcRate() throws JBranchException;
	
	/**
	 * 保險公司名稱
	 * @param body
	 * 
	 * */
	public void queryCOM_ID(Object body, IPrimitiveMap header) throws JBranchException{
		INS810InputVO inputVO = (INS810InputVO)body;
		INS810OutputVO outputVO = new INS810OutputVO();
		
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COM_ID AS DATA,COM_NAME AS LABEL FROM TBPRD_INSDATA_COMPANY ORDER BY COM_TYPE ");
		qc.setQueryString(sb.toString());
		outputVO.setCOMList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 取得保險年齡-給後端用
	 * @param body
	 * @return OutpuVO
	 * @throws Exception
	 * */
	public int getAge(Date birthday) throws APException{
		INS810InputVO inputVO = new INS810InputVO();
		inputVO.setBirthday(birthday);
		INS810OutputVO ins810outputVO = getAge(inputVO);
		return ins810outputVO.getAge();
	}
	
	/**
	 * 取得保險年齡-給前端用
	 * @param body
	 * @return OutpuVO
	 * @throws Exception
	 * */
	public void getAge(Object body, IPrimitiveMap header) throws APException{
		INS810InputVO inputVO = (INS810InputVO) body;
		INS810OutputVO outputVO = new INS810OutputVO();
		outputVO = getAge(body);
		sendRtnObject(outputVO);
	}
	
	/**
	 * 取得保險年齡
	 * @param body
	 * @return OutpuVO
	 * @throws Exception
	 * */
	public INS810OutputVO getAge(Object body) throws APException{
		INS810InputVO inputVO = (INS810InputVO) body;
		INS810OutputVO outputVO = new INS810OutputVO();
		
		try {
			Calendar now = Calendar.getInstance();
			Calendar b = Calendar.getInstance();
			b.setTime(inputVO.getBirthday());
			int year = now.get(Calendar.YEAR) - b.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) - b.get(Calendar.MONTH);
			int day = now.get(Calendar.DAY_OF_MONTH) - b.get(Calendar.DAY_OF_MONTH);
			
			if(month < 0){
				month = 12-b.get(Calendar.MONTH)+now.get(Calendar.MONTH);
				year-=1;
			}
			
			if(day<0){
				day = b.getMaximum(Calendar.DAY_OF_MONTH) - b.get(Calendar.DAY_OF_MONTH) + now.get(Calendar.DAY_OF_MONTH);
				month-=1;
			}
			
			if((month==6 && day>0) || month>6){
				year+=1;
			}
			outputVO.setAge(year);
			return outputVO;
		} catch (Exception e) {
			throw new APException("保險年齡計算有錯誤");
		}
	}
	
	/**
	 * 取得最新一次規劃建議書
	 * @param body
	 * @return notifyClientToDownloadFile
	 * @throws Exception
	 * */
	public void getReport(Object body, IPrimitiveMap header){
		
		List<Map<String, Object>> blobList = new ArrayList<Map<String,Object>>();
		INS810InputVO inputVO = (INS810InputVO) body;
		INS810OutputVO outputVO = new INS810OutputVO();
		
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" select FILE_NAME, REPORT_FILE from TBINS_REPORT ")
				.append(" where PLAN_ID = :reportid ")
				.append(" ORDER BY CREATETIME DESC ")
				.append(" FETCH FIRST 1 ROWS ONLY ");
			
			qc.setObject("reportid", inputVO.getReportID());
			qc.setQueryString(sb.toString());
			blobList = dam.exeQuery(qc);
			
			if(blobList.size() > 0){
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				Blob blob = (Blob) blobList.get(0).get("REPORT_FILE");
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				
				String fileName = blobList.get(0).get("FILE_NAME") != null ? ObjectUtils.toString(blobList.get(0).get("FILE_NAME")) : "無設定檔名";
				String uuid = UUID.randomUUID().toString();
				
				File targetFile = new File(filePath,uuid+".pdf");
				FileOutputStream fos = new FileOutputStream(targetFile);
				fos.write(blobAsBytes);
				fos.close();
				
				notifyClientToDownloadFile("temp\\"+uuid+".pdf", fileName);
			}else{
				outputVO.setGetReportMsg("ehl_01_INS810_001");
				sendRtnObject(outputVO);
			}
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 保險客戶主檔儲存
	 * @param body
	 * @return outputVO-saveInsCustMastMsg(新增或更新成功訊息)
	 * @throws Exception
	 * */
	public void saveInsCustMast(Object body, IPrimitiveMap header){
		INS_CUST_MASTIutputVO inputVO = (INS_CUST_MASTIutputVO) body;
		INS810OutputVO outputVO = new INS810OutputVO();
		
		DataAccessManager dam = getDataAccessManager();
		TBINS_CUST_MASTVO vo = new TBINS_CUST_MASTVO();
		TBCRM_CUST_MASTVO crmVO = new TBCRM_CUST_MASTVO();
		
		try {			
			String LoginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
			ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
			
			crmVO = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, inputVO.getCUST_ID());
			boolean AO_CODE = false;
			boolean BRA_NBR = false;
			
			if(crmVO != null) {
				for(String AO : loginAO){
					if(AO.equals(crmVO.getAO_CODE())){
						AO_CODE = true;
						break;
					}
				}
				
				BRA_NBR = LoginBraNbr.equals(crmVO.getBRA_NBR()) ? true : false;
			}

			vo = (TBINS_CUST_MASTVO) dam.findByPKey(TBINS_CUST_MASTVO.TABLE_UID, inputVO.getCUST_ID());
			if(!(AO_CODE && BRA_NBR)){				
				if(vo != null){
					//有資料就更新
					vo.setCUST_NAME(inputVO.getCUST_NAME());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = sdf.parse(inputVO.getBirthDay());
					vo.setBIRTH_DATE(new Timestamp(date.getTime()));
					if(StringUtils.isNotBlank(inputVO.getGENDER())){
						vo.setGENDER(inputVO.getGENDER());
					}
					if(StringUtils.isNotBlank(inputVO.getMARRIAGE_STAT())){
						vo.setMARRIAGE_STAT(inputVO.getMARRIAGE_STAT());
					}
					if(StringUtils.isNotBlank(inputVO.getFB_CUST())){
						vo.setFB_CUST(inputVO.getFB_CUST());
					}
					dam.update(vo);
					outputVO.setSaveInsCustMastMsg("ehl_01_common_006");
				}else{
					//沒資料就新增
					vo = new TBINS_CUST_MASTVO();
					vo.setCUST_ID(inputVO.getCUST_ID());
					vo.setCUST_NAME(inputVO.getCUST_NAME());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = sdf.parse(inputVO.getBirthDay());
					vo.setBIRTH_DATE(new Timestamp(date.getTime()));
					if(StringUtils.isNotBlank(inputVO.getGENDER())){
						vo.setGENDER(inputVO.getGENDER());
					}
					if(StringUtils.isNotBlank(inputVO.getMARRIAGE_STAT())){
						vo.setMARRIAGE_STAT(inputVO.getMARRIAGE_STAT());
					}
					if(StringUtils.isNotBlank(inputVO.getFB_CUST())){
						vo.setFB_CUST(inputVO.getFB_CUST());
					}
					dam.create(vo);
					outputVO.setSaveInsCustMastMsg("ehl_01_common_001");
				}
			}
			sendRtnObject(outputVO);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 取得序號
	 * @param body
	 * @return outputVO
	 * @throws Exception
	 * */
	public INS810OutputVO getInsSeq(Object body){
		INS810OutputVO outputVO = new INS810OutputVO();
		outputVO.setInsSeq(doGetInsSeq());
			
		return outputVO;
	}
	
	/** 取得TBINS_SEQ序號 
	 *  @return InsSeq
	 */
	public String doGetInsSeq(){
		return doGetSeqLdap("TBINS_SEQ" , "5" , "0");
	}
	
		
	/** 取得序號 
	 *  @param brforeStr &nbsp;:&nbsp;加在序號前的字串
	 *  @return InsSeq &nbsp;:&nbsp;組合完成的序號
	 */
	public String doGetInsSeqBefore(String brforeStr){
		return doGetSeq("TBINS_SEQ" , brforeStr , null);
	}
	
	/** 取得序號 
	 *  @param afterStr &nbsp;:&nbsp;加在序號後的字串
	 *  @return InsSeq &nbsp;:&nbsp;組合完成的序號
	 */
	public String doGetInsSeqAfter(String after){
		return doGetSeq("TBINS_SEQ" , null , after);
	}
	
	/** 取得序號 
	 *  @param brforeStr &nbsp;:&nbsp;加在序號前的字串
	 *  @param afterStr &nbsp;:&nbsp;加在序號後的字串
	 *  @return InsSeq &nbsp;:&nbsp;組合完成的序號
	 */
	public String doGetInsSeq(String brforeStr , String after){
		return doGetSeq("TBINS_SEQ" , brforeStr , after);
	}
	
	/** 取得序號 
	 *  @param brforeStr &nbsp;:&nbsp;加在序號前的字串
	 *  @return InsSeq &nbsp;:&nbsp;組合完成的序號
	 */
	public String doGetSeqBefore(String seqName , String brforeStr){
		return doGetSeq(seqName , brforeStr , null);
	}
	
	public String doGetSeqBefore(String seqName , String brforeStr , String size , String reStr){
		return doGetSeq(seqName , brforeStr , null , size , reStr);
	}
	
	/** 取得序號
	 *  @param seqName &nbsp;:&nbsp;sequence名稱
	 *  @param afterStr &nbsp;:&nbsp;加在序號後的字串
	 *  @return InsSeq &nbsp;:&nbsp;組合完成的序號
	 */
	public String doGetSeqAfter(String seqName , String after){
		return doGetSeq(seqName , null , after);
	}
	
	public String doGetSeqAfter(String seqName , String after , String size , String reStr){
		return doGetSeq(seqName , null , after , size , reStr);
	}
	
	/** 取得序號 
	 *  @param seqName &nbsp;:&nbsp;sequence名稱
	 *  @param brforeStr &nbsp;:&nbsp;加在序號前的字串
	 *  @param afterStr &nbsp;:&nbsp;加在序號後的字串
	 *  @return InsSeq &nbsp;:&nbsp;組合完成的序號
	 */
	public String doGetSeq(String seqName , String brforeStr , String after){
		String insSeq = doGetSeq(seqName);
		
		if(StringUtils.isNotBlank(insSeq)){
			return ObjectUtils.toString(brforeStr).trim() + 
				   insSeq + 
				   ObjectUtils.toString(after).trim(); 
		}
		
		return insSeq;
	}
	
	public String doGetSeq(String seqName , String brforeStr , String after , String size , String reStr){
		String insSeq = doGetSeq(seqName , size , reStr);
		
		if(StringUtils.isNotBlank(insSeq)){
			return ObjectUtils.toString(brforeStr).trim() + 
				   insSeq + 
				   ObjectUtils.toString(after).trim(); 
		}
		
		return insSeq;
	}
	
	/** 取得序號 
	 *  @param seqName &nbsp;:&nbsp;sequence名稱
	 *  @return InsSeq
	 */
	@SuppressWarnings("unchecked")
	public String doGetSeq(String seqName){
		String InsSeq = null;
		
		try {
			DataAccessManager dam = getDataAccessManager();
			
			List<Map<String, Object>> seqList = 
				dam.exeQuery(dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL)
				   .setQueryString("Select " + seqName + ".NEXTVAL NEXTVAL from dual"));
			
			if(CollectionUtils.isNotEmpty(seqList)){
				Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
				String nowDate = sdf.format(now);
				InsSeq = nowDate + String.valueOf(seqList.get(0).get("NEXTVAL"));
			}
		} 
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return InsSeq;
	}
	
	@SuppressWarnings("unchecked")
	public String doGetSeqLdap(String seqName , String size , String reStr){
		String InsSeq = null;
		
		try {
			DataAccessManager dam = getDataAccessManager();
			
			List<Map<String, Object>> seqList = 
				dam.exeQuery(dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL)
				   .setQueryString("Select lpad(" + seqName + ".NEXTVAL, " + size + ", '" + reStr + "') NEXTVAL from dual"));
			
			if(CollectionUtils.isNotEmpty(seqList)){
				Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
				String nowDate = sdf.format(now);
				InsSeq = nowDate + String.valueOf(seqList.get(0).get("NEXTVAL"));
			}
		} 
		catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return InsSeq;
	}
	
	
	/**
	 * 將行內及行外保單整合
	 * <br>type=null時回傳行內及行外保單整合List
	 * <br>type=in只回傳行內保單
	 * <br>type=out只回傳行外保單
	 * @param body
	 * @return IntegrationOutputVO
	 * @throws JBranchException 
	 * @throws Exception
	 * */
	public IntegrationOutputVO queryPolicyContent(Object body , InOutBuyDataProcess...inOutBuyDataProcess) throws JBranchException{
		INS810InputVO inputVO = (INS810InputVO) body;
		IntegrationOutputVO outputVO = new IntegrationOutputVO();
		outputVO.setIntegrationList(queryInOutBuySinglePolicy(inputVO , inOutBuyDataProcess));
	
		return outputVO;
	}
	
	public IntegrationOutputVO integrationForFamailyList(Object body) throws JBranchException{
		INS810InputVO inputVO = (INS810InputVO) body;
		IntegrationOutputVO outputVO = queryPolicyContent(body);
		outputVO.setIntegrationList(doPutRelationcode(inputVO , outputVO.getIntegrationList()));
		
		return outputVO;
	}

	/**給資訊源getCoverage01使用*/
	public List<Map<String, Object>> queryInOutBuySinglePolicy(INS810InputVO inputVO , InOutBuyDataProcess...inOutBuyDataProcess) throws JBranchException{
		List<Map<String, Object>> inOutBuyList = new ArrayList<Map<String,Object>>();//整合IN&OUT的List
		
		boolean isAll = StringUtils.isBlank(inputVO.getType());
		
		if(isAll || "in".equals(inputVO.getType())){
			inOutBuyList.addAll(reNameInBuyForPolicyMap(doGetInBuy(inputVO) , inOutBuyDataProcess));
		}
		
		if(isAll || "out".equals(inputVO.getType())){
			inOutBuyList.addAll(reNameOutBuyForPolicyMap(doGetOutBuy(inputVO) , inOutBuyDataProcess));
		}
	
		return inOutBuyList;
	}
	
	/**給資訊源getCoverage03使用*/
	public List<Map<String, Object>> queryInOutBuyMutiPolicy(INS810InputVO inputVO  , InOutBuyDataProcess...inOutBuyDataProcess) throws JBranchException{
		return doPutRelationcode(inputVO , queryInOutBuySinglePolicy(inputVO , inOutBuyDataProcess));
	}
	
	public List<Map<String, Object>> doPutRelationcode(INS810InputVO inputVO , List<Map<String, Object>> inOutBuyList) throws JBranchException{
		if(CollectionUtils.isNotEmpty(inOutBuyList)){
			//家庭戶清單
			INS810OutputVO familyQueryResult = getFamailyLst(inputVO);
			List<Map<String , Object>> famailyList = familyQueryResult.getGenealogyList();
			//轉換親屬關係代碼
			Map<String , String> relMap = null;

			if(CollectionUtils.isNotEmpty(famailyList)){
				relMap = getGenealogyMap(familyQueryResult.getGenealogyList());
				
				for(Map<String, Object> inOutBuy : inOutBuyList){
					String custId = ObjectUtils.toString(inOutBuy.get("CUST_ID"));
					String relationcode = null;
					
					if(StringUtils.isBlank(custId)){
						continue;
					}
					
					for(Map<String , Object> family : famailyList){
						if(custId.equals(family.get("RELATION_ID"))){
							relationcode = ObjectUtils.toString(family.get("RELATIONCODE"));
							break;
						}
					}
					
					if(StringUtils.isNotBlank(custId) && StringUtils.isNotBlank(relationcode)){
						inOutBuy.put("RELATIONCODE" , relationcode);
					}
				}
			}
		}
		
		return inOutBuyList;
	}
	
	public List<Map<String , Object>> getInsFamailyLst(Object body) throws JBranchException{
		return getGenealogyList(getFamailyLst(body).getGenealogyList());
	}
	
	/**
	 * 用於檢查登入者是否可顯示覆核下拉式選單
	 * @throws JBranchException
	 * **/
	public void chkAuth (Object body, IPrimitiveMap<Object> header) throws JBranchException {
		INS810InputVO inputVO = (INS810InputVO) body;
		INS810OutputVO outputVO = new INS810OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(1) AS CHKAUTH  ");
		sb.append(" FROM TBSYSSECUROLPRIASS ");
		sb.append(" WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = :itemID  ");
		sb.append(" AND FUNCTIONID = 'confirm') ");
		sb.append(" AND ROLEID = '" + (String) getCommonVariable(SystemVariableConsts.LOGINROLE) +"'");
		queryCondition.setObject("itemID", inputVO.getItemID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setChkList(list);
		sendRtnObject(outputVO);
	}
	
	/**取個人或含家庭戶的INSSEQ**/
	public List queryInsseqList(QueryType queryType , INS810InputVO inputVO) throws DAOException, JBranchException {
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		String custId = inputVO.getCUST_ID();
		List<String> loginAo = inputVO.getLoginAOCode();
		String loginBranch = inputVO.getLoginBranch();
		List<Map<String, Object>> mutiList = inputVO.getInOutBuyMutiPolicyList();
		
		boolean isCustIdNotEmpty = StringUtils.isNotBlank(custId);
		boolean isAoNotEmpty = CollectionUtils.isNotEmpty(loginAo);
		boolean isBrankNotEmpty = StringUtils.isNotBlank(loginBranch);
		boolean isMutiNotEmpty = CollectionUtils.isNotEmpty(mutiList);
		boolean isIn = QueryType.IN_BUY.equals(queryType);
		boolean isOut = QueryType.OUT_BUY.equals(queryType);

		//檢核是否可查詢
		boolean isQuery = (isIn && isCustIdNotEmpty && isAoNotEmpty && isBrankNotEmpty) ||
				  		  (isOut && isCustIdNotEmpty && isAoNotEmpty) ||
				  		  isMutiNotEmpty;
		//不符合上述條件跳出
		if(!isQuery){
			return null;
		}
		
		if(isOut){
			return isMutiNotEmpty ? queryOutBuyPolicyList(mutiList) : queryOutBuyPolicyList(custId , loginAo); 
		}
		else if(isIn){
			return isMutiNotEmpty ? queryInBuyPolicyList(mutiList) : queryInBuyPolicyList(custId , loginAo , loginBranch);
		}
		
		return null;
	}
	
	/**行外取得可視範圍中保單號碼清單*/
	public List<String> queryOutBuyPolicyList(String custId, List<String> aoCode) throws JBranchException {
		List<Map<String , Object>> resultList = null;
		List<String> insseqList = new ArrayList();
		DataAccessManager dam = this.getDataAccessManager();
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT DISTINCT INSSEQ ");
		sb.append(" FROM TBINS_OUTBUY_MAST A ");
		sb.append(" WHERE A.AO_CODE in(:loginAO) ");
		sb.append(" AND A.INSURED_ID = :custId ");
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setObject("loginAO" , aoCode);
		queryCondition.setObject("custId" , custId);
		queryCondition.setQueryString(sb.toString());
		
		if(CollectionUtils.isNotEmpty(resultList = dam.exeQuery(queryCondition))){
			for(Map<String , Object> resultMap : resultList){
				String insseq = ObjectUtils.toString(resultMap.get("INSSEQ"));
				
				if(StringUtils.isNotBlank(insseq)){
					insseqList.add(insseq);
				}
			}
		}

		return insseqList;
	}
	
	/**行外取得可視範圍中保單號碼清單*/
	public List<String> queryOutBuyPolicyList(List<Map<String, Object>> inOutBuyFamilyList) throws JBranchException {
		List<Map<String , Object>> resultList = null;
		List<String> insseqList = new ArrayList();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT DISTINCT INSSEQ ");
		sb.append(" FROM TBINS_OUTBUY_MAST A ");
		sb.append(" WHERE ");
		
		int idx = 0;
		for(Map<String, Object> inOutBuyFamily : inOutBuyFamilyList){
			if(idx > 0){
				sb.append(" OR ");	
			}
			
			sb.append(" ( A.INSURED_ID = :CUST_ID" + idx);
			queryCondition.setObject("CUST_ID" + idx , inOutBuyFamily.get("CUST_ID"));
			
			if(inOutBuyFamily.get("AO") instanceof List){
				sb.append(" AND A.AO_CODE in(:AO" + idx + ")");
			}
			else {
				sb.append(" AND A.AO_CODE = :AO" + idx);
				
			}
			queryCondition.setObject("AO" + idx , inOutBuyFamily.get("AO"));
			
			sb.append(" ) ");
			
			idx++;
		}
		
		queryCondition.setQueryString(sb.toString());
		
		if(CollectionUtils.isNotEmpty(resultList = dam.exeQuery(queryCondition))){
			for(Map<String , Object> resultMap : resultList){
				String insseq = ObjectUtils.toString(resultMap.get("INSSEQ"));
				
				if(StringUtils.isNotBlank(insseq)){
					insseqList.add(insseq);
				}
			}
		}
		
		return insseqList;
	}
	
	/**行內保單查詢後加工**/
	public void doInBuyDataProcess(InOutBuyDataProcess[] inOutBuyDataProcess , List<Map<String, Object>> inOutBuyList) throws JBranchException{
		for(InOutBuyDataProcess ioDataProcess : inOutBuyDataProcess){
			if(ioDataProcess.isIn()){
				ioDataProcess.excute(inOutBuyList , new GenericMap().put(DataAccessManager.class , getDataAccessManager()));
			}
		}
	}
	
	/**外保單查詢後加工**/
	public void doOutBuyDataProcess(InOutBuyDataProcess[] inOutBuyDataProcess , List<Map<String, Object>> inOutBuyList) throws JBranchException{
		for(InOutBuyDataProcess ioDataProcess : inOutBuyDataProcess){
			if(ioDataProcess.isOut()){
				ioDataProcess.excute(inOutBuyList , new GenericMap().put(DataAccessManager.class , getDataAccessManager()));
			}
		}
	}
}
