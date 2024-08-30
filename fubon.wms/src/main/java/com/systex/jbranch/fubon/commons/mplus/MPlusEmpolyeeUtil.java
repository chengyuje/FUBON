package com.systex.jbranch.fubon.commons.mplus;

import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.ALERT_ENPOLYEE_URL;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.ALERT_GROUP_MEMBERS_URL;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.ALERT_GROUP_URL;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.CHENGE_TOKEN;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.COUNT_EA_MENBER_URL;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.DELETE_ENPOLYEE_URL;
import static com.systex.jbranch.fubon.commons.mplus.cons.MPlusAlterEmployeeCons.PROD;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.mplus.check.MPlusCheckInf;
import com.systex.jbranch.fubon.commons.mplus.check.MPlusCheckUtils;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

@Component("mPlusEmpolyeeUtil")
@Scope("prototype")
public class MPlusEmpolyeeUtil extends AbstractMPlus {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final Map<Class , String> urlMapping = new Hashtable();
	
	public MPlusEmpolyeeUtil() {
		urlMapping.put(MPlusAlterEmployeeInputVO.class, ALERT_ENPOLYEE_URL);//員工名單新增/異動 V2
		urlMapping.put(MPlusDelEmployeeInputVO.class, DELETE_ENPOLYEE_URL);//員工名單刪除 V2
		urlMapping.put(MPlusAlertGroupInputVO.class, ALERT_GROUP_URL);//異動公司群組 V2 
		urlMapping.put(MPlusAlterGroupMembersInputVO.class, ALERT_GROUP_MEMBERS_URL);//群組異動成員 V2
		urlMapping.put(MPlusCountEaMemberVO.class, COUNT_EA_MENBER_URL);//員工總數
	}
   
	public static void main(String...args){
		MPlusAlterEmployeeInputVO vo = new MPlusAlterEmployeeInputVO();
		vo.setEmployeeNo("sdapodjposjdpo111");
		new MPlusEmpolyeeUtil().send2MPlusForPost(vo , new MPlusCheckUtils());
	}
	
    /**
     * 呼叫mplus ad server
     * @param inputVO 對應服務的vo
     * @param mpCheck 檢核vo的檢核器
     * @return Map 從ad接回的json轉map結果
     */
	public MPlusResult send2MPlusForPost(MPlusInputVOInf inputVO , MPlusCheckInf mpCheck){
    	StringBuffer errorMessage = null;
    	String url = urlMapping.get(inputVO.getClass());
    	
        try {
        	Map<String , String> config = getMplusUtil().getMPlusAccPwd();//getMplusUtil().getMPlusAccPwd();
        	inputVO.setAccount(config.get("ACT"));//帳號
        	inputVO.setPassword(config.get("password"));//密碼
        	inputVO.setSecretKey(config.get("SecretKey"));
        	
        	//判斷驗證條件
        	if(StringUtils.isNotBlank((errorMessage = mpCheck.checkVoIsSuccess(inputVO)).toString())){
        		logger.error(errorMessage.toString());
        		return null;
        	}
        
        	return send2MPlusForPost(url.replaceFirst(CHENGE_TOKEN , PROD) , inputVO);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }

	public MPlusResult send2MPlusForPostNameValuePair(MPlusInputVOInf inputVO , MPlusCheckInf mpCheck){
    	StringBuffer errorMessage = null;
    	String url = urlMapping.get(inputVO.getClass());
    	
        try {
        	Map<String , String> config = getMplusUtil().getMPlusAccPwd();
        	inputVO.setAccount(config.get("ACT"));//帳號
        	inputVO.setPassword(config.get("password"));//密碼
        	inputVO.setSecretKey(config.get("SecretKey"));
        	
        	//判斷驗證條件
        	if(StringUtils.isNotBlank((errorMessage = mpCheck.checkVoIsSuccess(inputVO)).toString())){
        		logger.error(errorMessage.toString());
        		return null;
        	}
        
        	return send2MPlusForPostNameValuePair(url.replaceFirst(CHENGE_TOKEN , PROD) , inputVO);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }

	
	public MPlusResult send2MPlusForGet(MPlusInputVOInf inputVO , MPlusCheckInf mpCheck){
    	StringBuffer errorMessage = null;
    	String url = urlMapping.get(inputVO.getClass());
    	
        try {
        	Map<String , String> config = getMplusUtil().getMPlusAccPwd();
        	inputVO.setAccount(config.get("ACT"));//帳號
        	inputVO.setPassword(config.get("password"));//密碼
        	inputVO.setSecretKey(config.get("SecretKey"));
        	
        	//判斷驗證條件
        	if(StringUtils.isNotBlank((errorMessage = mpCheck.checkVoIsSuccess(inputVO)).toString())){
        		logger.error(errorMessage.toString());
        		return null;
        	}
        	
        	return send2MPlusForGet(url.replaceFirst(CHENGE_TOKEN , PROD) , inputVO);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }
    
	//是否為測試環境
	public boolean isTestMode()
	{	
		try
		{
			//改判斷系統參數
			Map<String , String> xmlVar = new XmlInfo().getVariable("FUBONSYS.MODE" , FormatHelper.FORMAT_3);
			return xmlVar != null && "D".equals(xmlVar.get("MODE"));
		}
		catch(Exception e)
		{
			return false;
		}
	}
    
}
