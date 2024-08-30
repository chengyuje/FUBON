package com.systex.jbranch.fubon.commons.esb.dao;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbCrmCons.HD00070000;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.server.fps.crm814.CRM814InputVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.hd00070000.HD00070000InputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Esb;


/**
 * 20200721_CBS_麗文_客戶首頁_明細查詢_改取電文邏輯_HD00070000
 * 帳戶明細歷史查詢
 * 暫時只有處理 支存跟活存
 * 另外根據20200114_CBS_麗文_CRM814明細查詢排除定存  目前應該也沒有定存帳號可以選
 * @author sam
 *
 */
@Repository
public class HD00070000DAO {
	@Autowired
	private CBSService cbsService;

	public List<ESBUtilOutputVO> search(CRM814InputVO inputVO) throws Exception {

		List<ESBUtilOutputVO> list = new ArrayList();
		Worker.call()
		.assign(Worker.ESB, HD00070000)
		.setRequest(
				Esb.createRequestVO()
				.setModule("HD00070000DAO.search")
				.setTxHeadVO(Esb.createTxHeadVO()
						.setHFMTID("1"))
						.setHd00070000InputVO(getHD00070000InputVO(inputVO)))
						.work()
						.getResponse(list);
		return list;
	}

	public HD00070000InputVO getHD00070000InputVO(CRM814InputVO inputVO) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// body
		HD00070000InputVO txBody = new HD00070000InputVO();
		txBody.setACNO_SA(inputVO.getAccount());
//		txBody.setTX_TYPE(getTxType(inputVO.getAccount()));//用帳號來判斷怎麼上送Txtype 
		txBody.setTXDATES(sdf.format(inputVO.getsCreDate()));
		txBody.setTXDATEE(sdf.format(inputVO.geteCreDate()));
		// txBody.setTXDATES("20190706");
		// txBody.setTXDATEE("20190706");
		txBody.setITEM("1");
		txBody.setPAGE_NUM("1");
		txBody.setPAGE_SIZE("50");
//		txBody.setJRNL_NO("000000000");
//		txBody.setRPT_NO("         ");
//		txBody.setRPT_BRH("00200");
		
		return txBody;
	}

	/**
	 * 20200721_CBS_麗文_客戶首頁_明細查詢_改取電文邏輯_HD00070000
	 * 支存回傳空白" "
	 * 活存回傳"1"
	 * @param account
	 * @return
	 */
	private String getTxType(String account) {
		String[] oldChequeAcctList = {"101","131","331","531","105"};
		String[] newChequeAcctList = {"91","92","93","94","95","96","97","99"};
		
		//支存判斷
		if("0".equals(account.substring(0,1))){ //舊帳號
			if(Arrays.asList(oldChequeAcctList).contains(account.substring(5, 8))){
				return " ";
			}
		}else if ("82010".equals(account.substring(0, 5))){ //新帳號
			if(account.substring(5,7).equals("98")){
				if(!account.substring(7, 8).equals("0")){
					return " ";
				}
			}
			if(Arrays.asList(newChequeAcctList).contains(account.substring(5,7))){
				return " ";
			}
		}
		
		return "1"; //判斷完支存剩下視為活存
	}

	/*
	 * 帳號上送可以吃14碼，故這個功能淘汰
	 * @param acct
	 * @return
	 */
	private String changeAcctLength(String acct) {
		String add = "";
		if (acct.length() < 16) {
			for (int i = 0; i < 16 - acct.length(); i++) {
				add += "0";
			}
			return add + acct;
		} else {
			return acct;
		}
	}

}
