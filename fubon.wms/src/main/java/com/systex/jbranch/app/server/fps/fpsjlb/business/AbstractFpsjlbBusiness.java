package com.systex.jbranch.app.server.fps.fpsjlb.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.app.server.fps.fpsjlb.dao.FpsjlbDaoInf;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;

abstract public class AbstractFpsjlbBusiness implements FpsjlbBusinessInf{
	@Autowired
	@Qualifier("fpsjlbDao")
	protected FpsjlbDaoInf fpsjlbDao;

	public FpsjlbDaoInf getFpsjlbDao() {
		return fpsjlbDao;
	}

	public void setFpsjlbDao(FpsjlbDaoInf fpsjlbDao) {
		this.fpsjlbDao = fpsjlbDao;
	}
		
	public static ResultObj checkErrorCode(ResultObj resultObj) throws Exception{
		String code = resultObj.getResultCode();
		 if("01".equals(code)) 
			throw new Exception("報酬率不可以小於-100%");
		else if("02".equals(code)) 
			throw new Exception("權重不可以小於0或等於0");
		else if("03".equals(code)) 
			throw new Exception("權重總合必需等於100%");
		else if("04".equals(code)) 
			throw new Exception("報酬率矩陣與權重資料不一致，無法計算");
		else if("11".equals(code)) 
			throw new Exception("標準差值小於0無法模擬");
		else if("34".equals(code)) 
			throw new Exception("投資期數小於0無法模擬");
		else if("99".equals(code)) 
			throw new Exception("系統發生錯誤，請洽系統管理員");
		 return resultObj;
	}
	
	public DataAccessManager getDataAccessManager(){
		return fpsjlbDao.getDataAccessManager();
	}
}
