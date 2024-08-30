package com.systex.jbranch.app.server.fps.service.inssig;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import javax.crypto.spec.SecretKeySpec;

import java.security.Key;
import java.sql.Blob;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.bouncycastle.util.encoders.Base64;

import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAPP_PDFVO;
import com.systex.jbranch.app.common.fps.table.TBSYS_SSO_INFOVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.ws.external.service.dao.ExtjlbDaoInf;

/***
 * 人壽視訊投保資料交換 Web Service
 */
@Service("WmsInsSigService")
public class WmsInsSigService extends BizLogic implements WmsInsSigServiceInf{
	@Autowired @Qualifier("ExtjlbDao")
	private ExtjlbDaoInf extjlbDao;
	
	@Autowired @Qualifier("WmsInsSigDao")
	private WmsInsSigDaoInf wmsInsSigDao;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * 產生JWT
	 */
	public GenericMap getWmsJWT(GenericMap paramGmap) throws Exception {
		//系統參數TBSYSPARAMETER: SYS.INS_SIG_JWT
		GenericMap configGmap = doGetSysInsSigConfig();
		
		//WS傳入資料
		String wsId = paramGmap.getNotNullStr("WsID"); //Web Service帳號
		String wsPwd = paramGmap.getNotNullStr("WsPwd"); //Web Service密碼
		String sysID = paramGmap.getNotNullStr("SYS_ID"); //驗證系統ID
		String empId = new String(Base64.decode(paramGmap.getNotNullStr("EMP_ID"))); //登入者員編
		String rdmNo = paramGmap.getNotNullStr("RDM_NO"); //隨機碼
		
		//檢核帳密系統代號
		if(configGmap.nEquals("WS_ID" , wsId) || configGmap.nEquals("WS_PWD" , wsPwd)) {
			return new GenericMap()
						.put("RtnCode", "E001")
						.put("RtnMessage", "Web Service帳密錯誤");
		
		} else if(configGmap.nEquals("SYS_ID" , sysID)) {
			return new GenericMap()
						.put("RtnCode", "E002")
						.put("RtnMessage", "系統代號錯誤");
		}
		
        //Get the key data
        Key key = MacProvider.generateKey(SignatureAlgorithm.HS256);
    	byte keySecretBytes[]= key.getEncoded();
    	
    	 //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = new SecretKeySpec(keySecretBytes, signatureAlgorithm.getJcaName());
        
        //驗證碼時效，預設60000 millisecond = 60 second, 若參數有設定，用參數
      	int jwtTimeToLive = 60000;
      	try { jwtTimeToLive = Integer.parseInt(configGmap.getNotNullStr("VN_DUE")); } catch(Exception e) {}
        //設定JWT時效
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + jwtTimeToLive; //預設60秒
        
        //set the JWT Claims
        JwtBuilder builder = Jwts.builder()
			        		.setId(wsId+wsPwd)
			                .setIssuedAt(now)
			                .setIssuer(sysID)
			                .setSubject(rdmNo)
			                .signWith(signingKey, signatureAlgorithm)
			                .setExpiration(new Date(expMillis));

        //Builds the JWT and serializes it to a compact, URL-safe string
        String jwtString = builder.compact();
        
        //寫入DB
        TBSYS_SSO_INFOVO tbsysSsoInfoVo = new TBSYS_SSO_INFOVO();
  		tbsysSsoInfoVo.setEMP_ID(empId);
  		tbsysSsoInfoVo.setSYS_CODE(sysID);
  		tbsysSsoInfoVo.setVN_SYS_ADDR(jwtString); //存入JWT String
  		tbsysSsoInfoVo.setRANDOM_NO(new BigDecimal(rdmNo));
  		tbsysSsoInfoVo.setSECRET_KEY(ObjectUtil.byteArrToBlob(keySecretBytes));
  		getDataAccessManager().create(tbsysSsoInfoVo);
      		
        logger.info("jwt = \"" + jwtString.toString() + "\"");
		
		return new GenericMap()
					.put("RtnCode", "0000")
					.put("RtnMessage", "成功")
					.put("Token", jwtString);		
	}
	
	/**
	 * 提供待簽署清單 
	 */
	@SuppressWarnings({ "unused"})
	public GenericMap getSignList(GenericMap paramGmap) throws Exception {
		
		HttpServletRequest servletRequest = paramGmap.get(HttpServletRequest.class.getName());
		String headerAuth = servletRequest.getHeader("Authorization");
		
		if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
			return new GenericMap()
					.put("RtnCode", "E999")
					.put("RtnMessage", "No JWT token found in request headers");	
	    }
		
