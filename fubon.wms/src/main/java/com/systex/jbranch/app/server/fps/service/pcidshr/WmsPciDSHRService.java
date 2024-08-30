package com.systex.jbranch.app.server.fps.service.pcidshr;

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
 * 保險作業管理系統(產險Property and Casualty Insurance)與北富銀保險資料檢核需求Web Service
 */
@Service("WmsPciDSHRService")
public class WmsPciDSHRService extends BizLogic implements WmsPciDSHRServiceInf{
	@Autowired @Qualifier("WmsPciDSHRDao")
	private WmsPciDSHRDaoInf wmsPciDSHRDao;
	
	@Autowired
	private WmsInsSigService wmsInsSigService;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	

	/**
	 * 保險作業管理系統(產險Property and Casualty Insurance)檢核
	 */
	public GenericMap validatePciData(GenericMap paramGmap) throws Exception {
		
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
			signList = wmsPciDSHRDao.validatePciData(paramGmap);
			
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
