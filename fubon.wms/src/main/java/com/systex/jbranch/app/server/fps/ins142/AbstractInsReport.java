package com.systex.jbranch.app.server.fps.ins142;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.systex.jbranch.app.server.fps.cmsub302.CMSUB302Util;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbBusinessInf;
import com.systex.jbranch.app.server.fps.insjlb.dao.InsjlbDaoInf;
import com.systex.jbranch.comutil.io.JoinDifferentSysBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

abstract public class AbstractInsReport extends JoinDifferentSysBizLogic{
	protected final String RPT_CMRPT231 = "CMRPT231";// 逐年壽險保障一覽表
	protected final String RPT_CMRPT232 = "CMRPT232";// 逐年意外保障一覽表
	protected final String RPT_CMRPT233 = "CMRPT233";// 逐年重大疾病保障一覽表
	protected final String RPT_CMRPT234 = "CMRPT234";// 逐年癌症保障一覽表
	protected final String RPT_CMRPT229 = "CMRPT229";// 逐年還本金額一覽表
	protected final String RPT_CMRPT230 = "CMRPT230";// 逐年保費一覽表
	protected final String RPT_CMRPT205 = "CMRPT205";// 個人化彙總表
	protected final String RPT_INS142 = "INS142";// 檢視結構圖
	protected final String RPT_CMRPT211 = "CMRPT211";// 給付內容彙總表
	protected final String RPT_CMRPT212 = "CMRPT212";// 給付內容明細表
	protected final String RPT_INS143 = "INS143";// 個人生涯彙總表
	protected final String RPT_INS144 = "INS144";// 生涯透視表
	protected final String RPT_CMRPT216 = "CMRPT216";// 家庭資產負債表
	protected final String RPT_R1 = "R1";
	protected final String STR_TOTAL = "合計";
	protected final String DATE_YEAR = "yyyy";
	protected final String DATE_YMD = "yyyy-MM-dd";
	protected final String INDEX_0301 = "10301,10301A,10301B,10301C,20301,20301A,20301B,20301C,30301,30301A,30301B,30301C,99901,99902";

	
	@Autowired @Qualifier("insjlbDao")
	private InsjlbDaoInf insjlbDao;
	
	@Autowired @Qualifier("insjlb")
	private InsjlbBusinessInf insjlb;
	
	@Autowired @Qualifier("ins810")
	private INS810 ins810;

	public String getStringToZero(Object data_obj) {
		if (StringUtils.isBlank(ObjectUtils.toString(data_obj))) {
			return "0";
		} else {
			return data_obj.toString();
		}
	}
	
