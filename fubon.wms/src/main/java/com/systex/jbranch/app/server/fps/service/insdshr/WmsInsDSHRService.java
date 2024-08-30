package com.systex.jbranch.app.server.fps.service.insdshr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCHVO;
import com.systex.jbranch.app.server.fps.service.inssig.WmsInsSigService;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

/***
 * 富壽與北富銀資料共享以減少新契約照會需求Web Service
 */
@Service("WmsInsDSHRService")
public class WmsInsDSHRService extends BizLogic implements WmsInsDSHRServiceInf{
	@Autowired @Qualifier("WmsInsDSHRDao")
	private WmsInsDSHRDaoInf wmsInsDSHRDao;
	
	@Autowired
	private WmsInsSigService wmsInsSigService;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	
	/**
	 * 人壽端回傳業管要保書暫存資料
	 */
	public GenericMap setCaseSaveData(GenericMap paramGmap) throws Exception {
		
		HttpServletRequest servletRequest = paramGmap.get(HttpServletRequest.class.getName());
		String headerAuth = servletRequest.getHeader("Authorization");
		
		if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
			return new GenericMap()
					.put("RtnCode", "E999")
					.put("RtnMessage", "No JWT token found in request headers");
	    }
		
		//檢核JTW
		String authToken = headerAuth.substring(7);
		GenericMap veriMap = wmsInsSigService.verityJWT(authToken);
		if(veriMap != null) return veriMap;
		
		GenericMap returnMap = new GenericMap();
		
		//案件編號
		String caseId = paramGmap.getNotNullStr("caseId");
		//上傳日期時間
		String uploadDatetime = paramGmap.getNotNullStr("uploadDatetime");
		//由理專身分證字號取得裡專資料005889：QTIyMDEyMzc4NQ==
		List<Map<String , Object>> empInfoList = wmsInsDSHRDao.getEmpInfo(new String(Base64.decode(paramGmap.getNotNullStr("empId"))));
				
		if(CollectionUtils.isEmpty(empInfoList) || 
				StringUtils.isEmpty(ObjectUtils.toString(empInfoList.get(0).get("EMP_ID"))) || 
				StringUtils.isEmpty(ObjectUtils.toString(empInfoList.get(0).get("DEPT_ID")))) {
			returnMap = new GenericMap()
						.put("RtnCode", "E003")
						.put("RtnMessage", "查無此理專資料");
		} else if(StringUtils.isBlank(caseId) || StringUtils.isBlank(uploadDatetime)){
			returnMap = new GenericMap()
						.put("RtnCode", "E001")
						.put("RtnMessage", "案件編號與上傳日期時間皆不得為空");
		} else if(wmsInsDSHRDao.isCaseIdExisted(caseId)){
	        returnMap = new GenericMap()
						.put("RtnCode", "E002")
						.put("RtnMessage", "此案件編號已存在");
		} else {
			//新增要保書暫存資料
			wmsInsDSHRDao.setCaseSaveData(caseId, uploadDatetime, 
											ObjectUtils.toString(empInfoList.get(0).get("EMP_ID")), 
											ObjectUtils.toString(empInfoList.get(0).get("DEPT_ID")));
			
			returnMap = new GenericMap()
						.put("RtnCode", "0000")
						.put("RtnMessage", "成功");	
		}
		
		return returnMap;
	}
	
	/**
	 * 人壽端取得行動要保書檢核
	 */
	public GenericMap validateInsData(GenericMap paramGmap) throws Exception {
		
		HttpServletRequest servletRequest = paramGmap.get(HttpServletRequest.class.getName());
		String headerAuth = servletRequest.getHeader("Authorization");
		
		if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
			return new GenericMap()
					.put("RtnCode", "E999")
					.put("RtnMessage", "No JWT token found in request headers");	
	    }
		
		//檢核JTW
		String authToken = headerAuth.substring(7);
		GenericMap veriMap = wmsInsSigService.verityJWT(authToken);
		if(veriMap != null) return veriMap;
		
		Map<String , Object> signList = new HashMap<String , Object>();
		try {
			//取得要保書檢核結果
			signList = wmsInsDSHRDao.validateInsData(paramGmap);
			
			return new GenericMap()
					.put("RtnCode", "0000")
					.put("RtnMessage", "成功")
					.put("Results", signList);	
		} catch(Exception e) {
			return new GenericMap()
					.put("RtnCode", "9999")
					.put("RtnMessage", e.getMessage())
					.put("Results", signList);	
		}
	}
	

}
