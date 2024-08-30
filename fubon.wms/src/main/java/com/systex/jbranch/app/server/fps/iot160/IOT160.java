package com.systex.jbranch.app.server.fps.iot160;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_BATCH_INFOVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.server.fps.iot160.IOT160InputVO;
import com.systex.jbranch.app.server.fps.iot160.IOT160OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * iot160
 * 
 * @author Joe
 * @date 2016/09/12
 * @spec null
 */
@Component("iot160")
@Scope("request")
public class IOT160 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT160.class);
	ArrayList<String> list = new ArrayList<String>();
	StringBuffer message = new StringBuffer();

	private boolean checkData(String BATCH_INFO_KEYNO, String opBatchNo) throws JBranchException {
		String SUBMIT_WAY = null;
		dam = getDataAccessManager();
		TBIOT_BATCH_INFOVO tbi = new TBIOT_BATCH_INFOVO();
		tbi = (TBIOT_BATCH_INFOVO) dam.findByPKey(TBIOT_BATCH_INFOVO.TABLE_UID, new BigDecimal(BATCH_INFO_KEYNO));
		if(tbi != null){
			if(tbi.getSUBMIT_WAY() != null){
				SUBMIT_WAY = tbi.getSUBMIT_WAY().toString();
				if(StringUtils.isNotBlank(SUBMIT_WAY)){
					throw new APException("該批號"+opBatchNo+"已設定批次，無法重新設定。");
				}else{
					return true;
				}
			}else{
				return true;
			}

		}else{
			return false;
		}
	}
	
	private void updateData(IOT160InputVO inputVO, String opBatchNo, String index) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select BATCH_INFO_KEYNO from TBIOT_BATCH_INFO where OP_BATCH_NO = :op_batch_no1 ");
		qc.setObject("op_batch_no1", opBatchNo);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> batchInfoList = dam.exeQuery(qc);

		if(CollectionUtils.isNotEmpty(batchInfoList) && checkData(batchInfoList.get(0).get("BATCH_INFO_KEYNO").toString(), opBatchNo)){
			TBIOT_BATCH_INFOVO tbi = new TBIOT_BATCH_INFOVO();
			tbi = (TBIOT_BATCH_INFOVO) dam.findByPKey(TBIOT_BATCH_INFOVO.TABLE_UID, new BigDecimal(batchInfoList.get(0).get("BATCH_INFO_KEYNO").toString()));
			if(tbi != null) {
				Date today = new Date();
				tbi.setSUBMIT_WAY(inputVO.getShipMethod_1());
				tbi.setSUBMIT_DATE(inputVO.getDate_1());
				tbi.setBATCH_SETUP_EMPID((String) getCommonVariable(SystemVariableConsts.LOGINID));
				tbi.setBATCH_SETUP_DATE(new Timestamp(today.getTime()));
				tbi.setBATCH_SEQ(inputVO.getBatchSeq_1());
				dam.update(tbi);

				tbi = new TBIOT_BATCH_INFOVO();
				tbi = (TBIOT_BATCH_INFOVO) dam.findByPKey(TBIOT_BATCH_INFOVO.TABLE_UID, new BigDecimal(batchInfoList.get(0).get("BATCH_INFO_KEYNO").toString()));
				
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select INS_KEYNO from TBIOT_MAIN where BATCH_INFO_KEYNO = :batch_info_keyno1 ");
				qc.setObject("batch_info_keyno1", tbi.getBATCH_INFO_KEYNO());
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> ins_keyno1 = dam.exeQuery(qc);
				
				if(CollectionUtils.isNotEmpty(ins_keyno1)) {
					for(Map<String, Object> map:ins_keyno1){
						TBIOT_MAINVO tm = new TBIOT_MAINVO();
						tm = (TBIOT_MAINVO) dam.findByPKey(TBIOT_MAINVO.TABLE_UID, new BigDecimal(map.get("INS_KEYNO").toString()));
						if(tm != null) {
							tm.setSTATUS(new BigDecimal("38"));
							dam.update(tm);
						}
					}
				}
			} else {
				list.add(index);
			}
		} else {
			list.add(index);
		}
	}
	
	public void saveData(Object body, IPrimitiveMap header) throws JBranchException {
		IOT160InputVO inputVO = (IOT160InputVO) body;
		IOT160OutputVO return_VO = new IOT160OutputVO();
		
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_1())) updateData(inputVO, inputVO.getBatchNbr_1(), "1");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_2())) updateData(inputVO, inputVO.getBatchNbr_2(), "2");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_3())) updateData(inputVO, inputVO.getBatchNbr_3(), "3");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_4())) updateData(inputVO, inputVO.getBatchNbr_4(), "4");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_5())) updateData(inputVO, inputVO.getBatchNbr_5(), "5");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_6())) updateData(inputVO, inputVO.getBatchNbr_6(), "6");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_7())) updateData(inputVO, inputVO.getBatchNbr_7(), "7");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_8())) updateData(inputVO, inputVO.getBatchNbr_8(), "8");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_9())) updateData(inputVO, inputVO.getBatchNbr_9(), "9");
		if (StringUtils.isNotBlank(inputVO.getBatchNbr_10())) updateData(inputVO, inputVO.getBatchNbr_10(), "10");
		
		if (list.size() > 0) {
			message.append("第: ");
			for (int i = 0; i < list.size(); i++) {
				message.append(list.get(i) + "、");
			}
			message.deleteCharAt(message.length()-1);
			message.append(" 筆，更新失敗");
		} else {
			message.append("更新成功");
		}
		return_VO.setMessage(message);
		sendRtnObject(return_VO);
	}
	
	private String initDate(Timestamp Date) {
        String data  =  new java.text.SimpleDateFormat("yyyy/MM/dd").format(Date);
		return data;
	}
}