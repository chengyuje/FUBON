package com.systex.jbranch.app.server.fps.mgm310;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM310InputVO extends PagingInputVO{
	
	private String gift_seq;				//贈品代碼
	private String gift_introduction;		//贈品簡介
	private String gift_name;				//贈品名稱
	private String gift_kind;				//贈品性質
	private String gift_get_way;			//領用方式
	private String supplier_ein;			//贈品廠商統編
	private String supplier_name;			//贈品廠商名稱
	private String gift_costs;				//贈品成本
	private String gift_amount;				//贈品總數量
	private String gift_taken;				//贈品已領用數量
	private String gift_remaining;			//贈品剩餘數量
	private String gift_photo;				//贈品圖片
	private String pictureName;   
	private String realpictureName;

	public String getGift_seq() {
		return gift_seq;
	}

	public void setGift_seq(String gift_seq) {
		this.gift_seq = gift_seq;
	}

	public String getGift_introduction() {
		return gift_introduction;
	}

	public void setGift_introduction(String gift_introduction) {
		this.gift_introduction = gift_introduction;
	}

	public String getGift_name() {
		return gift_name;
	}

	public void setGift_name(String gift_name) {
		this.gift_name = gift_name;
	}

	public String getGift_kind() {
		return gift_kind;
	}

	public void setGift_kind(String gift_kind) {
		this.gift_kind = gift_kind;
	}

	public String getGift_get_way() {
		return gift_get_way;
	}

	public void setGift_get_way(String gift_get_way) {
		this.gift_get_way = gift_get_way;
	}

	public String getSupplier_ein() {
		return supplier_ein;
	}

	public void setSupplier_ein(String supplier_ein) {
		this.supplier_ein = supplier_ein;
	}

	public String getSupplier_name() {
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name) {
		this.supplier_name = supplier_name;
	}

	public String getGift_costs() {
		return gift_costs;
	}

	public void setGift_costs(String gift_costs) {
		this.gift_costs = gift_costs;
	}

	public String getGift_amount() {
		return gift_amount;
	}

	public void setGift_amount(String gift_amount) {
		this.gift_amount = gift_amount;
	}

	public String getGift_taken() {
		return gift_taken;
	}

	public void setGift_taken(String gift_taken) {
		this.gift_taken = gift_taken;
	}

	public String getGift_remaining() {
		return gift_remaining;
	}

	public void setGift_remaining(String gift_remaining) {
		this.gift_remaining = gift_remaining;
	}

	public String getGift_photo() {
		return gift_photo;
	}

	public void setGift_photo(String gift_photo) {
		this.gift_photo = gift_photo;
	}

	public String getPictureName() {
		return pictureName;
	}

	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	public String getRealpictureName() {
		return realpictureName;
	}

	public void setRealpictureName(String realpictureName) {
		this.realpictureName = realpictureName;
	}
	
}
