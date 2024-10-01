package com.systex.jbranch.app.server.fps.iot900;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK;
import com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * IOT900
 * 
 * @author Jimmy
 * @date 2016/08/31
 * @spec null
 */
@Component("iot900")
@Scope("request")
public class IOT900 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DataAccessManager dam_obj;
	
	public void Initial(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT900InputVO inputVO = (IOT900InputVO) body;
		DocChkVO outputVO = new DocChkVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			switch (inputVO.getOPR_STATUS()) {
			case "UPDATE":
				if("1".equals(inputVO.getIN_TYPE())){
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" select DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ,DOC_NAME_OTH ");
					sb.append(" From TBIOT_DOC_CHK where doc_type= :doc_type and INS_KEYNO = :ins_keyno ");
					qc.setObject("doc_type", inputVO.getIN_TYPE());
					qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
					qc.setQueryString(sb.toString());
					outputVO.setINList(dam_obj.exeQuery(qc));
				}
				if("2".equals(inputVO.getIN_TYPE())){
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" select DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ,DOC_NAME_OTH ");
					sb.append(" From TBIOT_DOC_CHK where doc_type= :doc_type and INS_KEYNO = :ins_keyno ");
					qc.setObject("doc_type", inputVO.getIN_TYPE());
					qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
					qc.setQueryString(sb.toString());
					outputVO.setOUTList(dam_obj.exeQuery(qc));
				}
				sendRtnObject(outputVO);
				break;
			case "new":
				if("1".equals(inputVO.getIN_TYPE())){
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT 'N' DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ ");
					if(inputVO.getIn_othtype() == null){
						sb.append(" From VWPRD_INS_DOCCHK Where reg_type = :in_REGTYPE and nvl(oth_type,'$') = nvl("+inputVO.getIn_othtype()+",'$') ");
					}else{
						sb.append(" From VWPRD_INS_DOCCHK Where reg_type = :in_REGTYPE and nvl(oth_type,'$') = nvl(:in_othtype,'$') ");
						qc.setObject("in_othtype", inputVO.getIn_othtype());
					}
					if("1".equals(inputVO.getIn_REGTYPE())){
						sb.append(" and INSPRD_ID = :IN_INSPRDID  ");
						qc.setObject("IN_INSPRDID", inputVO.getINSPRD_ID());
					}
					if(StringUtils.equals("1", inputVO.getREG_TYPE()) &&
							StringUtils.isNotBlank(inputVO.getCASE_ID()) && !StringUtils.equals("J999", inputVO.getCASE_ID().substring(0, 4))) {
						//新契約電子要保書：要保書案件編號不為J999或空白，則一險種帶出重要性2(檢附), 3(APP必備)資料
						sb.append(" AND DOC_LEVEL IN ('2', '3') ");
					} else {
						//其他都不需列出3(APP必備)資料
						sb.append(" AND DOC_LEVEL <> '3' ");
					}
					sb.append(" And doc_type = :IN_DOCTYPE Order by doc_seq ");
					qc.setObject("in_REGTYPE", inputVO.getIn_REGTYPE());
					qc.setObject("IN_DOCTYPE", inputVO.getIN_TYPE());
					qc.setQueryString(sb.toString());
					outputVO.setINList(dam_obj.exeQuery(qc));
				}
				if("2".equals(inputVO.getIN_TYPE())){
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT 'N' DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ ");
					if(inputVO.getIn_othtype() == null){
						sb.append(" From VWPRD_INS_DOCCHK Where reg_type = :in_REGTYPE and nvl(oth_type,'$') = nvl("+inputVO.getIn_othtype()+",'$') ");
					}else{
						sb.append(" From VWPRD_INS_DOCCHK Where reg_type = :in_REGTYPE and nvl(oth_type,'$') = nvl(:in_othtype,'$') ");
						qc.setObject("in_othtype", inputVO.getIn_othtype());
					}
					if("1".equals(inputVO.getIn_REGTYPE())){
						sb.append(" and INSPRD_ID = :IN_INSPRDID  ");
						qc.setObject("IN_INSPRDID", inputVO.getINSPRD_ID());
					}
					if(StringUtils.equals("1", inputVO.getREG_TYPE()) &&
							StringUtils.isNotBlank(inputVO.getCASE_ID()) && !StringUtils.equals("J999", inputVO.getCASE_ID().substring(0, 4))) {
						//新契約電子要保書：要保書案件編號不為J999或空白，則一險種帶出重要性2(檢附), 3(APP必備)資料
						sb.append(" AND DOC_LEVEL IN ('2', '3') ");
					} else {
						//其他都不需列出3(APP必備)資料
						sb.append(" AND DOC_LEVEL <> '3' ");
					}
					sb.append(" And doc_type = :IN_DOCTYPE Order by doc_seq ");
					qc.setObject("in_REGTYPE", inputVO.getIn_REGTYPE());
					qc.setObject("IN_DOCTYPE", inputVO.getIN_TYPE());
					qc.setQueryString(sb.toString());
					outputVO.setOUTList(dam_obj.exeQuery(qc));
				}
				sendRtnObject(outputVO);
				break;
			case "Read":
				if("1".equals(inputVO.getIN_TYPE())){
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" select DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ,DOC_NAME_OTH ");
					sb.append(" From TBIOT_DOC_CHK where doc_type= :doc_type and INS_KEYNO = :ins_keyno ");
					qc.setObject("doc_type", inputVO.getIN_TYPE());
					qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
					qc.setQueryString(sb.toString());
					outputVO.setINList(dam_obj.exeQuery(qc));
				}
				if("2".equals(inputVO.getIN_TYPE())){
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" select DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ,DOC_NAME_OTH ");
					sb.append(" From TBIOT_DOC_CHK where doc_type= :doc_type and INS_KEYNO = :ins_keyno ");
					qc.setObject("doc_type", inputVO.getIN_TYPE());
					qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
					qc.setQueryString(sb.toString());
					outputVO.setOUTList(dam_obj.exeQuery(qc));
				}
				sendRtnObject(outputVO);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	
	public void submit(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		String errorMsg = "";
		IOT900InputVO inputVO = (IOT900InputVO) body;
		DocChkVO outputVO = new DocChkVO();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			switch (inputVO.getOPR_STATUS()) {
			case "new":
				sendRtnObject(outputVO);
				break;
			case "UPDATE":
				if("1".equals(inputVO.getIN_TYPE())){
					if(inputVO.getInList().size()>0){
						for(Map<String, Object> inItem:inputVO.getInList()){
							if("false".equals(inItem.get("select").toString())){
								dam_obj = this.getDataAccessManager();
								TBIOT_DOC_CHKPK tdc_pk = new TBIOT_DOC_CHKPK();
								tdc_pk.setDOC_TYPE(inputVO.getIN_TYPE());
								tdc_pk.setINS_KEYNO(new BigDecimal(inputVO.getINS_KEYNO().toString()));
								tdc_pk.setDOC_SEQ(new BigDecimal(inItem.get("DOC_SEQ").toString()));
								TBIOT_DOC_CHKVO tdc_vo = new TBIOT_DOC_CHKVO();
								tdc_vo = (TBIOT_DOC_CHKVO) dam_obj.findByPKey(TBIOT_DOC_CHKVO.TABLE_UID, tdc_pk);
								if(tdc_vo != null){
									BigInteger DOC_SEQ_Change = new BigDecimal(inItem.get("DOC_SEQ").toString()).toBigInteger();
									if("99".equals(DOC_SEQ_Change.toString())){
										tdc_vo.setDOC_NAME_OTH(null);
									}
									tdc_vo.setDOC_CHK("N");
									dam_obj.update(tdc_vo);
								}else{
									errorMsg = "ehl_01_common_009";
									throw new APException(errorMsg);
								}
							}else if("true".equals(inItem.get("select").toString())){
								dam_obj = this.getDataAccessManager();
								TBIOT_DOC_CHKPK tdc_pk = new TBIOT_DOC_CHKPK();
								tdc_pk.setDOC_TYPE(inputVO.getIN_TYPE());
								tdc_pk.setINS_KEYNO(new BigDecimal(inputVO.getINS_KEYNO().toString()));
								tdc_pk.setDOC_SEQ(new BigDecimal(inItem.get("DOC_SEQ").toString()));
								TBIOT_DOC_CHKVO tdc_vo = new TBIOT_DOC_CHKVO();
								tdc_vo = (TBIOT_DOC_CHKVO) dam_obj.findByPKey(TBIOT_DOC_CHKVO.TABLE_UID, tdc_pk);
								if(tdc_vo != null){
									BigInteger DOC_SEQ_Change = new BigDecimal(inItem.get("DOC_SEQ").toString()).toBigInteger();
									if("99".equals(DOC_SEQ_Change.toString())){
										if(inItem.get("DOC_NAME_OTH") != null){
											if(!"".equals(inItem.get("DOC_NAME_OTH").toString())){
												tdc_vo.setDOC_NAME_OTH(inItem.get("DOC_NAME_OTH").toString());
											}else{
												errorMsg = "ehl_01_common_022";
												throw new APException(errorMsg);
											}
										}else{
											errorMsg = "ehl_01_common_022";
											throw new APException(errorMsg);
										}
									}
									tdc_vo.setDOC_CHK("Y");
									dam_obj.update(tdc_vo);
								}else{
									errorMsg = "ehl_01_common_009";
									throw new APException(errorMsg);
								}
							}
						}
						//更新資料後丟回前端
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" select DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ,DOC_NAME_OTH ");
						sb.append(" From TBIOT_DOC_CHK where doc_type= :doc_type and INS_KEYNO = :ins_keyno ");
						qc.setObject("doc_type", inputVO.getIN_TYPE());
						qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
						qc.setQueryString(sb.toString());
						outputVO.setINList(dam_obj.exeQuery(qc));
					}
				}
				if("2".equals(inputVO.getIN_TYPE())){
					if(inputVO.getOutList().size()>0){
						for(Map<String, Object> outItem:inputVO.getOutList()){
							if("false".equals(outItem.get("select").toString())){
								dam_obj = this.getDataAccessManager();
								TBIOT_DOC_CHKPK tdc_pk = new TBIOT_DOC_CHKPK();
								tdc_pk.setDOC_TYPE(inputVO.getIN_TYPE());
								tdc_pk.setINS_KEYNO(new BigDecimal(inputVO.getINS_KEYNO().toString()));
								tdc_pk.setDOC_SEQ(new BigDecimal(outItem.get("DOC_SEQ").toString()));
								TBIOT_DOC_CHKVO tdc_vo = new TBIOT_DOC_CHKVO();
								tdc_vo = (TBIOT_DOC_CHKVO) dam_obj.findByPKey(TBIOT_DOC_CHKVO.TABLE_UID, tdc_pk);
								if(tdc_vo != null){
									BigInteger DOC_SEQ_Change = new BigDecimal(outItem.get("DOC_SEQ").toString()).toBigInteger();
									if("99".equals(DOC_SEQ_Change.toString())){
										tdc_vo.setDOC_NAME_OTH(null);
									}
									tdc_vo.setDOC_CHK("N");
									dam_obj.update(tdc_vo);
								}else{
									errorMsg = "ehl_01_common_009";
									throw new APException(errorMsg);
								}
							}else if("true".equals(outItem.get("select").toString())){
								dam_obj = this.getDataAccessManager();
								TBIOT_DOC_CHKPK tdc_pk = new TBIOT_DOC_CHKPK();
								tdc_pk.setDOC_TYPE(inputVO.getIN_TYPE());
								tdc_pk.setINS_KEYNO(new BigDecimal(inputVO.getINS_KEYNO().toString()));
								tdc_pk.setDOC_SEQ(new BigDecimal(outItem.get("DOC_SEQ").toString()));
								TBIOT_DOC_CHKVO tdc_vo = new TBIOT_DOC_CHKVO();
								tdc_vo = (TBIOT_DOC_CHKVO) dam_obj.findByPKey(TBIOT_DOC_CHKVO.TABLE_UID, tdc_pk);
								if(tdc_vo != null){
									BigInteger DOC_SEQ_Change = new BigDecimal(outItem.get("DOC_SEQ").toString()).toBigInteger();
									if("99".equals(DOC_SEQ_Change.toString())){
										if(outItem.get("DOC_NAME_OTH") != null){
											if(!"".equals(outItem.get("DOC_NAME_OTH").toString())){
												tdc_vo.setDOC_NAME_OTH(outItem.get("DOC_NAME_OTH").toString());
											}else{
												errorMsg = "ehl_01_common_022";
												throw new APException(errorMsg);
											}
										}else{
											errorMsg = "ehl_01_common_022";
											throw new APException(errorMsg);
										}
									}
									tdc_vo.setDOC_CHK("Y");
									dam_obj.update(tdc_vo);
								}else{
									errorMsg = "ehl_01_common_009";
									throw new APException(errorMsg);
								}
							}
						}
						//更新資料後丟回前端
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" select DOC_CHK,DOC_NAME,DOC_LEVEL,SIGN_INC,DOC_SEQ,DOC_NAME_OTH ");
						sb.append(" From TBIOT_DOC_CHK where doc_type= :doc_type and INS_KEYNO = :ins_keyno ");
						qc.setObject("doc_type", inputVO.getIN_TYPE());
						qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
						qc.setQueryString(sb.toString());
						outputVO.setOUTList(dam_obj.exeQuery(qc));
					}
				}
				sendRtnObject(outputVO);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			if(errorMsg.length()>0){
				throw new APException(errorMsg);
			}else{
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}

		}
	}
}
