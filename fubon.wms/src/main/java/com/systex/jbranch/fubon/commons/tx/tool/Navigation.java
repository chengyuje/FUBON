package com.systex.jbranch.fubon.commons.tx.tool;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.upperCase;

@Service
public class Navigation extends FubonWmsBizLogic {

    /**
     * 取得 ESB 電文 的 Host Drive Queue
     * 該欄位用來識別電文需要發送至哪個環境
     * **/
    public String getHostDriveQueue(String txId) {
        List<Map<String, String>> result = null;
        try {
            result = Manager.manage(this.getDataAccessManager())
                    .append("select PARAM_NAME from TBSYSPARAMETER ")
                    .append("where PARAM_TYPE = 'TX.ESB_HDRVQ1' ")
                    .append("and PARAM_CODE = :code ")
                    .put("code", upperCase(txId))
                    .query();
        } catch (JBranchException e) {
            e.printStackTrace();
        }

        if (isEmpty(result)) return null;
        return defaultString(
                result.get(0).get("PARAM_NAME"))
                .toUpperCase();
    }
}
