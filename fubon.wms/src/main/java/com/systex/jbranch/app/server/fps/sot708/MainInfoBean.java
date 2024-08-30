package com.systex.jbranch.app.server.fps.sot708;

import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SIINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SIVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;

/**
 * Created by SebastianWu on 2016/10/5.
 */
public class MainInfoBean {
    private TBSOT_TRADE_MAINVO tbsotTradeMainvo;
    private TBSOT_SI_TRADE_DVO tbsotSiTradeDvo;
    private TBORG_MEMBERVO tborgMembervo;
    private TBPRD_SIINFOVO tbprdSiInfovo;
    private TBPRD_SIVO tbprdSivo;

    public TBSOT_TRADE_MAINVO getTbsotTradeMainvo() {
        return tbsotTradeMainvo;
    }

    public void setTbsotTradeMainvo(TBSOT_TRADE_MAINVO tbsotTradeMainvo) {
        this.tbsotTradeMainvo = tbsotTradeMainvo;
    }

    public TBSOT_SI_TRADE_DVO getTbsotSiTradeDvo() {
        return tbsotSiTradeDvo;
    }

    public void setTbsotSiTradeDvo(TBSOT_SI_TRADE_DVO tbsotSiTradeDvo) {
        this.tbsotSiTradeDvo = tbsotSiTradeDvo;
    }

    public TBORG_MEMBERVO getTborgMembervo() {
        return tborgMembervo;
    }

    public void setTborgMembervo(TBORG_MEMBERVO tborgMembervo) {
        this.tborgMembervo = tborgMembervo;
    }

	public TBPRD_SIINFOVO getTbprdSiInfovo() {
		return tbprdSiInfovo;
	}

	public void setTbprdSiInfovo(TBPRD_SIINFOVO tbprdSiInfovo) {
		this.tbprdSiInfovo = tbprdSiInfovo;
	}

	public TBPRD_SIVO getTbprdSivo() {
		return tbprdSivo;
	}

	public void setTbprdSivo(TBPRD_SIVO tbprdSivo) {
		this.tbprdSivo = tbprdSivo;
	}
    
}
