package com.systex.jbranch.fubon.commons.cbs.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002023InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002024InputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002024OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002024OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.tx.stuff.Worker;
import com.systex.jbranch.fubon.commons.tx.traffic.Cbs;

/**
 * 電文 002023 發送至 CBS 回傳 002024
 */
@Repository
public class _002024_002024DAO {

	/**
	 * 查詢循環型貸款詳細資料
	 *
	 * @param loanAcct
	 *            放款透支帳號
	 * @param curr
	 *            幣別
	 */
	public List<CBSUtilOutputVO> search(CBS002024OutputVO cbs002024OutputVO) throws Exception {
		List<CBSUtilOutputVO> list = new ArrayList();
		Worker.call()
        .assign(Worker.CBS, "002024")
        .setRequest(Cbs.createRequestVO()
                .setPickUpId("002024")
                .setModule("_002024_002024DAO.search")
                .setTxHeadVO(Cbs.createTxHeadVO())
                .setCbs002024InputVO(cbs002024(cbs002024OutputVO)))
        .work()
        .getResponse(list);
return list;
	}

	/**
	 * 電文 InputVO
	 **/
	private CBS002024InputVO cbs002024(CBS002024OutputVO cbs002024OutputVO) {
		
		return getData(cbs002024OutputVO);

	}
	
