package com.systex.jbranch.app.server.fps.mtc110;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.systex.jbranch.app.common.fps.table.TBMTC_CONTRACT_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBMTC_CONTRACT_DETAILVO;
import com.systex.jbranch.app.common.fps.table.TBMTC_CONTRACT_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBMTC_PDFVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032671.FP032671OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author 	Carley
 * @date 	2020/10/12
 */
@Component("mtc110")
@Scope("request")
public class MTC110 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam;
	
	public void updateStatus(Object body, IPrimitiveMap header) throws JBranchException {
		MTC110InputVO inputVO = (MTC110InputVO) body;
		dam = this.getDataAccessManager();
		TBMTC_CONTRACT_MAINVO vo = (TBMTC_CONTRACT_MAINVO) dam.findByPKey(TBMTC_CONTRACT_MAINVO.TABLE_UID, inputVO.getCON_NO());
		if (null != vo) {
			vo.setCON_STATUS(inputVO.getStatus());	// 狀態(S：暫存　C：確認送出　D：刪除)
			dam.update(vo);
		} else {
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		this.sendRtnObject(null);
	}

	public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		MTC110InputVO inputVO = (MTC110InputVO) body;
		MTC110OutputVO outputVO = new MTC110OutputVO();
		dam = this.getDataAccessManager();
		TBMTC_CONTRACT_MAINVO vo = new TBMTC_CONTRACT_MAINVO();
		//
		Boolean twd_yn = false;
		if ("T".equals(inputVO.getCON_CURR())) {
			twd_yn = true;
		}
		//
		Boolean addNew = false;
		String con_no = inputVO.getCON_NO();
		if (StringUtils.isNotBlank(con_no)) {
			vo = (TBMTC_CONTRACT_MAINVO) dam.findByPKey(TBMTC_CONTRACT_MAINVO.TABLE_UID, con_no);
		} else {
			addNew = true;
			con_no = this.getCON_NO();
			vo.setCON_NO(con_no);
		}
		vo.setCON_TYPE("S");	// 信託類別（S：自益　O：他益）
		vo.setCUST_ID(inputVO.getCustList1() != null ? (objectToString(inputVO.getCustList1().get("CUST_ID"))) : null);
		vo.setCON_CURR(inputVO.getCON_CURR());
		vo.setCURR1(twd_yn ? "TWD" : inputVO.getCURR1());
		vo.setAMT1(this.getBigDecimal(inputVO.getAMT1()));
		vo.setCURR2("T".equals(inputVO.getCON_CURR()) ? null : inputVO.getCURR2());
		vo.setAMT2("T".equals(inputVO.getCON_CURR()) ? null : this.getBigDecimal(inputVO.getAMT2()));
		vo.setCURR3("T".equals(inputVO.getCON_CURR()) ? null : inputVO.getCURR3());
		vo.setAMT3("T".equals(inputVO.getCON_CURR()) ? null : this.getBigDecimal(inputVO.getAMT3()));
		vo.setSIGNING_FEE(this.getBigDecimal(inputVO.getSIGNING_FEE()));
		vo.setMODIFY_FEE(this.getBigDecimal(inputVO.getMODIFY_FEE()));
		vo.setMNG_FEE_RATE1(this.getBigDecimal(inputVO.getMNG_FEE_RATE1()));
		vo.setMNG_FEE_RATE2(this.getBigDecimal(inputVO.getMNG_FEE_RATE2()));
		vo.setMNG_FEE_RATE3(this.getBigDecimal(inputVO.getMNG_FEE_RATE3()));
		vo.setMNG_FEE_MIN(this.getBigDecimal(inputVO.getMNG_FEE_MIN()));
		vo.setEND_YEARS(this.getBigDecimal(inputVO.getEND_YEARS()));
		vo.setEND_AMT_LIMIT(this.getBigDecimal(inputVO.getEND_AMT_LIMIT()));
		vo.setTERM_CON(inputVO.getTERM_CON());
		vo.setMODI_CON(inputVO.getMODI_CON());
		vo.setDISC_CON(inputVO.getDISC_CON());
		vo.setAPP_SUP(inputVO.getAPP_SUP());
		vo.setDISC_ID(inputVO.getDISC_ID());
		//
		String pay_type = null;
		if (inputVO.getPAY_TYPE1() != null && inputVO.getPAY_TYPE2() != null &&
		    inputVO.getPAY_TYPE1() && inputVO.getPAY_TYPE2()) {
			pay_type = "1,2";
		} else if (inputVO.getPAY_TYPE1() != null && inputVO.getPAY_TYPE1()) {
			pay_type = "1";
		} else if (inputVO.getPAY_TYPE2() != null && inputVO.getPAY_TYPE2()) {
			pay_type = "2";
		}
		vo.setPAY_TYPE(pay_type);
		//
		String sip_type = null;
		if (inputVO.getSIP_TYPE1() != null && inputVO.getSIP_TYPE2() != null &&
			inputVO.getSIP_TYPE1() && inputVO.getSIP_TYPE2()) {
			sip_type = "1,2";
		} else if (inputVO.getSIP_TYPE1() != null && inputVO.getSIP_TYPE1()) {
			sip_type = "1";
		} else if (inputVO.getSIP_TYPE2() != null && inputVO.getSIP_TYPE2()) {
			sip_type = "2";
		}
		vo.setSIP_TYPE(sip_type);
		//
		vo.setAPL_CON(inputVO.getAPL_CON());
		//
		String apl_doc_yn = null;
		if (inputVO.getAPL_DOC_Y() != null && inputVO.getAPL_DOC_N() != null &&
			inputVO.getAPL_DOC_Y() && inputVO.getAPL_DOC_N()) {
			apl_doc_yn = "Y,N";
		} else if (inputVO.getAPL_DOC_Y() != null && inputVO.getAPL_DOC_Y()) {
			apl_doc_yn = "Y";
		} else if (inputVO.getAPL_DOC_N() != null && inputVO.getAPL_DOC_N()) {
			apl_doc_yn = "N";
		}
		vo.setAPL_DOC_YN(apl_doc_yn);
		//
		vo.setBEN_AGE_YN1(inputVO.getBEN_AGE_YN1() != null ? (inputVO.getBEN_AGE_YN1() ? "Y" : "N") : null);
		vo.setBEN_AGE1((inputVO.getBEN_AGE_YN1() != null && inputVO.getBEN_AGE_YN1()) ? this.getBigDecimal(inputVO.getBEN_AGE1()) : null);
		vo.setBEN_CURR1((inputVO.getBEN_AGE_YN1() != null && inputVO.getBEN_AGE_YN1()) ? (twd_yn ? "TWD" : inputVO.getBEN_CURR1()) : null);
		vo.setBEN_AMT1((inputVO.getBEN_AGE_YN1() != null && inputVO.getBEN_AGE_YN1()) ? this.getBigDecimal(inputVO.getBEN_AMT1()) : null);
		vo.setBEN_AGE_YN2(inputVO.getBEN_AGE_YN2() != null ? (inputVO.getBEN_AGE_YN2() ? "Y" : "N") : null);
		vo.setBEN_AGE2((inputVO.getBEN_AGE_YN2() != null && inputVO.getBEN_AGE_YN2()) ? this.getBigDecimal(inputVO.getBEN_AGE2()) : null);
		vo.setBEN_AGE3((inputVO.getBEN_AGE_YN2() != null && inputVO.getBEN_AGE_YN2()) ? this.getBigDecimal(inputVO.getBEN_AGE3()) : null);
		vo.setBEN_CURR2((inputVO.getBEN_AGE_YN2() != null && inputVO.getBEN_AGE_YN2()) ? (twd_yn ? "TWD" : inputVO.getBEN_CURR2()) : null);
		vo.setBEN_AMT2((inputVO.getBEN_AGE_YN2() != null && inputVO.getBEN_AGE_YN2()) ? this.getBigDecimal(inputVO.getBEN_AMT2()) : null);
		vo.setBEN_AGE_YN3(inputVO.getBEN_AGE_YN3() != null ? (inputVO.getBEN_AGE_YN3() ? "Y" : "N") : null);
		vo.setBEN_AGE4((inputVO.getBEN_AGE_YN3() != null && inputVO.getBEN_AGE_YN3()) ? this.getBigDecimal(inputVO.getBEN_AGE4()) : null);
		vo.setBEN_CURR3((inputVO.getBEN_AGE_YN3() != null && inputVO.getBEN_AGE_YN3()) ? (twd_yn ? "TWD" : inputVO.getBEN_CURR3()) : null);
		vo.setBEN_AMT3((inputVO.getBEN_AGE_YN3() != null && inputVO.getBEN_AGE_YN3()) ? this.getBigDecimal(inputVO.getBEN_AMT3()) : null);
		
//		vo.setAGR_MON_TYPE(inputVO.getAGR_MON_TYPE());
		String agr_mon_type = null;
		if (inputVO.getAGR_MON_TYPE_M() != null && inputVO.getAGR_MON_TYPE_A() != null &&
			inputVO.getAGR_MON_TYPE_M() && inputVO.getAGR_MON_TYPE_A()) {
			agr_mon_type = "M,A";
		} else if (inputVO.getAGR_MON_TYPE_M() != null && inputVO.getAGR_MON_TYPE_M()) {
			agr_mon_type = "M";
		} else if (inputVO.getAGR_MON_TYPE_A() != null && inputVO.getAGR_MON_TYPE_A()) {
			agr_mon_type = "A";
		}
		vo.setAGR_MON_TYPE(agr_mon_type);
		
		vo.setM_CURR((inputVO.getAGR_MON_TYPE_M() != null && inputVO.getAGR_MON_TYPE_M()) ? (twd_yn ? "TWD" : inputVO.getM_CURR()) : null);
		vo.setM_AMT((inputVO.getAGR_MON_TYPE_M() != null && inputVO.getAGR_MON_TYPE_M()) ? this.getBigDecimal(inputVO.getM_AMT()) : null);
		
		// 依約定月份給付_每年幾月：1~12（逗號分隔，代表1~12月）
		String a_months = null;
		if (inputVO.getAGR_MON_TYPE_A() != null && CollectionUtils.isNotEmpty(inputVO.getList1())) {
			for (String month : inputVO.getList1()) {
				if (a_months != null) {
					a_months += ("," + month);
				} else {
					a_months = month;
				}
			}
		}
		vo.setA_MONTHS(a_months);
		//
		vo.setA_CURR((inputVO.getAGR_MON_TYPE_A() != null && inputVO.getAGR_MON_TYPE_A()) ? (twd_yn ? "TWD" : inputVO.getA_CURR()) : null);
		vo.setA_AMT((inputVO.getAGR_MON_TYPE_A() != null && inputVO.getAGR_MON_TYPE_A()) ? this.getBigDecimal(inputVO.getA_AMT()) : null);
		vo.setUNLIMIT_DOC_YN(inputVO.getAPL_DOC_N() != null ? (inputVO.getAPL_DOC_N() ? "Y" : "N") : null);
		vo.setUNLIMIT_DOC_TYPE(inputVO.getUNLIMIT_DOC_TYPE());
		vo.setLIMIT_DOC_YN(inputVO.getAPL_DOC_Y() != null ? (inputVO.getAPL_DOC_Y() ? "Y" : "N") : null);
		vo.setLIMIT_DOC_TYPE(inputVO.getLIMIT_DOC_TYPE());
		vo.setEDU_YN(inputVO.getEDU_YN() != null ? (inputVO.getEDU_YN() ? "Y" : "N") : null);
		vo.setMED_YN(inputVO.getMED_YN() != null ? (inputVO.getMED_YN() ? "Y" : "N") : null);
//		vo.setMED_PAY_FOR(inputVO.getMED_PAY_FOR());
		//
		String med_pay_for = null;
		if (inputVO.getMED_PAY_FOR_A() != null && inputVO.getMED_PAY_FOR_B() != null &&
			inputVO.getMED_PAY_FOR_A() && inputVO.getMED_PAY_FOR_B()) {
			med_pay_for = "A,B";
		} else if (inputVO.getMED_PAY_FOR_A() != null && inputVO.getMED_PAY_FOR_A()) {
			med_pay_for = "A";
		} else if (inputVO.getMED_PAY_FOR_B() != null && inputVO.getMED_PAY_FOR_B()) {
			med_pay_for = "B";
		}
		vo.setMED_PAY_FOR(med_pay_for);
		//
		vo.setNUR_YN(inputVO.getNUR_YN() != null ? (inputVO.getNUR_YN() ? "Y" : "N") : null);
//		vo.setNUR_PAY_FOR(inputVO.getNUR_PAY_FOR());
		//
		String nur_pay_for = null;
		if (inputVO.getNUR_PAY_FOR_A() != null && inputVO.getNUR_PAY_FOR_B() != null &&
			inputVO.getNUR_PAY_FOR_A() && inputVO.getNUR_PAY_FOR_B()) {
			nur_pay_for = "A,B";
		} else if (inputVO.getNUR_PAY_FOR_A() != null && inputVO.getNUR_PAY_FOR_A()) {
			nur_pay_for = "A";
		} else if (inputVO.getNUR_PAY_FOR_B() != null && inputVO.getNUR_PAY_FOR_B()) {
			nur_pay_for = "B";
		}
		vo.setNUR_PAY_FOR(nur_pay_for);
		//
		vo.setOTR_YN(inputVO.getOTR_YN() != null ? (inputVO.getOTR_YN() ? "Y" : "N") : null);
		
		Boolean checkOtr = (inputVO.getOTR_YN() != null && inputVO.getOTR_YN());
		vo.setMAR_CURR(checkOtr ? this.checkCurr(inputVO.getMAR_CURR(), inputVO.getMAR_AMT(), twd_yn) : null);
		vo.setMAR_AMT(checkOtr  ? this.getBigDecimal(inputVO.getMAR_AMT()) : null);
		vo.setBIR_CURR(checkOtr ? this.checkCurr(inputVO.getBIR_CURR(), inputVO.getBIR_AMT(), twd_yn) : null);
		vo.setBIR_AMT(checkOtr  ? this.getBigDecimal(inputVO.getBIR_AMT()) : null);
		vo.setHOS_CURR(checkOtr ? this.checkCurr(inputVO.getHOS_CURR(), inputVO.getHOS_AMT(), twd_yn) : null);
		vo.setHOS_AMT(checkOtr  ? this.getBigDecimal(inputVO.getHOS_AMT()) : null);
		vo.setOTR_CURR(checkOtr ? this.checkCurr(inputVO.getOTR_CURR(), inputVO.getOTR_AMT(), twd_yn) : null);
		vo.setOTR_AMT(checkOtr  ? this.getBigDecimal(inputVO.getOTR_AMT()) : null);
		vo.setOTR_ITEM(checkOtr ? inputVO.getOTR_ITEM() : null);
		vo.setCON_STATUS("S");	// S：暫存、C：確認送出、D：刪除
		
		if (addNew) {
			dam.create(vo);
		} else {
			dam.update(vo);
		}
		outputVO.setCON_NO(con_no);
		
		// TBMTC_CONTRACT_DETAIL
		if (inputVO.getCustList1() != null && inputVO.getCustList1().size() > 0 && 
		    StringUtils.isNotBlank(this.objectToString(inputVO.getCustList1().get("CUST_ID")))
		) {
			Date birth_date = null;
			String birth_date_s = inputVO.getBIRTH_DATE();
			if (StringUtils.isNotBlank(birth_date_s)) {
				String[] birth = birth_date_s.split("/");
				String bir_y = birth[0] == null ? "0" : birth[0];
				String bir_m = birth[1] == null ? "0" : birth[1];
				String bir_d = birth[2] == null ? "0" : birth[2];
				int y = Integer.parseInt(bir_y) + 1911; 
				birth_date_s = String.valueOf(y) + "/" + bir_m + "/" + bir_d;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				birth_date = sdf.parse(birth_date_s);
			}
			
			this.mainDetail(con_no, inputVO.getCustList1(), "1", twd_yn, birth_date);
		} else {
			this.deleteDetail(con_no, "1");
		}
		
		if (inputVO.getCustList2() != null && inputVO.getCustList2().size() > 0 &&
			StringUtils.isNotBlank(this.objectToString(inputVO.getCustList2().get("CUST_ID")))
		) {
			this.mainDetail(con_no, inputVO.getCustList2(), "2", twd_yn, null);
		} else {
			this.deleteDetail(con_no, "2");
		}
		
		if (inputVO.getCustList3() != null && inputVO.getCustList3().size() > 0 &&
			StringUtils.isNotBlank(this.objectToString(inputVO.getCustList3().get("CUST_ID")))
		) {
			this.mainDetail(con_no, inputVO.getCustList3(), "3", twd_yn, null);
		} else {
			this.deleteDetail(con_no, "3");
		}
		
		if (inputVO.getCustList4() != null && inputVO.getCustList4().size() > 0 &&
			StringUtils.isNotBlank(this.objectToString(inputVO.getCustList4().get("CUST_ID")))
		) {
			this.mainDetail(con_no, inputVO.getCustList4(), "4", twd_yn, null);
		} else {
			this.deleteDetail(con_no, "4");
		}
		
		if (inputVO.getCustList5() != null && inputVO.getCustList5().size() > 0 &&
			StringUtils.isNotBlank(this.objectToString(inputVO.getCustList5().get("CUST_ID")))
		) {
			this.mainDetail(con_no, inputVO.getCustList5(), "5", twd_yn, null);
		} else {
			this.deleteDetail(con_no, "5");
		}
		
		this.sendRtnObject(outputVO);
	}
	
	private String checkCurr(String curr, String amt, Boolean twd_yn) {
		String str = null;
		if (twd_yn && StringUtils.isNotBlank(amt)) {
			str = "TWD";
		} else {
			if (StringUtils.isNotBlank(curr)) {
				str = curr;
			}
		}
		return str;
	}
	
	private void deleteDetail(String con_no, String type) throws JBranchException {
		dam = this.getDataAccessManager();
		TBMTC_CONTRACT_DETAILPK pk = new TBMTC_CONTRACT_DETAILPK();
		pk.setCON_NO(con_no);
		pk.setREL_TYPE(type);
		TBMTC_CONTRACT_DETAILVO vo = (TBMTC_CONTRACT_DETAILVO) dam.findByPKey(TBMTC_CONTRACT_DETAILVO.TABLE_UID, pk);
		if (null != vo)
			dam.delete(vo);
	}
	
	private void mainDetail(String con_no, Map<String,Object> map, String type, Boolean twd_yn, Date birth_date) throws JBranchException {
		dam = this.getDataAccessManager();
		TBMTC_CONTRACT_DETAILPK pk = new TBMTC_CONTRACT_DETAILPK();
		pk.setCON_NO(con_no);
		pk.setREL_TYPE(type);
		TBMTC_CONTRACT_DETAILVO vo = (TBMTC_CONTRACT_DETAILVO) dam.findByPKey(TBMTC_CONTRACT_DETAILVO.TABLE_UID, pk);
		Boolean addNew = false;
		if (null == vo) {
			addNew = true;
		}
		if (addNew) {
			vo = new TBMTC_CONTRACT_DETAILVO();
			vo.setcomp_id(pk);
		}
		vo.setCUST_ID(objectToString(map.get("CUST_ID")));
		vo.setCUST_NAME(objectToString(map.get("CUST_NAME")));
		vo.setBIRTH_DATE((birth_date != null && "1".equals(type)) ? new Timestamp(birth_date.getTime()) : null);
		vo.setTEL(objectToString(map.get("TEL")));
		vo.setMOBILE_NO(objectToString(map.get("MOBILE_NO")));
		vo.setEMAIL(objectToString(map.get("EMAIL")));
		vo.setCEN_ZIP_CODE(objectToString(map.get("CEN_ZIP_CODE")));
		vo.setCEN_ADDRESS(objectToString(map.get("CEN_ADDRESS")));
		vo.setCOM_ZIP_CODE(objectToString(map.get("COM_ZIP_CODE")));
		vo.setCOM_ADDRESS(objectToString(map.get("COM_ADDRESS")));
		String agr_acc_type = objectToString(map.get("AGR_ACC_TYPE"));
		vo.setAGR_ACC_TYPE(agr_acc_type);
		
		String agr_bra_nbr = null;
		if (StringUtils.isNotBlank(agr_acc_type)) {
			if ("A".equals(agr_acc_type)) {
				// 本行
				String agr_acc = objectToString(map.get("AGR_ACC"));
				if (StringUtils.isNotBlank(agr_acc)) {
					String[] agrInfo = agr_acc.split("-");
					agr_acc = agrInfo[0];
					agr_bra_nbr = agrInfo[1];
				}
				
				//委託人約定給付帳號為空 或 監察人有報酬及給付且約定給付帳號為空，則顯示錯誤
				if(StringUtils.isBlank(agr_acc)) {
					if(StringUtils.equals("1", type)) throw new APException("『委託人』約定給付帳號為必要欄位。");
					if(StringUtils.equals("2", type) && StringUtils.equals("Y", objectToString(map.get("PAY_YN")))) throw new APException("『監察人1』約定給付帳號為必要欄位。");
					if(StringUtils.equals("3", type) && StringUtils.equals("Y", objectToString(map.get("PAY_YN")))) throw new APException("『監察人2』約定給付帳號為必要欄位。");
				}
				
				vo.setAGR_ACC(agr_acc);
				vo.setAGR_BRA_NBR(agr_bra_nbr);
				vo.setOTR_BANK(null);
				vo.setOTR_BRANCH(null);
				vo.setOTR_ACC(null);
				
			} else if ("B".equals(agr_acc_type)) {
				// 他行
				vo.setAGR_ACC(null);
				vo.setAGR_BRA_NBR(agr_bra_nbr);
				vo.setOTR_BANK(objectToString(map.get("OTR_BANK")));
				vo.setOTR_BRANCH(objectToString(map.get("OTR_BRANCH")));
				vo.setOTR_ACC(objectToString(map.get("OTR_ACC")));
			}
		}
		if ("1".equals(type)) {
			String minor_yn = objectToString(map.get("MINOR_YN"));
			vo.setMINOR_YN(minor_yn == null ? "N" : minor_yn);
			vo.setLEG_AGENT_ID1(objectToString(map.get("LEG_AGENT_ID1")));
			vo.setLEG_AGENT_ID2(objectToString(map.get("LEG_AGENT_ID2")));
			vo.setLEG_AGENT_NAME1(objectToString(map.get("LEG_AGENT_NAME1")));
			vo.setLEG_AGENT_NAME2(objectToString(map.get("LEG_AGENT_NAME2")));
			vo.setLEG_AGENT_TEL1(objectToString(map.get("LEG_AGENT_TEL1")));
			vo.setLEG_AGENT_TEL2(objectToString(map.get("LEG_AGENT_TEL2")));
			vo.setLEG_AGENT_REL1(objectToString(map.get("LEG_AGENT_REL1")));
			vo.setLEG_AGENT_REL2(objectToString(map.get("LEG_AGENT_REL2")));
			vo.setLEG_AGENT_OTR1(objectToString(map.get("LEG_AGENT_OTR1")));
			vo.setLEG_AGENT_OTR2(objectToString(map.get("LEG_AGENT_OTR2")));
			
			vo.setSEAL_RETENTION_MTD(objectToString(map.get("SEAL_RETENTION_MTD")));
			vo.setSEAL_UNDER7(objectToString(map.get("SEAL_UNDER7")));
			vo.setSEAL_UNDER7_NAME(objectToString(map.get("SEAL_UNDER7_NAME")));
			vo.setSEAL_UNDER20_1(objectToString(map.get("SEAL_UNDER20_1")));
			vo.setSEAL_UNDER20_2(objectToString(map.get("SEAL_UNDER20_2")));
			vo.setSEAL_UNDER20_NAME(objectToString(map.get("SEAL_UNDER20_NAME")));
		}
		vo.setPAY_YN(objectToString(map.get("PAY_YN")));
		vo.setPAY_CURR(map.get("PAY_CURR") != null ? (twd_yn ? "TWD" : "USD") : null);	// 交付幣別為外幣：給付監察費幣別預設「美金」，不可改（2021/2/23）
		vo.setPAY_AMT(map.get("PAY_AMT") != null ? this.getBigDecimal(map.get("PAY_AMT")) : null);
		
		if (!"1".equals(type)) {
			// 報酬及給付頻率 M：每月　S：每季（1. 4. 7. 10月當月）　H：每半年（1. 7月當月）　1～12月（單選）
			String pay_freq = objectToString(map.get("PAY_FREQ"));
			if (StringUtils.isNotBlank(pay_freq) && "A".equals(pay_freq)) {
				pay_freq = objectToString(map.get("PAY_FREQ_A"));
			}
//			if (freqList == null) {
//				pay_freq = map.get("PAY_FREQ") != null ? map.get("PAY_FREQ").toString() : null;
//			} else {
//				for (String month : freqList) {
//					if (pay_freq != null) {
//						pay_freq += ("," + month);
//					} else {
//						pay_freq = month;
//					}
//				}
//			}
			vo.setPAY_FREQ(pay_freq);
		}
		
		// 所有監察人的解任方式皆以"監察人1"為準
		String dis_type = null;
		if ("2".equals(type)) {
			dis_type = objectToString(map.get("DIS_TYPE"));
		};
		vo.setDIS_TYPE(dis_type);
		
		if (addNew) {
			dam.create(vo);
		} else {
			dam.update(vo);
		}
	}
	
	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		MTC110InputVO inputVO = (MTC110InputVO) body;
		MTC110OutputVO outputVO = new MTC110OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<>(); 
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT D.MINOR_YN, D2.CUST_ID AS CUST_ID2, D3.CUST_ID AS CUST_ID3, NVL(C.CUST_ID, 'N') AS CUST_YN, ");
		sb.append("C.CUST_NAME, B.DEPT_ID AS BRANCH_NBR, O.DEPT_NAME AS BRANCH_NAME, ");		
		sb.append("A.*, A.CREATETIME + 60 AS EXPIRY_DATE ");
		sb.append("FROM TBMTC_CONTRACT_MAIN A LEFT JOIN TBORG_MEMBER B ON A.CREATOR = B.EMP_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST C ON A.CUST_ID = C.CUST_ID ");
		// for 前端檢核
		sb.append("LEFT JOIN ( ");
		sb.append("SELECT CON_NO, REL_TYPE, NVL(MINOR_YN, 'N') AS MINOR_YN FROM TBMTC_CONTRACT_DETAIL WHERE REL_TYPE = '1' ");
		sb.append(") D ON A.CON_NO = D.CON_NO ");
		sb.append("LEFT JOIN (SELECT CON_NO, REL_TYPE, CUST_ID FROM TBMTC_CONTRACT_DETAIL WHERE REL_TYPE = '4') D2 ON A.CON_NO = D2.CON_NO ");
		sb.append("LEFT JOIN (SELECT CON_NO, REL_TYPE, CUST_ID FROM TBMTC_CONTRACT_DETAIL WHERE REL_TYPE = '5') D3 ON A.CON_NO = D3.CON_NO ");
		//
		sb.append("LEFT JOIN TBORG_DEFN O ON B.DEPT_ID = O.DEPT_ID ");
		sb.append("WHERE 1 = 1 AND A.CON_STATUS <> 'D' ");
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);		// 總行
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); 				// 理專
		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2); 			// 理專FCH
		
		String loginRole = (String)getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		if (fcMap.containsKey(loginRole) || fchMap.containsKey(loginRole)) {
			// 理專
			sb.append("AND A.CREATOR = :loginID ");
			queryCondition.setObject("loginID", getCommonVariable(FubonSystemVariableConsts.LOGINID));
			
		} else if (!headmgrMap.containsKey(loginRole)) {
			// 非總行人員查詢
			sb.append("AND B.DEPT_ID IN (:branch_nbr_list ) ");
			queryCondition.setObject("branch_nbr_list", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		// 委託人ID
		if(StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND A.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		// 區間(起)
		if(inputVO.getPeriod_s() != null){
			sb.append("AND TRUNC(A.CREATETIME) >= TRUNC( :start ) ");
			queryCondition.setObject("start", inputVO.getPeriod_s());
		}
		
		// 區間(迄)
		if(inputVO.getPeriod_e() != null){
			sb.append("AND TRUNC(A.CREATETIME) <= TRUNC( :end ) ");
			queryCondition.setObject("end", inputVO.getPeriod_e());
		}
		
		// 狀態
		if(StringUtils.isNotBlank(inputVO.getCON_STATUS()) && !"A".equals(inputVO.getCON_STATUS())){
			sb.append("AND A.CON_STATUS = :con_status ");
			queryCondition.setObject("con_status", inputVO.getCON_STATUS());
		}
		
		sb.append("ORDER BY DECODE(A.CON_STATUS, 'S',1), A.CREATETIME DESC ");
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		for (Map<String,Object> map : resultList) {
			String pay_type = null;
			String sip_type = null;
			String apl_doc_yn = null;
			String agr_mon_type = null;
			String med_pay_for = null;
			String nur_pay_for = null;
			Boolean ben_age_yn1 = null;
			Boolean ben_age_yn2 = null;
			Boolean ben_age_yn3 = null;
			Boolean edu_yn = null;
			Boolean med_yn = null;
			Boolean nur_yn = null;
			Boolean otr_yn = null;
			
			for(String key : map.keySet()) {
				if ("PAY_TYPE".equals(key) && map.get("PAY_TYPE") != null) {
					pay_type = map.get("PAY_TYPE").toString();
				}
				if ("SIP_TYPE".equals(key) && map.get("SIP_TYPE") != null) {
					sip_type = map.get("SIP_TYPE").toString();
				}
				if ("APL_DOC_YN".equals(key) && map.get("APL_DOC_YN") != null) {
					apl_doc_yn = map.get("APL_DOC_YN").toString();
				}
				if ("AGR_MON_TYPE".equals(key) && map.get("AGR_MON_TYPE") != null) {
					agr_mon_type = map.get("AGR_MON_TYPE").toString();
				}
				
				if ("MED_PAY_FOR".equals(key) && map.get("MED_PAY_FOR") != null) {
					med_pay_for = map.get("MED_PAY_FOR").toString();
				}
				if ("NUR_PAY_FOR".equals(key) && map.get("NUR_PAY_FOR") != null) {
					nur_pay_for = map.get("NUR_PAY_FOR").toString();
				}
				if ("BEN_AGE_YN1".equals(key) && map.get("BEN_AGE_YN1") != null) {
					if ("Y".equals(map.get("BEN_AGE_YN1").toString())) {
						ben_age_yn1 = true;
					} else if ("N".equals(map.get("BEN_AGE_YN1").toString())) {
						ben_age_yn1 = false;
					}
				}
				if ("BEN_AGE_YN2".equals(key) && map.get("BEN_AGE_YN2") != null) {
					if ("Y".equals(map.get("BEN_AGE_YN2").toString())) {
						ben_age_yn2 = true;
					} else if ("N".equals(map.get("BEN_AGE_YN2").toString())) {
						ben_age_yn2 = false;
					}
				}
				if ("BEN_AGE_YN3".equals(key) && map.get("BEN_AGE_YN3") != null) {
					if ("Y".equals(map.get("BEN_AGE_YN3").toString())) {
						ben_age_yn3 = true;
					} else if ("N".equals(map.get("BEN_AGE_YN3").toString())) {
						ben_age_yn3 = false;
					}
				}
				if ("EDU_YN".equals(key) && map.get("EDU_YN") != null) {
					if ("Y".equals(map.get("EDU_YN").toString())) {
						edu_yn = true;
					} else if ("N".equals(map.get("EDU_YN").toString())) {
						edu_yn = false;
					}
				}
				if ("MED_YN".equals(key) && map.get("MED_YN") != null) {
					if ("Y".equals(map.get("MED_YN").toString())) {
						med_yn = true;
					} else if ("N".equals(map.get("MED_YN").toString())) {
						med_yn = false;
					}
				}
				if ("NUR_YN".equals(key) && map.get("NUR_YN") != null) {
					if ("Y".equals(map.get("NUR_YN").toString())) {
						nur_yn = true;
					} else if ("N".equals(map.get("NUR_YN").toString())) {
						nur_yn = false;
					}
				}
				if ("OTR_YN".equals(key) && map.get("OTR_YN") != null) {
					if ("Y".equals(map.get("OTR_YN").toString())) {
						otr_yn = true;
					} else if ("N".equals(map.get("OTR_YN").toString())) {
						otr_yn = false;
					}
				}
			}
			
			if (StringUtils.isNotBlank(pay_type)) {
				if (pay_type.length() > 1) {
					map.put("PAY_TYPE1", true);
					map.put("PAY_TYPE2", true);
				} else {
					map.put("PAY_TYPE" + pay_type, true);
				}
			}
			
			if (StringUtils.isNotBlank(sip_type)) {
				if (sip_type.length() > 1) {
					map.put("SIP_TYPE1", true);
					map.put("SIP_TYPE2", true);
				} else {
					map.put("SIP_TYPE" + sip_type, true);
				}
			}
			
			if (StringUtils.isNotBlank(apl_doc_yn)) {
				if (apl_doc_yn.length() > 1) {
					map.put("APL_DOC_Y", true);
					map.put("APL_DOC_N", true);
				} else {
					map.put("APL_DOC_" + apl_doc_yn, true);
				}
			}
			
			if (StringUtils.isNotBlank(agr_mon_type)) {
				if (agr_mon_type.length() > 1) {
					map.put("AGR_MON_TYPE_M", true);
					map.put("AGR_MON_TYPE_A", true);
				} else {
					map.put("AGR_MON_TYPE_" + agr_mon_type, true);
				}
			}
			
			if (StringUtils.isNotBlank(med_pay_for)) {
				if (med_pay_for.length() > 1) {
					map.put("MED_PAY_FOR_A", true);
					map.put("MED_PAY_FOR_B", true);
				} else {
					map.put("MED_PAY_FOR_" + med_pay_for, true);
				}
			}
			
			if (StringUtils.isNotBlank(nur_pay_for)) {
				if (nur_pay_for.length() > 1) {
					map.put("NUR_PAY_FOR_A", true);
					map.put("NUR_PAY_FOR_B", true);
				} else {
					map.put("NUR_PAY_FOR_" + nur_pay_for, true);
				}
			}
			
			if (ben_age_yn1 != null) {
				map.put("BEN_AGE_YN1", ben_age_yn1);
			}
			if (ben_age_yn2 != null) {
				map.put("BEN_AGE_YN2", ben_age_yn2);
			}
			if (ben_age_yn3 != null) {
				map.put("BEN_AGE_YN3", ben_age_yn3);
			}
			if (edu_yn != null) {
				map.put("EDU_YN", edu_yn);
			}
			if (med_yn != null) {
				map.put("MED_YN", med_yn);
			}
			if (nur_yn != null) {
				map.put("NUR_YN", nur_yn);
			}
			if (otr_yn != null) {
				map.put("OTR_YN", otr_yn);
			}
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	public void getDetail (Object body, IPrimitiveMap header) throws JBranchException {
		MTC110InputVO inputVO = (MTC110InputVO) body;
		MTC110OutputVO outputVO = new MTC110OutputVO();
		outputVO.setResultList(getDetail(inputVO.getCON_NO()));
		this.sendRtnObject(outputVO);
	}
	
	private List<Map<String,Object>> getDetail(String con_no) throws JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<>();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append("CASE WHEN A.AGR_ACC_TYPE = 'A' THEN O.DEPT_NAME ELSE ( ");
		sb.append("SELECT TRIM(SNAME) FROM TBSYS_BANK_BRANCH_SG WHERE BRANCHID <> '0000' AND BANKID = A.OTR_BANK AND BRANCHID = A.OTR_BRANCH ");
		sb.append(") END AS BRA_NAME, TRIM(B.SNAME) AS OTR_BANK_NAME, ");
		sb.append("TO_CHAR(A.BIRTH_DATE, 'yyy/mm/dd', 'NLS_CALENDAR= ''ROC Official''') AS BIR_ROC, ");
		sb.append("TO_CHAR(A.BIRTH_DATE, 'yyy', 'NLS_CALENDAR= ''ROC Official''') AS BIR_Y, ");
		sb.append("TO_CHAR(A.BIRTH_DATE, 'MM') AS BIR_M, ");
		sb.append("TO_CHAR(A.BIRTH_DATE, 'DD') AS BIR_D, ");
		sb.append("CASE WHEN A.LEG_AGENT_ID1 IS NOT NULL THEN 'Y' ELSE 'N' END AS LEG_AGENT_YN, ");
		sb.append("CASE WHEN (SELECT CUST_ID FROM TBCRM_CUST_MAST WHERE CUST_ID = A.CUST_ID) IS NOT NULL THEN 'Y' ELSE 'N' END AS CUST_YN, ");
		sb.append("A.* FROM TBMTC_CONTRACT_DETAIL A ");
		sb.append("LEFT JOIN TBORG_DEFN O ON A.AGR_BRA_NBR = O.DEPT_ID ");
		sb.append("LEFT JOIN ( ");
		sb.append("SELECT * FROM TBSYS_BANK_BRANCH_SG WHERE BRANCHID = '0000' AND BANKID <> '012' ");
		sb.append(") B ON A.OTR_BANK = B.BANKID ");
		sb.append("WHERE A.CON_NO = :con_no ");
		queryCondition.setObject("con_no", con_no);
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		return resultList;
	}
	
	public void print(Object body, IPrimitiveMap header) throws Exception {
		MTC110InputVO inputVO = (MTC110InputVO) body;
		String con_no = inputVO.getCON_NO();
		String con_curr = inputVO.getCON_CURR() != null ? inputVO.getCON_CURR() : "";
		dam = this.getDataAccessManager();
		
		if (StringUtils.isNotBlank(con_no)) {
			List<Map<String,Object>> pdfList = new ArrayList<>();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT CON_PDF FROM TBMTC_PDF WHERE CON_NO = :con_no ");
			queryCondition.setObject("con_no", con_no);
			queryCondition.setQueryString(sb.toString());
			pdfList = dam.exeQuery(queryCondition);
	    	
    		if (pdfList.size() > 0 && pdfList.get(0).get("CON_PDF") != null) {
    			try {
					String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					String uuid = UUID.randomUUID().toString();
					String fileName = String.format("%s.pdf", uuid);
					Blob blob = (Blob) pdfList.get(0).get("CON_PDF");
					int blobLength = (int) blob.length();
					byte[] blobAsBytes = blob.getBytes(1, blobLength);

					File targetFile = new File(filePath, fileName);
					FileOutputStream fos = new FileOutputStream(targetFile);
					fos.write(blobAsBytes);
					fos.close();
					notifyClientViewDoc("temp//" + fileName, "pdf");
    			} catch (Exception e) {
		    	  throw new APException("ehl_01_common_035"); // 文件下載錯誤！
		    	}
			} else {
				// 開啟.pdf 並將.pdf 儲存至 TBMTC_PDFVO
				this.openPdfAndSave(inputVO);
			}
    		this.sendRtnObject(null);
		}
	}
	
    private void openPdfAndSave(MTC110InputVO inputVO) throws Exception {
    	String url = "";
		String txnCode = "MTC110";
		String con_no = inputVO.getCON_NO();
		String con_curr = inputVO.getCON_CURR();
		String reportID = "T".equals(con_curr) ? "R4" : "R5";
		ReportIF report = null;
		ArrayList<String> url_list = new ArrayList<String>() ; 
	
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();			// 取得傳輸資料給report模組的instance
		ReportGeneratorIF gen = factory.getGenerator(); // 取得產生PDF檔的instance
//		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		data.addParameter("CON_NO", inputVO.getCON_NO());
		report = gen.generateReport(txnCode, reportID, data);
		url_list.add(report.getLocation());
		
		reportID = "T".equals(con_curr) ? "R1" : "R2";
		report = null;
		data.addParameter("AMT1", inputVO.getAMT1());
		data.addParameter("AMT2", inputVO.getAMT2());
		data.addParameter("AMT3", inputVO.getAMT3());
		data.addParameter("CURR1", inputVO.getCURR1());
		data.addParameter("CURR2", inputVO.getCURR2());
		data.addParameter("CURR3", inputVO.getCURR3());
		data.addParameter("SIGNING_FEE", inputVO.getSIGNING_FEE());
		data.addParameter("MODIFY_FEE", inputVO.getMODIFY_FEE());
		data.addParameter("MNG_FEE_RATE1", inputVO.getMNG_FEE_RATE1());
		data.addParameter("MNG_FEE_RATE2", inputVO.getMNG_FEE_RATE2());
		data.addParameter("MNG_FEE_RATE3", inputVO.getMNG_FEE_RATE3());
		data.addParameter("MNG_FEE_MIN", inputVO.getMNG_FEE_MIN());
		data.addParameter("END_YEARS_YN", StringUtils.isNotBlank(inputVO.getEND_YEARS()) ? "■" : "□");
		data.addParameter("END_YEARS", inputVO.getEND_YEARS());
		data.addParameter("END_AMT_LIMIT_YN", StringUtils.isNotBlank(inputVO.getEND_AMT_LIMIT()) ? "■" : "□");
		data.addParameter("END_AMT_LIMIT", inputVO.getEND_AMT_LIMIT());
		
		String term_con = this.checkMapVal(inputVO.getTERM_CON());
		data.addParameter("TERM_CON_A", "A".equals(term_con) ? "■" : "□");
		data.addParameter("TERM_CON_B", "B".equals(term_con) ? "■" : "□");
		
		String modi_con = this.checkMapVal(inputVO.getMODI_CON());
		data.addParameter("MODI_CON_A", "A".equals(modi_con) ? "■" : "□");
		data.addParameter("MODI_CON_B", "B".equals(modi_con) ? "■" : "□");
		
		String disc_con = this.checkMapVal(inputVO.getDISC_CON());
		data.addParameter("DISC_CON_B", "B".equals(disc_con) ? "■" : "□");
		data.addParameter("DISC_CON_C", "C".equals(disc_con) ? "■" : "□");
		
		String app_sup = this.checkMapVal(inputVO.getAPP_SUP());
		data.addParameter("APP_SUP_C1", "C1".equals(app_sup) ? "■" : "□");
		data.addParameter("APP_SUP_C2", "C2".equals(app_sup) ? "■" : "□");
		
		String ben_age_yn  = "□";
		String ben_age_yn1 = "□";
		String ben_age_yn2 = "□";
		String ben_age_yn3 = "□";
		if (inputVO.getBEN_AGE_YN1() != null && inputVO.getBEN_AGE_YN1() == true) {
			ben_age_yn  = "■";
			ben_age_yn1 = "■";
		}
		if (inputVO.getBEN_AGE_YN2() != null && inputVO.getBEN_AGE_YN2() == true) {
			ben_age_yn  = "■";
			ben_age_yn2 = "■";
		}
		if (inputVO.getBEN_AGE_YN3() != null && inputVO.getBEN_AGE_YN3() == true) {
			ben_age_yn  = "■";
			ben_age_yn3 = "■";
		}
		data.addParameter("BEN_AGE_YN", ben_age_yn);
		data.addParameter("BEN_AGE_YN1", ben_age_yn1);
		data.addParameter("BEN_AGE_YN2", ben_age_yn2);
		data.addParameter("BEN_AGE_YN3", ben_age_yn3);
		
		data.addParameter("BEN_AGE1", inputVO.getBEN_AGE1());
		data.addParameter("BEN_AGE2", inputVO.getBEN_AGE2());
		data.addParameter("BEN_AGE3", inputVO.getBEN_AGE3());
		data.addParameter("BEN_AGE4", inputVO.getBEN_AGE4());
		
		data.addParameter("BEN_AMT1", inputVO.getBEN_AMT1());
		data.addParameter("BEN_AMT2", inputVO.getBEN_AMT2());
		data.addParameter("BEN_AMT3", inputVO.getBEN_AMT3());
		
		data.addParameter("BEN_CURR1", inputVO.getBEN_CURR1());
		data.addParameter("BEN_CURR2", inputVO.getBEN_CURR2());
		data.addParameter("BEN_CURR3", inputVO.getBEN_CURR3());
		
		data.addParameter("M_CURR", inputVO.getM_CURR());
		data.addParameter("A_CURR", inputVO.getA_CURR());
		
		String agr_mon_yn = "□";
		String agr_mon_m  = "□";
		String agr_mon_a  = "□";
		if (inputVO.getAGR_MON_TYPE_M() != null && inputVO.getAGR_MON_TYPE_M()) {
			agr_mon_yn = "■";
			agr_mon_m  = "■";
			data.addParameter("M_AMT", inputVO.getM_AMT());
		}
		if (inputVO.getAGR_MON_TYPE_A() != null && inputVO.getAGR_MON_TYPE_A()) {
			agr_mon_yn = "■";
			agr_mon_a  = "■";
			data.addParameter("A_MONTHS", inputVO.getA_MONTHS());
			data.addParameter("A_AMT", inputVO.getA_AMT());
		}
		data.addParameter("AGR_MON_YN", agr_mon_yn);
		data.addParameter("AGR_MON_M", agr_mon_m);
		data.addParameter("AGR_MON_A", agr_mon_a);
		
//		if (null != inputVO.getAGR_MON_TYPE()) {
//			if ("M".equals(inputVO.getAGR_MON_TYPE())) {
//				agr_mon_yn = "■";
//				agr_mon_m  = "■";
//				data.addParameter("M_AMT", inputVO.getM_AMT());
//			}
//			if ("A".equals(inputVO.getAGR_MON_TYPE())) {
//				agr_mon_yn = "■";
//				agr_mon_a  = "■";
//				data.addParameter("A_MONTHS", inputVO.getA_MONTHS());
//				data.addParameter("A_AMT", inputVO.getA_AMT());
//			}
//			data.addParameter("AGR_MON_YN", agr_mon_yn);
//			data.addParameter("AGR_MON_M", agr_mon_m);
//			data.addParameter("AGR_MON_A", agr_mon_a);
//		}
		
		String unlimit_doc_a = "□";
		String unlimit_doc_b = "□";
		if (null != inputVO.getUNLIMIT_DOC_YN() && "Y".equals(inputVO.getUNLIMIT_DOC_YN()) && null != inputVO.getUNLIMIT_DOC_TYPE()) {
			if ("A".equals(inputVO.getUNLIMIT_DOC_TYPE())) {
				unlimit_doc_a = "■";
			}
			if ("B".equals(inputVO.getUNLIMIT_DOC_TYPE())) {
				unlimit_doc_b = "■";
			}
		}
		data.addParameter("UNLIMIT_DOC_A", unlimit_doc_a);
		data.addParameter("UNLIMIT_DOC_B", unlimit_doc_b);
		
		String limit_doc_a = "□";
		String limit_doc_b = "□";
		if (null != inputVO.getLIMIT_DOC_YN() && "Y".equals(inputVO.getLIMIT_DOC_YN()) && null != inputVO.getLIMIT_DOC_TYPE()) {
			if ("A".equals(inputVO.getLIMIT_DOC_TYPE())) {
				limit_doc_a = "■";
			}
			if ("B".equals(inputVO.getLIMIT_DOC_TYPE())) {
				limit_doc_b = "■";
			}
		}
		data.addParameter("LIMIT_DOC_A", limit_doc_a);
		data.addParameter("LIMIT_DOC_B", limit_doc_b);
		
		String edu_yn = inputVO.getEDU_YN() == null ? "□" : (inputVO.getEDU_YN() ? "■" : "□");
		data.addParameter("EDU_YN", edu_yn);
		
		String med_yn = inputVO.getMED_YN() == null ? "□" : (inputVO.getMED_YN() ? "■" : "□");
		data.addParameter("MED_YN", med_yn);
		
		String med_pay_a = "□";
		String med_pay_b = "□";
		if (null != inputVO.getMED_YN() && inputVO.getMED_YN()) {
			if (inputVO.getMED_PAY_FOR_A() != null && inputVO.getMED_PAY_FOR_A()) {
				med_pay_a = "■";
			}
			if (inputVO.getMED_PAY_FOR_B() != null && inputVO.getMED_PAY_FOR_B()) {
				med_pay_b = "■";
			}
		}
		data.addParameter("MED_PAY_A", med_pay_a);
		data.addParameter("MED_PAY_B", med_pay_b);
		
		String nur_yn = inputVO.getNUR_YN() == null ? "□" : (inputVO.getNUR_YN() ? "■" : "□");
		data.addParameter("NUR_YN", nur_yn);
		
		String nur_pay_a = "□";
		String nur_pay_b = "□";
		if (null != inputVO.getNUR_YN() && inputVO.getNUR_YN()) {
			if (inputVO.getNUR_PAY_FOR_A() != null && inputVO.getNUR_PAY_FOR_A()) {
				nur_pay_a = "■";
			}
			if (inputVO.getNUR_PAY_FOR_B() != null && inputVO.getNUR_PAY_FOR_B()) {
				nur_pay_b = "■";
			}
		}
		data.addParameter("NUR_PAY_A", nur_pay_a);
		data.addParameter("NUR_PAY_B", nur_pay_b);
		
		String otr_yn = inputVO.getOTR_YN() == null ? "□" : (inputVO.getOTR_YN() ? "■" : "□");
		data.addParameter("OTR_YN", otr_yn);
		
		String mar_yn = "□";
		if (null != inputVO.getMAR_CURR() || null != inputVO.getMAR_AMT()) {
			mar_yn = "■";
			data.addParameter("MAR_CURR", inputVO.getMAR_CURR());
			if (null != inputVO.getMAR_AMT()) {
				data.addParameter("MAR_AMT", inputVO.getMAR_AMT());
			}
		}
		data.addParameter("MAR_YN", mar_yn);
		
		String bir_yn = "□";
		if (null != inputVO.getBIR_CURR() || null != inputVO.getBIR_AMT()) {
			data.addParameter("BIR_CURR", inputVO.getBIR_CURR());
			bir_yn = "■";
			if (null != inputVO.getBIR_AMT()) {
				data.addParameter("BIR_AMT", inputVO.getBIR_AMT());
			}
		}
		data.addParameter("BIR_YN", bir_yn);
		
		String hos_yn = "□";
		if (null != inputVO.getHOS_CURR() || null != inputVO.getHOS_AMT()) {
			data.addParameter("HOS_CURR", inputVO.getHOS_CURR());
			hos_yn = "■";
			if (null != inputVO.getHOS_AMT()) {
				data.addParameter("HOS_AMT", inputVO.getHOS_AMT());
			}
		}
		data.addParameter("HOS_YN", hos_yn);
		
		String ootr_yn = "□";
		if (null != inputVO.getOTR_CURR() || null != inputVO.getOTR_AMT()) {
			data.addParameter("OTR_AMT", inputVO.getOTR_AMT());
			ootr_yn = "■";
			if (null != inputVO.getOTR_AMT()) {
				data.addParameter("OTR_CURR", inputVO.getOTR_CURR());
			}
		}
		data.addParameter("OOTR_YN", ootr_yn);
		data.addParameter("OTR_ITEM", inputVO.getOTR_ITEM());
		
		// TBMTC_CONTRACT_DETAIL
		String disc_id = inputVO.getDISC_ID();	// 運用決定權條件＿推派監察人ID
		String disc_name = "";
		String dis_type_1 = "□";
		String dis_type_2 = "□";
		
		String seal_under7_1 = "□";
		String seal_under7_2 = "□";
		String seal_under7_name = "";
		String seal_under20_11 = "□";
		String seal_under20_12 = "□";
		String seal_under20_21 = "□";
		String seal_under20_22 = "□";
		String seal_under20_name = "";
		
		//簽約檢核表
		String chk_cust_name1 = "□";
		String chk_cust_name2 = "□";
		String chk_cust_name3 = "□";
		String chk_cust_name4 = "□";
		String chk_cust_name5 = "□";
		String chk_leg_name1 = "□";
		String chk_leg_name2 = "□";
		
		List<Map<String,Object>> resultList = this.getDetail(con_no);
		
		for (Map<String,Object> map : resultList) {
			if (map.get("REL_TYPE") != null) {
				String cust_id = this.checkMapVal(map.get("CUST_ID"));
				if (StringUtils.isNotBlank(disc_id) && StringUtils.isNotBlank(cust_id)) {
					if (disc_id.equals(cust_id)) {
						disc_name = this.checkMapVal(map.get("CUST_NAME"));
					}
				}
				String rel_type = map.get("REL_TYPE").toString();
				data.addParameter("CUST_ID" + rel_type, cust_id);
				data.addParameter("CUST_NAME" + rel_type, this.checkMapVal(map.get("CUST_NAME")));
				data.addParameter("TEL" + rel_type, this.checkMapVal(map.get("TEL")));
				data.addParameter("MOBILE_NO" + rel_type, this.checkMapVal(map.get("MOBILE_NO")));
				data.addParameter("EMAIL" + rel_type, this.checkMapVal(map.get("EMAIL")));
				data.addParameter("COM_ZIP_CODE" + rel_type, this.checkMapVal(map.get("COM_ZIP_CODE")));
				data.addParameter("COM_ADDRESS" + rel_type, this.checkMapVal(map.get("COM_ADDRESS")));
				
				String agr_acc_type = this.checkMapVal(map.get("AGR_ACC_TYPE"));
				
				String otr_bank_name = this.checkMapVal(map.get("OTR_BANK_NAME"));
				String bank = agr_acc_type.equals("A") ? "台北富邦" : (agr_acc_type.equals("B") ? otr_bank_name : "");
				data.addParameter("BANK" + rel_type, bank);
				
//				String branch = agr_acc_type.equals("A") ? this.checkMapVal(map.get("AGR_BRANCH")) : this.checkMapVal(map.get("OTR_BRANCH"));
//				String branch = this.checkMapVal(map.get("BRA_NAME"));
				String branch = (agr_acc_type.equals("A") ? "台北富邦銀行" : "") + this.checkMapVal(map.get("BRA_NAME"));
				data.addParameter("BRANCH" + rel_type, branch);
				
				String acc = agr_acc_type.equals("A") ? this.checkMapVal(map.get("AGR_ACC")) : (agr_acc_type.equals("B") ? this.checkMapVal(map.get("OTR_ACC")) : "");
				data.addParameter("ACC" + rel_type, acc);
				
				// 委託人
				if ("1".equals(rel_type)) {
					XmlInfo xmlInfo = new XmlInfo();
					Map<String, String> relMap = xmlInfo.doGetVariable("MTC.LEG_AGENT_REL", FormatHelper.FORMAT_3);
					String bir_y = this.checkMapVal(map.get("BIR_Y"));
					if ("0".equals(bir_y.charAt(0))) {
						bir_y = bir_y.substring(1);
					}
					data.addParameter("BIR_Y", bir_y);
					data.addParameter("BIR_M", this.checkMapVal(map.get("BIR_M")));
					data.addParameter("BIR_D", this.checkMapVal(map.get("BIR_D")));
					data.addParameter("CEN_ZIP_CODE" + rel_type, this.checkMapVal(map.get("CEN_ZIP_CODE")));
					data.addParameter("CEN_ADDRESS" + rel_type, this.checkMapVal(map.get("CEN_ADDRESS")));
					
					String leg_agent_yn = "Y".equals(this.checkMapVal(map.get("LEG_AGENT_YN"))) ? "■" : "□";
					data.addParameter("LEG_AGENT_YN", leg_agent_yn);
					data.addParameter("LEG_AGENT_ID1", this.checkMapVal(map.get("LEG_AGENT_ID1")));
					data.addParameter("LEG_AGENT_NAME1", this.checkMapVal(map.get("LEG_AGENT_NAME1")));
					data.addParameter("LEG_AGENT_TEL1", this.checkMapVal(map.get("LEG_AGENT_TEL1")));
					data.addParameter("LEG_AGENT_ID2", this.checkMapVal(map.get("LEG_AGENT_ID2")));
					data.addParameter("LEG_AGENT_NAME2", this.checkMapVal(map.get("LEG_AGENT_NAME2")));
					data.addParameter("LEG_AGENT_TEL2", this.checkMapVal(map.get("LEG_AGENT_TEL2")));
					
					String leg_agent_rel_key1 = this.checkMapVal(map.get("LEG_AGENT_REL1"));
					String leg_agent_rel_key2 = this.checkMapVal(map.get("LEG_AGENT_REL2"));
					data.addParameter("LEG_AGENT_REL1", relMap.get(leg_agent_rel_key1));
					data.addParameter("LEG_AGENT_REL2", relMap.get(leg_agent_rel_key2));
					
					String seal_under7 = this.checkMapVal(map.get("SEAL_UNDER7"));
					if ("1".equals(seal_under7)) {
						seal_under7_1 = "■";
					} else if ("2".equals(seal_under7)) {
						seal_under7_2 = "■";
						seal_under7_name = this.checkMapVal(map.get("SEAL_UNDER7_NAME"));
					}
					
					String seal_under20_1 = this.checkMapVal(map.get("SEAL_UNDER20_1"));
					if ("1".equals(seal_under20_1)) {
						seal_under20_11 = "■";
					} else if ("2".equals(seal_under20_1)) {
						seal_under20_12 = "■";
						
						String seal_under20_2 = this.checkMapVal(map.get("SEAL_UNDER20_2"));
						if ("1".equals(seal_under20_2)) {
							seal_under20_21 = "■";
						} else if ("2".equals(seal_under20_2)) {
							seal_under20_22 = "■";
							seal_under20_name = this.checkMapVal(map.get("SEAL_UNDER20_NAME"));
						}
					}
				}
				
				// 監察人1
				if ("2".equals(rel_type)) {
					String dis_type = this.checkMapVal(map.get("DIS_TYPE"));
					if ("1".equals(dis_type)) {
						dis_type_1 = "■";
					} else if ("2".equals(dis_type)) {
						dis_type_2 = "■";
					}
				}
				
				String pay_yn = this.checkMapVal(map.get("PAY_YN"));
				String pay_y = "□";
				String pay_n = "□";
				String pay_freq_m = "□";
				String pay_freq_s = "□";
				String pay_freq_h = "□";
				String pay_freq_a = "□";
				if ("Y".equals(pay_yn)) {
					pay_y = "■";
					String pay_amt = this.checkMapVal(map.get("PAY_AMT"));
					String curr = "T".equals(con_curr) ? "TWD " : "USD ";
					data.addParameter("PAY_AMT" + rel_type, curr + pay_amt);
					
					String pay_freq = this.checkMapVal(map.get("PAY_FREQ"));
					switch (pay_freq) {
						case "M" :
							pay_freq_m = "■";
							break;
						case "S" :
							pay_freq_s = "■";
							break;
						case "H" :
							pay_freq_h = "■";
							break;
					}
					if (pay_freq.length() >= 1 && !"M".equals(pay_freq) && !"S".equals(pay_freq) && !"H".equals(pay_freq)) {
						pay_freq_a = "■";
						data.addParameter("PAY_FREQ_AM" + rel_type, pay_freq);
					}
				} else {
					pay_n = "■";
				}
				data.addParameter("PAY_Y" + rel_type, pay_y);
				data.addParameter("PAY_N" + rel_type, pay_n);
				data.addParameter("PAY_FREQ_M" + rel_type, pay_freq_m);
				data.addParameter("PAY_FREQ_S" + rel_type, pay_freq_s);
				data.addParameter("PAY_FREQ_H" + rel_type, pay_freq_h);
				data.addParameter("PAY_FREQ_A" + rel_type, pay_freq_a);
				
				switch (rel_type) {
				case "1" :
					if(StringUtils.isNotBlank(this.checkMapVal(map.get("CUST_NAME")))) chk_cust_name1 = "■";
					if(StringUtils.isNotBlank(this.checkMapVal(map.get("LEG_AGENT_NAME1")))) chk_leg_name1 = "■";
					if(StringUtils.isNotBlank(this.checkMapVal(map.get("LEG_AGENT_NAME2")))) chk_leg_name2 = "■";
					break;
				case "2" :
					if(StringUtils.isNotBlank(this.checkMapVal(map.get("CUST_NAME")))) chk_cust_name2 = "■";
					break;
				case "3" :
					if(StringUtils.isNotBlank(this.checkMapVal(map.get("CUST_NAME")))) chk_cust_name3 = "■";
					break;
				case "4":
					if(StringUtils.isNotBlank(this.checkMapVal(map.get("CUST_NAME")))) chk_cust_name4 = "■";
					break;
				case "5":
					if(StringUtils.isNotBlank(this.checkMapVal(map.get("CUST_NAME")))) chk_cust_name5 = "■";
					break;
				}
			}
		}
		data.addParameter("DISC_NAME",  disc_name);
		data.addParameter("DIS_TYPE_1", dis_type_1);
		data.addParameter("DIS_TYPE_2", dis_type_2);
		
		data.addParameter("SEAL_UNDER7_1",     seal_under7_1);
		data.addParameter("SEAL_UNDER7_2",     seal_under7_2);
		data.addParameter("SEAL_UNDER7_NAME",  seal_under7_name);
		data.addParameter("SEAL_UNDER20_11",   seal_under20_11);
		data.addParameter("SEAL_UNDER20_12",   seal_under20_12);
		data.addParameter("SEAL_UNDER20_21",   seal_under20_21);
		data.addParameter("SEAL_UNDER20_22",   seal_under20_22);
		data.addParameter("SEAL_UNDER20_NAME", seal_under20_name);
		
		report = gen.generateReport(txnCode, reportID, data);
		String url_first = "T".equals(con_curr) ? "doc//MTC//MTC110_TWD_1.pdf" : "doc//MTC//MTC110_USD_1.pdf";
		String url_last = "T".equals(con_curr) ? "doc//MTC//MTC110_TWD_2.pdf" : "doc//MTC//MTC110_USD_2.pdf";
		url_list.add(url_first);
		url_list.add(report.getLocation());
		url_list.add(url_last);
//		String fileName = "自益" + ("T".equals(con_curr) ? "臺幣" : "外幣") + "金錢信託契約.pdf";
		
//		String txnCode = "MTC110";
//		String con_no = inputVO.getCON_NO();
//		String con_curr = inputVO.getCON_CURR();
		reportID = "R3";
		report = null;
//		data = new ReportData();			// 取得傳輸資料給report模組的instance
		
		String cust_yn = this.checkMapVal(inputVO.getCUST_YN());
		String cust_y = "□";
		String cust_n = "□";
		if ("N".equals(cust_yn)) {
			cust_n = "■";
		} else if (cust_yn.length() > 1) {
			cust_y = "■";
		}
		data.addParameter("CUST_Y", cust_y);
		data.addParameter("CUST_N", cust_n);
		data.addParameter("MTC_Y", "■");
		data.addParameter("ITC_Y", "□");
		
		String con_nbr_t = "T".equals(con_curr) ? "2" : "";
		String con_nbr_f = "F".equals(con_curr) ? "2" : "";
		data.addParameter("CON_NBR_T", con_nbr_t);
		data.addParameter("CON_NBR_F", con_nbr_f);
		data.addParameter("BRANCH_NBR", this.checkMapVal(inputVO.getBRANCH_NBR()));
		data.addParameter("BRANCH_NAME", this.checkMapVal(inputVO.getBRANCH_NAME()));
		
		data.addParameter("CHK_CUST_NAME1", chk_cust_name1);
		data.addParameter("CHK_CUST_NAME2", chk_cust_name2);
		data.addParameter("CHK_CUST_NAME3", chk_cust_name3);
		data.addParameter("CHK_CUST_NAME4", chk_cust_name4);
		data.addParameter("CHK_CUST_NAME5", chk_cust_name5);
		data.addParameter("CHK_LEG_NAME1", chk_leg_name1);
		data.addParameter("CHK_LEG_NAME2", chk_leg_name2);
		
		report = gen.generateReport(txnCode, reportID, data);
		url_list.add(report.getLocation());
		
		// 合併所有PDF＆加密碼
		String reportURL = PdfUtil.mergePDF(url_list, false);
		
		if (url_list.size() > 0) {
			notifyClientViewDoc(reportURL, "pdf");
			// 將.pdf 儲存至 TBMTC_PDFVO
			String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
			byte[] reportData = Files.readAllBytes(new File(serverPath, reportURL).toPath());
			
			TBMTC_PDFVO pdfVO = new TBMTC_PDFVO();
			pdfVO.setCON_NO(con_no);
			pdfVO.setCON_PDF(ObjectUtil.byteArrToBlob(reportData));
			dam.create(pdfVO);
		}
    }
	
	/**
     * 合併 PDF 檔案
     * @param files
     * @param savepath
	 * @throws APException 
     */
    public void mergePdfFiles(ArrayList<String> files, String uuid) throws DocumentException, APException{	
        try {	
    		String savaPath= SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH)+"reports" + System.getProperties().getProperty("file.separator") + uuid+".pdf";
            Document document = new Document(new PdfReader(files.get(0)).getPageSize(1));
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(savaPath));
            document.open();
            for (int i = 0; i < files.size(); i++) {
                PdfReader reader = new PdfReader(files.get(i));
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
            }
            document.close();  
        } catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
    }
    
	// 取得客戶基本資料 
    public void getCustInfo(Object body, IPrimitiveMap header) throws Exception {
    	MTC110InputVO inputVO = (MTC110InputVO) body;
    	MTC110OutputVO outputVO = new MTC110OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<>(); 
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			String cust_id = inputVO.getCust_id();
			
			// 先檢核ID
			ValidUtil.isValidIDorRCNumber(cust_id);
			
			FP032675OutputVO fp032675OutputVO = new FP032675OutputVO();
			WMS032154OutputDetailsVO wms032154OutputDetailsVO = new WMS032154OutputDetailsVO();
			
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			SOT701InputVO sot701inputVO = new SOT701InputVO();
			sot701inputVO.setCustID(cust_id);
			
			// 查生日（TBCRM_CUST_MAST）
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
//			sb.append("SELECT CUST_NAME, BIRTH_DATE FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
			sb.append("SELECT CUST_NAME, BIRTH_DATE, ");
			sb.append("TO_CHAR(BIRTH_DATE, 'yyy/mm/dd', 'NLS_CALENDAR= ''ROC Official''') AS BIR_ROC ");
			sb.append("FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", cust_id);
			queryCondition.setQueryString(sb.toString());
			List<Map<String,Object>> birList = dam.exeQuery(queryCondition);
			
			String cust_name = "";
			Date birth_date = null;
			String bir_roc = "";
			if (birList != null && birList.size() > 0) {
				resultMap.put("CUST_YN", "Y");
				cust_name = birList.get(0).get("CUST_NAME") != null ? birList.get(0).get("CUST_NAME").toString() : "";
				birth_date = birList.get(0).get("BIRTH_DATE") != null ? ((Date) birList.get(0).get("BIRTH_DATE")) : null;	
				bir_roc = birList.get(0).get("BIR_ROC") != null ? birList.get(0).get("BIR_ROC").toString() : "";
				
				// 是本行客戶再打電文（若為當日開戶者，須先做KYC，就會馬上寫入客戶主檔）
				// 取得地址 信箱資料
				fp032675OutputVO = sot701.getAddrandMail(sot701inputVO);
				// 取得電文 電話資料
				wms032154OutputDetailsVO = sot701.getPhoneData(sot701inputVO);
				
			} else {
				resultMap.put("CUST_YN", "N");
			}
			
			resultMap.put("CUST_NAME", cust_name);
			resultMap.put("BIRTH_DATE", birth_date);
			resultMap.put("BIRTH_DATE", birth_date);
			resultMap.put("BIR_ROC", bir_roc);
			String resd_tel = wms032154OutputDetailsVO.getResd_tel();
			String resd_tel_ext = wms032154OutputDetailsVO.getResd_tel_ext();
			String tel = StringUtils.isNotBlank(resd_tel_ext) ? (resd_tel + "#" + resd_tel_ext) : resd_tel;
			resultMap.put("TEL", tel);
			resultMap.put("MOBILE_NO", wms032154OutputDetailsVO.getHandphone());
			resultMap.put("EMAIL", fp032675OutputVO.getE_MAIL());
			resultMap.put("CEN_ZIP_CODE", fp032675OutputVO.getZIP_COD1());
			resultMap.put("CEN_ADDRESS", fp032675OutputVO.getDATA1());
			resultMap.put("COM_ZIP_CODE", fp032675OutputVO.getZIP_COD3());
			resultMap.put("COM_ADDRESS", fp032675OutputVO.getDATA3());
			resultList.add(resultMap);
			outputVO.setResultList(resultList);
			this.sendRtnObject(outputVO);
		}
    }
	
	private String objectToString(Object val) throws JBranchException {
		if (val == null) {
			return null;
		} else {
			return val.toString();
		}
	}
	
	// 取得序號：分行別(4 碼)+西元日期(8 碼)+流水號(2 碼)
	// 信託契約序號規則由14碼改為15碼：分行別(3碼)＋西元日期(8碼)＋流水號(4碼) 2020/11/26
	private String getCON_NO() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		String LoginBrh = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH);
