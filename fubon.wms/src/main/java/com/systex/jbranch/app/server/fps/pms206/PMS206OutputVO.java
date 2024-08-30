package com.systex.jbranch.app.server.fps.pms206;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : AMC活動量<br>
 * Comments Name : PMS208OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月27日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS206OutputVO extends PagingOutputVO {
	private List resultList;   //查詢結果
	private List<Map<String,Object>> dateList;  //本週,上週;本月,上月日期區間
	private List<Map<String,Object>> weekList;  //取週的下拉選單
	private List<Map<String,Object>> meonthList;  //取月的下拉選單


	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getDateList() {
		return dateList;
	}
	public void setDateList(List<Map<String, Object>> dateList) {
		this.dateList = dateList;
	}
	public List<Map<String, Object>> getWeekList() {
		return weekList;
	}
	public void setWeekList(List<Map<String, Object>> weekList) {
		this.weekList = weekList;
	}
	public List<Map<String, Object>> getMeonthList() {
		return meonthList;
	}
	public void setMeonthList(List<Map<String, Object>> meonthList) {
		this.meonthList = meonthList;
	}
	
}
