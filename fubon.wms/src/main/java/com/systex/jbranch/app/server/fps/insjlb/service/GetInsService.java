package com.systex.jbranch.app.server.fps.insjlb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.systex.jbranch.app.server.fps.cmjlb210.CMJLB210;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.insjlb.dao.InsjlbDaoInf;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

@SuppressWarnings("unchecked")
public class GetInsService {
	@Autowired @Qualifier("insjlbDao")
	private InsjlbDaoInf insjlbDao;
	
	@Autowired @Qualifier("cmjlb210")
	private CMJLB210 cmjlb210;
	
	@Resource(name = "ins810")
	private INS810 ins810;	

	@Resource(name = "GetOldItemListService.rePattern1")
	private Map<String , String> rePattern;
		
	public <T>T getBean(String name) {
		try {
			return (T)PlatformContext.getBean(name);
		} catch (JBranchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**查詢行內外保單保單整合的資料*/
	public Map<String , List<Map<String , Object>>> inOutBuyToLstInsData(INS810InputVO ins810inputvo) throws JBranchException{
		Map<String , List<Map<String , Object>>> result = new HashMap<String , List<Map<String , Object>>>();
		ins810inputvo.setType("out");
		result.put("OUT_BUY", ins810.queryInOutBuySinglePolicy(ins810inputvo));
		ins810inputvo.setType("in");
		result.put("IN_BUY", ins810.queryInOutBuySinglePolicy(ins810inputvo));
		return result;
	}

	public INS810 getIns810() {
		return ins810;
	}

	public void setIns810(INS810 ins810) {
		this.ins810 = ins810;
	}

	public Map<String, String> getRePattern() {
		return rePattern;
	}

	public void setRePattern(Map<String, String> rePattern) {
		this.rePattern = rePattern;
	}
	
	public InsjlbDaoInf getInsjlbDao() {
		return insjlbDao;
	}

	public void setInsjlbDao(InsjlbDaoInf insjlbDao) {
		this.insjlbDao = insjlbDao;
	}

	public CMJLB210 getCmjlb210() {
		return cmjlb210;
	}

	public void setCmjlb210(CMJLB210 cmjlb210) {
		this.cmjlb210 = cmjlb210;
	}
}
