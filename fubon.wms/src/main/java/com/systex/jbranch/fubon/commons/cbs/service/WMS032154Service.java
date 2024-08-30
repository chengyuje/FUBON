package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao.*;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067000OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067101OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067102OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067115OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067501OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067157_067157.CBS067157OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputVO;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 此服務將代替原本的 WMS032154 ESB 電文 （Note: 原本的 WMS032154 ESB 電文改走 CBS 路線）
 */
@Service
public class WMS032154Service {
	@Autowired
	private _067050_067000DAO _067050_067000dao;
	@Autowired
	private _067050_067501DAO _067050_067501dao;
	@Autowired
	private _067050_067101DAO _067050_067101dao;
	@Autowired
	private _067050_067102DAO _067050_067102dao;
	@Autowired
	private _067050_067115DAO _067050_067115dao;
	@Autowired
	private _067157_067157DAO _067157_067157dao;
	@Autowired
	private CBSService cbsService;

	/**
	 * 查詢客戶連絡方式
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public WMS032154OutputVO search(String custId) throws Exception {
		List details = new ArrayList();

		WMS032154OutputVO esbVO = new WMS032154OutputVO();
		WMS032154OutputDetailsVO esbDtl = new WMS032154OutputDetailsVO();

		String idType = cbsService.getCBSIDCode(custId);
		_067050_067501Transfer(esbDtl, _067050_067501dao.search(custId, idType));
		_067050_067000Transfer(esbDtl, _067050_067000dao.search(custId, idType));

		if (cbsService.isNaturalPerson(idType)) {
			_067050_067101Transfer(esbDtl, _067050_067101dao.search(custId, idType));
		} else {
			_067050_067102Transfer(esbDtl, _067050_067102dao.search(custId, idType));
		}

		details.add(esbDtl);
		esbVO.setDetails(details);
		return esbVO;
	}

	/**
	 * 查詢客戶連絡方式
	 *
	 * @param custId
	 *            客戶 Id
	 */
	public WMS032154OutputVO transfer(String custId, List<CBSUtilOutputVO> data067050_067101_2, List<CBSUtilOutputVO> data067050_067000) throws Exception {
		List details = new ArrayList();

		WMS032154OutputVO esbVO = new WMS032154OutputVO();
		WMS032154OutputDetailsVO esbDtl = new WMS032154OutputDetailsVO();

		String idType = cbsService.getCBSIDCode(custId);
		_067050_067501Transfer(esbDtl, _067050_067501dao.search(custId, idType));
		_067050_067000Transfer(esbDtl, data067050_067000);

		if (cbsService.isNaturalPerson(idType)) {
			_067050_067101Transfer(esbDtl, data067050_067101_2);
		} else {
			_067050_067102Transfer(esbDtl, data067050_067101_2);
		}

		details.add(esbDtl);
		esbVO.setDetails(details);
		return esbVO;
	}

	/**
	 * TODO 規格書轉換 _067050_067102 to WMS032154 Detail
	 **/
	private void _067050_067102Transfer(WMS032154OutputDetailsVO esbDtl, List<CBSUtilOutputVO> search) {
		CBS067102OutputVO cbsVO = search.get(0).getCbs067102OutputVO();
		esbDtl.setResd_tel(trim(cbsVO.getResidencePhone2()));
		esbDtl.setResd_tel_ext(cbsVO.getResidenceExtension2());
	}

	/**
	 * 規格書轉換 _067050_067101 to WMS032154 Detail
	 **/
	private void _067050_067101Transfer(WMS032154OutputDetailsVO esbDtl, List<CBSUtilOutputVO> data) {
		if (isEmpty(data))
			return;

		CBS067101OutputVO cbsVO = data.get(0).getCbs067101OutputVO();
		esbDtl.setResd_tel(trim(cbsVO.getResidencePhone2()));
		esbDtl.setResd_tel_ext(cbsVO.getResidenceExtension2());
	}

