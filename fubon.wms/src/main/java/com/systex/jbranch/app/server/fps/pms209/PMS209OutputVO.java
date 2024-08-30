package com.systex.jbranch.app.server.fps.pms209;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 競爭力趨勢<br>
 * Comments Name : 209OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS209OutputVO extends PagingOutputVO {
	
	private List DATA;
	private List TITLE;
	
	private List resultList;
	
	private List list;
	private List Image;
	private List prodList;  //商品清單
	private List ymList;    //下拉選單年月
	
	
	public List getTITLE() {
		return TITLE;
	}

	public void setTITLE(List tITLE) {
		TITLE = tITLE;
	}

	public List getList() {
		return list;
	}
	
	public void setList(List list) {
		this.list = list;
	}
	
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	
	public List getDATA() {
		return DATA;
	}
	
	public List getImage() {
		return Image;
	}
	
	public void setImage(List image) {
		Image = image;
	}
	
	public void setDATA(List dATA) {
		DATA = dATA;
	}
	
	public List getProdList() {
		return prodList;
	}
	
	public void setProdList(List prodList) {
		this.prodList = prodList;
	}
	
	public List getYmList() {
		return ymList;
	}
	
	public void setYmList(List ymList) {
		this.ymList = ymList;
	}
	
}
