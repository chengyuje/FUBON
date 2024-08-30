package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao.*;
import com.systex.jbranch.fubon.commons.cbs.vo._002023_002024.CBS002024OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._010400_032105.CBS032105OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017050_017000.CBS017000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._017079_017079.CBS017079OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060440_060441.CBS060441OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062141_062144.CBS062144OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062167_062167.CBS062167OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._062167_062167.CBS062167OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._085081_085105.CBS085105OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 此服務將代替原本的 SC120100 ESB 電文 （Note: 原本的 SC120100 ESB 電文改走 CBS 路線）
 */
@Service
public class SC120100Service {
	@Autowired
	private CBSService cbsService;
	@Autowired
	private MediatorService mediatorService;
	@Autowired
	private _085081_085105DAO _085081_085105dao;
	@Autowired
	private _010400_032105DAO _010400_032105dao;
	@Autowired
	private _017050_017000DAO _017050_017000dao;
	@Autowired
	private _062167_062167DAO _062167_062167dao;
	@Autowired
	private _062141_062144DAO _062141_062144dao;
	@Autowired
	private _002023_002024DAO _002023_002024dao;
	@Autowired
	private _017050_017200DAO _017050_017200dao;
	@Autowired
	private _060440_060441DAO _060440_060441dao;
	@Autowired
	private _017079_017079DAO _017079_017079dao;
	@Autowired
	private _002024_002024DAO _002024_002024dao;
	@Autowired
	private _017904_017904DAO _017904_017904dao;

	/**
	 * 所有貸款
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<SC120100OutputVO> searchAll(String custId) throws Exception {
		List result = new ArrayList();

		for (CBSUtilOutputVO each : getAcctData(custId)) {
			gatherToSC120100(result, each);
		}

		return result;
	}

	/**
	 * 使用客戶帳戶明細來取得所有貸款資訊
	 * @param acctData
	 * @return
	 * @throws Exception
	 */
	public List<SC120100OutputVO> searchAllByCustAcctList(List<CBSUtilOutputVO> acctData) throws Exception {
		List result = new ArrayList();

		for (CBSUtilOutputVO each : acctData) {
			gatherToSC120100(result, each);
		}

		return result;
	}

