package com.systex.jbranch.app.server.fps.prd171;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_INS_ANCDOCVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_COMMISSIONVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_DOCCHKVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_LINKINGVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_PARAMETERVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_INS_SPECIAL_CNDVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * PRD171
 *
 * @author Kevin Hsu
 * @date 2016/09/19
 * @spec null
 */

@Component("prd171")
@Scope("request")
public class PRD171 extends FubonWmsBizLogic {
	public static final String FUBON_COMPANY_NUM = "82";
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj = null;

	/** ==主查詢== **/
	public void queryData(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		PRD171InputVO inputVO = (PRD171InputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		dam_obj = getDataAccessManager();

		// old wrong 2016/10/06 edit
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		switch (inputVO.getP_TYPE()) {
			case "1":
				sb.append("select * from TBPRD_INS_MAIN ");
				// 進件壽險產品參數設定
				break;
			case "2":
				sb.append("select * from VWPRD_INS_SPECIAL_CND where 1=1 ");
				// 特殊條件設定
				break;
			case "3":
				sb.append("select distinct A.INSPRD_ID,A.INSPRD_NAME,A.INSPRD_ANNUAL from VWPRD_INS_ANCDOC A where 1=1 ");
				// 權益書設定
				break;
			case "4":
				sb.append("select * from VWPRD_INS_LINKING ");
				// 連結標的上傳
				break;
			case "5":
				sb.append("select * from VWPRD_INS_PARAMETER where 1=1 ");
				// 提存參數設定
				break;
			case "6":
				sb.append("select * from VWPRD_INS_DOCCHK where DOC_TYPE  = '2' and REG_TYPE = '1' and 1=1 ");
				// 新契約進件文件設定
				break;
			case "7":
				sb.append("select * from VWPRD_INS_DOCCHK where DOC_TYPE  = '1' and REG_TYPE = '1' and 1=1 ");
				// 分行文件設定
				break;
			case "8":
				sb.append("select * FROM VWPRD_INS_COMMISSION ");
				// 佣金檔設定
				break;
		}
		// 基本設定、連結標的設定、佣金檔設定串「保險公司」
		if (inputVO.getP_TYPE().matches("[148]")) {
			sb.append("left join ( ");
			sb.append("    select CNAME COMPANY_NAME, SERIALNUM COMPANY_SEQ ");
			sb.append("    from TBJSB_INS_PROD_COMPANY ");
			sb.append(") COM on (COMPANY_NUM = COM.COMPANY_SEQ)  ");
			sb.append("where 1=1 ");

			if (StringUtils.isNotBlank(inputVO.getCOMPANY_NAME())) {
				sb.append("and COM.COMPANY_NAME like :COM_NAME ");
				qc.setObject("COM_NAME", "%" + inputVO.getCOMPANY_NAME() + "%");
			}
		}
		// where
		if (!StringUtils.isBlank(inputVO.getINSPRD_TYPE())) {
			if (!StringUtils.equals(inputVO.getP_TYPE(), "8")) {
				sb.append("and INSPRD_TYPE=:INSPRD_TYPEE ");
				qc.setObject("INSPRD_TYPEE", inputVO.getINSPRD_TYPE());
			}
		}
		if (!StringUtils.isBlank(inputVO.getINSPRD_NAME())) {
			sb.append("and INSPRD_NAME LIKE :INSPRD_NAMEE ");
			qc.setObject("INSPRD_NAMEE", "%" + inputVO.getINSPRD_NAME() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
			sb.append("and INSPRD_ID=:INSPRD_IDD ");
			qc.setObject("INSPRD_IDD", inputVO.getINSPRD_ID());
		}
		if (!StringUtils.isBlank(inputVO.getAPPROVER())) {
			if ("未覆核".equals(inputVO.getAPPROVER()))
				sb.append(" and APPROVER IS NULL ");
			else
				sb.append(" AND APPROVER IS NOT NULL ");
		}
		// 進件壽險產品參數設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "1"))
			sb.append("order by INSPRD_ID,INSPRD_ANNUAL ");
		// 特殊條件設定
		if (StringUtils.equals(inputVO.getP_TYPE().toString(), "2"))
			sb.append("order by INSPRD_ID, PAY_TYPE, SEQ ");
		// 權益書設定
		//if (StringUtils.equals(inputVO.getP_TYPE(), "3"))
		//	sb.append("Order by INSPRD_ID,INSPRD_ANNUAL,A.Q_ID ");
		// 連結標的上傳
		if (StringUtils.equals(inputVO.getP_TYPE(), "4"))
			sb.append("Order by INSPRD_ID, TARGET_ID ");
		// 提存參數設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "5"))
			sb.append("Order by INSPRD_ID,INSPRD_ANNUAL ");
		// 新契約進件文件設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "6"))
			sb.append("Order by INSPRD_ID,DOC_SEQ  ");
		// 分行文件設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "7"))
			sb.append("Order by INSPRD_ID,DOC_SEQ ");
		qc.setQueryString(sb.toString());

		ResultIF list = dam_obj.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		outputVO.setINS_ANCDOCList(list);
		sendRtnObject(outputVO);
	}

	/** ==查list詢== **/
	public void queryData2(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			//
			if (!StringUtils.isBlank(inputVO.getQ_ID())) {
				sb.append("select distinct Q_TYPE,Q_NAME from TBPRD_INS_ANCDOC_Q where Q_ID =:Q_IDD");
				// Q_ID
				qc.setObject("Q_IDD", inputVO.getQ_ID());
				qc.setQueryString(sb.toString());
				list = dam_obj.exeQuery(qc);
			}
			if (!StringUtils.isBlank(inputVO.getFUND_ID())) {
				sb.append("select LIPPER_ID,LINKED_NAME,PRD_RISK from TBPRD_INS_LINKING WHERE  FUND_ID=:FUND_IDD ");
				// 銀行標的代號
				qc.setObject("FUND_IDD", inputVO.getFUND_ID());
				qc.setQueryString(sb.toString());
				list = dam_obj.exeQuery(qc);
			}
			if (!StringUtils.isBlank(inputVO.getINSPRD_ID())
					&& (StringUtils.equals(inputVO.getTYPE_TABLE(), "2")
					|| StringUtils.equals(inputVO.getTYPE_TABLE(), "5") || StringUtils
					.equals(inputVO.getTYPE_TABLE(), "8"))) {
				sb.append("Select distinct INSPRD_NAME,INSPRD_TYPE from TBPRD_INS_MAIN where INSPRD_ID =:INSPRD_IDD ");
				// 險種名稱
				qc.setObject("INSPRD_IDD", inputVO.getINSPRD_ID().toUpperCase());
				qc.setQueryString(sb.toString());
				list = dam_obj.exeQuery(qc);
			}
			if (!StringUtils.isBlank(inputVO.getINSPRD_ID()) && StringUtils.equals(inputVO.getTYPE_TABLE(), "3")) {
				sb.append(" SELECT B.INSPRD_ID,B.INSPRD_NAME,B.INSPRD_ANNUAL,B.KEY_NO,B.Q_SEQ, ");
				sb.append(" A.Q_ID, A.Q_NAME, A.Q_TYPE ");
				sb.append(" FROM TBPRD_INS_ANCDOC_Q A ");
				sb.append(" LEFT JOIN VWPRD_INS_ANCDOC B ");
				sb.append(" ON B.Q_ID = A.Q_ID AND B.INSPRD_ID =:INSPRD_ID AND B.INSPRD_ANNUAL =:INSPRD_ANNUAL ");
				sb.append(" ORDER BY A.Q_ID  ");
				// 險種名稱
				qc.setObject("INSPRD_ID", inputVO.getINSPRD_ID().toUpperCase());
				qc.setObject("INSPRD_ANNUAL", inputVO.getINSPRD_ANNUAL());
				qc.setQueryString(sb.toString());
				list = dam_obj.exeQuery(qc);
			}

			outputVO.setDILOGList(list);
			sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/** ==權益書題目代碼查詢== **/
	public void queryAncDoc(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			//
			if (!StringUtils.isBlank(inputVO.getTemp_Q_ID())) {
				sb.append("select distinct Q_TYPE,Q_NAME from TBPRD_INS_ANCDOC_Q where Q_ID =:Q_IDD");
				// Q_ID
				qc.setObject("Q_IDD", inputVO.getTemp_Q_ID());
				qc.setQueryString(sb.toString());
				list = dam_obj.exeQuery(qc);
			}
			outputVO.setTempList(list);
			sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	//權益書新增時查詢所以權益書
	public void AncDoc_Add_Query(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("select * from TBPRD_INS_ANCDOC_Q order by Q_ID");
		qc.setQueryString(sb.toString());
		outputVO.setDILOGList(dam_obj.exeQuery(qc));
		sendRtnObject(outputVO);
	}

	public void queryINSPRD_NAME(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select distinct INSPRD_NAME from TBPRD_INS_MAIN where INSPRD_ID = :insprd_id ");
			if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
				qc.setObject("insprd_id", inputVO.getINSPRD_ID().toUpperCase());
			} else {
				qc.setObject("insprd_id", inputVO.getINSPRD_ID());
			}
			qc.setQueryString(sb.toString());
			outputVO.setINSPRD_NAMEList(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}

	}

	public void addData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		Timestamp TM = new Timestamp(System.currentTimeMillis());
		List<Map<String, Object>> checkProd = new ArrayList<Map<String, Object>>();
		String errorMsg = "";
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		try {
			dam_obj = this.getDataAccessManager();
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "1")) {
				// TBPRD_INS_MAIN
				TBPRD_INS_MAINVO vo = new TBPRD_INS_MAINVO();

				if (!StringUtils.isBlank(inputVO.getCURR_CD())) {
					vo.setCURR_CD(inputVO.getCURR_CD());
				}

				if (!StringUtils.isBlank(inputVO.getAB_EXCH_RATE())) {
					//CURR_CD = TWD, 固定將 AB_EXCH_RATE 設為 1
					if ("TWD".equals(inputVO.getCURR_CD())) {
						vo.setAB_EXCH_RATE(new BigDecimal("1"));
					} else {
						vo.setAB_EXCH_RATE(new BigDecimal(inputVO.getAB_EXCH_RATE()));
					}
				}

				vo.setAPP_DATE(null);
				vo.setAPPROVER(null);

				if (!StringUtils.isBlank(inputVO.getCERT_TYPE())) {
					vo.setCERT_TYPE(inputVO.getCERT_TYPE());
				}

				if (!StringUtils.isBlank(inputVO.getCNR_RATE())) {
					vo.setCNR_RATE(new BigDecimal(inputVO.getCNR_RATE()));
				}
				if (!StringUtils.isBlank(inputVO.getCOEFFICIENT())) {
					vo.setCOEFFICIENT(new BigDecimal(inputVO.getCOEFFICIENT()
							.toString()));
				}

				if (!StringUtils.isBlank(inputVO.getEFFECT_DATE())) {
					vo.setEFFECT_DATE(new Timestamp(Long.parseLong(inputVO
							.getEFFECT_DATE())));
				}
//				vo.setEXCH_RATE(new BigDecimal("0.1"));
				if (!StringUtils.isBlank(inputVO.getEXPIRY_DATE())) {
					vo.setEXPIRY_DATE(new Timestamp(Long.parseLong(inputVO
							.getEXPIRY_DATE().toString())));
				}
				if (!StringUtils.isBlank(inputVO.getFEE_STATE())) {
					vo.setFEE_STATE(inputVO.getFEE_STATE());
				}
				if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
					vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL());
				}
				if (!StringUtils.isBlank(inputVO.getINSPRD_CLASS())) {
					vo.setINSPRD_CLASS(inputVO.getINSPRD_CLASS());
				}
				if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
					vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
				}