	public CBS002024InputVO getData(CBS002024OutputVO cbs002024OutputVO) {
		CBS002024InputVO cbs002024InputVO = new CBS002024InputVO();
		int i = -1;
		cbs002024InputVO.setAcctno1(cbs002024OutputVO.getAcctno1());
		cbs002024InputVO.setCustname1(cbs002024OutputVO.getCustname1());
		cbs002024InputVO.setIdno1(cbs002024OutputVO.getIdno1());
		cbs002024InputVO.setIntcalcbasis1(cbs002024OutputVO.getIntcalcbasis1());
		cbs002024InputVO.setAccttypedesc1(cbs002024OutputVO.getAccttypedesc1());
		cbs002024InputVO.setOdlimit1(cbs002024OutputVO.getOdlimit1());
		cbs002024InputVO.setAvailbal1(cbs002024OutputVO.getAvailbal1());
		cbs002024InputVO.setOdbal1(cbs002024OutputVO.getOdbal1());
		cbs002024InputVO.setCurrbal1(cbs002024OutputVO.getCurrbal1());
		cbs002024InputVO.setOdavail1(cbs002024OutputVO.getOdavail1());
		cbs002024InputVO.setCrintincr1(cbs002024OutputVO.getCrintincr1());
		cbs002024InputVO.setDrintincr1(cbs002024OutputVO.getDrintincr1());
		cbs002024InputVO.setCrintaccr1(cbs002024OutputVO.getCrintaccr1());
		cbs002024InputVO.setDrintaccr1(cbs002024OutputVO.getDrintaccr1());
		cbs002024InputVO.setOption01(cbs002024OutputVO.getOption01());
		cbs002024InputVO.setSigned41(cbs002024OutputVO.getSigned41());
		cbs002024InputVO.setMorerecsmsg1(cbs002024OutputVO.getMorerecsmsg1());
		cbs002024InputVO.setDefInteger1(cbs002024OutputVO.getDefInteger1());
		cbs002024InputVO.setDefInteger2(cbs002024OutputVO.getDefInteger2());
		cbs002024InputVO.setDefInteger3(cbs002024OutputVO.getDefInteger3());
		cbs002024InputVO.setDefInteger4(cbs002024OutputVO.getDefInteger4());
		cbs002024InputVO.setDefInteger5(cbs002024OutputVO.getDefInteger5());
		cbs002024InputVO.setDefInteger6(cbs002024OutputVO.getDefInteger6());
		cbs002024InputVO.setDefInteger7(cbs002024OutputVO.getDefInteger7());
		cbs002024InputVO.setCreditStatus(cbs002024OutputVO.getCreditStatus());
		cbs002024InputVO.setPageNum(cbs002024OutputVO.getPageNum());
		cbs002024InputVO.setDrExcessIntDue(cbs002024OutputVO.getDrExcessIntDue());
		cbs002024InputVO.setDrIntDue(cbs002024OutputVO.getDrIntDue());
		cbs002024InputVO.setLastCreditDate(cbs002024OutputVO.getLastCreditDate());
		cbs002024InputVO.setTotDeferAmt(cbs002024OutputVO.getTotDeferAmt());
		cbs002024InputVO.setTotMinDueAmt(cbs002024OutputVO.getTotMinDueAmt());
		cbs002024InputVO.setODSysType(cbs002024OutputVO.getODSysType());
		cbs002024InputVO.setCurrType(cbs002024OutputVO.getCurrType());

		for (CBS002024OutputDetailsVO detail : cbs002024OutputVO.getDetails()) {
			i = i + 1;
			switch (i) {
			case 0:
				cbs002024InputVO.setSelect1(detail.getSelect1());
				cbs002024InputVO.setOdno1(detail.getOdno1());
				cbs002024InputVO.setEffdate1(detail.getEffdate1());
				cbs002024InputVO.setExpdate1(detail.getExpdate1());
				cbs002024InputVO.setLimit2(detail.getLimit2());
				cbs002024InputVO.setBalance1(detail.getBalance1());
				cbs002024InputVO.setRate1(detail.getRate1());
				cbs002024InputVO.setInterest1(detail.getInterest1());
				cbs002024InputVO.setHighbal1(detail.getHighbal1());
				cbs002024InputVO.setType2(detail.getType2());
				cbs002024InputVO.setStat1(detail.getStat1());
				cbs002024InputVO.setODIntCalcMeth1(detail.getODIntCalcMeth1());
				break;
			case 1:
				cbs002024InputVO.setSelect2(detail.getSelect1());
				cbs002024InputVO.setOdno2(detail.getOdno1());
				cbs002024InputVO.setEffdate2(detail.getEffdate1());
				cbs002024InputVO.setExpdate2(detail.getExpdate1());
				cbs002024InputVO.setLimit3(detail.getLimit2());
				cbs002024InputVO.setBalance2(detail.getBalance1());
				cbs002024InputVO.setRate2(detail.getRate1());
				cbs002024InputVO.setInterest2(detail.getInterest1());
				cbs002024InputVO.setHighbal2(detail.getHighbal1());
				cbs002024InputVO.setType3(detail.getType2());
				cbs002024InputVO.setStat2(detail.getStat1());
				cbs002024InputVO.setODIntCalcMeth2(detail.getODIntCalcMeth1());
				break;
			case 2:
				cbs002024InputVO.setSelect3(detail.getSelect1());
				cbs002024InputVO.setOdno3(detail.getOdno1());
				cbs002024InputVO.setEffdate3(detail.getEffdate1());
				cbs002024InputVO.setExpdate3(detail.getExpdate1());
				cbs002024InputVO.setLimit4(detail.getLimit2());
				cbs002024InputVO.setBalance3(detail.getBalance1());
				cbs002024InputVO.setRate3(detail.getRate1());
				cbs002024InputVO.setInterest3(detail.getInterest1());
				cbs002024InputVO.setHighbal3(detail.getHighbal1());
				cbs002024InputVO.setType4(detail.getType2());
				cbs002024InputVO.setStat3(detail.getStat1());
				cbs002024InputVO.setODIntCalcMeth3(detail.getODIntCalcMeth1());
				break;
			case 3:
				cbs002024InputVO.setSelect4(detail.getSelect1());
				cbs002024InputVO.setOdno4(detail.getOdno1());
				cbs002024InputVO.setEffdate4(detail.getEffdate1());
				cbs002024InputVO.setExpdate4(detail.getExpdate1());
				cbs002024InputVO.setLimit5(detail.getLimit2());
				cbs002024InputVO.setBalance4(detail.getBalance1());
				cbs002024InputVO.setRate4(detail.getRate1());
				cbs002024InputVO.setInterest4(detail.getInterest1());
				cbs002024InputVO.setHighbal4(detail.getHighbal1());
				cbs002024InputVO.setType5(detail.getType2());
				cbs002024InputVO.setStat4(detail.getStat1());
				cbs002024InputVO.setODIntCalcMeth4(detail.getODIntCalcMeth1());
				break;
			case 4:
				cbs002024InputVO.setSelect5(detail.getSelect1());
				cbs002024InputVO.setOdno5(detail.getOdno1());
				cbs002024InputVO.setEffdate5(detail.getEffdate1());
				cbs002024InputVO.setExpdate5(detail.getExpdate1());
				cbs002024InputVO.setLimit6(detail.getLimit2());
				cbs002024InputVO.setBalance5(detail.getBalance1());
				cbs002024InputVO.setRate5(detail.getRate1());
				cbs002024InputVO.setInterest5(detail.getInterest1());
				cbs002024InputVO.setHighbal5(detail.getHighbal1());
				cbs002024InputVO.setType6(detail.getType2());
				cbs002024InputVO.setStat5(detail.getStat1());
				cbs002024InputVO.setODIntCalcMeth5(detail.getODIntCalcMeth1());
				break;
			case 5:
				cbs002024InputVO.setSelect6(detail.getSelect1());
				cbs002024InputVO.setOdno6(detail.getOdno1());
				cbs002024InputVO.setEffdate6(detail.getEffdate1());
				cbs002024InputVO.setExpdate6(detail.getExpdate1());
				cbs002024InputVO.setLimit7(detail.getLimit2());
				cbs002024InputVO.setBalance6(detail.getBalance1());
				cbs002024InputVO.setRate6(detail.getRate1());
				cbs002024InputVO.setInterest6(detail.getInterest1());
				cbs002024InputVO.setHighbal6(detail.getHighbal1());
				cbs002024InputVO.setType7(detail.getType2());
				cbs002024InputVO.setStat6(detail.getStat1());
				cbs002024InputVO.setODIntCalcMeth6(detail.getODIntCalcMeth1());
				break;
			case 6:
				cbs002024InputVO.setSelect7(detail.getSelect1());
				cbs002024InputVO.setOdno7(detail.getOdno1());
				cbs002024InputVO.setEffdate7(detail.getEffdate1());
				cbs002024InputVO.setExpdate7(detail.getExpdate1());
				cbs002024InputVO.setLimit8(detail.getLimit2());
				cbs002024InputVO.setBalance7(detail.getBalance1());
				cbs002024InputVO.setRate7(detail.getRate1());
				cbs002024InputVO.setInterest7(detail.getInterest1());
				cbs002024InputVO.setHighbal7(detail.getHighbal1());
				cbs002024InputVO.setType8(detail.getType2());
				cbs002024InputVO.setStat7(detail.getStat1());
				cbs002024InputVO.setODIntCalcMeth7(detail.getODIntCalcMeth1());
				break;
			}
		}

		cbs002024InputVO.setOption01("96");  // _002023_002024DAO的setOverdraftOption1("09"); 送96    05的話送98
		return cbs002024InputVO;
	}
}