//		if (LoginBrh.length() == 3) {
//			LoginBrh = "0" + LoginBrh;
//		}
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());
		
		try {
			seqNum = LoginBrh + date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMTC_CONTRACT_MAIN")));
		} catch (Exception e) {
			sn.createNewSerial("TBMTC_CONTRACT_MAIN", "00", 1, "d", new Timestamp(new Date().getTime()) , 1, new Long("99"), "y", new Long("0"), null);
			seqNum = LoginBrh + date + String.format("%04d", Integer.valueOf(sn.getNextSerialNumber("TBMTC_CONTRACT_MAIN")));
		}
		return seqNum;
	}
	
	// 取得客戶帳號資訊
	public void getAcct(Object body, IPrimitiveMap header) throws Exception {
		MTC110InputVO inputVO = (MTC110InputVO) body;
    	MTC110OutputVO outputVO = new MTC110OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		List<FP032671OutputVO> list = new ArrayList<>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Map<String, String>> accMapList = new ArrayList<Map<String, String>>();
		
		String cust_id = inputVO.getCust_id();
		if (StringUtils.isNotBlank(cust_id)) {
			String con_curr = inputVO.getCON_CURR();
			
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			try {
				list = sot701.getFp032671AcctData(cust_id);
				//信託帳號
				if (list.size() > 0) {
					for (FP032671OutputVO vo : list) {
						for (FP032671OutputDetailsVO detail : vo.getDetails()) {
							if ("DEP".equals(detail.getACNO_CATG_1())) {
								if ("T".equals(con_curr) && "TWD".equals(detail.getCURR())) {
									// 台幣
									String checkACNO = detail.getACNO_1().substring(0, 8);	// 取前8碼檢核
									if (this.checkAcc("1", checkACNO)) {
										Map<String, Object> map = new HashMap<>();
										map.put("AGR_ACC", detail.getACNO_1());
										map.put("AGR_BRA_NBR", detail.getBRA());
										resultList.add(map);										
									}
								} else if ("F".equals(con_curr) && null != detail.getCURR() && !"TWD".equals(detail.getCURR())) {
									// 外幣
									if("XXX".equals(detail.getCURR())) {
										//因85081電文顯示的分行名稱為「帳務行」，遇到外幣時可能會發生帳務行與歸屬行不一致的情況（當外幣帳戶較晚開立時）
										//因此外幣分行代碼一律取<WA-X-CURR>=XXX時的<WA-X-BRANCH-NO>
										//先將XXX的帳號以及分行代碼記下來
										Map<String, String> map = new HashMap<String, String>();
										map.put("AGR_ACC", detail.getACNO_1());
										map.put("AGR_BRA_NBR", detail.getBRA());
										accMapList.add(map);
									} else {
										String checkACNO = detail.getACNO_1().substring(0, 8);	// 取前8碼檢核
										if (this.checkAcc("2", checkACNO)) {
											Map<String, Object> map = new HashMap<>();
											if (StringUtils.isNotBlank(inputVO.getPAY_CURR())) {
												if (detail.getCURR().equals(inputVO.getPAY_CURR())) {
													//帳號不存在才放入，外幣會有相同帳號不同幣別
													if(!checkInList(resultList, detail.getACNO_1())) {
														map.put("AGR_ACC", detail.getACNO_1());
														map.put("AGR_BRA_NBR", detail.getBRA());
														resultList.add(map);
													}
												}
											} else {
												//帳號不存在才放入，外幣會有相同帳號不同幣別
												if(!checkInList(resultList, detail.getACNO_1())) {
													map.put("AGR_ACC", detail.getACNO_1());
													map.put("AGR_BRA_NBR", detail.getBRA());//帳務行
	//												map.put("CURR", detail.getCURR());											
													resultList.add(map);	
												}
											}
										}
									}
								}
							}
						}
					}
				}
				
				//若為外幣信託，寫入歸屬行分行代碼
				if("F".equals(con_curr) && CollectionUtils.isNotEmpty(resultList) && CollectionUtils.isNotEmpty(accMapList)) {
					for (Map<String, Object> map : resultList) {
						for (Map<String, String> braMap : accMapList) {
							if(StringUtils.equals(map.get("AGR_ACC").toString(), braMap.get("AGR_ACC"))) {
								map.put("AGR_BRA_NBR", braMap.get("AGR_BRA_NBR"));
							}								
						}
					}
				}
				
				outputVO.setResultList(resultList);				
			} catch (Exception e) {
				e.printStackTrace();
				throw new APException(e.getMessage());
			}
			
		}
		sendRtnObject(outputVO);
	}
	
	/***
	 * 檢查帳號是否已存在List中
	 * @param list
	 * @param acct
	 * @return
	 */
	private Boolean checkInList(List<Map<String, Object>> list, String acct) {
		if(CollectionUtils.isEmpty(list))
			return false;
		
		for(Map<String, Object> map: list) {
			if(map.containsValue(acct))
				return true;
		}
		
		return false;
	}
	
	// 取得帳號分行名稱
	public void getBraName(Object body, IPrimitiveMap header) throws JBranchException {
		MTC110InputVO inputVO = (MTC110InputVO) body;
    	MTC110OutputVO outputVO = new MTC110OutputVO();
    	List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
    	
    	if(StringUtils.isNotBlank(inputVO.getBRA_NBR())){
    		dam = this.getDataAccessManager();
    		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		StringBuffer sb = new StringBuffer();
    		sb.append("SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = :dept_id ");
    		queryCondition.setObject("dept_id", inputVO.getBRA_NBR());
    		queryCondition.setQueryString(sb.toString());
    		resultList = dam.exeQuery(queryCondition);
    	}
    	outputVO.setResultList(resultList);
    	sendRtnObject(outputVO);
	}
	
	// 取得他行下拉 ( BRANCHID = '0000'; 排除台北富邦 <012> )
	public void getOtrBank(Object body, IPrimitiveMap header) throws JBranchException {
		MTC110OutputVO outputVO = new MTC110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TBSYS_BANK_BRANCH_SG WHERE BRANCHID = '0000' AND BANKID <> '012' ORDER BY BANKID ");
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
    	sendRtnObject(outputVO);
	}
	
	// 取得他行分行下拉
	public void getOtrBranch(Object body, IPrimitiveMap header) throws JBranchException {
		MTC110InputVO inputVO = (MTC110InputVO) body;
		MTC110OutputVO outputVO = new MTC110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM TBSYS_BANK_BRANCH_SG WHERE BRANCHID <> '0000' ");
		sb.append("AND BANKID = :bankid ORDER BY BRANCHID ");
		queryCondition.setObject("bankid", inputVO.getBank_id());
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
    	sendRtnObject(outputVO);
	}
	
	// 取得法定代理人資訊
	public void getLegInfo(Object body, IPrimitiveMap header) throws JBranchException {
		MTC110InputVO inputVO = (MTC110InputVO) body;
    	MTC110OutputVO outputVO = new MTC110OutputVO();
    	List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
    	
    	if(StringUtils.isNotBlank(inputVO.getCust_id())){
    		// 檢核身分證字號
    		ValidUtil.isValidIDorRCNumber(inputVO.getCust_id());
    		
    		dam = this.getDataAccessManager();
    		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		StringBuffer sb = new StringBuffer();
    		sb.append("SELECT M.CUST_ID, M.CUST_NAME, C.MOBILE_NO FROM TBCRM_CUST_MAST M ");
    		sb.append("LEFT JOIN TBCRM_CUST_CONTACT C ON M.CUST_ID = C.CUST_ID ");
    		sb.append("WHERE M.CUST_ID = :cust_id ");
    		queryCondition.setObject("cust_id", inputVO.getCust_id());
    		queryCondition.setQueryString(sb.toString());
    		resultList = dam.exeQuery(queryCondition);
    	}
    	outputVO.setResultList(resultList);
    	sendRtnObject(outputVO);
	}
	
	public BigDecimal getBigDecimal (Object value) {
		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				if (StringUtils.isNotBlank(value.toString())) {
					ret = new BigDecimal((String) value);					
				}
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
			}
		}
		return ret;
	}
	
	// 檢核帳號前8碼
	private Boolean checkAcc (String type, String acc) {
		if ("1".equals(type)) {
//			台幣：
//			舊帳號
//			XXXXX102、XXXXX104、XXXXX120、XXXXX168、XXXXX210、XXXXX221
//			新帳號
//			8211XXXX、8216XXXX、8212XXXX、8168XXXX、8221XXXX、8222XXXX
			if ("8".equals(acc.substring(0, 1))) {
				// 新帳號
				String checkNbr = acc.substring(0, 4);
				if ("8211".equals(checkNbr) || "8216".equals(checkNbr) || "8212".equals(checkNbr) || 
					"8168".equals(checkNbr) || "8221".equals(checkNbr) || "8222".equals(checkNbr)) {
					return true;
				}
			} else {
				// 舊帳號
				String checkNbr = acc.substring(5);
				if ("102".equals(checkNbr) || "104".equals(checkNbr) || "120".equals(checkNbr) || 
					"168".equals(checkNbr) || "210".equals(checkNbr) || "221".equals(checkNbr)) {
					return true;
				}
			}
		} else if ("2".equals(type)) {
//			外幣（黃底為OBU帳號，因信託部原本說要但又說不做法人，不過OBU目前只有法人，晚點再問信託部有沒有加這幾個）：
//			舊帳號
//			XXXXX168、XXXXX17、XXXXX18、0056017X, 0056018X
//			新帳號
//			8168XXXX、8311XXXX、8312XXXX、8381XXXX, 8382XXXX
			if ("8".equals(acc.substring(0, 1))) {
				// 新帳號
				String checkNbr = acc.substring(0, 4);
				if ("8168".equals(checkNbr) || "8311".equals(checkNbr) || "8312".equals(checkNbr) || 
					"8381".equals(checkNbr) || "8382".equals(checkNbr)) {
					return true;
				}
			} else {
				// 舊帳號
				String checkNbr = acc.substring(5);
				if ("168".equals(checkNbr))
					return true;
				checkNbr = acc.substring(5, 7);
				if ("17".equals(checkNbr) || "18".equals(checkNbr))
					return true;
				checkNbr = acc.substring(0, 7);
				if ("0056017".equals(checkNbr) || "0056018".equals(checkNbr))
					return true;
			}
		}
		return false;
	}
	
	private String checkMapVal(Object obj) {
		if (null == obj) {
			return "";
		} else {
			return obj.toString();
		}
	}
	
}