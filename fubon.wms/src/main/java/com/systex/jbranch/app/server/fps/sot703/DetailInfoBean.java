package com.systex.jbranch.app.server.fps.sot703;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_BATCHVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;

/**
 * Created by SebastianWu on 2016/10/12.
 */
public class DetailInfoBean {
    private TBSOT_NF_PURCHASE_DVO purchaseDVO;
    private TBSOT_NF_REDEEM_DVO redeemDVO;
    private TBSOT_NF_TRANSFER_DVO transferDVO;
    private TBSOT_TRADE_MAINVO tradeMainVO;
    private TBSOT_NF_CHANGE_DVO changeDVO;
    private TBSOT_NF_CHANGE_BATCHVO changeBatchVO;
    private TBORG_MEMBERVO memberVO;
    private TBCRM_CUST_MASTVO custMastVO;
    
    
	public TBSOT_NF_PURCHASE_DVO getPurchaseDVO() {
		return purchaseDVO;
	}
	public void setPurchaseDVO(TBSOT_NF_PURCHASE_DVO purchaseDVO) {
		this.purchaseDVO = purchaseDVO;
	}
	public TBSOT_NF_REDEEM_DVO getRedeemDVO() {
		return redeemDVO;
	}
	public void setRedeemDVO(TBSOT_NF_REDEEM_DVO redeemDVO) {
		this.redeemDVO = redeemDVO;
	}
	public TBSOT_NF_TRANSFER_DVO getTransferDVO() {
		return transferDVO;
	}
	public void setTransferDVO(TBSOT_NF_TRANSFER_DVO transferDVO) {
		this.transferDVO = transferDVO;
	}
	public TBSOT_TRADE_MAINVO getTradeMainVO() {
		return tradeMainVO;
	}
	public void setTradeMainVO(TBSOT_TRADE_MAINVO tradeMainVO) {
		this.tradeMainVO = tradeMainVO;
	}
	public TBSOT_NF_CHANGE_DVO getChangeDVO() {
		return changeDVO;
	}
	public void setChangeDVO(TBSOT_NF_CHANGE_DVO changeDVO) {
		this.changeDVO = changeDVO;
	}
	public TBSOT_NF_CHANGE_BATCHVO getChangeBatchVO() {
		return changeBatchVO;
	}
	public void setChangeBatchVO(TBSOT_NF_CHANGE_BATCHVO changeBatchVO) {
		this.changeBatchVO = changeBatchVO;
	}
	public TBORG_MEMBERVO getMemberVO() {
		return memberVO;
	}
	public void setMemberVO(TBORG_MEMBERVO memberVO) {
		this.memberVO = memberVO;
	}
	public TBCRM_CUST_MASTVO getCustMastVO() {
		return custMastVO;
	}
	public void setCustMastVO(TBCRM_CUST_MASTVO custMastVO) {
		this.custMastVO = custMastVO;
	}
}