				vo.setINSPRD_KEYNO(getPRD_KEY_NO());
				if (!StringUtils.isBlank(inputVO.getINSPRD_NAME())) {
					vo.setINSPRD_NAME(inputVO.getINSPRD_NAME().toString());
				}
				if (!StringUtils.isBlank(inputVO.getINSPRD_STYLE())) {
					vo.setSPECIAL_CONDITION(inputVO.getINSPRD_STYLE().toString());
				}
				if (!StringUtils.isBlank(inputVO.getINSPRD_TYPE())) {
					vo.setINSPRD_TYPE(inputVO.getINSPRD_TYPE().toString());
				}
				if (!StringUtils.isBlank(inputVO.getMAIN_RIDER())) {
					vo.setMAIN_RIDER(inputVO.getMAIN_RIDER().toString());
				}
				if (!StringUtils.isBlank(inputVO.getNEED_MATCH())) {
					vo.setNEED_MATCH(inputVO.getNEED_MATCH());
				}
				if (!StringUtils.isBlank(inputVO.getPRD_RISK())) {
					vo.setPRD_RISK(inputVO.getPRD_RISK());
				}
				if (!StringUtils.isBlank(inputVO.getPAY_TYPE())) {
					vo.setPAY_TYPE(inputVO.getPAY_TYPE().toString());
				}
				if (!StringUtils.isBlank(inputVO.getPRD_RATE())) {
					vo.setPRD_RATE(new BigDecimal(inputVO.getPRD_RATE().toString()));
				}
				if (!StringUtils.isBlank(inputVO.getTRAINING_TYPE())) {
					vo.setTRAINING_TYPE(inputVO.getTRAINING_TYPE().toString());
				}
				if (inputVO.getCOMPANY_NUM() != null) {
					vo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
				}
				vo.setDIVIDEND_YN(inputVO.getDIVIDEND_YN());
				dam_obj.create(vo);
				sendRtnObject(null);
			}
			//新增-特殊條件設定(2)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "2")) {
				if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , inputVO.getPAY_TYPE())){
					// TBPRD_INS_SPECIAL_CND
					TBPRD_INS_SPECIAL_CNDVO vo = new TBPRD_INS_SPECIAL_CNDVO();
					vo.setAPP_DATE(TM);
					vo.setAPPROVER("1");
					if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
					}
					vo.setKEY_NO(getPRD_KEY_NO());
					if (!StringUtils.isBlank(inputVO.getPAY_TYPE())) {
						vo.setPAY_TYPE(inputVO.getPAY_TYPE());
					}
					if (!StringUtils.isBlank(inputVO.getSEQ())) {
						vo.setSEQ(new BigDecimal(inputVO.getSEQ().toString()));
					}
					if (!StringUtils.isBlank(inputVO.getSPECIAL_CONDITION())) {
						vo.setSPECIAL_CONDITION(inputVO.getSPECIAL_CONDITION()
								.toString());
					}
					dam_obj.create(vo);
					sendRtnObject(null);
				} else {
					errorMsg = "ehl_01_prd171_002";
					throw new APException(errorMsg);
				}
			}

			//新增-權益書設定(3)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "3")) {
				List<Map<String, Object>> checkData = new ArrayList<Map<String, Object>>();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select 'x' as X from TBPRD_INS_ANCDOC ");
				sb.append(" where INSPRD_ID = :INSPRD_ID ");
				sb.append(" and INSPRD_ANNUAL = :INSPRD_ANNUAL ");
				if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
					qc.setObject("INSPRD_ID", inputVO.getINSPRD_ID().toUpperCase());
				} else {
					qc.setObject("INSPRD_ID", inputVO.getINSPRD_ID());
				}
				qc.setObject("INSPRD_ANNUAL", inputVO.getINSPRD_ANNUAL());
				qc.setQueryString(sb.toString());
				checkData = dam_obj.exeQuery(qc);
				if (checkData.size() > 0) {
					errorMsg = "ehl_01_common_016";
					throw new APException(errorMsg);
				} else {
//					if(checkProd.size()>0){
					if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , inputVO.getINSPRD_ANNUAL())){
						// TBPRD_INS_ANCDOC
						for (Map<String, Object> Data : inputVO.getDILOGList()) {
							if (Data.get("Q_SEQ") != null) {
								TBPRD_INS_ANCDOCVO vo = new TBPRD_INS_ANCDOCVO();
								vo.setAPP_DATE(TM);
								vo.setAPPROVER("1");
								if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
									vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL());
								}
								if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
									vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
								}

								vo.setKEY_NO(getPRD_KEY_NO());
								if (!StringUtils.isBlank(Data.get("Q_ID").toString())) {
									vo.setQ_ID(Data.get("Q_ID").toString());
								}
								if (!StringUtils.isBlank(Data.get("Q_SEQ").toString())) {
									vo.setQ_SEQ(new BigDecimal(Data.get("Q_SEQ").toString()));
								}
								dam_obj.create(vo);
							}
						}
						sendRtnObject(null);
					} else {
						errorMsg = "ehl_01_prd171_003";
						throw new APException(errorMsg);
					}
				}

			}

			//新增-連結標的設定(4)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "4")) {
				if(!isFubon(inputVO.getCOMPANY_NUM()) || chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , "")){
					// TBPRD_INS_LINKING
					TBPRD_INS_LINKINGVO vo = new TBPRD_INS_LINKINGVO();

					vo.setAPP_DATE(TM);
					if (!StringUtils.isBlank(inputVO.getAPPROVER())) {
						vo.setAPPROVER(inputVO.getAPPROVER());
					}
					if (!StringUtils.isBlank(inputVO.getFUND_ID())) {
						vo.setFUND_ID(inputVO.getFUND_ID().toString());
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
					}
					vo.setKEY_NO(getPRD_KEY_NO());

					if (!StringUtils.isBlank(inputVO.getLINKED_NAME())) {
						vo.setLINKED_NAME(inputVO.getLINKED_NAME().toString());
					}
					if (!StringUtils.isBlank(inputVO.getLIPPER_ID())) {
						vo.setLIPPER_ID(inputVO.getLIPPER_ID().toString());
					}
					if (!StringUtils.isBlank(inputVO.getPRD_RISK())) {
						vo.setPRD_RISK(inputVO.getPRD_RISK().toString());
					}
					if (!StringUtils.isBlank(inputVO.getCOM_PRD_RISK())) {
						vo.setCOM_PRD_RISK(inputVO.getCOM_PRD_RISK().toString());
					}
					if (!StringUtils.isBlank(inputVO.getTARGET_ID())) {
						vo.setTARGET_ID(inputVO.getTARGET_ID().toString());
					}
					if (!StringUtils.isBlank(inputVO.getTRAINING_TYPE())) {
						vo.setTRAINING_TYPE(inputVO.getTRAINING_TYPE().toString());
					}
					if (inputVO.getCOMPANY_NUM() != null) {
						vo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
					}
//					if (inputVO.getKYC_SCORE() != null) {
//						vo.setKYC_SCORE(inputVO.getKYC_SCORE());
//					}
					vo.setTARGET_CURR(inputVO.getTARGET_CURR());
					vo.setINT_TYPE(inputVO.getINT_TYPE());
					vo.setTRANSFER_FLG(inputVO.getTRANSFER_FLG());

					dam_obj.create(vo);
					sendRtnObject(null);
				} else {
					errorMsg = "ehl_01_prd171_001";
					throw new APException(errorMsg);
				}
			}

			//新增-提存參數設定(5)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "5")) {
				if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , inputVO.getINSPRD_ANNUAL())){
					// TBPRD_INS_PARAMETER
					TBPRD_INS_PARAMETERVO vo = new TBPRD_INS_PARAMETERVO();

					vo.setAPP_DATE(TM);
					vo.setAPPROVER("1");
					if (!StringUtils.isBlank(inputVO.getEMP_BONUS_RATE())) {
						vo.setEMP_BONUS_RATE(new BigDecimal(inputVO.getEMP_BONUS_RATE()
								.toString()));
					}
					if (!StringUtils.isBlank(inputVO.getHIGH_CPCT_BONUS_RATE())) {
						vo.setHIGH_CPCT_BONUS_RATE(new BigDecimal(inputVO
								.getHIGH_CPCT_BONUS_RATE()));
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
						vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL().toString());
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
					}

					vo.setKEY_NO(getPRD_KEY_NO());
					if (!StringUtils.isBlank(inputVO.getOSEA_BONUS_RATE())) {
						vo.setOSEA_BONUS_RATE(new BigDecimal(inputVO
								.getOSEA_BONUS_RATE()));
					}
					if (!StringUtils.isBlank(inputVO.getPERIOD())) {
						vo.setPERIOD(inputVO.getPERIOD());
					}
					if (!StringUtils.isBlank(inputVO.getPRD_BONUS_RATE())) {
						vo.setPRD_BONUS_RATE(new BigDecimal(inputVO.getPRD_BONUS_RATE()
								.toString()));
					}
					if (!StringUtils.isBlank(inputVO.getYEAR_END_BONUS())) {
						vo.setYEAR_END_BONUS(new BigDecimal(inputVO.getYEAR_END_BONUS()
								.toString()));
					}
					dam_obj.create(vo);
					sendRtnObject(null);
				} else {
					errorMsg = "ehl_01_prd171_003";
					throw new APException(errorMsg);
				}
			}

			//新增-新契約進件文件設定(6)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "6")) {
				if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , "")){
					// TBPRD_INS_DOCCHK
					TBPRD_INS_DOCCHKVO vo = new TBPRD_INS_DOCCHKVO();
					if (!StringUtils.isBlank(inputVO.getDOC_LEVEL())) {
						vo.setDOC_LEVEL(inputVO.getDOC_LEVEL());
					}
					if (!StringUtils.isBlank(inputVO.getDOC_NAME())) {
						vo.setDOC_NAME(inputVO.getDOC_NAME());
					}
					if (!StringUtils.isBlank(inputVO.getDOC_SEQ())) {
						vo.setDOC_SEQ(new BigDecimal(inputVO.getDOC_SEQ()));
					}

					vo.setDOC_TYPE("2");

//					if (!StringUtils.isBlank(inputVO.getSIGN_INC())) {
//					vo.setSIGN_INC(inputVO.getSIGN_INC());
//					}
					//新契約須簽屬必為Y
					vo.setSIGN_INC("Y");
					vo.setKEY_NO(getPRD_KEY_NO());
					vo.setREG_TYPE("1");
					if (!StringUtils.isBlank(inputVO.getOTH_TYPE())) {
						vo.setOTH_TYPE(inputVO.getOTH_TYPE());
					}
					if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID().toUpperCase());
					} else {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID());
					}
					vo.setAPP_DATE(TM);
					vo.setAPPROVER("2");
					dam_obj.create(vo);
					sendRtnObject(null);
				} else {
					errorMsg = "ehl_01_prd171_001";
					throw new APException(errorMsg);
				}
			}

			//新增-分行留存文件設定(7)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "7")) {
				if (chk_prdins(inputVO.getTYPE_TABLE(), inputVO.getINSPRD_ID(), "")) {
					// TBPRD_INS_DOCCHK
					TBPRD_INS_DOCCHKVO vo = new TBPRD_INS_DOCCHKVO();
					if (!StringUtils.isBlank(inputVO.getDOC_LEVEL())) {
						vo.setDOC_LEVEL(inputVO.getDOC_LEVEL());
					}
					if (!StringUtils.isBlank(inputVO.getDOC_NAME())) {
						vo.setDOC_NAME(inputVO.getDOC_NAME());
					}
					vo.setDOC_SEQ(new BigDecimal(inputVO.getDOC_SEQ()));

//					if (!StringUtils.isBlank(inputVO.getSIGN_INC())) {
//					vo.setSIGN_INC(inputVO.getSIGN_INC());
//					}
					vo.setSIGN_INC("Y");
					vo.setKEY_NO(getPRD_KEY_NO());
					vo.setREG_TYPE("1");
					if (!StringUtils.isBlank(inputVO.getOTH_TYPE())) {
						vo.setOTH_TYPE(inputVO.getOTH_TYPE());
					}
					if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID().toUpperCase());
					} else {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID());
					}
					vo.setAPP_DATE(TM);
					vo.setAPPROVER("1");
					vo.setDOC_TYPE("1");
					dam_obj.create(vo);
					sendRtnObject(null);
				} else {
					errorMsg = "ehl_01_prd171_001";
					throw new APException(errorMsg);
				}
			}

			//新增 - 傭金檔設定(8)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "8")) {
				if (!isFubon(inputVO.getCOMPANY_NUM()) || chk_prdins(inputVO.getTYPE_TABLE(), inputVO.getINSPRD_ID(), inputVO.getINSPRD_ANNUAL())) {
					// TBPRD_INS_COMMISSION
					dam_obj = this.getDataAccessManager();
					TBPRD_INS_COMMISSIONVO vo = new TBPRD_INS_COMMISSIONVO();
					if (!StringUtils.isBlank(inputVO.getANNUAL())) {
						vo.setANNUAL(inputVO.getANNUAL().toString());
					}
					vo.setAPP_DATE(TM);
					vo.setAPPROVER("1");
					if (!StringUtils.isBlank(inputVO.getCOMM_RATE())) {
						vo.setCOMM_RATE(new BigDecimal(inputVO.getCOMM_RATE()
								.toString()));
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
						vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL().toString());
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
					}
					vo.setKEY_NO(getPRD_KEY_NO().toString());
					if (!StringUtils.isBlank(inputVO.getTYPE())) {
						vo.setTYPE(inputVO.getTYPE().toString());
					}
					if (StringUtils.isNotBlank(inputVO.getCNR_RATE())) {
						vo.setCNR_RATE(new BigDecimal(inputVO.getCNR_RATE()));
					}
					if (StringUtils.isNotBlank(inputVO.getCNR_YIELD())) {
						vo.setCNR_YIELD(new BigDecimal(inputVO.getCNR_YIELD()));
					}
					if (StringUtils.isNotBlank(inputVO.getCNR_MULTIPLE())) {
						vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getCNR_MULTIPLE()));
					}
					if (inputVO.getMULTIPLE_SDATE() != null) {
						vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMULTIPLE_SDATE().getTime()));
					}
					if (inputVO.getMULTIPLE_EDATE() != null) {
						vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMULTIPLE_EDATE().getTime()));
					}
					if (inputVO.getCOMPANY_NUM() != null) {
						vo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
					}
					dam_obj.create(vo);
					sendRtnObject(null);
				} else {
					errorMsg = "ehl_01_prd171_003";
					throw new APException(errorMsg);
				}
			}

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			if (errorMsg.length() > 0) {
				throw new APException(errorMsg);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}

	public void updateData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		Timestamp TM = new Timestamp(System.currentTimeMillis());
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> checkProd = new ArrayList<Map<String, Object>>();
		String errorMsg = "";
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		try {
			dam_obj = this.getDataAccessManager();
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "1")) {
				// TBPRD_INS_MAIN
				TBPRD_INS_MAINVO vo = (TBPRD_INS_MAINVO) dam_obj.findByPKey(
						TBPRD_INS_MAINVO.TABLE_UID,
						new BigDecimal(inputVO.getINSPRD_KEYNO()));

				if (vo != null) {

					if (!StringUtils.isBlank(inputVO.getAB_EXCH_RATE())) {
						//CURR_CD = TWD, 固定將 AB_EXCH_RATE 設為 1
						if ("TWD".equals(inputVO.getCURR_CD())) {
							vo.setAB_EXCH_RATE(new BigDecimal("1"));
						} else {
							vo.setAB_EXCH_RATE(new BigDecimal(inputVO.getAB_EXCH_RATE()));
						}
					} else {
						vo.setAB_EXCH_RATE(null);
					}
					// 覆核規則：如修改則清空覆核相關欄位
					vo.setAPP_DATE(null);
					vo.setAPPROVER(null);

					if (!StringUtils.isBlank(inputVO.getCERT_TYPE())) {
						vo.setCERT_TYPE(inputVO.getCERT_TYPE());
					} else {
						vo.setCERT_TYPE(null);
					}

					if (!StringUtils.isBlank(inputVO.getCNR_RATE())) {
						vo.setCNR_RATE(new BigDecimal(inputVO.getCNR_RATE()));
					} else {
						vo.setCNR_RATE(null);
					}
					if (!StringUtils.isBlank(inputVO.getCOEFFICIENT())) {
						vo.setCOEFFICIENT(new BigDecimal(inputVO.getCOEFFICIENT()
								.toString()));
					} else {
						vo.setCOEFFICIENT(null);
					}
					if (!StringUtils.isBlank(inputVO.getCURR_CD())) {
						vo.setCURR_CD(inputVO.getCURR_CD());
					} else {
						vo.setCURR_CD(null);
					}
					if (!StringUtils.isBlank(inputVO.getEFFECT_DATE())) {
						vo.setEFFECT_DATE(new Timestamp(Long.parseLong(inputVO
								.getEFFECT_DATE())));
					} else {
						vo.setEFFECT_DATE(null);
					}
//					vo.setEXCH_RATE(new BigDecimal("0.1"));
					if (!StringUtils.isBlank(inputVO.getEXPIRY_DATE())) {
						vo.setEXPIRY_DATE(new Timestamp(Long.parseLong(inputVO
								.getEXPIRY_DATE().toString())));
					} else {
						vo.setEXPIRY_DATE(null);
					}
					if (!StringUtils.isBlank(inputVO.getFEE_STATE())) {
						vo.setFEE_STATE(inputVO.getFEE_STATE());
					} else {
						vo.setFEE_STATE(null);
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
						vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL());
					} else {
						vo.setINSPRD_ANNUAL(null);
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_CLASS())) {
						vo.setINSPRD_CLASS(inputVO.getINSPRD_CLASS());
					} else {
						vo.setINSPRD_CLASS(null);
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
						vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
					} else {
						vo.setINSPRD_ID(null);
					}

