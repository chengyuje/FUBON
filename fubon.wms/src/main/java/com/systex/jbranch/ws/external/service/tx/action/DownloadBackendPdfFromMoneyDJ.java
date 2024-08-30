package com.systex.jbranch.ws.external.service.tx.action;

import com.systex.jbranch.app.server.fps.sot815.SOT815MoneyDJ;
import com.systex.jbranch.platform.common.errHandle.APException;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 測試嘉實報表是否能夠正常下載
 */
@Component("downloadBackendPdfFromMoneyDJTxAction")
@Scope("request")
public class DownloadBackendPdfFromMoneyDJ extends TxAction {
    @Override
    public String execute(String prdId) throws Exception {
        if (StringUtils.isBlank(prdId)) {
            throw new APException("請在網址後面使用參數（custId=）後面接上欲從 MoneyDJ 下載的後收結構商品代號！");
        }
        return new SOT815MoneyDJ().downloadFileFromMoneydj(prdId);
    }
}
