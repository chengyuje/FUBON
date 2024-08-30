package com.systex.jbranch.fubon.commons.esb.service;

import com.systex.jbranch.fubon.commons.esb.dao.AML004DAO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.aml004.AML004OutputVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * 防洗錢註記（AML）
 */
@Service
public class AML004Service {
    @Autowired
    private AML004DAO aml004dao;

    public AML004OutputVO search(String custID) throws Exception {
    	try{
    		List<ESBUtilOutputVO> data = aml004dao.search(custID);
    		
    		if (isEmpty(data)) return null;
            return aml004Transfer(data.get(0).getAml004OutputVO());
    	} catch (Exception e){
    		return null;
    	}
        

        
    }

    private AML004OutputVO aml004Transfer(AML004OutputVO aml004OutputVO) {
        Map<String, String> map = new HashMap();
        map.put("H", "高"); // 高風險
        map.put("M", "中"); // 中風險
        map.put("L", "低"); // 低風險
        aml004OutputVO.setRiskRanking(map.get(trim(aml004OutputVO.getRiskRanking())));
        return aml004OutputVO;
    }
}
