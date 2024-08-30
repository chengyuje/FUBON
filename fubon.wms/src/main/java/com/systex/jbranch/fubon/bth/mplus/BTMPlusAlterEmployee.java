package com.systex.jbranch.fubon.bth.mplus;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.mplus.MPlusAlterEmployeeInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusDelEmployeeInputVO;
import com.systex.jbranch.fubon.commons.mplus.MPlusEmpolyeeUtil;
import com.systex.jbranch.fubon.commons.mplus.MPlusResult;
import com.systex.jbranch.fubon.commons.mplus.MPlusResult.AlterParam;
import com.systex.jbranch.fubon.commons.mplus.check.MPlusCheckUtils;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("btmPlusAlterEmployee")
@Scope("prototype")
public class BTMPlusAlterEmployee extends BizLogic { 	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired@Qualifier("mPlusEmpolyeeUtil")
	private MPlusEmpolyeeUtil mPlusEmpolyeeUtil;
	
	@Autowired@Qualifier("mplusWebServiceDao")
	private MplusWebServiceDao mplusWebServiceDao;
	
	public void sendMPlusAlterEmployee(Object body, IPrimitiveMap<?> header) throws Exception {
		//設置參數
		String action = "add";
		String alterKey = "1";
		
		List<Map<String, Object>> list = null;
		MPlusResult res = null;
		
		//新進人員+今日職務異動後可覆核人員清單
		if(CollectionUtils.isEmpty(list = mplusWebServiceDao.queryInstalList())){
			logger.info("Mplus無人員異動");
		}

		//將人員逐筆打包送往MPlus
		for(Map<String, Object> map : list) {
			MPlusAlterEmployeeInputVO inputVO = new MPlusAlterEmployeeInputVO();
			inputVO.setAction(action);//add 新增員工
			inputVO.setAlterKey(alterKey);//By email
			inputVO.setName(map.get("EMP_NAME").toString());//員工姓名
			inputVO.setEmail(map.get("EMP_EMAIL_ADDRESS").toString());//員工Email
			
			logger.info("Mplus人員異動新增名單：" + inputVO.getEmail());
			res = mPlusEmpolyeeUtil.send2MPlusForPostNameValuePair(inputVO, new MPlusCheckUtils());

			if(res == null){
				logger.info("response is null");
				return;
			}
			
			//處理訊息
			logger.info(res.getText());
			logger.info(res.getResult());
		}
	}
	

	public void sendMPlusDelEmployee(Object body, IPrimitiveMap<?> header) throws Exception {
		List<Map<String, Object>> list = null;
		MPlusResult res = null;
		AlterParam[] alterParamAr = null;
		
		String alterKey = "1";//刪除員工資料的鍵值，1: By email，2: By 員工編號 
		StringBuffer allMemberMailAddr = new StringBuffer();//所有員工email地址
		
		if(CollectionUtils.isEmpty(list = mplusWebServiceDao.queryDelGroupList())){
			logger.info("Mplus無人員移除名單：");
		}

		//新進人員 + 今日職務異動後可覆核人員清單
		MPlusDelEmployeeInputVO inputVO = new MPlusDelEmployeeInputVO();
		inputVO.setAlterKey(alterKey);//刪除員工資料的鍵值By email
		
		//將人員逐筆打包送往MPlus
		for(Map<String, Object> map : list) {
			String email = ObjectUtils.toString(map.get("EMP_EMAIL_ADDRESS"));
			
			if(StringUtils.isNotBlank(email))
				allMemberMailAddr.append(email).append(",");
		}
		
		inputVO.setAlterParam(allMemberMailAddr.toString().replaceFirst(",$", ""));
		logger.info("Mplus移除名單：" + inputVO.getAlterParam());
		res = mPlusEmpolyeeUtil.send2MPlusForPostNameValuePair(inputVO, new MPlusCheckUtils());
		
		//處理訊息
		if(res == null){
			logger.info("response is null");
			return;
		}
		
		logger.info(res.getText());
		logger.info(res.getResult());
		
		wirterAlterParam("刪除成功名單:" , res.getSuccessList());
		wirterAlterParam("刪除失敗名單:" , res.getFailList());
	}
	
	public void wirterAlterParam(String name , AlterParam[] alterParamAr){
		if(alterParamAr == null || alterParamAr.length == 0)
			return;
		
		logger.info(name);
		
		for(AlterParam alterParam : alterParamAr){
			logger.info(alterParam.getAlterParam());
			logger.info(alterParam.getCause());
		}
	}
}
