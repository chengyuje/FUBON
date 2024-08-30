package com.systex.jbranch.app.server.fps.crm811;

import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.WMS032675Service;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author walalala
 * @date 2016/12/06
 * 
 */
@Component("crm811")
@Scope("request")
public class CRM811 extends FubonWmsBizLogic {
	@Autowired
	private WMS032675Service wms032675Service;

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM811.class);
	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	private String thisClaz = this.getClass().getSimpleName() + ".";
	private CBSService cbsService = new CBSService();

	public void getBranchName(Object body, IPrimitiveMap header) throws Exception {
		CRM811OutputVO return_VO = new CRM811OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT BRANCH_NBR, BRANCH_NAME FROM VWORG_DEFN_INFO ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return_VO.setResultList(list);
		sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM811OutputVO return_VO = new CRM811OutputVO();
		CRM811InputVO inputVO = (CRM811InputVO) body;

		List<WMS032675OutputVO> vos = wms032675Service.searchCurrentDepositTW(inputVO.getCust_id());

		List<WMS032675OutputDetailsVO> results = new ArrayList<WMS032675OutputDetailsVO>();

		for (WMS032675OutputVO wms032675OutputVO : vos) {
			List<WMS032675OutputDetailsVO> details = wms032675OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<WMS032675OutputDetailsVO>() : details;
			for (WMS032675OutputDetailsVO datas : details) {
				if (!StringUtils.isBlank(datas.getACCT_NBR())) {

					datas.setBRANCH_NBR(datas.getBRANCH_NBR().trim());
					
					//開戶日期
					if (!StringUtils.isBlank(datas.getACCT_OPEN_DATE())) {
						datas.setACCT_OPEN_DATE(cbsService.changeDateView(datas.getACCT_OPEN_DATE(),"3"));
					}			
					//處理異動日
					if (!StringUtils.isBlank(datas.getALTERATION())) {
						datas.setALTERATION(datas.getALTERATION().replace("/", ""));
						if(datas.getALTERATION().equals("99999999")){
							datas.setALTERATION("久未異動");
						} else{
							datas.setALTERATION(cbsService.changeDateView(datas.getALTERATION(),"2"));
						}
						
					}
					
					
					//處理金額
					if (!StringUtils.isBlank(datas.getCURRENT_AMT_BAL())) {
						datas.setCURRENT_AMT_BAL(new BigDecimal (cbsService.amountFormat(datas.getCURRENT_AMT_BAL())).toString());
					}
	            	if (!StringUtils.isBlank(datas.getAVAILABLE_AMT_BAL())) {
						datas.setAVAILABLE_AMT_BAL(new BigDecimal (cbsService.amountFormat(datas.getAVAILABLE_AMT_BAL())).toString());
					}
					
					//儲存
					results.add(datas);
				}
			}
		}

		// 查無資料則拋出錯誤訊息
		// if (CollectionUtils.isEmpty(results)) {
		// throw new JBranchException("ehl_01_common_009");
		// }

		return_VO.setResultList(results);

		this.sendRtnObject(return_VO);
	}

	public void inquire2(Object body, IPrimitiveMap header) throws Exception {
		CRM811OutputVO return_VO = new CRM811OutputVO();
		CRM811InputVO inputVO = (CRM811InputVO) body;

		List<WMS032675OutputVO> vos = wms032675Service.searchCurrentDepositFC(inputVO.getCust_id());

		XMLInfo xmlinfo = new XMLInfo();
		HashMap<String, BigDecimal> ex_map = xmlinfo.getExchangeRate();

		List<WMS032675OutputDetailsVO> results = new ArrayList<WMS032675OutputDetailsVO>();

		for (WMS032675OutputVO wms032675OutputVO : vos) {
			List<WMS032675OutputDetailsVO> details = wms032675OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<WMS032675OutputDetailsVO>() : details;
			for (WMS032675OutputDetailsVO datas : details) {
				if (!StringUtils.isBlank(datas.getACCT_NBR())) {

					if (!StringUtils.isBlank(datas.getACCT_OPEN_DATE())) {
						datas.setACCT_OPEN_DATE(cbsService.changeDateView(datas.getACCT_OPEN_DATE(),"3"));
					}
					if (!StringUtils.isBlank(datas.getALTERATION())) {
						datas.setALTERATION(datas.getALTERATION().replace("/", ""));
						if(datas.getALTERATION().equals("99999999")){
							datas.setALTERATION("久未異動");
						} else{
							datas.setALTERATION(cbsService.changeDateView(datas.getALTERATION(),"2"));
						}
					}

					datas.setBRANCH_NBR(datas.getBRANCH_NBR().trim());

				
					//處理金額
					if (!StringUtils.isBlank(datas.getCURRENT_AMT_BAL_O())) {
						datas.setCURRENT_AMT_BAL_O(new BigDecimal (cbsService.amountFormat(datas.getCURRENT_AMT_BAL_O())).toString());
					}
	            	if (!StringUtils.isBlank(datas.getAVAILABLE_AMT_BAL())) {
						datas.setAVAILABLE_AMT_BAL(new BigDecimal (cbsService.amountFormat(datas.getAVAILABLE_AMT_BAL())).toString());
					}
					
					// 折合台幣
					if (!datas.getCURRENCY_CD().equals("XXX")) {

						BigDecimal curr = ex_map.get(datas.getCURRENCY_CD());						
						BigDecimal CURRENT_AMT_BAL = new BigDecimal(datas.getCURRENT_AMT_BAL_O());
						datas.setCURRENT_AMT_BAL(CURRENT_AMT_BAL.multiply(curr).setScale(0, BigDecimal.ROUND_HALF_UP).toString());

					}
					
					

					results.add(datas);
				}
			}
		}

		// 查無資料則拋出錯誤訊息
		// if (CollectionUtils.isEmpty(results)) {
		// throw new JBranchException("ehl_01_common_009");
		// }

		return_VO.setResultList(results);

		this.sendRtnObject(return_VO);
	}

}