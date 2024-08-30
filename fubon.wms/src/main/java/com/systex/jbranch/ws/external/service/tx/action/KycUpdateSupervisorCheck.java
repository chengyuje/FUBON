package com.systex.jbranch.ws.external.service.tx.action;

import com.systex.jbranch.fubon.commons.cbs.service.WMS032154Service;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154InputVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 將指定 custId（多筆以 ',' 隔開）
 * 上送電文以更新「主管已覆核欄位」
 */
@Component("kycUpdateSupervisorCheckTxAction")
@Scope("prototype")
public class KycUpdateSupervisorCheck extends TxAction {
    @Autowired
    private WMS032154Service wms032154Service;

    @Override
    public String execute(String custId) {
        List<String> success = new ArrayList();
        List<String> fail = new ArrayList();

        for (String each : custId.split(",")) {
            WMS032154InputVO inputVO = new WMS032154InputVO();
            inputVO.setCUST_NO(each);
            try {
                wms032154Service.kycUpdateSupervisorCheck(inputVO);
                success.add(each);
            } catch (Exception e) {
                fail.add(each);
            }
        }

        return getXml(success, fail);
    }

    private String getXml(List<String> success, List<String> fail) {
        return "<success>" + StringUtils.join(success, ",") + "</success>\n" +
                "<fail>" + StringUtils.join(fail, ",") + "</fail>\n";
    }
}