	/**
	 * 規格書轉換 _067050_067000 to WMS032154 Detail
	 **/
	private void _067050_067000Transfer(WMS032154OutputDetailsVO esbDtl, List<CBSUtilOutputVO> data) {
		if (isEmpty(data))
			return;

		CBS067000OutputVO cbsVO = data.get(0).getCbs067000OutputVO();
		esbDtl.setCUST_NAME(cbsVO.getDefaultString2());
		esbDtl.setReg_tel(trim(cbsVO.getReg_phne1()));
		esbDtl.setReg_tel_ext(cbsVO.getReg_ext1());
		esbDtl.setCon_tel(trim(cbsVO.getDefaultString10()));
		esbDtl.setCon_tel_ext(cbsVO.getCont_ext1());
		esbDtl.setHandphone(trim(cbsVO.getDefaultString13()));
		esbDtl.setFax(trim(cbsVO.getDefaultString11()));
		esbDtl.setMTN_DATE(cbsVO.getLstmntdate());
		esbDtl.setMTN_BRH(cbsVO.getBranchID1());
		esbDtl.setCmpy_tel(trim(cbsVO.getDefaultString12()));
		esbDtl.setCmpy_ext(trim(cbsVO.getCmpy_ext1()));
	}

	/**
	 * 規格書轉換 _067050_067501 to WMS032154 Detail
	 **/
	private void _067050_067501Transfer(WMS032154OutputDetailsVO esbDtl, List<CBSUtilOutputVO> data) {
		if (isEmpty(data))
			return;

		CBS067501OutputVO cbsVO = data.get(0).getCbs067501OutputVO();
		esbDtl.setLOST_FLG1(cbsVO.getPhonePer1UncFlag());
		esbDtl.setLOST_FLG2(cbsVO.getPhoneRes1UncFlag());
		esbDtl.setLOST_FLG3(cbsVO.getPhoneRes2UncFlag());
		esbDtl.setLOST_FLG4(cbsVO.getPhoneBus1UncFlag());
		esbDtl.setLOST_FLG5(cbsVO.getMobileNo1UncFlag());
		esbDtl.setLOST_FLG6(cbsVO.getFaxNo1UncFlag());
		esbDtl.setLOST_FLG7(cbsVO.getAddressUncFlag1());
		esbDtl.setLOST_FLG8(cbsVO.getAddressUncFlag2());
		esbDtl.setLOST_FLG9(cbsVO.getPhoneBus2UncFlag());
		esbDtl.setLOST_FLG10(cbsVO.getAddressPerUncFlag1());
		esbDtl.setLOST_FLG11(cbsVO.getMobileNo2UncFlag());
		esbDtl.setLOST_FLG12(cbsVO.getEmail1UncFlag());
		esbDtl.setLOST_FLG13(cbsVO.getFaxNo2UncFlag());
		esbDtl.setLOST_FLG14(cbsVO.getMsgApp1UncFlag());
		esbDtl.setLOST_FLG15(cbsVO.getMsgApp2UncFlag());
		esbDtl.setLOST_FLG16(cbsVO.getMsgApp3UncFlag());
		esbDtl.setLOST_FLG17(cbsVO.getMsgApp4UncFlag());
		esbDtl.setLOST_FLG18(cbsVO.getMsgApp5UncFlag());
		esbDtl.setLOST_FLG19(cbsVO.getCompAddUncFlag());
	}

	/**
	 * KYC更新信箱
	 */
	public void kycUpdateEmail(WMS032154InputVO esbVO) throws Exception {
		String custId = trim(esbVO.getCUST_NO());
		String idType = cbsService.getCBSIDCode(custId);

		updateEmail(esbVO, _067050_067000dao.search(custId, idType));

	}