//					vo.setINSPRD_KEYNO(new BigDecimal(getTBPRD_INS_MAIN_SEQ()));
					if (!StringUtils.isBlank(inputVO.getINSPRD_NAME())) {
						vo.setINSPRD_NAME(inputVO.getINSPRD_NAME().toString());
					} else {
						vo.setINSPRD_NAME(null);
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_STYLE())) {
						vo.setSPECIAL_CONDITION(inputVO.getINSPRD_STYLE().toString());
					} else {
						vo.setSPECIAL_CONDITION(null);
					}
					if (!StringUtils.isBlank(inputVO.getINSPRD_TYPE())) {
						vo.setINSPRD_TYPE(inputVO.getINSPRD_TYPE().toString());
					} else {
						vo.setINSPRD_TYPE(null);
					}
					if (!StringUtils.isBlank(inputVO.getMAIN_RIDER())) {
						vo.setMAIN_RIDER(inputVO.getMAIN_RIDER().toString());
					} else {
						vo.setMAIN_RIDER(null);
					}
					if (!StringUtils.isBlank(inputVO.getNEED_MATCH())) {
						vo.setNEED_MATCH(inputVO.getNEED_MATCH());
					} else {
						vo.setNEED_MATCH(null);
					}
					if (!StringUtils.isBlank(inputVO.getPRD_RISK())) {
						vo.setPRD_RISK(inputVO.getPRD_RISK());
					}else{
						vo.setPRD_RISK(null);
					}
					if (!StringUtils.isBlank(inputVO.getPAY_TYPE())) {
						vo.setPAY_TYPE(inputVO.getPAY_TYPE().toString());
					} else {
						vo.setPAY_TYPE(null);
					}
					if (!StringUtils.isBlank(inputVO.getPRD_RATE())) {
						vo.setPRD_RATE(new BigDecimal(inputVO.getPRD_RATE().toString()));
					} else {
						vo.setPRD_RATE(null);
					}
					if (!StringUtils.isBlank(inputVO.getTRAINING_TYPE())) {
						vo.setTRAINING_TYPE(inputVO.getTRAINING_TYPE().toString());
					} else {
						vo.setTRAINING_TYPE(null);
					}
					if (inputVO.getCOMPANY_NUM() != null) {
						vo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
					}
					vo.setDIVIDEND_YN(inputVO.getDIVIDEND_YN());
					
					dam_obj.update(vo);
					sendRtnObject(null);
				}
			}
			//修改-特殊條件設定(2)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "2")) {
				// TBPRD_INS_SPECIAL_CND
				dam_obj = this.getDataAccessManager();
				TBPRD_INS_SPECIAL_CNDVO vo = (TBPRD_INS_SPECIAL_CNDVO) dam_obj
						.findByPKey(TBPRD_INS_SPECIAL_CNDVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
//					if(checkProd.size()>0){
					if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID(), inputVO.getPAY_TYPE())){
						vo.setAPP_DATE(TM);
						vo.setAPPROVER("1");
						if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
							vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
						}
//						vo.setKEY_NO(new BigDecimal(getTBPRD_INS_SPECIAL_CND()));
						if (!StringUtils.isBlank(inputVO.getPAY_TYPE())) {
							vo.setPAY_TYPE(inputVO.getPAY_TYPE());
						}
						if (!StringUtils.isBlank(inputVO.getSEQ())) {
							vo.setSEQ(new BigDecimal(inputVO.getSEQ().toString()));
						}
						if (!StringUtils.isBlank(inputVO.getSPECIAL_CONDITION())) {
							vo.setSPECIAL_CONDITION(inputVO.getSPECIAL_CONDITION()
									.toString());
						}
						dam_obj.update(vo);
						sendRtnObject(null);
					} else {
						errorMsg = "ehl_01_prd171_002";
						throw new APException(errorMsg);
					}
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			//修改-權益書設定(3)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "3")) {
				if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , inputVO.getINSPRD_ANNUAL())){
					//deleteData
					if (inputVO.getDeleteList().size() > 0) {
						for (Object deletedata : inputVO.getDeleteList()) {
							TBPRD_INS_ANCDOCVO vo = (TBPRD_INS_ANCDOCVO) dam_obj.findByPKey(TBPRD_INS_ANCDOCVO.TABLE_UID, new BigDecimal(deletedata.toString()));
							if (vo != null) {
								dam_obj.delete(vo);
							}
						}
					}

					// TBPRD_INS_ANCDOC
					for (Map<String, Object> Data : inputVO.getDILOGList()) {
						//判斷該新增還是修改
						if (Data.get("KEY_NO") != null && Data.get("Q_SEQ") != null) {
							TBPRD_INS_ANCDOCVO vo = (TBPRD_INS_ANCDOCVO) dam_obj.findByPKey(TBPRD_INS_ANCDOCVO.TABLE_UID, new BigDecimal(Data.get("KEY_NO").toString()));
							if (vo != null) {
								vo.setAPP_DATE(TM);
								vo.setAPPROVER("1");
								if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
									vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL());
								}
								if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
									vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
								}

								if (!StringUtils.isBlank(inputVO.getQ_ID())) {
									vo.setQ_ID(inputVO.getQ_ID());
								}
								if (!StringUtils.isBlank(inputVO.getQ_SEQ())) {
									vo.setQ_SEQ(new BigDecimal(inputVO.getQ_SEQ()));
								}
								dam_obj.update(vo);
							} else {
								throw new APException("ehl_01_common_008");
							}
						} else if (Data.get("KEY_NO") == null && Data.get("Q_SEQ") != null) {
							TBPRD_INS_ANCDOCVO vo = new TBPRD_INS_ANCDOCVO();
							vo.setAPP_DATE(TM);
							vo.setAPPROVER("1");
							if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
								vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL());
							}
							if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
								vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
							}

							vo.setKEY_NO(getPRD_KEY_NO());
							if (!StringUtils.isBlank(Data.get("Q_ID").toString())) {
								vo.setQ_ID(Data.get("Q_ID").toString());
							}
							if (!StringUtils.isBlank(Data.get("Q_SEQ").toString())) {
								vo.setQ_SEQ(new BigDecimal(Data.get("Q_SEQ").toString()));
							}
							dam_obj.create(vo);
						} else if (Data.get("Q_SEQ") == null && Data.get("KEY_NO") != null) {
							TBPRD_INS_ANCDOCVO vo = (TBPRD_INS_ANCDOCVO) dam_obj.findByPKey(TBPRD_INS_ANCDOCVO.TABLE_UID,
									new BigDecimal(Data.get("KEY_NO").toString()));
							dam_obj.delete(vo);
						}
					}
					sendRtnObject(null);
				} else {
					errorMsg = "ehl_01_prd171_003";
					throw new APException(errorMsg);
				}
			}
			// 修改-連結標的設定(4)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "4")) {
				// TBPRD_INS_LINKING
				TBPRD_INS_LINKINGVO vo = (TBPRD_INS_LINKINGVO) dam_obj
						.findByPKey(TBPRD_INS_LINKINGVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
					if(!isFubon(inputVO.getCOMPANY_NUM()) || chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , "")){
						vo.setAPP_DATE(TM);
						if (!StringUtils.isBlank(inputVO.getAPPROVER())) {
							vo.setAPPROVER(inputVO.getAPPROVER());
						}
						if (!StringUtils.isBlank(inputVO.getFUND_ID())) {
							vo.setFUND_ID(inputVO.getFUND_ID().toString());
						}
						if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
							vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
						}
//						vo.setKEY_NO(new BigDecimal(getTBPRD_INS_LINKING_SEQ()));

						if (!StringUtils.isBlank(inputVO.getLINKED_NAME())) {
							vo.setLINKED_NAME(inputVO.getLINKED_NAME().toString());
						}
						if (!StringUtils.isBlank(inputVO.getLIPPER_ID())) {
							vo.setLIPPER_ID(inputVO.getLIPPER_ID().toString());
						}
						if (!StringUtils.isBlank(inputVO.getPRD_RISK())) {
							vo.setPRD_RISK(inputVO.getPRD_RISK().toString());
						}
						if (!StringUtils.isBlank(inputVO.getCOM_PRD_RISK())) {
							vo.setCOM_PRD_RISK(inputVO.getCOM_PRD_RISK().toString());
						}
						if (!StringUtils.isBlank(inputVO.getTARGET_ID())) {
							vo.setTARGET_ID(inputVO.getTARGET_ID().toString());
						}
						if (!StringUtils.isBlank(inputVO.getTRAINING_TYPE())) {
							vo.setTRAINING_TYPE(inputVO.getTRAINING_TYPE().toString());
						}
						if (inputVO.getCOMPANY_NUM() != null) {
							vo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
						}
