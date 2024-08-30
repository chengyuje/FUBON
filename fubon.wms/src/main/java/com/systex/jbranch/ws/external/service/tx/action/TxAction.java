package com.systex.jbranch.ws.external.service.tx.action;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;

public abstract class TxAction extends FubonWmsBizLogic {
    public abstract String execute(String custId) throws Exception;
}
