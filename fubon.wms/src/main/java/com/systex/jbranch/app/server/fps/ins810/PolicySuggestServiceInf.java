package com.systex.jbranch.app.server.fps.ins810;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

public interface PolicySuggestServiceInf {

	/**
	 * 商品建議調用入口  For 前端
	 * @param body
	 * @param header
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public void getSuggestPrd(Object body, IPrimitiveMap<Object> header) throws DAOException, JBranchException ;
	
	/**
	 * 取得費率 premRate & 匯率 exchangeRate
	 * @param body
	 * @param header
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public void getPremAndExchangeRate(Object body, IPrimitiveMap<Object> header) throws DAOException, JBranchException ;
	
	/**
	 * 商品建議  (其他 service 也能直接調用可以不透過 getSuggestPrd FOR 前端)
	 * 必要參數 幣別 參數編號
	 * @param inputVO 當前 inputvo
	 * @param dam 執行物件
	 * @return 建議商品資料清單 (含繳費年期 & 上下限相關資訊整理過的)
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getSuggestDataParser(PolicySuggestInputVO inputVO, DataAccessManager dam) throws DAOException, JBranchException;
	
	/**
	 * 取得 PlanNo (其他 service 也能直接調用可以不透過 getSuggestPrd FOR 前端)
	 * @param inputVO 當前 inputvo
	 * @param dam 執行物件
	 * @return 回傳 根據 paraType 找到的 planNo (應該只會有一筆)
	 * @throws DAOException
	 * @throws JBranchException
	 * 
	 * ParaType 說明
	 * '1' 壽險保障
	 * '2' 意外險保障
	 * '3' 醫療保障
	 * '4' 重大疾病(癌症、長期看護)
	 * '5' 退休規劃
	 * '6' 子女教育
	 * '7' 特定目的
	 * '8' 主推還本
	 * '9' 推薦保障
	 */
	public List<Map<String, Object>> getPlanNo(PolicySuggestInputVO inputVO, DataAccessManager dam) throws DAOException, JBranchException;
	
	/**
	 * 細方法，單純撈取推薦商品
	 * @param sql
	 * @param planNo
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getSuggestListDatas(String sql, PolicySuggestInputVO inputVO, DataAccessManager dam) throws DAOException, JBranchException;
	
	/**
	 * 規則
	 * 組合 一個商品 不同繳費年期對應出不同 KEY_NO 保額線下線與級距
	 * @param currentMap 當前的商品 map 資料
	 * @param insprdAnnualList 最後給前端的資料
	 * 
	 * insprdAnnualList 表示 對應的繳費年期所對的 相關資訊 (上限、下限、級距、保障比)
	 * 
	 */
	public void reCombianMap(Map<String, Object> currentMap, List<Map<String, Object>> insprdAnnualList);
	
	/**
	 * 取得費率
	 * @param inputVO
	 * @param dam
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	public BigDecimal getPremRate(PolicySuggestInputVO inputVO, DataAccessManager dam) throws DAOException, JBranchException ;
	
	/**
	 * 商品建議 SQL 可改實作時自己寫要撈的來源
	 * @param currCD 幣別
	 * @return SQL
	 */
	public String getSuggestPrdSql(String currCD, String insage, String estate);
	
	/**
	 * 參數編號 SQL 可改實作時自己寫要撈的來源
	 * @return SQL
	 */
	public String getPlanNoSql();
	
	/**
	 * 取得費率 SQL 可改實作時自己寫要撈的來源
	 * @return
	 */
	public String getPremRateSql();
	
}