	/**
	 * KYC更新更新信箱邏輯
	 **/
	private void updateEmail(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {
		if (isEmpty(data))
			return;

		CBS067000OutputVO cbsVO = data.get(0).getCbs067000OutputVO();
		cbsVO.setEmail_addr1(esbVO.getMail());
		_067050_067115dao.update067000(cbsVO);
	}

	/**
	 * KYC更新地址
	 */
	public void kycUpdateAddress(WMS032154InputVO esbVO) throws Exception {
		String custId = trim(esbVO.getCUST_NO());
		String idType = cbsService.getCBSIDCode(custId);

		updateAddress(esbVO, _067050_067000dao.search(custId, idType));

	}

	/**
	 * KYC更新地址邏輯
	 **/
	private void updateAddress(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {
		if (isEmpty(data))
			return;

		CBS067000OutputVO cbsVO = data.get(0).getCbs067000OutputVO();
		/*
		 * 20201026_#393_麗文_KYC上送地址邏輯異動
		 * 要拆60個byte，但根據窗口提供資料，它們那邊一個字似乎是3byte(UTF-8)
		 * 補滿長度用全形空白
		 */
		if (StringUtils.isNotBlank(esbVO.getAddr())) {
			if(esbVO.getAddr().length() > 20){
				cbsVO.setDefaultString5(esbVO.getAddr().substring(0,20));
				String after = esbVO.getAddr().substring(20);
				cbsVO.setDefaultString6(cbsService.padRight(after, 20, "　"));
			}else{
				cbsVO.setDefaultString5(cbsService.padRight(esbVO.getAddr(), 20, "　"));
				String after = "";
				cbsVO.setDefaultString6(cbsService.padRight(after, 20, "　"));
			}
			
		}
		_067050_067115dao.update067000(cbsVO);
	}

	/**
	 * KYC更新聯絡方式
	 */
	public void kycUpdatePhone(WMS032154InputVO esbVO) throws Exception {
		String custId = trim(esbVO.getCUST_NO());
		String idType = cbsService.getCBSIDCode(custId);

		updatePhone067000(esbVO, _067050_067000dao.search(custId, idType));
		if (cbsService.isNaturalPerson(idType)) {
		updatePhone067101(esbVO, _067050_067101dao.search(custId, idType));
		}

	}

	/**
	 * KYC更新聯絡方式邏輯
	 **/
	private void updatePhone067000(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {
		if (isEmpty(data))
			return;

		CBS067000OutputVO cbsVO = data.get(0).getCbs067000OutputVO();
		cbsVO.setDefaultString10(esbVO.getCon_tel());
		cbsVO.setDefaultString13(esbVO.getHandphone());
		cbsVO.setDefaultString11(esbVO.getFax());
		_067050_067115dao.update067000(cbsVO);
	}

	/**
	 * KYC更新聯絡方式邏輯
	 **/
	private void updatePhone067101(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {

		CBS067101OutputVO cbsVO = data.get(0).getCbs067101OutputVO();
		cbsVO.setResidencePhone2(esbVO.getResd_tel());
		_067050_067115dao.update067101(cbsVO);
	}

	/**
	 * KYC更新基本資料
	 */
	public void kycUpdateBasic(WMS032154InputVO esbVO) throws Exception {
		String custId = trim(esbVO.getCUST_NO());
		String idType = cbsService.getCBSIDCode(custId);
		if (cbsService.isNaturalPerson(idType)) {
		updateBasic067101(esbVO, _067050_067101dao.search(custId, idType));
		}
		updateBasic067115(esbVO, _067050_067115dao.search(custId, idType));

	}

	/**
	 * KYC更新基本資料邏輯
	 **/
	public void updateBasic067101(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {
		CBS067101OutputVO cbsVO = data.get(0).getCbs067101OutputVO();
		cbsVO.setChildren(esbVO.getCHILD_NO());
		cbsVO.setDefaultString13(esbVO.getEDUCATION());
		cbsVO.setOccupationCode(esbVO.getCAREER());
		cbsVO.setDefaultString3(esbVO.getMARRAGE());
		_067050_067115dao.update067101(cbsVO);
	}

	/**
	 * KYC更新基本資料邏輯
	 * 20200316_CBS_彥德_067115上送更新時FinManInstrmt1把別的BYTE資料都刪掉
	 **/
	private void updateBasic067115(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {
		if (isEmpty(data))
			return;
		CBS067115OutputVO cbsVO = data.get(0).getCbs067115OutputVO();
		
		
		//自然人才更新傷病註記
        if(cbsService.isNaturalPerson(cbsService.getCBSIDCode(esbVO.getCUST_NO()))){
    		if ("1".equals(esbVO.getSICK_TYPE())) {
    			cbsVO.setAnntationFlag12("N");
    		} else if ("2".equals(esbVO.getSICK_TYPE())) {
    			cbsVO.setAnntationFlag12("Y");
    		}
        }
		_067050_067115dao.update067115(cbsVO);
	}
	
	/**
	 * KYC更新主管已覆核欄位
	 */
	public void kycUpdateSupervisorCheck(WMS032154InputVO esbVO) throws Exception {
		String custId = trim(esbVO.getCUST_NO());
		String idType = cbsService.getCBSIDCode(custId);

		updateSupervisorCheck067115(esbVO, _067050_067115dao.search(custId, idType));

	}
	
	/**
	 * KYC更新基本資料邏輯
	 * 20200316_CBS_彥德_067115上送更新時FinManInstrmt1把別的BYTE資料都刪掉
	 **/
	private void updateSupervisorCheck067115(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {
		if (isEmpty(data))
			return;
		CBS067115OutputVO cbsVO = data.get(0).getCbs067115OutputVO();
	
		try {
			if (cbsVO.getFinManInstrmt1().length() > 1) {
				cbsVO.setFinManInstrmt1("Y" + cbsVO.getFinManInstrmt1().substring(1)); // [主管已覆核]是第一個byte
																						// 其他要照舊上送
			} else {
				// 長度若<= 1 代表直接上送 "Y" 即可
				cbsVO.setFinManInstrmt1("Y");
			}

		} catch (Exception e) {
			// try會錯的話應該是在 cbsVO.getFinManInstrmt1().length()
			// 發生java.lang.NullPointerException
			// 那代表FinManInstrmt1沒資料 故也只上送"Y"
			cbsVO.setFinManInstrmt1("Y");
		}

		_067050_067115dao.update067115(cbsVO);
	}
	
	/**
	 * KYC更新C值
	 */
	public void kycUpdateCValue(WMS032154InputVO esbVO) throws Exception {
		String custId = trim(esbVO.getCUST_NO());
		String idType = cbsService.getCBSIDCode(custId);

		updateCValue(esbVO, _067157_067157dao.search(custId, idType));
	}
	
	/**
	 * KYC更新C值邏輯
	 **/
	private void updateCValue(WMS032154InputVO esbVO, List<CBSUtilOutputVO> data) throws Exception {
		if (isEmpty(data))
			return;

		CBS067157OutputVO cbsVO = data.get(0).getCbs067157OutputVO();
		if (!"C".equals(cbsVO.getOption1())) {
			cbsVO.setOption1("A"); // 修改
		}
		
		cbsVO.setFilDate1(esbVO.getKYC_TEST_DATE());
		cbsVO.setExpiryDate1(esbVO.getKYC_EXPIRY_DATE());
		cbsVO.setResult1(esbVO.getCVALUE());
		// 因為查詢時出現[0188查無資料]會避開，導致有些必填值沒有，需判斷加入
		if (isBlank(cbsVO.getIdno())) {
			String custId = trim(esbVO.getCUST_NO());
			cbsVO.setIdno(custId);
			cbsVO.setIdtype(cbsService.getCBSIDCode(custId));
		}

		// 09999 = 網行銀
		// 00+分行別 = 臨櫃
		String branch = checkBranch(esbVO.getBRANCH());
		if ("999".equals(branch)) {
			cbsVO.setSource1("09999");
		} else {
			cbsVO.setSource1("00" + branch);
		}
		_067157_067157dao.update067157(cbsVO);
	}
	
	private String checkBranch(String branch) {
		if(StringUtils.isBlank(branch)) {
			return "000";
		}
		if(branch.length() > 3) {
			return "000";
		}
		return branch;
	}

}
