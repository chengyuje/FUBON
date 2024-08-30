package com.systex.jbranch.app.server.fps.pms108;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_PARVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_POT_CF_PAR_INSVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :潛力金流客戶參數設定<br>
 * Comments Name : PMS108.java<br>
 * Author : Kevin Hsu<br>
 * Date :2016年06月16日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */
@Component("pms108")
@Scope("request")
public class PMS108 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS108.class);

	/**
	 * 以下是查詢保險
	 * 
	 * @author 俊緯
	 * @date 2017/03/09
	 * 
	 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		// 輸入VO
		PMS108InputVO inputVO = (PMS108InputVO) body;
		// 輸入VO
		PMS108OutputVO outputVO = new PMS108OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		// ==查詢==
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(" SELECT distinct A.*,B.INSPRD_NAME AS INS_NAME from ");
			sb.append(" TBPMS_POT_CF_PAR_INS A , TBPRD_INS_MAIN B ");
			sb.append(" where 1=1 ");
			sb.append(" and A.INS_NBR = B.INSPRD_ID(+) ");
			queryCondition.setQueryString(sb.toString());
			// 分頁查詢結果
			ResultIF list = dam.executePaging(queryCondition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList2(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 以下是查詢險種名稱
	 * 
	 * @author 俊緯
	 * @date 2017/03/09
	 * 
	 */
	public void queryINS(Object body, IPrimitiveMap header) throws JBranchException {
		// 輸入VO
		PMS108InputVO inputVO = (PMS108InputVO) body;
		// 輸入VO
		PMS108OutputVO outputVO = new PMS108OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(" SELECT distinct INSPRD_NAME AS INS_NAME FROM ");
			sb.append(" TBPRD_INS_MAIN ");
			sb.append(" where 1=1 ");
			sb.append(" and INSPRD_ID =:ins_nbr ");

			queryCondition.setObject("ins_nbr", inputVO.getINS_NBR());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			outputVO.setResultList(list1);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 以下是新增method
	 * 
	 * @author Kevin
	 * @date 2016/06/13
	 * 
	 */
	public void retMod(Object body, IPrimitiveMap header) throws JBranchException {
		PMS108InputVO inputVO = (PMS108InputVO) body;
		dam = this.getDataAccessManager();
		try {
			TBPMS_POT_CF_PAR_INSVO VO = (TBPMS_POT_CF_PAR_INSVO) dam.findByPKey(TBPMS_POT_CF_PAR_INSVO.TABLE_UID,inputVO.getINS_NBR());       
			// 確認找到那筆資料
			if (VO == null) {
				TBPMS_POT_CF_PAR_INSVO VOS = new TBPMS_POT_CF_PAR_INSVO();
				VOS.setINS_NBR(inputVO.getINS_NBR());
				BigDecimal TER_FEE_YEAR = new BigDecimal(ObjectUtils.toString(inputVO.getTER_FEE_YEAR()));
				VOS.setTER_FEE_YEAR(TER_FEE_YEAR);
				dam.create(VOS);
			} else {
				throw new APException("ehl_01_common_009");
			}
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 以下是查詢基金/ETF/SISN/海外債
	 * 
	 * @author Kevin
	 * @date 2016/06/13
	 * 
	 */
	public void queryMod(Object body, IPrimitiveMap header) throws JBranchException {
		PMS108InputVO inputVO = (PMS108InputVO) body;
		PMS108OutputVO outputVO = new PMS108OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			// ==查詢==
			sb.append("SELECT * from" + " TBPMS_POT_CF_PAR " + "where 1=1  ");
			queryCondition.setQueryString(sb.toString());
			// 查詢結果
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
			outputVO.setResultList(list1);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 以下是刪除method
	 * 
	 * @author Kevin
	 * @date 2016/06/13
	 * 
	 */
	public void delRes(Object body, IPrimitiveMap header) throws JBranchException {
		PMS108InputVO inputVO = (PMS108InputVO) body;
		dam = this.getDataAccessManager();
		TBPMS_POT_CF_PAR_INSVO VO = (TBPMS_POT_CF_PAR_INSVO) dam.findByPKey(TBPMS_POT_CF_PAR_INSVO.TABLE_UID, inputVO.getINS_NBR());
		if (VO != null) {
			dam.delete(VO);
		} else {
			throw new APException("ehl_01_common_009");
		}
		this.sendRtnObject(null);

	}

	/**
	 * 以下是還原method
	 * 
	 * @author Kevin
	 * @date 2016/06/13
	 * 
	 */
	public void rMod(Object body, IPrimitiveMap header) throws JBranchException {
		PMS108InputVO inputVO = (PMS108InputVO) body;
		dam = this.getDataAccessManager();
		List<Map<String, Object>> list = inputVO.getList();
		try {
			for (Map<String, Object> map : list) { // 資料修改後
				TBPMS_POT_CF_PARVO VO = (TBPMS_POT_CF_PARVO) dam.findByPKey(TBPMS_POT_CF_PARVO.TABLE_UID, map.get("TYPE").toString());
				if (VO == null) {
					TBPMS_POT_CF_PARVO VOS = new TBPMS_POT_CF_PARVO();
					VOS.setTYPE(map.get("TYPE").toString());
					BigDecimal ROI = new BigDecimal(map.get("ROI").toString());
					VOS.setROI(ROI);
					BigDecimal AMT_TWD = new BigDecimal(map.get("AMT_TWD").toString());
					VOS.setAMT_TWD(AMT_TWD);
					dam.create(VOS);
				} else {
					VO.setTYPE(map.get("TYPE").toString());
					BigDecimal ROI = new BigDecimal(map.get("ROI").toString());
					VO.setROI(ROI);
					System.out.println("ggggggg"+map.get("AMT_TWD"));
					BigDecimal AMT_TWD = new BigDecimal(String.valueOf(map.get("AMT_TWD")));
					VO.setAMT_TWD(AMT_TWD);
					dam.update(VO);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		this.sendRtnObject(null);
	}
}