//						if (inputVO.getKYC_SCORE() != null) {
//							vo.setKYC_SCORE(inputVO.getKYC_SCORE());
//						}
						vo.setTARGET_CURR(inputVO.getTARGET_CURR());
						vo.setINT_TYPE(inputVO.getINT_TYPE());
						vo.setTRANSFER_FLG(inputVO.getTRANSFER_FLG());

						dam_obj.update(vo);
						sendRtnObject(null);
					} else {
						errorMsg = "ehl_01_prd171_001";
						throw new APException(errorMsg);
					}
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			// 修改-提存參數設定(5)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "5")) {
				// TBPRD_INS_PARAMETER
				TBPRD_INS_PARAMETERVO vo = (TBPRD_INS_PARAMETERVO) dam_obj
						.findByPKey(TBPRD_INS_PARAMETERVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
					if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , inputVO.getINSPRD_ANNUAL())){
						vo.setAPP_DATE(TM);
						vo.setAPPROVER("1");
						if (!StringUtils.isBlank(inputVO.getEMP_BONUS_RATE())) {
							vo.setEMP_BONUS_RATE(new BigDecimal(inputVO.getEMP_BONUS_RATE()
									.toString()));
						}
						if (!StringUtils.isBlank(inputVO.getHIGH_CPCT_BONUS_RATE())) {
							vo.setHIGH_CPCT_BONUS_RATE(new BigDecimal(inputVO
									.getHIGH_CPCT_BONUS_RATE()));
						}
						if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
							vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL().toString());
						}
						if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
							vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
						}

