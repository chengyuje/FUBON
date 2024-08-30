package com.systex.jbranch.app.server.fps.cmmgr015;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSI18NVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 訊息維護
 *
 * @author SamTu
 * @date 20180719
 *
 */
@Component("cmmgr015")
@Scope("request")
public class CMMGR015 extends FubonWmsBizLogic {
	private DataAccessManager dam;
	private QueryConditionIF condition;

	/** 取得 {@link DataAccessManager} 物件 */
	private void getDam() {
		dam = this.getDataAccessManager();
	}

	/** 取得 {@link QueryConditionIF} 物件 */
	private void getCondition() throws DAOException, JBranchException {
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	}

	// 查詢
	public void inquire(Object body, IPrimitiveMap header) throws DAOException, JBranchException {

		CMMGR015InputVO inputVO = (CMMGR015InputVO) body;

		// System.out.println(cmmgr015InputVO.getCode()+","+cmmgr015InputVO.getText());
		getDam();
		getCondition();

		condition.setQueryString(new StringBuffer().append("select CODE, TEXT ").append("from TBSYSI18N ").toString());
		dam.exeQuery(condition);
		CMMGR015OutputVO returnVO = new CMMGR015OutputVO();
		returnVO.setResultList(dam.exeQuery(condition));
		sendRtnObject(returnVO);
	}

	// 新增
	public void insert(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		CMMGR015InputVO inputVO = (CMMGR015InputVO) body;

		// System.out.println(cmmgr015InputVO.getCode()+","+cmmgr015InputVO.getText());
		getDam();
		getCondition();

		TBSYSI18NVO vo = new TBSYSI18NVO();
		TBSYSI18NPK pk = new TBSYSI18NPK();
		pk.setLOCALE("zh-tw");
		pk.setCODE(inputVO.getCode());
		TBSYSI18NVO queryVO = new TBSYSI18NVO();// 用于判斷是否已經存在相同主鍵
		// 先查找是否已經存在同樣主鍵的數據
		queryVO = (TBSYSI18NVO) dam.findByPKey(TBSYSI18NVO.TABLE_UID, pk);
		if (queryVO != null) {
			throw new APException("ehl_01_common_005");
		} else {
			vo.setcomp_id(pk);
			vo.setTEXT(inputVO.getText());
			vo.setTYPE("T");
			dam.create(vo);
		}
		this.sendRtnObject(null);
	}

	// 修改
	public void update(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		CMMGR015InputVO inputVO = (CMMGR015InputVO) body;

		// System.out.println(cmmgr015InputVO.getCode()+","+cmmgr015InputVO.getText());
		getDam();
		getCondition();

		TBSYSI18NVO vo = new TBSYSI18NVO();
		TBSYSI18NPK pk = new TBSYSI18NPK();
		pk.setLOCALE("zh-tw");
		pk.setCODE(inputVO.getCode());

		vo = (TBSYSI18NVO) dam.findByPKey(
				TBSYSI18NVO.TABLE_UID, pk);
		if (vo != null) {
			vo.setTEXT(inputVO.getText());
			vo.setTYPE("T");
			dam.update(vo);
		}
		this.sendRtnObject(null);


	}