		//檢核JTW
		String authToken = headerAuth.substring(7);
		GenericMap veriMap = verityJWT(authToken);
		if(veriMap != null) return veriMap;
		
		//取得待簽署清單
		List<Map<String , Object>> signList = wmsInsSigDao.querySignList(paramGmap);
		
		return new GenericMap()
				.put("RtnCode", "0000")
				.put("RtnMessage", "成功")
				.put("ResultCount", signList.size())
				.put("Results", signList);	
	}
	
	/**
	 * 人壽端回傳案件簽署結果
	 */
	@SuppressWarnings("unchecked")
	public GenericMap setCaseStatus(GenericMap paramGmap) throws Exception {
		
		HttpServletRequest servletRequest = paramGmap.get(HttpServletRequest.class.getName());
		String headerAuth = servletRequest.getHeader("Authorization");
		
		if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
			return new GenericMap()
					.put("RtnCode", "E999")
					.put("RtnMessage", "No JWT token found in request headers");
	    }
		
		//檢核JTW
		String authToken = headerAuth.substring(7);
		GenericMap veriMap = verityJWT(authToken);
		if(veriMap != null) return veriMap;
		
		
		List<Map<String , Object>> returnMap = new ArrayList();
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		
		//回傳資料
		List<Map<String , Object>> inputParam = paramGmap.get("InputParams");
		
		for(Map<String , Object> inputMap : inputParam) {
			BigDecimal insKeyNo = new BigDecimal(inputMap.get("insKeyNo").toString());	//保險進件主檔主鍵
			String isPass = ObjectUtils.toString(inputMap.get("isPass"));		//簽署狀態 Y/N
			String signOprId = ObjectUtils.toString(inputMap.get("signOprId"));	//簽署人ID
			String signDate = ObjectUtils.toString(inputMap.get("signDate"));	//簽署日期時間 YYYYMMDD HH24:MI:SS
			String rejReason = ObjectUtils.toString(inputMap.get("rejReason"));	//被退件原因代碼
			String rejReasonOther = ObjectUtils.toString(inputMap.get("rejReasonOther"));	//被退件原因為其他
			
			Map<String,Object> rMap = new HashMap<String,Object>();
			rMap.put("insKeyNo", insKeyNo);
			
			TBIOT_MAINVO mvo = (TBIOT_MAINVO)getDataAccessManager().findByPKey(TBIOT_MAINVO.TABLE_UID, insKeyNo);
			if(mvo == null) {
				rMap.put("RtnCode", "E001");
				rMap.put("RtnMessage", "查無此保險進件主檔主鍵");
			} else {
				if(StringUtils.isBlank(signOprId) || StringUtils.isBlank(signDate)) {
					rMap.put("RtnCode", "E002");
					rMap.put("RtnMessage", "簽署人ID/簽署日期時間為必填欄位");
				} else {
					try {
						sdFormat.parse(signDate);
						
						if(StringUtils.equals("Y", isPass)) {
							//簽署成功，更新簽署後狀態
							wmsInsSigDao.updateCaseStatus(inputMap);
							
							rMap.put("RtnCode", "0000");
							rMap.put("RtnMessage", "成功");
							
						} else if(StringUtils.equals("N", isPass)) {
							//退回，退回代碼為1-10
							if(StringUtils.isBlank(rejReason) || !rejReason.matches("[1-9]|10")) {
								rMap.put("RtnCode", "E004");
								rMap.put("RtnMessage", "退回原因代碼錯誤");
							} else {
								if(StringUtils.equals(rejReason, "10") && StringUtils.isBlank(rejReasonOther)) {
									rMap.put("RtnCode", "E005");
									rMap.put("RtnMessage", "退回原因為[其他]，需填寫詳細原因");
								} else {
									//簽署退回，更新簽署後狀態
									wmsInsSigDao.updateCaseStatus(inputMap);
									
									rMap.put("RtnCode", "0000");
									rMap.put("RtnMessage", "成功");
								}
							}
						} else {
							rMap.put("RtnCode", "E006");
							rMap.put("RtnMessage", "簽署狀態錯誤");
						}
					} catch(ParseException e) {
						rMap.put("RtnCode", "E003");
						rMap.put("RtnMessage", "簽署日期時間格式錯誤");
					} catch(Exception e) {
						rMap.put("RtnCode", "E007");
						rMap.put("RtnMessage", e.getMessage());
					}
				}
			}
			returnMap.add(rMap);
		}
		
		return new GenericMap()
				.put("ResultCount", returnMap.size())
				.put("Results", returnMap);		
	}
	
	/**
	 * 人壽端回傳已簽署案件PDF
	 */
	public GenericMap setCasePDF(GenericMap paramGmap) throws Exception {
		
		HttpServletRequest servletRequest = paramGmap.get(HttpServletRequest.class.getName());
		String headerAuth = servletRequest.getHeader("Authorization");
		
		if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
			return new GenericMap()
					.put("RtnCode", "E999")
					.put("RtnMessage", "No JWT token found in request headers");
	    }
		
		//檢核JTW
		String authToken = headerAuth.substring(7);
		GenericMap veriMap = verityJWT(authToken);
		if(veriMap != null) return veriMap;
		
		
		GenericMap returnMap = new GenericMap();
		
		//案件編號
		String caseId = paramGmap.getNotNullStr("caseId");
		//PDF檔案
		MultipartFile pdfFile = paramGmap.get("pdfFile");
		
		if(StringUtils.isBlank(caseId) || pdfFile == null){
			returnMap = new GenericMap()
						.put("RtnCode", "E001")
						.put("RtnMessage", "案件編號與PDF檔案皆不得為空");
		} else {
			TBIOT_MAPP_PDFVO tmpdf = (TBIOT_MAPP_PDFVO)getDataAccessManager().findByPKey(TBIOT_MAPP_PDFVO.TABLE_UID, caseId);
			
			if(tmpdf != null){
				//更新已簽署電子要保書PDF
				wmsInsSigDao.updateCasePDF(paramGmap);
				
				returnMap = new GenericMap()
							.put("RtnCode", "0000")
							.put("RtnMessage", "成功");
			} else {
				returnMap = new GenericMap()
							.put("RtnCode", "E002")
							.put("RtnMessage", "查無此案件編號");
			}
		}
		
		return returnMap;
	}
	
	/**
	 * 取得參數資料
	 * @return
	 * @throws Exception
	 */
	private GenericMap doGetSysInsSigConfig() throws Exception{
		GenericMap configGmap = new GenericMap();
		List<Map<String , Object>> wsConfig = extjlbDao.queryParameterConf("SYS.INS_SIG_JWT");
		
		if(CollectionUtils.isEmpty(wsConfig)){
			throw new Exception("can not found config[SYS.INS_SIG_JWT]");
		}
		
		for(Map<String , Object> configMap : wsConfig){
			String key = ObjectUtils.toString(configMap.get("PARAM_CODE"));
			
			//檢查數值格式參數
			if(StringUtils.equals("VN_DUE", key) && !ObjectUtils.toString(configMap.get("PARAM_NAME")).matches("\\d+")){
				throw new NumberFormatException("SYS.INS_SIG_JWT parameter " + key + " is format error");
			}
			
			configGmap.put(configMap.get("PARAM_CODE") , configMap.get("PARAM_NAME"));
		}
		
		return configGmap;
	}

	/**
	 * JWT驗證
	 * @param jwtString
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ExpiredJwtException
	 */
	public GenericMap verityJWT(String jwtString) throws SQLException, DAOException, JBranchException, ExpiredJwtException {
		//清除過時資料，只保留兩天
		wmsInsSigDao.delTbsysSsoInfoTimeOutData();
				
		//查詢該JWT是否存在
		List<Map<String , Object>> resultList = wmsInsSigDao.queryForJwtString(jwtString);
		if(CollectionUtils.isEmpty(resultList)) {
			return new GenericMap()
					.put("RtnCode", "E998")
					.put("RtnMessage", "JWT驗證碼錯誤");
		}
		
		Blob blob = (Blob) ((Map) resultList.get(0)).get("SECRET_KEY");
		int blobLength = (int) blob.length();
		byte[] sKeyData = blob.getBytes(1, blobLength);
    	
		try {
			Claims claims = Jwts.parserBuilder()
							.setSigningKey(sKeyData)
							.build()
							.parseClaimsJws(jwtString)
							.getBody();
			
			logger.info("claims = " + claims.toString());
		} catch(ExpiredJwtException e) {
			return new GenericMap()
					.put("RtnCode", "E996")
					.put("RtnMessage", "JWT驗證碼已超過有效期間");
		} catch(SignatureException e) {
			return new GenericMap()
					.put("RtnCode", "E997")
					.put("RtnMessage", "JWT驗證簽章錯誤(JWT validity cannot be asserted and should not be trusted.)");
		}
		
		return null;
	}
}
