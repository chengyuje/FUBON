package com.systex.jbranch.platform.common.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.quartz.CronTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSCHDQUERYDETAILVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSCHDQUERYMASTERVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdaddetailPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdaddetailVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysschdadmasterVO;
import com.systex.jbranch.platform.common.util.NullTK;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

public class ScheduleUtil {
// ------------------------------ FIELDS ------------------------------

    private static Logger logger = LoggerFactory.getLogger(ScheduleUtil.class);

    private static Map<String, String> resultMapping = new HashMap<String, String>();
    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String PARAMETER_SEPARATOR = ";";
    private static final String SCHD_MASTER = "MASTER";
    private static final String SCHD_DETAIL = "DETAIL";

// -------------------------- STATIC METHODS --------------------------

    static {
        resultMapping.put("1", "SUCC");
        resultMapping.put("2", "FAIL");
        resultMapping.put("3", "EXCEPTION");
    }

    /**
     * 記錄排程稽核記錄
     *
     * @param message
     */
    public static void audit(SessionFactory sf,String scheduleid ,Long masterID, String type, String message, String userID) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            TbsysschdaddetailVO detailVO = new TbsysschdaddetailVO();
            TbsysschdaddetailPK detailPK = new TbsysschdaddetailPK();
            SerialNumberUtil seq=new SerialNumberUtil();
            String hostName = InetAddress.getLocalHost().getHostName()+SCHD_DETAIL;    
            String seqno = null;
    		try {
    			seqno = seq.getNextSerialNumber(hostName);
    		} catch (Exception e) {
    			// ignore
    		}
    		
