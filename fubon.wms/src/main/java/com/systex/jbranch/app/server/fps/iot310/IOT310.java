package com.systex.jbranch.app.server.fps.iot310;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHKPK;
import com.systex.jbranch.app.common.fps.table.TBIOT_PPT_DOCCHKVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_PPT_MAINVO;
import com.systex.jbranch.app.server.fps.iot920.INSIDDataVO;
import com.systex.jbranch.app.server.fps.iot920.INSIDInputVOinputVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT310
 * 
 * @author Kevin Hsu
 * @date 2016/09/22
 * @spec null
 */
@Component("iot310")
@Scope("request")
public class IOT310 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	DataAccessManager dam_obj;

	public void submit(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT310InputVO inputVO = (IOT310InputVO) body;
		String seq1 = "";
		Timestamp TM = new Timestamp(System.currentTimeMillis());
		String loginbrh = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		String INS_ID = "";
		try {
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			// TBIOT_PPT_MAIN
			TBIOT_PPT_MAINVO vo = new TBIOT_PPT_MAINVO();
			// TBIOT_PPT_DOCCHK
			TBIOT_PPT_DOCCHKVO vok = new TBIOT_PPT_DOCCHKVO();
			TBIOT_PPT_DOCCHKPK vokpk = new TBIOT_PPT_DOCCHKPK();

			if (inputVO.getIn_OprStatus().equals("NEW")) {
				seq1 = getSEQ();
				vo.setINS_KEYNO(new BigDecimal(seq1));
			} else {
				if (StringUtils.isNotBlank(inputVO.getINS_KEYNO())) {
					sb.append("DELETE FROM TBIOT_PPT_DOCCHK  WHERE INS_KEYNO="
							+ inputVO.getINS_KEYNO().toString());
					qc.setQueryString(sb.toString());
					dam_obj.exeUpdate(qc);
					// vokpk.setINS_KEYNO(new
					// BigDecimal(inputVO.getINS_KEYNO()));
				}
				// vokpk.setINS_KEYNO(new BigDecimal(inputVO.getINS_KEYNO()));
			}

			if (!inputVO.getIn_OprStatus().equals("NEW"))
				vo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(
						TBIOT_PPT_MAINVO.TABLE_UID,
						new BigDecimal(inputVO.getINS_KEYNO()));

			if (vo != null) {
				if (inputVO.getAPPLY_DATE() != null) {
					vo.setAPPLY_DATE(new Timestamp(inputVO.getAPPLY_DATE()
							.getTime()));
				}

				if (inputVO.getKEYIN_DATE() != null) {
					vo.setKEYIN_DATE(new Timestamp(inputVO.getKEYIN_DATE()
							.getTime()));
				}

				if (!StringUtils.isBlank(inputVO.getPROPOSER_NAME())) {
					vo.setPROPOSER_NAME(inputVO.getPROPOSER_NAME().toUpperCase());
				}

				if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
					vo.setBRANCH_NBR(inputVO.getBRANCH_NBR());

				}
				if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
					vo.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
				}
				// Inkey
				if (inputVO.getIn_OprStatus().equals("NEW")) {
					IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
					INSIDInputVOinputVO iot920_inputVO = new INSIDInputVOinputVO();
					INSIDDataVO iot920_outputVO = new INSIDDataVO();
					iot920_inputVO.setBRANCH_NBR(loginbrh);
					iot920_inputVO.setINS_KIND("2");
					iot920_inputVO.setREG_TYPE(inputVO.getREG_TYPE());
					iot920_outputVO = iot920.getINS_ID(iot920_inputVO);
					INS_ID = iot920_outputVO.getINS_ID().toString();
					vo.setINS_ID(INS_ID);
				}
				if (!StringUtils.isBlank(inputVO.getREAL_PREMIUM())) { // 總保費
					String RP = inputVO.getREAL_PREMIUM().replaceAll(",", "");//去掉千分位符號
					vo.setREAL_PREMIUM(new BigDecimal(RP));
				}

				// REF_CON_ID
				if (!StringUtils.isBlank(inputVO.getREF_CON_ID())) { // 轉介編號
					vo.setREF_CON_ID(inputVO.getREF_CON_ID());
				}
				// RECRUIT_ID
				if (!StringUtils.isBlank(inputVO.getRECRUIT_ID())) { // 招攬人員編
					vo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
				}
				// 主鍵
				if (!StringUtils.isBlank(inputVO.getREG_TYPE()))
					vo.setREG_TYPE(inputVO.getREG_TYPE().toString());

				vo.setINS_KIND("2"); // 產險

				if (!StringUtils.isBlank(inputVO.getINSURED_ID())) {
					vo.setINSURED_ID(inputVO.getINSURED_ID().toUpperCase());
				}
				if (!StringUtils.isBlank(inputVO.getINSURED_NAME())) {
					vo.setINSURED_NAME(inputVO.getINSURED_NAME().toUpperCase());
				}
				if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
					vo.setPOLICY_NO(inputVO.getPOLICY_NO());
				}else{
					vo.setPOLICY_NO(null);
				}
				// if (!StringUtils.isBlank(inputVO.getPPT_DOC())) {
				// vo.setPPT_DOC(inputVO.getPPT_DOC());
				// }
				if (!StringUtils.isBlank(inputVO.getPPT_TYPE())) {
					vo.setPPT_TYPE(inputVO.getPPT_TYPE());
				}
				if (!StringUtils.isBlank(inputVO.getSTATUS())) {
					vo.setSTATUS(new BigDecimal(inputVO.getSTATUS()));
				}
				if (!StringUtils.isBlank(inputVO.getUNOPEN_ACCT())) {
					vo.setUNOPEN_ACCT(inputVO.getUNOPEN_ACCT());
				}
				vo.setSIGN_INC("N");
				if (inputVO.getIn_OprStatus().equals("NEW")) {
					dam_obj.create(vo);
				} else {
					dam_obj.update(vo);
				}

				// String[] c = inputVO.getDOC_SEQ().split("");
				if (inputVO.getDOC_SEQ() != null) {
					if (inputVO.getIn_OprStatus().equals("NEW"))
					for (int i = 0; i < inputVO.getDOC_SEQ().size(); i++) {
						dam_obj = this.getDataAccessManager();
						vokpk = new TBIOT_PPT_DOCCHKPK();
						Integer a = (inputVO.getDOC_SEQ().get(i)) + 1;
						BigDecimal c = new BigDecimal(a);
						vokpk.setDOC_SEQ(c);
						vokpk.setINS_KEYNO(new BigDecimal(seq1));
						vok = new TBIOT_PPT_DOCCHKVO();
						vok.setcomp_id(vokpk);
						if (!StringUtils.isBlank(inputVO.getPPT_DOC_OTHER())
								&& "6".equals(c.toString())) {
							vok.setDOC_OTH(inputVO.getPPT_DOC_OTHER());
						}
						dam_obj.create(vok);
					}
					if (!inputVO.getIn_OprStatus().equals("NEW"))
						for (int i = 0; i < inputVO.getDOC_SEQ().size(); i++) {
							dam_obj = this.getDataAccessManager();
							vokpk = new TBIOT_PPT_DOCCHKPK();
							Integer a = (inputVO.getDOC_SEQ().get(i)) + 1;
							BigDecimal c = new BigDecimal(a);
							vokpk.setDOC_SEQ(c);
							vokpk.setINS_KEYNO(new BigDecimal(inputVO.getINS_KEYNO().toString()));
							vok = new TBIOT_PPT_DOCCHKVO();
							vok.setcomp_id(vokpk);
							if (!StringUtils.isBlank(inputVO.getPPT_DOC_OTHER())
									&& "6".equals(c.toString())) {
								vok.setDOC_OTH(inputVO.getPPT_DOC_OTHER());
							}
							dam_obj.create(vok);
						}
				}

				sendRtnObject(null);
			}

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("ehl_01_common_008");
		}
	}

	public void submitUpdate(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT310InputVO inputVO = (IOT310InputVO) body;
		Timestamp TM = new Timestamp(System.currentTimeMillis());
		try {
			dam_obj = this.getDataAccessManager();

			// TBIOT_PPT_MAIN
			TBIOT_PPT_MAINVO vo = (TBIOT_PPT_MAINVO) dam_obj.findByPKey(
					TBIOT_PPT_MAINVO.TABLE_UID,
					new BigDecimal(inputVO.getINS_KEYNO()));

			if (inputVO.getAPPLY_DATE() != null) {
				vo.setAPPLY_DATE(new Timestamp(inputVO.getAPPLY_DATE()
						.getTime()));
			}

			if (!StringUtils.isBlank(inputVO.getPROPOSER_NAME())) {
				vo.setPROPOSER_NAME(inputVO.getPROPOSER_NAME().toUpperCase());
			}

			if (!StringUtils.isBlank(inputVO.getBRANCH_NBR())) {
				vo.setBRANCH_NBR(inputVO.getBRANCH_NBR());

			}
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				vo.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
			}
			// Inkey
//			vo.setINS_ID(Inkey(body, header));
			if (!StringUtils.isBlank(inputVO.getREAL_PREMIUM())) { // 總保費
				String RP = inputVO.getREAL_PREMIUM().replaceAll(",", "");//去掉千分位符號
				vo.setREAL_PREMIUM(new BigDecimal(RP));
			}

			// REF_CON_ID
			if (!StringUtils.isBlank(inputVO.getREF_CON_ID())) { // 轉介編號
				vo.setREF_CON_ID(inputVO.getREF_CON_ID());
			}
			// RECRUIT_ID
			if (!StringUtils.isBlank(inputVO.getRECRUIT_ID())) { // 招攬人員編
				vo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
			}

			// 主鍵
			vo.setINS_KEYNO(new BigDecimal(getSEQ()));
			vo.setREG_TYPE("2"); // 其他

			vo.setINS_KIND("1"); // 產險

			if (!StringUtils.isBlank(inputVO.getINSURED_ID())) {
				vo.setINSURED_ID(inputVO.getINSURED_ID().toUpperCase());
			}
			if (!StringUtils.isBlank(inputVO.getINSURED_NAME())) {
				vo.setINSURED_NAME(inputVO.getINSURED_NAME().toUpperCase());
			}
			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
				vo.setPOLICY_NO(inputVO.getPOLICY_NO());
			}
			// if (!StringUtils.isBlank(inputVO.getPPT_DOC())) {
			// vo.setPPT_DOC(inputVO.getPPT_DOC());
			// }
			// if (!StringUtils.isBlank(inputVO.getPPT_DOC_OTHER())) {
			// vo.setPPT_DOC_OTHER(inputVO.getPPT_DOC_OTHER());
			// }
			if (!StringUtils.isBlank(inputVO.getPPT_TYPE())) {
				vo.setPPT_TYPE(inputVO.getPPT_TYPE());
			}
			if (!StringUtils.isBlank(inputVO.getSTATUS())) {
				vo.setSTATUS(new BigDecimal(inputVO.getSTATUS()));
			}
			if (!StringUtils.isBlank(inputVO.getUNOPEN_ACCT())) {
				vo.setUNOPEN_ACCT(inputVO.getUNOPEN_ACCT());
			}
			vo.setSIGN_INC("N");
			dam_obj.create(vo);
			sendRtnObject(null);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("資料無法重複新增");
		}
	}

	//改call IOT920共用程式
