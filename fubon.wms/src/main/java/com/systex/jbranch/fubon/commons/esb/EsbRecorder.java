package com.systex.jbranch.fubon.commons.esb;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.commons.tx.tool.Journal;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import static com.systex.jbranch.platform.common.util.PlatformContext.getBean;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.join;

/**
 * 紀錄電文的上下文與相關資訊至 DB
 *
 * @author Eli
 */
@Component
public class EsbRecorder extends FubonWmsBizLogic {
    /**
     * 使用 Thread Pool 去管理執行塞入資訊的 Thread
     **/
    public static ThreadPoolTaskExecutor executor;

    static {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.initialize();
    }

    /**
     * 取得實例
     **/
    public static EsbRecorder getInstance() throws JBranchException {
        return getBean(EsbRecorder.class);
    }

    /**
     * 新架構 Tx Module 所採用的紀錄電文方法
     *
     * @param journal 電文資訊物件
     */
    public void record(final Journal journal) {
    	Object temp ;
        try {
            final Object id = getLoginId();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (recordNoNeed()) return;
                        recordInformation(journal, id);
                    } catch (JBranchException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getLoginId() throws JBranchException {
        try {
            return SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID);
        } catch (NullPointerException e) { // 網銀並無登入 ID，所以給特定值
            return "WebService";
        }
    }

    /**
     * 根據參數`SYS.ESB_LOG`來決定是否要紀錄電文資訊
     **/
    private boolean recordNoNeed() throws JBranchException {
        return Manager.manage(this.getDataAccessManager())
                .append("select 1 from TBSYSPARAMETER ")
                .append("where PARAM_TYPE = 'SYS.ESB_LOG' and PARAM_CODE = 'RECORD' and PARAM_NAME = 'Y' ")
                .query()
                .isEmpty();
    }

    /**
     * 紀錄資訊
     **/
    private void recordInformation(Journal journal, Object id) throws JBranchException {
        Manager.manage(this.getDataAccessManager())
                .append("insert into TBSYS_ESB_RECORD (HSTANO, HTXTID, ONMSG, OFFMSG, CREATOR, CREATETIME, ENDTIME) ")
                .append("values (:hstano, :htxtid, :onmsg, :offmsg, :creator, :createtime, :endtime) ")
                .put("hstano", journal.getSeq())
                .put("htxtid", journal.getItemId() + (isNotBlank(journal.getPickUpId())? "-" + journal.getPickUpId(): ""))
                .put("onmsg", join(journal.getOnMsg(), "\n\n"))
                .put("offmsg", join(journal.getOffMsg(), "\n\n"))
                .put("creator", id)
                .put("createtime", journal.getStartTime())
                .put("endtime", journal.getEndTime())
                .update();
    }
}