	private void gatherToSC120100(List result, CBSUtilOutputVO each) {
		SC120100OutputVO esbVO = new SC120100OutputVO();
		List details = new ArrayList();

		for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
			if (!fit(cbsDtl))
				continue;

			SC120100DetailOutputVO esbDtl = new SC120100DetailOutputVO();
			_085081_085105BasicTransfer(cbsDtl, esbDtl, each.getCbs085105OutputVO().getDefaultString1());

			// 就學貸款用 DOC_NO 欄位判斷學期別(四位數) 學期別異常的是就學貸款的主帳號所以排除
			esbDtl.setDOC_NO(cbsService.isStudentLoan(defaultString(trim(cbsDtl.getWA_X_ATYPE()))) ? substring(cbsDtl.getOthers(), 238, 242) : substring(cbsDtl.getOthers(), 220, 238));
			details.add(esbDtl);
		}
		esbVO.setDetails(details);
		result.add(esbVO);
	}

	/**
	 * 取得客戶帳戶資料
	 */
	private List<CBSUtilOutputVO> getAcctData(String custId) throws Exception {
		return _085081_085105dao.search(custId, cbsService.getCBSIDCode(custId));
	}

	/**
	 * 符合條件: 帳號不為空，且為貸款類型
	 **/
	private boolean fit(CBS085105OutputDetailsVO cbsVO) {
		return isNotBlank(cbsVO.getWA_X_ACCTNO()) && mediatorService.isLoan(cbsVO);
	}

	/**
	 * 規格書轉換（基本資訊）_085081_085105 to SC120100
	 **/
	private void _085081_085105BasicTransfer(CBS085105OutputDetailsVO cbsDtl, SC120100DetailOutputVO esbDtl, String idNo) {
		esbDtl.setIDNO(idNo);
		esbDtl.setACNO(cbsService.checkAcctLength(cbsDtl.getWA_X_ACCTNO()));
		esbDtl.setWA_X_ATYPE(cbsDtl.getWA_X_ATYPE()); // 後續貸款分類用(較重要)
		esbDtl.setWA_X_ICAT(cbsDtl.getWA_X_ICAT());
		esbDtl.setCUR_COD(cbsDtl.getWA_X_CURR());
		esbDtl.setACT_STS(cbsDtl.getWA_X_STATUS());
		esbDtl.setACT_BAL(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17, 18) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17));
		esbDtl.setWA_X_PROJ_CODE(substring(cbsDtl.getOthers(), 248, 252));
		esbDtl.setWA_LN_NATURE_TYPE(substring(cbsDtl.getOthers(), 124, 127)); // 重要貸款判斷用
		esbDtl.setDUE_DATE(substring(cbsDtl.getOthers(), 21, 29));
		esbDtl.setRATE(substring(cbsDtl.getOthers(), 11, 21));
	}

	/**
	 * 分期型房貸
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<SC120100OutputVO> searchInstallment(String custId) throws Exception {
		List result = new ArrayList();

		for (CBSUtilOutputVO each : getAcctData(custId)) {
			SC120100OutputVO esbVO = new SC120100OutputVO();
			List details = new ArrayList();

			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				// 符合條件且為分期型房貸才需執行，否則跳過
				if (!(fit(cbsDtl) && cbsService.isInstallment(cbsDtl.getWA_X_ATYPE())))
					continue;

				SC120100DetailOutputVO esbDtl = new SC120100DetailOutputVO();
				_085081_085105BasicTransfer(cbsDtl, esbDtl, each.getCbs085105OutputVO().getDefaultString1());
				esbDtl.setDOC_NO(substring(cbsDtl.getOthers(), 220, 238));

				CBS032105OutputVO cbs032105OutputVO = getLoanData(cbsDtl.getWA_X_ACCTNO(), null);
				_010400_032105BasicTransfer(cbs032105OutputVO, esbDtl);
				esbDtl.setACNO_SA(cbsService.checkAcctLength(trim(cbs032105OutputVO.getDefaultString17())));

				// 20200327_CBS_麗文_分期型房貸_原貸金額來源修改
				esbDtl.setORI_LOAN_BAL(cbsDtl.getOthers().substring(70, 71) + cbsDtl.getOthers().substring(53, 70));

				
				setINS_AMTAndRATE(esbDtl,cbs032105OutputVO);

				// 20200415_CBS_麗文_分期型房貸_每月應繳款日來源修改
				setINT_CYCLE(esbDtl,cbsDtl.getWA_X_ACCTNO());

				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}

		return result;
	}
	

	/**
	 * 取得貸款資料
	 **/
	private CBS032105OutputVO getLoanData(String acctNo, String yearTerm) throws Exception {
		String term = defaultString(trim(yearTerm));

		List<CBSUtilOutputVO> result = _010400_032105dao.search(defaultString(trim(acctNo)), cbsService.fitYearTermPattern(term) ? term : null);

		return isNotEmpty(result) ? result.get(0).getCbs032105OutputVO() : null;
	}

	/**
	 * 規格書轉換（詳細資訊）_010400_032105 to SC120100
	 * 20221205_#1369_麗文_調整客首負債總覽- 循環型房貸回復式的可用餘額
	 **/
	private void _010400_032105BasicTransfer(CBS032105OutputVO cbsDtl, SC120100DetailOutputVO esbDtl) {
		if (cbsDtl == null)
			return;

		esbDtl.setACT_BAL_NT(trim(cbsDtl.getDefaultString30()));
		esbDtl.setGLCD_LOAN(trim(cbsDtl.getDefaultString19()));
		esbDtl.setORI_LOAN_BAL(trim(cbsDtl.getDefaultString27()));
		esbDtl.setLOAN_TERM(trim(cbsDtl.getDefaultString26()));
		esbDtl.setLOAN_TYP(trim(cbsDtl.getDefaultString9()));
		esbDtl.setINT_CYCLE(cbsDtl.getDate6());
		esbDtl.setPRN_STR_DATE(trim(cbsDtl.getSloan_int_pay_day()));
		esbDtl.setPART_RECV(trim(cbsDtl.getSloan_write_off_flg()));
		esbDtl.setACH_FLG(trim(cbsDtl.getSloan_ach_status()));
		esbDtl.setSPEC_STS(trim(cbsDtl.getDefaultString37()));
		esbDtl.setPENL_STR_DATE(trim(cbsDtl.getGraceStrtDate()));
		esbDtl.setPRN_ALW_DUR(trim(cbsDtl.getGracePeriod()));
		esbDtl.setRECLOAN_AVAL_AMT(cbsService.amountFormat(substring(cbsDtl.getSloan_avail_limit(), 17, 18) + substring(cbsDtl.getSloan_avail_limit(), 0, 17)));
	}

	/**
	 * 循環型貸款 20200303 新規格 影響四個欄位 INS_AMT、ACT_BAL_NT、ORI_LOAN_BAL、 循環型房貸(回復式)
	 * 20200427_CBS_麗文_客首_循環型貸款_調整循環型(額度式及回復式)_可用餘額欄位  --> 兩種只有訂約額度不同 其他欄位取法都相同
	 * 
	 * 
	 * 
	 * @param custId
	 *            客戶 Id
	 */
	public List<SC120100OutputVO> searchCirculation(String custId) throws Exception { 
		List result = new ArrayList();

		for (CBSUtilOutputVO each : getAcctData(custId)) {
			SC120100OutputVO esbVO = new SC120100OutputVO();
			List details = new ArrayList();

			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				SC120100DetailOutputVO esbDtl = new SC120100DetailOutputVO();
				// 符合條件且為循環型貸款才需執行，否則跳過
				String acctType = defaultString(trim(cbsDtl.getWA_X_ATYPE()));
				if (!(fit(cbsDtl) && cbsService.isCirculation(acctType)))
					continue;

				_085081_085105BasicTransfer(cbsDtl, esbDtl, each.getCbs085105OutputVO().getDefaultString1());
				esbDtl.setDOC_NO(substring(cbsDtl.getOthers(), 220, 238));
				esbDtl.setACNO_SA(cbsService.checkAcctLength(trim(substring(cbsDtl.getOthers(), 204, 220)))); // 透支帳號
				CBS032105OutputVO cbs032105OutputVO = getLoanData(cbsDtl.getWA_X_ACCTNO(), null);
				_010400_032105BasicTransfer(cbs032105OutputVO, esbDtl);
				
				dealINS_AMT(acctType, cbs032105OutputVO, cbsDtl, esbDtl); //20200427訂約額度
				dealAVAL_AMT(esbDtl, acctType, cbsDtl);                   //20200427取分期餘額
				dealORI_LOAN_BAL(esbDtl);                                 //20200427已透支動用額度&可用額度
				
				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}
		return result;
	}

	private void dealAVAL_AMT(SC120100DetailOutputVO esbDtl, String acctType, CBS085105OutputDetailsVO cbsDtl) throws Exception {
		/*
		 * 循環型房貸(回復式) 取分期餘額
		 * 分期餘額：　　　　AVAL_AMT
		 * #1369:
		 * 透支核淮額度: RECLOAN_AVAL_AMT
		 * 分期型帳號數量: accountNumber
		 */
		if (CBSService.HOME_REC_LOAN.equals(acctType)) {
			List<CBSUtilOutputVO> avalDataStep1 = _017050_017200dao.search(cbsDtl.getWA_X_ACCTNO());
			List<CBSUtilOutputVO> avalDataStep2 = _060440_060441dao.search(avalDataStep1.get(0).getCbs017200OutputVO().getAccntNumber1().trim(), cbsDtl.getWA_X_ATYPE());
			double AVAL_AMT = 0;
			for (CBSUtilOutputVO outputVO : avalDataStep2) {
				for (CBS060441OutputDetailsVO detail : outputVO.getCbs060441OutputVO().getDetails()) {
					if (isNotBlank(detail.getCustno()) & !detail.getCustno().substring(3, 17).equals("00000000000000")) {
						List<CBSUtilOutputVO> avalDataStep3 = _010400_032105dao.search(detail.getCustno().substring(3, 17));
						AVAL_AMT = AVAL_AMT + getDouble(avalDataStep3.get(0).getCbs032105OutputVO().getDefaultString30().trim());
					}
				}
			}
			esbDtl.setAVAL_AMT(String.valueOf(AVAL_AMT));
		}else {
			esbDtl.setAVAL_AMT("0");
		}
		
	}

	/*
	 * #1369 循環型回復式房貸用不同計算方式
	 * #1470 調整循環型回復式房貸 > 可用額度  Betty 
	 */
	private void dealORI_LOAN_BAL(SC120100DetailOutputVO esbDtl) throws Exception {
		int i = -1;
		List<CBSUtilOutputVO> circleData = null;
		//取得所有貸款明細 直到002024下行電文回錯
		while (true) {
			i = i + 1;
			if (i == 0) {
				circleData = _002023_002024dao.search(esbDtl.getACNO_SA(), esbDtl.getCUR_COD());
			} else {
				try {
					circleData.add(_002024_002024dao.search(circleData.get(i-1).getCbs002024OutputVO()).get(0));
				} catch (Exception e) {
					break;
				}
			}
		}

		for (CBSUtilOutputVO cbsOutputDtl : circleData) {
			for (CBS002024OutputDetailsVO detail : cbsOutputDtl.getCbs002024OutputVO().getDetails()) {
				if (detail.getOdno1().equals(esbDtl.getACNO())) {
					//透支金額
					esbDtl.setACT_BAL_NT(defaultIfEmpty(trim(detail.getBalance1()), "0"));
					if(detail.getStat1().trim().equals("A")){
						//透支金額
						double ACT_BAL_NT = getDouble(esbDtl.getACT_BAL_NT());
						double AVAL_AMT = getDouble(esbDtl.getAVAL_AMT());
						double INS_AMT = 0;
						if(cbsService.isHomeRecLoan(esbDtl.getWA_X_ATYPE().trim())){							
							//透支額度
							INS_AMT = getDouble(defaultIfEmpty(trim(detail.getLimit2()), "0"));
//							esbDtl.setORI_LOAN_BAL(esbDtl.getRECLOAN_AVAL_AMT());
							
							//20230509 BETTY 循環型回復式：002024交易的可用額度 = 透支額度 - 透支金額
							esbDtl.setORI_LOAN_BAL(INS_AMT - ACT_BAL_NT > 0 ? String.valueOf(INS_AMT - ACT_BAL_NT) : "0");
						}  else {
							//透支額度
							INS_AMT = getDouble(esbDtl.getINS_AMT());
							esbDtl.setORI_LOAN_BAL(INS_AMT - ACT_BAL_NT - AVAL_AMT > 0 ? String.valueOf(INS_AMT - ACT_BAL_NT - AVAL_AMT) : "0");
						}								
					} else {
						esbDtl.setORI_LOAN_BAL("0");
					}				
					break;
				}
			}
		}	
	}

	/*
	 * #1369
	 * 循環型回復式房貸 多筆分期型貸款情境
	 * 加入 房貸透支最低還本額 : overDraftLimitValue
	 */
	private void dealINS_AMT(String acctType, CBS032105OutputVO cbs032105OutputVO, CBS085105OutputDetailsVO cbsDtl, SC120100DetailOutputVO esbDtl) throws Exception {
		if (Arrays.asList(CBSService.HOME_CYCLE_LOAN).contains(acctType) || Arrays.asList(CBSService.checkCreditLON).contains(acctType) ) {
			esbDtl.setINS_AMT(cbs032105OutputVO.getDefaultString24().trim());
		}else if (CBSService.HOME_REC_LOAN.equals(acctType)) {
			List<CBSUtilOutputVO> loanData = _017050_017000dao.search(cbsDtl.getWA_X_ACCTNO());
			if (isNotEmpty(loanData)) {
				CBS017000OutputVO cbs017000 = loanData.get(0).getCbs017000OutputVO();
				esbDtl.setINS_AMT(cbsService.amountFormat(substring(cbs017000.getThreshAmt(), 17, 18) + substring(cbs017000.getThreshAmt(), 0, 17))); // 訂約額度
			}
		}else {
			esbDtl.setINS_AMT("0");
		}
		
		
	}

	/**
	 * 信用貸款
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<SC120100OutputVO> searchCredit(String custId) throws Exception {
		List result = new ArrayList();
		for (CBSUtilOutputVO each : getAcctData(custId)) {
			SC120100OutputVO esbVO = new SC120100OutputVO();
			List details = new ArrayList();
			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				String acctType = defaultString(trim(cbsDtl.getWA_X_ATYPE()));
				// 符合條件且為信用貸款才需執行，否則跳過
				if (!(fit(cbsDtl) && cbsService.isCredit(acctType)))
					continue;

				SC120100DetailOutputVO esbDtl = new SC120100DetailOutputVO();
				_085081_085105BasicTransfer(cbsDtl, esbDtl, each.getCbs085105OutputVO().getDefaultString1());
				esbDtl.setDOC_NO(substring(cbsDtl.getOthers(), 220, 238));

				CBS032105OutputVO cbs032105OutputVO = getLoanData(cbsDtl.getWA_X_ACCTNO(), null);
				_010400_032105BasicTransfer(cbs032105OutputVO, esbDtl);
				esbDtl.setACNO_SA(cbsService.checkAcctLength(trim(cbs032105OutputVO.getDefaultString17())));
				esbDtl.setINS_AMT(trim(cbs032105OutputVO.getDefaultString32()));
				
                
				// 20200505_CBS_麗文_將信用貸款的每月應繳月付金來源改成跟分期型房貸一樣
				setINS_AMTAndRATE(esbDtl,cbs032105OutputVO);
				
				// 20200415_CBS_麗文_分期型房貸_每月應繳款日來源修改
				setINT_CYCLE(esbDtl,cbsDtl.getWA_X_ACCTNO());

				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}
		return result;
	}


	/**
	 * 就學貸款
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<SC120100OutputVO> searchStudentLoan(String custId) throws Exception {
		List result = new ArrayList();

		for (CBSUtilOutputVO each : getAcctData(custId)) {
			SC120100OutputVO esbVO = new SC120100OutputVO();
			List details = new ArrayList();

			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				String acctType = defaultString(trim(cbsDtl.getWA_X_ATYPE()));
				// 符合條件且為就學貸款才需執行，否則跳過
				if (!(fit(cbsDtl) && cbsService.isStudentLoan(acctType)))
					continue;

				SC120100DetailOutputVO esbDtl = new SC120100DetailOutputVO();
				esbDtl.setDOC_NO(substring(cbsDtl.getOthers(), 238, 242));

				// 就學貸款用 DOC_NO 欄位判斷學期別(四位數) 學期別異常的是就學貸款的主帳號所以排除
				if (!cbsService.fitYearTermPattern(esbDtl.getDOC_NO()))
					continue;

				_085081_085105BasicTransfer(cbsDtl, esbDtl, each.getCbs085105OutputVO().getDefaultString1());

				CBS032105OutputVO cbs032105 = getLoanData(esbDtl.getACNO(), esbDtl.getDOC_NO());
				_010400_032105BasicTransfer(cbs032105, esbDtl);
				esbDtl.setINS_AMT(trim(cbs032105.getPrePayAmt1()));
				esbDtl.setACNO_SA(cbsService.checkAcctLength(trim(cbs032105.getDefaultString17())));

				// 20200303_CBS_#79022_分行業管系統_就貸戶的負債分佈欄位資料有誤 (四個變數)
				esbDtl.setACT_BAL_NT(substring(cbsDtl.getWA_X_CURR_BALANCE(), 17, 18) + substring(cbsDtl.getWA_X_CURR_BALANCE(), 0, 17));
				esbDtl.setORI_LOAN_BAL(substring(cbsDtl.getOthers(), 169, 170) + substring(cbsDtl.getOthers(), 152, 169));
				esbDtl.setINS_AMT(substring(cbsDtl.getOthers(), 187, 188) + substring(cbsDtl.getOthers(), 170, 187));
				//淘汰 esbDtl.setINT_CYCLE(substring(cbsDtl.getOthers(), 114, 122));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
				
				//20200513_CBS_麗文_就學貸款_改欄位邏輯
				setINS_AMTAndRATE(esbDtl,cbs032105);
				
				// 20200415_CBS_麗文_分期型房貸_每月應繳款日來源修改
				setINT_CYCLE(esbDtl,cbsDtl.getWA_X_ACCTNO());
				
				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}

		return result;
	}

	/**
	 * 留學貸款
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public List<SC120100OutputVO> searchStudentForeignLoan(String custId) throws Exception {
		List result = new ArrayList();
		for (CBSUtilOutputVO each : getAcctData(custId)) {
			SC120100OutputVO esbVO = new SC120100OutputVO();
			List details = new ArrayList();

			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				String acctType = defaultString(trim(cbsDtl.getWA_X_ATYPE()));
				if (!(fit(cbsDtl) && cbsService.isStudentForeignLoan(acctType)))
					continue;

				SC120100DetailOutputVO esbDtl = new SC120100DetailOutputVO();
				_085081_085105BasicTransfer(cbsDtl, esbDtl, each.getCbs085105OutputVO().getDefaultString1());
				esbDtl.setDOC_NO(substring(cbsDtl.getOthers(), 238, 242));
				CBS032105OutputVO cbs032105 = getLoanData(cbsDtl.getWA_X_ACCTNO(), null);
				_010400_032105BasicTransfer(cbs032105, esbDtl);
				esbDtl.setACNO_SA(cbsService.checkAcctLength(trim(cbs032105.getDefaultString17())));
				esbDtl.setINS_AMT(trim(cbs032105.getDefaultString32()));
				
				//20200526_CBS_麗文_留學貸款_改欄位邏輯
				setINS_AMTAndRATE(esbDtl,cbs032105);
				
				// 20200727_CBS_麗文_留學貸款_每月應繳日邏輯修正
				setINT_CYCLE(esbDtl,cbsDtl.getWA_X_ACCTNO());
				

				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}

		return result;
	}


	/**
	 * 存單質借 && 信託質借
	 *
	 * @param custId
	 */
	public List<SC120100OutputVO> searchMortgage(String custId) throws Exception {
		List result = new ArrayList();

		for (CBSUtilOutputVO each : getAcctData(custId)) {
			SC120100OutputVO esbVO = new SC120100OutputVO();
			List<SC120100DetailOutputVO> details = new ArrayList();

			for (CBS085105OutputDetailsVO cbsDtl : each.getCbs085105OutputVO().getDetails()) {
				SC120100DetailOutputVO esbDtl = new SC120100DetailOutputVO();
				// 參考規格書轉換
				String acctType = defaultString(trim(cbsDtl.getWA_X_ATYPE()));
				if (!(fit(cbsDtl) && cbsService.isMortgage(acctType)))
					continue;

				_085081_085105BasicTransfer(cbsDtl, esbDtl, each.getCbs085105OutputVO().getDefaultString1());
				esbDtl.setDOC_NO(substring(cbsDtl.getOthers(), 220, 238));

				CBS032105OutputVO cbs032105 = getLoanData(cbsDtl.getWA_X_ACCTNO(), null);
				_010400_032105BasicTransfer(cbs032105, esbDtl);
				esbDtl.setINS_AMT(trim(cbs032105.getPrePayAmt1()));
				esbDtl.setACNO_SA(cbsService.checkAcctLength(trim(cbs032105.getDefaultString17())));

				CBS062167OutputVO cbs062167 = getLoanDetailData(cbsDtl.getWA_X_ACCTNO());
				esbDtl.setACT_STS("");
				esbDtl.setTD_NO("");
				for (CBS062167OutputDetailsVO detail : cbs062167.getDetails()) {
					if (StringUtils.isNotBlank(detail.getColl_No1().trim())) {
						CBS062144OutputVO cbs062144 = getTD_NO(custId, detail.getColl_No1().trim());
						_062141_062144BasicTransfer(cbs062144, esbDtl);
					}

				}
				esbDtl.setACT_STS(esbDtl.getACT_STS().substring(0, esbDtl.getACT_STS().length() - 1));
				esbDtl.setTD_NO(esbDtl.getTD_NO().substring(0, esbDtl.getTD_NO().length() - 1));
				details.add(esbDtl);
			}
			esbVO.setDetails(details);
			result.add(esbVO);
		}

		return result;
	}

	private void _062141_062144BasicTransfer(CBS062144OutputVO cbs062144, SC120100DetailOutputVO esbDtl) {
		esbDtl.setACT_STS(esbDtl.getACT_STS() + cbs062144.getIbdNo().trim() + "，");
		esbDtl.setTD_NO(esbDtl.getTD_NO() + cbs062144.getIBDNumber().trim() + "，");
	}

	/**
	 * 取得貸款資料
	 **/
	private CBS062167OutputVO getLoanDetailData(String acctNo) throws Exception {

		List<CBSUtilOutputVO> result = _062167_062167dao.search(defaultString(trim(acctNo)));

		return isNotEmpty(result) ? result.get(0).getCbs062167OutputVO() : null;
	}

	/**
	 * 取得存單編號跟定存帳號
	 **/
	private CBS062144OutputVO getTD_NO(String custID, String colNo) throws Exception {

		List<CBSUtilOutputVO> result = _062141_062144dao.search(custID, colNo);

		return isNotEmpty(result) ? result.get(0).getCbs062144OutputVO() : null;
	}
	
	/**
	 * 取得每月應繳款日(共用模組)
	 * @param esbDtl
	 * @param acct
	 * @throws Exception 
	 */
	private void setINT_CYCLE(SC120100DetailOutputVO esbDtl, String acct) throws Exception {
		// TODO Auto-generated method stub
		CBS017000OutputVO cbs017000OutputVO = _017050_017000dao.search(cbsService.checkAcctLength(acct)).get(0).getCbs017000OutputVO();
		esbDtl.setINT_CYCLE(cbs017000OutputVO.getRpmt_day1());
	}
	
	/**
	 * 取每月應繳月付金跟適用利率 (共用功能)
	 * 2020.08.06  改以017079_017079為優先
	 * @param esbDtl
	 * @param cbs032105OutputVO
	 * @throws Exception
	 */

	private void setINS_AMTAndRATE(SC120100DetailOutputVO esbDtl, CBS032105OutputVO cbs032105OutputVO) throws Exception {
		// TODO Auto-generated method stub
		
		/*
		 * 20200324_CBS_麗文_分期型房貸_就學貸款_每月應繳月付金來源 20200409更新
		 */
			// 第一道_010400_032105的DefaultString26 (累計還款期數)
			// 第二道_017079_017079
			String term = String.valueOf((Integer.parseInt(cbs032105OutputVO.getDefaultString26()) + 1));
			if(cbsService.isStudentLoan(esbDtl.getWA_X_ATYPE()) || cbsService.isStudentForeignLoan(esbDtl.getWA_X_ATYPE())){ //就學貸款跟留學貸款要多送學期別
				_017079_017079BasicTransfer(_017079_017079dao.search(esbDtl.getACNO(), term, esbDtl.getDOC_NO()), esbDtl);
			} else {
				_017079_017079BasicTransfer(_017079_017079dao.search(esbDtl.getACNO(), term), esbDtl);
			}
			
		
			if (esbDtl.getINS_AMT().equals("0.0")) {
				esbDtl.setINS_AMT(trim(cbs032105OutputVO.getDefaultString32())); // 每月應繳月付金
				esbDtl.setRATE(trim(cbs032105OutputVO.getDefaultString11())); // 適用利率
			} 
		
	}

	/*
	 * 每三筆DATA為完整資料 其中第一筆DATA的判斷方式為 1-5為期數(四位) 5-16要有中文(利息 違約 等等等)(十一位)
	 */
	private void _017079_017079BasicTransfer(List<CBSUtilOutputVO> cbsDtl, SC120100DetailOutputVO esbDtl) {
		Boolean processSecondData = false;
		Boolean processThirdData = false;
		String type = null; // 類別 I 利息 P 本金 A 違約
		Double value = 0.0;

		for (CBSUtilOutputVO cbsUtilOutputVO : cbsDtl) {
			if (StringUtils.isNotBlank(cbsUtilOutputVO.getCbs017079OutputVO().getAccntNumber1()))
				continue;
			for (CBS017079OutputDetailsVO cbs017079Dtl : cbsUtilOutputVO.getCbs017079OutputVO().getDetails()) {
				String data = cbs017079Dtl.getDATA();
				if (processSecondData && processThirdData) {
					if (type.matches("I|P")) {
						value = value + getDouble(data.substring(0, 19).trim());
					}
					processSecondData = false;
					processThirdData = false;
					type = null; // 類別 I 利息 P 本金 A 違約
					continue;

				} else if (processSecondData) {
					processThirdData = true;
					continue;

				} else {
					if (StringUtils.isBlank(data)) {
						continue;
					}
					if (NumberUtils.isNumber(data.substring(1, 5)) && StringUtils.isNotBlank(data.substring(5, 12))) {
						type = data.substring(12, 13);
						if (type.equals("I")) {
							esbDtl.setRATE(data.substring(58, 69).trim());
							processSecondData = true;
							continue;
						} else {
							processSecondData = true;
							continue;
						}

					}

				}
			}
		}
		esbDtl.setINS_AMT(String.valueOf(value));

	}
	
	private Double getDouble(String number) {
		double d = 0.0;
		try {
			d = Double.parseDouble(number);
			return d;
		} catch (Exception e) {
			return 0.0;
		}
	}

}