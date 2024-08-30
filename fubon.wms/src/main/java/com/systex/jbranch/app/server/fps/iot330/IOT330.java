package com.systex.jbranch.app.server.fps.iot330;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_BATCH_INFOVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_PPT_MAINVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT330
 * 
 * @author Kevin Hsu
 * @date 2016/10/03
 * @spec null
 */

@Component("iot330")
@Scope("request")
public class IOT330 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj;

	//2017/01/10 modify by jimmy 產險沒有檢核退件
//	public void Initial(Object body, IPrimitiveMap<Object> header)
//			throws JBranchException {
//		IOT330InputVO inputVO = (IOT330InputVO) body;
//		IOT330OutputVO outputVO = new IOT330OutputVO();
//		String loginBranch = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
//		dam_obj = this.getDataAccessManager();
//		QueryConditionIF qc = dam_obj
//				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuilder sb = new StringBuilder();
//		try {
//			sb.append(" SELECT OP_BATCH_NO,count(*) cnt ");
//			sb.append(" from VWIOT_MAIN ");
//			sb.append(" where status='42' and branch_nbr = :LOGINBRH ");
//			sb.append(" Group by OP_BATCH_NO ");
//			qc.setObject("LOGINBRH", loginBranch);
//			qc.setQueryString(sb.toString());
//			outputVO.setBounced(dam_obj.exeQuery(qc));
//			sendRtnObject(outputVO);
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s",
//					StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
//	}

	// 送件打包key
	public String op_Batch_No(String in_type) throws JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			if (in_type.equals("1"))
				sb.append("Select 'P'||LPAD(TBIOT_PPT_SEQ.nextval, 9, '0') AS op_Batch_No from dual   ");
			if (in_type.equals("2"))
				sb.append("Select 'C'||LPAD(TBIOT_PPT_SEQ.nextval, 9, '0') AS op_Batch_No from dual   ");
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> list1 = dam_obj.exeQuery(qc);
			String op_Batch_No = list1.get(0).get("OP_BATCH_NO").toString();
			return op_Batch_No;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("op_Batch_No__錯誤,請聯絡管理者");
		}
	}



	// 送件打包key
	public String batch_Info_Keyno() throws JBranchException {
		dam_obj = this.getDataAccessManager();
		StringBuilder sbs = new StringBuilder();

		QueryConditionIF qcs = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		try {
			sbs.append(" Select  TBIOT_MAIN_SEQ.NEXTVAL AS batch_info_keyno  from dual  ");
			qcs.setQueryString(sbs.toString());
			List<Map<String, Object>> list1 = dam_obj.exeQuery(qcs);
			String batch_info_keyno = list1.get(0).get("BATCH_INFO_KEYNO")
					.toString();
			return batch_info_keyno;

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("batch_info_keyno__錯誤,請聯絡管理者");
		}
	}

	public void queryData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT330InputVO inputVO = (IOT330InputVO) body;
		IOT330OutputVO outputVO = new IOT330OutputVO();
		//登入行
		String loginBranch = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);

		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT                 ");
			sb.append(" STATUS,                ");
			sb.append(" INS_KEYNO,             ");
			sb.append(" INS_ID,                ");
			sb.append(" APPLY_DATE,            ");
			sb.append(" INSURED_ID,            ");
			sb.append(" PROPOSER_NAME,            ");
			sb.append(" INSURED_NAME,          ");
			sb.append("	POLICY_NO,          	");
			sb.append(" PPT_TYPE,              ");
			sb.append(" OP_BATCH_NO,            ");
			sb.append(" DECODE (a.REJ_REASON, '10', a.REJ_REASON_OTH,"
					+ "(select b.PARAM_NAME from TBSYSPARAMETER b WHERE b.PARAM_TYPE = 'IOT.REJ_REASON' AND a.REJ_REASON = b.PARAM_CODE)) as REJ_REASON ");
			if("Y".equals(inputVO.getINCLUDED().toString())){
				sb.append(" FROM VWIOT_PPT_MAIN a WHERE BRANCH_NBR = :LOGINBRH AND STATUS IN ('10','20','30','42') ");
			}else{
				sb.append(" FROM VWIOT_PPT_MAIN a WHERE BRANCH_NBR = :LOGINBRH AND STATUS IN ('10','20','30') ");
			}
			qc.setObject("LOGINBRH", loginBranch);
			
			sb.append(" and trunc(KEYIN_DATE) >= NVL(to_date(:keyin_date_f,'yyyyMMdd') , KEYIN_DATE) ");
			if(inputVO.getKEYIN_DATE_F().equals(inputVO.getKEYIN_DATE_T()))
				sb.append(" and trunc(KEYIN_DATE) >= NVL(to_date(:keyin_date_t,'yyyyMMdd') , KEYIN_DATE) ");
			if(!inputVO.getKEYIN_DATE_F().equals(inputVO.getKEYIN_DATE_T()))
				sb.append(" and trunc(KEYIN_DATE) <= NVL(to_date(:keyin_date_t,'yyyyMMdd') , KEYIN_DATE) ");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String d_f_s = sdf.format(inputVO.getKEYIN_DATE_F());
			String d_t_s = sdf.format(inputVO.getKEYIN_DATE_T());
			qc.setObject("keyin_date_f", d_f_s);
			qc.setObject("keyin_date_t", d_t_s);
			
			if (!StringUtils.isBlank(inputVO.getREG_TYPE())) { //
				sb.append(" AND REG_TYPE=:REG_TYPEEE ");
			}
			// INCLUDED
			// if (!StringUtils.isBlank(inputVO.getINCLUDED())) { //
			// sb.append(" AND STATUS=:STATUSS ");
			// }
			
			// CUST_ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) { //
				sb.append(" And CUST_ID = :CUST_IDD ");
			}
			// INSURED_ID
			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) { //
				sb.append(" And INSURED_ID = :INSURED_IDD ");
			}
			// POLICY_NO
			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) { //
				sb.append(" And POLICY_NO = :POLICY_NOO ");
			}
			// INS_ID
			if (!StringUtils.isBlank(inputVO.getINS_ID())) { //
				sb.append(" And INS_ID = :INS_IDD ");
			}
			// OP_BATCH_OPRID
			if (!StringUtils.isBlank(inputVO.getOP_BATCH_OPRID())) { //
				sb.append(" And OP_BATCH_OPRID = :OP_BATCH_OPRIDD ");
			}
			sb.append(" order by status, OP_BATCH_NO, INS_ID ");
			qc.setQueryString(sb.toString());

			if (!StringUtils.isBlank(inputVO.getREG_TYPE())) { //
				qc.setObject("REG_TYPEEE", inputVO.getREG_TYPE());
			}

			// if (!StringUtils.isBlank(inputVO.getINCLUDED())) { //
			// qc.setObject("STATUSS", inputVO.getINCLUDED());
			// }


			// CUST_ID
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) { //
				qc.setObject("CUST_IDD", inputVO.getCUST_ID());
			}
			// INSURED_ID
			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) { //
				qc.setObject("INSURED_IDD", inputVO.getINSURED_ID());
			}

			// POLICY_NOO
			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) { //
				qc.setObject("POLICY_NOO", inputVO.getPOLICY_NO());
			}

			// INS_ID
			if (!StringUtils.isBlank(inputVO.getINS_ID())) { //
				qc.setObject("INS_IDD", inputVO.getINS_ID());
			}
			if (!StringUtils.isBlank(inputVO.getOP_BATCH_OPRID())) { //
				qc.setObject("OP_BATCH_OPRIDD", inputVO.getOP_BATCH_OPRID());
			}
			List<Map<String, Object>> list1 = dam_obj.exeQuery(qc);
			if(list1.size()>0){
				for(Map<String, Object> map:list1){
					List<Map<String, Object>> outList = new ArrayList<Map<String,Object>>();
					dam_obj = getDataAccessManager();
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT A.DOC_SEQ,DECODE(A.DOC_SEQ, '6', A.DOC_OTH, B.PARAM_NAME) as DOC_NAME ");
					sb.append(" FROM TBIOT_PPT_DOCCHK A  ");
					sb.append(" LEFT JOIN TBSYSPARAMETER B ");
					sb.append(" ON B.PARAM_CODE = A.DOC_SEQ ");
					sb.append(" WHERE B.PARAM_TYPE = 'IOT.PPT_DOC_SEQ' ");
					sb.append(" AND A.INS_KEYNO = :ins_keyno ");
					sb.append(" ORDER BY A.DOC_SEQ ");
					qc.setObject("ins_keyno", map.get("INS_KEYNO").toString());
					qc.setQueryString(sb.toString());
					outList = dam_obj.exeQuery(qc);
					if(outList.size()>0){
						map.put("outList", outList);
					}
				}
			}
			
			outputVO.setIOT_MAIN(list1);
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	public void submit(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		try {

			dam_obj = this.getDataAccessManager();
			IOT330InputVO inputVO = (IOT330InputVO) body;
			IOT330OutputVO outputVO = new IOT330OutputVO();
			Timestamp TM = new Timestamp(System.currentTimeMillis());
			// loginid
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			// 送件打包key
			String BATCH_INFO_KEYNO = batch_Info_Keyno();
			// OP_BATCH_NO
			String OP_BATCH_NO = op_Batch_No(inputVO.getREG_TYPE());
			List<Map<String, Object>> notpackList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> selectList = new ArrayList<Map<String,Object>>();
			//check pack
			for (Map<String, Object> IOT_MAIN : inputVO.getIOT_MAINList()) {
				BigInteger status_change = new BigDecimal(IOT_MAIN.get("STATUS")
						.toString()).toBigInteger();
				String status_change1 = status_change.toString();
				if("30".equals(status_change1) && "Y".equals(IOT_MAIN.get("CHECK"))){
					notpackList.add(IOT_MAIN);
					outputVO.setINS_KEYNO(IOT_MAIN.get("INS_KEYNO").toString());
					sendRtnObject(outputVO);
					break;
				}
				if(IOT_MAIN.get("CHECK") !=null){
					if("Y".equals(IOT_MAIN.get("CHECK").toString())){
						selectList.add(IOT_MAIN);
					}
				}
			}
			
			//do pack
			if(notpackList.size()<=0){
				// TBIOT_BATCH_INFO
				TBIOT_BATCH_INFOVO vos = new TBIOT_BATCH_INFOVO();
	
				vos.setBATCH_INFO_KEYNO(new BigDecimal(BATCH_INFO_KEYNO));
				vos.setOP_BATCH_DATE(TM);
				vos.setOP_BATCH_NO(OP_BATCH_NO);
				vos.setOP_BATCH_OPRID(loginID);
				dam_obj.create(vos);
				
				for (Map<String, Object> IOT_MAIN : selectList) {
				BigInteger status_change = new BigDecimal(IOT_MAIN.get("STATUS")
						.toString()).toBigInteger();
				String status_change1 = status_change.toString();
				//

				if (!"30".equals(status_change1)
						&& "Y".equals(IOT_MAIN.get("CHECK"))) {
					TBIOT_PPT_MAINVO vo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(
							TBIOT_PPT_MAINVO.TABLE_UID, new BigDecimal(IOT_MAIN
									.get("INS_KEYNO").toString()));
						if (vo != null) {
							vo.setSTATUS(new BigDecimal("30"));
							vo.setBATCH_INFO_KEYNO(new BigDecimal(BATCH_INFO_KEYNO));
							dam_obj.update(vo);
						}

					}

				}
				sendRtnObject(true);
			}

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("ehl_01_common_008");
		}
	}

	public void unPack(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT330InputVO inputVO = (IOT330InputVO) body;
		IOT330OutputVO outputVO = new IOT330OutputVO();
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");

		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		// 送件打包key
		String BATCH_INFO_KEYNO = batch_Info_Keyno();
		// OP_BATCH_NO
		String OP_BATCH_NO = op_Batch_No(inputVO.getREG_TYPE());
		List<Map<String, Object>> NotunpackList = new ArrayList<Map<String, Object>>();
		String loginRoleID = (String) getCommonVariable(SystemVariableConsts.LOGINROLE);
		// check unpack
		for (Map<String, Object> IOT_MAIN : inputVO.getIOT_MAINList()) {
			BigInteger status_change = new BigDecimal(IOT_MAIN.get("STATUS")
					.toString()).toBigInteger();
			String status_change1 = status_change.toString();
			if ("Y".equals(IOT_MAIN.get("CHECK"))) {
				if("N".equals(iot920.unPackchk(status_change1, loginRoleID))){
					NotunpackList.add(IOT_MAIN);
					outputVO.setINS_KEYNO(IOT_MAIN.get("INS_KEYNO").toString());
					sendRtnObject(outputVO);
					break;
				}
			}
		}
		if(NotunpackList.size() <= 0){
			for(Map<String, Object> IOT_MAIN : inputVO.getIOT_MAINList()){
				if ("Y".equals(IOT_MAIN.get("CHECK"))) {
					dam_obj = getDataAccessManager();
					TBIOT_PPT_MAINVO vo = new TBIOT_PPT_MAINVO();
					vo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(
							TBIOT_PPT_MAINVO.TABLE_UID, new BigDecimal(IOT_MAIN
									.get("INS_KEYNO").toString()));
					if (vo != null) {
						vo.setSTATUS(new BigDecimal("10"));
						vo.setBATCH_INFO_KEYNO(null);
						dam_obj.update(vo);
					}
				}
			}
			sendRtnObject(null);
		}
	}

	public void delData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT330InputVO inputVO = (IOT330InputVO) body;
		IOT330OutputVO outputVO = new IOT330OutputVO();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		// String loginRoleID = (String)
		// getCommonVariable(SystemVariableConsts.LOGINROLE);

		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();
		String chk = "";
		try {
			
			String abc=inputVO.getIOT_MAIN().get("INS_KEYNO").toString();
			TBIOT_PPT_MAINVO vo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(
					TBIOT_PPT_MAINVO.TABLE_UID, new BigDecimal(abc));
			if (vo != null) {
				vo.setSTATUS(new BigDecimal("99"));
				vo.setINS_ID(inputVO.getIOT_MAIN().get("INS_ID").toString() + "D");
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}
