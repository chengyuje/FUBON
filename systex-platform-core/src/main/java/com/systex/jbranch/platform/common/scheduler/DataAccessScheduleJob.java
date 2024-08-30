package com.systex.jbranch.platform.common.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.conversation.ConversationIF;

public class DataAccessScheduleJob implements DataAccessScheduleJobIF {
// ------------------------------ FIELDS ------------------------------
	
	private Logger logger = LoggerFactory.getLogger(DataAccessScheduleJob.class);

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DataAccessScheduleJobIF ---------------------

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeScheduleJob(UUID uuid, ConversationIF conversation, Map jobInfoMap, Map scheduleParaMap) {
        try {
            //取得job相關參數
            String beanname = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_BEANNAME);
            String precondition = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_PRECONDITION);
            String postcondition = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_POSTCONDITION);
            String parameterStr = (String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_PARAMETER);
            Map jobParaMap = ScheduleUtil.getParameter(parameterStr);

            //取得欲呼叫的method名稱
            String methodName = (String) jobParaMap.get("method");
            if (methodName == null || methodName.trim().length() == 0) {
                throw new Exception("method name not found");
            }

            //為呼叫bizlogic所模擬的參數
            Map transientVars = new HashMap();
            transientVars.put(BizLogic.VAR_UUID, uuid);
            transientVars.put(BizLogic.VAR_CONVERSATION, conversation);
            Map args = new HashMap();
            args.put("method", methodName);//欲呼叫的method名稱

            // for re-run by Jemmy 2018-01-22
            jobInfoMap.put("transientVars", transientVars);
            jobInfoMap.put("args", args);
            //執行PreCondition
            exePrecondition(precondition, jobParaMap, scheduleParaMap, (DataAccessPreConditionIF) jobInfoMap.get(SchedulerHelper.PRE_INSTANCE), jobInfoMap);

            //執行Job
            exeJob(transientVars, args, beanname, (BizLogic) jobInfoMap.get(SchedulerHelper.JOB_INSTANCE), jobParaMap, scheduleParaMap, jobInfoMap);

            //執行PostCondition
            exePostcondition(postcondition, jobParaMap, scheduleParaMap, (DataAccessPostConditionIF) jobInfoMap.get(SchedulerHelper.POST_INSTANCE), jobInfoMap);
        }
        catch (Exception e) {
            Throwable t = e.getCause();
            if (t != null) {
                throw new RuntimeException(t);
            }
            else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 執行Job Class
     *
     * @param transientVars
     * @param args
     * @param classname
     * @param jobParaMap
     * @param scheduleParaMap
     */
    private void exeJob(Map transientVars, Map args, String beanname, BizLogic biz, Map jobParaMap,
                        Map scheduleParaMap, Map jobInfoMap) throws Exception {
        if (beanname == null || beanname.trim().length() == 0) {
            throw new JBranchException("無法取得beanname");
        }
        if (logger.isInfoEnabled()) {
            logger.info("start job " + beanname);
        }

        if (jobInfoMap.get(SchedulerHelper.JOB_INSTANCE) == null) {
            throw new Exception((String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_JOBTYPE));
        }

        try {
            biz.execute(transientVars, args, null);
        }
        catch (Exception e) {
            logger.error("execute job class error " + beanname, e);
            throw e;
        }
    }

    /**
     * 執行Post Condition
     *
     * @param postcondition
     * @param jobParaMap
     * @param scheduleParaMap
     */
    private void exePostcondition(String postcondition, Map jobParaMap, Map scheduleParaMap, DataAccessPostConditionIF postc, Map jobInfoMap) throws Exception {
        if (postcondition == null || postcondition.trim().length() == 0) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("start postcondition " + postcondition);
        }
        if (jobInfoMap.get(SchedulerHelper.POST_INSTANCE) == null) {
            throw new Exception((String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_POSTCONDITION_INFO));
        }

        try {
            postc.process(jobInfoMap, scheduleParaMap);
        }
        catch (Exception e) {
            logger.error("execute postcondition error " + postcondition, e);
            throw e;
        }
    }

    /**
     * 執行PreCondition
     *
     * @param precondition
     * @param jobParaMap
     * @param scheduleParaMap
     */
    private void exePrecondition(String precondition, Map jobParaMap, Map scheduleParaMap, DataAccessPreConditionIF prec, Map jobInfoMap) throws Exception {
        if (precondition == null || precondition.trim().length() == 0) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("start precondition " + precondition);
        }

        if (jobInfoMap.get(SchedulerHelper.PRE_INSTANCE) == null) {
            throw new Exception((String) jobInfoMap.get(SchedulerHelper.JOB_INFO_KEY_PRECONDITION_INFO));
        }

        try {
            prec.process(jobInfoMap, scheduleParaMap);
        }
        catch (Exception e) {
            logger.error("execute precondition error " + precondition, e);
            throw e;
        }
    }
}
