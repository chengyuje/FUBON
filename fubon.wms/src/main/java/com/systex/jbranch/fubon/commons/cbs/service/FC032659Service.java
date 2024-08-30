package com.systex.jbranch.fubon.commons.cbs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._085081_085105DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032659.FC032659OutputVO;

/**
 * 查詢：網銀取得戶名電文 
 * 20200319_CBS_麗文_下單_網銀快速下單出錯  >> 改用067050 - 067108
 *
 * @return
 */
@Service
public class FC032659Service {
   
	@Autowired
    private CBSService cbsService;
	@Autowired 
	private _085081_085105DAO _085081_085105dao;


	public FC032659OutputVO search(SOT701InputVO sot701InputVO) throws Exception{
		FC032659OutputVO esbVO = new FC032659OutputVO();
		
		List<CBSUtilOutputVO> cbsListVO = _085081_085105dao.search(sot701InputVO.getCustID(), cbsService.getCBSIDCode(sot701InputVO.getCustID()));
		
		for(CBSUtilOutputVO cbsVO : cbsListVO){
			for(CBS085105OutputDetailsVO detailVO : cbsVO.getCbs085105OutputVO().getDetails()){
				if(sot701InputVO.getDebitAcct().equals(cbsService.checkAcctLength(detailVO.getWA_X_ACCTNO()))){
					esbVO.setCOD(detailVO.getWA_X_ACCT_SUBID());
					return esbVO;
				}
			}
		}
		
		esbVO.setCOD("0000");
		return esbVO;
		
	}
	

}