//						vo.setKEY_NO(new BigDecimal(getTBPRD_INS_PARAMETER()));
						if (!StringUtils.isBlank(inputVO.getOSEA_BONUS_RATE())) {
							vo.setOSEA_BONUS_RATE(new BigDecimal(inputVO
									.getOSEA_BONUS_RATE()));
						}
						if (!StringUtils.isBlank(inputVO.getPERIOD())) {
							vo.setPERIOD(inputVO.getPERIOD());
						}
						if (!StringUtils.isBlank(inputVO.getPRD_BONUS_RATE())) {
							vo.setPRD_BONUS_RATE(new BigDecimal(inputVO.getPRD_BONUS_RATE()
									.toString()));
						}
						if (!StringUtils.isBlank(inputVO.getYEAR_END_BONUS())) {
							vo.setYEAR_END_BONUS(new BigDecimal(inputVO.getYEAR_END_BONUS()
									.toString()));
						}
						dam_obj.update(vo);
						sendRtnObject(null);
					} else {
						errorMsg = "ehl_01_prd171_003";
						throw new APException(errorMsg);
					}
				} else {
					throw new APException("ehl_01_common_008");
				}
			}
			// 修改-新契約文件設定(6)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "6")) {
				// TBPRD_INS_DOCCHK
				TBPRD_INS_DOCCHKVO vo = (TBPRD_INS_DOCCHKVO) dam_obj
						.findByPKey(TBPRD_INS_DOCCHKVO.TABLE_UID,
								new BigDecimal(inputVO.getSEQ()));
				if (vo != null) {
					if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , "")){
						if (!StringUtils.isBlank(inputVO.getDOC_LEVEL())) {
							vo.setDOC_LEVEL(inputVO.getDOC_LEVEL());
						}
						if (!StringUtils.isBlank(inputVO.getDOC_NAME())) {
							vo.setDOC_NAME(inputVO.getDOC_NAME());
						}
						if (!StringUtils.isBlank(inputVO.getDOC_SEQ())) {
							vo.setDOC_SEQ(new BigDecimal(inputVO.getDOC_SEQ()
									.toString()));
						}

						if (!StringUtils.isBlank(inputVO.getSIGN_INC())) {
							vo.setSIGN_INC(inputVO.getSIGN_INC());
						}
						if (!StringUtils.isBlank(inputVO.getOTH_TYPE())) {
							vo.setOTH_TYPE(inputVO.getOTH_TYPE());
						}
						dam_obj.update(vo);
						sendRtnObject(null);
					} else {
						errorMsg = "ehl_01_prd171_001";
						throw new APException(errorMsg);
					}
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			// 修改-分行留存文件設定(7)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "7")) {
				// TBPRD_INS_DOCCHK
				TBPRD_INS_DOCCHKVO vo = (TBPRD_INS_DOCCHKVO) dam_obj
						.findByPKey(TBPRD_INS_DOCCHKVO.TABLE_UID,
								new BigDecimal(inputVO.getSEQ()));
				if (vo != null) {
					if(chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , "")){
						if (!StringUtils.isBlank(inputVO.getDOC_LEVEL())) {
							vo.setDOC_LEVEL(inputVO.getDOC_LEVEL());
						}
						if (!StringUtils.isBlank(inputVO.getDOC_NAME())) {
							vo.setDOC_NAME(inputVO.getDOC_NAME());
						}
						if (!StringUtils.isBlank(inputVO.getDOC_SEQ())) {
							vo.setDOC_SEQ(new BigDecimal(inputVO.getDOC_SEQ()
									.toString()));
						}

						if (!StringUtils.isBlank(inputVO.getSIGN_INC())) {
							vo.setSIGN_INC(inputVO.getSIGN_INC());
						}
						if (!StringUtils.isBlank(inputVO.getOTH_TYPE())) {
							vo.setOTH_TYPE(inputVO.getOTH_TYPE());
						}
						dam_obj.update(vo);
						sendRtnObject(null);
					} else {
						errorMsg = "ehl_01_prd171_001";
						throw new APException(errorMsg);
					}
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			// 修改-佣金檔設定(8)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "8")) {
				// TBPRD_INS_COMMISSION
				TBPRD_INS_COMMISSIONVO vo = (TBPRD_INS_COMMISSIONVO) dam_obj
						.findByPKey(TBPRD_INS_COMMISSIONVO.TABLE_UID,
								inputVO.getKEY_NO());
				if (vo != null) {
					if(!isFubon(inputVO.getCOMPANY_NUM()) || chk_prdins(inputVO.getTYPE_TABLE() , inputVO.getINSPRD_ID() , inputVO.getINSPRD_ANNUAL())){
						if (!StringUtils.isBlank(inputVO.getANNUAL())) {
							vo.setANNUAL(inputVO.getANNUAL().toString());
						}
						vo.setAPP_DATE(TM);
						vo.setAPPROVER("1");
						if (!StringUtils.isBlank(inputVO.getCOMM_RATE())) {
							vo.setCOMM_RATE(new BigDecimal(inputVO.getCOMM_RATE()
									.toString()));
						}
						if (!StringUtils.isBlank(inputVO.getINSPRD_ANNUAL())) {
							vo.setINSPRD_ANNUAL(inputVO.getINSPRD_ANNUAL().toString());
						}
						if (!StringUtils.isBlank(inputVO.getINSPRD_ID())) {
							vo.setINSPRD_ID(inputVO.getINSPRD_ID().toString().toUpperCase());
						}
						if (!StringUtils.isBlank(inputVO.getKEY_NO())) {
							vo.setKEY_NO(inputVO.getKEY_NO().toString());
						}
						if (!StringUtils.isBlank(inputVO.getTYPE())) {
							vo.setTYPE(inputVO.getTYPE().toString());
						}
						if (StringUtils.isNotBlank(inputVO.getCNR_RATE())) {
							vo.setCNR_RATE(new BigDecimal(inputVO.getCNR_RATE()));
						}
						if (StringUtils.isNotBlank(inputVO.getCNR_YIELD())) {
							vo.setCNR_YIELD(new BigDecimal(inputVO.getCNR_YIELD()));
						}
						if (StringUtils.isNotBlank(inputVO.getCNR_MULTIPLE())) {
							vo.setCNR_MULTIPLE(new BigDecimal(inputVO.getCNR_MULTIPLE()));
						}
						if (inputVO.getMULTIPLE_SDATE() != null) {
							vo.setMULTIPLE_SDATE(new Timestamp(inputVO.getMULTIPLE_SDATE().getTime()));
						}
						if (inputVO.getMULTIPLE_EDATE() != null) {
							vo.setMULTIPLE_EDATE(new Timestamp(inputVO.getMULTIPLE_EDATE().getTime()));
						}
						if (inputVO.getCOMPANY_NUM() != null) {
							vo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
						}
						dam_obj.update(vo);
						sendRtnObject(null);
					} else {
						errorMsg = "ehl_01_prd171_003";
						throw new APException(errorMsg);
					}
				} else {
					throw new APException("ehl_01_common_008");
				}

			}

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			if (errorMsg.length() > 0) {
				throw new APException(errorMsg);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}

	}

	public void deleteData(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		String errorMsg = "";
		try {
			PRD171OutputVO return_VO = new PRD171OutputVO();
			QueryConditionIF queryCondition;
			StringBuffer sql = new StringBuffer();
			dam_obj = this.getDataAccessManager();
			// 刪除-基本設定(1)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "1")) {
				// TBPRD_INS_MAIN
				TBPRD_INS_MAINVO vo = (TBPRD_INS_MAINVO) dam_obj.findByPKey(
						TBPRD_INS_MAINVO.TABLE_UID,
						new BigDecimal(inputVO.getINSPRD_KEYNO()));

				if (vo != null) {
					queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					List<Map<String, Object>> chk_TBIOT_MAIN = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> chk_TBIOT_RIDER_DTL = new ArrayList<Map<String, Object>>();
					sql.append(" SELECT 'x' FROM TBIOT_MAIN ");
					sql.append(" WHERE INSPRD_KEYNO = :INSPRD_KEYNO");
					queryCondition.setObject("INSPRD_KEYNO", inputVO.getINSPRD_KEYNO());
					queryCondition.setQueryString(sql.toString());
					chk_TBIOT_MAIN = dam_obj.exeQuery(queryCondition);

					sql = new StringBuffer();
					sql.append(" SELECT 'x' FROM TBIOT_RIDER_DTL ");
					sql.append(" WHERE INSPRD_KEYNO = :INSPRD_KEYNO ");
					queryCondition.setObject("INSPRD_KEYNO", inputVO.getINSPRD_KEYNO());
					queryCondition.setQueryString(sql.toString());
					chk_TBIOT_RIDER_DTL = dam_obj.exeQuery(queryCondition);

					if (chk_TBIOT_MAIN.size() > 0 || chk_TBIOT_RIDER_DTL.size() > 0) {
						errorMsg = "ehl_01_prd171_005";
						throw new APException(errorMsg);
					} else {
						dam_obj.delete(vo);
						sendRtnObject(null);
					}
				} else {
					throw new APException("ehl_01_common_008");
				}
			}
			// 刪除-特殊條件設定(2)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "2")) {
				// TBPRD_INS_SPECIAL_CND
				TBPRD_INS_SPECIAL_CNDVO vo = (TBPRD_INS_SPECIAL_CNDVO) dam_obj
						.findByPKey(TBPRD_INS_SPECIAL_CNDVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
					dam_obj.delete(vo);
					sendRtnObject(null);

				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			// 刪除-權益書設定(3)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "3")) {
				dam_obj = getDataAccessManager();
				QueryConditionIF qc = dam_obj
						.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				sb.append(" select KEY_NO from VWPRD_INS_ANCDOC where INSPRD_ID = :insprd_id and INSPRD_ANNUAL = :insprd_annual ");
				if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
					qc.setObject("insprd_id", inputVO.getINSPRD_ID().toUpperCase());
				} else {
					qc.setObject("insprd_id", inputVO.getINSPRD_ID());
				}
				qc.setObject("insprd_annual", inputVO.getINSPRD_ANNUAL());
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> key_noList = dam_obj.exeQuery(qc);

				for (Map<String, Object> key_no : key_noList) {
					// TBPRD_INS_ANCDOC
					TBPRD_INS_ANCDOCVO vo = (TBPRD_INS_ANCDOCVO) dam_obj
							.findByPKey(TBPRD_INS_ANCDOCVO.TABLE_UID,
									new BigDecimal(key_no.get("KEY_NO").toString()));
					if (vo != null) {
						dam_obj.delete(vo);
					}
//					else {
//						throw new APException("ehl_01_common_008");
//					}
				}
				sendRtnObject(null);
			}
			// 刪除-連結標的設定(4)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "4")) {
				// TBPRD_INS_LINKING
				TBPRD_INS_LINKINGVO vo = (TBPRD_INS_LINKINGVO) dam_obj
						.findByPKey(TBPRD_INS_LINKINGVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
					dam_obj.delete(vo);
					sendRtnObject(null);
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			// 刪除-提存參數設定(5)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "5")) {
				// TBPRD_INS_PARAMETER
				TBPRD_INS_PARAMETERVO vo = (TBPRD_INS_PARAMETERVO) dam_obj
						.findByPKey(TBPRD_INS_PARAMETERVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
					dam_obj.delete(vo);
					sendRtnObject(null);
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			// 刪除-新契約文件設定(6)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "6")) {
				// TBPRD_INS_DOCCHK
				TBPRD_INS_DOCCHKVO vo = (TBPRD_INS_DOCCHKVO) dam_obj
						.findByPKey(TBPRD_INS_DOCCHKVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
					dam_obj.delete(vo);
					sendRtnObject(null);
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			// 刪除-分行留存文件設定(7)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "7")) {
				// TBPRD_INS_DOCCHK
				TBPRD_INS_DOCCHKVO vo = (TBPRD_INS_DOCCHKVO) dam_obj
						.findByPKey(TBPRD_INS_DOCCHKVO.TABLE_UID,
								new BigDecimal(inputVO.getKEY_NO()));
				if (vo != null) {
					dam_obj.delete(vo);
					sendRtnObject(null);
				} else {
					throw new APException("ehl_01_common_008");
				}

			}
			//刪除-佣金檔設定(8)
			if (StringUtils.equals(inputVO.getTYPE_TABLE(), "8")) {
				// TBPRD_INS_COMMISSION
				TBPRD_INS_COMMISSIONVO vo = (TBPRD_INS_COMMISSIONVO) dam_obj
						.findByPKey(TBPRD_INS_COMMISSIONVO.TABLE_UID,
								inputVO.getKEY_NO());
				if (vo != null) {
					dam_obj.delete(vo);
					sendRtnObject(null);
				} else {
					throw new APException("ehl_01_common_008");
				}

			}

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			if (errorMsg.length() > 0) {
				throw new APException(errorMsg);
			} else {
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}

	}

	// moron 2016/10/05
	public void download(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		PRD171InputVO inputVO = (PRD171InputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		dam_obj = this.getDataAccessManager();

		// xml
		XmlInfo xmlInfo = new XmlInfo();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		switch (inputVO.getP_TYPE()) {
			case "1":
				sb.append("select * from TBPRD_INS_MAIN ");
				// 進件壽險產品參數設定
				break;
			case "2":
				sb.append("select * from VWPRD_INS_SPECIAL_CND where 1=1 ");
				// 特殊條件設定
				break;
			case "3":
				sb.append("select * from VWPRD_INS_ANCDOC where 1=1 ");
				// 權益書設定
				break;
			case "4":
				sb.append("select * from VWPRD_INS_LINKING ");
				// 連結標的上傳
				break;
			case "5":
				sb.append("select * from VWPRD_INS_PARAMETER where 1=1 ");
				// 提存參數設定
				break;
			case "6":
				sb.append("select * from VWPRD_INS_DOCCHK where DOC_TYPE  = '2' and REG_TYPE = '1' and 1=1 ");
				// 新契約進件文件設定
				break;
			case "7":
				sb.append("select * from VWPRD_INS_DOCCHK where DOC_TYPE  = '1' and REG_TYPE = '1' and 1=1 ");
				// 分行文件設定
				break;
			case "8":
				sb.append(" SELECT * FROM VWPRD_INS_COMMISSION ");
				// 佣金檔設定
				break;
		}
		// 基本設定、連結標的設定、佣金檔設定串「保險公司」
		if (inputVO.getP_TYPE().matches("[148]")) {
			sb.append("left join ( ");
			sb.append("    select CNAME COMPANY_NAME, SERIALNUM COMPANY_SEQ ");
			sb.append("    from TBJSB_INS_PROD_COMPANY ");
			sb.append(") COM on (COMPANY_NUM = COM.COMPANY_SEQ)  ");
			sb.append("where 1=1 ");

			if (StringUtils.isNotBlank(inputVO.getCOMPANY_NAME())) {
				sb.append("and COM.COMPANY_NAME like :COM_NAME ");
				qc.setObject("COM_NAME", "%" + inputVO.getCOMPANY_NAME() + "%");
			}
		}

		// where
		if (!StringUtils.isBlank(inputVO.getINSPRD_TYPE())) {
			if (!StringUtils.equals(inputVO.getP_TYPE(), "8")) {
				sb.append("and INSPRD_TYPE=:INSPRD_TYPEE ");
				qc.setObject("INSPRD_TYPEE", inputVO.getINSPRD_TYPE());
			}
		}
		if (!StringUtils.isBlank(inputVO.getINSPRD_NAME())) {
			sb.append("and INSPRD_NAME LIKE :INSPRD_NAMEE ");
			qc.setObject("INSPRD_NAMEE", "%" + inputVO.getINSPRD_NAME() + "%");
		}
		// 進件壽險產品參數設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "1"))
			sb.append("order by INSPRD_ID,INSPRD_ANNUAL ");
		// 特殊條件設定
		if (StringUtils.equals(inputVO.getP_TYPE().toString(), "2"))
			sb.append("order by SEQ ");
		// 權益書設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "3"))
			sb.append("Order by Q_SEQ ");
		// 連結標的上傳
		if (StringUtils.equals(inputVO.getP_TYPE(), "4"))
			sb.append("Order by INSPRD_ID, TARGET_ID ");
		// 提存參數設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "5"))
			sb.append("Order by INSPRD_ID ");
		// 新契約進件文件設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "6"))
			sb.append("Order by DOC_SEQ   ");
		// 分行文件設定
		if (StringUtils.equals(inputVO.getP_TYPE(), "7"))
			sb.append("Order by DOC_SEQ ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam_obj.exeQuery(qc);
		if (list.size() > 0) {
			// gen csv
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "清單_" + sdf.format(new Date()) + "_" + ws.getUser().getUserID() + ".csv";
			List listCSV = new ArrayList();
			for (Map<String, Object> map : list) {
				String[] records;
				int i;
				switch (inputVO.getP_TYPE()) {
					case "1":
						// 進件壽險產品參數設定
						// 27 columns
						records = new String[28];
						i = 0;
						records[i] = checkIsNull(map, "COMPANY_NAME");
						records[++i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = xmlInfo.getVariable("IOT.PRODUCT_TYPE", checkIsNull(map, "INSPRD_TYPE"), FormatHelper.FORMAT_3);
						records[++i] = checkIsNull(map, "INSPRD_ANNUAL");
						records[++i] = checkIsNull(map, "INSPRD_NAME");
						records[++i] = xmlInfo.getVariable("PRD.MAIN_RIDER", checkIsNull(map, "MAIN_RIDER"), FormatHelper.FORMAT_3);
						records[++i] = xmlInfo.getVariable("COMMON.YES_NO", checkIsNull(map, "DIVIDEND_YN"), FormatHelper.FORMAT_3);
						records[++i] = xmlInfo.getVariable("IOT.PAY_TYPE", checkIsNull(map, "PAY_TYPE"), FormatHelper.FORMAT_3);
						records[++i] = xmlInfo.getVariable("FPS.CURRENCY", checkIsNull(map, "CURR_CD"), FormatHelper.FORMAT_3);
						records[++i] = checkIsNull(map, "FEE_STATE");
						records[++i] = checkIsNull(map, "PRD_RATE");
						records[++i] = checkIsNull(map, "CNR_RATE");
						records[++i] = checkIsNull(map, "COEFFICIENT");
						records[++i] = checkIsNull(map, "NEED_MATCH");
						records[++i] = checkIsNull(map, "PRD_RISK");
						records[++i] = xmlInfo.getVariable("PRD.CERT_TYPE", checkIsNull(map, "CERT_TYPE"), FormatHelper.FORMAT_3);
						records[++i] = xmlInfo.getVariable("TRAINING_TYPE", checkIsNull(map, "TRAINING_TYPE"), FormatHelper.FORMAT_3);
//						records[++i] = checkIsNull(map, "EXCH_RATE");
						records[++i] = checkIsNull(map, "AB_EXCH_RATE");
						records[++i] = xmlInfo.getVariable("PRD.INS_CLASS", checkIsNull(map, "INSPRD_CLASS"), FormatHelper.FORMAT_3);
						records[++i] = checkIsNull(map, "SPECIAL_CONDITION");
						records[++i] = "=\"" + checkIsNull(map, "EFFECT_DATE") + "\"";
						records[++i] = "=\"" + checkIsNull(map, "EXPIRY_DATE") + "\"";
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = checkIsNull(map, "APPROVER");
						records[++i] = "=\"" + checkIsNull(map, "APP_DATE") + "\"";
						listCSV.add(records);
						break;
					case "2":
						// 特殊條件設定
						// 12 column
						records = new String[12];
						i = 0;
						records[i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = xmlInfo.getVariable("IOT.PRODUCT_TYPE", checkIsNull(map, "INSPRD_TYPE"), FormatHelper.FORMAT_3);
						records[++i] = checkIsNull(map, "INSPRD_NAME");
						records[++i] = xmlInfo.getVariable("IOT.PAY_TYPE", checkIsNull(map, "PAY_TYPE"), FormatHelper.FORMAT_3);
						records[++i] = checkIsNull(map, "SPECIAL_CONDITION");
						records[++i] = checkIsNull(map, "SEQ");
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = "";
						records[++i] = "";
						listCSV.add(records);
						break;
					case "3":
						// 權益書設定
						// 12 column
						records = new String[12];
						i = 0;
						records[i] = checkIsNull(map, "Q_ID");
						records[++i] = checkIsNull(map, "Q_TYPE");
						records[++i] = checkIsNull(map, "Q_NAME");
						records[++i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = checkIsNull(map, "INSPRD_ANNUAL");
						records[++i] = checkIsNull(map, "Q_SEQ");
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = "";
						records[++i] = "";
						listCSV.add(records);
						break;
					case "4":
						// 連結標的上傳
						// 18 columns
						records = new String[19];
						i = 0;
						records[i] = checkIsNull(map, "COMPANY_NAME");
						records[++i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = checkIsNull(map, "TARGET_ID");
						records[++i] = checkIsNull(map, "FUND_ID");
						records[++i] = checkIsNull(map, "LIPPER_ID");
						records[++i] = checkIsNull(map, "LINKED_NAME");
						records[++i] = checkIsNull(map, "PRD_RISK");
						records[++i] = checkIsNull(map, "COM_PRD_RISK");
						records[++i] = xmlInfo.getVariable("PRD.TRAINING_TYPE", checkIsNull(map, "TRAINING_TYPE"), FormatHelper.FORMAT_3);
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = "";
						records[++i] = "";

						records[++i] = checkIsNull(map, "KYC_SCORE");
						records[++i] = checkIsNull(map, "TARGET_CURR");
						records[++i] = checkIsNull(map, "INT_TYPE");
						records[++i] = checkIsNull(map, "TRANSFER_FLG");

						listCSV.add(records);
						break;
					case "5":
						// 提存參數設定
						// 15 column
						records = new String[15];
						i = 0;
						records[i] = checkIsNull(map, "PERIOD");
						records[++i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = checkIsNull(map, "INSPRD_NAME");
						records[++i] = checkIsNull(map, "INSPRD_ANNUAL");
						records[++i] = checkIsNull(map, "EMP_BONUS_RATE");
						records[++i] = checkIsNull(map, "PRD_BONUS_RATE");
						records[++i] = checkIsNull(map, "OSEA_BONUS_RATE");
						records[++i] = checkIsNull(map, "HIGH_CPCT_BONUS_RATE");
						records[++i] = checkIsNull(map, "YEAR_END_BONUS");
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = "";
						records[++i] = "";
						listCSV.add(records);
						break;
					case "6":
						// 新契約進件文件設定
						// 10 column
						records = new String[10];
						i = 0;
						records[i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = checkIsNull(map, "DOC_NAME");
						records[++i] = checkIsNull(map, "DOC_LEVEL");
						records[++i] = checkIsNull(map, "DOC_SEQ");
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = "";
						records[++i] = "";
						listCSV.add(records);
						break;
					case "7":
						// 分行文件設定
						// 10 column
						records = new String[10];
						i = 0;
						records[i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = checkIsNull(map, "DOC_NAME");
						records[++i] = checkIsNull(map, "DOC_LEVEL");
						records[++i] = checkIsNull(map, "DOC_SEQ");
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = "";
						records[++i] = "";
						listCSV.add(records);
						break;
					case "8":
						// 佣金檔設定
						// 18 columns
						records = new String[18];
						i = 0;
						records[i] = checkIsNull(map, "COMPANY_NAME");
						records[++i] = checkIsNull(map, "INSPRD_ID");
						records[++i] = checkIsNull(map, "INSPRD_ANNUAL");
						records[++i] = checkIsNull(map, "ANNUAL");
//						records[++i] = checkIsNull(map, "TYPE");
						records[++i] = xmlInfo.getVariable("PRD.FEE_STATE", checkIsNull(map, "TYPE"), FormatHelper.FORMAT_3);
						records[++i] = checkIsNull(map, "INSPRD_NAME");
						records[++i] = checkIsNull(map, "COMM_RATE");
						records[++i] = checkIsNull(map, "CNR_RATE");
						records[++i] = checkIsNull(map, "CNR_YIELD");
						records[++i] = checkIsNull(map, "CNR_MULTIPLE");
						records[++i] = checkIsNull(map, "MULTIPLE_SDATE");
						records[++i] = checkIsNull(map, "MULTIPLE_EDATE");
						records[++i] = "=\"" + checkIsNull(map, "CREATETIME") + "\"";
						records[++i] = checkIsNull(map, "CREATOR");
						records[++i] = "=\"" + checkIsNull(map, "LASTUPDATE") + "\"";
						records[++i] = checkIsNull(map, "MODIFIER");
						records[++i] = "";
						records[++i] = "";
						listCSV.add(records);
						break;
				}
			}
			// header
			String[] csvHeader = new String[27];
			int j;
			switch (inputVO.getP_TYPE()) {
				case "1":
					// 進件壽險產品參數設定
					csvHeader = new String[28];
					j = 0;
					csvHeader[j] = "保險公司";
					csvHeader[++j] = "險種代碼";
					csvHeader[++j] = "產品類型";
					csvHeader[++j] = "繳費年期";
					csvHeader[++j] = "險種名稱";
					csvHeader[++j] = "主/附約別";
					csvHeader[++j] = "是否為分紅商品";
					csvHeader[++j] = "繳別";
					csvHeader[++j] = "幣別";
					csvHeader[++j] = "保費類型";
					csvHeader[++j] = "商品收益率";
					csvHeader[++j] = "CNR收益率";
					csvHeader[++j] = "倍數";
					csvHeader[++j] = "是否需要適配";
					csvHeader[++j] = "商品風險值";
					csvHeader[++j] = "證照類型";
					csvHeader[++j] = "教育訓練";
//					csvHeader[++j] = "匯率";
					csvHeader[++j] = "非常態交易參考匯率";
					csvHeader[++j] = "四大分類";
					csvHeader[++j] = "特殊條件";
					csvHeader[++j] = "上市日期";
					csvHeader[++j] = "下市日期";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					break;
				case "2":
					// 特殊條件設定
					csvHeader = new String[12];
					j = 0;
					csvHeader[j] = "險種代碼";
					csvHeader[++j] = "產品類型";
					csvHeader[++j] = "險種名稱";
					csvHeader[++j] = "繳別";
					csvHeader[++j] = "輸入特殊條件內容";
					csvHeader[++j] = "順序";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					break;
				case "3":
					// 權益書設定
					csvHeader = new String[12];
					j = 0;
					csvHeader[j] = "題目代碼";
					csvHeader[++j] = "題目類型";
					csvHeader[++j] = "題目名稱";
					csvHeader[++j] = "險種代碼";
					csvHeader[++j] = "繳費年期";
					csvHeader[++j] = "呈現順序";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					break;
				case "4":
					// 連結標的上傳
					csvHeader = new String[19];
					j = 0;
					csvHeader[j] = "保險公司";
					csvHeader[++j] = "險種代碼";
					csvHeader[++j] = "人壽標的代號";
					csvHeader[++j] = "銀行標的代號";
					csvHeader[++j] = "Lipper標的代號";
					csvHeader[++j] = "連結標的名稱";
					csvHeader[++j] = "風險值";
					csvHeader[++j] = "保險公司標的風險值";
					csvHeader[++j] = "教育訓練";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					csvHeader[++j] = "最低投資風險屬性問卷分數";
					csvHeader[++j] = "標的幣別";
					csvHeader[++j] = "配息方式";
					csvHeader[++j] = "是否可轉換 (V/X)";
					break;
				case "5":
					// 提存參數設定
					csvHeader = new String[15];
					j = 0;
					csvHeader[j] = "專案期間";
					csvHeader[++j] = "險種代碼";
					csvHeader[++j] = "險種名稱";
					csvHeader[++j] = "繳費年期";
					csvHeader[++j] = "理專個獎獎金率";
					csvHeader[++j] = "商品專案獎金率";
					csvHeader[++j] = "海外高峰獎金率";
					csvHeader[++j] = "高產能獎金率";
					csvHeader[++j] = "年終獎金率";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					break;
				case "6":
					// 新契約進件文件設定
					csvHeader = new String[10];
					j = 0;
					csvHeader[j] = "險種代碼";
					csvHeader[++j] = "文件名稱";
					csvHeader[++j] = "文件重要性";
					csvHeader[++j] = "顯示順序";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					break;
				case "7":
					// 分行文件設定
					csvHeader = new String[10];
					j = 0;
					csvHeader[j] = "險種代碼";
					csvHeader[++j] = "文件名稱";
					csvHeader[++j] = "文件重要性";
					csvHeader[++j] = "顯示順序";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					break;
				case "8":
					// 佣金檔設定
					csvHeader = new String[18];
					j = 0;
					csvHeader[j] = "保險公司";
					csvHeader[++j] = "商品代號";
					csvHeader[++j] = "繳費年期";
					csvHeader[++j] = "保單年度";
					csvHeader[++j] = "保費種類";
					csvHeader[++j] = "險種名稱";
					csvHeader[++j] = "商品收益率";
					csvHeader[++j] = "CNR收益率";
					csvHeader[++j] = "CNR分配率";
					csvHeader[++j] = "CNR加減碼";
					csvHeader[++j] = "CNR加碼區間起日";
					csvHeader[++j] = "CNR加碼區間迄日";
					csvHeader[++j] = "建立時間";
					csvHeader[++j] = "建立人員";
					csvHeader[++j] = "異動時間";
					csvHeader[++j] = "異動人員";
					csvHeader[++j] = "覆核人員";
					csvHeader[++j] = "覆核時間";
					break;
			}
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			// download
			notifyClientToDownloadFile(url, fileName);
		} else
			outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	public void upload(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PRD171InputVO inputVO = (PRD171InputVO) body;
		PRD171OutputVO return_VO = new PRD171OutputVO();
		dam_obj = this.getDataAccessManager();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		List<Integer> error = new ArrayList<Integer>();
		// sql
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// (4)連結標的判斷用
		String chkINSPRD_ID = "";
		boolean delete_INSPRD;

		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFileName());
		if (!dataCsv.isEmpty()) {
			for (int i = 0; i < dataCsv.size(); i++) {
				if (i == 0) {
					continue;
				}
				String[] str = dataCsv.get(i);
				// type
				switch (inputVO.getP_TYPE()) {
					case "1":
						// 進件壽險產品參數設定
						// add TBPRD_INS_MAIN
						TBPRD_INS_MAINVO mainvo = new TBPRD_INS_MAINVO();
						mainvo.setINSPRD_KEYNO(getPRD_KEY_NO());
						// 設定保險公司序號
						if (StringUtils.isNotBlank(str[0])) {
							try {
								mainvo.setCOMPANY_NUM(new BigDecimal(str[0]));
							} catch (NumberFormatException e) {
								error.add(i + 1);
								continue;
							}
						}
						// 設定產品類型(必填)
						if (StringUtils.isNotBlank(str[1]))
							mainvo.setINSPRD_TYPE(str[1].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 設定產品分類(必填)
						if (StringUtils.isNotBlank(str[2]))
							mainvo.setINSPRD_CLASS(str[2].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 設定型態
						mainvo.setSPECIAL_CONDITION(str[3].replaceAll("　", "").trim());
						// 設定險種代號(必填)
						if (StringUtils.isNotBlank(str[4]))
							mainvo.setINSPRD_ID(str[4].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 設定險種名稱(必填)
						if (StringUtils.isNotBlank(str[5]))
							mainvo.setINSPRD_NAME(str[5].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 設定險種年期(必填)
						mainvo.setINSPRD_ANNUAL(str[6].replaceAll("　", "").trim());
						if (StringUtils.isNotBlank(str[7]))
							mainvo.setMAIN_RIDER(str[7].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 設定繳別(必填)
						if (StringUtils.isNotBlank(str[8]))
							mainvo.setPAY_TYPE(str[8].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 設定幣別(必填)
						if (StringUtils.isNotBlank(str[9]))
							mainvo.setCURR_CD(str[9].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 設定保費類型
						mainvo.setFEE_STATE(str[10].replaceAll("　", "").trim());
						// 商品收益率
						if (StringUtils.isNotBlank(str[11]))
							mainvo.setPRD_RATE(new BigDecimal(str[11].replaceAll("　", "").trim()));
						// 設定CNR收益率
						if (StringUtils.isNotBlank(str[12]))
							mainvo.setCNR_RATE(new BigDecimal(str[12].replaceAll("　", "").trim()));
						// 設定倍數
						if (StringUtils.isNotBlank(str[13]))
							mainvo.setCOEFFICIENT(new BigDecimal(str[13].replaceAll("　", "").trim()));
						// 設定是否需要適配(必填)
						if (StringUtils.isNotBlank(str[14]))
							mainvo.setNEED_MATCH(str[14].replaceAll("　", "").trim());
						else {
							error.add(i + 1);
							continue;
						}
						// 商品風險值
						if (StringUtils.isNotBlank(str[15])) {
							mainvo.setPRD_RISK(str[15].replaceAll("　", "").trim());
						} else {
							//非投資型商品且需適配，風險值不可為空
							if(StringUtils.equals("1", mainvo.getINSPRD_TYPE()) && StringUtils.equals("Y", mainvo.getNEED_MATCH())) {
								error.add(i+1);
								continue;
							}
						}
						// 設定證照類型
						mainvo.setCERT_TYPE(str[16].replaceAll("　", "").trim());
						// 設定教育訓練
						mainvo.setTRAINING_TYPE(str[17].replaceAll("　", "").trim());
						// 設定匯率
						if (StringUtils.isNotBlank(str[18]))
							mainvo.setEXCH_RATE(new BigDecimal(str[18].replaceAll("　", "").trim()));
						// 設定非常態檢核交易參考匯率
						if (StringUtils.isNotBlank(str[19]))
							mainvo.setAB_EXCH_RATE(new BigDecimal(str[19].replaceAll("　", "").trim()));
						// 設定上市日期(必填)
						if (StringUtils.isNotBlank(str[20])) {
							try {
								mainvo.setEFFECT_DATE(new Timestamp(sdf.parse(str[20].replaceAll("　", "").trim()).getTime()));
							} catch (Exception e) {
								try {
									mainvo.setEFFECT_DATE(new Timestamp(sdf2.parse(str[20].replaceAll("　", "").trim()).getTime()));
								} catch (Exception e2) {
									error.add(i + 1);
									continue;
								}
							}
						} else {
							error.add(i + 1);
							continue;
						}
						// 設定下市日期
						if (StringUtils.isNotBlank(str[21])) {
							try {
								mainvo.setEXPIRY_DATE(new Timestamp(sdf.parse(str[21].replaceAll("　", "").trim()).getTime()));
							} catch (Exception e) {
								try {
									mainvo.setEXPIRY_DATE(new Timestamp(sdf2.parse(str[21].replaceAll("　", "").trim()).getTime()));
								} catch (Exception e2) {
									error.add(i + 1);
									continue;
								}
							}
						}
						// 設定是否為分紅商品
						if (StringUtils.isNotBlank(str[22])) {
							String dividendYN = str[22].replaceAll("　", "").trim();
							if(dividendYN.matches("Y|N")) { //只能是Y/N的值
								mainvo.setDIVIDEND_YN(dividendYN);
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							mainvo.setDIVIDEND_YN("N");
						}
						dam_obj.create(mainvo);
						break;
					case "2":
						// 特殊條件設定
						// add TBPRD_INS_SPECIAL_CND
						TBPRD_INS_SPECIAL_CNDVO specialvo = new TBPRD_INS_SPECIAL_CNDVO();
						specialvo.setKEY_NO(getPRD_KEY_NO());
						// 險種代碼(必填)+繳費類型(必填)[需檢核]
						if (StringUtils.isNotBlank(str[0])) {
							if(chk_prdins(inputVO.getP_TYPE() ,str[0].replaceAll("　", "").trim(), str[1].replaceAll("　", "").trim())){
								specialvo.setINSPRD_ID(str[0].replaceAll("　", "").trim());
								specialvo.setPAY_TYPE(str[1].replaceAll("　", "").trim());
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							error.add(i + 1);
							continue;
						}
						// 輸入特殊條件內容(必填)
						if (StringUtils.isNotBlank(str[2])) {
							specialvo.setSPECIAL_CONDITION(str[2].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						// 順序(必填)
						if (StringUtils.isNotBlank(str[3])) {
							specialvo.setSEQ(new BigDecimal(str[3].replaceAll("　", "").trim()));
						} else {
							error.add(i + 1);
							continue;
						}
						dam_obj.create(specialvo);
						break;
					case "3":
						// 權益書設定
						// add TBPRD_INS_ANCDOCVO
						TBPRD_INS_ANCDOCVO ancdocvo = new TBPRD_INS_ANCDOCVO();
						ancdocvo.setKEY_NO(getPRD_KEY_NO());
						// 險種代碼(必填)+繳費年期(必填) [需檢核]
						if (StringUtils.isNotBlank(str[0]) || StringUtils.isNotBlank(str[1])) {
							if(chk_prdins(inputVO.getP_TYPE() , str[0].replaceAll("　", "").trim() , str[1].replaceAll("　", "").trim())){
								ancdocvo.setINSPRD_ID(str[0].replaceAll("　", "").trim());
								ancdocvo.setINSPRD_ANNUAL(str[1].replaceAll("　", "").trim());
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							error.add(i + 1);
							continue;
						}
						// 題目代碼(必填)
						if (StringUtils.isNotBlank(str[2])) {
							ancdocvo.setQ_ID(str[2].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						// 呈現順序(必填)
						if (StringUtils.isNotBlank(str[3])) {
							ancdocvo.setQ_SEQ(new BigDecimal(str[3].replaceAll("　", "").trim()));
						} else {
							error.add(i + 1);
							continue;
						}
						dam_obj.create(ancdocvo);
						break;
					case "4":
						// 連結標的上傳
						// add TBPRD_INS_LINKING
						
						//先檢查保險公司序號、險種代碼、人壽標的代號
						if (StringUtils.isBlank(str[0]) || StringUtils.isBlank(str[1]) || StringUtils.isBlank(str[2])) {
							error.add(i + 1);
							continue;
						}
						
						//檢查新增資料或更新資料
						boolean isUpdate = false; //是否為更新資料
						TBPRD_INS_LINKINGVO linkvo = new TBPRD_INS_LINKINGVO();
						List<Criterion> criList = new ArrayList<Criterion>();
						List<TBPRD_INS_LINKINGVO> findResult = new ArrayList<TBPRD_INS_LINKINGVO>();
						String companyNum = StringUtils.trimToEmpty(str[0]);
						criList.add(Restrictions.eq("INSPRD_ID", StringUtils.trimToEmpty(str[1])));
						criList.add(Restrictions.eq("TARGET_ID", StringUtils.trimToEmpty(str[2])));
						if(!"82".equals(companyNum)) {
							//非富壽
							criList.add(Restrictions.eq("COMPANY_NUM", new BigDecimal(companyNum)));
							criList.add(Restrictions.eq("TARGET_CURR", StringUtils.trimToEmpty(str[9])));
						}
						findResult = (List<TBPRD_INS_LINKINGVO>) dam_obj.findByCriteria(TBPRD_INS_LINKINGVO.TABLE_UID, criList);
						if(CollectionUtils.isNotEmpty(findResult)) {
							//為更新資料
							linkvo = findResult.get(0);
							isUpdate = true;
						}
						
						// 保險公司序號
						if (StringUtils.isNotBlank(str[0])) {
							try {
								linkvo.setCOMPANY_NUM(new BigDecimal(str[0]));
							} catch (NumberFormatException e) {
								error.add(i + 1);
								continue;
							}
						}
						//險種代號檢核(必填)[需檢核]
						if (StringUtils.isNotBlank(str[1])) {
							if(!isFubon(str[0]) || chk_prdins(inputVO.getP_TYPE() ,str[1].replaceAll("　", "").trim(), "")){
								linkvo.setINSPRD_ID(str[1].replaceAll("　", "").trim());
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							error.add(i + 1);
							continue;
						}
						//人壽標的代號(必填)[不檢核]
						if (StringUtils.isNotBlank(str[2])) {
							linkvo.setTARGET_ID(str[2].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						//銀行標的代號(非必填)[暫時不檢核]
						if (StringUtils.isNotBlank(str[3])) {
							linkvo.setFUND_ID(str[3].replaceAll("　", "").trim());
						}
						//Lipper標的代號(非必填)
						if (StringUtils.isNotBlank(str[4])) {
							linkvo.setLIPPER_ID(str[4].replaceAll("　", "").trim());
						}
						//連結標的名稱(必填)[不檢核]
						if (StringUtils.isNotBlank(str[5])) {
							linkvo.setLINKED_NAME(str[5].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						//風險值(必填)[需檢核]
						if (StringUtils.isNotBlank(str[3])) {
							// get PRD_RISK
							queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sql = new StringBuffer();
							sql.append("select RISKCATE_ID from TBPRD_FUND where PRD_ID = :id ");
							queryCondition.setObject("id", str[3].replaceAll("　", "").trim());
							queryCondition.setQueryString(sql.toString());
							List<Map<String, Object>> prd_risklist = dam_obj.exeQuery(queryCondition);
							if (prd_risklist.get(0).get("RISKCATE_ID") != null) {
								linkvo.setPRD_RISK((String) prd_risklist.get(0).get("RISKCATE_ID"));
							} else {
								error.add(i + 1);
								continue;
							}
						} else if (!StringUtils.isNotBlank(str[3])) { //沒有銀行標的，則用自行填入的風險值
							linkvo.setPRD_RISK(str[6].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						//保險公司標的風險值(非必填)
						if (StringUtils.isNotBlank(str[7])) {
							linkvo.setCOM_PRD_RISK(str[7].replaceAll("　", "").trim());
						}
						//教育訓練(非必填)
						if (StringUtils.isNotBlank(str[8])) {
							linkvo.setTRAINING_TYPE(str[8].replaceAll("　", "").trim());
						}
						// 標的幣別
						if (StringUtils.isNotBlank(str[9])) {
							linkvo.setTARGET_CURR(str[9].replaceAll("　", "").trim());
						}
						// 配息方式
						if (StringUtils.isNotBlank(str[10])) {
							linkvo.setINT_TYPE(str[10].replaceAll("　", "").trim());
						}
						// 是否可轉換 (V/X)
						if (StringUtils.isNotBlank(str[11])) {
							linkvo.setTRANSFER_FLG(str[11].replaceAll("　", "").trim());
						}
						
						if(isUpdate) {
							dam_obj.update(linkvo);
						} else {
							linkvo.setKEY_NO(getPRD_KEY_NO());
							dam_obj.create(linkvo);
						}
						
						break;
					case "5":
						// 提存參數設定
						// add TBPRD_INS_PARAMETER
						TBPRD_INS_PARAMETERVO insvo = new TBPRD_INS_PARAMETERVO();
						insvo.setKEY_NO(getPRD_KEY_NO());
						// 專案期間(必填)
						if (StringUtils.isNotBlank(str[0])) {
							insvo.setPERIOD(str[0].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						// 險種代碼(必填)+繳費年期(必填)[需檢核]
						if (StringUtils.isNotBlank(str[1]) || StringUtils.isNotBlank(str[2])) {
							if(chk_prdins(inputVO.getP_TYPE() , str[1].replaceAll("　", "").trim(),str[2].replaceAll("　", "").trim())){
								insvo.setINSPRD_ID(str[1].replaceAll("　", "").trim());
								insvo.setINSPRD_ANNUAL(str[2].replaceAll("　", "").trim());
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							error.add(i + 1);
							continue;
						}
						// 理專個獎獎金率(非必填)
						if (StringUtils.isNotBlank(str[3]))
							insvo.setEMP_BONUS_RATE(new BigDecimal(str[3].replaceAll("　", "").trim()));
						// 商品專案獎金率(非必填)
						if (StringUtils.isNotBlank(str[4]))
							insvo.setPRD_BONUS_RATE(new BigDecimal(str[4].replaceAll("　", "").trim()));
						// 海外高峰獎金率(非必填)
						if (StringUtils.isNotBlank(str[5]))
							insvo.setOSEA_BONUS_RATE(new BigDecimal(str[5].replaceAll("　", "").trim()));
						// 高產能獎勵金率(非必填)
						if (StringUtils.isNotBlank(str[6]))
							insvo.setHIGH_CPCT_BONUS_RATE(new BigDecimal(str[6].replaceAll("　", "").trim()));
						// 年終獎金(非必填)
						if (StringUtils.isNotBlank(str[7]))
							insvo.setYEAR_END_BONUS(new BigDecimal(str[7].replaceAll("　", "").trim()));
						dam_obj.create(insvo);
						break;
					case "6":
						// 新契約進件文件設定
						// add TBPRD_INS_DOCCHKVO
						TBPRD_INS_DOCCHKVO insdocvo = new TBPRD_INS_DOCCHKVO();
						insdocvo.setKEY_NO(getPRD_KEY_NO());
						// 險種代號(必填)(需檢核)
						if (StringUtils.isNotBlank(str[0])) {
							if(chk_prdins(inputVO.getP_TYPE(),str[0].replaceAll("　", "").trim(),"")){
								insdocvo.setINSPRD_ID(str[0].replaceAll("　", "").trim());
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							error.add(i + 1);
							continue;
						}
						// 文件名稱(必填)
						if (StringUtils.isNotBlank(str[1])) {
							insdocvo.setDOC_NAME(str[1].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						// 顯示順序(必填)
						if (StringUtils.isNotBlank(str[2])) {
							insdocvo.setDOC_SEQ(new BigDecimal(str[2].replaceAll("　", "").trim()));
						} else {
							error.add(i + 1);
							continue;
						}
						// 文件重要性(必填)
						if (StringUtils.isNotBlank(str[3])) {
							insdocvo.setDOC_LEVEL(str[3].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}

						//新契約須簽屬必為Y
						insdocvo.setDOC_TYPE("2");
						insdocvo.setREG_TYPE("1");
						insdocvo.setSIGN_INC("Y");
						dam_obj.create(insdocvo);
						break;
					case "7":
						// 分行文件設定
						/// add TBPRD_INS_DOCCHKVO
						TBPRD_INS_DOCCHKVO insdocvo1 = new TBPRD_INS_DOCCHKVO();
						insdocvo1.setKEY_NO(getPRD_KEY_NO());
						// 險種代號(必填)(需檢核)
						if (StringUtils.isNotBlank(str[0])) {
							if(chk_prdins(inputVO.getP_TYPE(),str[0].replaceAll("　", "").trim(),"")){
								insdocvo1.setINSPRD_ID(str[0].replaceAll("　", "").trim());
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							error.add(i + 1);
							continue;
						}
						// 文件名稱(必填)
						if (StringUtils.isNotBlank(str[1])) {
							insdocvo1.setDOC_NAME(str[1].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						// 顯示順序(必填)
						if (StringUtils.isNotBlank(str[2])) {
							insdocvo1.setDOC_SEQ(new BigDecimal(str[2].replaceAll("　", "").trim()));
						} else {
							error.add(i + 1);
							continue;
						}
						// 文件重要性(必填)
						if (StringUtils.isNotBlank(str[3])) {
							insdocvo1.setDOC_LEVEL(str[3].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}

						//新契約須簽屬必為Y
						insdocvo1.setDOC_TYPE("1");
						insdocvo1.setREG_TYPE("1");
						insdocvo1.setSIGN_INC("Y");
						dam_obj.create(insdocvo1);
						break;
					case "8":
						// 佣金檔設定
						/// add TBPRD_INS_COMMISSIONVO
						TBPRD_INS_COMMISSIONVO commissionVO = new TBPRD_INS_COMMISSIONVO();
						commissionVO.setKEY_NO(getPRD_KEY_NO().toString());
						// 保險公司序號
						if (StringUtils.isNotBlank(str[0])) {
							try {
								commissionVO.setCOMPANY_NUM(new BigDecimal(str[0]));
							} catch (NumberFormatException e) {
								error.add(i + 1);
								continue;
							}
						}
						// 險種代碼(必填) + 繳費年期(必填)[需檢核]
						if (StringUtils.isNotBlank(str[1]) || StringUtils.isNotBlank(str[2])) {
							if(!isFubon(str[0]) || chk_prdins(inputVO.getP_TYPE() , str[1].replaceAll("　", "").trim() , str[2].replaceAll("　", "").trim())){
								commissionVO.setINSPRD_ID(str[1].replaceAll("　", "").trim());
								commissionVO.setINSPRD_ANNUAL(str[2].replaceAll("　", "").trim());
							} else {
								error.add(i + 1);
								continue;
							}
						} else {
							error.add(i + 1);
							continue;
						}
						// 保單年度(必填)
						if (StringUtils.isNotBlank(str[3])) {
							commissionVO.setANNUAL(str[3].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						// 保費種類(必填)
						if (StringUtils.isNotBlank(str[4])) {
							commissionVO.setTYPE(str[4].replaceAll("　", "").trim());
						} else {
							error.add(i + 1);
							continue;
						}
						// 商品收益率(必填)
						if (StringUtils.isNotBlank(str[5])) {
							commissionVO.setCOMM_RATE(new BigDecimal(str[5].replaceAll("　", "").trim()));
						} else {
							error.add(i + 1);
							continue;
						}
						// CNR收益率(必填)
						if (StringUtils.isNotBlank(str[6])) {
							commissionVO.setCNR_RATE(new BigDecimal(str[6].replaceAll("　", "").trim()));
						} else {
							error.add(i + 1);
							continue;
						}
						// CNR分配率(非必填)
						if (StringUtils.isNotBlank(str[7]))
							commissionVO.setCNR_YIELD(new BigDecimal(str[7].replaceAll("　", "").trim()));
						// CNR加減碼(非必填)
						if (StringUtils.isNotBlank(str[8]))
							commissionVO.setCNR_MULTIPLE(new BigDecimal(str[8].replaceAll("　", "").trim()));
						// CNR加碼區間起日(非必填)
						if (StringUtils.isNotBlank(str[9]))
							commissionVO.setMULTIPLE_SDATE(new Timestamp(df.parse(str[9].replaceAll("　", "").trim()).getTime()));
//						// CNR加碼區間迄日(非必填)
						if (StringUtils.isNotBlank(str[10]))
							commissionVO.setMULTIPLE_EDATE(new Timestamp(df.parse(str[10].replaceAll("　", "").trim()).getTime()));

						dam_obj.create(commissionVO);
						break;
				}
			}
		}
		return_VO.setErrorList(error);
		this.sendRtnObject(return_VO);
	}

	public void downloadSimple(Object body, IPrimitiveMap header) throws Exception {
		PRD171InputVO inputVO = (PRD171InputVO) body;

		switch (inputVO.getP_TYPE()) {
			case "1":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE1.csv", "基本設定-上傳範例.csv");
				// 進件壽險產品參數設定
				break;
			case "2":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE2.csv", "特殊條件設定-上傳範例.csv");
				// 特殊條件設定
				break;
			case "3":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE3.csv", "權益書設定-上傳範例.csv");
				// 權益書設定
				break;
			case "4":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE4.csv", "連結標的設定-上傳範例.csv");
				// 連結標的上傳
				break;
			case "5":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE5.csv", "提存參數設定-上傳範例.csv");
				// 提存參數設定
				break;
			case "6":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE6.csv", "新契約文件設定-上傳範例.csv");
				// 新契約進件文件設定
				break;
			case "7":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE7.csv", "分行留存文件設定-上傳範例.csv");
				// 分行文件設定
				break;
			case "8":
				notifyClientToDownloadFile("doc//PRD//PRD171_TYPE8.csv", "佣金檔設定-上傳範例.csv");
				// 佣金檔設定
				break;
		}
		this.sendRtnObject(null);
	}

	// 複製文件-新契約文件設定(6)/分行留存文件設定(7)用
	public void copyDOCCHK(Object body, IPrimitiveMap header) throws JBranchException {
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		List<Map<String, Object>> checkProd = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> DOCCHKList = new ArrayList<Map<String, Object>>();


		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" select 'x' as X from TBPRD_INS_MAIN  ");
		sb.append(" where INSPRD_ID = :INSPRD_ID ");
		switch (inputVO.getYN_Copy()) {
			case "1":
				if (StringUtils.isNotBlank(inputVO.getCopy_insprd_id())) {
					qc.setObject("INSPRD_ID", inputVO.getCopy_insprd_id().toUpperCase());
				} else {
					qc.setObject("INSPRD_ID", inputVO.getCopy_insprd_id());
				}
				break;
			case "2":
				if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
					qc.setObject("INSPRD_ID", inputVO.getINSPRD_ID().toUpperCase());
				} else {
					qc.setObject("INSPRD_ID", inputVO.getINSPRD_ID());
				}
				break;
			default:
				break;
		}
		qc.setQueryString(sb.toString());
		checkProd = dam_obj.exeQuery(qc);
		//確認基本檔有無資料
		if (checkProd.size() > 0) {
			qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" select * from TBPRD_INS_DOCCHK where INSPRD_ID = :insprd_id  ");
			sb.append(" and DOC_TYPE = :doc_type and REG_TYPE = '1' ");
			switch (inputVO.getYN_Copy()) {
				case "1":
					if (StringUtils.isNotBlank(inputVO.getCopy_insprd_id())) {
						qc.setObject("insprd_id", inputVO.getCopy_insprd_id().toUpperCase());
					} else {
						qc.setObject("insprd_id", inputVO.getCopy_insprd_id());
					}
					break;
				case "2":
					if (StringUtils.isNotBlank(inputVO.getINSPRD_ID())) {
						qc.setObject("insprd_id", inputVO.getINSPRD_ID().toUpperCase());
					} else {
						qc.setObject("insprd_id", inputVO.getINSPRD_ID());
					}
					break;
				default:
					break;
			}
			qc.setObject("doc_type", inputVO.getDOC_TYPE());
			qc.setQueryString(sb.toString());
			DOCCHKList = dam_obj.exeQuery(qc);

			switch (inputVO.getYN_Copy()) {
				case "1":
					if (DOCCHKList.size() <= 0) {
						throw new APException("ehl_01_common_017");
					} else {
						outputVO.setDOCCHKList(DOCCHKList);
						sendRtnObject(outputVO);
					}
					break;
				case "2":
					if (DOCCHKList.size() > 0) {
						throw new APException("ehl_01_common_016");
					} else {
						sendRtnObject(null);
					}
				default:
					break;
			}
		} else {
			throw new APException("ehl_01_prd171_001");
		}
	}
	/** KEY_NO產生 **/
	public BigDecimal getPRD_KEY_NO() throws JBranchException {
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBPRD_INS_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam_obj.exeQuery(queryCondition);
		BigDecimal seqNo = (BigDecimal) SEQLIST.get(0).get("SEQ");
		return seqNo;
	}

	/** 文件拷貝加入 **/
	public void DOCCHK_COPY_ADD(Object body, IPrimitiveMap header) throws JBranchException {
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		Timestamp TM = new Timestamp(System.currentTimeMillis());
		switch (inputVO.getTYPE_TABLE()) {
			case "6":
				for (Map<String, Object> map : inputVO.getDOCCHKList()) {
					dam_obj = getDataAccessManager();
					TBPRD_INS_DOCCHKVO vo = new TBPRD_INS_DOCCHKVO();

					if (map.get("DOC_LEVEL") != null) {
						vo.setDOC_LEVEL(map.get("DOC_LEVEL").toString());
					}
					if (map.get("DOC_NAME") != null) {
						vo.setDOC_NAME(map.get("DOC_NAME").toString());
					}
					if (map.get("DOC_SEQ") != null) {
						vo.setDOC_SEQ(new BigDecimal(map.get("DOC_SEQ").toString()));
					}
					vo.setDOC_TYPE("2");

					//新契約須簽屬必為Y
					vo.setSIGN_INC("Y");
					vo.setKEY_NO(getPRD_KEY_NO());
					vo.setREG_TYPE("1");
					if (map.get("OTH_TYPE") != null) {
						vo.setOTH_TYPE(map.get("OTH_TYPE").toString());
					}
					vo.setINSPRD_ID(inputVO.getINSPRD_ID().toUpperCase());
					vo.setAPP_DATE(TM);
					vo.setAPPROVER("2");
					dam_obj.create(vo);
				}
				break;
			case "7":
				for (Map<String, Object> map : inputVO.getDOCCHKList()) {
					dam_obj = getDataAccessManager();
					TBPRD_INS_DOCCHKVO vo = new TBPRD_INS_DOCCHKVO();
					if (map.get("DOC_LEVEL") != null) {
						vo.setDOC_LEVEL(map.get("DOC_LEVEL").toString());
					}
					if (map.get("DOC_NAME") != null) {
						vo.setDOC_NAME(map.get("DOC_NAME").toString());
					}
					if (map.get("DOC_SEQ") != null) {
						vo.setDOC_SEQ(new BigDecimal(map.get("DOC_SEQ").toString()));
					}

					vo.setSIGN_INC("Y");
					vo.setKEY_NO(getPRD_KEY_NO());
					vo.setREG_TYPE("1");
					if (map.get("OTH_TYPE") != null) {
						vo.setOTH_TYPE(map.get("OTH_TYPE").toString());
					}
					vo.setINSPRD_ID(inputVO.getINSPRD_ID().toUpperCase());
					vo.setAPP_DATE(TM);
					vo.setAPPROVER("1");
					vo.setDOC_TYPE("1");
					dam_obj.create(vo);
				}
				break;
			default:
				break;
		}
		sendRtnObject(null);
	}
	/** 依TABLE_TYPE檢查險種是否存在 **/
	private boolean chk_prdins (String Table_Type ,String PRD_ID , String type) throws JBranchException{
		List<Map<String, Object>> TypeList = null;
		//連結標的設定(4) , 新契約進件文件設定(6) , 分行文件設定(7)
		if (Table_Type.equals("4") || Table_Type.equals("6") || Table_Type.equals("7")) {
			StringBuffer sql = new StringBuffer();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" select 'x' as X from TBPRD_INS_MAIN ");
			sql.append(" where INSPRD_ID =:INSPRD_ID");
			queryCondition.setObject("INSPRD_ID", PRD_ID.toUpperCase());
			queryCondition.setQueryString(sql.toString());
			TypeList = dam_obj.exeQuery(queryCondition);
		}
		//特殊條件設定(2)
		if (Table_Type.equals("2")) {
			StringBuffer sql = new StringBuffer();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" select 'x' as X from TBPRD_INS_MAIN ");
			sql.append(" where INSPRD_ID =:INSPRD_ID ");
			sql.append(" and PAY_TYPE =:PAT_TYPE ");
			queryCondition.setObject("INSPRD_ID", PRD_ID.toUpperCase());
			queryCondition.setObject("PAT_TYPE", type);
			queryCondition.setQueryString(sql.toString());
			TypeList = dam_obj.exeQuery(queryCondition);
		}
		//權益書設定(3) , 提存參數設定(5) , 佣金檔設定(8)
		if (Table_Type.equals("3") || Table_Type.equals("5") || Table_Type.equals("8")) {
			StringBuffer sql = new StringBuffer();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" select 'x' as X from TBPRD_INS_MAIN ");
			sql.append(" where INSPRD_ID =:INSPRD_ID ");
			sql.append(" and INSPRD_ANNUAL =:INSPRD_ANNUAL ");
			queryCondition.setObject("INSPRD_ID", PRD_ID.toUpperCase());
			queryCondition.setObject("INSPRD_ANNUAL", type);
			queryCondition.setQueryString(sql.toString());
			TypeList = dam_obj.exeQuery(queryCondition);
		}

		if (TypeList.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * 用於檢查登入者是否可顯示覆核下拉式選單
	 * @throws JBranchException
	 * **/
	public void chkAuth(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		PRD171InputVO inputVO = (PRD171InputVO) body;
		PRD171OutputVO outputVO = new PRD171OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(1) AS chkAuth FROM TBSYSSECUROLPRIASS ");
		sb.append(" WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PRD171' AND FUNCTIONID = 'confirm') ");
		sb.append(" AND ROLEID = '" + getUserVariable(FubonSystemVariableConsts.LOGINROLE) + "'");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam_obj.exeQuery(queryCondition);
		outputVO.setChk_list(list);
		sendRtnObject(outputVO);
	}

	/**
	 * 更新覆核人員
	 * @throws JBranchException
	 * */
	public void updataAPPROVER(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		Timestamp TM = new Timestamp(System.currentTimeMillis());
		PRD171EDITInputVO inputVO = (PRD171EDITInputVO) body;
		PRD171OutputVO return_VO = new PRD171OutputVO();
		String errorMsg = "";
		String sucessMsg = "";
		dam_obj = this.getDataAccessManager();
		for (Map<String, Object> AOC_LIST : inputVO.getDILOGList()) {
			try {
				TBPRD_INS_MAINVO vo = (TBPRD_INS_MAINVO) dam_obj.findByPKey(TBPRD_INS_MAINVO.TABLE_UID, new BigDecimal((double) AOC_LIST.get("INSPRD_KEYNO")));
				if (vo != null) {
					if ("Y".equals(AOC_LIST.get("choice")) && "未覆核".equals(inputVO.getAPPROVER())) {
						vo.setAPP_DATE(TM);
						vo.setAPPROVER(getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
						dam_obj.update(vo);
						sucessMsg = "核可成功";
					}
					if ("Y".equals(AOC_LIST.get("choice")) && "已覆核".equals(inputVO.getAPPROVER())) {
						vo.setAPP_DATE(null);
						vo.setAPPROVER(null);
						dam_obj.update(vo);
						sucessMsg = "退回成功";
					}
				}
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s",
						StringUtil.getStackTraceAsString(e)));
				if (errorMsg.length() > 0) {
					throw new APException(errorMsg);
				} else {
					throw new APException("系統發生錯誤請洽系統管理員");
				}
			}
		}
		sendRtnObject(sucessMsg);
	}
	//end

	/** 判斷傳入的參數是否為富邦的保險公司代號 **/
	public static boolean isFubon(BigDecimal companyNum) {
		return companyNum != null && FUBON_COMPANY_NUM.equals(Integer.toString(companyNum.intValue()));
	}
	/** 判斷傳入的參數是否為富邦的保險公司代號 **/
	public static boolean isFubon(String companyNum) {
		return StringUtils.isNotBlank(companyNum) && FUBON_COMPANY_NUM.equals(companyNum);
	}
}
