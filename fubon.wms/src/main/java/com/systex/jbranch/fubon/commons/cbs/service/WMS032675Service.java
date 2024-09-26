package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._000400_032041DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._085081_085105DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._000400_032041.CBS032041OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 此服務將代替原本的 FC032675 ESB 電文 （Note: 原本的 FC032675 ESB 電文改走 CBS 路線）
 */
@Service
public class WMS032675Service {
	@Autowired
	private CBSService cbsService;
	@Autowired
	private MediatorService mediatorService;
	@Autowired
	private _085081_085105DAO _085081_085105dao;
	@Autowired
	private _000400_032041DAO _000400_032041dao;

	/**
	 * 10: 撈出所有客戶符合條件的帳號
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<WMS032675OutputVO> searchAcct(String custId) throws Exception {
		List<WMS032675OutputVO> result = new ArrayList();
		for (CBSUtilOutputVO each : _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId))) {
			gatherToWMS032675(result, each);
		}
		return result;
	}
	
	/**
	 * 10: 撈出所有客戶符合條件的帳號
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<WMS032675OutputVO> searchAcctForWMSHA001(String custId) throws Exception {
		List<WMS032675OutputVO> result = new ArrayList();
		for (CBSUtilOutputVO each : _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId))) {
			gatherToWMS032675ForWMSHA001(result, each);
		}
		return result;
	}

	/**
	 * 10: 撈出所有客戶符合條件的帳號
	 */
	public List<WMS032675OutputVO> filterAcctData(List<CBSUtilOutputVO> acctData) throws Exception {
		List<WMS032675OutputVO> result = new ArrayList();
		for (CBSUtilOutputVO each : acctData) {
			gatherToWMS032675(result, each);
		}
		return result;
	}

