package com.systex.jbranch.fubon.commons.esb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.sot330.SOT330InputVO;
import com.systex.jbranch.fubon.commons.esb.dao.NJBRVC3DAO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;

/**
 * 海外債成交結果查詢
 * SamTu
 * 2020.11.05
 */
@Service
public class NJBRVC3Service {
	@Autowired
	NJBRVC3DAO njbrvc3dao;

	public List<ESBUtilOutputVO> search(SOT330InputVO inputVO) throws Exception{
		return njbrvc3dao.search(inputVO);
	}

}
