package com.systex.jbranch.app.server.fps.crm621;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_SP_CONTACTVO;
import com.systex.jbranch.fubon.commons.cbs.service.FP032675Service;
import com.systex.jbranch.fubon.commons.cbs.service.WMS032154Service;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author walalala
 * @date 2016/06/23
 * 
 */
@Component("crm621")
@Scope("request")
public class CRM621 extends EsbUtil {
	@Autowired
	private FP032675Service fp032675Service;
	@Autowired
	private WMS032154Service wms032154Service;

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM621.class);
	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	private String thisClaz = this.getClass().getSimpleName() + ".";

	// 查詢A - 特殊聯絡方式
	public void inquire_A(Object body, IPrimitiveMap header) throws JBranchException {
		CRM621InputVO inputVO = (CRM621InputVO) body;
		CRM621OutputVO return_VO = new CRM621OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SP_CONTACT_ID , CUST_ID , VALID_TYPE, VALID_BGN_DATE, VALID_END_DATE, CONTENT, CREATOR, CREATETIME, MODIFIER, LASTUPDATE FROM TBCRM_CUST_SP_CONTACT where 1=1 ");
		sql.append("and CUST_ID = :cust_id ");
		// #0002475 : 特殊聯絡方式,超過有效期間,就不要顯示，Detail除外
		if (StringUtils.equals(inputVO.getDetail_YN(), "N")) {
			sql.append("AND ((VALID_TYPE = 'F') ");
			sql.append("OR (VALID_TYPE = 'D' AND TRUNC(SYSDATE) <= TRUNC(VALID_END_DATE)) ");
			sql.append("OR (VALID_TYPE = 'B' AND TRUNC(SYSDATE) BETWEEN TRUNC(VALID_BGN_DATE) AND TRUNC(VALID_END_DATE))) ");
		}

		// #0002475 : 有效資料排序上方(依建立日期,新的先),過期排下方
		sql.append("ORDER BY CREATETIME DESC ");

		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList_A(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	// 新增A- 特殊聯絡方式
	public void add(Object body, IPrimitiveMap header) throws JBranchException {
		CRM621InputVO inputVO = (CRM621InputVO) body;
		dam = this.getDataAccessManager();
		TBCRM_CUST_SP_CONTACTVO vo = new TBCRM_CUST_SP_CONTACTVO();

		vo.setSP_CONTACT_ID(getSN());
		vo.setCUST_ID(inputVO.getCust_id());
		vo.setVALID_TYPE(inputVO.getValid_type());

		if (inputVO.getValid_bgn_Date() != null) {
			vo.setVALID_BGN_DATE(new Timestamp(inputVO.getValid_bgn_Date().getTime()));
		}
		if (inputVO.getValid_end_Date() != null) {
			vo.setVALID_END_DATE(new Timestamp(inputVO.getValid_end_Date().getTime()));
		}
		vo.setCONTENT(inputVO.getContent());
		dam.create(vo);

		this.sendRtnObject(null);
	}

	// 刪除A - 特殊聯絡方式
	public void delete(Object body, IPrimitiveMap header) throws JBranchException {
		CRM621InputVO inputVO = (CRM621InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_CUST_SP_CONTACTVO vo = new TBCRM_CUST_SP_CONTACTVO();
		vo = (TBCRM_CUST_SP_CONTACTVO) dam.findByPKey(TBCRM_CUST_SP_CONTACTVO.TABLE_UID, inputVO.getSp_contact_id());
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
		}
		this.sendRtnObject(null);
	}

	// 序號SEQ
	private String getSN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("CRM621");
		} catch (Exception e) {
			sn.createNewSerial("CRM621", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("CRM621");
		}
		return seqNum;
	}

	/**
	 * 客戶聯絡資料（地址與信箱）
	 */
	public void inquire_message(Object body, IPrimitiveMap header) throws Exception {
		CRM621InputVO inputVO = (CRM621InputVO) body;
		CRM621OutputVO returnVO = new CRM621OutputVO();

		FP032675OutputVO txVO;
		if (inputVO.getData067050_067101_2() != null &&
				inputVO.getData067050_067000() != null)
			txVO = fp032675Service.transferBasicInfo(inputVO.getCust_id(), inputVO.getData067050_067101_2(), inputVO.getData067050_067000());
		else
		 	txVO = fp032675Service.searchBasicInfo(inputVO.getCust_id());

		// 地址
	    List<CBSAddressList> addrList = new ArrayList();
		getAddress(addrList, txVO.getZIP_COD1(), txVO.getDATA1(), "戶籍地址");
		getAddress(addrList, txVO.getZIP_COD2(), txVO.getDATA2(), "現居地址");
		getAddress(addrList, txVO.getZIP_COD3(), txVO.getDATA3(), "通訊地址");
		getAddress(addrList, txVO.getZIP_COD4(), txVO.getDATA4(), "現職公司地址");

		//信箱
		CBSMailList mail = new CBSMailList();
		mail.setMail(txVO.getE_MAIL());
		List<CBSMailList> mailList = new ArrayList();
		mailList.add(mail);

		returnVO.setMail(txVO.getE_MAIL());
		returnVO.setAddr(txVO.getDATA3());
			
		returnVO.setAddrList(addrList);
		returnVO.setMailList(mailList);
		
		this.sendRtnObject(returnVO);
	}

	/** 組合地址資訊 **/
	private void getAddress(List<CBSAddressList> addrList, String zip_cod1, String data1, String memo) {
		if (isBlank(zip_cod1) && isBlank(data1)) return;

		CBSAddressList addr = new CBSAddressList();
		addr.setZIP_COD(zip_cod1);
		addr.setDATA(data1);
		addr.setMEMO(memo);
		addrList.add(addr);
	}

	public void inquire_phone(Object body, IPrimitiveMap header) throws Exception {
		CRM621InputVO inputVO = (CRM621InputVO) body;
		CRM621OutputVO outputVO = new CRM621OutputVO();

		WMS032154OutputDetailsVO txVO;
		if (inputVO.getData067050_067101_2() != null &&
				inputVO.getData067050_067000() != null)
			txVO = wms032154Service.transfer(inputVO.getCust_id(), inputVO.getData067050_067101_2(), inputVO.getData067050_067000()).getDetails().get(0);
		else
			txVO = wms032154Service.search(inputVO.getCust_id()).getDetails().get(0);

		List<CBSPhoneList> phoneList = new ArrayList<CBSPhoneList>();
		CBSPhoneList phone = new CBSPhoneList();

		// 戶籍（註冊）電話
		if(!isBlank(txVO.getReg_tel())){
			phone = new CBSPhoneList();
			phone.setCODE("1");
			phone.setTYPE("戶籍（註冊）電話");
			if(!isBlank(txVO.getReg_tel_ext())){
				phone.setNUMBER(txVO.getReg_tel().trim()+"#"+txVO.getReg_tel_ext().trim());
			}else{
				phone.setNUMBER(txVO.getReg_tel().trim());
			}
			phoneList.add(phone);
		}
		// 現居（主要營業處所）電話
		if(!isBlank(txVO.getResd_tel())){
			phone = new CBSPhoneList();
			phone.setCODE("2");
			phone.setTYPE("現居（主要營業處所）電話");
			if(!isBlank(txVO.getResd_tel_ext())){
				phone.setNUMBER(txVO.getResd_tel()+"#"+txVO.getResd_tel_ext());
			}else{
				phone.setNUMBER(txVO.getResd_tel());
			}
			phoneList.add(phone);
		}
		
		//通訊電話
		if(!isBlank(txVO.getCon_tel())){
			phone = new CBSPhoneList();
			phone.setCODE("3");
			phone.setTYPE("通訊電話");
			if(!isBlank(txVO.getCon_tel_ext())){
				phone.setNUMBER(txVO.getCon_tel()+"#"+txVO.getCon_tel_ext());
			}else{
				phone.setNUMBER(txVO.getCon_tel());
			}
			phoneList.add(phone);
		}
		
		// 現職公司電話
		if(!isBlank(txVO.getCmpy_tel())){
			phone = new CBSPhoneList();
			phone.setCODE("4");
			phone.setTYPE("現職公司電話");
			if(!isBlank(txVO.getCmpy_ext())){
				phone.setNUMBER(txVO.getCmpy_tel()+"#"+txVO.getCmpy_ext());
			}else{
				phone.setNUMBER(txVO.getCmpy_tel());
			}
			phoneList.add(phone);
		}
		
		//行動電話
		if(!isBlank(txVO.getHandphone())){
			phone = new CBSPhoneList();
			phone.setCODE("5");
			phone.setTYPE("行動電話");
			phone.setNUMBER(txVO.getHandphone());
			phoneList.add(phone);
		}
		
		//傳真電話
		if(!isBlank(txVO.getFax())){
			phone = new CBSPhoneList();
			phone.setCODE("6");
			phone.setTYPE("傳真電話");
			phone.setNUMBER(txVO.getFax());
			phoneList.add(phone);
		}
		outputVO.setPhoneList(phoneList);
		this.sendRtnObject(outputVO);
	}

	/** 已無使用 Eli 註解 **/
//	public void wrong_phone_number(Object body, IPrimitiveMap header) throws Exception {
//		CRM621InputVO inputVO = (CRM621InputVO) body;
//
//		XmlInfo xmlInfo = new XmlInfo();
//		Map<String, String> htlid_map = xmlInfo.doGetVariable("CRM.FC032154_HTLID", FormatHelper.FORMAT_3);
//
//		String htlid = htlid_map.get("1");
//
//		String custID = inputVO.getCust_id();
//
//		String htxtid = CUST_MAINTAIN;
//
//		// init util
//		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
//		esbUtilInputVO.setModule(thisClaz + new Object() {
//		}.getClass().getEnclosingMethod().getName());
//
//		for (Map<String, String> data : inputVO.getDatas()) {
//
//			String cod_01 = data.get("COD");
//			String tel_no_01 = data.get("TEL_NO");
//			String day_use_01 = data.get("DAY_USE");
//			String night_use_01 = data.get("NIGHT_USE");
//			String voice_use_01 = data.get("VOICE_USE");
//			String fax_use_01 = data.get("FAX_USE");
//			String typ_01 = data.get("TYP");
//
//			// head
//			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
//			txHead.setDefaultTxHead();
//			txHead.setHTLID(htlid);
//			esbUtilInputVO.setTxHeadVO(txHead);
//
//			// body
//			FC032154InputVO fc032154InputVO = new FC032154InputVO();
//			fc032154InputVO.setCUST_NO(custID); // 客戶統一編號
//			fc032154InputVO.setFUNC_01("2"); // 功能(1:新增 2:修改 3:刪除)
//			fc032154InputVO.setFORM_RMK_01("Y"); // 失聯電話註記
//			fc032154InputVO.setDAY_USE_01(day_use_01); // 日間註記
//			fc032154InputVO.setNIGHT_USE_01(night_use_01); // 晚上註記
//			fc032154InputVO.setVOICE_USE_01(voice_use_01); // 語音註記
//			fc032154InputVO.setFAX_USE_01(fax_use_01); // 傳真註記
//			fc032154InputVO.setCOD_01(cod_01); // 代碼
//			fc032154InputVO.setTEL_NO_01(tel_no_01); // 電話號碼
//			fc032154InputVO.setTYP_01(typ_01); // 電話類別
//			esbUtilInputVO.setFc032154InputVO(fc032154InputVO);
//
//			// 發送電文
//			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
//		}
//
//		this.sendRtnObject(null);
//
//	}

}