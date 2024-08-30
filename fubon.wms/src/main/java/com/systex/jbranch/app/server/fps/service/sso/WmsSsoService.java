package com.systex.jbranch.app.server.fps.service.sso;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.bouncycastle.util.encoders.Base64;

import com.systex.jbranch.app.common.fps.table.TBSYS_SSO_INFOVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import com.systex.jbranch.ws.external.service.dao.ExtjlbDaoInf;

@Service("WmsSsoService")
public class WmsSsoService extends BizLogic implements WmsSsoServiceInf{
	@Autowired @Qualifier("ExtjlbDao")
	private ExtjlbDaoInf extjlbDao;
	
	@Autowired @Qualifier("WmsSsoDao")
	private WmsSsoDaoInf wmsSsoDao;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	public static void main(String...args){
		for(int i = 0 ; i < 100 ; i++){
			Long rabdomNum = new Random().nextLong();
			rabdomNum = rabdomNum < 0 ? (0 - rabdomNum) : rabdomNum;
			System.out.println(rabdomNum);
		}

	}
	
	/** 產生token **/
	@SuppressWarnings({"rawtypes", "unused" })
	public void tokenGeneration(Object body , IPrimitiveMap<EnumTiaHeader> headers) throws Exception {
		//系統參數TBSYSPARAMETER
		GenericMap configGmap = doGetSysSigSsoConfig();
		//傳述參數
		GenericMap paramGmap = new GenericMap((Map)body);
		HttpServletRequest request = paramGmap.get(HttpServletRequest.class);
		//簽署系統網址 + token參數
		String url = null;
		
		//系統ID
		String sysCode = paramGmap.getNotNullStr("SYS_CODE");
		
		//驗證碼常數 (隨機碼與驗證碼常數XOR，產生驗證碼)
		Long vnConst = Long.parseLong(configGmap.getNotNullStr("VN_CONST"));
		//系統代碼(VN_SYS_CODE)
		String vnSysCode = configGmap.getNotNullStr("SYS_CODE");
		//server ip
		//String serverIp = PipelineUtil.getRemoteAddr();
		String serverIp = InetAddress.getLocalHost().getHostAddress();
		//登入者員編
		String empId = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID));
		//登入者角色
		String role = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		//產生64位元長度隨機碼
		Long rabdomNum = new Random().nextLong();
		rabdomNum = rabdomNum < 0 ? (0 - rabdomNum) : rabdomNum;
		//隨機碼 與1618033988749894848L不做XOR
		String randomNumStr = ObjectUtils.toString(rabdomNum);		
		//產生token
		String token = sysCode + "@" + serverIp + "#" + empId + "#" + role + "#" + randomNumStr;
		//base64加密
		token = new String(Base64.encode(token.getBytes()));
		token = URLEncoder.encode(token, "UTF-8");
		
		//url
		String serverUrl = configGmap.getNotNullStr("SERVER_URL");
		//serverUrl = "http://172.17.240.26/eSoafWeb/Security/FubonSsoAuth.aspx";
		
		//寫入db
		TBSYS_SSO_INFOVO tbsysSsoInfoVo = new TBSYS_SSO_INFOVO();
		tbsysSsoInfoVo.setEMP_ID(empId);
		tbsysSsoInfoVo.setSYS_CODE(sysCode);
		tbsysSsoInfoVo.setROLE_ID(role);
		tbsysSsoInfoVo.setVN_SYS_CODE(vnSysCode);
		tbsysSsoInfoVo.setVN_SYS_ADDR(serverIp);
		tbsysSsoInfoVo.setRANDOM_NO(new BigDecimal(rabdomNum));
		getDataAccessManager().create(tbsysSsoInfoVo);

		url = serverUrl + "?TOKEN=" + token;
		logger.info("SIGN System url = " + url);
		
		sendRtnObject(url);
	}
	
	
	/** 提供外部系統回頭確認token是否正確  **/
	@SuppressWarnings({ "unused"})
	public GenericMap tokenVerification(GenericMap paramGmap) throws Exception {
		Long nowTime = Calendar.getInstance().getTime().getTime();
		String reqName = HttpServletRequest.class.getName();
		HttpServletRequest request = paramGmap.get(reqName);
		//清除過時資料
		wmsSsoDao.delTbsysSsoInfoTimeOutData();
		
		//Web Service帳號
		String wsId = paramGmap.getNotNullStr("WsID");
		//Web Service密碼
		String wsPwd = paramGmap.getNotNullStr("WsPwd");
		//驗證系統ID
		String sysCode = paramGmap.getNotNullStr("SysCode");
		//驗證字串：<系統代號> # <驗證碼>，以Base64編碼
		String verifyCode = paramGmap.getNotNullStr("VerifyCode");
		//取設系統參數設定
		GenericMap configGmap = doGetSysSigSsoConfig();
		//驗證碼時效(秒)
		Long vnDue = configGmap.getBigDecimal("VN_DUE").longValue();
		//驗證碼常數 (隨機碼與驗證碼常數XOR，產生驗證碼)
		Long vnConst = configGmap.getBigDecimal("VN_CONST").longValue();
		vnConst = Long.parseLong(configGmap.getNotNullStr("VN_CONST"));
		//解密後驗證碼
		String verifyDeCode = null;
		
		logger.info("#wsid:" + wsId);
		logger.info("#wspwd:" + wsPwd);
		logger.info("#verifyCode:" + verifyCode);
		
		if(configGmap.nEquals("WS_ID" , wsId) || configGmap.nEquals("WS_PWD" , wsPwd)) {
			return new GenericMap()
				.put("RtnCode", "E005")
				.put("RtnMessage", "Web Service帳密錯誤");
		
		}
		else if(StringUtils.isBlank(verifyCode)){
			logger.info("E003-1錯誤，驗證碼為空");
			return new GenericMap()
				.put("RtnCode", "E003")
				.put("RtnMessage", "驗證碼有誤");			
		}
		else if(StringUtils.isNotBlank(verifyCode)){
			verifyDeCode = new String(Base64.decode(verifyCode));
			int lastIdx = verifyDeCode.lastIndexOf("#");
			boolean isError = lastIdx != -1 && verifyDeCode.length() > lastIdx && verifyDeCode.substring(lastIdx + 1).matches("-?\\d+");
			
			if(!isError){
				logger.info("E003-2錯誤 start");
				logger.info("after base64" + verifyDeCode);
				logger.info("E003-2錯誤 end");
				return new GenericMap()
					.put("RtnCode", "E003")
					.put("RtnMessage", "驗證碼有誤");
			}
		}

		String [] verifyDeCodes = verifyDeCode.split("#");
		String paramSysCode = verifyDeCodes[0];		
		String randomCodeStr = verifyDeCodes[1];
		
		//server ip
		String serverIp = paramSysCode.split("@")[1];

		//還原隨機碼後查詢
		BigDecimal randowCode = new BigDecimal(new BigDecimal(randomCodeStr).longValue() ^ vnConst);
		logger.info("#after XOR verifyCode:" + randowCode);
		
		//查詢該隨機碼是否存在
		List<Map<String , Object>> resultList = wmsSsoDao.queryForRandomNo("SIG", randowCode);
		
		if(CollectionUtils.isEmpty(resultList)){
			logger.info("E003-3錯誤，資料庫無相關驗證碼");
			return new GenericMap()
				.put("RtnCode", "E003")
				.put("RtnMessage", "驗證碼有誤");
		}
		else if(!paramSysCode.startsWith(sysCode)){
			logger.info("E00錯誤" + sysCode + "," + paramSysCode);
			return new GenericMap()
				.put("RtnCode", "E00")
				.put("RtnMessage", "系統代號錯誤");
		}
		
		GenericMap resultGmap = new GenericMap(resultList.get(0));
		Date createTime = resultGmap.getDate("CREATETIME");
		
		//差超過60秒
		if((nowTime - createTime.getTime()) / 1000 > 60){
			logger.info("E004錯誤 驗證碼已超過有效期間");
			return new GenericMap()
				.put("RtnCode", "E004")
				.put("RtnMessage", "驗證碼已超過有效期間");
			
		}
		else if(resultGmap.nEquals("VN_SYS_ADDR" , serverIp)){
			logger.info("E002錯誤" + serverIp);
			logger.info("E002錯誤" + resultGmap.get("VN_SYS_ADDR"));
			return new GenericMap()
				.put("RtnCode", "E002")
				.put("RtnMessage", "系統位址錯誤");			
		}
		
		return new GenericMap()
			.put("RtnCode", "0000")
			.put("RtnMessage", "成功");
	}
	
	private GenericMap doGetSysSigSsoConfig() throws Exception{
		GenericMap configGmap = new GenericMap();
		List<Map<String , Object>> wsConfig = extjlbDao.queryParameterConf("SYS.SIG_SSO");
		
		if(CollectionUtils.isEmpty(wsConfig)){
			throw new Exception("can not found config[SYS.SIG_SSO]");
		}
		
		for(Map<String , Object> configMap : wsConfig){
			String key = ObjectUtils.toString(configMap.get("PARAM_CODE"));
			
			//檢查數值格式參數
			if(key.matches("VN_DUE|VN_CONST") && !ObjectUtils.toString(configMap.get("PARAM_NAME")).matches("\\d+")){
				
				throw new NumberFormatException(key + " is format error");
			}
			
			configGmap.put(configMap.get("PARAM_CODE") , configMap.get("PARAM_NAME"));
		}
		
		return configGmap;
	}
	
	
	public static String getRemoteAddr(HttpServletRequest request)
	{
		String ip = request.getHeader("x-forwarded-for");

		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
		{ 
			ip = request.getHeader("Proxy-Client-IP");
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{ 
			ip = request.getHeader("WL-Proxy-Client-IP"); 
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
		{ 
			ip = request.getRemoteAddr(); 
		}

		int endIndex=ip.indexOf(",");
		if(endIndex>=0)
			ip=ip.substring(0, endIndex).trim();
		
		return ip;
	}
}