//	public String Inkey(Object body, IPrimitiveMap<Object> header)
//			throws JBranchException {
//		IOT310InputVO inputVO = (IOT310InputVO) body;
//
//		dam_obj = this.getDataAccessManager();
//		QueryConditionIF qc = dam_obj
//				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuilder sb = new StringBuilder();
//		sb.append(" SELECT (SELECT SUBSTR(TO_CHAR(SYSDATE,'YYYY'),3,2) FROM DUAL) ||  "
//				+ "  (SELECT (CASE SUBSTR(MAX(A.INS_ID),6,9)   "
//				+ "                 WHEN '2000' THEN '0000'   "
//				+ "                 WHEN SUBSTR(MAX(A.INS_ID),6,9) THEN LPAD(SUBSTR(MAX(A.INS_ID),6,9)+1,4, '0')  "
//				+ "                 ELSE  '0000'  END  "
//				+ "                  ) AS   "
//				+ "     FROM TBIOT_PPT_MAIN A   "
//				+ "    WHERE A.BRANCH_NBR= :BRANCH_NBRR   "
//				+ "     AND TRUNC(A.CREATETIME,'Y')=TRUNC(SYSDATE,'Y')) AS  C   "
//				+ " FROM DUAL  ");
//
//		qc.setObject("BRANCH_NBRR", inputVO.getBRANCH_NBR());
//		qc.setQueryString(sb.toString());
//		List<Map<String, Object>> List = dam_obj.exeQuery(qc);
//		String Inkeys = inputVO
//				.getBRANCH_NBR()
//				.toString()
//				.substring(inputVO.getBRANCH_NBR().toString().length() - 3,
//						inputVO.getBRANCH_NBR().toString().length())
//				+ List.get(0).get("C").toString();
//		return Inkeys.replaceAll(" ", "");
//
//	}

	public void Initial(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		IOT310InputVO inputVO = (IOT310InputVO) body;
		IOT310OutputVO OutputVO = new IOT310OutputVO();
		List<Map<String, Object>> uplist = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> uplist1 = new ArrayList<Map<String, Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		QueryConditionIF qc1 = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb1 = new StringBuilder();

		try {
			sb.append("     Select                          ");
			sb.append("     A.INS_KEYNO,                      ");
			sb.append("     A.INS_KIND,                       ");
			sb.append("     A.INS_ID,                         ");
			sb.append("     A.REG_TYPE,                       ");
			sb.append("     A.BRANCH_NBR,                     ");
			sb.append("     A.CUST_ID,                        ");
			sb.append("     A.PROPOSER_NAME,                  ");
			sb.append("     A.INSURED_ID,                     ");
			sb.append("     A.INSURED_NAME,                   ");
			sb.append("     A.PPT_TYPE,                       ");
			sb.append("     A.APPLY_DATE,                     ");
			sb.append("     A.KEYIN_DATE,                     ");
			sb.append("     A.REAL_PREMIUM,                   ");
			sb.append("     A.REF_CON_ID,                     ");
			sb.append("     A.RECRUIT_ID,                     ");
			sb.append("     (SELECT EMP_NAME FROM tborg_member WHERE emp_id = A.RECRUIT_ID)RECRUIT_NAME, ");
			sb.append("     A.STATUS,                         ");
			sb.append("     A.CREATOR,                        ");
			sb.append("     A.SIGN_OPRID,                     ");
			sb.append("     A.CREATETIME,                     ");
			sb.append("     A.SIGN_DATE,                      ");
			sb.append("     A.OP_BATCH_NO,                    ");
			sb.append("     A.BEF_SIGN_OPRID,                 ");
			sb.append("     A.AFT_SIGN_OPRID,                 ");
			sb.append("     A.BEF_SIGN_DATE,                  ");
			sb.append("     A.AFT_SIGN_DATE,                  ");
			sb.append("		A.POLICY_NO,          	");
			sb.append("     A.UNOPEN_ACCT                     ");
			sb.append("     From VWIOT_PPT_MAIN A            ");
			sb.append("     Where INS_KEYNO = :in_InsKeyno   ");
			sb1.append("     select * from TBIOT_PPT_DOCCHK    ");
			sb1.append("     Where INS_KEYNO = :in_InsKeyno   ");

			qc.setObject("in_InsKeyno", inputVO.getINS_KEYNO().toString());
			qc1.setObject("in_InsKeyno", inputVO.getINS_KEYNO().toString());

			qc.setQueryString(sb.toString());
			qc1.setQueryString(sb1.toString());
			uplist = dam_obj.exeQuery(qc);
			uplist1 = dam_obj.exeQuery(qc1);
			OutputVO.setInitList(uplist);
			OutputVO.setInitList1(uplist1);

			this.sendRtnObject(OutputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/** 產生seq 6 7 TABEL No */
	private String getSEQ() throws JBranchException {
		String seqNum = "";
		List<Map<String, Object>> INS_KEYNOList = new ArrayList<Map<String,Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" select TBIOT_MAIN_SEQ.NEXTVAL from dual ");
		qc.setQueryString(sb.toString());
		INS_KEYNOList = dam_obj.exeQuery(qc);
		seqNum = INS_KEYNOList.get(0).get("NEXTVAL").toString();
		return seqNum;
	}

}
