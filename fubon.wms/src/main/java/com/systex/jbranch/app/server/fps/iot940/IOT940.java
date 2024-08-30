package com.systex.jbranch.app.server.fps.iot940;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLISTPK;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAPPVIDEO_CHKLISTVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.fubon.commons.http.client.HttpClientJsonUtils;
import com.systex.jbranch.fubon.commons.http.client.callback.DefHeaderCallBack;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * IOT940
 *
 */
@Component("iot940")
@Scope("request")
public class IOT940 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam;
	
	/***
	 * 取得保險進件視訊投保品質檢核內容檔
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void query(Object body, IPrimitiveMap<Object> header) throws Exception {
		IOT940InputVO inputVO = (IOT940InputVO) body;
		IOT940OutputVO outputVO = new IOT940OutputVO();		

		// 視訊投保品質檢核內容
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append(" select A.PREMATCH_SEQ, A.CHK_STEP, A.CHK_CODE, A.CHK_YN, A.NP_REASON, ");
		sb.append(" (A.CHK_EMP_ID || ' ' || B.EMP_NAME) AS CHK_EMP, TO_CHAR(A.LASTUPDATE, 'YYYY/MM/DD HH24:MI') AS LASTUPDATE ");
		sb.append(" from TBIOT_MAPPVIDEO_CHKLIST A ");
		sb.append(" left join TBORG_MEMBER B on B.EMP_ID = A.CHK_EMP_ID ");
		sb.append(" where A.PREMATCH_SEQ = :prematchSeq ");
		qc.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		qc.setQueryString(sb.toString());
		
		outputVO.setCHK_YN_LIST(dam.exeQuery(qc));
		
		sendRtnObject(outputVO);
	}
	
	public void save(Object body, IPrimitiveMap<Object> header) throws Exception {
		IOT940InputVO inputVO = (IOT940InputVO) body;
		IOT940OutputVO outputVO = new IOT940OutputVO();		
		dam = this.getDataAccessManager();

		if(StringUtils.isBlank(inputVO.getPREMATCH_SEQ()) || StringUtils.isBlank(inputVO.getCHKLIST_TYPE())) {
			throw new JBranchException("適合度檢核編號為空或檢核步驟為空");
		}
		
		//逐筆寫入
		for(Map.Entry<String, Object> itmes : inputVO.getCHK_YN_LIST().entrySet()) {
			Map<String, Object> map = (Map<String, Object>) itmes.getValue();
			
			if(StringUtils.equals(inputVO.getCHKLIST_TYPE(), map.get("CHK_STEP").toString())) {
				TBIOT_MAPPVIDEO_CHKLISTPK vpk = new TBIOT_MAPPVIDEO_CHKLISTPK();
				vpk.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
				vpk.setCHK_STEP(map.get("CHK_STEP").toString());
				vpk.setCHK_CODE(map.get("CHK_CODE").toString());
				
				TBIOT_MAPPVIDEO_CHKLISTVO vo = (TBIOT_MAPPVIDEO_CHKLISTVO)dam.findByPKey(TBIOT_MAPPVIDEO_CHKLISTVO.TABLE_UID, vpk);
				if(vo == null) {
					vo = new TBIOT_MAPPVIDEO_CHKLISTVO();
					vo.setcomp_id(vpk);

					if(ObjectUtils.toString(map.get("CHK_CODE")).matches("97|98|99")) {
						//檢核人員&覆核日期不會用到這個欄位，先直接放"Y"
						vo.setCHK_YN("Y");
						//不通過原因
						if(StringUtils.equals("97", ObjectUtils.toString(map.get("CHK_CODE")))) {
							vo.setNP_REASON(ObjectUtils.toString(map.get("DATA")));
						}
					} else {
						vo.setCHK_YN(ObjectUtils.toString(map.get("DATA")));
					}	
					
					vo.setCHK_EMP_ID((String) getCommonVariable(SystemVariableConsts.LOGINID));
					
					dam.create(vo);
				} else {
					if(ObjectUtils.toString(map.get("CHK_CODE")).matches("97|98|99")) {
						//檢核人員&覆核日期不會用到這個欄位，先直接放"Y"
						vo.setCHK_YN("Y");
						//不通過原因
						if(StringUtils.equals("97", ObjectUtils.toString(map.get("CHK_CODE")))) {
							vo.setNP_REASON(ObjectUtils.toString(map.get("DATA")));
						}
					} else {
						vo.setCHK_YN(ObjectUtils.toString(map.get("DATA")));
					}					
					vo.setCHK_EMP_ID((String) getCommonVariable(SystemVariableConsts.LOGINID));
					
					dam.update(vo);
				}
			}
		}
		
		sendRtnObject(outputVO);
	}
	

}
