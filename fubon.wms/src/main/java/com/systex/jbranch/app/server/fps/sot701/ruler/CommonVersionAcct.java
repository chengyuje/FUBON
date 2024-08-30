package com.systex.jbranch.app.server.fps.sot701.ruler;

import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isES;
import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isFUND;
import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isINS;
import static com.systex.jbranch.app.server.fps.sot701.ruler.ProdRuler.isSI;
import static com.systex.jbranch.app.server.fps.sot701.ruler.Ruler.eq;
import static org.apache.commons.lang.StringUtils.isBlank;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
 * 改用產品大小類判斷
 * 不區別560帳號
 * 2022.08.19 Sam Tu
 */
@Component
@Scope("prototype")
public class CommonVersionAcct extends AcctRuler {
    

	@Override
	protected boolean exclude() {
		return isBlank(acct) 
    			|| acct.length() != 14
    			|| acctCat.matches(".*定存.*|.*定期存款.*");
	}

	@Override
	protected String pkind1() {
		return acct.substring(0, 4);
	}

	@Override
	protected String pkind2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String pkind3() {
		return acct.substring(2, 5);
	}

	@Override
	protected boolean fitTrust() {
		//SI 組合式商品帳號
		if (isSI(prodType)) return eq(wa_x_type, "3013");
		// 保險
		else if (isINS(prodType)) return wa_x_type.matches("1621|1623|1631|1633|8168");
		//其他商品
		else return wa_x_type.matches("1621|1623|1631|1633|8168|8178|8388"); //&& ((isFUND(prodType) ||isES(prodType)) || check560Acct());;
	}

	@Override
	protected boolean fitDebit() {
		StringBuffer match = new StringBuffer();
		match.append("1241|1261|1263|1271|1273|1441|1442|1443|1451|1452|1453|1461|1462|1463|1471|1472|1473|1474|1521|1523|");
		match.append("1531|1533|1541|1542|1543|1551|1552|1553|1561|1562|1563|1571|1572|1573|1574|1621|1623|1631|1633|");
		match.append("1641|1651|1661|1663|1671|1673|1762|1772");

		if (isINS(prodType)) return false;
		// 其他商品 扣款帳號&收益入帳帳號
		else return (wa_x_type.matches(match.toString())); //&& ((isFUND(prodType) ||isES(prodType)) || check560Acct());
	}
	
	private boolean check560Acct() {
		String flag = acct.substring(0, 1);
		boolean isOld = flag.equals("0");
		boolean isNew = flag.equals("8");
		
		if(isOld) {
			return !eq(pkind3(), "560");
		} else if (isNew) {
			return !eq(pkind1(), "8388");
		} else {
			return false;
		}
	}

}