	private void gatherToWMS032675(List<WMS032675OutputVO> result, CBSUtilOutputVO each) {
		WMS032675OutputVO esbVO = new WMS032675OutputVO();
		List details = new ArrayList();
		for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
			if (!cbsDtl.getWA_X_CURR().trim().equals("XXX") && isNotBlank(cbsDtl.getWA_X_CURR().trim()) ) {
				WMS032675OutputDetailsVO esbDtl = _085081_085105AcctTransfer(cbsDtl);
				if (isNotBlank(esbDtl.getBUSINESS_CODE()))
					details.add(esbDtl);
			}
		}
		esbVO.setDetails(details);
		result.add(esbVO);
	}
	
	private void gatherToWMS032675ForWMSHA001(List<WMS032675OutputVO> result, CBSUtilOutputVO each) {
		WMS032675OutputVO esbVO = new WMS032675OutputVO();
		List details = new ArrayList();
		for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
			if (!cbsDtl.getWA_X_CURR().trim().equals("XXX") && isNotBlank(cbsDtl.getWA_X_CURR().trim()) ) {
				WMS032675OutputDetailsVO esbDtl = _085081_085105AcctTransferForWMSHA001(cbsDtl);
				if (isNotBlank(esbDtl.getBUSINESS_CODE()))
					details.add(esbDtl);
			}
		}
		esbVO.setDetails(details);
		result.add(esbVO);
	}

	/**
	 * 主要是AVAILABLE_AMT_BAL有新邏輯
	 */
	private WMS032675OutputDetailsVO _085081_085105AcctTransferForWMSHA001(CBS085105OutputDetailsVO cbsDtl) {
		WMS032675OutputDetailsVO esbDtl = new WMS032675OutputDetailsVO();

		esbDtl.setACT_NO(cbsService.checkAcctLength(cbsDtl.getWA_X_ACCTNO()));
		esbDtl.setOPEN_DATE(cbsDtl.getWA_X_DATE_OPEN());

		esbDtl.setBUSINESS_CODE(getBusinessCode(cbsDtl));
		esbDtl.setCURRENCY(cbsDtl.getWA_X_CURR());	
//		esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17, 18) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17)); //可用餘額

		if(esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("1")){ //台活
			esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 51, 52) + substring(cbsDtl.getOthers(), 34, 51));
		}else if (!esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("1")){ //外活
			esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 33, 34) + substring(cbsDtl.getOthers(), 16, 33));
		}else if (esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("2")){ //台支
			esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 51, 52) + substring(cbsDtl.getOthers(), 34, 51));
		}else if (esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("3")){ //台定
			esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 239, 256));
		}else if (!esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("3")){ //外定
			esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 239, 256));
		}
		
		if(esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("1")){ //台活帳戶餘額
			esbDtl.setTOTAL_SUM(substring(cbsDtl.getOthers(), 51, 52) + substring(cbsDtl.getOthers(), 34, 51)); // 帳戶餘額
		}else if (!esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("1")){ //外活帳戶餘額
			esbDtl.setTOTAL_SUM(substring(cbsDtl.getOthers(), 33, 34) + substring(cbsDtl.getOthers(), 16, 33));
		}else if (esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("2")){
			esbDtl.setTOTAL_SUM(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17));
		}
		
		//#0695
		esbDtl.setDIGIT_TYPE(substring(cbsDtl.getOthers(), 144, 145));
		return esbDtl;
	}
	
	/**
	 * 規格書轉換（所有客戶符合條件的帳號） _085081_085105 Detail to WMS032675 Detail
	 * 20200303_CBS_#79006_基金單筆申購透支帳戶餘額與實際數不符 加入外幣餘額判斷
	 * 20200310_CBS_麗文_#79651_海外ETF可投資餘額有問題
	 * 20200310_CBS_麗文_#79666_海外ETF股票盤中可投資金額有誤
	 * 20210720_#0695_偲偲_實體戶判斷
	 */
	private WMS032675OutputDetailsVO _085081_085105AcctTransfer(CBS085105OutputDetailsVO cbsDtl) {
		WMS032675OutputDetailsVO esbDtl = new WMS032675OutputDetailsVO();

		esbDtl.setACT_NO(cbsService.checkAcctLength(cbsDtl.getWA_X_ACCTNO()));
		esbDtl.setOPEN_DATE(cbsDtl.getWA_X_DATE_OPEN());

		esbDtl.setBUSINESS_CODE(getBusinessCode(cbsDtl));
		esbDtl.setCURRENCY(cbsDtl.getWA_X_CURR());	
		esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17, 18) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17)); //可用餘額

		
		if(esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("1")){ //台活帳戶餘額
			esbDtl.setTOTAL_SUM(substring(cbsDtl.getOthers(), 51, 52) + substring(cbsDtl.getOthers(), 34, 51)); // 帳戶餘額
		}else if (!esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("1")){ //外活帳戶餘額
			esbDtl.setTOTAL_SUM(substring(cbsDtl.getOthers(), 33, 34) + substring(cbsDtl.getOthers(), 16, 33));
		}else if (esbDtl.getCURRENCY().equals("TWD") && esbDtl.getBUSINESS_CODE().equals("2")){
			esbDtl.setTOTAL_SUM(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17));
		}
		
		//#0695
		esbDtl.setDIGIT_TYPE(substring(cbsDtl.getOthers(), 144, 145));
		return esbDtl;
	}

	private String getBusinessCode(CBS085105OutputDetailsVO cbsDtl) {
		// 判斷支存&&台幣
		if (mediatorService.isCheckTW(cbsDtl))
			return "2";

		// 存款 && 活存
		if (mediatorService.isCurrentDepositTW(cbsDtl) || mediatorService.isCurrentDepositFC(cbsDtl))
			return "1";

		// 存款 && 定存
		if (mediatorService.isTimedDepositTW(cbsDtl) || mediatorService.isTimedDepositFC(cbsDtl))
			return "3";

		return "";
	}

	/**
	 * 20: 台幣支存
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<WMS032675OutputVO> searchCheckTW(String custId) throws Exception {
		List result = new ArrayList();

		for (CBSUtilOutputVO each : _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId))) {
			List details = new ArrayList();
			WMS032675OutputVO esbVO = new WMS032675OutputVO();
			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				if (isBlank(cbsDtl.getWA_X_ACCTNO()))
					continue;
				if (!mediatorService.isCheckTW(cbsDtl))
					continue;

				WMS032675OutputDetailsVO esbDtl = new WMS032675OutputDetailsVO();
				esbDtl.setPERSON_ID(custId);
				_085081_085105checkTWTransfer(cbsDtl, esbDtl);
				_000400_032041checkTWTransfer(cbsDtl, esbDtl);

				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}

		return result;
	}

	/**
	 * 規格書轉換（台幣支存） _000400_032041 to WMS032675 Detail
	 */
	private void _000400_032041checkTWTransfer(CBS085105OutputDetailsVO acctDetail, WMS032675OutputDetailsVO esbDtl) throws Exception {
		CBS032041OutputVO cbsVO = get000400_032041Data(acctDetail);
		if (cbsVO == null)
			return;

		esbDtl.setREFUND_STS(cbsVO.getChqDisHonCnt1());
		esbDtl.setALTERATION(cbsVO.getLasFinDate1());
	}

	/**
	 * 規格書轉換（台幣支存） _085081_085105 Detail to WMS032675 Detail
	 */
	private WMS032675OutputDetailsVO _085081_085105checkTWTransfer(CBS085105OutputDetailsVO cbsDtl, WMS032675OutputDetailsVO esbDtl) {
		// 此段應該沒用到，先註解
		if (mediatorService.isCurrentDeposit(cbsDtl)) {
			esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 51, 52) + substring(cbsDtl.getOthers(), 34, 51));
			esbDtl.setACCT_TYPE("01");
		} else if (mediatorService.isTimedDeposit(cbsDtl)) {
			esbDtl.setACCT_TYPE("02");
		} else if ("LON".equals(cbsDtl.getWA_X_SYS())) {
			esbDtl.setACCT_TYPE("03");
		} else {
			esbDtl.setACCT_TYPE("04");
		}

		esbDtl.setBRANCH_NBR(cbsService.checkBraLength(cbsDtl.getWA_X_BRANCH_NO()));
		esbDtl.setACCT_NBR(cbsService.checkAcctLength(cbsDtl.getWA_X_ACCTNO()));
		esbDtl.setACCT_OPEN_DATE(cbsDtl.getWA_X_DATE_OPEN());
		esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 51, 52) + substring(cbsDtl.getOthers(), 34, 51));

		if (!isBlank(cbsDtl.getWA_X_CURR_BALANCE())) {
			esbDtl.setCURRENT_AMT_BAL(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17));
		}
		return esbDtl;
	}

	/**
	 * 30: 台幣活存
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<WMS032675OutputVO> searchCurrentDepositTW(String custId) throws Exception {
		List result = new ArrayList();
		for (CBSUtilOutputVO each : _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId))) {
			WMS032675OutputVO esbVO = new WMS032675OutputVO();

			List details = new ArrayList();
			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				if (isBlank(cbsDtl.getWA_X_ACCTNO()))
					continue;
				if (!mediatorService.isCurrentDepositTW(cbsDtl))
					continue;

				WMS032675OutputDetailsVO esbDtl = new WMS032675OutputDetailsVO();
				esbDtl.setPERSON_ID(custId);
				_085081_085105CurrentDepositTWTransfer(cbsDtl, esbDtl);
				_000400_032041CurrentDepositTWTransfer(cbsDtl, esbDtl);
				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}

		return result;
	}

	/**
	 * 規格書轉換（台幣活存） _085081_085105 Detail to WMS032675 Detail
	 */
	private void _000400_032041CurrentDepositTWTransfer(CBS085105OutputDetailsVO cbsDtl, WMS032675OutputDetailsVO esbDtl) throws Exception {
		CBS032041OutputVO cbsVO = get000400_032041Data(cbsDtl);
		if (cbsVO == null)
			return;

		esbDtl.setACCT_STATUS("T".equals(cbsVO.getProdAcctInd1()) ? "Y" : "N");
		esbDtl.setALTERATION(cbsVO.getLasFinDate1());
	}

	/**
	 * 取得 _000400_032041dao search data
	 **/
	private CBS032041OutputVO get000400_032041Data(CBS085105OutputDetailsVO cbsDtl) throws Exception {
		List<CBSUtilOutputVO> data = _000400_032041dao.search(mediatorService.getCbs000400InputVO(cbsDtl));
		if (isEmpty(data))
			return null;
		return data.get(0).getCbs032041OutputVO();
	}

	/**
	 * 規格書轉換（台幣活存） _085081_085105 Detail to WMS032675 Detail
	 */
	private void _085081_085105CurrentDepositTWTransfer(CBS085105OutputDetailsVO cbsDtl, WMS032675OutputDetailsVO esbDtl) {
		esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getOthers(), 51, 52) + substring(cbsDtl.getOthers(), 34, 51));
		esbDtl.setPROJECT_CODE(substring(cbsDtl.getOthers(), 12, 16));
		esbDtl.setBRANCH_NBR(cbsService.checkBraLength(cbsDtl.getWA_X_BRANCH_NO()));
		esbDtl.setACCT_NBR(cbsService.checkAcctLength(cbsDtl.getWA_X_ACCTNO()));
		esbDtl.setACCT_OPEN_DATE(cbsDtl.getWA_X_DATE_OPEN());
		if (length(cbsDtl.getWA_X_CURR_BALANCE()) > 15) {
			esbDtl.setCURRENT_AMT_BAL(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17));
		}
	}

	/**
	 * 40: 外幣活存
	 *
	 * @param custId
	 *            客戶 Id
	 * @return
	 */
	public List<WMS032675OutputVO> searchCurrentDepositFC(String custId) throws Exception {
		List result = new ArrayList();
		for (CBSUtilOutputVO each : _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId))) {
			WMS032675OutputVO esbVO = new WMS032675OutputVO();

			List details = new ArrayList();
			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				if (isBlank(cbsDtl.getWA_X_ACCTNO()))
					continue;
				if (!mediatorService.isCurrentDepositFC(cbsDtl))
					continue;

				WMS032675OutputDetailsVO esbDtl = new WMS032675OutputDetailsVO();
				esbDtl.setPERSON_ID(custId);
				_085081_085105CurrentDepositFCTransfer(cbsDtl, esbDtl);
				_000400_032041CurrentDepositFCTransfer(cbsDtl, esbDtl);

				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}

		return result;
	}

	/**
	 * 規格書轉換（外幣活存） _000400_032041 to WMS032675 Detail
	 */
	private void _000400_032041CurrentDepositFCTransfer(CBS085105OutputDetailsVO cbsDtl, WMS032675OutputDetailsVO esbDtl) throws Exception {
		CBS032041OutputVO cbsVO = get000400_032041Data(cbsDtl);
		esbDtl.setACCT_STATUS("T".equals(cbsVO.getProdAcctInd1()) ? "Y" : "N");
		esbDtl.setALTERATION(cbsVO.getLasFinDate1());
	}

	/**
	 * 規格書轉換（外幣活存） _085081_085105 Detail to WMS032675 Detail
	 */
	private void _085081_085105CurrentDepositFCTransfer(CBS085105OutputDetailsVO cbsDtl, WMS032675OutputDetailsVO esbDtl) {
		esbDtl.setCURRENT_AMT_BAL_O(substring(cbsDtl.getOthers(), 33, 34) + substring(cbsDtl.getOthers(), 16, 33));
		esbDtl.setBRANCH_NBR(cbsService.checkBraLength(cbsDtl.getWA_X_BRANCH_NO()));
		esbDtl.setACCT_NBR(cbsService.checkAcctLength(cbsDtl.getWA_X_ACCTNO()));
		esbDtl.setACCT_OPEN_DATE(cbsDtl.getWA_X_DATE_OPEN());
		esbDtl.setCURRENCY_CD(cbsDtl.getWA_X_CURR());
		if (length(cbsDtl.getWA_X_CURR_BALANCE()) > 15) {
			esbDtl.setAVAILABLE_AMT_BAL(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17));
		}
	}
}
