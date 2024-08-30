package com.systex.jbranch.fubon.commons.esb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.crm814.CRM814InputVO;
import com.systex.jbranch.fubon.commons.esb.dao.HD00070000DAO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;


/**
 * 帳戶明細歷史查詢
 * @author sam
 *
 */
@Service
public class HD00070000Service {
	@Autowired
	HD00070000DAO hd000070000dao;
	
	public List<ESBUtilOutputVO> search(CRM814InputVO inputVO) throws Exception{
		return hd000070000dao.search(inputVO);
		
	}


}
