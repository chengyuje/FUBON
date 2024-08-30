package com.systex.jbranch.app.server.fps.kyc999;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm998.CRM998InputVO;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * @author Eli
 *
 *
 */
@Component("kyc999")
@Scope("request")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class KYC999 extends FubonWmsBizLogic {
	@Autowired
	private KYC999Stored stored;
	@Autowired
	private KYC999InputVO inputVO;
	@Autowired
	private KYC999OutputVO returnVO;
	/*CSV附檔名*/
	private final String CSV = ".csv";

	/** 設定inputVO */
	private void setKYC999InputVO(Object body) {
		inputVO = (KYC999InputVO) body;
	}
	
	/**
	 * 查詢KYC資料
	 * @param body
	 * @param header
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	public void inquire(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException {
		setKYC999InputVO(body);
		returnVO.setResultList(getResult());
		this.sendRtnObject(returnVO);
	}

	/**取得查詢結果*/
	private List getResult() throws DAOException, JBranchException {
		return exeQueryForMap(stored.getSQL(), getParamMap());
	}

	/**取得查詢參數*/
	private Map getParamMap() {
		HashMap param = new HashMap();
		param.put("dateStart", inputVO.getDateStart());
		param.put("dateEnd", inputVO.getDateEnd());
		return param;
	}	
	
	/**
	 * 匯出KYC資料
	 * @param body
	 * @param header
	 * @throws Exception 
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	public void export(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException, Exception {
		setKYC999InputVO(body);
		notifyClientToDownloadFile(
				AccessContext.generateCSV(
						stored.getCsvFileName(), 
						stored.getCsvHeader(), 
						stored.getCsvColumn(), 
						getResult()), stored.getCsvFileName() + CSV );
	}
}