	// 刪除
	public void delete(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		CMMGR015InputVO inputVO = (CMMGR015InputVO) body;

		// System.out.println(cmmgr015InputVO.getCode()+","+cmmgr015InputVO.getText());
		getDam();
		getCondition();

		TBSYSI18NVO vo = new TBSYSI18NVO();
		TBSYSI18NPK pk = new TBSYSI18NPK();
		pk.setLOCALE("zh-tw");
		pk.setCODE(inputVO.getCode());

		vo = (TBSYSI18NVO) dam.findByPKey(TBSYSI18NVO.TABLE_UID, pk);
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

	  public void inquireSQL(Object body, IPrimitiveMap header)
			    throws DAOException, JBranchException
			  {
			    CMMGR015InputVO inputVO = ((CMMGR015InputVO)body);

			    getDam();
			    getCondition();

			  	String sqlStr = inputVO.getSqlStr();
			  	validFetchSize(sqlStr);
			  	this.logger.info(sqlStr);
			    this.condition.setQueryString(sqlStr);

			    Map<String, Object> resultMap = null;
			    StringBuffer sb = new StringBuffer("");
			    Object[] keys = null;
			    List<Map<String, Object>> resultList = this.dam.exeQuery(this.condition);
			    if ((resultList == null) || (resultList.size() == 0)) {
			      return;
			    }
			    resultMap = (Map)resultList.get(0);
			    keys = resultMap.keySet().toArray();
			    Arrays.sort(keys);

			    String colName = "";
			    for (int col = 0; col < keys.length; col++)
			    {
			      colName = keys[col].toString();
			      sb.append(colName).append("\t");
			    }
			    sb.append("\r\n");
			    for (int row = 0; row < resultList.size(); row++)
			    {
			      for (int col = 0; col < keys.length; col++)
			      {
			        colName = keys[col].toString();
			        sb.append(isNotNull(((Map)resultList.get(row)).get(colName))).append("\t");
			      }
			      sb.append("\r\n");
			    }

				CMMGR015OutputVO returnVO = new CMMGR015OutputVO();
			    returnVO.setSqlResult(sb.toString());
			    sendRtnObject(returnVO);
			  }

	private void validFetchSize(String sqlStr) throws JBranchException {
		// count 筆數不需要 valid
		if (StringUtils.containsIgnoreCase(sqlStr, "count("))
			return;

		BigDecimal fetchSizeLimit = getFetchSizeLimit();
		BigDecimal querySize = getQuerySize(sqlStr);
		if (fetchSizeLimit.compareTo(querySize) < 0) {
			throw new APException(String.format("查詢筆數共 %d 筆，已超過限制筆數 %d！請調整查詢語法，謝謝！",querySize.intValue(), fetchSizeLimit.intValue()));
		}
	}

	private BigDecimal getFetchSizeLimit() throws JBranchException {
		condition.setQueryString("select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE = 'CMMGR015.QRY' and PARAM_NAME = 'FETCH_SIZE' ");
		List<Map<String, String>> data= dam.exeQueryWithoutSort(condition);
		if (data.size() > 0) {
			String fetchsize = data.get(0).get("PARAM_CODE");
			if (fetchsize.matches("\\d+"))
				return new BigDecimal(fetchsize);
		}
		throw new APException("請確認 CMMGR015.QRY-FETCH_SIZE 參數是否設定正確！");
	}

	private BigDecimal getQuerySize(String sqlStr) throws JBranchException {
		String countSql = String.format("select count(1) CNT from (%s)", sqlStr);
		condition.setQueryString(countSql);
		List<Map<String, BigDecimal>> data = dam.exeQueryWithoutSort(condition);
		return data.get(0).get("CNT");
	}

	public void updateSQL(Object body, IPrimitiveMap header)
	    throws DAOException, JBranchException
	  {
	    CMMGR015InputVO inputVO = ((CMMGR015InputVO)body);

	    getDam();
	    getCondition();
	    this.logger.info(inputVO.getSqlStr());
	    String sqlStr = inputVO.getSqlStr();

		CMMGR015OutputVO outputVO = new CMMGR015OutputVO();
	    if (!sqlStr.startsWith("##"))
	    {
	      outputVO.setSqlResult("無法執行更新");
	    }
	    else
	    {
	      sqlStr = sqlStr.replaceFirst("##", "");
	      this.condition.setQueryString(sqlStr);
	      Integer cnt = Integer.valueOf(this.dam.exeUpdate(this.condition));
	      outputVO.setSqlResult("執行更新結果:" + cnt + "筆");
	    }
	    sendRtnObject(outputVO);
	  }

	  private String isNotNull(Object value)
			  {
			    try
			    {
			      return value.toString().trim();
			    }
			    catch (Exception localException) {}
			    return "";
			  }
}
