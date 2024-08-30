package com.systex.jbranch.platform.common.workday.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.datasource.DataSource;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWORKDAYRULEVO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.workday.CalendarDataProvider;
import com.systex.jbranch.platform.common.workday.WorkDayHelper;
import com.systex.jbranch.platform.common.workday.impl.rule.BaseCalendar;
import com.systex.jbranch.platform.common.workday.impl.rule.ManualRollWorkCalendar;

/**
 * @author Angus Luo
 * @date 2010-05-07
 */
@Repository
public class WorkDayUtils {
// ------------------------------ FIELDS ------------------------------

	private Logger logger = LoggerFactory.getLogger(WorkDayUtils.class);
// -------------------------- OTHER METHODS --------------------------

    /**
     * @param str 欲格式化字串,ex:{fps_host_calendar.yyyyMMdd-1}
     * @return 已日期格式化的字串
     * @throws Exception
     */
    public String formatDateString(String str) throws Exception {
        if (str == null || str.trim().length() == 0) {
            throw new Exception("pf_ftp_common_003");
        }

        String formatStr = formatDateStringOld(str);
        if (!formatStr.equals(str)) {
            return formatStr;
        }

        String pattern = ".*\\{([^\\.]+)\\.([^-^\\+\\}]+)(([-\\+])(\\d))?\\}.*";
        if (str.matches(pattern)) {
            String ruleId = str.replaceAll(pattern, "$1");
            String datePattern = str.replaceAll(pattern, "$2");
            String flag = str.replaceAll(pattern, "$4");

            int days = 0;
            try {
                days = Integer.parseInt(str.replaceAll(pattern, "$5"));
            }
            catch (Exception e) {
                //ingore
            }
            java.util.Calendar fileDate = java.util.Calendar.getInstance();
            String currentDate = null;//info.getVariable(WorkDayHelper.PARAM_TYPE, ruleId, FormatHelper.FORMAT_3);

            DataSource ds = new DataSource();
            SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
            Session session = null;
            Connection conn = null;
            
            conn = SessionFactoryUtils.getDataSource(sf).getConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE=? and PARAM_CODE=?");
                ps.setString(1, WorkDayHelper.PARAM_TYPE);
                ps.setString(2, ruleId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    try {
                        currentDate = rs.getString("PARAM_NAME");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        fileDate.setTime(sdf.parse(currentDate));
                    }
                    catch (Exception e) {
                        //use current date
                    }
                }
            }
            catch (SQLException e) {
                throw new JBranchException(e.getMessage(), e);
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (Exception e) {
                        //ignore
                    }
                }
                if (ps != null) {
                    try {
                        ps.close();
                    }
                    catch (Exception e) {
                        //ignore
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (Exception e) {
                        //ignore
                    }
                }
                if (session != null) {
                    try {
                        session.close();
                    }
                    catch (Exception e) {
                        //ignore
                    }
                }
            }
//			if(!currentDate.equals(ruleId)){//有值時
//				try {
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//					fileDate.setTime(sdf.parse(currentDate));
//				} catch (Exception e) {
//					//use current date
//				}
//			}
            for (int i = 0; i < days; i++) {
                Date d = null;
                if ("+".equals(flag)) {
                    d = getNextWorkDay(ruleId, fileDate.getTimeInMillis());
                }
                else if ("-".equals(flag)) {
                    d = getPreWorkDay(ruleId, fileDate.getTimeInMillis());
                }
                if (d != null) {
                    fileDate.setTime(d);
                }
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

                return str.replaceAll("\\{" + ruleId + "." + datePattern + "([-\\+](\\d+))?\\}", sdf.format(fileDate.getTime()));
            }
            catch (Exception e) {
                return str;
            }
        }
        else {
            return str;
        }
    }

    private String formatDateStringOld(String str) {
        String pattern = ".*\\{([^-^\\+\\}]+)(([-\\+])(\\d))?\\}.*";
        if (str.matches(pattern)) {
            String datePattern = str.replaceAll(pattern, "$1");
            if (datePattern.indexOf(".") != -1) {
                return str;
            }
            String flag = str.replaceAll(pattern, "$3");

            int days = 0;
            try {
                days = Integer.parseInt(str.replaceAll(pattern, "$4"));
            }
            catch (Exception e) {
                //ingore
            }
            Calendar cal = Calendar.getInstance();
            boolean isOffset = false;
            if ("+".equals(flag)) {
                isOffset = true;
            }
            else if ("-".equals(flag)) {
                days = days * -1;
                isOffset = true;
            }
            if (isOffset) {
                cal.add(Calendar.DATE, days);
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

                return str.replaceAll("\\{" + datePattern + "([-\\+](\\d+))?\\}", sdf.format(cal.getTime()));
            }
            catch (Exception e) {
                return str;
            }
        }
        else {
            return str;
        }
    }

    public Date getNextWorkDay(String ruldId, long millis) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        do {
            cal.add(Calendar.DATE, 1);
        } while (!bcal.isTimeIncluded(cal.getTime().getTime()));
        return cal.getTime();
    }

    public Date getPreWorkDay(String ruldId, long millis) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        do {
            cal.add(Calendar.DATE, -1);
        } while (!bcal.isTimeIncluded(cal.getTime().getTime()));
        return cal.getTime();
    }

    public Date getNextWorkDay(String ruldId) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bcal.getToday());
        do {
            cal.add(Calendar.DATE, 1);
        } while (!bcal.isTimeIncluded(cal.getTime().getTime()));
        return cal.getTime();
    }

    public Date getPreWorkDay(String ruldId) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bcal.getToday());
        do {
            cal.add(Calendar.DATE, -1);
        } while (!bcal.isTimeIncluded(cal.getTime().getTime()));
        return cal.getTime();
    }

    public Date getToday(String ruldId) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        return bcal.getToday();
    }

    public BaseCalendar getBaseCalendar(String ruldId) throws SchedulerException {
        Scheduler schd = StdSchedulerFactory.getDefaultScheduler();
        String[] calNames = schd.getCalendarNames();
        if (calNames != null) {
            for (int i = 0; i < calNames.length; i++) {
                String calName = calNames[i];
                if (!ruldId.equals(calName)) {
                    continue;
                }
                org.quartz.Calendar cal = schd.getCalendar(calName);
                if (cal instanceof BaseCalendar == false) {
                    continue;
                }
                BaseCalendar bcal = (BaseCalendar) cal;
                return bcal;
            }
        }
        return new BaseCalendar();
    }

    public Map getWorkDayRuleMap(Connection con) throws JBranchException, SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select * from TBSYSWORKDAYRULE");

            DataAccessManager dam = new DataAccessManager();
            List<TBSYSWORKDAYRULEVO> ruleVos = dam.findAll(TBSYSWORKDAYRULEVO.TABLE_UID);
            Map<String, TBSYSWORKDAYRULEVO> extMap = new HashMap<String, TBSYSWORKDAYRULEVO>();
            while (rs.next()) {
                String ruleId = rs.getString("RULE_ID");
                String cpId = rs.getString("CALENDAR_PROVIDER_ID");
                String bcId = rs.getString("BASE_CALENDAR_ID");
                TBSYSWORKDAYRULEVO ruleVO = new TBSYSWORKDAYRULEVO();
                ruleVO.setRULE_ID(ruleId);
                ruleVO.setCALENDAR_PROVIDER_ID(cpId);
                ruleVO.setBASE_CALENDAR_ID(bcId);
                extMap.put(ruleId, ruleVO);
            }

            return extMap;
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    public boolean isHoliday(String ruldId) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        return bcal.isTimeIncluded(bcal.getToday().getTime());
    }

    public boolean isHoliday(String ruldId, Date date) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        return bcal.isTimeIncluded(date.getTime());
    }

    public boolean isHoliday(String ruldId, long timeStamp) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        return bcal.isTimeIncluded(timeStamp);
    }

    public boolean isHolidayByProvider(String providerId) throws Exception {
        return isHolidayByProvider(providerId, System.currentTimeMillis());
    }

    public boolean isHolidayByProvider(String providerId, Date date) throws Exception {
        return isHolidayByProvider(providerId, date.getTime());
    }

    public boolean isHolidayByProvider(String providerId, long timeStamp) throws Exception {
        if (!PlatformContext.beanExists(providerId)) {
            throw new JBranchException("provider[" + providerId + "]不存在。");
        }
        Object o = PlatformContext.getBean(providerId);
        Date[] dates = null;
        if ((o instanceof CalendarDataProvider) == false) {
            throw new JBranchException("provider[" + providerId + "]需繼承CalendarDataProvider。");
        }

        CalendarDataProvider provider = (CalendarDataProvider) o;
        dates = provider.getExcludedDates();

        BaseCalendar bcal = new BaseCalendar();
        bcal.addExcludedDates(dates);

        return bcal.isHoliday(timeStamp);
    }

    public void reloadAllRule() throws Exception {
        Map<String, TBSYSWORKDAYRULEVO> extMap = getWorkDayRuleMap();
        Map<String, Date[]> providerMap = new HashMap<String, Date[]>();

        Iterator<String> it = extMap.keySet().iterator();
        while (it.hasNext()) {
            try {
                String id = it.next();
                TBSYSWORKDAYRULEVO ruleVo = extMap.get(id);
                String calendarProviderId = ruleVo.getCALENDAR_PROVIDER_ID();
                Object o;
                try {
                    o = PlatformContext.getBean(calendarProviderId);
                }
                catch (Exception e) {
                    continue;
                }
                if (o instanceof CalendarDataProvider) {
                    Date[] dates = providerMap.get(calendarProviderId);
                    if (dates == null) {
                        CalendarDataProvider provider = (CalendarDataProvider) o;
                        dates = provider.getExcludedDates();
                        providerMap.put(calendarProviderId, dates);
                    }

                    Object o2 = null;
                    String bcId = ruleVo.getBASE_CALENDAR_ID();
                    if (bcId != null && PlatformContext.beanExists(bcId)) {
                        o2 = PlatformContext.getBean(bcId);
                    }

                    if (o2 instanceof BaseCalendar) {
                        BaseCalendar bcal = (BaseCalendar) o2;
                        bcal.addExcludedDates(dates);
                        bcal.setName(ruleVo.getRULE_ID());
                        addCalendar(ruleVo.getRULE_ID(), bcal, true, true);
                        if (logger.isInfoEnabled()) {
                            logger.info("Scheduler加入CalendarDataProvider[" + ruleVo.getRULE_ID() + "]");
                        }
                    }
                    else {
                        BaseCalendar bcal = new BaseCalendar();
                        bcal.addExcludedDates(dates);
                        bcal.setName(ruleVo.getRULE_ID());
                        addCalendar(ruleVo.getRULE_ID(), bcal, true, true);
                        logger.warn("CalendarDataProvider[" + ruleVo.getRULE_ID() + "]加入失敗，使用BaseCalendar");
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info("Reload RuleId[" + ruleVo.getRULE_ID() + "], CalendarDataProvider[" + calendarProviderId + "]");
                    }
                }
                else {
                    logger.warn("CalendarDataProvider[" + ruleVo.getRULE_ID() + "]加入失敗，該類別必需繼承CalendarDataProvider");
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void reloadRule(String ruleId) throws Exception {
        Map<String, TBSYSWORKDAYRULEVO> extMap = getWorkDayRuleMap();

        try {
            TBSYSWORKDAYRULEVO ruleVo = extMap.get(ruleId);
            String calendarProviderId = ruleVo.getCALENDAR_PROVIDER_ID();
            Object o;
            try {
                o = PlatformContext.getBean(calendarProviderId);
            }
            catch (Exception e) {
                return;
            }
            if (o instanceof CalendarDataProvider) {
                CalendarDataProvider provider = (CalendarDataProvider) o;
                Date[] dates = provider.getExcludedDates();
                Object o2 = null;
                String bcId = ruleVo.getBASE_CALENDAR_ID();
                if (bcId != null && PlatformContext.beanExists(bcId)) {
                    o2 = PlatformContext.getBean(bcId);
                }
                if (o2 instanceof BaseCalendar) {
                    BaseCalendar bcal = (BaseCalendar) o2;
                    bcal.addExcludedDates(dates);
                    bcal.setName(ruleVo.getRULE_ID());
                    addCalendar(ruleVo.getRULE_ID(), bcal, true, true);
                    if (logger.isInfoEnabled()) {
                        logger.info("Scheduler加入CalendarDataProvider[" + ruleVo.getRULE_ID() + "]");
                    }
                }
                else {
                    BaseCalendar bcal = new BaseCalendar();
                    bcal.addExcludedDates(dates);
                    bcal.setName(ruleVo.getRULE_ID());
                    addCalendar(ruleVo.getRULE_ID(), bcal, true, true);
                    logger.warn("CalendarDataProvider[" + ruleVo.getRULE_ID() + "]加入失敗，使用BaseCalendar");
                }
                if (logger.isInfoEnabled()) {
                    logger.info("Reload RuleId[" + ruleVo.getRULE_ID() + "], CalendarDataProvider[" + calendarProviderId + "]");
                }
            }
            else {
                logger.warn("CalendarDataProvider[" + ruleVo.getRULE_ID() + "]加入失敗，該類別必需繼承CalendarDataProvider");
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Map getWorkDayRuleMap() throws JBranchException, SQLException {
        DataSource ds = new DataSource();
        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
        Session session = null;
        Connection conn = null;
        
        conn = SessionFactoryUtils.getDataSource(sf).getConnection();
        
        try {
            return getWorkDayRuleMap(conn);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    public void reloadRuleByProviderId(String providerId) throws Exception {
        DataSource ds = new DataSource();
        SessionFactory sf = (SessionFactory) PlatformContext.getBean(ds.getDataSource());
        Session session = null;
        Connection conn = null;
        
        conn = SessionFactoryUtils.getDataSource(sf).getConnection();
        
        try {
            reloadRuleByProviderId(conn, providerId);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (session != null) {
                try {
                    session.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    public void reloadRuleByProviderId(DataAccessManager dam, String providerId) throws Exception {
        TBSYSWORKDAYRULEVO ruleVO = new TBSYSWORKDAYRULEVO();
        ruleVO.setCALENDAR_PROVIDER_ID(providerId);
        List<TBSYSWORKDAYRULEVO> list = dam.findByFields(ruleVO, "CALENDAR_PROVIDER_ID");

        Object o = PlatformContext.getBean(providerId);
        Date[] dates = null;
        if (o instanceof CalendarDataProvider) {
            CalendarDataProvider provider = (CalendarDataProvider) o;
            dates = provider.getExcludedDates();
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                TBSYSWORKDAYRULEVO ruleVo = list.get(i);

                if (o instanceof CalendarDataProvider) {
                    Object o2 = null;
                    String bcId = ruleVo.getBASE_CALENDAR_ID();
                    if (bcId != null && PlatformContext.beanExists(bcId)) {
                        o2 = PlatformContext.getBean(bcId);
                    }

                    if (o2 instanceof BaseCalendar) {
                        BaseCalendar bcal = (BaseCalendar) o2;
                        bcal.addExcludedDates(dates);
                        bcal.setName(ruleVo.getRULE_ID());
                        addCalendar(ruleVo.getRULE_ID(), bcal, true, true);
                        if (logger.isInfoEnabled()) {
                            logger.info("Scheduler加入CalendarDataProvider[" + ruleVo.getRULE_ID() + "]");
                        }
                    }
                    else {
                        BaseCalendar bcal = new BaseCalendar();
                        bcal.addExcludedDates(dates);
                        bcal.setName(ruleVo.getRULE_ID());
                        addCalendar(ruleVo.getRULE_ID(), bcal, true, true);
                        logger.warn("CalendarDataProvider[" + ruleVo.getRULE_ID() + "]加入失敗，使用BaseCalendar");
                    }
                    if (logger.isInfoEnabled()) {
                        logger.info("Reload RuleId[" + ruleVo.getRULE_ID() + "], CalendarDataProvider[" + providerId + "]");
                    }
                }
                else {
                    logger.warn("CalendarDataProvider[" + ruleVo.getRULE_ID() + "]加入失敗，該類別必需繼承CalendarDataProvider");
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void reloadRuleByProviderId(Connection conn, String providerId) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("select RULE_ID, BASE_CALENDAR_ID from TBSYSWORKDAYRULE where CALENDAR_PROVIDER_ID=?");
            ps.setString(1, providerId);
            rs = ps.executeQuery();

//			DataAccessManager dam = new DataAccessManager();
//			TBSYSWORKDAYRULEVO ruleVO = new TBSYSWORKDAYRULEVO();
//			ruleVO.setCALENDAR_PROVIDER_ID(providerId);
//			List<TBSYSWORKDAYRULEVO> list = dam.findByFields(ruleVO, new String[]{"CALENDAR_PROVIDER_ID"});

            Object o = PlatformContext.getBean(providerId);
            Date[] dates = null;
            if (o instanceof CalendarDataProvider) {
                CalendarDataProvider provider = (CalendarDataProvider) o;
                dates = provider.getExcludedDates();
                logger.info("reload[" + providerId + "]");
                for (int i = 0; i < dates.length; i++) {
                	logger.info("dates[" + i + "]=" + dates[i]);
                }
            }

            while (rs.next()) {
                String ruleId = rs.getString("RULE_ID");
                String bcId = rs.getString("BASE_CALENDAR_ID");
                try {
                    if (o instanceof CalendarDataProvider) {
                        Object o2 = null;

                        if (bcId != null && PlatformContext.beanExists(bcId)) {
                            o2 = PlatformContext.getBean(bcId);
                        }

                        if (o2 instanceof BaseCalendar) {
                            BaseCalendar bcal = (BaseCalendar) o2;
                            bcal.addExcludedDates(dates);
                            bcal.setName(ruleId);
                            addCalendar(ruleId, bcal, true, true);
                            if (logger.isInfoEnabled()) {
                                logger.info("Scheduler加入CalendarDataProvider[" + ruleId + "]");
                            }
                        }
                        else {
                            BaseCalendar bcal = new BaseCalendar();
                            bcal.addExcludedDates(dates);
                            bcal.setName(ruleId);
                            addCalendar(ruleId, bcal, true, true);
                            logger.warn("CalendarDataProvider[" + ruleId + "]加入失敗，使用BaseCalendar");
                        }
                        if (logger.isInfoEnabled()) {
                            logger.info("Reload RuleId[" + ruleId + "], CalendarDataProvider[" + providerId + "]");
                        }
                    }
                    else {
                        logger.warn("CalendarDataProvider[" + ruleId + "]加入失敗，該類別必需繼承CalendarDataProvider");
                    }
                }
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    public void addCalendar(String ruleId, org.quartz.Calendar calendar, boolean replace, boolean updateTriggers) throws SchedulerException {
        StdSchedulerFactory.getDefaultScheduler().addCalendar(ruleId, calendar, replace, updateTriggers);
    }

    public void rollNextDay(String ruldId) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        if (bcal == null) {
            throw new JBranchException("無此Calendar Name[" + ruldId + "]");
        }
        bcal.rollNextDay();
    }

    public void rollNextDayAll() throws JBranchException, SchedulerException {
        Scheduler schd = StdSchedulerFactory.getDefaultScheduler();
        String[] calNames = schd.getCalendarNames();
        if (calNames != null) {
            for (int i = 0; i < calNames.length; i++) {
                String ruldId = calNames[i];

                org.quartz.Calendar cal = schd.getCalendar(ruldId);
                if (cal instanceof ManualRollWorkCalendar == false) {
                    continue;
                }
                BaseCalendar bcal = (BaseCalendar) cal;
                bcal.rollNextDay();
            }
        }
    }

    public void rollNextWorkDay(String ruldId) throws JBranchException, SchedulerException {
        BaseCalendar bcal = getBaseCalendar(ruldId);
        if (bcal == null) {
            throw new JBranchException("無此Calendar Name[" + ruldId + "]");
        }
        bcal.rollNextWorkDay();
    }

    public void rollNextWorkDayAll() throws JBranchException, SchedulerException {
        Scheduler schd = StdSchedulerFactory.getDefaultScheduler();
        String[] calNames = schd.getCalendarNames();
        if (calNames != null) {
            for (int i = 0; i < calNames.length; i++) {
                String ruldId = calNames[i];

                org.quartz.Calendar cal = schd.getCalendar(ruldId);
                if (cal instanceof ManualRollWorkCalendar == false) {
                    continue;
                }
                BaseCalendar bcal = (BaseCalendar) cal;
                bcal.rollNextWorkDay();
            }
        }
    }
}
