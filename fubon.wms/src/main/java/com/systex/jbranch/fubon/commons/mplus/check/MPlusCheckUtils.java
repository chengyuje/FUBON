package com.systex.jbranch.fubon.commons.mplus.check;

import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.fubon.commons.mplus.MPlusAlertGroupInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusAlterEmployeeInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusAlterGroupMembersInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusCountEaMemberVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusDelEmployeeInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusEmployeeInputVO;

public class MPlusCheckUtils extends AbstractMPlusCheckUtils{
	
	//員工名單新增/異動 V2檢核
	public StringBuffer checker(MPlusAlterEmployeeInputVO inputVO){
		StringBuffer errorMessage = checker((MPlusEmployeeInputVO)inputVO);
		String email = inputVO.getEmail();
		String countryCode = inputVO.getCountryCode();
		String msisdn = inputVO.getMsisdn();
		boolean msidnCodeIsNotEmpty = false;
		
		if(StringUtils.isNotBlank(countryCode) && StringUtils.isBlank(msisdn)){
			errorMessage.append("國碼與門號需同時存在");
		}
		else if(StringUtils.isNotBlank(msisdn) && StringUtils.isBlank(countryCode)){
			errorMessage.append("國碼與門號需同時存在");
		}
		else if(StringUtils.isNotBlank(countryCode) && StringUtils.isNotBlank(msisdn)){
			msidnCodeIsNotEmpty = true;
		}
		
		
		//新增
		if("add".equals(inputVO.getAction())){			
			if(email == null && !msidnCodeIsNotEmpty){
				errorMessage.append("email、門號擇一必入");	
			}
			errorMessage.append(chkEmptyReturnNextLine(inputVO.getName() , "員工姓名為必入"));
		}
		else if("modify".equals(inputVO.getAction())){
			errorMessage.append(chkEmptyReturnNextLine(inputVO.getAlterKey() , "alterKey為必入"));
		}
		else{
			errorMessage.append(chkEmptyReturnNextLine(inputVO.getAction() , "action為必入"));
		}
		
		if("1".equals(inputVO.getAlterKey())){
			errorMessage.append(chkEmptyReturnNextLine(inputVO.getEmail() , "電子郵件為必入"));
		}
		else if("2".equals(inputVO.getAlterKey())){
			errorMessage.append(chkEmptyReturnNextLine(inputVO.getEmployeeNo() , "員工編號為必入"));
		}

    	return errorMessage;
    }
    
    
	//群組異動成員 V2檢核
	public StringBuffer checker(MPlusAlterGroupMembersInputVO inputVO){
		return checker((MPlusEmployeeInputVO)inputVO).
			   append(chkEmptyReturnNextLine(inputVO.getAction() , "action為必入")).
			   append(chkEmptyReturnNextLine(inputVO.getGroupId() , "群組 ID為必入")).
			   append(chkEmptyReturnNextLine(inputVO.getAlterKey() , "員工資料的鍵值為必入")).
			   append(chkEmptyReturnNextLine(inputVO.getAlterParam() , "mail或員編為必入"));
	}

	//異動公司群組 V2檢核
	public StringBuffer checker(MPlusAlertGroupInputVO inputVO){
		StringBuffer errorMessage = checker((MPlusEmployeeInputVO)inputVO);
		
		if(chkEmpty(inputVO.getAction())){
			errorMessage.append("action為必入");
			return errorMessage;
		}
		
		if("create".equals(inputVO.getAction()))
			errorMessage.append(chkEmptyReturnNextLine(inputVO.getHostParam() , "action為create時，組長 Email或組長員工編號為必入"))
						.append(chkEmptyReturnNextLine(inputVO.getGroupName() , "action為create時，群組名稱為必入"));
						
		if(inputVO.getAction().matches("create|modify"))
			errorMessage.append(chkAllEmpty(inputVO.getHostParam() , inputVO.getAlterKey()) ? "action為create或modify時，組長資料的鍵值為必入" : "");
		
		if(inputVO.getAction().matches("modify|dismiss"))
			errorMessage.append(chkEmptyReturnNextLine(inputVO.getGroupId() , "action為modify或dismiss時，群組 ID為必入"));
		
		return errorMessage;		
	}

	//員工名單刪除 V2檢核
	public StringBuffer checker(MPlusDelEmployeeInputVO inputVO){
		return checker((MPlusEmployeeInputVO)inputVO).
			   append(chkEmptyReturnNextLine(inputVO.getAlterKey() , "刪除員工資料的鍵值為必入")).
			   append(chkEmptyReturnNextLine(inputVO.getAlterParam() , "mail或員編為必入"));
	}
	
	//員工總數檢核
	public StringBuffer checker(MPlusCountEaMemberVO inputVO){
		return new StringBuffer()
			.append(chkEmptyReturnNextLine(inputVO.getAccount() , "企業帳號為必入"))
	 		.append(chkEmptyReturnNextLine(inputVO.getPassword() , "企業帳號密碼為必入"));
	}
	
	//登入帳密檢核
	public StringBuffer checker(MPlusEmployeeInputVO inputVO){
		return new StringBuffer()
			.append(chkEmptyReturnNextLine(inputVO.getAccount() , "企業帳號為必入"))
	 		.append(chkEmptyReturnNextLine(inputVO.getPassword() , "企業帳號密碼為必入"));
	}
}
