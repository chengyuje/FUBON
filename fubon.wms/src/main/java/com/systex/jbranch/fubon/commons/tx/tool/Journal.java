package com.systex.jbranch.fubon.commons.tx.tool;

import com.systex.jbranch.fubon.commons.esb.EsbRecorder;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 紀錄電文相關資訊
 */
@Component
@Scope("prototype")
public class Journal {
    public static String ON = "上行";
    public static String OFF = "下行";

    /**
     * 電文流水號
     **/
    private String seq;
    /**
     * 帳號
     **/
    private String id;
    /**
     * 路徑
     **/
    private String url;
    /**
     * 電文送達端點名稱
     **/
    private String terminal;
    /**
     * 電文交易名稱
     **/
    private String itemId;
    /**
     * 數字電文的回傳電文 Id（CBS 有）
     */
    private String pickUpId;
    /**
     * 模組
     */
    private String module;

    /**
     * 電文專屬 LOG
     */
    private Logger txlogger;

    /**
     * 日誌 LOG
     */
    private Logger logger;
    /**
     * 上行電文
     **/
    private List<String> onMsg;
    /**
     * 下行電文
     **/
    private List<String> offMsg;
    /**
     * 上送時間
     **/
    private Timestamp startTime;
    /**
     * 結束時間
     **/
    private Timestamp endTime;

    public Journal() {
        init();
    }

    /**
     * 初始化
     **/
    private void init() {
        onMsg = new ArrayList();
        offMsg = new ArrayList();
        txlogger = LoggerFactory.getLogger("TX_LOG");
        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 電文上送起始時間
     **/
    public void start() {
        startTime = new Timestamp(System.currentTimeMillis());
    }

    /**
     * 紀錄電文結束時間並寫入資料庫
     **/
    public void end() throws JBranchException {
        endTime = new Timestamp(System.currentTimeMillis());
        EsbRecorder.getInstance().record(this);
    }

    /**
     * 紀錄電文
     *
     * @param dirType [ON, OFF]
     * @param txMsg   電文 XML
     */
    public void record(String dirType, String txMsg) {
        /** 將電文保存在 List，之後記錄在 Table 上 **/
        if (ON.equals(dirType)) onMsg.add(txMsg);
        else offMsg.add(txMsg);

        String log = String.format("[%s][%s][%s][%s]", getSeq(), dirType, getItemId(), getModule());
        logger.info(log); // 紀錄電文重要參數以方便從 jbranch.log 追蹤 esb.log
        txlogger.info(log + "\nsendMsg:\n" + txMsg + "\n"); // esb.log 紀錄完整電文資訊
    }

    /**
     * 電文專屬 LOG
     **/
    private String getTxLog(String sendType, String txMsg) {
        return String.format("[%s][%s][%s][%s]%nsendMsg:%n%s%n",
                getSeq(), sendType, getItemId(), getModule(), txMsg);
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public List<String> getOnMsg() {
        return onMsg;
    }

    public void setOnMsg(List<String> onMsg) {
        this.onMsg = onMsg;
    }

    public List<String> getOffMsg() {
        return offMsg;
    }

    public void setOffMsg(List<String> offMsg) {
        this.offMsg = offMsg;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getPickUpId() {
        return pickUpId;
    }

    public void setPickUpId(String pickUpId) {
        this.pickUpId = pickUpId;
    }
}