	/**
	 * Carey 20120824 根據客戶跟投保時間排序
	 * 
	 * @param list
	 */
	private void sort209(List<Map<String, Object>> list) {
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String o1Val = ObjectUtils.toString(o1.get("value"));
				String o2Val = ObjectUtils.toString(o2.get("value"));
				return o1Val.compareTo(o2Val) > 0 ? 1 : -1;
			}
		});
	}
	
	/**
	 * 根據客戶跟投保時間排序
	 * 
	 * @param list
	 */
	private <T extends List> T sortByIns145Ex(List<Map<String, Object>> list , final String...keys) {
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int result = 0;
				
				for(String key : keys){
					result = ObjectUtils.toString(o1.get(key)).compareTo(ObjectUtils.toString(o2.get(key)));
					
					if(result != 0){
						return result;
					} 
				}
				
				return result;
			}
		});
		
		return (T)list;
	}
	
	public Map<String, String> getBthXML(String paramType_str) throws DAOException, JBranchException {
		StringBuilder xml_sb = new StringBuilder("");
		xml_sb.append(" SELECT PARAM_NAME, PARAM_CODE FROM TBSYSPARAMETER");
		xml_sb.append(" WHERE PARAM_TYPE=? AND PARAM_STATUS <> '3'");
		
		DataAccessManager dam_obj = this.getDataAccessManager();
		QueryConditionIF xml_qc = dam_obj.getQueryCondition();
		xml_qc.setString(1, paramType_str);
		xml_qc.setQueryString(xml_sb.toString());

		List<Map<String, Object>> xml_lst = dam_obj.exeQuery(xml_qc);
		Map<String, String> return_map = new HashMap<String, String>();
		
		if (!xml_lst.isEmpty()) {
			for (Map<String, Object> xml_map : xml_lst) {
				return_map.put(CMSUB302Util.getString(xml_map.get("PARAM_NAME")), CMSUB302Util.getString(xml_map.get("PARAM_CODE")));
			}
		}
		return return_map;
	}
	
	/**保險公司名稱與代碼
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String , Object> doGetInsCompany() throws DAOException, JBranchException{
		DataAccessManager dam = this.getDataAccessManager();
		String queryStr = "select COM_ID AS INSCO, COM_NAME  AS INSCO_NAME from TBPRD_INSDATA_COMPANY A ";
		Map<String , Object> inscoMap = new HashMap<String , Object>();
		
		//取保險公司資訊
		List<Map<String, Object>> inscoList = dam.exeQuery(dam.getQueryCondition().setQueryString(queryStr));
		
		if (CollectionUtils.isNotEmpty(inscoList)) {
			for (Map<String, Object> tempInsCompanyMap : inscoList) {
				inscoMap.put(ObjectUtils.toString(tempInsCompanyMap.get("INSCO")) , tempInsCompanyMap.get("INSCO_NAME"));
			}
		}
		
		return inscoMap;
	}
	
	/**給付編號與中文，key為編號，value為給附項目中文*/
	public Map<String, Object> doGetSortNoName() throws DAOException, JBranchException{
		List<Map<String, Object>> sortNoNameList = null;
		Map sortNoNameMap = null;
		
		if (CollectionUtils.isNotEmpty(sortNoNameList = insjlbDao.querySortMap())) {
			sortNoNameMap = new HashMap<String, Object>();
			
			for (Map<String, Object> tempsortNoNameMap : sortNoNameList) {
				String sortNo = ObjectUtils.toString(tempsortNoNameMap.get("SORTNO"));
				String dsc = ObjectUtils.toString(tempsortNoNameMap.get("DSC"));
				sortNoNameMap.put(sortNo , dsc);
			}
		}
		
		return sortNoNameMap;
	}
	
	public String getDate() {
		return (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
	}
	
	public String getDateStr(Object obj, String pattern, Boolean yyyBol) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (obj != null) {
			String result_str = sdf.format(obj);
			if (yyyBol) {
				int yyy_int = Integer.valueOf(result_str.substring(0, 4)) - 1911;
				return result_str.replaceFirst(result_str.substring(0, 4), "" + yyy_int);
			} else {
				return result_str;
			}
		} else {
			return "";
		}
	}
	
	/**
	 * 把 String 類型的數據加上千分撇節
	 * 
	 * @param fromChar
	 * @return
	 */
	public String changeStr(String fromChar) {
		Boolean isFirst_bol = false;
		String newString_str = "";
		if ("0".equals(fromChar) || fromChar == null) {
			return "0";
		} else {
			String[] arrAtr = fromChar.split("\\.");
			fromChar = arrAtr[0];
			for (int index_int = fromChar.length(); index_int >= 3; index_int -= 3) {
				if (isFirst_bol) {
					newString_str = fromChar.substring(index_int - 3, index_int) + "," + newString_str;
				} else {
					newString_str = fromChar.substring(index_int - 3, index_int);
					isFirst_bol = true;
				}
			}
			if (fromChar.length() % 3 != 0) {
				if (fromChar.length() < 3) {
					newString_str = fromChar;
				} else {
					newString_str = fromChar.substring(0, fromChar.length() % 3) + "," + newString_str;
				}
			}
			if (arrAtr.length == 2) {
				newString_str = newString_str + "." + arrAtr[1];
			}
			return newString_str;
		}
	}

	public boolean checkSortNo(String value, String sortNo) {
		Map<String, Object> split_map = new HashMap<String, Object>();
		if (value.indexOf("@") != -1) {
			for (String b_str : value.split("\\@")) {
				if (b_str.indexOf("+") != -1) {
					for (String add_str : b_str.split("\\+")) {
						split_map.put(add_str, add_str);
					}
				} else {
					split_map.put(b_str, b_str);
				}
			}
		} else if (value.indexOf("*") != -1) {
			String[] mu_str = null;
			for (String b_str : value.split("\\+")) {
				mu_str = b_str.split("\\*");
				if (mu_str.length > 0) {
					split_map.put(mu_str[0], mu_str[0]);
				}
			}
		} else {
			for (String add_str : value.split("\\+")) {
				split_map.put(add_str, add_str);
			}
		}

		if (sortNo.equals(split_map.get(sortNo))) {
			return true;
		}
		return false;
	}
	
	/**
	 * int轉化
	 * 
	 * @param data_obj
	 * @return
	 */
	public int getInt(Object data_obj) {
		if (data_obj == null || data_obj == "") {
			return 0;
		} else {
			return Integer.valueOf(data_obj.toString());
		}
	}

	public InsjlbDaoInf getInsjlbDao() {
		return insjlbDao;
	}

	public void setInsjlbDao(InsjlbDaoInf insjlbDao) {
		this.insjlbDao = insjlbDao;
	}

	public InsjlbBusinessInf getInsjlb() {
		return insjlb;
	}

	public void setInsjlb(InsjlbBusinessInf insjlb) {
		this.insjlb = insjlb;
	}

	public INS810 getIns810() {
		return ins810;
	}

	public void setIns810(INS810 ins810) {
		this.ins810 = ins810;
	}

	public String getDATE_YMD() {
		return DATE_YMD;
	}
}
