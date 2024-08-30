package com.systex.jbranch.fubon.commons.tx.traffic;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.tx.tool.Journal;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 電文抽象
 */
public abstract class Truck extends FubonWmsBizLogic {
    /**
     * 紀錄電文相關資訊
     */
    protected Journal journal;

    /**
     * 初始電文參數 (來源: DB Parameter)
     *
     * @param terminal 電文送達端點名稱
     * @param itemId   電文交易名稱
     * @return
     */
    public void initParameters(String terminal, String itemId) throws APException {
        if (StringUtils.isBlank(terminal) || StringUtils.isBlank(itemId))
            throw new APException("電文參數設置不正確！");
        journal.setTerminal(terminal);
        journal.setItemId(itemId);

        try {
            Map params = (Map) Manager.manage(this.getDataAccessManager())
                    .append("select SQ_TBSYS_ESB_LOG_HSTANO.NEXTVAL SEQ, ")
                    .append("       ( select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = :type and PARAM_CODE = 'ID') ID, ")
                    .append("       ( select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = :type and PARAM_CODE = 'URL') URL ")
                    .append("from dual ")
                    .put("type", "TX." + terminal)
                    .query()
                    .get(0);
            journal.setSeq(String.format("%07d", ((BigDecimal) params.get("SEQ")).intValueExact()));
            journal.setId(params.get("ID").toString());
            journal.setUrl(params.get("URL").toString());
        } catch (JBranchException e) {
            throw new APException("資料庫參數設置不正確！");
        }
    }

    /**
     * 初始配置
     */
    public abstract void configure() throws Exception;

    /**
     * 設置電文 RequestVO
     **/
    public abstract void setRequestVO(Object object);

    /**
     * 通知開始工作
     **/
    public void work() throws Exception {
        /** 紀錄電文開始執行時間 **/
        journal.start();
        try {
            /** 發送 **/
            send();
        } finally {
            /** 紀錄電文結束時間並寫入資料庫 **/
            journal.end();
        }
    }

    /**
     * 發送實作邏輯
     *
     * @throws Exception
     */
    protected abstract void send() throws Exception;

    /**
     * 取得電文 ResponseVO
     **/
    public abstract List getResponseVO();

    @Autowired
    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    /**
     * 紀錄上行電文
     **/
    protected void recordOnMsg(String onMsg) {
        journal.record(Journal.ON, onMsg);
    }

    /**
     * 紀錄下行電文
     **/
    protected void recordOffMsg(String offMsg) {
        journal.record(Journal.OFF, offMsg);
    }
}
