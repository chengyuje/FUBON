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
 * 新帳號規則
 * @author Eli
 * @date 20180822
 * 
 * 20200323_CBS_彥德_SI申購_扣款帳號出現不該出現的帳號
 * 參考.21的SOT701.java的classifyacct把多餘的OBU邏輯刪除
 *
 */
@Component
@Scope("prototype")
public final class NewVersionAcct  extends AcctRuler {

	@Override
	protected String pkind1() {
		return acct.substring(0, 4);
	}

	@Override
	protected String pkind2() {
		return acct.substring(0, 3);
	}

	@Override
	protected String pkind3() {
		return null;
	}

	@Override
	protected boolean fitTrust() {
		//SI 組合式商品帳號
		if (isSI(prodType)) return pkind1().matches("8345|8395");  // FIXME 舊帳號 pkind1() equals "677" ，待新帳號提供
		// 保險
		else if (isINS(prodType)) return eq(pkind1(), "8168");
		//其他商品:
		else return pkind1().matches("8168|8178|8388"); // FIXME 基金或ETF商品，可包含1-3碼為560帳號；其他商品不可包含560帳號 待新帳號提供
	}

	/*
	 * 
	 * modified 
	 * 2021/04/20  #0597 SI比照SN調整邏輯  by SamTu
	 */
	@Override
	protected boolean fitDebit() {
		// 保險不加入扣款帳號
		if (isINS(prodType)) return false;
		// 其他商品 扣款帳號&收益入帳帳號    FIXME ETF商品，可包含1-3碼為560帳號；其他商品不可包含560帳號 待新帳號提供
		//OBU帳號只有8381 8382 8389 所以 !pkind2().matches("838")
		else return pkind1().matches("8211|8216|8212|8168|8221|8222|8311|8312|8381|8382|8389") && ((isFUND(prodType) ||isES(prodType))  || !pkind2().matches("838"));  
	}

	@Override
	protected boolean exclude() {   // FIXME 舊帳號eq(pkind1(), "177")，待新帳號提供
		return isBlank(acct) 
    			|| acct.length() != 14
    			|| acctCat.matches(".*定存.*|.*定期存款.*");
	}
}
