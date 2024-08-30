package com.systex.jbranch.app.server.fps.pms201;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專個人生產力診斷書<br>
 * Comments Name : PMS202OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月22日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS201OutputVO extends PagingOutputVO {
	
	private List weekDate;     		//月週
	
	//A GROUP
	private List aGroupTitleList;	//A.業績表現達成率tr TITLE
	private List resultBehaveRate;  //業績表現達成率
	private List empDtl;

	//B GROUP
	private List bGroupTitleList;	//B.產品組合指標tr TITLE
	private List prodGroupIndex;	//產品組口指標
	private List prodTrackDtl;		//本月追蹤商品明細
	
	//C GROUP
	private List cGroupTitle1List;	//C.客戶經營指標tr TITLE
	private List cGroupTitle2List;	//C.客戶經營指標tr TITLE
	private List cGroupTitle3List;	//C.客戶經營指標tr TITLE
	private List cGroupTitle4List;	//C.客戶經營指標tr TITLE
	private List prdctvt1List;
	private List prdctvt2List;
	private List prdctvt3List;
	private List prdCtvtMngmDtl;
	
	//D GROUP
	private List dGroupTitleList;	//D.活動量指標tr TITLE
	private List actIndexList;		//活動量指標
	private List transCustList;		//成交客戶
	
	private List resultList; 
	
	
	
	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getPrdCtvtMngmDtl() {
		return prdCtvtMngmDtl;
	}

	public void setPrdCtvtMngmDtl(List prdCtvtMngmDtl) {
		this.prdCtvtMngmDtl = prdCtvtMngmDtl;
	}

	public List getcGroupTitle4List() {
		return cGroupTitle4List;
	}

	public void setcGroupTitle4List(List cGroupTitle4List) {
		this.cGroupTitle4List = cGroupTitle4List;
	}

	public List getcGroupTitle2List() {
		return cGroupTitle2List;
	}

	public void setcGroupTitle2List(List cGroupTitle2List) {
		this.cGroupTitle2List = cGroupTitle2List;
	}

	public List getcGroupTitle3List() {
		return cGroupTitle3List;
	}

	public void setcGroupTitle3List(List cGroupTitle3List) {
		this.cGroupTitle3List = cGroupTitle3List;
	}

	public List getcGroupTitle1List() {
		return cGroupTitle1List;
	}

	public void setcGroupTitle1List(List cGroupTitle1List) {
		this.cGroupTitle1List = cGroupTitle1List;
	}

	public List getPrdctvt1List() {
		return prdctvt1List;
	}

	public void setPrdctvt1List(List prdctvt1List) {
		this.prdctvt1List = prdctvt1List;
	}

	public List getPrdctvt2List() {
		return prdctvt2List;
	}

	public void setPrdctvt2List(List prdctvt2List) {
		this.prdctvt2List = prdctvt2List;
	}

	public List getPrdctvt3List() {
		return prdctvt3List;
	}

	public void setPrdctvt3List(List prdctvt3List) {
		this.prdctvt3List = prdctvt3List;
	}

	public List getdGroupTitleList() {
		return dGroupTitleList;
	}

	public void setdGroupTitleList(List dGroupTitleList) {
		this.dGroupTitleList = dGroupTitleList;
	}

	public List getActIndexList() {
		return actIndexList;
	}

	public void setActIndexList(List actIndexList) {
		this.actIndexList = actIndexList;
	}

	public List getTransCustList() {
		return transCustList;
	}

	public void setTransCustList(List transCustList) {
		this.transCustList = transCustList;
	}

	public List getEmpDtl() {
		return empDtl;
	}

	public void setEmpDtl(List empDtl) {
		this.empDtl = empDtl;
	}

	public List getaGroupTitleList() {
		return aGroupTitleList;
	}

	public void setaGroupTitleList(List aGroupTitleList) {
		this.aGroupTitleList = aGroupTitleList;
	}

	public List getResultBehaveRate() {
		return resultBehaveRate;
	}

	public void setResultBehaveRate(List resultBehaveRate) {
		this.resultBehaveRate = resultBehaveRate;
	}

	public List getProdTrackDtl() {
		return prodTrackDtl;
	}

	public void setProdTrackDtl(List prodTrackDtl) {
		this.prodTrackDtl = prodTrackDtl;
	}

	public List getbGroupTitleList() {
		return bGroupTitleList;
	}

	public void setbGroupTitleList(List bGroupTitleList) {
		this.bGroupTitleList = bGroupTitleList;
	}

	public List getProdGroupIndex() {
		return prodGroupIndex;
	}

	public void setProdGroupIndex(List prodGroupIndex) {
		this.prodGroupIndex = prodGroupIndex;
	}

	public List getWeekDate() {
		return weekDate;
	}
	
	public void setWeekDate(List weekDate) {
		this.weekDate = weekDate;
	}

	
//	public List getEmpDateUL3() {
//		return empDateUL3;
//	}
//	public void setEmpDateUL3(List empDateUL3) {
//		this.empDateUL3 = empDateUL3;
//	}
//	public List getEmpDateR() {
//		return empDateR;
//	}
//	public List getEmpDateUL2() {
//		return empDateUL2;
//	}
//	public void setEmpDateUL2(List empDateUL2) {
//		this.empDateUL2 = empDateUL2;
//	}
//	public List getEmpDateUL() {
//		return empDateUL;
//	}
//	public void setEmpDateUL(List empDateUL) {
//		this.empDateUL = empDateUL;
//	}
//	public List getEmpDateUR() {
//		return empDateUR;
//	}
//	public void setEmpDateUR(List empDateUR) {
//		this.empDateUR = empDateUR;
//	}
//	public List getEmpDateURL() {
//		return empDateURL;
//	}
//	public List getEmpDateUR2() {
//		return empDateUR2;
//	}
//	public void setEmpDateUR2(List empDateUR2) {
//		this.empDateUR2 = empDateUR2;
//	}
//	public void setEmpDateURL(List empDateURL) {
//		this.empDateURL = empDateURL;
//	}
//	public void setEmpDateR(List empDateR) {
//		this.empDateR = empDateR;
//	}
//	public List getEmpDateR1() {
//		return empDateR1;
//	}
//	public void setEmpDateR1(List empDateR1) {
//		this.empDateR1 = empDateR1;
//	}
//	public List getResultList() {
//		return resultList;
//	}
//	public void setResultList(List resultList) {
//		this.resultList = resultList;
//	}
//	public List getEmpData() {
//		return empData;
//	}
//	public void setEmpData(List empData) {
//		this.empData = empData;
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//
//
//

}
