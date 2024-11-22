package com.systex.jbranch.app.server.fps.sot701.ruler;

import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isES;
import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isFUND;
import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isINS;
import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isSI;
import static com.systex.jbranch.app.server.fps.sot701.ruler.Ruler.eq;
import static org.apache.commons.lang.StringUtils.isBlank;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 舊帳號規則
 * @author Eli
 * @date 20180822
 * 
 *  * 
 * 20200323_CBS_彥德_SI申購_扣款帳號出現不該出現的帳號
 * 參考.21的SOT701.java的classifyacct把多餘的OBU邏輯刪除

 *
 */
@Component
@Scope("prototype")
public final class OldVersionAcct extends AcctRuler {

	@Override
	protected String pkind1() {
		return acct.substring(5, 8);
	}

	@Override
	protected String pkind2() {
		return acct.substring(5, 7);
	}

	@Override
	protected String pkind3() {
		return acct.substring(2, 5);
	}

	@Override
	protected boolean fitTrust() {
		//SI 組合式商品帳號
		if (isSI(prodType)) return eq(pkind1(), "677");
		// 保險
		else if (isINS(prodType)) return eq(pkind1(), "168");
		//其他商品: 基金或ETF商品，可包含1-3碼為560帳號；其他商品不可包含560帳號 ==> WMS-CR-20240311-01_海外債券及SN OBU申購及贖回套表功能：此CR將該判斷移除(只有OBU客戶有560帳號 by 美芳)
		else return pkind1().matches("168|368");
}

	/*
	 * 
	 * modified 
	 * 2021/04/20  #0597 SI比照SN調整邏輯  by SamTu
	 */
	@Override
	protected boolean fitDebit() {
		if (isSI(prodType)) return eq(pkind1(), "168") || pkind2().matches("17|18");
		// 保險不加入扣款帳號
		else if (isINS(prodType)) return false;
		// 其他商品 扣款帳號&收益入帳帳號
		else return (pkind1().matches("102|104|120|168|210|221") || pkind2().matches("17|18"))
					&& ((isFUND(prodType) ||isES(prodType)) || !eq(pkind3(), "560"));
	}

	@Override
	protected boolean exclude() {
		return isBlank(acct) 
    			|| acct.length() != 14 
    			|| eq(pkind1(), "177") 
    			|| acctCat.matches(".*定存.*|.*定期存款.*");
	}
}