            if (seqno == null) {
            	seqno = "1";
            	createNewSerial(seq, hostName, 1L);
    		}
            detailPK.setdetailid(Long.valueOf(seqno));
            detailPK.setauditid(String.valueOf(masterID));
            detailPK.setscheduleid(scheduleid);
            if (message.length() > 1024) {
                message = message.substring(0, 1024);
            }
            detailVO.setcomp_id(detailPK);
            detailVO.setmessage(message);
            detailVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
            detailVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
            detailVO.setCreator(userID);
            detailVO.setModifier(userID);
            session.save(detailVO);
//            session.flush();

//			detailps.setLong(1, currentMasterVO.getauditid());//auditid
//			detailps.setString(2, currentMasterVO.getjobid());//jobid
//			detailps.setString(3, currentMasterVO.getjobname());//jobname
//			detailps.setString(4, type);//type
//			detailps.setString(5, message);//message
//			detailps.setLong(6,0);//version
//			detailps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));//createtime
//			detailps.setString(8, SCHEDULER_USER);//creator
//			detailps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));//lastudpate
//			detailps.setString(10, SCHEDULER_USER);//modifier
//			detailps.execute();
        }
        catch (Exception e) {
            logger.error("create audit detail log exception", e);
        }
        finally {
            try {
                transaction.commit();
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
            try {
                session.close();
            }
            catch (Exception e) {
            	;
            }
        }
    }

    /**
     * 建立排程稽核記錄主檔
     *
     * @param con
     */
    public static void audit(SessionFactory sf, String scheduleID, String scheduleName,
                             String status, String userID, String result, String description) throws JBranchException {
        Session session = null;
        Transaction transaction = null;
        try {
            if (description.length() > 1024) {
                description = description.substring(0, 1024);
            }
            session = sf.openSession();
            transaction = session.beginTransaction();
            TbsysschdadmasterVO vo = new TbsysschdadmasterVO();
            TbsysschdadmasterPK pk = new TbsysschdadmasterPK();
            SerialNumberUtil seq=new SerialNumberUtil();
            String hostName = InetAddress.getLocalHost().getHostName()+SCHD_MASTER;   
            String seqno = null;
    		try {
    			seqno = seq.getNextSerialNumber(hostName);
    		} catch (Exception e) {
    			// ignore
    		}
            if (seqno == null) {
            	seqno = "1";
            	createNewSerial(seq, hostName, 1L);
    		}         
            pk.setauditid(Long.valueOf(seqno));
            pk.setscheduleid(scheduleID);
            
            vo.setcomp_id(pk);
            vo.setstarttime(new Timestamp(System.currentTimeMillis()));
            vo.setstatus(status);
            vo.setresult(result);
            vo.setdescription(description);
            vo.setCreatetime(new Timestamp(System.currentTimeMillis()));
            vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
            vo.setCreator(userID);
            vo.setModifier(userID);
            session.save(vo);
            if (logger.isInfoEnabled()) {
                logger.info(getSchedulerAuditLog(vo));
            }
        }
        catch (Exception e) {
            logger.error("audit failed", e);
        }
        finally {
            try {
                transaction.commit();
            }
            catch (Exception e) {
            }
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    public static String getSchedulerAuditLog(TbsysschdadmasterVO mvo) {
        String beginTime = "";
        String endTime = "";
        if (mvo.getstarttime() != null) {
            beginTime = SDF.format(new Date(mvo.getstarttime().getTime()));
        }
        if (mvo.getendtime() != null) {
            endTime = SDF.format(new Date(mvo.getendtime().getTime()));
        }
        String descript = "";
        if (mvo.getresult() != null && !SchedulerHelper.RESULT_SUCCESS.equals(mvo.getresult())) {
            descript = String.format("%s-%s-%s-%s-%s-%s~%s"
                    , NullTK.checkNull(mvo.getcomp_id().getscheduleid(), "")
                    , NullTK.checkNull(mvo.getjobid(), "")
                    , NullTK.checkNull(resultMapping.get(mvo.getresult()), "")
                    , auditFormat(NullTK.checkNull(mvo.getdescription(), ""))
                    , NullTK.checkNull(mvo.getcomp_id().getauditid(), "")
                    , beginTime
                    , endTime);
        }

        return String.format("|%s|%s|%s|%s|%s|%s|%s|" + descript
                , beginTime
                , endTime
                , NullTK.checkNull(mvo.getcomp_id().getauditid(), "")
                , NullTK.checkNull(mvo.getcomp_id().getscheduleid(), "")
                , NullTK.checkNull(mvo.getjobid(), "")
                , NullTK.checkNull(mvo.getstatus(), "")
                , NullTK.checkNull(mvo.getresult(), ""));
    }

    private static String auditFormat(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("|", " ").replace("[", "{").replace("]", "}");
    }

    /**
     * 建立排程稽核記錄主檔
     *
     * @param con
     */
    public static TbsysschdadmasterVO createAuditMaster(SessionFactory sf, String scheduleID, String scheduleName,
                                                        String jobID, String jobName, String type, String status, String userID) throws JBranchException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            TbsysschdadmasterVO vo = new TbsysschdadmasterVO();
            TbsysschdadmasterPK pk = new TbsysschdadmasterPK();
                      
            SerialNumberUtil seq=new SerialNumberUtil();
            String hostName = InetAddress.getLocalHost().getHostName()+SCHD_MASTER;   
            String seqno = null;
    		try {
    			seqno = seq.getNextSerialNumber(hostName);
    		} catch (Exception e) {
    			// ignore
    		}
            if (seqno == null) {
            	seqno = "1";
            	createNewSerial(seq, hostName, 1L);
    		}
            pk.setauditid(Long.valueOf(seqno));         
            pk.setscheduleid(scheduleID);       
            
            vo.setcomp_id(pk);
            vo.setjobid(jobID);
            vo.setstarttime(new Timestamp(System.currentTimeMillis()));
            vo.settype(type);
            vo.setstatus(status);
            vo.setCreatetime(new Timestamp(System.currentTimeMillis()));
            vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
            vo.setCreator(userID);
            vo.setModifier(userID);
            session.save(vo);
            //session.flush();
            if (logger.isInfoEnabled()) {
                logger.info(getSchedulerAuditLog(vo));
            }
            
            return vo;

//			masterPs.setLong(1, scheduleMasterId);//auditid
//			masterPs.setString(2, scheduleId);//scheduleid
//			masterPs.setString(3, scheduleName);//schedulename
//			masterPs.setTimestamp(4, new Timestamp(System.currentTimeMillis()));//starttime
//			masterPs.setString(5, type);//type
//			masterPs.setString(6, STATUS_RUNNING);//status
//			masterPs.setLong(7, 0);//version
//			masterPs.setTimestamp(8, new Timestamp(System.currentTimeMillis()));//createtime
//			masterPs.setString(9, SCHEDULER_USER);//creator
//			masterPs.setTimestamp(10, new Timestamp(System.currentTimeMillis()));//lastupdate
//			masterPs.setString(11, SCHEDULER_USER);//modifier
//			masterPs.execute();
        }
        catch (Exception e) {
            logger.error("create schedule audit master log exception", e);
            throw new JBranchException(SchedulerErrMsg.GEN_AUDIT_MASTER_ERROR);
        }
        finally {
            try {
                transaction.commit();
            }
            catch (Exception e) {
            }
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    public static void updateAuditMaster(SessionFactory sf, TbsysschdadmasterVO currentMasterVO) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            TbsysschdadmasterVO mvo = (TbsysschdadmasterVO) session.get(TbsysschdadmasterVO.class, currentMasterVO.getcomp_id());

            if (mvo.getresult() == null || mvo.getresult().trim().equals("0")) {
                mvo.setresult(currentMasterVO.getresult());
            }

            mvo.setLastupdate(currentMasterVO.getLastupdate());
            mvo.setstatus(currentMasterVO.getstatus());
            mvo.setdescription(currentMasterVO.getdescription());
            mvo.setendtime(currentMasterVO.getendtime());
            mvo.setfailrecord(currentMasterVO.getfailrecord());
            mvo.setMemo(currentMasterVO.getMemo());
            mvo.setInsertrecord(currentMasterVO.getInsertrecord());
            mvo.settotalrecord(currentMasterVO.gettotalrecord());
            mvo.setUpdaterecord(currentMasterVO.getUpdaterecord());
            mvo.setModifier(SchedulerHelper.SCHEDULER_USER);
            session.update(mvo);
            if (logger.isInfoEnabled()) {
                logger.info(getSchedulerAuditLog(mvo));
            }
            //session.flush();

//			masterPs.setLong(1, scheduleMasterId);//auditid
//			masterPs.setString(2, scheduleId);//scheduleid
//			masterPs.setString(3, scheduleName);//schedulename
//			masterPs.setTimestamp(4, new Timestamp(System.currentTimeMillis()));//starttime
//			masterPs.setString(5, type);//type
//			masterPs.setString(6, STATUS_RUNNING);//status
//			masterPs.setLong(7, 0);//version
//			masterPs.setTimestamp(8, new Timestamp(System.currentTimeMillis()));//createtime
//			masterPs.setString(9, SCHEDULER_USER);//creator
//			masterPs.setTimestamp(10, new Timestamp(System.currentTimeMillis()));//lastupdate
//			masterPs.setString(11, SCHEDULER_USER);//modifier
//			masterPs.execute();
        }
        catch (Exception e) {
            logger.error("create schedule audit master log exception", e);
            throw new JBranchException(SchedulerErrMsg.GEN_AUDIT_MASTER_ERROR);
        }
        finally {
            try {
                transaction.commit();
            }
            catch (Exception e) {
            }
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 取得參數Map
     *
     * @param scheduleParameterStr
     * @return
     */
    public static Map getParameter(String parameterStr) throws JBranchException {
        try {
            Map paraMap = new HashMap();
            if (parameterStr != null && parameterStr.trim().length() > 0) {
                String[] parameters = parameterStr.split(PARAMETER_SEPARATOR);
                for (int i = 0; i < parameters.length; i++) {
                    int j = parameters[i].indexOf(KEY_VALUE_SEPARATOR);
                    if (j != -1) {
                        String keyStr = parameters[i].substring(0, j);
                        String valueStr = parameters[i].substring(j + 1);
                        paraMap.put(keyStr, valueStr);
                    }
                }
            }
            return paraMap;
        }
        catch (Exception e) {
            logger.error("parameter parse error, parameter string = " + parameterStr, e);
            throw new JBranchException(SchedulerErrMsg.PARAMETER_PARSE_ERROR);
        }
    }

    public static String getStackTraceAsString(Throwable t) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes, true);
        t.printStackTrace(writer);
        return bytes.toString();
    }

    /**
     * 建立排程查詢主檔
     *
     * @param con
     */
    public static TBSYSSCHDQUERYMASTERVO createQueryMaster(SessionFactory sf, String processor) throws JBranchException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            TBSYSSCHDQUERYMASTERVO vo = new TBSYSSCHDQUERYMASTERVO();
            long tm = System.currentTimeMillis();
            vo.setPROCESSOR(processor);
            vo.setVersion(0L);
            vo.setCreatetime(new Timestamp(tm));
            vo.setCreator("SYSTEM");
            vo.setLastupdate(new Timestamp(tm));
            vo.setModifier("SYSTEM");
            session.save(vo);
            return vo;
        }
        catch (Exception e) {
            logger.error("create schedule query master log exception", e);
            throw new JBranchException(e.getMessage(), e);
        }
        finally {
            try {
                transaction.commit();
            }
            catch (Exception e) {
            }
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * 建立排程查詢明細
     *
     * @param con
     */
    public static void createQueryDetail(SessionFactory sf, Long queryId, String scheduleId, String jobId, Long auditid, Timestamp preFiretime, Timestamp firetime, Timestamp nextFiretime) throws JBranchException {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sf.openSession();
            transaction = session.beginTransaction();
            TBSYSSCHDQUERYDETAILVO vo = new TBSYSSCHDQUERYDETAILVO();
            vo.setQUERYID(queryId);
            vo.setAUDITID(auditid);
            vo.setPRE_FIRETIME(preFiretime);
            vo.setFIRETIME(firetime);
            vo.setNEXT_FIRETIME(nextFiretime);
            long tm = System.currentTimeMillis();
            vo.setVersion(0L);
            vo.setCreatetime(new Timestamp(tm));
            vo.setCreator("SYSTEM");
            vo.setLastupdate(new Timestamp(tm));
            vo.setModifier("SYSTEM");
            session.save(vo);
        }
        catch (Exception e) {
            logger.error("create schedule query detail log exception", e);
            throw new JBranchException(e.getMessage(), e);
        }
        finally {
            try {
                transaction.commit();
            }
            catch (Exception e) {
            }
            try {
                session.close();
            }
            catch (Exception e) {
            }
        }
    }

    static CronTrigger generateTrigger(TbsysschdVO vo) throws ParseException {
        CronTrigger trigger = new CronTrigger(
                vo.getscheduleid() + "_trigger",//name
                vo.getscheduleid() + "_group",//group
                vo.getscheduleid(),//jobName
                SchedulerHelper.DEFAULT_GROUP,//jobGroup
                vo.getcronexpression());
        trigger.setCalendarName(SchedulerHelper.DEFAULT_CALENDAR);
        return trigger;
    }

    static CronTrigger generateTrigger(String name, String group, String jobName, String cronexpression) throws ParseException {
        CronTrigger trigger = new CronTrigger(
                name + "_trigger",//name
                group + "_group",//group
                jobName,//jobName
                SchedulerHelper.DEFAULT_GROUP,//jobGroup
                cronexpression);
        trigger.setCalendarName(SchedulerHelper.DEFAULT_CALENDAR);
        return trigger;
    }

    static Set getCalendarSet() throws JBranchException {
        XmlInfo info = new XmlInfo();
        Hashtable ht = info.getVariable("TBSYSSCHD.CANDLANDER", FormatHelper.FORMAT_3);
        return ht.keySet();
    }
    static void createNewSerial(SerialNumberUtil serialNO, String snid, Long TXTNO)
	throws JBranchException {
    	serialNO.createNewSerial(snid, "0000000000", (Integer) null,
		(String) null, (Timestamp) null, new Integer(1),
		new Long("9999999999"), "Y", TXTNO, "SYSTEM");
    }
}
